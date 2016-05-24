package com.liming.longan.www.bletest.adapter;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liming.longan.www.bletest.R;
import com.liming.longan.www.bletest.utils.BLEManager;
import com.liming.longan.www.bletest.model.BleDevice;
import com.liming.longan.www.bletest.utils.ShareUtil;

import java.util.List;

/**
 * Created by jmg-mac0sx on 16/3/11.
 */
public class BLEMoreAdapter extends RecyclerView.Adapter implements BLEManager.DeviceStateListener {


    private List<BleDevice> deviceList;
    private Context context;
    private ShareUtil shareUtil;



    public BLEMoreAdapter(List<BleDevice> deviceList, Context context) {
        this.deviceList = deviceList;
        this.context = context;
        shareUtil = new ShareUtil(context);
    }


        @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_carview_more, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new ConnHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final BleDevice device = deviceList.get(position);

        final ConnHolder ui = (ConnHolder) holder;
        ui.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (device.getIsSelect()) {
                    device.setIsSelect(false);
                } else {
                    device.setIsSelect(true);
                }
                deviceList.remove(position);
                deviceList.add(position, device);
                notifyDataSetChanged();

            }
        });
        ui.cb_select.setChecked(device.getIsSelect());
        String name = device.getDevice().getName();
        if (TextUtils.isEmpty(name))
        {
            ui.textview_name.setText(shareUtil.getString(device.getDevice().getAddress() + "name", "未知"));
        } else {
            ui.textview_name.setText(shareUtil.getString(device.getDevice().getAddress() + "name", name));
        }

        ui.textview_uuid.setText(device.getDevice().getAddress().trim());

        ui.textview_edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(context);
                new AlertDialog.Builder(context).setTitle("修改设备名称").setIcon(
                        android.R.drawable.ic_dialog_info).setView(
                        input).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString().trim();
                        if (TextUtils.isEmpty(name) || name.length() > 10) {
                            Toast.makeText(context, "设备名称为空或长度超过限制", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        shareUtil.setShare(device.getDevice().getAddress() + "name", name);
                        ui.textview_name.setText(name);
                    }
                })
                        .setNegativeButton("取消", null).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    @Override
    public void onDeviceScan(BluetoothDevice device) {
        deviceList.add(new BleDevice(device, false));
        notifyDataSetChanged();
    }

    @Override
    public void onDeviceConnect(BluetoothGatt gatt, boolean isConnect) {

    }

    @Override
    public void onGattWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean isSucc) {

    }

    @Override
    public void onValueUpdate(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] msg) {

    }


    class ConnHolder extends RecyclerView.ViewHolder{

        private TextView textview_name;
        private TextView textview_uuid;
        private TextView textview_edit_name;
        private CheckBox cb_select;
        private View view;

        public ConnHolder(View itemView) {
            super(itemView);
            view = itemView;
            textview_name = (TextView) itemView.findViewById(R.id.textview_name);
            textview_uuid = (TextView) itemView.findViewById(R.id.textview_uuid);
            textview_edit_name = (TextView) itemView.findViewById(R.id.textview_edit_name);
            cb_select = (CheckBox) itemView.findViewById(R.id.cb_select);
        }
    }

}
