package com.jiubang.ggheart.screen.systemsettings;

import java.util.ArrayList;
import java.util.List;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.util.device.Machine;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DesksettingNoAdvertTipDialog;
import com.jiubang.ggheart.apps.desks.diy.pref.PrivatePreference;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.components.DeskActivity;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;
import com.jiubang.ggheart.data.statistics.Statistics;

/**
 * 简易系统设置
 * @author chenguanyu
 *
 */
public class SystemSettingsCenter extends DeskActivity implements OnClickListener {

	private static final String NOT_FIRST_GOLOCKER_DIALOG = "is_not_first_golocker_dialog";
	private PrivatePreference mPref = null;
	private BroadcastReceiver mRefreshReceiver;
	private ImageButton mPrimeButton = null;
	// 网络数据
	private SystemSettingBean mWIFI = null;			// wifi
	private SystemSettingBean mMobiData = null;		// 移动数据
	private SystemSettingBean mTether = null;		// 网络共享
	private SystemSettingBean mBluetooth = null;	// 蓝牙
	// 日常生活
	private SystemSettingBean mScreenLock = null;	// 屏幕锁屏
	private SystemSettingBean mDisplay = null;		// 亮度
	private SystemSettingBean mRingTone = null;		// 铃声
	private SystemSettingBean mLanguage = null;		// 语言输入法
	// 设置信息
	private SystemSettingBean mAppInfo = null;		// 应用详情
	private SystemSettingBean mStorage = null;		// 存储
	private SystemSettingBean mBattery = null;		// 电池
	private SystemSettingBean mDataUsage = null;	// 流量或账户
	
