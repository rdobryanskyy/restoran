package com.example.restaurant.utils;

import android.app.Activity;
import android.os.Bundle;

import com.example.restaurant.R;

public class ExtrasUtils
{
    public static String GetReservationID(Activity a)
    {
        String reservationID = null;
        Bundle extras = a.getIntent().getExtras();
        if (extras != null)
        {
            reservationID = extras.getString("ReservationID");
        }

        if(reservationID == null || reservationID.isEmpty())
        {
            ProgressDialogUtil.ShowError(a.getString(R.string.internal_application_error), a);
        }
        return reservationID;
    }
}
