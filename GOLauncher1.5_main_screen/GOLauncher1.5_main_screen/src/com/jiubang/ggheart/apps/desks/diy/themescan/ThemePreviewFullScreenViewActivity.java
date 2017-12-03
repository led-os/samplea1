package com.jiubang.ggheart.apps.desks.diy.themescan;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gau.go.launcherex.R;
import com.getjar.sdk.GetjarConstants;
import com.getjar.sdk.OnAdAvailableListener;
import com.go.util.scroller.ScreenScroller;
import com.go.util.scroller.ScreenScrollerListener;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.InterstitialAd;
import com.jiubang.ggheart.admob.AdConstanst;
import com.jiubang.ggheart.admob.AdInfo;
import com.jiubang.ggheart.admob.AdViewBuilder;
import com.jiubang.ggheart.apps.desks.purchase.getjar.GetJarManager;
import com.jiubang.ggheart.apps.desks.purchase.getjar.GetJarManager.IConnectCallBack;
import com.jiubang.ggheart.apps.gowidget.gostore.views.ScrollerViewGroup;
import com.jiubang.ggheart.data.theme.ImageExplorer;
import com.jiubang.ggheart.data.theme.bean.ThemeInfoBean;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
/**
 * 
 * @author liulixia
 *主题预览详情页图片全景图
 */
public class ThemePreviewFullScreenViewActivity extends Activity implements ScreenScrollerListener, AdListener {
	private final static int REQUEST_INTERSTITIAL = 1;
	private final static int REQUEST_RESOLUTION = 2;
	private int mCurImageIndex = 0; // 当前显示的图片下标
	private ScrollerViewGroup mScrollerViewGroup = null; // 图片滚动器
	private ArrayList<String> mScanImageIdsArrayList = null; // 浏览图片的ID集合
	private ArrayList<RelativeLayout> mImageViewLayouts = null; // 图片layout的集合
	private ThemeInfoBean mThemeBean = null;
	private BroadcastReceiver mBroadcastReceiver = null;
	private Object mFullScreenAd;
	private boolean mFullAdSucess;
	private int mThemeType;
	private boolean mShowAdImmediately;
	private boolean mHasRetry; // 防止getjar sdk异常不断返回RESOLUTION_REQUIRED 
	private boolean mIsLastScreen = false; // 是否在最后一屏
	private boolean mHasShowAd = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		GetJarManager.buildInstance().connect(mConnectCallBack);
		mThemeBean = intent.getParcelableExtra(ThemeConstants.FULLSCREEN_THEME_INFO);
		mCurImageIndex = intent.getIntExtra(ThemeConstants.FULLSCREEN_CURRENT_INDEX, 0);
		mScanImageIdsArrayList = mThemeBean.getPreViewDrawableNames();
		if (mScanImageIdsArrayList.size() > 1 && mThemeBean.getFeaturedId() == 0) {
			mCurImageIndex = mCurImageIndex - 1;
			mScanImageIdsArrayList.remove(0);
		}
		mThemeType = getIntent().getIntExtra(ThemeConstants.TAB_TYPE_EXTRA_KEY,
				ThemeConstants.LAUNCHER_INSTALLED_THEME_ID);
		// 初始化View
		initView();
		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String imageId = intent.getStringExtra(ThemeConstants.CHANGE_FULLSCREEN_IMAGE_ID);
				if (imageId != null && mImageViewLayouts != null) {
					int beanType = mThemeBean.getBeanType();
					for (RelativeLayout imageviewLayout : mImageViewLayouts) {
						String tag = (String) imageviewLayout.getTag();
						if (tag != null && tag.equals(imageId)) {
							setImageView(imageviewLayout, beanType, imageId);
						}
					}
				}
			}
		};
		registerBrocastReceiver();
	}

