package com.example.android_mobile_app.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.android_mobile_app.domain.Measurement;

import java.util.List;

@Dao
public interface MeasurementDao {

    @Query("SELECT * FROM measurement")
    List<Measurement> getAll();

    @Insert
    void insert(Measurement measurement);

    @Query("DELETE FROM measurement where ID = :id")
    void delete(String id);
}
