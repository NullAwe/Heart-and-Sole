package com.allen.heartandsole;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class WalkFragment extends Fragment {

    private final LocalUserAPI userAPI;

    public WalkFragment(LocalUserAPI userAPI) {
        this.userAPI = userAPI;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_walk, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        makeNavButtonsCircular(view);
        if (!userAPI.hasAccount()) {
            Bundle bundle = new Bundle();
            bundle.putBinder("userAPI", new BinderWrapper<>(userAPI));
            Intent intent = new Intent(getContext(), SignUpActivity.class);
            startActivity(intent.putExtras(bundle));
            return;
        }
        String welcome = "Welcome, " + userAPI.getUsername();
        ((TextView) view.findViewById(R.id.me_heading)).setText(welcome);
    }

    private static void makeNavButtonsCircular(View view) {
        LinearLayout navButtons = view.findViewById(R.id.nav_buttons);
        for (int i = 0; i < navButtons.getChildCount(); i++) {
            View next = navButtons.getChildAt(i);
            if (!(next instanceof LinearLayout)) continue;
            LinearLayout ll = (LinearLayout) next;
            for (int j = 0; j < ll.getChildCount(); j++) {
                next = ll.getChildAt(j);
                if (!(next instanceof RelativeLayout)) continue;
                RelativeLayout rl = (RelativeLayout) next;
                for (int k = 0; k < rl.getChildCount(); k++) {
                    next = rl.getChildAt(k);
                    if (next instanceof ImageButton) {
                        ImageButton button = (ImageButton) next;
                        button.post(() -> {
                            ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                            layoutParams.width = button.getHeight();
                            button.setLayoutParams(layoutParams);
                            button.postInvalidate();
                        });
                    }
                }
            }
        }
    }
}
