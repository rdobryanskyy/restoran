package com.example.restaurant.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.restaurant.R;
import com.example.restaurant.database.ReservationDao;
import com.example.restaurant.models.Reservation;
import com.example.restaurant.utils.ExtrasUtils;
import com.example.restaurant.utils.ProgressDialogUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EditDateActivity extends AppCompatActivity {

    Reservation mReservation;
    TextView dateLabel;
    TextView timeLabel;
    View mProgressView;
    View mTimeFormView;
    Date selectedDate;
    CheckTimeTask mCheckTimeTask;
    ArrayList<String> mReservationTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_date);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        catch(NullPointerException ex)
        {
            ex.printStackTrace();
        }

        dateLabel = findViewById(R.id.date_label);
        timeLabel = findViewById(R.id.time_label);
        mProgressView = findViewById(R.id.date_progress);
        mTimeFormView = findViewById(R.id.date_form);

        String reservationID = ExtrasUtils.GetReservationID(this);
        if(reservationID == null)
        {
            finish();
            return;
        }

        mReservation = ReservationDao.getInstance().getReservationByID(reservationID);
        selectedDate = mReservation.getReservationTime();
        if(selectedDate == null)
        {
            Calendar newDate = Calendar.getInstance();
            newDate.setTime(new Date());
            newDate.set(newDate.get(Calendar.YEAR),
                    newDate.get(Calendar.MONTH),
                    newDate.get(Calendar.DAY_OF_MONTH) + 1,
                    0, 0, 0);
            selectedDate = newDate.getTime();
        }

        ProgressDialogUtil.showProgress(true, mTimeFormView, mProgressView);
        mCheckTimeTask = new CheckTimeTask(selectedDate, this);
        mCheckTimeTask.execute((Void) null);

        DoSetTimeStrings(selectedDate);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void DoSetTimeStrings(Date date)
    {
        selectedDate = date;
        mReservation.setReservationTime(selectedDate);
        String stringDate = DateFormat.getDateInstance().format(selectedDate);
        dateLabel.setText(stringDate);
        String stringTime = DateFormat.getTimeInstance().format(selectedDate);
        timeLabel.setText(stringTime);
    }

    public void onButtonClick(View v)
    {
        if(v.getId() == R.id.date_set)
        {

            Calendar newCalendar = Calendar.getInstance();
            newCalendar.setTime(new Date());
            DatePickerDialog  dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth,0,0,0);
                    selectedDate = newDate.getTime();
                    if (mCheckTimeTask == null) {
                        ProgressDialogUtil.showProgress(true, mTimeFormView, mProgressView);
                        mCheckTimeTask = new CheckTimeTask(selectedDate,EditDateActivity.this);
                        mCheckTimeTask.execute((Void) null);
                    }
                    DoSetTimeStrings(selectedDate);
                }

            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis());
            dialog.show();
        }
        else if(v.getId() == R.id.time_set)
        {
            if(mReservationTimes != null && !mReservationTimes.isEmpty())
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditDateActivity.this);
                builder.setTitle(getString(R.string.chose_time));

                String[] mStringArray = new String[mReservationTimes.size()];
                mStringArray = mReservationTimes.toArray(mStringArray);

                builder.setItems(mStringArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        String selectedTime = mReservationTimes.get(which);
                        String[] parts = selectedTime.split("\\:");

                        Calendar newDate = Calendar.getInstance();
                        newDate.setTime(selectedDate);
                        newDate.set(newDate.get(Calendar.YEAR),
                                newDate.get(Calendar.MONTH),
                                newDate.get(Calendar.DAY_OF_MONTH),
                                Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), 0);
                        selectedDate = newDate.getTime();
                        DoSetTimeStrings(selectedDate);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else
            {
                ProgressDialogUtil.ShowError(getString(R.string.select_date), EditDateActivity.this);
            }
        }
    }

    static public class CheckTimeTask extends AsyncTask<Void, Void, Boolean> {

        private final Date mReservationDate;
        private final EditDateActivity Context;
        CheckTimeTask(Date date, EditDateActivity parent)
        {
            mReservationDate = date;
            Context = parent;
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            Context.mReservationTimes = ReservationDao.getInstance().GetReservationTimeForDate(mReservationDate);
            return !Context.mReservationTimes.isEmpty();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Context.mCheckTimeTask = null;
            ProgressDialogUtil.showProgress(false, Context.mTimeFormView, Context.mProgressView);

            if (!success)
            {
                ProgressDialogUtil.ShowError(Context.getString(R.string.now_dates), Context);
            }
        }

        @Override
        protected void onCancelled() {
            Context.mCheckTimeTask = null;
            ProgressDialogUtil.showProgress(false, Context.mTimeFormView, Context.mProgressView);
        }
    }

}
