package com.jiubang.ggheart.apps.desks.diy.themescan;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.getjar.sdk.GetjarConstants;
import com.getjar.sdk.OnAdAvailableListener;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.InterstitialAd;
import com.jiubang.ggheart.admob.AdConstanst;
import com.jiubang.ggheart.admob.AdInfo;
import com.jiubang.ggheart.admob.AdViewBuilder;
import com.jiubang.ggheart.admob.GoAdView;
import com.jiubang.ggheart.admob.GoDetailAdView;
import com.jiubang.ggheart.apps.desks.purchase.getjar.GetJarManager;
import com.jiubang.ggheart.apps.desks.purchase.getjar.GetJarManager.IConnectCallBack;
import com.jiubang.ggheart.data.theme.OnlineThemeGetter;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.data.theme.bean.ThemeInfoBean;

/**
 * 主题详情Activity
 * 
 * @author yangbing
 * */
public class ThemeDetailActivity extends Activity implements AdListener, OnIndicatorChangeListener {

	public static final int MSG_GET_DETAIL_FINISHED = 0X1;
	public static final int MSG_GET_DETAIL_FAILED = 0X2;
	public static final int ADD_BANNER_VIEW_MAG_CODE = 0x3;
	public static final int TOAST_SHOW_TIME = 600;
	private final static int REQUEST_INTERSTITIAL = 1;
	private final static int REQUEST_RESOLUTION = 2;
	private ThemeDetailView mThemeDetailView;
	private String mThemePackageName;
	private String mThemeName = "";
	private ThemeInfoBean mThemeDetailBean;
	private BroadcastReceiver mThemeUninstallReceiver; // 监听主题被卸载
	private int mModel;
	private boolean mIsGetThemeBeanSuccessByNet = true;
	private int mSortId = -1;
	private int mType;
	private GoAdView mAdView;
	private Object mFullScreenAd;
	private int mCurrentOrientation;
	private int mThemeType;
	private boolean mShowAdImmediately;
	private boolean mHasRetry; // 防止getjar sdk异常不断返回RESOLUTION_REQUIRED 
	private boolean mIsLastScreen = false; // 是否是做好
	private boolean mHasShowAd = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		confirmOrientation();
		mThemeDetailView = (ThemeDetailView) LayoutInflater.from(ThemeDetailActivity.this).inflate(
				R.layout.theme_detail, null);
		setContentView(mThemeDetailView);
		
		mThemeDetailView.setFullScreenAdEnable(true);
		mThemeDetailView.setIndicatorChangeListener(this);
		Intent intent = getIntent();
		mModel = intent.getIntExtra(ThemeConstants.DETAIL_MODEL_EXTRA_KEY,
				ThemeConstants.DETAIL_MODEL_INSTALL_EXTRA_VALUE);
		mThemePackageName = intent.getStringExtra(ThemeConstants.PACKAGE_NAME_EXTRA_KEY);
		int position = intent.getIntExtra(ThemeConstants.THEME_LIST_ITEM_POSITION_KEY, 0);
		mThemeDetailView.setItemPostion(position);
		if (mModel == ThemeConstants.DETAIL_MODEL_FEATURED_EXTRA_VALUE) {
			mThemeName = intent.getStringExtra(ThemeConstants.TITLE_EXTRA_KEY);
			mType = intent.getIntExtra(ThemeConstants.TITLE_EXTRA_THEME_TYPE_KEY,
					ThemeConstants.LAUNCHER_FEATURED_THEME_ID);
			ThemeInfoBean infoBean = new ThemeInfoBean();
			infoBean.setPackageName(mThemePackageName);
			int id = intent.getIntExtra(ThemeConstants.DETAIL_ID_EXTRA_KEY, 0);
			infoBean.setFeaturedId(id);
			if (mType == ThemeConstants.LAUNCHER_SPEC_THEME_ID) {
				mSortId = intent.getIntExtra(ThemeConstants.TITLE_EXTRA_THEME_SPECID_KEY, -1);
			}
			mThemeDetailBean = new OnlineThemeGetter(this).getFeatureThemeDetailInfo(infoBean,
					mHandler);
			if (mThemeDetailBean != null) {
				mThemeDetailBean.setBeanType(ThemeConstants.LAUNCHER_SPEC_THEME_ID);
				mThemeDetailBean.setSortId(mSortId);
				dismissProgress();
				mThemeDetailView.reLayoutView(mThemeDetailBean);
			} else {
				showProgress();
			}
		} else {
			dismissProgress();
			registerThemeUninstallReceiver();
			mThemeDetailBean = ThemeManager.getInstance(this).getThemeInfo(mThemePackageName);
			mThemeDetailBean.setBeanType(ThemeConstants.LAUNCHER_INSTALLED_THEME_ID);
			mThemeDetailView.reLayoutView(mThemeDetailBean);
		}
		mCurrentOrientation = Configuration.ORIENTATION_PORTRAIT;
		mThemeType = getIntent().getIntExtra(ThemeConstants.TAB_TYPE_EXTRA_KEY,
				ThemeConstants.LAUNCHER_INSTALLED_THEME_ID);
		
