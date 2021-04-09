package io.wurmatron.petfeeder.threading;

import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import io.wurmatron.petfeeder.models.Consume;
import io.wurmatron.petfeeder.routes.RouteGenerator;
import io.wurmatron.petfeeder.ui.main.HistoryRecycleViewAdapter;

public class HistoryUpdateAsync extends AsyncTask<String, String, Consume[]> {

    public RecyclerView recyclerView;

    public HistoryUpdateAsync(RecyclerView view) {
        this.recyclerView = view;
    }

    @Override
    protected Consume[] doInBackground(String... strings) {
        if (recyclerView.getAdapter() != null)
            ((HistoryRecycleViewAdapter) recyclerView.getAdapter()).consumeList.clear();
        else
            ((HistoryRecycleViewAdapter) recyclerView.getAdapter()).consumeList = new ArrayList<>();
        Consume[] consumes = RouteGenerator.get("history/consume/1", Consume[].class);
        ((HistoryRecycleViewAdapter) recyclerView.getAdapter()).consumeList.addAll(Arrays.asList(consumes));
        return consumes;
    }

    @Override
    protected void onPostExecute(Consume[] consumes) {
        super.onPostExecute(consumes);
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}