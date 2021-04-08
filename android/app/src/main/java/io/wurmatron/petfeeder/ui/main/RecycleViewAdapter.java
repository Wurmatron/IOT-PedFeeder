package io.wurmatron.petfeeder.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.wurmatron.petfeeder.R;
import io.wurmatron.petfeeder.models.Schedule;
import io.wurmatron.petfeeder.routes.RouteGenerator;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ScheduleViewHolder> {

    public Context context;
    public static List<Schedule> scheduleList;

    public RecycleViewAdapter(Context context, List<Schedule> scheduleList) {
        this.context = context;
        RecycleViewAdapter.scheduleList = scheduleList;
    }

    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);
        holder.scheduleName.setText(schedule.name);
        holder.time.setText(String.join(", ", schedule.time));
        holder.date.setText(Arrays.toString(schedule.days));
        holder.amount.setText((schedule.amount * 100) + "g");
        holder.edit.setTag(R.string.scheduleStorage, schedule);
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {

        TextView scheduleName;
        TextView time;
        TextView date;
        TextView amount;
        Button edit;

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            scheduleName = itemView.findViewById(R.id.scheduleName);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
            edit = itemView.findViewById(R.id.scheduleEdit);
        }
    }
}
