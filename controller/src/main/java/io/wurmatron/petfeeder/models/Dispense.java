package io.wurmatron.petfeeder.models;

public class Dispense {

    public int amount;
    public long timestamp;
    public double before;
    public double after;

    public Dispense(int amount, long timestamp, double before, double after) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.before = before;
        this.after = after;
    }

    public Dispense() {
    }
}
