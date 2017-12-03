package com.jiubang.ggheart.data.statistics;

import java.util.Map;

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
public class SearchStatisticSaver {
	// MenuKey_Long
	// SearchKey
	public static void saveStatistic(Context context, Map<String, Integer> content) {
		PrivatePreference preferences = PrivatePreference.getPreference(context);
		Integer value = content.get(PrefConst.KEY_LONG_MENU);
		if (null == value) {
			preferences.putInt(PrefConst.KEY_LONG_MENU, 0);
		} else {
			preferences.putInt(PrefConst.KEY_LONG_MENU, value.intValue());
		}
		value = content.get(PrefConst.KEY_SEARCH_KEY);
		if (null == value) {
			preferences.putInt(PrefConst.KEY_SEARCH_KEY, 0);
		} else {
			preferences.putInt(PrefConst.KEY_SEARCH_KEY, value.intValue());
		}
		value = content.get(PrefConst.KEY_WIDGET_SEARCH_KEY);
		if (null == value) {
			preferences.putInt(PrefConst.KEY_WIDGET_SEARCH_KEY, 0);
		} else {
			preferences.putInt(PrefConst.KEY_WIDGET_SEARCH_KEY, value.intValue());
		}
		// value = content.get("gowidget_search_key");
		// if (null == value)
		// {
		// editor.putInt("gowidget_search_key", 0);
		// }
		// else
		// {
		// editor.putInt("gowidget_search_key", value.intValue());
		// }
		preferences.commit();
	}

	// MenuKey_Long
	// SearchKey
	public static void initStatistic(Context context, Map<String, Integer> content) {
		PrivatePreference preferences = PrivatePreference.getPreference(context);

		int value = preferences.getInt(PrefConst.KEY_LONG_MENU, 0);
		content.put(PrefConst.KEY_LONG_MENU, Integer.valueOf(value));
		value = preferences.getInt(PrefConst.KEY_SEARCH_KEY, 0);
		content.put(PrefConst.KEY_SEARCH_KEY, Integer.valueOf(value));
		value = preferences.getInt(PrefConst.KEY_WIDGET_SEARCH_KEY, 0);
		content.put(PrefConst.KEY_WIDGET_SEARCH_KEY, Integer.valueOf(value));
		// value = preferences.getInt("gowidget_search_key", 0);
		// content.put("gowidget_search_key", Integer.valueOf(value));
	}
}
