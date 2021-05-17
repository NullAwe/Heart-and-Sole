package com.allen.heartandsole;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesAccountAPI implements AccountAPI {

    private final Context context;
    private SignUpResponseHandler signUpResponseHandler;
    private SignInResponseHandler signInResponseHandler;

    public SharedPreferencesAccountAPI(Context context, SignUpResponseHandler signUpResponseHandler,
                                       SignInResponseHandler signInResponseHandler) {
        this.context = context;
        this.signUpResponseHandler = signUpResponseHandler;
        this.signInResponseHandler = signInResponseHandler;
    }

    @Override
    public void addAccount(Account acc) {
        SharedPreferences sp = context.getSharedPreferences(acc.getUsername(), Context.MODE_PRIVATE);
        if (sp.getString("username", "").length() > 0) {
            signUpResponseHandler.handle(false);
            return;
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", acc.getUsername());
        editor.putString("password", acc.getPassword());
        editor.apply();
        signUpResponseHandler.handle(true);
    }

    @Override
    public void auth(Account acc) {
        String un = acc.getUsername();
        SharedPreferences sp = context.getSharedPreferences(un, Context.MODE_PRIVATE);
        Account get = new Account(sp.getString("username", null), sp.getString("password", null));
        if (get.getUsername() == null)
            signInResponseHandler.handle(SignInResponseHandler.Status.NO_USER, un);
        else if (!acc.getPassword().equals(get.getPassword()))
            signInResponseHandler.handle(SignInResponseHandler.Status.WRONG_PASSWORD, un);
        else
            signInResponseHandler.handle(SignInResponseHandler.Status.SUCCESS, un);
    }
}
