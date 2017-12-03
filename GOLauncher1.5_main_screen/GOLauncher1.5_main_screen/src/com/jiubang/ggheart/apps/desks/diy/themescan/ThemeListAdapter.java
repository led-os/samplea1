package com.jiubang.ggheart.apps.desks.diy.themescan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.util.AppUtils;
import com.go.util.BroadCaster.BroadCasterObserver;
import com.go.util.graphics.DrawUtils;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.admob.AdConstanst;
import com.jiubang.ggheart.admob.GoDetailAdView;
import com.jiubang.ggheart.appgame.base.component.AppsDetail;
import com.jiubang.ggheart.appgame.gostore.base.component.AppsThemeDetailActivity;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.guide.RateGuideTask;
import com.jiubang.ggheart.apps.desks.diy.themescan.coupon.CouponManageActivity;
import com.jiubang.ggheart.apps.desks.purchase.getjar.GetJarThemePurchaseStateListener;
import com.jiubang.ggheart.apps.desks.purchase.getjar.GetJarThemePurchaseStateListener.BindListener;
import com.jiubang.ggheart.billing.ThemeAppInBillingManager;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.StatisticsData;
import com.jiubang.ggheart.data.theme.GoLockerThemeManager;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.data.theme.bean.SpecThemeViewConfig;
import com.jiubang.ggheart.data.theme.bean.ThemeBannerBean;
import com.jiubang.ggheart.data.theme.bean.ThemeBannerBean.BannerElement;
import com.jiubang.ggheart.data.theme.bean.ThemeInfoBean;
import com.jiubang.ggheart.data.theme.broadcastReceiver.MyThemeReceiver;
import com.jiubang.ggheart.launcher.CheckApplication;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 主题数据适配器
 *
 * @author yangbing
 * */
public class ThemeListAdapter extends BaseAdapter implements BroadCasterObserver {

	private static final int KILL_PROCESS_DELAY_TIME = 1500;
	public static final String GOOGLEMARKET_PREFIX = "market://";
	public static final String HTTP_PREFIX = "http://";
	public static final String HTTPS_PREFIX = "https://";
	public static final String WEBVIEW_PREFIX = "webview://";

	public static final int MSG_INAPP_PAID_FINISHED = 1;

	private static final int TAB_FEATURED = 0;
	private static final int TAB_INSTALLED = 1;

	private Context mContext = null;
	private LayoutInflater mLayoutInflater = null;
	private int mItemThemeCount = 0; // item项显示主题个数
	private boolean mIsDealSpecialResolution; // 是否处理特殊分辨率
	private int mSpecialResolutionWidth; // 特殊分辨率宽
	private int mSpecialResolutionHight; // 特殊分辨率高
	private ArrayList<ThemeInfoBean[]> mThemeDataArrays; // 数据
	private View.OnClickListener mItemClickListener;
	private View.OnClickListener mItemControlClickListener;
	private View.OnClickListener mItemImageClickListener;
	private RelativeLayout mGoStoreLayout = null; // 去go精品下载 布局
	private RelativeLayout mBannerLayout = null; // 主题Banner的布局
	private SpaceCalculator mSpaceCalculator;
	private boolean mIsOverScreen;
	private int mTabType;
	private ThemePurchaseManager mPurchaseManager;
	private BroadcastReceiver mDownloadReceiver = null;
	private HashMap<Long, Integer> mDoloadingMap;
	private boolean mIsLauncherFeature = false;
	private ThemeBannerBean mBannerBean;
	private HashMap<String, Bitmap> mBannerImageMap;
	private int mThemeType;
	private boolean mIsSpecTheme = false;
	private boolean mIsShowBanner = false;
	private GetJarThemePurchaseStateListener mBindTool;
	public ThemeListAdapter(Context context) {
		mContext = context;
		mPurchaseManager = ThemePurchaseManager.getInstance(mContext);
		mLayoutInflater = LayoutInflater.from(mContext);
		mSpaceCalculator = SpaceCalculator.getInstance(mContext);
		mDoloadingMap = new HashMap<Long, Integer>();
		mBannerImageMap = new HashMap<String, Bitmap>();
		initDownloadReceiver();
		initClickListener();
		isDealSpecialResolution();
		initGoStoreView();
	}

