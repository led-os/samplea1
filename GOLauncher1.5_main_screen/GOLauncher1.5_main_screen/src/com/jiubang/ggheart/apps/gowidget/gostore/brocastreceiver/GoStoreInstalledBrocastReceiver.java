package com.jiubang.ggheart.apps.gowidget.gostore.brocastreceiver;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.RemoteException;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.util.device.Machine;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.base.component.AppRecommendDialogActivity;
import com.jiubang.ggheart.appgame.base.net.DownloadUtil;
import com.jiubang.ggheart.appgame.base.net.InstallCallbackManager;
import com.jiubang.ggheart.appgame.base.utils.AppDownloadListener;
import com.jiubang.ggheart.appgame.download.DefaultDownloadListener;
import com.jiubang.ggheart.appgame.download.DownloadCompleteManager;
import com.jiubang.ggheart.appgame.download.DownloadTask;
import com.jiubang.ggheart.appgame.download.ServiceCallbackDownload;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.MessageManager;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStoreStatisticsUtil;
import com.jiubang.ggheart.data.statistics.AppManagementStatisticsUtil;
import com.jiubang.ggheart.data.statistics.AppRecommendedStatisticsUtil;
import com.jiubang.ggheart.data.statistics.GoRecommWidgetStatisticsUtil;
import com.jiubang.ggheart.data.statistics.GoSwitchWidgetStatisticsUtil;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.MonitorAppstatisManager;
import com.jiubang.ggheart.data.statistics.StatisticsData;
import com.jiubang.ggheart.data.statistics.realtiemstatistics.RealTimeStatisticsUtil;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.switchwidget.GoSwitchWidgetUtils;
/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 */
public class GoStoreInstalledBrocastReceiver extends BroadcastReceiver {

	public static final String PACKAGE_NAME_KEY = "packageName";
	public static final String PACKAGE_ACTION_KEY = "packageAction";

	@Override
	public void onReceive(final Context context, Intent intent) {
		if (context != null && intent != null) {
			final String action = intent.getAction();
			String data = intent.getDataString();
			String packageName = null;
			if (data != null && !"".equals(data)) {
				String[] items = data.split(":");
				if (items != null && items.length >= 2) {
					packageName = items[1];
				}
			}
			final String pkgName = packageName;
			final MessageManager manager = MessageManager.getMessageManager(context.getApplicationContext());
			if (pkgName != null && !"".equals(pkgName)) {
				
				new Thread() {
					@Override
					public void run() {
						super.run();
						//上传安装统计数据-------------Go推荐Widget中的统计功能    caoyaming 2014-01-15
						GoRecommWidgetStatisticsUtil.uploadDownloadInstallStatistic(context, pkgName);
						//上传Go开关安装统计数据-------------内置Go开关Widget中的统计功能    caoyaming 2014-02-27
						if (GoSwitchWidgetUtils.isGoSwitchWidgetPackageName(pkgName)) {
							//安装Go开关Widget,上传统计数据.
							GoSwitchWidgetStatisticsUtil.uploadDownloadInstallStatistic(context);
						}
						//判断下载服务是是否有运行
						final boolean isDownloadServiceStarted = isDownloadServiceStarted(context);
						// 记录GO精品软件安装数
						goStoreUpdateInstallInfo(context, pkgName);
						
						StatisticsData.updateAppInstallData(context, pkgName);
						// 上传消息中心推送的第三方应用统计数据
						manager.messageCenterRecommandAppUpdateStatistics(pkgName);
						
						// 若下载服务没开启，直接读写SD数据
						if (!isDownloadServiceStarted) {
							updateDownloadData(context, pkgName, action);
						} else {
							updateDownloadServiceStatictics(pkgName, action);
						}
					}
				}.start();
			}
		}
	}
	
	/**
	 * 
	 * @param context
	 * @param pkgName
	 */
	private void goStoreUpdateInstallInfo(Context context , String pkgName) {
		GuiThemeStatistics guistatistics = GuiThemeStatistics
				.getInstance(context);
		// 新安装统计
		MonitorAppstatisManager.getInstance(context)
				.handleAppInstalled(pkgName);

		// 老安装统计
		GoStoreStatisticsUtil.doInstallStistics(context,
				pkgName);
		guistatistics.checkInstallPkg(pkgName);
		// 如果是从电子市场下载，则下载完成后再统计 “下载量”
		if (GoStorePhoneStateUtil.is200ChannelUid(context)
				|| !GoAppUtils.isCnUser(context)) {
			//从应用中心下载安装更新后 这边监听统计  add by zzf 做安装统计
			RealTimeStatisticsUtil.upInstalledStaticData(context, pkgName);
			// 应用更新
			// 统计：国外，先将UI入口设置为2(详细)
			AppManagementStatisticsUtil.getInstance().saveCurrentUIEnter(
					context,
					AppManagementStatisticsUtil.UIENTRY_TYPE_DETAIL);
			// 统计 应用更新 下载完成
			AppManagementStatisticsUtil.getInstance().saveUpdataComplete(
					context,
					pkgName,
					AppManagementStatisticsUtil.getInstance().getDownloadAppID(
							context, pkgName), 1);
			// 统计 应用更新 安装完成
			AppManagementStatisticsUtil.getInstance().saveUpdataSetup(
					context, pkgName);

			// 应用推荐：
			// 统计:先保存推荐界面入口
			AppRecommendedStatisticsUtil.getInstance().saveCurrentUIEnter(
					context,
					AppRecommendedStatisticsUtil.UIENTRY_TYPE_DETAIL);

			// 统计:应用推荐--下载/更新完成
			AppRecommendedStatisticsUtil.getInstance().saveDownloadComplete(
					context,
					pkgName,
					AppRecommendedStatisticsUtil.getInstance().getDownloadAppID(
							context, pkgName), 1);

			// 统计：应用推荐--安装完成
			AppRecommendedStatisticsUtil.getInstance().saveDownloadSetup(
					context, pkgName);

		} else {
			// 统计 应用更新 安装完成
			AppManagementStatisticsUtil.getInstance().saveUpdataSetup(
					context, pkgName);

			// 统计：应用推荐--安装完成
			AppRecommendedStatisticsUtil.getInstance().saveDownloadSetup(
					context, pkgName);
		}
	}
	
