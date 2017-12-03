package com.jiubang.ggheart.apps.desks.diy.plugin;

/**
 * 插件管理go插件和小部件实体类
 * @author liulixia
 *
 */
public class GoPluginOrWidgetInfo {
	/**
	 * 已安装
	 */
	public final static int INSTALLED = 0;
	/**
	 * 未安装
	 */
	public final static int NOT_INSTALLED = 1;
	/**
	 * 等待下载
	 */
	public final static int WAIT_FOR_DOWNLOAD = 2;
	/**
	 * 下载中
	 */
	public final static int DOWNLOADING = 3;
	/**
	 * 下载完成
	 */
	public final static int DOWNLOAD_COMPLETE = 4;
	/**
	 * 暂停
	 */
	public final static int PAUSE = 5;
	/**
	 * 下载失败
	 */
	public final static int DOWNLOAD_FAIL = 6;
	
	/**
	 * 是插件
	 */
	public final static int TYPE_PLUGIN = 0;
	/**
	 * 是小部件
	 */
	public final static int TYPE_WIDGET = 1;
	
	//是插件还是小部件
	public int mWidgetType = -1;
	// widget的包名
	public String mWidgetPkgName = "";
	// widget的名字
	public String mWidgetName = "";
	// widget的推荐图片id
	public int mWidgetImgId = 0;
	// widget的FTP下载地址
	public String mWidgetFtpUrl = "";
	// widget的电子市场地址
	public String mWidgetMarket = "";
	// widget当前状态（已安装、未安装）
	public int mState = -1;
	// 下载百分比
	public int mPrecent = 0;
	//是否需要更新
	public boolean mNeedUpdate = false;
	//widge服务器地址
	public String mWidgetImgUrl = "";
}
