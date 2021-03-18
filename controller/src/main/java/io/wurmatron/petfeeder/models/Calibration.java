package io.wurmatron.petfeeder.models;

public class Calibration {

    public long emptyVal;
    public double emptyWeight;
    public long calibratedVal;
    public double calibratedWeight;
    public double diff;

    public Calibration(long emptyVal, double emptyWeight, long calibratedVal, double calibratedWeight, double diff) {
        this.emptyVal = emptyVal;
        this.emptyWeight = emptyWeight;
        this.calibratedVal = calibratedVal;
        this.calibratedWeight = calibratedWeight;
        this.diff = diff;
    }
}
