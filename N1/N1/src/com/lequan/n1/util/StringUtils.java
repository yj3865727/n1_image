package com.lequan.n1.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.lequan.n1.entity.WorkPhotosEntity;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Base64;

public class StringUtils {

	/**
	 * 格式化时间字符串
	 * 
	 * @param time
	 * @return
	 */
	public static String timeFormat(String time) {
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
			Date now = new Date();
			Date last = df.parse(time);
			// 如果大于一天--->显示天
			long dif = now.getTime() - last.getTime();// 毫秒
			long days = dif / (24 * 60 * 60 * 1000);
			if (days > 0) {// 超过天
				return days + "天前";
			} else {
				long hours = dif / (60 * 60 * 1000);
				if (hours > 0) {
					return hours + "小时前";
				} else {
					long min = dif / (60 * 1000);
					if (min > 0) {
						return min + "分钟前";
					} else {
						return "1分钟前";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将bitmap转换成string数据
	 */
	public static String convertorBitmap2Str(Bitmap bitmap) {
		if (bitmap != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
			bitmap.compress(CompressFormat.PNG, 100, baos);
			byte[] appicon = baos.toByteArray();// 转为byte数组
			return Base64.encodeToString(appicon, Base64.DEFAULT);
		}
		return null;
	}

	/** 格式化文件大小，保留末尾的0，达到长度一致 */
	public static String formatFileSize(long len, boolean keepZero) {
		String size;
		DecimalFormat formatKeepTwoZero = new DecimalFormat("#.00");
		DecimalFormat formatKeepOneZero = new DecimalFormat("#.0");
		if (len < 1024) {
			size = String.valueOf(len + "B");
		} else if (len < 10 * 1024) {
			// [0, 10KB)，保留两位小数
			size = String.valueOf(len * 100 / 1024 / (float) 100) + "KB";
		} else if (len < 100 * 1024) {
			// [10KB, 100KB)，保留一位小数
			size = String.valueOf(len * 10 / 1024 / (float) 10) + "KB";
		} else if (len < 1024 * 1024) {
			// [100KB, 1MB)，个位四舍五入
			size = String.valueOf(len / 1024) + "KB";
		} else if (len < 10 * 1024 * 1024) {
			// [1MB, 10MB)，保留两位小数
			if (keepZero) {
				size = String.valueOf(formatKeepTwoZero.format(len * 100 / 1024 / 1024 / (float) 100)) + "MB";
			} else {
				size = String.valueOf(len * 100 / 1024 / 1024 / (float) 100) + "MB";
			}
		} else if (len < 100 * 1024 * 1024) {
			// [10MB, 100MB)，保留一位小数
			if (keepZero) {
				size = String.valueOf(formatKeepOneZero.format(len * 10 / 1024 / 1024 / (float) 10)) + "MB";
			} else {
				size = String.valueOf(len * 10 / 1024 / 1024 / (float) 10) + "MB";
			}
		} else if (len < 1024 * 1024 * 1024) {
			// [100MB, 1GB)，个位四舍五入
			size = String.valueOf(len / 1024 / 1024) + "MB";
		} else {
			// [1GB, ...)，保留两位小数
			size = String.valueOf(len * 100 / 1024 / 1024 / 1024 / (float) 100) + "GB";
		}
		return size;
	}

	public static String[] files2PathArray(List<File> allPhotos) {
		if (ValidateUtils.isValidate(allPhotos)) {
			String[] paths = new String[allPhotos.size()];
			for (int i = 0; i < allPhotos.size(); i++) {
				paths[i] = allPhotos.get(i).getAbsolutePath();
			}
			return paths;
		}
		return null;
	}

	public static ArrayList<String> list2StringArray(List<WorkPhotosEntity> photos) {
		if (ValidateUtils.isValidate(photos)) {
			ArrayList<String> paths = new ArrayList<String>();
			for (int i = 0; i < photos.size(); i++) {
				paths.add(photos.get(i).url);
			}
			return paths;
		}
		return null;
	}

}
