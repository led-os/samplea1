package com.jiubang.ggheart.apps.desks.imagepreview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeConstants;

/**
 * 
 * <br>类描述: 用于监听主题包的安装及卸载
 * <br>功能详细描述:
 * 
 * @author  lichong
 * @date  [2014年1月24日]
 */
public class GoThemeInstalledReceiver extends BroadcastReceiver {

	private IReloadViewData mIReloadViewData;

	public GoThemeInstalledReceiver(IReloadViewData iReloadViewData) {
		mIReloadViewData = iReloadViewData;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())
				|| Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())
				|| Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
			String packageName = intent.getData().getSchemeSpecificPart();
			if (packageName.contains(ThemeConstants.LAUNCHER_THEME_PREFIX)
					|| packageName.contains(ThemeConstants.LAUNCHER_BIG_THEME_PREFIX)) {
				if (mIReloadViewData != null) {
					mIReloadViewData.reloadViewData(packageName);
				}
			}
		}
	}

	/**
	 * 
	 * <br>类描述: 重新加载数据，刷新视图
	 * <br>功能详细描述:
	 * 
	 * @author  lichong
	 * @date  [2014年1月24日]
	 */
	public static interface IReloadViewData {
		public void reloadViewData(String themePkgName);
	}
}

