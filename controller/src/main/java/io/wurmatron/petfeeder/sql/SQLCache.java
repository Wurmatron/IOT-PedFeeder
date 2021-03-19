package io.wurmatron.petfeeder.sql;

import io.wurmatron.petfeeder.models.Consume;
import io.wurmatron.petfeeder.models.Dispense;
import io.wurmatron.petfeeder.models.Schedule;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

public class SQLCache extends SQLGenerator {

    private static final long FORCE_UPDATE_TIME = 5 * 60; // 5m in seconds

    // Cache
    private static final NonBlockingHashMap<Long, Consume> consumeHistory = new NonBlockingHashMap<>();
    private static final NonBlockingHashMap<Long, Dispense> dispenseHistory = new NonBlockingHashMap<>();
    private static final NonBlockingHashMap<Integer, Schedule> scheduleCache = new NonBlockingHashMap<>();
    private static long consumeLastUpdate;
    private static long dispenseLastUpdate;
    private static long scheduleLastUpdate;


    public static void add(Dispense dispense) throws SQLException {
        add(dispense);
        dispenseHistory.put(dispense.timestamp, dispense);
    }

    public static void add(Consume consume) throws SQLException {
        add(consume);
        consumeHistory.put(consume.startTimestamp, consume);
    }

    public static void add(Schedule schedule) throws SQLException {
        add(schedule);
        scheduleCache.put(schedule.scheduleID, schedule);
    }

    public static Consume[] getConsumeHistory() throws SQLException {
        if (consumeHistory.size() > 0 && Instant.now().getEpochSecond() > consumeLastUpdate + FORCE_UPDATE_TIME) {
            return consumeHistory.values().toArray(new Consume[0]);
        } else {
            List<Consume> history = getHistoryConsume();
            consumeHistory.clear();
            history.forEach(consume -> consumeHistory.put(consume.startTimestamp, consume));
            consumeLastUpdate = Instant.now().getEpochSecond();
            return history.toArray(new Consume[0]);
        }
    }

    public static Dispense[] getDispenseHistory() throws SQLException {
        if (dispenseHistory.size() > 0 && Instant.now().getEpochSecond() > dispenseLastUpdate + FORCE_UPDATE_TIME) {
            return dispenseHistory.values().toArray(new Dispense[0]);
        } else {
            List<Dispense> history = getHistoryDispense();
            dispenseHistory.clear();
            history.forEach(dispense -> dispenseHistory.put(dispense.timestamp, dispense));
            dispenseLastUpdate = Instant.now().getEpochSecond();
            return history.toArray(new Dispense[0]);
        }
    }

    public static Schedule[] getSchedules() throws SQLException, NumberFormatException {
        if (scheduleCache.size() > 0 && Instant.now().getEpochSecond() > scheduleLastUpdate + FORCE_UPDATE_TIME) {
            return scheduleCache.values().toArray(new Schedule[0]);
        } else {
            List<Schedule> schedules = getSchedule();
            scheduleCache.clear();
            schedules.forEach(schedule -> scheduleCache.put(schedule.scheduleID, schedule));
            scheduleLastUpdate = Instant.now().getEpochSecond();
            return schedules.toArray(new Schedule[0]);
        }
    }

    public static void deleteSchedule(int scheduleID) throws SQLException {
        deleteSchedule(scheduleID);
        scheduleCache.remove(scheduleID);
    }

    public static void updateSchedule(Schedule schedule) throws SQLException {
        updateSchedule(schedule);
        scheduleCache.remove(schedule.scheduleID);
        scheduleCache.put(schedule.scheduleID, schedule);
    }
}
