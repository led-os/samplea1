package com.jiubang.ggheart.apps.desks.diy.frames.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import mobi.intuitit.android.content.LauncherIntent;
import mobi.intuitit.android.content.LauncherMetadata;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.SettingProxy;
import com.go.util.AppUtils;
import com.go.util.graphics.ImageUtil;
import com.go.util.market.MarketConstant;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.LockScreenHandler;
import com.jiubang.ggheart.apps.desks.diy.OutOfMemoryHandler;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.apps.gowidget.GoWidgetConstant;
import com.jiubang.ggheart.apps.gowidget.GoWidgetFinder;
import com.jiubang.ggheart.apps.gowidget.GoWidgetManager;
import com.jiubang.ggheart.apps.gowidget.GoWidgetProviderInfo;
import com.jiubang.ggheart.apps.gowidget.WidgetParseInfo;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.data.AppCore;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.AppDataFilter;
import com.jiubang.ggheart.data.info.GoWidgetBaseInfo;
import com.jiubang.ggheart.data.info.IItemType;
import com.jiubang.ggheart.data.info.ItemInfo;
import com.jiubang.ggheart.data.info.ScreenAppWidgetInfo;
import com.jiubang.ggheart.data.info.ScreenFolderInfo;
import com.jiubang.ggheart.data.info.ScreenSettingInfo;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.data.info.UserFolderInfo;
import com.jiubang.ggheart.data.theme.DeskThemeControler;
import com.jiubang.ggheart.data.theme.ImageExplorer;
import com.jiubang.ggheart.data.theme.bean.DeskThemeBean;
import com.jiubang.ggheart.data.theme.bean.DeskThemeBean.FolderStyle;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.IconUtilities;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 屏幕层工具类
 * 
 */
public class ScreenUtils {
	public static void showToast(int id, Context context) {
		String textString = context.getString(id);
		try {
			DeskToast.makeText(context, textString, Toast.LENGTH_SHORT).show();
		} catch (OutOfMemoryError e) {
			OutOfMemoryHandler.handle();
			Log.w("showToast", " Error Code is " + e);
		} catch (Throwable e2) {
			Log.w("showToast", " Error Code is " + e2);
		}
		textString = null;
	}

	/**
	 * 功能描述：弹出Toast
	 * 
	 * @param id
	 * @param context
	 * @param duration
	 *            控制Toast弹出时间 (Either {Toast.LENGTH_SHORT} or
	 *            {Toast.LENGTH_LONG})
	 */
	public static void showToast(int id, Context context, int duration) {
		String textString = context.getString(id);
		try {
			DeskToast.makeText(context, textString, duration).show();
		} catch (OutOfMemoryError e) {
			OutOfMemoryHandler.handle();
			Log.w("showToast", " Error Code is " + e);
		} catch (Throwable e2) {
			Log.w("showToast", " Error Code is " + e2);
		}
		textString = null;
	}

	static void unbindShortcut(ShortCutInfo shortCutInfo) {
		if (shortCutInfo != null && shortCutInfo.mIcon != null) {
			shortCutInfo.selfDestruct();
		}
	}

	public static void unbindeUserFolder(UserFolderInfo folderInfo) {
		if (folderInfo != null) {
			folderInfo.clear();
			folderInfo.selfDestruct();
		}
	}

	public static void unbindDesktopObject(ArrayList<ItemInfo> screenInfos) {
		if (screenInfos == null) {
			return;
		}

		for (ItemInfo itemInfo : screenInfos) {
			if (itemInfo == null) {
				continue;
			}

			final int itemType = itemInfo.mItemType;
			switch (itemType) {
				case IItemType.ITEM_TYPE_APPLICATION : {
					unbindShortcut((ShortCutInfo) itemInfo);
					break;
				}
				case IItemType.ITEM_TYPE_SHORTCUT : {
					unbindShortcut((ShortCutInfo) itemInfo);
					break;
				}

				case IItemType.ITEM_TYPE_USER_FOLDER : {
					unbindeUserFolder((UserFolderInfo) itemInfo);
					break;
				}

				default :
					break;
			}
		}
	}

	static int computeIndex(int cur, int src, int dst) {
		if (src > cur && dst <= cur) {
			++cur; // 从当前位置后面移到前面
		} else if (src < cur && dst >= cur) {
			--cur; // 从当前位置前面移到后面
		} else if (src == cur) {
			cur = dst;
		}
		return cur;
	}

	static void appwidgetReadyBroadcast(int appWidgetId, ComponentName cname, int[] widgetSpan,
			Context context) {
		if (GoWidgetManager.isGoWidget(appWidgetId)) {
			return;
		}

		Intent motosize = new Intent(ICustomAction.ACTION_SET_WIDGET_SIZE);

		motosize.setComponent(cname);
		motosize.putExtra("appWidgetId", appWidgetId);
		motosize.putExtra("spanX", widgetSpan[0]);
		motosize.putExtra("spanY", widgetSpan[1]);
		motosize.putExtra("com.motorola.blur.home.EXTRA_NEW_WIDGET", true);
		context.sendBroadcast(motosize);

		Intent ready = new Intent(LauncherIntent.Action.ACTION_READY)
				.putExtra(LauncherIntent.Extra.EXTRA_APPWIDGET_ID, appWidgetId)
				.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
				.putExtra(LauncherIntent.Extra.EXTRA_API_VERSION,
						LauncherMetadata.CurrentAPIVersion).setComponent(cname);
		context.sendBroadcast(ready);
	}

	static void pauseGoWidget(int screenIndex, SparseArray<ArrayList<ItemInfo>> allInfos) {
		if (allInfos == null) {
			return;
		}

		ArrayList<ItemInfo> list = allInfos.get(screenIndex);
		final GoWidgetManager widgetManager = AppCore.getInstance().getGoWidgetManager();
		if (list != null) {
			int size = list.size();
			for (int i = 0; i < size; i++) {
				ItemInfo info = list.get(i);
				if (info != null && info instanceof ScreenAppWidgetInfo) {
					ScreenAppWidgetInfo widgetInfo = (ScreenAppWidgetInfo) info;
					if (GoWidgetManager.isGoWidget(widgetInfo.mAppWidgetId)) {
						widgetManager.pauseWidget(widgetInfo.mAppWidgetId);
					}
				}
			}
		}
	}

