package com.jiubang.ggheart.data.info;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.go.util.ConvertUtils;
import com.jiubang.ggheart.data.tables.DeskLockable;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author 
 * @date  [2012-10-18]
 */
public class DeskLockSettingInfo implements Parcelable {

	/**
	 *  应用锁
	 */
	public boolean mAppLock;
	/**
	 * 锁定隐藏程序
	 */
	public boolean mLockHideApp;
	/**
	 * 锁定恢复备份
	 */
	public boolean mRestoreSetting;
	/**
	 * 锁定恢复默认，由于恢复备份已经整合到一个项，所以这个参数没用了，暂时不删，以防产品又改来改去
	 */
	public boolean mRestoreDefault;

	// private boolean mTablet = false;

	public DeskLockSettingInfo() {
		mAppLock = false;
		mLockHideApp = false;
		mRestoreSetting = false;
		mRestoreDefault = false;
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
		values.put(DeskLockable.APPLOCK, ConvertUtils.boolean2int(mAppLock));
		values.put(DeskLockable.LOCKHIDEAPP, ConvertUtils.boolean2int(mLockHideApp));
		values.put(DeskLockable.RESTORESETTING, ConvertUtils.boolean2int(mRestoreSetting));
		values.put(DeskLockable.RESTOREDEFAULT, ConvertUtils.boolean2int(mRestoreDefault));

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

			int appLock = cursor.getColumnIndex(DeskLockable.APPLOCK);

			int lockHideApp = cursor.getColumnIndex(DeskLockable.LOCKHIDEAPP);

			int restoreSetting = cursor.getColumnIndex(DeskLockable.RESTORESETTING);

			int restoreDefault = cursor.getColumnIndex(DeskLockable.RESTOREDEFAULT);

			mAppLock = ConvertUtils.int2boolean(cursor.getInt(appLock));
			mLockHideApp = ConvertUtils.int2boolean(cursor.getInt(lockHideApp));
			mRestoreSetting = ConvertUtils.int2boolean(cursor.getInt(restoreSetting));
			mRestoreDefault = ConvertUtils.int2boolean(cursor.getInt(restoreDefault));

		}
		return bData;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public static final Parcelable.Creator<DeskLockSettingInfo> CREATOR = new Parcelable.Creator<DeskLockSettingInfo>() {
		@Override
		public DeskLockSettingInfo createFromParcel(Parcel source) {
			// 从Parcel中读取数据，返回DownloadTask对象
			return new DeskLockSettingInfo(source);
		}

		@Override
		public DeskLockSettingInfo[] newArray(int size) {
			return new DeskLockSettingInfo[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mAppLock ? 1 : 0);
		dest.writeInt(mLockHideApp ? 1 : 0);
		dest.writeInt(mRestoreSetting ? 1 : 0);
		dest.writeInt(mRestoreDefault ? 1 : 0);
	}

	public DeskLockSettingInfo(Parcel in) {
		mAppLock = ConvertUtils.int2boolean(in.readInt());
		mLockHideApp = ConvertUtils.int2boolean(in.readInt());
		mRestoreSetting = ConvertUtils.int2boolean(in.readInt());
		mRestoreDefault = ConvertUtils.int2boolean(in.readInt());
	}
}
