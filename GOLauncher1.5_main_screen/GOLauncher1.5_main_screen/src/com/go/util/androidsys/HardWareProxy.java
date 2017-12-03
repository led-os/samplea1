package com.go.util.androidsys;

import java.lang.reflect.Method;

import android.app.Activity;
import android.os.Build;
import android.view.Window;

/**
 * 
 * @author liuheng
 *
 */
public class HardWareProxy {
	private static final int FLAG_HARDWARE_ACCELERATED = 0x01000000; // 该值是3.0版本开始有的固定值
	
	public static void hardwareAcceleratedByWindow(Activity context) {
		// android 3.0 API Level = 11, android 4.0 API Level = 14
		if (Build.VERSION.SDK_INT < 11 /* || Build.VERSION.SDK_INT >= 14 */) {
			return;
		}
		Class[] arrayOfClass = new Class[] { Integer.TYPE, Integer.TYPE };
		Method localMethod;
		try {
			localMethod = Window.class.getMethod("setFlags", arrayOfClass);
			Object[] arrayOfObject = new Object[2];
			Integer localInteger1 = Integer.valueOf(FLAG_HARDWARE_ACCELERATED);
			arrayOfObject[0] = localInteger1;
			Integer localInteger2 = Integer.valueOf(FLAG_HARDWARE_ACCELERATED);
			arrayOfObject[1] = localInteger2;
			localMethod.invoke(context.getWindow(), arrayOfObject);
		} catch (Throwable e) {

		}
	}
}
