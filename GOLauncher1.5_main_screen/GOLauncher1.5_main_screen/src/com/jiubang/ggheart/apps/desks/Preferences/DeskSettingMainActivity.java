package com.jiubang.ggheart.apps.desks.Preferences;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.gau.go.launcherex.R;
import com.go.proxy.DataChangeListenerProxy;
import com.go.proxy.VersionControl;
import com.go.util.androidsys.ClearDefaultIntent;
import com.go.util.device.Machine;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingNewMarkManager.NewMarkKeys;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogFactory;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogTypeId;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.IRequestCodeIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.MessageManager;
import com.jiubang.ggheart.apps.desks.diy.themescan.coupon.PromotionBean;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.data.info.MessageInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;
import com.jiubang.ggheart.data.statistics.Statistics;

/**
 * 
 * @author zouguiquan
 *
 */
public class DeskSettingMainActivity extends DeskSettingBaseActivity {

	private static final long CLICK_TIME = 400L;
	
	public static boolean sStopped = true;

	private long mLastClickTime;
	private ImageButton mPrimeButton;
	private BroadcastReceiver mRefreshReceiver;

	/**
	 * prime设置
	 */
	private DeskSettingItemBaseView mSettingPrime;
	/**
	 * 常用设置
	 */
	private DeskSettingItemBaseView mSettingCommon;
	/**
	 * 图标设置
	 */
	private DeskSettingItemBaseView mSettingIcon;
	/**
	 * 字体设置
	 */
	private DeskSettingItemBaseView mSettingFont;
	/**
	 * 屏幕设置
	 */
	private DeskSettingItemBaseView mSettingScreen;
	/**
	 * 功能表设置
	 */
	private DeskSettingItemBaseView mSettingAppDrawer;
	/**
	 * 背景设置
	 */
	private DeskSettingItemBaseView mSettingBackground;
	/**
	 * 快捷条设置
	 */
	private DeskSettingItemBaseView mSettingDock;
	/**
	 * 手势与特效设置
	 */
	private DeskSettingItemBaseView mSettingGuestureAndTransition;
	/**
	 * 高级设置
	 */
	private DeskSettingItemBaseView mSettingAdvanced;
	/**
	 * 关于GO桌面
	 */
	private DeskSettingItemBaseView mSettingAbout;
	/**
	 * 退出GO桌面
	 */
	private DeskSettingItemBaseView mSettingExit;
	
	private Handler mHandler;
	private static final int LOAD_FINISH = 1;
	
