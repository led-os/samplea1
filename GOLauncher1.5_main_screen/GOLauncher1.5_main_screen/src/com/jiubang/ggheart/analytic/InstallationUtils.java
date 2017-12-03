package com.jiubang.ggheart.analytic;

import android.content.Context;

import com.jiubang.ggheart.apps.desks.diy.pref.PrefConst;
import com.jiubang.ggheart.apps.desks.diy.pref.PrivatePreference;
/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2012-9-27]
 */
public class InstallationUtils {

	public static boolean isRefInfoStored(Context context) {
		PrivatePreference pref = PrivatePreference.getPreference(context);
		return pref.getBoolean(PrefConst.KEY_REFERRER_INFO_STORE_FLAG, false);
	}

	public static void refInfoStored(Context context) {
		PrivatePreference pref = PrivatePreference.getPreference(context);
		pref.putBoolean(PrefConst.KEY_REFERRER_INFO_STORE_FLAG, true);
		pref.commit();
	}

	public static void needStoreRefInfo(Context context) {
		PrivatePreference pref = PrivatePreference.getPreference(context);
		pref.putBoolean(PrefConst.KEY_REFERRER_INFO_STORE_FLAG, false);
		pref.commit();
	}
}
