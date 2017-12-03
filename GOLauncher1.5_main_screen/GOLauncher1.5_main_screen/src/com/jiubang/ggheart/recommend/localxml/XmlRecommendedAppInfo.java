package com.jiubang.ggheart.recommend.localxml;



/**
 * 
 * <br>
 * 类描述:本地推荐信息 <br>
 * 功能详细描述:
 * 
 * @author rongjinsong
 * @date [2012-12-27]
 */
public class XmlRecommendedAppInfo {

	public static final int GOTO_MARKET = 0;
	public static final int GOTO_FTP = GOTO_MARKET + 1;
	public static final int GOTO_BROWSER = 2;
	public int mTitle; // 广告标题
	public int mDetail; // 广告详细描述
	public int mIconId; // 广告图标
	public String mPackagename; // 推荐应用包名
	public String mAction;
	public String mDownloadUrl;
	public int mGroup; // 对应屏幕上的四个图标分组，从1开始
	public int mPriority; // 推荐优先级，0为第一个
	public int mAppId; // 跳转到gostore里的appid，如果无需跳转可不配置
	public String mChannelId; // 推荐渠道ID号，可不填，如果填了推荐只会在相应的渠道号内出现
	public int mRowIndex; // 行数，负数的表示倒数第几行
	public String mSTime; // 推荐开始时间
	public String mETime; // 结束时间
	public int mScreenIndex;
	public int mActType = -1; // 跳转类型，0是电子市场，1是ftp，如果200未填跳电子市场，其它ftp下载
	public boolean mShowInstallIcon = false; // 如果应用已安装是否在相应位置摆放已安装应用的图标

	// 实时统计参数
	public String mClickurl; // 点击url
	public String mId; // 图标唯一id
	public String mMapId; // mapid
	
	public String mRecommendArea;	// 需要推荐的国家
	public String mUnRecommendArea;	// 不需要推荐的国家
	
	/**
	 * 判断当前uid是否存在于推荐的uid数组里
	 * @param uid
	 * @return
	 */
	public boolean isContainUid(String uid) {
		if (uid == null || uid.equals("")) {
			// 如果uid为空，直接返回true
			return true;
		}
		
		boolean isContainUid = false;
		String[] channelIds = mChannelId.split(";");
		for (int i = 0; i < channelIds.length; i++) {
			if (uid.equals(channelIds[i].trim())) {
				isContainUid = true;
			}
		}
		
		return isContainUid;
	}
}
