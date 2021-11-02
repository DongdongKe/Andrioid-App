package com.example.android_mobile_app.domain;

import java.io.Serializable;

public class MeasurementValue implements Serializable {

    private double timestamp;

    private double GSR;

    private double SCL;

    private double SCR;

    //Using to match Final SCR with timestamp
    private int index;

    private double finalSCR;

    //User's Emotion
    private String emotion;
    //User's Event
    private String event;

    public MeasurementValue() {
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public double getGSR() {
        return GSR;
    }

    public void setGSR(double GSR) {
        this.GSR = GSR;
    }

    public double getSCL() {
        return SCL;
    }

    public void setSCL(double SCL) {
        this.SCL = SCL;
    }

    public double getSCR() {
        return SCR;
    }

    public void setSCR(double SCR) {
        this.SCR = SCR;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String mark) {
        this.emotion = mark;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getFinalSCR() {
        return finalSCR;
    }

    public void setFinalSCR(double finalSCR) {
        this.finalSCR = finalSCR;
    }

    @Override
    public String toString() {
        return "MeasurementValue{" +
                "timestamp=" + timestamp +
                ", GSR=" + GSR +
                ", SCL=" + SCL +
                ", SCR=" + SCR +
                '}';
    }


}
