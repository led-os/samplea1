package com.jiubang.ggheart.apps.desks.Preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.gau.go.launcherex.key.ndk.NdkUtil;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.util.file.FileUtil;
import com.go.util.graphics.effector.united.EffectorControler;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DesksettingNoAdvertTipDialog;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.bean.WallpaperSubInfo;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.ConstValue;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.data.info.ThemeSettingInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 
 * <br>类描述:桌面设置工具类
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2013-5-22]
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DeskSettingUtils {
	
	public static final String ACTION_KEY_NOTIFLY_GO_LAUNCHER = "com.gau.go.launcherex.key.ACTION_KEY_NOTIFLY_GO_LAUNCHER";
	public static final String ACTION_HAD_PAY_REFRESH = "com.gau.go.launcherex.action_had_pay_refresh";
	private final static String KEYSIGNER_STRING =  "android.content.pm.Signature@f8cc4014"; //"android.content.pm.Signature@f583c575";
	private final static String KEY_PACKAGE_RECHECK_BROADCAST = "com.gau.go.launcherex.key.recheck";
	private final static String CHECK_STATUS = "check_status";
	//"android.content.pm.Signature@8e635ffa";// ffa 为测试签名
	public static final String KEY_PACKAGE_NAME_STRING = "com.gau.go.launcherex.key";
	public static final String KEY_CLASS_NAME_STRING = "com.gau.go.launcherex.key.GuideMainActivity"; 
	public static final String GO_LAUNCHEREX_MARKET_KEY_URI = "market://details?id="
			+ KEY_PACKAGE_NAME_STRING;
			
	
	private static BroadcastReceiver sKeyReceiver;
	private static String sGomarketEntrance;
	private static final int CLEAN_SP = 1;
	private static final int DISABLE_APP = 2;
	private static final int RECHECK = 3;
	
	/**
	 * 付费功能当前状态，默认可见但需要付费才能用
	 */
	private static int sPayFunctionState = 1;
	
	/**
	 * 付费功能状态-不可见不可用
	 */
	public static final int STATE_GONE = 0;
	
	/**
	 * 付费功能状态-可见但需要付费才能用
	 */
	public static final int STATE_VISABLE = 1;
	
	/**
	 * 付费功能状态-可见可用
	 */
	public static final int STATE_CAN_USE = 2; 

	/**
	 * 大于4.07版本的version code
	 */
	public static final int REOPEN_NEW_FLAG_VERSION_CODE = 299;
	
	/**
	 * <br>功能简述:显示付费对话框
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 */
	public static void showPayDialog(Context context, int entranceId) {
		String entrance = Integer.toString(entranceId);
		if (GoAppUtils.isAppExist(context, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
			showGetJarPayPage(context, entrance);
		} else {
			Intent intent = new Intent();
			intent.putExtra(DeskSettingAdvancedPayActivity.ENTRANCE_ID, entrance);
			if (entrance.startsWith("1") && entrance.length() == 3) {
				//总入口
				intent.putExtra(DeskSettingAdvancedPayActivity.CURRENT_INDEX,
						DeskSettingAdvancedPayActivity.PAY_START_PAGE);
			} else if (entrance.startsWith("3") && entrance.length() == 3) {
				//手势入口
				intent.putExtra(DeskSettingAdvancedPayActivity.CURRENT_INDEX,
						DeskSettingAdvancedPayActivity.PAY_GESTURE);
			} else if (entrance.startsWith("4") && entrance.length() == 3) {
				//特效入口
				intent.putExtra(DeskSettingAdvancedPayActivity.CURRENT_INDEX,
						DeskSettingAdvancedPayActivity.PAY_EFFECTOR);
			} else if (entrance.startsWith("5") && entrance.length() == 3) {
				//安全锁入口
				intent.putExtra(DeskSettingAdvancedPayActivity.CURRENT_INDEX,
						DeskSettingAdvancedPayActivity.PAY_SECURITY_LOCK);
			} else if (entrance.startsWith("6") && entrance.length() == 3) {
				//侧边栏入口
				intent.putExtra(DeskSettingAdvancedPayActivity.CURRENT_INDEX,
						DeskSettingAdvancedPayActivity.PAY_SIDEBAR);
			}  else if (entrance.startsWith("7") && entrance.length() == 3) {
				//滤镜入口
				intent.putExtra(DeskSettingAdvancedPayActivity.CURRENT_INDEX,
						DeskSettingAdvancedPayActivity.PAY_WALLPAPER_FILTER);
			} else {
				//其他默认为无广告入口
				intent.putExtra(DeskSettingAdvancedPayActivity.CURRENT_INDEX,
						DeskSettingAdvancedPayActivity.PAY_NO_ADS);
			}
			intent.setClass(context, DeskSettingAdvancedPayActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			//上传统计
			GuiThemeStatistics.payStaticData("g001", 1, entrance);
		}
	}
	
	private static void showGetJarPayPage(Context context, String entrance) {
		int getJarEntrance = 7;
		if (entrance.startsWith("3") && entrance.length() == 3) {
			//手势入口
			getJarEntrance = 8;
		} else if (entrance.startsWith("4") && entrance.length() == 3) {
			//特效入口
			getJarEntrance = 10;
		} else if (entrance.startsWith("5") && entrance.length() == 3) {
			//安全锁入口
			getJarEntrance = 13;
		} else if (entrance.startsWith("6") && entrance.length() == 3) {
			//侧边栏入口
			getJarEntrance = 11;
		} else if (entrance.startsWith("1") && entrance.length() == 3) {
			//钻石
			getJarEntrance = 1;
		} else if (entrance.equals("701")) {
			getJarEntrance = 15;
		} else if (entrance.equals("702")) {
			getJarEntrance = 16;
		}
//		String tabId = FunctionPurchaseManager.TAB_FUNCTION_NO_PAY;
//		if (FunctionPurchaseManager.getInstance(context).isPayAnyFunction()) {
//			tabId = FunctionPurchaseManager.TAB_FUNCTION_HAS_PAY;
//		}
		FunctionPurchaseManager.getInstance(context).startItemPayPage(getJarEntrance + "");
	}
	
	/**
	 * <br>功能简述:显示使用界面
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 */
//	public static void showTrailDialog(Context context, String entrance, int page) {
//		Intent intent = new Intent();
//		//其他默认为无广告入口
//		intent.putExtra(DeskSettingAdvancedPayActivity.CURRENT_INDEX, page);
//		intent.putExtra(DeskSettingAdvancedPayActivity.ENTRANCE_ID, entrance);
//		intent.putExtra(DeskSettingAdvancedPayActivity.EXTRA_SHOW_TRAIL, true);
//		intent.setClass(context, DeskSettingAdvancedPayActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		context.startActivity(intent);
//		GuiThemeStatistics.functionPurchaseStaticData("-1", "h000", 1, entrance, FunctionPurchaseManager.TAB_FUNCTION_INTRO,
//				FunctionPurchaseManager.getInstance(context).getTrialStatus());
//	}
	/**
	 * 跳去已付费的Activity，而非diaolog
	 * @param context
	 */
	private static void showHasPaidDialog(Context context) {
		Intent intent = new Intent();
		intent.putExtra(DeskSettingAdvancedPayActivity.CURRENT_INDEX, DeskSettingAdvancedPayActivity.PAY_NO_ADS);
		intent.putExtra(DeskSettingAdvancedPayActivity.HAS_UPGRADED, true);
		intent.setClass(context, DeskSettingAdvancedPayActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	/**
	 * <br>功能简述:检查是否选择了去广告功能
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static boolean isNoAdvert() {
		//判断是否收费用户，是否选择去广告功能
		ThemeSettingInfo settingInfo = SettingProxy.getThemeSettingInfo();
		if (settingInfo != null) {
			if (settingInfo.mNoAdvert) {
				return true;
			}
		}
			
		return false;
	}
	
	public static boolean checkHadPay(Context context) {
		if (!checkKeyIsExit(context)) {
//			setNoAdvert(false);
			return false;
		}
		String ndkretString = KEYSIGNER_STRING;
		try {
			ndkretString = getByteString(NdkUtil.detectGLES20(context,
					KEYSIGNER_STRING, Secure.getString(
							context.getContentResolver(), Secure.ANDROID_ID)));
		} catch (Throwable e) {
			ndkretString =  getByteString(Secure.getString(
							context.getContentResolver(), Secure.ANDROID_ID));
			if (ConstValue.DEBUG) {
				Log.e("android had respace ndkstring", ndkretString);
			}
		}

		Context c = null;
		String name = "null";
		try {
			c = context.createPackageContext(KEY_PACKAGE_NAME_STRING,
					Context.CONTEXT_IGNORE_SECURITY);
			SharedPreferences sharedPreferences = c.getSharedPreferences(
					"launcher_key", Context.MODE_WORLD_READABLE
							+ Context.MODE_MULTI_PROCESS);
			name = sharedPreferences.getString("key_string", "null");

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		// 如果GO桌面主包认证不成功，通知KEY包把sharepreference删除；
		if (!name.equals(ndkretString)) {
			sendBroadCastClearSharedP(context);
		}
		// 如果读取不到sharedPreferences，则尝试读取SD卡
		if (ConstValue.DEBUG) {
			Log.e(name, ndkretString);
		}
		if (name.equals("null")) {
			name = readSDFile(
					Environment.getExternalStorageDirectory().getAbsolutePath()
							.concat("/GolauncherKey").concat("/key.key"))
					.trim();
		}
		if (ConstValue.DEBUG) {
			Log.e(name, ndkretString);
		}

		return name.equals(ndkretString);
	}

	/**
	 * <br>功能简述:由于有些用户下载了5.99的key包后之后卸载导致无入口引导到电子市场，
	 * <br>		  增加此接口使用sd卡残留的验证文件来作为判断用户是否下载过5.99版key的依据,如果用户连sd卡数据也删除了，就只能在电子市场里找了
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static boolean checkPrimeSDFile(Context context) {

		String name = readSDFile(
				Environment.getExternalStorageDirectory().getAbsolutePath()
						.concat("/GolauncherKey").concat("/key.key")).trim();
		return name != null && !name.trim().equals("");

	}
	private static boolean checkKeyIsExit(Context context) {
		return isAvilible(context, KEY_PACKAGE_NAME_STRING);
	}

	private static boolean isAvilible(Context context, String packageName) {
		 return GoAppUtils.isAppExist(context, packageName);
	}

	/**
	 * 发送一个清除KEY包 sharedPerfernce数据的广播
	 * 
	 * @param context
	 */
	private static void sendBroadCastClearSharedP(Context context) {
		Intent intentBr = new Intent(KEY_PACKAGE_RECHECK_BROADCAST);
		intentBr.putExtra("flagsfromGL", CLEAN_SP);
		context.sendBroadcast(intentBr, null);
		if (ConstValue.DEBUG) {
			Log.e("", "set clear SP broadcast");
		}
	}
	
	/**
	 * 发送一个隐藏KEY包的广播
	 * @param context
	 */
	private static void sendBroadCastDisableAPP(Context context) {
		Intent intentBr = new Intent(KEY_PACKAGE_RECHECK_BROADCAST);
		intentBr.putExtra("flagsfromGL", DISABLE_APP);
		intentBr.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		context.sendBroadcast(intentBr, null);
		if (ConstValue.DEBUG) {
			Log.e("", "set disable app broadcast");
		}
	}
	
	
	/**
	 * 发送一个重新检测licensing的广播
	 * @param context
	 */
	public static void sendBroadCastRecheck(Context context) {
		if (isAvilible(context, KEY_PACKAGE_NAME_STRING)) {
			Intent intentBr = new Intent(KEY_PACKAGE_RECHECK_BROADCAST);
			intentBr.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			intentBr.putExtra("flagsfromGL", RECHECK);
			context.sendBroadcast(intentBr, null);
			if (ConstValue.DEBUG) {
				Log.e("", "set recheck broadcast");
			}
		}
	}

	/**
	 * 启动key包介绍页面去验证，因为4.3以上系统没有启动过的app是收不到broadcast的
	 * @param context
	 */
	public static void startKeyGuideActivity(Context context) {
		if (isAvilible(context, KEY_PACKAGE_NAME_STRING)) {
			try {
				ComponentName cn = new ComponentName(KEY_PACKAGE_NAME_STRING,
						KEY_CLASS_NAME_STRING);
				Intent intentBr = new Intent();
				intentBr.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intentBr.setComponent(cn);
				context.startActivity(intentBr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (ConstValue.DEBUG) {
				Log.e("", "set recheck broadcast");
			}
		}
	}

	public static String readSDFile(String fileName) {

		StringBuffer sb = new StringBuffer();
		File file = new File(fileName);
		try {
			FileInputStream fis = new FileInputStream(file);
			int c;
			while ((c = fis.read()) != -1) {
				sb.append((char) c);
			}
			fis.close();

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		return sb.toString();

	}

	private static String getByteString(String input) {
		StringBuilder sb = new StringBuilder();
		byte[] bytes = input.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(bytes[i]);
		}
		return sb.toString();
	}

	public static void registeKeyReceiver() {
		final Application app = ApplicationProxy.getApplication();
		sKeyReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context ctx, Intent intent) {
				if (intent != null) {
					int status = intent.getIntExtra(CHECK_STATUS, 0);
					// 只有受到成功的广播才做处理
					if (status == 101) {
						FunctionPurchaseManager purchaseManager = FunctionPurchaseManager.getInstance(ApplicationProxy.getContext());
						boolean hasPay = checkHadPay(app);
						if (hasPay) {
							if (!purchaseManager.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_FULL)) {
								purchaseManager.savePurchasedItem(FunctionPurchaseManager.PURCHASE_ITEM_FULL);
							}
							if (sGomarketEntrance == null) {
								PreferencesManager manager = new PreferencesManager(
										ApplicationProxy.getContext());
								sGomarketEntrance = manager.getString(
										IPreferencesIds.GO_MARKET_PAY_ENTRANCE, null);
							}
                            if (isFirstUpLoadStatist(
                                    ctx,
                                    LauncherEnv.Path.STATIST_FILE_NAME,
                                    IPreferencesIds.PREFERENCES_PAY_STATICST_FIRST_UPLOAD)) {
                                GuiThemeStatistics.payStaticData("j000", 1,
                                        sGomarketEntrance);
                            }
							gotoHadPayView(app);
							sendHadPayBoradCast(app);
							show3DSetting(app);
							MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
									ICommonMsgId.RESTART_GOLAUNCHER, -1, null, null);
//							purchaseManager.setPayFunctionState(app); //更新付费功能状态
						}
						//隐藏key包
						//sendBroadCastDisableAPP(context);   //change at 2013.6.7 改成key包自己隐藏自己
						if (ConstValue.DEBUG) {
							Log.e("", purchaseManager.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_FULL) + "aaa-----------");
						}
					}

				}

			}

		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_KEY_NOTIFLY_GO_LAUNCHER);
		filter.setPriority(Integer.MAX_VALUE);
		app.registerReceiver(sKeyReceiver, filter);
	}
	
	public static void unregisteKeyReceiver() {
		if (sKeyReceiver != null) {
			Application app = ApplicationProxy.getApplication();
			try {
				app.unregisterReceiver(sKeyReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean isFirstUpLoadStatist(Context context, String fileName, String preferenceId) {
	    boolean result = false;
	    PreferencesManager ps = new PreferencesManager(context);
	    boolean isFirst = ps.getBoolean(preferenceId, true);
	    boolean isCacheExist = FileUtil.isFileExist(LauncherEnv.Path.STATIST_PATH + fileName);
	    if (!isFirst || isCacheExist) {
	        //不是第一次上传
	        if (isFirst) {
	            ps.putBoolean(preferenceId, false);
	            ps.commit();
	        }
	        
	        if (!isCacheExist) {
	            saveFileToSD(LauncherEnv.Path.STATIST_PATH + fileName, false);
	        }
	        
	        result = false;
	        return result;
	    }
	    
	    ps.putBoolean(preferenceId, false);
        ps.commit();
        
        saveFileToSD(LauncherEnv.Path.STATIST_PATH + fileName, false);
        result = true;
	    return result;
	}
	
	private static void saveFileToSD(final String path, boolean isReplace) {
	    new Thread(new Runnable() {
            
            @Override
            public void run() {
                FileUtil.createFile(path, false);
            }
        }).start();
	}
	
	/**
	 * 付费成功后显示3D设置项
	 */
	private static void show3DSetting(Context context) {
		if (FunctionPurchaseManager.getInstance(context.getApplicationContext()).isItemCanUse(
				FunctionPurchaseManager.PURCHASE_ITEM_FULL)) {
			PreferencesManager sharedPreferences = new PreferencesManager(context,
					IPreferencesIds.PREFERENCE_ENGINE, Context.MODE_PRIVATE);
			sharedPreferences.putBoolean(IPreferencesIds.PREFERENCE_ENGINE_SHOW_SETTING, true);
			sharedPreferences.commit();
		}
	}
	
	/**
	 * 设置是否显示广告的广播
	 * @param flag
	 */
	public static void setNoAdvert(boolean flag) {
		ThemeSettingInfo themeSettingInfo = SettingProxy.getThemeSettingInfo();
		if (themeSettingInfo.mNoAdvert != flag) {
			themeSettingInfo.mNoAdvert = flag;
			SettingProxy.updateThemeSettingInfo2(
					themeSettingInfo, flag);
		}
	}

/**
	 * <br>功能简述:去广告是否第一次打开
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static boolean isFirstShowNoAdvertTip(Context context, boolean setIsOpen) {
		PreferencesManager ps = new PreferencesManager(context);
		boolean isFirstShow = ps.getBoolean(IPreferencesIds.PREFERENCES_DESK_SETTING_NO_ADVERT_FIRST_OPEN, true);
		if (isFirstShow) {
			if (setIsOpen) {
				ps.putBoolean(IPreferencesIds.PREFERENCES_DESK_SETTING_NO_ADVERT_FIRST_OPEN, false);
				ps.commit();
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * <br>功能简述:四种手势设置是否第一次打开，为方便查找.删除，放在和上面的函数一起
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static boolean isFirstShowFourGestureTip(Context context, boolean setIsOpen) {
		PreferencesManager ps = new PreferencesManager(context);
		boolean isFirstShow = ps.getBoolean(IPreferencesIds.PREFERENCES_DESK_SETTING_FOUR_GESTURE_FIRST_OPEN, true);
		if (isFirstShow) {
			if (setIsOpen) {
				ps.putBoolean(IPreferencesIds.PREFERENCES_DESK_SETTING_FOUR_GESTURE_FIRST_OPEN, false);
				ps.commit();
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * <br>功能简述:已激活收费功能界面是否第一次打开
	 * <br>功能详细描述:
	 * <br>注意:
	 * 6.20 修改，将SharedPreferences文件名改为prime_key_use ，让恢复默认时不被删掉，不再出现收费功能介绍页
	 * @return
	 */
	public static boolean isFirstShowHadPayTip(Context context, boolean setIsOpen) {
		int mode = Context.MODE_PRIVATE;
		SharedPreferences sp = context.getSharedPreferences("prime_key_use", mode);
		boolean isFirstShow = sp.getBoolean(
				IPreferencesIds.PREFERENCES_SETTING_HADPAY_VIEW_OPEN, true);
		if (isFirstShow) {
			if (setIsOpen) {
				sp.edit().putBoolean(IPreferencesIds.PREFERENCES_SETTING_HADPAY_VIEW_OPEN,
						false).commit();
						
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * <br>功能简述:已激活点击上去广告
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static boolean isFirstShowHadSetNoAdVertTip(Context context, boolean setIsOpen) {
		PreferencesManager ps = new PreferencesManager(context);
		boolean isFirstShow = ps.getBoolean(IPreferencesIds.PREFERENCES_SETTING_HADPAY_NO_AD_OPEN, true);
		if (isFirstShow) {
			if (setIsOpen) {
				ps.putBoolean(IPreferencesIds.PREFERENCES_SETTING_HADPAY_NO_AD_OPEN, false);
				ps.commit();
			}
			return true;
		} else {
			return false;
		}
	}
	
	public static void setGomarketPayEntrance(String entrance) {
		sGomarketEntrance = entrance;
		PreferencesManager manager = new PreferencesManager(ApplicationProxy.getContext());
		manager.putString(IPreferencesIds.GO_MARKET_PAY_ENTRANCE, sGomarketEntrance);
		manager.commit();
	}
	
	/**
	 * <br>功能简述:安全锁设置在添加界面是否第一次打开
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static boolean isFirstShowLockTip(Context context, boolean setIsOpen) {
		PreferencesManager ps = new PreferencesManager(context);
		boolean isFirstShow = ps.getBoolean(IPreferencesIds.PREFERENCES_LOCK_FIRST_OPEN, true);
		if (isFirstShow) {
			if (setIsOpen) {
				ps.putBoolean(IPreferencesIds.PREFERENCES_LOCK_FIRST_OPEN, false);
				ps.commit();
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * <br>功能简述:侧边栏设置在添加界面是否第一次打开
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static boolean isFirstShowSideDock(Context context, boolean setIsOpen) {
		PreferencesManager ps = new PreferencesManager(context);
		boolean isFirstShow = ps.getBoolean(IPreferencesIds.PREFERENCES_SIDE_DOCK_FIRST_SHOW, true);
		if (isFirstShow) {
			if (setIsOpen) {
				ps.putBoolean(IPreferencesIds.PREFERENCES_SIDE_DOCK_FIRST_SHOW, false);
				ps.commit();
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * <br>功能简述:侧边栏设置在添加界面是否第一次打开
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static boolean isFirstShowSideDockTip(Context context, boolean setIsOpen) {
		PreferencesManager ps = new PreferencesManager(context);
		boolean isFirstShow = ps.getBoolean(IPreferencesIds.PREFERENCES_SIDE_DOCK_FIRST_OPEN, true);
		if (isFirstShow) {
			if (setIsOpen) {
				ps.putBoolean(IPreferencesIds.PREFERENCES_SIDE_DOCK_FIRST_OPEN, false);
				ps.commit();
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * <br>功能简述:侧边栏设置某选项在添加界面是否第一次打开  某产品的设定真2，明明都依附与第一个选项还要设置其他的选项为new
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static boolean isFirstShowSideDockPositionTip(Context context, boolean setIsOpen) {
		PreferencesManager ps = new PreferencesManager(context);
		boolean isFirstShow = ps.getBoolean(IPreferencesIds.PREFERENCES_SIDE_DOCK_POSITION_FIRST_OPEN, true);
		if (isFirstShow) {
			if (setIsOpen) {
				ps.putBoolean(IPreferencesIds.PREFERENCES_SIDE_DOCK_POSITION_FIRST_OPEN, false);
				ps.commit();
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * <br>功能简述:侧边栏设置某选项在添加界面是否第一次打开  某产品的设定真2，明明都依附与第一个选项还要设置其他的选项为new
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static boolean isFirstShowSideDockAreaTip(Context context, boolean setIsOpen) {
		PreferencesManager ps = new PreferencesManager(context);
		boolean isFirstShow = ps.getBoolean(IPreferencesIds.PREFERENCES_SIDE_DOCK_AREA_FIRST_OPEN, true);
		if (isFirstShow) {
			if (setIsOpen) {
				ps.putBoolean(IPreferencesIds.PREFERENCES_SIDE_DOCK_AREA_FIRST_OPEN, false);
				ps.commit();
			}
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * 显示已付费的diaolog
	 * @param context
	 */
	public static void showHadPayDialog(Context context) {
		DesksettingNoAdvertTipDialog dialog = new DesksettingNoAdvertTipDialog(
				context);
		dialog.show();
		dialog.setNegativeButton(null, new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});

	}

	/**
	 * 显示已付费的解锁界面。这个界面只会出现一次
	 * @param context
	 */
	public static void gotoHadPayView(Context context) {
		if (FunctionPurchaseManager.getInstance(context.getApplicationContext()).queryItemPurchaseState(
				FunctionPurchaseManager.PURCHASE_ITEM_FULL)
				&& isFirstShowHadPayTip(context, true)) {
			showHasPaidDialog(context);
			setNoAdvert(true);
		}
	}
	
	public static void onlyFirstTimeSetNoAdvert(Context context) {
		if (isFirstShowHadSetNoAdVertTip(context, true)) {
			setNoAdvert(true);
		}
	}
	
	/**
	 * 发送一个给已经解锁收费的广播，给当前的VIEW刷新自己的界面
	 */
	protected static void sendHadPayBoradCast(Context context) {
		Intent intent = new Intent(ACTION_HAD_PAY_REFRESH);
		context.sendBroadcast(intent, null);
	}
	
	/**
	 * <br>功能简述:去显示广告付费对话框是否
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	public static boolean isFirstShowNoAdvertPayDialog(Context context) {
		PreferencesManager ps = new PreferencesManager(context);
		boolean isShow = ps.getBoolean(IPreferencesIds.PREFERENCES_NO_ADVERT_PAY_DIALOG_FIRST_OPEN, true);
		return isShow;
	}
	
	/**
	 * <br>功能简述:设置显示广告付费对话框
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	public static void setNoAdvertPayDialogPreference(Context context) {
		PreferencesManager ps = new PreferencesManager(context);
		ps.putBoolean(IPreferencesIds.PREFERENCES_NO_ADVERT_PAY_DIALOG_FIRST_OPEN, false);
		ps.commit();
	}
	
	public static final int CONDITION_NOT_PASS = 0;
	public static final int CONDITION_PASS_NEED_SHOW = 1;
	public static final int CONDITION_PASS_HAD_SHOW = 2;
	
	/**
	 * <br>功能简述:延续读写sp系列方法，如果要进行重构的同学，祝你好运
	 * <br>功能详细描述:付费引导窗口弹出次数持久化
	 * <br>注意:
	 * @param context
	 */
	public static int getGesturePrimeDialogPreference(Context context, int entranceId) {
		PreferencesManager ps = new PreferencesManager(context);
		int type = ps.getInt(String.valueOf(entranceId), CONDITION_NOT_PASS);
		boolean isShowed = ps.getBoolean(IPreferencesIds.SHOW_GESTURE_PRIME_DIALOG, false);
		if (isShowed) {
			type = CONDITION_PASS_HAD_SHOW;
		}
		return type;
	}
	
	public static void setGesturePrimeDialogPreference(Context context, int entranceId, int type) {
		PreferencesManager ps = new PreferencesManager(context);
		ps.putInt(String.valueOf(entranceId), type);
		if (type == CONDITION_PASS_HAD_SHOW) {
			ps.putBoolean(IPreferencesIds.SHOW_GESTURE_PRIME_DIALOG, true);
		}
		ps.commit();
	}
	
	private static void showGesturePrimeDialog(final Context context, final int entranceId) {
		DialogConfirm dialog = new DialogConfirm(context);
		dialog.show();
		dialog.setTitle(R.string.gesture_update_title);
		dialog.setMessage(R.string.gesture_update_cents);
		dialog.setNegativeButton(R.string.cancel, null);
		dialog.setPositiveButton(R.string.gesture_update_download, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPayDialog(context, entranceId);
			}
		});
	}
	
	public static boolean showGuidePrimeDialog(Context context, int entranceId) {
		if (getGesturePrimeDialogPreference(context, entranceId) == CONDITION_PASS_NEED_SHOW) {
			setGesturePrimeDialogPreference(context, entranceId, CONDITION_PASS_HAD_SHOW);
			showGesturePrimeDialog(context, entranceId);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * <br>功能简述:判断设置是否开启0屏
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static boolean getIsShowZeroScreen() {
		return false;
	}
	
	public static Bitmap filteDrwableColor(Resources res, Bitmap bitmap) {
		try {
			BitmapDrawable drawable = new BitmapDrawable(res, bitmap);
			//变灰
			drawable.mutate();
			ColorMatrix cm = new ColorMatrix();
			cm.setSaturation(0);
			ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);
			drawable.setColorFilter(cf);
			Bitmap b = drawableToBitmap(drawable);
			return b;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = (int) drawable.getIntrinsicWidth();
		int height = (int) drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}
	
	/**
	 * 重新打开New标识
	 * @param context
	 */
	public static void reOpenNewFlag(Context context) {

		PreferencesManager ps = new PreferencesManager(context);
		FunctionPurchaseManager purchaseManager = FunctionPurchaseManager.getInstance(context);
		
		if (!purchaseManager.queryItemPurchaseState(FunctionPurchaseManager.PURCHASE_ITEM_AD)) {
			// 桌面设置-高级设置(new)-关闭广告推送(new)
			ps.putBoolean(IPreferencesIds.PREFERENCES_DESK_SETTING_NO_ADVERT_FIRST_OPEN, true);
		}

		if (!purchaseManager.queryItemPurchaseState(FunctionPurchaseManager.PURCHASE_ITEM_SECURITY)) {
			//桌面设置-高级设置(new)-安全锁(new)
			ps.putBoolean(IPreferencesIds.PREFERENCES_LOCK_FIRST_OPEN, true);
		}

		if (!purchaseManager.queryItemPurchaseState(FunctionPurchaseManager.PURCHASE_ITEM_QUICK_ACTIONS)) {
			//桌面设置-界面设置(new)-侧边快捷栏(new)
			ps.putBoolean(IPreferencesIds.PREFERENCES_SIDE_DOCK_FIRST_SHOW, true);
			//桌面设置-行为设置(new)-桌面手势设置(new)
			ps.putBoolean(IPreferencesIds.PREFERENCES_DESK_SETTING_FOUR_GESTURE_FIRST_OPEN, true);
		}

		ps.commit();

		if (!purchaseManager.queryItemPurchaseState(FunctionPurchaseManager.PURCHASE_ITEM_EFFECT)) {
			//桌面设置-行为设置(new)-屏幕切换特效(new)
			EffectorControler.getInstance().updateEffectorNewState(
					EffectorControler.TYPE_SCREEN_SETTING, true);
			//桌面设置-行为设置(new)-功能表-横向滚屏特效(new)
			EffectorControler.getInstance().updateEffectorNewState(
					EffectorControler.TYPE_APP_DRAWER_SETTING, true);
		}
	}
	
	/**
     * <br>功能简述:弹出toast
     * <br>功能详细描述:
     * <br>注意:
     */
    public static void showNeedPayThemeToast() {
        Toast.makeText(
                ApplicationProxy.getContext(),
                ApplicationProxy.getContext().getResources()
                        .getString(R.string.tip_can_not_use_theme),
                Toast.LENGTH_SHORT).show();
    }
    
    /**
     * <br>功能简述:判断该壁纸，背景，dock背景是否为付费的主题图片
     * <br>功能详细描述:
     * <br>注意:
     * @param imageItem
     */
    public static boolean isCanUseTheme(WallpaperSubInfo imageItem) {
        //对于设置界面中使用需要付费的主题，则弹出付费对话框。
        String value = imageItem.getPackageName();
        boolean isCanUseTheme = ThemeManager.getInstance(
                ApplicationProxy.getContext()).getThemeResrouceBeanisPay(value);

        return isCanUseTheme;
    }
	
	/**
	 * <br>功能简述:未付费 或 付费了但没有勾选开关。这个时候可以推广告
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	public static boolean isPrimeAd(Context context) {
        FunctionPurchaseManager purchaseManager = FunctionPurchaseManager.getInstance(context
                .getApplicationContext());
        boolean hasPayAd = purchaseManager
                .isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_AD);
        if (!hasPayAd || hasPayAd && !DeskSettingUtils.isNoAdvert()) {
            return true;
        }
        return false;
    }
	
	/**
	 * 壁纸滤镜第一次打开
	 */
	public static boolean isFirstShowWallpaperFilter(Context context, boolean setIsOpen) {
		PreferencesManager ps = new PreferencesManager(context);
		boolean isFirstShow = ps.getBoolean(IPreferencesIds.PREFERENCES_WALLPAPER_FILTER_FIRST_SHOW, true);
		if (isFirstShow) {
			if (setIsOpen) {
				ps.putBoolean(IPreferencesIds.PREFERENCES_WALLPAPER_FILTER_FIRST_SHOW, false);
				ps.commit();
			}
			return true;
		} else {
			return false;
		}
	}
}
