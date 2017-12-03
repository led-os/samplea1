package com.jiubang.ggheart.apps.desks.diy.themescan;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.go.commomidentify.IGoLauncherClassName;
import com.go.util.ConvertUtils;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.message.IAppDrawerMsgId;
import com.golauncher.message.IDockMsgId;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.data.Setting;
import com.jiubang.ggheart.data.ContentProvider.GoContentProvider;
import com.jiubang.ggheart.data.info.ShortCutSettingInfo;
import com.jiubang.ggheart.data.model.GoContentProviderUtil;
import com.jiubang.ggheart.data.tables.AppFuncSettingTable;
import com.jiubang.ggheart.data.tables.ScreenStyleConfigTable;
import com.jiubang.ggheart.data.tables.ShortcutSettingTable;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.data.theme.broadcastReceiver.MyThemeReceiver;
import com.jiubang.ggheart.launcher.ICustomAction;

/**
 * 判断主题是否被修改
 * 
 * 
 * @author yangbing
 * */
public class ThemePrefSettingManager {

	private Context mContext;
//	private ScreenStyleConfigInfo mScreenStyleConfigInfo;
	private String mPackageName;
	private String mCurPackageName; // 当前正在使用的主题
	private boolean mIsAppNameColorModifyed; // 程序名颜色设置

	private boolean mIsShortCutBgModifyed; // 快捷条背景
	private boolean mIsFuncBgModifyed; // 功能表背景
	private boolean mIsTabHomeBgModifyed; // 选项卡和底座背景
	private boolean mIsIconStyleModifyed; // 主题图标
	private boolean mIsShortCutStyleModifyed; // 快捷条图标风格
	private boolean mIsFolderStyleModifyed; // 文件夹图标风格
	private boolean mIsGGMenuStyleModifyed; // 菜单项图标风格
	private boolean mIsIndicatorStyleModifyed; // 指示器风格

	private ContentResolver mContentResolver;

	public ThemePrefSettingManager(Context mContext, String packageName) {
		this.mContext = mContext;
		mContentResolver = mContext.getContentResolver();

		this.mPackageName = packageName;
//		mSettingControler = GoSettingControler.getInstance(ApplicationProxy.getContext());
//		mScreenStyleConfigInfo = mSettingControler.getScreenStyleSettingInfo();
		mCurPackageName = ThemeManager.getInstance(mContext).getCurThemePackage();
	}

	/**
	 * 判断主题是否被修改
	 * 
	 * @param packageName
	 * */
	public boolean isModifyed() {

		mIsIconStyleModifyed = isIconStyleModifyed();
		mIsShortCutStyleModifyed = isShortCutStyleModifyed();
		mIsFolderStyleModifyed = isFolderStyleModifyed();
		mIsGGMenuStyleModifyed = isGGMenuStyleModifyed();
		mIsIndicatorStyleModifyed = isIndicatorStyleModifyed();
		mIsTabHomeBgModifyed = isTabHomeBgModifyed();
		mIsShortCutBgModifyed = isShortCutBgModifyed();
		mIsFuncBgModifyed = isFuncBgModifyed();
		// mIsAppNameColorModifyed = isAppNameColorModifyed();

		return mIsIconStyleModifyed || mIsShortCutStyleModifyed || mIsFolderStyleModifyed
				|| mIsGGMenuStyleModifyed || mIsIndicatorStyleModifyed || mIsShortCutBgModifyed
				|| mIsFuncBgModifyed || mIsTabHomeBgModifyed;

	}

	private boolean isShortCutStyleModifyed() {
		String shortCutStyle = null;
		String selection = ShortcutSettingTable.THEMENAME + " = " + "'" + mPackageName + "'";
		String[] projection = new String[] { ShortcutSettingTable.STYLE_STRING };
		Cursor cursor = mContentResolver.query(GoContentProvider.CONTENT_SHORTCUTSETTING_URI,
				projection, selection, null, null);
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					shortCutStyle = cursor.getString(0);
				}

			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				cursor.close();
			}
		}
		if (IGoLauncherClassName.DEFAULT_THEME_PACKAGE.equals(mPackageName)
				&& "defaultstyle".equals(shortCutStyle)) {
			return false;
		}
		if (shortCutStyle == null || mPackageName.equals(shortCutStyle)) {

			return false;
		}
		return true;

	}

	/**
	 * 程序名称颜色
	 * */
