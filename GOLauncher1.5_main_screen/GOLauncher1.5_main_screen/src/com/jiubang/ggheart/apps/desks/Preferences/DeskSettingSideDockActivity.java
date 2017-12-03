package com.jiubang.ggheart.apps.desks.Preferences;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.proxy.SettingProxy;
import com.go.util.AppUtils;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemCheckBoxView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemListView;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.data.info.DesktopSettingInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 
 * <br>
 * 类描述:桌面设置-侧边栏Activity <br>
 * 功能详细描述:
 * 
 * @author zhujian
 */
public class DeskSettingSideDockActivity extends DeskSettingBaseActivity {

	private DesktopSettingInfo mDesktopSettingInfo;

	/**
	 * 开启侧边栏
	 */
	private DeskSettingItemCheckBoxView mSettingSideDock;
	/**
	 * 侧边栏方向
	 */
	private DeskSettingItemListView mSettingSideDockPosition;
	/**
	 * 侧边栏响应区域
	 */
	private DeskSettingItemListView mSettingSideDockArea;

	/**
	 * 付费广告条
	 */
	private View mSettingPrimeBanner;
	
	/**
	 * 付费广告条容器
	 */
	private View mSettingPrimeBannerContainer;
	
	private int mLeftOrRight = -1;
	private int mEntranceid = 8;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.desk_setting_side_dock);
		mDesktopSettingInfo = SettingProxy.getDesktopSettingInfo();
		mEntranceid = getIntent().getIntExtra(DeskSettingAdvancedPayActivity.ENTRANCE_ID, 8);
		initViews();
		load();
		
		if (getIntent().getBooleanExtra("showTips", false)) {
			Toast.makeText(this, R.string.activation_code_side_dock_available, 2000).show();
	}
	}

	/**
	 * <br>
	 * 功能简述:初始化View <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void initViews() {

		mSettingSideDock = (DeskSettingItemCheckBoxView) findViewById(R.id.enable_side_dock);
		mSettingSideDock.setOnValueChangeListener(this);

		mSettingSideDockPosition = (DeskSettingItemListView) findViewById(R.id.side_dock_position);
		mSettingSideDockPosition.setOnValueChangeListener(this);
		mSettingSideDockPosition.setOnClickListener(this);

		mSettingSideDockArea = (DeskSettingItemListView) findViewById(R.id.side_dock_area);
		mSettingSideDockArea.setOnClickListener(this);
		
		mSettingPrimeBanner = findViewById(R.id.setting_prime_banner);
		mSettingPrimeBanner.setOnClickListener(this);
		
		mSettingPrimeBannerContainer = findViewById(R.id.setting_prime_banner_container);
		mSettingPrimeBannerContainer.setOnClickListener(this);
	}

	@Override
	public void load() {
		super.load();
		FunctionPurchaseManager purchaseManager = FunctionPurchaseManager.getInstance(getApplicationContext());
		boolean hasPay = purchaseManager.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_QUICK_ACTIONS);
		if (hasPay) {
			mSettingPrimeBannerContainer.setVisibility(View.GONE);
			View scrollView = findViewById(R.id.desk_setting_lock_scrollview);
			LayoutParams params = (LayoutParams) scrollView.getLayoutParams();
			params.topMargin = 0;
			scrollView.setLayoutParams(params);
			
			if (AppUtils.getVersionCodeByPkgName(this, PackageName.PRIME_KEY) < 5
					&& !GoAppUtils.isAppExist(this, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
				mSettingSideDock.setIsCheck(false);
				mSettingSideDockArea.setEnabled(false);
				mSettingSideDockPosition.setEnabled(false);
				//侧边栏数据还原
				mDesktopSettingInfo.mEnableSideDock = mSettingSideDock.getIsCheck();
				mDesktopSettingInfo.mSideDockPosition = 1;
				SettingProxy.updateDesktopSettingInfo(mDesktopSettingInfo);
				PreferencesManager.deleteSharedPreference(IPreferencesIds.PREFERENCE_SIDE_DOCK); 
			}
		} else {
			if (mDesktopSettingInfo.mEnableSideDock) {
				mDesktopSettingInfo.mEnableSideDock = false;
			}
			SettingProxy.updateDesktopSettingInfo(mDesktopSettingInfo);
			mSettingPrimeBannerContainer.setVisibility(View.VISIBLE);
		}

		if (mDesktopSettingInfo.mEnableSideDock) {
			mSettingSideDock.setIsCheck(true);
			mSettingSideDockPosition.setEnabled(true);
			mSettingSideDockArea.setEnabled(true);
		} else {
			mSettingSideDock.setIsCheck(false);
			mSettingSideDockPosition.setEnabled(false);
			mSettingSideDockArea.setEnabled(false);
		}

		mLeftOrRight = mDesktopSettingInfo.mSideDockPosition;
		DeskSettingConstants.updateSingleChoiceListView(mSettingSideDockPosition,
				String.valueOf(mLeftOrRight));
		if (mDesktopSettingInfo.mSideDockPosition == 0) {
			mSettingSideDockPosition.setSummaryText(R.string.desksetting_enable_side_dock_left);
			mSettingSideDock.setSummaryText(R.string.desksetting_enable_side_dock_slide_to_right);
		} else {
			mSettingSideDockPosition.setSummaryText(R.string.desksetting_enable_side_dock_right);
			mSettingSideDock.setSummaryText(R.string.desksetting_enable_side_dock_slide_to_left);
		}
	}

	@Override
	public void save() {
		super.save();

		boolean isChangeDesk = false;
		if (mDesktopSettingInfo != null) {

			//侧边栏
			if (mDesktopSettingInfo.mEnableSideDock != mSettingSideDock.getIsCheck()) {
				mDesktopSettingInfo.mEnableSideDock = mSettingSideDock.getIsCheck();
				isChangeDesk = true;
			}

			if (mDesktopSettingInfo.mSideDockPosition != mLeftOrRight) {
				mDesktopSettingInfo.mSideDockPosition = mLeftOrRight;
				isChangeDesk = true;

			}

			if (isChangeDesk) {
				//更改数据库数据
				SettingProxy.updateDesktopSettingInfo(mDesktopSettingInfo);
			}
		}
	}

	@Override
	public boolean onValueChange(DeskSettingItemBaseView baseView, Object value) {
		if (baseView == mSettingSideDock) {
			//判断是否付费 // 为什么去广告这个功能可以不用判断韩国版和CN包？？？
			FunctionPurchaseManager purchaseManager = FunctionPurchaseManager.getInstance(getApplicationContext());
			boolean hasPay = purchaseManager.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_QUICK_ACTIONS);
			if (!hasPay) {
				DeskSettingUtils.showPayDialog(this, 601); //显示付费对话框
				GuiThemeStatistics.payStaticData("g001", 0, "601");
				mSettingSideDock.setIsCheck(false);
				return false;
			}
			//TODO:付费
			Intent intent = new Intent();
			if (Build.VERSION.SDK_INT >= 12) {
				intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			}
			Intent serviceIntent = new Intent();
			ComponentName com = null;
			if (GoAppUtils.isAppExist(this, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
				com = new ComponentName(LauncherEnv.Plugin.PRIME_GETJAR_KEY,
						"com.jiubang.plugin.controller.AppService");

			} else {
				com = new ComponentName(PackageName.PRIME_KEY,
						"com.jiubang.plugin.controller.AppService");
			}
			serviceIntent.setComponent(com);
			if ((Boolean) value == true) {
				mDesktopSettingInfo.mEnableSideDock = true;
				mSettingSideDock.setIsCheck(true);
				mSettingSideDockArea.setEnabled(true);
				mSettingSideDockPosition.setEnabled(true);
				serviceIntent.putExtra("isFromSetting", true); // 如果是从设置项打开就闪烁
				getBaseContext().startService(serviceIntent);
				SettingProxy.updateDesktopSettingInfo(mDesktopSettingInfo);
			} else {
				mDesktopSettingInfo.mEnableSideDock = false;
				mSettingSideDock.setIsCheck(false);
				mSettingSideDockArea.setEnabled(false);
				mSettingSideDockPosition.setEnabled(false);
				getBaseContext().stopService(serviceIntent);
				SettingProxy.updateDesktopSettingInfo(mDesktopSettingInfo);
			}

		} else if (baseView == mSettingSideDockPosition) {
			if (value instanceof String) {
				DeskSettingConstants.updateSingleChoiceListView(mSettingSideDockPosition,
						(String) value);

				Intent intent = new Intent();
				if (Build.VERSION.SDK_INT >= 12) {
					intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
				}
				mLeftOrRight = Integer.valueOf((String) value);
				if (mLeftOrRight == 0) {
					intent.setAction(ICustomAction.ACTION_START_LEFT_SIDEBAR);
					mSettingSideDockPosition
							.setSummaryText(R.string.desksetting_enable_side_dock_left);
					mSettingSideDock
							.setSummaryText(R.string.desksetting_enable_side_dock_slide_to_right);
					save();
				} else {
					intent.setAction(ICustomAction.ACTION_START_RIGHT_SIDEBAR);
					mSettingSideDockPosition
							.setSummaryText(R.string.desksetting_enable_side_dock_right);
					mSettingSideDock
							.setSummaryText(R.string.desksetting_enable_side_dock_slide_to_left);
					save();
				}
				getBaseContext().sendBroadcast(intent);

			}
		}
		return false;

	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		load();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.side_dock_area :

				if (mDesktopSettingInfo.mSideDockPosition == 0) {
					startActivityForResult(new Intent(this, ResponseAreaLeftSettingActivity.class),
							-1);
				} else {
					startActivityForResult(
							new Intent(this, ResponseAreaRightSettingActivity.class), -1);
				}
				break;

			case R.id.side_dock_position :
				mSettingSideDockPosition.onClick(v);

				break;
			case R.id.setting_prime_banner :
			case R.id.setting_prime_banner_container : 
				if (GoAppUtils.isAppExist(this, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
					FunctionPurchaseManager.getInstance(getApplicationContext()).startItemPayPage(
							mEntranceid + "");
				} else {
					DeskSettingUtils.showPayDialog(this, 601); //显示付费对话框
				}
				break;
			default :
				break;
		}
	}

	@Override
	protected void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
		return false;
	}

}
