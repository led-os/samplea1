package com.jiubang.ggheart.components.advert;

import com.go.proxy.SettingProxy;
import com.jiubang.ggheart.data.info.DesktopSettingInfo;

/**
 * 
 * <br>类描述:15屏广告请求工具类
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2012-9-5]
 */
public class AdvertUtils {
	/**
	 * <br>功能简述:通过位置计算摆放位置
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param pos 图标所谓屏幕位置 1-16
	 * @return
	 */
	public static int[] getXY(int pos) {
		DesktopSettingInfo desktopSettingInfo = SettingProxy.getDesktopSettingInfo();
		int rowSize = desktopSettingInfo.mRow;
		
		int cellX = 0;
		int cellY = 0;
		int lowSize = 4;
		
		int remainder = pos % lowSize;	//余数
		int divisor = pos / lowSize;	//除数
		
		if (remainder == 0) {
			cellX = 0;
			cellY = rowSize - divisor;
		} else {
			cellX = lowSize - remainder;
			cellY = rowSize - divisor - 1;
		}
		
		int[] xy = new int[2];
		xy[0] = cellX;
		xy[1] = cellY;

		return xy;
	}
}
