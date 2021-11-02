package com.example.android_mobile_app.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.android_mobile_app.domain.Measurement;

@Database(entities = {Measurement.class}, version = 1,exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract MeasurementDao measurementDao();
}
