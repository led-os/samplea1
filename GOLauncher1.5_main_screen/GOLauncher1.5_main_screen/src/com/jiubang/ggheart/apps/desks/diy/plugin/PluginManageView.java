package com.jiubang.ggheart.apps.desks.diy.plugin;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.go.util.scroller.ScreenScroller;
import com.go.util.scroller.ScreenScrollerListener;
import com.jiubang.ggheart.appgame.download.DownloadTask;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.themescan.SpaceCalculator;
import com.jiubang.ggheart.apps.gowidget.gostore.views.ScrollerViewGroup;

/**
 * 插件管理view
 * @author liulixia
 *
 */
public class PluginManageView extends RelativeLayout 
		implements  ScreenScrollerListener,
					View.OnClickListener {
	private PluginManagerActivity mContext;
	private LayoutInflater mInflater;
	// 可左右滑动的ViewGroup，在精选容器和本地主题之间滑动
	private ScrollerViewGroup mScrollerViewGroup = null;
	
	private View mGoPluginPoint;
	private View mGoWidgetPoint;
	private TextView mGoPluginThemeTitle = null; // 桌面主题
	private TextView mGoWidgetThemeTitle = null; // 锁屏主题
	private RelativeLayout mGoPluginLayout = null;
	private RelativeLayout mGoWidgetLayout = null;
	
	private RelativeLayout mTabLayout = null;
	private TextView mFeaturedThemeTab = null;
	private TextView mInstalledThemeTab = null;
	private ImageView mFeaturedThemeImg = null;
	private ImageView mInstalledThemeImg = null;
	private RelativeLayout mFeaturedLayout = null;
	private RelativeLayout mInstalledLayout = null;
	
	private ImageView mNewThemeLog;
	private boolean mHasNewTheme = false;
	
	private PluginManageContainer mPluginInstallContainer = null;
	private PluginManageContainer mPluginFeatureContainer = null;
	
	private ArrayList<GoPluginOrWidgetInfo> mInstallPluginsOrWidget = null;
	private ArrayList<GoPluginOrWidgetInfo> mFeaturePluginsOrWidget = null;
	
	private ArrayList<GoPluginOrWidgetInfo> mAllPlugins = null;
	private ArrayList<GoPluginOrWidgetInfo> mAllWidgets = null;
	
	private PluginXmlParser mParser = null;
	
	
	/**
	 * 精选界面
	 * */
	public static final int FEATURED_VIEW_ID = 1;
	/**
	 * 本地界面
	 * */
	public static final int INSTALLED_VIEW_ID = 0;
	/**
	 * plugin标签
	 * */
	public static final int GO_PLUGIN_VIEW_ID = 4;
	/**
	 * widget标签
	 * */
	public static final int GO_WIDGET_VIEW_ID = 3;
	
	private int mEntranceId = -1; // 入口id,当前是桌面tab还是锁屏tab
	private int mCurTabId = -1; // 当前是精选tab还是本地tab
	// 子视图id
	private static final int TOP_LAYOUT_ID = 0x1;
	private static final int TAB_LAYOUT_ID = 0x2;
	
	private boolean mIsCnUser = false;
	
	public PluginManageView(PluginManagerActivity activity, boolean isCnUser) {
		super(activity);
		mContext = activity;
		mInflater = LayoutInflater.from(activity);
		mCurTabId = 0;
		mEntranceId = GO_PLUGIN_VIEW_ID;
		mIsCnUser = isCnUser;
		initView();
	}

	private void initView() {
		setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		setBackgroundResource(R.color.theme_bg);
		initTopView();
		initTabView();
		initScrollerView();
//		startLoadData();
	}
	
	/**
	 * 初始化顶部tab栏
	 * */
	private void initTopView() {

		RelativeLayout mTopLayout = (RelativeLayout) mInflater.inflate(R.layout.theme_manage_top,
				null);
		mGoPluginPoint = mTopLayout.findViewById(R.id.desk_lightpoint);
		mGoWidgetPoint = mTopLayout.findViewById(R.id.lock_lightpoint);
		mGoPluginLayout = (RelativeLayout) mTopLayout.findViewById(R.id.desk_theme_layout);
		mGoPluginLayout.setOnClickListener(this);
		mGoPluginThemeTitle = (TextView) mTopLayout.findViewById(R.id.desk_theme);
		mGoPluginThemeTitle.setText(R.string.plugin_manage_plugin);
		// mDeskThemeTitle.setOnClickListener(this);
		mGoWidgetLayout = (RelativeLayout) mTopLayout.findViewById(R.id.locker_theme_layout);
		mGoWidgetLayout.setOnClickListener(this);
		mGoWidgetThemeTitle = (TextView) mTopLayout.findViewById(R.id.lock_theme);
		mGoWidgetThemeTitle.setText(R.string.plugin_manage_widget);
		// mLockerThemeTitle.setOnClickListener(this);
		switch (mEntranceId) {
			case GO_PLUGIN_VIEW_ID :
				changeTopFocus(mGoPluginThemeTitle, mGoWidgetThemeTitle);
				// mDeskPoint.setVisibility(VISIBLE);
				mGoWidgetPoint.setVisibility(GONE);
				break;
			case GO_WIDGET_VIEW_ID :
				changeTopFocus(mGoWidgetThemeTitle, mGoPluginThemeTitle);
				mGoPluginPoint.setVisibility(GONE);
				// mLockerPoint.setVisibility(VISIBLE);
				break;
			default :
				break;
		}
		// 添加tab视图
		mTopLayout.setId(TOP_LAYOUT_ID);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		addView(mTopLayout, params);

	}
	
	/**
	 * 功能简述:初始化锁屏主题的Tab（精选、本地）
	 */
	private void initTabView() {
		if (mTabLayout != null) {
			mTabLayout.removeAllViews();
			this.removeView(mTabLayout);
		}
		if (SpaceCalculator.sPortrait) {
			mTabLayout = (RelativeLayout) mInflater.inflate(R.layout.plugin_manage_tab2_v, null);
		} else {
			//TODO 未定义横屏
			mTabLayout = (RelativeLayout) mInflater.inflate(R.layout.plugin_manage_tab2_h, null);
		}

//		boolean hasUpdate = hasNewLockerSetting();
//		if (mNewLockerSettingLogo != null) {
//			mTabLayout.removeView(mNewLockerSettingLogo);
//		}
//		if (hasUpdate) {
//			mNewLockerSettingLogo = new ImageView(getContext());
//			mNewLockerSettingLogo.setImageResource(R.drawable.locker_setting_new);
//			RelativeLayout.LayoutParams pa = new RelativeLayout.LayoutParams(
//					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
//					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
//			pa.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//			pa.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//			pa.setMargins(0, 10, 10, 0);
//			mTabLayout.addView(mNewLockerSettingLogo, pa);
//		}
		mFeaturedThemeTab = (TextView) mTabLayout.findViewById(R.id.featured_theme_text);
		mFeaturedThemeImg = (ImageView) mTabLayout.findViewById(R.id.featured_theme_image);
		mFeaturedLayout = (RelativeLayout) mTabLayout.findViewById(R.id.featured_layout);
		mFeaturedThemeTab.setOnClickListener(this);
		mFeaturedThemeImg.setOnClickListener(this);
		mFeaturedLayout.setOnClickListener(this);

		mInstalledThemeTab = (TextView) mTabLayout.findViewById(R.id.installed_theme_text);
		mInstalledThemeImg = (ImageView) mTabLayout.findViewById(R.id.installed_theme_image);
		mInstalledLayout = (RelativeLayout) mTabLayout.findViewById(R.id.installed_layout);
		mInstalledThemeTab.setOnClickListener(this);
		mInstalledThemeImg.setOnClickListener(this);
		mInstalledLayout.setOnClickListener(this);

		switch (mCurTabId) {
			case FEATURED_VIEW_ID :
				focusFeaturedPluginTab();
				break;
			case INSTALLED_VIEW_ID :
				focusInstalledPluginTab();
				break;

			default :
				break;
		}
		// 添加tab视图
		mTabLayout.setId(TAB_LAYOUT_ID);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, TOP_LAYOUT_ID);
		addView(mTabLayout, params);
	}
	
	/**
	 * 本地tab选中状态
	 * */
	private void focusInstalledPluginTab() {
		mFeaturedThemeTab.setTextColor(getResources().getColor(R.color.theme_tab_no_focus));
		mFeaturedThemeImg.setImageResource(R.drawable.theme_tab_normal);
		mInstalledThemeTab.setTextColor(getResources().getColor(R.color.theme_tab_focus));
		mInstalledThemeImg.setImageResource(R.drawable.theme_tab_light);
	}

	/**
	 * 精选tab选中状态
	 * */
	private void focusFeaturedPluginTab() {
		mFeaturedThemeTab.setTextColor(getResources().getColor(R.color.theme_tab_focus));
		mFeaturedThemeImg.setImageResource(R.drawable.theme_tab_light);
		mInstalledThemeTab.setTextColor(getResources().getColor(R.color.theme_tab_no_focus));
		mInstalledThemeImg.setImageResource(R.drawable.theme_tab_normal);
	}

	/**
	 * 初始化锁屏主题主界面
	 * */
	private void initScrollerView() {
		mScrollerViewGroup = new ScrollerViewGroup(getContext(), this);
		if (mPluginInstallContainer == null || mPluginFeatureContainer == null) {
			initPluginContainer();
		}
		LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		mScrollerViewGroup.addView(mPluginInstallContainer, linearParams);
		mScrollerViewGroup.addView(mPluginFeatureContainer, linearParams);
		mScrollerViewGroup.setScreenCount(mScrollerViewGroup.getChildCount());
		RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeParams.addRule(RelativeLayout.BELOW, TAB_LAYOUT_ID);
		addView(mScrollerViewGroup, relativeParams);
		mScrollerViewGroup.gotoViewByIndex(mCurTabId);
		mScrollerViewGroup.setCircle(false);
	}
	
	private void initPluginContainer() {
		mPluginInstallContainer = (PluginManageContainer) mInflater.inflate(
				R.layout.plugin_manage_container_layout, null);
		mPluginFeatureContainer = (PluginManageContainer) mInflater.inflate(
				R.layout.plugin_manage_container_layout, null);
	}

	
	public void startLoadData() {
		mPluginInstallContainer.setType(mEntranceId);
		mPluginFeatureContainer.setType(mEntranceId);
		
		if (mInstallPluginsOrWidget == null) {
			mInstallPluginsOrWidget = new ArrayList<GoPluginOrWidgetInfo>();
		} else {
			mInstallPluginsOrWidget.clear();
		}
		if (mFeaturePluginsOrWidget == null) {
			mFeaturePluginsOrWidget = new ArrayList<GoPluginOrWidgetInfo>();
		} else {
			mFeaturePluginsOrWidget.clear();
		}
		if (mParser == null) {
			mParser = new PluginXmlParser();
		}
		if (mEntranceId == GO_PLUGIN_VIEW_ID) {
			if (mAllPlugins == null || mAllPlugins.size() == 0) {
				mAllPlugins = new ArrayList<GoPluginOrWidgetInfo>();
				mParser.parsePlugin(true, mContext, mIsCnUser,
							mAllPlugins, mInstallPluginsOrWidget, mFeaturePluginsOrWidget);
			} else {
				mParser.getInfosByType(mContext, mAllPlugins, mInstallPluginsOrWidget, mFeaturePluginsOrWidget);
			}
		} else {
			if (mAllWidgets == null || mAllWidgets.size() == 0) {
				mAllWidgets = new ArrayList<GoPluginOrWidgetInfo>();
				mParser.parsePlugin(false, mContext, mIsCnUser,
							mAllWidgets, mInstallPluginsOrWidget, mFeaturePluginsOrWidget);
			} else {
				mParser.getInfosByType(mContext, mAllWidgets, mInstallPluginsOrWidget, mFeaturePluginsOrWidget);
			}
		}
		if (mIsCnUser) { //只有国内包才有ftp下载更新提示以及检查更新
			parseDownloadTasksToPluginInfos(mContext.getManagerDownloadList());
			checkNeedUpdateInstalled();
		}
		if (mInstallPluginsOrWidget.size() == 0) {
			mPluginInstallContainer.hasPluginsOrWidgets(false);
			mPluginFeatureContainer.hasPluginsOrWidgets(true);
			mCurTabId = FEATURED_VIEW_ID;
			mScrollerViewGroup.gotoViewByIndex(mCurTabId);
			focusFeaturedPluginTab();
		} else {
			mPluginInstallContainer.hasPluginsOrWidgets(true);
			if (mFeaturePluginsOrWidget.size() == 0) {
				mPluginFeatureContainer.hasAllPluginsOrWidgets(true);
			} else {
				mPluginFeatureContainer.hasAllPluginsOrWidgets(false);
			}
		}
		mPluginInstallContainer.setData(mIsCnUser, mInstallPluginsOrWidget, mContext);
		mPluginFeatureContainer.setData(mIsCnUser, mFeaturePluginsOrWidget, mContext);
	}
	
	/**
	 * 改变标题栏聚集状态(桌面主题和锁屏主题)
	 * 
	 * @param getFocus
	 *            :获取焦点的对象
	 * @param lostFocus
	 *            :失去焦点对象
	 * */
	private void changeTopFocus(TextView getFocus, TextView lostFocus) {
		getFocus.setTextColor(getResources().getColor(R.color.theme_top_cur_text));
		lostFocus.setTextColor(getResources().getColor(R.color.theme_top_text));
	}
	
	@Override
	public ScreenScroller getScreenScroller() {
		return null;
	}

	@Override
	public void setScreenScroller(ScreenScroller scroller) {
		
	}

	@Override
	public void onFlingIntercepted() {
		
	}

	@Override
	public void onScrollStart() {
		
	}

	@Override
	public void onFlingStart() {
		
	}

	@Override
	public void onScrollChanged(int newScroll, int oldScroll) {
		
	}

	@Override
	public void onScreenChanged(int newScreen, int oldScreen) {
		mCurTabId = newScreen;
	}

	@Override
	public void onScrollFinish(int currentScreen) {
		if (mCurTabId == FEATURED_VIEW_ID) {
			// 滚动到精选tab
			removeNewThemeLog();
			focusFeaturedPluginTab();
		} else if (mCurTabId == INSTALLED_VIEW_ID) {
			// 滚动到本地tab
			focusInstalledPluginTab();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mGoPluginLayout) {
			if (mEntranceId == GO_PLUGIN_VIEW_ID) {
				return;
			}
			removeNewThemeLog();
			mEntranceId = GO_PLUGIN_VIEW_ID;
			mCurTabId = INSTALLED_VIEW_ID;
//			mCurTabId = FEATURED_VIEW_ID;
			gotoPluginTab();
			startLoadData();
		} else if (v == mGoWidgetLayout) {
			if (mEntranceId == GO_WIDGET_VIEW_ID) {
				return;
			}
			removeNewThemeLog();
			mEntranceId = GO_WIDGET_VIEW_ID;
			mCurTabId = INSTALLED_VIEW_ID;
//			mCurTabId = FEATURED_VIEW_ID;
			gotoWidgetTab();
			startLoadData();
		} else if (v == mFeaturedThemeTab || v == mFeaturedLayout || v == mFeaturedThemeImg) {
			if (mCurTabId == FEATURED_VIEW_ID || mScrollerViewGroup == null) {
				return;
			}
			removeNewThemeLog();
			mCurTabId = FEATURED_VIEW_ID;
			mScrollerViewGroup.gotoViewByIndex(mCurTabId);
			focusFeaturedPluginTab();
		} else if (v == mInstalledThemeTab || v == mInstalledThemeImg || v == mInstalledLayout) {
			if (mCurTabId == INSTALLED_VIEW_ID || mScrollerViewGroup == null) {
				return;
			}
			mCurTabId = INSTALLED_VIEW_ID;
			mScrollerViewGroup.gotoViewByIndex(mCurTabId);
			focusInstalledPluginTab();
		}
	}
	
	/**
	 * 切换到go插件
	 * */
	private void gotoPluginTab() {
		if (mTabLayout.getVisibility() == GONE) {
			mTabLayout.setVisibility(VISIBLE);
		}
		changeTopFocus(mGoPluginThemeTitle, mGoWidgetThemeTitle);
		mGoWidgetPoint.setVisibility(GONE);
		mGoPluginPoint.setVisibility(VISIBLE);
//		focusFeaturedThemeTab();
		focusInstalledPluginTab();
		clearScrollerViewGroup();
	}

	/**
	 * 切换到widget界面
	 * */
	private void gotoWidgetTab() {
		changeTopFocus(mGoWidgetThemeTitle, mGoPluginThemeTitle);
		mGoPluginPoint.setVisibility(GONE);
		mGoWidgetPoint.setVisibility(VISIBLE);
//		focusFeaturedThemeTab();
		focusInstalledPluginTab();
		clearScrollerViewGroup();
	}
	
	/**
	 * 清空ViewGroup
	 * */
	private void clearScrollerViewGroup() {
		mScrollerViewGroup.gotoViewByIndexImmediately(mCurTabId);
	}
	
	public void clearup() {
		//TODO
		mAllPlugins = null;
		mAllWidgets = null;
		mInstallPluginsOrWidget = null;
		mFeaturePluginsOrWidget = null;
		if (mPluginFeatureContainer != null) {
			mPluginFeatureContainer.removeAllViews();
			mPluginFeatureContainer = null;
		}
		
		if (mPluginInstallContainer != null) {
			mPluginInstallContainer.removeAllViews();
			mPluginInstallContainer = null;
		}
	}
	
	public void addNewLog() {
		if (!mHasNewTheme) {
			if (mNewThemeLog == null) {
				mNewThemeLog = new ImageView(getContext());
			}
			mNewThemeLog.setImageResource(R.drawable.theme_new_log);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			mInstalledLayout.addView(mNewThemeLog, params);
			mHasNewTheme = true;
		}
	}

	public void removeNewThemeLog() {
		if (mHasNewTheme && mNewThemeLog != null && mInstalledLayout != null) {
			mInstalledLayout.removeView(mNewThemeLog);
			mHasNewTheme = false;
		}
	}
	
	public void refreshFeatureLayout(ArrayList<DownloadTask> managedDownloads) {
		parseDownloadTasksToPluginInfos(managedDownloads);
		if (mPluginFeatureContainer != null && 
				mFeaturePluginsOrWidget != null && mFeaturePluginsOrWidget.size() > 0) {
			mPluginFeatureContainer.updateData(mIsCnUser);
		}
	}
	
	public void updateFeatureData() {
		mPluginFeatureContainer.updateData(mIsCnUser);
	}
	
	public void updateInstallData() {
		mPluginInstallContainer.updateData(mIsCnUser);
	}
	
	/**
	 * 根据下载管理页面管理的内容对未安装的小部件或插件进行信息更新
	 * @param managedDownloads
	 */
	private void parseDownloadTasksToPluginInfos(ArrayList<DownloadTask> managedDownloads) {
		if (managedDownloads == null || managedDownloads.size() == 0) {
			return ;
		}
		
		for (DownloadTask downloadTask : managedDownloads) {
			String pkn = downloadTask.getDownloadApkPkgName();
			GoPluginOrWidgetInfo info = getPluginOrWidgetPkg(pkn);
			if (info != null && info.mState != GoPluginOrWidgetInfo.INSTALLED) {
				parseDownloadTask(downloadTask, info);
			}
			
		}
		
	}
	
	/**
	 * 检测安装的widget或部件是否有更新
	 * @return
	 */
	public boolean checkNeedUpdateInstalled() {
		if (mInstallPluginsOrWidget != null && mInstallPluginsOrWidget.size() > 0) {
			PreferencesManager preferences = new PreferencesManager(mContext,
					IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
			String updateInfos = preferences.getString(IPreferencesIds.NEED_UPDATE_GOPLUGINS, "");
			if (updateInfos.equals("")) {
				return false;
			}
			for (GoPluginOrWidgetInfo widgetInfo : mInstallPluginsOrWidget) {
				String pkgName = widgetInfo.mWidgetPkgName;
				if (updateInfos.contains(pkgName)) { //本地图片需要更新
					widgetInfo.mNeedUpdate = true;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 删除已安装的shareference数据
	 */
	public void deleteAlreadyUpdateInstalled(String pkgName) {
		PreferencesManager preferences = new PreferencesManager(mContext,
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
		String updateInfos = preferences.getString(IPreferencesIds.NEED_UPDATE_GOPLUGINS, "");
		if (updateInfos.equals("")) {
			return;
		}
		if (updateInfos.contains(pkgName)) { //本地图片需要更新
			String newUpdateInfos = updateInfos.replace(pkgName, "");
			preferences.putString(IPreferencesIds.NEED_UPDATE_GOPLUGINS, newUpdateInfos);
			preferences.commit();
		}
	}
	
	public void deleteUpdateInfos() {
		PreferencesManager preferences = new PreferencesManager(mContext,
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
		preferences.putString(IPreferencesIds.NEED_UPDATE_GOPLUGINS, "");
		preferences.commit();
	}
	/**
	 * 检查是否更新完widget或部件
	 * @return
	 */
	public boolean checkAlreadyUpdateInstalled(String pkgName) {
		if (mInstallPluginsOrWidget != null && mInstallPluginsOrWidget.size() > 0) {
			
		}
		return false;
	}
	
	private void parseDownloadTask(DownloadTask downloadTask, GoPluginOrWidgetInfo info) {
		switch (downloadTask.getState()) {
			case DownloadTask.STATE_START :
				info.mState = GoPluginOrWidgetInfo.DOWNLOADING;
				info.mPrecent = 0;
				break;
			case DownloadTask.STATE_WAIT :
				info.mState = GoPluginOrWidgetInfo.WAIT_FOR_DOWNLOAD;
				break;
			case DownloadTask.STATE_STOP :
				info.mState = GoPluginOrWidgetInfo.PAUSE;
				break;
			case DownloadTask.STATE_FAIL :
				info.mState = GoPluginOrWidgetInfo.DOWNLOAD_FAIL;
				break;
			case DownloadTask.STATE_FINISH :
				info.mState = GoPluginOrWidgetInfo.DOWNLOAD_COMPLETE;
				break;
			case DownloadTask.STATE_DELETE :
				info.mState = GoPluginOrWidgetInfo.NOT_INSTALLED;
				info.mPrecent = 0;
				break;
			case DownloadTask.STATE_DOWNLOADING :
				info.mState = GoPluginOrWidgetInfo.DOWNLOADING;
				int percent = downloadTask.getAlreadyDownloadPercent();
				info.mPrecent = percent;
				break;
			case DownloadTask.STATE_RESTART :
				info.mState = GoPluginOrWidgetInfo.DOWNLOADING;
				info.mPrecent = 0;
				break;
			default :
				break;
		}
	}
	/**
	 * 根据包名判断是否是go插件或go小部件
	 * @param pkgName
	 * @return
	 */
	public GoPluginOrWidgetInfo getPluginOrWidgetPkg(String pkgName) {
		if (mAllPlugins != null) {
			String pkg = "";
			for (GoPluginOrWidgetInfo info : mAllPlugins) {
				pkg = info.mWidgetPkgName;
				if (pkgName.equals(pkg)) {
					return info;
				}
			}
		}
		
		if (mAllWidgets != null) {
			String pkg = "";
			for (GoPluginOrWidgetInfo info : mAllWidgets) {
				pkg = info.mWidgetPkgName;
				if (pkgName.equals(pkg)) {
					return info;
				}
			}
		}
		
		return null;
	}
	
	public void changeOrientation() {
//		if (mHasNewTheme) {
//			mHasNewTheme = false;
//			addNewLog();
//		}
		if (mPluginInstallContainer != null) {
			mPluginInstallContainer.changeOrientation(mIsCnUser);
		}
		if (mPluginFeatureContainer != null) {
			mPluginFeatureContainer.changeOrientation(mIsCnUser);
		}
	}
}
