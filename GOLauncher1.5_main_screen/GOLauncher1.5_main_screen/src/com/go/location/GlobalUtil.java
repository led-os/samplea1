package com.go.location;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

/**
 * 常用的方法
 * 
 * @author Mark_Wen
 * 
 */
public class GlobalUtil {
	/**
	 * 获取客户端手机的语言,注意是帶地區的
	 * 
	 * @param context
	 * 
	 * @return
	 */
	public static String getLanguage(Context context) {

		String language = "en_US";
		try {
			Locale locale = context.getResources().getConfiguration().locale;
			language = locale.getLanguage() + "_" + locale.getCountry();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return language;
	}

	/**
	 * 检查当前网络状态是否可用
	 * 
	 * @param mContext
	 * @return true，网络可用；false，网络不可用
	 */
	public static boolean isNetworkOK(Context mContext) {
		boolean result = false;
		if (mContext != null) {
			android.net.ConnectivityManager cm = (android.net.ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				android.net.NetworkInfo networkInfo = cm.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					result = true;
				}
			}
		}
		return result;
	}

	/** 获取sim卡状态 */
	public static int getSimState(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimState();
	}

	/**
	 * 获取当前软件的版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersion(Context context) {

		String versionName = null;
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = info.versionName;
			if (versionName == null || versionName.length() < 0) {
				versionName = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			versionName = "";
		}
		return versionName;
	}

	public static boolean isInternalUser(Context context) {
		boolean result = false;
		if (context != null) {
			// 通过版本控制器拿sim卡地区码
			String simOperator = getSimOperator(context);
			// 获取到simOperator信息
			if (simOperator != null && simOperator.equalsIgnoreCase("CN")) {
				result = true;
			}
		}
		return result;
	}

	public static String getSimOperator(Context context) {

		String simOperator = null;
		// 从系统服务上获取了当前网络的MCC(移动国家号)，进而确定所处的国家和地区
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		// SIM卡状态
		boolean simCardUnable = manager.getSimState() == TelephonyManager.SIM_STATE_READY;
		if (simCardUnable) {
			simOperator = manager.getSimCountryIso();
		} else {
			// 如果获取不到国家信息，则虚拟一个外国地区码
			Locale locale = context.getResources().getConfiguration().locale;
			simOperator = locale.getCountry();
			if ("".equals(simOperator)) {
				simOperator = "ZZ";
			}
		}
		return simOperator;
	}

	/**
	 * <br>
	 * 功能简述: 读取输入流，转为字符串.默认使用UTF-8格式
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String readInputStream(InputStream in) throws IOException {
		return readInputStream(in, "UTF-8");
	}

	/**
	 * <br>
	 * 功能简述: 读取输入流，转为字符串
	 * 
	 * @param in
	 * @param charset
	 *            字符格式
	 * @return
	 * @throws IOException
	 */
	public static String readInputStream(InputStream in, String charset)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		final int bufferLength = 1024;
		byte[] buf = new byte[bufferLength];
		int len = 0;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		byte[] data = out.toByteArray();
		in.close();
		out.close();
		return new String(data, charset);
	}
}