	/**
	 * 获取widget所在的屏幕索引
	 * 
	 * @param widgetid
	 *            widgetid
	 * @param allInfos
	 * @return
	 */
	static int getScreenIndexofWidget(int widgetid, SparseArray<ArrayList<ItemInfo>> allInfos) {
		int screenCount = allInfos.size();
		for (int i = 0; i < screenCount; i++) {
			ArrayList<ItemInfo> list = allInfos.get(i);
			if (list != null) {
				for (ItemInfo itemInfo : list) {
					if (itemInfo.mItemType == IItemType.ITEM_TYPE_APP_WIDGET) {
						ScreenAppWidgetInfo widgetInfo = (ScreenAppWidgetInfo) itemInfo;
						if (widgetInfo.mAppWidgetId == widgetid) {
							return i;
						}
					}
				}
			}
		}
		return -1;
	}

	static void resumeGoWidget(int screenIndex, SparseArray<ArrayList<ItemInfo>> allInfos) {
		if (allInfos == null) {
			return;
		}

		ArrayList<ItemInfo> list = allInfos.get(screenIndex);
		final GoWidgetManager widgetManager = AppCore.getInstance().getGoWidgetManager();
		if (list != null) {
			int size = list.size();
			for (int i = 0; i < size; i++) {
				ItemInfo info = list.get(i);
				if (info != null && info instanceof ScreenAppWidgetInfo) {
					ScreenAppWidgetInfo widgetInfo = (ScreenAppWidgetInfo) info;
					if (GoWidgetManager.isGoWidget(widgetInfo.mAppWidgetId)) {
						widgetManager.resumeWidget(widgetInfo.mAppWidgetId);
					}
				}
			}
		}
	}

	public static CharSequence getItemTitle(ItemInfo targetInfo) {
		if (targetInfo == null) {
			return null;
		}

		CharSequence title = null;
		if (targetInfo.mItemType == IItemType.ITEM_TYPE_APPLICATION
				|| targetInfo.mItemType == IItemType.ITEM_TYPE_SHORTCUT) {
			title = ((ShortCutInfo) targetInfo).mTitle;
		} else if (targetInfo.mItemType == IItemType.ITEM_TYPE_USER_FOLDER) {
			title = ((ScreenFolderInfo) targetInfo).mTitle;
		}
		return title;
	}

	static float easeOut(float begin, float end, float t) {
		t = 1 - t;
		return begin + (end - begin) * (1 - t * t * t);
	}

	/** BEGIN　初始化应用程序列表 */
	/** ScreenFrame初始化了几个桌面初始化应用程序数组里的数据 */
	public static int sScreenInitedDefaultAppCount = 0;

	/** BEGIN　初始化应用程序列表 */
	/** ScreenFrame初始化了几个功能表里的数据 */
	public static int sScreenInitedDefaultAppCountAppFunc = 0;

	/**
	 * <br>
	 * 功能简述:桌面初始化应用程序数组 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @return
	 */
	public static String[] getDefaultInitAppPkg() {
		String[] packageName = null;
		String[] name = null;
		if (GoAppUtils.isCnUser()) {
			name = new String[] {
					"cn.jingling.motu.photowonder", // 魔图精灵
					"com.qihoo360.mobilesafe", // 360
					"com.sds.android.ttpod", // 天天动听
					"com.tencent.qq", PackageName.SINA_WEIBO, MarketConstant.PACKAGE,
					"com.uc.browser", "com.android.clock", "com.android.calendar",
					PackageName.GMAIL, "com.android.settings", "com.android.camera",
					"com.google.android.apps.maps", "com.android.gallery",
					"com.android.calculator", "com.android.music",
					PackageName.GOOGLE_TALK_ANDROID_TALK, "com.android.email",
					"com.android.contacts", "com.android.mms", "com.android.browser" };
		} else {
			String country = Locale.getDefault().getLanguage();
			if (country != null && !country.trim().equals("") && country.toLowerCase().equals("ko")) {
				// 如果是语言是韩国的
				name = new String[] { "com.kakao.talk", "com.nhn.android.search",
						"com.kth.PuddingCamera", "kr.co.tictocplus", "com.btb.minihompy",
						"com.brainpub.phonedecor", "ss.ga.jess", PackageName.GMAIL,
						PackageName.GOOGLE_TALK_ANDROID_TALK, MarketConstant.PACKAGE,
						"com.google.android.apps.maps", "com.android.clock",
						"com.android.calendar", "com.android.settings", "com.android.camera",
						"com.android.gallery", "com.android.calculator", "com.android.music",
						"com.android.email", "com.android.contacts", "com.android.mms",
						"com.android.browser" };
			} else {
				// 其他国家地区
				name = new String[] {
						PackageName.FACEBOOK, // Facebook
						"com.devuni.flashlight", // Tiny Flashlight+LED
						"com.metago.astro", // Astro File Manager
						"com.google.android.apps.translate", // google翻译
						"com.ringdroid", // Ringdroid
						PackageName.TWITTER, // Twitter
						"com.rechild.advancedtaskkiller", // advanced task
															// killer
						PackageName.GMAIL, PackageName.GOOGLE_TALK_ANDROID_TALK,
						MarketConstant.PACKAGE, "com.google.android.apps.maps",
						"com.android.clock", "com.android.calendar", "com.android.settings",
						"com.android.camera", "com.android.gallery", "com.android.calculator",
						"com.android.music", "com.android.email", "com.android.contacts",
						"com.android.mms", "com.android.browser" };
			}
		}
		//Step1: 生成完整常用应用列表，不区分地区
		int capacity = name.length + DEFAULT_APP_PKGS_SYS.length + DEFAULT_APP_PKGS_GOOGLE.length
				+ DEFAULT_APP_PKGS_HOT.length;
		packageName = new String[capacity];
		int dstPos = 0;
		System.arraycopy(name, 0, packageName, dstPos, name.length);
		dstPos += name.length;
		System.arraycopy(DEFAULT_APP_PKGS_SYS, 0, packageName, dstPos, DEFAULT_APP_PKGS_SYS.length);
		dstPos += DEFAULT_APP_PKGS_SYS.length;
		System.arraycopy(DEFAULT_APP_PKGS_GOOGLE, 0, packageName, dstPos,
				DEFAULT_APP_PKGS_GOOGLE.length);
		dstPos += DEFAULT_APP_PKGS_GOOGLE.length;
		System.arraycopy(DEFAULT_APP_PKGS_HOT, 0, packageName, dstPos, DEFAULT_APP_PKGS_HOT.length);
		ArrayList<String> pkgList = new ArrayList<String>();
		//Step2: 如果是国内用户，需要把特定应用排序靠前，后面需要去重
		if (GoAppUtils.isCnUser()) {
			for (String pkg : DEFAULT_APP_PKGS_HOT_CN) {
				pkgList.add(pkg);
			}
		}
		//Step3: 去重，生成目标列表
		for (int i = 0; i < packageName.length; i++) {
			String pkgName = packageName[i];
			if (pkgList.contains(pkgName)) {
				//				Log.i("lichong", "index: " + i + ", pkgName: " + pkgName);
				continue;
			}
			// 去除dock默认四个快捷方式对应的系统应用
			boolean skip = false;
			for (String pkg : PROTOGENIC_APP_PKGS) {
				if (pkg.equals(pkgName)) {
					skip = true;
					break;
				}
			}
			if (skip) {
				continue;
			}
			pkgList.add(pkgName);
		}
		//Step4: 转换数组
		packageName = new String[pkgList.size()];
		pkgList.toArray(packageName);
		return packageName;
	}

