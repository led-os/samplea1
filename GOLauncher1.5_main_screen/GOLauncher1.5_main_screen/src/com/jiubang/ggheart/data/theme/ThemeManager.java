package com.jiubang.ggheart.data.theme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.gau.utils.cache.encrypt.CryptTool;
import com.gau.utils.cache.utils.CacheFileUtils;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.DataChangeListenerProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.util.AppUtils;
import com.go.util.BroadCaster.BroadCasterObserver;
import com.go.util.SortUtils;
import com.go.util.StringUtils;
import com.go.util.device.Machine;
import com.go.util.file.FileUtil;
import com.go.util.market.MarketConstant;
import com.go.util.window.WindowControl;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.core.framework.ICleanable;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.ConstValue;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageFilterBean;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeConstants;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemePurchaseManager;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeVipPage;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStoreOperatorUtil;
import com.jiubang.ggheart.billing.PurchaseStateManager;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.data.DataProvider;
import com.jiubang.ggheart.data.Setting;
import com.jiubang.ggheart.data.ContentProvider.GoContentProvider;
import com.jiubang.ggheart.data.info.ScreenStyleConfigInfo;
import com.jiubang.ggheart.data.model.GoContentProviderUtil;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.data.statistics.StatisticsAppsInfoData;
import com.jiubang.ggheart.data.tables.ConfigTable;
import com.jiubang.ggheart.data.theme.bean.AppDataThemeBean;
import com.jiubang.ggheart.data.theme.bean.DeskFolderThemeBean;
import com.jiubang.ggheart.data.theme.bean.DeskThemeBean;
import com.jiubang.ggheart.data.theme.bean.DeskThemeBean.DockBean;
import com.jiubang.ggheart.data.theme.bean.DeskThemeBean.MenuBean;
import com.jiubang.ggheart.data.theme.bean.DodolThemeResourceBean;
import com.jiubang.ggheart.data.theme.bean.DrawResourceThemeBean;
import com.jiubang.ggheart.data.theme.bean.ThemeBannerBean;
import com.jiubang.ggheart.data.theme.bean.ThemeBean;
import com.jiubang.ggheart.data.theme.bean.ThemeInfoBean;
import com.jiubang.ggheart.data.theme.bean.ThemeNotifyBean;
import com.jiubang.ggheart.data.theme.broadcastReceiver.MyThemeReceiver;
import com.jiubang.ggheart.data.theme.parser.AppThemeParser;
import com.jiubang.ggheart.data.theme.parser.AtomThemeIconParser;
import com.jiubang.ggheart.data.theme.parser.AtomThemeWallpaperParser;
import com.jiubang.ggheart.data.theme.parser.DeskFolderThemeParser;
import com.jiubang.ggheart.data.theme.parser.DeskThemeParser;
import com.jiubang.ggheart.data.theme.parser.DodolThemeIconParser;
import com.jiubang.ggheart.data.theme.parser.DodolThemeResourceParser;
import com.jiubang.ggheart.data.theme.parser.DrawResourceThemeParser;
import com.jiubang.ggheart.data.theme.parser.FuncThemeParser;
import com.jiubang.ggheart.data.theme.parser.IParser;
import com.jiubang.ggheart.data.theme.parser.ThemeInfoParser;
import com.jiubang.ggheart.data.theme.zip.ZipResources;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.ThreadName;

/**
 * 
 * <br>类描述: 主题管理类
 * <br>功能详细描述:
 * 
 * @author  
 * @date  [2012-9-11]
 */
public class ThemeManager implements ICleanable {

	public static final boolean SDEBUG = false;

	public static final String XMLFILE = LauncherEnv.Path.GOTHEMES_PATH + "paidthemes.xml"; // 配置文件名
	public static final String HOT_XMLFILE = LauncherEnv.Path.GOTHEMES_PATH + "hottheme.xml"; //热门配置文件名
	public static final String ICONPATH = LauncherEnv.Path.GOTHEMES_PATH + "icon/"; // 图片保存路径
	public static final String FEATURED_THEME_DETAIL_CATCH = LauncherEnv.Path.GOTHEMES_PATH
			+ "ThemeDetailCatch.xml"; // 详情缓存文件名

	public static final String FEATURED_NOTIFY_DATA_BEAN_XML = LauncherEnv.Path.GOTHEMES_PATH
			+ "featured_notify_data_bean.xml";
	public static final String HOT_NOTIFY_DATA_BEAN_XML = LauncherEnv.Path.GOTHEMES_PATH
			+ "hot_notify_data_bean.xml";
	public static final String LOCKER_NOTIFY_DATA_BEAN_XML = LauncherEnv.Path.GOTHEMES_PATH
			+ "locker_notify_data_bean.xml";

	//	private final String ACTION_THEME_CHANGED = "com.jiubang.ggheart.launcher.themechanged";

	public static final String THEME_CATEGORY = "android.intent.category.DEFAULT"; // 主题包category

	public static final String LOCKER_DEFAULT_THEME_PKG = "com.jiubang.goscreenlock.theme.classic.default";
	public static final String LCOKER_CUR_THEME_PKG_IN_PROVIDER = "usingThemePackageName";
	public static final String LOCKER_THEME_QUERY_URI = "content://com.jiubang.goscreenlock/theme";
	
	//UI3.0新主题的版本号，同时也需要在UI3.0新主题包的工程里面themecfg.xml中修改版本号
	public static final int NEW_UI3_THEME_VERSION = 8;

	private static final String THEME_TITLE = "theme_title"; // 定义在主题包中的主题名称
	private static final String THEME_INFO = "theme_info"; // 定义在主题包中的主题信息
	private static final String THEME_TITLE_3 = "theme_title_3"; // 默认3.0主题名称
	private static final String THEME_INFO_3 = "theme_info_3"; // 默认3.0主题信息
	public final static String IAPKEY = "key_paid_status";
	private final static String GETJAR = "getjar";
	private final static String DEFALUT_PKG = "";
	
	// 以主题包名为key，主题包bean信息为value
	private ConcurrentHashMap<String, ThemeInfoBean> mAllInstalledThemeInfosMap = null; // 所有主题信息
	private Vector<String> mAllInstalledDodolThemeInfos = null; // 所有dodol主题信息
	private Vector<String> mAllInstalledAtomThemeInfos = null; //所有atom主题

	private ThemeInfoBean mCurThemeInfo = null; // 当前主题
	// 一个主题下，供各ui使用的bean集合
	public ConcurrentHashMap<Integer, ThemeBean> mCurThemeBeansMap = null;

	private static ThemeManager sThemeManagerSelf = null;

	private Context mContext = null;

	private ArrayList<ThemeNotifyBean> mNotifyBeans;
	private ArrayList<MessageFilterBean> mFilterBeans;
	private long mLastModifiedTime;

	private static final String THEME_ACTIVATION_CODE_FILE_NAME = "ThemeActivationCode";

	/**
	 * 美化中心入口key
	 */
	public static final String KITTYPLAY_ENTRANCE_KEY = "APPS_MANAGEMENT_ENTRANCE_KEY";
	/**
	 * 桌面主题预览美化中心入口id
	 */
	public static final int ACCESS_FOR_APPCENTER_THEME = 21;
	/**
	 * 桌面锁屏主题预览美化中心入口id
	 */
	public static final int ACCESS_FOR_APPCENTER_LOCKER = 17;
	
	private ScreenStyleConfigInfo mScreenStyleConfigInfo;

	public static synchronized ThemeManager getInstance(Context context) {
		if (sThemeManagerSelf == null) {
			Log.i("ThemeManager", "getInstance to construct");
			sThemeManagerSelf = new ThemeManager(context);
		}
		// Log.i("ThemeManager", "getInstance to construct 2");
		return sThemeManagerSelf;
	}

	private ThemeManager(Context context) {
		mContext = context;
		mFilterBeans = new ArrayList<MessageFilterBean>();
		initTheme();
		parseNotifyAllData();
	}

	/**
	 * 
	 * 初始化
	 * */
	private void initTheme() {
		String themePackageName = getPackageNameFromDB();
		Log.i("ThemeManager", "initTheme pkgName = " + themePackageName);
		if (IGoLauncherClassName.DEFAULT_THEME_PACKAGE.equals(themePackageName)) {
			// 是默认主题
			boolean applySuccess = applyThemeOnlyInMemorry(themePackageName);
			if (applySuccess) {
				// 根据需要，判断是否需要重新设置墙纸。
				// 当前规则为：除开用户手动应用某一主题时需要设置墙纸外，其他情况（如2分钟、SD卡到来引起主题还原时）
				// 不需要重新设置墙纸。用户想要当前主题下的墙纸，通过wallpaperchooser来选取。
				DeskThemeBean deskBean = (DeskThemeBean) getThemeBean(ThemeBean.THEMEBEAN_TYPE_DESK);
				if (deskBean != null) {
					deskBean.mWallpaper = null;
				}
				int setWallPaperflag = 0;
				MsgMgrProxy.sendBroadcastHandler(ThemeManager.this, IAppCoreMsgId.EVENT_THEME_CHANGED,
						setWallPaperflag, getCurThemePackage());
			}
		} else {
			// 如果数据库中备份的当前主题是UI3.0新主题的，而且当前UI3.0的主题已经被卸载就使用默认的UI3.0
			if (themePackageName.equals(IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER)
					&& !GoAppUtils.isAppExist(mContext, IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER)) {
				themePackageName = IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3;
			}

			initApplyThemePackage(themePackageName);
			//解决vip超级主题启动桌面时无法使用问题
			Intent intent = new Intent(ICustomAction.ACTION_HIDE_THEME_ICON);
			int level = ThemePurchaseManager.getCustomerLevel(mContext);
			intent.putExtra("viplevel", level);
			intent.putExtra(MyThemeReceiver.PKGNAME_STRING, themePackageName);
			intent.putExtra(MyThemeReceiver.LAUNCHER_PKGNAME_STRING, mContext.getPackageName());
			if (Machine.IS_HONEYCOMB_MR1) {
				// 3.1之后，系统的package manager增加了对处于“stopped state”应用的管理
				intent.setFlags(MyThemeReceiver.FLAG_INCLUDE_STOPPED_PACKAGES);
			}
			mContext.sendBroadcast(intent);

			if (mCurThemeInfo == null) {
				// 说明应用不成功，可能当前sd卡未准备好或主题已被卸载。
				boolean applySuccess = applyThemeOnlyInMemorry(IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3);
				if (applySuccess) {
					// 根据需要，判断是否需要重新设置墙纸。
					// 当前规则为：除开用户手动应用某一主题时需要设置墙纸外，其他情况（如2分钟、SD卡到来引起主题还原时）
					// 不需要重新设置墙纸。用户想要当前主题下的墙纸，通过wallpaperchooser来选取。
					DeskThemeBean deskBean = (DeskThemeBean) getThemeBean(ThemeBean.THEMEBEAN_TYPE_DESK);
					if (deskBean != null) {
						deskBean.mWallpaper = null;
					}
					int setWallPaperflag = 0;
					MsgMgrProxy.sendBroadcastHandler(ThemeManager.this,
							IAppCoreMsgId.EVENT_THEME_CHANGED, setWallPaperflag, getCurThemePackage());
				}
				/*
				 * //当前sd卡状态良好，则直接将当前应用的主题信息保存起来。 if (isSDCardOK()) {
				 * //当前SD卡可用，则需要将当前值保存到DB中。
				 * savePackageNameToDB(IGoLauncherClassName.DEFAULT_THEME_PACKAGE);
				 * 
				 * }
				 */
			}
		}

	}