	/**
	 * <br>
	 * 功能简述:banner条 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	private void initThemeBannerView() {
		mBannerLayout = (RelativeLayout) mLayoutInflater.inflate(R.layout.theme_banner_view, null);
		mBannerLayout.setVisibility(View.VISIBLE);
		mBannerLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBannerBean != null) {
					if (mBannerBean.mType == ThemeConstants.LOCKER_UNINSTALL_BANNER) {
						CheckApplication.downloadAppFromMarketGostoreDetail(mContext,
						        PackageName.GO_LOCK_PACKAGE_NAME,
								LauncherEnv.Url.GOLOCKER_IN_THEME_WITH_GOOGLE_REFERRAL_LINK);
					} else if (mBannerBean.mElements != null && mBannerBean.mElements.size() > 0) {
						int tabID = ThemeConstants.STATICS_ID_DESK_BANNER;
						if (mBannerBean.mType == ThemeConstants.LOCKER_FEATURED_THEME_ID) {
							tabID = ThemeConstants.STATICS_ID_LOCKER_BANNER;
						}
						BannerElement element = mBannerBean.mElements.get(0);
						if (element.mId == BannerDetailActivity.COUPON_SPEC_ID) {
							tabID = BannerDetailActivity.COUPON_SPEC_ID;
						}
						GuiThemeStatistics.guiStaticData("",
								GuiThemeStatistics.OPTION_CODE_TAB_CLICK, 1, "",
								String.valueOf(tabID), "", "");
						if (element.mGroup != null
								&& element.mGroup.startsWith(GOOGLEMARKET_PREFIX)) {
							if (GoAppUtils.isMarketExist(mContext)) {
								GoAppUtils.gotoMarket(mContext, element.mGroup);
							}
						} else if (element.mGroup != null
								&& (element.mGroup.startsWith(HTTP_PREFIX) || element.mGroup
										.startsWith(HTTPS_PREFIX))) {
							GoAppUtils.gotoBrowserInRunTask(mContext, element.mGroup);
						} else if (element.mGroup != null
								&& element.mGroup.startsWith(WEBVIEW_PREFIX)) {
							String url = element.mGroup.replace(WEBVIEW_PREFIX, HTTP_PREFIX);
							GoAppUtils.gotoWebView(mContext, url);
						} else {
							((ThemeManageActivity) mContext).gotoBannerList(
									mBannerBean.mElements.get(0).mId,
									mBannerBean.mElements.get(0).mName);
						}
					}
				}
			}
		});
		android.widget.AbsListView.LayoutParams layoutParams = new android.widget.AbsListView.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		mBannerLayout.setLayoutParams(layoutParams);
	}

	/**
	 * 去gostore下载更多界面
	 * */
	private void initGoStoreView() {
		if (mGoStoreLayout == null) {
			mGoStoreLayout = (RelativeLayout) mLayoutInflater.inflate(R.layout.theme_gostore, null);
			mGoStoreLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!GoAppUtils.isGoLockerExist(mContext)
							&& mThemeType == ThemeConstants.LOCKER_FEATURED_THEME_ID) {
						CheckApplication.downloadAppFromMarketGostoreDetail(mContext,
						        PackageName.GO_LOCK_PACKAGE_NAME,
								LauncherEnv.Url.GOLOCKER_IN_THEME_WITH_GOOGLE_REFERRAL_LINK);
					} else if (mContext instanceof ThemeManageActivity) {
						((ThemeManageActivity) mContext).gotoGoStore();
					} else if (mContext instanceof BannerDetailActivity) {
						((BannerDetailActivity) mContext).gotoGoStore();
					}
				}
			});
		}
		mGoStoreLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int acition = event.getAction();
				switch (acition) {
					case MotionEvent.ACTION_UP :
						v.performClick();
						break;

					default :
						break;
				}
				return true;
			}
		});
		android.widget.AbsListView.LayoutParams layoutParams = new android.widget.AbsListView.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.height = mSpaceCalculator.getGoStoreBarHeight();
		mGoStoreLayout.setLayoutParams(layoutParams);
	}

	/**
	 * 判断是否要对特殊分辨率处理,例如：moto me860 density=1.5 540*960
	 * */
	private void isDealSpecialResolution() {

		int width = mContext.getResources().getDimensionPixelSize(R.dimen.mytheme_pic_width);
		int height = mContext.getResources().getDimensionPixelSize(R.dimen.mytheme_pic_height);
		final int screenwidth = SpaceCalculator.sPortrait
				? mContext.getResources().getDisplayMetrics().widthPixels
				: mContext.getResources().getDisplayMetrics().heightPixels;
		final int normal_HDPI_Width = 480;
		float density = 1.5f;
		if (DrawUtils.sDensity == density && screenwidth != normal_HDPI_Width) {
			mIsDealSpecialResolution = true;
			float scale = (float) screenwidth / (float) normal_HDPI_Width;
			mSpecialResolutionWidth = (int) (width * scale);
			mSpecialResolutionHight = (int) (height * scale);
		}

	}

	/**
	 * 设置item视图单击事件监听
	 * */
	public void setmItemClickListener(View.OnClickListener mItemClickListener) {
		this.mItemClickListener = mItemClickListener;
	}

	/**
	 * 应用锁屏主题后，更改数据状态
	 * 
	 * */
	public ArrayList<ThemeInfoBean> updateInstalledLockerList(String packageName,
			String oldPackageName) {
		ArrayList<ThemeInfoBean> mThemeDatas = ThemeDataManager.getInstance(mContext).getThemeData(
				ThemeConstants.LOCKER_INSTALLED_THEME_ID);
		if (mThemeDatas == null || mThemeDatas.size() <= 0 || oldPackageName == null) {
			return null;
		}
		for (ThemeInfoBean bean : mThemeDatas) {
			if (oldPackageName.equals(bean.getPackageName())) {
				bean.setIsCurTheme(false);
			}
			if (packageName.equals(bean.getPackageName())) {
				bean.setIsCurTheme(true);
			}
		}
		return mThemeDatas;
	}

	/**
	 * 设置主题数据
	 * */
	public void setThemeDatas(ArrayList<ThemeInfoBean> mThemeDatas, ThemeBannerBean banner, int themeType) {
		if (mThemeDatas == null || mThemeDatas.size() == 0) {
			return;
		}
		// 因为大范围报空指针问题，但查不到原因这里做一次检查
		Iterator iterator = mThemeDatas.iterator();
		while (iterator.hasNext()) {
			ThemeInfoBean info = (ThemeInfoBean) iterator.next();
			if (info.getPackageName() == null) {
				iterator.remove();
				continue;
			}
		}
		mBannerBean = banner;
		clearThemeDatas();
		mThemeDataArrays = new ArrayList<ThemeInfoBean[]>();
		mItemThemeCount = SpaceCalculator.getThemeListItemCount();
		mThemeType = themeType;
		if (mThemeType == ThemeConstants.LAUNCHER_FEATURED_THEME_ID
				|| mThemeType == ThemeConstants.LOCKER_FEATURED_THEME_ID
				|| mThemeType == ThemeConstants.LAUNCHER_SPEC_THEME_ID
				|| mThemeType == ThemeConstants.LAUNCHER_HOT_THEME_ID) {
			mPurchaseManager.registerObserver(this);
			mTabType = TAB_FEATURED;
		} else {
			mTabType = TAB_INSTALLED;
		}
		if (mThemeType == ThemeConstants.LAUNCHER_FEATURED_THEME_ID
				|| ThemeConstants.LOCKER_FEATURED_THEME_ID == mThemeType) {
			mIsLauncherFeature = true;
		} else {
			mIsLauncherFeature = false;
		}

		if (mThemeType == ThemeConstants.LAUNCHER_SPEC_THEME_ID) {
			mIsSpecTheme = true;
		}
		ArrayList<ThemeInfoBean> beanList = new ArrayList<ThemeInfoBean>();
		for (ThemeInfoBean bean : mThemeDatas) {
			beanList.add(bean);
			if (beanList.size() == mItemThemeCount) {
				mThemeDataArrays.add(beanList.toArray(new ThemeInfoBean[] {}));
				beanList.clear();
			}
		}
		if (beanList.size() > 0) {
			mThemeDataArrays.add(beanList.toArray(new ThemeInfoBean[] {}));
			beanList.clear();
			beanList = null;
		}
		int count = mThemeDataArrays.size();
		mIsOverScreen = mSpaceCalculator.isOverscreen(count, isShowBanner());
		changeGoStoreBannerText();
		notifyDataSetChanged();
	}

	public boolean isOverScreen() {
		return mIsOverScreen;
	}
	
	private void changeGoStoreBannerText() {
		if (!GoAppUtils.isGoLockerExist(mContext)
				&& mThemeType == ThemeConstants.LOCKER_FEATURED_THEME_ID) {
			((TextView) mGoStoreLayout.findViewById(R.id.theme_findmore_text))
					.setText(R.string.thememanager_install_locker_tip);
		} else {
			((TextView) mGoStoreLayout.findViewById(R.id.theme_findmore_text))
					.setText(R.string.theme_findmore);
		}
	}
	/**
	 * 清空数据
	 * */
	public void clearThemeDatas() {
		if (mThemeDataArrays != null) {
			mThemeDataArrays.clear();
			mThemeDataArrays = null;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		int count = 0;
		if (mThemeDataArrays == null || mThemeDataArrays.isEmpty()) {
			return 0;
		} else {
			if (mThemeType == ThemeConstants.LAUNCHER_INSTALLED_THEME_ID) {
				++count;
			}
			count = mThemeDataArrays.size();
			if (isShowBanner()) {
				++count;
			}
			if (mIsSpecTheme) {
				return count;
			}
			if (mIsOverScreen
					&& (mThemeType != ThemeConstants.LOCKER_FEATURED_THEME_ID || GoAppUtils
							.isGoLockerExist(mContext))) {
				return count + 1;
			}
			int themeListItemCount = count;
			if (isShowBanner()) {
				--themeListItemCount;
			}
			if (mSpaceCalculator.calculateIsCover(themeListItemCount, isShowBanner())
					&& (mThemeType != ThemeConstants.LOCKER_FEATURED_THEME_ID || GoAppUtils
							.isGoLockerExist(mContext))) {
				return count + 1;
			}
		}

		return count;
	}

	@Override
	public Object getItem(int position) {

		return mThemeDataArrays == null ? null : mThemeDataArrays.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 当第一个的时候&&当主题Banner里面有缓存数据的时候
		if (position == 0 && isShowBanner()) {

			initThemeBannerView();
			ImageView img = (ImageView) mBannerLayout.findViewById(R.id.banner_view);
			if (mBannerBean != null) {
				if (mBannerBean.mType == ThemeConstants.LOCKER_UNINSTALL_BANNER) {
					Locale locale = Locale.getDefault();
					String lan = locale.getLanguage();
					if (lan.equals("zh")) {
						img.setImageResource(R.drawable.theme_manager_nogolocker_cn_title);
					} else {
						img.setImageResource(R.drawable.theme_manager_nogolocker_en);
					}
				} else {
					BannerElement element = mBannerBean.mElements.get(0);
					String id = null;
					for (int i = 0; element.mImgids != null && i < element.mImgids.length; i++) {
						Bitmap bmp = null;
						id = element.mImgids[i];
						bmp = mBannerImageMap.get(id);
						if (element.mSource == 0 || element.mImgUrl == null
								|| element.mImgUrl.isEmpty()
								&& (element.mImgids != null && element.mImgids.length > 0)) {
							if (bmp == null) {
								Drawable drawable = ThemeImageManager.getInstance(mContext)
										.getImageById(id, LauncherEnv.Path.GOTHEMES_PATH + "icon/",
												this);
								if (drawable != null) {
									bmp = ((BitmapDrawable) drawable).getBitmap();
								}
							}
						} else if (element.mImgUrl != null && !element.mImgUrl.isEmpty()) {
							String url = element.mImgUrl.get(0);
							if (bmp == null) {

								Drawable drawable = ThemeImageManager.getInstance(mContext)
										.getImageByUrl(url, this,
												LauncherEnv.Path.GOTHEMES_PATH + "icon/", id);
								if (drawable != null) {
									bmp = ((BitmapDrawable) drawable).getBitmap();
								}
							}
						}
						if (bmp != null) {
							mBannerImageMap.put(id, bmp);
						}
						if (bmp != null) {
							img.setImageBitmap(bmp);
							break;
						}
					}
				}
			}
			return mBannerLayout;
		}
		if (isShowGoStore(position)) {
			return mGoStoreLayout;
		} else {
			if (isShowBanner()) {
				position = position - 1;
			}
			ItemThemeScanView itemScan = null;
			ThemeInfoBean[] itemBeanArrays; // 数据
			if (mThemeDataArrays != null) {
				itemBeanArrays = mThemeDataArrays.get(position);
			} else {
				itemBeanArrays = new ThemeInfoBean[mItemThemeCount];
			}
			if (convertView != null && convertView instanceof ItemThemeScanView) {
				// itemview重用
				itemScan = (ItemThemeScanView) convertView;
				int height = mSpaceCalculator.calculateItemThemeScanViewHeight();
				if (height != itemScan.getHeight()) {
					ViewGroup.LayoutParams params = itemScan.getLayoutParams();
					params.height = height;
					itemScan.setLayoutParams(params);
				}
				// itemScan.cleanup();
				ArrayList<ItemThemeView> itemThemeViews = itemScan.getmItemThemeViews();
				if (itemThemeViews.size() == itemBeanArrays.length) {
					itemScan.cleanupItemThemeView();
					for (int m = 0; m < itemBeanArrays.length; m++) {
						ItemThemeView itemThemeView = itemThemeViews.get(m);
						itemThemeView.setItemThemeViewProperties();
						setDownloadPriveView(itemThemeViews.get(m), itemBeanArrays[m]);

						itemThemeViews.get(m).setThemeData(itemBeanArrays[m],
								mItemThemeCount * position + m + 1);
					}
					return itemScan;
				} else {
					itemScan.cleanup();
				}
			} else {
				itemScan = inflateConvertView();
			}

			for (int m = 0; m < itemBeanArrays.length; m++) {
				itemScan.addItemView(inflateItemThemeView(itemBeanArrays[m], mItemThemeCount
						* position + m + 1));
			}
			return itemScan;
		}
	}
	/**
	 * 生成listview的item项视图
	 * */
	private ItemThemeScanView inflateConvertView() {
		android.widget.AbsListView.LayoutParams layoutParams = new android.widget.AbsListView.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		ItemThemeScanView itemScan = new ItemThemeScanView(mContext, !mIsSpecTheme);
		layoutParams.height = mSpaceCalculator.calculateItemThemeScanViewHeight();
		itemScan.setLayoutParams(layoutParams);
		return itemScan;
	}

	/**
	 * 生成每个主题的显示视图
	 * */
	private ItemThemeView inflateItemThemeView(ThemeInfoBean bean, int position) {
		ItemThemeView itemView = (ItemThemeView) mLayoutInflater.inflate(R.layout.item_theme_view,
				null);
		itemView.setThemeData(bean, position);
		if (bean != null && bean.getBeanType() == ThemeConstants.LAUNCHER_SPEC_THEME_ID) {
			ImageView img = (ImageView) itemView.findViewById(R.id.image);
			if (img != null) {
				img.setBackgroundResource(R.drawable.spec_theme_bg);
			}
		}
		if (mIsDealSpecialResolution) {
			try {
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						mSpecialResolutionWidth, mSpecialResolutionHight);
				ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
				imageView.setLayoutParams(params);
			} catch (Exception e) {
			}
		}
		setDownloadPriveView(itemView, bean);
		return itemView;

	}

	/**
	 * 横竖屏切换
	 * */
	public void changeOrientation() {
		mItemThemeCount = SpaceCalculator.getThemeListItemCount();
		// if (SpaceCalculator.sPortrait) {
		// mItemThemeCount = ITEM_DATA_COUNT_V;
		// } else {
		// mItemThemeCount = ITEM_DATA_COUNT_H;
		// }

	}

	/**
	 * <br>
	 * 功能简述:控制下载按钮的显示以及点击响应 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param itemView
	 * @param infoBean
	 */
	private void setDownloadPriveView(ItemThemeView itemView, ThemeInfoBean infoBean) {
		if (mPurchaseManager == null || itemView == null || infoBean == null) {
			return;
		}
		int type;
		if (infoBean.getBeanType() == ThemeConstants.LOCKER_INSTALLED_THEME_ID
				|| infoBean.getBeanType() == ThemeConstants.LOCKER_FEATURED_THEME_ID) {
			type = ThemeManageView.LOCKER_THEME_VIEW_ID;
		} else {
			type = ThemeManageView.LAUNCHER_THEME_VIEW_ID;
		}
		boolean isVip = ThemePurchaseManager.getCustomerLevel(mContext, type) == ThemeConstants.CUSTOMER_LEVEL0
				? false
				: true;
		boolean isSpecTheme = infoBean.getBeanType() == ThemeConstants.LAUNCHER_SPEC_THEME_ID;
		//		Button button = (Button) itemView.findViewById(R.id.imgbtn_get);
		TextView button = (TextView) itemView.findViewById(R.id.imgbtn_get);
		button.setCompoundDrawablePadding(0);
		button.setGravity(Gravity.CENTER);
		ProgressBar progressBar = (ProgressBar) itemView
				.findViewById(R.id.theme_detail_download_progress);
		View image = itemView.findViewById(R.id.image);
		Drawable drawable = null;
		//		int padding = mContext.getResources().getDimensionPixelSize(R.dimen.mytheme_button_drawable_padding);
		if (mTabType == TAB_FEATURED) {
			if (button.getVisibility() != View.VISIBLE
					&& !isShowProgressBar(infoBean.getFeaturedId())) {
				button.setVisibility(View.VISIBLE);
			}
			drawable = button.getBackground();
			if (mPurchaseManager.hasDownloaded(infoBean.getThemeName(), infoBean.getPackageName())) {
				if (isSpecTheme) {
					button.setBackgroundResource(R.drawable.spec_theme_btn_selector);
					//					button.setGravity(Gravity.CENTER);
				} else {
					drawable = mContext.getResources().getDrawable(R.drawable.item_theme_apply);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(),
							drawable.getMinimumHeight());
					// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
					//					button.setCompoundDrawablePadding(padding);
					button.setCompoundDrawables(drawable, null, null, null);
				}

				button.setText(R.string.theme_pages_apply);
				progressBar.setVisibility(View.GONE);
			} else if (isShowProgressBar(infoBean.getFeaturedId())) {
				if (progressBar.getVisibility() != View.VISIBLE) {
					progressBar.setVisibility(View.VISIBLE);
				}
				button.setVisibility(View.GONE);
				updateProgressBar(progressBar, infoBean.getFeaturedId());
			} else if (isVip
					|| ThemePurchaseManager.queryPurchaseState(mContext, infoBean.getPackageName()) != null) {
				progressBar.setVisibility(View.GONE);
				if (isSpecTheme) {
					button.setBackgroundResource(R.drawable.spec_theme_btn_selector);
					//					button.setGravity(Gravity.CENTER);
				} else {
					//					button.setBackgroundResource(R.drawable.item_theme_get_bg_selector);
					//					button.setPadding(padding.left, 0, 0, 0);
					drawable = mContext.getResources().getDrawable(R.drawable.item_theme_get_now);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(),
							drawable.getMinimumHeight());
					//					button.setCompoundDrawablePadding(padding);
					button.setCompoundDrawables(drawable, null, null, null);
				}
				button.setText(R.string.theme_detail_download);
			} else {
				progressBar.setVisibility(View.GONE);
				if (isSpecTheme) {
					button.setBackgroundResource(R.drawable.spec_theme_btn_selector);
					//					button.setGravity(Gravity.CENTER);
					button.setText(R.string.theme_featured_get);
				} else {
					if (infoBean.getFeeType() == ThemeInfoBean.FEETYPE_GETJAR
							&& !infoBean.isZipTheme()) {
						drawable = mContext.getResources()
								.getDrawable(R.drawable.item_theme_getjar);
						button.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
						button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
						button.setText(R.string.theme_detail_getfree); //getjar
						float textSize = button.getPaint().getTextSize();
						int txtviewW = mContext.getResources().getDimensionPixelSize(
								R.dimen.mytheme_pic_width);
						int leftPadding = (int) ((txtviewW - textSize) / 2);
						button.setCompoundDrawablePadding(leftPadding
								- drawable.getIntrinsicWidth());
					} else if (infoBean.getFeeType() == ThemeInfoBean.FEETYPE_PAID) {
						drawable = mContext.getResources().getDrawable(R.drawable.item_theme_buy);
						button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
						button.setText(R.string.theme_detail_buynow); //收费
					} else if (infoBean.getFeeType() == ThemeInfoBean.FEETYPE_FREE) {
						drawable = mContext.getResources().getDrawable(
								R.drawable.item_theme_get_now);
						button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
						button.setText(R.string.theme_detail_download); //免费
					} else if (infoBean.getPayType() != null && infoBean.getPayType().size() > 1) {
						drawable = mContext.getResources().getDrawable(
								R.drawable.item_theme_download);
						button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
						button.setText(R.string.theme_detail_getnow); //多种收费
					}
				}
			}
			itemView.setOnClickListener(null);
			button.setClickable(true);
			button.setOnClickListener(mItemControlClickListener);
			button.setTag(itemView);
			if (mContext instanceof BannerDetailActivity) {
				SpecThemeViewConfig config = ((BannerDetailActivity) mContext).getViewConfig();
				if (config != null) {
					button.setTextColor(config.mBtnTextColor);
				} else {
					button.setTextColor(Color.BLACK);
				}
			}
			image.setClickable(true);
			image.setTag(itemView);
			image.setOnClickListener(mItemImageClickListener);
			// }
		} else {
			//modified by liulixia 本地主题也添加button，直接应用主题
			if (progressBar.getVisibility() != View.GONE) {
				progressBar.setVisibility(View.GONE);
			}
			if (button.getVisibility() != View.VISIBLE) {
				button.setVisibility(View.VISIBLE);
			}
			drawable = mContext.getResources().getDrawable(R.drawable.item_theme_apply);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
			//			button.setCompoundDrawablePadding(padding);
			button.setCompoundDrawables(drawable, null, null, null);
			if (infoBean.getBeanType() == ThemeConstants.THEMEM_COUPON_ID) {
				button.setText(R.string.btn_coupon);
			} else {
				button.setText(R.string.theme_pages_apply);
			}
			button.setClickable(true);
			button.setOnClickListener(mItemControlClickListener);
			button.setTag(itemView);

			itemView.setOnClickListener(mItemClickListener);
			image.setOnClickListener(null);
			image.setClickable(false);
		}
	}

	private void initClickListener() {
		mItemControlClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ItemThemeView itemThemeView = (ItemThemeView) v.getTag();
				ThemeInfoBean infoBean = itemThemeView.getThemeData();
				if (infoBean.getBeanType() == ThemeConstants.THEMEM_COUPON_ID) {
					Intent intent = new Intent(mContext, CouponManageActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intent);
					return;
				}
				if ((infoBean.getBeanType() == ThemeConstants.LOCKER_FEATURED_THEME_ID || infoBean
						.getBeanType() == ThemeConstants.LOCKER_INSTALLED_THEME_ID)
						&& !GoAppUtils.isGoLockerExist(mContext)) {
					CheckApplication.downloadAppFromMarketGostoreDetail(mContext,
					        PackageName.GO_LOCK_PACKAGE_NAME,
							LauncherEnv.Url.GOLOCKER_IN_THEME_WITH_GOOGLE_REFERRAL_LINK);
					return;
				}
				if (infoBean.getBeanType() == ThemeConstants.LAUNCHER_FEATURED_THEME_ID
						|| infoBean.getBeanType() == ThemeConstants.LAUNCHER_HOT_THEME_ID
						|| infoBean.getBeanType() == ThemeConstants.LAUNCHER_SPEC_THEME_ID
						|| infoBean.getBeanType() == ThemeConstants.LOCKER_FEATURED_THEME_ID) {
					if (infoBean.isInAppPay()) {
						mPurchaseManager.handleInAppClick(infoBean, (Activity) mContext,
								itemThemeView.getmPosition());
					} else {
						mPurchaseManager.handleNormalFeaturedClickEvent(mContext, infoBean,
								itemThemeView.getmPosition());
					}
					PreferencesManager pm = new PreferencesManager(mContext,
							IPreferencesIds.FEATUREDTHEME_CONFIG, Context.MODE_PRIVATE);
					boolean bool = pm.getBoolean(IPreferencesIds.HAD_SHOW_VIP_TIP, false);
					int type = ThemeManageView.LAUNCHER_THEME_VIEW_ID;
					if (infoBean.getBeanType() == ThemeConstants.LOCKER_FEATURED_THEME_ID || infoBean
						.getBeanType() == ThemeConstants.LOCKER_INSTALLED_THEME_ID) {
						type = ThemeManageView.LOCKER_THEME_VIEW_ID;
					}
					if (!bool
							&& infoBean.getFeeType() != ThemeInfoBean.FEETYPE_FREE
							&& ThemePurchaseManager.getCustomerLevel(mContext, type) == ThemeConstants.CUSTOMER_LEVEL0) {
						ThemePurchaseManager.getInstance(mContext).savePaidThemePkg(
								infoBean.getPackageName());
					}
				} else {
					// 应用
					String type = infoBean.getThemeType();
					int tabId;
					if (infoBean.getBeanType() == ThemeConstants.LOCKER_INSTALLED_THEME_ID) {
						tabId = ThemeConstants.STATICS_ID_LOCKER_INSTALL;
					} else {
						tabId = ThemeConstants.STATICS_ID_DESK_INSTALL;
					}
					if (null != type && type.equals(ThemeInfoBean.THEMETYPE_GETJAR)
							&& GoAppUtils.isAppExist(mContext, infoBean.getPackageName())) {
						if (ThemeManager.canBeUsedTheme(mContext, infoBean.getPackageName())) {
							applyTheme(infoBean);
						} else if (AppUtils.getServiceInfo(
								mContext,
								new ComponentName(infoBean.getPackageName(), infoBean
										.getPackageName() + ThemeConstants.GET_JAR_THEME_SERVICE)) != null) {
							mBindTool = new GetJarThemePurchaseStateListener();
							mBindTool.bindService(mContext, infoBean, mBindListener);
						} else {
							Intent intent = new Intent();
							intent = mContext.getPackageManager().getLaunchIntentForPackage(
									infoBean.getPackageName());
							intent.putExtra(MyThemeReceiver.PKGNAME_STRING, mContext.getPackageName());
							intent.putExtra(MyThemeReceiver.SUPPORT_COUPON, true);
							mContext.startActivity(intent);
						}
					} else {
						StatisticsData
								.countThemeTabData(StatisticsData.THEME_TAB_ID_LOCAL_THEME_APPLY);
						GuiThemeStatistics.guiStaticData(infoBean.getPackageName(),
								GuiThemeStatistics.OPTION_CODE_APPLY_THEME, 1, "", String
										.valueOf(tabId), String.valueOf(itemThemeView
										.getmPosition()), ThemeManager.getInstance(mContext)
										.getCurThemePackage());
						if (infoBean.getBeanType() == ThemeConstants.LOCKER_INSTALLED_THEME_ID) { //锁屏主题
							String curPackageName = infoBean.getPackageName();
							if (curPackageName != null) {
								boolean isGetJar = false;
								boolean hasPurchased = true;
								try {
									Context otherAppsContext = null;
									otherAppsContext = mContext.createPackageContext(
											curPackageName, Context.CONTEXT_IGNORE_SECURITY);
									int strid = otherAppsContext.getResources().getIdentifier(
											"isGetJar", "string", curPackageName);
									if (strid != 0) {
										String str = otherAppsContext.getResources().getString(
												strid);
										isGetJar = str != null && str.equals("true");
									}
									if (isGetJar) {
										// 是否已经付费
										SharedPreferences sharedPrefs = otherAppsContext
												.getSharedPreferences(curPackageName,
														Context.MODE_WORLD_READABLE);
										if (sharedPrefs != null) {
											hasPurchased = sharedPrefs.getBoolean("purchased",
													false);
										}
									}
								} catch (NameNotFoundException e) {

								}
								String oldPackageName = ThemeManager.getInstance(mContext)
										.getCurLockerTheme();
								new GoLockerThemeManager(mContext).changeLockTheme(curPackageName);
								if ((isGetJar && hasPurchased) || !isGetJar) {
									ArrayList<ThemeInfoBean> beanList = updateInstalledLockerList(
											curPackageName, oldPackageName);
									setThemeDatas(beanList, mBannerBean, mThemeType);
									Toast.makeText(mContext, R.string.locker_theme_apply_ok, Toast.LENGTH_LONG)
											.show();
								}
							}
						} else {
							applyTheme(infoBean);
						}
					}
				}
			}

		};

		mItemImageClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ItemThemeView itemThemeView = (ItemThemeView) v.getTag();
				ThemeInfoBean infoBean = itemThemeView.getThemeData();
				if ((infoBean.getBeanType() == ThemeConstants.LOCKER_FEATURED_THEME_ID || infoBean
						.getBeanType() == ThemeConstants.LOCKER_INSTALLED_THEME_ID)
						&& !GoAppUtils.isGoLockerExist(mContext)) {
					CheckApplication.downloadAppFromMarketGostoreDetail(mContext,
					        PackageName.GO_LOCK_PACKAGE_NAME,
							LauncherEnv.Url.GOLOCKER_IN_THEME_WITH_GOOGLE_REFERRAL_LINK);
					return;
				}
				if (infoBean.getFeaturedId() != 0) {
					gotoFeaturedThemeDetailPage(infoBean, itemThemeView.getmPosition());
					StatisticsData
							.countThemeTabData(StatisticsData.THEME_TAB_ID_CHOICENESS_THEME_DETAIL);
					//					int srcType = 0;
					//					if (infoBean.isInAppPay()) {
					//						srcType = 1;
					//					}
					int tabId = 0;
					if (infoBean.getBeanType() == ThemeConstants.LAUNCHER_FEATURED_THEME_ID) {
						tabId = ThemeConstants.STATICS_ID_FEATURED;
					} else if (infoBean.getBeanType() == ThemeConstants.LAUNCHER_HOT_THEME_ID) {
						tabId = ThemeConstants.STATICS_ID_HOT;
					} else if (infoBean.getBeanType() == ThemeConstants.LAUNCHER_SPEC_THEME_ID) {
						tabId = infoBean.getSortId();
					} else if (infoBean.getBeanType() == ThemeConstants.LOCKER_FEATURED_THEME_ID) {
						tabId = ThemeConstants.STATICS_ID_LOCKER;
					}
					//					GuiThemeStatistics.getInstance(mContext).saveUserDetailClick(mContext,
					//							infoBean.getPackageName(), itemThemeView.getmPosition(), staticsType,
					//							String.valueOf(srcType), String.valueOf(tabId));
					GuiThemeStatistics
							.guiStaticData(String.valueOf(infoBean.getFeaturedId()),
									GuiThemeStatistics.OPTION_CODE_PREVIEW_IMG_CLICK, 1, "",
									String.valueOf(tabId),
									String.valueOf(itemThemeView.getmPosition()), "");

				} else {
					mPurchaseManager.handleNormalFeaturedClickEvent(mContext, infoBean,
							itemThemeView.getmPosition());
				}
			}
		};

	}

	/**
	 * 应用主题
	 */
	private void applyTheme(ThemeInfoBean mInfoBean) {
		if (mInfoBean == null) {
			return;
		}
		final String pkgName = mInfoBean.getPackageName();
		if (pkgName.equals(ThemeManager.getInstance(mContext).getCurThemePackage())) {
			Toast.makeText(mContext, R.string.theme_already_using, Toast.LENGTH_SHORT).show();
			return;
		}
		if (!mInfoBean.isNewTheme()) {
			// 不是大主题
			Intent intentGoLauncher = new Intent();
			intentGoLauncher.setClassName(mContext, IGoLauncherClassName.GOLAUNCHER_ACTIVITY);
			intentGoLauncher.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intentGoLauncher.putExtra(MyThemeReceiver.ACTION_TYPE_STRING,
					MyThemeReceiver.CHANGE_THEME);
			intentGoLauncher.putExtra(MyThemeReceiver.PKGNAME_STRING, pkgName);
			mContext.startActivity(intentGoLauncher);
			//			Intent intent = new Intent(ICustomAction.ACTION_THEME_BROADCAST);
			//			intent.putExtra(MyThemeReceiver.ACTION_TYPE_STRING, MyThemeReceiver.CHANGE_THEME);
			//			intent.putExtra(MyThemeReceiver.PKGNAME_STRING, pkgName);
			//			mContext.sendBroadcast(intent);
			ThemeManageActivity.exit(mContext, KILL_PROCESS_DELAY_TIME);
		} else {
			// 大主题
			initNewThemeResource();
			showDialog(pkgName, mInfoBean);
		}
	}

	private AlertDialog mDialog;
	private String mGolauncherText;
	private String mGowidgetText;
	private String mGolockText;

	/**
	 * 初始化大主题需要的一些资源
	 * */
	private void initNewThemeResource() {

		mGolauncherText = mContext.getResources().getString(R.string.new_theme_golauncher);
		mGowidgetText = mContext.getResources().getString(R.string.new_theme_gowidget);
		mGolockText = mContext.getResources().getString(R.string.new_theme_golock);

	}

	private void showDialog(final String pkgName, final ThemeInfoBean mInfoBean) {

		LayoutInflater factory = LayoutInflater.from(mContext);
		View view = factory.inflate(R.layout.theme_detail_alertdialog, null);

		final MyAdapter myAdapter = new MyAdapter(mContext, mInfoBean);
		myAdapter.filterNotExistTheme();

		ListView myListView = (ListView) view.findViewById(R.id.Theme_detail_alertdialog_list);
		myListView.setAdapter(myAdapter);
		myListView.setCacheColorHint(Color.TRANSPARENT);
		myListView.setDivider(null);

		Button button = (Button) view.findViewById(R.id.theme_detail_alertdialog_sure);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				Context context = mContext;
				if (mInfoBean != null && context != null) {
					// 存在桌面主题且被选中
					if (mInfoBean.ismExistGolauncher()
							&& myAdapter.getmCheckBoxState().get(mGolauncherText)) {
						Intent it = new Intent();
						it.setClassName(context, IGoLauncherClassName.GOLAUNCHER_ACTIVITY);
						context.startActivity(it);

						Intent intent = new Intent(ICustomAction.ACTION_THEME_BROADCAST);
						intent.putExtra(MyThemeReceiver.ACTION_TYPE_STRING,
								MyThemeReceiver.CHANGE_THEME);
						intent.putExtra(MyThemeReceiver.PKGNAME_STRING, pkgName);
						context.sendBroadcast(intent);
					}
					// 存在widget主题且被选中
					if (mInfoBean.getGoWidgetPkgName() != null
							&& myAdapter.getmCheckBoxState().get(mGowidgetText)) {
						Intent it = new Intent(ICustomAction.ACTION_CHANGE_WIDGETS_THEME);
						it.putExtra(ICustomAction.WIDGET_THEME_KEY, pkgName);
						context.sendBroadcast(it);
					}
					// 存在GO锁屏主题且被选中
					if (mInfoBean.ismExistGolock()
							&& myAdapter.getmCheckBoxState().get(mGolockText)) {
						if (GoAppUtils.isGoLockerExist(context)) {
							try {
								String newThemePkgName = mInfoBean.getPackageName();
								if (newThemePkgName != null) {
									Intent it = new Intent(
											ICustomAction.ACTION_SEND_TO_GOLOCK_FOR_THEME_DETAIL);
									it.putExtra(ThemeDetailView.NEW_THEME_KEY, newThemePkgName);
									context.sendBroadcast(it);
									
									RateGuideTask.getInstacne(mContext.getApplicationContext())
											.scheduleShowRateDialog(RateGuideTask.EVENT_APPLY_THEME);
								}
							} catch (Exception e) {
							}
						} else {

						}
					}
				}
				dimissDialog();
			}
		});

		mDialog = new AlertDialog.Builder(mContext).create();
		mDialog.show();

		WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
		layoutParams.width = android.view.ViewGroup.LayoutParams.FILL_PARENT;
		layoutParams.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

		mDialog.getWindow().setGravity(Gravity.CENTER);
		mDialog.getWindow().setAttributes(layoutParams);

		Window window = mDialog.getWindow();
		window.setBackgroundDrawableResource(R.drawable.theme_detail_menu_bg);
		window.setContentView(view);

	}

	private void dimissDialog() {
		mDialog.dismiss();
	}

	/**
	 * 
	 * <br>类描述:
	 * <br>功能详细描述:
	 * 
	 * @author  rongjinsong
	 * @date  [2012-9-13]
	 */
	class MyAdapter extends BaseAdapter {
		private ArrayList<String> mNewThemeTips = null;
		private HashMap<String, Boolean> mCheckBoxState = null;
		private LayoutInflater mInflater;
		ThemeInfoBean mInfoBean = null;

		public MyAdapter(Context context, ThemeInfoBean infoBean) {

			this.mInflater = LayoutInflater.from(context);
			mInfoBean = infoBean;
			mNewThemeTips = new ArrayList<String>();
			mCheckBoxState = new HashMap<String, Boolean>();
			mNewThemeTips.add(mGolauncherText);
			mNewThemeTips.add(mGowidgetText);
			mNewThemeTips.add(mGolockText);

			for (int i = 0; i < mNewThemeTips.size(); i++) {
				// 默认checkbox状态为选中
				mCheckBoxState.put(mNewThemeTips.get(i), true);
			}
		}

		public HashMap<String, Boolean> getmCheckBoxState() {
			return mCheckBoxState;
		}

		public void filterNotExistTheme() {
			if (mInfoBean != null) {
				if (!mInfoBean.ismExistGolauncher()) {
					mNewThemeTips.remove(mGolauncherText);
					mInfoBean.getNewThemeInfo().getNewThemePkg()
							.remove(ThemeDetailView.GOLAUNCHER_ACTION);
				}
				if (!mInfoBean.ismExistGolock()) {
					mNewThemeTips.remove(mGolockText);
					mInfoBean.getNewThemeInfo().getNewThemePkg()
							.remove(ThemeDetailView.GOLOCK_ACTION);
				}
				if (mInfoBean.getGoWidgetPkgName() == null) {
					mNewThemeTips.remove(mGowidgetText);
					mInfoBean.getNewThemeInfo().getNewThemePkg()
							.remove(ThemeDetailView.GOWIDGET_ACTION);
				}
			}
		}

		@Override
		public int getCount() {
			return mNewThemeTips.size();
		}

		@Override
		public Object getItem(int position) {
			return mNewThemeTips.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = null;
			CheckBox checkBox = null;
			Button downloadButton = null;
			if (convertView != null) {
				textView = (TextView) convertView.findViewById(R.id.new_theme_tip);
				checkBox = (CheckBox) convertView.findViewById(R.id.new_theme_checkbox);
				downloadButton = (Button) convertView.findViewById(R.id.new_theme_download_button);
			} else {
				convertView = mInflater.inflate(R.layout.new_theme_tips_item, null);
				textView = (TextView) convertView.findViewById(R.id.new_theme_tip);
				checkBox = (CheckBox) convertView.findViewById(R.id.new_theme_checkbox);
				downloadButton = (Button) convertView.findViewById(R.id.new_theme_download_button);
			}
			textView.setText(mNewThemeTips.get(position));
			DeskSettingConstants.setTextViewTypeFace(textView);
			DeskSettingConstants.setTextViewTypeFace(downloadButton);
			final int pos = position;
			// final String newPkg =
			// mInfoBean.getNewThemeInfo().getNewThemePkg().get(position);
			final String newPkg = getNewPkg(position);
			if (newPkg != null && !newPkg.trim().equals("")) {
				Intent intent = new Intent(newPkg);
				if (!AppUtils.isAppExist(mContext, intent)) {
					checkBox.setVisibility(View.GONE);
					downloadButton.setVisibility(View.VISIBLE);
				} else {
					checkBox.setVisibility(View.VISIBLE);
					downloadButton.setVisibility(View.GONE);
				}
			}

			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					mCheckBoxState.put(mNewThemeTips.get(pos), isChecked);
				}
			});

			downloadButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (newPkg != null && !newPkg.trim().equals("")) {
						if (newPkg.trim().equals(ThemeDetailView.GOWIDGET_ACTION)) {
							Intent toGoWidget = new Intent(ICustomAction.ACTION_GOTO_GOWIDGET_FRAME);
							mContext.sendBroadcast(toGoWidget);
							// 退出主题预览
							Intent goLauncherIntent = new Intent();
							goLauncherIntent.setClassName(mContext, IGoLauncherClassName.GOLAUNCHER_ACTIVITY);
							mContext.startActivity(goLauncherIntent);
							ThemeDetailActivity.exit();
						} else if (newPkg.trim().equals(ThemeDetailView.GOLOCK_ACTION)) {
							AppsDetail.gotoDetailDirectly(mContext,
									AppsDetail.START_TYPE_APPRECOMMENDED, newPkg);
							//							GoStoreOperatorUtil.gotoStoreDetailDirectly(getContext(), newPkg);
						}
					}
					dimissDialog();
				}
			});

			return convertView;
		}

		private String getNewPkg(int position) {
			String newPkg = null;
			try {
				newPkg = mInfoBean.getNewThemeInfo().getNewThemePkg().get(position);
			} catch (Exception e) {
				// TODO: handle exception
			}
			return newPkg;
		}

	}

	protected ThemeInfoBean geThemeInfoBean(String pkgName) {
		if (mThemeDataArrays == null || pkgName == null) {
			return null;
		}
		ArrayList<ThemeInfoBean[]> list = (ArrayList<ThemeInfoBean[]>) mThemeDataArrays.clone();
		for (int i = 0; i < list.size(); i++) {
			ThemeInfoBean[] themeInfoBeans = list.get(i);
			for (int j = 0; j < themeInfoBeans.length; j++) {
				ThemeInfoBean infoBean = themeInfoBeans[j];
				if (infoBean != null && infoBean.getPackageName() != null
						&& infoBean.getPackageName().equals(pkgName)) {
					list.clear();
					return infoBean;
				}
			}
		}
		list.clear();
		return null;
	}

	private void gotoFeaturedThemeDetailPage(ThemeInfoBean infoBean, int position) {
	    
	  //加载详情页面的广告
        GoDetailAdView.getAdView(
                mContext, AdConstanst.PRODUCT_ID_LAUNCHER_THEME,
                AdConstanst.POS_ID_LAUNCHER_THEME_DETAIL_BANNER, null);
	    
		Intent intent = new Intent(mContext, ThemeDetailActivity.class);
		intent.putExtra(ThemeConstants.DETAIL_MODEL_EXTRA_KEY,
				ThemeConstants.DETAIL_MODEL_FEATURED_EXTRA_VALUE);
		intent.putExtra(ThemeConstants.PACKAGE_NAME_EXTRA_KEY, infoBean.getPackageName());
		intent.putExtra(ThemeConstants.DETAIL_ID_EXTRA_KEY, infoBean.getFeaturedId());
		intent.putExtra(ThemeConstants.TITLE_EXTRA_KEY, infoBean.getThemeName());
		intent.putExtra(ThemeConstants.TITLE_EXTRA_THEME_TYPE_KEY, infoBean.getBeanType());
		intent.putExtra(ThemeConstants.THEME_LIST_ITEM_POSITION_KEY, position);
		if (infoBean.getBeanType() == ThemeConstants.LAUNCHER_SPEC_THEME_ID) {
			intent.putExtra(ThemeConstants.TITLE_EXTRA_THEME_SPECID_KEY, infoBean.getSortId());
		}
		intent.putExtra(ThemeConstants.TAB_TYPE_EXTRA_KEY, mThemeType);
		mContext.startActivity(intent);
	}

	private void initDownloadReceiver() {
		mDownloadReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (ICustomAction.ACTION_UPDATE_DOWNLOAD_PERCENT.equals(action)) {
					Bundle data = intent.getExtras();
					if (data != null) {
						long appId = data.getInt(AppsThemeDetailActivity.DOWNLOADING_APP_ID);
						int percent = data.getInt(AppsThemeDetailActivity.PERSENT_KEY);
						if (isDownLoadTheme(appId) && mDoloadingMap != null) {
							mDoloadingMap.put(appId, percent);
							notifyDataSetChanged();
						}
					}
				} else if (ICustomAction.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
					Bundle data = intent.getExtras();
					if (data != null) {
						long appId = data.getInt(AppsThemeDetailActivity.DOWNLOADING_APP_ID);
						if (mDoloadingMap != null && mDoloadingMap.containsKey(appId)) {
							mDoloadingMap.remove(appId);
							notifyDataSetChanged();
							ThemeInfoBean bean = getDownloadThemeInfo(appId);
							if (bean != null && bean.isInAppPay()) {
								Intent it = new Intent(ICustomAction.ACTION_NEW_THEME_INSTALLED);
								it.setData(Uri.parse("package://"));
								context.sendBroadcast(it);
							}
						}
					}
				} else if (ICustomAction.ACTION_UPDATE_DOWNLOAD_STOP.equals(action)
						|| ICustomAction.ACTION_UPDATE_DOWNLOAD_FAILED.equals(action)
						|| ICustomAction.ACTION_DOWNLOAD_DESTROY.equals(action)) {
					Bundle data = intent.getExtras();
					if (data != null) {
						long appId = data.getInt(AppsThemeDetailActivity.DOWNLOADING_APP_ID);
						if (mDoloadingMap != null) {
							mDoloadingMap.remove(appId);
							notifyDataSetChanged();
						}
					}
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ICustomAction.ACTION_DOWNLOAD_COMPLETE);
		intentFilter.addAction(ICustomAction.ACTION_UPDATE_DOWNLOAD_PERCENT);
		intentFilter.addAction(ICustomAction.ACTION_UPDATE_DOWNLOAD_STOP);
		intentFilter.addAction(ICustomAction.ACTION_UPDATE_DOWNLOAD_FAILED);
		intentFilter.addAction(ICustomAction.ACTION_DOWNLOAD_DESTROY);
		mContext.registerReceiver(mDownloadReceiver, intentFilter);
	}

	private boolean isShowProgressBar(long appId) {
		if (null != mDoloadingMap) {
			return mDoloadingMap.containsKey(appId);
		} else {
			return false;
		}
	}

	public void recyle() {
		mBannerImageMap.clear();
		mContext.unregisterReceiver(mDownloadReceiver);
		mPurchaseManager.unRegisterObserver(this);
	}

	@Override
	public void onBCChange(int msgId, int param, Object ...object) {
		// TODO Auto-generated method stub
		switch (msgId) {
			case IAppCoreMsgId.THEME_INAPP_PAID_FINISHED :
				Message msg = Message.obtain();
				msg.what = msgId;
				msg.arg1 = param;
				msg.obj = object[0];
				mHandler.sendMessage(msg);
				break;
			case ThemeImageManager.EVENT_NETWORK_ICON_URL_CHANGE :
			case ThemeImageManager.EVENT_NETWORK_ICON_CHANGE :
			case ThemeImageManager.EVENT_LOCAL_ICON_EXIT :
				msg = Message.obtain();
				msg.what = msgId;
				if (object != null) {
					if (object[0] instanceof Bitmap) {
						mBannerImageMap.put((String) object[1], (Bitmap) object[0]);
					} else if (object[0] instanceof BitmapDrawable) {
						mBannerImageMap
								.put((String) object[1], ((BitmapDrawable) object[0]).getBitmap());
					}
				}
				mHandler.sendMessage(msg);
				break;
			default :
				break;
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
				case IAppCoreMsgId.THEME_INAPP_PAID_FINISHED :
					if (ThemeAppInBillingManager.PURCHASE_STATE_PURCHASED == msg.arg1) {
						ThemeInfoBean bean = geThemeInfoBean((String) msg.obj);
						mPurchaseManager.startDownload(bean);
						if (bean != null) {
							GuiThemeStatistics.getInstance(mContext).onAppInstalled(
									bean.getPackageName());
						}
					}
					removeMessages(IAppCoreMsgId.THEME_INAPP_PAID_FINISHED);
				case ThemeImageManager.EVENT_NETWORK_ICON_CHANGE :
				case ThemeImageManager.EVENT_LOCAL_ICON_EXIT :
				case ThemeImageManager.EVENT_NETWORK_ICON_URL_CHANGE :
					notifyDataSetChanged();
					break;

				default :
					break;
			}

		}
	};

	private void updateProgressBar(ProgressBar progressBar, long id) {
		if (progressBar == null || mDoloadingMap == null) {
			return;
		}
		int per = mDoloadingMap.get(id);
		progressBar.setProgress(per);
	}

	/**
	 * 桌面本地主题点击事件
	 * */
	protected void deskInstalledClickEvent(String packageName) {
		Intent intent = new Intent(mContext, ThemeDetailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(ThemeConstants.PACKAGE_NAME_EXTRA_KEY, packageName);
		mContext.startActivity(intent);

	}

	/**
	 * <br>功能简述:检查banner是否显示
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	private boolean isShowBanner() {
		boolean bRet = false;
		if ((mContext instanceof ThemeManageActivity) && mIsLauncherFeature && mBannerBean != null) {
			if (!GoAppUtils.isGoLockerExist(mContext)	&& mThemeType == ThemeConstants.LOCKER_FEATURED_THEME_ID) {
				bRet = true;
			} else if (mBannerBean.mElements != null && mBannerBean.mElements.size() > 0) {
				if (mBannerBean.mType == mThemeType) {
					BannerElement element = mBannerBean.mElements.get(0);
					if (element.mSDate != null && element.mEDate != null) {
						if (element.mPkgs != null) {
							for (String pkg : element.mPkgs) {
								if (!AppUtils.isAppExist(mContext, pkg)) {
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
									Date date = new Date();
									String today = sdf.format(date);
									if (today.compareTo(element.mSDate) >= 0
											&& today.compareTo(element.mEDate) <= 0) {
										bRet = true;
										break;
									}
								}

							}
						}
					}
				}
			}
		}
		if (mIsShowBanner != bRet) {
			mIsShowBanner = bRet;
			notifyDataSetChanged();
		}
		return bRet;
	}

	private boolean isShowGoStore(int position) {
		if (mThemeDataArrays == null || mIsSpecTheme) {
			return false;
		}
		if (mThemeType == ThemeConstants.LOCKER_FEATURED_THEME_ID
				&& !GoAppUtils.isGoLockerExist(mContext)) {
			return false;
		}
		int itemCount = mThemeDataArrays.size();
		if (position == getCount() - 1
				&& (mIsOverScreen || mSpaceCalculator.calculateIsCover(itemCount, isShowBanner()) || (getCount() == position))) {
			return true;
		}
		return false;
	}

	private boolean isDownLoadTheme(long downloadId) {
		if (mThemeDataArrays != null) {
			for (ThemeInfoBean[] infoArray : mThemeDataArrays) {
				for (int i = 0; i < infoArray.length; i++) {
					ThemeInfoBean bean = infoArray[i];
					if (bean.getFeaturedId() == downloadId) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private ThemeInfoBean getDownloadThemeInfo(long downloadId) {
		if (mThemeDataArrays != null) {
			for (ThemeInfoBean[] infoArray : mThemeDataArrays) {
				for (int i = 0; i < infoArray.length; i++) {
					ThemeInfoBean bean = infoArray[i];
					if (bean.getFeaturedId() == downloadId) {
						return bean;
					}
				}
			}
		}

		return null;
	}
	
	private BindListener mBindListener = new BindListener() {

		@Override
		public void applyTheme(ThemeInfoBean info) {
			// TODO Auto-generated method stub
			ThemeListAdapter.this.applyTheme(info);
		}
		
	};
}
