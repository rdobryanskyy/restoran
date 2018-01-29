package com.example.restaurant.models;


public class OrderedDish
{
    Dish mDish;
    protected boolean selected;



    public OrderedDish(Dish dish)
    {

        mDish = dish;
        selected = false;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getDishID()
    {
        return mDish.getDishID();
    }

    public String getDishName()
    {
        return  mDish.getDishName();
    }

    public String getDishBrief()
    {
        return mDish.getDishBrief();
    }

};
