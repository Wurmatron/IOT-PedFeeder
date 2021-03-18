package io.wurmatron.petfeeder.models;

public class Dispense {

    public int count;
    public long timestamp;

    public Dispense(int count, long timestamp) {
        this.count = count;
        this.timestamp = timestamp;
    }
}
