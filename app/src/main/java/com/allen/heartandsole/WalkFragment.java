package com.allen.heartandsole;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class WalkFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_walk, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        String welcome = "Welcome, " + SharedPrefSingleton.getInstance().getUsername();
        ((TextView) view.findViewById(R.id.me_heading)).setText(welcome);
        resizeRelativeLayouts(view);
    }

    private void resizeRelativeLayouts(View view) {
        LinearLayout navButtons = view.findViewById(R.id.nav_buttons);
        int numChildren = navButtons.getChildCount();
        for (int i = 0; i < numChildren; i++) {
            View child = navButtons.getChildAt(i);
            if (!(child instanceof LinearLayout)) continue;
            LinearLayout ll = (LinearLayout) child;
            int llChildren = ll.getChildCount();
            for (int j = 0; j < llChildren; j++) {
                child = ll.getChildAt(j);
                if (!(child instanceof RelativeLayout)) continue;
                RelativeLayout layout = (RelativeLayout) child;
                layout.post(() -> {
                    ViewGroup.LayoutParams params = layout.getLayoutParams();
                    params.width = Math.min((int) (layout.getHeight() * 1.2), layout.getWidth());
                    layout.setLayoutParams(params);
                });
            }
        }
    }
}