		initAds();
	}

	private boolean isLocker() {
		boolean isLocker = false;
		if (mThemeType == ThemeConstants.LOCKER_FEATURED_THEME_ID
				|| mThemeType == ThemeConstants.LOCKER_INSTALLED_THEME_ID) {
			isLocker = true;
		}
		return isLocker;
	}
	
	
	private int getPid() {
		return isLocker()
				? AdConstanst.PRODUCT_ID_LOCKER_THEME
				: AdConstanst.PRODUCT_ID_LAUNCHER_THEME;
	}
	private int getFullAdPosId() {
		return isLocker()
				? AdConstanst.POS_ID_LOCKER_THEME_DETAIL_FULLSCREEN
				: AdConstanst.POS_ID_LAUNCHER_THEME_DETAIL_FULLSCREEN;
	}
	private void initAds() {
		LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);
		//		mAdView = AdViewBuilder.getBuilder().getAdView(
		//				this,
		//				isLocker
		//						? AdConstanst.PRODUCT_ID_LOCKER_THEME
		//						: AdConstanst.PRODUCT_ID_LAUNCHER_THEME,
		//				isLocker
		//						? AdConstanst.POS_ID_LOCKER_THEME_DETAIL_BANNER
		//						: AdConstanst.POS_ID_LAUNCHER_THEME_DETAIL_BANNER, true);

		mAdView = GoDetailAdView.sBannerAdView;
		if (GoDetailAdView.sBannerAdView != null) {
			GoDetailAdView.sBannerAdView.setNewInfo(getPid(), isLocker()
					? AdConstanst.POS_ID_LOCKER_THEME_DETAIL_BANNER
					: AdConstanst.POS_ID_LAUNCHER_THEME_DETAIL_BANNER);
			if (GoDetailAdView.sParent != null) {
				GoDetailAdView.sParent.removeView(mAdView);
			}
			GoDetailAdView.sParent = layout;
		}

		if (layout != null && mAdView != null) {
			layout.addView(mAdView);
		}
		
		GetJarManager.buildInstance().connect(mConnectCallBack);
	}
	
	public void initFullScreenAd(boolean getjarConnect) {
		boolean getjarEnable = AdViewBuilder.getBuilder().isGetjarEnable(this, getPid(),
				getFullAdPosId())
				&& getjarConnect;
		if (getjarEnable
				&& AdViewBuilder.getBuilder().getProvity(this, getPid(), getFullAdPosId()) == AdInfo.PROVITY_GETJAR) {
			prepareGetJarAd(true);
		} else {
			if (!showAdmobAd(false) && getjarEnable) { //无admob，显示getjar
				prepareGetJarAd(false);
			}
		}
	}
	private IConnectCallBack mConnectCallBack = new IConnectCallBack() {
		
		@Override
		public void onConnect(final boolean connect, Intent result) {
			if (!connect && result != null && !mHasRetry) {
				startActivityForResult(result, REQUEST_RESOLUTION);
			} else {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						initFullScreenAd(connect);
					}
				});
			}
		}
	};
	
	/**
	 * <br>功能简述:准备getjar全屏广告
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param showAdmob 如果失败是否需要显示admob广告
	 */
	private void prepareGetJarAd(final boolean showAdmob) {
		GetJarManager.buildInstance().prepareAd(new OnAdAvailableListener() {

			@Override
					public void onAdAvailable(final boolean arg0) {
						// TODO Auto-generated method stub
						Log.d(GetJarManager.TAG, "onAdAvailable:" + arg0);
						runOnUiThread(new Runnable() {

							@Override
					public void run() {
						// TODO Auto-generated method stub
						if (arg0) {
							mFullScreenAd = GetJarManager.buildInstance()
									.getInterstitialIntent();
							if (mFullScreenAd == null && showAdmob) {
								showAdmobAd(false);
							} else if (mIsLastScreen) {
								startActivityForResult((Intent) mFullScreenAd, REQUEST_INTERSTITIAL);
							}

						} else if (showAdmob) {
							showAdmobAd(false);
						}
					}
						});

					}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		confirmOrientation();
		//		mThemeDetailView.reLayoutView(mThemeDetailBean);
	}

	private void registerThemeUninstallReceiver() {

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addDataScheme("package");
		intentFilter.setPriority(Integer.MAX_VALUE);

		mThemeUninstallReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
					String packageName = intent.getDataString();
					if (packageName.contains(ThemeConstants.LAUNCHER_THEME_PREFIX)
							|| packageName.contains(ThemeConstants.LAUNCHER_BIG_THEME_PREFIX)) {
						ThemeDataManager.getInstance(context).clearup();
						ThemeDetailActivity.this.finish();
						ThemeManageActivity.sRefreshFlag = true;
					}
				} else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
					// 当在详情主题那里下载主题并且安装后，需要刷新界面
					String packageName = intent.getDataString();
					if (packageName.contains(ThemeConstants.LAUNCHER_THEME_PREFIX)
							|| packageName.contains(ThemeConstants.LAUNCHER_BIG_THEME_PREFIX)) {
						ThemeDataManager.getInstance(context).clearup();
						ThemeDetailActivity.this.finish();
						ThemeManageActivity.sRefreshFlag = true;
					}
				}

			}
		};

		try {

			registerReceiver(mThemeUninstallReceiver, intentFilter);
		} catch (Throwable e) {
			try {
				unregisterReceiver(mThemeUninstallReceiver);
				registerReceiver(mThemeUninstallReceiver, intentFilter);
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
		}

	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		mThemeDetailView.cleanup();
		if (mThemeUninstallReceiver != null) {
			unregisterReceiver(mThemeUninstallReceiver);
		}
		//		if (mAdView != null) {
		//			mAdView.destroy();
		//			mAdView = null;
		//		}
		if (mFullScreenAd != null && mFullScreenAd instanceof InterstitialAd) {
			InterstitialAd ad = (InterstitialAd) mFullScreenAd;
			ad.setAdListener(null);
			ad.stopLoading();
			mFullScreenAd = null;
		}
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
		}

		if (GoDetailAdView.sParent != null) {
			GoDetailAdView.sParent.removeAllViews();
			GoDetailAdView.sParent = null;
		}
		super.onDestroy();
	};

	/**
	 * 判断是横屏还是竖屏
	 * */
	private void confirmOrientation() {
		DisplayMetrics mMetrics = getResources().getDisplayMetrics();
		if (mMetrics.widthPixels <= mMetrics.heightPixels) {
			if (!SpaceCalculator.sPortrait) {
				SpaceCalculator.setIsPortrait(true);
				SpaceCalculator.getInstance(this).calculateItemViewInfo();
			}
		} else {
			if (SpaceCalculator.sPortrait) {
				SpaceCalculator.setIsPortrait(false);
				SpaceCalculator.getInstance(this).calculateThemeListItemCount();
			}
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mThemeDetailView != null) {
				mThemeDetailView.goBack();
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (mThemeDetailView != null) {
				mThemeDetailView.onMenuKeyUp();
				return true;
			}
		}
		return false;
	}

	public static void exit() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		confirmOrientation();
		if (!mIsGetThemeBeanSuccessByNet) {
			showProgress();
		}
		mThemeDetailView.setCurrentScreen(0);
		mThemeDetailView.reLayoutView(mThemeDetailBean);
		mCurrentOrientation = newConfig.orientation;
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
				case MSG_GET_DETAIL_FINISHED :
					if (msg.obj != null && msg.obj instanceof ThemeInfoBean) {
						dismissProgress();
						mThemeDetailBean = (ThemeInfoBean) msg.obj;
						mThemeDetailBean.setBeanType(mType);
						mThemeDetailBean.setSortId(mSortId);
						mThemeDetailView.reLayoutView(mThemeDetailBean);
					} else {
						Toast.makeText(ThemeDetailActivity.this, R.string.http_exception,
								TOAST_SHOW_TIME).show();
						finish();
					}
					break;
				case MSG_GET_DETAIL_FAILED :
					Toast.makeText(ThemeDetailActivity.this, R.string.theme_get_detailinfo_failed,
							TOAST_SHOW_TIME).show();
					finish();
					break;
				default :
					break;
			}
		}
	};
	private boolean mFullAdSucess;

	private void showProgress() {
		mIsGetThemeBeanSuccessByNet = false;
		mThemeDetailView.findViewById(R.id.theme_detail_loading).setVisibility(View.GONE);
		mThemeDetailView.findViewById(R.id.theme_detail).setVisibility(View.VISIBLE);
		//		mThemeDetailView.findViewById(R.id.detail_buttons).setVisibility(View.GONE);
		mThemeDetailView.setImageBackground(mThemeName, mThemePackageName);
	}

	private void dismissProgress() {
		mIsGetThemeBeanSuccessByNet = true;
		mThemeDetailView.findViewById(R.id.theme_detail_loading).setVisibility(View.GONE);
		mThemeDetailView.findViewById(R.id.detail_buttons).setVisibility(View.VISIBLE);
		mThemeDetailView.findViewById(R.id.theme_detail).setVisibility(View.VISIBLE);
	}

	@Override
	public void onDismissScreen(Ad arg0) {
	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		mFullAdSucess = false;
	}

	@Override
	public void onLeaveApplication(Ad arg0) {

	}

	@Override
	public void onPresentScreen(Ad arg0) {

	}

	@Override
	public void onReceiveAd(Ad arg0) {
		mFullAdSucess = true;
		//		if (mFullScreenAd != null) {
		//			if (mFullScreenAd.isReady()) {
		//				mFullScreenAd.show();
		//			}
		//		}
		Log.d(GetJarManager.TAG, "onReceiveAd admob");
		if (mFullScreenAd != null && (mShowAdImmediately || mIsLastScreen) && mFullScreenAd instanceof InterstitialAd
				&& ((InterstitialAd) mFullScreenAd).isReady()) {
			((InterstitialAd) mFullScreenAd).show();
			mHasShowAd = true;
		}
	}

	@Override
	public void onIndicatorChange(int max, int current) {
		if (max - 1 == current) {
			mIsLastScreen = true;
			if (mFullScreenAd != null && !mHasShowAd) {
				if (mFullAdSucess && mFullScreenAd instanceof InterstitialAd
						&& ((InterstitialAd) mFullScreenAd).isReady()) {
					if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
						((InterstitialAd) mFullScreenAd).show();
						mHasShowAd = true;
					}
				} else if (mFullScreenAd instanceof Intent) {
						startActivityForResult((Intent) mFullScreenAd, REQUEST_INTERSTITIAL);
				}
			}
		} else {
			mIsLastScreen = false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
			case REQUEST_INTERSTITIAL :
				if (resultCode == GetjarConstants.RESULT_NO_AD_DISPLAYED && !mHasShowAd) {
					if (AdViewBuilder.getBuilder().getProvity(this, getPid(), getFullAdPosId()) == AdInfo.PROVITY_GETJAR) {
						showAdmobAd(true);
					}
				} else {
					mHasShowAd = true;
				}
				break;
			case REQUEST_RESOLUTION :
				if (resultCode == Activity.RESULT_OK) {
					mHasRetry = true;
					GetJarManager.buildInstance().connect(mConnectCallBack);
				} else {
					initFullScreenAd(false);
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * <br>功能简述:显示admob
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param immediately 收到广告时是否立即显示
	 * @return
	 */
	private boolean showAdmobAd(boolean immediately) {
		// 如果优先显示的是getjar此种情况下再尝试admob
		mFullScreenAd = AdViewBuilder.getBuilder().getInterstitialAd(this, getPid(),
				getFullAdPosId());
		if (mFullScreenAd != null) {
			mShowAdImmediately = immediately;
			((InterstitialAd) mFullScreenAd).setAdListener(this);
			return true;
		}
		return false;
	}
	
}
