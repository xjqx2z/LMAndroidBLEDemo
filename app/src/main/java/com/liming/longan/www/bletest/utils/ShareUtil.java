package com.liming.longan.www.bletest.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class ShareUtil {

	private Context mContext;
	private String filename = null;

	public ShareUtil(Context context) {
		mContext = context;
	}

	public ShareUtil(Context context, String shareFile) {
		mContext = context;
		filename = shareFile;
	}

	private SharedPreferences getPreferences() {
		if (filename == null) {
			return PreferenceManager.getDefaultSharedPreferences(mContext);
		} else {
			return mContext.getSharedPreferences(filename, Context.MODE_WORLD_READABLE);
		}
	}

	private Editor getEditor() {
		if (filename == null) {
			return PreferenceManager.getDefaultSharedPreferences(mContext).edit();
		} else {
			return mContext.getSharedPreferences(filename, Context.MODE_WORLD_WRITEABLE).edit();
		}
	}

	/**
	 * 获取共享boolean值
	 * 
	 * @param context
	 * @param filename共享文件名
	 * @param key共享数据名
	 * @param defValue默认值
	 * @return
	 */
	public boolean getBoolean(String key, boolean defValue) {
		return getPreferences().getBoolean(key, defValue);
	}

	/**
	 * 获取共享float值
	 * 
	 * @param context
	 * @param filename共享文件名
	 * @param key共享数据名
	 * @param defValue默认值
	 * @return
	 */
	public float getFloat(String key, float defValue) {
		return getPreferences().getFloat(key, defValue);
	}

	/**
	 * 获取共享int值
	 * 
	 * @param context
	 * @param filename共享文件名
	 * @param key共享数据名
	 * @param defValue默认值
	 * @return
	 */
	public int getInt(String key, int defValue) {
		return getPreferences().getInt(key, defValue);
	}

	/**
	 * 获取共享long值
	 * 
	 * @param context
	 * @param filename共享文件名
	 * @param key共享数据名
	 * @param defValue默认值
	 * @return
	 */
	public long getLong(String key, long defValue) {
		return getPreferences().getLong(key, defValue);
	}

	/**
	 * 获取共享String值
	 * 
	 * @param context
	 * @param filename共享文件名
	 * @param key共享数据名
	 * @param defValue默认值
	 * @return
	 */
	public String getString(String key, String defValue) {
		return getPreferences().getString(key, defValue);
	}

	/**
	 * 设置共享boolean值
	 * 
	 * @param context
	 * @param filename共享文件名
	 * @param key共享数据名
	 * @param value数据值
	 * @return
	 */
	public boolean setShare(String key, boolean value) {
		return getEditor().putBoolean(key, value).commit();
	}

	/**
	 * 设置共享float值
	 * 
	 * @param context
	 * @param filename共享文件名
	 * @param key共享数据名
	 * @param value数据值
	 * @return
	 */
	public boolean setShare(String key, float value) {
		return getEditor().putFloat(key, value).commit();
	}

	/**
	 * 设置共享int值
	 * 
	 * @param context
	 * @param filename共享文件名
	 * @param key共享数据名
	 * @param value数据值
	 * @return
	 */
	public boolean setShare(String key, int value) {
		return getEditor().putInt(key, value).commit();
	}

	/**
	 * 设置共享long值
	 * 
	 * @param context
	 * @param filename共享文件名
	 * @param key共享数据名
	 * @param value数据值
	 * @return
	 */
	public boolean setShare(String key, long value) {
		return getEditor().putLong(key, value).commit();
	}

	/**
	 * 设置共享String值
	 * 
	 * @param context
	 * @param filename共享文件名
	 * @param key共享数据名
	 * @param value数据值
	 * @return
	 */
	public boolean setShare(String key, String value) {
		return getEditor().putString(key, value).commit();
	}

	/**
	 * 删除共享数据
	 * 
	 * @param mContext
	 * @param name共享文件名
	 * @param key共享数据名
	 */
	public void removeShare(String key) {
		getEditor().remove(key).commit();
	}

}
