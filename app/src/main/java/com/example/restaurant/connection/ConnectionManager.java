package com.example.restaurant.connection;

import android.os.AsyncTask;
import android.util.Log;

import com.example.restaurant.database.ReservationDao;
import com.example.restaurant.models.Dish;
import com.example.restaurant.models.Menu;
import com.example.restaurant.models.Reservation;
import com.example.restaurant.utils.PasswordUtils;
import com.example.restaurant.utils.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ConnectionManager
{

    static  final  String connectionURL = "http://api-mob.biz-apps.ru/resto/RestrauntService.svc";
    static  final  String menuRequest = "/getMenu";
    static  final  String timeRequest = "/vacantTime?";
    static  final  String reservationsRequest = "/getClientOrders?";
    static  final  String reservationsWaiterRequest = "/getCurrentOrders?";
    static  final  String authRequest = "/getAuth?";


    static  final  String cancelRequest = "/delOrder?";
    static  final  String makeRequest = "/addOrder?";



    public class ConnectionException extends RuntimeException
    {
        String message;
        public ConnectionException() {
            super();
        }

        public ConnectionException(String message)
        {
            super(message);
            this.message = message;
        }
    }


    private  static final String TAG = "ConnectionManager";
    public  static  ConnectionManager Instance()
    {
        if(_instance == null)
        {
            _instance = new ConnectionManager();
        }
        return _instance;
    }


    protected ConnectionManager() {}


    private ArrayList<Reservation> ParseReservationsXML(String xml)
    {
        if(xml != null)
        {
            ArrayList<Reservation> res = new ArrayList<Reservation>();
            try
            {
                InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8.name()));
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(is);
                Element element=doc.getDocumentElement();
                element.normalize();
                NodeList nList = doc.getElementsByTagName("Order");

                for (int i=0; i< nList.getLength(); i++)
                {
                    Node node = nList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element element2 = (Element) node;

                        Reservation reservation = Reservation.fromXML(element2);
                        res.add( reservation);
                    }
                }
                return res;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        return null;
    }

    public ArrayList<Reservation> GetReservationsForWaiter(String waiterID) throws ConnectionException
    {
        String req = String.format("%suserId=%s", reservationsWaiterRequest, waiterID);
        String reservationXML = GetRequestForAddress(req);
        return ParseReservationsXML(reservationXML);
    }


    public ArrayList<Reservation> GetReservationsForUser(String email) throws ConnectionException
    {
        String req = String.format("%seMail=%s", reservationsRequest, email);
        String reservationXML = GetRequestForAddress(req);
        return ParseReservationsXML(reservationXML);
    }


    public ArrayList<String> GetReservationTimeForDate(Date dateTime)
    {
        SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String req = String.format("%sdate=%s", timeRequest, dateFormat.format(dateTime));
        String timeXML = GetRequestForAddress(req);
        if(timeXML != null)
        {
            try
            {
                ArrayList<String> res = new ArrayList<String>();
                InputStream is = new ByteArrayInputStream(timeXML.getBytes(StandardCharsets.UTF_8.name()));
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(is);
                Element element=doc.getDocumentElement();
                element.normalize();
                NodeList nList = doc.getElementsByTagName("string");

                for (int i=0; i< nList.getLength(); i++)
                {
                    Node node = nList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        res.add( node.getFirstChild().getNodeValue() );
                    }
                }
                return res;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        return null;
    }

    public boolean ApplyReservation(String reservationID, String userID, String userEmail, boolean isNew)
    {
            String xml = ReservationDao.getInstance().getReservationByID(reservationID).toString();
            String subUrl = String.format("%seMail=%s&userId=%s&orderId=%s",
                    makeRequest,
                    (userEmail == null)? "": userEmail,
                    (userID == null)? "": userID,
                    (isNew)? "": reservationID);


            String res = PostRequestForAddress( makeRequest + reservationID, xml);
            if(res == null)
            {
                ReservationDao.getInstance().RemoveReservationWithID(reservationID);
            }
            return res == null;
    }

    public boolean CancelReservation(String reservationID, String userID)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%sorderId=%s", cancelRequest, reservationID));
        if(userID != null && !userID.isEmpty())
        {
            builder.append(String.format("&userId=%s", userID));
        }

        String res = GetRequestForAddress(builder.toString());
        if(res == null)
            ReservationDao.getInstance().RemoveReservationWithID(reservationID);
        return  (res == null || res.isEmpty());
    }

    public Menu GetMenu()
    {
        String menuXML = GetRequestForAddress(menuRequest);
        if(menuXML != null)
        {
            return new Menu(menuXML);
        }
        return null;
    }


    private String GetRequestForAddress(String addr)
    {
        String url;
        url = String.format("%s%s", connectionURL , addr);
        String result;
        HttpGetRequest getRequest = new HttpGetRequest();

        try {
            return  getRequest.execute(url);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private String PostRequestForAddress(String addr, String xml)
    {
        String url;
        url = String.format("%s%s", connectionURL , addr);
        String result;
        HttpPostRequest getRequest = new HttpPostRequest();

        try {
            return  getRequest.execute(url, xml);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }




    public String userAuth(String email, String pass)
    {
        String passHash = PasswordUtils.MD5_Hash(pass);
        if(passHash != null) {
            String subUrl = String.format("%slogin=%s&password=%s", authRequest, email, passHash);
            String authXML = GetRequestForAddress(subUrl);
            if (authXML != null)
            {
                try {
                    InputStream is = new ByteArrayInputStream(authXML.getBytes(StandardCharsets.UTF_8.name()));
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(is);
                    Element element = doc.getDocumentElement();
                    element.normalize();
                    if (element.getNodeType() == Node.ELEMENT_NODE)
                    {
                        return element.getFirstChild().getNodeValue();
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private class HttpGetRequest {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 1500;
        public static final int CONNECTION_TIMEOUT = 1500;
        public String execute(String... params)
        {
            String stringUrl = params[0];
            String result;
            String inputLine;
            try {

                URL myUrl = new URL(stringUrl);
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();

                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                connection.connect();
                InputStreamReader streamReader = new  InputStreamReader(connection.getInputStream());

                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();

                while((inputLine = reader.readLine()) != null)
                {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;
        }
    }


    public class HttpPostRequest {
        public HttpPostRequest() {}
        public String execute(String... params)
        {
            String urlString = params[0]; // URL to call
            String data = params[1]; //data to post

            OutputStream out = null;
            try {

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                out.close();
                urlConnection.connect();
            } catch (Exception e) {

                System.out.println(e.getMessage());

            }
            return null;
        }
    }



    protected  static  ConnectionManager _instance = null;
}
