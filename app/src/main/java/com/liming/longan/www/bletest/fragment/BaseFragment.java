package com.liming.longan.www.bletest.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liming.longan.www.bletest.utils.BLEManager;


public abstract class BaseFragment extends Fragment {

	protected Activity activity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	protected abstract void initViews(View view);

	protected abstract void initValues();

	protected abstract void initListeners();

}