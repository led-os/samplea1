package com.go.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * 类描述: GPS定位类
 * 
 * @author liuwenqin
 * @date [2012-9-4]
 */
public class GPSLocation extends SuperLocation {

	/** 定位完成后返回的各种状态 */
	private static final int LOCATION_CHANGED = 1; // 获取当前位置的经纬度成功
	private LocationManager mLocationManager;
	private boolean mLocating;
	private MyLocationListener mLocationListener;

	private Handler mLocationHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case LOCATION_CHANGED :
					disablePhoneProvider();
					Location curLocation = (Location) msg.obj;
					mReqLocationListener.onLocationLatLngFectched(curLocation);
					break;
			}
		}
	};

	public GPSLocation(Context context, ReqLocation reqLocation) {
		super(context, reqLocation);
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
	}

	public boolean getLocating() {
		return mLocating;
	}

	private void disablePhoneProvider() {
		if (mLocationListener == null) {
			return;
		}
		mLocationManager.removeUpdates(mLocationListener);
	}

	/**
	 * 定位结果监听者
	 * 类描述:
	 * 功能详细描述:
	 * 
	 * @author  liuwenqin
	 * @date  [2012-9-4]
	 */
	class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				mReqLocation.removeTimer();
				Message message = new Message();
				message.what = LOCATION_CHANGED;
				message.obj = location;
				mLocationHandler.sendMessage(message);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	@Override
	public boolean startLocation(int prevLocationWay, ReqLocationListener reqLocationListener) {
		this.mReqLocationListener = reqLocationListener;

		boolean enablePhone = false;
		int gpsProviderStatus = LocationConstants.isProviderEnabled(mLocationManager,
				LocationManager.GPS_PROVIDER);
		if (gpsProviderStatus == LocationConstants.LOCATION_PROVIDER_ENABLED) {
			mLocationListener = new MyLocationListener();
			if (prevLocationWay == LocationConstants.WAY_CELL_LOCATION) {
				this.mReqLocationListener.onLocationWayChanged(LocationConstants.CELL_TO_GPS);
			} else if (prevLocationWay == LocationConstants.WAY_NETWORK_LOCATION) {
				this.mReqLocationListener.onLocationWayChanged(LocationConstants.NETWORK_TO_GPS);
			}
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
					mLocationListener);
			enablePhone = true;
		} else if (gpsProviderStatus == LocationConstants.LOCATION_PROVIDER_UNABLED) {
			mReqLocation.removeTimer();
			mReqLocationListener.onLocationFailed(LocationConstants.LOCATION_NETWORK_UNABLED);
		} else {
			mReqLocation.removeTimer();
			mReqLocationListener.onLocationFailed(LocationConstants.LOCATION_UNSUPPORTED_BY_SYSYTEM);
		}
		return enablePhone;
	}

	@Override
	public void cancel() {
		disablePhoneProvider();
	}
}