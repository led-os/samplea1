package com.go.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.telephony.TelephonyManager;

/** 基站定位 */
/**
 *此类原用于基站定位,后因google基站定位不提供服务,
 *对于国内用户改用mapbar定位,为了保留原有的定位代码
 *的结构,因此类名不变,调用方法不变,只是内部逻辑更改 
 *补充于2013.01.14
 */
public class CellLocation extends SuperLocation implements LocationListener {

	/** 是否取消基站定位 */
	private boolean mIsCanceled;
	private ReqLocationListener mReqLocationListener;

	/**================mapbar定位相关================**/
//	private CellLocationProvider mCellLocationProvider; // 定位提供者

	public CellLocation(Context context, ReqLocation reqLocation) {
		super(context, reqLocation);
		this.mIsCanceled = false;
	}

	public void cancel() {
		this.mIsCanceled = true;
	}

	@Override
	public boolean startLocation(int prevLocationWay, final ReqLocationListener reqLocationListener) {
		boolean enableCell = false;
		mReqLocationListener = reqLocationListener;
		if (GlobalUtil.getSimState(mContext) != TelephonyManager.SIM_STATE_READY) {
			mReqLocation.removeTimer();
			mReqLocationListener.onLocationFailed(LocationConstants.LOCATION_SIMCARD_NOT_READY);
		} else if (!GlobalUtil.isNetworkOK(mContext)) {
			mReqLocation.removeTimer();
			mReqLocationListener.onLocationFailed(LocationConstants.LOCATION_NETWORK_ERROR);
		} else {
			enableCell = true;
			if (prevLocationWay == LocationConstants.WAY_GPS_LOCATION) {
				mReqLocationListener.onLocationWayChanged(LocationConstants.GPS_TO_CELL);
			} else if (prevLocationWay == LocationConstants.WAY_NETWORK_LOCATION) {
				mReqLocationListener.onLocationWayChanged(LocationConstants.NETWORK_TO_CELL);
			}
//			// 非国内用户回调定位失败,然后转用其他的定位方式
//			if (GlobalUtil.isInternalUser(mContext)) {
//				startMapbarLocate();
//			} else {
				enableCell = false;
				mReqLocationListener.onLocationFailed(LocationConstants.LOCATION_CELL_FAILED);
//			}
		}
		return enableCell;
	}

//	private void startMapbarLocate() {
//		if (mCellLocationProvider == null) {
//			mCellLocationProvider = new CellLocationProvider(mContext);
//		}
//		mCellLocationProvider.addLocationListener(this);
//		mCellLocationProvider.enableLocation();
//	}

	@Override
	public void onLocationChanged(Location location) {
		if (!mIsCanceled) {
			mReqLocation.removeTimer();
			if (null != location) {
				mReqLocationListener.onLocationLatLngFectched(location);
			} else {
				mReqLocationListener.onLocationFailed(LocationConstants.LOCATION_CELL_FAILED);
			}
		}
//		//删除监听(如果想停止定位，删除监听)  
//		mCellLocationProvider.clearLocationListener();
//		mCellLocationProvider.disableLocation();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}
}