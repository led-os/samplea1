package com.go.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.appwidget.AppWidgetHostView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.go.util.lib.ViewCompatProxy;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.ConstValue;
/**
 * 
 * @author dengdazhong
 *
 */
public class GoViewCompatProxy extends ViewCompatProxy {
	public static final boolean DEBUG = ConstValue.DEBUG;
	public static final String TAG = "GoViewCompatProxy";
	public static final int SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN = 0x400;
	public static final int SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION = 0x200;
	
	/**
	 * AppWidgetHostView.updateAppWidgetSize() 的一个代理方法
	 * AppWidgetHostView.updateAppWidgetSize (Bundle newOptions, int minWidth, int minHeight, int maxWidth, int maxHeight) Added in API level 16
	 * @param option
	 * @param minW 单位是dp不是px
	 * @param minH 单位是dp不是px
	 * @param maxW 单位是dp不是px
	 * @param maxH 单位是dp不是px
	 */
	public static void updateAppWidgetSize(AppWidgetHostView hostView, Bundle option, int minW, int minH, int maxW, int maxH) {
		// API16才有这个方法
		if (Build.VERSION.SDK_INT >= 16) {
			if (hostView != null) {
				Class cls = hostView.getClass();
				try {
					Method method = cls.getMethod("updateAppWidgetSize", Bundle.class, int.class, int.class, int.class, int.class);
					method.invoke(hostView, option, minW, minH, maxW, maxH);
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (DEBUG) {
					Log.e(TAG, "The Argument hostView can't be null");
				}
			}
		} else {
			if (DEBUG) {
				Log.e(TAG,
						"unable to proxy AppWidgetHostView.updateAppWidgetSize(Bundle, int, int, int, int), because the method is added in api16 and the current api level is "
								+ Build.VERSION.SDK_INT);
			}
		}
	}
}
