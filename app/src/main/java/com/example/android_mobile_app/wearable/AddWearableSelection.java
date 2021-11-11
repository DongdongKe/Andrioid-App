package com.example.android_mobile_app.wearable;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android_mobile_app.MovesenseConnectionActivity;
import com.example.android_mobile_app.R;
import com.example.android_mobile_app.measurement.MeasurementEmpatica;
import com.example.android_mobile_app.measurement.MeasurementMovesense;
import com.google.android.material.card.MaterialCardView;

public class AddWearableSelection extends AppCompatActivity {

    MaterialCardView card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wearable_selection);
        card=findViewById(R.id.card);
    }

    public void addShimmer(View view) {
        startActivity(new Intent(this, AddShimmer.class));
    }

    public void addEmpatica(View view) {
        startActivity(new Intent(this, MeasurementEmpatica.class));
    }
}