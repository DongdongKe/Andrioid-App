package com.example.android_mobile_app;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDeviceSingleton {

    private static BluetoothDeviceSingleton single_instance = null;

    public String s;

    public List<BluetoothDevice> devices;
    private BluetoothDeviceSingleton()
    {
        s = "Hello I am a string part of Singleton class";

        devices = new ArrayList<>();
    }

    public static BluetoothDeviceSingleton getInstance()
    {
        if (single_instance == null)
            single_instance = new BluetoothDeviceSingleton();

        return single_instance;
    }

    public void addDevice(BluetoothDevice bluetoothDevice){
        this.devices.add(bluetoothDevice);
    }

    public String getSize(){
        return Integer.toString(this.devices.size());
    }
}
