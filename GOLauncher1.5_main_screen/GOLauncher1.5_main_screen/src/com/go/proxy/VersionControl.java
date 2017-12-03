package com.go.proxy;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.go.util.AppUtils;
import com.go.util.ConvertUtils;
import com.go.util.device.Machine;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.HttpUtil;
import com.jiubang.ggheart.data.DataProvider;
import com.jiubang.ggheart.data.tables.SettingTable;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 
 * @author liuheng
 *
 */
public class VersionControl {

	public static final int VERSION_APPFUNC_SEARCH_SUPPORT_MEDIA = 133; //应用搜索支持多媒体搜索版本号
	public static final int VERSION_MEDIA_MANAGEMENT_BECOMES_PLUGIN = 158; //资源管理独立为插件版本号，暂未确定
	public static final int VERSION_SIDEBAR_SETTING_SUPPORT = 266;

	private boolean mHadPayFlag = false; // 是否已经付费


	private static boolean sFirstRun;
	private static int sLastVersionCode;
	private static int sCurrentVersionCode;
	private static boolean sNewVersionFirstRun;
	
	public static int getLastVersionCode() {
		return sLastVersionCode;
	}

	public static boolean getNewVeriosnFirstRun() {
		return sNewVersionFirstRun;
	}
	
	public static int getCurrentVersionCode() {
		return sCurrentVersionCode;
	}
	
	/**
	 * 检测是否为第一次运行GO桌面
	 */
	public static void checkFirstRun() {
		Context context = ApplicationProxy.getContext();
		DataProvider dataProvider = DataProvider.getInstance(context);
		boolean firstRun = dataProvider.getFirstRunValue();
		if (firstRun) {
		    
			// 记录用户安装后第一次运行的时间，也有可能是恢复默认数据后第一次运行的时间
			saveInstallTimeFirstRunSharedPreferences();
			// 记录是否是新用户，也有可能是恢复默认数据的用户
			saveIsNewUserPref(true);
			// 删除用来保存消息中心时间戳的文件(主要针对恢复默认和卸载再安装处理) added by liulixia
			HttpUtil.deleteLastUpdateTimeFromSD();
			// 更新数据库里面第一次运行的字段值
			ContentValues values = new ContentValues();
			values.put(SettingTable.FIRSTRUN, ConvertUtils.boolean2int(false));
			dataProvider.updateFirstRunValue(values);
			
//			Machine.initRandomAd(context);
//			if (Machine.isLimitFreeDate()) {
//				Machine.validLimitFreeInstall(context);
//			}
			// Modify begin rongjinsong sharedPreferences操作涉及IO操作且有锁放到线程中防止阻塞UI
			new Thread() {
				@Override
				public void run() {
					super.run();
					//检查安装时间
					Context c = ApplicationProxy.getContext();
					Machine.checkInstallStamp(c);
				}

			}.start();
			//Modify end
			
			// 桌面第一次运行，记录一下是不是clean master比桌面早存在于用户设备 
			// 安装GO桌面时，CM已安装，则推荐安卓优化大师
			// 安装GO桌面时，CM未安装，则推荐CM
			PreferencesManager pre = new PreferencesManager(context);
			if (AppUtils.isAppExist(context, PackageName.CLEAN_MASTER_PACKAGE)) {
				pre.putBoolean(IPreferencesIds.PREFERENCE_IS_RECOMMEND_DUSPEEDBOOSTER, true);
			} else {
				pre.putBoolean(IPreferencesIds.PREFERENCE_IS_RECOMMEND_DUSPEEDBOOSTER, false);
			}
			pre.commit();
		}

		sFirstRun = firstRun;
	}

	
	/**
     * <br>功能简述:对具体老用户国家推荐遨游统计的点击数进行清零处理。
     * <br>功能详细描述:
     * <br>注意:
     */
    private static void checkClearBrowserCountData() {
        String curCountry = Machine.getCountry(ApplicationProxy.getContext());
        boolean isNewUser = VersionControl.isNewUser();

        PreferencesManager ps = new PreferencesManager(
                ApplicationProxy.getContext());
        boolean isNeedDisplay = ps.getBoolean(
                IPreferencesIds.PREFERENCES_HAD_OPEN_BROWSER, true);
        int openCount = ps.getInt(
                IPreferencesIds.PREFERENCES_OPEN_DOCK_BROWSER_COUNT, 0);

        if (!isNewUser && Machine.isMathonCountory(curCountry) && isNeedDisplay
                && VersionControl.getLastVersionCode() <= 313 && openCount >= 2) {
            ps.putInt(IPreferencesIds.PREFERENCES_OPEN_DOCK_BROWSER_COUNT, 0);
            ps.commit();
        }
    }
	
	public static boolean getFirstRun() {
		return sFirstRun;
	}

