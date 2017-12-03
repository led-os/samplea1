package com.jiubang.ggheart.apps.desks.appfunc.menu;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.appcenter.component.AppsManagementActivity;
import com.jiubang.ggheart.appgame.base.component.MainViewGroup;
import com.jiubang.ggheart.apps.config.ChannelConfig;
import com.jiubang.ggheart.apps.config.GOLauncherConfig;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingFunActivity;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.LockScreenHandler;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.data.statistics.AppManagementStatisticsUtil;
import com.jiubang.ggheart.data.statistics.StatisticsData;
import com.jiubang.ggheart.data.statistics.realtiemstatistics.RealTimeStatisticsContants;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.plugin.shell.IViewId;

/**
 * 所有程序菜单控制器
 * @author yejijiong
 *
 */
public class AllAppMenuControler {

	private ArrayList<BaseMenuItemInfo> mMenuItemList = null;
	private static AllAppMenuControler sInstance;
	private Context mActivity;
	private AppFuncAllAppMenuItemInfo mCleanCacheItemInfo;
	
	public AllAppMenuControler() {
		mActivity = GoLauncherActivityProxy.getActivity();
	}
	
	public static AllAppMenuControler getInstance() {
		if (sInstance == null) {
			sInstance = new AllAppMenuControler();
		}
		return sInstance;
	}
	
	public ArrayList<BaseMenuItemInfo> getMenuItemResource() {
		if (mMenuItemList == null) {
			mMenuItemList = new ArrayList<BaseMenuItemInfo>();
			
			mMenuItemList.add(new AppFuncAllAppMenuItemInfo(
					AppFuncAllAppMenuItemInfo.ACTION_RUNNING, true));
			
			mMenuItemList.add(new AppFuncAllAppMenuItemInfo(
					AppFuncAllAppMenuItemInfo.ACTION_SORT_ICON, R.string.menuitem_sorticon));
			mMenuItemList.add(new AppFuncAllAppMenuItemInfo(
					AppFuncAllAppMenuItemInfo.ACTION_CREATE_NEW_FOLDER,
					R.string.menuitem_createfolder));
			mMenuItemList.add(new AppFuncAllAppMenuItemInfo(
					AppFuncAllAppMenuItemInfo.ACTION_APP_ARRANGE_APP,
					R.string.menuitem_arrangedrawer));
			// 应用游戏分支的菜单项，要根据渠道配置信息来决定是否需要添加
			ChannelConfig channelConfig = GOLauncherConfig.getInstance(ApplicationProxy.getContext()).getChannelConfig();
			if (channelConfig != null) {
				if (channelConfig.isAddGameFunMenuItem()) {
					mMenuItemList.add(new AppFuncAllAppMenuItemInfo(
							AppFuncAllAppMenuItemInfo.ACTION_GAME_CENTER,
							R.string.appmgr_menuitem_game_center));
				}
				// 如果本渠道没有应用中心，要在功能表菜单里面加入应用管理
				if (!channelConfig.isNeedAppCenter()) {
					mMenuItemList.add(new AppFuncAllAppMenuItemInfo(
							AppFuncAllAppMenuItemInfo.ACTION_APP_MANAGEMENT,
							R.string.menuitem_apps_mananement));
				}
			}
//			mMenuItemList.add(new AppFuncAllAppMenuItemInfo(
//					AppFuncAllAppMenuItemInfo.ACTION_APP_SEARCH, R.string.menuitem_search));
			mMenuItemList.add(new AppFuncAllAppMenuItemInfo(
					AppFuncAllAppMenuItemInfo.ACTION_APPDRAWER_SETTING,
					R.string.menuitem_appfuncSetting));
			
			if (needAddCleanCache()) {
				mCleanCacheItemInfo = new AppFuncAllAppMenuItemInfo(
						AppFuncAllAppMenuItemInfo.ACTION_CLEAN_APP_CACHE,
						R.string.appfunc_menu_clean_app_cache);
				mMenuItemList.add(mCleanCacheItemInfo);
			}
		} else {
			if (needAddCleanCache()) {
				if (mCleanCacheItemInfo == null) {
					mCleanCacheItemInfo = new AppFuncAllAppMenuItemInfo(
							AppFuncAllAppMenuItemInfo.ACTION_CLEAN_APP_CACHE,
							R.string.appfunc_menu_clean_app_cache);
					mMenuItemList.add(mCleanCacheItemInfo);
				}
			} else {
				if (mCleanCacheItemInfo != null) {
					mMenuItemList.remove(mCleanCacheItemInfo);
					mCleanCacheItemInfo = null;
				}
			}
		}
		return mMenuItemList;
	}
	
