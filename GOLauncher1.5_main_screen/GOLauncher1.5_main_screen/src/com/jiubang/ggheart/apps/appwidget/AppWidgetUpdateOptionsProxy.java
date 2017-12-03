package com.jiubang.ggheart.apps.appwidget;

import java.lang.reflect.Method;

import android.appwidget.AppWidgetManager;
import android.os.Bundle;

/**
 * 4.1之上缩放widget后通知widget大小发生改变
 * @author dengdazhong
 *
 */
public class AppWidgetUpdateOptionsProxy {
	public static final String OPTION_APPWIDGET_MIN_WIDTH = "appWidgetMinWidth";

	public static final String OPTION_APPWIDGET_MIN_HEIGHT = "appWidgetMinHeight";

	public static final String OPTION_APPWIDGET_MAX_WIDTH = "appWidgetMaxWidth";

	public static final String OPTION_APPWIDGET_MAX_HEIGHT = "appWidgetMaxHeight";

	public static void updateAppWidgetOptions(AppWidgetManager manager, int appWidgetId,
			Bundle options) {
		Method method;
		try {
			method = manager.getClass().getMethod("updateAppWidgetOptions", Integer.TYPE,
					Bundle.class);
			method.invoke(manager, appWidgetId, options);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