	/**
	 * 二期需求，新增通用推荐应用（不区分地区）
	 * <li>分三块，优先级有高到低依次是:DEFAULT_APP_PKGS_SYS,DEFAULT_APP_PKGS_GOOGLE,DEFAULT_APP_PKGS_HOT </li>
	 */
	/**
	 * 原生系统应用 用于dock条固定四个快捷方式去重
	 */
	public static final String[] PROTOGENIC_APP_PKGS = new String[] { "com.android.contacts",
			"com.android.mms", "com.android.browser" };

	/**
	 * 系统应用
	 */
	private static final String[] DEFAULT_APP_PKGS_SYS = new String[] { "com.google.android.talk", // 环聊 Hangouts
			"com.google.android.calendar", // 日历 Calendar
			MarketConstant.PACKAGE, // Google Play商店 
			"com.google.android.gm", // Gmail
	};

	/**
	 * 其他Google应用
	 */
	private static final String[] DEFAULT_APP_PKGS_GOOGLE = new String[] { "com.android.chrome", // Chrome浏览器 
			"com.google.android.apps.plus", // Google+
			"com.google.android.youtube", // YouTube
			"com.google.android.googlequicksearchbox", // Google Search  Google搜索
			"com.google.android.apps.currents", // 新鲜汇 Currents
			"com.facebook.katana", // Facebook
			"com.twitter.android", // Twitter
	};

	/**
	 * 热门应用
	 * {@link #DEFAULT_APP_PKGS_HOT_CN} 国内包这些应用需要提前到最前面
	 */
	private static final String[] DEFAULT_APP_PKGS_HOT_CN = new String[] { "com.tencent.mm", // WeChat 微信
			"com.tencent.mobileqq", // QQ
			"com.UCMobile", // UC浏览器
			"com.sina.weibo", // 新浪微博
			"com.taobao.taobao", // 淘宝
			"com.youku.phone", // 优酷
			"com.zhihu.android", // 知乎
			"com.netease.newsreader.activity", // 网易新闻
	};
	private static final String[] DEFAULT_APP_PKGS_HOT = new String[] { "com.skype.raider", // Skype
			"com.tencent.mm", // WeChat 微信
			"com.tencent.mobileqq", // QQ
			"com.UCMobile", // UC浏览器
			"com.sina.weibo", // 新浪微博
			"com.path", // Path
			"com.instagram.android", // Instagram
			"com.whatsapp", // Whatsapp
			"jp.naver.line.android", // Line
			"com.viber.voip", // Viber
			"com.kakao.talk", // Kakao talk
			"com.jb.gosms", // GO短信
			"com.facebook.orca", // Facebook messenge
			"com.ted.android", // TED
			"com.taobao.taobao", // 淘宝
			"com.youku.phone", // 优酷
			"flipboard.app", // Flipboard
			"flipboard.cn", // Flipboard中国版
			"com.tumblr", // Tumblr
			"com.evernote.world", // 印象笔记
			"com.myzaker.ZAKER_Phone", // 扎客 Zaker
			"com.ideashower.readitlater.pro", // Pocket
			"com.amazon.mShop.android", // Amazon 亚马逊
			"LinkedIn", // LinkedIn
			"com.dropbox.android", // Dropbox
			"com.microsoft.skydrive", // SkyDrive
			"vStudio.Android.Camera360", // 360相机
			"com.tencent.mtt", // QQ浏览器
			"com.netease.pris", // 网易云阅读
			"com.qzone", // QQ空间
			"com.renren.xiaonei.android", // 人人
			"com.zhihu.android", // 知乎
			"com.netease.newsreader.activity", // 网易新闻
			"com.adobe.reader", // Adobe Reader
			"com.bumptech.bumpga", // Bump
			"com.melodis.midomiMusicIdentifier.freemium", // 猎曲骑兵
			"com.shazam.android", // 音乐雷达
			"com.maxmpz.audioplayer", // Poweramp
			"com.drippler.android.updates", // Drippler
			"com.discovercircle10", // 本地网络  Circle
			"tunein.player", // TuneIn Radio
	};

