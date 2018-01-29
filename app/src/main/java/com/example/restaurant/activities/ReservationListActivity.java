package com.example.restaurant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.restaurant.R;
import com.example.restaurant.database.ReservationDao;
import com.example.restaurant.models.OrderedDish;
import com.example.restaurant.models.Reservation;
import com.example.restaurant.utils.ExtrasUtils;

import java.util.List;

public class ReservationListActivity extends AppCompatActivity {

    RecyclerView mListReservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mListReservations = (RecyclerView) findViewById(R.id.lv_dishes);

        setupReservationsList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void setupReservationsList()
    {
        mListReservations.setAdapter(new ReservationListActivity.ReservationListAdapter(this, ReservationDao.getInstance().getReservations()));
    }

    public static class ReservationListAdapter
            extends RecyclerView.Adapter<ReservationListActivity.ReservationListAdapter.ViewHolder> {
        private final ReservationListActivity mParent;
        private List<Reservation> mReservations;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String reservationID = view.getTag().toString();
                if(reservationID != null && !reservationID.isEmpty())
                {
                    Intent _i = new Intent(mParent.getBaseContext(), ReservationEditActivity.class);
                    _i.putExtra("ReservationID", reservationID);
                    mParent.startActivity(_i);
                }
            }
        };

        ReservationListAdapter(ReservationListActivity parent,  List<Reservation> reservations) {
            mReservations = reservations;
            mParent = parent;
        }

        @Override
        public ReservationListActivity.ReservationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dish_list_content, parent, false);
            return new ReservationListActivity.ReservationListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ReservationListActivity.ReservationListAdapter.ViewHolder holder, int position)
        {
            holder.mIdView.setText(mReservations.get(position).getBriefDescription());
            holder.itemView.setTag(mReservations.get(position).getReservationID());
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount()
        {
            return mReservations.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder
        {
            final TextView mIdView;
            ViewHolder(View view)
            {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
            }
        }
    }
}
