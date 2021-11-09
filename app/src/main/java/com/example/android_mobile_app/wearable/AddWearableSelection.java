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

    MaterialCardView card3;
    MaterialCardView card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wearable_selection);
        card=findViewById(R.id.card);
        card3= findViewById(R.id.card3);
    }

    public void addShimmer(View view) {
        startActivity(new Intent(this, AddShimmer.class));
    }

    public void addEmpatica(View view) {
        startActivity(new Intent(this, MeasurementEmpatica.class));
    }

    public void addMovesense(View view) {
        startActivity(new Intent(this, MovesenseConnectionActivity.class));
    }

//    @OnClick({R.id.card3, R.id.card})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.card3:
//                startActivity(new Intent(AddWearableSelection.this, MovesenseConnectionActivity.class));
//                break;
//        }
//    }
}