	private MessageManager mMsgManager;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.desk_setting_new_main);
		mMsgManager = MessageManager.getMessageManager(getApplicationContext());
		//针对亚太区的用户显示高级版设置项的 new 标识
		reOpenNewFlag();
		
		//prime设置
		mSettingPrime = (DeskSettingItemBaseView) findViewById(R.id.setting_prime);
		mSettingPrime.setBackgroundResource(R.drawable.desk_setting_item_single_selector);
		mPrimeButton = (ImageButton) findViewById(R.id.desksetting_update_prime);
		//如果是韩国地区或者非200渠道的，隐藏购买按钮
		if (FunctionPurchaseManager.getInstance(getApplicationContext()).getPayFunctionState(
				FunctionPurchaseManager.PURCHASE_ITEM_FULL) == FunctionPurchaseManager.STATE_GONE
				|| !Statistics.is200ChannelUid(this)) {
			
			mPrimeButton.setVisibility(View.GONE);
			mPrimeButton.setVisibility(View.GONE);
		} else {
			
			if (FunctionPurchaseManager.getInstance(getApplicationContext()).queryItemPurchaseState(
					FunctionPurchaseManager.PURCHASE_ITEM_FULL)) {
				
				mSettingPrime.setVisibility(View.GONE);
				mPrimeButton.setVisibility(View.VISIBLE);
				mPrimeButton.setImageResource(R.drawable.update_prime_highlight);
				mPrimeButton.setOnClickListener(this);
			} else {
				mPrimeButton.setVisibility(View.GONE);
				mSettingPrime.setVisibility(View.VISIBLE);
//				mSettingPrime.setImageDeskSettingPrimeVisibile(View.VISIBLE, R.drawable.desk_setting_pay_dialog_sale_img);
				mSettingPrime.setOnClickListener(this);
			}
		}
		
		mSettingCommon = (DeskSettingItemBaseView) findViewById(R.id.setting_common);
		mSettingCommon.setOpenIntent(new Intent(this, DeskSettingCommonActivity.class),
				IRequestCodeIds.REQUEST_CODE_COMMON_SETTING);

		//图标设置
		mSettingIcon = (DeskSettingItemBaseView) findViewById(R.id.setting_icon);
		mSettingIcon.setOpenIntent(new Intent(this, DeskSettingIconActivity.class));
		
		//字体设置
		mSettingFont = (DeskSettingItemBaseView) findViewById(R.id.setting_typeface);
		mSettingFont.setOpenIntent(new Intent(this, DeskSettingFontActivity.class),
				IRequestCodeIds.REQUEST_CODE_FONT_SETTING);

		//屏幕设置
		mSettingScreen = (DeskSettingItemBaseView) findViewById(R.id.setting_screen);
		mSettingScreen.setOpenIntent(new Intent(this, DeskSettingScreenActivity.class));

		//功能表设置
		mSettingAppDrawer = (DeskSettingItemBaseView) findViewById(R.id.setting_appdraw);
		mSettingAppDrawer.setOpenIntent(new Intent(this, DeskSettingAppDrawerActivity.class));

		//背景设置
		mSettingBackground = (DeskSettingItemBaseView) findViewById(R.id.setting_background);
		mSettingBackground.setOpenIntent(new Intent(this, DeskSettingBackgroundActivity.class));

		//快捷条设置
		mSettingDock = (DeskSettingItemBaseView) findViewById(R.id.setting_dock);
		mSettingDock.setOpenIntent(new Intent(this, DeskSettingDockActivity.class));

		//手势与特效设置
		mSettingGuestureAndTransition = (DeskSettingItemBaseView) findViewById(R.id.setting_gesture_and_transition);
		mSettingGuestureAndTransition.setOpenIntent(new Intent(this, DeskSettingGesAndTranActivity.class));

		//高级设置
		mSettingAdvanced = (DeskSettingItemBaseView) findViewById(R.id.setting_advanced);
		mSettingAdvanced.setOpenIntent(new Intent(this, DeskSettingAdvancedActivity.class),
				IRequestCodeIds.REQUEST_CODE_ADVANCED_SETTING);

		//关于GO桌面
		mSettingAbout = (DeskSettingItemBaseView) findViewById(R.id.setting_about);
		mSettingAbout.setOpenIntent(new Intent(this, DeskSettingAboutActivity.class));

		//退出GO桌面
		mSettingExit = (DeskSettingItemBaseView) findViewById(R.id.setting_exit);
		mSettingExit.setOnClickListener(this);

		load();
		registeRefreshReceiver(this);
		
		//获取是否需要促销
		initHandler();
		startGetMessageBeanList();
	}
	
	private void registeRefreshReceiver(Context context) {
		mRefreshReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context2, Intent intent) {
				if (intent != null) {
					if (FunctionPurchaseManager.getInstance(getApplicationContext())
							.queryItemPurchaseState(FunctionPurchaseManager.PURCHASE_ITEM_FULL)) {
						mPrimeButton.setVisibility(View.VISIBLE);
						mPrimeButton.setImageResource(R.drawable.update_prime_highlight);
						mSettingPrime.setVisibility(View.GONE);
					}
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(DeskSettingUtils.ACTION_HAD_PAY_REFRESH);
		filter.setPriority(Integer.MAX_VALUE);
		context.registerReceiver(mRefreshReceiver, filter);
	}

	private void initHandler() {
	    mHandler = new Handler() { 
	      @Override
	    public void handleMessage(Message msg) {
	          switch (msg.what) {
	              case LOAD_FINISH :
	                  boolean isPromotion = (Boolean) msg.obj;

                      if (isPromotion) {
                          // 正在促销
                          mSettingPrime.setImageDeskSettingPrimeVisibile(View.VISIBLE, R.drawable.desk_setting_pay_dialog_btn_sale_img);
                      }
	                  
	                  
	                  break;
	          }
	    }  
	    };
	}
	
	private void startGetMessageBeanList() {

	    new Thread(new Runnable() {
            
            @Override
            public void run() {
                ArrayList<MessageInfo> msgList = mMsgManager.getPromotionMessageInfo();
                ArrayList<PromotionBean> promotionList = mMsgManager.getPromotionList(msgList);
                boolean isPromotion = mMsgManager.isPromotionPeriod(promotionList);
                
                //更新UI
                Message msg = mHandler.obtainMessage(LOAD_FINISH);
                msg.obj = isPromotion;
                mHandler.sendMessage(msg);
            }
        }).start();
	    
        
        
    }

	@Override
	public void onClick(View v) {
		long curTime = System.currentTimeMillis();
		if (curTime - mLastClickTime < CLICK_TIME) {
			return;
		}
		mLastClickTime = curTime;

		switch (v.getId()) {
			// 退出桌面
			case R.id.setting_exit :
				if (isDefault()) {
					showClearDefaultDialog(true); // 显示清除默认桌面提示对话框
				} else {
					sendExit(false);
				}
				GuiThemeStatistics
						.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.PREFERENCES_EGL);
				break;
				
			case R.id.setting_prime :
				DeskSettingUtils.showPayDialog(this, 101);
				GuiThemeStatistics
						.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.PREFERENCES_PRIME);
				break;
			case R.id.desksetting_update_prime:
//				TODO GOTO PAY DIALOG 钻石点击事件
				DeskSettingUtils.showHadPayDialog(this);
				break;

			default :
				break;
		}
	}
	
	/**
	 * <br>
	 * 功能简述:判断是否默认使用GO桌面 <br>
	 * 功能详细描述:点击HOME键弹出的默认使用此应用 <br>
	 * 注意:
	 * 
	 * @return
	 */
	private boolean isDefault() {
		PackageManager pm = this.getPackageManager();
		boolean isDefault = false;
		List<ComponentName> prefActList = new ArrayList<ComponentName>();
		// Intent list cannot be null. so pass empty list
		List<IntentFilter> intentList = new ArrayList<IntentFilter>();
		pm.getPreferredActivities(intentList, prefActList, null);
		if (0 != prefActList.size()) {
			for (int i = 0; i < prefActList.size(); i++) {
				if (this.getPackageName().equals(
						prefActList.get(i).getPackageName())) {
					isDefault = true;
					break;
				}
			}
		}
		return isDefault;
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
	 * 功能简述:清除默认使用GO桌面 <br>
	 * 功能详细描述:点击HOME键弹出的默认使用此应用 <br>
	 * 注意:
	 */
	private void clearDefault() {
		ClearDefaultIntent.clearCurrentPkgDefault(this);
	}

	private void reOpenNewFlag() {
		if (Machine.isPurchaseByGetjarContury(getApplicationContext())) {
			PreferencesManager ps = new PreferencesManager(getApplicationContext());
			int lastVersionCode = ps.getInt(IPreferencesIds.PREFERENCE_LAST_VERSION_CODE, -1);
			int currentVersionCode = VersionControl.getCurrentVersionCode();
			
			if (lastVersionCode >= 0
					&& lastVersionCode < DeskSettingUtils.REOPEN_NEW_FLAG_VERSION_CODE
					&& currentVersionCode >= DeskSettingUtils.REOPEN_NEW_FLAG_VERSION_CODE) {
				DeskSettingUtils.reOpenNewFlag(getApplicationContext());
				ps.putInt(IPreferencesIds.PREFERENCE_LAST_VERSION_CODE, DeskSettingUtils.REOPEN_NEW_FLAG_VERSION_CODE);
				ps.commit();
			}
		}
	}

	private void sendExit(boolean restart) {
		Intent localIntent = getIntent();
		Bundle localBundle = new Bundle();
		localBundle.putInt("exit", 1);
		localIntent.putExtras(localBundle);
		setResult(-1, localIntent);
		finish();
		DataChangeListenerProxy.getInstance().exit(restart);
	}

	private void unRegisterRefreshReceiver(Context paramContext) {
		try {
			paramContext.unregisterReceiver(mRefreshReceiver);
			return;
		} catch (Exception localException) {
			Log.e(localException.toString(), localException.getMessage());
		}
	}

	protected void onDestroy() {
		unRegisterRefreshReceiver(this);
		DeskSettingNewMarkManager.destroy();
		super.onDestroy();
	}

	protected void onResume() {
		sStopped = false;
		super.onResume();
	}

	protected void onStop() {
		sStopped = true;
		super.onStop();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
			case DeskSettingConstants.RESULT_CODE_RESTART_GO_LAUNCHER :
				sendExit(true);
				break;
			case DeskSettingConstants.RESULT_CODE_EXIT_GO_LAUNCHER :
				sendExit(false);
				break;
			default :
				break;
		}
	}
	
	@Override
	protected void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		if (newMarkManager.isShowNew(NewMarkKeys.COMMON, false)) {
			mSettingCommon.setImageNewVisibile(View.VISIBLE);
		} else {
			mSettingCommon.setImageNewVisibile(View.GONE);
		}
		if (newMarkManager.isShowNew(NewMarkKeys.ICON, false)) {
			mSettingIcon.setImageNewVisibile(View.VISIBLE);
		} else {
			mSettingIcon.setImageNewVisibile(View.GONE);
		}
		if (newMarkManager.isShowNew(NewMarkKeys.BACKGROUND, false)) {
			mSettingBackground.setImageNewVisibile(View.VISIBLE);
		} else {
			mSettingBackground.setImageNewVisibile(View.GONE);
		}
		if (newMarkManager.isShowNew(NewMarkKeys.DOCK, false)) {
			mSettingDock.setImageNewVisibile(View.VISIBLE);
		} else {
			mSettingDock.setImageNewVisibile(View.GONE);
		}
		if (newMarkManager.isShowNew(NewMarkKeys.GESTURE_TRANSITION, false)) {
			mSettingGuestureAndTransition.setImageNewVisibile(View.VISIBLE);
		} else {
			mSettingGuestureAndTransition.setImageNewVisibile(View.GONE);
		}
	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
		return false;
	}
}