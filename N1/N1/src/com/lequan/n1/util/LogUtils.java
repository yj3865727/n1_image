package com.lequan.n1.util;

import android.util.Log;

public class LogUtils {

	// 日志级别
	private static int level = 0;
	private static final String TAG = "N1";

	private static int VERBOSE = Log.VERBOSE;
	private static int DEBUG = Log.DEBUG;
	private static int INFO = Log.INFO;
	private static int WARN = Log.WARN;
	private static int ERROR = Log.ERROR;

	public static void v(String msg) {
		if (level < VERBOSE) {
			Log.v(TAG, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (level < VERBOSE) {
			Log.v(tag, msg);
		}
	}

	public static void d(String msg) {
		if (level < DEBUG) {
			Log.d(TAG, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (level < DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void i(String msg) {
		if (level < INFO) {
			Log.i(TAG, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (level < INFO) {
			Log.i(tag, msg);
		}
	}

	public static void w(String msg) {
		if (level < WARN) {
			Log.w(TAG, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (level < WARN) {
			Log.w(tag, msg);
		}
	}

	public static void e(String msg) {
		if (level < ERROR) {
			Log.e(TAG, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (level < ERROR) {
			Log.e(tag, msg);
		}
	}

}
