package com.example.android_mobile_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.icu.util.Measure;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android_mobile_app.activities.Home;
import com.example.android_mobile_app.data.AppDatabase;
import com.example.android_mobile_app.domain.Measurement;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class MeasurementCompleted extends AppCompatActivity {

    private Measurement measurement;

    private TextView textView_name, textView_wearable, textView_start, textView_end, textView_time;

    private Button saveMeasurementButton;

    private LinearLayout returnToHomeLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_completed);

        measurement = (Measurement) getIntent().getSerializableExtra("measurement");

        saveMeasurementButton = findViewById(R.id.saveMeasurementButton);

        returnToHomeLinearLayout = findViewById(R.id.returnToHomeLinearLayout);

        textView_name = findViewById(R.id.textView_name);
        textView_wearable = findViewById(R.id.textView_wearable);
        textView_start = findViewById(R.id.textView_start);
        textView_end = findViewById(R.id.textView_end);
        textView_time = findViewById(R.id.textView_time);

        textView_name.setText(measurement.getName());
        textView_wearable.setText(measurement.getWearable());
        textView_start.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(measurement.getTimestampStart()));
        textView_end.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(measurement.getTimestampEnd()));

        long diffTime = measurement.getTimestampEnd().getTime() - measurement.getTimestampStart().getTime() ;

        String periodAsHH_MM_SS = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(diffTime),
                TimeUnit.MILLISECONDS.toMinutes(diffTime) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(diffTime) % TimeUnit.MINUTES.toSeconds(1));

        textView_time.setText(periodAsHH_MM_SS);
    }

    public void saveMeasurement(View view) {

        saveMeasurementButton.setVisibility(View.INVISIBLE);

        measurement.setName(textView_name.getText().toString());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "database-name").build();

                db.measurementDao().insert(measurement);


                update();
            }
        });
    }

    private void update(){
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast toast = Toast.makeText(getApplicationContext(), "De meting is succesvol opgeslagen.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                returnToHomeLinearLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void backToHome(View view) {
        startActivity(new Intent(this, Home.class));
    }
}