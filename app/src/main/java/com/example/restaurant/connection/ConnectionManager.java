package com.example.restaurant.connection;

import android.os.AsyncTask;
import android.util.Log;

import com.example.restaurant.database.ReservationDao;
import com.example.restaurant.models.Dish;
import com.example.restaurant.models.Menu;
import com.example.restaurant.models.Reservation;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ConnectionManager
{

    static  final  String connectionURL = "http://localhost/restaurant";
    static  final  String menuRequest = "/getMenu";
    static  final  String timeRequest = "/getTime?";
    static  final  String reservationsRequest = "/getReservations?";
    static  final  String cancelRequest = "/cancelRequest?";
    static  final  String makeRequest = "/makeRequest?";



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



    public ArrayList<Reservation> GetReservationsForUser(String email, String password) throws ConnectionException
    {

        String req = String.format("%suser=%s,pass=%s", reservationsRequest, email, (password == null ? "": password));
        String timeXML = GetRequestForAddress(req);
        if(timeXML != null)
        {
            ArrayList<Reservation> res = new ArrayList<Reservation>();
            try
            {
                InputStream is = new ByteArrayInputStream(timeXML.getBytes(StandardCharsets.UTF_8.name()));
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(is);
                Element element=doc.getDocumentElement();
                element.normalize();
                NodeList nList = doc.getElementsByTagName("Reservation");

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


    public ArrayList<Integer> GetReservationTimeForDate(Date dateTime)
    {

        String req = String.format("%s%s", timeRequest, DateFormat.getDateInstance().format(dateTime));
        String timeXML = GetRequestForAddress(timeRequest);
        if(timeXML != null)
        {
            try
            {
                ArrayList<Integer> res = new ArrayList<Integer>();
                InputStream is = new ByteArrayInputStream(timeXML.getBytes(StandardCharsets.UTF_8.name()));
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(is);
                Element element=doc.getDocumentElement();
                element.normalize();
                NodeList nList = doc.getElementsByTagName("Time");

                for (int i=0; i< nList.getLength(); i++)
                {
                    Node node = nList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element element2 = (Element) node;
                        String timStr = XmlUtils.getXMLValue("value", element2);
                        res.add( Integer.parseInt(timStr));
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

    public boolean ApplyReservation(String reservationID)
    {

        String xml = ReservationDao.getInstance().getReservationByID(reservationID).toString();
        String res = PostRequestForAddress( makeRequest + reservationID, xml);
        if(res == null)
        {
            ReservationDao.getInstance().RemoveReservationWithID(reservationID);
        }
        return res == null;
    }

    public boolean CancelReservation(String reservationID)
    {
        String res = GetRequestForAddress(cancelRequest+reservationID);
        if(res == null)
            ReservationDao.getInstance().RemoveReservationWithID(reservationID);

        return  (res == null);
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


    private String PostRequestForAddress(String addr, String xmlReq)
    {
        String url;
        url = String.format("%s%s", connectionURL , addr);
        String result;
        HttpPostRequest postRequest = new HttpPostRequest();
        try {
            return  postRequest.execute(url, xmlReq).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String GetRequestForAddress(String addr)
    {

        if(addr.equals(menuRequest))
        {
            return DebugStrings.debugMenu;
        }
        if(addr.contains(timeRequest))
        {
            return DebugStrings.debugTimes;
        }

        if(addr.contains(reservationsRequest))
        {
            return DebugStrings.debugReservations;
        }
        if(addr.contains(cancelRequest))
        {
            return null;
        }

        String url;
        url = String.format("%s%s", connectionURL , addr);
        String result;
        HttpGetRequest getRequest = new HttpGetRequest();

        try {
            return  getRequest.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    public class HttpPostRequest extends AsyncTask<String, String, String> {

        public HttpPostRequest()
        {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {

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

    public class HttpGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 1500;
        public static final int CONNECTION_TIMEOUT = 1500;
        @Override
        protected String doInBackground(String... params){
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
        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }
    }



    protected  static  ConnectionManager _instance = null;
}
