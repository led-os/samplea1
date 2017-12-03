package com.jiubang.ggheart.components.advert.untils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.go.proxy.ApplicationProxy;
import com.go.util.device.Machine;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.components.advert.AdvertDialogCenter;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 
 * <br>类描述:手动清除通知栏的广告通知时发送的广播
 * <br>功能详细描述:检查是否有购买付费功能。进行提示
 * 
 * @author  licanhui
 * @date  [2013-7-8]
 */
public class NoAdvertCheckReceiver extends BroadcastReceiver {
	public static NoAdvertCheckReceiver sReceiver;
	
	public  static final String NOADVERT_CHECK_ACTION = "com.jiubang.intent.action.no.advert.check";

	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(NOADVERT_CHECK_ACTION)) {
			//判断是否第一次显示
			if (DeskSettingUtils.isFirstShowNoAdvertPayDialog(context)) {

				//是否在桌面上还是其他程序打开
				boolean isLauncherTop = Machine.isTopActivity(ApplicationProxy.getContext(),
				        PackageName.PACKAGE_NAME);

				if (!isLauncherTop) {
					return;
				}

				DeskSettingUtils.setNoAdvertPayDialogPreference(context); //设置下次不再显示
				unregisteNoAdvertCheckReceiver(context); //取消注册
				AdvertDialogCenter.showRemoveAdDialog(context, 202);
			}
		}
	}
	
	/**
	 * <br>功能简述:通知栏监听手动清除广告消息
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 */
	public static void registeNoAdvertCheckReceiver(Context context) {
		//只有没有显示过的才会注册
		//没有付费，且第一次打开才注册
		if (DeskSettingUtils.isFirstShowNoAdvertPayDialog(context)
				&& !FunctionPurchaseManager.getInstance(context.getApplicationContext())
						.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_AD)) {
			if (sReceiver == null) {
				sReceiver = new NoAdvertCheckReceiver();
				IntentFilter filter = new IntentFilter();
				filter.addAction(NOADVERT_CHECK_ACTION);
				context.registerReceiver(sReceiver, filter);
			}
		}
		
	}
	
	public static void unregisteNoAdvertCheckReceiver(Context context) {
		if (sReceiver != null) {
			context.unregisterReceiver(sReceiver);
			sReceiver = null;
		}
	}
}
