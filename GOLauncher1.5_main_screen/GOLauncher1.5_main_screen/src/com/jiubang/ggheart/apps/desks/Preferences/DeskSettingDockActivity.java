package com.jiubang.ggheart.apps.desks.Preferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.gau.go.launcherex.R;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.util.AppUtils;
import com.go.util.device.Machine;
import com.go.util.market.MarketConstant;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IDockMsgId;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingNewMarkManager.NewMarkKeys;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingSingleInfo;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemCheckBoxView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemListView;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.frames.dock.DockConstants;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.data.info.ShortCutSettingInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 
 * @author guoyiqing
 * 
 */
public class DeskSettingDockActivity extends DeskSettingBaseActivity {

	private DeskSettingItemBaseView mSettingSideDock;
	private DeskSettingItemListView mSettingDockRows;
	private DeskSettingItemCheckBoxView mSettingShowDock;
	private ShortCutSettingInfo mShortCutSettingInfo;
	private DeskSettingItemCheckBoxView mSettingDockChangeLoop;
	private BroadcastReceiver mRefreshReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registeRefreshReceiver(this);
		setContentView(R.layout.desk_setting_dock_main);

		mShortCutSettingInfo = SettingProxy.getShortCutSettingInfo();
		mSettingSideDock = (DeskSettingItemBaseView) findViewById(R.id.side_dock);
		mSettingSideDock.setOnClickListener(this);
		if (!Statistics.is200ChannelUid(this)
		/* || Machine.isKorea(this) */) {
			mSettingSideDock.setVisibility(View.GONE);
		}
		// 桌面-快捷条设置
		mSettingDockRows = (DeskSettingItemListView) findViewById(R.id.dock_rows);
		mSettingDockRows.setOnValueChangeListener(this);

		mSettingShowDock = (DeskSettingItemCheckBoxView) findViewById(R.id.show_dock);
		mSettingShowDock.setOnValueChangeListener(this);

