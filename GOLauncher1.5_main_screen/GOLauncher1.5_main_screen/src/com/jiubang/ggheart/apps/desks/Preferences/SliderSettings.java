package com.jiubang.ggheart.apps.desks.Preferences;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.go.proxy.SettingProxy;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.launcher.ICustomAction;

/**
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  xuqian
 * @date  [2013-6-28]
 */
public class SliderSettings {
	private static SliderSettings sSettings = null;
	private PreferencesManager mSharedPreferences = null;

	public ViewResponAreaInfo mViewResponAreaInfo = null;

	private Context mContext = null;

	private SliderSettings(Context context) {
		mSharedPreferences = new PreferencesManager(context, IPreferencesIds.PREFERENCE_SIDE_DOCK,
				Context.MODE_PRIVATE);
		mContext = context;
		init();
	}

	private void init() {
		if (SettingProxy.getDesktopSettingInfo().mSideDockPosition == 0) {
			mViewResponAreaInfo = getResponAreaInfoLeft();
		} else {
			mViewResponAreaInfo = getResponAreaInfoRight();
		}

	}

	public static synchronized SliderSettings getInstence(Context context) {
		if (sSettings == null) {
			sSettings = new SliderSettings(context);
		}
		return sSettings;
	}

	public ViewResponAreaInfo getResponAreaInfoLeft() {
		mViewResponAreaInfo = new ViewResponAreaInfo();

		mViewResponAreaInfo.setLeftAreaX(mSharedPreferences.getFloat(
				IPreferencesIds.PREFERENCE_LEFT_AREAINFO_LEFTAREAX, 0f));
		mViewResponAreaInfo.setLeftAreaY(mSharedPreferences.getFloat(
				IPreferencesIds.PREFERENCE_LEFT_AREAINFO_LEFTAREAY, 0.08f));
		mViewResponAreaInfo.setLeftAreaWidth(mSharedPreferences.getFloat(
				IPreferencesIds.PREFERENCE_LEFT_AREAINFO_LEFTAREAWIDTH, SideBarDefaultConfigUtil.getDefaultConfigWidthScale(mContext)));
		mViewResponAreaInfo.setLeftAreaHeight(mSharedPreferences.getFloat(
				IPreferencesIds.PREFERENCE_LEFT_AREAINFO_LEFTAREAHEIGHT, 0.84f));

		return mViewResponAreaInfo;
	}

	public void setResponAreaInfoLeft(ViewResponAreaInfo info) {
		mViewResponAreaInfo = info;

		mSharedPreferences.putFloat(IPreferencesIds.PREFERENCE_LEFT_AREAINFO_LEFTAREAX,
				info.getLeftAreaX());
		mSharedPreferences.putFloat(IPreferencesIds.PREFERENCE_LEFT_AREAINFO_LEFTAREAY,
				info.getLeftAreaY());
		mSharedPreferences.putFloat(IPreferencesIds.PREFERENCE_LEFT_AREAINFO_LEFTAREAWIDTH,
				info.getLeftAreaWidth());
		mSharedPreferences.putFloat(IPreferencesIds.PREFERENCE_LEFT_AREAINFO_LEFTAREAHEIGHT,
				info.getLeftAreaHeight());

		mSharedPreferences.commit();

		Intent intent = new Intent();
		if (Build.VERSION.SDK_INT >= 12) {
			intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		}
		intent.setAction(ICustomAction.ACTION_START_LEFT_AREA_SIDEBAR);
		intent.putExtra(IPreferencesIds.PREFERENCE_LEFT_AREAINFO_LEFTAREAX, info.getLeftAreaX());
		intent.putExtra(IPreferencesIds.PREFERENCE_LEFT_AREAINFO_LEFTAREAY, info.getLeftAreaY());
		intent.putExtra(IPreferencesIds.PREFERENCE_LEFT_AREAINFO_LEFTAREAWIDTH,
				info.getLeftAreaWidth());
		intent.putExtra(IPreferencesIds.PREFERENCE_LEFT_AREAINFO_LEFTAREAHEIGHT,
				info.getLeftAreaHeight());
		mContext.sendBroadcast(intent);

	}

	public ViewResponAreaInfo getResponAreaInfoRight() {

		mViewResponAreaInfo = new ViewResponAreaInfo();

		mViewResponAreaInfo.setRightAreaX(mSharedPreferences.getFloat(
				IPreferencesIds.PREFERENCE_RIGHT_AREAINFO_RIGHTAREAX, 1 - 0f - SideBarDefaultConfigUtil.getDefaultConfigWidthScale(mContext)));
		mViewResponAreaInfo.setRightAreaY(mSharedPreferences.getFloat(
				IPreferencesIds.PREFERENCE_RIGHT_AREAINFO_RIGHTAREAY, 0.08f));
		mViewResponAreaInfo.setRightAreaWidth(mSharedPreferences.getFloat(
				IPreferencesIds.PREFERENCE_RIGHT_AREAINFO_RIGHTAREAWIDTH, SideBarDefaultConfigUtil.getDefaultConfigWidthScale(mContext)));
		mViewResponAreaInfo.setRightAreaHeight(mSharedPreferences.getFloat(
				IPreferencesIds.PREFERENCE_RIGHT_AREAINFO_RIGHTAREAHEIGHT, 0.84f));
		return mViewResponAreaInfo;
	}

	public void setResponAreaInfoRight(ViewResponAreaInfo info) {
		mViewResponAreaInfo = info;

		mSharedPreferences.putFloat(IPreferencesIds.PREFERENCE_RIGHT_AREAINFO_RIGHTAREAX,
				info.getRightAreaX());
		mSharedPreferences.putFloat(IPreferencesIds.PREFERENCE_RIGHT_AREAINFO_RIGHTAREAY,
				info.getRightAreaY());
		mSharedPreferences.putFloat(IPreferencesIds.PREFERENCE_RIGHT_AREAINFO_RIGHTAREAWIDTH,
				info.getRightAreaWidth());
		mSharedPreferences.putFloat(IPreferencesIds.PREFERENCE_RIGHT_AREAINFO_RIGHTAREAHEIGHT,
				info.getRightAreaHeight());

		mSharedPreferences.commit();

		Intent intent = new Intent();
		if (Build.VERSION.SDK_INT >= 12) {
			intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		}
		intent.setAction(ICustomAction.ACTION_START_RIGHT_AREA_SIDEBAR);
		intent.putExtra(IPreferencesIds.PREFERENCE_RIGHT_AREAINFO_RIGHTAREAX, info.getRightAreaX());
		intent.putExtra(IPreferencesIds.PREFERENCE_RIGHT_AREAINFO_RIGHTAREAY, info.getRightAreaY());
		intent.putExtra(IPreferencesIds.PREFERENCE_RIGHT_AREAINFO_RIGHTAREAWIDTH,
				info.getRightAreaWidth());
		intent.putExtra(IPreferencesIds.PREFERENCE_RIGHT_AREAINFO_RIGHTAREAHEIGHT,
				info.getRightAreaHeight());
		mContext.sendBroadcast(intent);
	}

	public ViewResponAreaInfo getResponAreaInfo() {
		if (mViewResponAreaInfo == null) {
			init();
		}
		return mViewResponAreaInfo;
	}
}
