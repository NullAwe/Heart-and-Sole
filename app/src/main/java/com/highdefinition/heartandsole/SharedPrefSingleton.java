package com.highdefinition.heartandsole;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefSingleton {

    private static final String UN = "username", DEST_TYPE = "destType";
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

    public void setDestType(String type) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEST_TYPE, type);
        editor.apply();
    }

    public String getDestType() {
        return sharedPreferences.getString(DEST_TYPE, "All");
    }

    public static void setContext(Context context) {
        INSTANCE.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPrefSingleton getInstance() {
        return INSTANCE;
    }
}
