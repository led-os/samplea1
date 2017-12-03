package com.jiubang.ggheart.apps.desks.Preferences;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gau.go.launcherex.R;
import com.go.proxy.SettingProxy;
import com.go.util.AppUtils;
import com.go.util.ClL;
import com.go.util.androidsys.ClearDefaultIntent;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingNewMarkManager.NewMarkKeys;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogFactory;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogLanguageChoice;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogTypeId;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemCheckBoxView;
import com.jiubang.ggheart.apps.desks.diy.IRequestCodeIds;
import com.jiubang.ggheart.apps.desks.diy.WallpaperDensityUtil;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.common.controler.InvokeLockControler;
import com.jiubang.ggheart.common.password.PasswordActivity.ActionResultCallBack;
import com.jiubang.ggheart.data.info.ScreenSettingInfo;
import com.jiubang.ggheart.data.info.ThemeSettingInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 
 * @author zouguiquan
 *
 */
public class DeskSettingCommonActivity extends DeskSettingBaseActivity {

	private DeskSettingItemCheckBoxView mSetDefaultHome;

	private DeskSettingItemCheckBoxView mSettingScreenChangeLoop;

	private DeskSettingItemCheckBoxView mWallpaperScroable;

	private DeskSettingItemCheckBoxView mSettingNoAdvert;

	private DeskSettingItemBaseView mSettingLock;

	private DeskSettingItemBaseView mSettingLanguage;

	private BroadcastReceiver mRefreshReceiver;

	private ScreenSettingInfo mScreenInfo;
	private ThemeSettingInfo mThemeSettingInfo;

	private int mEntranceid = 7;
	private long mLastClickTime;

	private DialogLanguageChoice mLanguageDialog;

	private long mLastLauncherClickTime;

	private static final long CLICK_TIME = 400L;

