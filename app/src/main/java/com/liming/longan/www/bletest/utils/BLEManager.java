package com.liming.longan.www.bletest.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * Created by jmg-mac0sx on 16/3/3.
 */
public class BLEManager implements BluetoothAdapter.LeScanCallback{

    private static final String TAG = BLEManager.class.getSimpleName();
    public static final int REQUEST_ENABLE_BT = 12345;
    private static BLEManager ourInstance = null;
    private Context context;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler = new Handler();
    private boolean isScaning;  //是否正在搜索
    private HashMap<String, BluetoothGatt> gattMap ;    //保存已连接对gatt
//    private HashMap<String, BluetoothGattCharacteristic> characteristicMap ;    //保存已连接对gatt
    private List<String> macList =  new ArrayList<>();

    public interface DeviceStateListener {
        void onDeviceScan(BluetoothDevice device);
        void onDeviceConnect(BluetoothGatt gatt, boolean isConnect);
        void onGattWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean isSucc);
        void onValueUpdate(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] msg);
    }

    public void setDeviceStateListener(DeviceStateListener deviceStateListener) {
        this.deviceStateListener = deviceStateListener;
    }

    private DeviceStateListener deviceStateListener;

    public static BLEManager getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new BLEManager(context);
        }
        return ourInstance;
    }

    private BLEManager(Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "设备不支持蓝牙4.0", Toast.LENGTH_LONG).show();
        }
        this.context = context;
        bluetoothManager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        gattMap = new HashMap<String, BluetoothGatt>();
