package com.jiubang.ggheart.net.form;

/**
 * 保存刷新的各种状态码
 * @author lishen
 *
 */
public class HttpRequestStatus {

	// 刷新状态码
	// =================== 这部分必须与服务器返回码一致 ========================
	/** 服务器错误 */
	public static final int REQUEST_SERVER_ERROR = -1;
	/** 服务器找不到该城市 */
	public static final int REQUEST_NO_DATA = 0;
	/** 刷新成功（统计项） */
	public static final int REQUEST_SUCCESS = 1;
	/** 没有更新数据 */
	public static final int REQUEST_DATA_LATEST = 2;
	// =====================================================================
	/** 请求处理失败*/
	public static final int REQUEST_FAILED = 11;
	/** 下面是具体请求处理失败的原因*/
	/** 无网络 */
	public static final int REQUEST_NETWORK_UNAVAILABLE = 3;
	/** 请求URL不合法 */
	public static final int REQUEST_INVALID_URL = 4;
	/** HTTP协议不合法 */
	public static final int REQUEST_PROTOCOL_EXCEPTION = 5;
	/** 网络连接超时（统计项） */
	public static final int REQUEST_TIMEOUT = 6;
	/** 网络通信过程被中断（统计项） */
	public static final int REQUEST_IO_EXCEPTION = 7;
	/** 执行网络请求失败（统计项）（HTTP返回码不是200的情况） */
	public static final int REQUEST_HTTP_FAILED = 8;
	/** 数据解压出错 */
	public static final int REQUEST_ZIP_ERROR = 9;
	/** 下发数据不符合JSON格式（统计项） */
	public static final int REQUEST_INVALID_JSON_FORMAT = 10;
	/** 读取超时（统计项） */
	public static final int REQUEST_READ_TIMEOUT = 12;
	/** 网络空指针 */
	public static final int REQUEST_NULLPOINT_EXCEPTION = 13;

	// =====================================================================
	/** 网络通信和解析数据成功 */
	public static final int REQUEST_PARSE_SUCCESS = 0;
	/** 网络连接或者读取超时 */
	public static final int ERROR_NETWORK_TIMEOUT = 1;
	/** 网络通信过程被中断 */
	public static final int ERROR_NETWORK_ABORTED = 2;
	/** HTTP返回码不是200的情况  */
	public static final int ERROR_NETWORK_RESPONSECODE = 3;
	/** 服务器没有请求参数所对应的数据  */
	public static final int ERROR_SERVICE_NO_DATA = 4;
	/** 服务器错误  */
	public static final int ERROR_SERVICE_ERROR = 5;
	/** 下发数据不符合JSON格式  */
	public static final int ERROR_INVALID_JSON_FORMAT = 6;
	/** 读取超时 */
	public static final int ERROR_NETWORK_READ_TIMEOUT = 7;
	/** 未知  */
	public static final int ERROR_UNKNOW = 100;
}
