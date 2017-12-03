package com.jiubang.ggheart.components.advert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.MsgMgrProxy;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IScreenAdvertMsgId;
import com.golauncher.utils.GoAppUtils;

/**
 * 
 * <br>类描述:15屏广告图标8小时通知栏提示广播接收器
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2012-12-22]
 */
public class AdvertOpenAppReceive extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
//		Log.i("lch", "onReceive:" + intent);
		String packageName = intent.getStringExtra(AdvertConstants.ADVERT_PACK_NAME);
		if (packageName != null && !packageName.equals("")) {
			boolean isAppExist = GoAppUtils.isAppExist(context, packageName);
			// 如果该包名则打开该应用
			if (isAppExist) {
				try {
					PackageManager pm = context.getPackageManager();
					Intent openIntent = pm.getLaunchIntentForPackage(packageName);
					if (openIntent != null) {
						//判断桌面是否正在运行
						if (GoLauncherActivityProxy.getActivity() != null) {
//							Log.i("lch3", "launcher open");
							MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
									ICommonMsgId.START_ACTIVITY, -1, openIntent, null);
						} else {
//							Log.i("lch3", "system open");
							MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_ADVERT_BUSINESS,
									IScreenAdvertMsgId.SET_OPEN_CACHE, -1, packageName,
									AdvertConstants.ADVERT_IS_OPENED); //设置缓存代表这个应用已经打开过,8小时后不提示
							context.startActivity(openIntent);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
