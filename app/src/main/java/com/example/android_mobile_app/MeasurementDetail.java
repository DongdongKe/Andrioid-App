package com.example.android_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android_mobile_app.domain.Measurement;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MeasurementDetail extends AppCompatActivity {

    private Measurement measurement;

    private TextView textView_name, textView_wearable, textView_start, textView_end, textView_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_detail);

        measurement = (Measurement) getIntent().getSerializableExtra("measurement");

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

        Log.e("f", measurement.getTimestampStart().toString());
    }

    public void showGraph(View view) {
        Intent intent = new Intent(getApplicationContext(), MeasurementGraph.class);
        intent.putExtra("measurement", measurement);

        startActivity(intent);
    }

    public void shareDataset(View view) throws JSONException {
        sendPost(this.measurement);
    }

    public void sendPost(final Measurement measurement) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://20.73.125.161:8080/data");//"http://20.73.125.161:8080/data" "http://192.168.31.87:8084/data"
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();

                    ObjectMapper objectMapper = new ObjectMapper();

                    String json = objectMapper.writeValueAsString(measurement);

                    jsonParam = new JSONObject(json);

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}