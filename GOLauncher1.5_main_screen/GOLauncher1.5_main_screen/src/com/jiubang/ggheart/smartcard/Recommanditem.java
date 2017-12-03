package com.jiubang.ggheart.smartcard;

import android.graphics.Bitmap;

/**
 * 
 * @author guoyiqing
 * 
 */
public class Recommanditem {

	public static final int TYPE_APP = 0;
	public static final int TYPE_LIGHTGAME = 1;

	private int mMapId;

	private int mRecommType;

	private String mPackageName;

	private Bitmap mIcon;

	private String mIconUrl;

	private String mAppName;

	private String mVersion;

	private String mAppSize;

	private String mSummary;

	private long mDisplayTime;

	/**
	 * yyyy-mm-dd hh：MM：ss
	 */
	private String mDisplayTimeBegin;

	/**
	 * yyyy-mm-dd hh：MM：ss
	 */
	private String mDisplayTimeEnd;

	/**
	 * 1：ftp下载， 2：电子市场下载， 3：打开浏览器
	 */
	private int mDownloadType;

	/**
	 * 下载地址或打开地址
	 */
	private String mDownloadUrl;

	private int mFolderType;

	private long mShowEndTime;

	private String mCBackUrl;
	private String mLocalIconPath;

	public String getPackageName() {
		return mPackageName;
	}

	public void setPackageName(String mPackageName) {
		this.mPackageName = mPackageName;
	}

	public Bitmap getIcon() {
		return mIcon;
	}

	public void setIcon(Bitmap mIcon) {
		this.mIcon = mIcon;
	}

	public String getAppName() {
		return mAppName;
	}

	public void setAppName(String mAppName) {
		this.mAppName = mAppName;
	}

	public String getVersion() {
		return mVersion;
	}

	public void setVersion(String mVersion) {
		this.mVersion = mVersion;
	}

	public String getAppSize() {
		return mAppSize;
	}

	public void setAppSize(String mAppSize) {
		this.mAppSize = mAppSize;
	}

	public long getDisplayTime() {
		return mDisplayTime;
	}

	public void setDisplayTime(long displayTime) {
		this.mDisplayTime = displayTime;
	}

	public String getDisplayTimeBegin() {
		return mDisplayTimeBegin;
	}

	public void setDisplayTimeBegin(String mDisplayTimeBegin) {
		this.mDisplayTimeBegin = mDisplayTimeBegin;
	}

	public String getDisplayTimeEnd() {
		return mDisplayTimeEnd;
	}

	public void setDisplayTimeEnd(String mDisplayTimeEnd) {
		this.mDisplayTimeEnd = mDisplayTimeEnd;
	}

	public int getDownloadType() {
		return mDownloadType;
	}

	public void setDownloadType(int mDownloadType) {
		this.mDownloadType = mDownloadType;
	}

	public String getDownloadUrl() {
		return mDownloadUrl;
	}

	public void setDownloadUrl(String mDownloadUrl) {
		this.mDownloadUrl = mDownloadUrl;
	}

	public int getFolderType() {
		return mFolderType;
	}

	public void setFolderType(int mFolderType) {
		this.mFolderType = mFolderType;
	}

	public long getShowEndTime() {
		return mShowEndTime;
	}

	public void setShowEndTime(long showEndTime) {
		this.mShowEndTime = showEndTime;
	}

	public String getIconUrl() {
		return mIconUrl;
	}

	public void setIconUrl(String mIconUrl) {
		this.mIconUrl = mIconUrl;
	}

	public void setIconLocalUrl(String localPath) {
		mLocalIconPath = localPath;
	}
	
	public String getIconLocalUrl() {
		return mLocalIconPath;
	}
	
	public int getRecommType() {
		return mRecommType;
	}

	public void setRecommType(int mRecommType) {
		this.mRecommType = mRecommType;
	}

	public void setCBackUrl(String cback) {
		mCBackUrl = cback;
	}

	public String getCBackUrl(String cback) {
		return mCBackUrl;
	}

	public int getMapId() {
		return mMapId;
	}

	public void setMapId(int mMapId) {
		this.mMapId = mMapId;
	}

	public String getSummary() {
		return mSummary;
	}

	public void setSummary(String mSummary) {
		this.mSummary = mSummary;
	}

	@Override
	public String toString() {
		return "mMapId:" + mMapId + " mCBackUrl:" + mCBackUrl
				+ " mShowStartTime:" + mShowEndTime + " mFolderType:"
				+ mFolderType + " mDownloadUrl:" + mDownloadUrl
				+ " mDownloadType:" + mDownloadType + " mDisplayTimeEnd:"
				+ mDisplayTimeEnd + " mDisplayTimeBegin:" + mDisplayTimeBegin
				+ " mDisplayTime:" + " mSummary:" + mSummary + " mAppSize:"
				+ mAppSize + " mVersion:" + mVersion + " mAppName:" + mAppName
				+ " mIconUrl:" + mIconUrl + " mIcon:" + mIcon
				+ " mPackageName:" + mPackageName + " mRecommType:"
				+ mRecommType + "\n";
	}

}
