package com.liming.longan.www.bletest.activity;

import android.support.v7.app.AppCompatActivity;

import com.liming.longan.www.bletest.utils.BLEManager;

/**
 * Created by jmg-mac0sx on 16/5/5.
 */
public abstract class BaseActivity extends AppCompatActivity {


    protected abstract void initViews();

    protected abstract void initValues();

    protected abstract void initListeners();

}
