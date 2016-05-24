package com.liming.longan.www.bletest.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.liming.longan.www.bletest.R;
import com.liming.longan.www.bletest.adapter.BLEMoreAdapter;
import com.liming.longan.www.bletest.model.BleDevice;
import com.liming.longan.www.bletest.model.BluetoothGround;
import com.liming.longan.www.bletest.utils.BLEManager;
import com.liming.longan.www.bletest.utils.ShareUtil;
import com.liming.longan.www.bletest.utils.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class AddGroundActivity extends BaseActivity {


    private static final int REQUEST_FINE_LOCATION=0;
    private RecyclerView recycle_ble_list;
    private SwipeRefreshLayout swiperefresh;
    private BLEMoreAdapter adapter;
    private List<BleDevice> BLEList = new ArrayList<>();
    private ShareUtil shareUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ground);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
        initValues();
        initListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ground, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            final BluetoothGround ground = new BluetoothGround();
            StringBuffer stringBuffer = new StringBuffer();
            for (BleDevice d: BLEList) {
                if (d.getIsSelect()) {
                    stringBuffer.append(d.getDevice().getAddress()+",");
                }
            }

            if (stringBuffer.length() == 0) {
                Toast.makeText(this, "尚未选择设备", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }


            ground.setAddressArray(stringBuffer.toString());

            final EditText input = new EditText(this);
            new AlertDialog.Builder(this).setTitle("设置分组名称").setIcon(
                    android.R.drawable.ic_dialog_info).setView(
                    input).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = input.getText().toString().trim();
                    if (TextUtils.isEmpty(name) || name.length() > 10) {
                        Toast.makeText(AddGroundActivity.this, "名字为空或名字太长", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ground.setGround_name(name);
                    ground.save();
                    finish();

                }
            })
                    .setNegativeButton("取消", null).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BLEManager.getInstance(this).stopScan();
    }

    @Override
    protected void initViews() {
        shareUtil = new ShareUtil(this);
        swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        recycle_ble_list = (RecyclerView) findViewById(R.id.recycle_ble_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycle_ble_list.setLayoutManager(layoutManager);
        adapter = new BLEMoreAdapter(BLEList, this);
        recycle_ble_list.setAdapter(adapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycle_div);
        recycle_ble_list.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
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
        BLEList.clear();
        BLEManager.getInstance(this).setDeviceStateListener(adapter);
        BLEManager.getInstance(this).scanBLE();
    }
}
