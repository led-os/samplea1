package com.jiubang.ggheart.apps.systemwidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.CellUtils;
import com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.ScreenEditConstants;
import com.jiubang.ggheart.apps.gowidget.ScreenEditItemInfo;

/**
 * 
 * @author zouguiquan
 *
 */
public class SystemWidgetLoader {

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static ArrayList<Object> getSortedWidgetsAndShortcuts(Context context) {

		PackageManager packageManager = context.getPackageManager();
		final ArrayList<Object> widgetsAndShortcuts = new ArrayList<Object>();
		widgetsAndShortcuts.addAll(AppWidgetManager.getInstance(context).getInstalledProviders());

		Intent shortcutsIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
		widgetsAndShortcuts.addAll(packageManager.queryIntentActivities(shortcutsIntent, 0));

		return widgetsAndShortcuts;
	}

	public static ArrayList<Object> getSysWidgetList(ArrayList<Object> list, Context context) {

		if (list == null || list.size() <= 0) {
			return null;
		}

		PackageManager pkManager = context.getPackageManager();
		ArrayList<Object> result = new ArrayList<Object>();
		HashSet<String> cache = new HashSet<String>();
		String pkgName = null;
		String title = null;

		for (Object obj : list) {
			pkgName = (obj instanceof AppWidgetProviderInfo)
					? ((AppWidgetProviderInfo) obj).provider.getPackageName()
					: ((ResolveInfo) obj).activityInfo.packageName;

			if (!cache.contains(pkgName)) {
				cache.add(pkgName);

				ScreenEditItemInfo info = new ScreenEditItemInfo();

				title = (obj instanceof AppWidgetProviderInfo)
						? ((AppWidgetProviderInfo) obj).label
						: ((ResolveInfo) obj).loadLabel(pkManager).toString().trim();

				info.setTitle(title);
				info.setPkgName(pkgName);

				result.add(info);
			}
		}
		result.add(createSearchWidget(context));

		if (result.size() > 0) {
			Collections.sort(result, new WidgetAndShortcutNameComparator());
		}

		return result;
	}

	public static ArrayList<Object> getSysWidgetSubList(ArrayList<Object> list, Context context,
			String pkgName, int cellRealWidth, int cellRealHeight) {

		if (list == null || list.size() <= 0) {
			return null;
		}

		ArrayList<Object> result = new ArrayList<Object>();
		Resources resources = context.getResources();
		String packageName = null;

		for (Object obj : list) {
			packageName = (obj instanceof AppWidgetProviderInfo)
					? ((AppWidgetProviderInfo) obj).provider.getPackageName()
					: ((ResolveInfo) obj).activityInfo.packageName;

			if (packageName != null && packageName.equals(pkgName)) {

				if (obj instanceof AppWidgetProviderInfo) {

					AppWidgetProviderInfo info = (AppWidgetProviderInfo) obj;
					int rect[] = rectToCell(resources, info.minWidth, info.minHeight,
							cellRealWidth, cellRealHeight);

					SysSubWidgetInfo providerInfo = new SysSubWidgetInfo();
					providerInfo.setProviderInfo(info);
					providerInfo.setCellWidth(rect[0]);
					providerInfo.setCellHeight(rect[1]);
					result.add(providerInfo);

				} else {
					result.add(obj);
				}
			}
		}

		return result;
	}

	/**
	 * 获取系统Widget的第一张预览图
	 * @param context
	 * @param pkgName	widget包名
	 * @return
	 */
	public static Drawable getFirstPreviewByPkg(Context context, String pkgName) {
		Drawable drawable = null;

		AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
		List<AppWidgetProviderInfo> providerlist = widgetManager.getInstalledProviders();
		if (providerlist != null && providerlist.size() > 0) {

			for (AppWidgetProviderInfo item : providerlist) {
				String widgetPkg = item.provider.getPackageName();
				if (widgetPkg != null && widgetPkg.equals(pkgName)) {
					drawable = getWidgetPreviewById(context, pkgName, item.previewImage);
					break;
				}
			}
		}
		return drawable;
	}

