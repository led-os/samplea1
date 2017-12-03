package com.jiubang.ggheart.data.info;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.go.util.ConvertUtils;
import com.jiubang.ggheart.data.tables.DeskMenuTable;

public class DeskMenuSettingInfo implements Parcelable {
	public boolean mEnable;

	public DeskMenuSettingInfo() {
		mEnable = true;
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
		values.put(DeskMenuTable.ENABLE, ConvertUtils.boolean2int(mEnable));
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
			int enableIndex = cursor.getColumnIndex(DeskMenuTable.ENABLE);

			if (-1 == enableIndex) {
				return false;
			}

			mEnable = ConvertUtils.int2boolean(cursor.getInt(enableIndex));
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
		dest.writeInt(mEnable ? 1 : 0);
		
	}
	
	public static final Parcelable.Creator<DeskMenuSettingInfo> CREATOR = new Parcelable.Creator<DeskMenuSettingInfo>() {
		@Override
		public DeskMenuSettingInfo createFromParcel(Parcel source) {
			// 从Parcel中读取数据，返回DownloadTask对象
			return new DeskMenuSettingInfo(source);
		}

		@Override
		public DeskMenuSettingInfo[] newArray(int size) {
			return new DeskMenuSettingInfo[size];
		}
	};

	public DeskMenuSettingInfo(Parcel in) {
		mEnable = ConvertUtils.int2boolean(in.readInt());
	}
}