	/**
	 * 判断当前SDCard的状态，非共享模式下。
	 * 
	 * @return
	 */
	private boolean isSDCardOK() {
		boolean isShared = Environment.getExternalStorageState().equals(Environment.MEDIA_SHARED);
		return !isShared;
	}

	public void onBCChange(int msgId, int param, Object ...obj) {
		String pkgName = (String) obj[0];
		switch (msgId) {
			case IAppCoreMsgId.EVENT_UPDATE_APP :
			case IAppCoreMsgId.EVENT_UPDATE_PACKAGE : {
				if (GoAppUtils.isGOLauncherReplaceTheme(mContext, pkgName)) {
					handleAppChange(param, pkgName);
					cleanDirtyStyle(pkgName);
				} else {
					if (!handleMaskThemeUpdated(pkgName)) {
						handleAppInstallOrUpdate(pkgName);
					}
				}
			}
				break;
			case IAppCoreMsgId.EVENT_INSTALL_PACKAGE : {
				handleAppInstallOrUpdate(pkgName);
			}
				break;
			case IAppCoreMsgId.EVENT_UNINSTALL_APP : {
				// （短信+桌面+锁屏主题的）特殊大主题的卸载事件
				handleAppChange(param, pkgName);
				cleanDirtyStyle(pkgName);
			}
				break;
			case IAppCoreMsgId.EVENT_UNINSTALL_PACKAGE : {
				// 主题的卸载事件
				handleAppChange(param, pkgName);
				cleanDirtyStyle(pkgName);
			}
				break;
			case IAppCoreMsgId.EVENT_SD_MOUNT :
			case IAppCoreMsgId.EVENT_REFLUSH_SDCARD_IS_OK : {
				// 检验主题图标
				MsgMgrProxy.sendBroadcastHandler(ThemeManager.this,
						IAppCoreMsgId.EVENT_CHECK_THEME_ICON, 0);
				handleSDCardOK();
			}
				break;

			case IAppCoreMsgId.EVENT_REFLUSH_TIME_IS_UP : {
				// 检验主题图标
				MsgMgrProxy.sendBroadcastHandler(ThemeManager.this,
						IAppCoreMsgId.EVENT_CHECK_THEME_ICON, 0);
				handleReflushTimeUp();
			}
				break;

			default :
				break;
		}
	}

	private void handleAppInstallOrUpdate(String pkgName) {
		if (pkgName != null && mCurThemeInfo != null) {
			String curTheme = mCurThemeInfo.getPackageName();
			ImageExplorer.getInstance(mContext).setCurrentPackageName(curTheme);
			
			if (IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER.equals(pkgName)
					&& IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3.equals(curTheme)) {
				// 当前使用的是老版主题3.0，且新安装使用的是新版主题3.0，则直接应用新版主题3.0。
				mAppThemeParser = null;
				mDeskThemeParser = null;
				applyThemePackage(pkgName, false);
			} else if (pkgName.equals(curTheme)) {
				mAppThemeParser = null;
				mDeskThemeParser = null;
				// 主题更新覆盖安装，则重新应用。
				updateThemePackage(pkgName, false);
			}
		}
	}

