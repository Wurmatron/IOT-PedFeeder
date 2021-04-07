package io.wurmatron.petfeeder.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    Context context;
    List<Schedule> scheduleList;

    public RecycleViewAdapter(Context context, List<Schedule> scheduleList) {
        this.context = context;
        this.scheduleList = scheduleList;
    }

    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ScheduleViewHolder holder, int position) {
        holder.scheduleName.setText(scheduleList.get(position).name);
        holder.time.setText(String.join(", ", scheduleList.get(position).time));
        holder.date.setText(Arrays.toString(scheduleList.get(position).days));
        holder.amount.setText((scheduleList.get(position).amount * 100) + "g");
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

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            scheduleName = (TextView) itemView.findViewById(R.id.scheduleName);
            time = (TextView) itemView.findViewById(R.id.time);
            date = (TextView) itemView.findViewById(R.id.date);
            amount = (TextView) itemView.findViewById(R.id.amount);
        }
    }
}
