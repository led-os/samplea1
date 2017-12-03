package com.jiubang.ggheart.zeroscreen.navigation.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;

/**
 * <br>
 * 类描述:工具类 <br>
 * 功能详细描述:将公用的方法抽出来，用来复用
 * 
 * @author panshangan
 * @date [2013-2-16]
 */
public class ToolUtil {
	private static boolean sIsInit = false;

	public static void initValue(Context context) {
		if (!sIsInit) {
			smScale = context.getResources().getDisplayMetrics().density;
			sWidth = context.getResources().getDisplayMetrics().widthPixels;
			sHeight = context.getResources().getDisplayMetrics().heightPixels;
		}
		sIsInit = true;
	}

	/**
	 * 屏幕密度
	 */
	public static float smScale = 0;

	/**
	 * 屏幕宽度
	 */
	public static int sWidth = 0;

	/**
	 * 屏幕高度
	 */
	public static int sHeight = 0;

	private static final float ADJUSTVALUE_FOR_DIP_PX_TRANSFORM = 0.5f;

	private static CRC32 sCreater = null;

	public static final int BUTTON_DISABLED_ALPHA_VAL = 76;
	public static final int BUTTON_ENABLED_ALPHA_VAL = 255;

	/**
	 * 
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(float dipValue) {
		return (int) (dipValue * smScale + ADJUSTVALUE_FOR_DIP_PX_TRANSFORM);
	}

	public static int px2dip(float pxValue) {
		return (int) (pxValue / smScale + ADJUSTVALUE_FOR_DIP_PX_TRANSFORM);
	}

	/**
	 * 判断当前网络环境是否为wifi
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前网络连接状况
	 * 
	 * @param context
	 * @return
	 */
	public static boolean contectionTest(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		return connectivity.getActiveNetworkInfo() != null;
	}

	/**
	 * 判断当前网络连接类型是否是cmwap还是cmnet
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean isCMWAP(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netWrokInfo = connectivityManager.getActiveNetworkInfo();

		// LogUtil.v("网络类型："+netWrokInfo.getTypeName()+"，网络详细信息："+netWrokInfo.getExtraInfo());
		if (netWrokInfo == null) {
			return false;
		}
		if (netWrokInfo.getTypeName().equalsIgnoreCase("mobile")
				&& netWrokInfo.getExtraInfo().equals("cmwap")) {
			// LogUtil.v("检测到cmwap");
			return true;
		}
		return false;
	}

	/**
	 * Bitmap转byte[]方法
	 * 
	 * @param bitmap
	 * @return
	 */
	public static byte[] bitmap2Bytes(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		return out.toByteArray();
	}

