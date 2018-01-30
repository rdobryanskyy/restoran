package com.example.restaurant.models;

public class Dish
{
    protected  String dishID;
    protected  String dishName;
    protected  String dishPrice;


    public String getDishID() {
        return dishID;
    }

    public void setDishID(String dishID) {
        this.dishID = dishID;
    }


    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }


    public String getDishPrice() {
        return dishPrice;
    }

    public void setDishPrice(String dishPrice) {
        this.dishPrice = dishPrice;
    }


    public Dish(String id, String name, String price)
    {
        dishID = id;
        dishName = name;
        dishPrice = price;
    }

    public Dish (String xmlString)
    {

    }

    public String getDishBrief()
    {
        String brief = String.format("%s : price %s", getDishName(), getDishPrice());

        return brief;
    }
}

