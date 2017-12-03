package com.jiubang.ggheart.tuiguanghuodong.double11;

import android.content.BroadcastReceiver;
import android.content.Context;

import com.jiubang.ggheart.tuiguanghuodong.double11.bean.ScreenIconBeanForEleven;

/**
 * 
 * @author zhujian
 * 
 */
public class ScreenIconForElevenController {
	private static ScreenIconForElevenController selfInstance;

	private Context mContext;
	private BroadcastReceiver mReceiver;

	private ScreenIconForElevenController(Context context) {
		mContext = context;
	}

	public static synchronized ScreenIconForElevenController getController(
			Context context) {
		if (selfInstance == null) {
			selfInstance = new ScreenIconForElevenController(context);
		}
		return selfInstance;
	}

	public ScreenIconBeanForEleven getBean() {
		return null;
	}

	public void setBeanNull() {
	}
}
