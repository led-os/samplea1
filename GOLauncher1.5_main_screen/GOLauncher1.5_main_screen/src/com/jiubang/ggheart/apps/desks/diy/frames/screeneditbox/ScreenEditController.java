package com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.gau.go.launcherex.R;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.util.AppUtils;
import com.go.util.SortUtils;
import com.go.util.device.Machine;
import com.go.util.file.FileUtil;
import com.go.util.graphics.BitmapUtility;
import com.go.util.graphics.DrawUtils;
import com.go.util.graphics.ImageUtil;
import com.go.util.graphics.effector.united.EffectorControler;
import com.go.util.graphics.effector.united.EffectorInfo;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IScreenFrameMsgId;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.appcenter.component.AppsManagementActivity;
import com.jiubang.ggheart.appgame.appcenter.help.AppsManagementConstants;
import com.jiubang.ggheart.appgame.base.component.MainViewGroup;
import com.jiubang.ggheart.apps.config.ChannelConfig;
import com.jiubang.ggheart.apps.config.GOLauncherConfig;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.OutOfMemoryHandler;
import com.jiubang.ggheart.apps.desks.diy.WallpaperDensityUtil;
import com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.bean.WallpaperItemInfo;
import com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.bean.WallpaperSubInfo;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeConstants;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.apps.gowidget.GoWidgetFinder;
import com.jiubang.ggheart.apps.gowidget.GoWidgetManager;
import com.jiubang.ggheart.apps.gowidget.GoWidgetProviderInfo;
import com.jiubang.ggheart.apps.gowidget.GowidgetSortUtil;
import com.jiubang.ggheart.apps.gowidget.InnerWidgetInfo;
import com.jiubang.ggheart.apps.gowidget.ScreenEditItemInfo;
import com.jiubang.ggheart.data.AppCore;
import com.jiubang.ggheart.data.info.GoWidgetBaseInfo;
import com.jiubang.ggheart.data.info.IItemType;
import com.jiubang.ggheart.data.info.ScreenSettingInfo;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.data.statistics.AppRecommendedStatisticsUtil;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.data.statistics.StatisticsData;
import com.jiubang.ggheart.data.statistics.realtiemstatistics.RealTimeStatisticsContants;
import com.jiubang.ggheart.data.theme.ImageExplorer;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.data.theme.adrecommend.AdElement;
import com.jiubang.ggheart.data.theme.adrecommend.AdHttpAdapter;
import com.jiubang.ggheart.data.theme.adrecommend.AdHttpAdapter.AdResponseData;
import com.jiubang.ggheart.data.theme.bean.DeskThemeBean;
import com.jiubang.ggheart.data.theme.bean.DeskThemeBean.MenuItemBean;
import com.jiubang.ggheart.data.theme.bean.ThemeInfoBean;
import com.jiubang.ggheart.data.theme.parser.ThemeInfoParser;
import com.jiubang.ggheart.data.theme.zip.ZipResources;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.switchwidget.GoSwitchWidgetUtils;

/**
 * 
 * @author zouguiquan
 *
 */
public class ScreenEditController {

	public static final int THEMETAB_DESK_THEME = 1; 				// 应用程序tab
	public static final int THEMETAB_LOCK = 2; 						// 文件夹tab
	public static final String GODYNAMICA_WALLPAPER = "com.go.dynamicawallpaper";
	public static final String DOWNLOAD_ITEM_PACKAGE = "com.go.screenedit.downitem";
	public static final String WALLPAPER_FILTER = "com.go.wallpaperfilter";

	private Paint mPaint;
	private PorterDuffXfermode mXfermode;
	private BitmapDrawable mIconMask;
	private BitmapDrawable mIconBase;
	private BitmapDrawable mDownloadMask;
	private Context mContext = null;
	private static ScreenEditController sInstance = null;
	private String mThemePkgName;

	//go快捷方式
	public static final int MAIN_SCREEN = 0;
	public static final int MAIN_SCREEN_OR_PREVIEW = 1;
	public static final int FUNCMENU = 2;
	public static final int NOTIFICATION = 3;
	public static final int STATUS_BAR = 4;
	public static final int THEME_SETTING = 5;
	public static final int PREFERENCES = 6;
	public static final int GO_STORE = 7;
	public static final int PREVIEW = 8;
	public static final int LOCK_SCREEN = 9;
	public static final int DOCK_BAR = 10;
	public static final int MAIN_MENU = 11;
	public static final int DIY_GESTURE = 12;
	public static final int PHOTO = 13;
	public static final int MUSIC = 14;
	public static final int VIDEO = 15;

	//壁纸一级
	public final static String GOWALLPAPER = "go_wallpaper";
	public final static String MULTIPLEWALLPAPER = "multiple_wallpaper";
	public final static String GALLERYWALLPAPER = "gallery_wallpaper";
	public final static String LIVEWALLPAPER = "live_wallpaper";
	public final static String OHTERWALLPAPER = "other_wallpaper";
	// 图库壁纸
	private final static String GALLERY_ACTIVITY_NAME = "com.android.launcher2.WallpaperChooser";
	private final static String GALLERY_PACKAGE = "com.android.launcher";
	// 动态壁纸
	private final static String LIVE_ACTIVITY_NAME = "com.android.wallpaper.livepicker.LiveWallpaperActivity";
	private final static String LIVE_PACKAGE = "com.android.wallpaper.livepicker";

	// 多屏多壁纸相关参数
	public final static String MULTIPLEWALLPAPER_ACTIVITY_NAME = "com.go.multiplewallpaper.MultipleWallpaperSettingActivity";
	// 推荐的动态壁纸相关参数
	public final static String RECOMM_LIVEWALLPAPER_ACTIVITY_NAME = "com.jiubang.livewallpaper.techball.TechBallLiveWallpaperService";

	public static final String WIDGET_WEIBO = "com.gau.go.launcherex.gowidget.weibowidget";
	public static final String WIDGET_GOTORE = "com.gau.go.launcherex.gowidget.gostore";
	public static final String TASK_MANAGER = "com.gau.go.launcherex.gowidget.taskmanager";

	public static final String LANGUAGE = "language";
	public int mShortCut2Widget = 0;
	public int mInstall = 0; 						// 已安装与未安装的标志位   

	private ArrayList<Object> mGoWidgetInfoList; 	// 所有widget
	public GoWidgetFinder mFinder;
	private HashMap<String, GoWidgetProviderInfo> mProviderMap;
	public ArrayList<ThemeInfoBean> mThemeInfos;

	//壁纸二级	
	private ArrayList<Object> mThumbs; 				// 缩略图
	private ArrayList<Object> mImages; 				// 墙纸应用大图

	//gowidget
	public static final List<String> INSTALLED_PACKAGES = new ArrayList<String>();
	static {
		INSTALLED_PACKAGES.add(PackageName.RECOMMAND_GOWEATHEREX_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.RECOMMAND_GOPOWERMASTER_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.RECOMMAND_GOTASKMANAGER_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.RECOMMAND_GOBACKUPEX_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.TASK_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.SWITCH_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.NEW_SWITCH_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.CALENDAR_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.NEW_CALENDAR_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.CLOCK_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.NOTE_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.NEW_NOTE_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.CONTACT_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.SINA_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.TENCNT_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.EMAIL_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.FACEBOOK_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.TWITTER_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.RECOMMAND_GOSMS_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.BOOKMARK_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.SEARCH_WIDGET_PACKAGE);
		//		INSTALLED_PACKAGES.add(PackageName.NEW_SWITCH_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.TIMER_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.SCANNER_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.FLASH_LIGHT_PACKAGE);
		INSTALLED_PACKAGES.add(PackageName.OK_SCREEN_SHOT);
	}

	public void setThemePkgName(String pkgName) {
		this.mThemePkgName = pkgName;
	}

	private ScreenEditController() {
		mContext = ApplicationProxy.getContext();
		mThemePkgName = IGoLauncherClassName.DEFAULT_THEME_PACKAGE;

		mPaint = new Paint();
		mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
		mIconMask = (BitmapDrawable) mContext.getResources().getDrawable(
				R.drawable.screen_edit_icon_mask);
		mIconBase = (BitmapDrawable) mContext.getResources().getDrawable(
				R.drawable.screen_edit_icon_base);
		mDownloadMask = (BitmapDrawable) mContext.getResources().getDrawable(
				R.drawable.gl_push_download);
	}

