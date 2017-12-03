package com.jiubang.ggheart.apps.desks.diy.frames.screen.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.SettingProxy;
import com.go.util.AppUtils;
import com.go.util.ConvertUtils;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.diy.OutOfMemoryHandler;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.DefaultWorkspaceControler;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.IScreenObserver;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.ScreenUtils;
import com.jiubang.ggheart.apps.desks.diy.pref.PrefConst;
import com.jiubang.ggheart.apps.desks.diy.pref.PrivatePreference;
import com.jiubang.ggheart.apps.gowidget.GoWidgetManager;
import com.jiubang.ggheart.apps.gowidget.WidgetErrorView;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.components.advert.AdvertConstants;
import com.jiubang.ggheart.data.AppCore;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.DockItemControler;
import com.jiubang.ggheart.data.FavoriteProvider;
import com.jiubang.ggheart.data.SelfAppItemInfoControler;
import com.jiubang.ggheart.data.SysShortCutControler;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.DesktopSettingInfo;
import com.jiubang.ggheart.data.info.DockItemInfo;
import com.jiubang.ggheart.data.info.EffectSettingInfo;
import com.jiubang.ggheart.data.info.FavoriteInfo;
import com.jiubang.ggheart.data.info.FunAppItemInfo;
import com.jiubang.ggheart.data.info.GoWidgetBaseInfo;
import com.jiubang.ggheart.data.info.IItemType;
import com.jiubang.ggheart.data.info.ItemInfo;
import com.jiubang.ggheart.data.info.ScreenAppWidgetInfo;
import com.jiubang.ggheart.data.info.ScreenSettingInfo;
import com.jiubang.ggheart.data.info.SelfAppItemInfo;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.data.info.UserFolderInfo;
import com.jiubang.ggheart.data.model.GoWidgetDataModel;
import com.jiubang.ggheart.data.model.ScreenDataModel;
import com.jiubang.ggheart.data.statistics.StaticScreenSettingInfo;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherWidgetHostView;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.plugin.shell.folder.GLAppFolderController;
import com.jiubang.ggheart.plugin.shell.folder.GLAppFolderInfo;
import com.jiubang.ggheart.recommend.localxml.XmlRecommendedApp;
import com.jiubang.ggheart.recommend.localxml.XmlRecommendedAppInfo;

/**
 * 屏幕管理，负责屏幕操作与后台数据的管理
 * 
 * @author huyong
 * 
 */
public class ScreenControler {
	// 日志
	private static final String LOG_TAG = "ScreenControlLog";
	private ScreenDataModel mDataModel = null;

	private SparseArray<ArrayList<ItemInfo>> mScreenSparseArray = null;
	private FavoriteProvider mFavoriteProvider;

	private Context mContext;
	private ScreenSettingInfo mScreenSettingInfo;
	private DesktopSettingInfo mDesktopSettingInfo;
	private EffectSettingInfo mEffectSettingInfo;
	private IScreenObserver mScreenObserver;
	private ScreenAdvertBusiness mScreenAdvertBusiness;
	private static final String GOSTOREPKGNAME = "com.gau.diy.gostore";
	private static final String GOTHEMEPKGNAME = "com.gau.diy.gotheme";
	private static final String GOWIDGETPKGNAME = "com.gau.diy.gowidget";
	public static final String RECOMMAND_APP_FTP_URL_KEY = "recommand_app_ftp_url_key";

	public ScreenControler(final Context context, AppWidgetHost widgetHost, IScreenObserver observer) {
		mContext = context;
		mDataModel = new ScreenDataModel(context);
		mScreenSparseArray = new SparseArray<ArrayList<ItemInfo>>();
		mFavoriteProvider = new FavoriteProvider(context, widgetHost);
		mScreenAdvertBusiness = new ScreenAdvertBusiness(this);
		mScreenObserver = observer;
	}

	private void initFavorites() {
		ArrayList<ItemInfo> favList = mFavoriteProvider.loadFavorite();
		final AppDataEngine dataEngine = AppDataEngine.getInstance(ApplicationProxy.getContext());
		ArrayList<AppItemInfo> dbItemInfos = dataEngine.getAllAppItemInfos();
		if (favList != null) {
			int count = favList.size();
			GoWidgetManager widgetManager = AppCore.getInstance().getGoWidgetManager();
			
//			int adType = Machine.getAdType(mContext);
			for (int i = 0; i < count; i++) {
				final ItemInfo info = favList.get(i);
				if (info instanceof FavoriteInfo) {
					final FavoriteInfo favoriteInfo = (FavoriteInfo) info;
					GoWidgetBaseInfo widgetBaseInfo = favoriteInfo.mWidgetInfo;
					final String pkgName = widgetManager.getWidgetPackage(widgetBaseInfo);
					if (widgetBaseInfo != null && pkgName != null) {
						
						//广告测试
//						if (adType == 2) {
//							if ((favoriteInfo.mScreenIndex == 1 && pkgName.equals("com.jb.gosms"))
//									|| (favoriteInfo.mScreenIndex == 3 && (pkgName
//											.equals(LauncherEnv.Plugin.NEXT_BROWSER_PACKAGE_NAME)
//											|| pkgName
//													.equals("com.gau.go.launcherex.gowidget.gopowermaster") || (pkgName
//											.equals("com.gau.go.launcherex") && favoriteInfo.mWidgetInfo.mPrototype == GoWidgetBaseInfo.PROTOTYPE_APPGAME)))) {
//								continue;
//							}
//						}
						
						
						boolean isInstalled = GoAppUtils.isAppExist(mContext, pkgName);
						if (pkgName.equals(PackageName.GOSMS_PACKAGE)) {
							int versionCode = AppUtils.getVersionCodeByPkgName(mContext, pkgName);
							if (versionCode < 80) {
								isInstalled = false;
							}
						}
						
						// 对开关widget版本低于38的，进行特殊处理 add by Ryan 2013.07.13
						// 如果当前版本低于38,则显示为老样式
						if (isInstalled && pkgName.equals(PackageName.SWITCH_PACKAGE) && 
								AppUtils.getVersionCodeByPkgName(mContext, pkgName) < 38) {
							widgetBaseInfo.mLayout = "main";
							widgetBaseInfo.mType = 0;
						}
						// end
						
						if (isInstalled) {
							ScreenAppWidgetInfo appWidgetInfo = new ScreenAppWidgetInfo(
									widgetBaseInfo.mWidgetId, null, info);
							addDesktopItem(info.mScreenIndex, appWidgetInfo);
							// 添加信息到Gowidget表
							widgetManager.addGoWidget(widgetBaseInfo);
						}
						// 仅当有预览图和下载地址才添加到数据库中，方便用户点击
						// NOTE:注释这个条件：/* && favoriteInfo.mUrl !=
						// null*/，因为现在全跳gostore下载
						else if (favoriteInfo.mPreview > 0/* && favoriteInfo.mUrl != null*/) {
							addDesktopItem(info.mScreenIndex, info);
						}
					}
				} else if (info instanceof ScreenAppWidgetInfo) {
					addDesktopItem(info.mScreenIndex, info);
				} else if (info instanceof ShortCutInfo) {
					final ShortCutInfo shortCutInfo = (ShortCutInfo) info;
					final ComponentName cn = shortCutInfo.mIntent.getComponent();
					for (AppItemInfo appItemInfo : dbItemInfos) {
						final ComponentName appcn = appItemInfo.mIntent.getComponent();
						if (appcn.equals(cn)) {
							shortCutInfo.mInScreenId = System.currentTimeMillis();
							shortCutInfo.mIntent = appItemInfo.mIntent;
							shortCutInfo.mTitle = appItemInfo.mTitle;
							shortCutInfo.mIcon = appItemInfo.mIcon;
							addDesktopItem(shortCutInfo.mScreenIndex, info);
							break;
						}
					}
				} else if (info instanceof UserFolderInfo) {
					final UserFolderInfo folderInfo = (UserFolderInfo) info;
					addDesktopItem(folderInfo.mScreenIndex, folderInfo);
					int childCount = folderInfo.getChildCount();
					ArrayList<ItemInfo> existChild = new ArrayList<ItemInfo>();
					for (int j = 0; j < childCount; j++) {
						ItemInfo itemInfo = folderInfo.getChildInfo(j);
						if (itemInfo != null && itemInfo instanceof ShortCutInfo) {
							final ShortCutInfo shortCutInfo = (ShortCutInfo) itemInfo;
							final ComponentName cn = shortCutInfo.mIntent.getComponent();
							for (AppItemInfo appItemInfo : dbItemInfos) {
								ComponentName appcn = appItemInfo.mIntent.getComponent();
								if (appcn.equals(cn)) {
									shortCutInfo.mIntent = appItemInfo.mIntent;
									shortCutInfo.mTitle = appItemInfo.mTitle;
									shortCutInfo.mIcon = appItemInfo.mIcon;
									existChild.add(shortCutInfo);
									break;
								}
							}
						}
					}
					addUserFolderContent(folderInfo.mInScreenId, existChild, false);
				}
			}
		}
		mFavoriteProvider.clearFavorite();
	}

	public FavoriteInfo getFavoriteInfo(FavoriteInfo info) {
		if (info != null && info.mWidgetInfo != null) {
			final FavoriteInfo cachedInfo = mFavoriteProvider
					.getFavoriteInfo(info.mWidgetInfo.mWidgetId);
			if (cachedInfo != null) {
				// 补齐从partsToScreen表中缺少的信息
				info.mPreview = cachedInfo.mPreview;
				info.mUrl = cachedInfo.mUrl;
				info.mWidgetInfo = cachedInfo.mWidgetInfo;
				info.mTitleId = cachedInfo.mTitleId;
				info.mDetailId = cachedInfo.mDetailId;
				info.mAId = cachedInfo.mAId;
				info.mMapId = cachedInfo.mMapId;
				info.mClickUrl = cachedInfo.mClickUrl;
				info.mGALink = cachedInfo.mGALink;
			}
		}
		return info;
	}

