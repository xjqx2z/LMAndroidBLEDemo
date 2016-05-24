package com.liming.longan.www.bletest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liming.longan.www.bletest.R;
import com.liming.longan.www.bletest.activity.GrounControlActivity;
import com.liming.longan.www.bletest.model.BluetoothGround;
import com.liming.longan.www.bletest.utils.BLEManager;

import java.util.List;

/**
 * Created by jmg-mac0sx on 16/3/11.
 */
public class GroundAdapter extends RecyclerView.Adapter  {


    private List<BluetoothGround> deviceList;
    private Context context;



    public GroundAdapter(List<BluetoothGround> deviceList, Context context) {
        this.deviceList = deviceList;
        this.context = context;
    }


        @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_carview_ground, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new GroundHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final BluetoothGround ground = deviceList.get(position);
        final GroundHolder ui = (GroundHolder) holder;
        ui.textview_name.setText(ground.getGround_name());
        ui.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ground.delete();
                deviceList.remove(position);
                notifyDataSetChanged();
                return true;
            }
        });
        ui.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BLEManager.getInstance(context).exitBluetooth();
                Activity activity = (Activity) context;
                Intent i = new Intent(activity, GrounControlActivity.class);
                i.putExtra("id", ground.getId());
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }



    class GroundHolder extends RecyclerView.ViewHolder{

        private TextView textview_name;
        private View view;

        public GroundHolder(View itemView) {
            super(itemView);
            view = itemView;
            textview_name = (TextView) itemView.findViewById(R.id.textview_name);
        }
    }

}
