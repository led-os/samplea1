package com.jiubang.ggheart.apps.desks.diy.themescan;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.util.ProcessKiller;
import com.go.util.log.LogConstants;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.admob.AdViewInfoReceiver;
import com.jiubang.ggheart.admob.GoDetailAdView;
import com.jiubang.ggheart.apps.desks.diy.INotificationId;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.MessageManager;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.apps.desks.purchase.getjar.GetJarManager;
import com.jiubang.ggheart.apps.desks.purchase.getjar.GetJarManager.IConnectCallBack;
import com.jiubang.ggheart.components.DeskResources;
import com.jiubang.ggheart.components.DeskResourcesConfiguration;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.StatisticsData;
import com.jiubang.ggheart.data.theme.OnlineThemeGetter;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 主题管理Activity
 * 
 * @author yangbing
 * */
public class ThemeManageActivity extends Activity {

	// public static final String ACTION_FEATURED_THEME_CHANGED =
	// "android.intent.action.FEATURED_THEME_CHANGED";
	// public static final String ACTION_NEW_THEME_INSTALLED =
	// "android.intent.action.NEW_THEME_INSTALLED";

	protected static final int LOAD_AD_MSG_CODE = 0x1;
	private static final int REQUEST_RESOLUTION = 1;
	private ThemeManageView mThemeManageView; // 主界面

	public static boolean sRefreshFlag = false;
	private BroadcastReceiver mThemeChangeReceiver;
	private BroadcastReceiver mLockerVipReceiver; // 来自锁屏vip的广播
	public static boolean sRuning = false;

	private static boolean sRunKill;
	private boolean mStoped = false;

	/**
	 * 是否已经绑定下载服务的标记
	 */
	private boolean mAdLoaded;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case LOAD_AD_MSG_CODE :
					if (mThemeManageView != null) {
						mThemeManageView.loadAd();
					}
					break;
				default :
					break;
			}
		};
	};

	private long mCreateTime;
	private static final int INIT_TIME = 5 * 1000;
	private static final int KILL_PROCESS_DELAY_TIME = 1800;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Log.e("zyz", "initLauncherMainView");