	/**
	 * 是否存在初始化推荐widget，如果存在，则需要解析对应的xml文件
	 * 
	 * @return
	 */
	public boolean isExistFavorite() {
		if (mScreenSparseArray != null) {
			int size = mScreenSparseArray.size();
			for (int i = 0; i < size; i++) {
				ArrayList<ItemInfo> list = mScreenSparseArray.get(i);
				if (list != null) {
					int count = list.size();
					for (int j = 0; j < count; j++) {
						ItemInfo itemInfo = list.get(j);
						if (itemInfo != null && itemInfo.mItemType == IItemType.ITEM_TYPE_FAVORITE) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 加载屏幕 如果确认的、未确认的都为空 则重新获取
	 * 
	 * @return
	 */
	public synchronized SparseArray<ArrayList<ItemInfo>> loadScreen() {
		if (mScreenSparseArray.size() <= 0) {
			getScreenItems();
			// 如果存在初始化推荐widget，需要解析xml文件
			if (isExistFavorite()) {
				mFavoriteProvider.loadFavorite();
			}
		}
		
		if (mScreenSparseArray.size() <= 0) {
			// 获取数据出错，恢复数据
			Log.i(LOG_TAG, "loadScreen screen info failed!!");

			// 恢复数据
			mDataModel.cleanDesktopItem();
			initDesktopItem();

			// 再次获取
			getScreenItems();
			// Gowidget初始化列表
			initFavorites();
		}
		
		//add by dengdazhong
		//resolve ADT-7595 低配置手机，新安装后恢复默认桌面时，桌面中的指示器会显示为5，等待一段时间才变成3
		//如果低配需要减屏,就减掉前面和最后面的两个屏
		if (StaticScreenSettingInfo.sNeedDelScreen) {
			delScreen(0);
			delScreen(mScreenSparseArray.size() - 1);
			StaticScreenSettingInfo.sNeedDelScreen = false;
		}
		// 保存当前有多少屏，供挂掉的时候err report反馈
		int count = mScreenSparseArray.size();
		saveScreenCount(count);

		return mScreenSparseArray;
	}

	private void getScreenItems() {
		mDataModel.getScreenItems(mScreenSparseArray);
		for (int i = 0; i < mScreenSparseArray.size(); i++) {
			ArrayList<ItemInfo> itemInfoList = mScreenSparseArray.get(i);
			prepareItemInfo(itemInfoList);
		}
	}

	public void updateFolderIndex(long folderID, ArrayList<ItemInfo> infos) {
		mDataModel.updateFolderIndex(folderID, infos);
	}

	public void updateFolderItem(long folderID, ItemInfo info) {
		mDataModel.updateFolderItem(folderID, info);
		prepareItemInfo(info);
	}

	/***
	 ****************************** 屏幕操作*******************************************
	 */

	/**
	 * 移动屏幕位置
	 * 
	 * @author huyong
	 * @param srcScreenIndex
	 *            屏幕源索引
	 * @param DescScreenIndex
	 *            屏幕目标索引
	 */
	public synchronized boolean moveScreen(final int srcScreenIndex, final int descScreenIndex) {
		if (srcScreenIndex == descScreenIndex) {
			return true;
		}

		try {
			mDataModel.moveScreen(srcScreenIndex, descScreenIndex);

			// 修改缓存
			// 删除源，加入目标
			ArrayList<ItemInfo> data = delScreenToHashMap(srcScreenIndex);
			addScreenToHashMap(descScreenIndex, data);

			return true;
		} catch (Exception e) {
			Log.i(LOG_TAG, "keep data (move screen) exception");
			return false;
		}
	}

	/**
	 * 增加一个屏幕
	 * 
	 * @author huyong
	 * @param screenIndex
	 *            屏幕索引值
	 */
	public synchronized boolean addScreen(int screenIndex) {

		int index = 0;
		if (mScreenSparseArray != null) {
			index = screenIndex == -1 ? mScreenSparseArray.size() : screenIndex;
		}
		/*
		 * if (screenExsit(screenIndex)) { Log.i(LOG_TAG,
		 * "add screen already exist"); return false; }
		 */
		try {
			// 保存数据
			mDataModel.addScreen(index);

			// 缓存
			addScreenToHashMap(index, null);

			return true;
		} catch (Exception e) {
			Log.i(LOG_TAG, "keep data (add screen) exception");
			return false;
		}
	}

	/**
	 * 删除指定屏幕
	 * 
	 * @author huyong
	 * @param delScreenIndex
	 *            屏幕索引值
	 */
	
	public	synchronized boolean delScreen(final int delScreenIndex) {
		// 不存在
		if (!screenExsit(delScreenIndex)) {
			Log.i(LOG_TAG, "delete screen not exsit");
			return false;
		}

		try {
			// 删除数据库中数据
			mDataModel.removeScreen(delScreenIndex);

			// 删除内存中屏幕
			delScreenToHashMap(delScreenIndex);

			return true;
		} catch (Exception e) {
			Log.i(LOG_TAG, "keep data (delete screen) exception");
			return false;
		}
	}

	/**
	 * 返回屏幕个数
	 * 
	 * @author huyong
	 * @return
	 */
	public	int getScreenCount() {
		// 新数据库返回默认屏幕个数
		int screenCount = mDataModel.getScreenCount();
		if (screenCount == 0 || mDataModel.isNewDB()) {
			//add by dengdazhong
			//resolve ADT-7595 低配置手机，新安装后恢复默认桌面时，桌面中的指示器会显示为5，等待一段时间才变成3
			//如果低配需要减屏,就减掉前面和最后面的两个屏
			//为什么不直接修改ScreenSettingInfo.DEFAULT_SCREEN_COUNT？因为其他地方会用到DEFAULT_SCREEN_COUNT
			if (StaticScreenSettingInfo.sNeedDelScreen) {
				return ScreenSettingInfo.DEFAULT_SCREEN_COUNT - 2;
			}
			return ScreenSettingInfo.DEFAULT_SCREEN_COUNT;
		}
		return screenCount;
	}

	private boolean screenExsit(int screenIndex) {
		if (mScreenSparseArray != null) {
			int sz = mScreenSparseArray.size();
			if (screenIndex >= 0 && screenIndex < sz) {
				return true;
			}
		}
		return false;
	}

	/***
	 ****************************** 桌面组件操作*******************************************
	 */

	/**
	 * 添加文件夹、快捷方式到桌面，parts表将新增一条记录
	 * 
	 * @author huyong
	 * @param screenIndex
	 *            //目标屏幕索引
	 * @param itemInfo
	 *            //添加的item项
	 */
	public synchronized boolean addDesktopItem(final int screenIndex, ItemInfo itemInfo) {
		if (null == itemInfo) {
			Log.i(LOG_TAG, "add null item");
			return false;
		}
		if (!screenExsit(screenIndex)) {
			Log.i(LOG_TAG, "add item to not exist screen");
			return false;
		}

		// 特殊处理
		// 快捷方式
		if (IItemType.ITEM_TYPE_SHORTCUT == itemInfo.mItemType) {
			final SysShortCutControler shortCutControler = AppCore.getInstance()
					.getSysShortCutControler();
			if (null != shortCutControler) {
				ShortCutInfo sInfo = (ShortCutInfo) itemInfo;
				// 添加保护，防止空指针的出现
				String itemNameString = sInfo.mTitle == null ? null : sInfo.mTitle.toString();
				BitmapDrawable itemIcon = (sInfo.mIcon != null && sInfo.mIcon instanceof BitmapDrawable)
						? (BitmapDrawable) sInfo.mIcon
						: null;

				boolean bRet = shortCutControler.addSysShortCut(sInfo.mIntent, itemNameString,
						itemIcon);
				if (!bRet) {
					Log.i(LOG_TAG, "add system shortcut is fail");
					return false;
				}
			} else {
				Log.i(LOG_TAG, "system shortcut controler is null");
				return false;
			}
		}

		try {
			if (itemInfo.mInScreenId == 0 || itemInfo.mInScreenId == -1) {
				// 如果没赋值，就赋值，有值的就不修改，因为其他层拖过来的item，还要通过此id来删除原视图
				itemInfo.mInScreenId = System.currentTimeMillis();
			}
			mDataModel.addDesktopItem(screenIndex, itemInfo);

			prepareItemInfo(itemInfo);

			// 缓存相应修改
			addItemInfoToHashMap(itemInfo, screenIndex);

			return true;
		} catch (Exception e) {
			Log.i(LOG_TAG, "keep data (add item) exception");
			return false;
		}
	}

	/**
	 * 移动桌面item的位置
	 * 
	 * @author huyong
	 * @param screenIndex
	 *            目标屏幕
	 * @param desktopItemInScreenId
	 *            组件在屏幕上的id
	 * @param screenX
	 *            屏幕x坐标
	 * @param screenY
	 *            屏幕y坐标
	 * @param spanX
	 *            占x方向网格个数
	 * @param spanY
	 *            占y方向网格个数
	 */
	public synchronized boolean updateDesktopItem(final int screenIndex, ItemInfo itemInfo) {
		if (null == itemInfo) {
			Log.i(LOG_TAG, "move null item");
			return false;
		}
		if (!screenExsit(screenIndex)) {
			Log.i(LOG_TAG, "move item to not exist screen");
			return false;
		}

		try {
			mDataModel.updateDesktopItem(screenIndex, itemInfo);

			// 修改缓存数据
			delItemInfoFromHashMap(itemInfo.mInScreenId);
			addItemInfoToHashMap(itemInfo, screenIndex);

			return true;
		} catch (Exception e) {
			Log.i(LOG_TAG, "keep data (move item) exception");
			return false;
		}
	}

	/**
	 * 只清除db及hashmap中的数据
	 * 
	 * @param itemInfo
	 */
	public synchronized void removeDesktopItemInDBAndCache(ItemInfo itemInfo) {
		mDataModel.removeDesktopItem(itemInfo.mInScreenId);
		// 修改相应缓存
		delItemInfoFromHashMap(itemInfo.mInScreenId);
	}

	/**
	 * 
	 * @author huyong
	 * @param desktopItemInScreenId
	 *            组件在屏幕上的id
	 */
	public synchronized boolean removeDesktopItem(ItemInfo itemInfo) {
		if (null == itemInfo) {
			Log.i(LOG_TAG, "remove desk item param is null");
			return false;
		}

		try {
			removeDesktopItemInDBAndCache(itemInfo);
			// 清除绑定
			itemInfo.selfDestruct();
		} catch (Exception e) {
			return false;
		}

		// 特殊处理
		// 快捷方式
		if (IItemType.ITEM_TYPE_SHORTCUT == itemInfo.mItemType) {
			final SysShortCutControler shortCutControler = AppCore.getInstance()
					.getSysShortCutControler();
			if (null != shortCutControler) {
				ShortCutInfo shortCutInfoItem = (ShortCutInfo) itemInfo;
				boolean bRet = shortCutControler.delSysShortCut(shortCutInfoItem.mIntent);
				if (!bRet) {
					Log.i(LOG_TAG, "delete system shortcut fail");
				}
			} else {
				Log.i(LOG_TAG, "system shortcut controler is null");
			}
		}

		return true;
	}

	/**
	 * **************************************文件夹操作******************************
	 * *
	 */
	/**
	 * 移动桌面上指定item到指定文件夹
	 * 
	 * @param itemInScreenId
	 * @param folderId
	 * @param index
	 *            -1 追尾
	 */
	synchronized boolean moveDesktopItemToFolder(final ItemInfo itemInScreen, final long folderId,
			int index) {
		try {
			mDataModel.moveScreenItemToFolder(itemInScreen, folderId, index);

			// 缓存控制
			delItemInfoFromHashMap(itemInScreen.mInScreenId);

			return true;
		} catch (Exception e) {
			Log.i(LOG_TAG, "move to fold exception");
			return false;
		}
	}

	/**
	 * **************************************文件夹操作******************************
	 * *
	 */
	/**
	 * 添加ItemInfo到指定文件夹
	 * 
	 * @param itemInfo
	 * @param folderId
	 */
	public synchronized boolean addItemInfoToFolder(ItemInfo itemInfo, final long folderId) {
		try {
			mDataModel.addItemToFolder(itemInfo, folderId);
			return true;
		} catch (Exception e) {
			Log.i(LOG_TAG, "move to fold exception");
			return false;
		}
	}

	/**
	 * 移动桌面上指定item到指定文件夹
	 * 
	 * @param itemInScreenId
	 * @param folderId
	 * @return
	 */
	public synchronized boolean moveDesktopItemToFolder(final ItemInfo itemInScreen, final long folderId) {
		try {
			return moveDesktopItemToFolder(itemInScreen, folderId, -1);
		} catch (Exception e) {
			Log.i(LOG_TAG, "get fold count exception");
			return false;
		}
	}

	/**
	 * 从文件夹移除
	 * 
	 * @param itemInScreenId
	 * @param folderId
	 * @return
	 */
	public synchronized boolean moveDesktopItemFromFolder(ItemInfo info, int screenIndex,
			final long folderId) {
		if (null == info) {
			Log.i(LOG_TAG, "move item from folder, the item info is null");
			return false;
		}
		if (!screenExsit(screenIndex)) {
			Log.i(LOG_TAG, "move item from folder, the des screen is not exsit");
			return false;
		}
		try {
			mDataModel.moveScreenItemFromFolder(info, folderId);

			return true;
		} catch (Exception e) {
			Log.i(LOG_TAG, "move from fold exception");
			return false;
		}
	}

	/**
	 * 增加用户文件夹 将数据写入文件夹表 * 在此函数前必须addDesktopItem增加一个屏幕文件夹项
	 * 
	 * @param itemInfos
	 * @return
	 */
	public synchronized boolean addUserFolderContent(UserFolderInfo folderInfo, boolean fromDrawer) {
		if (null == folderInfo) {
			return false;
		}
		int count = folderInfo.getChildCount();
		for (int i = 0; i < count; i++) {
			ShortCutInfo info = folderInfo.getChildInfo(i);
			if (null == info) {
				continue;
			}
			info.mInScreenId = System.currentTimeMillis();
			mDataModel.addItemToFolder(info, folderInfo.mInScreenId, i + count, fromDrawer);
		}
		return count > 0;
	}

	/**
	 * 增加用户文件夹 将数据写入文件夹表 * 在此函数前必须addDesktopItem增加一个屏幕文件夹项
	 * 
	 * @param itemInfos
	 * @return
	 */
	public synchronized boolean addUserFolderContent(final long folderId, ArrayList<ItemInfo> itemInfos,
			boolean fromDrawer) {
		if (null == itemInfos) {
			Log.i(LOG_TAG, "add folder content, but the content is null");
			return false;
		}
		int count = mDataModel.getUserFolderCount(folderId);
		int sz = itemInfos.size();
		for (int i = 0; i < sz; i++) {
			ItemInfo info = itemInfos.get(i);
			if (null == info) {
				continue;
			}
			info.mInScreenId = System.currentTimeMillis();
			mDataModel.addItemToFolder(info, folderId, i + count, fromDrawer);
		}
		return true;
	}

	public synchronized boolean removeUserFolderContent(final long folderId,
			ArrayList<ItemInfo> itemInfos, boolean fromDrawer) {
		if (null == itemInfos) {
			Log.i(LOG_TAG, "add folder content, but the content is null");
			return false;
		}
		int sz = itemInfos.size();
		for (int i = 0; i < sz; i++) {
			ItemInfo info = itemInfos.get(i);
			if (null == info) {
				continue;
			}
			removeItemFromFolder(info, folderId, fromDrawer);
			// 移除时取出绑定
			info.selfDestruct();
		}
		return true;
	}

	public synchronized void removeItemFromFolder(ItemInfo info, long folderId, boolean fromDrawer) {
		if (info.mInScreenId != 0) {
			mDataModel.removeItemFromFolder(info.mInScreenId, folderId);
		} else {
			if (info instanceof ShortCutInfo) {
				mDataModel
						.removeItemFromFolder(((ShortCutInfo) info).mIntent, fromDrawer, folderId);
			}
		}
	}

	/**
	 * 删除指定文件夹内指定ShortCutInfo的图标
	 * 
	 * @param folderInfo
	 * @param intent
	 */
	public void removeItemsFromFolder(UserFolderInfo folderInfo, ShortCutInfo shortCutInfo) {
		// 1:删除缓存
		ArrayList<ShortCutInfo> list = folderInfo.remove(shortCutInfo);
		// 2:删除DB
		int size = list.size();
		if (size > 0) {
			final long folderId = folderInfo.mInScreenId;
			for (int i = 0; i < size; i++) {
				ShortCutInfo info = list.get(i);
				mDataModel.removeItemFromFolder(info.mInScreenId, folderId);
			}
		}
		list.clear();
		list = null;
	}

	public synchronized boolean removeUserFolder(ItemInfo info) {
		if (null == info) {
			Log.i(LOG_TAG, "remove user folder, the param is null");
			return false;
		}
		try {
			// 1. 删除屏幕项
			// 2. 删除文件夹表对应项
			boolean bOk = removeDesktopItem(info);
			if (bOk) {
				GLAppFolderController folderController = GLAppFolderController.getInstance();
				folderController.removeFolderInfo(folderController.getFolderInfoById(
						info.mInScreenId, GLAppFolderInfo.FOLDER_FROM_SCREEN));
				Log.i("dzj", "Screen--->" + "removeFolderInfo");
				mDataModel.removeUserFolder(info.mInScreenId);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.i(LOG_TAG, "delete folder table exception");
			return false;
		}
	}

	/**
	 * 获取文件夹项
	 * 
	 * @param folderId
	 * @return
	 */
	public synchronized ArrayList<ItemInfo> getFolderItems(final long folderId) {
		ArrayList<ItemInfo> folderItemList = mDataModel.getScreenFolderItems(folderId, -1);
		prepareItemInfo(folderItemList);
		return folderItemList;
	}

	synchronized ArrayList<ItemInfo> getFolderItems(final long folderId, boolean prepare) {
		ArrayList<ItemInfo> folderItemList = mDataModel.getScreenFolderItems(folderId, -1);
		if (prepare) {
			prepareItemInfo(folderItemList);
		}
		return folderItemList;
	}

	synchronized ArrayList<ItemInfo> getFolderItems(final long folderId, int count) {
		ArrayList<ItemInfo> folderItemList = mDataModel.getScreenFolderItems(folderId, count);
		prepareItemInfo(folderItemList);
		return folderItemList;
	}

	public ArrayList<ItemInfo> handleSDIsReady() {
		ArrayList<ItemInfo> ret = new ArrayList<ItemInfo>();

		int mapSz = mScreenSparseArray.size();
		for (int i = 0; i < mapSz; i++) {
			ArrayList<ItemInfo> infos = mScreenSparseArray.get(i);
			if (null == infos) {
				continue;
			}
			int listSz = infos.size();
			for (int j = 0; j < listSz; j++) {
				ItemInfo info = infos.get(j);
				if (null == info) {
					continue;
				}
				boolean bChanged = prepareItemInfo(info);

				// 文件夹
				if (IItemType.ITEM_TYPE_USER_FOLDER == info.mItemType) {
					UserFolderInfo folderInfo = (UserFolderInfo) info;
					bChanged |= prepareItemInfo(folderInfo);
				}

				if (bChanged) {
					ret.add(info);
				}
			}
		}
		return ret;
	}

	public synchronized ArrayList<ItemInfo> unInstallApp(Intent intent) {
		if (null == intent || null == mScreenSparseArray) {
			return null;
		}

		ArrayList<ItemInfo> itemInfos = null;
		int mapSz = mScreenSparseArray.size();
		for (int i = 0; i < mapSz; i++) {
			ArrayList<ItemInfo> infos = mScreenSparseArray.get(i);
			if (null == infos) {
				continue;
			}
			int listSz = infos.size();
			for (int j = 0; j < listSz; j++) {
				ItemInfo info = infos.get(j);
				if (null == info) {
					continue;
				}
				if (!(info instanceof UserFolderInfo)) {
					continue;
				}

				// 清理文件夹 子项
				if (IItemType.ITEM_TYPE_USER_FOLDER == info.mItemType) {
					ArrayList<ItemInfo> deskInfos = getFolderItems(info.mInScreenId, false);
					int deskSz = deskInfos.size();
					for (int k = 0; k < deskSz; k++) {
						ItemInfo deskInfo = deskInfos.get(k);
						if (null == deskInfo) {
							continue;
						}
						if (ConvertUtils.intentCompare(intent, ((ShortCutInfo) deskInfo).mIntent)) {
							try {
								mDataModel.removeItemFromFolder(deskInfo.mInScreenId,
										info.mInScreenId);

								if (null == itemInfos) {
									itemInfos = new ArrayList<ItemInfo>();
								}
								itemInfos.add(info);
							} catch (Exception e) {
								Log.i(LOG_TAG, "uninstall process remove folder exception");
							}

						}
					}
				}

				// 屏幕项清理
				// Intent itemIntent = getItemInfoIntent(info);
				// if (ConvertUtils.intentCompare(intent, itemIntent))
				// {
				// removeDesktopItem(info);
				// }
			}
		}

		return itemInfos;
	}

	/**
	 * **************************************缓存处理*******************************
	 * **
	 * ************************************文件夹不缓存*******************************
	 */

	/**
	 * 增加一个屏幕 大于指定位置的屏幕向后 插入屏幕
	 * 
	 * @param screenIndex
	 *            屏幕位置,MAP不对位置做保护
	 * @param itemInfos
	 *            屏幕元素 NULL 则为空屏幕
	 */
	private void addScreenToHashMap(int screenIndex, ArrayList<ItemInfo> itemInfos) {
		int sz = mScreenSparseArray.size();
		for (int i = sz - 1; i >= screenIndex; i--) {
			ArrayList<ItemInfo> infos = mScreenSparseArray.get(i);
			if (null != infos) {
				mScreenSparseArray.put(i + 1, infos);
			}
		}
		if (null == itemInfos) {
			itemInfos = new ArrayList<ItemInfo>();
		}
		mScreenSparseArray.put(screenIndex, itemInfos);

		// 保存当前有多少屏，供挂掉的时候err report反馈
		int count = mScreenSparseArray.size();
		saveScreenCount(count);
	}

	/**
	 * 删除一个屏幕 小于屏幕索引不变 等于屏幕索引取值 大于屏幕索引的依次前靠 移除最后一个
	 * 
	 * @param screenIndex
	 *            屏幕位置
	 * @return 屏幕内容
	 */
	private ArrayList<ItemInfo> delScreenToHashMap(int screenIndex) {
		ArrayList<ItemInfo> data = null;
		int sz = mScreenSparseArray.size();
		if (screenIndex < sz) {
			data = mScreenSparseArray.get(screenIndex);
		}
		for (int i = screenIndex + 1; i < sz; i++) {
			ArrayList<ItemInfo> infos = mScreenSparseArray.get(i);
			if (null != infos) {
				mScreenSparseArray.put(i - 1, infos);
			}
		}
		mScreenSparseArray.remove(sz - 1);

		// 保存当前有多少屏，供挂掉的时候err report反馈
		int count = mScreenSparseArray.size();
		saveScreenCount(count);
		return data;
	}

	private void saveScreenCount(int count) {
		Context context = ApplicationProxy.getContext();
		PrivatePreference pref = PrivatePreference.getPreference(mContext);
		pref.putInt(PrefConst.KEY_SCREEN_COUNT, count);
		pref.commit();
		try {
			pref.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 增加一个元素
	 * 
	 * @param info
	 * @param screenIndex
	 */
	private void addItemInfoToHashMap(ItemInfo info, int screenIndex) {
		info.mScreenIndex = screenIndex;
		ArrayList<ItemInfo> itemInfos = mScreenSparseArray.get(screenIndex);
		if (null == itemInfos) {
			itemInfos = new ArrayList<ItemInfo>();
			itemInfos.add(info);

			mScreenSparseArray.put(screenIndex, itemInfos);
		} else {
			itemInfos.add(info);
		}

		// 保存当前有多少屏，供挂掉的时候err report反馈
		int count = mScreenSparseArray.size();
		saveScreenCount(count);
	}

	/**
	 * 删除一个元素
	 * 
	 * @param itemScreenId
	 */
	private void delItemInfoFromHashMap(long itemScreenId) {
		int sz = mScreenSparseArray.size();
		for (int i = 0; i < sz; i++) {
			ArrayList<ItemInfo> itemInfos = mScreenSparseArray.get(i);
			if (null != itemInfos) {
				for (int j = 0; j < itemInfos.size(); j++) {
					ItemInfo itemInfo = itemInfos.get(j);
					if (null != itemInfo && itemInfo.mInScreenId == itemScreenId) {
						itemInfos.remove(j);
					}
				}
			}
		}
	}

	// add by huyong 2011-03-17 for 清理桌面脏数据
	/**
	 * 判断桌面是否有脏数据
	 */
	public boolean isScreenDirtyData() {
		return mDataModel.isScreenDirtyData();
	}

	/**
	 * 清理桌面表中脏数据
	 * 
	 * @author huyong
	 */
	public void clearScreenDirtyData() {
		mDataModel.clearScreenDirtyData();
	}

	// add by huyong end 2011-03-17 for 清理桌面脏数据

	private GoWidgetDataModel mGoWidgetDataModel;
	public ArrayList<GoWidgetBaseInfo> getAllGoWidgetInfos() {
		if (mGoWidgetDataModel == null) {
			mGoWidgetDataModel = new GoWidgetDataModel(mContext);
		}
		return mGoWidgetDataModel.getAllGoWidgetInfos();
	}

	public int findSpecificGoWidgetScreenIndex(int gowidgetId) {
		return mDataModel.getGoWidgetScreenIndexByWidgetId(gowidgetId);
	}

	public void getScreenItems(int index, ArrayList<ItemInfo> itemArray) {
		mDataModel.getScreenItems(index, itemArray);
		prepareItemInfo(itemArray);
	}

	public void updateDBItem(ItemInfo itemInfo) {
		mDataModel.updateDBItem(itemInfo);
	}
	
	public void loadSetting() {
		mScreenSettingInfo = SettingProxy.getScreenSettingInfo();
		mEffectSettingInfo = SettingProxy.getEffectSettingInfo();
		mDesktopSettingInfo = SettingProxy.getDesktopSettingInfo();
	}
	
	public ScreenSettingInfo updateScreenSettingInfo() {
		mScreenSettingInfo = SettingProxy.getScreenSettingInfo();
		return mScreenSettingInfo;
	}
	
	public ScreenSettingInfo getScreenSettingInfo() {
		return mScreenSettingInfo;
	}
	
	public EffectSettingInfo updateEffectSettingInfo() {
		mEffectSettingInfo = SettingProxy.getEffectSettingInfo();
		return mEffectSettingInfo;
	}
	
	public EffectSettingInfo getEffectSettingInfo() {
		return mEffectSettingInfo;
	}
	
	public DesktopSettingInfo updateDesktopSettingInfo() {
		mDesktopSettingInfo = SettingProxy.getDesktopSettingInfo();
		return mDesktopSettingInfo;
	}
	
	public DesktopSettingInfo getDesktopSettingInfo() {
		return mDesktopSettingInfo;
	}
	
	/**
	 * 卸载应用时在DesktopItems缓存中删除
	 * @param intent
	 */
	public void clearDesktopItems(Intent intent) {
		// 清理数据
		if (mScreenSparseArray != null) {
			int sz = mScreenSparseArray.size();
			for (int i = 0; i < sz; i++) {
				ArrayList<ItemInfo> infos = mScreenSparseArray.get(i);
				if (null != infos) {
					int j = 0;
					while (j < infos.size()) {
						ItemInfo info = infos.get(j);
						if (info instanceof ShortCutInfo) {
							ShortCutInfo shortCutInfo = (ShortCutInfo) info;
							if (ConvertUtils.intentCompare(intent, shortCutInfo.mIntent)) {
								infos.remove(j);
								removeDesktopItem(shortCutInfo);
								continue;
							}
						} else if (info instanceof UserFolderInfo) {
							final UserFolderInfo folderInfo = (UserFolderInfo) info;
							folderInfo.remove(intent);
						}
						j++;
					}
				}
			}
		}
	}
	
	// 清除桌面注册到DeskItemInfo的View
	public void unRigistDesktopObject() {
		if (mScreenSparseArray != null) {
			final int size = mScreenSparseArray.size();
			for (int i = 0; i < size; i++) {
				ArrayList<ItemInfo> screenInfos = mScreenSparseArray.get(i);
				if (screenInfos != null) {
					int count = screenInfos.size();
					for (int j = 0; j < count; j++) {
						ItemInfo itemInfo = screenInfos.get(j);
						if (itemInfo != null) {
							// 仅清除到BubbleTextView的注册关系
							itemInfo.clearAllObserver();
						}
					}
				}
			}
		}
	}
	
	public void unbindObjectInScreen(int screenId) {
		if (mScreenSparseArray != null) {
			final ArrayList<ItemInfo> sc = mScreenSparseArray.get(screenId);
			ScreenUtils.unbindDesktopObject(sc);
		}
	}
	
	// 获取对应文件夹的ItemInfo
	public UserFolderInfo getFolderItemInfo(long refFolderId) {
		ArrayList<ItemInfo> items = getItemFromRefId(refFolderId);
		if (null != items && items.size() > 0) {
			ItemInfo info = items.get(0);
			if (info != null && info instanceof UserFolderInfo) {
				return (UserFolderInfo) info;
			}
		}
		return null;
	}
	
	public synchronized ArrayList<ItemInfo> getItemFromRefId(long refId) {
		ArrayList<ItemInfo> retInfos = new ArrayList<ItemInfo>();
		int screenCount = mScreenSparseArray.size();

		for (int i = 0; i < screenCount; i++) {
			ArrayList<ItemInfo> infoList = mScreenSparseArray.get(i);
			if (null == infoList) {
				continue;
			}

			int viewCount = infoList.size();
			for (int j = 0; j < viewCount; j++) {
				ItemInfo info = infoList.get(j);
				if (null != info) {
					if (info.mRefId == refId || info.mInScreenId == refId) {
						retInfos.add(info);
					}
				}
			}
		}
		return retInfos;
	}
	
	public boolean isTheSameIconInToFolder(UserFolderInfo info, Object dragInfo) {
		int size = info.getContents().size();
		Intent desIntent = null;
		if (dragInfo instanceof DockItemInfo) {
			ShortCutInfo shortCutInfo = (ShortCutInfo) ((DockItemInfo) dragInfo).mItemInfo;
			desIntent = shortCutInfo.mIntent;
		} else if (dragInfo instanceof FunAppItemInfo) {
			desIntent = ((FunAppItemInfo) dragInfo).getAppItemInfo().mIntent;

		} else if (dragInfo instanceof ShortCutInfo) {
			desIntent = ((ShortCutInfo) dragInfo).mIntent;
		}
		for (int i = 0; i < size; i++) {
			ShortCutInfo app = (ShortCutInfo) info.getContents().get(i);
//			boolean isHaveCmp = (desIntent != null && app.mIntent != null)
//					&& (desIntent.getComponent() != null && app.mIntent
//							.getComponent() != null);
			boolean isHaveSameData = false;
			if (desIntent != null && app.mIntent != null) {
				if ((desIntent.getDataString() != null && app.mIntent.getDataString() != null)
						&& (desIntent.getDataString().equals(app.mIntent.getDataString()))) {
					isHaveSameData = true;
				} else if (desIntent.getDataString() == null && app.mIntent.getDataString() == null) {
					isHaveSameData = true;
				}
			}
			if (ConvertUtils.shortcutIntentCompare(app.mIntent, desIntent)) {
				return true;
			} else if (ConvertUtils.intentToStringCompare(app.mIntent, desIntent)) {
				return true;
			} else if (ConvertUtils.intentCompare(app.mIntent, desIntent) && isHaveSameData) {
				return true;
			} else if (ConvertUtils.selfIntentCompare(app.mIntent, desIntent)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 更新文件夹里面全部仔项的foldID
	 * 
	 * @param folderID
	 * @param info
	 */
	public void updateFolderContents(UserFolderInfo userFolderInfo) {
		if (userFolderInfo != null) {
			ArrayList<ItemInfo> items = userFolderInfo.getContents();
			if (items == null) {
				return;
			}
			final long folderID = userFolderInfo.mInScreenId;
			final int count = items.size();
			for (int i = 0; i < count; i++) {
				updateFolderItem(folderID, userFolderInfo.getChildInfo(i));
			} // end for
		} // end if
	}
	
	public boolean dragItemInfoToFolder(ShortCutInfo itemInfo, UserFolderInfo folderInfo) {
		if (null != folderInfo) {
			if (null != itemInfo) {
				if (itemInfo.mInScreenId == 0 || itemInfo.mInScreenId == -1) {
					itemInfo.mInScreenId = System.currentTimeMillis();
				}
					// 图标去重
//					mControler.removeItemsFromFolder(folderInfo, itemInfo);
					// 修改数据库
				addItemInfoToFolder(itemInfo, folderInfo.mInScreenId);
			}
			return true;
		}
		return false;
	} // end dragToFolder

	/**
	 * 获取是进入文件夹的应用是第几个
	 */
	public int getAddInFolderIndex(UserFolderInfo userFolderInfo, Object dragInfo) {
		int size = userFolderInfo.getContents().size();
		int toAddIndex = size;
		Intent desIntent = null;
		if (dragInfo instanceof DockItemInfo) {
			ShortCutInfo shortCutInfo = (ShortCutInfo) ((DockItemInfo) dragInfo).mItemInfo;
			desIntent = shortCutInfo.mIntent;
		} else if (dragInfo instanceof FunAppItemInfo) {
			desIntent = ((FunAppItemInfo) dragInfo).getAppItemInfo().mIntent;

		} else if (dragInfo instanceof ShortCutInfo) {
			desIntent = ((ShortCutInfo) dragInfo).mIntent;
		}
		for (int i = 0; i < size; i++) {
			ShortCutInfo app = (ShortCutInfo) userFolderInfo.getContents().get(i);
			if (ConvertUtils.intentToStringCompare(app.mIntent, desIntent)) {
				toAddIndex = i;
				break;
			}
		}
		return toAddIndex;
	}
	
	public void uninstallApplication(Intent intent) {
		if (intent != null) {
			final ComponentName componentName = intent.getComponent();
			if (componentName != null) {
				String action = intent.getAction();
				try {
					// go主题和go精品假图标提示用户不能删除
					if (ICustomAction.ACTION_FUNC_SPECIAL_APP_GOSTORE
							.equals(action)
							|| ICustomAction.ACTION_FUNC_SPECIAL_APP_GOTHEME
									.equals(action)
							|| ICustomAction.ACTION_FUNC_SPECIAL_APP_GOWIDGET
									.equals(action)) {
						Toast.makeText(mContext, R.string.uninstall_fail, Toast.LENGTH_LONG).show();
					} else {
						boolean goToQuestionnaire = GoAppUtils.goToQuestionnaire(mContext,
								componentName.getPackageName());
						if (!goToQuestionnaire) {
							AppUtils.uninstallPackage(mContext, componentName.getPackageName());
						}
					}
				} catch (Exception e) {
					// 处理卸载异常
					Toast.makeText(mContext, R.string.uninstall_fail, Toast.LENGTH_LONG).show();
				}
			}
		} else {
			// 卸载失败
			Toast.makeText(mContext, R.string.uninstall_fail, Toast.LENGTH_LONG).show();
		}
		
//		AppFuncUninstallHelper.uninstallApp(mContext, intent, null);
	}
	
	public int handleDuplicateItemInfo(ShortCutInfo itemInfo, final UserFolderInfo folderInfo,
			int addIndex, int folderIconMaxIconCount) {
		if (folderInfo.getContents().size() >= folderIconMaxIconCount) {
			ArrayList<ShortCutInfo> cutInfos = folderInfo.remove(itemInfo);
			for (ShortCutInfo info : cutInfos) {
				GLAppFolderController.getInstance().removeAppFromScreenFolder(info.mInScreenId,
						folderInfo.mInScreenId);
			}
			folderInfo.add(itemInfo);
			addItemInfoToFolder(itemInfo, folderInfo.mInScreenId);
			addIndex = folderInfo.getContents().size();
		}
		return addIndex;
	}
	
	public void checkErrorAppWidget(ScreenAppWidgetInfo widgetInfo,
			AppWidgetProviderInfo originalInfo, AppWidgetProviderInfo info, boolean accessable) {
		boolean error = false;
		if (widgetInfo != null && widgetInfo.mHostView != null
				&& widgetInfo.mHostView instanceof LauncherWidgetHostView) {
			LauncherWidgetHostView hostView = (LauncherWidgetHostView) widgetInfo.mHostView;
			int childCount = hostView.getChildCount();
			for (int i = 0; i < childCount; i++) {
				View view = hostView.getChildAt(i);
				if (view != null && view instanceof WidgetErrorView) {
					error = true;
					// 对error widget view设置点击恢复的监听
					((WidgetErrorView) view).setText(com.gau.go.launcherex.R.string.restore_widget_tip);
					view.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							View parent = (View) v.getParent();
							LauncherWidgetHostView hostView = null;
							if (parent != null && parent instanceof LauncherWidgetHostView) {
								hostView = (LauncherWidgetHostView) parent;
							} else {
								return;
							}
							Object obj = hostView.getTag();
							ScreenAppWidgetInfo info = null;
							if (obj != null && obj instanceof ScreenAppWidgetInfo) {
								info = (ScreenAppWidgetInfo) obj;
							} else {
								return;
							}
							
							// 恢复widget
							mScreenObserver.restoreAppWidget(info);
						}
					});
					break;
				}
			}
		}
		if (!error) {
			return;
		}
		// 记录加载error widget的信息
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("Error===================================start" + "\n");
		try {
			if (originalInfo != null) {
				stringBuffer.append("originalInfo != null" + "\n");
				stringBuffer.append("originalInfo.label = " + originalInfo.label + "\n");
				if (originalInfo.provider != null) {
					stringBuffer.append("originalInfo.provider:" + originalInfo.provider.toString()
							+ "\n");
				}
			} else {
				stringBuffer.append("originalInfo == null" + "\n");
				// 当originalInfo == null && widgetInfo.mProviderIntent != null时
				// 才有机会重新绑定widget，这时才能知道有没有权限去绑定widget。
				if (widgetInfo.mProviderIntent != null) {
					stringBuffer.append("Is bind app widget accessable? = " + accessable + "\n");
				}
			}

			if (info == null) {
				stringBuffer.append("info == null" + "\n");
			} else {
				stringBuffer.append("info != null" + "\n");
				stringBuffer.append("info.label = " + info.label + "\n");
				if (info.provider != null) {
					stringBuffer.append("info.provider:" + info.provider.toString() + "\n");
				}
			}

			if (widgetInfo.mProviderIntent != null) {
				stringBuffer.append("widgetInfo.mProviderIntent != null" + "\n");
			} else {
				stringBuffer.append("widgetInfo.mProviderIntent == null" + "\n");
			}

			// 判断 widget程序的状态(是否安装，是否在SD卡)
			if (widgetInfo.mProviderIntent != null) {
				ComponentName componentName = widgetInfo.mProviderIntent.getComponent();
				if (componentName == null) {
					stringBuffer.append("widgetInfo.mProviderIntent.getComponent() == null" + "\n");
				} else {
					stringBuffer.append("widgetInfo.mProviderIntent.getComponent():"
							+ componentName.toString() + "\n");
					String packageName = componentName.getPackageName();
					if (!TextUtils.isEmpty(packageName)) {
						boolean isExist = AppUtils.isAppExist(mContext, packageName);
						if (!isExist) {
							stringBuffer.append("application doesn't exist!");
						} else {
							ApplicationInfo applicatioInfo = mContext.getPackageManager()
									.getApplicationInfo(packageName, 0);
							// 程序在SD卡上
							if ((applicatioInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
								stringBuffer.append("application is in SDCard!" + "\n");
							} else {
								stringBuffer.append("application is not in SDCard!" + "\n");
							}
						}
					}
				}
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			stringBuffer.append("occur NameNotFoundException" + "\n");
			e.printStackTrace();
		} finally {
			stringBuffer.append("Error===================================end" + "\n");
			String log = stringBuffer.toString();
			Log.i("problem_app_widget", log);
			GoAppUtils.postLogInfo("widget_error_view", log);
		}
	}
	
	/**
	 * 初始化默认桌面数据
	 * 
	 * @author huyong
	 * @param context
	 */
	public void initDesktopItem() {
		final AppDataEngine dataEngine = AppDataEngine.getInstance(ApplicationProxy.getContext());
		ArrayList<AppItemInfo> dbItemInfos = dataEngine.getAllAppItemInfos();
		if (dbItemInfos == null) {
			return;
		}

		// 添加屏幕
		for (int i = 0; i < ScreenSettingInfo.DEFAULT_SCREEN_COUNT; i++) {
			addScreen(i);
		}

		int rowCount = StaticScreenSettingInfo.sScreenRow - 1; // 最后一行
		int count = StaticScreenSettingInfo.sScreenCulumn >= 6 ? 6 : StaticScreenSettingInfo.sScreenCulumn;
		// TODO:这里的扫描匹配算法可以优化
		ArrayList<ShortCutInfo> infos = addRecommendedApp(-1, -1, -1, true);
		
		DefaultWorkspaceControler controler = new DefaultWorkspaceControler(mContext);
		for (int row = rowCount; row >= rowCount - 1; row--) { // 扫描最后两行，为空位补图标
			for (int column = 0; column < count; column++) {
				Object[] objs = controler.getDefaultDesktopAlternateIcon(infos, column, row,
						ScreenSettingInfo.DEFAULT_MAIN_SCREEN);
				if (objs != null) {
					int type = (Integer) objs[0];
					if (type == DefaultWorkspaceControler.ICON_TYPE_ADD_GO_FOLDER) {
						UserFolderInfo userFolderInfo = (UserFolderInfo) objs[1];
						mDataModel.addDesktopItem(ScreenSettingInfo.DEFAULT_MAIN_SCREEN, userFolderInfo);
						// 把GO系列应用添加到文件夹里
						addGoAppsToDeskFolder(userFolderInfo);
					} else if (type == DefaultWorkspaceControler.ICON_TYPE_CREATE_ITEM_TO_DESK) {
						Intent intent = (Intent) objs[1];
						int itemType = (Integer) objs[2]; // 图标类型
						int titleResId = (Integer) objs[3]; // 标题resId
						Drawable icon = (Drawable) objs[4]; // 图标icon
						createItemToDesk(mContext, intent, itemType, column, row,
								ScreenSettingInfo.DEFAULT_MAIN_SCREEN, titleResId, icon);
						
					}
				}
			}
		}
		// add search widget
		/*
		 * ScreenAppWidgetInfo desktopItemInfo = new
		 * ScreenAppWidgetInfo(ICustomWidgetIds.SEARCH_WIDGET);
		 * desktopItemInfo.mInScreenId = System.currentTimeMillis();
		 * desktopItemInfo.mItemType = IItemType.ITEM_TYPE_APP_WIDGET;
		 * desktopItemInfo.mCellX = 0; desktopItemInfo.mCellY = 0;
		 * desktopItemInfo.mSpanX = 4; desktopItemInfo.mSpanY = 1; try {
		 * addDesktopItem(ScreenSettingInfo.DEFAULT_MAIN_SCREEN,
		 * desktopItemInfo); } catch (Exception e) { }
		 */
	}
	
	/**
	 * 添加推荐app,每个位置可能有几个候选，选第一个未安装的做为推荐
	 * @param screen -1表示为主屏幕
	 * @param cellX  -1表示直接读取 info.mGroup
	 * @param cellY  -1表示直接读取 info.RowIndex
	 * @param result 是否返回 iteminfo
	 */
	private ArrayList<ShortCutInfo> addRecommendedApp(int screen, int cellX, int cellY,
			boolean result) {
		HashMap<Integer, ArrayList<XmlRecommendedAppInfo>> map = XmlRecommendedApp
				.getRecommendedAppMap();
		ShortCutInfo shortCutInfo = null;
		String uid = GoStorePhoneStateUtil.getUid(mContext);
		ArrayList<ShortCutInfo> shortCutInfos = new ArrayList<ShortCutInfo>();
		if (map != null) {
			ArrayList<XmlRecommendedAppInfo> list = null;
			Iterator iterator = map.entrySet().iterator();
			
			// 原来：应用图标:已安装的应用不推；
			//现在：200渠道下，保留现有规则的前提下,如果用户未安装电子市场,则: 应用图标:只推送兜底的非广告应用图标（2014-1-13）
            boolean isRecommend = true;
            if (GoAppUtils.isMarketNotExitAnd200Channel(mContext)) {
                isRecommend = false;
            }
			
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				list = (ArrayList<XmlRecommendedAppInfo>) entry.getValue();
				if (list != null) {
					XmlRecommendedAppInfo installedInfo = null;
					int size = list.size();
					Date date = Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).getTime();
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					String today = sf.format(date);
					//保存已加的row信息
					Set<Integer> rows = new HashSet<Integer>();
					for (int j = 0; j < size; j++) {
						XmlRecommendedAppInfo info = list.get(j);
						if (info == null
								|| (info.mSTime != null && today.compareTo(info.mSTime) < 0)
								|| (info.mETime != null && today.compareTo(info.mETime) > 0)) {
							continue;
						}

							if ((info.mChannelId == null || (info.mChannelId != null
									&& !info.mChannelId.trim().equals("") && info.isContainUid(uid)))
									&& null != info.mPackagename) {
								// 判断该行是否已添加了推荐应用
								if (rows.contains(info.mRowIndex)) {
									continue;
								} else {
									rows.add(info.mRowIndex);
								}
								// 原来：应用图标:已安装的应用不推；
					            //现在：200渠道下，保留现有规则的前提下,如果用户未安装电子市场,则: 应用图标:只推送兜底的非广告应用图标（2014-1-13）
								if (!GoAppUtils.isAppExist(mContext, info.mPackagename) && isRecommend) {
									if (info.mPackagename.equals("com.UCMobile")
											&& GoAppUtils.isAppExist(mContext, "com.uc.browser")) {
										info.mPackagename = "com.uc.browser";
										installedInfo = info;
										continue;
									}

									int s = screen >= 0 ? screen : info.mScreenIndex;
									int x = cellX == -1 ? info.mGroup : cellX;
									int y = cellY == -1 ? info.mRowIndex : cellY;

								//(屏幕格子行数 < 5 && 是主屏 && cellY不是倒数第一行）将跳过这个推荐图标的添加
								if (skipRecommendedApp(s, y)) {
									continue;
								}

									shortCutInfo = addDownLoadShortCut(info.mPackagename, info.mAction,
											new int[] { x, y }, info.mTitle, info.mIconId, s, result);
									shortCutInfos.add(shortCutInfo);
									continue;

								} else {
									installedInfo = info;
									if (installedInfo != null && installedInfo.mShowInstallIcon) {
										int s = screen >= 0 ? screen : installedInfo.mScreenIndex;
										int x = cellX == -1 ? installedInfo.mGroup : cellX;
										int y = cellY == -1 ? installedInfo.mRowIndex : cellY;

									//(屏幕格子行数 < 5 && 是主屏 && cellY不是倒数第一行）将跳过这个推荐图标的添加
									if (skipRecommendedApp(s, y)) {
										continue;
									}

									shortCutInfo = addExistAppShortCut(installedInfo.mPackagename,
											new int[] { x, y }, s, result);
										shortCutInfos.add(shortCutInfo);
										installedInfo = null;;
									}
								}
							}
					}
				}
			}
		}
		return shortCutInfos;
	}
	
	/**
	 * 添加GO应用到桌面文件夹
	 */
	private void addGoAppsToDeskFolder(UserFolderInfo userFolderInfo) {
		PackageManager pm = mContext.getPackageManager();
		String[] goAppsPkg = ScreenUtils.getGoAppsPkgName(); // GO系列应用的包名数组
		int[] goAppsNameIds = ScreenUtils.getGoAppsNameIds(); // GO系列应用的程序名称的id数组
		int[] goAppsIconIds = ScreenUtils.getGoAppsIconIds(); // GO系列应用的图标id数组
		String[] goAppsActions = ScreenUtils.getGoAppsActions(); // GO系列应用的Action数组
		String[] goAppsFtpUrls = ScreenUtils.getGoAppsFtpUrl(); // GO系列应用的FTP下载地址数组
		int itemX = 0;
		int itemY = 0;
		if (goAppsPkg.length == goAppsNameIds.length) {
			ArrayList<ShortCutInfo> installList = new ArrayList<ShortCutInfo>(); // 保存已安装的程序
			ArrayList<ShortCutInfo> recommandList = new ArrayList<ShortCutInfo>(); // 保存未安装的推荐程序
			for (int k = 0; k < goAppsPkg.length; k++) {
				ShortCutInfo itemInfo = new ShortCutInfo();
				itemInfo.mInScreenId = System.currentTimeMillis() + k;
				itemInfo.mItemType = IItemType.ITEM_TYPE_APPLICATION;
				itemInfo.mCellX = itemX;
				itemInfo.mCellY = itemY;
				itemInfo.mSpanX = 1;
				itemInfo.mSpanY = 1;

				if (goAppsPkg[k].equals(GOSTOREPKGNAME)) {
					Intent goStoreIntent = new Intent(ICustomAction.ACTION_FUNC_SPECIAL_APP_GOSTORE);
					itemInfo.mIntent = goStoreIntent;
					ComponentName goStoreCom = new ComponentName(GOSTOREPKGNAME,
							ICustomAction.ACTION_FUNC_SPECIAL_APP_GOSTORE);
					itemInfo.mIntent.setComponent(goStoreCom);
					itemInfo.mIntent.setData(Uri.parse("package:" + GOSTOREPKGNAME));
					installList.add(itemInfo);
				} else if (goAppsPkg[k].equals(GOWIDGETPKGNAME)) {
					Intent goWidgetIntent = new Intent(
							ICustomAction.ACTION_FUNC_SPECIAL_APP_GOWIDGET);
					itemInfo.mIntent = goWidgetIntent;
					ComponentName goWidgetCom = new ComponentName(GOWIDGETPKGNAME,
							ICustomAction.ACTION_FUNC_SPECIAL_APP_GOWIDGET);
					itemInfo.mIntent.setComponent(goWidgetCom);
					itemInfo.mIntent.setData(Uri.parse("package:" + GOWIDGETPKGNAME));
					installList.add(itemInfo);
				} else if (goAppsPkg[k].equals(GOTHEMEPKGNAME)) {
					Intent goThemeIntent = new Intent(ICustomAction.ACTION_FUNC_SPECIAL_APP_GOTHEME);
					itemInfo.mIntent = goThemeIntent;
					ComponentName goThemeCom = new ComponentName(GOTHEMEPKGNAME,
							ICustomAction.ACTION_FUNC_SPECIAL_APP_GOTHEME);
					itemInfo.mIntent.setComponent(goThemeCom);
					itemInfo.mIntent.setData(Uri.parse("package:" + GOTHEMEPKGNAME));
					installList.add(itemInfo);
				} else {
					if (GoAppUtils.isAppExist(mContext, goAppsPkg[k])) {
						if (goAppsPkg[k].equals(PackageName.RECOMMAND_GOWEATHEREX_PACKAGE)
								&& (AppUtils.getVersionCodeByPkgName(mContext, goAppsPkg[k]) < 10)) {
							// 如果是GO天气，则需要判断其版本号是否低于10，低于10的版本为widget版本，无法获取图标，则换成推荐图
							Intent downloadIntent = new Intent(goAppsActions[k]);
							ComponentName cmpName = new ComponentName(goAppsPkg[k], goAppsPkg[k]);
							downloadIntent.setComponent(cmpName);
							downloadIntent.putExtra(RECOMMAND_APP_FTP_URL_KEY, goAppsFtpUrls[k]);
							itemInfo.mIntent = downloadIntent;
							itemInfo.mItemType = IItemType.ITEM_TYPE_SHORTCUT;
							itemInfo.mTitle = mContext.getString(goAppsNameIds[k]);
							try {
								Drawable drawable = ScreenUtils.getGoAppsIcons(mContext, goAppsIconIds[k]);
								itemInfo.mIcon = drawable;
								writeToShortCutTable(itemInfo);
							} catch (OutOfMemoryError e) {
								OutOfMemoryHandler.handle();
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							itemInfo.mIntent = pm.getLaunchIntentForPackage(goAppsPkg[k]);
						}
						installList.add(itemInfo);
					} else if ((PackageName.LOCKER_PACKAGE.equals(goAppsPkg[k])) && GoAppUtils.isGoLockerExist(mContext)) {
						Intent golockerIntent = new Intent(ICustomAction.ACTION_LOCKER);
						List<ResolveInfo> infosList = null;
						infosList = mContext.getPackageManager().queryIntentActivities(
								golockerIntent, 0);
						if (infosList != null && infosList.size() > 0) {
							ResolveInfo resolveInfo = infosList.get(0);
							String pkg = resolveInfo.activityInfo.packageName;
							itemInfo.mIntent = pm.getLaunchIntentForPackage(pkg);
							installList.add(itemInfo);
						}
					} else {
						Intent downloadIntent = new Intent(goAppsActions[k]);
						ComponentName cmpName = new ComponentName(goAppsPkg[k], goAppsPkg[k]);
						downloadIntent.setComponent(cmpName);
						downloadIntent.putExtra(RECOMMAND_APP_FTP_URL_KEY, goAppsFtpUrls[k]);
						itemInfo.mIntent = downloadIntent;
						itemInfo.mItemType = IItemType.ITEM_TYPE_SHORTCUT;
						itemInfo.mTitle = mContext.getString(goAppsNameIds[k]);
						try {
							// 加download Tag
							Drawable drawable = ScreenUtils.getGoAppsIcons(mContext, goAppsIconIds[k]);
							itemInfo.mIcon = drawable;
							writeToShortCutTable(itemInfo);
						} catch (OutOfMemoryError e) {
							OutOfMemoryHandler.handle();
						} catch (Exception e) {
							e.printStackTrace();
						}
						recommandList.add(itemInfo);
					}
				}
			}
			try {
				// 添加未安装的程序
				for (ShortCutInfo recommandItem : recommandList) {
					mDataModel.addItemToFolder(recommandItem, userFolderInfo.mInScreenId);
					if (itemX >= 4) {
						itemX = 0;
						++itemY;
					} else {
						++itemX;
					}
				}

				// 添加已安装的程序
				for (ShortCutInfo installItem : installList) {
					mDataModel.addItemToFolder(installItem, userFolderInfo.mInScreenId);
					if (itemX >= 4) {
						itemX = 0;
						++itemY;
					} else {
						++itemX;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 添加进快捷方式的表里
	 * 
	 * @param shortCutInfo
	 */
	public void writeToShortCutTable(ShortCutInfo shortCutInfo) {
		final SysShortCutControler shortCutControler = AppCore.getInstance()
				.getSysShortCutControler();
		if (null != shortCutControler) {
			// 添加保护，防止空指针的出现
			String itemNameString = shortCutInfo.mTitle == null ? null : shortCutInfo.mTitle
					.toString();
			BitmapDrawable itemIcon = (shortCutInfo.mIcon != null && shortCutInfo.mIcon instanceof BitmapDrawable)
					? (BitmapDrawable) shortCutInfo.mIcon
					: null;

			boolean bRet = shortCutControler.addSysShortCut(shortCutInfo.mIntent, itemNameString,
					itemIcon);
			if (!bRet) {
				Log.i(LOG_TAG, "add system shortcut is fail");
			}
		}
	}
	
	/**
	 * 添加图标到桌面
	 * @param intent		图标的intent
	 * @param itemType		图标的类型————IItemType里面的几种类型
	 * @param columnCount	列数
	 * @param rowCount		行数
	 * @param screenIndex	屏幕下标
	 * @param titleResId	图标的名字resid————如果不是IItemType.ITEM_TYPE_SHORTCUT，则填-1
	 * @param icon			图标icon————如果不是IItemType.ITEM_TYPE_SHORTCUT，则填null
	 */
	private void createItemToDesk(Context context, Intent intent, int itemType, int columnCount,
			int rowCount, int screenIndex, int titleResId, Drawable icon) {
		ShortCutInfo shortCutInfo = new ShortCutInfo();
		shortCutInfo.mIntent = intent;
		shortCutInfo.mInScreenId = System.currentTimeMillis();
		shortCutInfo.mItemType = itemType;
		shortCutInfo.mCellX = columnCount;
		shortCutInfo.mCellY = rowCount;
		shortCutInfo.mSpanX = 1;
		shortCutInfo.mSpanY = 1;
		if (titleResId > 0) {
			// 如果id不为0
			shortCutInfo.mTitle = context.getString(titleResId);
		}
		if (icon != null) {
			// 如果icon不为空
			shortCutInfo.mIcon = icon;
		}
		if (itemType == IItemType.ITEM_TYPE_SHORTCUT) {
			writeToShortCutTable(shortCutInfo);
		}
		mDataModel.addDesktopItem(screenIndex, shortCutInfo);
	}
	
	/**
	 * <br>功能简述:(屏幕格子行数 < 5 && 是主屏 && cellY不是倒数第一行）将跳过这个推荐图标的添加
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param row
	 * @return
	 */
	private boolean skipRecommendedApp(int screen, int cellY) {
		int rowCount = StaticScreenSettingInfo.sScreenRow - 1; // 最后一行
		if (ScreenSettingInfo.DEFAULT_MAIN_SCREEN == screen && rowCount < 4 && cellY < rowCount) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param packageName
	 *            包名
	 * @param className
	 *            启动的acitivity
	 * @param downloadAction
	 *            自定义action
	 * @param location
	 *            摆放在主屏位置
	 * @param titleId
	 *            名称资源
	 * @param iconId
	 *            icon资源
	 */
	private void addDownLoadShortCut(String packageName, String className, String downloadAction,
			int[] location, int titleId, int iconId) {
		if (null == packageName || null == className || null == downloadAction || null == location
				|| titleId <= 0 || iconId <= 0) {
			return;
		}
		ShortCutInfo itemInfo = new ShortCutInfo();
		Intent intent = new Intent(downloadAction);
		ComponentName cmpName = new ComponentName(packageName, className);
		intent.setComponent(cmpName);
		itemInfo.mIntent = intent;
		itemInfo.mItemType = IItemType.ITEM_TYPE_SHORTCUT;
		itemInfo.mTitle = mContext.getString(titleId);
		try {
			itemInfo.mIcon = mContext.getResources().getDrawable(iconId);
			itemInfo.mInScreenId = SystemClock.elapsedRealtime();
			itemInfo.mCellX = location[0];
			itemInfo.mCellY = location[1];
			itemInfo.mSpanX = 1;
			itemInfo.mSpanY = 1;
			writeToShortCutTable(itemInfo);
			mDataModel.addDesktopItem(ScreenSettingInfo.DEFAULT_MAIN_SCREEN, itemInfo);
		} catch (OutOfMemoryError e) {
			OutOfMemoryHandler.handle();
		} catch (Exception e) {
			e.printStackTrace();
		}
		itemInfo = null;
		intent = null;
		cmpName = null;
	}
	
	/**
	 * 
	 * @param packageName 包名
	 * @param className 启动的acitivity
	 * @param downloadAction 自定义action
	 * @param location 摆放在主屏位置
	 * @param titleId 名称资源
	 * @param iconId icon资源
	 * @param screen 添加到的指定屏幕,如果指定屏幕为-1，则默认添加到主屏幕
	 * @param result 是否需要返回结果
	 */
	private ShortCutInfo addDownLoadShortCut(String packageName, String downloadAction,
			int[] location, int titleid, int iconId, int screen, boolean result) {

		if (null == packageName || null == downloadAction || null == location || titleid <= 0
				|| iconId <= 0) {
			return null;
		}
		ShortCutInfo itemInfo = new ShortCutInfo();
		Intent intent = new Intent(downloadAction);
		ComponentName cmpName = new ComponentName(packageName, packageName);
		intent.setComponent(cmpName);
		itemInfo.mIntent = intent;
		itemInfo.mItemType = IItemType.ITEM_TYPE_SHORTCUT;
		itemInfo.mTitle = mContext.getString(titleid);
		try {
			if (packageName.equals(PackageName.CLEAN_MASTER_PACKAGE)) {
				//clean master不需加download标志
				itemInfo.mIcon = mContext.getResources().getDrawable(iconId);
			} else {
				itemInfo.mIcon = ScreenUtils.composeRecommendIcon(mContext, iconId);
			}
			itemInfo.mInScreenId = System.currentTimeMillis();
			itemInfo.mCellX = location[0];
			itemInfo.mCellY = location[1];
			itemInfo.mSpanX = 1;
			itemInfo.mSpanY = 1;
			writeToShortCutTable(itemInfo);
			// 如果指定屏幕为-1，则默认添加到主屏幕
			screen = screen == -1 ? ScreenSettingInfo.DEFAULT_MAIN_SCREEN : screen;
			itemInfo.mScreenIndex = screen;
			mDataModel.addDesktopItem(screen, itemInfo);
		} catch (OutOfMemoryError e) {
			OutOfMemoryHandler.handle();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!result) {
			itemInfo = null;
		}
		intent = null;
		cmpName = null;
		return itemInfo;
	}
	
	private ShortCutInfo addExistAppShortCut(String packageName, int[] location, int screen,
			boolean result) {
		ShortCutInfo shortCutInfo = null;
		final AppDataEngine dataEngine = AppDataEngine.getInstance(ApplicationProxy.getContext());
		ArrayList<AppItemInfo> dbItemInfos = dataEngine.getAllAppItemInfos();
		int dbItemsSize = dbItemInfos.size();
		for (int j = 0; j < dbItemsSize; j++) {
			AppItemInfo dbItemInfo = dbItemInfos.get(j);
			if (null == dbItemInfo.mIntent.getComponent()) {
				continue;
			}
			String dbPackageName = dbItemInfo.mIntent.getComponent().getPackageName();
			if (dbPackageName.equals(packageName)) {
				ShortCutInfo desktopItemInfo = new ShortCutInfo();
				desktopItemInfo.mInScreenId = System.currentTimeMillis();
				desktopItemInfo.mItemType = dbItemInfo.mItemType;
				desktopItemInfo.mCellX = location[0];
				desktopItemInfo.mCellY = location[1];
				desktopItemInfo.mSpanX = 1;
				desktopItemInfo.mSpanY = 1;
				desktopItemInfo.mIntent = dbItemInfo.mIntent;
				try {
					screen = screen == -1 ? ScreenSettingInfo.DEFAULT_MAIN_SCREEN : screen;
					desktopItemInfo.mScreenIndex = screen;
					mDataModel.addDesktopItem(screen, desktopItemInfo);
					if (result) {
						shortCutInfo = desktopItemInfo;
					}
					break; // 添加下一个图标
				} catch (Exception e) {
				}
				break;
			}
		}
		return shortCutInfo;
	}
	
	public LinkedList<ItemInfo> getShortCutLinkList(int currentScreen) {
		LinkedList<ItemInfo> shortCuts = new LinkedList<ItemInfo>();
		if (mScreenSparseArray != null) {
			final int screenCount = mScreenSparseArray.size();
			addDesktopItem(mScreenSparseArray, currentScreen, shortCuts);
			int i = currentScreen - 1, j = currentScreen + 1;
			while (i >= 0 || j < screenCount) {
				addDesktopItem(mScreenSparseArray, i--, shortCuts);
				addDesktopItem(mScreenSparseArray, j++, shortCuts);
			}
		}
		return shortCuts;
	}
	
	private void addDesktopItem(
			SparseArray<ArrayList<ItemInfo>> desktopItems, int screen, LinkedList<ItemInfo> shortCuts) {
		ArrayList<ItemInfo> itemList = desktopItems.get(screen);
		if (itemList != null) {
			final int size = itemList.size();
			for (int i = 0; i < size; i++) {
				final ItemInfo itemInfo = itemList.get(i);
				itemInfo.mScreenIndex = screen;
				shortCuts.addLast(itemInfo);
			}
		}
	}
	
	public void updateAllFolder(boolean reloadContent) {
		if (mScreenSparseArray != null) {
			final int size = mScreenSparseArray.size();
			for (int i = 0; i < size; i++) {
				ArrayList<ItemInfo> itemList = mScreenSparseArray.get(i);
				if (itemList != null) {
					int count = itemList.size();
					for (int j = 0; j < count; j++) {
						final ItemInfo itemInfo = itemList.get(j);
						if (itemInfo != null && itemInfo instanceof UserFolderInfo) {
							mScreenObserver.updateFolderIconAsync((UserFolderInfo) itemInfo,
									reloadContent, false);
						} // end if2
					} // end for2
				} // end if1
			} // end for1
		}
	}
	
	SparseArray<ArrayList<ItemInfo>> getScreenSparseArray() {
		return mScreenSparseArray;
	}
	
	public boolean prepareItemInfo(ItemInfo info) {
		boolean bRet = false;
		if (null == info) {
			return bRet;
		}
		try {
			// 关联
			// 图标、名称
			switch (info.mItemType) {
				case IItemType.ITEM_TYPE_APPLICATION : {
					ShortCutInfo sInfo = (ShortCutInfo) info;
					if (null == sInfo.getRelativeItemInfo()) {
						bRet |= sInfo.setRelativeItemInfo(getAppItemInfo(sInfo.mIntent, null,
								sInfo.mItemType));
					} else if (sInfo.getRelativeItemInfo() instanceof SelfAppItemInfo) {
						AppItemInfo appItemInfo = getAppItemInfo(sInfo.mIntent, null,
								sInfo.mItemType);
						if (!(appItemInfo instanceof SelfAppItemInfo)) {
							bRet |= sInfo.setRelativeItemInfo(appItemInfo);
						}
					}
					if (null == sInfo.getFeatureIcon()) {
						bRet |= sInfo.prepareFeatureIcon();
					}

					// 数据冗余导致
					if (null != sInfo.getFeatureIcon()) {
						int isAddBottom = sInfo.mIntent.getIntExtra(
								AdvertConstants.ADVERT_IS_ADD_BOTTOM, 0);
						// 这里的处理是由于1，5屏的图标需要根据主题添加底座，而1，5屏的图标为自定义图标的APPLICATION
						if (isAddBottom == 0) { // 图标不需要加底座
							sInfo.mIcon = sInfo.getFeatureIcon();
						} else { // 图标需要加底座
							sInfo.mIcon = ScreenUtils
									.composeCustomIconBack(sInfo
											.getFeatureIcon());
						}
//						sInfo.mIcon = sInfo.getFeatureIcon();
						sInfo.mIsUserIcon = true;
					} else {
						if (null != sInfo.getRelativeItemInfo()) {
							sInfo.mIcon = sInfo.getRelativeItemInfo().getIcon();
						}
						sInfo.mIsUserIcon = false;
					}
					if (null != sInfo.getFeatureTitle()) {
						sInfo.mTitle = sInfo.getFeatureTitle();
						sInfo.mIsUserTitle = true;
					} else {
						if (null != sInfo.getRelativeItemInfo()) {
							sInfo.mTitle = sInfo.getRelativeItemInfo().getTitle();
						}
						sInfo.mIsUserTitle = false;
					}
				}
					break;

				case IItemType.ITEM_TYPE_SHORTCUT : {
					ShortCutInfo sInfo = (ShortCutInfo) info;
					if (null == sInfo.getRelativeItemInfo()) {
						bRet |= sInfo.setRelativeItemInfo(getAppItemInfo(sInfo.mIntent, null,
								sInfo.mItemType));
					}
					if (null == sInfo.getFeatureIcon()) {
						bRet |= sInfo.prepareFeatureIcon();
					}

					// 数据冗余导致
					if (null != sInfo.getFeatureIcon()) {
						sInfo.mIcon = sInfo.getFeatureIcon();
						sInfo.mIsUserIcon = true;
					} else {
						if (null != sInfo.getRelativeItemInfo()) {
							sInfo.mIcon = sInfo.getRelativeItemInfo().getIcon();
						}
						sInfo.mIsUserIcon = false;
					}
					if (null != sInfo.getFeatureTitle()) {
						sInfo.mTitle = sInfo.getFeatureTitle();
						sInfo.mIsUserTitle = true;
					} else {
						if (null != sInfo.getRelativeItemInfo()) {
							//ADT-10241	英文下添加通知栏及状态栏两个快捷方式到桌面，菜单名称显示不中文
							if (sInfo.mTitle == null) {
								sInfo.mTitle = sInfo.getRelativeItemInfo().getTitle();
							}
						}
						sInfo.mIsUserTitle = false;
					}
				}
					break;

				case IItemType.ITEM_TYPE_USER_FOLDER : {
					UserFolderInfo fInfo = (UserFolderInfo) info;
					if (null == fInfo.getRelativeItemInfo()) {
						bRet |= fInfo.setRelativeItemInfo(getAppItemInfo(null, null,
								fInfo.mItemType));
					}
					if (null == fInfo.getFeatureIcon()) {
						bRet |= fInfo.prepareFeatureIcon();
					}

					// 数据冗余导致
					if (null != fInfo.getFeatureIcon()) {
						fInfo.mIcon = fInfo.getFeatureIcon();
						fInfo.mIsUserIcon = true;
					} else {
						if (null != fInfo.getRelativeItemInfo()) {
							fInfo.mIcon = fInfo.getRelativeItemInfo().getIcon();
						}
						fInfo.mIsUserIcon = false;
					}
					if (null != fInfo.getFeatureTitle()) {
						fInfo.mTitle = fInfo.getFeatureTitle();
						// fInfo.mIsUserTitle = true;
					} else {
						if (null != fInfo.getRelativeItemInfo()) {
							fInfo.mTitle = fInfo.getRelativeItemInfo().getTitle();
						}
						// fInfo.mIsUserTitle = false;
					}
				}
					break;

				default :
					break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bRet;
	}
	
	private AppItemInfo getAppItemInfo(Intent intent, Uri uri, int type) {
		AppItemInfo info = null;
		switch (type) {
			case IItemType.ITEM_TYPE_APPLICATION : {
				AppDataEngine appEngine = AppDataEngine.getInstance(ApplicationProxy.getContext());
				if (appEngine != null) {
					info = appEngine.getAppItem(intent);
				}
			}
				break;
			case IItemType.ITEM_TYPE_SHORTCUT : {
				DockItemControler dockItemControler = AppCore.getInstance().getDockItemControler();
				if (null != dockItemControler) {
					info = dockItemControler.getDockAppItemInfo(intent);
				}
				if (null == info) {
					SysShortCutControler shortcutEngine = AppCore.getInstance()
							.getSysShortCutControler();
					if (shortcutEngine != null) {
						info = shortcutEngine.getSysShortCutItemInfo(intent);
					}
				}
			}
				break;
			case IItemType.ITEM_TYPE_USER_FOLDER : {
				SelfAppItemInfoControler selfAppEngine = AppCore.getInstance()
						.getSelfAppItemInfoControler();
				if (selfAppEngine != null) {
					info = selfAppEngine.getUserFolder();
				}
			}
				break;

			default :
				break;
		}

		if (null == info) {
//			final AppDataEngine dataEngine = AppDataEngine.getInstance(ApplicationProxy
//					.getContext());
//			info = dataEngine.getRecommendAppItemInfo(intent);
//			if (info == null) {
				SelfAppItemInfoControler selfAppEngine = AppCore.getInstance()
						.getSelfAppItemInfoControler();
				if (selfAppEngine != null) {
					info = selfAppEngine.getDefaultApplication();
				}
//			}
		}
		return info;
	}
	

	public boolean prepareItemInfo(UserFolderInfo folderInfo) {
		boolean bRet = false;
		final int count = folderInfo.getChildCount();
		for (int i = 0; i < count; i++) {
			ShortCutInfo item = folderInfo.getChildInfo(i);
			if (null != item) {
				if (prepareItemInfo(item)) {
					bRet = true;
				}
			}
		}
		return bRet;
	}

	public boolean prepareItemInfo(ArrayList<ItemInfo> itemArray) {
		boolean bRet = false;
		int sz = itemArray.size();
		for (int i = 0; i < sz; i++) {
			if (prepareItemInfo(itemArray.get(i))) {
				bRet = true;
			}
		}
		return bRet;
	}

}
