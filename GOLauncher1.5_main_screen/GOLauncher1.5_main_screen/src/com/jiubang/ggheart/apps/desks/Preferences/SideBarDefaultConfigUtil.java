package com.jiubang.ggheart.apps.desks.Preferences;

import android.content.Context;

/**
 * 
 * @author dengdazhong
 *
 */
public class SideBarDefaultConfigUtil {
	public static float getDefaultConfigWidthScale(Context context) {
		String brand = android.os.Build.BRAND;
		if (brand != null && brand.toLowerCase().contains("samsung")/*model.contains("GT-N7000") || model.contains("GT-I9500")*/) {
			return 0.04f;
		}
		return 0.02f;
	}
}
