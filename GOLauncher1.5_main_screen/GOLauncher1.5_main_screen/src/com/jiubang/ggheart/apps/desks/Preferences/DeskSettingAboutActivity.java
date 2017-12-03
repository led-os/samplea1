package com.jiubang.ggheart.apps.desks.Preferences;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.VersionControl;
import com.go.util.AppUtils;
import com.go.util.device.Machine;
import com.go.util.market.MarketConstant;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.base.component.AppsDetail;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.InputDialog;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.apps.desks.purchase.PromotionsControl;
import com.jiubang.ggheart.apps.desks.purchase.PromotionsControl.IActiveListener;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStoreAppInforUtil;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;

/**
 * 
 * <br>类描述:桌面设置-关于GO桌面Activity
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2012-9-25]
 */
public class DeskSettingAboutActivity extends DeskSettingBaseActivity implements OnClickListener {
	
	public static final String DOWNLOAD_200 = "http://goo.gl/R6Vml"; // 200渠道的下載地址
	public static final String DOWNLOAD_UN_200 = "http://t.cn/zW0DwGX"; // 非200渠道的下載地址
	public static final int MSG_CHECK_CODE_FINISH = 1;
	
	public static final int DIALOG_NO_NET = 1;
	public static final int DIALOG_INVALID_CODE	 = 2;
	public static final int DIALOG_EXPRIE_CODE = 3;
	/**
	 * 版本更新
	 */
	private DeskSettingItemBaseView mSettingCheckVersion;
	
	/**
	 * 功能预告
	 */
//	private DeskSettingItemBaseView mSettingFuncForenotice;

	/**
	 * 试用帮助
	 */
	private DeskSettingItemBaseView mSettingHelpUse;

	/**
	 * 分享软件
	 */
	private DeskSettingItemBaseView mSettingShareAPP;

	/**
	 * 软件评分
	 */
	private DeskSettingItemBaseView mSettingRateGo;

	/**
	 * 意见反馈
	 */
	private DeskSettingItemBaseView mSettingFeedBack;

	/**
	 * 加入我们
	 */
//	private DeskSettingItemBaseView mSettingJoinUSetting;

	/**
	 * 版权信息
	 */
	private DeskSettingItemBaseView mSettingCopyRight;

	/**
	 * Check for beta updates
	 */
	private DeskSettingItemBaseView mSettingBetaUpfate;
	
	private boolean mClickCheck = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.desk_setting_about);
		initViews();
		GuiThemeStatistics
				.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.PREFERENCES_AGL);
	}

	/**
	 * <br>功能简述:初始化View
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void initViews() {
		mSettingCheckVersion = (DeskSettingItemBaseView) findViewById(R.id.check_version_item);
		mSettingCheckVersion.setOnClickListener(this);
		
//		mSettingFuncForenotice = (DeskSettingItemBaseView) findViewById(R.id.function_forenotice_item);
//		mSettingFuncForenotice.setOnClickListener(this);

		mSettingHelpUse = (DeskSettingItemBaseView) findViewById(R.id.help_use_item);
		mSettingHelpUse.setOnClickListener(this);

		mSettingShareAPP = (DeskSettingItemBaseView) findViewById(R.id.share_app_item);
		mSettingShareAPP.setOnClickListener(this);

		mSettingRateGo = (DeskSettingItemBaseView) findViewById(R.id.rate_go_item);
		mSettingRateGo.setOnClickListener(this);

		mSettingFeedBack = (DeskSettingItemBaseView) findViewById(R.id.feedback_item);
		mSettingFeedBack.setOnClickListener(this);

//		mSettingJoinUSetting = (DeskSettingItemBaseView) findViewById(R.id.joinus_info_item);
//		mSettingJoinUSetting.setOnClickListener(this);

		mSettingCopyRight = (DeskSettingItemBaseView) findViewById(R.id.copyright_info_item);
		mSettingCopyRight.setOnClickListener(this);

//		mSettingBetaUpfate = (DeskSettingItemBaseView) findViewById(R.id.beta_updates_info_item);
//		mSettingBetaUpfate.setOnClickListener(this);

		//判断是否CN地区。是就显示加入我们，否则显示Check for beta updates
		boolean isCn = GoAppUtils.isCnUser(this);
		if (isCn) {
//			mSettingBetaUpfate.setVisibility(View.GONE);
			mSettingCopyRight.setBottomLineVisible(View.GONE);
		}/* else {
			mSettingJoinUSetting.setVisibility(View.GONE);
		}*/
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.check_version_item :
				String uid = GoStorePhoneStateUtil.getUid(this);
				if (GoStoreAppInforUtil.isExistGoogleMarket(this)
						&& !TextUtils.isEmpty(uid) && uid.equals("200")) {
					//  跳转电子市场 
					AppUtils.gotoMarket(this, MarketConstant.APP_DETAIL + getPackageName());
				} else {
					// 点击跳转GOStore详情界面
					AppsDetail.gotoDetailDirectly(this, 
							AppsDetail.START_TYPE_APPRECOMMENDED, getPackageName());
				}