private IConnectCallBack mConnectCallBack = new IConnectCallBack() {
		
		@Override
		public void onConnect(final boolean connect, Intent resolution) {
			// TODO Auto-generated method stub
			if (!connect && resolution != null && !mHasRetry) {
				startActivityForResult(resolution, REQUEST_RESOLUTION);
			} else {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						intAd(connect);
					}
				});
			}
		}
	};
	
	
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

	private void intAd(boolean getJarConnect) {
		boolean getjarEnable = AdViewBuilder.getBuilder().isGetjarEnable(this, getPid(), getFullAdPosId()) && getJarConnect;
		if (getjarEnable 
				&& AdViewBuilder.getBuilder().getProvity(this, getPid(), getFullAdPosId()) == AdInfo.PROVITY_GETJAR) {
			prepareGetJarAd(true);
		} else {
			if (!showAdmobAd(false) && getjarEnable) {
				prepareGetJarAd(false);
			}
		}
	}
	/**
	 * <br>功能简述:准备
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param showAdmob 失败时是否显示admob
	 */
	private void prepareGetJarAd(final boolean showAdmob) {
		if (getIntent().getBooleanExtra(ThemeConstants.GETJAR_AD_AVAILABLE, false)) {
			mFullScreenAd = GetJarManager.buildInstance()
					.getInterstitialIntent();
		} else {
			GetJarManager.buildInstance().prepareAd(
					new OnAdAvailableListener() {
						
						@Override
						public void onAdAvailable(final boolean arg0) {
							// TODO Auto-generated method stub
							Log.d(GetJarManager.TAG, "onAdAvailable:" + arg0);
							runOnUiThread(new Runnable() {
								public void run() {
									if (arg0) {
										mFullScreenAd = GetJarManager.buildInstance(
												).getInterstitialIntent();
										if (mIsLastScreen && mFullScreenAd != null) {
											startActivityForResult((Intent) mFullScreenAd,
													REQUEST_INTERSTITIAL);
										} else if (mFullScreenAd == null && showAdmob) {
											showAdmobAd(false);
										}
									} else if (showAdmob) {
										showAdmobAd(false);
									}
								}
							});
						}
					});
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 反注册广播接收器
		if (mBroadcastReceiver != null) {
			unregisterReceiver(mBroadcastReceiver);
		}
		mBroadcastReceiver = null;
		if (mScrollerViewGroup != null) {
			mScrollerViewGroup.removeAllViews();
			mScrollerViewGroup.destory();
			mScrollerViewGroup = null;
		}
		if (mImageViewLayouts != null) {
			for (RelativeLayout imageViewLayout : mImageViewLayouts) {
				if (imageViewLayout != null) {
					ImageView view = (ImageView) imageViewLayout.findViewById(R.id.image);
					if (view != null) {
						//						Drawable drawable = view.getDrawable();
						view.setImageDrawable(null);
						//						if (drawable != null && !((BitmapDrawable) drawable).getBitmap().isRecycled()) {
						//							((BitmapDrawable) drawable).getBitmap().recycle();
						//						}
					}
				}
			}
			mImageViewLayouts.clear();
			mImageViewLayouts = null;
		}
	}

	private void registerBrocastReceiver() {
		// 注册广播接收器
		if (mBroadcastReceiver != null) {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(ICustomAction.ACTION_CHANGE_FULLSCREEN_IMAGE);
			registerReceiver(mBroadcastReceiver, intentFilter);
		}
	}

	/**
	 * 初始化View的方法
	 */
	private void initView() {
		// 去掉title部分
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 去掉信息栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.singleview_fullscreenactivity);
		// 初始化图片滚动界面
		initImageViews();
	}

	/**
	 * 初始化界面的方法
	 */
	private void initImageViews() {
		// 图片切换器
		RelativeLayout relativeLayout = (RelativeLayout) this.findViewById(R.id.scrollLinearLayout);
		mScrollerViewGroup = new ScrollerViewGroup(this, this);
		mScrollerViewGroup.setBackgroundColor(Color.parseColor("#000000"));
		if (relativeLayout != null && mScrollerViewGroup != null) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			relativeLayout.addView(mScrollerViewGroup, params);
			if (mScanImageIdsArrayList != null && mScanImageIdsArrayList.size() > 0) {
				int size = mScanImageIdsArrayList.size();
				if (mFullScreenAd != null) {
					mScrollerViewGroup.setScreenCount(size + 1);
				} else {
					mScrollerViewGroup.setScreenCount(size);
				}
				mImageViewLayouts = new ArrayList<RelativeLayout>(size);
				RelativeLayout imageLayout = null;
				int beanType = mThemeBean.getBeanType();
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				for (int i = 0; i < size; i++) {
					imageLayout = (RelativeLayout) inflater.inflate(
							R.layout.theme_detail_singletheme_fullscreen, null);
					imageLayout.setTag(mScanImageIdsArrayList.get(i));
					setImageView(imageLayout, beanType, mScanImageIdsArrayList.get(i));
					mImageViewLayouts.add(imageLayout);
					mScrollerViewGroup.addScreenView(imageLayout);
				}
				addFullScreenDetailView(inflater);
				if (mCurImageIndex >= 0 && mCurImageIndex < size) {
					mScrollerViewGroup.gotoViewByIndex(mCurImageIndex);
					// 如果是index为0，ScrollView不会刷新，必须手动刷新
					refreshScrollView();
				}
			}
		}
	}

	private void addFullScreenDetailView(LayoutInflater inflater) {
		if (mFullScreenAd != null) {
			RelativeLayout imageLayout = null;
			try {
				imageLayout = (RelativeLayout) inflater.inflate(
						R.layout.theme_detail_singletheme_fullscreen, null);
				imageLayout.findViewById(R.id.image_loading).setVisibility(View.GONE);
				ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
				imageView.setImageResource(R.drawable.theme_detail_mock);
				setImageSize(imageView);
				mImageViewLayouts.add(imageLayout);
				mScrollerViewGroup.addScreenView(imageLayout);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				if (imageLayout != null) {
					imageLayout.findViewById(R.id.image_loading).setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private void refreshScrollView() {
		if (mScrollerViewGroup != null) {
			mCurImageIndex = mScrollerViewGroup.getCurrentViewIndex();
		}
	}

	private void setImageView(RelativeLayout imageViewLayout, int themeType, String preview) {
		ImageView imageView = (ImageView) imageViewLayout.findViewById(R.id.image);
		Drawable drawable = null;
		if (ThemeConstants.LAUNCHER_INSTALLED_THEME_ID == themeType
				|| ThemeConstants.LOCKER_INSTALLED_THEME_ID == themeType) { //从apk读资源
			drawable = ImageExplorer.getInstance(this).getDrawable(mThemeBean.getPackageName(),
					preview);
		} else { // 从sd卡读图片
			String path = null;
			if (mThemeBean.getBeanType() == ThemeConstants.LOCKER_FEATURED_THEME_ID) {
				path = LauncherEnv.Path.GOTHEMES_PATH + "lockericon/";
			} else {
				path = LauncherEnv.Path.GOTHEMES_PATH + "icon/";
			}
			String imagePath = path + preview;
			try {
				Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
				if (bitmap != null) {
					drawable = new BitmapDrawable(bitmap);
				}
			} catch (OutOfMemoryError e) {

			} catch (Exception e) {

			}
		}
		if (drawable != null) {
			imageView.setImageDrawable(drawable);
			imageViewLayout.findViewById(R.id.image_loading).setVisibility(View.GONE);
		} else {
			imageView.setImageResource(R.drawable.themestore_detail_thumbnail_default_icon);
			imageViewLayout.findViewById(R.id.image_loading).setVisibility(View.VISIBLE);
		}
		setImageSize(imageView);
	}

	private void setImageSize(ImageView imageView) {
		Drawable drawable = imageView.getDrawable();
		if (drawable != null) {
			int drawableWidth = drawable.getIntrinsicWidth();
			int drawableHeight = drawable.getIntrinsicHeight();
			int screenheight = getResources().getDisplayMetrics().heightPixels;
			int screenwidth = getResources().getDisplayMetrics().widthPixels;
			int imageViewWidth = screenheight * drawableWidth / drawableHeight;
			int imageViewHeight = screenheight;
			if (imageViewWidth > screenwidth) {
				imageViewWidth = screenwidth;
				imageViewHeight = screenwidth * drawableHeight / drawableWidth;
			}
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageViewWidth,
					imageViewHeight);
			imageView.setLayoutParams(params);
		}
	}

	@Override
	public ScreenScroller getScreenScroller() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setScreenScroller(ScreenScroller scroller) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFlingIntercepted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFlingStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollChanged(int newScroll, int oldScroll) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScreenChanged(int newScreen, int oldScreen) {
		if (mScrollerViewGroup.getScreenScroller() != null
				&& newScreen == mScrollerViewGroup.getScreenScroller().getScreenCount() - 1) {
			mIsLastScreen = true;
			if (mFullScreenAd != null && !mHasShowAd) {
				if (mFullAdSucess && mFullScreenAd instanceof InterstitialAd
						&& ((InterstitialAd) mFullScreenAd).isReady()) {
						((InterstitialAd) mFullScreenAd).show();
						mHasShowAd = true;
				} else if (mFullScreenAd instanceof Intent) {
						startActivityForResult((Intent) mFullScreenAd, REQUEST_INTERSTITIAL);
				}
			}
		} else {
			mIsLastScreen = false;
		}
	}

	@Override
	public void onScrollFinish(int currentScreen) {
		// TODO Auto-generated method stub
		refreshScrollView();
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void scrollBy(int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getScrollX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getScrollY() {
		// TODO Auto-generated method stub
		return 0;
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
		if (mFullScreenAd != null && (mShowAdImmediately || mIsLastScreen) && mFullScreenAd instanceof InterstitialAd
				&& ((InterstitialAd) mFullScreenAd).isReady()) {
			mHasShowAd = true;
			((InterstitialAd) mFullScreenAd).show();
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
					intAd(false);
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * <br>功能简述:显示admob广告当getjar无广告时
	 * <br>功能详细描述:
	 * <br>注意:
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
