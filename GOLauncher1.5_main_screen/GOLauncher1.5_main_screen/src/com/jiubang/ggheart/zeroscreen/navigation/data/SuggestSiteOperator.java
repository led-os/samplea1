package com.jiubang.ggheart.zeroscreen.navigation.data;

import android.content.Context;

/**
 * 
 * <br>类描述: 推荐网址数据操作表
 * <br>功能详细描述:
 * 
 * @author  linhang
 * @date  [2013-7-30]
 */
public class SuggestSiteOperator {

	private static SuggestSiteOperator sSuggestSiteOperator;

	private Context mContext;

	private SuggestSiteOperator(Context context) {

		mContext = context;

	}

	public static synchronized SuggestSiteOperator getInstance(Context context) {

		if (sSuggestSiteOperator == null) {
			sSuggestSiteOperator = new SuggestSiteOperator(context);
		}

		return sSuggestSiteOperator;

	}

	/**
	 * <br>功能简述: 根据热门网址从数据库中获取图标（前景图或者网站图标）的字节数组，用于生成Bitmap
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param url 热门网址的
	 * @param type 类型
	 * @return
	 */
	public byte[] getIconByteByUrl(String url, int type) {

//		if (type == SuggestImageCache.TYPE_LOGO) {
//			return NavigationController.getInstance(mContext)
//					.queryZeroScreenAdSuggestSiteInfoLogoIcon(url);
//		} else if (type == SuggestImageCache.TYPE_FRONT) {
//			url = url.replace(ToolUtil.HTTPHEAD, "");
//			return NavigationController.getInstance(mContext)
//					.queryZeroScreenAdSuggestSiteInfoFrontPic(url);
//		}

		return null;
	}

}
