package com.jiubang.ggheart.data.info;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.go.util.ConvertUtils;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.ScreenIndicator;
import com.jiubang.ggheart.data.statistics.StaticScreenSettingInfo;
import com.jiubang.ggheart.data.tables.ScreenSettingTable;

/**
 * 桌面设置
 *
 */
public class ScreenSettingInfo implements Parcelable {
	public static final int DEFAULT_SCREEN_COUNT = 5;
	public static final int DEFAULT_MAIN_SCREEN = 2;

	public boolean mEnableIndicator;
	public boolean mAutoHideIndicator;
	public boolean mWallpaperScroll; // 切换屏幕时壁纸滚动
	public boolean mScreenLooping; // 屏幕循环切换
	public boolean mLockScreen; // 屏幕锁定
	public int mScreenCount;
	public int mMainScreen;
	public String mIndicatorShowmode; // 指示器显示模式
	public String mIndicatorPosition; // 桌面指示器位置
	public String mAppDrawerIndicatorPosition; // 功能表指示器位置

	public ScreenSettingInfo() {
		mEnableIndicator = true;
		mAutoHideIndicator = false;
		mScreenCount = DEFAULT_SCREEN_COUNT;
		//add by dengdazhong
		//resolve ADT-7595 低配置手机，新安装后恢复默认桌面时，桌面中的指示器会显示为5，等待一段时间才变成3
		//如果低配需要减屏，主页前面会删掉一页，所以主页Index要相应的改变
		mMainScreen = StaticScreenSettingInfo.sNeedDelScreen ? 1 : DEFAULT_MAIN_SCREEN;
		mWallpaperScroll = true;
		mScreenLooping = false;
		mLockScreen = false; // 默认不锁定
		mIndicatorShowmode = ScreenIndicator.SHOWMODE_NORMAL; // 默认正常模式
		mIndicatorPosition = ScreenIndicator.INDICRATOR_ON_BOTTOM;
		mAppDrawerIndicatorPosition = ScreenIndicator.INDICRATOR_ON_TOP;
	}

	/**
	 * 加入键值对
	 * 
	 * @param values
	 *            键值对
	 */
	public void contentValues(ContentValues values) {
		if (null == values) {
			return;
		}
		values.put(ScreenSettingTable.ENABLE, ConvertUtils.boolean2int(mEnableIndicator));
		values.put(ScreenSettingTable.ISAUTOHIDE, ConvertUtils.boolean2int(mAutoHideIndicator));
		values.put(ScreenSettingTable.COUNT, mScreenCount);
		values.put(ScreenSettingTable.MAINSCREEN, mMainScreen);
		values.put(ScreenSettingTable.WALLPAPER_SCROLL, ConvertUtils.boolean2int(mWallpaperScroll));
		values.put(ScreenSettingTable.SCREEN_LOOPING, ConvertUtils.boolean2int(mScreenLooping));
		values.put(ScreenSettingTable.LOCK_SCREEN, ConvertUtils.boolean2int(mLockScreen));
		values.put(ScreenSettingTable.INDICATORSTYLEPACKAGE, mIndicatorShowmode);
		values.put(ScreenSettingTable.INDICATORPOSITION, mIndicatorPosition);
		values.put(ScreenSettingTable.APPDRAWERINDICATORPOSITION, mAppDrawerIndicatorPosition);
	}

