package com.example.restaurant.database;

import android.util.Log;

import com.example.restaurant.R;
import com.example.restaurant.connection.ConnectionManager;
import com.example.restaurant.models.Customer;
import com.example.restaurant.models.Menu;
import com.example.restaurant.models.Reservation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ReservationDao {

    private static final String TAG_NEW_RESERVATION = "TAG_NEW_RESERVATION";
    private static ReservationDao _instance = null;

    public enum AppMode
    {
        Customer,
        Waiter
    };

    AppMode mode;
    HashMap<String, Reservation> Reservations;
    static final String TAG = "RestaurantDao";
    Menu mMenu;
    String userID;

    public String getUserID()
    {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void Clear()
    {
        Reservations.clear();
        mode = AppMode.Customer;
        mMenu = null;
        userID = null;
    }

    public static ReservationDao getInstance()
    {
        if (_instance == null) {
            _instance = new ReservationDao();
        }
        return _instance;
    }


    public AppMode getMode() {
        return mode;
    }

    public void RemoveReservationWithID(String reservationID)
    {
        Reservations.remove(reservationID);
    }

    public boolean RequestReservationForUser(String email, String password) {

        if (password == null || password.isEmpty())
        {
            mode = AppMode.Customer;
        }
        else
        {
            mode = AppMode.Waiter;
        }
        try {
            if(mode == AppMode.Customer)
            {
//TEST
//                email = "dummy@dummy.com";
//TEST


                setReservations(ConnectionManager.Instance().GetReservationsForUser(email));
            }
            else if (mode == AppMode.Waiter)
            {
//TEST
//                email = "waiter";
//                password = "waiter";
//TEST
                String userId = ConnectionManager.Instance().userAuth(email, password);
                if(userId != null)
                {
                    setUserID(userId);
                    setReservations(ConnectionManager.Instance().GetReservationsForWaiter(userId));
                    return true;
                }
                return false;
            }
            else
            {
                return false;
            }
        } catch (ConnectionManager.ConnectionException ex) {
            Log.d(TAG, "Server Error: ", ex);
            return false;
        }
        try {
            mMenu = ConnectionManager.Instance().GetMenu();
        }
        catch (ConnectionManager.ConnectionException ex) {
            Log.d(TAG, "Server Error: ", ex);
        }
        return true;
    }




    public String CreateNewReservationForUser(String email) {
        //replace with new, if exist
        String reservationID =  String.format("%s:%s", TAG_NEW_RESERVATION, email);
        Reservation newReservation = new Reservation(reservationID, email);
        Reservations.put(reservationID, newReservation);
        return reservationID;
    }


    public Menu getMenu() {

        if(mMenu == null)
        {
            mMenu =ConnectionManager.Instance().GetMenu();
        }
        return mMenu;
    }

    public Integer getReservationsCount() {
        return Reservations.size();
    }

    public Reservation getReservationByID(String strID) {
        return Reservations.get(strID);
    }

    public Reservation getFirstReservation() {
        if (Reservations.size() == 1) {

            return Reservations.get(Reservations.keySet().toArray()[0]);
        }
        return null;
    }

    public List<Reservation> getReservations() {
        return new ArrayList<>(Reservations.values());
    }

    private void setReservations(ArrayList<Reservation> reservations)
    {
        if(reservations != null) {
            for (Reservation res : reservations) {
                Reservations.put(res.getReservationID(), res);
            }
        }
    }

    private ReservationDao()
    {
        Reservations = new HashMap<>();
        Clear();
    }

    public boolean ApplyReservation(String reservationID, String userID, String userEmail)
    {
        boolean isNewReservation = reservationID.contains(TAG_NEW_RESERVATION);
        return ConnectionManager.Instance().ApplyReservation(reservationID, userID, userEmail, isNewReservation);
    }

    public boolean CancelReservation(String reservationID, String userID, String userEmail)
    {
        if(reservationID.contains(TAG_NEW_RESERVATION))
        {//was a new reservation, just clean a data
            Clear();
        }
        else {
            boolean res = ConnectionManager.Instance().CancelReservation(reservationID, userID);
            if (res)
            {
                Reservations.remove(reservationID);
            }
            return res;
        }
        return false;
    }

    public ArrayList<String> GetReservationTimeForDate(Date datetime)
    {
        return ConnectionManager.Instance().GetReservationTimeForDate(datetime);
    }


}
