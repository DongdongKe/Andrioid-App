package com.example.android_mobile_app.domain;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.android_mobile_app.data.Converters;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Measurement implements Serializable {

    @PrimaryKey
    @NonNull
    private String ID;

    @ColumnInfo(name = "wearable")
    private String wearable;

    @ColumnInfo(name = "timestamp_start")
    private Timestamp timestampStart;

    @ColumnInfo(name = "timestamp_end")
    private Timestamp timestampEnd;

    @ColumnInfo(name = "name")
    private String name;

    @TypeConverters(Converters.class)
    private List<MeasurementValue> measurementValues = new ArrayList<MeasurementValue>();


//    @TypeConverters(Converters.class)
//    private List<MeasurementValue> hrvValues = new ArrayList<MeasurementValue>();

    public Measurement(String ID, String wearable, Timestamp timestampStart, Timestamp timestampEnd, String name, List<MeasurementValue> measurementValues) {
        this.ID = ID;
        this.wearable = wearable;
        this.timestampStart = timestampStart;
        this.timestampEnd = timestampEnd;
        this.name = name;
        this.measurementValues = measurementValues;

    }

    @Ignore
    public Measurement() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getWearable() {
        return wearable;
    }

    public void setWearable(String wearable) {
        this.wearable = wearable;
    }

    public Timestamp getTimestampStart() {
        return timestampStart;
    }

    public void setTimestampStart(Timestamp timestampStart) {
        this.timestampStart = timestampStart;
    }

    public Timestamp getTimestampEnd() {
        return timestampEnd;
    }

    public void setTimestampEnd(Timestamp timestampEnd) {
        this.timestampEnd = timestampEnd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MeasurementValue> getMeasurementValues() {
        return measurementValues;
    }

    public void setMeasurementValues(List<MeasurementValue> measurementValues) {
        this.measurementValues = measurementValues;
    }


    @Override
    public String toString() {
        return "Measurement{" +
                "ID='" + ID + '\'' +
                ", wearable='" + wearable + '\'' +
                ", timestampStart=" + timestampStart +
                ", timestampEnd=" + timestampEnd +
                ", name='" + name + '\'' +
                ", measurementValues=" + measurementValues.toString() +
                '}';
    }
}
