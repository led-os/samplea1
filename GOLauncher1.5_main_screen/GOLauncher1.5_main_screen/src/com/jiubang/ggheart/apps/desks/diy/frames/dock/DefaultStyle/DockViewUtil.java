package com.jiubang.ggheart.apps.desks.diy.frames.dock.DefaultStyle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.go.proxy.ApplicationProxy;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.ScreenUtils;
import com.jiubang.ggheart.apps.desks.dock.DockUtil;
import com.jiubang.ggheart.apps.desks.imagepreview.ImagePreviewResultType;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.DockItemInfo;
import com.jiubang.ggheart.data.info.IItemType;
import com.jiubang.ggheart.data.info.ShortCutInfo;

/**
 * 
 * <br>类描述:dockview内部工具类
 * <br>功能详细描述:
 * 
 * @author  ruxueqin
 * @date  [2012-9-20]
 */
public class DockViewUtil {

	/**
	 * 获取15个初始化系统程序，可能找到不足15个,不足的部分，用功能表中的应用填补
	 * 外部用完负责释放ArrayList<DockItemInfo>
	 * 
	 * @param engine
	 * @return
	 */
	public static ArrayList<DockItemInfo> getInitDockData() {
		ArrayList<DockItemInfo> list = new ArrayList<DockItemInfo>();
		// 获取常用应用
		ArrayList<AppItemInfo> mDefaultInitAppList = null;
		ArrayList<AppItemInfo> dbItemInfos = null;
		try {
			String[] packageName = ScreenUtils.getDefaultInitAppPkg();
			final AppDataEngine dataEngine = AppDataEngine.getInstance(ApplicationProxy.getContext());
			dbItemInfos = dataEngine.getAllAppItemInfos();
			mDefaultInitAppList = new ArrayList<AppItemInfo>();
			for (int i = 0; i < packageName.length; i++) {
				if (mDefaultInitAppList.size() > DockUtil.DOCK_COUNT) {
					break;
					}
				for (AppItemInfo dbItemInfo : dbItemInfos) {
					if (null != dbItemInfo.mIntent.getComponent()) {
					String dbPackageName = dbItemInfo.mIntent.getComponent().getPackageName();
						if (dbPackageName.equals(packageName[i])) {
							mDefaultInitAppList.add(dbItemInfo);
							break;
						}
					}
				}
			}
			final int size = mDefaultInitAppList.size();
			if (size > ScreenUtils.sScreenInitedDefaultAppCount) {
				for (int i = ScreenUtils.sScreenInitedDefaultAppCount; i < size; i++) {
					AppItemInfo dbItemInfo = mDefaultInitAppList.get(i);
					DockItemInfo dockItemInfo = new DockItemInfo(IItemType.ITEM_TYPE_APPLICATION,
							DockUtil.ICON_COUNT_IN_A_ROW);
							ShortCutInfo shortCutInfo = (ShortCutInfo) dockItemInfo.mItemInfo;
							shortCutInfo.mFeatureIconType = ImagePreviewResultType.TYPE_DEFAULT;
							shortCutInfo.mFeatureTitle = dbItemInfo.getTitle();
							shortCutInfo.mIntent = dbItemInfo.mIntent;

							list.add(dockItemInfo);
						}
					}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mDefaultInitAppList != null) {
				mDefaultInitAppList.clear();
				mDefaultInitAppList = null;
				}
			if (dbItemInfos != null) {
				dbItemInfos.clear();
				dbItemInfos = null;
			}
		}

		//不足15个，用功能表应用填补空缺
		if (list.size() < DockUtil.DOCK_COUNT) {
			ArrayList<AppItemInfo> mAllAppItemList = null;
			List<AppItemInfo> infoRemoved = new ArrayList<AppItemInfo>();
			try {
				String[] packageNames = ScreenUtils.getDefaultInitAppPkg();
				final AppDataEngine dataEngine = AppDataEngine.getInstance(ApplicationProxy.getContext());
				mAllAppItemList = (ArrayList<AppItemInfo>) dataEngine.getAllAppItemInfos().clone();
				// 去重
				for (AppItemInfo info : mAllAppItemList) {
					for (String pkgName : packageNames) {
						boolean needRemoved = false;
						if (info.mIntent.getComponent() == null) {
							needRemoved = true;
						} else {
							String funcAppPkgName = info.mIntent.getComponent().getPackageName();
							// 排除常用应用
							if (funcAppPkgName.equals(pkgName)) {
								needRemoved = true;
							} else {
								//dock条固定四个快捷方式去重
								for (String protogenicAppPkgName : ScreenUtils.PROTOGENIC_APP_PKGS) {
									if (protogenicAppPkgName.equals(pkgName)) {
										needRemoved = true;
					break;
				}
			}
							}
						}
						if (needRemoved) {
							infoRemoved.add(info);
						}
					}
				}
				mAllAppItemList.removeAll(infoRemoved);
				// 排除已经添加到屏幕广告页中的应用
				infoRemoved.clear();
				int removeCount = ScreenUtils.sScreenInitedDefaultAppCountAppFunc;
				if (mAllAppItemList.size() <= removeCount) {
					mAllAppItemList.clear();
				} else {
					int count = 0;
					while (count++ < removeCount) {
						mAllAppItemList.remove(0);
					}
				}
				Iterator<AppItemInfo> iteratorAllApp = mAllAppItemList.iterator();
				while (iteratorAllApp.hasNext() && list.size() < DockUtil.DOCK_COUNT) {
					AppItemInfo dbItemInfo = iteratorAllApp.next();
					DockItemInfo dockItemInfo = new DockItemInfo(IItemType.ITEM_TYPE_APPLICATION,
							DockUtil.ICON_COUNT_IN_A_ROW);
					ShortCutInfo shortCutInfo = (ShortCutInfo) dockItemInfo.mItemInfo;
					shortCutInfo.mFeatureIconType = ImagePreviewResultType.TYPE_DEFAULT;
					shortCutInfo.mFeatureTitle = dbItemInfo.getTitle();
					shortCutInfo.mIntent = dbItemInfo.mIntent;

					list.add(dockItemInfo);
					iteratorAllApp.remove();
				}
		} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (infoRemoved != null) {
					infoRemoved.clear();
					infoRemoved = null;
		}
				if (mAllAppItemList != null) {
					mAllAppItemList.clear();
					mAllAppItemList = null;
				}
			}
		}
		return list;
	}
}
