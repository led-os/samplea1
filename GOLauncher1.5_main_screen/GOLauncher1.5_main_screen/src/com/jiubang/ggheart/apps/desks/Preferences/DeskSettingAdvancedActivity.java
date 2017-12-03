package com.jiubang.ggheart.apps.desks.Preferences;

import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.util.device.Machine;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IDockMsgId;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.base.component.AppsDetail;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogLanguageChoice;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemCheckBoxView;
import com.jiubang.ggheart.apps.desks.diy.IRequestCodeIds;
import com.jiubang.ggheart.apps.desks.diy.WallpaperControler;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.common.password.LockPatternUtils;
import com.jiubang.ggheart.data.AppService;
import com.jiubang.ggheart.data.info.ThemeSettingInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;
import com.jiubang.ggheart.data.statistics.StatisticsData;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 
 * <br>
 * 类描述:桌面设置-高级设置Activity <br>
 * 功能详细描述:
 * 
 * @author licanhui
 * @date [2012-9-10]
 */
public class DeskSettingAdvancedActivity extends DeskSettingBaseActivity {
	
	private ThemeSettingInfo mThemeSettingInfo;

	/**
	 * 支持透明通知栏
	 */
	private DeskSettingItemCheckBoxView mSettingTransparentStatusbar;

	/**
	 * 显示通知栏背景
	 */
	private DeskSettingItemCheckBoxView mShowStatusbarBg;
	
	/**
	 * 常驻内存
	 */
	private DeskSettingItemCheckBoxView mSettingPermanentMemory;

	/**
	 * 阻止强制关闭
	 */
	private DeskSettingItemCheckBoxView mSettingPreventFC;

	/**
	 * 无广告
	 */
//	private DeskSettingItemCheckBoxView mSettingNoAdvert;

	/**
	 * 检查垃圾数据
	 */
	private DeskSettingItemBaseView mSettingCleanDirtyData;

	/**
	 * Debug模式
	 */
//	private DeskSettingItemBaseView mSettingDebugMode;

	/**
	 * 语言设置
	 */
//	private DeskSettingItemBaseView mSettingLanguage;

	/**
	 * 桌面搬家
	 */
	private DeskSettingItemBaseView mSettingDeskMigrate;
	/**
	 * 应用管理插件
	 */
//	private DeskSettingItemBaseView mMediaPlugin;

	/**
	 *  安全锁
	 */
//	private DeskSettingItemBaseView mSettingLock;

	private long mLastClickTime; // 最后一次点击时间
	private static final long CLICK_TIME = 400; // 每次点击间隔时间

	public static final int REQUEST_CODE_LOCK = 10;
	private int mEntranceid = 7;
	// onex升级向导
	private DeskSettingItemBaseView mOneXGuid;
	private BroadcastReceiver mRefreshReceiver;
	private DeskSettingItemBaseView mSettingBackup;
	private DialogLanguageChoice mLanguageDialog;