	public synchronized static ScreenEditController getInstance() {
		if (sInstance == null) {
			sInstance = new ScreenEditController();
		}
		return sInstance;
	}

	/**
	 * 获取GLMainTab中的显示数据
	 * @return
	 */
	public ArrayList<Object> requestMainTabData() {
		Resources res = mContext.getResources();
		ArrayList<Object> items = new ArrayList<Object>();

		// 应用程序
		ScreenEditItemInfo itemInfo = new ScreenEditItemInfo();
		itemInfo.setId(ScreenEditConstants.CLICK_TAB_ADD);
		itemInfo.setTitle(res.getString(R.string.tab_add_apps));
		itemInfo.setIcon(res.getDrawable(R.drawable.screenedit_apps_tab_icon));
		items.add(itemInfo);

		// 文件夹
		itemInfo = new ScreenEditItemInfo();
		itemInfo.setId(ScreenEditConstants.CLICK_TAB_FOLDER);
		itemInfo.setTitle(res.getString(R.string.tab_add_folder));
		itemInfo.setIcon(res.getDrawable(R.drawable.screenedit_folder_tab_icon));
		items.add(itemInfo);

		// go小部件
		itemInfo = new ScreenEditItemInfo();
		itemInfo.setId(ScreenEditConstants.CLICK_TAB_GO_WIDGET);
		itemInfo.setTitle(res.getString(R.string.tab_add_gowidget));
		itemInfo.setIcon(res.getDrawable(R.drawable.screenedit_gowidget_tab_icon));
		items.add(itemInfo);

		// 系统小部件
		itemInfo = new ScreenEditItemInfo();
		itemInfo.setId(ScreenEditConstants.CLICK_TAB_SYSTEM_WIDGET);
		itemInfo.setTitle(res.getString(R.string.add_widget));
		itemInfo.setIcon(res.getDrawable(R.drawable.screenedit_syswidget_tab_icon));
		items.add(itemInfo);

		// Go桌面快捷方式
		itemInfo = new ScreenEditItemInfo();
		itemInfo.setId(ScreenEditConstants.CLICK_TAB_GO_SHORTCUT);
		itemInfo.setTitle(res.getString(R.string.dialog_name_go_shortcut));
		itemInfo.setIcon(res.getDrawable(R.drawable.screen_edit_go_shortcut));
		items.add(itemInfo);
		return items;
	}

	/**
	 * 获取GLGoShortCutTab中的显示数据
	 * @return
	 */
	public ArrayList<Object> getShortcutItem() {
		String[] mIntentActions = null;
		String[] mTitles;
		int[] mDrawableIds;

		ArrayList<Object> mList = new ArrayList<Object>();
		try {

			mIntentActions = new String[] {
					ICustomAction.ACTION_SHOW_MAIN_SCREEN,
					ICustomAction.ACTION_SHOW_MAIN_OR_PREVIEW,
					ICustomAction.ACTION_SHOW_FUNCMENU_FOR_LAUNCHER_ACITON,
					ICustomAction.ACTION_SHOW_EXPEND_BAR,
					ICustomAction.ACTION_SHOW_HIDE_STATUSBAR,
					ICustomAction.ACTION_FUNC_SPECIAL_APP_GOTHEME,
					ICustomAction.ACTION_SHOW_PREFERENCES,
					ICustomAction.ACTION_FUNC_SPECIAL_APP_GOSTORE,
					ICustomAction.ACTION_SHOW_PREVIEW,
					// ICustomAction.ACTION_SHOW_LOCKER_SETTING
					ICustomAction.ACTION_ENABLE_SCREEN_GUARD, ICustomAction.ACTION_SHOW_DOCK,
					ICustomAction.ACTION_SHOW_MENU, ICustomAction.ACTION_SHOW_DIYGESTURE,
					ICustomAction.ACTION_SHOW_PHOTO, ICustomAction.ACTION_SHOW_MUSIC,
					ICustomAction.ACTION_SHOW_VIDEO };

			mTitles = new String[] {
					mContext.getString(R.string.customname_mainscreen),
					mContext.getString(R.string.customname_mainscreen_or_preview),
					mContext.getString(R.string.customname_Appdrawer),
					mContext.getString(R.string.customname_notification),
					mContext.getString(R.string.customname_status_bar),
					mContext.getString(R.string.customname_themeSetting),
					mContext.getString(R.string.customname_preferences),
					mContext.getString(R.string.customname_gostore),
					mContext.getString(R.string.customname_preview),
					// getString(R.string.customname_golocker)
					mContext.getString(R.string.goshortcut_lockscreen),
					mContext.getString(R.string.goshortcut_showdockbar),
					mContext.getString(R.string.customname_mainmenu),
					mContext.getString(R.string.customname_diygesture),
					mContext.getString(R.string.customname_photo),
					mContext.getString(R.string.customname_music),
					mContext.getString(R.string.customname_video) };

			mDrawableIds = new int[] { MAIN_SCREEN, MAIN_SCREEN_OR_PREVIEW, FUNCMENU, NOTIFICATION,
					STATUS_BAR, THEME_SETTING, PREFERENCES, GO_STORE, PREVIEW// , GO_LOCK
					, LOCK_SCREEN, DOCK_BAR, MAIN_MENU, DIY_GESTURE, PHOTO, MUSIC, VIDEO };
			final int count = mIntentActions.length;
			final String goComponentName = "com.gau.launcher.action";
			ShortCutInfo itemInfo = null;
			Intent intent = null;
			ComponentName cmpName = null;
			for (int i = 0; i < count; i++) {
				itemInfo = new ShortCutInfo();
				intent = new Intent(mIntentActions[i]);
				cmpName = new ComponentName(goComponentName, mIntentActions[i]);
				intent.setComponent(cmpName);
				itemInfo.mIntent = intent;
				itemInfo.mItemType = IItemType.ITEM_TYPE_SHORTCUT;
				itemInfo.mTitle = mTitles[i];
				itemInfo.mIcon = getItemImage(null, mDrawableIds[i], mContext, null);

				mList.add(itemInfo);

				itemInfo = null;
				intent = null;
				cmpName = null;
			}
			mIntentActions = null;
			mTitles = null;
			mDrawableIds = null;
		} catch (Exception e) {
		}
		return mList;
	}

	/**
	 * 获取GLEffectTab中的显示数据
	 * 下标0-特效名数组(String[])；
	 * 下标1-特效值数组(int[])；
	 * 下表2-图片数组(Drawable[])
	 */
	public Object[] getAllEffectors(boolean isForRandomCustom) {
		EffectorControler controler = EffectorControler.getInstance();
		Object[] objs = controler.getAllEffectors(EffectorControler.TYPE_EFFECT_TAB,
				isForRandomCustom);
		int[] idArray = (int[]) objs[1];
		Drawable[] drawableArray = new Drawable[idArray.length];
		for (int i = 0; i < idArray.length; i++) {
			drawableArray[i] = getEffectorIcon(idArray[i]);
		}
		objs[2] = drawableArray;
		controler.updateEffectorNewState(EffectorControler.TYPE_EFFECT_TAB, false);
		return objs;
	}

