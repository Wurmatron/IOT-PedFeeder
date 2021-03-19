package io.wurmatron.petfeeder.sql;

import io.wurmatron.petfeeder.models.Consume;
import io.wurmatron.petfeeder.models.Dispense;
import io.wurmatron.petfeeder.models.Schedule;
import jdk.internal.joptsimple.internal.Strings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static io.wurmatron.petfeeder.PetFeeder.database;

public class SQLGenerator {

    protected static void add(Dispense dispense) throws SQLException {
        Statement statement = database.getConnection().createStatement();
        String query = "INSERT INTO dispense (timestamp, amount, weightBefore, weightAfter) VALUES ('%TIMESTAMP%', '%AMOUNT%', '%WEIGHT_BEFORE%', '%WEIGHT_AFTER%');"
                .replaceAll("%TIMESTAMP%", String.valueOf(dispense.timestamp))
                .replaceAll("%AMOUNT%", String.valueOf(dispense.amount))
                .replaceAll("%WEIGHT_BEFORE%", String.valueOf(dispense.before))
                .replaceAll("%WEIGHT_AFTER%", String.valueOf(dispense.after));
        statement.execute(query);
    }

    protected static void add(Consume consume) throws SQLException {
        Statement statement = database.getConnection().createStatement();
        String query = "INSERT INTO consume (timestamp, interval, amount) VALUES ('%TIMESTAMP%', '%INTERVAL%', '%AMOUNT%');"
                .replaceAll("%TIMESTAMP%", String.valueOf(consume.startTimestamp))
                .replaceAll("%INTERVAL%", String.valueOf(consume.timeInterval))
                .replaceAll("%AMOUNT%", String.valueOf(consume.amount));
        statement.execute(query);
    }

    protected static void add(Schedule schedule) throws SQLException {
        Statement statement = database.getConnection().createStatement();
        String query = "INSERT INTO schedule (scheduleID, name, nextInterval, days, time, amount) VALUES ('%SCHEDULE_ID%', '%NAME%', '%NEXT_INTERVAL%','%DAYS%', '%TIME%', '%AMOUNT%');"
                .replaceAll("%SCHEDULE_ID%", String.valueOf(schedule.scheduleID))
                .replaceAll("%NAME%", String.valueOf(schedule.name))
                .replaceAll("%NEXT_INTERVAL%", String.valueOf(schedule.nextInterval))
                .replaceAll("%DAYS%", daysToString(schedule.days))
                .replaceAll("%TIME%", Strings.join(schedule.time, ","))
                .replaceAll("%AMOUNT%", String.valueOf(schedule.amount));
        statement.execute(query);
    }

    protected static List<Consume> getHistoryConsume() throws SQLException, NumberFormatException {
        String query = "SELECT * FROM consume";
        List<Consume> consumeHistory = new ArrayList<>();
        Statement statement = database.getConnection().createStatement();
        ResultSet set = statement.executeQuery(query);
        while (set.next()) {
            Consume consume = new Consume();
            consume.startTimestamp = Long.parseLong(set.getString("timestamp"));
            consume.timeInterval = set.getInt("interval");
            consume.amount = set.getLong("amount");
            consumeHistory.add(consume);
        }
        return consumeHistory;
    }

    protected static List<Dispense> getHistoryDispense() throws SQLException, NumberFormatException {
        String query = "SELECT * FROM dispense";
        List<Dispense> dispenseHistory = new ArrayList<>();
        Statement statement = database.getConnection().createStatement();
        ResultSet set = statement.executeQuery(query);
        while (set.next()) {
            Dispense dispense = new Dispense();
            dispense.timestamp = Long.parseLong(set.getString("timestamp"));
            dispense.amount = Integer.parseInt(set.getString("amount"));
            dispense.before = Double.parseDouble(set.getString("weightBefore"));
            dispense.after = Double.parseDouble(set.getString("weightAfter"));
            dispenseHistory.add(dispense);
        }
        return dispenseHistory;
    }

    protected static List<Schedule> getSchedule() throws SQLException, NumberFormatException {
        String query = "SELECT * FROM schedule";
        List<Schedule> schedules = new ArrayList<>();
        Statement statement = database.getConnection().createStatement();
        ResultSet set = statement.executeQuery(query);
        while (set.next()) {
            Schedule schedule = new Schedule();
            schedule.scheduleID = set.getInt("scheduleID");
            schedule.name = set.getString("name");
            schedule.nextInterval = set.getLong("nextInterval");
            String[] sqlDays = set.getString("days").split(",");
            List<Schedule.Day> days = new ArrayList<>();
            for (String day : sqlDays) {
                days.add(Schedule.Day.valueOf(day.toUpperCase()));
            }
            schedule.days = days.toArray(new Schedule.Day[0]);
            schedule.time = set.getString("time").split(",");
            schedule.amount = set.getInt("amount");
            schedules.add(schedule);
        }
        return schedules;
    }

    protected static void deleteSchedule(int scheduleID) throws SQLException {
        String query = "DELETE FROM schedule WHERE scheduleID='%ID%' LIMIT 1;".replaceAll("%ID%", "" + scheduleID);
        Statement statement = database.getConnection().createStatement();
        statement.execute(query);
    }

    private static String daysToString(Schedule.Day[] days) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < builder.length(); index++) {
            if (index < builder.length() - 1) {
                builder.append(days[index]).append(", ");
            } else {    // Final Entry
                builder.append(days[index]);
            }
        }
        return builder.toString();
    }

    protected static void updateSchedule(Schedule schedule) throws SQLException {
        String query = "UPDATE schedule SET `name`='%NAME%', `nextInterval`='%NEXT_INTERVAL%', `days`='%DAYS%', `time`='%TIME%', `amount`='%AMOUNT%'"
                .replaceAll("%NAME%", schedule.name)
                .replaceAll("%NEXT_INTERVAL%", "" + schedule.nextInterval)
                .replaceAll("%DAYS%", daysToString(schedule.days))
                .replaceAll("%TIME%", Strings.join(schedule.time, ","))
                .replaceAll("%AMOUNT%", "" + schedule.amount);
        Statement statement = database.getConnection().createStatement();
        statement.execute(query);
    }
}
