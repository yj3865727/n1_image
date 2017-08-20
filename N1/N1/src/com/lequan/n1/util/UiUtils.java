package com.lequan.n1.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.lequan.n1.N1Application;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Process;

public class UiUtils {

	/**
	 * 获取应用上下文
	 * 
	 * @return
	 */
	public static Context getContext() {
		return N1Application.getContext();
	}

	/**
	 * 获取资源
	 * 
	 * @return
	 */
	public static Resources getResource() {
		return getContext().getResources();
	}

	/**
	 * 获取字符串
	 * 
	 * @param resId
	 * @return
	 */
	public static String getString(int resId) {
		return getResource().getString(resId);
	}
	
	/** 计算活动剩余的时间
	 * @param endTime
	 * @return 剩余的天数
	 */
	public static long calculateEndTime(String endTime) {
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
			Date start = new Date();
			Date end = df.parse(endTime);
			long diff = end.getTime() - start.getTime();
			if(diff>0){
				long days = diff / (1000 * 60 * 60 * 24);
				if(diff % (1000 * 60 * 60 * 24)>0){
					days++;
				}
				return days;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 将密度值转换为像素值
	 * 
	 * @param context
	 * @param dip
	 * @return
	 */
	public static int dip2px(int dip) {
		float density = getResource().getDisplayMetrics().density;
		return (int) (dip * density + .5f);
	}

	/**
	 * 将像素值转换为密度值
	 * 
	 * @param context
	 * @param px
	 * @return
	 */
	public static int px2dip(int px) {
		float density = getResource().getDisplayMetrics().density;
		return (int) (px / density + .5f);
	}

	/**
	 * 在子线程中改变ui
	 * 
	 * @param task
	 */
	public static void runOnSafe(Runnable task) {
		// 如果在主线程中
		if (Process.myTid() == N1Application.getMainThreadId()) {
			task.run();
		} else {
			N1Application.getHandler().post(task);
		}
	}

	/**
	 * 获得当前进程的名字
	 */
	public static String getCurProcessName() {
		int pid = N1Application.getProcessId();

		ActivityManager activityManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);

		for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

	/**
	 * 获取应用的包名
	 */
	public static String getPackageName() {
		return getContext().getPackageName();
	}
	
	/**
	 * 显示加载对话框
	 */
	public static void showSimpleProcessDialog(ProgressDialog dialog,String message){
		if(dialog!=null&&!dialog.isShowing()){
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setMessage(message);
			dialog.show();
		}
	}
	
	/**
	 * 关闭加载对话框
	 */
	public static void closeProcessDialog(ProgressDialog dialog){
		if(dialog!=null&&dialog.isShowing()){
			dialog.dismiss();
		}
	}

}
