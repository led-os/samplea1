package com.jiubang.ggheart.data.info;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.jiubang.ggheart.data.tables.GestureTable;


/**
 *
 */
public class GestureSettingInfo implements Parcelable {
	
	public static final int MAX_GESTURE_COUNT = 8;
	/**
	 * Home手势ID。。//ID 记录设置位置了，don't modify
	 */
	public static final int GESTURE_HOME_ID = 1;
	/**
	 * Up手势ID   //ID 记录设置位置了，don't modify
	 */
	public static final int GESTURE_UP_ID = 2;
	/**
	 * Down手势ID     //ID 记录设置位置了，don't modify
	 */
	public static final int GESTURE_DOWN_ID = 3;
	/**
	 * 双击空白处手势   //ID 记录设置位置了，don't modify
	 */
	public static final int GESTURE_DOUBLLE_CLICK_ID = 4;
	/**
	 * 双击空白处手势   //ID 记录设置位置了，don't modify
	 */
	public static final int GESTURE_PINCHOUT_ID = 5;
	/**
	 * 双击空白处手势   //ID 记录设置位置了，don't modify
	 */
	public static final int GESTURE_SWIPEUP_ID = 6;
	/**
	 * 逆时针手势   //ID 记录设置位置了，don't modify
	 */
	public static final int GESTURE_ROTATECCW_ID = 7;
	/**
	 * 顺时处手势   //ID 记录设置位置了，don't modify
	 */
	public static final int GESTURE_ROTATECW_ID = 8;

	public int mGestureId;
	public int mGestureAction;
	public int mGoShortCut = -1;
	public String mGestrueName;
	public String mAction;

	public GestureSettingInfo() {

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
		values.put(GestureTable.GESTUREID, mGestureId);
		values.put(GestureTable.GESTURENAME, mGestrueName);
		values.put(GestureTable.GESTURACTION, mGestureAction);
		values.put(GestureTable.ACTION, mAction);
		values.put(GestureTable.GOSHORTCUTITEM, mGoShortCut);
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
			int idIndex = cursor.getColumnIndex(GestureTable.GESTUREID);
			int nameIndex = cursor.getColumnIndex(GestureTable.GESTURENAME);
			int gestureactionIndex = cursor.getColumnIndex(GestureTable.GESTURACTION);
			int actionIndex = cursor.getColumnIndex(GestureTable.ACTION);
			int goshortcutIndex = cursor.getColumnIndex(GestureTable.GOSHORTCUTITEM);
			if (-1 == idIndex || -1 == nameIndex || -1 == gestureactionIndex || -1 == actionIndex
					|| -1 == goshortcutIndex) {
				return false;
			}

			mGestureId = cursor.getInt(idIndex);
			mGestrueName = cursor.getString(nameIndex);
			mGestureAction = cursor.getInt(gestureactionIndex);
			mAction = cursor.getString(actionIndex);
			mGoShortCut = cursor.getInt(goshortcutIndex);
		}
		return bData;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mGestureId);
		dest.writeString(mGestrueName);
		dest.writeInt(mGestureAction);
		dest.writeString(mAction);
		dest.writeInt(mGoShortCut);		
	}
	
	
	public static final Parcelable.Creator<GestureSettingInfo> CREATOR = new Parcelable.Creator<GestureSettingInfo>() {
		@Override
		public GestureSettingInfo createFromParcel(Parcel source) {
			// 从Parcel中读取数据，返回DownloadTask对象
			return new GestureSettingInfo(source);
		}

		@Override
		public GestureSettingInfo[] newArray(int size) {
			return new GestureSettingInfo[size];
		}
	};

	public GestureSettingInfo(Parcel in) {
		mGestureId = in.readInt();
		mGestrueName = in.readString();
		mGestureAction = in.readInt();
		mAction = in.readString();
		mGoShortCut = in.readInt();		
	}
}