	/**
	 * byte[] 转 Bitmap
	 * 
	 * @param b
	 * @return
	 */
	public static Bitmap bytes2Bimap(byte[] b) {
		if (b == null || b.length == 0) {
			return null;
		}
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
		} catch (OutOfMemoryError e) {
			// LogPrint.i("bytes2Bimap", "OutOfMemoryError" + " " +
			// e.getMessage());
		} catch (Exception e) {
			// LogPrint.i("bytes2Bimap", "Exception" + " " + e.getMessage());
		}
		return bmp;
	}

	public static final boolean saveBitmap(Bitmap bmp, String bmpName) {
		if (null == bmp) {
			return false;
		}

		FileOutputStream stream = null;
		try {
			File file = new File(bmpName);
			if (file.exists()) {
				boolean bDel = file.delete();
				if (!bDel) {
					return false;
				}
			} else {
				File parent = file.getParentFile();
				if (null == parent) {
					return false;
				}
				if (!parent.exists()) {
					boolean bDir = parent.mkdirs();
					if (!bDir) {
						return false;
					}
				}
			}
			boolean bCreate = file.createNewFile();
			if (!bCreate) {
				return false;
			}

			stream = new FileOutputStream(file);
			boolean bOk = bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
			if (!bOk) {
				return false;
			}
		} catch (Exception e) {
			return false;
		} finally {
			if (null != stream) {
				try {
					stream.close();
				} catch (Exception e2) {

				}
			}
		}

		return true;
	}

	/**
	 * 根据图标获取平均颜色值
	 * 
	 * @param bmp
	 * @return int
	 */
	public static int obtainColor(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();

		int heightToRead = height;
		int stride = width;
		int dividenNum = 1;
		long pixelTotalNum = width * height;

		if (pixelTotalNum > 0 && pixelTotalNum <= 64 * 64) {
			dividenNum = 1;
		} else if (pixelTotalNum > 64 * 64 && pixelTotalNum <= 128 * 128) {
			dividenNum = 2;
		} else if (pixelTotalNum > 128 * 128 && pixelTotalNum <= 256 * 256) {
			dividenNum = 4;
		} else if (pixelTotalNum > 256 * 256 && pixelTotalNum <= 512 * 512) {
			dividenNum = 6;
		} else if (pixelTotalNum > 512 * 512) {
			dividenNum = 8;
		} else {
			return 0xffffffff;
		}

		// To speed up the processing, we pass some rows not to read.
		// This trick dose not weaken the original function obviously.
		heightToRead = height / dividenNum;
		stride = width * dividenNum;

		if (heightToRead <= 0) {
			heightToRead = height;
			stride = width;
		}

		int pixels[] = new int[(int) pixelTotalNum];
		bmp.getPixels(pixels, 0, stride, 0, 0, width, heightToRead);
		long redCounter = 0;
		long greenCounter = 0;
		long blueCounter = 0;

		int i = 0;
		int firstElementInRowIndex = 0;
		for (int row = 0; row < height; row += dividenNum) {
			firstElementInRowIndex = row * width;
			for (int column = 0; column < width; column++) {
				i = firstElementInRowIndex + column;
				redCounter += (pixels[i] & 0xff0000) >> 16;
				greenCounter += (pixels[i] & 0xff00) >> 8;
				blueCounter += pixels[i] & 0xff;
			}
		}

		long pixelRealNum = width * heightToRead;
		redCounter /= pixelRealNum;
		greenCounter /= pixelRealNum;
		blueCounter /= pixelRealNum;

		int resultColor = ((int) blueCounter & 0xff)
				| ((int) (greenCounter & 0xff) << 8)
				| (((int) redCounter & 0xff) << 16);
		resultColor |= 0xff000000;

		return resultColor;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		int width = drawable.getIntrinsicWidth();
		width = width > 0 ? width : 1;
		int height = drawable.getIntrinsicHeight();
		height = height > 0 ? height : 1;

		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public static String getListStr(List<?> idList) {
		if (idList == null || idList.size() == 0) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < idList.size(); ++i) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append("\'" + idList.get(i) + "\'");
		}

		return sb.toString();
	}

	public static long getBookamrkCrc(String title, String url, long folderId,
			boolean isFolder) {
		long value = -1;
		if (title == null || ((url == null) && !isFolder)) {
			return value;
		}
		if (sCreater == null) {
			sCreater = new CRC32();
		}
		sCreater.reset();
		long parentId = folderId > 0 ? folderId : 0;
		sCreater.update(title.getBytes());
		sCreater.update(converLongToBytes(parentId));
		if (!isFolder) {
			sCreater.update(url.getBytes());
		}
		value = sCreater.getValue();
		return value;
	}

	/**
	 * <br>
	 * 功能简述:long 他哦byte <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param view
	 */
	public static byte[] converLongToBytes(long l) {

		byte[] b = new byte[8];
		b = java.lang.Long.toString(l).getBytes();
		return b;
	}

	/**
	 * <br>
	 * 功能简述:根据屏幕宽度计算对话框宽度 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param view
	 */
	public static void calculateDialogWidth(View view) {
		LinearLayout.LayoutParams params = (LayoutParams) view
				.getLayoutParams();
		params.width = sWidth - 2 * dip2px(19);
	}

	/**
	 * <br>
	 * 功能简述:设置dialog自适应屏幕宽度 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param
	 */
	public static void setDialogMatchParent(Dialog dialog) {
		if (null == dialog) {
			return;
		}
		// 4.x以下就不设置
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return;
		}
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		dialogWindow.setAttributes(lp);
	}

	public static void calculateListDialogWidth(View view) {
		LinearLayout.LayoutParams params = (LayoutParams) view
				.getLayoutParams();
		params.width = sWidth - 2 * dip2px(18);
	}

	private static final String DOTCOM = ".com";
	private static final String DOTNET = ".net";
	private static final String DOTCN = ".cn";
	public static final String HTTPHEAD = "http://";
	public static final String HTTPSHEAD = "https://";
	private static final String NULLSTR = "";
	private static final char DOT = '.';

	/**
	 * 获取Url的域名
	 * 
	 * @param url
	 * @return
	 */
	public static String getDomian(String url) {
		if (url == null) {
			return null;
		}

		if (url.contains(DOTCOM)) {
			return getKey(url, DOTCOM);
		} else if (url.contains(DOTNET)) {
			return getKey(url, DOTNET);
		} else if (url.contains(DOTCN)) {
			return getKey(url, DOTCN);
		}

		return url;
	}

	private static String getKey(String url, String end) {
		int a = url.indexOf(end);

		if (a != -1) {
			String head = url.substring(0, a);
			int b = head.lastIndexOf(DOT);
			if (b != -1) {
				return head.substring(b + 1).concat(end);
			}

			return head.replace(HTTPHEAD, NULLSTR).concat(end);
		}

		return url;
	}

	/**
	 * 缩放 裁剪图片
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap decodeFile(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		int h = options.outHeight;
		int w = options.outWidth;

		// 如果高度或宽度小于80 则是小图标 丢弃
		if (h < 80 || w < 80) {
			return null;
		}

		options.inSampleSize = w / sWidth + 1;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inJustDecodeBounds = false;

		boolean bool = true;
		while (bool) {
			try {
				bitmap = BitmapFactory.decodeFile(path, options);
				bool = false;
			} catch (OutOfMemoryError e) {
				// LogPrint.i("RssController", path + " " + e.getMessage());
				options.inSampleSize++;
			} catch (Throwable e) {
				// LogPrint.i("RssController", path + " " + e.getMessage());
				bool = false;
			}
		}

		return bitmap;
	}

	/**
	 * 高质量解码 用于搜索背景图
	 */
	public static Bitmap decodeFileHigh(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		int w = options.outWidth;

		// options.inSampleSize = w / sWidth;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inJustDecodeBounds = false;

		boolean bool = true;
		while (bool) {
			try {
				bitmap = BitmapFactory.decodeFile(path, options);
				bool = false;
			} catch (OutOfMemoryError e) {
				options.inSampleSize++;
			} catch (Throwable e) {
				bool = false;
			}
		}

		return bitmap;
	}

	/**
	 * <br>
	 * 功能简述:获取图片 <br>
	 * 功能详细描述: <br>
	 * 注意:不对图片进行裁剪
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap decodeFileWithoutClip(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		int w = options.outWidth;
		options.inSampleSize = w / sWidth + 1;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inJustDecodeBounds = false;

		boolean bool = true;
		while (bool) {
			try {
				bitmap = BitmapFactory.decodeFile(path, options);
				bool = false;
			} catch (OutOfMemoryError e) {
				// LogPrint.i("RssController", path + " " + e.getMessage());
				options.inSampleSize++;
			} catch (Throwable e) {
				// LogPrint.i("RssController", path + " " + e.getMessage());
				bool = false;
			}
		}

		return bitmap;
	}

	/**
	 * author: lengjianxing Check if a bitmap visible simply by testing five
	 * pixels value. Warning: it's simple, but not always right!!
	 * 
	 * @param icon
	 * @return
	 */
	public static boolean isFavoriteBitmapVisible(Bitmap icon) {

		if (null != icon) {
			int width = icon.getWidth();
			int height = icon.getHeight();

			if (0 < width && 0 < height) {
				int color1 = icon.getPixel(width / 2, height / 4);
				int color2 = icon.getPixel(width / 4, height / 2);
				int color3 = icon.getPixel(width / 2, 3 * height / 4);
				int color4 = icon.getPixel(3 * width / 4, height / 2);
				int color5 = icon.getPixel(width / 2, height / 2);

				if (0 != color1 + color2 + color3 + color4 + color5) {
					return true;
				}
			}
		}

		return false;
	}

	public static Bitmap decodeByteArray(byte[] mIconByte) {
		if (mIconByte == null) {
			return null;
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeByteArray(mIconByte, 0,
				mIconByte.length, options);
		// int w = options.outWidth;
		//
		// options.inSampleSize = w / sWidth + 1;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inJustDecodeBounds = false;

		boolean bool = true;
		while (bool) {
			try {
				bitmap = BitmapFactory.decodeByteArray(mIconByte, 0,
						mIconByte.length, options);
				bool = false;
			} catch (OutOfMemoryError e) {
				// options.inSampleSize++;
			} catch (Throwable e) {
				bool = false;
			}
		}

		return bitmap;
	}

	/**
	 * <br>
	 * 功能简述: 将字符串转换成数字 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param aValue
	 * @return
	 */
	public static int toInt(final String aValue) {

		int iRet = 0;

		if (aValue == null || aValue.length() == 0) {
			return 0;
		}

		try {
			iRet = Integer.valueOf(aValue).intValue();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return iRet;
	}

	/**
	 * 获取语言
	 */
	public static String getLanguage() {
		return Locale.getDefault().getLanguage();
	}

	/**
	 * 获取国家
	 */
	public static String getCountry() {
		return Locale.getDefault().getCountry();
	}

	/**
	 * 解压缩收到的数据流
	 */
	public static String unzipDataAndLog(InputStream inStream) {
		try {
			byte[] old_bytes = toByteArray(inStream);
			byte[] new_bytes = ungzip(old_bytes);
			return new String(new_bytes, "utf-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}

	public static int copy(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[1024 * 4];
		int count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static byte[] ungzip(byte[] bs) throws Exception {
		GZIPInputStream gzin = null;
		ByteArrayInputStream bin = null;
		try {
			bin = new ByteArrayInputStream(bs);
			gzin = new GZIPInputStream(bin);
			return toByteArray(gzin);
		} catch (Exception e) {
			throw e;
		} finally {
			if (bin != null) {
				bin.close();
			}
		}
	}

	/**
	 * author:lengjianxing String filter for sql search, escaping keywords of
	 * sqllite with escape char "/".
	 */
	public static String sqliteEscape(String keyWord) {
		// Escape keywords used by sqllite internally.
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

	/**
	 * 采用随机数的形式模拟imei号，避免申请权限。
	 * 
	 * @param context
	 * @return
	 */
	public static String getVirtualIMEI(Context context) {
		String deviceidString = "";
		try {
			long randomDeviceid = SystemClock.elapsedRealtime();
			Random rand = new Random();
			long randomLong = rand.nextLong();
			while (randomLong == Long.MIN_VALUE) {
				randomLong = rand.nextLong();
			}
			randomDeviceid += Math.abs(randomLong);
			deviceidString = String.valueOf(randomDeviceid);
			rand = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deviceidString;
	}

	/**
	 * 通过应用程序包名判断程序是否安装的方法
	 * 
	 * @param packageName
	 *            应用程序包名
	 * @return 程序已安装返回TRUE，否则返回FALSE
	 */
	public static boolean isApplicationExsit(Context context, String packageName) {
		boolean result = false;
		if (context != null && packageName != null) {
			try {
				context.getPackageManager().getPackageInfo(packageName,
						PackageManager.GET_SHARED_LIBRARY_FILES);
				result = true;
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 获取语言和国家地区的方法 格式: SIM卡方式：cn 系统语言方式：zh-CN
	 * 
	 * @return
	 */
	public static String language(Context context) {

		String ret = null;
		Locale locale = null;
		if (locale == null) {
			locale = Locale.getDefault();
		}

		if (ret == null || ret.equals("")) {
			ret = String.format("%s_%s", locale.getLanguage().toLowerCase(),
					locale.getCountry().toLowerCase());
		}
		return null == ret ? "error" : ret;
	}

	/**
	 * 国家
	 * 
	 * @param context
	 * @return 取本地语言代表的国家
	 */
	public static String local(Context context) {
		String ret = null;
		Locale locale = null;
		if (locale == null) {
			locale = Locale.getDefault();
		}
		ret = locale.getCountry().toLowerCase();
		return null == ret ? "error" : ret;
	}

	/**
	 * 获取设备分辨率
	 * 
	 * @param context
	 * @return
	 */
	public static String getDisplay(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wMgr = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		wMgr.getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		return width + "*" + height;
	}

	/**
	 * 获取当前网络状态，wifi，GPRS，3G，4G
	 * 
	 * @param context
	 * @return
	 */
	public static String buildNetworkState(Context context) {
		String ret = "";
		try {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkinfo = manager.getActiveNetworkInfo();
			if (networkinfo != null
					&& networkinfo.getType() == ConnectivityManager.TYPE_WIFI) {
				ret = "WIFI";
			} else if (networkinfo != null
					&& networkinfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				int subtype = networkinfo.getSubtype();
				switch (subtype) {
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_CDMA:
				case TelephonyManager.NETWORK_TYPE_EDGE:
				case TelephonyManager.NETWORK_TYPE_GPRS:
				case TelephonyManager.NETWORK_TYPE_IDEN:
					// 2G
					ret = "2G" /*
								 * + "(typeid = " + networkinfo.getType() +
								 * "  typename = " + networkinfo.getTypeName() +
								 * "  subtypeid = " + networkinfo.getSubtype() +
								 * "  subtypename = " +
								 * networkinfo.getSubtypeName() + ")"
								 */;
					break;
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_UMTS:
					// 3G,4G
					ret = "3G/4G" /*
								 * + "(typeid = " + networkinfo.getType() +
								 * "  typename = " + networkinfo.getTypeName() +
								 * "  subtypeid = " + networkinfo.getSubtype() +
								 * "  subtypename = " +
								 * networkinfo.getSubtypeName() + ")"
								 */;
					break;
				case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				default:
					// unknow
					ret = "UNKNOW" /*
									 * + "(typeid = " + networkinfo.getType() +
									 * "  typename = " +
									 * networkinfo.getTypeName() +
									 * "  subtypeid = " +
									 * networkinfo.getSubtype() +
									 * "  subtypename = " +
									 * networkinfo.getSubtypeName() + ")"
									 */;
					break;
				}
			} else {
				ret = "UNKNOW" /*
								 * + "(typeid = " + networkinfo.getType() +
								 * "  typename = " + networkinfo.getTypeName() +
								 * ")"
								 */;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 获取版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String buildVersion(Context context) {
		String ret = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			ret = pi.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static boolean isCnUser(Context context) {
		boolean result = false;

		if (context != null) {
			// 从系统服务上获取了当前网络的MCC(移动国家号)，进而确定所处的国家和地区
			TelephonyManager manager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			// SIM卡状态
			boolean simCardUnable = manager.getSimState() != TelephonyManager.SIM_STATE_READY;
			String simOperator = manager.getSimOperator();

			if (simCardUnable || TextUtils.isEmpty(simOperator)) {
				// 如果没有SIM卡的话simOperator为null，然后获取本地信息进行判断处理
				// 获取当前国家或地区，如果当前手机设置为简体中文-中国，则使用此方法返回CN
				String curCountry = Locale.getDefault().getCountry();
				if (curCountry != null && curCountry.contains("CN")) {
					// 如果获取的国家信息是CN，则返回TRUE
					result = true;
				} else {
					// 如果获取不到国家信息，或者国家信息不是CN
					result = false;
				}
			} else if (simOperator.startsWith("460")) {
				// 如果有SIM卡，并且获取到simOperator信息。
				/**
				 * 中国大陆的前5位是(46000) 中国移动：46000、46002 中国联通：46001 中国电信：46003
				 */
				result = true;
			}
		}

		return result;
	}

	public static void changeViewClickableState(ImageView v, boolean isEnabled) {
		if (null == v) {
			return;
		}

		if (isEnabled) {
			v.setClickable(true);
			v.setAlpha(BUTTON_ENABLED_ALPHA_VAL);
		} else {
			v.setClickable(false);
			v.setAlpha(BUTTON_DISABLED_ALPHA_VAL);
		}
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static JSONObject getJSONhead(Context context) throws JSONException {
		JSONObject phead = new JSONObject();

		phead.put("pversion", 2);
		phead.put("aid", getAndroidID(context));
		phead.put("cid", 1);
		phead.put("cversion", 1);
		phead.put("uid", getAndroidID(context));
		phead.put("channel", GoStorePhoneStateUtil.getUid(context));
		phead.put("local", getCountry());
		phead.put("lang", getLanguage());
		phead.put("sdk", android.os.Build.VERSION.SDK_INT);
		phead.put("imsi", ToolUtil.getImsi(context));

		return phead;
	}

	private static String getCVersion(Context context) {
		String curVersion = context.getString(R.string.curVersion);
		int index = curVersion.indexOf("beta");

		if (index > 0) {
			curVersion = curVersion.substring(0, index);
		} else if ((index = curVersion.indexOf("Beta")) > 0) {
			curVersion = curVersion.substring(0, index);
		}
		return curVersion;
	}

	/**
	 * 获取Android ID
	 * 
	 * @param context
	 * @return
	 */
	private static String getAndroidID(Context context) {
		return Settings.Secure.getString(context.getContentResolver(),
				Settings.Secure.ANDROID_ID);

	}

	public static String getImsi(Context context) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (manager != null) {
			return manager.getSimOperator();
		}

		return null;
	}

}