	/**
	 * 处理超级主题的覆盖安装事件，若当前正在应用的主题正是覆盖更新的主题，则需要重启桌面
	 * @param packageName
	 */
	private boolean handleMaskThemeUpdated(final String packageName) {
		try {
			if (mCurThemeInfo != null) {
				String curTheme = mCurThemeInfo.getPackageName();
				if (packageName.equals(curTheme) && mCurThemeInfo.isMaskView()) {
					DataChangeListenerProxy.getInstance().exit(true);
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void handleAppChange(int type, String packageName) {
		//		if (packageName == null || GoAppUtils.isAppExist(mContext, packageName)) {
		//		// 收到卸载消息，却仍检测到对应包存在，则说明消息处理错误。
		//		return;
		//		}

		if (packageName == null) {
			// 收到卸载消息，若包名是空，则说明消息处理错误。
			return;
		}

		// 首先移除内存中的主题列表信息
		if (mAllInstalledThemeInfosMap != null) {
			mAllInstalledThemeInfosMap.remove(packageName);
		}
		if (mCurThemeInfo != null) {
			String curPackageName = mCurThemeInfo.getPackageName();
			if (curPackageName.equals(packageName)) {
				// 卸载主题为当前主题，且为ui3.0新主题，则直接应用老版3.0主题，否则，直接应用默认主题
				if (packageName.equals(IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER)) {
					applyThemePackage(IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3, false);
				} else {
					applyThemePackage(IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3, true);
				}
			}
		}
	}

	private void cleanDirtyStyle(String packageName) {
		if (packageName == null || GoAppUtils.isAppExist(mContext, packageName)) {
			return;
		}
		
		SettingProxy.clearDirtyStyleSetting(packageName);
	}

	private void handleSDCardOK() {
		String curThemePackageName = getCurThemePackage();
		String themePackageName = getPackageNameFromDB();
		if (curThemePackageName.equals(themePackageName)
				&& !(!GoAppUtils.isAppExist(mContext, themePackageName) && ZipResources
						.isZipThemeExist(themePackageName))) {
			// 若当前内存中应用与数据库中保存的主题相同且不为sd内主题，则直接返回，不进行扫描
			return;
		}
		// SD卡准备好，重新扫描主题包，并且应用上一主题包
		// 由于每次进入主题预览时，都会重新扫描一次本地主题，而这里之所以也要会进行扫描一次，是因为某些装在sd卡上的主题需要显示出来。
		scanInitAllInstalledThemes(true);
		// 待应用的主题已被卸载时如何处理，若应用不成功，则保留当前状态。
		applySdThemePackage(themePackageName, false);
	}

	/**
	 * 2min到，则首先判断Sharedpreference中记录的主题包是否安装，
	 * 若已安装，则与当前内存中的进行比较，若不同，则应用之，若相同，则保存并跳过， 若未安装，则直接保存当前内存中的主题。 2分钟到了，重新扫描主题包
	 */
	private void handleReflushTimeUp() {
		// TODO:是否可以去掉，为什么需要重新扫描？
		String curThemePackageName = getCurThemePackage();
		String preThemePkgName = getPackageNameFromDB();
		if (isSDCardOK()) {
			// 此处需要再次判断当前sd卡状态，以便
			// 在初始时使用的默认主题，但此刻sd卡不可用，不能保存当前内存中主题值即默认主题到db中，db中保存的是上次应用的主题，
			// 那么，2min时间到后（2.1版本不能收到SD卡消息，以2min为界限，表示手机启动完成），应将当前内存中应用的值保存到db中，
			// 以表明db与内存中值是同步。后续应用上次的主题，也是同步修改内存与DB中的值。
			// 非共享模式下，才保存进入Sharedpreference
			if (!preThemePkgName.equals(curThemePackageName)) {
				savePackageNameToDB(curThemePackageName);
			}
		}
		if (curThemePackageName.equals(preThemePkgName)) {
			// 若当前内存中应用与数据库中保存的主题相同，则直接返回，不进行扫描
			return;
		}
		scanInitAllInstalledThemes(true);
		applyThemePackage(preThemePkgName, false);
	}

	/**
	 * 获取所有本地安装的主题信息，按照安装时间降序排序
	 * 
	 * @author huyong
	 * @return
	 */
	public ArrayList<ThemeInfoBean> getAllInstalledThemeInfos() {
		ConcurrentHashMap<String, ThemeInfoBean> installedThemeInfosMap = scanInitAllInstalledThemes(false);
		// 重新扫描后，需要重新确定mCurThemeInfo
		String curPkgName = getCurThemePackage();
		mCurThemeInfo = installedThemeInfosMap.get(curPkgName);
		ImageExplorer.getInstance(mContext).setCurrentPackageName(curPkgName);

		ArrayList<ThemeInfoBean> arrayList = new ArrayList<ThemeInfoBean>(
				installedThemeInfosMap.values());
		String sortMethod = "getThemeInstalledTime";
		String order = "ASC";
		PackageManager packageMgr = mContext.getPackageManager();
		Class[] methodArgClasses = new Class[] { PackageManager.class };
		Object[] methodArg = new Object[] { packageMgr };
		try {
			SortUtils.sort(arrayList, sortMethod, methodArgClasses, methodArg, order);
		} catch (Exception e) {
			// 弹出提示排序失败
			Log.i("ThemeManager", "getAllInstalledThemeInfos when sort " + e.getMessage());
		}
		return arrayList;
	}

	/**
	 * 获取精选主题
	 *   参数 tab 标识是热门主题还是精选主题
	 * @author huyong      
	 * @return
	 */
	public ArrayList<ThemeInfoBean> getFeaturedThemeInfoBeans(int type, BroadCasterObserver observer) {
		return new OnlineThemeGetter(mContext).getFeaturedThemeInfoBeans(
				getAllThemeInfosWithoutDefaultTheme(), type, false, observer);
	}
	/**
	 * 获取banner数据
	 *   
	 * @author huyong      
	 * @return
	 */
	public ThemeBannerBean getBannerData(int type, BroadCasterObserver observer) {
		return new OnlineThemeGetter(mContext).getBannerData(type, observer);
	}
	/**
	 * 获取精选主题
	 *   参数 tab 标识是热门主题还是精选主题
	 * @author huyong      
	 * @return
	 */
	public ArrayList<ThemeInfoBean> getSpecThemeInfoBeans(int ty, BroadCasterObserver observer) {
		return new OnlineThemeGetter(mContext).getSpecThemeThemeInfoBeans(
				getAllThemeInfosWithoutDefaultTheme(), ty, observer);
	}
	/*
	 * 获取除默认主题以外的所有主题
	 * 
	 * @author huyong
	 * 
	 * @return null for 没有其他主题，ArrayList for 所有其他主题的信息
	 */
	public ArrayList<ThemeInfoBean> getAllThemeInfosWithoutDefaultTheme() {
		ConcurrentHashMap<String, ThemeInfoBean> installedThemeInfosMap = scanInitAllInstalledThemes(false);
		String curPkgName = getCurThemePackage();
		mCurThemeInfo = installedThemeInfosMap.get(curPkgName);
		ImageExplorer.getInstance(mContext).setCurrentPackageName(curPkgName);
		
		if (mCurThemeInfo == null) {
			// TODO:此处属于异常情况
			Log.i("ThemeManager", "mCurThemeInfo = null when getAllThemeInfos() is called ");
		}
		HashMap<String, ThemeInfoBean> tmpAllThemesMap = new HashMap<String, ThemeInfoBean>(
				installedThemeInfosMap);
		// 移除默认主题
		tmpAllThemesMap.remove(IGoLauncherClassName.DEFAULT_THEME_PACKAGE);
		ArrayList<ThemeInfoBean> arrayList = new ArrayList<ThemeInfoBean>(tmpAllThemesMap.values());
		return arrayList;
	}

	/**
	 * 获取当前主题包名
	 * 
	 * @author huyong
	 * @return
	 */
	public String getCurThemePackage() {
		if (mCurThemeInfo != null) {
			return mCurThemeInfo.getPackageName();
		} else {
			return IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
		}
	}

	/**
	 * 由于锁屏应用不能被监听，不能被保存到内存，因此每次获取当前使用的锁屏主题时需要重新查询
	 * 
	 * @author yangbing
	 * @return
	 */
	public String getCurLockerTheme() {
		String result = null;
		if (mContext != null) {
			ContentResolver contentResolver = mContext.getContentResolver();
			if (contentResolver != null) {
				String uri = GoAppUtils.getCurLockerPkgName(mContext);
				StringBuffer sb = new StringBuffer();
				sb.append("content://");
				sb.append(uri);
				sb.append("/theme");
				Uri golockerUri = Uri.parse(sb.toString());
				Cursor cursor = null;
				try {
					cursor = contentResolver.query(golockerUri, null, null, null, null);
					if (cursor != null && cursor.moveToFirst()) {
						int index = cursor.getColumnIndex(LCOKER_CUR_THEME_PKG_IN_PROVIDER);
						result = cursor.getString(index);
					}
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					if (cursor != null) {
						cursor.close();
					}
				}
			}
		}
		if (result == null || result.equals(LOCKER_DEFAULT_THEME_PKG)) {
			result = GoAppUtils.getCurLockerPkgName(mContext);
		}
		return result;
	}

	/**
	 * 获取当前主题包Bean信息
	 * 
	 * @author huyong
	 * @return
	 */
	public ThemeInfoBean getCurThemeInfoBean() {
		return mCurThemeInfo;
	}

	/**
	 * 当前应用ui3.0主题，若当前已有ui3.0最新主题包，则强行使用ui3.0最新主题包，不使用ui3.0主题
	 * 
	 * @param themePackage
	 * @return
	 */
	private String forceChangeDefaultToNewest(String themePackage) {
		if (themePackage != null && themePackage.equals(IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3)) {
			if (GoAppUtils.isAppExist(mContext, IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER)) {
				// 若初始启动时当前应用的是老版3.0主题，则
				themePackage = IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER;
			}
		}
		return themePackage;
	}

	/**
	 * 同步应用解析主题
	 * 
	 * @author huyong
	 * @param themePackage
	 * @param isDefault3
	 * @return
	 */
	private boolean initApplyThemePackage(String themePackage) {

		themePackage = forceChangeDefaultToNewest(themePackage);

		// 解析应用主题
		boolean result = applyThemeOnlyInMemorry(themePackage);
		// TODO: 修改初始化启动时不能使用主题中的部分
		if (result) {
			// 首先应用默认主题3.0的壁纸
			setWallpaperWhenFirstInstalled(themePackage);

			// 根据需要，判断是否需要重新设置墙纸。
			// 当前规则为：除开用户手动应用某一主题时需要设置墙纸外，其他情况（如2分钟、SD卡到来引起主题还原时）
			// 不需要重新设置墙纸。用户想要当前主题下的墙纸，通过wallpaperchooser来选取。
			DeskThemeBean deskBean = (DeskThemeBean) getThemeBean(ThemeBean.THEMEBEAN_TYPE_DESK);
			if (deskBean != null) {
				deskBean.mWallpaper = null;
			}
			int setWallPaperflag = 0;
			MsgMgrProxy.sendBroadcastHandler(ThemeManager.this, IAppCoreMsgId.EVENT_THEME_CHANGED,
					setWallPaperflag, getCurThemePackage());
		}

		return result;
	}

	/**
	 * 产品需求，对新安装用户和恢复默认操作时，需要设置壁纸，其他情况均不设壁纸。
	 * 
	 * @param themePackage
	 */
	private void setWallpaperWhenFirstInstalled(String themePackage) {
		String processName = AppUtils.getCurProcessName(mContext);
		if (processName != null && processName.equals(LauncherEnv.PROCESS_NAME)) {
			DeskThemeBean deskBean = (DeskThemeBean) getThemeBean(ThemeBean.THEMEBEAN_TYPE_DESK);
			if (deskBean == null) {
				return;
			}
			if ((IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3.equals(themePackage) || IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER
					.equals(themePackage)) && DataProvider.getInstance(mContext).isNewDB()) {
				setWallpaper(themePackage, deskBean);
			} else {
				deskBean.mWallpaper = null;
			}
		}
	}

	/**
	 * 更换壁纸
	 * 
	 * */
	private void setWallpaper(String themePackage, DeskThemeBean deskBean) {

		if (IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3.equals(themePackage)) {
			themePackage = IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
		}
		try {
			final Resources themeResources = mContext.getPackageManager()
					.getResourcesForApplication(themePackage);
			if (themeResources != null) {
				final int wallpaperId = themeResources.getIdentifier(deskBean.mWallpaper.mResName,
						"drawable", themePackage);
				if (wallpaperId > 0) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							boolean ret = WindowControl.setWallpaper(mContext, themeResources, wallpaperId);
							if (!ret) {
								try {
									DeskToast.makeText(mContext, mContext.getString(R.string.set_wallpaper_error),
											Toast.LENGTH_SHORT).show();
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
							
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
									ICommonMsgId.UPDATE_WALLPAPER, -1, null, null);
						}

					}).start();

				}
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 对外公开接口，应用选择的主题，应用成功后，将修改内存中当前主题值及保存当前主题到DB中。
	 * 
	 * @author huyong
	 * @param themepackage
	 *            应用的主题包名
	 * @param isSetWallPaper
	 *            ：是否设置墙纸
	 */
	public void applyThemePackage(String themePackage, boolean isSetWallPaper) {
		if (mCurThemeInfo != null && mCurThemeInfo.getPackageName().equals(themePackage)) {
			Log.i("applyThemePackage", "curTheme has used " + themePackage);
			return;
		}
		if (!isInstalledTheme(mContext, themePackage)) {
			// 未安装该主题包，或当前该主题包不可用（可能由于外部修改了sd卡的存储模式）
			return;
		}
		themePackage = forceChangeDefaultToNewest(themePackage);

		// 增加判断解析主题是否成功，以便来判断是否能应用对外广播
		asynApplyThemeWithDialog(themePackage, isSetWallPaper);
	}

	/**
	 * 显示激活码验证对话框
	 */
	public void showCheckDialog(final String pkgName, final String url) {
		if (GoLauncherActivityProxy.getActivity() == null) {
			return;
		}
		
		final Dialog dialog = new Dialog(GoLauncherActivityProxy.getActivity(), R.style.AppGameSettingDialog);
		dialog.setContentView(R.layout.theme_manager_check_dialog);

		final EditText et = (EditText) dialog.findViewById(R.id.theme_manager_dialog_check_input);

		((Button) dialog.findViewById(R.id.appgame_download_delete_dialog_ok))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Uri myBlogUri = Uri.parse(url);
						Intent intent = new Intent(Intent.ACTION_VIEW, myBlogUri);
						try {
							mContext.startActivity(intent);
						} catch (Exception e) {
							//							Toast.makeText(mContext, "打开浏览器失败", Toast.LENGTH_LONG).show();
						}

					}
				});
		((Button) dialog.findViewById(R.id.appgame_download_delete_dialog_cancel))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String str = et.getEditableText().toString();
						if (str == null || str.length() == 0) {
							Toast.makeText(GoLauncherActivityProxy.getActivity(),
									R.string.theme_manager_activation_dialog_check_failed,
									Toast.LENGTH_LONG).show();
						} else {
							if (checkActivationCode(str)) {
								// 保存激活码
								saveActivationCode(pkgName, str);
								Toast.makeText(GoLauncherActivityProxy.getActivity(),
										R.string.theme_manager_activation_dialog_check_success,
										Toast.LENGTH_LONG).show();
								dialog.dismiss();
								asynApplyThemeWithDialog(pkgName, true);
							} else {
								Toast.makeText(GoLauncherActivityProxy.getActivity(),
										R.string.theme_manager_activation_dialog_check_failed,
										Toast.LENGTH_LONG).show();
							}
						}
					}
				});
		((Button) dialog.findViewById(R.id.theme_manager_dialog_check_clear_btn))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						et.setText("");
					}
				});
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		dialog.setCancelable(true);
		dialog.show();
	}

	public static void gotoVipPage(Context context) {
		Intent intent = new Intent(context, ThemeVipPage.class);
		String url = getVipPayPageUrl(context);
		intent.putExtra("url", url);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static String getVipPayPageUrl(Context context) {
		int vip = ThemePurchaseManager.getCustomerLevel(context);
		String url = ThemeConstants.VIPPAY_PAGE_URL_FOREIGN;
		if (GoAppUtils.isCnUser(context)) {
			url = ThemeConstants.VIPPAY_PAGE_URL_CN;
		}
		Locale local = Locale.getDefault();
		String country = local.getCountry().toLowerCase();
		String lan = local.getLanguage().toLowerCase();
		url = url.replace("%lang", lan);
		if (vip == ThemeConstants.CUSTOMER_LEVEL1) {
			url = url.replace("%pid", "2");
			url = url.replace("%page", "1");
			PreferencesManager pm = new PreferencesManager(context,
					IPreferencesIds.THEME_SETTING_CONFIG, Context.MODE_PRIVATE);
			pm.putBoolean(IPreferencesIds.HAS_SHOW_VIPUPGRADE, true);
			pm.commit();
		} else {
			url = url.replace("%pid", "1");
			url = url.replace("%page", "3");
		}
		url = url.replace("%local", country);
		Random random = new Random(new Date().getTime());
		url = url + random.nextLong();
		return url;
	}

	public String getVipPayPageUrl() {
		if (GoAppUtils.isCnUser(mContext)) {
			return ThemeConstants.VIPPAY_PAGE_URL_CN;
		}
		return ThemeConstants.VIPPAY_PAGE_URL_CN;
	}

	/**
	 * 验证激活码
	 * @param str
	 * @return
	 */
	public static boolean checkActivationCode(String str) {
		if (str == null || str.length() != 8) {
			return false;
		}
		int[] array = new int[8];
		for (int i = 0; i < 8; ++i) {
			char ch = str.charAt(i);
			if (i < 2) {
				// 判断前两位是否字母，不是的话直接返回
				if (!StringUtils.isCharacter(ch)) {
					return false;
				}

			} else {
				if (!StringUtils.isNumeric(ch)) {
					return false;
				} else {
					array[i] = Integer.parseInt(str.valueOf(ch));
				}
			}

		}
		//		Log.i("ABEN", "1:" + ( array[2] + array[4]));
		//		Log.i("ABEN", "2:" + ( array[3] - array[6]));
		//		Log.i("ABEN", "3:" + ( array[5] + array[7]));
		if (array[2] + array[4] == 9 && array[3] - array[6] == 3 && array[5] + array[7] == 8) {
			return true;
		}
		return false;
	}

	private void saveActivationCode(String packageName, String code) {
		if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(code)) {
			return;
		}
		File file = mContext.getFileStreamPath(THEME_ACTIVATION_CODE_FILE_NAME);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		JSONObject json = getActivationCodeJson();
		if (json == null) {
			json = new JSONObject();
		}
		try {
			json.put(packageName, code);
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		FileOutputStream fopt = null;
		try {
			fopt = new FileOutputStream(file);
			fopt.write(json.toString().getBytes());
			fopt.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fopt != null) {
				try {
					fopt.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private String getActivationCode(String packageName) {
		String ret = null;
		JSONObject json = getActivationCodeJson();
		if (json != null) {
			try {
				ret = json.getString(packageName);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	private JSONObject getActivationCodeJson() {
		File file = mContext.getFileStreamPath(THEME_ACTIVATION_CODE_FILE_NAME);
		if (!file.exists()) {
			return null;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		String data = CacheFileUtils.readToString(fis, "utf-8");
		if (data == null) {
			return null;
		}
		JSONObject json = null;
		try {
			json = new JSONObject(data);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return json;
	}

	

	/**
	 * 更新当前应用的主题，应用成功后，将修改内存中当前主题值及保存当前主题到DB中。
	 * 
	 * @author huyong
	 * @param themepackage
	 *            应用的主题包名
	 * @param isSetWallPaper
	 *            ：是否设置墙纸
	 */
	private void updateThemePackage(String themePackage, boolean isSetWallPaper) {
		if (!isInstalledTheme(mContext, themePackage)) {
			// 未安装该主题包，或当前该主题包不可用（可能由于外部修改了sd卡的存储模式）
			return;
		}
		themePackage = forceChangeDefaultToNewest(themePackage);
		// 增加判断解析主题是否成功，以便来判断是否能应用对外广播
		asynApplyThemeWithDialog(themePackage, isSetWallPaper);
	}

	/**
	 * <br>
	 * 功能简述:该接口用于sd卡挂载后重新应用sd卡内主题 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param themePackage
	 * @param isSetWallPaper
	 */
	public void applySdThemePackage(String themePackage, boolean isSetWallPaper) {
		if (!isInstalledTheme(mContext, themePackage)) {
			// 未安装该主题包，或当前该主题包不可用（可能由于外部修改了sd卡的存储模式）
			return;
		}
		themePackage = forceChangeDefaultToNewest(themePackage);

		// 增加判断解析主题是否成功，以便来判断是否能应用对外广播
		asynApplyThemeWithDialog(themePackage, isSetWallPaper);
	}

	/**
	 * 设置当前应用的主题，确保内存中的值，并保存到sharedpreference中供任务管理器插件使用。 不能与保存到DB操作同步。
	 * 当前主题的修改只能发生在已确定情况下。
	 * 
	 * @author huyong
	 * @param themePackage
	 */
	private synchronized boolean setThemePackage(String themePackage) {
		if (themePackage == null) {
			// TODO:没有找到，说明外部已经卸载该主题包，应弹出提示，不能应用
			return false;
		}
		if (mAllInstalledThemeInfosMap == null) {
			mAllInstalledThemeInfosMap = new ConcurrentHashMap<String, ThemeInfoBean>();
			ThemeInfoBean infoBean = parserThemeInfo(themePackage, null);
			if (infoBean != null) {
				mAllInstalledThemeInfosMap.put(themePackage, infoBean);
			} else {
				scanInitAllInstalledThemes(false);
			}
		}

		ThemeInfoBean curInfoBean = mAllInstalledThemeInfosMap.get(themePackage);
		if (null == curInfoBean) {
			// 这个添加是因为把主题UI2.0做成process时，设置主题时没scanInitAllInstalledThemes,
			// mAllInstalledThemeInfosMap信息为空
			curInfoBean = parserThemeInfo(themePackage, null);
			if (curInfoBean != null) {
				mAllInstalledThemeInfosMap.put(themePackage, curInfoBean);
			}
		}

		saveCurPkgNameToSharedpreference(mContext, themePackage);
		ImageExplorer.getInstance(mContext).setCurrentPackageName(themePackage);
		
		mCurThemeInfo = curInfoBean;
		if (mCurThemeInfo == null) {
			// TODO:没有找到，说明外部已经卸载该主题包，应弹出提示，不能应用
			return false;
		}
		return true;
	}

	/**
	 * 将应用的主题包名保存至数据库
	 * 
	 * @author huyong
	 * @param packageName
	 */
	private void savePackageNameToDB(String packageName) {
		try {
			ContentValues values = new ContentValues();
			values.put(ConfigTable.THEMENAME, packageName);
			ContentResolver resolver = mContext.getContentResolver();
			resolver.update(GoContentProvider.CONTENT_CURTHEME_URI, values, null, null);
			
		} catch (Exception e) {
			// TODO: handle exception
			DataProvider.getInstance(mContext.getApplicationContext()).saveThemeName(packageName);
		}
		Log.i("ThemeManager", "savePkgName = " + packageName);
	}

	/**
	 * 从DB中获取上次保存的主题包名
	 * 
	 * @author huyong
	 * @return
	 */
	private String getPackageNameFromDB() {
		ContentResolver resolver = mContext.getContentResolver();
		Cursor cursor = resolver.query(GoContentProvider.CONTENT_CURTHEME_URI, new String[]{ConfigTable.THEMENAME}, null, null,
				null);
		String pkgName = null;
		try {
			if (null != cursor && cursor.moveToFirst()) {
				int index = cursor.getColumnIndex(ConfigTable.THEMENAME);
				pkgName = cursor.getString(index);
			}
		} catch (Exception e) {
			Log.i("ThemeManager", "getThemeName() = " + e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return pkgName != null ? pkgName : IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
	}

	/**
	 * 保存当前应用的主题到sharedpreference中， 为了给任务管理器提供当前主题信息，以便可以显示带当前主题的icon信息。
	 * 
	 * @param context
	 * @param pkgName
	 */
	private void saveCurPkgNameToSharedpreference(final Context context, String pkgName) {
		PreferencesManager sharedPreferences = new PreferencesManager(context,
				IPreferencesIds.CUR_THEME_PKG_PREFERENCES, Context.MODE_WORLD_READABLE);
		sharedPreferences.putString(IPreferencesIds.CUR_THEME_PKG, pkgName);
		sharedPreferences.commit();
	}

	/**
	 * 应用主题时，通知更新， 为了给任务管理器提供当前主题信息，以便可以显示带当前主题的icon信息。
	 * 
	 * @param context
	 * @param pkgName
	 */
	private void broadCasetCurThemepkg(final Context context, String pkgName) {
		Intent intent = new Intent();
		intent.setAction(ICustomAction.ACTION_THEME_CHANGED);
		intent.putExtra(IPreferencesIds.CUR_THEME_PKG, pkgName);
		context.sendBroadcast(intent);
	}

	/**
	 * 从Sharedpreference获取上次保存的主题包名(数据库版本号11-12之间升级的时候，需要用到，不再被使用)
	 * 
	 * @author huyong
	 * @return
	 */
	@Deprecated
	public static String getPackageNameFromSharedpreference(final Context context) {
		Log.i("ThemeManager", "getPackageNameFromSharedpreference() in here");
		PreferencesManager sharedPreferences = new PreferencesManager(context,
				IPreferencesIds.SHAREDPREFERENCES_THEME, Context.MODE_PRIVATE);
		final String sharedpreferences_theme_name_key = "themepackagename"; // 主题的sharedpreferences中的key
		return sharedPreferences.getString(sharedpreferences_theme_name_key,
				IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3);
	}

	/**
	 * 清除主题的Sharedpreference,(数据库版本号11-12之间升级的时候，需要用到，不再被使用)
	 * 
	 * @author huyong
	 */
	@Deprecated
	public static void clearThemeSharedpreference(final Context context) {
		PreferencesManager sharedPreferences = new PreferencesManager(context,
				IPreferencesIds.SHAREDPREFERENCES_THEME, Context.MODE_PRIVATE);
		if (sharedPreferences != null) {
			sharedPreferences.clear();
			sharedPreferences.commit();
		}
	}

	/**
	 * 仅在内存中应用主题，尚未保存到DB中。 确保应用成功情况下，内存中的当前主题及当前主题的map结构均为一一对应关系。
	 * 
	 * @param themePkgName
	 * @return
	 */
	private boolean applyThemeOnlyInMemorry(String themePkgName) {
		if (!isInstalledTheme(mContext, themePkgName)) {
			// 未安装该主题包，或当前该主题包不可用（可能由于外部修改了sd卡的存储模式）
			return false;
		}

		boolean result = parserTheme(themePkgName);
		if (result) {
			setThemePackage(themePkgName);
		}
		return result;
	}

	/**
	 * 异步解析主题资源文件
	 * 
	 * @author huyong
	 * @param themePackage
	 *            ：主题包名称
	 * @param isSetWallPaper
	 *            ：是否设置墙纸
	 */
	private void asynApplyThemeWithDialog(final String themePackage, final boolean isSetWallPaper) {
		if (themePackage == null) {
			return;
		}
		ThemeInfoBean bean = mAllInstalledThemeInfosMap.get(themePackage);
		if (bean == null) {
			bean = new ThemeInfoBean();
			bean = parserThemeInfo(themePackage, bean);
		}
		if (bean != null && bean.getNeedActivationCode()
				&& !TextUtils.isEmpty(bean.getActivationCodeUrl())) {
			// 检查激活码是否可用
			String url = bean.getActivationCodeUrl();
			String code = getActivationCode(themePackage);
			if (code == null || !checkActivationCode(code)) {
				showCheckDialog(themePackage, url);
				return;
			}
		}

		boolean isFiltered = isFilteredTheme(themePackage);
		if (isFiltered) {
			Toast.makeText(mContext, R.string.theme_infringed_copyright, 800).show();
			return;
		}

		// 通知屏幕层显示全屏loading
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME, ICommonMsgId.SHOW_PROGRESSBAR, -1,
				null, null);
		// final ProgressDialog progressDialog = showProgressDialog();
		new Thread(ThreadName.ASYNC_PARSE_THEME) {
			@Override
			public void run() {
				boolean result = applyThemeOnlyInMemorry(themePackage);
				if (result) {
					// 应用成功，则首先保存到DB中。
					savePackageNameToDB(themePackage);
					if (!isSetWallPaper) {
						// 根据需要，判断是否需要重新设置墙纸。
						// 当前规则为：除开用户手动应用某一主题时需要设置墙纸外，其他情况（如2分钟、SD卡到来引起主题还原时）
						// 不需要重新设置墙纸。用户想要当前主题下的墙纸，通过wallpaperchooser来选取。
						DeskThemeBean deskBean = (DeskThemeBean) getThemeBean(ThemeBean.THEMEBEAN_TYPE_DESK);
						if (deskBean != null) {
							deskBean.mWallpaper = null;
						}
					}
					int flag = isSetWallPaper ? 1 : 0;
					MsgMgrProxy.sendBroadcastHandler(ThemeManager.this,
							IAppCoreMsgId.EVENT_THEME_CHANGED, flag, getCurThemePackage());
					// 广播通知当前主题，just for 任务管理器。
					broadCasetCurThemepkg(mContext, themePackage);

					//通知3D插件取消loading
					MsgMgrProxy.sendHandler(this, IDiyFrameIds.SHELL_FRAME,
							IAppCoreMsgId.DISMISS_PROGRESSBAR, -1, null, null);
					
				} else {
					//取消屏幕层loading
					MsgMgrProxy.sendBroadcastHandler(ThemeManager.this,
							IAppCoreMsgId.DISMISS_PROGRESSBAR, -1, null, null);
				}

				// //通知关闭换主题转圈圈
				// dismissProgressDialog(progressDialog);
			};
		}.start();
	}

	/***
	 * 判断主题是否需要过滤掉
	 * liulixia
	 * @param packageName
	 * @return
	 */
	private boolean isFilteredTheme(String packageName) {
		//判断是否是黑名单内包含的apk，如果是，禁止使用此套主题
		boolean isFilterd = false;
		String filePath = LauncherEnv.Path.MESSAGECENTER_PATH + "filterinfo.txt";
		if (FileUtil.isSDCardAvaiable() && FileUtil.isFileExist(filePath)) {
			File file = new File(filePath);
			long modifiedTime = file.lastModified();
			if (mFilterBeans.isEmpty() || mLastModifiedTime != modifiedTime) {
				mFilterBeans.clear();
				String content = EncodingUtils.getString(FileUtil.getByteFromSDFile(filePath),
						"UTF-8");
				if (content != null) {
					String decryptString = CryptTool.decrypt(content, ConstValue.ENCRYPT_KEY);
					int index = decryptString == null ? -1 : decryptString.indexOf("#");
					if (index < 0) {
						return false;
					}
					String apkSignatures = decryptString.substring(0, index);
					String apkNames = decryptString.substring(index + 1);
					parseFilterApks(apkSignatures, apkNames);
				}
				mLastModifiedTime = modifiedTime;
			}
		}

		if (mFilterBeans != null && !mFilterBeans.isEmpty()) {
			PackageManager pm = mContext.getPackageManager();
			String apkSignature = StatisticsAppsInfoData.getAppSignature(pm, packageName);
			for (MessageFilterBean bean : mFilterBeans) {
				int type = bean.filterType;
				if (type == MessageFilterBean.FILTERD_BY_PACKAGE_NAME) {
					String pkgName = bean.apkName;
					if (pkgName.equals(packageName)) {
						isFilterd = true;
						break;
					}
				} else if (type == MessageFilterBean.FILTERD_BY_SIGNATURE) {
					String signature = bean.apkSignature;
					if (apkSignature.equals(signature)) {
						isFilterd = true;
						break;
					}
				}
			}
		}

		return isFilterd;
	}

	/**
	 * 获取apk过滤信息
	 * @param apkSignatures
	 * @param apkNames
	 */
	public void parseFilterApks(String apkSignatures, String apkNames) {
		if (apkSignatures == null || apkNames == null) {
			return;
		}
		String[] signatures = apkSignatures.split(",");
		String[] names = apkNames.split(",");
		if (signatures.length == 0 && names.length == 0) {
			return;
		}
		for (int i = 0; i < signatures.length; i++) {
			String apkSignature = signatures[i];
			if (apkSignature.equals("")) {
				continue;
			}
			MessageFilterBean filterBean = new MessageFilterBean();
			filterBean.apkSignature = apkSignature;
			filterBean.filterType = MessageFilterBean.FILTERD_BY_SIGNATURE;
			mFilterBeans.add(filterBean);
		}
		for (int i = 0; i < names.length; i++) {
			String apkName = names[i];
			if (apkName.equals("")) {
				continue;
			}
			MessageFilterBean filterBean = new MessageFilterBean();
			filterBean.apkName = apkName;
			filterBean.filterType = MessageFilterBean.FILTERD_BY_PACKAGE_NAME;
			mFilterBeans.add(filterBean);
		}
	}

	/**
	 * 开始解析主题 根据具体需要，通过中间变量的控制，可以提供支持部分主题的成功解析与应用；
	 * 也可以提供为，只要其中任一模块解析出错，则该主题均不可应用。 目前采取第二种方式。
	 * 
	 * @author huyong
	 * @param themePackage
	 */
	private boolean parserTheme(String themePackage) {
		if (themePackage == null) {
			return false;
		}
		Log.i("ThemeManager", "begin parserTheme pkg = " + themePackage);
		if (IGoLauncherClassName.DEFAULT_THEME_PACKAGE.equals(themePackage)) {
			// 若是应用默认主题，则直接应用成功并立即返回
			return true;
		}

		ThemeInfoBean infoBean = getThemeInfo(themePackage);

		if (isDodolTheme(themePackage)) {
			AppDataThemeBean themeBean = (AppDataThemeBean) new DodolThemeIconParser()
					.autoParseAppThemeXml(mContext, themePackage, false);
			DodolThemeResourceBean themeResourceBean = (DodolThemeResourceBean) new DodolThemeResourceParser()
					.autoParseAppThemeXml(mContext, themePackage, false);
			ConcurrentHashMap<Integer, ThemeBean> tmpMap = themeResourceBean.getThemeResourceMap();
			if (tmpMap == null) {
				return false;
			}
			AppDataThemeBean appThemeBean = (AppDataThemeBean) tmpMap
					.get(ThemeBean.THEMEBEAN_TYPE_APPDATA);
			appThemeBean.getFilterAppsMap().putAll(themeBean.getFilterAppsMap());
			themeBean = null;
			if (tmpMap != null && tmpMap.size() > 0) {
				if (mCurThemeBeansMap != null) {
					mCurThemeBeansMap.clear();
				}
				mCurThemeBeansMap = tmpMap;
				return true;
			}
			return false;
		} else if (isAtomTheme(themePackage)) {
			ConcurrentHashMap<Integer, ThemeBean> tmpMap = new ConcurrentHashMap<Integer, ThemeBean>();
			ThemeBean themeBean = (AppDataThemeBean) new AtomThemeIconParser()
					.autoParseAppThemeXml(mContext, themePackage, false);
			if (themeBean != null) {
				tmpMap.put(themeBean.getBeanType(), themeBean);
			} else {
				return false;
			}

			DeskThemeBean deskBean = null;
			if (infoBean != null && !infoBean.isHasWallpaper()) { //atom主题没有wallpaper.xml文件
				deskBean = new DeskThemeBean(themePackage);
				deskBean.mWallpaper.mResName = "img_wallpaper";
			} else {
				deskBean = (DeskThemeBean) new AtomThemeWallpaperParser().autoParseAppThemeXml(
						mContext, themePackage, false);
			}
			if (deskBean != null) {
				tmpMap.put(deskBean.getBeanType(), deskBean);
			} else {
				return false;
			}

			if (tmpMap != null && tmpMap.size() > 0) {
				if (mCurThemeBeansMap != null) {
					mCurThemeBeansMap.clear();
				}
				mCurThemeBeansMap = tmpMap;
				return true;
			}
			return false;
		}

		ConcurrentHashMap<Integer, ThemeBean> tmpThemeBeansMap = new ConcurrentHashMap<Integer, ThemeBean>();
		boolean isEncrypt = false;
		if (infoBean != null) {
			isEncrypt = infoBean.isEncrypt();
		}
		ThemeBean themeBean = null;
		String pkg = GoContentProviderUtil.getScreenStyleSetting(mContext, themePackage, Setting.ICONSTYLEPACKAGE);
		if (pkg == null) {
			pkg = themePackage;
		}
		if (!IGoLauncherClassName.DEFAULT_THEME_PACKAGE.equals(pkg)) {
			boolean iconEncrypt = isEncrypt;
			if (!pkg.equals(themePackage)) {
				ThemeInfoBean iconinfoBean = getThemeInfo(pkg);
				if (iconinfoBean != null) {
					iconEncrypt = iconinfoBean.isEncrypt();
				}
			}
			// 解析应用程序过滤器信息
			themeBean = getAppThemeParser()
					.autoParseAppThemeXml(mContext, pkg, iconEncrypt);
			if (themeBean != null) {
				tmpThemeBeansMap.put(themeBean.getBeanType(), themeBean);
			} else {
				// 解析失败
				return false;
			}
		}
		// 解析功能表中相关主题信息
		themeBean = new FuncThemeParser().autoParseAppThemeXml(mContext, themePackage, isEncrypt);
		if (themeBean != null) {
			tmpThemeBeansMap.put(themeBean.getBeanType(), themeBean);
		} else {
			// 解析失败
			return false;
		}

		// 解析桌面中相关主题信息
		themeBean = getDeskThemeParser().autoParseAppThemeXml(mContext, themePackage, isEncrypt);
		if (themeBean != null) {
			if (((DeskThemeBean) themeBean).mIndicator != null) {
				((DeskThemeBean) themeBean).mIndicator.setPackageName(themePackage);
			}
			if (((DeskThemeBean) themeBean).mScreen != null
					&& ((DeskThemeBean) themeBean).mScreen.mFolderStyle != null) {
				((DeskThemeBean) themeBean).mScreen.mFolderStyle.mPackageName = themePackage;
			}
			tmpThemeBeansMap.put(themeBean.getBeanType(), themeBean);
		} else {
			// 解析失败
			return false;
		}

		// 成功解析，则修改当前内存中主题bean的map中的值
		if (mCurThemeBeansMap != null) {
			mCurThemeBeansMap.clear();
		}
		mCurThemeBeansMap = tmpThemeBeansMap;
		Log.i("ThemeManager", "parserTheme is over");
		return true;
	}

	/**
	 * 解析各个包中主题信息
	 * 
	 * @author huyong
	 * @param themePackage
	 */
	private ThemeInfoBean parserThemeInfo(String themePackage, ThemeInfoBean themeInfoBean) {
		if (isDodolTheme(themePackage)) { //dodol主题
			return parseDodolThemeInfo(themePackage, themeInfoBean);
		} else if (isAtomTheme(themePackage)) { //Atom主题
			return parseAtomThemeInfo(themePackage, themeInfoBean);
		}

		boolean isUsedDefault3 = false;
		String newThemeFileName;
		String fileName;
		if (IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3.equals(themePackage)) {
			isUsedDefault3 = true;
			newThemeFileName = ThemeConfig.NEWTHEMECFGFILENAME_3; // 默认3.0大主题配置信息文件名
			fileName = ThemeConfig.THEMECFGFILENAME_3; // 默认3.0主题配置信息
		} else {
			newThemeFileName = ThemeConfig.NEWTHEMECFGFILENAME; // 大主题配置信息文件名
			fileName = ThemeConfig.THEMECFGFILENAME; // 主题配置信息
		}
		InputStream inputStream = XmlParserFactory.createInputStream(mContext, themePackage,
				newThemeFileName);
		XmlPullParser xmlPullParser = null;
		ThemeInfoBean themeBean = themeInfoBean;
		if (themeBean == null) {
			themeBean = new ThemeInfoBean();
		}

		if (inputStream != null) {
			xmlPullParser = XmlParserFactory.createXmlParser(inputStream);
		} else {
			xmlPullParser = XmlParserFactory.createXmlParser(mContext, newThemeFileName,
					themePackage);
		}
		if (xmlPullParser != null) {
			// 大主题配置文件解析
			themeBean.setIsNewTheme(true);
			new ThemeInfoParser().parseNewThemecfg(xmlPullParser, themeBean);
		}

		inputStream = XmlParserFactory.createInputStream(mContext, themePackage, fileName);
		if (inputStream != null) {
			xmlPullParser = XmlParserFactory.createXmlParser(inputStream);
		} else {
			xmlPullParser = XmlParserFactory.createXmlParser(mContext, fileName, themePackage);
		}
		if (xmlPullParser == null) {
			return null;
		}

		// 解析基本信息
		IParser parser = new ThemeInfoParser();
		themeBean.setPackageName(themePackage);
		parser.parseXml(xmlPullParser, themeBean);

		// 关闭inputStream
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 由于需要提示信息国际化，因此，此处需要通过resource来从主题包中自动适配获取相关文字
		// 上处ThemeInfoParser的parseXml中可以去除多余的针对themeinfo和themetitle的解析
		try {
			String themeTitleResId = THEME_TITLE;
			String themeInfoResId = THEME_INFO;
			if (isUsedDefault3) {
				themePackage = IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
				themeTitleResId = THEME_TITLE_3;
				themeInfoResId = THEME_INFO_3;

			}
			Resources resources = null;
			if (GoAppUtils.isAppExist(mContext, themePackage)) {
				resources = mContext.getPackageManager().getResourcesForApplication(themePackage);
			} else {
				resources = ZipResources.getThemeResourcesFromReflect(mContext, themePackage); // mZipRes.getThemeInfoItem(themePackage,
																								// themeTitleResId);
			}
			int resId = resources.getIdentifier(themeTitleResId, "string", themePackage);
			String themeTitle = resources.getString(resId);
			themeBean.setThemeName(themeTitle);
			resId = resources.getIdentifier(themeInfoResId, "string", themePackage);
			String themeInfo = resources.getString(resId);
			themeBean.setThemeInfo(themeInfo);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		parser = null;
		return themeBean;
	}

	/**
	 * 解析dodol主题
	 * added by liulixia
	 * @param themePackage
	 * @param themeInfoBean
	 * @return
	 */
	private ThemeInfoBean parseDodolThemeInfo(String themePackage, ThemeInfoBean themeInfoBean) {
		String fileName = ThemeConfig.DODOLTHEMEINFO;
		XmlPullParser xmlPullParser = null;
		ThemeInfoBean themeBean = themeInfoBean;
		if (themeBean == null) {
			themeBean = new ThemeInfoBean();
		}

		InputStream inputStream = XmlParserFactory.createInputStream(mContext, themePackage,
				fileName);
		if (inputStream != null) {
			xmlPullParser = XmlParserFactory.createXmlParser(inputStream);
		} else {
			xmlPullParser = XmlParserFactory.createXmlParser(mContext, fileName, themePackage);
		}
		if (xmlPullParser == null) {
			return null;
		}

		// 解析基本信息
		ThemeInfoParser parser = new ThemeInfoParser();
		themeBean.setPackageName(themePackage);
		parser.parseDodolXml(xmlPullParser, themeBean);

		// 关闭inputStream
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		parser = null;
		return themeBean;
	}

	/**
	 * 解析Atom主题
	 * added by liulixia
	 * @param themePackage
	 * @param themeInfoBean
	 * @return
	 */
	private ThemeInfoBean parseAtomThemeInfo(String themePackage, ThemeInfoBean themeInfoBean) {
		String fileName = ThemeConfig.ATOMTHEMEINFO;
		XmlPullParser xmlPullParser = null;
		ThemeInfoBean themeBean = themeInfoBean;
		if (themeBean == null) {
			themeBean = new ThemeInfoBean();
		}

		InputStream inputStream = XmlParserFactory.createInputStream(mContext, themePackage,
				fileName);
		if (inputStream != null) {
			xmlPullParser = XmlParserFactory.createXmlParser(inputStream);
		} else {
			xmlPullParser = XmlParserFactory.createXmlParser(mContext, fileName, themePackage);
		}
		if (xmlPullParser == null) {
			return null;
		}

		// 解析基本信息
		ThemeInfoParser parser = new ThemeInfoParser();
		themeBean.setPackageName(themePackage);
		parser.parseAtomXml(xmlPullParser, themeBean);

		// 关闭inputStream
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		parser = null;
		return themeBean;
	}

	/**
	 * 返回主题信息
	 * 
	 * @author huyong
	 * @param themepackage
	 */
	public ThemeInfoBean getThemeInfo(String themepackage) {
		if (themepackage == null) {
			return null;
		}
		if (mAllInstalledThemeInfosMap == null
				|| mAllInstalledThemeInfosMap.get(themepackage) == null) {
			// TODO:返回默认主题包
			return parserThemeInfo(themepackage, null);
		}
		return mAllInstalledThemeInfosMap.get(themepackage);
	}

	/**
	 * 根据类型来获取不同ui所需要的bean
	 * 
	 * @author huyong
	 * @param beanType
	 * @return
	 */
	public ThemeBean getThemeBean(int beanType) {
		if (beanType < 0 || mCurThemeBeansMap == null) {
			return null;
		}

		return mCurThemeBeansMap.get(beanType);
	}

	/**
	 * 扫描当前系统程序中主题包的数量
	 * 
	 * @author huyong
	 * @param isAsynScan
	 *            是否异步扫描
	 */
	private synchronized ConcurrentHashMap<String, ThemeInfoBean> scanInitAllInstalledThemes(
			boolean isAsynScan) {
		if (mAllInstalledThemeInfosMap != null) {
			mAllInstalledThemeInfosMap.clear();
			mAllInstalledThemeInfosMap = null;
		}
		mAllInstalledThemeInfosMap = new ConcurrentHashMap<String, ThemeInfoBean>();

		Intent intent = new Intent(ICustomAction.ACTION_MAIN_THEME_PACKAGE);
		intent.addCategory(THEME_CATEGORY);
		PackageManager pm = mContext.getPackageManager();

		ThemeInfoBean defaultInfoBean = parserThemeInfo(IGoLauncherClassName.DEFAULT_THEME_PACKAGE, null);
		String loadingThemeName = mContext.getString(R.string.loading);
		List<ResolveInfo> themes = pm.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo resolveInfo : themes) {
			String appPackageName = resolveInfo.activityInfo.packageName.toString();
			ThemeInfoBean themeInfoBean = new ThemeInfoBean(defaultInfoBean);
			themeInfoBean.clearPreviewName();
			themeInfoBean.setPackageName(appPackageName);
			themeInfoBean.setThemeName(loadingThemeName);
			if (appPackageName != null && themeInfoBean != null) {
				mAllInstalledThemeInfosMap.put(appPackageName, themeInfoBean);
			}
		}
		ConcurrentHashMap<String, ThemeInfoBean> tmpMap = scanAllZipThemes();
		if (tmpMap != null && tmpMap.size() > 0) {
			mAllInstalledThemeInfosMap.putAll(tmpMap);
		}

		//添加dodol主题
		tmpMap = scanAllDodolThemes(defaultInfoBean, loadingThemeName);
		if (tmpMap != null && tmpMap.size() > 0) {
			mAllInstalledThemeInfosMap.putAll(tmpMap);
		}

		//添加atom主题
		tmpMap = scanAllAtomThemes(defaultInfoBean, loadingThemeName);
		if (tmpMap != null && tmpMap.size() > 0) {
			mAllInstalledThemeInfosMap.putAll(tmpMap);
		}

		// 添加默认主题
		if (defaultInfoBean != null) {
			mAllInstalledThemeInfosMap.put(IGoLauncherClassName.DEFAULT_THEME_PACKAGE, defaultInfoBean);
		}

		// TODO:首先判断是否存在3.1以上版本的主题
		if (!GoAppUtils.isAppExist(mContext, IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER)) {
			// 添加默认3.0主题 add by yangbing 4-28
			ThemeInfoBean defaultInfoBean3 = parserThemeInfo(IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3, null);
			if (defaultInfoBean3 != null) {
				mAllInstalledThemeInfosMap.put(IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3, defaultInfoBean3);
			}
		}
		if (isAsynScan) {
			// 异步加载
			// 重新生成一份新的map，避免多线程操作map引起非法操作异常。
			final ConcurrentHashMap<String, ThemeInfoBean> themeInfosMap = new ConcurrentHashMap<String, ThemeInfoBean>(
					mAllInstalledThemeInfosMap);

			Thread thread = new Thread(ThreadName.SCAN_INSTALLED_THEMES) {
				@Override
				public void run() {
					if (themeInfosMap != null) {
						parserAllThemeInfos(themeInfosMap);
					}
				}
			};
			thread.setPriority(Thread.NORM_PRIORITY - 2);
			thread.start();

		} else {
			// 同步加载
			parserAllThemeInfos(mAllInstalledThemeInfosMap);

		}
		return new ConcurrentHashMap<String, ThemeInfoBean>(mAllInstalledThemeInfosMap); // 返回一份克隆，防止多线程操作
	}

	/**
	 * 获取所有韩国dodol桌面主题
	 * @return
	 */
	private ConcurrentHashMap<String, ThemeInfoBean> scanAllDodolThemes(
			ThemeInfoBean defaultInfoBean, String loadingThemeName) {
		ConcurrentHashMap<String, ThemeInfoBean> dodolThemesHashMap = null;
		Intent intent = new Intent(ICustomAction.ACTION_DODOL_THEME_PACKAGE);
		intent.addCategory(THEME_CATEGORY);
		PackageManager pm = mContext.getPackageManager();
		List<ResolveInfo> themes = pm.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		if (themes == null || themes.size() == 0) {
			return dodolThemesHashMap;
		}

		dodolThemesHashMap = new ConcurrentHashMap<String, ThemeInfoBean>();
		if (mAllInstalledDodolThemeInfos == null) {
			mAllInstalledDodolThemeInfos = new Vector<String>();
		} else {
			mAllInstalledDodolThemeInfos.clear();
		}
		for (ResolveInfo resolveInfo : themes) {
			String appPackageName = resolveInfo.activityInfo.packageName.toString();
			ThemeInfoBean themeInfoBean = new ThemeInfoBean(defaultInfoBean);
			themeInfoBean.clearPreviewName();
			themeInfoBean.setPackageName(appPackageName);
			themeInfoBean.setThemeName(loadingThemeName);
			if (appPackageName != null && themeInfoBean != null) {
				dodolThemesHashMap.put(appPackageName, themeInfoBean);
				mAllInstalledDodolThemeInfos.add(appPackageName);
			}
		}
		return dodolThemesHashMap;
	}

	/**
	 * 获取所有的atom主题
	 * @param defaultInfoBean
	 * @param loadingThemeName
	 * @return
	 */
	private ConcurrentHashMap<String, ThemeInfoBean> scanAllAtomThemes(
			ThemeInfoBean defaultInfoBean, String loadingThemeName) {
		ConcurrentHashMap<String, ThemeInfoBean> atomThemesHashMap = null;
		Intent intent = new Intent(ICustomAction.ACTION_ATOM_THEME_PACKAGE);
		PackageManager pm = mContext.getPackageManager();
		List<ResolveInfo> themes = pm.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
		if (themes == null || themes.size() == 0) {
			return atomThemesHashMap;
		}

		atomThemesHashMap = new ConcurrentHashMap<String, ThemeInfoBean>();
		if (mAllInstalledAtomThemeInfos == null) {
			mAllInstalledAtomThemeInfos = new Vector<String>();
		} else {
			mAllInstalledAtomThemeInfos.clear();
		}
		for (ResolveInfo resolveInfo : themes) {
			String appPackageName = resolveInfo.activityInfo.packageName.toString();
			ThemeInfoBean themeInfoBean = new ThemeInfoBean(defaultInfoBean);
			themeInfoBean.clearPreviewName();
			themeInfoBean.setPackageName(appPackageName);
			themeInfoBean.setThemeName(loadingThemeName);
			if (appPackageName != null && themeInfoBean != null) {
				atomThemesHashMap.put(appPackageName, themeInfoBean);
				mAllInstalledAtomThemeInfos.add(appPackageName);
			}
		}
		return atomThemesHashMap;
	}

	private void parserAllThemeInfos(ConcurrentHashMap<String, ThemeInfoBean> themeInfosMap) {
		if (themeInfosMap != null && themeInfosMap.size() > 0) {
			Iterator<Entry<String, ThemeInfoBean>> it = themeInfosMap.entrySet().iterator();
			while (it != null && it.hasNext()) {
				Entry<String, ThemeInfoBean> entry = it.next();
				String appPackageName = entry.getKey();
				ThemeInfoBean themeInfoBean = entry.getValue();
				if (parserThemeInfo(appPackageName, themeInfoBean) == null) {
					themeInfosMap.remove(appPackageName);
				}

			}
		}

	}

	/**
	 * 判断主题包是否安装,如果是go主题包，首先判断文件是否存在， 还需要判断是否付费，付了费才算存在
	 * 
	 * @author huyong
	 * @param packageName
	 * @return
	 */
	public static boolean isInstalledTheme(Context context, String packageName) {
		boolean isVip = ThemePurchaseManager.getCustomerLevel(context) == ThemeConstants.CUSTOMER_LEVEL0
				? false
				: true;
		if (GoAppUtils.isAppExist(context, packageName)
				|| ((ZipResources.isZipThemeExist(packageName) || LockerManager
						.getInstance(context).isZipTheme(context, packageName)) && (isVip
						|| PurchaseStateManager.query(context, packageName) != null || SDEBUG))) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否使用主题
	 * 
	 * @author huyong
	 * @return true for 使用其他主题，false for 默认主题
	 */
	public boolean isUsedTheme() {
		boolean result = false;
		if (mCurThemeInfo != null) {
			result = !(IGoLauncherClassName.DEFAULT_THEME_PACKAGE.equals(mCurThemeInfo.getPackageName()));
		}
		return result;

	}

	public static boolean canBeUsedTheme(Context context, String pkgName) {
		boolean result = false;
		int level = ThemePurchaseManager.getCustomerLevel(context);
		result = level > ThemeConstants.CUSTOMER_LEVEL0;
		if (!result) {
			Context otherAppsContext = null;
			try {
				otherAppsContext = context.createPackageContext(pkgName,
						Context.CONTEXT_IGNORE_SECURITY);
			} catch (NameNotFoundException e) {
				return false;
			}
			PreferencesManager preferences = new PreferencesManager(otherAppsContext, pkgName,
					Context.MODE_WORLD_READABLE);
			boolean isPurchased = preferences.getBoolean(IAPKEY, false);
			String appPkg = preferences.getString(GETJAR, DEFALUT_PKG);
			if (isPurchased || GoAppUtils.isAppExist(context, appPkg)) {
				result = true;
			} else {
				result = PurchaseStateManager.isPay(context, pkgName);
			}
		}
		return result;
	}

	public DrawResourceThemeBean getThemeResrouceBean(String themePackageName) {
		// 解析图片资源信息
		if (themePackageName == null) {
			return null;
		}
		ThemeInfoBean infoBean = getThemeInfo(themePackageName);
		String type = infoBean.getThemeType();
		boolean bZip = getThemeInfo(themePackageName).isZipTheme();
		if (!bZip && null != type && type.equals(ThemeInfoBean.THEMETYPE_GETJAR)
				&& !canBeUsedTheme(mContext, themePackageName)) {
//			Intent intent = new Intent();
//			intent = mContext.getPackageManager().getLaunchIntentForPackage(themePackageName);
//			mContext.startActivity(intent);
			return null;
		}
		DrawResourceThemeBean themeBean = null;
		String fileName = ThemeConfig.DRAWRESOURCEFILENAME;
		Log.i("ThemeManager", "begin parserTheme " + fileName);
		InputStream inputStream = createParserInputStream(themePackageName, fileName);
		XmlPullParser xmlPullParser = null;
		if (inputStream != null) {
			xmlPullParser = XmlParserFactory.createXmlParser(inputStream);
		} else {
			xmlPullParser = XmlParserFactory.createXmlParser(ApplicationProxy.getApplication(),
					fileName, themePackageName);
		}
		if (xmlPullParser != null) {
			IParser parser = new DrawResourceThemeParser();
			themeBean = new DrawResourceThemeBean();
			parser.parseXml(xmlPullParser, themeBean);
			if (themeBean != null) {
				themeBean.setPackageName(themePackageName);
			}
			parser = null;
		}
		// 关闭inputStream
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//该功能暂时不上
		//		if(themeBean != null){
		////			if(canBeUsedTheme(mContext, themePackageName)){
		//				DrawResourceThemeBean upgradeBean = null;
		//				fileName = ThemeConfig.DRAWRESOURCEFILENAME_UPGRADE;
		//				inputStream = createParserInputStream(themePackageName, fileName);
		//				xmlPullParser = null;
		//				if (inputStream != null) {
		//					xmlPullParser = XmlParserFactory.createXmlParser(inputStream);
		//				} else {
		//					xmlPullParser = XmlParserFactory.createXmlParser(ApplicationProxy.getApplication(),
		//							fileName, themePackageName);
		//				}
		//				if (xmlPullParser != null) {
		//					IParser parser = new DrawResourceThemeParser();
		//					upgradeBean = new DrawResourceThemeBean();
		//					parser.parseXml(xmlPullParser, upgradeBean);
		//					if (upgradeBean != null) {
		//						upgradeBean.setPackageName(themePackageName);
		//					}
		//					parser = null;
		//				}
		//				// 关闭inputStream
		//				if (inputStream != null) {
		//					try {
		//						inputStream.close();
		//					} catch (IOException e) {
		//						// TODO Auto-generated catch block
		//						e.printStackTrace();
		//					}
		//				}
		//				if(upgradeBean != null && upgradeBean.getDrawrResourceList() != null && !upgradeBean.getDrawrResourceList().isEmpty()){
		//					themeBean.getDrawrResourceList().addAll(upgradeBean.getDrawrResourceList());
		//				}
		////			}
		//		}
		//end
		return themeBean;
	}

	/**
	 * <br>功能简述:判断该套主题是否已经付费
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param themePackageName
	 * @return
	 */
	public boolean getThemeResrouceBeanisPay(String themePackageName) {
        
        boolean result = false;
        
        // 解析图片资源信息
        if (themePackageName == null) {
            return result;
        }
        ThemeInfoBean infoBean = getThemeInfo(themePackageName);
        String type = null;
        boolean bZip = false;
		if (infoBean != null) {
			type = infoBean.getThemeType();
			bZip = infoBean.isZipTheme();
		}
        if (!bZip && null != type && type.equals(ThemeInfoBean.THEMETYPE_GETJAR)
                && !canBeUsedTheme(mContext, themePackageName)) {
//          Intent intent = new Intent();
//          intent = mContext.getPackageManager().getLaunchIntentForPackage(themePackageName);
//          mContext.startActivity(intent);
            
            return result;
        }
        
        result = true;
        
        return result;
    }
	
	/**
	 * 获取当前主题包的resource
	 * 
	 * @author huyong
	 * @return
	 */
	public Resources getCurThemeResources() {
		String curThemePackage = null;
		if (mCurThemeInfo != null) {
			curThemePackage = mCurThemeInfo.getPackageName();
		}
		return getThemeResources(curThemePackage);

	}

	/**
	 * 获取指定主题包的resource
	 * 
	 * @author huyong
	 * @param themePackage
	 * @return
	 */
	public Resources getThemeResources(String themePackage) {
		if (themePackage == null) {
			return null;
		}
		if (IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3.equals(themePackage)) {
			themePackage = IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
		}
		Resources resources = null;
		try {
			if (GoAppUtils.isAppExist(mContext, themePackage)) {
				resources = mContext.getPackageManager().getResourcesForApplication(themePackage);
			} else {
				resources = ZipResources.getThemeResourcesFromReflect(mContext, themePackage);
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resources;
	}

	public MenuBean getGGmenuBean(String themePackage) {
		if (null == themePackage) {
			return null;
		}
		if (IGoLauncherClassName.DEFAULT_THEME_PACKAGE.equals(themePackage)) {
			return null;
		}
		DeskThemeBean themeBean = null;
		if (null != getThemeBean(ThemeBean.THEMEBEAN_TYPE_DESK)) {
			themeBean = (DeskThemeBean) getThemeBean(ThemeBean.THEMEBEAN_TYPE_DESK);
			if (themeBean.mDeskMenuBean != null && themeBean.mDeskMenuBean.mPackageName != null
					&& themeBean.mDeskMenuBean.mPackageName.equals(themePackage)) {
				return themeBean.mDeskMenuBean;
			}
		}
		// 解析桌面中相关主题信息
		String fileName = ThemeConfig.DESKTHEMEFILENAME;
		InputStream inputStream = createParserInputStream(themePackage, fileName);
		XmlPullParser xmlPullParser = null;
		if (inputStream != null) {
			xmlPullParser = XmlParserFactory.createXmlParser(inputStream);
		} else {
			xmlPullParser = XmlParserFactory.createXmlParser(ApplicationProxy.getApplication(),
					fileName, themePackage);
		}
		themeBean = new DeskThemeBean(themePackage);
		if (xmlPullParser != null) {
			DeskThemeParser parser = new DeskThemeParser();
			parser.parseXmlToDeskMenuBean(xmlPullParser, themeBean);
			if (themeBean.mDeskMenuBean != null && mCurThemeBeansMap != null) {
				themeBean.mDeskMenuBean.mPackageName = themePackage;
				mCurThemeBeansMap.put(ThemeBean.THEMEBEAN_TYPE_DESK, themeBean);
			} else if (themeBean.mDeskMenuBean == null) {
				themeBean.mDeskMenuBean = themeBean.new MenuBean();
			}
			parser = null;
		}
		// 关闭inputStream
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return themeBean.mDeskMenuBean;
	}

	/**
	 * 判断是否是dodol主题
	 * @param themePackage
	 * @return
	 */
	public boolean isDodolTheme(String themePackage) {
		if (mAllInstalledDodolThemeInfos == null || mAllInstalledDodolThemeInfos.size() == 0) {
			Intent intent = new Intent(ICustomAction.ACTION_DODOL_THEME_PACKAGE);
			intent.addCategory(THEME_CATEGORY);
			PackageManager pm = mContext.getPackageManager();
			List<ResolveInfo> themes = pm.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
			if (themes == null || themes.size() == 0) {
				return false;
			}

			if (mAllInstalledDodolThemeInfos == null) {
				mAllInstalledDodolThemeInfos = new Vector<String>();
			}
			for (ResolveInfo resolveInfo : themes) {
				String appPackageName = resolveInfo.activityInfo.packageName.toString();
				if (appPackageName != null) {
					mAllInstalledDodolThemeInfos.add(appPackageName);
				}
			}
		}
		if (mAllInstalledDodolThemeInfos != null
				&& mAllInstalledDodolThemeInfos.contains(themePackage)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是atom主题
	 * @param themePackage
	 * @return
	 */
	public boolean isAtomTheme(String themePackage) {
		if (themePackage == null) {
			return false;
		}
		if (mAllInstalledAtomThemeInfos == null || mAllInstalledAtomThemeInfos.size() == 0) {
			Intent intent = new Intent(ICustomAction.ACTION_ATOM_THEME_PACKAGE);
			PackageManager pm = mContext.getPackageManager();
			List<ResolveInfo> themes = pm.queryIntentActivities(intent,
					PackageManager.GET_ACTIVITIES);
			if (themes == null || themes.size() == 0) {
				return false;
			}

			if (mAllInstalledAtomThemeInfos == null) {
				mAllInstalledAtomThemeInfos = new Vector<String>();
			}
			for (ResolveInfo resolveInfo : themes) {
				String appPackageName = resolveInfo.activityInfo.packageName.toString();
				if (appPackageName != null) {
					mAllInstalledAtomThemeInfos.add(appPackageName);
				}
			}
		}
		if (mAllInstalledAtomThemeInfos != null
				&& mAllInstalledAtomThemeInfos.contains(themePackage)) {
			return true;
		}
		return false;
	}

	/**
	 * 根据主题包名得到此主题包的DockBean
	 * 
	 * @param themeName
	 *            主题包名
	 * @return
	 */
	public DockBean getDockBeanFromTheme(String themePackage) {
		if (null == themePackage) {
			return null;
		}

		// 解析桌面中相关主题信息
		String fileName = ThemeConfig.DESKTHEMEFILENAME;

		if (isDodolTheme(themePackage)) {
			fileName = ThemeConfig.DODOLTHEMERESOURCE;
		}
		InputStream inputStream = createParserInputStream(themePackage, fileName);
		XmlPullParser xmlPullParser = null;
		if (inputStream != null) {
			xmlPullParser = XmlParserFactory.createXmlParser(inputStream);
		} else {
			xmlPullParser = XmlParserFactory.createXmlParser(ApplicationProxy.getApplication(),
					fileName, themePackage);
		}
		DeskThemeBean themeBean = new DeskThemeBean(themePackage);
		if (xmlPullParser != null) {
			if (isDodolTheme(themePackage)) {
				DodolThemeResourceParser parser = new DodolThemeResourceParser();
				parser.parseXmlToDockBeanPics(xmlPullParser, themeBean);
				parser = null;
			} else {
				DeskThemeParser parser = new DeskThemeParser();
				parser.parseXmlToDockBeanPics(xmlPullParser, themeBean);
				parser = null;
			}
		}

		// 关闭inputStream
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return themeBean.mDock;
	}

	@Override
	public void cleanup() {
		// clearAllObserver();
	}

	/**
	 * 是否当前默认主题处理， 3.0主题需要使用默认主题中资源，故而提供该接口为外部使用
	 * 
	 * @param pkgName
	 * @return
	 */
	public static boolean isAsDefaultThemeToDo(final String pkgName) {
		return IGoLauncherClassName.DEFAULT_THEME_PACKAGE.equals(pkgName) 
				|| IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3.equals(pkgName)
				|| IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER.equals(pkgName);
	}

	/**
	 * 扫描所有已付费的主题
	 * 
	 * @return
	 */
	public ConcurrentHashMap<String, ThemeInfoBean> scanAllZipThemes() {
		ConcurrentHashMap<String, ThemeInfoBean> zipHashMap = null;
		File dir = new File(ZipResources.ZIP_THEME_PATH);
		if (dir.exists()) {
			FilenameFilter filter = new FilenameFilter() {
				private Pattern mPattern = Pattern.compile("go");

				@Override
				public boolean accept(File dir, String filename) {
					// TODO Auto-generated method stub
					String nameString = new File(filename).getName();
					String postfix = nameString.substring(nameString.lastIndexOf(".") + 1);
					return mPattern.matcher(postfix).matches();
				}
			};
			String[] zipThemeNames = dir.list(filter);
			ThemeInfoBean defaultInfoBean = parserThemeInfo(IGoLauncherClassName.DEFAULT_THEME_PACKAGE, null);
			String loadingThemeName = mContext.getString(R.string.loading);
			int level = ThemePurchaseManager.getCustomerLevel(mContext);
			if (zipThemeNames != null && zipThemeNames.length > 0) {
				zipHashMap = new ConcurrentHashMap<String, ThemeInfoBean>();
				for (int i = 0; i < zipThemeNames.length; i++) {
					String fileName = zipThemeNames[i];
					String packageName = ZipResources
							.getThemePkgFromReflect(ZipResources.ZIP_THEME_PATH + fileName);
					if (packageName == null
							|| !packageName.contains(ThemeConstants.LAUNCHER_THEME_PREFIX)
							|| (mAllInstalledThemeInfosMap != null && mAllInstalledThemeInfosMap
									.get(packageName) != null)) {
						continue;
					}
					if (level == ThemeConstants.CUSTOMER_LEVEL0 && !SDEBUG
							&& PurchaseStateManager.query(mContext, packageName) == null) {
						continue;
					}
					ThemeInfoBean themeInfoBean = new ThemeInfoBean(defaultInfoBean);
					themeInfoBean.clearPreviewName();
					themeInfoBean.setPackageName(packageName);
					themeInfoBean.setThemeName(loadingThemeName);
					themeInfoBean.setIsZipTheme(true);
					zipHashMap.put(packageName, themeInfoBean);
				}
			}

		}
		return zipHashMap;
	}

	public DeskFolderThemeBean parserDeskFolderTheme(String themePackage) {
		if (ThemeManager.getInstance(ApplicationProxy.getContext()).isAtomTheme(themePackage)) {
			return null;
		}
		String fileName = ThemeConfig.DESKTHEMEFILENAME;
		boolean isDodolTheme = ThemeManager.getInstance(ApplicationProxy.getContext()).isDodolTheme(
				themePackage);
		if (isDodolTheme) {
			fileName = ThemeConfig.DODOLTHEMERESOURCE;
		}
		XmlPullParser xmlPullParser = null;
		DeskFolderThemeBean themeBean = null;
		InputStream inputStream = createParserInputStream(themePackage, fileName);
		if (inputStream != null) {
			xmlPullParser = XmlParserFactory.createXmlParser(inputStream);
		} else {
			xmlPullParser = XmlParserFactory.createXmlParser(ApplicationProxy.getApplication(),
					fileName, themePackage);
		}
		if (xmlPullParser != null) {
			themeBean = new DeskFolderThemeBean(themePackage);
			if (isDodolTheme) {
				DodolThemeResourceParser parser = new DodolThemeResourceParser();
				parser.parseDeskFolderTheme(xmlPullParser, themeBean);
				parser = null;
			} else {
				IParser parser = new DeskFolderThemeParser();
				parser.parseXml(xmlPullParser, themeBean);
				parser = null;
			}
		}
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return themeBean;
	}

	/**
	 * 统一在这里创建方便管理
	 * 
	 * @param packageName
	 * @param fileName
	 * @return
	 */
	public InputStream createParserInputStream(String packageName, String fileName) {
		InputStream inputStream = null;
		ThemeInfoBean infoBean = getThemeInfo(packageName);
		if (null != infoBean && infoBean.isEncrypt()) {
			inputStream = XmlParserFactory.createEncryptXmlInputStream(mContext, packageName,
					fileName);
		} else {
			inputStream = XmlParserFactory.createInputStream(mContext, packageName, fileName);
		}
		return inputStream;

	}

	public static void uninstallZipTheme(String packageName) {
		ZipResources.deleteTheme(packageName);
	}

	/**
	 * 获取完整的主题信息
	 * 
	 * @param themePackage
	 * @param themeInfoBean
	 * @return
	 */
	public ThemeInfoBean getThemeInfo(String themepackage, ThemeInfoBean themeInfoBean) {
		if (themepackage == null) {
			return null;
		}
		if (mAllInstalledThemeInfosMap == null
				|| mAllInstalledThemeInfosMap.get(themepackage) == null) {
			// TODO:返回默认主题包
			return parserThemeInfo(themepackage, themeInfoBean);
		}
		return mAllInstalledThemeInfosMap.get(themepackage);
	}

	public void addNotifyBean(ThemeNotifyBean bean) {
		if (mNotifyBeans == null) {
			mNotifyBeans = new ArrayList<ThemeNotifyBean>();
		}
		for (int i = 0; i < mNotifyBeans.size(); i++) {
			if (mNotifyBeans.get(i).getType() == bean.getType()) {
				mNotifyBeans.remove(i);
			}
		}
		mNotifyBeans.add(bean);
	}

	public ArrayList<ThemeNotifyBean> getNotifyBean() {
		return mNotifyBeans;
	}

	public void removeNotifyBean(int type) {
		if (mNotifyBeans != null) {
			for (int i = 0; i < mNotifyBeans.size(); i++) {
				ThemeNotifyBean bean = mNotifyBeans.get(i);
				if (bean.getType() == type) {
					mNotifyBeans.remove(i);
					break;
				}
			}
		}
		deleteNotifyCatch(type);
	}

	private void deleteNotifyCatch(int type) {
		String xmlFile = null;
		if (type == ThemeConstants.LOCKER_FEATURED_THEME_ID) {
			xmlFile = ThemeManager.LOCKER_NOTIFY_DATA_BEAN_XML;
		} else if (type == ThemeConstants.LAUNCHER_FEATURED_THEME_ID) {
			xmlFile = ThemeManager.FEATURED_NOTIFY_DATA_BEAN_XML;
		} else if (type == ThemeConstants.LAUNCHER_HOT_THEME_ID) {
			xmlFile = ThemeManager.HOT_NOTIFY_DATA_BEAN_XML;
		}
		if (xmlFile != null) {
			FileUtil.deleteFile(xmlFile);
		}
	}

	/**
	 * <br>功能简述:检查是否有通知栏缓存
	 * <br>功能详细描述:
	 * <br>注意:异步方法
	 */
	public void parseNotifyAllData() {
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ThemeInfoParser parse = new ThemeInfoParser();
				ThemeNotifyBean bean = null;
				bean = parse.parseNotifyData(ThemeManager.LOCKER_NOTIFY_DATA_BEAN_XML);
				if (bean != null) {
					addNotifyBean(bean);
				}
				bean = parse.parseNotifyData(ThemeManager.LOCKER_NOTIFY_DATA_BEAN_XML);
				if (bean != null) {
					addNotifyBean(bean);
				}
				bean = parse.parseNotifyData(ThemeManager.LOCKER_NOTIFY_DATA_BEAN_XML);
				if (bean != null) {
					addNotifyBean(bean);
				}
			}
		}.start();
	}

	/**
	 * ==================================================================================
	 * 以下2个方法提供给主题升级所用
	 */
	private boolean mIsDownloading = false;

	private IParser mAppThemeParser;

	private IParser mDeskThemeParser;

	private void showUpdateDialog(ThemeInfoBean themeInfoBean) {

		final ThemeInfoBean infoBean = themeInfoBean;
		new AlertDialog.Builder(mContext).setTitle(R.string.theme_pages_update)
				.setMessage(R.string.fav_content).setCancelable(false)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int i) {
						// ftp下载
						GoStoreOperatorUtil.downloadFileDirectly(mContext, infoBean.getThemeName(),
								LauncherEnv.Url.UI_3NEW_THEME_URL, 0, infoBean.getPackageName(),
								null, 0, null);
						mIsDownloading = true;

						Toast.makeText(mContext, mContext.getString(R.string.theme_download_tips),
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();
	}

	public void goUpdateDownload(ThemeInfoBean InfoBean) {

		// 首先判断是否为200渠道 ，如果是200渠道就跳到电子市场
		if (Statistics.is200ChannelUid(mContext)) { // 国外

			if (GoAppUtils.isMarketExist(mContext)) {
				// 跳转到market
				GoAppUtils.gotoMarket(mContext, MarketConstant.APP_DETAIL
						+ IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER);
			} else {
				// DeskToast.makeText(getContext(),
				// R.string.no_googlemarket_tip,
				// Toast.LENGTH_SHORT).show();
				// 跳转到网页版market
				GoStoreOperatorUtil.gotoBrowser(mContext, MarketConstant.BROWSER_APP_DETAIL
						+ IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER
						+ LauncherEnv.GOLAUNCHER_GOOGLE_REFERRAL_LINK);

			}

		} else { // 国内非200渠道，使用ftp下载
			// 弹出提示内容，提示用户下载
			if (!mIsDownloading) {
				showUpdateDialog(InfoBean);
			} else {
				// 特殊处理， 华为 U8860 在使用FTP下载的时候，点击通知栏的下载项，华为U8860偶尔会出现下载项消失
				// 其实只是显示上消失了，后台数据还在处于暂停状态，用户再次点击检查更新，通知栏的下载项还是之前的进度
				GoStoreOperatorUtil.downloadFileDirectly(mContext, InfoBean.getThemeName(),
						LauncherEnv.Url.UI_3NEW_THEME_URL, 0, InfoBean.getPackageName(), null, 0,
						null);
				Toast.makeText(mContext, mContext.getString(R.string.theme_download_tips),
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	/**======================================================================================================*/
	
	
	public static boolean isInnerTheme(String packageName) {
		if (packageName == null || packageName.equals(IGoLauncherClassName.DEFAULT_THEME_PACKAGE)
				|| packageName.equals(IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3)
				|| packageName.equals(IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER)) {
			return true;
		}
		return false;
	}

	public ScreenStyleConfigInfo getScreenStyleSettingInfo() {
		if (null == mScreenStyleConfigInfo) {
			mScreenStyleConfigInfo = createScreenStyleConfig(mContext);
		}
		return mScreenStyleConfigInfo;
	}

	private ScreenStyleConfigInfo createScreenStyleConfig(Context context) {
		mScreenStyleConfigInfo = new ScreenStyleConfigInfo(context, this);
		return mScreenStyleConfigInfo;
	}
	
	public IParser getAppThemeParser() {
		if (mAppThemeParser == null) {
			mAppThemeParser = new AppThemeParser();
		}
		return mAppThemeParser;
	}

	public IParser getDeskThemeParser() {
		if (mDeskThemeParser == null) {
			mDeskThemeParser = new DeskThemeParser();
		}
		return mDeskThemeParser;
	}
}