	private boolean mIndicatorForMemoeryOffFromNotifiaation;
	public static final String ISCOMEFROMNOTIFICATION = "is_come_from_notification";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.desk_setting_advanced);

		mThemeSettingInfo = SettingProxy.getThemeSettingInfo();
		mEntranceid = getIntent().getIntExtra(DeskSettingAdvancedPayActivity.ENTRANCE_ID, 7);
		initViews();
		load();
		registeRefreshReceiver(this);
		mIndicatorForMemoeryOffFromNotifiaation = false;
		Bundle myBundel = this.getIntent().getExtras();
		
		if (myBundel != null) {
			if (myBundel.getBoolean(ISCOMEFROMNOTIFICATION)) {
				mIndicatorForMemoeryOffFromNotifiaation = true;
				showPermanentMemoryDialogNew();
			}
		}
		GuiThemeStatistics
				.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.PREFERENCES_AS);
	}

	/**
	 * <br>
	 * 功能简述:初始化View <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void initViews() {
		mSettingTransparentStatusbar = (DeskSettingItemCheckBoxView) findViewById(R.id.transparent_statusbar);
		mSettingTransparentStatusbar.setSummaryText(R.string.transparent_statusbar_summary);
		mSettingTransparentStatusbar.setOnValueChangeListener(this);
		if (Machine.IS_SDK_ABOVE_KITKAT) {
			mSettingTransparentStatusbar.setVisibility(View.GONE);
		}
		mShowStatusbarBg = (DeskSettingItemCheckBoxView) findViewById(R.id.show_statusbar_bg);
		mShowStatusbarBg.setOnValueChangeListener(this);
		updateShowStatusbarBgStatus();
		mSettingPermanentMemory = (DeskSettingItemCheckBoxView) findViewById(R.id.permanentmemory_enable);
		mSettingPermanentMemory.setOnValueChangeListener(this);

		mSettingPreventFC = (DeskSettingItemCheckBoxView) findViewById(R.id.prevent_force_close);
		mSettingPreventFC.setOnValueChangeListener(this);

//		mSettingNoAdvert = (DeskSettingItemCheckBoxView) findViewById(R.id.no_advert);
//		mSettingNoAdvert.setOnValueChangeListener(this);
//		//非200渠道屏蔽
//		if (!Statistics.is200ChannelUid(this)) {
//			mSettingNoAdvert.setVisibility(View.GONE);
//		} else {
//			mSettingNoAdvert.setVisibility(View.VISIBLE);
//		}

		mSettingCleanDirtyData = (DeskSettingItemBaseView) findViewById(R.id.clean_dirty_data);
		mSettingCleanDirtyData.setOnClickListener(this);
		
//		mSettingDebugMode = (DeskSettingItemBaseView) findViewById(R.id.golauncher_debug_mode);
//		mSettingDebugMode.setOnClickListener(this);
//
//		mSettingLanguage = (DeskSettingItemBaseView) findViewById(R.id.language_setting);
//		mSettingLanguage.setOnClickListener(this);

		mSettingDeskMigrate = (DeskSettingItemBaseView) findViewById(R.id.migrate_app_tip_title);
		mSettingDeskMigrate.setOnClickListener(this);

//		mOneXGuid = (DeskSettingItemBaseView) findViewById(R.id.onexguide);
//		if (Machine.isONE_X() && !Machine.IS_ICS_MR1) {
//			mOneXGuid.setOnClickListener(this);
//		} else {
//			mOneXGuid.setVisibility(View.GONE);
//		}
//		//资源管理插件
//		mMediaPlugin = (DeskSettingItemBaseView) findViewById(R.id.fun_app_ui_media_plugin);
//		mMediaPlugin.setOnClickListener(this);
//
//		mSettingLock = (DeskSettingItemBaseView) findViewById(R.id.lock_setting);
//		mSettingLock.setOnClickListener(this);

		// ======其他设置=====
		// 恢复&备份
		mSettingBackup = (DeskSettingItemBaseView) findViewById(R.id.setting_backup);
		mSettingBackup.setOnClickListener(this);
		
	}

	@Override
	public void load() {
		super.load();
		if (mThemeSettingInfo != null) {
			mSettingTransparentStatusbar.setIsCheck(mThemeSettingInfo.mTransparentStatusbar);
			mShowStatusbarBg.setIsCheck(mThemeSettingInfo.mIsShowStatusbarBg);
			updateShowStatusbarBgStatus();
			mSettingPermanentMemory.setIsCheck(mThemeSettingInfo.mIsPemanentMemory);
			mSettingPreventFC.setIsCheck(mThemeSettingInfo.mPreventForceClose);
			if (FunctionPurchaseManager.getInstance(getApplicationContext()).isItemCanUse(
					FunctionPurchaseManager.PURCHASE_ITEM_AD)) {
//				mSettingNoAdvert.setIsCheck(mThemeSettingInfo.mNoAdvert);
			}
		}

		//是否已经打开过，第一次打开就显示new标志,去广告
//		if (DeskSettingUtils.isFristShowNoAdvertTip(this, true)) {
//			mSettingNoAdvert.setImageNewVisibile(View.VISIBLE);
//		}
//
//		if (DeskSettingUtils.isFristShowLockTip(this, true)) {
//			mSettingLock.setImageNewVisibile(View.VISIBLE);
//		}

		// cn包需要去掉去广告功能
//		if (!Statistics.is200ChannelUid(this)) {
//			mSettingNoAdvert.setVisibility(View.GONE);
//		}
//		if (FunctionPurchaseManager.getInstance(getApplicationContext()).getPayFunctionState(
//				FunctionPurchaseManager.PURCHASE_ITEM_AD) == FunctionPurchaseManager.STATE_GONE) {
//			mSettingNoAdvert.setVisibility(View.GONE);
//			mSettingLock.setVisibility(View.GONE);
//		}
//		//判断是否付费
//		if (FunctionPurchaseManager.getInstance(getApplicationContext()).getPayFunctionState(
//				FunctionPurchaseManager.PURCHASE_ITEM_AD) == FunctionPurchaseManager.STATE_CAN_USE) {
//			mSettingNoAdvert.setImagePrimeVisibile(View.GONE);
//		} else {
//			mSettingNoAdvert.setImagePrimeVisibile(View.VISIBLE);
//
//		}
//		//判断是否付费
//		if (FunctionPurchaseManager.getInstance(getApplicationContext()).getPayFunctionState(
//				FunctionPurchaseManager.PURCHASE_ITEM_SECURITY) == FunctionPurchaseManager.STATE_CAN_USE) {
//			mSettingLock.setImagePrimeVisibile(View.GONE);
//		} else {
//			mSettingLock.setImagePrimeVisibile(View.VISIBLE);
//			
//		}

	}

	
	@Override
	public void save() {
		super.save();
		if (mThemeSettingInfo != null) {
			boolean isChangeTheme = false;
			boolean broadCast = false;
			// 透明通知栏的设置发生变化时苏要即时通知wallpaperControler更新，所以在退出设置的activity时不需要广播通知更新
			if (mThemeSettingInfo.mTransparentStatusbar != mSettingTransparentStatusbar
					.getIsCheck()) {
				mThemeSettingInfo.mTransparentStatusbar = mSettingTransparentStatusbar.getIsCheck();
				isChangeTheme = true;
			}

			if (mThemeSettingInfo.mIsShowStatusbarBg != mShowStatusbarBg.getIsCheck()) {
				mThemeSettingInfo.mIsShowStatusbarBg = mShowStatusbarBg.getIsCheck();
				isChangeTheme = true;
			}
			
			if (mThemeSettingInfo.mIsPemanentMemory != mSettingPermanentMemory.getIsCheck() || mIndicatorForMemoeryOffFromNotifiaation) {
				mThemeSettingInfo.mIsPemanentMemory = mSettingPermanentMemory.getIsCheck();
				isChangeTheme = true;
				broadCast = true;
			}

			if (mThemeSettingInfo.mPreventForceClose != mSettingPreventFC.getIsCheck()) {
				mThemeSettingInfo.mPreventForceClose = mSettingPreventFC.getIsCheck();
				isChangeTheme = true;
				broadCast = true;
			}

//			if (mThemeSettingInfo.mNoAdvert != mSettingNoAdvert.getIsCheck()) {
//				mThemeSettingInfo.mNoAdvert = mSettingNoAdvert.getIsCheck();
//				isChangeTheme = true;
//				broadCast = true;
//			}

			if (isChangeTheme) {
				SettingProxy.updateThemeSettingInfo2(mThemeSettingInfo,
						broadCast);
			}
		}
	}

	@Override
	public boolean onValueChange(DeskSettingItemBaseView baseView, Object value) {
		// 支持透明通知栏，透明通知栏的设置发生变化时苏要即时通知wallpaperControler更新，否则在快速退出设置界面的时候，3D桌面的背景会是黑色
		if (baseView == mSettingTransparentStatusbar) {
			if (mSettingTransparentStatusbar.getIsCheck()) {
				showTransparentStatusbarDialog();
			} else {
				WallpaperControler wpc = WallpaperControler.getInstance();
				if (wpc != null) {
					wpc.setTransparentStatusbarSupport(false);
				}
				updateShowStatusbarBgStatus();
			}
		}

		// 常驻内存
		if (baseView == mSettingPermanentMemory) {
			showPermanentMemoryDialog();
		}

		// 去广告
//		if (baseView == mSettingNoAdvert) {
//			//判断是否付费 // 为什么去广告这个功能可以不用判断韩国版和CN包？？？
//			FunctionPurchaseManager purchaseManager = FunctionPurchaseManager
//					.getInstance(getApplicationContext());
//			boolean hasPay = purchaseManager.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_AD);
//			if (!hasPay) {
//				mSettingNoAdvert.setIsCheck(false);
//				if (GoAppUtils.isAppExist(this, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
//					FunctionPurchaseManager.getInstance(getApplicationContext()).startItemPayPage(
//							mEntranceid + "");
//				} else {
//					DeskSettingUtils.showPayDialog(this, 201); //显示付费对话框
//				}
//			}
//			mSettingNoAdvert.setImageNewVisibile(View.GONE);
//		}

		return true;
	}

	/**
	 * <br>
	 * 功能简述:支持透明通知栏提示对话框 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	private void showTransparentStatusbarDialog() {
		DialogConfirm mTransparentStatusbarDialog = new DialogConfirm(this);
		mTransparentStatusbarDialog.show();
		mTransparentStatusbarDialog.setTitle(R.string.tran_statusbar_dialog_title);
		mTransparentStatusbarDialog.setMessage(getString(R.string.tran_statusbar_dialog_content));
		mTransparentStatusbarDialog.setNegativeButton(null, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mSettingTransparentStatusbar.setIsCheck(false);
				updateShowStatusbarBgStatus();
			}
		});

		mTransparentStatusbarDialog.setPositiveButton(null, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 透明通知栏的设置发生变化时苏要即时通知wallpaperControler更新，否则在快速退出设置界面的时候，3D桌面的背景会是黑色
				WallpaperControler wpc = WallpaperControler.getInstance();
				if (wpc != null) {
					wpc.setTransparentStatusbarSupport(true);
					updateShowStatusbarBgStatus();
				}
			}
		});

		mTransparentStatusbarDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				mSettingTransparentStatusbar.setIsCheck(false);
				updateShowStatusbarBgStatus();
				dialog.dismiss();
				return false;
			}
		});

	}

	/**
	 * <br>
	 * 功能简述:常驻内存提示对话框 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	private void showPermanentMemoryDialog() {
		DialogConfirm mPermanentMemoryDialog = new DialogConfirm(this);
		mPermanentMemoryDialog.show();
		mPermanentMemoryDialog.setTitle(getString(R.string.setSystemPersistentTitle));
		mPermanentMemoryDialog.setMessage(getString(R.string.setSystemPersistentRestart));
		mPermanentMemoryDialog.setPositiveButton(R.string.reboot_right_now,
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
//						save();
						Intent intent = new Intent();
						intent.setClass(ApplicationProxy.getContext(), AppService.class);
						ApplicationProxy.getContext().getApplicationContext().stopService(intent);
						exitAndRestart();
					}
				});
		mPermanentMemoryDialog.setNegativeButton(R.string.reboot_next_time, null);
	}

	/**
	 * <br>
	 * 功能简述:常驻内存提示对话框 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	private void showPermanentMemoryDialogNew() {
		DialogConfirm mPermanentMemoryDialog = new DialogConfirm(this);
		mPermanentMemoryDialog.show();
		mPermanentMemoryDialog.setTitle(getString(R.string.setSystemPersistentTitle));
		mPermanentMemoryDialog.setMessage(getString(R.string.permanentmemory_enablesummary_dialog));
		mPermanentMemoryDialog.setPositiveButton(R.string.ok, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mThemeSettingInfo.mIsPemanentMemory = false;
				mSettingPermanentMemory.setIsCheck(mThemeSettingInfo.mIsPemanentMemory);
//				save();
				Intent intent = new Intent();
				intent.setClass(ApplicationProxy.getContext(), AppService.class);
				ApplicationProxy.getContext().getApplicationContext().stopService(intent);
				exitAndRestart();
			}
		});
		mPermanentMemoryDialog.setNegativeButton(R.string.cancel, null);
	}

	@Override
	public void onClick(View v) {
		// 处理快速点击的BUG
		long curTime = System.currentTimeMillis();
		if (curTime - mLastClickTime < CLICK_TIME) {
			return;
		}
		mLastClickTime = curTime;
		switch (v.getId()) {
		// 检查垃圾数据
			case R.id.clean_dirty_data :
				StatisticsData.saveUseRecordPreferences(this, StatisticsData.CLEANDIRTYDATA_ITEM); // 统计
				checkDirtyData();
				break;
				// 进入debug模式
//			case R.id.golauncher_debug_mode :
//				DebugService.startDebugLogcat(GoLauncher.getContext(), R.drawable.icon);
//				break;

			// 语言设置
//			case R.id.language_setting :
//				showInstallLanguageTip(this);
//				break;

			// 桌面搬家
			case R.id.migrate_app_tip_title :
				StatisticsData.saveUseRecordPreferences(this, StatisticsData.DESKMIGRATE_ITEM); // 统计
				deskMigrate(); // 桌面搬家
				break;

			// oneX
//			case R.id.onexguide :
//				oneXGuide();
//				break;
			//资源管理插件	
//			case R.id.fun_app_ui_media_plugin :
//				if (MediaPluginFactory.isMediaPluginExist(getApplicationContext())) {
//				    Intent intent = new Intent();
//				    intent.setClassName(this, IGoLauncherClassName.GOLAUNCHER_ACTIVITY);
//					startActivity(intent);
//					switch (AppFuncContentTypes.sType_for_setting) {
//						case AppFuncContentTypes.IMAGE :
//							SwitchControler.getInstance(ApplicationProxy.getContext())
//									.showMediaManagementImageContent();
//							break;
//						case AppFuncContentTypes.MUSIC :
//							SwitchControler.getInstance(ApplicationProxy.getContext())
//									.showMediaManagementMusicContent();
//							break;
//						case AppFuncContentTypes.VIDEO :
//							SwitchControler.getInstance(ApplicationProxy.getContext())
//									.showMediaManagementVideoContent();
//							break;
//						default :
//							SwitchControler.getInstance(ApplicationProxy.getContext())
//									.showMediaManagementImageContent();
//							break;
//					}
//				} else {
//					final Context context = ApplicationProxy.getContext();
//					String textFirst = context
//							.getString(R.string.download_mediamanagement_plugin_dialog_text_first);
//					String textMiddle = context
//							.getString(R.string.download_mediamanagement_plugin_dialog_text_middle);
//					String textLast = context
//							.getString(R.string.download_mediamanagement_plugin_dialog_text_last);
//					SpannableStringBuilder messageText = new SpannableStringBuilder(textFirst
//							+ textMiddle + textLast);
//					messageText.setSpan(new RelativeSizeSpan(0.8f), textFirst.length(),
//							messageText.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//					messageText.setSpan(
//							new ForegroundColorSpan(context.getResources().getColor(
//									R.color.snapshot_tutorial_notice_color)), textFirst.length(),
//							textFirst.length() + textMiddle.length(),
//							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置提示为绿色
//
//					DialogConfirm dialog = new DialogConfirm(this);
//					dialog.show();
//					dialog.setTitle(R.string.download_mediamanagement_plugin_dialog_title);
//					dialog.setMessage(messageText);
//					dialog.setPositiveButton(
//							R.string.download_mediamanagement_plugin_dialog_download_btn_text,
//							new View.OnClickListener() {
//
//								@Override
//								public void onClick(View v) {
//									// 跳转进行下载
//									Context context = ApplicationProxy.getContext();
//									String packageName = PackageName.MEDIA_PLUGIN;
//									String url = LauncherEnv.Url.MEDIA_PLUGIN_FTP_URL; // 插件包ftp地址
//									String linkArray[] = { packageName, url };
//									String title = context
//											.getString(R.string.mediamanagement_plugin_download_title);
//									boolean isCnUser = GoAppUtils.isCnUser(context);
//
//									CheckApplication.downloadAppFromMarketFTPGostore(context, "",
//											linkArray, LauncherEnv.GOLAUNCHER_GOOGLE_REFERRAL_LINK,
//											title, System.currentTimeMillis(), isCnUser,
//											CheckApplication.FROM_MEDIA_DOWNLOAD_DIGLOG, 0, null);
//								}
//							});
//					dialog.setNegativeButton(
//							R.string.download_mediamanagement_plugin_dialog_later_btn_text, null);
//
//				}
//				break;
			case R.id.setting_backup:
				startActivityForResult(new Intent(this,
						DeskSettingBackupActivity.class),
						IRequestCodeIds.REQUEST_CODE_BACKUP_SETTING);
				
				break;
//			case R.id.lock_setting :
//
//				mSettingLock.setImageNewVisibile(View.GONE);
//
//				//判断是否付费
////				if (FunctionPurchaseManager.getInstance(getApplicationContext())
////						.getPayFunctionState(FunctionPurchaseManager.PURCHASE_ITEM_SECURITY) == FunctionPurchaseManager.STATE_CAN_USE) {
//					//TODO:付费
//					InvokeLockControler controler = InvokeLockControler
//							.getInstance(getApplicationContext());
//					controler.startLockAction(
//							-1,
//							new ActionResultCallBack() {
//
//								@Override
//								public void onUnlockSuccess(int actionId) {
//									// TODO Auto-generated method stub
//									Intent intent = new Intent(getApplicationContext(),
//											DeskSettingLockActivity.class);
//									intent.putExtra(DeskSettingAdvancedPayActivity.ENTRANCE_ID, mEntranceid);
//									startActivityForResult(intent, REQUEST_CODE_LOCK);
//								}
//
//								@Override
//								public void onUnlockFail(int actionId) {
//									// TODO Auto-generated method stub
//
//								}
//							}, DeskSettingAdvancedActivity.this, R.drawable.safe_lock_icon,
//							DeskSettingAdvancedActivity.this
//									.getString(R.string.desksetting_security_lock));

//				} else {
//					DeskSettingUtils.showPayDialog(this, 501); //显示付费对话框
//				}

//				break;
			default :
				break;
		}

	}

	/**
	 * <br>
	 * 功能简述:检查是否存在垃圾数据 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	synchronized void checkDirtyData() {
		boolean isScreenDirty = MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
				ICommonMsgId.IS_EXIST_TRASH_DATA, -1, null, null);

		boolean dockDirty = MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
				IDockMsgId.IS_EXIST_DOCK_TRASH_DATA, -1, null, null);

		boolean funcDirty = MsgMgrProxy.sendMessage(this, IDiyFrameIds.APP_DRAWER,
				ICommonMsgId.IS_EXIST_TRASH_DATA, -1, null, null);

		if (isScreenDirty || dockDirty || funcDirty) {
			showCleanListDialog(isScreenDirty, dockDirty, funcDirty);
		} else {
			showNoDirtyDataDialog();
		}
	}

	/**
	 * <br>
	 * 功能简述:没有垃圾数据提示对话框 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	private void showNoDirtyDataDialog() {
		DialogConfirm mNoDirtyDataDialog = new DialogConfirm(this);
		mNoDirtyDataDialog.show();
		mNoDirtyDataDialog.setTitle(getString(R.string.clean_dirty_data));
		mNoDirtyDataDialog.setMessage(getString(R.string.no_dirty_data));
		mNoDirtyDataDialog.setNegativeButtonVisible(View.GONE);

	}

	/**
	 * <br>
	 * 功能简述:清除垃圾数据列表对话框 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	private void showCleanListDialog(final boolean screenDirty, final boolean dockDirty,
			final boolean funcDirty) {
		StringBuffer messageBuffer = new StringBuffer();
		if (screenDirty) {
			messageBuffer.append(getString(R.string.desk_setting_tab_title_desk));
		}

		if (dockDirty) {
			if (messageBuffer.length() != 0) {
				messageBuffer.append("、");
			}
			messageBuffer.append(getString(R.string.desk_setting_tab_title_dock));
		}
		if (funcDirty) {
			if (messageBuffer.length() != 0) {
				messageBuffer.append("、");
			}
			messageBuffer.append(getString(R.string.desk_setting_tab_title_function));
		}
		String messageString = String.format(this.getString(R.string.desk_setting_have_dirty_data),
				messageBuffer);

		DialogConfirm mCleanListDialog = new DialogConfirm(this);
		mCleanListDialog.show();
		mCleanListDialog.setTitle(getString(R.string.clean_dirty_data));
		mCleanListDialog.setMessage(messageString);
		mCleanListDialog.setPositiveButton(null, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					boolean reboot = false;
					if (screenDirty) {
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
								ICommonMsgId.CLEAN_TRASH_DATA, -1, null, null);
						reboot = true;
					}

					if (dockDirty) {
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
								ICommonMsgId.CLEAN_TRASH_DATA, -1, null, null);
						reboot = true;
					}

					if (funcDirty) {
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.APP_DRAWER,
								ICommonMsgId.CLEAN_TRASH_DATA, -1, null, null);
						reboot = true;
					}

					// 重启桌面
					if (reboot) {
						exitAndRestart();
					}
				} catch (Exception e) {
				}
			}
		});
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
		mLanguageDialog = new DialogLanguageChoice(this);
		mLanguageDialog.show();
	}

	/**
	 * <br>
	 * 功能简述:桌面搬家 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void deskMigrate() {
		StatisticsData.saveUseRecordPreferences(this, StatisticsData.DESKMIGRATE_ITEM);
		if (GoAppUtils.isAppExist(this, PackageName.DESKMIGRATE_PACKAGE_NAME)) {
			Intent intent = new Intent("com.ma.deskmigrate.DeskMigrate");
			Bundle bundle = new Bundle();
			bundle.putInt("code", IRequestCodeIds.REQUEST_MIGRATE_DESK);
			intent.putExtras(bundle);
			try {
				startActivity(intent);
				finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			//			Intent it = new Intent();
			//			it.setClass(this, ItemDetailActivity.class);
			//			it.putExtra("pkgname", LauncherEnv.Plugin.DESKMIGRATE_PACKAGE_NAME);
			//			startActivity(it);
			//			Intent it = new Intent();
			//			it.setClass(this, AppDetailActivity.class);
			//			it.putExtra("pkgname", LauncherEnv.Plugin.DESKMIGRATE_PACKAGE_NAME);
			//			startActivity(it);
			AppsDetail.gotoDetailDirectly(this, AppsDetail.START_TYPE_APPRECOMMENDED,
					PackageName.DESKMIGRATE_PACKAGE_NAME);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mLanguageDialog != null && mLanguageDialog.isShowing()) {
			showInstallLanguageTip(this);
		}
	}

	private void oneXGuide() {
		String url = "http://golauncher.goforandroid.com/2012/10/htc-one-xs-update-guide/";
		if (Locale.getDefault().getLanguage().equals("zh")) {
			url = "http://golauncher.goforandroid.com/zh/2012/10/htc-one-xs-update-guide/";
		}
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		saveStartActivity(intent);
	}

	public void saveStartActivity(Intent intent) {
		try {
			super.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
//						mSettingNoAdvert.setIsCheck(true);
//						mSettingNoAdvert.setImagePrimeVisibile(View.GONE);
					}
					 hasPay = purchaseManager
								.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_SECURITY);
					 if (hasPay) {
//						 mSettingLock.setImagePrimeVisibile(View.GONE);
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
		unRegisterRefreshReceiver(this);
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == LockPatternUtils.REQUEST_SAVE_PASSWORD_OK) {
			startActivityForResult(new Intent(getApplicationContext(),
					DeskSettingLockActivity.class), REQUEST_CODE_LOCK);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		// TODO Auto-generated method stub
		
	}
	
	private void updateShowStatusbarBgStatus() {
		if (Machine.isSupportAPITransparentStatusBar()) {
			if (mShowStatusbarBg.getVisibility() != View.VISIBLE) {
				mShowStatusbarBg.setVisibility(View.VISIBLE);
}
			if (mSettingTransparentStatusbar.getIsCheck()) {
				mShowStatusbarBg.setEnabled(true);
				mShowStatusbarBg.updateEnabledStatus(true);
			} else {
				mShowStatusbarBg.setEnabled(false);
				mShowStatusbarBg.updateEnabledStatus(false);
			}
		} else {
			if (mShowStatusbarBg.getVisibility() != View.GONE) {
				mShowStatusbarBg.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
		return false;
	}
	
}
