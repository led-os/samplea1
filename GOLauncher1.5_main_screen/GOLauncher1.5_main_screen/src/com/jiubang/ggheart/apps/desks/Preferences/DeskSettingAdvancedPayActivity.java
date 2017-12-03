package com.jiubang.ggheart.apps.desks.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.util.device.Machine;
import com.go.util.market.MarketConstant;
import com.go.util.scroller.ScreenScroller;
import com.go.util.scroller.ScreenScrollerListener;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.IAdvancedPayListener;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.view.AdvancedPayBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.view.AdvancedPayCoverView;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.view.AdvancedPayCoverView.ClickBackListenr;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.view.AdvancedPayViewContainer;
import com.jiubang.ggheart.apps.desks.diy.GoLauncher;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.StatusBarHandler;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.DesktopIndicator;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.IndicatorListner;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.ScreenIndicator;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.ScreenIndicatorItem;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.MessageManager;
import com.jiubang.ggheart.apps.desks.diy.themescan.coupon.PromotionBean;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.apps.gowidget.gostore.views.ScrollerViewGroup;
import com.jiubang.ggheart.components.DeskResources;
import com.jiubang.ggheart.components.DeskResourcesConfiguration;
import com.jiubang.ggheart.data.info.MessageInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;

/**
 * 升级付费功能
 * @author liulixia
 *
 */
