package io.wurmatron.petfeeder.routes;

import android.graphics.Color;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.wurmatron.petfeeder.models.Weight;

public class UpdateHelper {

    public static void updateWeight(TextView view) {
        RouteGenerator.EXECUTORS.schedule(() -> {
            try {
                Weight result = RouteGenerator.postResults("sensor/weight", "GET", Weight.class);
                if (result != null) {
                    view.setText(((int) result.weight) + "g");
                } else {
                    view.setTextColor(Color.rgb(255, 0, 0));
                    view.setText("Err");
                }
            } catch (Exception e) {
                view.setTextColor(Color.rgb(255, 0, 0));
                view.setText("Err");
            }
        }, 0, TimeUnit.SECONDS);
    }
}
