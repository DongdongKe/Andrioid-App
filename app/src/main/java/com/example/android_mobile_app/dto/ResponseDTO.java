package com.example.android_mobile_app.dto;

import java.util.ArrayList;
import java.util.List;

public class ResponseDTO {
    //Index
    private int index;
    //SCL from sparsEDA algorithm
    private Double SCL;
    //SCR from sparsEDA algorithm
    private Double SCR;
    //live-data SCR list
    private List<SCR> SCRvalues = new ArrayList<SCR>();
    //Final SCR list
    private List<SCR> finalSCRvalues = new ArrayList<SCR>();
    //User's Mark
    private String Mark;
    //User's Event
    private String Event;

    public ResponseDTO() {
    }

    public ResponseDTO(int index, Double SCL, Double SCR) {
        this.index = index;
        this.SCL = SCL;
        this.SCR = SCR;
    }

    public List<SCR> getSCRvalues() {
        return SCRvalues;
    }

    public void setSCRvalues(List<SCR> SCRvalues) {
        this.SCRvalues = SCRvalues;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Double getSCL() {
        return SCL;
    }

    public void setSCL(Double SCL) {
        this.SCL = SCL;
    }

    public Double getSCR() {
        return SCR;
    }

    public void setSCR(Double SCR) {
        this.SCR = SCR;
    }

    public String getMark() {
        return Mark;
    }

    public void setMark(String mark) {
        Mark = mark;
    }

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public List<com.example.android_mobile_app.dto.SCR> getFinalSCRvalues() {
        return finalSCRvalues;
    }

    public void setFinalSCRvalues(List<com.example.android_mobile_app.dto.SCR> finalSCRvalues) {
        this.finalSCRvalues = finalSCRvalues;
    }
}
