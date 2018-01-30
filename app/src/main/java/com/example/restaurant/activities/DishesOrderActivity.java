package com.example.restaurant.activities;

import android.os.Bundle;
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

public class DishesOrderActivity extends AppCompatActivity {

    RecyclerView mListDishes;
    Reservation mReservation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dishes_order);
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

        mListDishes = findViewById(R.id.lv_dishes);
        String reservationID = ExtrasUtils.GetReservationID(this);
        if(reservationID == null)
        {
            finish();
            return;
        }
        mReservation = ReservationDao.getInstance().getReservationByID(reservationID);

        setupDishList();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void setupDishList()
    {
        mListDishes.setAdapter(new DishListAdapter( mReservation));
    }

    public static class DishListAdapter
            extends RecyclerView.Adapter<DishListAdapter.ViewHolder>
    {
        private Reservation mReservation;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                //TODO show dish details
            }
        };

        DishListAdapter( Reservation reservation) {
            mReservation = reservation;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dish_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position)
        {
            holder.mIdView.setText(mReservation.getOrderedDishes().get(position).getDishName());
            holder.mContentView.setText(mReservation.getOrderedDishes().get(position).getDishBrief());

            holder.itemView.setTag(mReservation.getOrderedDishes().get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.mSelected.setChecked(mReservation.getOrderedDishes().get(position).isSelected());
            holder.mSelected.setTag(mReservation.getOrderedDishes().get(position));

            holder.mSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    OrderedDish dish = (OrderedDish) cb.getTag();
                    dish.setSelected(cb.isChecked());
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return mReservation.getOrderedDishes().size();
        }

        class ViewHolder extends RecyclerView.ViewHolder
        {
            final TextView mIdView;
            final TextView mContentView;
            final CheckBox mSelected;

            ViewHolder(View view) {
                super(view);
                mIdView = view.findViewById(R.id.id_text);
                mContentView = view.findViewById(R.id.content);
                mSelected = view.findViewById(R.id.select);

            }
        }
    }
    }
