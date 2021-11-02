package com.example.android_mobile_app.dto;

public class MeasurementDTO {

    private String ID;

    private String wearable;

    private double value;

    public MeasurementDTO(String ID, Double value) {
        this.ID = ID;
        this.value = value;
    }

    public MeasurementDTO() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getWearable() {
        return wearable;
    }

    public void setWearable(String wearable) {
        this.wearable = wearable;
    }
}
