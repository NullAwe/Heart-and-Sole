package com.allen.heartandsole.solely_for_you;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.allen.heartandsole.R;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.emitters.StreamEmitter;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class SolelyForYouDoneFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solely_for_you_done, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KonfettiView confetti = view.findViewById(R.id.confetti);
        confetti.post(() -> {
            int width = confetti.getWidth();
            Drawable icon = AppCompatResources.getDrawable(requireContext(),
                    R.drawable.heart_and_sole_icon);
            if (icon == null) return;
            confetti.build()
                    .addColors(ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                            ContextCompat.getColor(requireContext(), R.color.lightBlue))
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 3f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(Shape.Square.INSTANCE, new Shape.DrawableShape(icon, false))
                    .addSizes(new Size(12, 5f))
                    .setPosition(-50f, width + 50f, -50f, -50f)
                    .streamFor(150, StreamEmitter.INDEFINITE);
        });
    }
}
