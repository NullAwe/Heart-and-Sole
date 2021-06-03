package com.allen.heartandsole;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class AppInfoFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_info, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Fixes the ImageView background height.
        View bg = view.findViewById(R.id.unique_route_feature_bg),
                darkener = view.findViewById(R.id.darkener);
        bg.post(() -> {
            ViewGroup.LayoutParams params = bg.getLayoutParams();
            params.height = view.findViewById(R.id.unique_route_feature).getHeight();
            bg.setLayoutParams(params);
        });
        darkener.post(() -> {
            ViewGroup.LayoutParams params = darkener.getLayoutParams();
            params.height = view.findViewById(R.id.unique_route_feature).getHeight();
            darkener.setLayoutParams(params);
            darkener.getBackground().setAlpha(100);
        });
    }
}