	/**
	 * <br>功能简述:检查是否是该版本第一次运行
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 */
	public static void checkNewVersionFirstRun() {
		Context context = ApplicationProxy.getContext();
		boolean newVersion = false;
		//
		// final String preferenceString = getString(R.string.curVersion);
		// final String storeString =
		// DataProvider.getInstance(this).getShowTipFrameCurVersion();
		// if (!preferenceString.equals(storeString))
		// {
		// DataProvider.getInstance(this).saveShowTipFrame(preferenceString);
		//
		// newVersion = true;
		// }

		// 从数据库取出上次版本号
		sLastVersionCode = DataProvider.getInstance(context).getVersionCode();
		PackageManager pm = context.getPackageManager();
		PackageInfo info;
		try {
			info = pm.getPackageInfo(context.getPackageName(), 0);
			// 取出当前版本号
			sCurrentVersionCode = info.versionCode;
			// 更新数据库上次版本号的字段
			DataProvider.getInstance(context).saveVersionCode(info.versionCode);
			if (info.versionCode != sLastVersionCode) {
			    
			    //对推荐遨游具体老用户国家进行统计点击数清零处理
	            checkClearBrowserCountData();
			    
				// 如果上次版本号与当前版本号不相等，证明是该版本第一次运行
				if (sLastVersionCode > 0) {
					// 如果上次版本号大于0，证明该用户是升级用户，如果是全新用户，上次版本号为0
					// 这里保存该用户不是新用户，因为上次版本号不是0，已经运行过了
					saveIsNewUserPref(false);
					// 保存3D引擎上个版本的选择状态
					saveLastEngineSelected();
				}
				
				newVersion = true;
				Context c = ApplicationProxy.getContext();
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sNewVersionFirstRun = newVersion;
	}
	
	//是否是全新用户
	public static boolean isNewUser() {
		Context context = ApplicationProxy.getContext();
		PreferencesManager sharedPreferences = new PreferencesManager(context,
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(IPreferencesIds.IS_NEW_USER, false);
	}
	
	/**
	 * 是否为应用搜索支持多媒体搜索的版本
	 */
	public static boolean isAppFuncSearchSupportMediaEdition() {
		return getVersionCode() >= VERSION_APPFUNC_SEARCH_SUPPORT_MEDIA;
	}

	/**
	 * 升级前的版本是否为应用搜索不支持多媒体搜索的版本
	 */
	public static boolean isUpgradeFromAppFuncSearchUnsupportMediaEdition() {
		return sLastVersionCode < VERSION_APPFUNC_SEARCH_SUPPORT_MEDIA;
	}

	/**
	 * 是否为资源管理支持插件化的版本
	 */
	public static boolean isMdiamanagementBecomesPlugin() {
		return getVersionCode() >= VERSION_MEDIA_MANAGEMENT_BECOMES_PLUGIN;
	}

	/**
	 * 升级前的版本是否为资源管理不支持插件化的版本
	 */
	public static boolean isUpgradeFromMdiamanagementUnbecomesPlugin() {
		return sLastVersionCode < VERSION_MEDIA_MANAGEMENT_BECOMES_PLUGIN;
	}
	
	//记录当前用户是否是全新用户
	private static void saveIsNewUserPref(boolean isNewUser) {
		Context context = ApplicationProxy.getContext();
		PreferencesManager sharedPreferences = new PreferencesManager(context,
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
		sharedPreferences.putBoolean(IPreferencesIds.IS_NEW_USER, isNewUser);
		sharedPreferences.commit();
	}
	
	//保存上一个版本的3D引擎是否打开
	private static void saveLastEngineSelected() {
		Context context = ApplicationProxy.getContext();
		PreferencesManager sharedPreferences = new PreferencesManager(context,
				IPreferencesIds.PREFERENCE_ENGINE, Context.MODE_PRIVATE);
		boolean lastEngineSelected = sharedPreferences.getBoolean(
				IPreferencesIds.PREFERENCE_ENGINE_SELECTED, false);
		
		sharedPreferences.putBoolean(
				IPreferencesIds.PREFERENCE_LAST_ENGINE_SELECTED, lastEngineSelected);
		
//		Log.d("3Dcore", "saveLastEngineSelected lastEngineSelected= " + lastEngineSelected);
		sharedPreferences.commit();
	}

	public boolean getHadPayFlag() {
		return mHadPayFlag;
	}

	public boolean setHadPayFlag(boolean hadPayFlag) {
		if (!this.mHadPayFlag && hadPayFlag) {
			this.mHadPayFlag = hadPayFlag;
			return true;
		}
		return false;
	}

	/**
	 * 获取当前GO桌面版本号
	 */
	public static int getVersionCode() {
		try {
			Context c = ApplicationProxy.getContext();
			PackageInfo info = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	private static void saveInstallTimeFirstRunSharedPreferences() {
		Context context = ApplicationProxy.getContext();
		PreferencesManager prefs = new PreferencesManager(context,
				IPreferencesIds.USERTUTORIALCONFIG, Context.MODE_PRIVATE);
		prefs.putLong(IPreferencesIds.LAUNCHER_INSTALL_OR_SETDEFAULT_TIME, System.currentTimeMillis());
		prefs.commit();
	}
}
