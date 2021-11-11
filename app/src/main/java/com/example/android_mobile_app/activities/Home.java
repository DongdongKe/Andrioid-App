package com.example.android_mobile_app.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.android_mobile_app.BluetoothDeviceSingleton;
import com.example.android_mobile_app.BuildConfig;
import com.example.android_mobile_app.Measurements;
import com.example.android_mobile_app.Movesense.ShareLogs;
import com.example.android_mobile_app.MovesenseConnectionActivity;
import com.example.android_mobile_app.R;
import com.example.android_mobile_app.SelectWearable;
import com.example.android_mobile_app.Settings;
import com.example.android_mobile_app.SignalDetector;
import com.example.android_mobile_app.WearablesMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialization();
        checkBluetoothStatus();
        checkConnectedDevices();

    }

    public void MovesenseSavedData(View view) {
        startActivity(new Intent(Home.this, ShareLogs.class));
    }

    public void MovesenseSelection(View view) {
        startActivity(new Intent(Home.this, MovesenseConnectionActivity.class));
    }

    public void Measurement(View view) {
        startActivity(new Intent(this, SelectWearable.class));
    }

    public void Data(View view) {
        startActivity(new Intent(this, Measurements.class));
    }

    public void Wearables(View view) {
        startActivity(new Intent(this, WearablesMenu.class));
    }

    public void Settings(View view) {
        startActivity(new Intent(this, Settings.class));
    }

    private void initialization(){
        final String PREFS_NAME = "settings";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOES_NOT_EXIST = -1;

        int currentVersionCode = BuildConfig.VERSION_CODE;

        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        int savedVersionCode = sharedPref.getInt(PREF_VERSION_CODE_KEY, DOES_NOT_EXIST);

        if (currentVersionCode == savedVersionCode)
            return;
        else if (savedVersionCode == DOES_NOT_EXIST){
            String ID = UUID.randomUUID().toString();

            sharedPref.edit().putString("UUID", ID).apply();
        } else if (currentVersionCode > savedVersionCode){

        }

        sharedPref.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }
    private void checkBluetoothStatus() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "Device does not support to Bluetooth", Toast.LENGTH_LONG).show();
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1);
        }
    }
    private void checkConnectedDevices(){
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                if (device.getName().toLowerCase().contains("shimmer3") || device.getName().toLowerCase().contains("rn42")){
                    BluetoothDeviceSingleton.getInstance().addDevice(device);

                    Log.e("Device paired: ", Integer.toString(BluetoothDeviceSingleton.getInstance().devices.size()));
                }
            }
        }
    }
}