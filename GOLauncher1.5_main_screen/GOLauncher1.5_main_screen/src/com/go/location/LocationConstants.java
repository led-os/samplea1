package com.go.location;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;

/**
 * 
 * 类描述: 获取城市位置用到的常量
 * 
 * @author liuwenqin
 * @date [2012-9-4]
 */
public class LocationConstants {

	// ////////////////////////////定位方式////////////////////////
	/** 基站定位方式 */
	public static final int WAY_CELL_LOCATION = 1;
	/** 网络定位 */
	public static final int WAY_NETWORK_LOCATION = 2;
	/** GPS定位 */
	public static final int WAY_GPS_LOCATION = 3;
	/** Google基站定位方式 */
	public static final int WAY_GOOGLE_CELL_LOCATION = 4;

	// /////////////////////////定位超时时间////////////////////////
	/** 基站定位超时时间 */
	public static final int CELL_LOCATION_COUNT_TIME = 15;
	/** 网络定位超时时间 */
	public static final int NETWORK_LOCATION_COUNT_TIME = 10;
	/** GPS定位超时时间 */
	public static final int GPS_LOCATION_COUNT_TIME = 30;
	/** Google基站定位超时时间 */
	public static final int GOOGLE_CELL_LOCATION_COUNT_TIME = 10;

	// /////////////////////////定位切换方式////////////////////////
	/** 基站定位换为手机网络定位 */
	public static final int CELL_TO_NETWORK = 1;
	/** 手机网络定位换为GPS定位 */
	public static final int NETWORK_TO_GPS = 2;
	/** 基站定位换为GPS定位 */
	public static final int CELL_TO_GPS = 3;
	/** GPS定位切换为网络定位 */
	public static final int GPS_TO_NETWORK = 4;
	/** GPS定位方式切换为基站定位 */
	public static final int GPS_TO_CELL = 5;
	/** 网络定位方式切换为基站定位 */
	public static final int NETWORK_TO_CELL = 6;

	// ///////////////////////////////////定位失败原因///////////////
	/** 系统不支持定位功能 */
	public static final int LOCATION_UNSUPPORTED_BY_SYSYTEM = 1;
	/** 网络定位没打开 */
	public static final int LOCATION_NETWORK_UNABLED = 2;
	/** 基站定位失败 */
	public static final int LOCATION_CELL_FAILED = 5;
	/** SIM卡不可用 */
	public static final int LOCATION_SIMCARD_NOT_READY = 6;
	/** 网络不可用 */
	public static final int LOCATION_NETWORK_ERROR = 7;
	/** 定位超时 */
	public static final int LOCATION_TIME_OUT = 9;

	/** 某项定位功能开启 */
	public static final int LOCATION_PROVIDER_ENABLED = 1;
	/** 某项定位功能关闭 */
	public static final int LOCATION_PROVIDER_UNABLED = 2;
	/** 某项定位功能系统不支持 */
	public static final int LOCATION_PROVIDER_UNSUPPORTED = 3;

	// ////////////////////////////搜索城市方式////////////////////////

	/** 关键字方式搜索城市 */
	public static final int SEARCH_CITY_BY_KEYWORD = 1;
	/** 定位方式搜索城市 */
	public static final int SEARCH_CITY_BY_LOCATION = 2;
	/** 定位+手动方式添加城市 */
	public static final int SEARCH_CITY_BY_LOCATION_MAUALLY = 3;

	public static SQLiteDatabase openDatabase(Context context, int go_city) {
		// 获得dictionary.db文件的绝对路径
		String dataBasePath = context.getFilesDir() + "/city";
		String databaseFilename = dataBasePath + "/" + "go_city.db";
		File dir = new File(dataBasePath);
		// 创建这个目录
		if (!dir.exists()) {
			dir.mkdir();
		}
		SQLiteDatabase database = null;
		try {
			if (!(new File(databaseFilename)).exists()) {
				// 获得封装go_city.db文件的InputStream对象
				InputStream is = context.getResources()
						.openRawResource(go_city);
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				final int bufferLength = 8192;
				byte[] buffer = new byte[bufferLength];
				int count = 0;
				// 开始复制dictionary.db文件
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
			// 打开/sdcard/dictionary目录中的go_city.db文件
			database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return database;
	}

	public static String readInputStream(InputStream in) throws IOException {
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
		return new String(data, "UTF-8");
	}

	/**
	 * GPS或网络定位是否开启
	 * 
	 * @param provider
	 *            GPS_PROVIDER 或者 NETWORK_PROVIDER
	 * @return 1，定位已开启 2、定位关闭 3、系统不支持该定位功能
	 */
	public static int isProviderEnabled(LocationManager locationManager,
			String provider) {
		int providerStatus = LocationConstants.LOCATION_PROVIDER_UNABLED;
		/** 防止指定的Provider不存在 */
		try {
			if (locationManager.isProviderEnabled(provider)) {
				providerStatus = LocationConstants.LOCATION_PROVIDER_ENABLED;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			providerStatus = LocationConstants.LOCATION_PROVIDER_UNSUPPORTED;
		}
		return providerStatus;
	}
}