package io.wurmatron.petfeeder.models;

public class CalibrationLoadCell {

    public Stage stage;
    public double weight;

    public CalibrationLoadCell(Stage stage, double weight) {
        this.stage = stage;
        this.weight = weight;
    }

    public enum Stage {
        WITHOUT, WITH
    }
}
