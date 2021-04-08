package io.wurmatron.petfeeder.threading;

import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import io.wurmatron.petfeeder.models.Schedule;
import io.wurmatron.petfeeder.routes.RouteGenerator;
import io.wurmatron.petfeeder.ui.main.RecycleViewAdapter;

public class ScheduleUpdateAsync extends AsyncTask<String, String, Schedule[]> {

    public RecyclerView recyclerView;

    public ScheduleUpdateAsync(RecyclerView view) {
        this.recyclerView = view;
    }

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