package com.liming.longan.www.bletest.model;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by jmg-mac0sx on 16/5/9.
 */
public class BluetoothGround extends DataSupport {


    private int id;

    private String ground_name;

    private String addressArray;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGround_name() {
        return ground_name;
    }

    public void setGround_name(String ground_name) {
        this.ground_name = ground_name;
    }

    public String getAddressArray() {
        return addressArray;
    }

    public void setAddressArray(String addressArray) {
        this.addressArray = addressArray;
    }
}
