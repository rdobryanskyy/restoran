package com.example.restaurant.utils;

/**
 * Created by alexeynikitenko on 1/29/18.
 */

public class PasswordUtils
{
    public static boolean isEmailValid(String email) {

        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}
