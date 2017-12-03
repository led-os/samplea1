package com.jiubang.ggheart.components.appmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.go.util.AppUtils;
import com.go.util.market.MarketConstant;
import com.go.util.scroller.ScreenScroller;
import com.go.util.scroller.ScreenScrollerListener;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.gowidget.gostore.views.ScrollerViewGroup;
import com.jiubang.ggheart.components.DeskActivity;
import com.jiubang.ggheart.components.advert.AdvertDialog;
import com.jiubang.ggheart.components.advert.AdvertDialogCenter;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2013-5-8]
 */
public class SimpleAppManagerActivity extends DeskActivity
		implements
			ScreenScrollerListener,
			OnClickListener {

	private ScrollerViewGroup mScrollerViewGroup; // 滚动控件
	private UpdateAppView mUpdateAppView;
	private UninstallAppView mUninstallView;
	private LinearLayout mLineUninstall;
	private LinearLayout mLineCleanScreen;
	private TextView mTextUninstall;
	private TextView mTextUpdateApp;
	private ImageView mAppManagerSettings;
	private ImageView mTitleCleanMaster;
	private View mTitleGroup;
	private int mTitleSelectColor;
	private int mTitleNoSelectColor;
	private int mUnderlineSelectColor;
	private int mUnderlineNoSelectColor;

	private LayoutInflater mInflater;
	
	private final BroadcastReceiver mAppInstallListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (mUninstallView != null) {
				mUninstallView.dispatchInstallEvent(context, intent);
			}
			if (mUpdateAppView != null) {
				mUpdateAppView.dispatchInstallEvent(context, intent);
			}
		}
	};
	
	private BroadcastReceiver mNetworkListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				if (mUpdateAppView != null) {
					mUpdateAppView.dispatchNetworkEvent(context, intent);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clean_screen_main_view);

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		initView();
		initScrollerViewGroup();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerInstallUninstallReceiver();
		registerNetworkReceiver();
		mUpdateAppView.onResume();
	}

	/**
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void initView() {
		mTextUninstall = (TextView) findViewById(R.id.tab_text_uninstall);
		mTextUpdateApp = (TextView) findViewById(R.id.tab_text_update_app);
		mAppManagerSettings = (ImageView) findViewById(R.id.clean_settings);
		mTitleCleanMaster = (ImageView) findViewById(R.id.title_cleanMaster);
		mTitleGroup = findViewById(R.id.titleGroup);
		mTextUninstall.setOnClickListener(this);
		mTextUpdateApp.setOnClickListener(this);
		mAppManagerSettings.setOnClickListener(this);
		mTitleCleanMaster.setOnClickListener(this);
		mTitleGroup.setOnClickListener(this);
		mLineUninstall = (LinearLayout) findViewById(R.id.tab_line_uninstall);
		mLineCleanScreen = (LinearLayout) findViewById(R.id.tab_line_clean_screen);

		mTitleSelectColor = getResources().getColor(R.color.app_manager_tap_title_select);
		mTitleNoSelectColor = getResources().getColor(R.color.app_manager_tap_title_not_select);
		mUnderlineSelectColor = getResources().getColor(R.color.app_manager_tap_underline_select);
		mUnderlineNoSelectColor = getResources().getColor(
				R.color.app_manager_tap_underline_not_select);
		
		showTabUninstall();
	}

	/**
	 * <br>功能简述:初始化滚动内容
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void initScrollerViewGroup() {
		Context context = this.getApplicationContext();
		mScrollerViewGroup = new ScrollerViewGroup(context, this);
		mUninstallView = new UninstallAppView(this);
		mUpdateAppView = new UpdateAppView(context, mInflater);
		mScrollerViewGroup.addView(mUninstallView);
		mScrollerViewGroup.addView(mUpdateAppView);
		mScrollerViewGroup.setScreenCount(2);
		LinearLayout viewGroupLayout = (LinearLayout) findViewById(R.id.view_group_layout);
		viewGroupLayout.addView(mScrollerViewGroup);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tab_text_uninstall :
				mScrollerViewGroup.gotoViewByIndex(0);
				break;
			case R.id.tab_text_update_app :
				mScrollerViewGroup.gotoViewByIndex(1);
				break;
			case R.id.clean_settings :
				Intent intent = new Intent(this, SimpleAppManagerSettingActivity.class);
				startActivity(intent);
				GuiThemeStatistics
						.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.APP_MANAGER_06);
				break;
			case R.id.titleGroup :
				finish();
				break;
			case R.id.title_cleanMaster :
				recommendCleanMaster();
				break;	
			default :
				break;
		}
	}
	
	private void recommendCleanMaster() {
		PreferencesManager pre = new PreferencesManager(this);
		boolean isRecommendDuSpeedBooster = pre.getBoolean(
				IPreferencesIds.PREFERENCE_IS_RECOMMEND_DUSPEEDBOOSTER, false);
		if (isRecommendDuSpeedBooster) {
			if (!GoAppUtils.isAppExist(this, PackageName.DU_SPEED_BOOSTER)) {
				// 假如没有安装“安卓优化大师“
				final String mapId = "5377557";
				final String aId = "596";
				GuiThemeStatistics.guiStaticData(40, mapId, "f000", 1, "-1", "-1", "-1", "-1");
				AdvertDialogCenter.showDuSpeedBoosterDialog(SimpleAppManagerActivity.this, mapId, aId,
						LauncherEnv.Url.DU_SPEED_BOOSTER_URL);
			} else {
				Intent intent = getPackageManager().getLaunchIntentForPackage(
						PackageName.DU_SPEED_BOOSTER);
				if (intent != null) {
					startActivity(intent);
				}
			}
		} else {
			if (!GoAppUtils.isAppExist(this, PackageName.CLEAN_MASTER_PACKAGE)) {

				GuiThemeStatistics.appManagerAdStaticData("2463865", "f000", 1, "-1");

				final AdvertDialog dialog = new AdvertDialog(this);
				dialog.show();
				dialog.setTitle(R.string.residue_file_dialog_title);
				dialog.setMessage(R.string.residue_file_dialog_msg_no_ad);
				dialog.setNegativeButton(R.string.residue_file_dialog_btn_later,
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								GuiThemeStatistics.appManagerAdStaticData("2463865", "cancel", 1,
										"-1");
								dialog.dismiss();
							}
						});
				dialog.setPositiveButton(R.string.residue_file_dialog_btn_get_it,
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								GuiThemeStatistics.appManagerAdStaticData("2463865", "a000", 1,
										"-1");

								AdvertDialogCenter.sDownloadCleanMasterInAppManager = System
										.currentTimeMillis();
								if (!GoAppUtils.isAppExist(SimpleAppManagerActivity.this,
										PackageName.CLEAN_MASTER_PACKAGE)) {

									if (GoAppUtils.isMarketExist(SimpleAppManagerActivity.this)) {
										GoAppUtils
												.gotoMarket(
														SimpleAppManagerActivity.this,
														MarketConstant.APP_DETAIL
																+ PackageName.CLEAN_MASTER_PACKAGE
																+ LauncherEnv.Plugin.CLEAN_MASTER_GA_FOR_DIALOG);
									} else {
										AppUtils.gotoBrowser(
												SimpleAppManagerActivity.this,
												MarketConstant.BROWSER_APP_DETAIL
														+ PackageName.CLEAN_MASTER_PACKAGE
														+ LauncherEnv.Plugin.CLEAN_MASTER_GA_FOR_DIALOG);
									}
								}
							}
						});
			} else {
				Intent intent = getPackageManager().getLaunchIntentForPackage(
						PackageName.CLEAN_MASTER_PACKAGE);
				if (intent != null) {
					startActivity(intent);
				}
			}
		}
	}

	/**
	 * <br>功能简述:显示卸载
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void showTabUninstall() {
		mLineUninstall.setBackgroundColor(mUnderlineSelectColor);
		mLineCleanScreen.setBackgroundColor(mUnderlineNoSelectColor);
		mTextUninstall.setTextColor(mTitleSelectColor);
		mTextUpdateApp.setTextColor(mTitleNoSelectColor);
		GuiThemeStatistics
				.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.APP_MANAGER_01);
	}

	/**
	 * <br>功能简述:显示更新
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void showTabAppsUpdate() {
		mLineUninstall.setBackgroundColor(mUnderlineNoSelectColor);
		mLineCleanScreen.setBackgroundColor(mUnderlineSelectColor);
		mTextUninstall.setTextColor(mTitleNoSelectColor);
		mTextUpdateApp.setTextColor(mTitleSelectColor);
		GuiThemeStatistics
				.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.APP_MANAGER_02);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterAppInstallReceiver();
		unRegisterNetworkReceiver();
		mUninstallView.onDestroy();
		DeskSettingConstants.selfDestruct(getWindow().getDecorView());
	}

	@Override
	public void onScreenChanged(int newScreen, int oldScreen) {
		if (newScreen == 0) {
			showTabUninstall();
		} else if (newScreen == 1) {
			showTabAppsUpdate();
		}
	}
	
	/**
	 * 注册应用安装卸载事件
	 * 
	 * @param context
	 */
	private void registerInstallUninstallReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		intentFilter.addDataScheme("package");
		registerReceiver(mAppInstallListener, intentFilter);
	}
	
	/**
	 * 注册网络状态广播接收器
	 */
	private void registerNetworkReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mNetworkListener, filter);
	}
	
	private void unregisterAppInstallReceiver() {
		unregisterReceiver(mAppInstallListener);
	}
	
	private void unRegisterNetworkReceiver() {
		unregisterReceiver(mNetworkListener);
	}

	@Override
	public ScreenScroller getScreenScroller() {
		return null;
	}

	@Override
	public void setScreenScroller(ScreenScroller scroller) {
	}

	@Override
	public void onFlingIntercepted() {
	}

	@Override
	public void onScrollStart() {
	}

	@Override
	public void onFlingStart() {
	}

	@Override
	public void onScrollChanged(int newScroll, int oldScroll) {
	}

	@Override
	public void onScrollFinish(int currentScreen) {
	}

	@Override
	public void invalidate() {
	}

	@Override
	public void scrollBy(int x, int y) {
	}

	@Override
	public int getScrollX() {
		return 0;
	}

	@Override
	public int getScrollY() {
		return 0;
	}
}