	private ArrayList<SystemSettingBean> mSettingList = null; // 保持所有设置项的列表（除了闹钟），用于扫描
	private DesksettingNoAdvertTipDialog mHadPayDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_settings_center);

		// prime 钻石图标
		mPrimeButton = (ImageButton) findViewById(R.id.desksetting_update_prime);
		mPrimeButton.setOnClickListener(this);
		//如果是韩国地区或者非200渠道的，隐藏购买按钮
		if (FunctionPurchaseManager.getInstance(getApplicationContext()).getPayFunctionState(
				FunctionPurchaseManager.PURCHASE_ITEM_FULL) == FunctionPurchaseManager.STATE_GONE
				|| !Statistics.is200ChannelUid(this)) {
			mPrimeButton.setVisibility(View.GONE);
		}
		if (FunctionPurchaseManager.getInstance(getApplicationContext()).queryItemPurchaseState(
				FunctionPurchaseManager.PURCHASE_ITEM_FULL)) {
			mPrimeButton.setImageResource(R.drawable.update_prime_highlight);
		}

		findViewById(R.id.wifi_btn).setOnClickListener(this);
		findViewById(R.id.mobidata_btn).setOnClickListener(this);
		findViewById(R.id.battery_btn).setOnClickListener(this);
		findViewById(R.id.storage_btn).setOnClickListener(this);
		findViewById(R.id.appinfo_btn).setOnClickListener(this);
		findViewById(R.id.language_btn).setOnClickListener(this);
		findViewById(R.id.ringtone_btn).setOnClickListener(this);
		findViewById(R.id.display_btn).setOnClickListener(this);
		findViewById(R.id.screenlock_btn).setOnClickListener(this);
		findViewById(R.id.tether_btn).setOnClickListener(this);
		findViewById(R.id.bluetooth_btn).setOnClickListener(this);
		findViewById(R.id.datausage_btn).setOnClickListener(this);
		findViewById(R.id.more_sys_setting).setOnClickListener(this);

		// 初始化
		initSettingBeans();
		// 扫描所有设置项
		scanAllSettings();

		registeRefreshReceiver(this);
	}

	/**
	 * 初始化各个设置项
	 */
	private void initSettingBeans() {
		mSettingList = new ArrayList<SystemSettingBean>();
		
		mWIFI = new SystemSettingBean(Settings.ACTION_WIFI_SETTINGS);
		mSettingList.add(mWIFI);
		mMobiData = new SystemSettingBean(Settings.ACTION_DATA_ROAMING_SETTINGS);
		mSettingList.add(mMobiData);
		// 由于流量在2.x系统无法打开，在此增加系统判断
		if (Machine.IS_ICS) {
			mTether = new SystemSettingBean("com.android.settings.TetherSettings");
		} else {
			mTether = new SystemSettingBean(Settings.ACTION_WIRELESS_SETTINGS);
			((ImageView) findViewById(R.id.tether_btn).findViewById(
					R.id.tether_btn_img))
					.setImageResource(R.drawable.system_settings_wireless);
			((TextView) findViewById(R.id.tether_btn).findViewById(
					R.id.tether_btn_text))
					.setText(R.string.system_settings_wireless);
		}
		mSettingList.add(mTether);
		mBluetooth = new SystemSettingBean(Settings.ACTION_BLUETOOTH_SETTINGS);
		mSettingList.add(mBluetooth);
		
		mScreenLock = new SystemSettingBean(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
		mSettingList.add(mScreenLock);
		mDisplay = new SystemSettingBean(Settings.ACTION_DISPLAY_SETTINGS);
		mSettingList.add(mDisplay);
		mRingTone = new SystemSettingBean(Settings.ACTION_SOUND_SETTINGS);
		mSettingList.add(mRingTone);
		mLanguage = new SystemSettingBean("com.android.settings.LanguageSettings");
		mSettingList.add(mLanguage);
		
		mAppInfo = new SystemSettingBean(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
		mSettingList.add(mAppInfo);
		mStorage = new SystemSettingBean(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
		mSettingList.add(mStorage);
		mBattery = new SystemSettingBean("com.android.settings.fuelgauge.PowerUsageSummary");
		mSettingList.add(mBattery);
		// 由于流量在2.x系统无法打开，在此增加系统判断
		if (Machine.IS_ICS) {
			mDataUsage = new SystemSettingBean("com.android.settings.Settings$DataUsageSummaryActivity");
		} else {
			mDataUsage = new SystemSettingBean(Settings.ACTION_DEVICE_INFO_SETTINGS);
			((ImageView) findViewById(R.id.datausage_btn).findViewById(
					R.id.datausage_btn_img))
					.setImageResource(R.drawable.system_settings_about_phone);
			((TextView) findViewById(R.id.datausage_btn).findViewById(
					R.id.datausage_btn_text))
					.setText(R.string.system_settings_about);
		}
		mSettingList.add(mDataUsage);
	}

	/**
	 * 扫描所有设置项
	 */
	private void scanAllSettings() {
		if (mSettingList == null || mSettingList.size() <= 0) {
			return;
		}
		
		SystemSettingBean settingBean = null;
		Intent intent = null;
		for (int i = 0; i < mSettingList.size(); i++) {
			settingBean = mSettingList.get(i);
			intent = scanSettingPkgByAction(settingBean.mAction);
			settingBean.mIntent = intent;
		}
	}
	
	/**
	 * 扫描系统设置包名
	 * @return pkg
	 */
	private Intent scanSettingPkgByAction(String action) {
		if (action == null) {
			return null;
		}
		
		ComponentName componentName = null;
		Intent tempIntent = null;
		String pkgName = null;
		String className = null;
		
		if (mPref == null) {
			mPref = PrivatePreference.getPreference(this);
		}
		
		String component = mPref.getString(action, null);
		if (component != null) {
			// 有记录
			String[] splitStrings = component.split("/");
			pkgName = splitStrings[0];
			className = splitStrings[1];
		} else {
			if (action.startsWith("com")) {
				pkgName = "com.android.settings";
				className = action;
			} else if (action.startsWith("android")) {
				Intent queryIntent = new Intent(action);
				PackageManager pm = getPackageManager();
				List<ResolveInfo> list = pm.queryIntentActivities(queryIntent, 0);
				if (list != null && list.size() > 0) {
					ResolveInfo info = list.get(0);
					pkgName = info.activityInfo.packageName;
					className = info.activityInfo.name;
				}
			}
			if (mPref == null) {
				mPref = PrivatePreference.getPreference(this);
			}
			mPref.putString(action, pkgName + "/" + className);
			mPref.commit();
		}
		
		if (pkgName != null && className != null) {
			componentName = new ComponentName(pkgName, className);
			tempIntent = new Intent(Intent.ACTION_MAIN);
			tempIntent.setComponent(componentName);
		}
		
		return tempIntent;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mHadPayDialog != null && mHadPayDialog.isShowing()) {
			mHadPayDialog.dismiss();
			mHadPayDialog = new DesksettingNoAdvertTipDialog(
					this);
			mHadPayDialog.show();
			mHadPayDialog.setNegativeButton(null, new OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});
		}
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(Settings.ACTION_SETTINGS);
		if (v == mPrimeButton) {
			if (!FunctionPurchaseManager.getInstance(getApplicationContext()).queryItemPurchaseState(
					FunctionPurchaseManager.PURCHASE_ITEM_FULL)) {
				DeskSettingUtils.showPayDialog(this, 103);
			} else {
				if (mHadPayDialog != null) {
					mHadPayDialog.dismiss();
				}
				mHadPayDialog = new DesksettingNoAdvertTipDialog(
						this);
				mHadPayDialog.show();
				mHadPayDialog.setNegativeButton(null, new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				});
			}
		} else {
			switch (v.getId()) {
				case R.id.wifi_btn :
					intent = mWIFI.mIntent;
					GuiThemeStatistics
							.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.QUICK_SETTINGS_WIFI);
					break;
				case R.id.datausage_btn :
					intent = mDataUsage.mIntent;
					GuiThemeStatistics
							.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.QUICK_SETTINGS_DATA_USAGE);
					break;
				case R.id.battery_btn :
					intent = mBattery.mIntent;
					GuiThemeStatistics
							.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.QUICK_SETTINGS_BATTERY);
					break;
				case R.id.storage_btn :
					intent = mStorage.mIntent;
					GuiThemeStatistics
							.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.QUICK_SETTINGS_STORAGE);
					break;
				case R.id.appinfo_btn :
					intent = mAppInfo.mIntent;
					GuiThemeStatistics
							.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.QUICK_SETTINGS_APPLICATION);
					break;
				case R.id.language_btn :
					intent = mLanguage.mIntent;
					GuiThemeStatistics
							.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.QUICK_SETTINGS_LANGUAGE);
					break;
				case R.id.ringtone_btn :
					intent = mRingTone.mIntent;
					GuiThemeStatistics
							.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.QUICK_SETTINGS_SOUND);
					break;
				case R.id.display_btn :
					intent = mDisplay.mIntent;
					GuiThemeStatistics
							.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.QUICK_SETTINGS_DISPLAY);
					break;
				case R.id.screenlock_btn :
					intent = mScreenLock.mIntent;
					GuiThemeStatistics
							.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.QUICK_SETTINGS_SECURITY);
					break;
				case R.id.tether_btn :
					intent = mTether.mIntent;
					GuiThemeStatistics
							.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.QUICK_SETTINGS_TETHER);
					break;
				case R.id.bluetooth_btn :
					intent = mBluetooth.mIntent;
					GuiThemeStatistics
							.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.QUICK_SETTINGS_BLUETOOTH);
					break;
				case R.id.mobidata_btn :
					intent = mMobiData.mIntent;
					GuiThemeStatistics
							.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.QUICK_SETTINGS_NETWORK);
					break;
				case R.id.more_sys_setting :
					GuiThemeStatistics
							.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.QUICK_SETTINGS_MORE_SETTINGS);
					break;
			}
			startActivitySafety(intent);
		}
	}
	
	private void startActivitySafety(Intent intent) {
		try {
			startActivity(intent);
		} catch (Exception e) {
			// 找不到对应设置项的情况下，先弹出提示，然后跳转到系统设置界面
			Toast.makeText(this, R.string.system_settings_start_failed_tip, Toast.LENGTH_LONG).show();
			
			intent = new Intent();
			intent.setAction(Settings.ACTION_SETTINGS);
			startActivity(intent);
		}
	}
	
	private void registeRefreshReceiver(final Context context) {
		mRefreshReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context2, Intent intent) {
				if (intent != null) {
					if (FunctionPurchaseManager.getInstance(getApplicationContext())
							.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_FULL)) {
						mPrimeButton.setImageResource(R.drawable.update_prime_highlight);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DeskSettingConstants.selfDestruct(getWindow().getDecorView());
		if (mSettingList != null && mSettingList.size() > 0) {
			SystemSettingBean bean = null;
			for (int i = 0; i < mSettingList.size(); i++) {
				bean = mSettingList.get(i);
				bean.cleanData();
			}
			bean = null;
			mSettingList.clear();
			mSettingList = null;
		}
		unRegisterRefreshReceiver(this);
	}
}
