package io.wurmatron.petfeeder.ui.main;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.TimeUnit;

import io.wurmatron.petfeeder.R;
import io.wurmatron.petfeeder.models.Results;
import io.wurmatron.petfeeder.models.Weight;
import io.wurmatron.petfeeder.routes.RouteGenerator;
import io.wurmatron.petfeeder.routes.UpdateHelper;

import static io.wurmatron.petfeeder.MainActivity.sharedpreferences;


public class SettingsFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

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
    // Misc
    private EditText ipAddr;
    private TextView isConnected;
    private EditText token;


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
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Load Preferences
        try {
            // LED
            testLED = getView().findViewById(R.id.testLED);
            ledBlinkTime = getView().findViewById(R.id.ledBlinkCount);
            ledUp = getView().findViewById(R.id.ledUP);
            ledDown = getView().findViewById(R.id.ledDown);
            setupLEDClickListeners();
            // Servo
            testServo = getView().findViewById(R.id.testServo);
            servoTime = getView().findViewById(R.id.servoTime);
            servoUp = getView().findViewById(R.id.servoTimeUp);
            servoDown = getView().findViewById(R.id.servoTimeDown);
            setupServoClickListeners();
            // Photo
            testPhoto = getView().findViewById(R.id.testPhoto);
            photoResults = getView().findViewById(R.id.photoDisplay);
            // Load Cell
            testLoad = getView().findViewById(R.id.testWeight);
            loadResults = getView().findViewById(R.id.weightDisplay);
            setupUpdateListeners();
            // IP / Token
            ipAddr = getView().findViewById(R.id.editIP);
            token = getView().findViewById(R.id.editToken);
            isConnected = getView().findViewById(R.id.isConnected);
            setupEditUpdates();
            // Setup Default settings
            updatePhotoStatus();
            updatePhotoStatus();
            updateConnectionStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupServoClickListeners() {
        testServo.setOnClickListener(v -> {
            int iTime = Integer.parseInt(servoTime.getText().toString());
            System.out.println("Servo");
            RouteGenerator.postQuery("sensor/servo", "?time=" + iTime);
        });
        servoDown.setOnClickListener(v -> {
            int current = Integer.parseInt(servoTime.getText().toString());
            current -= 50;
            if (current < 0)
                current = 0;
            servoTime.setText(String.valueOf(current));
        });
        servoUp.setOnClickListener(v -> {
            int current = Integer.parseInt(servoTime.getText().toString());
            current += 50;
            servoTime.setText(String.valueOf(current));
        });
    }

    public void setupLEDClickListeners() {
        testLED.setOnClickListener((v -> {
            int iCount = Integer.parseInt(ledBlinkTime.getText().toString());
            RouteGenerator.postQuery("sensor/led", "?count=" + iCount);
        }));
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
    }

    public void setupUpdateListeners() {
        updateWeightStatus();
        testLoad.setOnClickListener(v -> {
            updateWeightStatus();
        });
        testPhoto.setOnClickListener(v -> {
            updatePhotoStatus();
        });
    }

    public void setupEditUpdates() {
        ipAddr.setText(sharedpreferences.getString("ip", "192.168.1.1"));
        ipAddr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                RouteGenerator.BASE_URL = "http://" + s.toString() + ":8080/";
                updateConnectionStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {
                RouteGenerator.BASE_URL = "http://" + s.toString() + ":8080/";
                updateConnectionStatus();
            }
        });
        token.setText(sharedpreferences.getString("token", ""));
        token.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                RouteGenerator.token = s.toString();
                updateConnectionStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {
                RouteGenerator.token = s.toString();
                updateConnectionStatus();
            }
        });
    }

    public void updateConnectionStatus() {
        RouteGenerator.EXECUTORS.schedule(() -> {
            try {
                Weight result = RouteGenerator.postResults("sensor/weight", "GET", Weight.class);
                if (result != null) {
                    isConnected.setText("Yes");
                    isConnected.setTextColor(Color.rgb(0, 255, 0));
                } else {
                    isConnected.setText("No");
                    isConnected.setTextColor(Color.rgb(255, 0, 0));
                }
            } catch (Exception e) {
                isConnected.setText("No");
                isConnected.setTextColor(Color.rgb(255, 0, 0));
            }
            updatePreferences();
        }, 0, TimeUnit.SECONDS);
    }

    public void updatePhotoStatus() {
        RouteGenerator.EXECUTORS.schedule(() -> {
            try {
                Results result = RouteGenerator.postResults("sensor/level", "POST", Results.class);
                if (result != null) {
                    if (Boolean.getBoolean(result.result)) {
                        photoResults.setTextColor(Color.rgb(255, 0, 0));
                        photoResults.setText("Empty");
                    } else {
                        photoResults.setTextColor(Color.rgb(0, 255, 0));
                        photoResults.setText("Full");
                    }
                } else {
                    photoResults.setTextColor(Color.rgb(255, 0, 0));
                    photoResults.setText("Err");
                }
            } catch (Exception e) {
                photoResults.setTextColor(Color.rgb(255, 0, 0));
                photoResults.setText("Err");
            }
        }, 0, TimeUnit.SECONDS);
    }

    public void updateWeightStatus() {
        UpdateHelper.updateWeight(loadResults);
    }

    public void updatePreferences() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("ip", ipAddr.getText().toString());
        editor.putString("token", token.getText().toString());
        editor.apply();
    }


}