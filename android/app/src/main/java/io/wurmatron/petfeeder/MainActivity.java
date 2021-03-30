package io.wurmatron.petfeeder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;

import io.wurmatron.petfeeder.routes.RouteGenerator;
import io.wurmatron.petfeeder.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences sharedpreferences;

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
}