package com.jiubang.ggheart.plugin;

import android.content.Context;

import com.go.proxy.ApplicationProxy;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;

/**
 * GGMenu代理类
 * @author yejijiong
 *
 */
public class GGMenuPoxy implements IGGMenuPoxy {

	@Override
	public boolean getIsNeedToShowMoreTipPoint() {
		PreferencesManager manager = new PreferencesManager(ApplicationProxy.getContext(),
				IPreferencesIds.FEATUREDTHEME_CONFIG, Context.MODE_PRIVATE);
		boolean needToShowPoint = manager.getBoolean(IPreferencesIds.NEED_TO_SHOW_MORE_TIP_POINT, false);
		return needToShowPoint;
	}

	@Override
	public boolean getIsNeedRemoveMoreTipPoint() {
		return true;
	}

}
