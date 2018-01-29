package com.example.restaurant.database;

import android.util.Log;

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

    private static ReservationDao _instance = null;

    public enum AppMode
    {
        Customer,
        Waiter
    };

    AppMode mode;
    private HashMap<String, Reservation> Reservations;
    private static final String TAG = "RestaurantDao";
    private Menu mMenu;


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

        try {
            setReservations(ConnectionManager.Instance().GetReservationsForUser(email, password));

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
        if (password == null || password.isEmpty())
        {
            mode = AppMode.Customer;
        }
        else
        {
            mode = AppMode.Waiter;
        }
        return true;
    }


    public void CreateNewReservationWithID(String reservationID, String email) {
        //replace with new, if exist
        Reservation newReservation = new Reservation(reservationID, email);
        Reservations.put(reservationID, newReservation);
    }


    public Menu getMenu() {
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

    private void setReservations(ArrayList<Reservation> mReservations) {
        for (Reservation res : mReservations) {
            Reservations.put(res.getReservationID(), res);
        }
    }

    private ReservationDao()
    {
        Reservations = new HashMap<>();
        mMenu = ConnectionManager.Instance().GetMenu();

    }

    public boolean ApplyReservation(String reservationID)
    {
        return ConnectionManager.Instance().ApplyReservation(reservationID);
    }

    public boolean CancelReservation(String reservationID)
    {
        return ConnectionManager.Instance().CancelReservation(reservationID);
    }

    public ArrayList<Integer> GetReservationTimeForDate(Date datetime)
    {
        return ConnectionManager.Instance().GetReservationTimeForDate(datetime);
    }


}
