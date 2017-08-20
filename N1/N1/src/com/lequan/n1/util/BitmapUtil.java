package com.lequan.n1.util;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;

import android.graphics.Bitmap.Config;
import android.view.View;

public class BitmapUtil {

	private static BitmapUtils mBitmapUtils;
	
	public static final String cachePath=FileUtils.getImageCache()+"/images";

	static {
		mBitmapUtils = new BitmapUtils(UiUtils.getContext(),cachePath);
		mBitmapUtils.configDefaultBitmapConfig(Config.ARGB_8888);
	}

	public static <T extends View> void display(T container, String uri) {
		mBitmapUtils.display(container, uri);
	}
	
	public static <T extends View> void display(T container, String uri,BitmapLoadCallBack<View> callBack) {
		mBitmapUtils.display(container, uri, callBack);
	}

}
