package com.jiubang.ggheart.apps.desks.diy.themescan;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.go.util.device.Machine;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.core.framework.ICleanable;
import com.jiubang.ggheart.data.theme.bean.ThemeInfoBean;
import com.jiubang.ggheart.launcher.CheckApplication;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 
 * 主题数据主界面
 * 
 * @author yangbing
 * */
public class ThemeContainer extends RelativeLayout implements ICleanable, OnScrollListener {

	private int mThemeDataType = 0; // 主题数据类型
	private LinearLayout mLoadingLayout; // 加载等待界面
	private RelativeLayout mGoStoreLayout; // 去gostore下载更多 界面
	private ThemeListView mThemeListView = null; // 主题内容列表
	private ThemeDataManager mThemeDataManager = null; // 数据管理
	private ThemeImageManager mThemeImageManager = null; // 图片管理
	private LoadThemeDataTask mLoadDataTask; // 异步加载数据Task
	private LoadThemeImageTask mLoadImageTask; // 异步加载图片Task
	private ArrayList<ThemeInfoBean> mThemeInfoBeans; // 主题数据
//	private ThemeBannerBean mBannerData;
//	private ThemeBannerBean mLockerBannerData;
	private TextView mNoThemes;
	private int mSpecId; //分类主题ID
	private RelativeLayout mExtraGoStoreLayout;
	private LinearLayout mAdLayout;
	private SparseBooleanArray mAdDisableArray;
	
