package com.go.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * 字符串处理工具类
 * @author caoyaming
 *
 */
public class StringUtil {
	//UTF-8
	private static final String DEFAULT_CHARSET_UTF8 = "UTF-8";
	/**
	 * 将字符串转换为大写
	 * @param obj 要转换的字符串
	 * @return 转换后的字符串
	 */
	public static String toUpperCase(Object obj) {
		if (obj == null) {
			obj = "";
		}
		return obj.toString().trim().toUpperCase();
	}
	/**
	 * 将字符串转换为小写
	 * @param obj 要转换的字符串
	 * @return 转换后的字符串
	 */
	public static String toLowerCase(Object obj) {
		if (obj == null) {
			obj = "";
		}
		return obj.toString().trim().toLowerCase();
	}
	/**
	 * 将Object类型转换为Integer
	 * @param srcStr
	 * @param defaultValue
	 * @return
	 */
	public static Integer toInteger(Object srcStr, Integer defaultValue) {
		try {
			if (srcStr != null && StringUtil.isInt(srcStr)) {
				String s = srcStr.toString().replaceAll("(\\s)", "");
				return s.length() > 0 ? Integer.valueOf(s) : defaultValue;
			}
		} catch (Exception e) {

		}
		return defaultValue;
	}
	/**
	 * 将Object类型转换为Long
	 * @param srcStr
	 * @param defaultValue
	 * @return
	 */
	public static Long toLong(Object srcStr, Long defaultValue) {
		try {
			if (srcStr != null && StringUtil.isInt(srcStr)) {
				String s = srcStr.toString().replaceAll("(\\s)", "");
				return s.length() > 0 ? Long.parseLong(s) : defaultValue;
			}
		} catch (Exception e) {

		}
		return defaultValue;
	}
	/**
	 * 判断是否为数字
	 * @param srcStr
	 * @return
	 */
	public static boolean isInt(Object srcStr) {
		if (srcStr == null) {
			return false;
		}
		String s = srcStr.toString().replaceAll("(\\s)", "");
		Pattern p = Pattern.compile("([-]?[\\d]+)");
		Matcher m = p.matcher(s);
		return m.matches();
	}
	/**
	 * 将Object类型转换为String,且去除空格
	 * @param obj
	 * @return
	 */
	public static String toString(Object obj) {
		if (obj == null) {
			obj = "";
		}
		return obj.toString().trim();
	}
	/**
	 * 判断字符串是否为数字
	 * @param str 字符串
	 * @return true:数字   false:非数字
	 */
	public static boolean isNumber(String str) {
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}
	/**
	 * 去空格
	 * @param srcStr
	 * @return
	 */
	public static String trim(Object srcStr) {
		if (srcStr != null) {
			return srcStr.toString().trim();
		}
		return null;
	}

	//==============================CacheUtil.java===================
	/** <br>功能简述:字符串转换成二进制数据流
	 * <br>功能详细描述:使用utf-8编码
	 * <br>注意:
	 * @param src
	 * @return
	 */
	public static byte[] stringToByteArray(String src) {
		if (TextUtils.isEmpty(src)) {
			return null;
		}
		byte[] ret = null;
		try {
			ret = src.getBytes(DEFAULT_CHARSET_UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/** <br>功能简述:二进制数据流转换成字符串
	 * <br>功能详细描述:使用utf-8编码
	 * <br>注意:
	 * @param src
	 * @return
	 */
	public static String byteArrayToString(byte[] src) {
		if (src == null) {
			return null;
		}
		String ret = null;
		try {
			ret = new String(src, DEFAULT_CHARSET_UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static byte[] jsonToByteArray(JSONObject json) {
		if (json == null) {
			return null;
		}
		byte[] ret = null;
		ret = stringToByteArray(json.toString());
		return ret;
	}

	public static JSONObject byteArrayToJson(byte[] src) {
		if (src == null) {
			return null;
		}
		JSONObject ret = null;		
		String str = byteArrayToString(src);
		if (str == null) {
			return null;
		}
		try {
			ret = new JSONObject(str);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 将整个url进行Encode
	 * 
	 * @param url ：请求的url
	 * @return
	 */
	public static String urlEncode(String url) {
		String reutrnUrl = url;
		try {
			// url中参数进行urlencode
			if (url.indexOf('?') > 0) {
				// uri
				String uri = url.substring(url.indexOf("://") + 3, url.indexOf("?"));
				uri = URLEncoder.encode(uri, DEFAULT_CHARSET_UTF8);
				uri = uri.replace("%2F", "/");
				// paramStr
				String paramStr = url.substring(url.indexOf('?') + 1);
				if (paramStr != null && paramStr.length() > 0) {
					paramStr = URLEncodedUtils.format(URLEncodedUtils.parse(URI.create(paramStr), DEFAULT_CHARSET_UTF8), DEFAULT_CHARSET_UTF8);
					url = url.substring(0, url.indexOf("://")) + "://" + uri + "?" + paramStr;
				} else {
					url = url.substring(0, url.indexOf("://")) + "://" + uri;
				}
			} else {
				// uri
				String uri = url.substring(url.indexOf("://") + 3);
				uri = URLEncoder.encode(uri, DEFAULT_CHARSET_UTF8);
				uri = uri.replace("%2F", "/");
				url = url.substring(0, url.indexOf("://")) + "://" + uri;
			}
			url = url.trim().replace(" ", "%20");
			url = url.trim().replace("+", "%20");
			return url;
		} catch (Exception e) {
			return reutrnUrl;
		}
	}
	/**
	 * 转义SQLite特殊字符
	 * @param keyWord
	 * @return
	 */
	public static String sqliteEscape(String keyWord) {  
	    keyWord = keyWord.replace("/", "//");  
	    keyWord = keyWord.replace("'", "''");  
	    keyWord = keyWord.replace("[", "/[");  
	    keyWord = keyWord.replace("]", "/]");  
	    keyWord = keyWord.replace("%", "/%");  
	    keyWord = keyWord.replace("&", "/&");  
	    keyWord = keyWord.replace("_", "/_");  
	    keyWord = keyWord.replace("(", "/(");  
	    keyWord = keyWord.replace(")", "/)");  
	    return keyWord;  
	}
}
