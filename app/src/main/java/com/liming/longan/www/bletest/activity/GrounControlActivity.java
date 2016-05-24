package com.liming.longan.www.bletest.activity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.liming.longan.www.bletest.R;
import com.liming.longan.www.bletest.model.BluetoothGround;
import com.liming.longan.www.bletest.utils.BLEManager;
import com.liming.longan.www.bletest.utils.ByteUtils;
import com.liming.longan.www.bletest.utils.ShareUtil;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GrounControlActivity extends BaseActivity implements BLEManager.DeviceStateListener ,View.OnClickListener{

    private static final int REQUEST_FINE_LOCATION = 0;
    private BluetoothGround ground;
    private String[] addressArray;
    private ShareUtil shareUtil;
    private ListView listview_message;
    private ArrayAdapter<String> adapter;
    private List<String> messages = new ArrayList<>();
    private Switch btn_notify;
    private Button btn_write;
    private Button btn_read;
    private EditText et_value_uuid;
    private EditText et_charac_uuid;
    private EditText et_service_uuid;
    SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groun_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
        initValues();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocation();
    }

    @Override
    protected void initViews() {
        shareUtil = new ShareUtil(this);
        ground = DataSupport.find(BluetoothGround.class, getIntent().getIntExtra("id", 0));
        addressArray = ground.getAddressArray().split(",");
        listview_message = (ListView) findViewById(R.id.listview_message);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,messages);
        listview_message.setAdapter(adapter);
        btn_notify = (Switch) findViewById(R.id.btn_notify);
        btn_write = (Button) findViewById(R.id.btn_write);
        btn_read = (Button) findViewById(R.id.btn_read);
        et_value_uuid = (EditText) findViewById(R.id.et_value_uuid);
        et_charac_uuid = (EditText) findViewById(R.id.et_charac_uuid);
        et_service_uuid = (EditText) findViewById(R.id.et_service_uuid);
    }

    @Override
    protected void initValues() {
        et_value_uuid.setText(shareUtil.getString("et_value_uuid", ""));
        et_charac_uuid.setText(shareUtil.getString("et_charac_uuid", ""));
        et_service_uuid.setText(shareUtil.getString("et_service_uuid", ""));
    }

    @Override
    protected void initListeners() {
        btn_read.setOnClickListener(this);
        btn_write.setOnClickListener(this);

        btn_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (TextUtils.isEmpty(et_service_uuid.getText().toString().trim())
                        || TextUtils.isEmpty(et_charac_uuid.getText().toString().trim()))
                    return;
                String service = et_service_uuid.getText().toString().trim();
                String charact = et_charac_uuid.getText().toString().trim();
                BLEManager.getInstance(GrounControlActivity.this).notifyFromAll(service, charact, isChecked);
            }
        });

        et_value_uuid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                shareUtil.setShare("et_value_uuid", s.toString().trim());
            }
        });

        et_charac_uuid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                shareUtil.setShare("et_charac_uuid", s.toString().trim());
            }
        });

        et_service_uuid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                shareUtil.setShare("et_service_uuid", s.toString().trim());
            }
        });
    }

    @Override
    public void onDeviceScan(BluetoothDevice device) {
        for(int i = 0; i < addressArray.length; i++) {
            if (device.getAddress().equals(addressArray[i])) {
                BLEManager.getInstance(this).connectBle(device);
            }
        }
    }

    @Override
    public void onDeviceConnect(BluetoothGatt gatt, boolean isConnect) {
        if (isConnect) {
            messages.add(0, shareUtil.getString(gatt.getDevice().getAddress() + "name",
                    gatt.getDevice().getName())+"连接成功");
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGattWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean isSucc) {
        messages.add(0, dateformat.format(new Date(System.currentTimeMillis()))
                + " Write to "
                + shareUtil.getString(gatt.getDevice().getAddress() + "name", gatt.getDevice().getName())
                            + " : 0x" + ByteUtils.decodeBytesToHexString(characteristic.getValue()));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onValueUpdate(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] msg) {
        messages.add(0, dateformat.format(new Date(System.currentTimeMillis()))
                + " Read from "
                + shareUtil.getString(gatt.getDevice().getAddress() + "name", gatt.getDevice().getName())
                + " : 0x" + ByteUtils.decodeBytesToHexString(characteristic.getValue()));
        adapter.notifyDataSetChanged();
    }

    public void requestLocation() {
        BLEManager.getInstance(this).exitBluetooth();
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                    Toast.makeText(this, "不开启权限app将无法正常使用", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_FINE_LOCATION);
                return;
            }else{
                scanBle();
            }
        } else {
            scanBle();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanBle();
                } else{
                    Toast.makeText(this, "不开启权限app将无法正常使用", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void scanBle(){
        BLEManager.getInstance(this).stopScan();
        BLEManager.getInstance(this).setDeviceStateListener(this);
        BLEManager.getInstance(this).scanBLE();
        messages.add(0, "正在连接...");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(et_service_uuid.getText().toString().trim())
                || TextUtils.isEmpty(et_charac_uuid.getText().toString().trim()))
            return;
        String service = et_service_uuid.getText().toString().trim();
        String charact = et_charac_uuid.getText().toString().trim();
        switch (v.getId()) {
            case R.id.btn_read:
                BLEManager.getInstance(this).readDataFromAll(service, charact);
                break;
            case R.id.btn_write:
                if (TextUtils.isEmpty(et_value_uuid.getText().toString().trim()))
                    return;
                byte[] data = ByteUtils.chatOrders(et_value_uuid.getText().toString().trim());
                BLEManager.getInstance(this).writeDataToAll(data, service, charact );
                break;
        }
    }
}
