package com.example.android_mobile_app.wearable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android_mobile_app.BluetoothDeviceSingleton;
import com.example.android_mobile_app.R;
import com.example.android_mobile_app.activities.Home;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.shimmerresearch.android.Shimmer;
import com.shimmerresearch.android.manager.ShimmerBluetoothManagerAndroid;
import com.shimmerresearch.driver.ShimmerDevice;

public class AddShimmer extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothDevice bluetoothDevice;

    private final int REQUEST_ENABLE_BT = 1;

    private Button searchShimmerButton;

    private LinearLayout shimmerSearchLinearLayout, shimmerFoundLinearLayout, shimmerNotFoundLinearLayout, shimmerConnectedLinearLayout;

    private TextView macAddressTextView, deviceNameTextView, macAddressConnectedTextView, deviceNameConnectedTextView;

    private ShimmerBluetoothManagerAndroid bluetoothManagerAndroid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shimmer);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        registerReceiver(bluetoothAdapterDiscoveryStarted, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(bluetoothAdapterDiscoveryFinished, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        registerReceiver(bluetoothDeviceFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(bluetoothDeviceConnectedReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));

        searchShimmerButton = findViewById(R.id.searchShimmerButton);

        shimmerSearchLinearLayout = findViewById(R.id.shimmerSearchLinearLayout);
        shimmerFoundLinearLayout = findViewById(R.id.shimmerFoundLinearLayout);
        shimmerNotFoundLinearLayout = findViewById(R.id.shimmerNotFoundLinearLayout);
        shimmerConnectedLinearLayout = findViewById(R.id.shimmerConnectedLinearLayout);

        macAddressTextView = findViewById(R.id.macAddressTextView);
        deviceNameTextView = findViewById(R.id.deviceNameTextView);
        macAddressConnectedTextView = findViewById(R.id.macAddressConnectedTextView);
        deviceNameConnectedTextView = findViewById(R.id.deviceNameConnectedTextView);

        hideShimmerLinearLayout();

        try {
            bluetoothManagerAndroid = new ShimmerBluetoothManagerAndroid(this, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideShimmerLinearLayout(){
        shimmerSearchLinearLayout.setVisibility(View.GONE);
        shimmerFoundLinearLayout.setVisibility(View.GONE);
        shimmerNotFoundLinearLayout.setVisibility(View.GONE);
        shimmerConnectedLinearLayout.setVisibility(View.GONE);
    }

    private void shimmerFoundLinearLayout(){
        shimmerSearchLinearLayout.setVisibility(View.GONE);
        shimmerFoundLinearLayout.setVisibility(View.VISIBLE);
        shimmerNotFoundLinearLayout.setVisibility(View.GONE);
    }

    private void shimmerNotFoundLinearLayout(){
        shimmerSearchLinearLayout.setVisibility(View.GONE);
        shimmerFoundLinearLayout.setVisibility(View.GONE);
        shimmerNotFoundLinearLayout.setVisibility(View.VISIBLE);
    }

    public void addShimmer(View view) {
        if (bluetoothAdapter.isEnabled()){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
            hideShimmerLinearLayout();
            bluetoothDevice = null;
            userCanceled = false;
            bluetoothAdapter.startDiscovery();
        } else {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Shimmer Toevoegen")
                    .setMessage("Het toevoegen van een Shimmer vereist dat bluetooth is ingeschakeld. Op het moment is bluetooth uitgeschakeld op het toestel. \n \n" +
                            "Wilt u bluetooth inschakelen op het toestel?")
                    .setNegativeButton("Annuleren", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .setPositiveButton("Inschakelen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkBluetoothAdapter();
                        }
                    })
                    .show();
        }
    }

    private void checkBluetoothAdapter(){
        if (!bluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }

    private final BroadcastReceiver bluetoothAdapterDiscoveryStarted = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Discovery", "discovery started.");

            searchShimmerButton.setEnabled(false);

            shimmerSearchLinearLayout.setVisibility(View.VISIBLE);
        }
    };

    private final BroadcastReceiver bluetoothAdapterDiscoveryFinished = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Discovery", "discovery finished.");

            searchShimmerButton.setEnabled(true);

            if (bluetoothDevice == null && userCanceled == false) {
                shimmerNotFoundLinearLayout();
            }
        }
    };

    private final BroadcastReceiver bluetoothDeviceFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();

                Log.e("Device found", deviceName + " " + deviceHardwareAddress);

                if (device.getName() != null && device.getBondState() == BluetoothDevice.BOND_NONE)
                    if (device.getName().toLowerCase().contains("shimmer3") || device.getName().toLowerCase().contains("rn42")){
                    Log.e("Shimmer found", "Shimmer found");

                    macAddressTextView.setText(deviceHardwareAddress);
                    deviceNameTextView.setText(deviceName);

                    bluetoothAdapter.cancelDiscovery();

                    bluetoothDevice = device;

                    shimmerFoundLinearLayout();
                }
            }
        }
    };


    boolean userCanceled = false;

    public void cancelSearch(View view) {

        userCanceled = true;

        bluetoothAdapter.cancelDiscovery();

        hideShimmerLinearLayout();
    }

    int i = 1;
    public void connect(View view) {
        bluetoothDevice.createBond();
    }

    private final BroadcastReceiver bluetoothDeviceConnectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Discovery", "Bluetooth device connected.");

            shimmerFoundLinearLayout.setVisibility(View.GONE);
            shimmerConnectedLinearLayout.setVisibility(View.VISIBLE);

            macAddressConnectedTextView.setText(bluetoothDevice.getAddress());
            deviceNameConnectedTextView.setText(bluetoothDevice.getName());

            BluetoothDeviceSingleton.getInstance().addDevice(bluetoothDevice);
        }
    };

    public void returnToHome(View view) {
        startActivity(new Intent(this, Home.class));
    }

    @Override
    public void onBackPressed() {
        bluetoothAdapter.cancelDiscovery();

        super.onBackPressed();
    }
}