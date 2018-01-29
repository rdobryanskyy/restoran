package com.example.restaurant.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.restaurant.R;
import com.example.restaurant.database.ReservationDao;
import com.example.restaurant.models.Reservation;
import com.example.restaurant.utils.ExtrasUtils;

public class EditTableActivity extends AppCompatActivity {

    TextView mPersonsLabel;
    Reservation mReservation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_table);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mPersonsLabel = findViewById(R.id.persons_label);
        String reservationID = ExtrasUtils.GetReservationID(this);
        if(reservationID == null)
        {
            finish();
            return;
        }

        mReservation = ReservationDao.getInstance().getReservationByID(reservationID);
        mPersonsLabel.setText( mReservation.getNumOfVisitors().toString());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void onButtonClick(View v) {
        if (v.getId() == R.id.persons_set) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.get_num_pers));

            final EditText input = new EditText(this);

            input.setInputType(InputType.TYPE_CLASS_NUMBER );
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    String visitors = input.getText().toString();
                    mPersonsLabel.setText( visitors);
                    if(!visitors.isEmpty()) {
                        mReservation.setNumOfVisitors(Integer.parseInt(visitors));
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

}
