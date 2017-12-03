package com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.tabs.push.util;

import java.util.Date;
import java.util.Random;

import android.content.Context;

import com.go.util.device.Machine;

/**
 * 
 * @author zouguiquan
 *
 */
public class ScreenEditPushConstants {

	public static String sWallpaperLastRequestTime = "wpLastRequestTime";		        //最近一次的请求时间
	public static final int REQUEST_DATA_SUCCESS = 1;						//请求数据成功
	public static final int REQUEST_DATA_FAILE = -1;						//请求数据失败
	public static final int REQUEST_ID_FOR_WALLPAPER = 602;
	public static final int REQUEST_ID_FOR_WIDGET = 603;
	public static String sNewPushAppPerfix = "newPushAppPerfix_";
	public static String sInstalledAppPerfix = "installedAppPerfix_";
	public static String sCurrentPushAppPerfix = "currentPushAppPerfix_";
	
	public static int[] sPushKeyArray = new int[]{REQUEST_ID_FOR_WALLPAPER, 
												  REQUEST_ID_FOR_WIDGET};
	
	public static String sJsonAppId = "appid";
	public static String sJsonName = "name";
	public static String sJsonIconDownload = "icon";
	public static String sJsonIconRes = "iconRes";
	public static String sJsonDownloadUrl = "downloadurl";
	public static String sJsonSize = "size";
	public static String sJsonUpdateTime = "updatetimedesc";
	public static String sJsonPackage = "packname";
	public static String sJsonInstalled = "installed";
	
	private static String sPushUrl = "http://183.61.112.38:8011/recommendedapp/common.do?";
	private static String sInternalUrl = "http://goappcenter.3g.net.cn/recommendedapp/common.do?";
	private static String sExternalUrl = "http://goappcenter.goforandroid.com/recommendedapp/common.do?";
	
	/**
	 * <br>功能简述:获取URL地址
	 * <br>功能详细描述:funid=x&rd=1234;其中rd是随机数，防止网关cache.
	 * <br>注意:1:服务器请求，2：统计
	 * @return
	 */
	public static String getUrl(Context context, String funid) {
		StringBuffer buffer = null;
		String local = Machine.getCountry(context);
		if (local.equals("cn")) {
			buffer = new StringBuffer(sInternalUrl);
		} else {
			buffer = new StringBuffer(sExternalUrl);
		}		
//		buffer = new StringBuffer(sPushUrl);
		Random random = new Random(new Date().getTime()); //随机数
		buffer.append("funid=" + funid + "&rd=" + random.nextLong());
		random = null;
		return buffer.toString();
	}
	
}
