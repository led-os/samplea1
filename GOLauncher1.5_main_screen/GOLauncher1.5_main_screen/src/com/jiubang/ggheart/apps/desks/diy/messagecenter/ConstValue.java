package com.jiubang.ggheart.apps.desks.diy.messagecenter;

import com.go.util.ServerUtils;
import com.jiubang.ggheart.launcher.LauncherEnv;


/**
 * 
 * @author rongjinsong
 * @version 1.0
 */
public class ConstValue {

	public final static boolean DEBUG = false;
	public final static String MSG_TAG = "messagecenter";
	public final static String ENCRYPT_KEY = "MESSAGECENTER130114";

	public static String sHosturlBase = LauncherEnv.Server.MSG_CENTER_BASE_HOSTURL_PRO; // 正式服务器地址
	public static String sMESSAGE_CENTER_IMAGE_PATH = LauncherEnv.Path.MESSAGECENTER_IMAGE_PATH;
	public static final int URL_GET_MSG_LIST = 1;
	public static final int URL_GET_MSG_CONTENT = 2;
	public static final int URLPOST_MSG_STATICDATA = 3;
	public static final int URL_GET_URL = 4;
	public static final int URL_GET_COUPON = 5;

	public static final int STATTUS_OK = 1;
	public static final int STATTUS_ERR_CODE_0 = 0; // 请求参数错误,
	public static final int STATTUS_ERR_CODE_1 = -1; // 服务器处理出错,
	public static final int STATTUS_ERR_CODE_2 = -2; // 业务处理异常；

	public static final String PREFIX_MSG = "msg://id=";
	public static final String PREFIX_GUI = "gui://id=";
	public static final String PREFIX_GUIDETAIL = "guidetail://id=";
	public static final String PREFIX_GUISPEC = "guispec://id=";
	public static final String PREFIX_MARKET = "market://id=";
	public static final String PREFIX_GOSTORETYPE = "gostoretype://id=";
	public static final String PREFIX_GOSTOREDETAIL = "gostoredetail://id=";
	public static final String PREFIX_HTTP = "http://";
	public static final String PREFIX_APPCENTERTYPE = "appcentertype://id=";
	public static final String PREFIX_APPCENTERTOPIC = "appcentertopic://id=";
	public static final String PREFIX_APPCENTERDETAIL = "appcenterdetail://id=";
	public static final String PREFIX_INTENT = "intent:";
	
	public static final String INSTRUCT_3D_OPEN = "3d#1";
	public static final String INSTRUCT_3D_CLOSE = "3d#0";
	public static final String INSTRUCT_3D_SHOW = "show3d#1"; //显示3D开关
	public static final String INSTRUCT_3D_HIDE = "show3d#0"; //隐藏3D开关
	
	public static final String CONTAINER_TOPBAR = "TopBarContainer"; //消息中心topbar容器名字
	public static final String CONTAINER_LIST = "ListContainer"; //消息中心列表容器名字

	// 测试后台
	// http://ggtest.3g.net.cn:8183/gomsgmanage/webcontent/index.jsp
	
	static {
		if (ServerUtils.isUseTestServer(LauncherEnv.Server.MSG_CENTER_CONFIG_USE_TEST_SERVER)) {
			sHosturlBase = LauncherEnv.Server.MSG_CENTER_BASE_HOSTURL_SIT;
		}
	}
}
