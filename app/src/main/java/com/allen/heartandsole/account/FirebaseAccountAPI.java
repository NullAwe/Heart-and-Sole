package com.allen.heartandsole.account;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAccountAPI implements AccountAPI {

    private final SignUpResponseHandler signUpResponseHandler;
    private final SignInResponseHandler signInResponseHandler;


    public FirebaseAccountAPI(SignUpResponseHandler signUpResponseHandler,
                              SignInResponseHandler signInResponseHandler) {
        this.signUpResponseHandler = signUpResponseHandler;
        this.signInResponseHandler = signInResponseHandler;
    }

    @Override
    public void addAccount(Account acc) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(acc.getUsername());
        docRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) return;
            DocumentSnapshot docSnap = task.getResult();
            if (!docSnap.exists()) {
                Map<String, Object> data = new HashMap<>();
                data.put("password", acc.getPassword());
                db.collection("users").document(acc.getUsername()).set(data);
                signUpResponseHandler.handle(true);
            } else signUpResponseHandler.handle(false);
        });
    }

    @Override
    public void auth(Account acc) {
        String un = acc.getUsername();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(un);
        docRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) return;
            DocumentSnapshot docSnap = task.getResult();
            if (!docSnap.exists())
                signInResponseHandler.handle(SignInResponseHandler.Status.NO_USER, un);
            else if (!acc.getPassword().equals(docSnap.get("password")))
                signInResponseHandler.handle(SignInResponseHandler.Status.WRONG_PASSWORD, un);
            else
                signInResponseHandler.handle(SignInResponseHandler.Status.SUCCESS, un);
        });
    }
}