	private boolean needAddCleanCache() {
		PreferencesManager pre = new PreferencesManager(mActivity);
		boolean isRecommendDuSpeedBooster = pre.getBoolean(
				IPreferencesIds.PREFERENCE_IS_RECOMMEND_DUSPEEDBOOSTER, false);
		if (isRecommendDuSpeedBooster) {
			if (!GoAppUtils.isAppExist(mActivity, PackageName.DU_SPEED_BOOSTER)) {
				return true;
			} else {
				return false;
			}
		} else {
			if (!GoAppUtils.isOldCMExist(mActivity)) {
				// 清除应用缓存
				FunctionPurchaseManager purchaseManager = FunctionPurchaseManager
						.getInstance(mActivity.getApplicationContext());
				boolean hasPayAd = purchaseManager
						.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_AD);

				//原来（）：已安装 CM 时,点击打开 CM;未安装 CM 时,点击跳转到电子市场； 现在：若新用户安装 GO 桌面时已经安装了CM,则没有“清除应用缓存”,针对200渠道包 2014.01.10
				boolean isRecommend = true;
				if (GoAppUtils.isMarketNotExitAnd200Channel(mActivity)) {
					isRecommend = false;
				}

				if ((!hasPayAd || (hasPayAd && !DeskSettingUtils.isNoAdvert())) && isRecommend
						&& !GoAppUtils.isAppExist(mActivity, PackageName.CLEAN_MASTER_PACKAGE)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean handleItemClickEvent(int actionId) {
		switch (actionId) {
			case AppFuncAllAppMenuItemInfo.ACTION_RUNNING :
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME,
						ICommonMsgId.SHOW_EXTEND_FUNC_VIEW, 1, IViewId.PRO_MANAGE);
				return true;
			case AppFuncAllAppMenuItemInfo.ACTION_SORT_ICON :
				if (SettingProxy.getScreenSettingInfo().mLockScreen) { // 判断当前是否锁屏
					LockScreenHandler.showLockScreenNotification(mActivity);
					return true;
				}
				//用户行为统计。
				StatisticsData.countUserActionData(
						StatisticsData.FUNC_ACTION_ID_APPLICATION,
						StatisticsData.USER_ACTION_SEVEN,
						IPreferencesIds.APP_FUNC_ACTION_DATA);
				return false;
			case AppFuncAllAppMenuItemInfo.ACTION_CREATE_NEW_FOLDER :
				if (SettingProxy.getScreenSettingInfo().mLockScreen) { // 判断当前是否锁屏
					LockScreenHandler.showLockScreenNotification(mActivity);
					return true;
				}
				//用户行为统计
				StatisticsData.countUserActionData(
						StatisticsData.FUNC_ACTION_ID_APPLICATION,
						StatisticsData.USER_ACTION_EIGHT,
						IPreferencesIds.APP_FUNC_ACTION_DATA);
				return false;
			case AppFuncAllAppMenuItemInfo.ACTION_APP_CENTER :
				AppsManagementActivity.startAppCenter(mActivity,
					MainViewGroup.ACCESS_FOR_FUNC_MUNE, true,
					RealTimeStatisticsContants.AppgameEntrance.FUNC_MENU);
				AppManagementStatisticsUtil.getInstance().saveCurrentEnter(mActivity,
						AppManagementStatisticsUtil.ENTRY_TYPE_FUNTAB_ICON);
				return true;
//			case AppFuncAllAppMenuItemInfo.ACTION_APP_SEARCH :
//				mMainView.doOptionsItemSelected(AppFuncAllAppMenuItemInfo.ACTION_APP_SEARCH);
//				//用户行为统计
//				StatisticsData.countUserActionData(
//						StatisticsData.FUNC_ACTION_ID_APPLICATION,
//						StatisticsData.USER_ACTION_ELEVEN,
//						IPreferencesIds.APP_FUNC_ACTION_DATA);
//				break;
			case AppFuncAllAppMenuItemInfo.ACTION_APPDRAWER_SETTING :
				Intent intent = new Intent(mActivity, DeskSettingFunActivity.class);
				if (intent != null) {
					mActivity.startActivity(intent);
				}
				//用户行为统计
				StatisticsData.countUserActionData(
						StatisticsData.FUNC_ACTION_ID_APPLICATION,
						StatisticsData.USER_ACTION_TWELVE,
						IPreferencesIds.APP_FUNC_ACTION_DATA);
				return true;
			case AppFuncAllAppMenuItemInfo.ACTION_APP_MANAGEMENT :
				// 启动appsmanagement activity
				AppsManagementActivity.startAppCenter(mActivity,
						MainViewGroup.ACCESS_FOR_WIDGET_MANAGER, false);
				//用户行为统计
				StatisticsData.countUserActionData(StatisticsData.FUNC_ACTION_ID_APPLICATION,
						StatisticsData.USER_ACTION_TEN, IPreferencesIds.APP_FUNC_ACTION_DATA);
				return true;
			case AppFuncAllAppMenuItemInfo.ACTION_APP_ARRANGE_APP :
				if (SettingProxy.getScreenSettingInfo().mLockScreen) { // 判断当前是否锁屏
					LockScreenHandler.showLockScreenNotification(mActivity);
					return true;
				}
				//用户行为统计
				StatisticsData.countUserActionData(
						StatisticsData.FUNC_ACTION_ID_APPLICATION,
						StatisticsData.USER_ACTION_TWENTY,
						IPreferencesIds.APP_FUNC_ACTION_DATA);
				return false;
				
			case AppFuncAllAppMenuItemInfo.ACTION_CLEAN_APP_CACHE :
				PreferencesManager pre = new PreferencesManager(mActivity);
				boolean isRecommendDuSpeedBooster = pre.getBoolean(
						IPreferencesIds.PREFERENCE_IS_RECOMMEND_DUSPEEDBOOSTER, false);
				// CM比go桌面早安装，则推荐安卓优化大师
				if (isRecommendDuSpeedBooster) {
					// 需要传入广告id
					GoAppUtils.openDetailsWithUrl(mActivity, PackageName.DU_SPEED_BOOSTER,
							"591", "5377557", LauncherEnv.Url.DU_SPEED_BOOSTER_URL);
				} else {
					if (!GoAppUtils.openDetailsWithGALink(mActivity, PackageName.CLEAN_MASTER_PACKAGE,
							"204", "2463865", LauncherEnv.Plugin.CLEAN_MASTER_GA_IN_APPFUNC_MENU)) {
						GoAppUtils.launchCleanMaster(mActivity);
					}
				}
				PreferencesManager prefManager = new PreferencesManager(mActivity, 
						IPreferencesIds.PREFERENCE_APPFUNC_CLEANMASTER_NEW, Context.MODE_PRIVATE);
				prefManager.putBoolean(IPreferencesIds.PREFERENCE_APPFUNC_CLEANMASTER_HAS_CLICKED, false);
				prefManager.commit();
//				if (!GoAppUtils.openDetailsWithGALink(mActivity, PackageName.CLEAN_MASTER_PACKAGE,
//						"204", "2463865", LauncherEnv.Plugin.CLEAN_MASTER_GA_IN_APPFUNC_MENU)) {
//					PreferencesManager prefManager = new PreferencesManager(mActivity, 
//							IPreferencesIds.PREFERENCE_APPFUNC_CLEANMASTER_NEW, Context.MODE_PRIVATE);
//					prefManager.putBoolean(IPreferencesIds.PREFERENCE_APPFUNC_CLEANMASTER_HAS_CLICKED, false);
//					prefManager.commit();
//					// 安卓优化大师，广告ID 591， mapID 5377557
//					if (!GoAppUtils.openDetailsWithUrl(mActivity, PackageName.DU_SPEED_BOOSTER,
//							"591", "5377557", LauncherEnv.Url.DU_SPEED_BOOSTER_URL)) {
//						GoAppUtils.launchCleanMaster(mActivity);
//					}
//				}
				return true;
		}
		return false;
	}
	
	public static final int ITEM_STATUS_NONE = 0;
	public static final int ITEM_STATUS_SHOW_NUM = 1;
	public static final int ITEM_STATUS_SHOW_NEW = 2;
	public int checkMenuItemStatus(int actionId) {
		if (actionId == AppFuncAllAppMenuItemInfo.ACTION_APP_CENTER
				|| actionId == AppFuncAllAppMenuItemInfo.ACTION_APP_MANAGEMENT) {
			return ITEM_STATUS_SHOW_NUM;
		} else if (actionId == AppFuncAllAppMenuItemInfo.ACTION_CLEAN_APP_CACHE) {
			PreferencesManager pm = new PreferencesManager(mActivity,
					IPreferencesIds.PREFERENCE_APPFUNC_CLEANMASTER_NEW, Context.MODE_PRIVATE);
			boolean needShowNewDrawable = pm.getBoolean(
					IPreferencesIds.PREFERENCE_APPFUNC_CLEANMASTER_HAS_CLICKED, true);
			if (needShowNewDrawable) {
				return ITEM_STATUS_SHOW_NEW;
			}
		}
		return ITEM_STATUS_NONE;
	}
	
	/**
	 * 获取功能表所有程序菜单菜单按钮是否显示New标识
	 * @return
	 */
	public boolean getAllAppMenuIconNeedShowNew() {
		PreferencesManager pm = new PreferencesManager(ApplicationProxy.getContext(),
				IPreferencesIds.PREFERENCE_APPFUNC_CLEANMASTER_NEW, Context.MODE_PRIVATE);
		boolean needShowMenuLight = pm.getBoolean(
				IPreferencesIds.PREFERENCE_APPFUNC_MENU_NEED_SHOW_LIGHT, true);
		if (needShowMenuLight) {
			if (needAddCleanCache()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 设置功能表所有程序菜单菜单按钮是否显示New标识
	 * @return
	 */
	public void setAllAppMenuIconNeedShowNew(boolean isNeedShow) {
		PreferencesManager pm = new PreferencesManager(mActivity,
				IPreferencesIds.PREFERENCE_APPFUNC_CLEANMASTER_NEW,
				Context.MODE_PRIVATE);
		pm.putBoolean(
				IPreferencesIds.PREFERENCE_APPFUNC_MENU_NEED_SHOW_LIGHT,
				isNeedShow);
		pm.commit();
	}
}