	/** END　初始化应用程序列表 */

	/**
	 * 返回GO系列应用的包名数组
	 * 
	 * @return
	 */
	public static String[] getGoAppsPkgName() {
		String[] goPkgName = { PackageName.GO_STORE_PACKAGE_NAME,
				PackageName.GO_WIDGET_PACKAGE_NAME, PackageName.GO_THEME_PACKAGE_NAME,
				PackageName.RECOMMAND_GOWEATHEREX_PACKAGE, PackageName.RECOMMAND_GOSMS_PACKAGE,
				PackageName.RECOMMAND_GOKEYBOARD_PACKAGE,
				PackageName.RECOMMAND_GOPOWERMASTER_PACKAGE,
				PackageName.RECOMMAND_GOTASKMANAGER_PACKAGE,
				PackageName.RECOMMAND_GOLOCKER_PACKAGE, PackageName.RECOMMAND_GOBACKUPEX_PACKAGE,
				PackageName.RECOMMAND_LOCKSCREEN_PACKAGE };
		return goPkgName;
	}

	/**
	 * 返回GO系列应用的程序名称的id数组
	 * 
	 * @return
	 */
	public static int[] getGoAppsNameIds() {
		int[] goAppsNameIds = { R.string.customname_gostore, R.string.func_gowidget_icon,
				R.string.go_theme, R.string.recommand_goweatherex, R.string.recommand_gosms,
				R.string.recommand_gokeyboard, R.string.recommand_gopowermaster,
				R.string.recommand_gotaskmanager, R.string.customname_golocker,
				R.string.recommand_gobackup, R.string.recommand_lockscreen };
		return goAppsNameIds;
	}

	/**
	 * 返回GO系列应用的图标id数组
	 * 
	 * @return
	 */
	public static int[] getGoAppsIconIds() {
		int[] goAppsIconIds = { R.drawable.store, R.drawable.gowidget,
				R.drawable.change_theme_4_def3, R.drawable.goweatherex_4_def3,
				R.drawable.gosmspro_4_def3, R.drawable.recommand_icon_keyboard,
				R.drawable.recommand_icon_gopowermaster, R.drawable.recommand_icon_gotaskmanager,
				R.drawable.screen_edit_golocker, R.drawable.gobackup, R.drawable.lock_screen };
		return goAppsIconIds;
	}

	/**
	 * 返回GO系列应用的Action
	 * 
	 * @return
	 */
	public static String[] getGoAppsActions() {
		String[] goAppsActions = { ICustomAction.ACTION_FUNC_SPECIAL_APP_GOSTORE,
				ICustomAction.ACTION_FUNC_SPECIAL_APP_GOWIDGET,
				ICustomAction.ACTION_FUNC_SPECIAL_APP_GOTHEME,
				ICustomAction.ACTION_RECOMMAND_GOWEATHEREX_DOWNLOAD,
				ICustomAction.ACTION_RECOMMAND_GOSMS_DOWNLOAD,
				ICustomAction.ACTION_RECOMMAND_GOKEYBOARD_DOWNLOAD,
				ICustomAction.ACTION_RECOMMAND_GOPOWERMASTER_DOWNLOAD,
				ICustomAction.ACTION_RECOMMAND_GOTASKMANAGER_DOWNLOAD,
				ICustomAction.ACTION_RECOMMAND_GOLOCKER_DOWNLOAD,
				ICustomAction.ACTION_RECOMMAND_GOBACKUP_DOWNLOAD,
				ICustomAction.ACTION_RECOMMAND_LOCKSCREEN_DOWNLOAD };
		return goAppsActions;
	}

	/**
	 * 返回GO系列应用的FTP地址 前四个分别是GOStore、GOWidget和桌面主题，不需要FTP地址
	 * 
	 * @return
	 */
	public static String[] getGoAppsFtpUrl() {
		String[] goAppsFtpUrls = { "", "", "", LauncherEnv.Url.GOWEATHEREX_FTP_URL,
				LauncherEnv.Url.GOSMSPRO_FTP_URL, LauncherEnv.Url.GOKEYBOARD_FTP_URL,
				LauncherEnv.Url.GOPOWERMASTER_FTP_URL, LauncherEnv.Url.GOTASKMANAGEREX_FTP_URL,
				LauncherEnv.Url.GOLOCKER_FTP_URL, LauncherEnv.Url.GOBACKUP_EX_FTP_URL,
				LauncherEnv.Url.LOCK_SCREEN };
		return goAppsFtpUrls;
	}

