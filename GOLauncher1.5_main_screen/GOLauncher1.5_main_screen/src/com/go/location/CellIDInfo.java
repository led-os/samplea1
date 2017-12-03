package com.go.location;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * 类描述: 基站信息
 * 
 * @author  liuwenqin
 * @date  [2012-9-4]
 */
public class CellIDInfo implements Parcelable {

	public int mCellId;
	public int mMobileCountryCode;
	public int mMobileNetworkCode;
	/** 每个基站信息都必须包含自身的locationAreaCode，作为获取当前基站经纬度的请求链接的附带参数 */
	public int mLocationAreaCode;
	public String mRadioType;
	public String mSimOperatorName;
	/**
	 * 默认中等强度信号
	 */
	public int mRssi = -50;

	public CellIDInfo() {
	}

	public void setCellId(int cellId) {
		this.mCellId = cellId;
	}

	public void setMobileCountryCode(int code) {
		this.mMobileCountryCode = code;
	}

	public void setMobileNetWorkCode(int code) {
		this.mMobileNetworkCode = code;
	}

	public void setLocationAreaCode(int areaCode) {
		this.mLocationAreaCode = areaCode;
	}

	public void setRaidoType(String type) {
		this.mRadioType = type;
	}

	public int getCellId() {
		return mCellId;
	}

	public int getCountryCode() {
		return mMobileCountryCode;
	}

	public int getNetworkCode() {
		return mMobileNetworkCode;
	}

	public int getAreaCode() {
		return mLocationAreaCode;
	}

	public String getRadioType() {
		return mRadioType;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(mCellId);
		out.writeInt(mMobileCountryCode);
		out.writeInt(mMobileNetworkCode);
		out.writeInt(mLocationAreaCode);
		out.writeString(mRadioType);
	}

	public static final Parcelable.Creator<CellIDInfo> CREATOR = new Parcelable.Creator<CellIDInfo>() {
		public CellIDInfo createFromParcel(Parcel in) {
			return new CellIDInfo(in);
		}

		public CellIDInfo[] newArray(int size) {
			return new CellIDInfo[size];
		}
	};

	private CellIDInfo(Parcel in) {
		mCellId = in.readInt();
		mMobileCountryCode = in.readInt();
		mMobileNetworkCode = in.readInt();
		mLocationAreaCode = in.readInt();
		mRadioType = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
}