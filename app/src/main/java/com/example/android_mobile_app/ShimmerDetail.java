package com.example.android_mobile_app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_mobile_app.domain.Wearable;
import com.shimmerresearch.android.Shimmer;
import java.lang.reflect.Method;

public class ShimmerDetail extends AppCompatActivity {

    private Wearable wearable;

    private Button disconnectButton;

    private TextView textView_name, textView_mac, textView_sampling_rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shimmer_detail);

        wearable = (Wearable) getIntent().getSerializableExtra("wearable");

        Shimmer shimmer = new Shimmer(new Handler());
        shimmer.connect(wearable.getAddress(),"default");

        disconnectButton = findViewById(R.id.disconnectButton);

        textView_name = findViewById(R.id.textView_name);
        textView_mac = findViewById(R.id.textView_mac);
        textView_sampling_rate = findViewById(R.id.textView_sampling_rate);

        textView_name.setText(wearable.getName());
        textView_mac.setText(wearable.getAddress());
        textView_sampling_rate.setText(Double.toString(shimmer.getSamplingRateShimmer()));
    }

    public void disconnect(View view) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(wearable.getAddress());

        unpairDevice(device);
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);

            disconnectButton.setEnabled(false);

            Toast toast = Toast.makeText(getApplicationContext(), "De wearable is succesvol ontkoppeld.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);

            toast.show();

        } catch (Exception e) { Log.e("TAG", e.getMessage()); }
    }
}