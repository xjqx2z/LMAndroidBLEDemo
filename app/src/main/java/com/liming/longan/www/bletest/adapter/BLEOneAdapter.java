package com.liming.longan.www.bletest.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liming.longan.www.bletest.R;
import com.liming.longan.www.bletest.activity.OneServiceActivity;
import com.liming.longan.www.bletest.utils.BLEManager;
import com.liming.longan.www.bletest.utils.ShareUtil;

import java.util.List;

/**
 * Created by jmg-mac0sx on 16/3/11.
 */
public class BLEOneAdapter extends RecyclerView.Adapter implements BLEManager.DeviceStateListener {


    private List<BluetoothDevice> deviceList;
    private Context context;
    private ShareUtil shareUtil;
    private ViewGroup parent;



    public BLEOneAdapter(List<BluetoothDevice> deviceList, Context context) {
        this.deviceList = deviceList;
        this.context = context;
        shareUtil = new ShareUtil(context);
        BLEManager.getInstance(context).setDeviceStateListener(this);
    }


        @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            this.parent = parent;
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_carview_one, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new ConnHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final BluetoothDevice device = deviceList.get(position);

        final ConnHolder ui = (ConnHolder) holder;
        ui.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BLEManager.getInstance(context).connectBle(device);
            }
        });
        String name = device.getName();
        if (TextUtils.isEmpty(name))
        {
            ui.textViewName.setText(shareUtil.getString(device.getAddress() + "name", "未知"));
        } else {
            ui.textViewName.setText(shareUtil.getString(device.getAddress() + "name", name));
        }

        ui.textViewUuid.setText(device.getAddress().trim());

        ui.textViewChangeName.setOnClickListener(new View.OnClickListener() {
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
                        shareUtil.setShare(device.getAddress() + "name", name);
                        ui.textViewName.setText(name);
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
        deviceList.add(device);
        notifyDataSetChanged();
    }

    @Override
    public void onDeviceConnect(BluetoothGatt gatt, boolean isConnect) {
        if (isConnect) {
            Activity activity = (Activity) context;
            Intent i = new Intent(activity, OneServiceActivity.class);
            i.putExtra("address", gatt.getDevice().getAddress());
            activity.startActivity(i);
        } else {
            Snackbar.make(parent, "连接失败", Snackbar.LENGTH_LONG)
                    .setAction("重试", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .show();
        }
    }

    @Override
    public void onGattWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean isSucc) {

    }

    @Override
    public void onValueUpdate(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] msg) {

    }


    class ConnHolder extends RecyclerView.ViewHolder{

        private TextView textViewName;
        private TextView textViewUuid;
        private TextView textViewChangeName;
        private View view;

        public ConnHolder(View itemView) {
            super(itemView);
            view = itemView;
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewUuid = (TextView) itemView.findViewById(R.id.textViewUuid);
            textViewChangeName = (TextView) itemView.findViewById(R.id.textViewChangeName);
        }
    }

}
