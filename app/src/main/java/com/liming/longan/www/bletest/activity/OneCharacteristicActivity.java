package com.liming.longan.www.bletest.activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.liming.longan.www.bletest.R;
import com.liming.longan.www.bletest.utils.BLEManager;
import com.liming.longan.www.bletest.utils.ByteUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OneCharacteristicActivity extends BaseActivity implements View.OnClickListener, BLEManager.DeviceStateListener{

    private TextView textview_cct_uuid;
    private TextView textview_cct_properties;
    private EditText edittext_value;
    private Button buttonRead;
    private Button button_write;
    private Switch switch_notify;
    private RelativeLayout layout_read;
    private LinearLayout layout_write;
    private BluetoothGattCharacteristic characteristic;
    private BluetoothGatt gatt;
    private ListView listview_value;
    private List<String> values;
    private ArrayAdapter<String> adapter;
    private SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_characteristic);
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
        BLEManager.getInstance(this).setDeviceStateListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        List<BluetoothGattDescriptor> mDescriptors = characteristic.getDescriptors();
        if (mDescriptors != null)
        for (BluetoothGattDescriptor desc :mDescriptors) {
            desc.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(desc);
        }
    }

    @Override
    protected void initViews() {
        values = new ArrayList<>();
        listview_value = (ListView) findViewById(R.id.listview_value);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
        layout_write = (LinearLayout) findViewById(R.id.layout_write);
        layout_read = (RelativeLayout) findViewById(R.id.layout_read);
        switch_notify = (Switch) findViewById(R.id.switch_notify);
        button_write = (Button) findViewById(R.id.button_write);
        buttonRead = (Button) findViewById(R.id.buttonRead);
        edittext_value = (EditText) findViewById(R.id.edittext_value);
        textview_cct_uuid = (TextView) findViewById(R.id.textview_cct_uuid);
        textview_cct_properties = (TextView) findViewById(R.id.textview_cct_properties);
        gatt = BLEManager.getInstance(this).getOnlyGatt();
        BluetoothGattService service = gatt.getService(UUID.fromString(getIntent().getStringExtra("data1")));
        characteristic = service.getCharacteristic(UUID.fromString(getIntent().getStringExtra("data2")));
    }

    @Override
    protected void initValues() {
        listview_value.setAdapter(adapter);
        textview_cct_uuid.setText("UUID : " + characteristic.getUuid().toString().split("-")[0].substring(4).toUpperCase());
        String properties = "";
        if ((characteristic.getProperties() & 1) == 1) {
            properties += "/BROADCAST";
        }
        if (((characteristic.getProperties() >> 1) & 1) == 1) {
            properties += "/READ";
            buttonRead.setVisibility(View.VISIBLE);
            listview_value.setVisibility(View.VISIBLE);
            layout_read.setVisibility(View.VISIBLE);
        }
        if (((characteristic.getProperties() >> 2) & 1) == 1) {
            properties += "/WRITE_NO_RESPONSE";
            layout_write.setVisibility(View.VISIBLE);
        }
        if (((characteristic.getProperties() >> 3) & 1) == 1) {
            properties += "/WRITE";
            layout_write.setVisibility(View.VISIBLE);
        }
        if (((characteristic.getProperties() >> 4) & 1) == 1) {
            properties += "/NOTIFY";
            listview_value.setVisibility(View.VISIBLE);
            layout_read.setVisibility(View.VISIBLE);
            switch_notify.setVisibility(View.VISIBLE);
        }
        if (((characteristic.getProperties() >> 5) & 1) == 1) {
            properties += "/INDICATE";
        }
        if (((characteristic.getProperties() >> 6) & 1) == 1) {
            properties += "/SIGNED_WRITE";
        }
        if (((characteristic.getProperties() >> 7) & 1) == 1) {
            properties += "/EXTENDED_PROPS";
        }

        textview_cct_properties.setText("Properties : " + properties);
    }

    @Override
    protected void initListeners() {
        button_write.setOnClickListener(this);
        buttonRead.setOnClickListener(this);
        switch_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gatt.setCharacteristicNotification(characteristic, true);
                    List<BluetoothGattDescriptor> mDescriptors = characteristic.getDescriptors();
                    if (mDescriptors != null)
                        for (BluetoothGattDescriptor desc : mDescriptors) {
                            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(desc);
                        }
                } else {
                    gatt.setCharacteristicNotification(characteristic, false);
                    List<BluetoothGattDescriptor> mDescriptors = characteristic.getDescriptors();
                    if (mDescriptors != null)
                        for (BluetoothGattDescriptor desc : mDescriptors) {
                            desc.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(desc);
                        }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRead:
                gatt.readCharacteristic(characteristic);
                break;
            case R.id.button_write:
                String value = edittext_value.getText().toString().trim();
                if (TextUtils.isEmpty(value)) {
                    Toast.makeText(this, "输入为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                characteristic.setValue(ByteUtils.chatOrders(value));
                gatt.writeCharacteristic(characteristic);
                break;
        }
    }

    @Override
    public void onDeviceScan(BluetoothDevice device) {

    }

    @Override
    public void onDeviceConnect(BluetoothGatt gatt, boolean isConnect) {

    }

    @Override
    public void onGattWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean isSucc) {
        if (isSucc) {
            values.add(0, dateformat.format(new Date(System.currentTimeMillis())) + " Write to " + gatt.getDevice().getName() + " : 0x" + ByteUtils.decodeBytesToHexString(characteristic.getValue()));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onValueUpdate(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] msg) {
        values.add(0, dateformat.format(new Date(System.currentTimeMillis())) + " Value from " + gatt.getDevice().getName() + " : 0x" + ByteUtils.decodeBytesToHexString(msg));
        adapter.notifyDataSetChanged();
    }
}
