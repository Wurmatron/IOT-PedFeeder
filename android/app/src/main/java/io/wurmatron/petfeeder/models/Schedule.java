package io.wurmatron.petfeeder.models;

public class Schedule {

    public Integer scheduleID;
    public String name;
    public Long nextInterval;
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

    public Schedule(String name, Day[] days, String[] time, int amount) {
        this.name = name;
        this.days = days;
        this.time = time;
        this.amount = amount;
        this.scheduleID = null;
        this.nextInterval = null;
    }

    public Schedule() {
    }

    public enum Day {
        MONDAY, TUESDAY, WENDSDAY, TURSDAY, FRIDAY, SATURDAY, SUNDAY;
    }
}
