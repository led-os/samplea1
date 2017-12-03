package com.jiubang.ggheart.apps.desks.diy.frames.screen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.util.device.Machine;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.diy.OutOfMemoryHandler;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.IItemType;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.data.info.UserFolderInfo;
import com.jiubang.ggheart.data.statistics.StaticScreenSettingInfo;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 默认桌面控制器
 * @author yejijiong
 *
 */
public class DefaultWorkspaceControler {
	
	public static final int ICON_TYPE_CREATE_ITEM_TO_DESK = 0;
	public static final int ICON_TYPE_ADD_GO_FOLDER = 1;
	
	public static final String RECOMMAND_APP_FTP_URL_KEY = "recommand_app_ftp_url_key";
	private static final String GOTHEMEPKGNAME = "com.gau.diy.gotheme";
	private Context mContext;
	/**
	 * 用户常用应用的队列(目前只初始化十个)
	 */
	private ArrayList<AppItemInfo> mDefaultInitAppList = null;
	private ArrayList<AppItemInfo> mAllAppItemList = null;
	
	public DefaultWorkspaceControler(Context context) {
		mContext = context;
	}
	
	/**
	 * 根据行列位置以及当前该位置占用情况获取候补图标
	 * @param infos
	 * @param column
	 * @param row
	 * @param screen
	 * @return Object[]: 下标0表示返回值的类型
	 */
	public Object[] getDefaultDesktopAlternateIcon(ArrayList<ShortCutInfo> infos, int column, int row, int screen) {
		// 韩国地区或者非200渠道，保持原先的逻辑
		if (Machine.isKorea(mContext) || !"200".equals(GoStorePhoneStateUtil.getUid(mContext))) {
		if (!checkHasAddRecommendApp(infos, column, row, screen)) {
			int rowCount = StaticScreenSettingInfo.sScreenRow - 1; // 最后一行
			Object[] objs = new Object[5];
			boolean hasAlternateIcon = false;
			switch(column) {
				case 0 :
					if (row == rowCount - 1) { // 倒数第2行第1个位置
							Intent goHandBookIntent = new Intent(
									ICustomAction.ACTION_SHOW_GO_HANDBOOK);
						Drawable goHandBookIcon = mContext.getResources().getDrawable(
								R.drawable.go_handbook_icon);
						objs[0] = ICON_TYPE_CREATE_ITEM_TO_DESK;
						objs[1] = goHandBookIntent;
						objs[2] = IItemType.ITEM_TYPE_SHORTCUT;
						objs[3] = R.string.go_handbook_title;
						objs[4] = goHandBookIcon;
						hasAlternateIcon = true;
					} else if (row == rowCount) { // 最后1行第1个位置
						final UserFolderInfo userFolderInfo = new UserFolderInfo();
						userFolderInfo.mInScreenId = System.currentTimeMillis();
						userFolderInfo.mCellX = column;
						userFolderInfo.mCellY = rowCount;
						userFolderInfo.setFeatureTitle(mContext.getResources().getString(
								R.string.go_apps));
						objs[0] = ICON_TYPE_ADD_GO_FOLDER;
						objs[1] = userFolderInfo;
						hasAlternateIcon = true;
					}
					break;
				case 1 :
					if (row == rowCount) { // 最后1行第2个位置
						// 添加GO锁屏到桌面 add by Ryan 2012.10.11
						Intent golockerIntent = null;
						int itemType = -1;
						Drawable lockerIcon = null;
						if (GoAppUtils.isGoLockerExist(mContext)) {
							itemType = IItemType.ITEM_TYPE_APPLICATION;
							PackageManager pm = mContext.getPackageManager();
								golockerIntent = pm
										.getLaunchIntentForPackage(PackageName.RECOMMAND_GOLOCKER_PACKAGE);
							
							//几个版本的GO锁屏安装包,包名不同
							if (golockerIntent == null) {
								try {
									Intent queryIntent = new Intent(ICustomAction.ACTION_LOCKER);
									List<ResolveInfo> infosList = null;
										infosList = mContext.getPackageManager()
												.queryIntentActivities(queryIntent, 0);
									if (infosList != null && infosList.size() > 0) {
										ResolveInfo resolveInfo = infosList.get(0);
										String pkg = resolveInfo.activityInfo.packageName;
										golockerIntent = pm.getLaunchIntentForPackage(pkg);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								itemType = IItemType.ITEM_TYPE_SHORTCUT;
								golockerIntent = new Intent(
										ICustomAction.ACTION_RECOMMAND_GOLOCKER_DOWNLOAD);
								ComponentName cmpName = new ComponentName(
										PackageName.RECOMMAND_GOLOCKER_PACKAGE,
										PackageName.RECOMMAND_GOLOCKER_PACKAGE);
								golockerIntent.setComponent(cmpName);
								golockerIntent.putExtra(RECOMMAND_APP_FTP_URL_KEY,
										LauncherEnv.Url.GOLOCKER_FTP_URL);
								try {
									// 加download Tag
									lockerIcon = ScreenUtils.getGoAppsIcons(mContext,
											R.drawable.screen_edit_golocker);
								} catch (OutOfMemoryError e) {
									OutOfMemoryHandler.handle();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							objs[0] = ICON_TYPE_CREATE_ITEM_TO_DESK;
							objs[1] = golockerIntent;
							objs[2] = itemType;
							objs[3] = R.string.customname_golocker;
							objs[4] = lockerIcon;
							hasAlternateIcon = true;
						}
					break;
				case 2 :
					if (row == rowCount) { // 最后1行第3个位置
//						if (Machine.getAdType(mContext) == 2) {
//							break;
//						}
						Intent intent = null;
						ComponentName component = null;
						int itemType = -1; // 图标类型
						int titleResId = -1; // 标题resId
						Drawable icon = null; // 图标icon
						// 应用中心
							intent = new Intent(ICustomAction.ACTION_FUNC_SHOW_RECOMMENDCENTER);
							component = new ComponentName(
							        PackageName.RECOMMAND_CENTER_PACKAGE_NAME,
									ICustomAction.ACTION_FUNC_SHOW_RECOMMENDCENTER);
							intent.setComponent(component);
							itemType = IItemType.ITEM_TYPE_APPLICATION;
							objs[0] = ICON_TYPE_CREATE_ITEM_TO_DESK;
							objs[1] = intent;
							objs[2] = itemType;
							objs[3] = titleResId;
							objs[4] = icon;
							hasAlternateIcon = true;
						}
						break;
					case 3 :
						if (row == rowCount - 1) { // 倒数第2行第4个位置
							if (Machine.isKorea(mContext)) { // 韩国补限免主题
								// 限免主题
								Intent freeThemeIntent = new Intent(
										ICustomAction.ACTION_FREE_THEME_ICON);
								ComponentName component = new ComponentName(
								        PackageName.FREE_THEME_PACKAGE_NAME,
										ICustomAction.ACTION_FREE_THEME_ICON);
								freeThemeIntent.setComponent(component);
								freeThemeIntent.setData(Uri.parse("package:"
										+ PackageName.FREE_THEME_PACKAGE_NAME));

								objs[0] = ICON_TYPE_CREATE_ITEM_TO_DESK;
							objs[1] = freeThemeIntent;
							objs[2] = IItemType.ITEM_TYPE_APPLICATION;
							objs[3] = -1;
							objs[4] = null;
							hasAlternateIcon = true;
						}
					} else if (row == rowCount) { // 最后1行第4个位置
							Intent goThemeIntent = new Intent(
									ICustomAction.ACTION_FUNC_SPECIAL_APP_GOTHEME);
						ComponentName goThemeCom = new ComponentName(GOTHEMEPKGNAME,
								ICustomAction.ACTION_FUNC_SPECIAL_APP_GOTHEME);
						goThemeIntent.setComponent(goThemeCom);
						goThemeIntent.setData(Uri.parse("package:" + GOTHEMEPKGNAME));
						objs[0] = ICON_TYPE_CREATE_ITEM_TO_DESK;
						objs[1] = goThemeIntent;
						objs[2] = IItemType.ITEM_TYPE_APPLICATION;
						objs[3] = -1;
						objs[4] = null;
						hasAlternateIcon = true;
					}
					break;
			}
			if (!hasAlternateIcon) { // 补齐空位
				getDefaultInitApp(objs);
			}
			return objs;
		} else {
			return null;
		}
			// 非韩国地区200渠道
		} else {
			if (!checkHasAddRecommendApp(infos, column, row, screen)) {
				int rowCount = StaticScreenSettingInfo.sScreenRow;
				int endRowIndex = rowCount - 1;
				Object[] objs = new Object[5];
				boolean hasAlternateIcon = false;
				// 对于屏幕默认为4行的手机：广告图标只在屏幕倒数第一行显示
				// 对于屏幕默认为5行的手机：广告图标在屏幕倒数第一第二行显示
				if (rowCount == 4 && row < endRowIndex) {
					objs = null;
					hasAlternateIcon = true;
					// 最后一排第三个
				} else if (row == endRowIndex && column == 2) {
					Intent goThemeIntent = new Intent(ICustomAction.ACTION_FUNC_SPECIAL_APP_GOTHEME);
					ComponentName goThemeCom = new ComponentName(GOTHEMEPKGNAME,
							ICustomAction.ACTION_FUNC_SPECIAL_APP_GOTHEME);
					goThemeIntent.setComponent(goThemeCom);
					goThemeIntent.setData(Uri.parse("package:" + GOTHEMEPKGNAME));
					objs[0] = ICON_TYPE_CREATE_ITEM_TO_DESK;
					objs[1] = goThemeIntent;
					objs[2] = IItemType.ITEM_TYPE_APPLICATION;
					objs[3] = -1;
					objs[4] = null;
					hasAlternateIcon = true;
	}
				if (!hasAlternateIcon) {
					getDefaultInitApp(objs);
				}
				return objs;
			} else {
				return null;
			}
		}
	}
	
	/**
	 * <br>功能简述:检查屏幕某个位置是否已被推荐图标占据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param recommedApps
	 * @param cellx
	 * @param celly
	 * @return
	 */
	boolean checkHasAddRecommendApp(ArrayList<ShortCutInfo> recommedApps, int cellx, int celly,
			int screen) {
		try {
			if (recommedApps != null && !recommedApps.isEmpty()) {
				for (ShortCutInfo info : recommedApps) {
					if (info.mCellX == cellx && info.mCellY == celly && info.mScreenIndex == screen) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 在指定位置添加用户常用图标（用于补空位）
	 * <li>优先取常用应用，常用应用不够，取应用列表中的其它应用</li>
	 */
	private Object[] getDefaultInitApp(Object[] objs) {
		// 这里对最后两行前五个位置空位补上常用图标
		if (mDefaultInitAppList == null) { // 构造出用户常用的应用
			String[] packageName = ScreenUtils.getDefaultInitAppPkg();
			final AppDataEngine dataEngine = AppDataEngine.getInstance(ApplicationProxy.getContext());
			ArrayList<AppItemInfo> dbItemInfos = dataEngine.getAllAppItemInfos();
			int max = 10; // 最多需要10个常用程序
			mDefaultInitAppList = new ArrayList<AppItemInfo>();
			for (int i = 0; i < packageName.length; i++) {
				if (mDefaultInitAppList.size() > max) {
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
		}
		if (mAllAppItemList == null) {
			String[] packageNames = ScreenUtils.getDefaultInitAppPkg();
			final AppDataEngine dataEngine = AppDataEngine.getInstance(mContext);
			mAllAppItemList = (ArrayList<AppItemInfo>) dataEngine.getAllAppItemInfos().clone();
			ArrayList<AppItemInfo> infoRemoved = new ArrayList<AppItemInfo>();
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
		}
		Iterator<AppItemInfo> iterator = mDefaultInitAppList.iterator();
		Iterator<AppItemInfo> iteratorAllApp = mAllAppItemList.iterator();
		if (iterator.hasNext()) {
			AppItemInfo dbItemInfo = iterator.next();
			try {
				objs[0] = ICON_TYPE_CREATE_ITEM_TO_DESK;
				objs[1] = dbItemInfo.mIntent;
				objs[2] = dbItemInfo.mItemType;
				objs[3] = -1;
				objs[4] = null;
				ScreenUtils.sScreenInitedDefaultAppCount++;
			} catch (Exception e) {
			}
			iterator.remove();
			return objs;
		} else if (iteratorAllApp.hasNext()) {
			AppItemInfo dbItemInfo = iteratorAllApp.next();
			try {
				objs[0] = ICON_TYPE_CREATE_ITEM_TO_DESK;
				objs[1] = dbItemInfo.mIntent;
				objs[2] = dbItemInfo.mItemType;
				objs[3] = -1;
				objs[4] = null;
				ScreenUtils.sScreenInitedDefaultAppCountAppFunc++;
			} catch (Exception e) {
		}
			iteratorAllApp.remove();
			return objs;
		}
		return null;
	}
}
