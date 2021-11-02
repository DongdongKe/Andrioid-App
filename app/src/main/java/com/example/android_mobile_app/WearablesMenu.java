package com.example.android_mobile_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android_mobile_app.wearable.AddWearableSelection;

public class WearablesMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wearables_menu);
    }

    public void addWearable(View view) {
        startActivity(new Intent(this, AddWearableSelection.class));
    }

    public void wearables(View view) {
        startActivity(new Intent(this, WearableOverview.class));
    }
}