	/**
	 * 获取GLWallpaperTab中的显示数据
	 * 排序:	壁纸滚动-GO 桌面壁纸-GO 动态壁纸-GO 多屏壁纸-系统和其他应用	壁纸-获取更多
	 */
	@SuppressLint({ "NewApi", "ServiceCast" })
	public ArrayList<Object> getWallPaperTabData() {

		List<WallpaperItemInfo> list = new ArrayList<WallpaperItemInfo>();
		ArrayList<Object> appInfoList = new ArrayList<Object>();
		PackageManager pm = mContext.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_SET_WALLPAPER, null);
		WallpaperItemInfo goAppInfo = null;
		// 通过查询，获得所有ResolveInfo对象.
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
				PackageManager.MATCH_DEFAULT_ONLY);

		// 调用系统排序 ， 根据name排序
		// 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
		if (list != null) {
			list.clear();
			for (ResolveInfo reInfo : resolveInfos) {
				list.add(getWallpaperItemInfo(reInfo, pm));
			}
			// 进行排序
			String sortMethod = "getTitle";
			String order = "ASC";
			SortUtils.sort(list, sortMethod, null, null, order);
			// 把GO桌面放第一位（如果设置了动态壁纸则顺延至第二位）
			for (WallpaperItemInfo appInfo : list) {
				if (PackageName.PACKAGE_NAME.equals(appInfo.getPkgName())) {
					//					appInfoList.add(0, appInfo);
					goAppInfo = appInfo;
					appInfoList.remove(appInfo);
				} else {
					appInfoList.add(appInfo);
				}
			}
			// 添加动态壁纸的设置项
			ResolveInfo settingInfo = null;
			WallpaperManager wm = (WallpaperManager) mContext
					.getSystemService(Context.WALLPAPER_SERVICE);
			android.app.WallpaperInfo wi = wm.getWallpaperInfo();
			if (wi != null && wi.getSettingsActivity() != null
					&& !wi.getPackageName().equals(PackageName.MULTIPLEWALLPAPER_PKG_NAME)) {
				LabeledIntent li = new LabeledIntent(mContext.getPackageName(),
						R.string.configure_wallpaper, 0);
				li.setClassName(wi.getPackageName(), wi.getSettingsActivity());
				settingInfo = pm.resolveActivity(li, 0);
				// 添加动态壁纸设置放在第1个位置,索引为0
				if (settingInfo != null) {
					WallpaperItemInfo appInfo = getWallpaperItemInfo(settingInfo, pm);
					if (appInfo != null) {
						// 桌面添加了动态壁纸属性，此处需要屏蔽桌面的动态壁纸属性
						if (!PackageName.PACKAGE_NAME.equals(appInfo.getPkgName())) {
							appInfoList.add(0, getWallpaperItemInfo(settingInfo, pm));
						}
					}

				} else {
					String activityName = wi.getSettingsActivity(); // 获得该应用程序的启动Activity的name
					String pkgName = wi.getPackageName(); // 获得应用程序的包名
					String appLabel = mContext.getResources().getString(
							R.string.configure_wallpaper);
					Drawable icon = mContext.getResources().getDrawable(
							R.drawable.live_wallpaper_logo);
					// 为应用程序的启动Activity 准备Intent
					Intent launchIntent = new Intent();
					launchIntent.setComponent(new ComponentName(pkgName, activityName));
					launchIntent.setAction(Intent.ACTION_SET_WALLPAPER);

					WallpaperItemInfo appInfo = new WallpaperItemInfo();
					appInfo.setAppLabel(appLabel);
					appInfo.setPkgName(pkgName);
					// 确定动态壁纸设置项的icon是已经匹配的，所以不进行fitIcon处理
					appInfo.setAppIcon(icon);
					appInfo.setIntent(launchIntent);
					appInfoList.add(0, appInfo);
				}
			}
			//显示位置
			int position = 0;

			//Go桌面壁纸, 排第二
			appInfoList.add(position++, goAppInfo);
			
			//Go动态壁纸, 排第三
			WallpaperItemInfo goWallpaperAppInfo = new WallpaperItemInfo();
			goWallpaperAppInfo.setPkgName(GODYNAMICA_WALLPAPER);
			goWallpaperAppInfo.setAppLabel(mContext
					.getString(R.string.go_dynamical_wallpaper_title));
			goWallpaperAppInfo.setAppIcon(mContext.getResources().getDrawable(
					R.drawable.screen_edit_wallpaper_livewallpaper));
			appInfoList.add(position++, goWallpaperAppInfo);

			//加入推荐动态壁纸图标(如果未安装电子市场,则不添加)update by caoyaming 2014-03-17
			if (!GoAppUtils.isMarketNotExitAnd200Channel(mContext)) {
				//存在电子市场,添加到添加入口
				appInfoList.add(position++, getRecommLiveWallpaperItem());
			}
			
			WallpaperItemInfo wallpaperFilterInfo = new WallpaperItemInfo();
			wallpaperFilterInfo.setPkgName(WALLPAPER_FILTER);
			wallpaperFilterInfo.setAppLabel(mContext.getString(R.string.wallpaper_filter_title));
			wallpaperFilterInfo.setAppIcon(mContext.getResources().getDrawable(
					R.drawable.screen_edit_wallpaper_filter));
			appInfoList.add(position++, wallpaperFilterInfo);

			// 加入多屏多壁纸,排在动态壁纸后面
			if (GoAppUtils.isAppExist(mContext, PackageName.MULTIPLEWALLPAPER_PKG_NAME)) {
				appInfoList.add(position++, getMultipleWallpaperItem());
			}

			addDownLoadItem(appInfoList);
		}
		return appInfoList;
	}

	/**
	 * 初始化列表的选项
	 * @param menuBean
	 * @param id
	 * @param context
	 * @param imageExplorer
	 * @return
	 */
	public static Drawable getItemImage(DeskThemeBean.MenuBean menuBean, int id, Context context,
			ImageExplorer imageExplorer) {
		Drawable ret = null;
		// 从主题获取(以后可能要用到)
		if (null != menuBean && null != menuBean.mItems) {

			int len = menuBean.mItems.size();
			for (int i = 0; i < len; i++) {
				MenuItemBean itemBean = menuBean.mItems.get(i);
				if (null == itemBean) {
					continue;
				}
				if (itemBean.mId == id) {
					if (null != itemBean.mImage) {
						ret = getDrawable(imageExplorer, itemBean.mImage.mResName);
					}
					break;
				}
			}
		}
		// 从主程序获取
		if (null == ret) {
			Resources resources = context.getResources();

			switch (id) {
				case MAIN_SCREEN :
					ret = resources.getDrawable(R.drawable.screen_edit_go_shortcut_mainscreen);
					break;

				case MAIN_SCREEN_OR_PREVIEW :
					ret = resources.getDrawable(R.drawable.screen_edit_go_shortcut_main_or_preview);
					break;

				case FUNCMENU :
					ret = resources.getDrawable(R.drawable.screen_edit_go_shortcut_appdrawer);
					break;

				case NOTIFICATION :
					ret = resources.getDrawable(R.drawable.screen_edit_go_shortcut_notification);
					break;

				case STATUS_BAR :
					ret = resources.getDrawable(R.drawable.screen_edit_go_shortcut_statusbar);
					break;

				case THEME_SETTING :
					ret = resources.getDrawable(R.drawable.screen_edit_go_shortcut_themes);
					break;

				case PREFERENCES :
					ret = resources.getDrawable(R.drawable.screen_edit_go_shortcut_preferences);
					break;

				case GO_STORE :
					ret = resources.getDrawable(R.drawable.screen_edit_go_shortcut_store);
					break;

				case PREVIEW :
					ret = resources.getDrawable(R.drawable.screen_edit_go_shortcut_preview);
					break;

				case LOCK_SCREEN :
					ret = resources.getDrawable(R.drawable.screen_edit_go_shortcut_lockscreen);
					break;
				case DOCK_BAR :
					ret = resources.getDrawable(R.drawable.screen_edit_go_shortcut_hide_dock);
					break;
				case MAIN_MENU : {
					ret = resources.getDrawable(R.drawable.screen_edit_go_shortcut_menu);
					break;
				}
				case DIY_GESTURE : {
					ret = resources.getDrawable(R.drawable.screen_edit_go_shortcut_diygesture);
					break;
				}
				case PHOTO : {
					ret = resources.getDrawable(R.drawable.screen_edit_go_shortcut_photo);
					break;
				}
				case MUSIC : {
					ret = resources.getDrawable(R.drawable.screen_edit_go_shortcut_music);
					break;
				}
				case VIDEO : {
					ret = resources.getDrawable(R.drawable.screen_edit_go_shortcut_video);
					break;
				}

				default :
					break;
			}
		}
		return ret;
	}

	private static Drawable getDrawable(ImageExplorer imageExplorer, String resName) {
		Drawable ret = null;
		if (null == imageExplorer || null == resName) {
			return ret;
		}
		try {
			ret = imageExplorer.getDrawable(resName);
		} catch (OutOfMemoryError e) {
			OutOfMemoryHandler.handle();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * <br>
	 * 功能简述:通过drawableId拿推荐图标图片 <br>
	 * 功能详细描述:可以过滤某些图标进行download tag标签合成图片（tag图片共享一张，减少图片资源） <br>
	 * @param drawableId
	 * @return　经过合成规则处理后的图片
	 */
	private static Drawable getIcons(Context context, int drawableId) {
		Drawable tag = context.getResources().getDrawable(drawableId);

		Drawable drawable = context.getResources().getDrawable(R.drawable.screenedit_icon_bg);
		try {
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			Canvas cv = new Canvas(bmp);
			ImageUtil.drawImage(cv, drawable, ImageUtil.STRETCHMODE, 0, 0, width, height, null);
			ImageUtil.drawImage(cv, tag, ImageUtil.STRETCHMODE, 0, 0, width, height, null);
			BitmapDrawable bmd = new BitmapDrawable(bmp);
			bmd.setTargetDensity(context.getResources().getDisplayMetrics());
			drawable = bmd;
		} catch (Throwable e) {
			// 出错则不进行download Tag合成图
		}
		return drawable;
	}

	/**
	 * 根据特效ID获取特效的图片
	 * @param effectorId
	 * @return
	 */
	public Drawable getEffectorIcon(int effectorId) {
		EffectorInfo info = EffectorControler.getInstance().getEffectorInfoById(effectorId);
		if (!info.mIsPrime) {
			return getGoAppsIcons(info.mEffectorDrawableId);
		} else {
			if (FunctionPurchaseManager.getInstance(mContext.getApplicationContext())
					.getPayFunctionState(FunctionPurchaseManager.PURCHASE_ITEM_EFFECT) == FunctionPurchaseManager.STATE_CAN_USE) {
				if (info.mIsEffectTabShowNew) {
					//添加界面--去除小红点
					return getGoAppsIcons(info.mEffectorDrawableId);
					//					return getNewLogoIcon(getGoAppsIcons(info.mEffectorDrawableId));
				} else {
					return getGoAppsIcons(info.mEffectorDrawableId);
				}
			} else {
				return getPrimeIcon(getGoAppsIcons(info.mEffectorDrawableId),
						FunctionPurchaseManager.PURCHASE_ITEM_EFFECT);
			}
		}
	}

	/**
	 * <br>
	 * 功能简述:通过drawableId拿推荐图标图片 <br>
	 * 功能详细描述:可以过滤某些图标进行download tag标签合成图片（tag图片共享一张，减少图片资源） <br>
	 * @param drawableId
	 * @return　经过合成规则处理后的图片
	 */
	private Drawable getGoAppsIcons(int drawableId) {
		Drawable tag = mContext.getResources().getDrawable(drawableId);

		Drawable drawable = mContext.getResources().getDrawable(
				R.drawable.desk_setting_effector_icon_bg);
		try {
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			Canvas cv = new Canvas(bmp);
			ImageUtil.drawImage(cv, drawable, ImageUtil.STRETCHMODE, 0, 0, width, height, null);
			ImageUtil.drawImage(cv, tag, ImageUtil.STRETCHMODE, 0, 0, width, height, null);
			BitmapDrawable bmd = new BitmapDrawable(bmp);
			bmd.setTargetDensity(mContext.getResources().getDisplayMetrics());
			drawable = bmd;
		} catch (Throwable e) {
			e.printStackTrace(); // 出错则不进行合成图
		}

		return drawable;
	}

	private Drawable getPrimeIcon(Drawable drawable, int purchaseItem) {
		if (FunctionPurchaseManager.getInstance(mContext.getApplicationContext())
				.getPayFunctionState(purchaseItem) != FunctionPurchaseManager.STATE_VISABLE) {
			return drawable;
		}
		Drawable tag = mContext.getResources().getDrawable(
				R.drawable.desk_setting_classify_dialog_prime_img);
		try {
			drawable = ImageUtil.drawCoverImage(mContext, drawable, tag);
		} catch (Throwable e) {
			e.printStackTrace(); // 出错则不进行合成图
		}

		return drawable;
	}

	/**
	 * 添加多屏多壁纸
	 * 
	 * @param appInfoList
	 * 
	 * @author chenbingdong
	 */
	private WallpaperItemInfo getMultipleWallpaperItem() {
		WallpaperItemInfo appInfo = new WallpaperItemInfo();
		String appLabel = mContext.getString(R.string.go_multiple_wallpaper_title);
		appInfo.setAppLabel(appLabel);
		appInfo.setPkgName(PackageName.MULTIPLEWALLPAPER_PKG_NAME);
		Intent launchIntent = null;
		launchIntent = new Intent();
		launchIntent.setComponent(new ComponentName(PackageName.MULTIPLEWALLPAPER_PKG_NAME,
				MULTIPLEWALLPAPER_ACTIVITY_NAME));
		Drawable drawable = mContext.getResources().getDrawable(
				R.drawable.screen_edit_wallpaper_multiple_wallpaper);
		//		// 判断多屏多壁纸应用是否存在
		//		if (!GoAppUtils.isAppExist(mContext, PackageName.MULTIPLEWALLPAPER_PKG_NAME)) {
		//			// 应用不存在，图标变灰处理
		//			mViewSize = (int) (drawable.getIntrinsicWidth() * DrawUtils.sDensity);
		//			drawable.mutate();
		//			ColorMatrix cm = new ColorMatrix();
		//			cm.setSaturation(0);
		//			ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);
		//			drawable.setColorFilter(cf);
		//			Bitmap b = drawableToBitmap(drawable);
		//			drawable = new BitmapDrawable(b);
		//
		//		} else {
		//			// 应用存在，图标恢复彩色处理
		//			drawable = BitmapUtility.getOriginalDrawable(drawable);
		//		}
		appInfo.setAppIcon(drawable);

		appInfo.setIntent(launchIntent);

		return appInfo;

	}

	/**
	 * 获取推荐动态壁纸信息对象
	 * @return
	 */
	private WallpaperItemInfo getRecommLiveWallpaperItem() {
		WallpaperItemInfo appInfo = new WallpaperItemInfo();
		String appLabel = mContext.getString(R.string.go_recomm_live_wallpaper_title);
		appInfo.setAppLabel(appLabel);
		appInfo.setPkgName(PackageName.RECOMM_LIVEWALLPAPER_PKG_NAME);
		Intent launchIntent = null;
		launchIntent = new Intent();
		ComponentName componentName = new ComponentName(PackageName.RECOMM_LIVEWALLPAPER_PKG_NAME,
				RECOMM_LIVEWALLPAPER_ACTIVITY_NAME);
		launchIntent.setAction("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
		launchIntent.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT",
				componentName);
		//设置Icon
		Drawable drawable = mContext.getResources().getDrawable(
				R.drawable.recomm_live_wallpaper_icon);
		appInfo.setAppIcon(composeIconMask(drawable));
		//设置Intent
		appInfo.setIntent(launchIntent);
		//返回信息
		return appInfo;
	}

	private int mViewSize;
	public Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(mViewSize, mViewSize, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		//canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, mViewSize, mViewSize);
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 将ResolveInfo转化为WallpaperItemInfo
	 * 
	 * @param reInfo
	 * @param pm
	 * @return
	 */
	private WallpaperItemInfo getWallpaperItemInfo(ResolveInfo reInfo, PackageManager pm) {
		String activityName = reInfo.activityInfo.name; 	// 获得该应用程序的启动Activity的name
		String pkgName = reInfo.activityInfo.packageName; 	// 获得应用程序的包名
		String appLabel = (String) reInfo.loadLabel(pm); 	// 获得应用程序的Label
		Drawable icon = reInfo.loadIcon(pm); 				// 获得应用程序图标
		// 为应用程序的启动Activity 准备Intent
		Intent launchIntent = new Intent();
		launchIntent.setComponent(new ComponentName(pkgName, activityName));
		launchIntent.setAction(Intent.ACTION_SET_WALLPAPER);

		WallpaperItemInfo appInfo = new WallpaperItemInfo();
		appInfo.setAppLabel(appLabel);
		appInfo.setPkgName(pkgName);
		appInfo.setAppIcon(fitIcon(icon, pkgName, activityName));
		appInfo.setIntent(launchIntent);
		return appInfo;
	}

	/**
	 * 功能简述:增加“获取更多”图标，点击后跳转至GO精品的壁纸分类。 功能详细描述: 注意:排在最后
	 * 
	 * @param appInfoList
	 */
	private void addDownLoadItem(List<Object> appInfoList) {
		String appLabel = mContext.getString(R.string.themestore_mainlistview_btmbutton);
		WallpaperItemInfo appInfo = new WallpaperItemInfo();
		appInfo.setPkgName(DOWNLOAD_ITEM_PACKAGE);
		Intent launchIntent = new Intent();
		launchIntent.setClass(mContext, AppsManagementActivity.class);
		Bundle bundle = new Bundle();
		//		if (GoStorePhoneStateUtil.is200ChannelUid(mContext)) {
		//			bundle.putString("sort", String.valueOf(SortsBean.SORT_LIVEWALLPAPER)); // 动态壁纸分类
		//		} else {
		//			bundle.putString("sort", String.valueOf(SortsBean.SORT_WALLPAPER)); // 壁纸分类
		//		}
		bundle.putInt(AppsManagementConstants.APPS_MANAGEMENT_ENTRANCE_KEY,
				MainViewGroup.ACCESS_FOR_APPCENTER_WALLPAPER);
		bundle.putBoolean(AppsManagementConstants.APPS_MANAGEMENT_SHOW_FRONTCOVER, false);
		//add by zzf  实时统计需要的入口值
		bundle.putInt(RealTimeStatisticsContants.ENTRANCE_KEY,
				RealTimeStatisticsContants.AppgameEntrance.WALLPAPER_ADD);
		StatisticsData.countStatData(mContext, StatisticsData.ENTRY_KEY_WALLPAPER);
		//		GoStoreStatisticsUtil.setCurrentEntry(GoStoreStatisticsUtil.ENTRY_TYPE_MOREWALLPAPER,
		//				mContext);
		AppRecommendedStatisticsUtil.getInstance().saveCurrentEnter(mContext,
				AppRecommendedStatisticsUtil.ENTRY_TYPE_WALLPAPER);
		launchIntent.putExtras(bundle);
		appInfo.setAppLabel(appLabel);
		appInfo.setAppIcon(mContext.getResources().getDrawable(R.drawable.screen_edit_more));
		// appInfo.setPkgName(mContext.getPackageName()); // 跟Go桌面壁纸tab包名冲突，暂关闭
		appInfo.setIntent(launchIntent);
		appInfoList.add(appInfo);
	}

	/**
	 * 对一些特殊的壁纸做图标处理
	 * 
	 * @param srcIcon
	 * @param packageName
	 * @return
	 */
	private Drawable fitIcon(Drawable srcIcon, String packageName, String activityName) {

		if (PackageName.PACKAGE_NAME.equals(packageName)) {
			Drawable icon = mContext.getResources().getDrawable(
					R.drawable.screen_edit_wallpaper_gowallpaper);
			return icon;
		} else if (packageName.equals(GALLERY_PACKAGE)
				&& activityName.equals(GALLERY_ACTIVITY_NAME)) {
			return mContext.getResources().getDrawable(R.drawable.gallery_4_def3);
		} else if (packageName.equals(LIVE_PACKAGE) && activityName.equals(LIVE_ACTIVITY_NAME)) {
			return mContext.getResources().getDrawable(R.drawable.live_wallpaper_logo);
		} else {
			Drawable fitIcon = composeIconMask(srcIcon);
			if (fitIcon != null) {
				return fitIcon;
			}
		}

		return srcIcon;
	}

	/**
	 * 返回合适添加模块的icon的图片
	 * 
	 * @param drawable 原图
	 * @param mask 是否需要勾图
	 * @return
	 */
	public BitmapDrawable composeIconMask(Drawable drawable) {

		BitmapDrawable bitmapDrawable = null;
		Bitmap bitmap = composeIconMaskBitmap(drawable, true, false);
		if (bitmap != null) {
			bitmapDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
		}
		return bitmapDrawable;
	}

	public Bitmap composeIconMaskBitmap(Drawable drawable, boolean drawBase, boolean showDownload) {

		try {
			int maskH = mIconMask.getIntrinsicHeight();
			int maskW = mIconMask.getIntrinsicWidth();
			Bitmap bitmap = Bitmap.createBitmap(maskW, maskH, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);

			if (drawBase) {
				canvas.drawBitmap(mIconBase.getBitmap(), 0f, 0f, null);
			}

			if (drawable != null) {
				Bitmap oldbmp = null;
				// drawable 转换成 bitmap
				if (drawable instanceof BitmapDrawable) {
					// 如果传入的drawable是BitmapDrawable,就不必要生成新的bitmap
					oldbmp = ((BitmapDrawable) drawable).getBitmap();
				} else {
					oldbmp = BitmapUtility.createBitmapFromDrawable(drawable);
				}

				int width = oldbmp.getWidth();
				int height = oldbmp.getHeight();

				Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
				float scale;
				int offsetCutX = 0;
				int offsetCutY = 0;
				if (width > height) {
					offsetCutX = (width - height) / 2;
					scale = (float) maskH / height;
				} else {
					offsetCutY = (height - width) / 2;
					scale = (float) maskW / width;
				}

				// 设置缩放比例
				matrix.postScale(scale, scale);

				// 建立新的bitmap，其内容是对原bitmap的缩放后的图
				Bitmap newbmp = Bitmap.createBitmap(oldbmp, offsetCutX, offsetCutY, width - 2
						* offsetCutX, height - 2 * offsetCutY, matrix, true);

				final int drawTop = Math.max(0, maskH - newbmp.getHeight());
				final int drawLeft = Math.max(0, (maskW - newbmp.getWidth()) / 2);
				canvas.drawBitmap(newbmp, drawLeft, drawTop, mPaint);

				if (showDownload) {
					canvas.drawBitmap(mDownloadMask.getBitmap(), 0, 0, mPaint);
				}
				matrix = null;

				Xfermode xf = mPaint.getXfermode();
				mPaint.setXfermode(mXfermode);
				// 画勾图的形状
				canvas.drawBitmap(mIconMask.getBitmap(), 0, 0, mPaint);
				mPaint.setXfermode(xf);
			}

			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 加载壁纸图片
	 */
	public Map<String, List<Object>> loadDrawables(String drawablesResName) {
		Map<String, List<Object>> map = new HashMap<String, List<Object>>();
		mThumbs = new ArrayList<Object>();
		mImages = new ArrayList<Object>();
		if (drawablesResName == null) {
			return null;
		}
		// 将本程序对应的默认主题也添加进去
		if (mContext != null) {
			// 4.0需求特殊处理,有ui3.0则显示ui3.0的两张图,不显示默认主题,如果UI3.0只有一张图,则在后面加上
			if (!isNewestUI30(mContext)) {
				addDrawables(mContext.getResources(), mContext.getPackageName(), drawablesResName,
						null);
			}

			// 查找第三方主题包，并将主题包中的墙纸提取出来
			Intent intent = new Intent(ICustomAction.ACTION_MAIN_THEME_PACKAGE);
			intent.addCategory(ThemeManager.THEME_CATEGORY);
			PackageManager pm = mContext.getPackageManager();
			List<ResolveInfo> themes = pm.queryIntentActivities(intent, 0);
			int size = themes.size();
			String themePackage = null;
			Resources resources = null;
			CharSequence lable; // 主题程序名称
			for (int i = 0; i < size; i++) {
				themePackage = themes.get(i).activityInfo.packageName.toString();
				lable = themes.get(i).activityInfo.loadLabel(pm);
				try {
					resources = pm.getResourcesForApplication(themePackage);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				addDrawables(resources, themePackage, drawablesResName, lable);
			}

			//update by zhoujun 解析zip主题
			if (Machine.isSDCardExist() && mContext != null) {
				//有sdcard
				ConcurrentHashMap<String, ThemeInfoBean> zipHashMap = ThemeManager.getInstance(
						mContext).scanAllZipThemes();
				if (zipHashMap != null) {
					Iterator<String> pakcageIterator = zipHashMap.keySet().iterator();
					if (pakcageIterator != null) {
						String packageName = null;
						ThemeInfoBean themeInfoBean = null;
						while (pakcageIterator.hasNext()) {
							packageName = pakcageIterator.next();
							themeInfoBean = zipHashMap.get(packageName);
							themeInfoBean = ThemeManager.getInstance(mContext).getThemeInfo(
									themeInfoBean.getPackageName(), themeInfoBean);
							resources = ZipResources.getThemeResourcesFromReflect(mContext,
									packageName);
							//非空保护,themeInfoBean异常为空时,则不加载该张壁纸
							if (themeInfoBean != null) {
								addDrawables(resources, packageName, drawablesResName,
										themeInfoBean.getThemeName());
							}
						}
					}
				}
			}
			// update by zhoujun 2012-08-23 end 
		}
		map.put("mThumbs", mThumbs);
		map.put("mImages", mImages);
		return map;
	}

	private void addDrawables(Resources resources, String packageName, String resName,
			CharSequence lable) {
		if (resources == null || packageName == null) {
			return;
		}
		// 不是最新版的ui3.0则返回,添加假图标(4.0UI3.0壁纸推广需求) 
		if (packageName.equals(mContext.getPackageName()) && !isNewestUI30(mContext)) {
			int thumbRes = resources.getIdentifier("old_default_wallpaper_thumb", "drawable",
					PackageName.PACKAGE_NAME);
			WallpaperSubInfo thumbItem = new WallpaperSubInfo();
			thumbItem.setImageResId(thumbRes);
			thumbItem.setPackageName(packageName);
			thumbItem.setResource(resources);
			thumbItem.setType(0);
			thumbItem.setImageResName("UI 3.0");
			mThumbs.add(0, thumbItem);
			mImages.add(0, new WallpaperSubInfo());
		}
		try {
			int drawableList = resources.getIdentifier(resName, "array", packageName);
			if (drawableList <= 0) {
				return;
			}
			final String[] extras = resources.getStringArray(drawableList);
			//			if (extras.length == 1 && packageName.equals(IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER)) {
			//				addDrawables(mContext.getResources(), mContext.getPackageName(), "wallpaperlist", null);
			//			}
			for (String extra : extras) {
				int res = resources.getIdentifier(extra, "drawable", packageName);
				if (res != 0) {
					final int thumbRes = resources.getIdentifier(extra + "_thumb", "drawable",
							packageName);
					if (thumbRes != 0) {
						WallpaperSubInfo thumbItem = new WallpaperSubInfo();
						// thumbItem.mImageResName = extra;
						thumbItem.setImageResId(thumbRes);
						thumbItem.setPackageName(packageName);
						thumbItem.setResource(resources);
						thumbItem.setType(1);
						// 设置图片名称
						if (lable == null) {
							thumbItem.setImageResName(mContext.getResources().getString(
									R.string.theme_title)); // 默认主题
						} else {
							thumbItem.setImageResName(lable.toString());
						}

						WallpaperSubInfo imageItem = new WallpaperSubInfo();
						// imageItem.mImageResName = extra;
						imageItem.setImageResId(res);
						imageItem.setPackageName(packageName);
						imageItem.setResource(resources);
						imageItem.setType(1);
						if (isNewestUI30(mContext)
								&& packageName
										.equals(IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER)
								|| packageName.equals(mContext.getPackageName())) {
							mThumbs.add(0, thumbItem);
							mImages.add(0, imageItem);
						} else {
							mImages.add(imageItem);
							mThumbs.add(thumbItem);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 判断是不是带上有4.0壁纸的ui3.0
	public static boolean isNewestUI30(Context context) {
		if (GoAppUtils.isAppExist(context, IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER)
				&& AppUtils.getVersionCodeByPkgName(context,
						IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER) > 31) {
			return true;
		} else {
			return false;
		}
	}

	public ArrayList<Object> getWidgetTabShortCutList() {
		ArrayList<Object> mItems = new ArrayList<Object>();
		ShortCutInfo itemInfo = new ShortCutInfo();
		String goTitle = mContext.getResources().getString(R.string.go_handbook_title);
		itemInfo.mIntent = new Intent(ICustomAction.ACTION_SHOW_GO_HANDBOOK);
		itemInfo.mItemType = IItemType.ITEM_TYPE_SHORTCUT;
		itemInfo.mTitle = goTitle;
		itemInfo.mIcon = mContext.getResources().getDrawable(R.drawable.go_handbook_icon);
		mItems.add(itemInfo);
		mShortCut2Widget = mInstall = mItems.size();
		return mItems;
	}

	public ArrayList<Drawable> getWidgetTabShortCutDrawable() {
		//需要与上面的list对应
		ArrayList<Drawable> mItems = new ArrayList<Drawable>();
		Drawable d = mContext.getResources().getDrawable(R.drawable.go_handbook_widget_icon);
		mItems.add(d);
		return mItems;
	}

	public ArrayList<Object> getWidgetTabList() {
		mGoWidgetInfoList = new ArrayList<Object>();
		initData();
		getWidgetList();
		return mGoWidgetInfoList;
	}

	private void initData() {
		if (mThemeInfos != null) {
			mThemeInfos.clear();
			mThemeInfos = null;
		}
		mThemeInfos = new ArrayList<ThemeInfoBean>();

		addTollTheme(mThemeInfos);

	}
	// 加载收费主题
	public void addTollTheme(ArrayList<ThemeInfoBean> arrayList) {

		ArrayList<ThemeInfoBean> paidThemeInfoBeans = getPaidThemeInfoBeans();
		if (paidThemeInfoBeans == null || paidThemeInfoBeans.size() <= 0) {
			return;
		}

		int size = paidThemeInfoBeans.size();
		for (int i = size - 1; i >= 0; --i) {
			arrayList.add(0, paidThemeInfoBeans.get(i));
		}

		paidThemeInfoBeans.clear();
		paidThemeInfoBeans = null;

	}

	// 获取付费主题列表
	private ArrayList<ThemeInfoBean> getPaidThemeInfoBeans() {

		ArrayList<ThemeInfoBean> paidThemeInfoBeans = new ArrayList<ThemeInfoBean>();
		final String xmlFile = LauncherEnv.Path.GOTHEMES_PATH + "gowidget.xml";
		final String iconPath = LauncherEnv.Path.GOTHEMES_PATH + "icon/";

		final StringBuffer curVersionBuf = new StringBuffer();
		final StringBuffer recommendThemesBuf = new StringBuffer();
		// 判断语言
		if (xmlFile != null) {
			InputStream inputStream = null;
			try {
				String language = null;
				XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
				inputStream = new FileInputStream(xmlFile);
				parser.setInput(inputStream, null);
				while (parser.next() != XmlPullParser.END_DOCUMENT) {
					if (parser.getName() != null && parser.getName().equals(LANGUAGE)) {
						language = parser.getAttributeValue("", LANGUAGE);
						break;
					}
				}
				String tmpLaunguage = Locale.getDefault().getLanguage()
						+ Locale.getDefault().getCountry();
				if (language == null || !language.equals(tmpLaunguage)) {
					FileUtil.deleteFile(xmlFile);
				}
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
						inputStream = null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		// 解析xml
		new ThemeInfoParser().parseLauncherThemeXml(mContext, xmlFile, curVersionBuf,
				recommendThemesBuf, paidThemeInfoBeans, ThemeConstants.LAUNCHER_FEATURED_THEME_ID);
		boolean isHasSinaWeibo = false;
		for (ThemeInfoBean bean : paidThemeInfoBeans) {
			String packageName = bean.getPackageName();
			if (WIDGET_WEIBO.equals(packageName)) {
				isHasSinaWeibo = true;
				break;
			}
		}
		/*
		 * if(!isHasSinaWeibo && GoAppUtils.isCnUser(mContext)){ ThemeInfoBean bean
		 * = new ThemeInfoBean("新浪微博", WIDGET_WEIBO);
		 * bean.addDrawableName("sina_weibo");
		 * bean.setThemeInfo(WIDGET_WEIBO_URL); paidThemeInfoBeans.add(bean); }
		 */
		/**
		 * 
		 * <br>类描述:网络状态观察者
		 */
		class ConnectListener implements IConnectListener {
			private String mNewVersion;
			private int mRecommendCount;

			public ConnectListener() {
			}

			public ConnectListener(String version, int recommendCount) {
				mNewVersion = version;
				mRecommendCount = recommendCount;
			}

			@Override
			public void onStart(THttpRequest request) {
			}

			@Override
			public void onFinish(THttpRequest request, IResponse response) {
				if (response.getResponseType() == IResponse.RESPONSE_TYPE_STREAM) {
					AdResponseData resData = (AdResponseData) response.getResponse();

					ArrayList<AdElement> adList = resData.mAdList;
					if (adList != null && adList.size() > 0
							&& adList.get(0).mAdName.equals("version")) {
						mNewVersion = adList.get(0).mAdText;
						if (mNewVersion == null || mNewVersion.trim() == null
								|| mNewVersion.trim().length() <= 0) {
							mNewVersion = "0";
						}
						mRecommendCount = adList.get(0).mMaxDisplayCount;
						String curVersion = null;
						if (curVersionBuf != null && curVersionBuf.toString() != null
								&& curVersionBuf.toString().trim() != null
								&& curVersionBuf.toString().trim().length() > 0) {
							curVersion = curVersionBuf.toString().trim();

						}
						if (curVersion == null
								|| Integer.valueOf(mNewVersion) > (Integer.valueOf(curVersion))) {
							AdHttpAdapter adHttpAdapter = new AdHttpAdapter(mContext,
									new ConnectListener(mNewVersion, mRecommendCount));
							String verString = mContext.getResources().getString(
									R.string.curVersion);
							String fm = Statistics.getUid(mContext);
							String pid = String.valueOf("1013");
							adHttpAdapter.getAdData(null, null, verString, null, pid, 10, null, fm,
									Statistics.getVirtualIMEI(mContext));
						}
					} else {
						boolean result = saveAdElementAsPaidBeanToSDCard(adList, mNewVersion,
								mRecommendCount);
					}
				}
			}

			@Override
			public void onException(THttpRequest request, int reason) {
				StatisticsData.saveHttpExceptionDate(mContext, request, reason);
			}

			private boolean saveAdElementAsPaidBeanToSDCard(ArrayList<AdElement> adList,
					String version, int recommendCount) {
				boolean result = false;
				if (adList == null || adList.size() <= 0) {
					return result;
				}
				int count = adList.size();
				ArrayList<ThemeInfoBean> beansList = new ArrayList<ThemeInfoBean>(count);
				String pkgName = null;
				ThemeInfoBean paidBean = null;
				AdElement adElement = null;
				for (int i = 0; i < count; i++) {
					adElement = adList.get(i);
					pkgName = adElement.mAdOptData;
					String imgName = "gowidget" + String.valueOf(i);
					paidBean = new ThemeInfoBean();
					paidBean.setPackageName(pkgName);
					paidBean.setThemeName(adElement.mAdName);
					paidBean.setThemeInfo(adElement.mAdText);
					String versionCode = String.valueOf(adElement.mDelay);
					paidBean.setVersionCode(versionCode);
					paidBean.setVersionName(adElement.mAppID); // 暂用作版本名称的描述
					FileUtil.saveBitmapToSDFile(adElement.mIcon, iconPath + imgName,
							CompressFormat.PNG);
					paidBean.addDrawableName(imgName);

					beansList.add(paidBean);
				}
				result = new ThemeInfoParser().writeGoThemeToXml(version, recommendCount,
						beansList, xmlFile);
				return result;
			}
		}

		int recomendThemesCount = 0;
		if (recommendThemesBuf != null && recommendThemesBuf.toString() != null
				&& recommendThemesBuf.toString().trim() != null
				&& recommendThemesBuf.toString().trim().length() > 0) {
			try {
				recomendThemesCount = Integer.valueOf(recommendThemesBuf.toString().trim());
			} catch (Exception e) {
				//				Log.i("ThemeManager", "Integer.valueOf has exception");
			}
		}
		AdHttpAdapter adHttpAdapter = new AdHttpAdapter(mContext, new ConnectListener());
		final String pid = String.valueOf("1012");
		int pageCount = 10;
		String fm = Statistics.getUid(mContext);
		String curVerString = mContext.getResources().getString(R.string.curVersion);
		adHttpAdapter.getAdData(null, null, curVerString, null, pid, pageCount, null, fm,
				Statistics.getVirtualIMEI(mContext));

		return paidThemeInfoBeans;
	}

	/**
	 *  获取widget列表信息
	 */
	public synchronized void getWidgetList() {

		mGoWidgetInfoList.clear();

		if (mContext == null) {
			return;
		}

		mFinder = new GoWidgetFinder(mContext);
		mFinder.scanAllInstalledGoWidget();
		mProviderMap = mFinder.getGoWidgetInfosMap();

		Set<Entry<String, GoWidgetProviderInfo>> entryset = mProviderMap.entrySet();
		GoWidgetProviderInfo providerInfo = null;
		int size = 0;
		if (mThemeInfos != null) {
			size = mThemeInfos.size();
		}

		// 已安装
		// 判断是否可以更新 参照 GoWidgetAdapter
		ThemeInfoBean bean = null;
		if (mThemeInfos != null && mThemeInfos.size() > 0) {
			bean = mThemeInfos.get(0);
			if (bean != null && bean.getVersionCode() == null) {
				for (Entry<String, GoWidgetProviderInfo> entry : entryset) {
					mGoWidgetInfoList.add(entry.getValue());
					++mInstall; // 处理卸载后 状态没有变化
				}
			} else {
				String pkgName, beanPkgName;
				for (Entry<String, GoWidgetProviderInfo> entry : entryset) {
					pkgName = entry.getValue().getPkgName();
					for (int i = 0; i < size; i++) {
						bean = mThemeInfos.get(i);
						beanPkgName = bean.getPackageName();
						String versionCode = bean.getVersionCode();
						String versionName = bean.getVersionName();
						if (pkgName.equals(beanPkgName) && versionCode != null) {
							entry.getValue().mVersionCode = Integer.parseInt(versionCode);
							entry.getValue().mVersionName = versionName;
							break;
						}
					}
					++mInstall;
					mGoWidgetInfoList.add(entry.getValue());
				}
			}
		}

		// 获取系统内置的
		final GoWidgetManager widgetManager = AppCore.getInstance().getGoWidgetManager();
		final ArrayList<InnerWidgetInfo> innerWidgets = widgetManager.getInnerWidgetList();

		if (innerWidgets != null) {
			int count = innerWidgets.size();

			for (int i = count - 1; i >= 0; i--) {

				final InnerWidgetInfo innerWidgetInfo = innerWidgets.get(i);
				//update by caoyaming 2014-02-20 如果安装了外置GO开关,则不需要再添加内置Go开关.
				if (innerWidgetInfo == null
						|| innerWidgetInfo.mPrototype == GoWidgetBaseInfo.PROTOTYPE_GOSWITCH
						&& GoSwitchWidgetUtils.isInstallGoSwitchWidget(mContext)) {
					continue;
				}
				//update by caoyaming 2014-02-20 end
				//update by wangzhuobin 2014-04-19 如果安装了GO天气EX,则不需要再添加内置Go天气Widget.
				if (innerWidgetInfo == null
						|| innerWidgetInfo.mPrototype == GoWidgetBaseInfo.PROTOTYPE_GOWEATHER
						&& AppUtils.isAppExist(mContext, PackageName.RECOMMAND_GOWEATHEREX_PACKAGE)) {
					continue;
				}
				//update by wangzhuobin 2014-04-19 end
				if (innerWidgetInfo.mBuildin == InnerWidgetInfo.BUILDIN_ALL) {
					// update by zhoujun 353渠道，不需要应用游戏中心的widget
					if (Statistics.APPGAME_WIDGET_PACKAGE_NAME
							.equals(innerWidgetInfo.mStatisticPackage)) {

						GOLauncherConfig goConfig = GOLauncherConfig.getInstance(ApplicationProxy
								.getContext());
						ChannelConfig channelConfig = goConfig.getChannelConfig();
						if (channelConfig != null
								&& (!channelConfig.isNeedAppCenter() || !channelConfig
										.isNeedGameCenter())) {
							continue;
						}
					}

					mGoWidgetInfoList.add(innerWidgetInfo);
					++mInstall;
				}
			}
		}

		// 未安装
		ThemeInfoBean infoBean = null;
		for (int i = 0; i < size; i++) {
			infoBean = mThemeInfos.get(i);
			providerInfo = mProviderMap.get(infoBean.getPackageName());
			if (providerInfo == null) {
				GoWidgetProviderInfo info = new GoWidgetProviderInfo("", "");
				info.getProviderInfo().label = infoBean.getThemeName();
				if (infoBean.getPackageName() != null) {
					info.mGoWidgetPkgName = infoBean.getPackageName();
				}
				if (infoBean.getPreViewDrawableNames() != null
						&& infoBean.getPreViewDrawableNames().size() > 0) {
					info.mIconPath = infoBean.getPreViewDrawableNames().get(0);
				}
				if (infoBean.getThemeInfo() != null) {
					info.mDownloadUrl = infoBean.getThemeInfo().trim();
				}
				if (infoBean.getGALink() != null) {
					info.mGALink = infoBean.getGALink();
				}
				mGoWidgetInfoList.add(info);
			}
			providerInfo = null;
		}

		// 内置widget和外置一起排序
		GowidgetSortUtil.sort(mGoWidgetInfoList);
	}

	/**
	 * 由于4.05版本中,开关和便签小部件进行自宫,在GO小部件第一次打开时提示有新内容
	 * @param context
	 * @param setIsOpen
	 * @return
	 */
	public static boolean isFristShowScreenEditGOWidget(Context context, boolean setIsOpen) {
		// 添加界面--去除小红点,所以返回false
		return false;
		//		PreferencesManager ps = new PreferencesManager(context);
		//		boolean isFirstShow = ps.getBoolean(IPreferencesIds.SCREENEDIT_FIRST_SHOW_GOWIDGET, true);
		//		if (isFirstShow) {
		//			if (setIsOpen) {
		//				ps.putBoolean(IPreferencesIds.SCREENEDIT_FIRST_SHOW_GOWIDGET, false);
		//				ps.commit();
		//			}
		//			return true;
		//		} else {
		//			return false;
		//		}
	}

	/**
	 * 滤镜新功能第一次new提示
	 * @param context
	 * @param setIsOpen
	 * @return
	 */
	public static boolean isFirstShowScreenEditFilter(Context context, boolean setIsOpen) {
		// 添加界面--去除小红点,所以返回false;
		return false;
		//		PreferencesManager ps = new PreferencesManager(context);
		//		boolean isFirstShow = ps.getBoolean(IPreferencesIds.SCREENEDIT_FIRST_SHOW_FILTER, true);
		//		if (isFirstShow) {
		//			if (setIsOpen) {
		//				ps.putBoolean(IPreferencesIds.SCREENEDIT_FIRST_SHOW_FILTER, false);
		//				ps.commit();
		//			}
		//			return true;
		//		} else {
		//			return false;
		//		}
	}

	private synchronized void cleanup() {
		if (mGoWidgetInfoList != null) {
			mGoWidgetInfoList.clear();
		}
		if (mProviderMap != null) {
			mProviderMap.clear();
		}
		if (mThemeInfos != null) {
			mThemeInfos.clear();
		}

		mPaint = null;
		mXfermode = null;
		mIconMask = null;
		mIconBase = null;
		mDownloadMask = null;
	}

	public static void destroy() {
		if (sInstance != null) {
			sInstance.cleanup();
			sInstance = null;
		}
	}

	public void changeCutMode(boolean isScrolling) {

		ScreenSettingInfo screenInfo = SettingProxy.getScreenSettingInfo();
		if (screenInfo.mWallpaperScroll == isScrolling) {
			return;
		}

		screenInfo.mWallpaperScroll = isScrolling;
		SettingProxy.updateScreenSettingInfo2(screenInfo, false);

		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
				IScreenFrameMsgId.SCREEN_UPDATE_WALLPAPER_FOR_SCROLL_MODE_CHANGE, -1);

		WallpaperDensityUtil.setWallpaperDimension(GoLauncherActivityProxy.getActivity());

		StatisticsData.countUserActionData(StatisticsData.DESK_ACTION_ID_SCREEN_EDIT,
				StatisticsData.USER_ACTION_TWELVE, IPreferencesIds.DESK_ACTION_DATA);
	}

	public Drawable getTabAddWidgetIcon() {
		return ImageExplorer.getInstance(mContext)
				.getDrawable(mThemePkgName, "tab_add_widget_icon");
	}

	public Drawable getTabAddSystemWidgetIcon() {
		return ImageExplorer.getInstance(mContext).getDrawable(mThemePkgName, "screen_edit_widget");
	}

	public Drawable getGestureApplication() {
		return ImageExplorer.getInstance(mContext)
				.getDrawable(mThemePkgName, "gesture_application");
	}

	public Drawable getTabAddFolderIcon() {
		return ImageExplorer.getInstance(mContext)
				.getDrawable(mThemePkgName, "tab_add_folder_icon");
	}

	public Drawable getScreenEditGoShortcut() {
		return ImageExplorer.getInstance(mContext).getDrawable(mThemePkgName,
				"screen_edit_go_shortcut");
	}

	public Drawable getChangeTheme4def3() {
		return ImageExplorer.getInstance(mContext)
				.getDrawable(mThemePkgName, "change_theme_4_def3");
	}
	public Drawable getScreenEditGolocker() {
		return ImageExplorer.getInstance(mContext).getDrawable(mThemePkgName,
				"screen_edit_golocker");
	}

	public Drawable getGoWallpaperLogo() {
		return ImageExplorer.getInstance(mContext).getDrawable(mThemePkgName,
				"screen_edit_wallpaper_gowallpaper");
	}

	public Drawable getGoDynamicWallpaperLogo() {
		return ImageExplorer.getInstance(mContext).getDrawable(mThemePkgName, "livewall");
	}

	public Drawable getGostore4def3() {
		return ImageExplorer.getInstance(mContext).getDrawable(mThemePkgName, "gostore_4_def3");
	}

	public int getNormalEditHeight() {
		Resources resources = mContext.getResources();
		int titleHeight = (int) resources.getDimension(R.dimen.screen_edit_tabtitle_height);
		int containerHeight = (int) resources.getDimension(R.dimen.screen_edit_tab_height_normal);
		int indicateHeight = (int) resources.getDimension(R.dimen.screen_edit_indicator_height);
		return titleHeight + containerHeight + indicateHeight;
	}

	public int getLargeEditHeight() {
		Resources resources = mContext.getResources();
		int titleHeight = (int) resources.getDimension(R.dimen.screen_edit_tabtitle_height);
		int tabHeight = getLargeTabHeight();
		int indicateHeight = (int) resources.getDimension(R.dimen.screen_edit_indicator_height);
		return titleHeight + tabHeight + indicateHeight;
	}
	
	public int getLargeTabHeight() {
		Resources resources = mContext.getResources();
		int tabHeight = 0;
		if (DrawUtils.sHeightPixels <= 800) {
			tabHeight = (int) resources.getDimension(R.dimen.screen_edit_tab_height_large_limit);
		} else {
			tabHeight = (int) resources.getDimension(R.dimen.screen_edit_tab_height_large);
		}
		return tabHeight;
	}
}
