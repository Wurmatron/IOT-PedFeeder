package io.wurmatron.petfeeder.threading;

import android.os.AsyncTask;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.wurmatron.petfeeder.R;
import io.wurmatron.petfeeder.models.Consume;
import io.wurmatron.petfeeder.models.Dispense;
import io.wurmatron.petfeeder.routes.RouteGenerator;
import io.wurmatron.petfeeder.ui.main.HistoryRecycleViewAdapter;

public class HistoryUpdateAsync extends AsyncTask<String, String, Consume[]> {

    public RecyclerView recyclerView;
    public XYPlot plot;
    private Consume[] consume;
    private Dispense[] dispense;
    private int month;
    private int year;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH:mm");

    public HistoryUpdateAsync(RecyclerView recyclerView, int month, int year, XYPlot plot) {
        this.recyclerView = recyclerView;
        this.plot = plot;
        this.month = month;
        this.year = year;

    }

    @Override
    protected Consume[] doInBackground(String... strings) {
        if (recyclerView.getAdapter() != null)
            ((HistoryRecycleViewAdapter) recyclerView.getAdapter()).consumeList.clear();
        else
            ((HistoryRecycleViewAdapter) recyclerView.getAdapter()).consumeList = new ArrayList<>();
        consume = RouteGenerator.get("history/consume/" + 1, Consume[].class);
        dispense = RouteGenerator.get("history/dispense/" + 2, Dispense[].class);
        ((HistoryRecycleViewAdapter) recyclerView.getAdapter()).consumeList.addAll(Arrays.asList(consume));
        return consume;
    }

    @Override
    protected void onPostExecute(Consume[] consumes) {
        super.onPostExecute(consumes);
        recyclerView.getAdapter().notifyDataSetChanged();
        // Domain Labels
        List<Number> labels = new ArrayList<>();
        for (int day = 1; day < 30; day++) {
            labels.add(day);
        }
        // Consume Series
        List<Number> consumeX = new ArrayList<>();
        for (int day = 1; day < 30; day++) {
            consumeX.add(getForDay(consume, month, day, year));
        }
        XYSeries series1 = new SimpleXYSeries(consumeX, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Consume");
        LineAndPointFormatter series1Format = new LineAndPointFormatter(plot.getContext(), R.xml.line_point_formatter_with_labels);
        series1Format.setInterpolationParams(new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
        plot.addSeries(series1, series1Format);
        // Dispense Series
        List<Number> dispsnseX = new ArrayList<>();
        for (int day = 1; day < 30; day++) {
            dispsnseX.add(getForDay(dispense, month, day, year));
        }
        XYSeries series2 = new SimpleXYSeries(dispsnseX, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Dispense");
        LineAndPointFormatter series2Format = new LineAndPointFormatter(plot.getContext(), R.xml.line_point_formatter2_with_labels);
        series2Format.setInterpolationParams(new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
        plot.addSeries(series2, series2Format);

        Number[] domainLabels = labels.toArray(new Number[0]);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i]);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
    }

    private static Number getForDay(Consume[] data, int month, int day, int year) {
        int total = 0;
        try {
            Date startDate = DATE_FORMAT.parse(year + "-" + ((month > 9) ? month : "0" + month) + "-" + (day > 9 ? day : "0" + day) + "-00:00");
            long start = startDate.getTime() / 1000;
            Date endDate = DATE_FORMAT.parse(year + "-" + ((month > 9) ? month : "0" + month) + "-" + (day > 9 ? day : "0" + day) + "-23:59");
            long end = endDate.getTime() / 1000;
            for (Consume consume : data)
                if (consume.startTimestamp >= start && end >= (consume.startTimestamp + consume.timeInterval))
                    total += consume.amount;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    private static Number getForDay(Dispense[] data, int month, int day, int year) {
        int total = 0;
        try {
            Date startDate = DATE_FORMAT.parse(year + "-" + ((month > 9) ? month : "0" + month) + "-" + (day > 9 ? day : "0" + day) + "-00:00");
            long start = startDate.getTime() / 1000;
            Date endDate = DATE_FORMAT.parse(year + "-" + ((month > 9) ? month : "0" + month) + "-" + (day > 9 ? day : "0" + day) + "-23:59");
            long end = endDate.getTime() / 1000;
            for (Dispense dispense : data)
                if (dispense.timestamp >= start && end >= (dispense.timestamp + 60))
                    total += dispense.amount;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }


}