package com.jiubang.ggheart.components.facebook;

import java.util.Locale;

import android.content.Context;

/**
 * 
 * <br>
 * 类描述: <br>
 * 功能详细描述:
 * 
 * @author ruxueqin
 * @date [2012-11-30]
 */
public class GoFacebookUtil {

	private static boolean sAble = true; // facebook是否可用
	private static boolean sInit = false;

	public static void initEnable(Context context) {
		Locale l = Locale.getDefault();
		String languageString = String.format("%s-%s", l.getLanguage(), l.getCountry());
		sAble = languageString != null && !languageString.contains("zh");
	}

	public static boolean isEnable(Context context) {
		if (!sInit) {
			synchronized(GoFacebookUtil.class) {
				if (!sInit) {
					initEnable(context);
				}
			}
		}
		
		return sAble;
	}
}
