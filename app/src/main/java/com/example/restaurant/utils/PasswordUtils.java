package com.example.restaurant.utils;

import android.util.Base64;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    static public String MD5_Hash(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            return new BigInteger(1, array).toString(16);
            //return Base64.encodeToString(array, Base64.DEFAULT);
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

}
