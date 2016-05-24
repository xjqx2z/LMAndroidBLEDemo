package com.liming.longan.www.bletest;

import org.litepal.LitePalApplication;

/**
 * Created by jmg-mac0sx on 16/5/5.
 */
public class LocalApplication extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePalApplication.initialize(this);
    }
}
