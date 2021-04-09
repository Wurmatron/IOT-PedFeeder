package io.wurmatron.petfeeder.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import io.wurmatron.petfeeder.R;
import io.wurmatron.petfeeder.models.Schedule;

public class ScheduleRecycleViewAdapter extends RecyclerView.Adapter<ScheduleRecycleViewAdapter.ScheduleViewHolder> {

    public Context context;
    public static List<Schedule> scheduleList;

    public ScheduleRecycleViewAdapter(Context context, List<Schedule> scheduleList) {
        this.context = context;
        ScheduleRecycleViewAdapter.scheduleList = scheduleList;
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