	private static final long LAUNCHER_CLICK_INTERVAL = 1200;              

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.desk_setting_common);
		
		if (getIntent() != null) {			
			mEntranceid = getIntent().getIntExtra("entrance_id", 7);
		}

		mSetDefaultHome = (DeskSettingItemCheckBoxView) findViewById(R.id.default_launcher);
		mSetDefaultHome.setOnClickListener(this);
		mSetDefaultHome.getCheckBox().setOnClickListener(this);

		mSettingScreenChangeLoop = (DeskSettingItemCheckBoxView) findViewById(R.id.screen_looping);
		mSettingScreenChangeLoop.setOnValueChangeListener(this);

		mWallpaperScroable = (DeskSettingItemCheckBoxView) findViewById(R.id.wallpaper_scrollable);
		mWallpaperScroable.setOnValueChangeListener(this);

		mSettingNoAdvert = (DeskSettingItemCheckBoxView) findViewById(R.id.no_advert);
		mSettingNoAdvert.setOnValueChangeListener(this);
		
		mSettingLanguage = (DeskSettingItemBaseView) findViewById(R.id.language_setting);
		mSettingLanguage.setOnClickListener(this);
		mSettingLanguage.setSummaryImage(R.drawable.desk_setting_language_summary);
		
		mSettingLock = (DeskSettingItemBaseView) findViewById(R.id.lock_setting);
		mSettingLock.setOnClickListener(this);

		load();
		registeRefreshReceiver(this);
		GuiThemeStatistics
				.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.PREFERENCES_CS);
	}

	@Override
	public void load() {
		super.load();
		
		if (isGoLauncherDefault()) {
			mSetDefaultHome.setIsCheck(true);
		} else {
			mSetDefaultHome.setIsCheck(false);
		}
		
		mScreenInfo = SettingProxy.getScreenSettingInfo();
		mThemeSettingInfo = SettingProxy.getThemeSettingInfo();
		
		if (null != mScreenInfo) {
			mSettingScreenChangeLoop.setIsCheck(mScreenInfo.mScreenLooping);
			
			boolean scrollable = mScreenInfo.mWallpaperScroll;
			mWallpaperScroable.setIsCheck(scrollable);
			int summaryId = scrollable ? R.string.normal_wallpaper
					: R.string.haploid_wallpaper;
			mWallpaperScroable.setSummaryText(summaryId);
		}
		
		
		if (mThemeSettingInfo != null) {
			if (FunctionPurchaseManager.getInstance(getApplicationContext()).isItemCanUse(
					FunctionPurchaseManager.PURCHASE_ITEM_AD)) {
				mSettingNoAdvert.setIsCheck(mThemeSettingInfo.mNoAdvert);
			}
		}

		//非200渠道屏蔽
		if (!Statistics.is200ChannelUid(this)) {
			mSettingNoAdvert.setVisibility(View.GONE);
		} else {
			mSettingNoAdvert.setVisibility(View.VISIBLE);
		}

		// cn包需要去掉去广告功能
		if (!Statistics.is200ChannelUid(this)) {
			mSettingNoAdvert.setVisibility(View.GONE);
		}
		if (FunctionPurchaseManager.getInstance(getApplicationContext()).getPayFunctionState(
				FunctionPurchaseManager.PURCHASE_ITEM_AD) == FunctionPurchaseManager.STATE_GONE) {
			mSettingNoAdvert.setVisibility(View.GONE);
			mSettingLock.setVisibility(View.GONE);
		}
		if (FunctionPurchaseManager.getInstance(getApplicationContext()).getPayFunctionState(
				FunctionPurchaseManager.PURCHASE_ITEM_SECURITY) == FunctionPurchaseManager.STATE_CAN_USE) {
			mSettingLock.setImagePrimeVisibile(View.GONE);
		} else {
			mSettingLock.setImagePrimeVisibile(View.VISIBLE);
		}
		//判断是否付费
		if (FunctionPurchaseManager.getInstance(getApplicationContext()).getPayFunctionState(
				FunctionPurchaseManager.PURCHASE_ITEM_AD) == FunctionPurchaseManager.STATE_CAN_USE) {
			mSettingNoAdvert.setImagePrimeVisibile(View.GONE);
		} else {
			mSettingNoAdvert.setImagePrimeVisibile(View.VISIBLE);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case IRequestCodeIds.REQUEST_SET_DEFAULT_HOME : {
				clDH();
			}
				break;
			case IRequestCodeIds.REQUEST_CODE_COOLPAD_CLEARDEFAULT : {
				if (AppUtils.getDefaultLauncher(this) == null) {
					mSetDefaultHome.performClick();
				}
			}
			default :
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void registeRefreshReceiver(final Context context) {
		mRefreshReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context2, Intent intent) {
				if (intent != null) {
					FunctionPurchaseManager purchaseManager = FunctionPurchaseManager
							.getInstance(getApplicationContext());
					boolean hasPay = purchaseManager
							.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_AD);
					if (hasPay) {
						mSettingNoAdvert.setIsCheck(true);
						mSettingNoAdvert.setImagePrimeVisibile(View.GONE);
					}
					 hasPay = purchaseManager
								.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_SECURITY);
					 if (hasPay) {
						 mSettingLock.setImagePrimeVisibile(View.GONE);
					 }
				}

			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(DeskSettingUtils.ACTION_HAD_PAY_REFRESH);
		filter.setPriority(Integer.MAX_VALUE);
		context.registerReceiver(mRefreshReceiver, filter);
	}
	
	private void unRegisterRefreshReceiver(Context context) {
		try {
			context.unregisterReceiver(mRefreshReceiver);
		} catch (Exception e) {
			Log.e(e.toString(), e.getMessage());
		}
	}
	
	private boolean clDH() {
		try {
			if ("com.yulong.android.launcher3".equals(AppUtils.getDefaultLauncher(this))
					&& Build.BRAND.toLowerCase().contains("coolpad")) {
				//coolpad手机，并设内置桌面为默认桌面，清除不了默认，只能跳到系统清除默认界面，让用户去手动清除
				Intent localIntent = new Intent();
				String pkg = "com.yulong.android.launcher3";
				localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
				localIntent.setData(Uri.fromParts("package", pkg, null));
				startActivityForResult(localIntent, IRequestCodeIds.REQUEST_CODE_COOLPAD_CLEARDEFAULT);
			} else if ("com.miui.home".equals(AppUtils.getDefaultLauncher(this))
					&& Build.BRAND.toLowerCase().contains("xiaomi")) {
				//小米手机，并设内置桌面为默认桌面，清除不了默认，只能跳到系统清除默认界面
				Intent localIntent = new Intent();
				String pkg = "com.miui.home";
				localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
				localIntent.setData(Uri.fromParts("package", pkg, null));
				startActivityForResult(localIntent, IRequestCodeIds.REQUEST_CODE_COOLPAD_CLEARDEFAULT);
			} else {
				PackageManager localPackageManager = this.getPackageManager();

				ComponentName component = new ComponentName(this, ClL.class);
				localPackageManager.setComponentEnabledSetting(component, 1, 1);

				Intent intent = new Intent(ICustomAction.ACTION_MAIN);
				intent.addCategory("android.intent.category.HOME");
				this.startActivity(intent);

				localPackageManager.setComponentEnabledSetting(component, 0, 1);

				return true;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		long curTime = System.currentTimeMillis();
		if (curTime - mLastClickTime < CLICK_TIME) {
			return;
		}
		mLastClickTime = curTime;

		switch (v.getId()) {

			case R.id.default_launcher :
				//由于桌面选择框启动时间较长,加了点击间隔限制
				if (curTime - mLastLauncherClickTime < LAUNCHER_CLICK_INTERVAL) {
					return;
				}
				mLastLauncherClickTime = curTime;
				clickSetDefaultHome();
				break;
				
			// 语言设置
			case R.id.language_setting :
				showInstallLanguageTip(this);
				break;

			case R.id.lock_setting :

				mSettingLock.setImageNewVisibile(View.GONE);

				//判断是否付费
//				if (FunctionPurchaseManager.getInstance(getApplicationContext())
//						.getPayFunctionState(FunctionPurchaseManager.PURCHASE_ITEM_SECURITY) == FunctionPurchaseManager.STATE_CAN_USE) {
					//TODO:付费
					InvokeLockControler controler = InvokeLockControler
							.getInstance(getApplicationContext());
					controler.startLockAction(
							-1,
							new ActionResultCallBack() {

								@Override
								public void onUnlockSuccess(int actionId) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(getApplicationContext(),
											DeskSettingLockActivity.class);
									intent.putExtra(DeskSettingAdvancedPayActivity.ENTRANCE_ID, mEntranceid);
									startActivityForResult(intent, IRequestCodeIds.REQUEST_CODE_LOCK);
								}

								@Override
								public void onUnlockFail(int actionId) {
									// TODO Auto-generated method stub

								}
							}, DeskSettingCommonActivity.this, R.drawable.safe_lock_icon,
							DeskSettingCommonActivity.this.getString(R.string.desksetting_security_lock));

//				} else {
//					DeskSettingUtils.showPayDialog(this, 501); //显示付费对话框
//				}

				break;

			default :
				if (v == mSetDefaultHome.getCheckBox()) {
					boolean checked = mSetDefaultHome.getCheckBox().isChecked();
					mSetDefaultHome.getCheckBox().setChecked(!checked);
					clickSetDefaultHome();
				}
				break;
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mLanguageDialog != null && mLanguageDialog.isShowing()) {
			showInstallLanguageTip(this);
		}
	}
	
	@Override
	public boolean onValueChange(DeskSettingItemBaseView view, Object value) {
		if (view == mWallpaperScroable) {
			mWallpaperScroable.setIsCheck((Boolean) value);
			int summaryId = ((Boolean) value) ? R.string.normal_wallpaper
					: R.string.haploid_wallpaper;
			mWallpaperScroable.setSummaryText(summaryId);
			if (null != mScreenInfo) {
				if (mScreenInfo.mWallpaperScroll != mWallpaperScroable.getIsCheck()) {
					mScreenInfo.mWallpaperScroll = mWallpaperScroable.getIsCheck();
					SettingProxy.updateScreenSettingInfo(mScreenInfo);
					WallpaperDensityUtil.setWallpaperDimension(this);
				}
			}
		} else if (view == mSettingScreenChangeLoop) {
			if (null != mScreenInfo) {
				if (mScreenInfo.mScreenLooping != mSettingScreenChangeLoop.getIsCheck()) {
					mScreenInfo.mScreenLooping = mSettingScreenChangeLoop.getIsCheck();
					SettingProxy.updateScreenSettingInfo(mScreenInfo); //保存
				}
			}
		}
		
		// 去广告
		if (view == mSettingNoAdvert) {
			//判断是否付费 // 为什么去广告这个功能可以不用判断韩国版和CN包？？？
			FunctionPurchaseManager purchaseManager = FunctionPurchaseManager
					.getInstance(getApplicationContext());
			boolean hasPay = purchaseManager.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_AD);
			if (!hasPay) {
				mSettingNoAdvert.setIsCheck(false);
				if (GoAppUtils.isAppExist(this, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
					FunctionPurchaseManager.getInstance(getApplicationContext()).startItemPayPage(mEntranceid + "");
				} else {
					DeskSettingUtils.showPayDialog(this, 201); //显示付费对话框
				}
			}
			mSettingNoAdvert.setImageNewVisibile(View.GONE);
		}
		return super.onValueChange(view, value);
	}
	
	@Override
	public void save() {
		super.save();
		if (mThemeSettingInfo != null) {
			boolean isChangeTheme = false;
			boolean broadCast = false;

			if (mThemeSettingInfo.mNoAdvert != mSettingNoAdvert.getIsCheck()) {
				mThemeSettingInfo.mNoAdvert = mSettingNoAdvert.getIsCheck();
				isChangeTheme = true;
				broadCast = true;
			}

			if (isChangeTheme) {
				SettingProxy.updateThemeSettingInfo2(mThemeSettingInfo,
						broadCast);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterRefreshReceiver(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		save();
	}
	
	/**
	 * <br>
	 * 功能简述:显示语言设置对话框 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param context
	 */
	public void showInstallLanguageTip(final Context context) {
		if (mLanguageDialog != null) {
			mLanguageDialog.dismiss();
		}
		mLanguageDialog = new DialogLanguageChoice(this);
		mLanguageDialog.show();
	}
	
	private void clickSetDefaultHome() {
		if (isGoLauncherDefault()) {
			showClearDefaultDialog(false);
		} else {
			clDH();
		}
	}
	
//	/**
//	 * <br>
//	 * 功能简述:判断是否默认使用GO桌面 <br>
//	 * 功能详细描述:点击HOME键弹出的默认使用此应用 <br>
//	 * 注意:
//	 * 
//	 * @return
//	 */
//	private boolean isDefault() {
//		PackageManager pm = this.getPackageManager();
//		boolean isDefault = false;
//		List<ComponentName> prefActList = new ArrayList<ComponentName>();
//		// Intent list cannot be null. so pass empty list
//		List<IntentFilter> intentList = new ArrayList<IntentFilter>();
//		pm.getPreferredActivities(intentList, prefActList, null);
//		if (0 != prefActList.size()) {
//			for (int i = 0; i < prefActList.size(); i++) {
//				if (this.getPackageName().equals(
//						prefActList.get(i).getPackageName())) {
//					isDefault = true;
//					break;
//				}
//			}
//		}
//		return isDefault;
//	}
	
	/**
	 * <br>
	 * 功能简述:判断是否默认使用GO桌面 <br>
	 * 功能详细描述:点击HOME键弹出的默认使用此应用 <br>
	 * 注意:类似这种方法能放到工具类里面吗？
	 * 
	 * @return
	 */
	private boolean isGoLauncherDefault() {
	    final String myPackageName = getPackageName();
	    final String defaultPkg = getDefaultLauncher();
		if (myPackageName.equals(defaultPkg)) {
			return true;
		}
	    return false;
	}
	
	/**
     * 获取默认运行桌面包名（注：存在多个桌面时且未指定默认桌面时，该方法返回Null,使用时需处理这个情况）
     */
    private String getDefaultLauncher() {
        return AppUtils.getDefaultLauncher(this);
        }
	
	/**
	 * <br>
	 * 功能简述:显示清除默认桌面提示对话框 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	synchronized void showClearDefaultDialog(final boolean exit) {
		DialogConfirm mNormalDialog = (DialogConfirm) DialogFactory
				.produceDialog(this, DialogTypeId.TYPE_NORMAL_MESSAGE);
		mNormalDialog.show();
		mNormalDialog.setTitle(getString(R.string.clearDefault_title));
		mNormalDialog.setMessage(getString(R.string.clearDefault));
		mNormalDialog.setPositiveButton(null, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					clearDefault();
					if (exit) {
						sendExit(false);
					} else {
						if (mSetDefaultHome != null) {
							mSetDefaultHome.setIsCheck(false);
						}
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});

		mNormalDialog.setNegativeButton(null, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (exit) {
						sendExit(false);
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * <br>
	 * 功能简述:退出桌面 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param restart
	 *            ture:重启 ，false：退出
	 */
	private void sendExit(boolean restart) {
		if (restart) {
			exitAndRestart();
		} else {
			setResult(DeskSettingConstants.RESULT_CODE_EXIT_GO_LAUNCHER, getIntent());
			save();
			finish();
		}
	}
	
	/**
	 * <br>
	 * 功能简述:清除默认使用GO桌面 <br>
	 * 功能详细描述:点击HOME键弹出的默认使用此应用 <br>
	 * 注意:
	 */
	private void clearDefault() {
		ClearDefaultIntent.clearCurrentPkgDefault(this);
	}

	@Override
	protected void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		//是否已经打开过，第一次打开就显示new标志,去广告
		if (newMarkManager.isShowNew(NewMarkKeys.NO_ADS, true)) {
			mSettingNoAdvert.setImageNewVisibile(View.VISIBLE);
		}

		if (newMarkManager.isShowNew(NewMarkKeys.SECURITY_LOCK, true)) {
			mSettingLock.setImageNewVisibile(View.VISIBLE);
		}
	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
		return false;
	}
}