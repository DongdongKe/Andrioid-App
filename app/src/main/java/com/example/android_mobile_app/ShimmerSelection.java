package com.example.android_mobile_app;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android_mobile_app.adapter.DeviceAdapter;
import com.example.android_mobile_app.domain.Wearable;
import com.example.android_mobile_app.measurement.MeasurementShimmer;

import java.util.ArrayList;
import java.util.Set;

public class ShimmerSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shimmer_selection);

        create();
    }

    private void create(){
        ListView listView = (ListView) findViewById(R.id.list_view);

        final ArrayList<Wearable> wearables = new ArrayList<>();

        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                    if (device.getName().toLowerCase().contains("shimmer3") || device.getName().toLowerCase().contains("rn42")){
                        wearables.add(new Wearable(deviceName, deviceHardwareAddress));
                    }
                }
            }
        }

        final DeviceAdapter deviceAdapter = new DeviceAdapter(this, wearables);
        listView.setAdapter(deviceAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Wearable wearable = deviceAdapter.getItem(position);

                if (wearable.getName().toLowerCase().contains("shimmer3") || wearable.getName().toLowerCase().contains("rn42")){
                    Intent intent = new Intent(getApplicationContext(), MeasurementShimmer.class);
                    intent.putExtra("wearable", wearable);
                    startActivity(intent);
                }
            }
        });

        TextView empty=(TextView)findViewById(R.id.empty);

        listView.setEmptyView(empty);
    }
}