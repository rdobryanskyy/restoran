package com.example.restaurant.models;


public class Customer
{
    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    private String mEmail;
    public Customer(String email)
    {
        mEmail = email;
    }



}
