package com.liming.longan.www.bletest.activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.liming.longan.www.bletest.R;
import com.liming.longan.www.bletest.adapter.OneServiceAdapter;
import com.liming.longan.www.bletest.utils.BLEManager;
import com.liming.longan.www.bletest.utils.SpaceItemDecoration;

import java.util.List;

public class OneServiceActivity extends BaseActivity {

    private List<BluetoothGattService> services ;
    private RecyclerView recycleview_service_list;
    private OneServiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_service);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
        initValues();
        initListeners();
    }

    @Override
    protected void initViews() {

        services = BLEManager.getInstance(this).getOnlyGatt().getServices();
        recycleview_service_list = (RecyclerView) findViewById(R.id.recycleview_service_list);

    }

    @Override
    protected void initValues() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycleview_service_list.setLayoutManager(layoutManager);
        adapter = new OneServiceAdapter(services, this);
        recycleview_service_list.setAdapter(adapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycle_div);
        recycleview_service_list.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
