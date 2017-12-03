package com.go.proxy;

import android.content.Context;
import android.content.Intent;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.jiubang.ggheart.apps.config.ChannelConfig;
import com.jiubang.ggheart.apps.config.GOLauncherConfig;
import com.jiubang.ggheart.components.advert.untils.NoAdvertCheckReceiver;
import com.jiubang.ggheart.data.DataChangeListener;
import com.jiubang.ggheart.data.DataType;
import com.jiubang.ggheart.launcher.ICustomAction;

/**
 * DataChangeListener代理
 * @author liuheng
 *
 */
public class DataChangeListenerProxy {

	private DataChangeListener mDataChangeListener = null;
	private static DataChangeListenerProxy sInstance;
	
	private DataChangeListenerProxy() {
		
	}
	
	public static DataChangeListenerProxy getInstance() {
		if (null == sInstance) {
			synchronized (DataChangeListenerProxy.class) {
				if (null == sInstance) {
					sInstance = new DataChangeListenerProxy();
				}
			}
		}
		
		return sInstance;
	}
	
	/**
	 * 设置数据改变监听
	 * 
	 * @param listener
	 *            监听者
	 */
	public void setDataChangeListener(DataChangeListener listener) {
		mDataChangeListener = listener;
	}
	
	/**
	 * <br>
	 * 功能简述: 当数据加载完毕时调用此方法，初始化一些数据 <br>
	 * 功能详细描述: <br>
	 * 注意: 这个方法是在主线程中调用，如果初始化的操作很耗时，需要在方法内启动线程
	 */
	public void dataLoadFinish() {
		final Context context = ApplicationProxy.getContext();
		// 初始化内付费的管理类
		initAppInBilling();
		// 通知周边插件桌面启动
		context.sendBroadcast(new Intent(ICustomAction.ACTION_LAUNCHER_START));

		StatisticsManager.getInstance(context).enableSoftManager();
		
//		DeskSettingUtils.setPayFunctionState(sContext);
		
		//通知栏监听手动清除广告消息
		NoAdvertCheckReceiver.registeNoAdvertCheckReceiver(context);
	}
	
	/**
	 * <br>
	 * 功能简述: 初始化应用内付费的管理类 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	private void initAppInBilling() {
		// 获取与渠道相关的桌面配置文件信息类
		Context context = ApplicationProxy.getContext();
		ChannelConfig channelConfig = GOLauncherConfig.getInstance(context).getChannelConfig();
		if (channelConfig != null) {
			// 非200渠道的包，默认是不支持内购的，不需要启动内购的服务
			if (channelConfig.isNeedBillingService()) {				
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
						ICommonMsgId.CHECK_BILLING_SUPPORT, 1, null, null);
			}
		}
	}
	
	/**
	 * 退出桌面
	 */
	public void exit(boolean restart) {
		Context context = ApplicationProxy.getContext();
		// 桌面退出的时候，同时也退出下载服务
		// 发送广播到downloadService，通知其保存下载信息
		Intent intent = new Intent();
		intent.setAction(ICustomAction.ACTION_SERVICE_BROADCAST);
		context.sendBroadcast(intent);
		if (null != mDataChangeListener) {
			final int msgId = restart ? DataType.RESTART_GOLAUNCHER
					: DataType.EXIT_GOLAUNCHER;
			mDataChangeListener.dataChanged(DataType.DATATYPE_THEMESETTING,
					msgId, null, null);
		}
		// 停止服务
		// TODO:暂时去掉service.by huyong 2011-02-28
		// stopAppService();

		// 清除付费管理类
//		destoryAppInBilling();
	}
}
