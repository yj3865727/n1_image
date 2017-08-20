package com.lequan.n1.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtils {

	private static SpUtils spUtils;
	private SharedPreferences mSp;

	private SpUtils(Context context) {
		mSp = context.getSharedPreferences(Constants.N1_SP, Context.MODE_PRIVATE);
	}

	public static SpUtils getInstance(Context context) {
		if (spUtils == null) {
			synchronized (SpUtils.class) {
				if (spUtils == null) {
					spUtils = new SpUtils(context);
				}
			}
		}
		return spUtils;
	}

	public void putBoolean(String key, boolean value) {
		mSp.edit().putBoolean(key, value).commit();
	}

	public boolean getBoolean(String key) {
		return mSp.getBoolean(key, false);
	}
	
	public void setString(String key,String value){
		mSp.edit().putString(key, value).commit();
	}
	
	public String getString(String key){
		return mSp.getString(key, "");
	}

}
