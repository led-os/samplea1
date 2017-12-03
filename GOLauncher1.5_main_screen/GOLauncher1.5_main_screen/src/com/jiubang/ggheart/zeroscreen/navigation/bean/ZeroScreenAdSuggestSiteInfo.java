package com.jiubang.ggheart.zeroscreen.navigation.bean;

import android.content.ContentValues;
import android.graphics.drawable.Drawable;

import com.jiubang.ggheart.data.tables.ZeroScreenAdSuggestSiteTable;

/**
 * 
 * @author zhujian
 * 
 */
public class ZeroScreenAdSuggestSiteInfo {

	public int mId;
	/**
	 * 国家
	 */
	public String mCountry;

	/**
	 * 推荐数据的版本号
	 */
	public String mVersion;

	/**
	 * 网站标题（如“新浪”）
	 */
	public String mTitle;
	/**
	 * 网站网址 如 www.sina.com.cn
	 */
	public String mUrl;

	/**
	 * 网站的顶级域名 如sina
	 */
	public String mDomain;

	/**
	 * 存放图标的服务器地址
	 */
	public String mIconHost;

	/**
	 * 网站Logo图标的网址
	 */
	public String mLogoUrl;

	/**
	 * 网站Logo图标
	 */
	public byte[] mLogoIcon;

	/**
	 * 网站快速标签图标的背景图的颜色
	 */
	public int mBackColor;

	/**
	 * 网站快速标签图标的前景图的网址
	 */
	public String mFrontUrl;

	/**
	 * 网站快速标签图标的前景图
	 */
	public byte[] mFontIcon;

	/**
	 * 默认数据的图片
	 */
	public Drawable mDrawable;

	public ZeroScreenAdSuggestSiteInfo() {

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
		values.put(ZeroScreenAdSuggestSiteTable.COLUMN_COUNTRY, mCountry);
		values.put(ZeroScreenAdSuggestSiteTable.COLUMN_VERSION, mVersion);
		values.put(ZeroScreenAdSuggestSiteTable.COLUMN_TITLE, mTitle);
		values.put(ZeroScreenAdSuggestSiteTable.COLUMN_DOMAIN, mDomain);
		values.put(ZeroScreenAdSuggestSiteTable.COLUMN_URL, mUrl);
		values.put(ZeroScreenAdSuggestSiteTable.COLUMN_ICON_HOST, mIconHost);
		values.put(ZeroScreenAdSuggestSiteTable.COLUMN_LOGO_URL, mLogoUrl);
		values.put(ZeroScreenAdSuggestSiteTable.COLUMN_LOGO_ICON, mLogoIcon);
		values.put(ZeroScreenAdSuggestSiteTable.COLUMN_BACK_COLOR, mBackColor);
		values.put(ZeroScreenAdSuggestSiteTable.COLUMN_FRONT_URL, mFrontUrl);
		values.put(ZeroScreenAdSuggestSiteTable.COLUMN_FRONT_ICON, mFontIcon);

	}
}
