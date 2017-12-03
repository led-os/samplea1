package com.go.util;

import android.text.TextUtils;

/**
 * 字符串操作工具
 * @author liuheng
 *
 */
public class StringUtils {
	/**
	 * 判断是否是数字
	 * @param ch
	 * @return
	 */
	public static boolean isNumeric(char ch) {
		if (ch < 48 || ch > 57) {
			return false;
		}
		return true;
	}

	/**
	 * 判断是否是字母
	 * @param ch
	 * @return
	 */
	public static boolean isCharacter(char ch) {
		if ((ch >= 65 && ch <= 90) || (ch >= 97 && ch <= 122)) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 是否为邮箱格式
	 * @param email 邮箱
	 * @return
	 */
	public static boolean isEmailFormat(String email) {
		if (!TextUtils.isEmpty(email) && email.trim().matches("\\w+@\\w+\\.\\w+")) {
			//邮箱格式
			return true;
		} 
		return false;
	}
}