	public static boolean isWidgetApp(Context context, String pkgName) {

		AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
		List<AppWidgetProviderInfo> providerlist = widgetManager.getInstalledProviders();

		if (providerlist != null && providerlist.size() > 0) {

			for (AppWidgetProviderInfo item : providerlist) {

				String widgetPkg = item.provider.getPackageName();
				if (widgetPkg != null && widgetPkg.equals(pkgName)) {
					return true;
				}
			}
		}

		return false;
	}

	public static Drawable getSysWidgetSubIcon(Context context, String widgetPkg, int iconId) {
		Drawable drawable = null;
		PackageManager packageManager = context.getPackageManager();
		try {
			Resources resources = packageManager.getResourcesForApplication(widgetPkg);
			drawable = resources.getDrawable(iconId);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return drawable;
	}

	public static Drawable getSysWidgetIcon(Context context, String widgetPkg) {
		Drawable drawable = null;

		if (widgetPkg.equals(ScreenEditConstants.SEARCH_WIDGET_PACKAGE)) {
			drawable = context.getResources().getDrawable(R.drawable.ic_search_widget);
		} else {
			try {
				PackageManager packageManager = context.getPackageManager();
				PackageInfo packageInfo = packageManager.getPackageInfo(widgetPkg,
						PackageManager.GET_ACTIVITIES);
				drawable = packageInfo.applicationInfo.loadIcon(packageManager);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}

		return drawable;
	}

	private static ScreenEditItemInfo createSearchWidget(Context context) {
		ScreenEditItemInfo searchInfo = new ScreenEditItemInfo();
		searchInfo.setPkgName(ScreenEditConstants.SEARCH_WIDGET_PACKAGE);
		searchInfo.setTitle(context.getResources().getString(R.string.group_search));

		SysSubWidgetInfo widgetInfo = new SysSubWidgetInfo();
		AppWidgetProviderInfo providerInfo = new AppWidgetProviderInfo();
		providerInfo.provider = new ComponentName(searchInfo.getPkgName(), "XXX.YYY");
		providerInfo.label = context.getResources().getString(R.string.group_search);
		widgetInfo.setProviderInfo(providerInfo);
		widgetInfo.setCustomerInfo(ScreenEditConstants.SEARCH_WIDGET_TAG);
		widgetInfo.setCellWidth(4);
		widgetInfo.setCellHeight(1);

		return searchInfo;
	}

	/**
	 * 
	 * @param context
	 * @param packageName
	 * @param previewId
	 * @return
	 */
	public static Drawable getWidgetPreviewById(Context context, String packageName, int previewId) {

		Drawable preview = null;
		int sdkVersion = getAndroidSDKVersion();
		PackageManager packageManager = context.getPackageManager();
		if (sdkVersion >= 11) {
			preview = packageManager.getDrawable(packageName, previewId, null);
		}
		return preview;
	}

	private static int getAndroidSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		} catch (NumberFormatException e) {
		}
		return version;
	}

	private static int[] rectToCell(Resources resources, int width, int height, int sColumns,
			int sRows) {

		int actualWidth = 0;
		int actualHeight = 0;

		actualWidth = resources.getDimensionPixelSize(CellUtils.sCellWidthPortRes);
		actualHeight = resources.getDimensionPixelSize(CellUtils.sCellHeightPortRes);

		final int smallerSize = Math.min(actualWidth, actualHeight);
		int spanX = (width + smallerSize) / smallerSize;
		int spanY = (height + smallerSize) / smallerSize;

		spanX = Math.max(1, Math.min(spanX, sColumns));
		spanY = Math.max(1, Math.min(spanY, sRows));
		return new int[] { spanX, spanY };
	}
}
