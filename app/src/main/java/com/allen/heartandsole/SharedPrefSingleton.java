package com.allen.heartandsole;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefSingleton {

    private static final String UN = "username";
    private static final SharedPrefSingleton INSTANCE = new SharedPrefSingleton();

    private SharedPreferences sharedPreferences;

    private SharedPrefSingleton() {}

    public boolean hasAccount() {
        return sharedPreferences.getString(UN, null) != null;
    }

    public void addAccount(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(UN, username);
        editor.apply();
    }

    public String getUsername() {
        return sharedPreferences.getString(UN, null);
    }

    public static void setContext(Context context) {
        INSTANCE.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPrefSingleton getInstance() {
        return INSTANCE;
    }
}
