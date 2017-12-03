package com.jiubang.ggheart.data.info;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.go.proxy.ApplicationProxy;
import com.go.util.ConvertUtils;
import com.go.util.device.Machine;
import com.jiubang.ggheart.data.tables.GravityTable;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 */
public class GravitySettingInfo implements Parcelable {
	public boolean mEnable;
	public boolean mLandscape;
	public int mOrientationType;

	public GravitySettingInfo(Context context) {
		// mEnable = true;
		// mLandscape=false;
		if (Machine.isTablet(ApplicationProxy.getContext())) {
			mOrientationType = 0;
		} else {
			mOrientationType = 1;
		}
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
		// values.put(GravityTable.ENABLE, ConvertUtils.boolean2int(mEnable));
		// values.put(GravityTable.LANDSCAPE,
		// ConvertUtils.boolean2int(mLandscape));
		values.put(GravityTable.ORIENTATIONTYPE, Integer.valueOf(mOrientationType));

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
			// int enableIndex = cursor.getColumnIndex(GravityTable.ENABLE);
			//
			// if (-1 == enableIndex)
			// {
			// return false;
			// }
			//
			// mEnable = ConvertUtils.int2boolean(cursor.getInt(enableIndex));
			// int landscapeIndex =
			// cursor.getColumnIndex(GravityTable.LANDSCAPE);
			//
			// if (-1 == landscapeIndex)
			// {
			// return false;
			// }
			int orientationIndex = cursor.getColumnIndex(GravityTable.ORIENTATIONTYPE);

			if (-1 == orientationIndex) {
				return false;
			}

			mOrientationType = cursor.getInt(orientationIndex);
		}
		return bData;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public static final Parcelable.Creator<GravitySettingInfo> CREATOR = new Parcelable.Creator<GravitySettingInfo>() {
		@Override
		public GravitySettingInfo createFromParcel(Parcel source) {
			// 从Parcel中读取数据，返回DownloadTask对象
			return new GravitySettingInfo(source);
		}

		@Override
		public GravitySettingInfo[] newArray(int size) {
			return new GravitySettingInfo[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mEnable ? 1 : 0);
		dest.writeInt(mLandscape ? 1 : 0);
		dest.writeInt(mOrientationType);		
	}
	
	public GravitySettingInfo(Parcel in) {
		mEnable = ConvertUtils.int2boolean(in.readInt());
		mLandscape = ConvertUtils.int2boolean(in.readInt());
		mOrientationType = in.readInt();
	}
}
