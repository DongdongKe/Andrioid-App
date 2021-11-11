package com.example.android_mobile_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android_mobile_app.measurement.MeasurementEmpatica;
import com.example.android_mobile_app.measurement.MeasurementMovesense;
import com.example.android_mobile_app.measurement.MeasurementShimmer;

public class SelectWearable extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_wearable);
    }

    public void ShimmerSelection(View view) {
        startActivity(new Intent(this, MeasurementShimmer.class));
    }

    public void EmpaticaSelection(View view) {
        startActivity(new Intent(this, MeasurementEmpatica.class));
    }

    public void MovesenseSelection(View view) {
        startActivity(new Intent(this, MovesenseConnectionActivity.class));
    }
}