//		new PurchaseStateManager(getApplicationContext()).save(IPreferencesIds.CUSTOMER_VIP_NEW, IPreferencesIds.CUSTOMER_VIP_NEW);
//		ThemePurchaseManager.writeNewLauncherVipToSD(IPreferencesIds.CUSTOMER_VIP_NEW, getApplicationContext());
		mCreateTime = System.currentTimeMillis();
		confirmOrientation();
		registerThemeChangedReceiver();
		Intent intent = getIntent();
		String viewTabName = intent.getAction();
		if (ICustomAction.ACTION_THEME_PAYVIPPAGE.equals(viewTabName)) {
			Intent it = new Intent(ThemeManageActivity.this, ThemeVipPage.class);
			mThemeManageView = new ThemeManageView(this, ThemeManageView.LAUNCHER_THEME_VIEW_ID,
					OnlineThemeGetter.TAB_LAUNCHER_FEATURED_ID);
			String url = ThemeManager.getVipPayPageUrl(ThemeManageActivity.this);
			it.putExtra("url", url);
			startActivity(it);
		} else if (ICustomAction.ACTION_SHOW_THEME_PREVIEW.equals(viewTabName)) {
			GuiThemeStatistics.guiStaticData("",
					GuiThemeStatistics.OPTION_CODE_LOGIN, 1,
					String.valueOf(GuiThemeStatistics.ENTRY_MESSAGE_PUSH), "", "", "");
			int tab = intent.getExtras().getInt(ThemeConstants.TAB_THEME_KEY);
			mThemeManageView = new ThemeManageView(this, ThemeManageView.LAUNCHER_THEME_VIEW_ID,
					tab);
			if (tab == OnlineThemeGetter.TAB_LAUNCHER_FEATURED_ID) {
				PreferencesManager sp = new PreferencesManager(this,
						IPreferencesIds.SHAREDPREFERENCES_MSG_THEME_NOTIFY_STATICS_DATA,
						Context.MODE_PRIVATE);
				int cnt = sp.getInt(IPreferencesIds.SHAREDPREFERENCES_MSG_CLICK_TIMES, 0);
				sp.putInt(IPreferencesIds.SHAREDPREFERENCES_MSG_CLICK_TIMES, cnt + 1);
				sp.commit();
				MessageManager.getMessageManager(getApplication()).updateThemeNotifyStatisticsData(
						ThemeConstants.STATICS_ID_FEATURED_NOTIFY, 0, false);
				//				StatisticsData.saveGuiTabStat(this,
				//						String.valueOf(ThemeConstants.STATICS_ID_FEATURED));
				GuiThemeStatistics.guiStaticData("",
						GuiThemeStatistics.OPTION_CODE_TAB_CLICK, 1, "",
						String.valueOf(ThemeConstants.STATICS_ID_FEATURED), "", "");
			} else if (tab == OnlineThemeGetter.TAB_LAUNCHER_HOT_ID) {
				//				StatisticsData.saveGuiTabStat(this, String.valueOf(ThemeConstants.STATICS_ID_HOT));
				GuiThemeStatistics.guiStaticData("",
						GuiThemeStatistics.OPTION_CODE_TAB_CLICK, 1, "",
						String.valueOf(ThemeConstants.STATICS_ID_HOT), "", "");
			}

		} else if (ICustomAction.ACTION_SHOW_LOCKER_THEME_PREVIEW.equals(viewTabName)) {
			GuiThemeStatistics.guiStaticData("",
					GuiThemeStatistics.OPTION_CODE_LOGIN, 1,
					String.valueOf(GuiThemeStatistics.ENTRY_MESSAGE_PUSH_LOCKER), "", "", "");
			mThemeManageView = new ThemeManageView(this, ThemeManageView.LOCKER_THEME_VIEW_ID, 0);
			PreferencesManager sp = new PreferencesManager(this,
					IPreferencesIds.SHAREDPREFERENCES_MSG_LOCKER_THEME_NOTIFY_STATICS_DATA,
					Context.MODE_PRIVATE);
			int cnt = sp.getInt(IPreferencesIds.SHAREDPREFERENCES_MSG_CLICK_TIMES, 0);
			sp.putInt(IPreferencesIds.SHAREDPREFERENCES_MSG_CLICK_TIMES, cnt + 1);
			sp.commit();
			MessageManager.getMessageManager(getApplication()).updateThemeNotifyStatisticsData(
					ThemeConstants.STATICS_ID_LOCKER_NOTIFY, 0, false);
			//			StatisticsData.saveGuiTabStat(this, String.valueOf(ThemeConstants.STATICS_ID_LOCKER));
			GuiThemeStatistics.guiStaticData("",
					GuiThemeStatistics.OPTION_CODE_TAB_CLICK, 1, "",
					String.valueOf(ThemeConstants.STATICS_ID_LOCKER), "", "");
		} else {
			Bundle bundle = getIntent().getExtras();
			int mEntrance = ThemeManageView.LAUNCHER_THEME_VIEW_ID;
			int tab = OnlineThemeGetter.TAB_LAUNCHER_FEATURED_ID;
			if (bundle != null) {
				mEntrance = bundle.getInt("entrance");
				tab = bundle.getInt(ThemeConstants.TAB_THEME_KEY);
			}
			if (ICustomAction.ACTION_MYTHEMES.equals(viewTabName)
					&& mEntrance == ThemeManageView.LOCKER_THEME_VIEW_ID) {
				GuiThemeStatistics.guiStaticData("",
						GuiThemeStatistics.OPTION_CODE_LOGIN, 1,
						String.valueOf(GuiThemeStatistics.ENTRY_GO_LOCKER), "", "", "");
			}
			mThemeManageView = new ThemeManageView(this, mEntrance, tab);
			if (mEntrance == ThemeManageView.LAUNCHER_THEME_VIEW_ID) {
				//				StatisticsData.saveGuiTabStat(this,
				//						String.valueOf(ThemeConstants.STATICS_ID_FEATURED));
				GuiThemeStatistics.guiStaticData("",
						GuiThemeStatistics.OPTION_CODE_TAB_CLICK, 1, "",
						String.valueOf(ThemeConstants.STATICS_ID_FEATURED), "", "");
			} else {
				//				StatisticsData.saveGuiTabStat(this,
				//						String.valueOf(ThemeConstants.STATICS_ID_LOCKER));
				GuiThemeStatistics.guiStaticData("",
						GuiThemeStatistics.OPTION_CODE_TAB_CLICK, 1, "",
						String.valueOf(ThemeConstants.STATICS_ID_LOCKER), "", "");
			}
		}
		setContentView(mThemeManageView);
		sRuning = true;
		mStoped = false;
		sRefreshFlag = false;

		clearAllNotifyData();
		StatisticsData.saveGuiEntry(this);
		Bundle bundle = getIntent().getExtras();
		int mEntrance = 0;
		if (bundle != null) {
			mEntrance = bundle.getInt("entrance");
		}
		if (mEntrance == 0) {
			mEntrance = ThemeManageView.LAUNCHER_THEME_VIEW_ID;
		}
		checkShowVipTip(mEntrance);
		PreferencesManager preferencesManager = new PreferencesManager(this, IPreferencesIds.PREFERENCE_SETTING_CFG, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
		preferencesManager.putBoolean(IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_OPEN_GOSTORE, false);
		preferencesManager.commit();
		//		showVipTip();
		final GetJarManager getJarManager = GetJarManager.buildInstance();
		getJarManager.addConnectCallBack(new IConnectCallBack() {
			@Override
			public void onConnect(boolean connect, Intent resolution) {
				// TODO Auto-generated method stub
				if (!connect && resolution != null) {
					startActivityForResult(resolution, REQUEST_RESOLUTION);
				}
			}
		});
		if (getJarManager.getCurrentAccount() == null) {
			GetJarManager.showSelectAccountDialog(this);
		}
		
	}

	private Toast mToast;

	@Override
	public Resources getResources() {
		DeskResourcesConfiguration configuration = DeskResourcesConfiguration.createInstance(this
				.getApplicationContext());
		if (null != configuration) {
			Resources resources = configuration.getDeskResources();
			if (null != resources) {
				return resources;
			}
		}
		return super.getResources();
	}

	private void registerThemeChangedReceiver() {

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		intentFilter.addAction(ICustomAction.ACTION_FEATURED_THEME_CHANGED);
		intentFilter.addAction(ICustomAction.ACTION_ZIP_THEME_REMOVED);
		intentFilter.addAction(ICustomAction.ACTION_NEW_THEME_INSTALLED);
		intentFilter.addAction(ICustomAction.ACTION_BANNER_DATA_CHANGEED);
		intentFilter.addAction(ICustomAction.ACTION_FEATURED_THEME_LOADFINISHED);
		intentFilter.addDataScheme("package");
		intentFilter.setPriority(Integer.MAX_VALUE);
		mThemeChangeReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String packageName = intent.getDataString();
				if (ICustomAction.ACTION_FEATURED_THEME_LOADFINISHED.equals(intent.getAction())) {
					int type = intent
							.getIntExtra("type", ThemeConstants.LAUNCHER_FEATURED_THEME_ID);
					mThemeManageView.loadDataFinished(type);
				} else if (ICustomAction.ACTION_FEATURED_THEME_CHANGED.equals(intent.getAction())) {
					if (mThemeManageView != null) {
						mThemeManageView.updateMainView(-1, -1);
					}
				} else if (ICustomAction.ACTION_NEW_THEME_INSTALLED.equals(intent.getAction())) {
					ThemeDataManager.getInstance(context).clearup();
					if (mThemeManageView != null) {
						mThemeManageView.addNewLog();
						if (!mStoped) {
							mThemeManageView.updateMainView(-1, -1);
						} else {
							sRefreshFlag = true;
						}
					} else {
						sRefreshFlag = true;
					}
				} else if (packageName.contains(ThemeConstants.LAUNCHER_THEME_PREFIX)
						|| packageName.contains(ThemeConstants.LOCKER_THEME_PREFIX)
						|| ICustomAction.ACTION_ZIP_THEME_REMOVED.equals(intent.getAction())) {
					ThemeDataManager.getInstance(context).clearup();
					if (mThemeManageView != null && !mStoped) {
						mThemeManageView.updateMainView(-1, -1);
					} else {
						sRefreshFlag = true;
					}
				} else if (packageName.contains(PackageName.LOCKER_PACKAGE)) {
					if (mThemeManageView != null
							&& mThemeManageView.getEntranceId() == ThemeManageView.LOCKER_THEME_VIEW_ID) {
						mThemeManageView.updateMainView(-1, -1);
					}
				} else if (ICustomAction.ACTION_BANNER_DATA_CHANGEED.equals(intent.getAction())) {
					if (mThemeManageView != null) {
						int type = intent.getIntExtra("type",
								ThemeConstants.LAUNCHER_FEATURED_THEME_ID);
						mThemeManageView.reLoadBannerData(type);
					}
				}
				// }
			}
		};
		
		IntentFilter lockerVipFilter = new IntentFilter();
		lockerVipFilter.addAction(ICustomAction.ACTION_THEME_GOLOCKER_VIPSTATUS_CHANGE);
		mLockerVipReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				if (ICustomAction.ACTION_THEME_GOLOCKER_VIPSTATUS_CHANGE.equals(intent.getAction())) {
					Log.d("zyz", "ACTION_THEME_GOLOCKER_VIPSTATE_CHANGE");
					if (mThemeManageView != null) {
						mThemeManageView.updateMainView(-1, -1);
					}
					
				}
			}
			
		};
		try {
			registerReceiver(mThemeChangeReceiver, intentFilter);
			registerReceiver(mLockerVipReceiver, lockerVipFilter);
		} catch (Throwable e) {
			try {
				unregisterReceiver(mThemeChangeReceiver);
				unregisterReceiver(mLockerVipReceiver);
				registerReceiver(mThemeChangeReceiver, intentFilter);
				registerReceiver(mLockerVipReceiver, lockerVipFilter);
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		confirmOrientation();
		sRuning = true;
		mStoped = false;
		if (sRefreshFlag) {
			mThemeManageView.updateMainView(-1, -1);
			sRefreshFlag = false;
		}
		mThemeManageView.onResume();
		if (!mAdLoaded) {
			mHandler.removeMessages(LOAD_AD_MSG_CODE);
			mHandler.sendEmptyMessageDelayed(LOAD_AD_MSG_CODE, 15);
			mAdLoaded = true;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mStoped = true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent == null || mThemeManageView == null) {
			return;
		}

		String viewTabName = intent.getAction();
		if (ICustomAction.ACTION_THEME_PAYVIPPAGE.equals(viewTabName)) {
			Intent it = new Intent(this, ThemeVipPage.class);
			String url = ThemeManager.getVipPayPageUrl(ThemeManageActivity.this);
			it.putExtra("url", url);
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(it);
		} else if (ICustomAction.ACTION_SHOW_THEME_PREVIEW.equals(viewTabName)) {
			// 点击通知栏，刷新桌面主题界面
			int tab = intent.getExtras().getInt(ThemeConstants.TAB_THEME_KEY);
			if (tab == OnlineThemeGetter.TAB_LAUNCHER_FEATURED_ID) {
				mThemeManageView.updateMainView(ThemeManageView.LAUNCHER_THEME_VIEW_ID,
						ThemeManageView.FEATURED_THEME_VIEW_ID);
			} else {
				mThemeManageView.updateMainView(ThemeManageView.LAUNCHER_THEME_VIEW_ID,
						ThemeManageView.HOT_THEME_VIEW_ID);
			}
		} else if (ICustomAction.ACTION_SHOW_LOCKER_THEME_PREVIEW.equals(viewTabName)) {
			// 点击通知栏，刷新锁屏主题界面
			mThemeManageView.updateMainView(ThemeManageView.LOCKER_THEME_VIEW_ID,
					ThemeManageView.FEATURED_THEME_VIEW_ID);
		}

	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		sRuning = false;
		super.onDestroy();
		StatisticsManager.getInstance(ApplicationProxy.getContext()).destory();
		//		GuiThemeStatistics.setCurrentEntry(GuiThemeStatistics.ENTRY_TYPE_OTHER, this);
		if (mThemeChangeReceiver != null) {
			unregisterReceiver(mThemeChangeReceiver);
			
		}
		if (mLockerVipReceiver != null) {
			unregisterReceiver(mLockerVipReceiver);
		}
//		ThemeImageManager.getInstance(this).destory();
		ThemeDataManager.getInstance(this).clearup();
		mThemeManageView.onDestroy();
		FunctionPurchaseManager.destory();
		ThemePurchaseManager.getInstance(getApplicationContext()).destory();
		mHandler.removeCallbacksAndMessages(null);
		GetJarManager.destory();
		//清除详细页面的adView
		GoDetailAdView.cleanView();
		
		exit(getApplicationContext(), KILL_PROCESS_DELAY_TIME);
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Resources res = getResources();
		if (res instanceof DeskResources) {
			res.updateConfiguration(super.getResources().getConfiguration(), super.getResources()
					.getDisplayMetrics());

			try {
				Configuration config = res.getConfiguration(); // 获得设置对象
				DisplayMetrics dm = res.getDisplayMetrics(); // 获得屏幕参数：主要是分辨率，像素等。
				PreferencesManager preferences = new PreferencesManager(this,
						IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
				String currentlanguage = preferences.getString(
						IPreferencesIds.CURRENT_SELECTED_LANGUAGE, "");
				if (currentlanguage != null && !currentlanguage.equals("")) {
					if (currentlanguage.length() == 5) {
						String language = currentlanguage.substring(0, 2);
						String country = currentlanguage.substring(3, 5);
						config.locale = new Locale(language, country);
					} else {
						config.locale = new Locale(currentlanguage);
					}
					res.updateConfiguration(config, dm);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//		DisplayMetrics mMetrics = getResources().getDisplayMetrics();
		//		if (mMetrics.widthPixels <= mMetrics.heightPixels) {
		//			SpaceCalculator.setIsPortrait(true);
		//		} else {
		//			SpaceCalculator.setIsPortrait(false);
		//		}
		//TODO
		confirmOrientation();
		if (null != mThemeManageView) {
			mThemeManageView.changeOrientation();
		}
	}

	/**
	 * 判断是横屏还是竖屏
	 * */
	public void confirmOrientation() {
		DisplayMetrics mMetrics = getResources().getDisplayMetrics();
		if (mMetrics.widthPixels <= mMetrics.heightPixels) {
			SpaceCalculator.setIsPortrait(true);
			SpaceCalculator.getInstance(this).calculateItemViewInfo();
		} else {
			SpaceCalculator.setIsPortrait(false);
			SpaceCalculator.getInstance(this).calculateThemeListItemCount();
		}
		//		SpaceCalculator.getInstance(this).calculateThemeListItemCount();
	}

	public static void exit(Context context, int delay) {
		if (!sRunKill) {
			sRunKill = true;
			if (context != null) {
				context.sendBroadcast(new Intent(AdViewInfoReceiver.UPLOAD_ADINFO_ACTION));
			}
			ProcessKiller.killSelf(context, delay);
		}
	}

	/**
	 * 跳转到gostore下载更多
	 * */
	public void gotoGoStore() {
		if (mThemeManageView != null) {
			mThemeManageView.gotoGoStore();
		}
	}

	/**
	 * 跳转到Banner的列表内容
	 */
	public void gotoBannerList(int ty, String title) {
//		Intent intent = new Intent();
//		intent.putExtra("ty", ty);
//		intent.putExtra("title", title);
//		intent.setClass(this, BannerDetailActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//		this.startActivity(intent);
		GoAppUtils.goToThemeSpec(this, ty, false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		// 传递到ThemeManagerView中处理
		Boolean bool = mThemeManageView.keyDown(keyCode, event);
		if (bool) {
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	private void clearAllNotifyData() {
		ThemeManager.getInstance(getApplicationContext()).removeNotifyBean(
				ThemeConstants.LAUNCHER_FEATURED_THEME_ID);
		ThemeManager.getInstance(getApplicationContext()).removeNotifyBean(
				ThemeConstants.LAUNCHER_HOT_THEME_ID);
		ThemeManager.getInstance(getApplicationContext()).removeNotifyBean(
				ThemeConstants.LOCKER_FEATURED_THEME_ID);

	}

	@Override
	public void onBackPressed() {
		try {
			if (System.currentTimeMillis() - mCreateTime < INIT_TIME) {
				showToast(getResources().getString(R.string.go_theme_strat_loading));
				return;
			}
			ThemeTabBannerManager.getCache().cleanUp();
			super.onBackPressed();
			exit(getApplicationContext(), KILL_PROCESS_DELAY_TIME);
		} catch (Exception e) {
			Log.e(LogConstants.HEART_TAG, "onBackPressed err " + e.getMessage());
		}
	}

	private void showToast(String tip) {
		if (mToast == null) {
			mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		}
		mToast.setText(tip);
		mToast.cancel();
		if (mThemeManageView != null) {
			mThemeManageView.postDelayed(new Runnable() {

				@Override
				public void run() {
					mToast.show();
				}
			}, 100);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public void checkShowVipTip(int mEntrance) {
		int level = ThemePurchaseManager.getCustomerLevel(getApplicationContext(), mEntrance);
		if (level == ThemeConstants.CUSTOMER_LEVEL0) {
			PreferencesManager pm = new PreferencesManager(this,
					IPreferencesIds.FEATUREDTHEME_CONFIG, Context.MODE_PRIVATE);
			boolean bool = pm.getBoolean(IPreferencesIds.HAD_SHOW_VIP_TIP, false);
			if (!bool) {
				ArrayList<String> pkgs = ThemePurchaseManager.getInstance(this).getPaidPkgs();
				if (pkgs != null) {
					for (int i = 0; i < pkgs.size(); i++) {
						String pkg = pkgs.get(i);
						if (GoAppUtils.isAppExist(this, pkg)) {
							showVipTip();
							ThemePurchaseManager.clearPaidThemePkgList();
							pm.putBoolean(IPreferencesIds.HAD_SHOW_VIP_TIP, true);
							pm.commit();
							break;
						}
					}
				}
			}
		}
	}

	private void showVipTip() {
		Intent intent = new Intent(this, ThemeManageActivity.class);
		intent.setAction(ICustomAction.ACTION_THEME_PAYVIPPAGE);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		GoAppUtils.sendNotification(this, intent, R.drawable.icon,
				getString(R.string.vip_notification_tip), getString(R.string.vip_notification_tip),
				getString(R.string.vip_notification_tip), INotificationId.THEME_VIP_NOTIFICATION);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
			case REQUEST_RESOLUTION :
				if (resultCode == Activity.RESULT_OK) {
					final GetJarManager getJarManager = GetJarManager.buildInstance();
					getJarManager.connect(new IConnectCallBack() {

						@Override
						public void onConnect(boolean connect, Intent resolution) {
							// TODO Auto-generated method stub
							if (connect) {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										if (getJarManager.getCurrentAccount() == null) {
											GetJarManager.showSelectAccountDialog(ThemeManageActivity.this);
										}
									}
								});
							}
						}
					});
				}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
