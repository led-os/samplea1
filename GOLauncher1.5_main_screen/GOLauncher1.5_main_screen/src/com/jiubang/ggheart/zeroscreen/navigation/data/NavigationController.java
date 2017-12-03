package com.jiubang.ggheart.zeroscreen.navigation.data;

import java.util.ArrayList;

import android.content.Context;

import com.jiubang.core.framework.ICleanable;
import com.jiubang.ggheart.data.Controler;
import com.jiubang.ggheart.zeroscreen.navigation.bean.ZeroScreenAdInfo;
import com.jiubang.ggheart.zeroscreen.navigation.bean.ZeroScreenAdSuggestSiteInfo;

/**
 * 
 * @author zhujian
 * 
 */
public class NavigationController extends Controler implements ICleanable {

	private static NavigationController sInstance = null;
	private Context mContext = null;
//	private ZeroScreenAdDataModel mZeroScreenAdDataModel;

	private NavigationController(Context context) {
		super(context);
		mContext = context;
	//	mZeroScreenAdDataModel = new ZeroScreenAdDataModel(mContext);
	}

	public static NavigationController getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new NavigationController(context);
		}
		return sInstance;
	}
//
//	/**
//	 * 前台显示
//	 * 
//	 * @return
//	 */
	public ArrayList<ZeroScreenAdInfo> createZeroScreenAdInfo() {
		return null;
//		return mZeroScreenAdDataModel.getZeroScreenAdInfoList();
	}
//
	public void queryZeroScreenAdInfo(ZeroScreenAdInfo info) {
//		mZeroScreenAdDataModel.updateZeroScreenAdInfo(info);
	}
//
	public void updateZeroScreenAdInfo(ZeroScreenAdInfo info) {
//		mZeroScreenAdDataModel.updateZeroScreenAdInfo(info);
	}
//
	public void insertZeroScreenAdInfo(ZeroScreenAdInfo info) {
//		mZeroScreenAdDataModel.insertZeroScreenAdInfo(info);
	}
//
	public void deleteZeroScreenAdInfo(ZeroScreenAdInfo info) {
//		mZeroScreenAdDataModel.deleteZeroScreenAdInfo(info);
	}
//
//	/**
//	 * 推荐列表
//	 * 
//	 * @return
//	 */
	public ArrayList<ZeroScreenAdSuggestSiteInfo> createZeroScreenAdSuggestSiteInfo() {
		return null;
//		return mZeroScreenAdDataModel.getZeroScreenAdSuggestSiteInfo();
	}
//
	public void insertZeroScreenAdSuggestSiteInfo(
			ArrayList<ZeroScreenAdSuggestSiteInfo> list) {
//		mZeroScreenAdDataModel.insertZeroScreenAdSuggestSiteInfo(list);
	}
//
	public void insertZeroScreenAdSuggestSiteFrontPic(byte[] data, String url) {
//		mZeroScreenAdDataModel.insertZeroScreenAdSuggestSiteFrontPic(data, url);
	}
//
	public void insertZeroScreenAdSuggestSiteLogoIcon(byte[] data, String url) {
//		mZeroScreenAdDataModel.insertZeroScreenAdSuggestSiteLogoIcon(data, url);
	}
//
	public byte[] queryZeroScreenAdSuggestSiteInfoFrontPic(String url) {
		return null;
//		return mZeroScreenAdDataModel
//				.queryZeroScreenAdSuggestSiteInfoFrontPic(url);
	}
//
	public byte[] queryZeroScreenAdSuggestSiteInfoLogoIcon(String url) {
		return null;
//		return mZeroScreenAdDataModel
//				.queryZeroScreenAdSuggestSiteInfoLogoIcon(url);
	}
//
	public String queryZeroScreenAdSuggestSiteInfoFrontPicUrl(String url) {
		return url;
//		return mZeroScreenAdDataModel
//				.queryZeroScreenAdSuggestSiteInfoFrontPicUrl(url);
	}
//
	public String queryZeroScreenAdSuggestSiteInfoLogoIconUrl(String url) {
		return url;
//		return mZeroScreenAdDataModel
//				.queryZeroScreenAdSuggestSiteInfoLogoIconUrl(url);
	}
//
	public int queryZeroScreenAdSuggestSiteInfoLogoColor(String url) {
		return 0;
//		return mZeroScreenAdDataModel
//				.queryZeroScreenAdSuggestSiteInfoLogoColor(url);
	}
//
	public void cleanZeroScreenAdSuggestSiteTable() {
//		mZeroScreenAdDataModel.cleanZeroScreenAdSuggestSiteInfo();
	}
//
	public String getZeroScreenAdSuggestSiteTableVersion() {
		return null;
//		return mZeroScreenAdDataModel.getZeroScreenAdSuggestSiteVersion();
	}
//
//	@Override
//	public void cleanup() {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}
}
