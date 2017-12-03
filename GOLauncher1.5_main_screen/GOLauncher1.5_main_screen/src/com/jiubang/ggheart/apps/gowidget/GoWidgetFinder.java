package com.jiubang.ggheart.apps.gowidget;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.util.Log;

import com.go.util.AppUtils;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.PackageName;
/**
 * 
 * @author dengdazhong
 *
 */
public class GoWidgetFinder {

	// 以主题包名为key，主题包bean信息为value
	private HashMap<String, GoWidgetProviderInfo> mAllGoWidgetInfosMap = null; // 所有主题信息

	private Context mContext;

//	public static String MAIN_GOWIDGET_PACKAGE = "com.gau.go.launcherex.gowidget"; // GoWidget包名
	public final static String GOWIDGET_CATEGORY = "android.intent.category.DEFAULT"; // GoWidget包category

	public GoWidgetFinder(Context context) {
		this.mContext = context;
	}

	public void scanAllInstalledGoWidget() {
		if (mAllGoWidgetInfosMap != null) {
			mAllGoWidgetInfosMap.clear();
		} else {
			mAllGoWidgetInfosMap = new HashMap<String, GoWidgetProviderInfo>();
		}
		// 扫gowidget
		HashMap<String, GoWidgetProviderInfo> tmpHash;
		tmpHash = scanGoWidget();
		if (tmpHash != null) {
			mAllGoWidgetInfosMap.putAll(tmpHash);
		}

		// 扫3Dwidget
		tmpHash = scanNextWidget();
		if (tmpHash != null) {
			mAllGoWidgetInfosMap.putAll(tmpHash);
		}
		// 扫2D转3Dwidget
		tmpHash = scanGO3DWidget();
		if (tmpHash != null) {
			mAllGoWidgetInfosMap.putAll(tmpHash);
		}
	}

	/**
	 * 扫2Dwidget
	 * 
	 * @return
	 */
	public HashMap<String, GoWidgetProviderInfo> scanGoWidget() {
		return scanWidgetCommon(ICustomAction.ACTION_MAIN_GOWIDGET_PACKAGE,
				GOWIDGET_CATEGORY, false);
	}

	/**
	 * 扫2D转3Dwidget
	 * 
	 * @return
	 */
	public HashMap<String, GoWidgetProviderInfo> scanGO3DWidget() {
		return scanWidgetCommon(GoWidgetConstant.GOFOR3DWIDGETACTION, null, true);
	}
	
	/**
	 * 扫3Dwidget
	 * 
	 * @return
	 */
	public HashMap<String, GoWidgetProviderInfo> scanNextWidget() {
		return scanWidgetCommon(GoWidgetConstant.NEXTWIDGETACTION, null, true);
	}

	public HashMap<String, GoWidgetProviderInfo> scanWidgetCommon(
			String action, String category, boolean needClassName) {
		HashMap<String, GoWidgetProviderInfo> widgetHash = null;
		Intent intent = new Intent(action);
		if (category != null) {
			intent.addCategory(category);
		}
		PackageManager pm = mContext.getPackageManager();

		List<ResolveInfo> widgets = pm.queryIntentActivities(intent, 0);

		// for gosms
		if (GoAppUtils.isAppExist(mContext, PackageName.GOSMS_PACKAGE)) {
			int versionCode = AppUtils.getVersionCodeByPkgName(mContext, PackageName.GOSMS_PACKAGE);
			if (versionCode >= 80
					&& GoAppUtils.isAppExist(mContext, "com.gau.go.launcherex.gowidget.smswidget")) {
				// gosms 的versionCode大于80才支持
				for (int i = 0; i < widgets.size(); i++) {
					if (widgets.get(i).activityInfo.packageName
							.equals("com.gau.go.launcherex.gowidget.smswidget")) {
						widgets.remove(i);
					}
				}
			}
		}

		int size = widgets.size();
		if (size > 0) {
			widgetHash = new HashMap<String, GoWidgetProviderInfo>();
		}
		
		for (int i = 0; i < size; i++) {
			
			final String appPackageName = widgets.get(i).activityInfo.packageName;
			String className = needClassName ? widgets.get(i).activityInfo.name : "";
			
			GoWidgetProviderInfo info = new GoWidgetProviderInfo(appPackageName, className);
			if (appPackageName != null) {
				Resources resources = getGoWidgetResources(appPackageName);
				if (resources != null) {
					try {
						// 查找Widget的名字
						int identifier = resources.getIdentifier(GoWidgetConstant.WIDGET_TITLE,
								"string", appPackageName);
						if (identifier != 0) {
							info.mProvider.label = resources.getString(identifier);
						}

						// 查找Widget的图标
						identifier = resources.getIdentifier(GoWidgetConstant.WIDGET_ICON,
								"string", appPackageName);

						if (identifier > 0) {
							String icon = resources.getString(identifier);
							info.mProvider.icon = resources.getIdentifier(icon, "drawable",
									appPackageName);
						}

						if (info.mProvider.icon > 0 && info.mProvider.label != null) {
							widgetHash.put(appPackageName, info);
						}
					} catch (Exception e) {
						Log.e("gowidget", "parse widget info error, pkg = " + appPackageName);
					}
				}
			}
		}
		return widgetHash;
	}
	
	public HashMap<String, GoWidgetProviderInfo> getGoWidgetInfosMap() {
		return mAllGoWidgetInfosMap;
	}

	public Resources getGoWidgetResources(String themePackage) {
		if (themePackage == null) {
			return null;
		}

		Resources resources = null;
		try {
			resources = mContext.getPackageManager().getResourcesForApplication(themePackage);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return resources;
	}
}
