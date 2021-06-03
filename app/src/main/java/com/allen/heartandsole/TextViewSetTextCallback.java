package com.allen.heartandsole;

import android.widget.TextView;

public class TextViewSetTextCallback {

    private final TextView textView;

    public TextViewSetTextCallback(TextView textView) {
        this.textView = textView;
    }

    public void setText(String text) {
        textView.setText(text);
    }
}
