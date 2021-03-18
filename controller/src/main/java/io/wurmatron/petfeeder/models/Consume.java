package io.wurmatron.petfeeder.models;

public class Consume {

    public long startTimestamp;
    public int timeInterval;
    public double amount;

    public Consume(long startTimestamp, int timeInterval, double amount) {
        this.startTimestamp = startTimestamp;
        this.timeInterval = timeInterval;
        this.amount = amount;
    }
}
