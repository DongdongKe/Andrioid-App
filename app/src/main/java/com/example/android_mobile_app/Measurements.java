package com.example.android_mobile_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Measure;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android_mobile_app.adapter.DeviceAdapter;
import com.example.android_mobile_app.adapter.MeasurementAdapter;
import com.example.android_mobile_app.data.AppDatabase;
import com.example.android_mobile_app.domain.Measurement;

import java.util.ArrayList;
import java.util.List;

public class Measurements extends AppCompatActivity {

    List<Measurement> measurementList;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);

        listView = findViewById(R.id.list_view);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "database-name").build();

                measurementList = db.measurementDao().getAll();

                Log.e("List:", String.valueOf(measurementList.size()));

                update();
            }
        });
    }

    private void update(){

        final Context context = this;

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final MeasurementAdapter measurementAdapter = new MeasurementAdapter(context, measurementList);
                listView.setAdapter(measurementAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Measurement measurement = measurementAdapter.getItem(position);

                        Intent intent = new Intent(getApplicationContext(), MeasurementDetail.class);
                        intent.putExtra("measurement", measurement);

                        startActivity(intent);
                    }
                });
            }
        });

    }
}