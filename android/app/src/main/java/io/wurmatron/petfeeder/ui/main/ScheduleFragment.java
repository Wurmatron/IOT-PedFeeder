package io.wurmatron.petfeeder.ui.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.wurmatron.petfeeder.MainActivity;
import io.wurmatron.petfeeder.R;
import io.wurmatron.petfeeder.models.Schedule;
import io.wurmatron.petfeeder.routes.RouteGenerator;


public class ScheduleFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private View v;
    private RecyclerView recyclerView;
    private List<Schedule> schedules;
    private long lastUpdate;
    private int UPDATE_PERIOD = 5 * 60000;

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
        recyclerView = (RecyclerView) v.findViewById(R.id.schedule_recycleView);
        schedules = new ArrayList<>();
        RecycleViewAdapter viewAdapter = new RecycleViewAdapter(getContext(), schedules);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(viewAdapter);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        ScheduleUpdateAsync sync = new ScheduleUpdateAsync();
        sync.execute("");
    }

    private class ScheduleUpdateAsync extends AsyncTask<String, String, Schedule[]> {

        @Override
        protected Schedule[] doInBackground(String... strings) {
            if (((RecycleViewAdapter) recyclerView.getAdapter()).scheduleList != null)
                ((RecycleViewAdapter) recyclerView.getAdapter()).scheduleList.clear();
            else
                ((RecycleViewAdapter) recyclerView.getAdapter()).scheduleList = new ArrayList<>();
            Schedule[] schedules = RouteGenerator.get("schedules", Schedule[].class);
            ((RecycleViewAdapter) recyclerView.getAdapter()).scheduleList.addAll(Arrays.asList(schedules));
            return schedules;
        }

        @Override
        protected void onPostExecute(Schedule[] schedules) {
            super.onPostExecute(schedules);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

}