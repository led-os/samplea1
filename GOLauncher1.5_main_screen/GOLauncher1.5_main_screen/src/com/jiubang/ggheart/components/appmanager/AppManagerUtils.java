package com.jiubang.ggheart.components.appmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.FavoriteInfo;
import com.jiubang.ggheart.data.info.IItemType;
import com.jiubang.ggheart.data.info.ItemInfo;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.data.info.UserFolderInfo;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.PackageName;


/**
 * 
 * <br>类描述:清理屏幕工具类
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2013-5-8]
 */
public class AppManagerUtils {
	public static final int TYPE_FARTHER_TITLE = 1;
	public static final int TYPE_NO_RESULT = 2;
	public static final int TYPE_FAVOURITE = 3;
	public static final int TYPE_DESK_SHORTCUT = 4;
	public static final int TYPE_FOLDER_SHORTCUT = 5;
	
	public static final int TYPE_UNINSTALL_APP = 6;
	public static final int TYPE_UPDATE_APP = 7;
	
	public static final long TIME_7_DATE = 24 * 60 * 60 * 1000 * 7; // 7天
	
	public static void scanCleanList(Context context, List sourceList, List sacnList) {
		if (sourceList == null || sacnList == null) {
			return;
		}

		if (sacnList.size() != 2) {
			return;
		}

		ArrayList<CleanScreenInfo> mRecommendList = (ArrayList<CleanScreenInfo>) sacnList.get(0); //推荐图标队列 
		ArrayList<CleanScreenInfo> mAutoShortCutList = (ArrayList<CleanScreenInfo>) sacnList.get(1); //自动添加的快捷方式图标

		ArrayList<CleanScreenInfo> mFavoriteList = new ArrayList<CleanScreenInfo>();
		ArrayList<CleanScreenInfo> mShortCutList = new ArrayList<CleanScreenInfo>();

		int sourceCount = sourceList.size();
		for (int i = 0; i < sourceCount; i++) {
			Object object = sourceList.get(i);

			//推荐widget
			if (object != null && object instanceof FavoriteInfo) {
				FavoriteInfo favoriteInfo = (FavoriteInfo) object;
				CleanScreenInfo cleanScreenInfo = new CleanScreenInfo();
				cleanScreenInfo.mItemInfo = favoriteInfo;
				cleanScreenInfo.mType = TYPE_FAVOURITE;
				mFavoriteList.add(cleanScreenInfo);
			}
			//推荐图标
			else if (object != null && object instanceof ShortCutInfo) {
				ShortCutInfo shortCutInfo = (ShortCutInfo) object;
				//判断是快捷方式 和 action是推荐图标的下载action
				if ((shortCutInfo.mItemType == IItemType.ITEM_TYPE_SHORTCUT && shortCutInfo.mIntent
						.getAction().startsWith("com.jiubang.intent.action.DOWNLOAD_"))
						|| (shortCutInfo.mItemType == IItemType.ITEM_TYPE_APPLICATION && shortCutInfo.mIntent
								.getAction().equals(ICustomAction.ACTION_SCREEN_ADVERT))) {

					CleanScreenInfo cleanScreenInfo = new CleanScreenInfo();
					cleanScreenInfo.mItemInfo = shortCutInfo;
					cleanScreenInfo.mType = TYPE_DESK_SHORTCUT;
					mShortCutList.add(cleanScreenInfo);
				}

				//自动添加的快捷方式图标
				else if (shortCutInfo.mItemType == IItemType.ITEM_TYPE_SHORTCUT
						&& checkIconIsAutoShortCut(context, shortCutInfo.mInScreenId)) {
					CleanScreenInfo cleanScreenInfo = new CleanScreenInfo();
					cleanScreenInfo.mItemInfo = shortCutInfo;
					cleanScreenInfo.mType = TYPE_DESK_SHORTCUT;
					mAutoShortCutList.add(cleanScreenInfo);
				}
			}

			//文件夹
			else if (object != null && object instanceof UserFolderInfo) {
				if (object != null && object instanceof UserFolderInfo) {
					UserFolderInfo folderInfo = (UserFolderInfo) object;
					ArrayList<ItemInfo> folderItemIconList = folderInfo.getContents();	//当前文件夹列表
					int folderSize = folderItemIconList.size();
					for (int k = 0; k < folderSize; k++) {
						ShortCutInfo shortCutInfo = (ShortCutInfo) folderItemIconList.get(k);
						//判断是快捷方式 和 action是推荐图标的下载action
						if ((shortCutInfo.mItemType == IItemType.ITEM_TYPE_SHORTCUT && shortCutInfo.mIntent
								.getAction().startsWith("com.jiubang.intent.action.DOWNLOAD_"))
								|| (shortCutInfo.mItemType == IItemType.ITEM_TYPE_APPLICATION && shortCutInfo.mIntent
										.getAction().equals(ICustomAction.ACTION_SCREEN_ADVERT))) {
							CleanScreenInfo cleanScreenInfo = new CleanScreenInfo();
							cleanScreenInfo.mItemInfo = shortCutInfo;
							cleanScreenInfo.mType = TYPE_FOLDER_SHORTCUT;
							cleanScreenInfo.mFolderId = folderInfo.mInScreenId;
							mShortCutList.add(cleanScreenInfo);
						}

						else if (shortCutInfo.mItemType == IItemType.ITEM_TYPE_SHORTCUT
								&& checkIconIsAutoShortCut(context, shortCutInfo.mInScreenId)) {
							CleanScreenInfo cleanScreenInfo = new CleanScreenInfo();
							cleanScreenInfo.mItemInfo = shortCutInfo;
							cleanScreenInfo.mType = TYPE_FOLDER_SHORTCUT;
							cleanScreenInfo.mFolderId = folderInfo.mInScreenId;
							mShortCutList.add(cleanScreenInfo);
						}
					}
				}
			}
		}
		mRecommendList.addAll(mFavoriteList);
		mRecommendList.addAll(mShortCutList);
		
		mFavoriteList.clear();
		mFavoriteList = null;
		mShortCutList.clear();
		mShortCutList = null;
	}
	
