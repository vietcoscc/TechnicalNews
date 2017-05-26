package com.example.vaio.technicalnews.model.application;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vaio on 28/03/2017.
 */

public class MySharedPreferences {
    public static final String USER_NAME = "User name";
    public static final String PASSWORD = "Password";
    public static final String SHARED_PREF = "shared preferences";
    private static SharedPreferences sharedPreferences;

    public static void putString(Context context, String key, String value) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static void clearSharedPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
