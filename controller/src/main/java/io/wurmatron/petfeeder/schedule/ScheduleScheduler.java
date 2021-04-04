package io.wurmatron.petfeeder.schedule;

import io.wurmatron.petfeeder.PetFeeder;
import io.wurmatron.petfeeder.endpoints.ScheduleController;
import io.wurmatron.petfeeder.models.Schedule;
import io.wurmatron.petfeeder.sql.SQLCache;

import java.sql.SQLException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class ScheduleScheduler {

    public static void startup() {
        System.out.println("Starting Scheduler");
        PetFeeder.SCHEDULE.scheduleAtFixedRate(ScheduleScheduler::checkForSchedule, 0, PetFeeder.config.schedulePollInterval, TimeUnit.SECONDS);
    }

    private static void checkForSchedule() {
        Schedule nextSchedule = getNextSchedule();
        if (nextSchedule != null) {
            long timeInSec = (nextSchedule.nextInterval - Instant.now().getEpochSecond());
            if (timeInSec <= (5 * 60)) {
                System.out.println("Schedule '" + nextSchedule.name + "@" + nextSchedule.scheduleID + "' is set to run within the next 5 min");
                PetFeeder.SCHEDULE.schedule(() -> {
                    runSchedule(nextSchedule);
                }, timeInSec, TimeUnit.SECONDS);
            }
        }
    }


    public static void runSchedule(Schedule schedule) {
        System.out.println("Running Schedule: '" + schedule.name + "@" + schedule.scheduleID + "'");
        // TODO Run Schedule
        schedule.nextInterval = ScheduleController.calculateNextInterval(schedule);
        try {
            SQLCache.updateSchedule(schedule);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Schedule getNextSchedule() {
        try {
            Schedule[] schedules = SQLCache.getSchedules();
            long nextSchedule = Long.MAX_VALUE;
            Schedule schedule = null;
            for (Schedule test : schedules) {
                if (test.nextInterval < nextSchedule && test.nextInterval > Instant.now().getEpochSecond()) {
                    nextSchedule = test.nextInterval;
                    schedule = test;
                }
            }
            return schedule;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