//	private boolean isAppNameColorModifyed() {
//		DesktopSettingInfo desktopSettingInfo = mSettingControler.getDesktopSettingInfo();
//		if (!desktopSettingInfo.mCustomTitleColor) {
//			mIsAppNameColorModifyed = true;
//			return true;
//		}
//		return false;
//	}

	/**
	 * 指示器风格
	 * */
	private boolean isIndicatorStyleModifyed() {
		String indicatorStyle = GoContentProviderUtil.getScreenStyleSetting(mContext, mPackageName,
				Setting.INDICATOR);
		if (indicatorStyle == null || mPackageName.equals(indicatorStyle)) {

			return false;
		}
		return true;
	}

//	private String getItemStyle(int item) {
//		String columnName = null;
//		switch (item) {
//			case Setting.THEMEPACKAGE :
//				columnName = ScreenStyleConfigTable.THEMEPACKAGE;
//				break;
//			case Setting.ICONSTYLEPACKAGE :
//				columnName = ScreenStyleConfigTable.ICONSTYLEPACKAGE;
//				break;
//			case Setting.FOLDERSTYLEPACKAGE :
//				columnName = ScreenStyleConfigTable.FOLDERSTYLEPACKAGE;
//				break;
//			case Setting.GGMENUPACKAGE :
//				columnName = ScreenStyleConfigTable.GGMENUPACKAGE;
//				break;
//			case Setting.INDICATOR :
//				columnName = ScreenStyleConfigTable.INDICATOR;
//				break;
//			default :
//				break;
//		}
//		String selection = ScreenStyleConfigTable.THEMEPACKAGE + " = '" + mPackageName + "'";
//		// 查询列数
//		String projection[] = { columnName };
//
//		Cursor cursor = mContentResolver.query(GoContentProvider.CONTENT_SCREENSTYLE_URI,
//				projection, selection, null, null);
//		String value = null;
//		if (cursor != null) {
//			try {
//				if (cursor.moveToFirst()) {
//					int valueIndex = cursor.getColumnIndex(columnName);
//					value = cursor.getString(valueIndex);
//				}
//			} catch (SQLiteException e) {
//				e.printStackTrace();
//			} finally {
//				cursor.close();
//			}
//		}
//		return value;
//	}
	/**
	 * 菜单项图标风格
	 * */
	private boolean isGGMenuStyleModifyed() {
		String ggmenuStyle = GoContentProviderUtil.getScreenStyleSetting(mContext, mPackageName, Setting.GGMENUPACKAGE);
		if (ggmenuStyle == null || mPackageName.equals(ggmenuStyle)
				|| IGoLauncherClassName.DEFAULT_THEME_PACKAGE.equals(ggmenuStyle)) {

			return false;
		}
		return true;
	}

	/**
	 * 文件夹图标风格
	 * */
	private boolean isFolderStyleModifyed() {
		String folderStyle = GoContentProviderUtil.getScreenStyleSetting(mContext, mPackageName, Setting.FOLDERSTYLEPACKAGE);
		if (folderStyle == null || mPackageName.equals(folderStyle)) {
			mIsFolderStyleModifyed = true;
			return false;
		}
		return true;
	}

	/**
	 * 主题图标
	 * */
	private boolean isIconStyleModifyed() {
		String iconStyle = GoContentProviderUtil.getScreenStyleSetting(mContext, mPackageName, Setting.ICONSTYLEPACKAGE);
		if (iconStyle == null || mPackageName.equals(iconStyle)) {

			return false;
		}
		return true;
	}

	/**
	 * 选项卡和底座背景
	 * */
	private boolean isTabHomeBgModifyed() {
		//		FunAppSetting mFunAppSetting = mSettingControler.getFunAppSetting();
		String bgName = getFunAppSettingItem(Setting.TAB_HOME_BG); //mFunAppSetting.getAppFuncSetting(mPackageName, Setting.TAB_HOME_BG, null);
		if (bgName == null || mPackageName.equals(bgName)) {
			return false;
		}
		return true;
	}

	/**
	 * 功能表背景是否被修改
	 * */
	private boolean isFuncBgModifyed() {
//		FunAppSetting mFunAppSetting = mSettingControler.getFunAppSetting();
		String mFuncBg = getFunAppSettingItem(Setting.BGVISIABLE);
		
		if (mFuncBg == null || FunAppSetting.BG_DEFAULT == Integer.parseInt(mFuncBg)) {
			return false;
		}
		return true;
	}

	private String getFunAppSettingItem(int item) {
		String value = null;
		String selection = AppFuncSettingTable.PKNAME + "='" + mPackageName + "' and "
				+ AppFuncSettingTable.SETTINGKEY + "=" + item;
		String columns[] = { AppFuncSettingTable.SETTINGVALUE, };
		Cursor cursor = mContentResolver.query(GoContentProvider.CONTENT_APPFUNCSETTING_URI,
				columns, selection, null, null);

		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					int valueIndex = cursor.getColumnIndex(AppFuncSettingTable.SETTINGVALUE);
					value = cursor.getString(valueIndex);
				}
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				cursor.close();
			}
		}
		return value;
	}
	
	private void updateFunAppSettingItem(String pkname, int key, String value) {
		boolean update = false;
		String selection = AppFuncSettingTable.PKNAME + "='" + pkname + "' and "
				+ AppFuncSettingTable.SETTINGKEY + "=" + key;
		String columns[] = { AppFuncSettingTable.SETTINGVALUE, };
		Cursor cur = mContentResolver.query(GoContentProvider.CONTENT_APPFUNCSETTING_URI, columns,
				selection, null, null);
		if (cur != null) {
			try {
				if (cur.moveToFirst()) {
					update = true;
				}
			} finally {
				cur.close();
			}
		}
		ContentValues values = new ContentValues();
		values.put(AppFuncSettingTable.SETTINGKEY, key);
		values.put(AppFuncSettingTable.SETTINGVALUE, value);
		values.put(AppFuncSettingTable.PKNAME, pkname);
		try {
			if (update) {
				mContentResolver.update(GoContentProvider.CONTENT_APPFUNCSETTING_URI, values,
						selection, null);
			} else {
				mContentResolver.insert(GoContentProvider.CONTENT_APPFUNCSETTING_URI, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 快捷条背景是否修改
	 * */
	private boolean isShortCutBgModifyed() {
		ShortCutSettingInfo shortCutSettingInfo = new ShortCutSettingInfo();
		String selection = ShortcutSettingTable.THEMENAME + " = " + "'" + mPackageName + "'";
		Cursor cursor = mContentResolver.query(GoContentProvider.CONTENT_SHORTCUTSETTING_URI, null,
				selection, null, null);
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					shortCutSettingInfo.mBgPicSwitch = ConvertUtils.int2boolean(cursor
							.getInt(cursor.getColumnIndex(ShortcutSettingTable.BGPICSWITCH)));
					shortCutSettingInfo.mBgtargetthemename = cursor.getString(cursor
							.getColumnIndex(ShortcutSettingTable.BG_TARGET_THEME_NAME));
					shortCutSettingInfo.mBgresname = cursor.getString(cursor
							.getColumnIndex(ShortcutSettingTable.BG_RESNAME));
				}
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				cursor.close();
			}
		}
		if (!shortCutSettingInfo.mBgPicSwitch) {
			// 无背景
			return true;
		}
		if (shortCutSettingInfo.mBgtargetthemename == null
				&& shortCutSettingInfo.mBgresname == null) {
			return false;
		}
		if (mPackageName.equals(shortCutSettingInfo.mBgtargetthemename)) {
			return false;
		}
		return true;
	}

	/**
	 * 恢复主题到默认设置
	 * 
	 * */
	public void resetSetting() {

		if (mIsIconStyleModifyed) {
			resetIconStyle();
		}
		if (mIsTabHomeBgModifyed) {
			resetTabHomeBg();
		}
		if (mIsShortCutStyleModifyed) {
			resetShortCutStyle();
		}
		if (mIsFolderStyleModifyed) {
			resetFolderStyle();
		}
		if (mIsGGMenuStyleModifyed) {
			resetGGMenuStyle();
		}
		if (mIsIndicatorStyleModifyed) {
			resetIndicatorStyle();
		}
		if (mIsShortCutBgModifyed) {
			resetShortCutBg();
		}
		if (mIsFuncBgModifyed) {
			resetFuncBg();
		}
		// if (mIsAppNameColorModifyed) {
		// resetAppNameColor();
		// }
	}

	private void resetTabHomeBg() {
//		FunAppSetting mFunAppSetting = mSettingControler.getFunAppSetting();
//		mFunAppSetting.setAppFuncSetting(mPackageName, Setting.TAB_HOME_BG, mPackageName);
		updateFunAppSettingItem(mPackageName, Setting.TAB_HOME_BG, mPackageName);
		if (mPackageName.equals(mCurPackageName)) {
			Intent intent = new Intent(ICustomAction.ACTION_THEME_BROADCAST);
			intent.putExtra(MyThemeReceiver.ACTION_TYPE_STRING,
					IAppDrawerMsgId.APPDRAWER_TAB_HOME_THEME_CHANGE);
			mContext.sendBroadcast(intent);
		}

	}

	private void resetFuncBg() {
//		FunAppSetting mFunAppSetting = mSettingControler.getFunAppSetting();
//		mFunAppSetting.setAppFuncSetting(mPackageName, Setting.BGVISIABLE,
//				String.valueOf(FunAppSetting.BG_DEFAULT));
		updateFunAppSettingItem(mPackageName, Setting.BGVISIABLE,
				String.valueOf(FunAppSetting.BG_DEFAULT));
		if (mPackageName.equals(mCurPackageName)) {
			Intent intent = new Intent(ICustomAction.ACTION_THEME_BROADCAST);
			intent.putExtra(MyThemeReceiver.ACTION_TYPE_STRING, FunAppSetting.INDEX_BGSWITCH);
			mContext.sendBroadcast(intent);

		}
	}

//	private void resetAppNameColor() {
//		DesktopSettingInfo desktopSettingInfo = mSettingControler.getDesktopSettingInfo();
//		if (desktopSettingInfo != null) {
//			desktopSettingInfo.mCustomTitleColor = false;
//			desktopSettingInfo.mTitleColor = 0xffffffff;
//			mSettingControler.updateDesktopSettingInfo(desktopSettingInfo);
//		}
//
//	}

	private void resetIndicatorStyle() {
		updateScreenStyle(mPackageName, Setting.INDICATOR, mPackageName);
//		mScreenStyleConfigInfo.setStyleSetting(mPackageName, Setting.INDICATOR, mPackageName);
//		mSettingControler.getFunAppSetting().setAppFuncSetting(mPackageName,
//				Setting.INDICATOR_STYLE, mPackageName);
		updateFunAppSettingItem(mPackageName, Setting.INDICATOR_STYLE, mPackageName);
		if (mPackageName.equals(mCurPackageName)) {
			Intent intent = new Intent(ICustomAction.ACTION_THEME_BROADCAST);
			intent.putExtra(MyThemeReceiver.ACTION_TYPE_STRING,
					IAppCoreMsgId.REFRESH_SCREENINDICATOR_THEME);
			mContext.sendBroadcast(intent);

		}

	}

	private void resetGGMenuStyle() {
//		mScreenStyleConfigInfo.setStyleSetting(mPackageName, Setting.GGMENUPACKAGE, mPackageName);
		updateScreenStyle(mPackageName, Setting.GGMENUPACKAGE, mPackageName);
		if (mPackageName.equals(mCurPackageName)) {
			Intent intent = new Intent(ICustomAction.ACTION_THEME_BROADCAST);
			intent.putExtra(MyThemeReceiver.ACTION_TYPE_STRING, IAppCoreMsgId.REFRESH_GGMENU_THEME);
			mContext.sendBroadcast(intent);
		}

	}

	private void resetFolderStyle() {
//		mScreenStyleConfigInfo.setStyleSetting(mPackageName, Setting.FOLDERSTYLEPACKAGE,
//				mPackageName);
		updateScreenStyle(mPackageName, Setting.FOLDERSTYLEPACKAGE,
				mPackageName);
		if (mPackageName.equals(mCurPackageName)) {
			Intent intent = new Intent(ICustomAction.ACTION_THEME_BROADCAST);
			intent.putExtra(MyThemeReceiver.ACTION_TYPE_STRING, IAppCoreMsgId.REFRESH_FOLDER_THEME);
			mContext.sendBroadcast(intent);

		}

	}

	private void resetIconStyle() {
//		mScreenStyleConfigInfo
//				.setStyleSetting(mPackageName, Setting.ICONSTYLEPACKAGE, mPackageName);
		updateScreenStyle(mPackageName, Setting.ICONSTYLEPACKAGE, mPackageName);
		if (mPackageName.equals(mCurPackageName)) {
			Intent intent = new Intent(ICustomAction.ACTION_THEME_BROADCAST);
			intent.putExtra(MyThemeReceiver.ACTION_TYPE_STRING, IAppCoreMsgId.REFRESH_SCREENICON_THEME);
			mContext.sendBroadcast(intent);
		}
	}

	
	private void updateScreenStyle(final String packageName, final int settingItem,
			final String value) {
		String columnName = null;
		switch (settingItem) {
			case Setting.THEMEPACKAGE :
				columnName = ScreenStyleConfigTable.THEMEPACKAGE;
				break;
			case Setting.ICONSTYLEPACKAGE :
				columnName = ScreenStyleConfigTable.ICONSTYLEPACKAGE;
				break;
			case Setting.FOLDERSTYLEPACKAGE :
				columnName = ScreenStyleConfigTable.FOLDERSTYLEPACKAGE;
				break;
			case Setting.GGMENUPACKAGE :
				columnName = ScreenStyleConfigTable.GGMENUPACKAGE;
				break;
			case Setting.INDICATOR :
				columnName = ScreenStyleConfigTable.INDICATOR;
				break;
			default :
				break;
		}

		ContentValues values = new ContentValues();
		values.put(columnName, value);
		String selection = ScreenStyleConfigTable.THEMEPACKAGE + " = '" + packageName + "'";
		mContentResolver.update(GoContentProvider.CONTENT_SCREENSTYLE_URI, values, selection, null);
	}
	/**
	 * 重置快捷条背景
	 * */
	private void resetShortCutBg() {
		ContentValues values = new ContentValues();
		values.put(ShortcutSettingTable.BGPICSWITCH, ConvertUtils.boolean2int(true));
		values.put(ShortcutSettingTable.CUSTOMBGPICSWITCH, ConvertUtils.boolean2int(false));
		values.put(ShortcutSettingTable.CUSTOM_PIC_OR_NOT, ConvertUtils.boolean2int(false));
		values.put(ShortcutSettingTable.BG_TARGET_THEME_NAME, mPackageName);
		values.put(ShortcutSettingTable.BG_RESNAME, "dock");
		updateShortCutSetting(mPackageName, values);
		//		mSettingControler.resetShortCutBg(mPackageName, mPackageName, "dock");
		if (mPackageName.equals(mCurPackageName)) {
			Intent intent = new Intent(ICustomAction.ACTION_THEME_BROADCAST);
			intent.putExtra(MyThemeReceiver.ACTION_TYPE_STRING, IDockMsgId.UPDATE_DOCK_BG);
			mContext.sendBroadcast(intent);

		}
	}

	/**
	 * 重置快捷条图标风格
	 * */
	private void resetShortCutStyle() {
		//		mSettingControler.updateShortCutStyleByPackage(mPackageName, mPackageName);
		ContentValues values = new ContentValues();
		values.put(ShortcutSettingTable.STYLE_STRING, mPackageName);
		updateShortCutSetting(mPackageName, values);
		if (mPackageName.equals(mCurPackageName)) {
			Intent intent = new Intent(ICustomAction.ACTION_THEME_BROADCAST);
			intent.putExtra(MyThemeReceiver.ACTION_TYPE_STRING,
					IDockMsgId.DOCK_SETTING_CHANGED_STYLE);
			mContext.sendBroadcast(intent);

		}
	}
	
	private void updateShortCutSetting(String pkgName, ContentValues values) {
		String selection = ShortcutSettingTable.THEMENAME + " = " + "'" + pkgName + "'";
		mContentResolver.update(GoContentProvider.CONTENT_SHORTCUTSETTING_URI, values, selection,
				null);
	}

}
