package io.wurmatron.petfeeder.ui.main;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.wurmatron.petfeeder.MainActivity;
import io.wurmatron.petfeeder.R;
import io.wurmatron.petfeeder.models.Schedule;
import io.wurmatron.petfeeder.routes.RouteGenerator;
import io.wurmatron.petfeeder.threading.ScheduleUpdateAsync;


public class ScheduleFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private View v;
    private RecyclerView recyclerView;
    private List<Schedule> schedules;
    private HashMap<Schedule, TextView> editButtons;

    public static ScheduleFragment newInstance(int index) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_schedule, container, false);
        recyclerView = v.findViewById(R.id.schedule_recycleView);
        schedules = new ArrayList<>();
        RecycleViewAdapter viewAdapter = new RecycleViewAdapter(getContext(), schedules);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(viewAdapter);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ScheduleUpdateAsync sync = new ScheduleUpdateAsync(recyclerView);
        sync.execute("");
    }

}