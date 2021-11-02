package com.example.android_mobile_app.domain;

import java.io.Serializable;

public class Wearable implements Serializable {

    private String name;

    private String address;

    public Wearable(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
