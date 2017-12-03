package com.jiubang.ggheart.net.form;

/**
 * 
 * <br>类描述: 网络请求结果
 * 
 * @author liuwenqin
 * @date  [2012-9-17]
 */
public class Result {

	/** 获取信息的状态,只能有 HttpRequestStatus.REQUEST_SUCCESS,HttpRequestStatus.REQUEST_DATA_LATEST,HttpRequestStatus.REQUEST_FAILED */
	private volatile int mStatus = HttpRequestStatus.REQUEST_FAILED;
	/** 获取天气信息失败的错误类型 */
	private volatile int mErrorType = -10000;
	/** 网络类型 */
	private volatile int mNetType;
	/** 是否采用压缩 */
	private volatile boolean mBGzip;
	/** 刷新开始时间 */
	private long mRequestStartTime = 0;
	/** 刷新结束时间 */
	private long mRequestEndTime = 0;
	/** 联网的类型 */
	private volatile int mRequestTYpe = TYPE_UPDATE_WEATHER;
	/** 服务器处理业务的时间 */
	private volatile int mServiceTime = 0;
	/** 解析数据的开始时间 */
	private long mParseStartTime = 0;
	/** 解析数据的结束时间 */
	private long mParseEndTime = 0;
	/** 服务器下发数据的时间点 */
	private long mServiceDownlinkTime = 0;

	/** 错误信息 */
	private String mErrorMsg;
	/**
	 * 联网更新方式
	 * 更新方式。可选值：0(未知) ；1(手动更新）；2（自动更新，包括开机更新）；3（进入主程序更新）；4（亮屏更新） 
	 */
	private int mUpdateWay = 0;

	// ====================== 网络状态 =====================

	/**Wifi网络*/
	public static final int TYPE_WIFI = 0x1;
	/**不使用代理的移动网络*/
	public static final int TYPE_MOBILE = 0x2;
	/**使用代理的移动网络*/
	public static final int TYPE_MOBILE_PROXY = 0x3;
	/**WIMAX网络*/
	public static final int TYPE_WIMAX = 0x4;
	/**以太网*/
	public static final int TYPE_ETHERNET = 0x5;
	/**运营商热点网络*/
	public static final int TYPE_MOBILE_DUN = 0x6;
	/**未知网络*/
	public static final int TYPE_UNKNOW = 0x7;
	/**没有网络*/
	public static final int TYPE_NETWORK_DOWN = 0x10;

	// ====================== 联网的类型 =====================

	/**刷新天气*/
	public static final int TYPE_UPDATE_WEATHER = 0x1;
	/**定位位置*/
	public static final int TYPE_LOCATION = 0x2;
	/**搜索城市*/
	public static final int TYPE_SEARCH_CITY = 0x3;
	/**国际城市列表*/
	public static final int TYPE_CITY_LIST = 0x4;

	public Result() {
		this.mBGzip = false;
	}

	public boolean getBGzip() {
		return this.mBGzip;
	}

	public void setBGzip(boolean isBGzip) {
		this.mBGzip = isBGzip;
	}

	public int getNetType() {
		return mNetType;
	}

	public void setNetType(int NetType) {
		this.mNetType = NetType;
	}

	/**
	 * @param status
	 */
	public void setStatus(int status) {
		this.mStatus = status;
	}

	public int getStatus() {
		return this.mStatus;
	}

	/**
	 * 注意：
	 * <li>在天气刷新中使用System.currentTimeMillis()，为了计算下行时间
	 * <li>在其他使用SystemClock.elapsedRealtime()
	 * @param requestStartTime
	 */
	public synchronized void setRequestStartTime(long requestStartTime) {
		this.mRequestStartTime = requestStartTime;
	}

	public synchronized long getRequestStartTime() {
		return this.mRequestStartTime;
	}

	public synchronized void setRequestEndTime(long requestEndTime) {
		this.mRequestEndTime = requestEndTime;
	}

	public synchronized long getRequestEndTime() {
		return this.mRequestEndTime;
	}

	public synchronized long getRequestTime() {
		return mRequestEndTime - mRequestStartTime;
	}

	public int getErrorType() {
		return mErrorType;
	}

	public void setErrorType(int errorType) {
		this.mErrorType = errorType;
	}

	public int getRequestTYpe() {
		return mRequestTYpe;
	}

	public void setRequestType(int mRequestTYpe) {
		this.mRequestTYpe = mRequestTYpe;
	}

	public int getServiceTime() {
		return mServiceTime;
	}

	public void setServiceTime(int mServiceTime) {
		this.mServiceTime = mServiceTime;
	}

	public synchronized long getParseStartTime() {
		return mParseStartTime;
	}

	public synchronized void setParseStartTime(long mParseStartTime) {
		this.mParseStartTime = mParseStartTime;
	}

	public synchronized long getParseEndTime() {
		return mParseEndTime;
	}

	public synchronized void setParseEndTime(long mParseEndTime) {
		this.mParseEndTime = mParseEndTime;
	}

	public synchronized long getParseTime() {
		return mParseEndTime - mParseStartTime;
	}

	public synchronized long getServiceDownlinkTime() {
		return mServiceDownlinkTime;
	}

	public synchronized void setServiceDownlinkTime(long serviceDownlinkTime) {
		this.mServiceDownlinkTime = serviceDownlinkTime;
	}

	/**
	 * 获取下行时间
	 * @return
	 */
	public synchronized long getDownlinkCostTime() {
		if (mServiceDownlinkTime == 0) {
			return 0;
		} else {
			return mRequestEndTime - mServiceDownlinkTime;
		}
	}

	/**
	 * 获取上行时间(服务器下发数据的时间 - 请求开始时间 - 服务器处理时间)
	 * @return
	 */
	public synchronized long getUplinkCostTime() {
		return mServiceDownlinkTime - mRequestStartTime - mServiceTime;
	}

	public String getErrorMsg() {
		return mErrorMsg;
	}

	public void setErrorMsg(String mErrorMsg) {
		this.mErrorMsg = mErrorMsg;
	}

	public void setUpdateWay(int updateWay) {
		this.mUpdateWay = updateWay;
	}

	public int getUpdateWay() {
		return this.mUpdateWay;
	}
}