	public static void startFeedbackIntent(final Context context) {
		String bugString = context.getResources().getString(R.string.feedback_select_type_bug);
		String suggestionString = context.getResources().getString(
				R.string.feedback_select_type_suggestion);
		String questionString = context.getResources().getString(
				R.string.feedback_select_type_question);
		final CharSequence[] items = { bugString, suggestionString, questionString };

		String bugForMailString = context.getResources().getString(
				R.string.feedback_select_type_bug_for_mail);
		String suggestionForMailString = context.getResources().getString(
				R.string.feedback_select_type_suggestion_for_mail);
		String questionForMailString = context.getResources().getString(
				R.string.feedback_select_type_question_for_mail);
		final CharSequence[] itemsForMail = { bugForMailString, suggestionForMailString,
				questionForMailString };

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.feedback_select_type_title);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int index) {
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				String[] receiver = new String[] { "golauncher@goforandroid.com" };
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, receiver);
				String subject = "GO Launcher EX(v" + context.getString(R.string.curVersion)
						+ ") Feedback(" + itemsForMail[index] + ")";
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
				//				String body = "\n\n";
				StringBuffer body = new StringBuffer("\n\n");
				body.append("\nProduct=" + android.os.Build.PRODUCT);
				body.append("\nPhoneModel=" + android.os.Build.MODEL);
				body.append("\nROM=" + android.os.Build.DISPLAY);
				body.append("\nBoard=" + android.os.Build.BOARD);
				body.append("\nDevice=" + android.os.Build.DEVICE);
				body.append("\nDensity="
						+ String.valueOf(context.getResources().getDisplayMetrics().density));
				body.append("\nPackageName=" + context.getPackageName());
				body.append("\nAndroidVersion=" + android.os.Build.VERSION.RELEASE);
				body.append("\nTotalMemSize="
						+ (AppUtils.getTotalInternalMemorySize() / 1024 / 1024) + "MB");
				body.append("\nFreeMemSize="
						+ (AppUtils.getAvailableInternalMemorySize() / 1024 / 1024) + "MB");
				body.append("\nRom App Heap Size="
						+ Integer
								.toString((int) (Runtime.getRuntime().maxMemory() / 1024L / 1024L))
						+ "MB");
				emailIntent.putExtra(Intent.EXTRA_TEXT, body.toString());
				emailIntent.setType("plain/text");
				try {
					context.startActivity(emailIntent);
				} catch (Exception e) {
					// e.printStackTrace();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("http://golauncher.goforandroid.com"));
					intent.setClassName("com.android.browser",
							"com.android.browser.BrowserActivity");
					context.startActivity(intent);
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * 获取文件夹默认背景图
	 * 
	 * @return
	 */
	public static BitmapDrawable getFolderBackIcon() {
		DeskThemeControler themeControler = AppCore.getInstance().getDeskThemeControler();
		FolderStyle folderStyle = null;
		// GO主题类型
		ImageExplorer imageExplorer = ImageExplorer.getInstance(ApplicationProxy.getContext());
		if (themeControler != null /* && themeControler.isUesdTheme() */) {
			DeskThemeBean themeBean = themeControler.getDeskThemeBean();
			if (themeBean != null && themeBean.mScreen != null) {
				folderStyle = themeBean.mScreen.mFolderStyle;
			}
		}
		if (folderStyle != null && folderStyle.mBackground != null) {
			Drawable icon = imageExplorer.getDrawable(folderStyle.mPackageName,
					folderStyle.mBackground.mResName);
			if (icon != null) {
				return (BitmapDrawable) icon;
			}
		}
		return (BitmapDrawable) ApplicationProxy.getContext().getResources()
				.getDrawable(R.drawable.folder_back);

	}

	/**
	 * 获取对应widget的所有样式
	 * 
	 * @param mActivity
	 * @param packageName
	 *            包名
	 * @return
	 */
	public static ArrayList<WidgetParseInfo> getWidgetStyle(Context mActivity, String packageName) {
		try {
			ArrayList<WidgetParseInfo> mWidgetDatasScan = new ArrayList<WidgetParseInfo>();

			int count = 0;
			Resources resources = mActivity.getPackageManager().getResourcesForApplication(
					packageName);

			// 获取图片
			int drawableList = resources.getIdentifier(GoWidgetConstant.PREVIEW_LIST, "array",
					packageName);
			if (drawableList > 0) {
				final String[] extras = resources.getStringArray(drawableList);
				for (String extra : extras) {
					int res = resources.getIdentifier(extra, "drawable", packageName);
					if (res != 0) {
						WidgetParseInfo item = new WidgetParseInfo();
						item.resouceId = res;
						item.resouces = resources;
						item.themePackage = null;
						mWidgetDatasScan.add(item);
					}
				}
			}

			// 获取标题
			int titilList = resources.getIdentifier(GoWidgetConstant.STYLE_NAME_LIST, "array",
					packageName);
			if (titilList > 0) {
				final String[] titles = resources.getStringArray(titilList);
				count = 0;
				for (String titl : titles) {
					int res = resources.getIdentifier(titl, "string", packageName);
					if (res != 0) {
						WidgetParseInfo item = mWidgetDatasScan.get(count);
						item.title = resources.getString(res);
						count++;
					}
				}
			}

			// 获取类型
			int typeList = resources
					.getIdentifier(GoWidgetConstant.TYPE_LIST, "array", packageName);
			if (typeList > 0) {
				final int[] typeLists = resources.getIntArray(typeList);
				count = 0;
				for (int types : typeLists) {

					WidgetParseInfo item = mWidgetDatasScan.get(count);
					item.type = types;
					item.styleType = String.valueOf(types);
					count++;
				}
			}

			// 获取行数
			int rowList = resources.getIdentifier(GoWidgetConstant.ROW_LIST, "array", packageName);
			if (rowList > 0) {
				final int[] rowLists = resources.getIntArray(rowList);
				count = 0;
				for (int row : rowLists) {

					WidgetParseInfo item = mWidgetDatasScan.get(count);
					item.mRow = row;
					count++;
				}
			}

			// 获取列数
			int colList = resources.getIdentifier(GoWidgetConstant.COL_LIST, "array", packageName);
			if (colList > 0) {
				final int[] colListS = resources.getIntArray(colList);
				count = 0;
				for (int col : colListS) {

					WidgetParseInfo item = mWidgetDatasScan.get(count);
					item.mCol = col;
					count++;
				}
			}

			// 获取layout id
			int layoutIDList = resources.getIdentifier(GoWidgetConstant.LAYOUT_LIST, "array",
					packageName);
			if (layoutIDList > 0) {
				final String[] layouIds = resources.getStringArray(layoutIDList);
				count = 0;
				for (String id : layouIds) {
					WidgetParseInfo item = mWidgetDatasScan.get(count);
					item.layoutID = id;
					count++;
				}
			}

			// 获取竖屏最小宽度
			int minWidthVer = resources.getIdentifier(GoWidgetConstant.MIN_WIDTH, "array",
					packageName);
			if (minWidthVer > 0) {
				final int[] widthIds = resources.getIntArray(minWidthVer);
				count = 0;
				for (int w : widthIds) {
					WidgetParseInfo item = mWidgetDatasScan.get(count);
					item.minWidth = w;
					count++;
				}
			}

			// 获取竖屏最小高度
			int minHeightVer = resources.getIdentifier(GoWidgetConstant.MIN_HEIGHT, "array",
					packageName);
			if (minHeightVer > 0) {
				final int[] widthIds = resources.getIntArray(minHeightVer);
				count = 0;
				for (int h : widthIds) {
					WidgetParseInfo item = mWidgetDatasScan.get(count);
					item.minHeight = h;
					count++;
				}
			}
			
			try {
				// 获取可以互换组
				int replaceGroup = resources.getIdentifier(
						GoWidgetConstant.REPLACE_GROUP, "array",
						packageName);
				if (replaceGroup > 0) {
					final int[] replaceGroups = resources
							.getIntArray(replaceGroup);
					count = 0;
					for (int group : replaceGroups) {

						WidgetParseInfo item = mWidgetDatasScan.get(count);
						item.replaceGroup = group;
						count++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 获取layout id
			int configActivityList = resources.getIdentifier(GoWidgetConstant.CONFIG_LIST, "array",
					packageName);
			if (configActivityList > 0) {
				final String[] layouIds = resources.getStringArray(configActivityList);
				count = 0;
				for (String id : layouIds) {
					WidgetParseInfo item = mWidgetDatasScan.get(count);
					item.configActivty = id;
					count++;
				}
			}

			int longkeyconfigActivityList = resources.getIdentifier(GoWidgetConstant.SETTING_LIST,
					"array", packageName);
			if (longkeyconfigActivityList > 0) {
				final String[] layouIds = resources.getStringArray(longkeyconfigActivityList);
				count = 0;
				for (String id : layouIds) {
					WidgetParseInfo item = mWidgetDatasScan.get(count);
					item.longkeyConfigActivty = id;
					count++;
				}
			}
			return mWidgetDatasScan;
		} catch (Exception e) {
			Log.i("ScreenUtils", "getWidgetStyle() has exception = " + e.getMessage());
			return null;
		}
	}

	/**
	 * 获取对应widget的信息
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static GoWidgetProviderInfo getWidgetProviderInfo(Context context, String packageName) {
		GoWidgetProviderInfo mGoWidgetProviderInfo = null;

		GoWidgetFinder mFinder = new GoWidgetFinder(context);
		mFinder.scanAllInstalledGoWidget();
		HashMap<String, GoWidgetProviderInfo> mProviderMap = mFinder.getGoWidgetInfosMap();
		Set<Entry<String, GoWidgetProviderInfo>> entryset = mProviderMap.entrySet();
		for (Entry<String, GoWidgetProviderInfo> entry : entryset) {
			String pkgName = entry.getValue().getPkgName();
			if (pkgName.equals(packageName)) {
				mGoWidgetProviderInfo = entry.getValue();
				break;
			}
		}
		return mGoWidgetProviderInfo;
	}

	/**
	 * 获取go天气调整大小后匹配的样式
	 * 
	 * @param widgetStyleList
	 * @param widgetProviderInfo
	 * @param rect
	 * @param curWidgetCols
	 * @param curWidgetRows
	 * @param curWidgetInfo
	 * @return
	 */
	public static WidgetParseInfo getWeatherWidgetStyle(ArrayList<WidgetParseInfo> widgetStyleList,
			GoWidgetProviderInfo widgetProviderInfo, Rect rect, int curWidgetCols,
			int curWidgetRows, GoWidgetBaseInfo curWidgetInfo) {
		// 获取包命对应widget的样式列表
		if (widgetStyleList == null || widgetProviderInfo == null || rect == null
				|| curWidgetCols <= 0 || curWidgetRows <= 0 || curWidgetInfo == null) {
			return null;
		}

		int currentIndex = -1;
		int newRows = rect.height(); // 新区域行数
		int newCols = rect.width(); // 新区域列数

		String curWidgetLayout = curWidgetInfo.mLayout; // 当前widget布局

		//		Log.i("lch", "当前widget:" + curWidgetCols + "x" + curWidgetRows);
		//		Log.i("lch", "拖动区域行数:" + newCols + "x" + newRows);
		//		Log.i("lch", "rect:" + rect.toString());

		// 判断新区域行数和列数是否一直
		if (curWidgetCols == newCols && curWidgetRows == newRows) {
			//			Log.i("lch", "区域和当前widget大小一样");
			return null;
		}

		// 遍历已有样式，看是否有符合当前行X列的样式
		int widgetStyleSize = widgetStyleList.size();
		for (int i = 0; i < widgetStyleSize; i++) {
			WidgetParseInfo widgetParseInfo = widgetStyleList.get(i);
			if (newRows == widgetParseInfo.mRow
					&& newCols == widgetParseInfo.mCol && widgetParseInfo.replaceGroup == curWidgetInfo.mReplaceGroup) {
				// 判断当前widget布局文件是否和匹配出来的布局文件一样，一样就退出
				// 例如3x1 变成 4x1
				if (curWidgetLayout.equals(widgetParseInfo.layoutID)) {
					//					Log.i("lch", "布局一样，不需要更换11：");
					//					Log.i("lch", curWidgetCols + "x" + curWidgetRows + " --> "
					//							+ newCols + "x" + newRows);
					return null;
				}

				currentIndex = i;
				//				Log.i("lch", "存在匹配样式11：currentIndex:" + currentIndex
				//						+ "   style: " + newCols + "x" + newRows);
				break;
			}
		}

		// 没有匹配到合适的样式，重新循环遍历。处理特殊情况
		if (currentIndex == -1) {
			for (int i = 0; i < widgetStyleSize; i++) {
				WidgetParseInfo widgetParseInfo = widgetStyleList.get(i);

				// 处理由1行直接拖动大于2行的，直接获取4x2的样式
				if (newRows > 2 && widgetParseInfo.mRow == 2
						&& widgetParseInfo.mCol == 4  && widgetParseInfo.replaceGroup == curWidgetInfo.mReplaceGroup) {
					// 判断当前widget样式是否已经是4x2
					if (curWidgetLayout.equals(widgetParseInfo.layoutID)) {
						//						Log.i("lch", "当前widget样式是否已经是4x2！");
						return null;
					}
					currentIndex = i;
					//					Log.i("lch", "存在匹配样式：currentIndex:" + currentIndex);
					break;
				}

				// 拖动区域是1行或者2行，就取 列数=4 和 行数=新行数 的样式
				else if (newRows <= 2 && widgetParseInfo.mRow == newRows
						&& widgetParseInfo.mCol == 4  && widgetParseInfo.replaceGroup == curWidgetInfo.mReplaceGroup) {
					// 判断当前样式是否一样，是否同一个局部，例如6x1（4x1） 变 3x1
					if (curWidgetLayout.equals(widgetParseInfo.layoutID)) {
						//						Log.i("lch", "布局一样，不需要更换22：");
						//						Log.i("lch", curWidgetCols + "x" + curWidgetRows
						//								+ " --> " + newCols + "x" + newRows);
						return null;
					}
					currentIndex = i;
					//					Log.i("lch", "存在匹配样式22：currentIndex:" + currentIndex
					//							+ "   style: " + widgetParseInfo.mCol + "x"
					//							+ widgetParseInfo.mRow);
					break;
				}
			}
		}

		if (currentIndex == -1) {
			return null;
		} else {
			WidgetParseInfo info = widgetStyleList.get(currentIndex);
			if (info.mCol == curWidgetCols && info.mRow == curWidgetRows) {
				return null;
			}
			return info;
		}
	}

	/**
	 * 获取go任务管理器调整大小后匹配的样式
	 * 
	 * @param widgetStyleList
	 * @param widgetProviderInfo
	 * @param rect
	 * @param curWidgetCols
	 * @param curWidgetRows
	 * @param curWidgetInfo
	 * @return
	 */
	public static WidgetParseInfo getTaskWidgetStyle(ArrayList<WidgetParseInfo> widgetStyleList,
			GoWidgetProviderInfo widgetProviderInfo, Rect rect, int curWidgetCols,
			int curWidgetRows, GoWidgetBaseInfo curWidgetInfo) {
		// 获取包命对应widget的样式列表
		if (widgetStyleList == null || widgetProviderInfo == null || rect == null
				|| curWidgetCols <= 0 || curWidgetRows <= 0 || curWidgetInfo == null) {
			return null;
		}

		String curWidgetLayout = curWidgetInfo.mLayout; // 当前widget布局
		int curWidgetType = curWidgetInfo.mType; // 当前样式类型

		int curType = -1;
		int type4X1 = 1; // 和widget配置文件对应的值一致
		int type4X2 = 2;

		// 判断当前类型是否指定的4x1或者4x2
		if (curWidgetType == type4X1) {
			curType = type4X1;
		}

		else if (curWidgetType == type4X2) {
			curType = type4X2;
		}

		if (!(curType == type4X1 || curType == type4X2)) {
			return null;
		}

		int currentIndex = -1;
		int newRows = rect.height(); // 新区域行数
		int newCols = rect.width(); // 新区域列数

		// 判断新区域行数和列数是否一直
		if (curWidgetCols == newCols && curWidgetRows == newRows) {
			return null;
		}

		// 遍历已有样式，看是否有符合当前行X列的样式
		int widgetStyleSize = widgetStyleList.size();
		for (int i = 0; i < widgetStyleSize; i++) {
			WidgetParseInfo widgetParseInfo = widgetStyleList.get(i);
			// 判断类型是否4x1 和4x2
			if (widgetParseInfo.type == type4X1
					|| widgetParseInfo.type == type4X2) {
				if (newRows == widgetParseInfo.mRow
						&& newCols == widgetParseInfo.mCol && widgetParseInfo.replaceGroup == curWidgetInfo.mReplaceGroup) {
					// 判断当前widget布局文件是否和匹配出来的布局文件一样，一样就退出
					// 例如3x1 变成 4x1
					if (curWidgetLayout.equals(widgetParseInfo.layoutID)) {
						return null;
					}

					currentIndex = i;
					break;
				}
			}
		}

		// 没有匹配到合适的样式，重新循环遍历。处理特殊情况
		if (currentIndex == -1) {
			for (int i = 0; i < widgetStyleSize; i++) {
				WidgetParseInfo widgetParseInfo = widgetStyleList.get(i);

				// 判断类型是否4x1 和4x2
				if (widgetParseInfo.type == type4X1 || widgetParseInfo.type == type4X2) {

					// 处理由1行直接拖动大于2行的，直接获取4x2的样式
					if (newRows > 2 && widgetParseInfo.mRow == 2
							&& widgetParseInfo.mCol == 4 && widgetParseInfo.replaceGroup == curWidgetInfo.mReplaceGroup) {
						// 判断当前widget样式是否已经是4x2
						if (curWidgetLayout.equals(widgetParseInfo.layoutID)) {
							return null;
						}
						currentIndex = i;
						break;
					}

					// 拖动区域是1行或者2行，就取 列数=4 和 行数=新行数 的样式
					else if (newRows <= 2 && widgetParseInfo.mRow == newRows
							&& widgetParseInfo.mCol == 4 && widgetParseInfo.replaceGroup == curWidgetInfo.mReplaceGroup) {
						// 判断当前样式是否一样，是否同一个局部，例如6x1（4x1） 变 3x1
						if (curWidgetLayout.equals(widgetParseInfo.layoutID)) {
							return null;
						}
						currentIndex = i;
						break;
					}
				}
			}
		}

		if (currentIndex == -1) {
			return null;
		} else {
			WidgetParseInfo info = widgetStyleList.get(currentIndex);
			if (info.mCol == curWidgetCols && info.mRow == curWidgetRows) {
				return null;
			}
			return widgetStyleList.get(currentIndex);
		}
	}

	public static int getReplaceGroupForGoWidget(Context context, GoWidgetBaseInfo info) {
		if (info != null) {
			ArrayList<WidgetParseInfo> parseInfos = getWidgetStyle(context,
					info.mPackage);
			if (parseInfos != null) {
				for (WidgetParseInfo parseInfo : parseInfos) {
					if (info.mLayout.equals(parseInfo.layoutID)) {
						return parseInfo.replaceGroup;
					}
				}
			}
		}
		return 0;
	}
	
	/**
	 * <br>
	 * 功能简述:通过drawableId拿Go系列推荐图标图片 <br>
	 * 功能详细描述:可以过滤某些图标进行download tag标签合成图片（tag图片共享一张，减少图片资源） <br>
	 * 注意:
	 * 
	 * @param drawableId
	 * @return　经过合成规则处理后的图片
	 */
	public static Drawable getGoAppsIcons(Context context, int drawableId) {
		Drawable drawable = context.getResources().getDrawable(drawableId);
		if (drawableId == R.drawable.goweatherex_4_def3 || drawableId == R.drawable.gosmspro_4_def3
				|| drawableId == R.drawable.screen_edit_golocker
				|| drawableId == R.drawable.lock_screen
				|| drawableId == R.drawable.recommand_icon_keyboard
				|| drawableId == R.drawable.recommand_icon_gopowermaster
				|| drawableId == R.drawable.recommand_icon_gotaskmanager) {
			drawable = composeRecommendIcon(context, drawable);
		}
		return drawable;
	}

	/**
	 * 合成图标和推荐图片，形成推荐图标
	 * @return
	 */
	public static Drawable composeRecommendIcon(Context context, int drawableId) {
		Drawable drawable = context.getResources().getDrawable(drawableId);
		return composeRecommendIcon(context, drawable);
	}

	/**
	 * 合成图标和推荐图片，形成推荐图标
	 * @return
	 */
	private static Drawable composeRecommendIcon(Context context, Drawable drawable) {
		if (drawable == null) {
			return drawable;
		}
		Drawable tag = context.getResources().getDrawable(R.drawable.recommand_icon_tag);
		try {
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			Canvas cv = new Canvas(bmp);
			ImageUtil.drawImage(cv, drawable, ImageUtil.STRETCHMODE, 0, 0, width, height, null);
			ImageUtil.drawImage(cv, tag, ImageUtil.STRETCHMODE, 0, 0, width, height, null);
			BitmapDrawable bmd = new BitmapDrawable(bmp);
			drawable = bmd;
		} catch (Throwable e) {
			// 出错则不进行download Tag合成图
		}
		return drawable;
	}

	/**
	 * 合成自定义图标与底图
	 */
	public static Drawable composeCustomIconBack(Drawable drawable) {
		if (drawable == null) {
			return drawable;
		}
		Context context = ApplicationProxy.getContext();
		AppDataFilter appDataFilter = AppDataEngine.getInstance(context).getAppDataFilter();
		BitmapDrawable iconBack = appDataFilter.getIconback(true);
		if (iconBack != null) {
			Bitmap iconBackBitmap = iconBack.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			if (bitmap != null) {
				float scale = appDataFilter.getIconScaleFactor() * 0.9f;
				Drawable composeDrawable = IconUtilities.composeAppIconDrawable(iconBackBitmap,
						null, bitmapDrawable, null, scale);
				if (composeDrawable != null) {
					return composeDrawable;
				}
			}
		}
		return drawable;
	}

	/**
	 * 弹出锁定屏幕提示对话框
	 */
	public static void showLockScreenDialog() {
		final PreferencesManager sharedPreferences = new PreferencesManager(
				ApplicationProxy.getContext(), IPreferencesIds.USERTUTORIALCONFIG,
				Context.MODE_PRIVATE);
		boolean needShowPayDialog = sharedPreferences.getBoolean(
				IPreferencesIds.SHOULD_SHOW_SCREEN_LOCK_GUIDE_PAY_DIALOG, true);
		int status = FunctionPurchaseManager.getInstance(ApplicationProxy.getContext())
				.getPayFunctionState(FunctionPurchaseManager.PURCHASE_ITEM_SECURITY);
		handleScreenLock(sharedPreferences);
		// 显示已付费情况下的弹框
		DialogConfirm lockScreenDialog = new DialogConfirm(GoLauncherActivityProxy.getActivity());
		lockScreenDialog.show();
		lockScreenDialog.setNegativeButtonVisible(View.GONE);
		lockScreenDialog.setTitle(R.string.menuitem_lockdesktop_lock);
		lockScreenDialog.setMessage(R.string.lockdesktop_dialog_context);
	}

	/**
	 * 处理屏幕锁定事件
	 * @param sharedPreferences
	 */
	private static void handleScreenLock(PreferencesManager sharedPreferences) {
		try {
			ScreenSettingInfo info = SettingProxy.getScreenSettingInfo();
			info.mLockScreen = true;
			SettingProxy.updateScreenSettingInfo(info);
			boolean needShowLockTutorial = sharedPreferences.getBoolean(
					IPreferencesIds.SHOULD_SHOW_SCREEN_LOCK_GUIDE, true);
			if (needShowLockTutorial) {
				LockScreenHandler.showUnlockScreenNotificationLong(GoLauncherActivityProxy
						.getActivity());
				sharedPreferences.putBoolean(IPreferencesIds.SHOULD_SHOW_SCREEN_LOCK_GUIDE, false);

				sharedPreferences.putBoolean(IPreferencesIds.SHOULD_SHOW_SCREEN_LOCK_GGMENU, true);
				sharedPreferences.commit();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
