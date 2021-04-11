package io.wurmatron.petfeeder.ui.main;

import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.wurmatron.petfeeder.R;
import io.wurmatron.petfeeder.models.Consume;
import io.wurmatron.petfeeder.models.Schedule;
import io.wurmatron.petfeeder.threading.HistoryUpdateAsync;
import io.wurmatron.petfeeder.threading.ScheduleUpdateAsync;


public class HistoryFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private View v;
    private RecyclerView recyclerView;
    private List<Consume> consumes;
    private XYPlot plot;
    private EditText editTextDate;

    public static HistoryFragment newInstance(int index) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = v.findViewById(R.id.history_recycleView);
        consumes = new ArrayList<>();
        HistoryRecycleViewAdapter viewAdapter = new HistoryRecycleViewAdapter(getContext(), consumes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(viewAdapter);
        plot = v.findViewById(R.id.monthGraph);
        editTextDate = v.findViewById(R.id.editTextDate);
        LocalDate date = LocalDate.now();
        editTextDate.setText(date.getMonthValue() + "/" + date.getYear());
        editTextDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                forceUpdate();
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        forceUpdate();
    }

    private void forceUpdate() {
        String date = editTextDate.getText().toString();
        int month = 4;
        int year = 2021;
        if (date.contains("/")) {
            String[] split = date.split("/");
            month = Integer.parseInt(split[0]);
            year = Integer.parseInt(split[1]);
        }
        HistoryUpdateAsync sync = new HistoryUpdateAsync(recyclerView, month, year, plot);
        sync.execute("");
    }

}