public class DeskSettingAdvancedPayActivity extends Activity 
	implements 
	OnClickListener,
	ScreenScrollerListener,
	IndicatorListner,
	IAdvancedPayListener {
	private LinearLayout mIndicatorLayout; //指示器布局
	private DesktopIndicator mIndicator; // 指示器
	private ScrollerViewGroup mScrollerViewGroup; // 滚动控件
	private View mUpgradeButton; //升级按钮
	private TextView mUpgradeBtnText; // 升级按钮文字
//	private View mSaleImageView; // 右上角Sale 4.15--促销标志不需要在此显示
	private View mBtnSaleImageView; // 按钮右上角Sale
	private AdvancedPayCoverView mCoverView; // 罩子View
	private View mBgBottomView; // 背景色变换底部的View
	
	private final static float MIN_PERCENT = 0; //指示器最小百分比
	private final static float MAX_PERCENT = 100; //指示器最大百分比
	
	public final static String CURRENT_INDEX = "current_index";
	public final static String ENTRANCE_ID = "entrance_id";
	public final static String HAS_UPGRADED = "has_upgraded";
	
	public final static int PAY_START_PAGE = 0; // 起始页
	public final static int PAY_NO_ADS = 1; //无广告功能
	public final static int PAY_EFFECTOR = 2; //特效
	public final static int PAY_WALLPAPER_FILTER = 3; // 壁纸滤镜
	public final static int PAY_SECURITY_LOCK = 4; //安全锁和隐藏文件夹
	public final static int PAY_SIDEBAR = 5; //侧边栏
	public final static int PAY_GESTURE = 6; //手势
	
	private int mCurIndex = PAY_NO_ADS; //默认滚动到第1页
	private String mEntrance = "101";
	private boolean mHasUpgraded = false;
	
	private int mUnlockedTextId = -1;
	
	private Handler mHandler;
	private static final int AUTO_GOTO_NEXT_PAGE_DURATION = 1500;
	private static final int GO_TO_NEXT_PAGE = 0;
	private static final int LOAD_FINISH = 1;
	private View mGoProgressBar;
	private boolean mIsInit = false;
	/**
	 * 当前顶部颜色的屏幕下标
	 */
	private int mCurTopScreenIndex;
	/**
	 * 当前底部颜色
	 */
	private int mCurBottomColor;
	private MessageManager mMsgManager;
	
	//是否在促销期
	private boolean mIsPromotion = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		mMsgManager = MessageManager.getMessageManager(getApplicationContext());
		
		// 透明通知栏和透明虚拟键
		if (Machine.IS_SDK_ABOVE_KITKAT) {
			StatusBarHandler.setStatusBarTransparentKitKat(getWindow(), true);
		}
		setContentView(R.layout.desk_setting_pay_dialog);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
						| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		
		Intent intent = getIntent();
		mCurIndex = intent.getIntExtra(CURRENT_INDEX, 0);
		mHasUpgraded = intent.getBooleanExtra(HAS_UPGRADED, false);
		mEntrance = intent.getStringExtra(ENTRANCE_ID);
		
		mUnlockedTextId = R.string.desksetting_pay_dialog_unlocked;
		initHandler();
		initScrollerViewGroupData();
		initView();
		startLoad();
	}
	
	/**
	 * 进行异步加载，过程中会启ProgressBar
	 */
	private void startLoad() {
		showProgressBar();
		new Thread(new Runnable() {
			@Override
			public void run() {
			    
			    //从数据库中查询是否存在促销卷
			    ArrayList<MessageInfo> msgList = mMsgManager.getPromotionMessageInfo();
                ArrayList<PromotionBean> promotionList = mMsgManager.getPromotionList(msgList);
                mIsPromotion = mMsgManager.isPromotionPeriod(promotionList);
                
				// 先加载当前页面
				AdvancedPayViewContainer container = (AdvancedPayViewContainer) mScrollerViewGroup
						.getCurrentView();
				AdvancedPayBaseView curPayView = container.getAdvancedPayView();
				container.setSingleMessage(mIsPromotion);
				curPayView.setIsPromotion(mIsPromotion);
				curPayView.startLoad();
				// 再加载其他页面
				int count = mScrollerViewGroup.getScreenCount();
				for (int i = 0 ; i < count; i++) {
					container = (AdvancedPayViewContainer) mScrollerViewGroup.getChildAt(i);
					AdvancedPayBaseView payView = container.getAdvancedPayView();
					if (payView != curPayView) {
						payView.startLoad();
					}
				}
				mCoverView.setIsPromotion(mIsPromotion);
				mCoverView.startLoad(); // 加载罩子View
//				mHandler.sendEmptyMessage(LOAD_FINISH);
				
//				//从数据库中查询是否存在促销卷
//				ArrayList<PromotionBean> promotionList = startGetMessageBeanList();
//				isPromotion(promotionList);
				
				//如果是在促销期间，则刷新第一页的广告
				
				
				Message msg = mHandler.obtainMessage(LOAD_FINISH);
                mHandler.sendMessage(msg);
			}
		}).start();
	}
	

	private void initView() {
		LinearLayout buttonGroup = (LinearLayout) findViewById(R.id.upgrade_bottom_group);
		mUpgradeButton = (View) buttonGroup.findViewById(R.id.upgrade_button);
		mUpgradeBtnText = (TextView) buttonGroup.findViewById(R.id.upgrade_button_text);
		mUpgradeButton.setOnClickListener(this);
		mGoProgressBar = findViewById(R.id.advanced_pay_progress);
//		mSaleImageView = findViewById(R.id.sale_image_view);
		mBtnSaleImageView = findViewById(R.id.button_sale_image_view);
		
		//4.15开始使用消息中心进行促销
//		if (checkIsOnSale()) { // 正在促销
//			mSaleImageView.setVisibility(View.VISIBLE);
//			mBtnSaleImageView.setVisibility(View.VISIBLE);
//		} else {
//			mSaleImageView.setVisibility(View.GONE);
//			mBtnSaleImageView.setVisibility(View.GONE);
//		}
		mCoverView = (AdvancedPayCoverView) findViewById(R.id.cover_view);
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mCoverView.getLayoutParams();
		lp.topMargin = StatusBarHandler.getStatusbarHeight();
//		mCoverView.setPadding(0, StatusBarHandler.getStatusbarHeight(), 0, 0);
		mCoverView.setListener(new ClickBackListenr() {
			@Override
			public void onclickBack() {
				finish();
	}
		});
		mBgBottomView = findViewById(R.id.bg_bottom_view);
	}
	
	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case GO_TO_NEXT_PAGE :
						if (mScrollerViewGroup.isScrollerFinished()) {
							int index = mScrollerViewGroup.getCurrentViewIndex();
							if (index >= 0 && index < mScrollerViewGroup.getScreenCount() - 1) {
								index++;
								mScrollerViewGroup.gotoViewByIndex(index);
							}
						}
						break;
					case LOAD_FINISH :

                        if (mIsPromotion) {
                            // 正在促销
//                            mSaleImageView.setVisibility(View.VISIBLE);
                            mBtnSaleImageView.setVisibility(View.VISIBLE);
                        } else {
//                            mSaleImageView.setVisibility(View.GONE);
                            mBtnSaleImageView.setVisibility(View.GONE);
                        }
					    
						hideProgressBar();
						mIsInit = true;
						onScrollFinish(mScrollerViewGroup.getCurrentViewIndex());
						updateScrollPercent(true);
						break;
				}
			}
		};
	}
	
	private void postGotoNextPage() {
		removeGotoNextPageMessage();
		if (mHandler != null) {
			mHandler.sendEmptyMessageDelayed(GO_TO_NEXT_PAGE, AUTO_GOTO_NEXT_PAGE_DURATION);
		}
	}
	
	private void removeGotoNextPageMessage() {
		if (mHandler != null) {
			mHandler.removeMessages(GO_TO_NEXT_PAGE);
		}
	}
	
	private void initScrollerViewGroupData() {
		mScrollerViewGroup = (ScrollerViewGroup) findViewById(R.id.viewGroup);
		mScrollerViewGroup.setScreenScrollerListener(this);
		mScrollerViewGroup.addView(new AdvancedPayViewContainer(this,
				PAY_START_PAGE, this)); // 起始页
		mScrollerViewGroup.addView(new AdvancedPayViewContainer(this,
				PAY_NO_ADS, this)); // 无广告功能
		mScrollerViewGroup.addView(new AdvancedPayViewContainer(this,
				PAY_EFFECTOR, this)); // 特效
		mScrollerViewGroup.addView(new AdvancedPayViewContainer(this,
				PAY_WALLPAPER_FILTER, this)); // 壁纸滤镜
		mScrollerViewGroup.addView(new AdvancedPayViewContainer(this,
				PAY_SECURITY_LOCK, this)); // 安全锁
		mScrollerViewGroup.addView(new AdvancedPayViewContainer(this,
				PAY_SIDEBAR, this)); // 侧边栏
		mScrollerViewGroup.addView(new AdvancedPayViewContainer(this,
				PAY_GESTURE, this)); // 手势
		int childCount = mScrollerViewGroup.getChildCount();
		
//		View foreNotice = initFunForeNotice();
//		mScrollerViewGroup.addView(foreNotice);
		
		mScrollerViewGroup.setScreenCount(childCount); //设置总页面
		mScrollerViewGroup.gotoViewByIndexImmediately(mCurIndex); //打开的页面

		//设置指示器
		mIndicator = (DesktopIndicator) findViewById(R.id.indicator);
		mIndicator.setDefaultDotsIndicatorImage(R.drawable.advanced_pay_top_indicator_cur,
				R.drawable.advanced_pay_top_indicator_other);
		mIndicator.setDotIndicatorLayoutMode(ScreenIndicator.LAYOUT_MODE_ADJUST_PICSIZE);
		mIndicator.setDotIndicatorDrawMode(ScreenIndicatorItem.DRAW_MODE_INDIVIDUAL);
		mIndicator.setIndicatorListner(this); //设置指示器监听器
		mIndicator.setTotal(childCount); //设置总数
		mIndicator.setCurrent(mCurIndex); //指示器当前页
		mIndicatorLayout = (LinearLayout) findViewById(R.id.indicator_layout);
		mIndicatorLayout.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updatePaidView();
	}	

	private void updatePaidView() {
		if (mHasUpgraded) {
			mUpgradeBtnText.setText(mUnlockedTextId);
			mUpgradeButton.setOnClickListener(mFinishOnClickListener);
		}
	}
	
	private OnClickListener mFinishOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intentGoLauncher = new Intent();
			intentGoLauncher.setClass(DeskSettingAdvancedPayActivity.this, GoLauncher.class);
			intentGoLauncher.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentGoLauncher);
			DeskSettingAdvancedPayActivity.this.finish();
		}
	};
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		mCurIndex = intent.getIntExtra(CURRENT_INDEX, 0);
		mHasUpgraded = intent.getBooleanExtra(HAS_UPGRADED, false);
	}
	
	@Override
	public void onClick(View v) {
		if (v == mUpgradeButton) {
			goToPay();
			DeskSettingAdvancedPayActivity.this.finish();
		}
	}

	private void goToPay() {
		// 跳转到下载
		// GoAppUtils.gotoBrowserIfFailtoMarket(this,
		// DeskSettingUtils.GO_LAUNCHEREX_MARKET_KEY_URI,
		// DeskSettingUtils.GO_LAUNCHEREX_BROWSER_KEY_URI);
		if (Machine.isPurchaseByGetjarContury(this) && !DeskSettingUtils.checkPrimeSDFile(this) && !mIsPromotion) {
			FunctionPurchaseManager.getInstance(getApplicationContext()).downLoadGetJarKey();
			GuiThemeStatistics.payStaticData("j000", 0, mEntrance);
		} else {
			String googleLink = null;
			final String emptyString = "";
			final String googleReferalLink = googleLink != null ? googleLink : emptyString;
			// 跳转至Market，若有market，则跳market，若没有，则跳浏览器版market
			if (GoAppUtils.isMarketExist(this)) {
				GoAppUtils.gotoMarket(this, MarketConstant.APP_DETAIL
						+ DeskSettingUtils.KEY_PACKAGE_NAME_STRING + googleReferalLink);
			} else {
				Toast.makeText(this, getString(R.string.desksetting_pay_dialog_no_market_tips),
						Toast.LENGTH_LONG).show();

				//			AppUtils.gotoBrowser(this, Market.BROWSER_APP_DETAIL
				//					+ DeskSettingUtils.KEY_PACKAGE_NAME_STRING
				//					+ googleReferalLink);
			}

			GuiThemeStatistics.payStaticData("j000", 0, mEntrance);
			DeskSettingUtils.setGomarketPayEntrance(mEntrance);
			DeskSettingUtils.sendBroadCastRecheck(this); // 如果已经安装Key的话会主动调用验证 流程
		}
	}
	
	@Override
	public void clickIndicatorItem(int index) {
		mScrollerViewGroup.gotoViewByIndex(index); //点击指示器某个位置跳到某页
		mCurIndex = index;
	}

	@Override
	public void sliding(float percent) {
		if (MIN_PERCENT <= percent && percent <= MAX_PERCENT) {
			mScrollerViewGroup.getScreenScroller().setScrollPercent(percent); //滑动指示器
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
		removeGotoNextPageMessage();
		AdvancedPayViewContainer container = (AdvancedPayViewContainer) mScrollerViewGroup
				.getCurrentView();
		AdvancedPayBaseView payView = container.getAdvancedPayView();
		payView.finishAnimate();
	}

	@Override
	public void onFlingStart() {
	}

	@Override
	public void onScrollChanged(int newScroll, int oldScroll) {
		updateScrollPercent(false);
	}
		
	/**
	 * 刷新滚动量百分比
	 */
	private void updateScrollPercent(boolean isNeedUpdateTopIndex) {
		ScreenScroller scroller = mScrollerViewGroup.getScreenScroller();
		float scrollPercent = 1.0f * scroller.getScroll()
				/ scroller.getScreenSize();
		mCoverView.onScrollChange(scrollPercent);
		
		if (isNeedUpdateTopIndex) { // 更新顶部屏幕的下标
			mCurTopScreenIndex = (int) scrollPercent;
	}

		if (scroller.getScroll() >= 0 && scrollPercent <= scroller.getScreenCount() - 1) { // 超过实际范围不进行处理
			
			if (scrollPercent <= mCurTopScreenIndex - 1) {
				mCurTopScreenIndex = mCurTopScreenIndex - 1;
			} else if (scrollPercent >= mCurTopScreenIndex + 1) {
				mCurTopScreenIndex = mCurTopScreenIndex + 1;
			}
			
			int bottomColorScreenIndex = scrollPercent > mCurTopScreenIndex
					? mCurTopScreenIndex + 1
					: mCurTopScreenIndex - 1;
			// 底部颜色
			AdvancedPayViewContainer container = (AdvancedPayViewContainer) mScrollerViewGroup
					.getChildAt(bottomColorScreenIndex);
			AdvancedPayBaseView payView;
			if (container != null) {
				payView = container.getAdvancedPayView();
				int bottomColor = payView.getBgColor();
				if (mCurBottomColor != bottomColor) {
					mBgBottomView.setBackgroundColor(bottomColor);
					mCurBottomColor = bottomColor;
				}
			}

			// 顶部颜色，需要做Alpha变化
			container = (AdvancedPayViewContainer) mScrollerViewGroup
					.getChildAt(mCurTopScreenIndex);
			if (container != null) {
				payView = container.getAdvancedPayView();
				payView.setIsPromotion(mIsPromotion);
				if (mIsPromotion) {
				    container.setSingleMessage(mIsPromotion);
				}
				int topColor = payView.getBgColor();
				int alpha = 0;
				if (scrollPercent >= mCurTopScreenIndex) { // 右划
					alpha = (int) ((1.0f - (scrollPercent - (int) scrollPercent)) * 255);
				} else { // 左划
					alpha = (int) ((scrollPercent - (int) scrollPercent) * 255);
				}
				topColor = (topColor & 0x00FFFFFF) | (alpha << 24);
				mScrollerViewGroup.setBackgroundColor(topColor);
			}
		}
	}

	@Override
	public void onScreenChanged(int newScreen, int oldScreen) {
		mIndicator.setCurrent(newScreen); //设置指示器当前页
		mCurIndex = newScreen;
	}

	@Override
	public void onScrollFinish(int currentScreen) {
		if (mIsInit) {
			AdvancedPayViewContainer container = (AdvancedPayViewContainer) mScrollerViewGroup
					.getCurrentView();
			AdvancedPayBaseView curPayView = container.getAdvancedPayView();
			curPayView.startAnimate();
			int count = mScrollerViewGroup.getScreenCount();
			for (int i = 0; i < count; i++) {
				container = (AdvancedPayViewContainer) mScrollerViewGroup.getChildAt(i);
				AdvancedPayBaseView payView = container.getAdvancedPayView();
				if (payView != curPayView) {
					payView.resetAnimationFactor();
				}
			}
		}
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Process.killProcess(Process.myPid());
	}
	
	private void showProgressBar() {
		if (mGoProgressBar != null && mGoProgressBar.getVisibility() == View.INVISIBLE) {
			mGoProgressBar.setVisibility(View.VISIBLE);
		}
	}
	
	private void hideProgressBar() {
		if (mGoProgressBar != null && mGoProgressBar.getVisibility() == View.VISIBLE) {
			mGoProgressBar.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public void onAnimateFinish() {
		postGotoNextPage();
	}
	
	private static final String[] ON_SALE_COUNTRIES = new String[] { "us", "gb", "de", "ru", "jp",
			"au", "fr", "it", "ca", "br", "es", "se", "tw", "mx", "nl", "no", "kr" };
	private static final String ON_SALE_START_TIME = "2013-11-26 20:00"; // 促销开始时间（纽约时间）
	private static final String ON_SALE_END_TIME = "2013-11-29 20:00"; // 促销结束时间（纽约时间）
	private static final String ON_SALE_TIME_ZONE = "GMT-5"; // 纽约时区，即西五区
	/**
	 * 获取是否正在进行促销
	 * @return
	 */
	private boolean checkIsOnSale() {
		String curCountry = Machine.getCountry(this);
		for (String country : ON_SALE_COUNTRIES) {
			if (country.equals(curCountry)) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeZone(TimeZone.getTimeZone(ON_SALE_TIME_ZONE));
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH) + 1;
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int hours = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				String today = year + "-" + getTwoDigitsString(month) + "-"
						+ getTwoDigitsString(day) + " " + getTwoDigitsString(hours) + ":"
						+ getTwoDigitsString(minute);
				if (today.compareTo(ON_SALE_START_TIME) >= 0 && today.compareTo(ON_SALE_END_TIME) <= 0) {
					return true;
				}
				return false;
			}
		}
		return false;
	}
	
	private String getTwoDigitsString(int number) {
		if (number < 10) {
			return "0" + number;
		} else {
			return "" + number;
		}
	}
	
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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		Resources res = getResources();
		if (res instanceof DeskResources) {

			try {
				res.updateConfiguration(super.getResources().getConfiguration(), super.getResources()
						.getDisplayMetrics());
				Configuration config = res.getConfiguration(); //获得设置对象
				DisplayMetrics dm = res.getDisplayMetrics(); //获得屏幕参数：主要是分辨率，像素等。
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
	}
}
