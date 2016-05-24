package com.liming.longan.www.bletest.model;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jmg-mac0sx on 16/3/11.
 */
public class BleDevice implements Parcelable {

    private BluetoothDevice device;
    private Boolean isSelect;

    public BleDevice() {
    }

    public BleDevice(BluetoothDevice device, Boolean isSelect) {
        this.device = device;
        this.isSelect = isSelect;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public Boolean getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(Boolean isSelect) {
        this.isSelect = isSelect;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.device, flags);
        dest.writeValue(this.isSelect);
    }

    protected BleDevice(Parcel in) {
        this.device = in.readParcelable(BluetoothDevice.class.getClassLoader());
        this.isSelect = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<BleDevice> CREATOR = new Creator<BleDevice>() {
        @Override
        public BleDevice createFromParcel(Parcel source) {
            return new BleDevice(source);
        }

        @Override
        public BleDevice[] newArray(int size) {
            return new BleDevice[size];
        }
    };
}
