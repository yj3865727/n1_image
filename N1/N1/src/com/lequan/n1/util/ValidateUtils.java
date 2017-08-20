package com.lequan.n1.util;

import java.util.Collection;

public class ValidateUtils {

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isValidate(String str) {
		if ("".equals(str) || str == null) {
			return false;
		}
		return true;
	}

	/**
	 * 判断该字符串是否是数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumbser(String str) {
		try {
			Long.parseLong(str);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断集合是否有效
	 */
	public static boolean isValidate(@SuppressWarnings("rawtypes") Collection c) {
		if (c != null && c.size() > 0) {
			return true;
		}
		return false;
	}

	public static boolean isValidate(Object[] imagePaths) {
		if(imagePaths!=null&&imagePaths.length>0){
			return true;
		}
		return false;
	}

}
