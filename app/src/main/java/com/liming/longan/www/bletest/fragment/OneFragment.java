package com.liming.longan.www.bletest.fragment;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.liming.longan.www.bletest.R;
import com.liming.longan.www.bletest.adapter.BLEOneAdapter;
import com.liming.longan.www.bletest.utils.BLEManager;
import com.liming.longan.www.bletest.utils.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmg-mac0sx on 16/5/5.
 */
public class OneFragment extends BaseFragment {

    private static final int REQUEST_FINE_LOCATION=0;
    private RecyclerView recycleview_ble_list;
    private SwipeRefreshLayout swiperefresh;
    private BLEOneAdapter adapter;
    private List<BluetoothDevice> BLEList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, null);
        initViews(view);
        initValues();
        initListeners();
        return view;
    }

    @Override
    protected void initViews(View view) {
        swiperefresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        recycleview_ble_list = (RecyclerView) view.findViewById(R.id.recycleview_ble_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recycleview_ble_list.setLayoutManager(layoutManager);
        adapter = new BLEOneAdapter(BLEList, activity);
        recycleview_ble_list.setAdapter(adapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycle_div);
        recycleview_ble_list.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    }


    @Override
    protected void initValues() {

    }

    @Override
    protected void initListeners() {
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestLocation();
                if (swiperefresh.isRefreshing()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swiperefresh.setRefreshing(false);
                        }
                    }, 1000);
                }
            }
        });
    }

    public void requestLocation() {
        BLEManager.getInstance(activity).exitBluetooth();
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION))
                    Toast.makeText(activity, "不开启权限app将无法正常使用", Toast.LENGTH_SHORT).show();
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
    public void onPause() {
        super.onPause();
        BLEManager.getInstance(activity).stopScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanBle();
                } else{
                    Toast.makeText(activity, "不开启权限app将无法正常使用", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void scanBle(){
        BLEList.clear();
        adapter.notifyDataSetChanged();
        BLEManager.getInstance(activity).setDeviceStateListener(adapter);
        BLEManager.getInstance(activity).scanBLE();
    }
}
