package com.jiubang.ggheart.apps.desks.diy.themescan.coupon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 消费卷
 * 
 * @author rongjinsong
 * 
 */
public class BaseBean {

	public static final int TYPE_COUPON = 1;
	public static final int TYPE_PROMOTION = 2;

	public static final String TAG_CODE = "code";
	public static final String TAG_TYPE = "type";
	public static final String TAG_USED = "used";
	public static final String TAG_STIME = "stime";
	public static final String TAG_ETIME = "etime";

	private String mCode;
	private int mType;
	private boolean mUsed;
	private String mETime;
	private String mSTime;

	public BaseBean(String code, int type, String stime, String etime) {
		mCode = code;
		mType = type;
		mSTime = stime;
		mETime = etime;
	}

	public String getCode() {
		return mCode;
	}

	public int getType() {
		return mType;
	}

	public boolean isVaild() {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		String today = dateFormat.format(now);
		return today.compareToIgnoreCase(mETime) <= 0;
	}

	public boolean isAfterStime() {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		String today = dateFormat.format(now);
		return today.compareToIgnoreCase(mSTime) >= 0;
	}

	public void setUsed(boolean bool) {
		mUsed = bool;
	}

	public boolean getUsed() {
		return mUsed;
	}

	public String getETime() {
		return mETime;
	}

	public String getSTime() {
		return mSTime;
	}

	public void setETime(String etime) {
		mETime = etime;
	}

	public void setSTime(String stime) {
		mSTime = stime;
	}
}
