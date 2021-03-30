package io.wurmatron.petfeeder.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.TimeUnit;

import io.wurmatron.petfeeder.R;
import io.wurmatron.petfeeder.models.Results;
import io.wurmatron.petfeeder.models.Weight;
import io.wurmatron.petfeeder.routes.RouteGenerator;


public class SettingsFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    // Buttons
    // LED
    private Button testLED;
    private TextView ledBlinkTime;
    private Button ledUp;
    private Button ledDown;
    // Servo
    private Button testServo;
    private TextView servoTime;
    private Button servoUp;
    private Button servoDown;
    // Photo
    private Button testPhoto;
    private TextView photoResults;
    // Load / Weight
    private Button testLoad;
    private TextView loadResults;

    public static SettingsFragment newInstance(int index) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
        pageViewModel.name = getResources().getString(SectionsPagerAdapter.TAB_TITLES[index - 1]);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            // LED
            testLED = getView().findViewById(R.id.testLED);
            ledBlinkTime = getView().findViewById(R.id.ledBlinkCount);
            testLED.setOnClickListener((v -> {
                int iCount = Integer.parseInt(ledBlinkTime.getText().toString());
                RouteGenerator.postQuery("sensor/led", "?count=" + iCount);
            }));
            ledUp = getView().findViewById(R.id.ledUP);
            ledDown = getView().findViewById(R.id.ledDown);
            ledDown.setOnClickListener(v -> {
                int current = Integer.parseInt(ledBlinkTime.getText().toString());
                current--;
                if (current < 1)
                    current = 1;
                ledBlinkTime.setText(String.valueOf(current));
            });
            ledUp.setOnClickListener(v -> {
                int current = Integer.parseInt(ledBlinkTime.getText().toString());
                current++;
                ledBlinkTime.setText(String.valueOf(current));
            });
            // Servo
            testServo = getView().findViewById(R.id.testServo);
            servoTime = getView().findViewById(R.id.servoTime);
            testServo.setOnClickListener(v -> {
                int iTime = Integer.parseInt(servoTime.getText().toString());
                RouteGenerator.postQuery("sensor/servo", "?time=" + iTime);
            });
            servoUp = getView().findViewById(R.id.servoTimeUp);
            servoDown = getView().findViewById(R.id.servoTimeDown);
            servoDown.setOnClickListener(v -> {
                int current = Integer.parseInt(servoTime.getText().toString());
                current--;
                if (current < 0)
                    current = 0;
                servoTime.setText(String.valueOf(current));
            });
            servoUp.setOnClickListener(v -> {
                int current = Integer.parseInt(servoTime.getText().toString());
                current++;
                servoTime.setText(String.valueOf(current));
            });
            // Photo
            testPhoto = getView().findViewById(R.id.testPhoto);
            photoResults = getView().findViewById(R.id.photoDisplay);
            testPhoto.setOnClickListener(v -> {
                RouteGenerator.EXECUTORS.schedule(() -> {
                    Results result = RouteGenerator.postResults("sensor/level","POST", Results.class);
                    if (result != null) {
                        if (Boolean.getBoolean(result.result)) {
                            photoResults.setTextColor(Color.rgb(255, 0, 0));
                            photoResults.setText("Empty");
                        } else {
                            photoResults.setTextColor(Color.rgb(0, 255, 0));
                            photoResults.setText("Full");
                        }
                    } else {
                        photoResults.setText("Err");
                    }
                }, 0, TimeUnit.SECONDS);
            });
            // Load Cell
            testLoad = getView().findViewById(R.id.testWeight);
            loadResults = getView().findViewById(R.id.weightDisplay);
            testLoad.setOnClickListener(v -> {
                RouteGenerator.EXECUTORS.schedule(() -> {
                    Weight result = RouteGenerator.postResults("sensor/weight","GET", Weight.class);
                    if (result != null) {
                        loadResults.setText(((int) result.weight) + "g");
                    } else {
                        loadResults.setText("Err");
                    }
                }, 0, TimeUnit.SECONDS);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}