	/**
	 * 解析数据
	 * 
	 * @param cursor
	 *            数据集
	 */
	public boolean parseFromCursor(Cursor cursor) {
		if (null == cursor) {
			return false;
		}

		boolean bData = cursor.moveToFirst();
		if (bData) {
			int enableIndex = cursor.getColumnIndex(ScreenSettingTable.ENABLE);
			int isautohideIndex = cursor.getColumnIndex(ScreenSettingTable.ISAUTOHIDE);
			int countIndex = cursor.getColumnIndex(ScreenSettingTable.COUNT);
			int mainscreenIndex = cursor.getColumnIndex(ScreenSettingTable.MAINSCREEN);
			int bgScrollIndex = cursor.getColumnIndex(ScreenSettingTable.WALLPAPER_SCROLL);
			int screenLoopingIndex = cursor.getColumnIndex(ScreenSettingTable.SCREEN_LOOPING);
			int lockScreenIndex = cursor.getColumnIndex(ScreenSettingTable.LOCK_SCREEN);
			// int indicatormodeIndex =
			// cursor.getColumnIndex(ScreenSettingTable.INDICATOR_SHOWMODE);
			int indicatormodeIndex = cursor
					.getColumnIndex(ScreenSettingTable.INDICATORSTYLEPACKAGE);
			int indicatorpositionIndex = cursor
					.getColumnIndex(ScreenSettingTable.INDICATORPOSITION);
			int appdrawerIndicatorpositionIndex = cursor
					.getColumnIndex(ScreenSettingTable.APPDRAWERINDICATORPOSITION);
			if (enableIndex >= 0) {
				mEnableIndicator = ConvertUtils.int2boolean(cursor.getInt(enableIndex));
			}

			if (isautohideIndex >= 0) {
				mAutoHideIndicator = ConvertUtils.int2boolean(cursor.getInt(isautohideIndex));
			}

			if (countIndex >= 0) {
				mScreenCount = cursor.getInt(countIndex);
			}

			if (mainscreenIndex >= 0) {
				mMainScreen = cursor.getInt(mainscreenIndex);
			}

			if (bgScrollIndex >= 0) {
				mWallpaperScroll = ConvertUtils.int2boolean(cursor.getInt(bgScrollIndex));
			}

			if (screenLoopingIndex >= 0) {
				mScreenLooping = ConvertUtils.int2boolean(cursor.getInt(screenLoopingIndex));
			}

			if (lockScreenIndex >= 0) {
				mLockScreen = ConvertUtils.int2boolean(cursor.getInt(lockScreenIndex));
			}

			if (indicatormodeIndex >= 0) {
				mIndicatorShowmode = cursor.getString(indicatormodeIndex);
			}

			if (indicatorpositionIndex >= 0) {
				mIndicatorPosition = cursor.getString(indicatorpositionIndex);
			}
			
			if (appdrawerIndicatorpositionIndex >= 0) {
				mAppDrawerIndicatorPosition = cursor.getString(appdrawerIndicatorpositionIndex);
			}
		}
		return bData;
	}
	
	public static final Parcelable.Creator<ScreenSettingInfo> CREATOR = new Parcelable.Creator<ScreenSettingInfo>() {
		@Override
		public ScreenSettingInfo createFromParcel(Parcel source) {
			// 从Parcel中读取数据，返回DownloadTask对象
			return new ScreenSettingInfo(source);
		}

		@Override
		public ScreenSettingInfo[] newArray(int size) {
			return new ScreenSettingInfo[size];
		}
	};

	public ScreenSettingInfo(Parcel in) {
		mEnableIndicator = ConvertUtils.int2boolean(in.readInt());
		mAutoHideIndicator = ConvertUtils.int2boolean(in.readInt());
		mScreenCount = in.readInt();
		mMainScreen = in.readInt();
		mWallpaperScroll = ConvertUtils.int2boolean(in.readInt());
		mScreenLooping = ConvertUtils.int2boolean(in.readInt());
		mLockScreen = ConvertUtils.int2boolean(in.readInt());
		mIndicatorShowmode = in.readString();
		mIndicatorPosition = in.readString();
		mAppDrawerIndicatorPosition = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mEnableIndicator ? 1 : 0);
		dest.writeInt(mAutoHideIndicator ? 1 : 0);
		dest.writeInt(mScreenCount);
		dest.writeInt(mMainScreen);
		dest.writeInt(mWallpaperScroll ? 1 : 0);
		dest.writeInt(mScreenLooping ? 1 : 0);
		dest.writeInt(mLockScreen ? 1 : 0);
		dest.writeString(mIndicatorShowmode);
		dest.writeString(mIndicatorPosition);
		dest.writeString(mAppDrawerIndicatorPosition);
	}
}
