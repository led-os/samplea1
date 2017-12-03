package com.go.launcher.taskmanager;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ComponentName;

import com.go.proxy.ApplicationProxy;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.info.AppItemInfo;

class TaskManagerImpl extends AbstractTaskManager {

	/**
	 * 获得手机中已经安装的程序信息
	 */
	@Override
	protected void loadAppTable() {
		_appInforMap.clear();
		final ConcurrentHashMap<ComponentName, AppItemInfo> allAppInfo = AppDataEngine.getInstance(ApplicationProxy.getContext()).getAllAppHashMap();
		if (allAppInfo != null) {
			Iterator<Entry<ComponentName, AppItemInfo>> it = allAppInfo.entrySet().iterator();
			while (it.hasNext()) {
				Entry<ComponentName, AppItemInfo> entry = it.next();
				if (entry != null) {
					AppItemInfo itemInfo = entry.getValue();
					if (itemInfo != null) {
						_appInforMap.put(itemInfo.mProcessName, itemInfo.mIntent);
					}
				}
			}
		}
	}
}
