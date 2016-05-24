package com.liming.longan.www.bletest.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liming.longan.www.bletest.R;
import com.liming.longan.www.bletest.activity.OneCharacteristicActivity;

import java.util.List;

/**
 * Created by jmg-mac0sx on 16/3/11.
 */
public class OneServiceAdapter extends RecyclerView.Adapter implements View.OnClickListener {


    private List<BluetoothGattService> services ;
    private Context context;
    private ViewGroup parent;

    public OneServiceAdapter( List<BluetoothGattService> services, Context context) {
        this.services = services;
        this.context = context;
    }


        @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            this.parent = parent;
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_carview_service, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new MviewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final BluetoothGattService service = services.get(position);

        final MviewHolder ui = (MviewHolder) holder;

        ui.linearLayoutService.removeAllViews();
        ui.textViewServiceUUID.setText(service.getUuid().toString().split("-")[0].substring(4).toUpperCase());
        ui.linearLayoutService.addView(ui.textViewServiceUUID);
        int i = 1;
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic :characteristics) {

            View characteristicView = LayoutInflater.from(context).inflate(R.layout.service_characteristic, null);
            ui.linearLayoutService.addView(characteristicView);
            TextView textViewCharactic = (TextView) characteristicView.findViewById(R.id.textViewCharactic);
            TextView textViewCharacticName = (TextView) characteristicView.findViewById(R.id.textViewCharacticName);
            TextView textViewCharacticProperties = (TextView) characteristicView.findViewById(R.id.textViewCharacticProperties);
            textViewCharacticName.setText("characteristic"+(i++));
            textViewCharactic.setText(characteristic.getUuid().toString().split("-")[0].substring(4).toUpperCase());
            String properties = "Properties";
            if ((characteristic.getProperties() & 1) == 1) {
                properties += "_BROADCAST";
            }
            if (((characteristic.getProperties() >> 1) & 1) == 1) {
                properties += "_READ";
            }
            if (((characteristic.getProperties() >> 2) & 1) == 1) {
                properties += "_WRITE_NO_RESPONSE";
            }
            if (((characteristic.getProperties() >> 3) & 1) == 1) {
                properties += "_WRITE";
            }
            if (((characteristic.getProperties() >> 4) & 1) == 1) {
                properties += "_NOTIFY";
            }
            if (((characteristic.getProperties() >> 5) & 1) == 1) {
                properties += "_INDICATE";
            }
            if (((characteristic.getProperties() >> 6) & 1) == 1) {
                properties += "_SIGNED_WRITE";
            }
            if (((characteristic.getProperties() >> 7) & 1) == 1) {
                properties += "_EXTENDED_PROPS";
            }
            textViewCharacticProperties.setText(properties);

            characteristicView.setTag(characteristic);
            characteristicView.setOnClickListener(this);
        }

    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    @Override
    public void onClick(View v) {
        BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) v.getTag();
        Activity activity = (Activity) context;
        Intent i = new Intent(activity, OneCharacteristicActivity.class);
        i.putExtra("data1",characteristic.getService().getUuid().toString());
        i.putExtra("data2",characteristic.getUuid().toString());
        activity.startActivity(i);

    }


    class MviewHolder extends RecyclerView.ViewHolder{

        private TextView textViewServiceUUID;
        private LinearLayout linearLayoutService;
        private View view;

        public MviewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textViewServiceUUID = (TextView) itemView.findViewById(R.id.textViewServiceUUID);
            linearLayoutService = (LinearLayout) itemView.findViewById(R.id.linearlayout_service);
        }
    }


}
