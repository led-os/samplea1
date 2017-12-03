package com.go.util.graphics;

import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.go.proxy.ApplicationProxy;
import com.go.util.device.Machine;

/**
 * 绘制工具类
 * 
 * @author luopeihuan
 * 
 */
public class DrawUtils {
	public static final int NAVBAR_LOCATION_RIGHT = 1;
	public static final int NAVBAR_LOCATION_BOTTOM = 2;
	public static float sDensity = 1.0f;
	public static int sDensityDpi;
	public static int sWidthPixels;
	public static int sHeightPixels;
	public static float sFontDensity;
	public static int sTouchSlop = 15; // 点击的最大识别距离，超过即认为是移动
	public static int sNavBarLocation;

	private static int sRealWidthPixels;
	private static int sRealHeightPixels;
	private static int sNavBarWidth; // 虚拟键宽度
	private static int sNavBarHeight; // 虚拟键高度
	public static int sStatusHeight; // 平板中底边的状态栏高度
	private static Class sClass = null;
	private static Method sMethodForWidth = null;
	private static Method sMethodForHeight = null;

	// 在某些机子上存在不同的density值，所以增加两个虚拟值
	public static float sVirtualDensity = -1;
	public static float sVirtualDensityDpi = -1;
	/**
	 * dip/dp转像素
	 * 
	 * @param dipValue
	 *            dip或 dp大小
	 * @return 像素值
	 */
	public static int dip2px(float dipVlue) {
		return (int) (dipVlue * sDensity + 0.5f);
	}

	/**
	 * 像素转dip/dp
	 * 
	 * @param pxValue
	 *            像素大小
	 * @return dip值
	 */
	public static int px2dip(float pxValue) {
		final float scale = sDensity;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * sp 转 px
	 * 
	 * @param spValue
	 *            sp大小
	 * @return 像素值
	 */
	public static int sp2px(float spValue) {
		final float scale = sDensity;
		return (int) (scale * spValue);
	}

	/**
	 * px转sp
	 * 
	 * @param pxValue
	 *            像素大小
	 * @return sp值
	 */
	public static int px2sp(float pxValue) {
		final float scale = sDensity;
		return (int) (pxValue / scale);
	}

	public static void resetDensity(Context context) {
		if (context != null && null != context.getResources()) {
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			sDensity = metrics.density;
			sFontDensity = metrics.scaledDensity;
			sWidthPixels = metrics.widthPixels;
			sHeightPixels = metrics.heightPixels;
			sDensityDpi = metrics.densityDpi;
			if (Machine.isTablet(ApplicationProxy.getContext())) {
				sStatusHeight = getTabletScreenHeight(context) - sHeightPixels;
			}
			try {
				final ViewConfiguration configuration = ViewConfiguration.get(context);
				if (null != configuration) {
					sTouchSlop = configuration.getScaledTouchSlop();
				}
			} catch (Throwable e) {
				Log.i("DrawUtils", "resetDensity has error" + e.getMessage());
			}
			resetNavBarHeight(context);
		}
	}

	private static void resetNavBarHeight(Context context) {
		if (context != null) {
			try {
				WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();
				if (sClass == null) {
					sClass = Class.forName("android.view.Display");
				}
				Point realSize = new Point();
				Method method = sClass.getMethod("getRealSize", Point.class);
				method.invoke(display, realSize);
				sRealWidthPixels = realSize.x;
				sRealHeightPixels = realSize.y;
				sNavBarWidth = realSize.x - sWidthPixels;
				sNavBarHeight = realSize.y - sHeightPixels;
			} catch (Throwable e) {
				e.printStackTrace();
				sRealWidthPixels = sWidthPixels;
				sRealHeightPixels = sHeightPixels;
				sNavBarHeight = 0;
			}
		}
		sNavBarLocation = getNavBarLocation();
	}
	
	public static int getTabletScreenWidth(Context context) {
		int width = 0;
		if (context != null) {
			try {
				WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();
				if (sClass == null) {
					sClass = Class.forName("android.view.Display");
				}
				if (sMethodForWidth == null) {
					sMethodForWidth = sClass.getMethod("getRealWidth");
				}
				width = (Integer) sMethodForWidth.invoke(display);
			} catch (Exception e) {
			}
		}

		// Rect rect= new Rect();
		// ((Activity)
		// context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		// int statusbarHeight = height - rect.bottom;
		if (width == 0) {
			width = sWidthPixels;
		}

		return width;
	}

	public static int getTabletScreenHeight(Context context) {
		int height = 0;
		if (context != null) {
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			try {
				if (sClass == null) {
					sClass = Class.forName("android.view.Display");
				}
				if (sMethodForHeight == null) {
					sMethodForHeight = sClass.getMethod("getRealHeight");
				}
				height = (Integer) sMethodForHeight.invoke(display);
			} catch (Exception e) {
			}
		}

		// Rect rect= new Rect();
		// ((Activity)
		// context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		// int statusbarHeight = height - rect.bottom;
		if (height == 0) {
			height = sHeightPixels;
		}

		return height;
	}
	
	public static void setVirtualDensity(float density) {
		sVirtualDensity = density;
	}
	
	public static void setVirtualDensityDpi(float densityDpi) {
		sVirtualDensityDpi = densityDpi;
	}
	
	public static int getRealWidth() {
		if (Machine.IS_SDK_ABOVE_KITKAT) {
			return sRealWidthPixels;
		}
		return sWidthPixels;
	}
	
	public static int getRealHeight() {
		if (Machine.IS_SDK_ABOVE_KITKAT) {
			return sRealHeightPixels;
		}
		return sHeightPixels;
	}
	

	/**
	 * 虚拟键在下面时
	 * @return
	 */
	public static int getNavBarHeight() {
		if (Machine.IS_SDK_ABOVE_KITKAT && Machine.canHideNavBar()) {
			return sNavBarHeight;
		}
		return 0;
	}
	
	/**
	 * 横屏，虚拟键在右边时
	 * @return
	 */
	public static int getNavBarWidth() {
		if (Machine.IS_SDK_ABOVE_KITKAT && Machine.canHideNavBar()) {
			return sNavBarWidth;
		}
		return 0;
	}
	
	public static int getNavBarLocation() {
		if (sRealWidthPixels > sWidthPixels) {
			return NAVBAR_LOCATION_RIGHT;
		}
		return NAVBAR_LOCATION_BOTTOM;
	}
	
	/**
	 * 判断虚拟导航键是否存在
	 * 需要注意存在用户用root的方式隐藏了虚拟导航键，然后用手势返回或呼出home的情况
	 * 这时候sRealHeightPixels = sHeightPixels，sRealWidthPixels = sWidthPixels
	 * @return
	 */
	public static boolean isNavBarAvailable() {
		Log.v("ddz", "sRealHeightPixels=" + sRealHeightPixels
				+ " sHeightPixels=" + sHeightPixels + " sRealWidthPixels="
				+ sRealWidthPixels + " sWidthPixels=" + sWidthPixels);
		if (sRealHeightPixels > sHeightPixels || sRealWidthPixels > sWidthPixels) {
			return true;
		}
		return false;
	}
}
