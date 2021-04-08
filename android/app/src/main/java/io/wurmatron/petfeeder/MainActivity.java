package io.wurmatron.petfeeder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.wurmatron.petfeeder.models.Schedule;
import io.wurmatron.petfeeder.routes.RouteGenerator;
import io.wurmatron.petfeeder.threading.ScheduleUpdateAsync;
import io.wurmatron.petfeeder.ui.main.ScheduleFragment;
import io.wurmatron.petfeeder.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences sharedpreferences;
    public static PopupWindow window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        sharedpreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        loadPreferences();
    }

    private void loadPreferences() {
        RouteGenerator.BASE_URL = "http://" + sharedpreferences.getString("ip", "192.168.1.X") + ":8080/";
        RouteGenerator.token = sharedpreferences.getString("token", "");
    }

    public void onEditSchedule(View view) {
        Schedule schedule = (Schedule) view.getTag(R.string.scheduleStorage);
        View child = getLayoutInflater().inflate(R.layout.editschedule_popup, null);
        PopupWindow pw = createPopup(child);
        // Load Schedule values
        EditText scheduleName = child.findViewById(R.id.editScheduleName);
        scheduleName.setText(schedule.name);
        EditText scheduleDays = child.findViewById(R.id.editScheduleDays);
        StringBuilder days = new StringBuilder();
        for (Schedule.Day day : schedule.days) {
            days.append(day.toString().substring(0, 1).toUpperCase() + day.toString().substring(1).toLowerCase() + ",");
        }
        scheduleDays.setText(schedule.days.length == 0 ? "" : days.toString().substring(0, days.toString().length() - 1));
        EditText scheduleTime = child.findViewById(R.id.editScheduleTime);
        StringBuilder time = new StringBuilder();
        for (String t : schedule.time) {
            time.append(t).append(",");
        }
        String timeDisp = time.toString();
        if (schedule.time.length > 0) {
            timeDisp = timeDisp.substring(0, timeDisp.length() - 1);
        }
        scheduleTime.setText(timeDisp);
        EditText scheduleAmount = child.findViewById(R.id.editScheduleAmount);
        scheduleAmount.setText("" + schedule.amount);
        Button button = child.findViewById(R.id.confirmSchedule);
        button.setTag(R.string.scheduleStorage, schedule);
        // Display Popup
        pw.showAtLocation(view.findViewById(R.id.scheduleEdit), Gravity.CENTER, 0, 0);
    }

    public void onCreateSchedule(View view) {
        View child = getLayoutInflater().inflate(R.layout.editschedule_popup, null);
        PopupWindow pw = createPopup(child);
        pw.showAtLocation(view.findViewById(R.id.scheduleCreate), Gravity.CENTER, 0, 0);
    }

    public void onSaveSchedule(View view) {
        Schedule schedule;
        Schedule oldSchedule = null;
        if (view.getTag(R.string.scheduleStorage) != null) {
            oldSchedule = (Schedule) view.getTag(R.string.scheduleStorage);
        }
        EditText scheduleName = window.getContentView().findViewById(R.id.editScheduleName);
        String name = scheduleName.getText().toString();
        EditText scheduleDays = window.getContentView().findViewById(R.id.editScheduleDays);
        List<Schedule.Day> listDays = new ArrayList<>();
        if (scheduleDays.getText().toString().length() > 0)
            for (String d : scheduleDays.getText().toString().split(",")) {
                listDays.add(Schedule.Day.valueOf(d.toUpperCase()));
            }
        Schedule.Day[] days = listDays.toArray(new Schedule.Day[0]);
        EditText scheduleTime = window.getContentView().findViewById(R.id.editScheduleTime);
        String[] time = scheduleTime.getText().toString().isEmpty() ? new String[0] : scheduleTime.getText().toString().contains(",") ? scheduleTime.getText().toString().split(",") : new String[]{scheduleTime.getText().toString()};
        EditText scheduleAmount = window.getContentView().findViewById(R.id.editScheduleAmount);
        int amount = Integer.parseInt(scheduleAmount.getText().toString().isEmpty() ? "1" : scheduleAmount.getText().toString());
        schedule = new Schedule(name, days, time, amount);
        String json = RouteGenerator.GSON.toJson(schedule);
        if (oldSchedule != null)    // Existing Schedule
            RouteGenerator.put("schedule", schedule);
        else {  // New Schedule
            RouteGenerator.post("schedule", schedule);
        }
        window.dismiss();
        // Resync Changes
        ScheduleUpdateAsync sync = new ScheduleUpdateAsync(findViewById(R.id.schedule_recycleView));
        sync.execute("");
    }

    public PopupWindow createPopup(View child) {
        PopupWindow pw = new PopupWindow(child, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT, true);
        pw.setAnimationStyle(R.style.popupwindow_schedule);
        window = pw;
        return pw;
    }
}