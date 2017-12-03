package com.go.util.device.meizu;

import android.os.Build;
import android.view.Window;

import com.go.util.lib.ViewCompatProxy;

/**
 * 魅族flyme系统Smartbar隐藏/显示工具类
 * @author dengdazhong
 *
 */
public class SmartBarUtil {
	private final static String MEIZU = "meizu";
	private final static int SHOW_SMARTBAR = 0;
	private final static int HIDE_SMARTBAR = 2;
	/**
	 * 是否为魅族手机且为flyme2.0以上系统
	 * @return
	 */
	private static boolean isMeizuAndFlyMeOs() {
		return Build.BRAND.toLowerCase().contains(MEIZU) && Build.VERSION.SDK_INT >= 14;
	}

	/**
	 * 是否可操作smartbar
	 * @return
	 */
	private static boolean canSmartBarBeHandle() {
		return isMeizuAndFlyMeOs();
	}

	/**
	 * 显示smartbar
	 * @param window
	 */
	public static void showSmartBar(Window window) {
		if (canSmartBarBeHandle()) {
			ViewCompatProxy.setSystemUiVisibility(window.getDecorView(), SHOW_SMARTBAR);
		}
	}

	/**
	 * 隐藏smartbar
	 * @param window
	 */
	public static void hideSmartBar(Window window) {
		if (canSmartBarBeHandle()) {
			ViewCompatProxy.setSystemUiVisibility(window.getDecorView(), HIDE_SMARTBAR);
		}
	}
}
