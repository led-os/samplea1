package com.go.location;

import android.location.Location;

/**
 * 
 * 类描述: 定位过程的回调接口
 * 
 * @author  liuwenqin
 * @date  [2012-9-4]
 */
public interface ReqLocationListener {
	/**
	 * 定位超时
	 * 
	 * @param locationWay
	 *            1，基站定位； 2，网络定位 3，GPS定位
	 */
	public void onLocationTimeout(int locationWay);

	/**
	 * 成功获取我的位置所处的城市的地址信息
	 * 
	 * @param curCity
	 *            我的位置所处的城市的地址信息
	 * @param curLocation 本次定位的经纬度信息
	 */
//	public void onLocationSuccess(Location curLocation);

	/** 已经获取我的位置的经纬度，但是获取该经纬度对应的城市名时失败 */
	public void onLocationNull();

	/**
	 * 通知定位方式发生改变
	 * 
	 * @param switchType
	 *            切换类型。 1，从基站定位切换为网络定位； 2，从网络定位切换为GPS定位 3，从基站定位切换为GPS定位
	 */
	public void onLocationWayChanged(int switchType);

	/**
	 * 基站定位失败，网络定位或GPS定位不能使用
	 * 
	 * @param status
	 *            1，系统不支持定位功能； 2、网络定位没打开 3、GPS定位没打开 4、网络定位和GPS定位都没有开启 5、基站定位失败
	 */
	public void onLocationFailed(int status);

	/**
	 * <br>功能简述: 当前定位到的经纬度和上次定位成功的经纬度差值小于定位精度。
	 * @param curLocation 当前定位到的经纬度位置
	 */
	public void onLocationLatLngFectched(Location curLocation);
}