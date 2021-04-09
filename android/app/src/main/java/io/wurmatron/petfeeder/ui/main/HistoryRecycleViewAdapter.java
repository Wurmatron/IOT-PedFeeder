package io.wurmatron.petfeeder.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import io.wurmatron.petfeeder.R;
import io.wurmatron.petfeeder.models.Consume;

public class HistoryRecycleViewAdapter extends RecyclerView.Adapter<HistoryRecycleViewAdapter.HistoryViewHolder> {

    public Context context;
    public static List<Consume> consumeList;
    private static String pattern = "MM-dd-YY";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    public HistoryRecycleViewAdapter(Context context, List<Consume> consumeList) {
        this.context = context;
        HistoryRecycleViewAdapter.consumeList = consumeList;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        Consume consume = consumeList.get(position);
        holder.historyTime.setText(Math.round(consume.timeInterval) + "s");
        Date date = Date.from(Instant.ofEpochSecond(consume.startTimestamp));
        holder.historyDate.setText(simpleDateFormat.format(date));
        holder.historyAmount.setText(consume.amount + "g");
    }

    @Override
    public int getItemCount() {
        return consumeList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView historyDate;
        TextView historyTime;
        TextView historyAmount;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            historyDate = itemView.findViewById(R.id.historyDate);
            historyTime = itemView.findViewById(R.id.historyTime);
            historyAmount = itemView.findViewById(R.id.historyConsumed);
        }
    }
}
