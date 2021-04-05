package io.wurmatron.petfeeder.models;

public class Schedule {

    public int scheduleID;
    public String name;
    public long nextInterval;
    public Day[] days;
    public String[] time;
    public int amount;

    public Schedule(int scheduleID, String name, long nextInterval, Day[] days, String[] time, int amount) {
        this.scheduleID = scheduleID;
        this.name = name;
        this.nextInterval = nextInterval;
        this.days = days;
        this.time = time;
        this.amount = amount;
    }

    public Schedule() {
    }

    public enum Day {
        SUNDAY, MONDAY, TUESDAY, WENDSDAY, TURSDAY, FRIDAY, SATURDAY;
    }
}