//        characteristicMap = new HashMap<String, BluetoothGattCharacteristic>();
    }

    /**
     * 请求打开蓝牙
     * */
    public void requestOpenBle() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ((Activity)context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (!macList.contains(device.getAddress())) {
            macList.add(device.getAddress());

            if (deviceStateListener != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        deviceStateListener.onDeviceScan(device);
                    }
                });
            }
        }
    }

    /**
     * 扫描蓝牙
     * */
    public void scanBLE() {
        macList.clear();
        if (!mBluetoothAdapter.isEnabled()) {
            requestOpenBle();
            return;
        }

        if (isScaning) {
            stopScan();
        }

        isScaning = true;
        mBluetoothAdapter.startLeScan(this);
    }

    /**
     * 停止扫描
     * */
    public void stopScan() {
        mBluetoothAdapter.stopLeScan(this);
        isScaning = false;
    }




    /**
     * 连接蓝牙
     * */
    public void connectBle(BluetoothDevice device) {
        device.connectGatt(context, true, new GattCallback() {
            @Override
            void didGattCallback(final BluetoothGatt gatt, final int state, final int type, final BluetoothGattCharacteristic characteristic) {
                if (deviceStateListener == null)
                    return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (type == TYPE_CONNECT) {
                            if (state == BluetoothProfile.STATE_CONNECTED) {
                                gatt.discoverServices();
                            }
                            if (state == BluetoothProfile.STATE_DISCONNECTED) {
                                BLEManager.this.deviceStateListener.onDeviceConnect(gatt, false);
                                gattMap.remove(gatt.getDevice().getAddress());
                            }

                        }

                        if (type == TYPE_SERVICES_DISCOVER) {
                            if (state != BluetoothGatt.GATT_SUCCESS) {
                                gatt.discoverServices();
                            } else {
                                BLEManager.this.deviceStateListener.onDeviceConnect(gatt, true);
                                gattMap.put(gatt.getDevice().getAddress(), gatt);
                            }
                        }

                        if (type == TYPE_CHARACTER_REC && state == BluetoothGatt.GATT_SUCCESS) {
                            BLEManager.this.deviceStateListener.onValueUpdate(gatt, characteristic, characteristic.getValue());
                        }

                        if (type == TYPE_CHARACTER_W && state == BluetoothGatt.GATT_SUCCESS) {
                            BLEManager.this.deviceStateListener.onGattWrite(gatt, characteristic, true);
                        }
                    }
                });

            }
        });
    }


    /**
     * 通过mac地址连接指定设备
     * */
    public void connectFromMac(final String mac) {
        if (!mBluetoothAdapter.isEnabled()) {
            requestOpenBle();
            return;
        }
        mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (device.getAddress().equalsIgnoreCase(mac)) {
                    mBluetoothAdapter.stopLeScan(this);
                    connectBle(device);
                    final BluetoothAdapter.LeScanCallback cb = this;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBluetoothAdapter.stopLeScan(cb);
                        }
                    }, 5000);
                }
            }
        });
    }

    /**
     * 写入数据
     * */
    public void writeData(BluetoothDevice device, byte[] data, String serviceUuid, String characterUuid) {
        BluetoothGatt gatt = gattMap.get(device.getAddress());
//        BluetoothGattCharacteristic ccts = characteristicMap.get(device.getAddress());
//        if (ccts == null){
            List<BluetoothGattService> services = gatt.getServices();
            for (BluetoothGattService service: services) {
                if (service.getUuid().toString().split("-")[0].substring(4).equalsIgnoreCase(serviceUuid)) {
                    List<BluetoothGattCharacteristic> charts = service.getCharacteristics();
                    for (BluetoothGattCharacteristic cct: charts) {
                        if (cct.getUuid().toString().split("-")[0].substring(4).equalsIgnoreCase(characterUuid)) {
//                            characteristicMap.put(gatt.getDevice().getAddress(), cct);
                            cct.setValue(data);
                            gatt.writeCharacteristic(cct);
                        }
                    }
                }
            }
//        } else {
//            ccts.setValue(data);
//            gatt.writeCharacteristic(ccts);

//        }

    }

    //获取指定gatt
    public BluetoothGatt getGattFromAddress(String address) {
        return gattMap.get(address);
    }

    //获取唯一连接的gatt
    public BluetoothGatt getOnlyGatt() {
        Iterator iter = gattMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            return gattMap.get(key);
        }
        return null;
    }

    /**
     * 获取指定特征值
     * */
    public BluetoothGattCharacteristic getCharacterFromUUID(BluetoothDevice device, String serviceUuid, String characterUuid){
        BluetoothGatt gatt = gattMap.get(device.getAddress());
        List<BluetoothGattService> services = gatt.getServices();
        for (BluetoothGattService service: services) {
            if (service.getUuid().toString().split("-")[0].substring(4).equalsIgnoreCase(serviceUuid)) {
                List<BluetoothGattCharacteristic> charts = service.getCharacteristics();
                for (BluetoothGattCharacteristic cct: charts) {
                    if (cct.getUuid().toString().split("-")[0].substring(4).equalsIgnoreCase(characterUuid)) {
                        return cct;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 全部写入数据
     * */
    public void writeDataToAll(byte[] data, String serviceUuid, String characterUuid) {
        Iterator iter = gattMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            BluetoothGatt gatt = gattMap.get(key);
//            BluetoothGattCharacteristic ccts = characteristicMap.get(gatt.getDevice().getAddress());
//            if (ccts == null) {
                for (BluetoothGattService service: gatt.getServices()) {
                    if (service.getUuid().toString().split("-")[0].substring(4).equalsIgnoreCase(serviceUuid)) {
                        for (BluetoothGattCharacteristic cct: service.getCharacteristics()) {
                            if (cct.getUuid().toString().split("-")[0].substring(4).equalsIgnoreCase(characterUuid)) {
                                cct.setValue(data);
                                gatt.writeCharacteristic(cct);
                            }
                        }
                    }
                }
//            } else {
//                ccts.setValue(data);
//                gatt.writeCharacteristic(ccts);
//            }
        }
    }

    /**
     * 全部写入数据
     * */
    public void readDataFromAll(String serviceUuid, String characterUuid) {
        Iterator iter = gattMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            BluetoothGatt gatt = gattMap.get(key);
            for (BluetoothGattService service: gatt.getServices()) {
                if (service.getUuid().toString().split("-")[0].substring(4).equalsIgnoreCase(serviceUuid)) {
                    for (BluetoothGattCharacteristic cct: service.getCharacteristics()) {
                        if (cct.getUuid().toString().split("-")[0].substring(4).equalsIgnoreCase(characterUuid)) {
                            gatt.readCharacteristic(cct);
                        }
                    }
                }
            }
        }
    }

    /**
     * 全部订阅
     * */
    public void notifyFromAll(String serviceUuid, String characterUuid, boolean isOpen) {
        Iterator iter = gattMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            BluetoothGatt gatt = gattMap.get(key);
            for (BluetoothGattService service: gatt.getServices()) {
                if (service.getUuid().toString().split("-")[0].substring(4).equalsIgnoreCase(serviceUuid)) {
                    for (BluetoothGattCharacteristic cct: service.getCharacteristics()) {
                        if (cct.getUuid().toString().split("-")[0].substring(4).equalsIgnoreCase(characterUuid)) {
                            if (isOpen) {
                                gatt.setCharacteristicNotification(cct, true);
                                List<BluetoothGattDescriptor> mDescriptors = cct.getDescriptors();
                                for (BluetoothGattDescriptor desc :mDescriptors) {
                                    desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    gatt.writeDescriptor(desc);
                                }
                            } else {
                                gatt.setCharacteristicNotification(cct, false);
                                List<BluetoothGattDescriptor> mDescriptors = cct.getDescriptors();
                                for (BluetoothGattDescriptor desc :mDescriptors) {
                                    desc.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                                    gatt.writeDescriptor(desc);
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    /**
     * 订阅,数据更新时通知
     * */
    public void notifyBle(BluetoothDevice device,  String serviceUuid, String characterUuid){
        final BluetoothGatt gatt = gattMap.get(device.getAddress());

        for (BluetoothGattService service: gatt.getServices()) {
            if (service.getUuid().toString().split("-")[0].substring(4).equalsIgnoreCase(serviceUuid)) {
                for (final BluetoothGattCharacteristic cct: service.getCharacteristics()) {
                    if (cct.getUuid().toString().split("-")[0].substring(4).equalsIgnoreCase(characterUuid)) {

                        gatt.setCharacteristicNotification(cct, true);
                        List<BluetoothGattDescriptor> mDescriptors = cct.getDescriptors();
                        for (BluetoothGattDescriptor desc :mDescriptors) {
                            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(desc);
                        }
                    }
                }
            }
        }
    }

    public void exitBluetooth() {
        Iterator iter = gattMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            BluetoothGatt gatt = gattMap.get(key);
            gatt.close();
        }
        gattMap.clear();
    }


    /**
     * 读
     * */
    public void readBle(BluetoothDevice device,  String serviceUuid, String characterUuid){
        BluetoothGatt gatt = gattMap.get(device.getAddress());
        List<BluetoothGattService> services = gatt.getServices();
        for (BluetoothGattService service: services) {
            String uuid = service.getUuid().toString();
            String[] parts = uuid.split("-");
            if (parts[0].substring(4).equalsIgnoreCase(serviceUuid)) {
                List<BluetoothGattCharacteristic> charts = service.getCharacteristics();
                for (BluetoothGattCharacteristic cct: charts) {
                    String uuids = cct.getUuid().toString();
                    String[] partss = uuids.split("-");
                    if (partss[0].substring(4).equalsIgnoreCase(characterUuid)) {
                        gatt.readCharacteristic(cct);
                    }
                }
            }
        }
    }


    public static final int TYPE_CONNECT = 0;
    public static final int TYPE_SERVICES_DISCOVER = 1;
    public static final int TYPE_CHARACTER_REC = 2;
    public static final int TYPE_CHARACTER_W = 3;
    private int gattType = -1;

    abstract  class GattCallback extends BluetoothGattCallback {


        abstract void didGattCallback(BluetoothGatt gatt, int errorCode, int type, BluetoothGattCharacteristic characteristic);

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            didGattCallback(gatt, newState, TYPE_CONNECT, null);
            Log.e(TAG, "onConnectionStateChange: gattStatus:"+status+",ConnectState:" +newState);
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.e(TAG, "onServicesDiscovered: gattStatus:"+status);
            didGattCallback(gatt, status, TYPE_SERVICES_DISCOVER, null);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.e(TAG, "onCharacteristicRead: gattStatus:"+status);
            didGattCallback(gatt, status, TYPE_CHARACTER_REC, characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.e(TAG, "onCharacteristicWrite: gattStatus:"+status);
            didGattCallback(gatt, status, TYPE_CHARACTER_W, characteristic);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.e(TAG, "onCharacteristicChanged:"+characteristic.getValue());
            didGattCallback(gatt, -1, TYPE_CHARACTER_REC, characteristic);

        }
    }

}