//				GoStoreOperatorUtil.gotoStoreDetailDirectly(this, getPackageName());
				break;

//			case R.id.function_forenotice_item :
//				Intent intent = new Intent();
//				intent.setClass(this, FuncForeActivity.class);
//				startActivity(intent);
//				break;
				
			case R.id.help_use_item :
				saveStartActivity(new Intent(this, DeskSettingQaTutorialActivity.class));
				break;

			case R.id.share_app_item :
				startShareIntent();
				break;

			case R.id.rate_go_item :
				if (GoAppUtils.isMarketExist(this)) {
					GoAppUtils.gotoMarket(this, MarketConstant.APP_DETAIL
							+ IGoLauncherClassName.DEFAULT_THEME_PACKAGE);
				} else {
					AppUtils.gotoBrowser(this, MarketConstant.APP_DETAIL
							+ IGoLauncherClassName.DEFAULT_THEME_PACKAGE);
				}
				break;

			case R.id.feedback_item :
				startFeedbackIntent();
				break;

//			case R.id.joinus_info_item :
//				startJoinus();
//				break;

			case R.id.copyright_info_item :
				startCopyrightIntent();
				break;

//			case R.id.beta_updates_info_item :
//				startBetaUpdates();
//				break;
			case R.id.activation_code_item:
				showActiveDialog();
				break;
			default :
				break;
		}
	}

	/**
	 * <br>功能简述:软件分享
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void startShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_title));

		String shareContent = getString(R.string.share_content_text);
		if (GoStorePhoneStateUtil.is200ChannelUid(this)) {
			shareContent += DOWNLOAD_200;
		} else {
			shareContent += DOWNLOAD_UN_200;
		}
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareContent);

		Intent chooser = Intent.createChooser(shareIntent, getString(R.string.choose_share_way));
		saveStartActivity(chooser);
	}

	/**
	 * <br>功能简述:意见反馈
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void startFeedbackIntent() {
		DeskSettingQaTutorialActivity.startFeedbackIntent(this);
	}

	/**
	 * <br>功能简述:加入我们
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void startJoinus() {
		Uri uri = Uri.parse("http://www.3g.cn/recruit/index.aspx");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		saveStartActivity(intent);
	}

	/**
	 * <br>功能简述:加入我们
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void startCopyrightIntent() {
		String curLanguage = Machine.getLanguage(this).toLowerCase();
		String uriString = "http://goforandroid.com/GDTEn/index.aspx";
		if (curLanguage.equals("zh")) {
			uriString = "http://goforandroid.com/gdt/index.aspx?nav=1";
		} else if (curLanguage.equals("ko")) {
			uriString = "http://goforandroid.com/Korea/index.aspx";
		}
		Uri uri = Uri.parse(uriString);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		saveStartActivity(intent);
	}

	/**
	 * <br>功能简述:Check for beta updates
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void startBetaUpdates() {
		Uri uri = Uri.parse("http://golauncher.goforandroid.com/tag/go-launcher-beta/");
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
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		// TODO Auto-generated method stub
		
	}
	
	private void showActiveDialog() {
		final InputDialog dialog = new InputDialog(this);
		dialog.show();
		dialog.setTitle(R.string.dialog_input_active_code_title);
		dialog.setHint(R.string.activation_code_input_hint);
		dialog.setSummaryText(R.string.activation_code_input_tips);
		dialog.setMaxInputLength(20);
		dialog.setPositiveButtonClickable(false);
		dialog.addInputTextWatcher(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String code = s.toString().trim();
				if (code.length() > 7 && !code.contains(" ")) {
					dialog.setPositiveButtonClickable(true);
				} else {
					dialog.setPositiveButtonClickable(false);
				}
			}
		});
		dialog.setPositiveButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mClickCheck = true;
			}
		});
		dialog.setNegativeButtonVisible(View.VISIBLE);
		dialog.setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialogInterface) {
				// TODO Auto-generated method stub
				if (mClickCheck) {
					mClickCheck = false;
					checkActiveCode(dialog.getInputContent());
				}
			}
		});
	}
	
	private boolean isShowActiveItem() {
		if (VersionControl.isNewUser() && !FunctionPurchaseManager.getInstance(getApplicationContext())
						.queryItemPurchaseState(FunctionPurchaseManager.PURCHASE_ITEM_FULL)) {
			return true;
		}
		return false;
	}
	
	private void checkActiveCode(String code) {
		if (code != null) {
			if (!Machine.isNetworkOK(this)) {
				showActiveCodeTipDialog(DIALOG_NO_NET);
			} else {
				findViewById(R.id.check_progress).setVisibility(View.VISIBLE);
				FunctionPurchaseManager.getInstance(getApplicationContext()).checkActiveCode(code,
						new IActiveListener() {

							@Override
							public void onResponse(int responseCode) {
								// TODO Auto-generated method stub
								Message msg = mHandler.obtainMessage(MSG_CHECK_CODE_FINISH);
								msg.arg1 = responseCode;
								mHandler.sendMessage(msg);
							}

						});
			}
		}
	}
	
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
				case MSG_CHECK_CODE_FINISH :
					findViewById(R.id.check_progress).setVisibility(View.GONE);
					if (msg.arg1 == PromotionsControl.ACTIVE_SUCCESS) {
						findViewById(R.id.activation_code_item).setVisibility(View.GONE);
						FunctionPurchaseManager.getInstance(getApplicationContext())
								.savePurchasedItem(
										FunctionPurchaseManager.PURCHASE_ITEM_ACTIVE_CODE);
						DeskSettingUtils.gotoHadPayView(DeskSettingAboutActivity.this);
						Toast.makeText(DeskSettingAboutActivity.this,
								R.string.activation_code_success, 2000).show();

					} else {
						showActiveCodeTipDialog(msg.arg1);
					}
					break;

				default :
					break;
			}
		}
	};
	
	
	private void showActiveCodeTipDialog(int type) {
		DialogConfirm dialogConfirm = new DialogConfirm(this);
		dialogConfirm.show();
		dialogConfirm.setTitle(R.string.activation_code_tip_dialog_title);
		dialogConfirm.setPositiveButtonVisible(View.VISIBLE);
		dialogConfirm.setNegativeButtonVisible(View.GONE);
		switch (type) {
			case DIALOG_NO_NET :
				dialogConfirm.setMessage(R.string.activation_code_tip_dialog_tip_net);
				break;
			case PromotionsControl.ACTIVE_INVALID_DATE :
				dialogConfirm.setMessage(R.string.activation_code_tip_dialog_tip_expired_code);
				break;
			case PromotionsControl.ACTIVE_INVALID_CODE :
				dialogConfirm.setMessage(R.string.activation_code_tip_dialog_tip_wrong_code);
				break;
			default :
				dialogConfirm.setMessage(R.string.activation_code_tip_dialog_tip_wrong_code);
				break;
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isShowActiveItem()) {
			findViewById(R.id.activation_code_item).setVisibility(View.VISIBLE);
			findViewById(R.id.activation_code_item).setOnClickListener(this);
		} else {
			findViewById(R.id.activation_code_item).setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
		return false;
	}
}
