package com.jiubang.ggheart.apps.desks.appfunc.menu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.SettingProxy;
import com.go.util.device.Machine;
import com.go.util.graphics.DrawUtils;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.appfunc.controler.SwitchControler;
import com.jiubang.ggheart.apps.desks.appfunc.help.AppFuncUtils;
import com.jiubang.ggheart.data.statistics.StatisticsData;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.plugin.mediamanagement.inf.AppFuncContentTypes;
import com.jiubang.ggheart.plugin.mediamanagement.inf.OnSwitchMenuItemClickListener;

/**
 * 
 * 选项卡横屏菜单
 */
public class AppFuncSwitchHorizontalMenu extends BaseHorizontalMenu {

	private OnSwitchMenuItemClickListener mListener;
	private SwitchControler mSwitchControler;
	
	public AppFuncSwitchHorizontalMenu(Activity activity) {
		super(activity);
		mSwitchControler = SwitchControler.getInstance(activity);
	}

	@Override
	public void onItemClick(int position) {
		AppFuncSwitchMenuItemInfo itemInfo = (AppFuncSwitchMenuItemInfo) mAdapter.getItem(position);
		if (itemInfo != null) {
			if (mListener != null) {
				mListener.preMenuItemClick(itemInfo.mActionId);
			}
			switch (itemInfo.mActionId) {
				case AppFuncSwitchMenuItemInfo.ACTION_GO_TO_APP :
					List<Object> objs = null;
					objs = new ArrayList<Object>();
					objs.add(false);
					mSwitchControler.showAppDrawerFrame(false);
					break;
				case AppFuncSwitchMenuItemInfo.ACTION_GO_TO_IMAGE :
					StatisticsData.countMenuData(mActivity, StatisticsData.FUNTAB_KEY_IMAGE);
					AppFuncContentTypes.sType_for_setting = AppFuncContentTypes.IMAGE;
					mSwitchControler.showMediaManagementImageContent();
					break;
				case AppFuncSwitchMenuItemInfo.ACTION_GO_TO_AUDIO :
					StatisticsData.countMenuData(mActivity, StatisticsData.FUNTAB_KEY_AUDIO);
					AppFuncContentTypes.sType_for_setting = AppFuncContentTypes.MUSIC;
					mSwitchControler.showMediaManagementMusicContent();
					break;
				case AppFuncSwitchMenuItemInfo.ACTION_GO_TO_VIDEO :
					StatisticsData.countMenuData(mActivity, StatisticsData.FUNTAB_KEY_VIDEO);
					AppFuncContentTypes.sType_for_setting = AppFuncContentTypes.VIDEO;
					mSwitchControler.showMediaManagementVideoContent();
					break;
				case AppFuncSwitchMenuItemInfo.ACTION_GO_TO_SEARCH :
					mSwitchControler.showSearchFrame(true, null);
					break;
				default :
					break;
			}
			if (mListener != null) {
				mListener.postMenuItemClick(itemInfo.mActionId);
			}
		}
		mPopupWindow.dismiss();
	}

	@Override
	public void show(View parent) {
		if (!isShowing()) {
			int y = 0;
			y = GoLauncherActivityProxy.getScreenHeight()
					- AppFuncUtils.getInstance(mActivity).getStandardSize(100);
			int offset = AppFuncUtils.getInstance(mActivity).getStandardSize(10);
			if (Machine.IS_SDK_ABOVE_KITKAT) {
				offset -= DrawUtils.getNavBarWidth();
			}
			show(parent, AppFuncUtils.getInstance(ApplicationProxy.getContext())
					.getDimensionPixelSize(R.dimen.appfunc_bottomheight) - offset, y,
					LayoutParams.WRAP_CONTENT,
					AppFuncUtils.getInstance(mActivity).getStandardSize(100));
		}
	}

	@Override
	protected void initialize() {
		super.initialize();
		mAdapter.setItemPadding(0, DrawUtils.dip2px(4), 0, 0);
		mAdapter.setItemTextSize(0);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_SEARCH) {
			if (isShowing()) {
				dismiss();
				return true;
			}
		}
		return false;
	}

	@Override
	protected void loadThemeResource() {
		String curPackageName = ThemeManager.getInstance(mActivity).getCurThemePackage();
		String packageName = null;
		if (!curPackageName.equals(SettingProxy.getFunAppSetting()
				.getTabHomeBgSetting())) {
			packageName = SettingProxy.getFunAppSetting()
					.getTabHomeBgSetting();
		}
		if (!GoAppUtils.isAppExist(mActivity, packageName)) {
			packageName = ThemeManager.getInstance(ApplicationProxy.getContext()).getCurThemePackage();
		}

		mMenuBg = mThemeCtrl.getDrawable(mThemeCtrl.getThemeBean().mSwitchMenuBean.mMenuBgH,
				packageName);
		if (mMenuBg == null) {
			mMenuBg = mActivity.getResources().getDrawable(R.drawable.appfunc_switch_menu_bg_h);
		}
		mMenuDivider = mThemeCtrl.getDrawable(
				mThemeCtrl.getThemeBean().mSwitchMenuBean.mMenuDividerH, packageName);
		if (mMenuDivider == null) {
			mMenuDivider = mActivity.getResources()
					.getDrawable(R.drawable.allfunc_allapp_menu_line);
		}
		mTextColor = mThemeCtrl.getThemeBean().mSwitchMenuBean.mMenuTextColor;
		if (mTextColor == 0) {
			mTextColor = 0xff000000;
		}
		mContainer.setDivider(mMenuDivider);
	}

	@Override
	protected void initAnimationStyle(PopupWindow popupWindow) {
		if (popupWindow != null) {
			if (GoLauncherActivityProxy.isPortait()) {
				popupWindow.setAnimationStyle(R.style.SwtichMenuAnimationZoom);
			} else {
				popupWindow.setAnimationStyle(R.style.SwtichMenuAnimationZoomH);
			}
		}
	}

	public void setOnSwitchMenuItemClickListener(OnSwitchMenuItemClickListener listener) {
		mListener = listener;
	}
}
