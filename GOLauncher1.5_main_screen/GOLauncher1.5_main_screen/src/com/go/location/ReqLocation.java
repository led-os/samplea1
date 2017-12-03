package com.go.location;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * 类描述: 请求定位类
 * 
 * @author liuwenqin
 * @date [2012-9-4]
 */
public class ReqLocation {

	/** 定位完成后返回的各种状态 */
	private static final int LOCATION_TIME_OUT = 2; // 获取经纬度超时
	private Context mContext;
	/** 定位回调接口 */
	private ReqLocationListener mReqLocationListener;
	private SuperLocation mLocationWay;
	private int mCurLocationWay;

	private Handler mLocationHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case LOCATION_TIME_OUT :
					mLocationWay.cancel();
					mReqLocationListener.onLocationTimeout((Integer) msg.obj);
					break;
			}
		}
	};

	/**
	 * @param context
	 * @param go_city
	 *            本地存放城市地址信息的数据库资源ID
	 */
	public ReqLocation(Context context) {
		this.mContext = context;
		this.mCurLocationWay = 0;
	}

	/**
	 * 开始定位
	 * 
	 * @param prevLocationWay
	 *            上一次的定位方式
	 * @param curLocationWay
	 *            当前定位方式
	 */
	public boolean startLocation(int prevLocationWay, int curLocationWay, int countTime,
			ReqLocationListener reqLocationListener) {
		this.mReqLocationListener = reqLocationListener;
		this.mCurLocationWay = curLocationWay;
		boolean enableLocation = false;
		switch (curLocationWay) {
			case LocationConstants.WAY_CELL_LOCATION :
				mLocationWay = new CellLocation(mContext, this);
//				FileLogger.getInstance().writeFileLogger("准备基站定位", FileLogger.LOCATION_FILE_NAME);
				enableLocation = mLocationWay.startLocation(prevLocationWay, mReqLocationListener);
				break;
			case LocationConstants.WAY_GPS_LOCATION :
				mLocationWay = new GPSLocation(mContext, this);
//				FileLogger.getInstance()
//						.writeFileLogger("准备GPS基站定位", FileLogger.LOCATION_FILE_NAME);
				enableLocation = mLocationWay.startLocation(prevLocationWay, mReqLocationListener);
				break;
			case LocationConstants.WAY_NETWORK_LOCATION :
				mLocationWay = new NetLocation(mContext, this);
//				FileLogger.getInstance().writeFileLogger("准备网络定位", FileLogger.LOCATION_FILE_NAME);
				enableLocation = mLocationWay.startLocation(prevLocationWay, mReqLocationListener);
				break;
			default :
				;
		}
		if (enableLocation) {
//			FileLogger.getInstance().writeFileLogger("定位开始", FileLogger.LOCATION_FILE_NAME);
			final int delayTime = 1000;
			mLocationHandler.postDelayed(mRunnable, countTime * delayTime);
		}
		return enableLocation;
	}

	private Runnable mRunnable = new Runnable() {
		public void run() {
			Message msg = new Message();
			msg.what = LOCATION_TIME_OUT;
			msg.obj = mCurLocationWay;
			mLocationHandler.sendMessage(msg);
			mLocationHandler.removeCallbacks(this);
		}
	};

	public void removeTimer() {
		mLocationHandler.removeCallbacks(mRunnable);
	}

	public void cancel() {
		removeTimer();
		if (mLocationWay != null) {
			mLocationWay.cancel();
		}
	}
}