	public static final int STATE_IDLE = 0x00;
	public static final int STATE_LOADING = 0x01;
	public static final int STATE_LOCAL_LOADFINISHED = 0x02;
	public static final int STATE_NET_LOADFINISHED = 0x04;
	public static final int STATE_LOADFINISHED = 0x07;
	private int mState = STATE_IDLE;
	public ThemeContainer(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public ThemeContainer(Context context) {
		super(context);

	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init();
	}

	/**
	 * 初始化
	 * */
	private void init() {
		mAdDisableArray = new SparseBooleanArray();
		mThemeListView = (ThemeListView) this.findViewById(R.id.theme_list);
		mThemeListView.initThemeListView();
		mThemeListView.setVisibility(GONE);
		mThemeListView.setOnScrollListener(this);
		mGoStoreLayout = (RelativeLayout) this.findViewById(R.id.theme_goto_store);
		mGoStoreLayout.setVisibility(GONE);
		mLoadingLayout = (LinearLayout) this.findViewById(R.id.theme_loading);
		mNoThemes = (TextView) findViewById(R.id.nothemes);
		mNoThemes.setVisibility(View.GONE);
		mThemeDataManager = ThemeDataManager.getInstance(getContext());
		mThemeImageManager = ThemeImageManager.getInstance(getContext().getApplicationContext());
		mAdLayout = (LinearLayout) findViewById(R.id.adLayout);
	}

	/**
	 * 加载主题数据
	 * */
	public void loadThemeData(int mThemeDataType, boolean isLoad) {
		this.mThemeDataType = mThemeDataType;
		if (!GoAppUtils.isGoLockerExist(getContext())
				&& mThemeDataType == ThemeConstants.LOCKER_FEATURED_THEME_ID) {
			if (mExtraGoStoreLayout == null) {
				mExtraGoStoreLayout = (RelativeLayout) findViewById(R.id.golock_ad);
				TextView textView = (TextView) mExtraGoStoreLayout.findViewById(R.id.theme_findmore_text);
				textView.setText(R.string.thememanager_install_locker_tip);
				textView.setGravity(Gravity.CENTER_VERTICAL);
				mExtraGoStoreLayout.setVisibility(GONE);
				mExtraGoStoreLayout.setClickable(true);
				addGoStoreClickListener(mExtraGoStoreLayout);
			}
			if (mAdLayout != null) {
				mAdLayout.setVisibility(GONE);
			}
		} else {
			if (!isLoad) {
				updateAd(mThemeDataType);
			}
		}
		// 判断主题数据缓存里是否存在数据，如果不存在再去开启异步线程加载数据
		mThemeInfoBeans = mThemeDataManager.getThemeData(mThemeDataType);
		if (mThemeInfoBeans == null) {
			execLoadThemeDataTask();
		} else {
			if (mThemeImageManager.isExsitImageCache(mThemeDataType)) {
				refreshListView();
			} else {
				execLoadThemeImageTask();
			}
		}
	}

	public void updateAd(int mThemeDataType) {
		if (!mAdDisableArray.get(mThemeDataType) && mAdLayout != null) {
			if (getContext() instanceof Activity) {
				ThemeTabBannerManager.getCache().update((Activity) getContext(), mAdLayout,
						mThemeDataType);
			}
			mAdLayout.setVisibility(VISIBLE);
		}
	}
	
	/**
	 * <br>功能简述:获取专题数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param id 分类ID
	 */
	public void loadSpecThemeData(int id) {
		mSpecId = id;
		mThemeDataType = ThemeConstants.LAUNCHER_SPEC_THEME_ID;
		// 判断主题数据缓存里是否存在数据，如果不存在再去开启异步线程加载数据
		mThemeInfoBeans = mThemeDataManager.getThemeData(mThemeDataType);
		if (mThemeInfoBeans == null) {
			execLoadThemeDataTask();
		} else {
			if (mThemeImageManager.isExsitImageCache(mThemeDataType)) {
				refreshListView();
			} else {
				execLoadThemeImageTask();
			}

		}

	}

	/**
	 * 刷新数据列表
	 * */
	public void refreshListView() {
		if (!isLoadFinish() && mState != STATE_IDLE) {
			setViewVisibility(mLoadingLayout, View.VISIBLE);
		} else {
			setViewVisibility(mLoadingLayout, View.GONE);
		}
		if (mExtraGoStoreLayout != null) {
		if (mThemeDataType == ThemeConstants.LOCKER_FEATURED_THEME_ID
				&& !GoAppUtils.isGoLockerExist(getContext())) {
				mExtraGoStoreLayout.setVisibility(VISIBLE);
		} else {
				mExtraGoStoreLayout.setVisibility(GONE);
			}
		}
		int bannerType = mThemeDataType;
		if (mSpecId == 0) {
			setViewVisibility(mGoStoreLayout, View.VISIBLE);
			if (mThemeDataType == ThemeConstants.LOCKER_FEATURED_THEME_ID
					&& !GoAppUtils.isGoLockerExist(getContext())) {
				bannerType = ThemeConstants.LOCKER_UNINSTALL_BANNER;
				((TextView) mGoStoreLayout.findViewById(R.id.theme_findmore_text))
						.setText(R.string.thememanager_install_locker_tip);
			} else {
				((TextView) mGoStoreLayout.findViewById(R.id.theme_findmore_text))
						.setText(R.string.theme_findmore);
			}
			addGoStoreClickListener(mGoStoreLayout);
		}
		mThemeListView.setThemeDatas(mThemeInfoBeans, mThemeDataManager.getBannerData(bannerType), mThemeDataType);
		if (mThemeInfoBeans == null || mThemeInfoBeans.isEmpty()) {
			mThemeListView.setVisibility(View.GONE);
		} else {
			mThemeListView.refreshView();
		}
		if (mSpecId == 0 && STATE_LOADFINISHED == mState) {
			switchNothemetips();
		}
		ListAdapter adapter = mThemeListView.getAdapter();
		if (adapter != null && adapter instanceof ThemeListAdapter) {
			if (!((ThemeListAdapter) adapter).isOverScreen() || mAdDisableArray.get(mThemeDataType)) {
				if (mAdLayout != null) {
					if (mThemeDataType != ThemeConstants.LAUNCHER_INSTALLED_THEME_ID
							&& mThemeDataType != ThemeConstants.LOCKER_INSTALLED_THEME_ID) {
						mAdLayout.setVisibility(GONE);
					} else {
						mAdLayout.setVisibility(VISIBLE);
					}
				}
		}
	}
	}

	private void addGoStoreClickListener(RelativeLayout layout) {
		if (layout == null) {
			return;
		}
		layout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Context context = getContext();
				if (!GoAppUtils.isGoLockerExist(context)
						&& mThemeDataType == ThemeConstants.LOCKER_FEATURED_THEME_ID) {
					CheckApplication.downloadAppFromMarketGostoreDetail(context,
					        PackageName.GO_LOCK_PACKAGE_NAME,
							LauncherEnv.Url.GOLOCKER_IN_THEME_WITH_GOOGLE_REFERRAL_LINK);
				} else {
					if (context instanceof ThemeManageActivity) {
						((ThemeManageActivity) context).gotoGoStore();
					} else if (context instanceof BannerDetailActivity) {
						((BannerDetailActivity) context).gotoGoStore();
					}
				}
			}
		});
	}

	/**
	 * 缓存主题图片
	 * */
	private void execLoadThemeImageTask() {
		if (mLoadImageTask != null && mLoadImageTask.getStatus() == AsyncTask.Status.RUNNING) {
			return;
		}
		if (mThemeInfoBeans == null || mThemeInfoBeans.size() <= 0) {
			return;
		}
		mLoadImageTask = new LoadThemeImageTask();
		mLoadImageTask.execute();
	}

	/**
	 * 异步加载主题数据
	 * */
	private void execLoadThemeDataTask() {
		// 加载数据
		if (mLoadDataTask != null && mLoadDataTask.getStatus() == AsyncTask.Status.RUNNING) {
			return;
		}
		mLoadDataTask = new LoadThemeDataTask();
		mLoadDataTask.execute();
	}

	/**
	 * 异步任务，加载主题数据
	 */
	private class LoadThemeDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			setViewVisibility(mThemeListView, View.GONE);
			setViewVisibility(mLoadingLayout, View.VISIBLE);
			initState();
			updateState(STATE_LOADING);
			}

		@Override
		protected Void doInBackground(Void... params) {
			if (mThemeDataType == ThemeConstants.LAUNCHER_SPEC_THEME_ID) {
				mThemeDataManager.setSpecThemeId(mSpecId);
			}
			mThemeDataManager.loadThemeData(mThemeDataType);
			mThemeInfoBeans = mThemeDataManager.getThemeData(mThemeDataType);
			loadImageCache();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			updateState(STATE_LOCAL_LOADFINISHED);
			refreshListView();
		}
	}

	/**
	 * 异步任务，加载主题图片
	 */
	private class LoadThemeImageTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			setViewVisibility(mThemeListView, View.GONE);
			setViewVisibility(mLoadingLayout, View.VISIBLE);
			}

		@Override
		protected Void doInBackground(Void... params) {
			loadImageCache();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			refreshListView();
		}
	}

	@Override
	public void cleanup() {
		mLoadDataTask = null;
		mLoadImageTask = null;
		mThemeInfoBeans = null;
		if (mThemeListView != null) {
			mThemeListView.cleanup();
		}

	}

	/**
	 * 预先加载前9张图片到缓存
	 * */
	private void loadImageCache() {
		if (mThemeInfoBeans == null) {
			return;
		}
		if (mThemeInfoBeans.size() > 9) {
			mThemeImageManager.putImageCache(mThemeInfoBeans.subList(0, 9));
		} else {
			mThemeImageManager.putImageCache(mThemeInfoBeans);
		}
	}

	/**
	 * 横竖屏切换
	 * */
	public void changeOrientation() {
		if (mThemeListView != null && mThemeInfoBeans != null) {
			mThemeListView.changeOrientation(mThemeInfoBeans,
					mThemeDataManager.getBannerData(mThemeDataType),
					mThemeDataType);
		}
	}

	public void hideNoThemesTips() {
		if (mNoThemes != null) {
			setViewVisibility(mNoThemes, View.GONE);
		}
		findViewById(R.id.network_tip_group).setVisibility(View.GONE);
		}

	public void switchNothemetips() {
		if (mNoThemes != null) {
		if ((mThemeDataType == ThemeConstants.LAUNCHER_FEATURED_THEME_ID
				|| mThemeDataType == ThemeConstants.LOCKER_FEATURED_THEME_ID
				|| mThemeDataType == ThemeConstants.LAUNCHER_HOT_THEME_ID || mThemeDataType == ThemeConstants.LAUNCHER_SPEC_THEME_ID)
				&& (mThemeInfoBeans == null || mThemeInfoBeans.size() == 0)) {
				if (!Machine.isNetworkOK(getContext())) {
					findViewById(R.id.network_tip_group).setVisibility(View.VISIBLE);
					setViewVisibility(mNoThemes, View.GONE);
				} else {
					findViewById(R.id.network_tip_group).setVisibility(View.GONE);
					setViewVisibility(mNoThemes, View.VISIBLE);
			}
		} else {
				setViewVisibility(mNoThemes, View.GONE);
			}
		}
	}

	public TextView getNoThemeTextView() {
		return mNoThemes;
	}

	public void onDestroy() {
		mThemeListView.onDestroy();
	}

	public void reLoadBannerData(int type) {
		mThemeListView.setThemeDatas(mThemeInfoBeans, mThemeDataManager.getBannerData(type), mThemeDataType);
		}

	@Override
	public void setBackgroundDrawable(Drawable d) {
		// TODO Auto-generated method stub
		super.setBackgroundDrawable(d);
		if (d != null && mLoadingLayout != null) {
			mLoadingLayout.setBackgroundDrawable(d);
		}
		if (d != null && mThemeListView != null) {
			mThemeListView.setBackgroundDrawable(d);;
		}
	}

	@Override
	public void setBackgroundColor(int color) {
		// TODO Auto-generated method stub
		super.setBackgroundColor(color);
		if (mThemeListView != null) {
			mThemeListView.setBackgroundColor(color);
		}
		if (mLoadingLayout != null) {
			mLoadingLayout.setBackgroundColor(color);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {
		if (totalItemCount == 0) {
			return;
		}
		if (mAdDisableArray.get(mThemeDataType)) {
			return;
		}
		// 本地主题页面广告不消失
		if (mThemeDataType == ThemeConstants.LAUNCHER_INSTALLED_THEME_ID
				|| mThemeDataType == ThemeConstants.LOCKER_INSTALLED_THEME_ID) {
			return;
		}
		if (visibleItemCount + firstVisibleItem == totalItemCount) {
			
			if (mAdLayout != null) {
				mAdDisableArray.put(mThemeDataType, true);
				mAdLayout.setVisibility(GONE);
			}
		}
		
	}

	private void initState() {
		mState = STATE_IDLE;
	}
	
	public void updateState(int state) {
		if (mState != state) {
			mState |= state;
			if (isLoadFinish()) {
				reloadData();
				refreshListView();
			}
		}
	}
	
	private void reloadData() {
		mThemeInfoBeans = mThemeDataManager.getThemeData(mThemeDataType);
	}
	
	private void setViewVisibility(View view, int visibility) {
		if (view.getVisibility() != visibility) {
			view.setVisibility(visibility);
		}
	}
	
	
	public boolean isLoadFinish() {
		if (mState == STATE_LOADFINISHED) {
			return true;
		} else {
			if ((mState & STATE_LOCAL_LOADFINISHED) != 0
					&& (mThemeDataType == ThemeConstants.LOCKER_INSTALLED_THEME_ID || mThemeDataType == ThemeConstants.LAUNCHER_INSTALLED_THEME_ID)) {
				return true;
			}
		}
		return false;
	}
}
