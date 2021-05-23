package com.allen.heartandsole;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This stores and retrieves user data from the local SharedPreferences storage.
 */
public class LocalUserAPI {

    private static final String ACC = "acc", UN = "username";

    private final Context context;

    public LocalUserAPI(Context context) {
        this.context = context;
    }

    public boolean hasAccount() {
        SharedPreferences acc = context.getSharedPreferences(ACC, Context.MODE_PRIVATE);
        return acc.getString(UN, null) != null;
    }

    public void addAccount(String username) {
        SharedPreferences acc = context.getSharedPreferences(ACC, Context.MODE_PRIVATE);
        if (acc.getString(UN, null) != null) return;
        SharedPreferences.Editor editor = acc.edit();
        editor.putString(UN, username);
        editor.apply();
    }

    public String getUsername() {
        return context.getSharedPreferences(ACC, Context.MODE_PRIVATE).getString(UN, null);
    }
}
