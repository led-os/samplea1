package com.jiubang.ggheart.apps.desks.appfunc.menu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
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
import com.jiubang.ggheart.apps.desks.diy.StatusBarHandler;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.plugin.mediamanagement.inf.AppFuncContentTypes;
import com.jiubang.ggheart.plugin.mediamanagement.inf.OnSwitchMenuItemClickListener;

/**
 * 
 * 功能表选项卡按钮菜单
 */
public class AppFuncSwitchListMenu extends BaseListMenu {

	private OnSwitchMenuItemClickListener mListener;
	private SwitchControler mSwitchControler;
	public AppFuncSwitchListMenu(Activity activity) {
		super(activity);
		mSwitchControler = SwitchControler.getInstance(activity);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
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
					AppFuncContentTypes.sType_for_setting = AppFuncContentTypes.IMAGE;
					mSwitchControler.showMediaManagementImageContent();
					break;
				case AppFuncSwitchMenuItemInfo.ACTION_GO_TO_AUDIO :
					AppFuncContentTypes.sType_for_setting = AppFuncContentTypes.MUSIC;
					mSwitchControler.showMediaManagementMusicContent();
					break;
				case AppFuncSwitchMenuItemInfo.ACTION_GO_TO_VIDEO :
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
			mAdapter.setItemPadding(0, DrawUtils.dip2px(4), 0, 0);
			mAdapter.setItemTextSize(0);
			int offset = AppFuncUtils.getInstance(mActivity).getStandardSize(10);
			if (Machine.IS_SDK_ABOVE_KITKAT) {
				offset -= DrawUtils.getNavBarHeight();
			}
			show(parent,
					StatusBarHandler.getDisplayWidth(),
					AppFuncUtils.getInstance(ApplicationProxy.getContext()).getDimensionPixelSize(
							R.dimen.appfunc_bottomheight)
							- offset, AppFuncUtils.getInstance(mActivity).getStandardSize(100),
					LayoutParams.WRAP_CONTENT);
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (v == mListView && keyCode == KeyEvent.KEYCODE_MENU
				|| keyCode == KeyEvent.KEYCODE_SEARCH) {
			if (isShowing()) {
				dismiss();
				return true;
			}
		}
		return false;
	}

	@Override
	protected void loadThemeResource() {
		String curPackageName = mThemeCtrl.getThemeBean().mSwitchMenuBean.mPackageName;
		String packageName = null;
		if (!curPackageName.equals(SettingProxy.getFunAppSetting()
				.getTabHomeBgSetting())) {
			packageName = SettingProxy.getFunAppSetting()
					.getTabHomeBgSetting();
		}
		if (!GoAppUtils.isAppExist(mActivity, packageName)) {
			packageName = ThemeManager.getInstance(ApplicationProxy.getContext()).getCurThemePackage();
		}
		mMenuBgV = mThemeCtrl.getDrawable(mThemeCtrl.getThemeBean().mSwitchMenuBean.mMenuBgV,
				packageName);
		if (mMenuBgV == null) {
			mMenuBgV = mActivity.getResources().getDrawable(R.drawable.appfunc_switch_menu_bg);
		}
		mMenuDividerV = mThemeCtrl.getDrawable(
				mThemeCtrl.getThemeBean().mSwitchMenuBean.mMenuDividerV, packageName);
		if (mMenuDividerV == null) {
			mMenuDividerV = mActivity.getResources().getDrawable(
					R.drawable.allfunc_allapp_menu_line);
		}
		mTextColor = mThemeCtrl.getThemeBean().mSwitchMenuBean.mMenuTextColor;
		if (mTextColor == 0) {
			mTextColor = 0xff000000;
		}
		mItemSelectedBg = mThemeCtrl.getDrawable(
				mThemeCtrl.getThemeBean().mSwitchMenuBean.mMenuItemSelected, packageName);
		if (mItemSelectedBg == null) {
			mItemSelectedBg = mActivity.getResources().getDrawable(R.drawable.transparent);
		}
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
