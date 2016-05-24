package com.liming.longan.www.bletest.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liming.longan.www.bletest.R;
import com.liming.longan.www.bletest.adapter.GroundAdapter;
import com.liming.longan.www.bletest.model.BluetoothGround;
import com.liming.longan.www.bletest.utils.SpaceItemDecoration;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by jmg-mac0sx on 16/5/5.
 */
public class MoreFragment extends BaseFragment{

    private static final int REQUEST_FINE_LOCATION=0;
    private RecyclerView recycle_ble_list;
    private SwipeRefreshLayout swiperefresh;
    private GroundAdapter adapter;
    private List<BluetoothGround> groundList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, null);
        initViews(view);
        initValues();
        initListeners();
        return view;
    }


    @Override
    protected void initViews(View view) {
        groundList = DataSupport.findAll(BluetoothGround.class);
        swiperefresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        recycle_ble_list = (RecyclerView) view.findViewById(R.id.recycle_ble_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recycle_ble_list.setLayoutManager(layoutManager);
        adapter = new GroundAdapter(groundList, activity);
        recycle_ble_list.setAdapter(adapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycle_div);
        recycle_ble_list.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    }

    public void updateGround(){
        List<BluetoothGround> list = DataSupport.findAll(BluetoothGround.class);
        groundList.clear();
        groundList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void initValues() {

    }

    @Override
    protected void initListeners() {
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                List<BluetoothGround> list = DataSupport.findAll(BluetoothGround.class);
                groundList.clear();
                groundList.addAll(list);
                adapter.notifyDataSetChanged();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                          if (swiperefresh.isRefreshing())
                              swiperefresh.setRefreshing(false);
                    }
                }, 500);
            }
        });
    }



}
