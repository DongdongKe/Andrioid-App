package com.example.android_mobile_app.dto;

public class SCR {
    private int index;

    private double value;

    public SCR(int index, double value) {
        this.index = index;
        this.value = value;
    }

    public SCR() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