		mSettingDockChangeLoop = (DeskSettingItemCheckBoxView) findViewById(R.id.dock_change_loop);
//		mSettingDockChangeLoop.setOnValueChangeListener(this);
//		mSettingDockAutoFit.setOnValueChangeListener(this);
		loadDesk();
		GuiThemeStatistics
				.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.PREFERENCES_DOCK);
	}

	/**
	 * <br>
	 * 功能简述:加载桌面设置 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void loadDesk() {
		loadDock();	
		//是否已经打开过，第一次打开就显示new标志 ,是200渠道，且非韩国区域
		mSettingShowDock.setIsCheck(ShortCutSettingInfo.sEnable); //设置Dock条是否显示
	}

	@Override
	protected void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		if (newMarkManager.isShowNew(NewMarkKeys.SIDE_DOCK, true)) {
			mSettingSideDock.setImageNewVisibile(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.side_dock:
			mSettingSideDock.setImageNewVisibile(View.GONE);
			FunctionPurchaseManager purchaseManager = FunctionPurchaseManager
					.getInstance(getApplicationContext());
			boolean hasPay = purchaseManager
					.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_QUICK_ACTIONS);
			// if (hasPay) {
			// if (AppUtils.getVersionCodeByPkgName(this,
			// LauncherEnv.Plugin.PRIME_KEY) <
			// LauncherEnv.Plugin.PRIME_KEY_WITH_SIDE_DOCK_VER
			// && !GoAppUtils.isAppExist(this,
			// LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
			// showUpdateKeyDialog();
			// } else {
			// startActivity(new Intent(getApplicationContext(),
			// DeskSettingSideDockActivity.class));
			// }
			// } else {
			// DeskSettingUtils.showPayDialog(this, 601); //显示付费对话框
			// }
			if (hasPay) {
				if (AppUtils.getVersionCodeByPkgName(this,
						PackageName.PRIME_KEY) < LauncherEnv.Plugin.PRIME_KEY_WITH_SIDE_DOCK_VER
						&& !GoAppUtils.isAppExist(this,
								LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
					showUpdateKeyDialog();
				} else {
					startActivity(new Intent(getApplicationContext(),
							DeskSettingSideDockActivity.class));
				}
			} else {
				startActivity(new Intent(getApplicationContext(),
						DeskSettingSideDockActivity.class));
			}
			break;
		default:
			break;
		}
	}

	private void showUpdateKeyDialog() {
		DialogConfirm updateKeyDialog = new DialogConfirm(this);
		updateKeyDialog.show();
		int titleId = R.string.key_version_update;
		int msgId = R.string.key_text;
		int btnTxt = R.string.key_update;
		if (Machine.isPurchaseByGetjarContury(DeskSettingDockActivity.this)
				|| FunctionPurchaseManager.getInstance(getApplicationContext())
						.queryItemPurchaseState(FunctionPurchaseManager.PURCHASE_ITEM_ACTIVE_CODE)) {
			titleId = R.string.title_tips;
			msgId = R.string.activation_code_side_dock_download_msg;
			btnTxt = R.string.widget_theme_apply_download;
		}
		updateKeyDialog.setTitle(getString(titleId));
		updateKeyDialog.setMessage(msgId);
		updateKeyDialog.setNegativeButtonVisible(View.GONE);
		updateKeyDialog.setPositiveButton(btnTxt, new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Machine.isPurchaseByGetjarContury(DeskSettingDockActivity.this)
						|| FunctionPurchaseManager.getInstance(getApplicationContext())
								.queryItemPurchaseState(
										FunctionPurchaseManager.PURCHASE_ITEM_ACTIVE_CODE)) {
					if (FunctionPurchaseManager.getInstance(getApplicationContext())
							.queryItemPurchaseState(
									FunctionPurchaseManager.PURCHASE_ITEM_ACTIVE_CODE)) {
						PreferencesManager pm = new PreferencesManager(
								DeskSettingDockActivity.this,
								IPreferencesIds.PREFERENCE_SETTING_CFG, Context.MODE_PRIVATE
										| Context.MODE_MULTI_PROCESS);
						pm.putBoolean(IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_GOTO_SLIDEDOCK,
								true);
						pm.commit();
					}
					GoAppUtils.gotoBrowserIfFailtoMarket(DeskSettingDockActivity.this,
							MarketConstant.APP_DETAIL + LauncherEnv.Plugin.PRIME_GETJAR_KEY,
							LauncherEnv.Plugin.PRIME_GETJAR_KEY_URL);
				} else {
					GoAppUtils.gotoBrowserIfFailtoMarket(DeskSettingDockActivity.this,
							MarketConstant.APP_DETAIL + PackageName.PRIME_KEY,
							LauncherEnv.Plugin.PRIME_KEY_URL);
				}

			}
		});
	}

	private void registeRefreshReceiver(final Context context) {
		mRefreshReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context2, Intent intent) {
				if (intent != null) {
					if (mSettingSideDock != null) {
						FunctionPurchaseManager purchaseManager = FunctionPurchaseManager
								.getInstance(getApplicationContext());
						boolean hasPay = purchaseManager
								.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_QUICK_ACTIONS);
						if (hasPay) {
							// mSettingSideDock.setIsCheck(true);
							mSettingSideDock.setImagePrimeVisibile(View.GONE);
						}
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
		if (mRefreshReceiver != null) {
			context.unregisterReceiver(mRefreshReceiver);
			mRefreshReceiver = null;
		}
	}

	@Override
	protected void onDestroy() {
		unRegisterRefreshReceiver(this);
		super.onDestroy();
	}
	/**
	 * <br>
	 * 功能简述:加载Dock条数据 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void loadDock() {
		if (mShortCutSettingInfo != null) {

			// 快捷条行数
			String oldSelectValue = String.valueOf(mShortCutSettingInfo.mRows);
			DeskSettingSingleInfo singleInfo = mSettingDockRows
					.getDeskSettingInfo().getSingleInfo();
			if (singleInfo != null) {
				singleInfo.setSelectValue(oldSelectValue);
				mSettingDockRows.updateSumarryText();
			}
			// 快捷条循环切换
			mSettingDockChangeLoop
					.setIsCheck(mShortCutSettingInfo.mAutoRevolve);
		}
		if (FunctionPurchaseManager.getInstance(this).getPayFunctionState(
				FunctionPurchaseManager.PURCHASE_ITEM_FILTER) == FunctionPurchaseManager.STATE_CAN_USE) {
			mSettingSideDock.setImagePrimeVisibile(View.GONE);
		} else {
			mSettingSideDock.setImagePrimeVisibile(View.VISIBLE);
		}
	}

	@Override
	public boolean onValueChange(DeskSettingItemBaseView baseView, Object value) {
		boolean bRet = true;
		if (baseView == mSettingDockRows) {
			changeDockRows(baseView);
		}
		return bRet;
	}

	/**
	 * <br>
	 * 功能简述:修改Dock条数的显示 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param view
	 */
	public void changeDockRows(DeskSettingItemBaseView view) {
		if (view == mSettingDockRows) {
			mSettingDockRows.updateSumarryText();
		}
	}

	/**
	 * <br>
	 * 功能简述:保存Dock条设置 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void saveDock() {
		if (mShortCutSettingInfo != null) {
			boolean isChangeDockChangeLoop = false;
			// 快捷条循环切换
			if (mShortCutSettingInfo.mAutoRevolve != mSettingDockChangeLoop
					.getIsCheck()) {
				mShortCutSettingInfo.mAutoRevolve = mSettingDockChangeLoop
						.getIsCheck();
				isChangeDockChangeLoop = true;
			}

			// 快捷条行数
			boolean isChangeDockRows = false;
			int rowValue = Integer.parseInt(String.valueOf(mSettingDockRows
					.getSelectValue()));
			if (mShortCutSettingInfo.mRows != rowValue) {
				mShortCutSettingInfo.mRows = rowValue;
				isChangeDockRows = true;
			}
			// 全局设置保存
			if (isChangeDockChangeLoop || isChangeDockRows) {
				SettingProxy.updateShortCutSettingNonIndepenceTheme(mShortCutSettingInfo);
				if (isChangeDockRows) {
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
							IDockMsgId.DOCK_SETTING_CHANGED_ROW, -1, null, null);
				}
			}
		}
	}

	@Override
	public void save() {
		super.save();
		saveDeskSetting();
	}

	/**
	 * <br>
	 * 功能简述:保存桌面设置 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void saveDeskSetting() {
		saveDock();
		// 显示Dock条：判断当前状态是否等于数据库状态，然后在做完动画后才修改数据库
		if (ShortCutSettingInfo.sEnable != mSettingShowDock.getIsCheck()) {
			if (ShortCutSettingInfo.sEnable) { // 在DOCK条那里更改数据库
				// 参数1：做动画
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
						IDockMsgId.DOCK_HIDE, DockConstants.HIDE_ANIMATION_NO,
						null, null);
			} else {
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
						IDockMsgId.DOCK_SHOW, DockConstants.HIDE_ANIMATION, null,
						null);
			}
		}
	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
		return false;
	}

}