	/**
	 * 无启动下载服务时，直接读取SD卡数据
	 * @param context
	 * @param pkgName
	 * @param action
	 */
	public void updateDownloadData(Context context, String pkgName, String action) {
		try {
			DownloadCompleteManager downloadCompleted = new DownloadCompleteManager(context);
			
			if (action.equals(Intent.ACTION_PACKAGE_ADDED)
					|| action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
				downloadCompleted.getInstalledTaskFromSD();
				downloadCompleted.addInstalledTask(pkgName);
			}
			
			// 添加安装安成打开应用的信息
			downloadCompleted.getDownloadCompleteTask();
			List<DownloadTask> list = downloadCompleted.getDownloadCompleteList();
			updateDownloadCompletedData(list, null, pkgName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 启动下载服务，需将“程序已下载完成，点击安装”的通知栏提示清除掉
	 * @param pkgName
	 * @param action
	 */
	private void updateDownloadServiceStatictics(final String pkgName, final String action) {
		ServiceCallbackDownload.ServiceCallbackRunnable runnable = new ServiceCallbackDownload.ServiceCallbackRunnable() {
			@Override
			public void run() {
				try {
					
					if (mDownloadController != null) {
						if (action.equals(Intent.ACTION_PACKAGE_ADDED)
								|| action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
							mDownloadController.addInstalledPackage(pkgName);
						}
						
						// 添加安装安成打开应用的信息
						ArrayList<DownloadTask> list = (ArrayList<DownloadTask>) mDownloadController
								.getDownloadCompleteList();
						
						ArrayList<Long> ids = (ArrayList<Long>) mDownloadController
								.getCompleteIdsByPkgName(pkgName);
						
						updateDownloadCompletedData(list, ids, pkgName);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		};
		ServiceCallbackDownload.callbackDownload(ApplicationProxy.getContext(), runnable);
	}
	
	private void updateDownloadCompletedData(List<DownloadTask> list, ArrayList<Long> ids, String pkgName) {
		NotificationManager notificationManager = (NotificationManager) ApplicationProxy.getContext().
				getSystemService(Context.NOTIFICATION_SERVICE);
		if (notificationManager != null) {
			// 移除下载完成点击安装的通知栏提示
			if (ids != null) {
				for (long id : ids) {
					notificationManager.cancel(AppDownloadListener.NOTIFY_TAG,
							(int) id);
					notificationManager.cancel(
							DefaultDownloadListener.NOTIFY_TAG, (int) id);
				}
			}
			// 判断需不需要进行回调
			String icbackurl = InstallCallbackManager.getIcbackurl(pkgName);
			if (icbackurl != null && !icbackurl.equals("")) {
				DownloadUtil.sendCBackUrl(icbackurl);
			}
			int treatment = InstallCallbackManager.getTreatment(pkgName);
			if (treatment == -1) {
				return;
			}
			if (treatment == 1) {
				DownloadTask task = null;
				if (list == null) {
					return;
				}
				for (DownloadTask dt : list) {
					if (dt != null
							&& dt.getDownloadApkPkgName().equals(pkgName)) {
						task = dt;
						break;
					}
				}
				if (task == null) {
					return;
				}
				PackageManager pm = ApplicationProxy.getContext()
						.getPackageManager();
				if (pm == null) {
					return;
				}
				Intent in = null;
				in = pm.getLaunchIntentForPackage(pkgName);
				if (in == null) {
					return;
				}
				in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				in.setAction(android.content.Intent.ACTION_VIEW);
				PendingIntent installComplete = PendingIntent.getActivity(
						ApplicationProxy.getContext(), 0, in, 0);
				Notification notification = new Notification(
						R.drawable.open_app_notification,
						task.getDownloadName()
						+ " "
						+ ApplicationProxy.getContext().getString(
								R.string.installed_tap_to_start),
								System.currentTimeMillis());
				notification.setLatestEventInfo(ApplicationProxy.getContext(),
						task.getDownloadName(), ApplicationProxy.getContext()
						.getString(R.string.installed_tap_to_start),
						installComplete);
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				notificationManager.notify(AppDownloadListener.NOTIFY_TAG,
						(int) task.getId(), notification);
			} else if (treatment == 2) {
				if (Machine.isTopActivity(ApplicationProxy.getContext(), PackageName.PACKAGE_NAME)) {
					Intent in = new Intent();
					in.setClass(ApplicationProxy.getContext(),
							AppRecommendDialogActivity.class);
					in.putExtra("packageName", pkgName);
					in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ApplicationProxy.getContext().startActivity(in);
				}
			} 
		}
	}
	
	
	private boolean isDownloadServiceStarted(Context context) {
		String downloadService = "com.jiubang.ggheart.appgame.download.DownloadService";
		boolean isStarted = false;
		ActivityManager mActivityManager =   
	            (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
		try {
			List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(Integer.MAX_VALUE);  
			String className = null;
			for (int i = 0; i < mServiceList.size(); i++) {  
				className = mServiceList.get(i).service.getClassName();
	            if (downloadService.equals(className)) {  
	                return true;  
	            }  
	        }  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isStarted;
	}
}
