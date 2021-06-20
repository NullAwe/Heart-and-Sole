package com.highdefinition.heartandsole;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SignUpFragment extends Fragment {

    private final SignUpResponseHandler handler;

    public SignUpFragment(SignUpResponseHandler handler) {
        super();
        this.handler = handler;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        view.findViewById(R.id.sign_up_button).setOnClickListener(l ->
                handler.onGo(((EditText) view.findViewById(R.id.username)).getText().toString()));
    }

    public interface SignUpResponseHandler {
        void onGo(String username);
    }
}
