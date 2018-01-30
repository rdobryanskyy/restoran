package com.example.restaurant.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.restaurant.R;
import com.example.restaurant.database.ReservationDao;
import com.example.restaurant.models.Reservation;
import com.example.restaurant.utils.ExtrasUtils;
import com.example.restaurant.utils.ProgressDialogUtil;

public class ReservationEditActivity extends AppCompatActivity
{
    Button mSetDateTimeButton;
    Button mSetTableButton;
    Button mSetDishesButton;
    Button mMakeReservationButton;
    Reservation mReservation;

    ReservationApplyTask mTask = null;

    TextView mLabel;
    View mEditProgress;
    View mEditForm;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_edit);
        mSetDateTimeButton = findViewById(R.id.date_details);
        mSetTableButton = findViewById(R.id.table_details);
        mSetDishesButton = findViewById(R.id.dishes_details);
        mMakeReservationButton = findViewById(R.id.make_reservation);
        mLabel = findViewById(R.id.reservation_label);
        String reservationID = ExtrasUtils.GetReservationID(this);
        if(reservationID == null)
        {
            finish();
            return;
        }

        if(!reservationID.contains(getString(R.string.new_reservation_tag))) {
            mMakeReservationButton.setText(R.string.save_shanges);
            mLabel.setText( String.format("%s %s", getText(R.string.modify_reservation), reservationID));
        }
        mEditProgress = findViewById(R.id.edit_progress);
        mEditForm = findViewById(R.id.edit_form);
        mReservation = ReservationDao.getInstance().getReservationByID(reservationID);
    }

    public void onButtonClick(View v)
    {
        if(v.getId() == R.id.date_details)
        {
            Intent _i = new Intent(getBaseContext(), EditDateActivity.class);
            _i.putExtra("ReservationID", mReservation.getReservationID());
            startActivity(_i);

        }
        else if(v.getId() == R.id.table_details)
        {
            Intent _i = new Intent(getBaseContext(), EditTableActivity.class);
            _i.putExtra("ReservationID", mReservation.getReservationID());
            startActivity(_i);
        }
        else if(v.getId() == R.id.dishes_details)
        {
            Intent _i = new Intent(getBaseContext(), DishesOrderActivity.class);
            _i.putExtra("ReservationID", mReservation.getReservationID());
            startActivity(_i);

        }
        else if(v.getId() == R.id.make_reservation)
        {
            if(mTask == null) {
                ProgressDialogUtil.showProgress(true, mEditForm, mEditProgress);
                mTask = new ReservationApplyTask(mReservation, false, this);
                mTask.execute((Void) null);
            }
        }

        else if(v.getId() == R.id.cancel_reservation)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getString(R.string.cancel_reservation));
            builder.setMessage(getString(R.string.ask_2));

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    if(mTask == null)
                    {

                        ProgressDialogUtil.showProgress(true, mEditForm, mEditProgress);
                        mTask = new ReservationApplyTask(mReservation, true, ReservationEditActivity.this);
                        mTask.execute((Void) null);
                    }

                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    boolean ValidateMakeReservationButton()
    {
        if(mReservation != null)
            return mReservation.isReservationValid();
        return false;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(ValidateMakeReservationButton())
        {
            mMakeReservationButton.setEnabled(true);
        }
        else
        {
            mMakeReservationButton.setEnabled(false);
        }
    }


    static public class ReservationApplyTask extends AsyncTask<Void, Void, Boolean> {

        private final String mReservationID;
        private final String mUserID;
        private final boolean mCancel;
        private final String mUserEmail;
        private final ReservationEditActivity mParent;

        ReservationApplyTask(Reservation reservation, boolean cancel, ReservationEditActivity parent) {
            mReservationID = reservation.getReservationID();
            mCancel = cancel;
            mParent = parent;
            mUserID = ReservationDao.getInstance().getUserID();
            mUserEmail = reservation.getCustomersEmail();
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            boolean res;

            if(mCancel)
            {
                res = ReservationDao.getInstance().CancelReservation(mReservationID, mUserID, mUserEmail);
            }
            else
            {
                res = ReservationDao.getInstance().ApplyReservation(mReservationID, mUserID, mUserEmail);
            }
            return res;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mParent.mTask = null;
            ProgressDialogUtil.showProgress(false, mParent.mEditForm, mParent.mEditProgress);

            if (success)
            {
                mParent.finish();
            }
            else
            {
                ProgressDialogUtil.ShowError(mParent.getString(R.string.internal_application_error), mParent);
            }
        }

        @Override
        protected void onCancelled() {
            mParent.mTask = null;
            ProgressDialogUtil.showProgress(false, mParent.mEditForm, mParent.mEditProgress);
        }
    }
}