	/**
	 * <br>功能简述:添加系统自动生产的快捷方式到缓存
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param shortCutId
	 */
	public static void addShortCutIdToDB(Context context, long shortCutId) {
			PreferencesManager preferencesManager = new PreferencesManager(context,
					IPreferencesIds.CLEAN_SCREEN_AUTO_ADD_SHORTCUT_FILE, Context.MODE_WORLD_READABLE);
			String cacheDataString = preferencesManager.getString(IPreferencesIds.CLEAN_SCREEN_AUTO_ADD_KEY, "");
			cacheDataString = cacheDataString + String.valueOf(shortCutId) + ";";
			preferencesManager.putString(IPreferencesIds.CLEAN_SCREEN_AUTO_ADD_KEY, cacheDataString);
			preferencesManager.commit();
	}
	
	/**
	 * <br>功能简述:检查是否系统自动生产的快捷方式
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param shortCutId
	 * @return
	 */
	public static boolean checkIconIsAutoShortCut(Context context, long shortCutId) {
		PreferencesManager preferencesManager = new PreferencesManager(context,
				IPreferencesIds.CLEAN_SCREEN_AUTO_ADD_SHORTCUT_FILE, Context.MODE_WORLD_READABLE);
		String cacheDataString = preferencesManager.getString(IPreferencesIds.CLEAN_SCREEN_AUTO_ADD_KEY, "");
		if (cacheDataString.contains(String.valueOf(shortCutId) + ";")) {
			preferencesManager = null;
			return true;
		}
		preferencesManager = null;
		return false;
	}
	
	
	
