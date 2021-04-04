package io.wurmatron.petfeeder.schedule;

import io.wurmatron.petfeeder.PetFeeder;
import io.wurmatron.petfeeder.gpio.IOController;
import io.wurmatron.petfeeder.models.Consume;
import io.wurmatron.petfeeder.sql.SQLCache;

import java.sql.SQLException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ConsumptionHandler {

    public static double lastKnownWeight;
    public static AtomicBoolean currentlyTracking = new AtomicBoolean(false);


    public static void startTracking() {
        lastKnownWeight = IOController.getLoadCellWeight();
        PetFeeder.SCHEDULE.scheduleAtFixedRate(() -> {
            // Check if weight has changed, and is not currently tracked by another
            if (detectChanges() && !currentlyTracking.get()) {
                System.out.println("Food has been consumed, starting tracker");
                currentlyTracking.set(true);
                PetFeeder.SCHEDULE.schedule(ConsumptionHandler::trackConsumption, 0, TimeUnit.SECONDS);
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    public static boolean detectChanges() {
        boolean test = Math.round(IOController.getLoadCellWeight()) > Math.round(lastKnownWeight);
        lastKnownWeight = IOController.getLoadCellWeight();
        return test;
    }

    public static void trackConsumption() {
        Consume consume = new Consume();
        consume.startTimestamp = Instant.now().getEpochSecond();
        double startingAmount = IOController.getLoadCellWeight();
        AtomicLong lastUpdate = new AtomicLong(consume.startTimestamp);
        Thread th = new Thread(() -> {
            while (lastUpdate.get() < Instant.now().getEpochSecond() + PetFeeder.config.consumptionTimeout) {
                if (detectChanges()) {
                    lastUpdate.set(Instant.now().getEpochSecond());
                } else {
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            consume.timeInterval = (int) (consume.startTimestamp - Instant.now().getEpochSecond());
            consume.amount = startingAmount - IOController.getLoadCellWeight();
            System.out.println("No new food has been consumed, consumption completed! (" + consume.amount + "over " + consume.timeInterval + "s)");
            try {
                SQLCache.add(consume);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, "Consumption Tracker");
        th.start();
    }
}
