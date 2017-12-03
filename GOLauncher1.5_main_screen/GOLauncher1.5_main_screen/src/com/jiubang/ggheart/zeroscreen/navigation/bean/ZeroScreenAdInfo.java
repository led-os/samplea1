package com.jiubang.ggheart.zeroscreen.navigation.bean;

import android.content.ContentValues;

import com.go.util.ConvertUtils;
import com.jiubang.ggheart.data.tables.ZeroScreenAdTable;

/**
 * 
 * @author zhujian
 * 
 */
public class ZeroScreenAdInfo {

	public int mId;
	/**
	 * 快速拨号的名称
	 */
	public String mTitle;

	/**
	 * 快速拨号的网址
	 */
	public String mUrl;

	/**
	 * 快速拨号的域名
	 */
	public String mDomain;

	/**
	 * 徐老师设计的大图的背景色
	 */
	public int mDesignedColor;

	/**
	 * 快速拨号的高清图标(用于合成大图标)
	 */
	public byte[] mLogoIcon;

	/**
	 * 根据高清图标计算得到的color
	 */
	public int mCustomColor;

	/**
	 * 新建时默认给它的随机数
	 */
	public String mRandom;

	/**
	 * 记录每个广告的位置
	 */
	public int mPosition;

	/**
	 * 是否显示+号
	 */
	public boolean mIsPlus;

	/**
	 * 是否是推荐广告
	 */
	public boolean mIsRecommend;

	/**
	 * 是否显示xx
	 */
	public boolean mIsShowDel = false;

	public ZeroScreenAdInfo() {
		mCustomColor = -1;
		mDesignedColor = -1;
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
		values.put(ZeroScreenAdTable.TITLE, mTitle);
		values.put(ZeroScreenAdTable.URL, mUrl);
		values.put(ZeroScreenAdTable.DOMAIN, mDomain);
		values.put(ZeroScreenAdTable.DESIGNED_COLOR, mDesignedColor);
		values.put(ZeroScreenAdTable.ICON, mLogoIcon);
		values.put(ZeroScreenAdTable.CUSTOM_COLOR, mCustomColor);
		values.put(ZeroScreenAdTable.RANDOM, mRandom);
		values.put(ZeroScreenAdTable.ISPLUS, ConvertUtils.boolean2int(mIsPlus));
		values.put(ZeroScreenAdTable.ISRECOMMEND,
				ConvertUtils.boolean2int(mIsRecommend));
		values.put(ZeroScreenAdTable.POSITION, mPosition);

	}

	public void setInfoNull(int position) {
		mTitle = null;

		mUrl = null;

		mDomain = null;

		mDesignedColor = -1;

		mLogoIcon = null;

		mCustomColor = -1;

		mPosition = position;

		mIsPlus = true;

		mIsRecommend = false;

		mIsShowDel = false;
	}
}