	/**
	 * <br>功能简述:添加系统自动生产的快捷方式到缓存
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param shortCutId
	 */
	public static void saveAppOpenTime(Context context, Intent intent) {
		try {
			if (context == null || intent == null || intent.getComponent() == null) {
				return;
			}
			String packageName = intent.getComponent().getPackageName();
			if (TextUtils.isEmpty(packageName)) {
				return;
			}
			
			PreferencesManager preferencesManager = new PreferencesManager(context,
					IPreferencesIds.CLEAN_SCREEN_APP_OPEN_TIME_FILE, Context.MODE_WORLD_READABLE);
			
			long time = System.currentTimeMillis();
			preferencesManager.putString(packageName, String.valueOf(time));
			preferencesManager.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取应用在运行时,占用的内存Pss值
	 * @param activityManager
	 * @param packageManager
	 * @param processInfos
	 * @param pkgName
	 * @return
	 */
//	private static long getRunningAppMemPSS(ActivityManager activityManager, PackageManager packageManager,
//			List<RunningAppProcessInfo> processInfos, String pkgName) {
//		
//		if (processInfos == null || pkgName == null) {
//			return -1;
//		}
//		
//		for (RunningAppProcessInfo info : processInfos) {
//			
//			String[] pkgs = packageManager.getPackagesForUid(info.uid);
//			
//			for (int i = 0; i < pkgs.length; i++) {
//				if (pkgs[i].equals(pkgName)) {
//					int pids[] = new int[]{info.pid};
//					MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(pids);
//					return memoryInfo[0].getTotalPrivateDirty() * 1024;
//				}
//			}
//		}
//		
//		return -1;
//	}
	
	/**
	 * 获取apk安装包的大小
	 * @param packageManager
	 * @param pkgName
	 * @return
	 */
	private static long getApkSize(PackageManager packageManager, String pkgName) {
		if (pkgName == null) {
			return 0;
		}
		
		try {
			ApplicationInfo applicationInfo = packageManager.getApplicationInfo(pkgName, 0);
			if (applicationInfo != null && applicationInfo.sourceDir != null) {
				return new File(applicationInfo.sourceDir).length();
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/**
	* 判断包名所对应的应用是否安装在SD卡上
	* @param packageName
	* @return, true if install on SD card 
	*/
	public static boolean isInstallOnSDCard(PackageManager packageManager, String packageName) {
		ApplicationInfo appInfo;
		try {
			appInfo = packageManager.getApplicationInfo(packageName, 0);

			if ((appInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
				return true;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	/**
	 * <br>功能简述:获取一周打开和没打开的列表
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param intent
	 * @return
	 */
	public static void getWeekOpenAppList(Context context, Activity activity, ArrayList<ArrayList<UninstallAppInfo>> list) {
		
		if (list == null || list.size() == 0) {
			return;
		}
		
//		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager packageManager = activity.getPackageManager();
		
//		List<RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
		ArrayList<UninstallAppInfo> noUsedList = list.get(0);
		ArrayList<UninstallAppInfo> usedList = list.get(1);
		
		try {
//			PreferencesManager preferencesManager = new PreferencesManager(context,
//					IPreferencesIds.CLEAN_SCREEN_APP_OPEN_TIME_FILE, Context.MODE_WORLD_READABLE);
//			Map<String, ?> allAppOpenCache = preferencesManager.getAll();
				ArrayList<AppItemInfo> allAppItemInfos = AppDataEngine.getInstance(context).getAllAppItemInfos();
				if (allAppItemInfos == null) {
					return;
				}

				for (AppItemInfo appItemInfo : allAppItemInfos) {
					//如果是系统程序，或者是桌面自定义程序，去掉
					if (appItemInfo.getIsSysApp() || appItemInfo.mIntent == null || appItemInfo.mIntent.getComponent() == null) {
						continue;
					}
					
					//去掉假图标包名 和桌面名称
					String packageName = appItemInfo.mIntent.getComponent().getPackageName();
					if (TextUtils.isEmpty(packageName) || packageName.equals(PackageName.GO_STORE_PACKAGE_NAME)
							|| packageName.equals(PackageName.GO_THEME_PACKAGE_NAME)
							|| packageName.equals(PackageName.GO_WIDGET_PACKAGE_NAME)
							|| packageName.equals(PackageName.RECOMMAND_CENTER_PACKAGE_NAME)
							|| packageName.equals(PackageName.GAME_CENTER_PACKAGE_NAME)
							|| packageName.equals(PackageName.FREE_THEME_PACKAGE_NAME)
							|| packageName.equals(context.getPackageName())) {
						continue;
					}
					
					long timeCache = appItemInfo.getClickTime(activity);
					if (timeCache == 0) {
						UninstallAppInfo uninstallAppInfo = new UninstallAppInfo();
						uninstallAppInfo.mAppItemInfo = appItemInfo;
//						uninstallAppInfo.mAppItemInfo.mMemoryPss = getRunningAppMemPSS(activityManager, 
//								packageManager, processInfos, packageName);
						uninstallAppInfo.mAppItemInfo.mPackageSize = getApkSize(packageManager, packageName);
						uninstallAppInfo.mAppItemInfo.mIsInstallOnSDCard = isInstallOnSDCard(packageManager, packageName);
						noUsedList.add(uninstallAppInfo);
						continue;
					}
					
					long curTime = System.currentTimeMillis();
					UninstallAppInfo uninstallAppInfo = new UninstallAppInfo();
					uninstallAppInfo.mAppItemInfo = appItemInfo;
					uninstallAppInfo.mClickTime = timeCache;
//					uninstallAppInfo.mAppItemInfo.mMemoryPss = getRunningAppMemPSS(activityManager, 
//							packageManager, processInfos, packageName);
					
					uninstallAppInfo.mAppItemInfo.mPackageSize = getApkSize(packageManager, packageName);
					if ((curTime - timeCache) >= TIME_7_DATE) {
						noUsedList.add(uninstallAppInfo);
					} else {
						usedList.add(uninstallAppInfo);
					}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	public static List<AppItemInfo> lessUsedApp(Context context) {
		
		ArrayList<AppItemInfo> result = null;
//		PreferencesManager preferencesManager = new PreferencesManager(context, 
//				IPreferencesIds.CLEAN_SCREEN_APP_OPEN_TIME_FILE, Context.MODE_WORLD_READABLE);
//		Map<String, ?> allAppOpenCache = preferencesManager.getAll();
		
			ArrayList<AppItemInfo> allAppItemInfos = AppDataEngine.getInstance(context).getAllAppItemInfos();
			if (allAppItemInfos == null) {
				return null;
			}
			
			result = new ArrayList<AppItemInfo>();
			
			for (AppItemInfo appItemInfo : allAppItemInfos) {
				//如果是系统程序，或者是桌面自定义程序，去掉
				if (appItemInfo.getIsSysApp() || appItemInfo.mIntent == null || appItemInfo.mIntent.getComponent() == null) {
					continue;
				}
				
				//去掉假图标包名 和桌面名称
				String packageName = appItemInfo.mIntent.getComponent().getPackageName();
				if (TextUtils.isEmpty(packageName) || packageName.equals(PackageName.GO_STORE_PACKAGE_NAME)
						|| packageName.equals(PackageName.GO_THEME_PACKAGE_NAME)
						|| packageName.equals(PackageName.GO_WIDGET_PACKAGE_NAME)
						|| packageName.equals(PackageName.RECOMMAND_CENTER_PACKAGE_NAME)
						|| packageName.equals(PackageName.GAME_CENTER_PACKAGE_NAME)
						|| packageName.equals(PackageName.FREE_THEME_PACKAGE_NAME)
						|| packageName.equals(context.getPackageName())) {
					continue;
				}
				
				long timeCache = 
				appItemInfo.getClickTime(context);
				if (timeCache == 0) {
					result.add(appItemInfo);
					continue;
				}
				
				long curTime = System.currentTimeMillis();
				if ((curTime - timeCache) >= TIME_7_DATE) {
					result.add(appItemInfo);
				} 
		}
		
		return result;
	}
	
	/**
	 * <br>功能简述:获取sd卡空闲
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static long getSdAvailaleSize() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return (availableBlocks * blockSize) / 1024 / 1024;  //MIB单位
		} else {
			return 0;
		}
	}
	
	/**
	 * <br>功能简述:获取sd卡已用大小
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static long getSdUseSize() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long blockCount = stat.getBlockCount();
			long availableBlocks = stat.getAvailableBlocks();
			return ((blockCount - availableBlocks) * blockSize) / 1024 / 1024;
		} else {
			return 0;
		}
	}
	
	/**
	 * <br>功能简述:获取sd卡总大小
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static long getSdAllSize() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getBlockCount();
			return (availableBlocks * blockSize) / 1024 / 1024;
		} else {
			return 0;
		}
	}
	
	public static void uninstallAPK(String packageName, Context context) {  
        Uri uri = Uri.parse("package:" + packageName);  
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);  
        context.startActivity(intent);  
    }  
}


