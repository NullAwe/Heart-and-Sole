package com.allen.heartandsole;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesAccountAPI implements AccountAPI {

    private final Context context;

    public SharedPreferencesAccountAPI(Context context) {
        this.context = context;
    }

    @Override
    public boolean addAccount(Account acc) {
        SharedPreferences sp = context.getSharedPreferences(acc.getUsername(), Context.MODE_PRIVATE);
        if (sp.getString("username", "").length() > 0) return false;
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", acc.getUsername());
        editor.putString("password", acc.getPassword());
        editor.apply();
        return true;
    }

    @Override
    public Account getAccount(String name) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return new Account(sp.getString("username", ""), sp.getString("password", ""));
    }
}
