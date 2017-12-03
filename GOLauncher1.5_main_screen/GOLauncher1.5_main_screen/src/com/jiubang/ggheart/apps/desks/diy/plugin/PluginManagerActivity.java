package com.jiubang.ggheart.apps.desks.diy.plugin;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Window;

import com.go.proxy.ApplicationProxy;
import com.go.proxy.DownloadControllerProxy;
import com.jiubang.ggheart.appgame.base.utils.AppDownloadListener;
import com.jiubang.ggheart.appgame.download.DownloadTask;
import com.jiubang.ggheart.appgame.download.IDownloadService;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.apps.desks.diy.plugin.PluginItemViewAdapter.OnPluginClosedListener;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.launcher.ICustomAction;

/**
 * 插件管理启动类
 * @author liulixia
 *
 */
public class PluginManagerActivity extends Activity implements OnPluginClosedListener {
	private PluginManageView mPluginManageView = null;
	private boolean mHasBindService = false;
	private BroadcastReceiver mInstalledReceiver = null;
	private BroadcastReceiver mDownloadReceiver = null;
	private IDownloadService mDownloadMgr = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		confirmOrientation();
		boolean isCnUser = isCnUser();
		mPluginManageView = new PluginManageView(this, isCnUser);
		setContentView(mPluginManageView);
		registerBroadCastReceiver();
		
		if (isCnUser) {
			GoPluginUtil.checkPluginUpdate(this);
			// 先启动下载服务
			ApplicationProxy.getContext().startService(new Intent(ICustomAction.ACTION_DOWNLOAD_SERVICE));
			// 再bind服务
			if (!mHasBindService) {
				mHasBindService = ApplicationProxy.getContext().bindService(
						new Intent(ICustomAction.ACTION_DOWNLOAD_SERVICE), mConnenction,
						Context.BIND_AUTO_CREATE);
			}
			registerDownloadBroadCast();
		}
	}
	
	/**
	 * 功能简述: 注册监听程序安装或卸载广播接收器
	 */
	private void registerBroadCastReceiver() {
		// 注册安装广播接收器
		// 收到广播，对数据源重新排序
		mInstalledReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())
						|| Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())
						|| Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
					String packageName = intent.getData().getSchemeSpecificPart();
					if (packageName != null) {
						GoPluginOrWidgetInfo info = mPluginManageView.getPluginOrWidgetPkg(packageName);
						if (info != null) {
							if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) { //添加
								if (packageName.equals("com.youmi.filemaster")) {
									//如果是文件全能王，加统计
									GuiThemeStatistics.guiStaticData(52, packageName, "b000",
											1, "0", "40", "", "");
								}
								mPluginManageView.addNewLog();
							} else if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
								mPluginManageView.removeNewThemeLog();
							} else {
								if (isCnUser()) { //如果是国内渠道，要把本地update本地标签删掉
									info.mNeedUpdate = false;
									mPluginManageView.updateInstallData();
								}
							}
							mPluginManageView.startLoadData();
						}
					}
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		filter.addDataScheme("package");
		
		registerReceiver(mInstalledReceiver, filter);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mPluginManageView != null) {
			mPluginManageView.startLoadData();
		}
	}
	/**
	 * 功能简述: 注册下载广播接收器
	 */
	private void registerDownloadBroadCast() {
		mDownloadReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				if (ICustomAction.ACTION_APP_DOWNLOAD.equals(intent.getAction())) {
					DownloadTask downloadTask = intent
							.getParcelableExtra(AppDownloadListener.UPDATE_DOWNLOAD_INFO);
					// 判断task状态，向handler发送消息
					Message msg = new Message();
					Bundle bundle = new Bundle();
					String pkn = downloadTask.getDownloadApkPkgName();
					int percent = downloadTask.getAlreadyDownloadPercent();
					switch (downloadTask.getState()) {
						case DownloadTask.STATE_START :
							bundle.putString("pkn", pkn);
							msg.what = GoPluginOrWidgetInfo.DOWNLOADING;
							msg.obj = bundle;
							mViewHandler.sendMessage(msg);
							break;
						case DownloadTask.STATE_WAIT :
							bundle.putString("pkn", pkn);
							msg.what = GoPluginOrWidgetInfo.WAIT_FOR_DOWNLOAD;
							msg.obj = bundle;
							mViewHandler.sendMessage(msg);
							break;
						case DownloadTask.STATE_STOP :
							bundle.putString("pkn", pkn);
							msg.what = GoPluginOrWidgetInfo.PAUSE;
							msg.obj = bundle;
							mViewHandler.sendMessage(msg);
							break;
						case DownloadTask.STATE_FAIL :
							bundle.putString("pkn", pkn);
							msg.what = GoPluginOrWidgetInfo.DOWNLOAD_FAIL;
							msg.obj = bundle;
							mViewHandler.sendMessage(msg);
							break;
						case DownloadTask.STATE_FINISH :
							bundle.putString("pkn", pkn);
							msg.what = GoPluginOrWidgetInfo.DOWNLOAD_COMPLETE;
							msg.obj = bundle;
							mViewHandler.sendMessage(msg);
							break;
						case DownloadTask.STATE_DELETE :
							bundle.putString("pkn", pkn);
							msg.what = GoPluginOrWidgetInfo.NOT_INSTALLED;
							msg.obj = bundle;
							mViewHandler.sendMessage(msg);
							break;
						case DownloadTask.STATE_DOWNLOADING :
							bundle.putString("pkn", pkn);
							bundle.putInt("percent", percent);
							msg.what = GoPluginOrWidgetInfo.DOWNLOADING;
							msg.obj = bundle;
							mViewHandler.sendMessage(msg);
							break;
						case DownloadTask.STATE_RESTART :
							bundle.putString("pkn", pkn);
							msg.what = GoPluginOrWidgetInfo.DOWNLOADING;
							msg.obj = bundle;
							mViewHandler.sendMessage(msg);
							break;
						default :
							break;
					}
				}  else if (ICustomAction.ACTION_NEED_UPDATE_GOPLUGINS.equals(intent.getAction())) {
					if (mPluginManageView.checkNeedUpdateInstalled()) {
						mPluginManageView.updateInstallData();
					}
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(ICustomAction.ACTION_APP_DOWNLOAD);
		filter.addAction(ICustomAction.ACTION_NEED_UPDATE_GOPLUGINS);
		registerReceiver(mDownloadReceiver, filter);
	}
	private boolean isCnUser() {
		return !Statistics.is200ChannelUid(this);
//		return true;
	}

	/**
	 * 下载服务的控制接口Connector
	 */
	private ServiceConnection mConnenction = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mDownloadMgr = IDownloadService.Stub.asInterface(service);
			DownloadControllerProxy.getInstance().setDownloadController(mDownloadMgr);
			try {
				ArrayList<DownloadTask> allManagerdDownload = getManagerDownloadList();
				if (allManagerdDownload.size() > 0) {
					mPluginManageView.refreshFeatureLayout(allManagerdDownload);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mDownloadMgr = null;
		}
	};
	
	/**
	 * 获取所有ftp正在下载和已下载的task
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<DownloadTask> getManagerDownloadList() {
		ArrayList<DownloadTask> allManagerdDownload = new ArrayList<DownloadTask>();
		try {
			if (mDownloadMgr != null) {
				ArrayList<DownloadTask> list = getDownloadTaskList((Map<Long, DownloadTask>) mDownloadMgr
						.getDownloadConcurrentHashMap());
				if (list != null && list.size() > 0) {
					allManagerdDownload.addAll(list);
				}
				list = (ArrayList<DownloadTask>) mDownloadMgr.getDownloadCompleteList();
				if (list != null && list.size() > 0) {
					allManagerdDownload.addAll(list);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allManagerdDownload;
	}
	
	private Handler mViewHandler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle = (Bundle) msg.obj;
			String pkn = bundle.getString("pkn");
			GoPluginOrWidgetInfo info = mPluginManageView.getPluginOrWidgetPkg(pkn);
			if (info != null && info.mState != GoPluginOrWidgetInfo.INSTALLED) {
				int what = msg.what;
				info.mState = what;
				if (what == GoPluginOrWidgetInfo.DOWNLOADING) {
					int percent = bundle.getInt("percent");
					info.mPrecent = percent;
				}
				mPluginManageView.updateFeatureData();
			}
		}
	};
	
	/**
	 * <br>
	 * 功能简述:获取下载队列的列表
	 * 
	 * @param cHashMap
	 * @return
	 */
	private ArrayList<DownloadTask> getDownloadTaskList(Map<Long, DownloadTask> cHashMap) {
		ArrayList<DownloadTask> list = new ArrayList<DownloadTask>();
		for (DownloadTask dt : cHashMap.values()) {
			list.add(dt);
		}
		return list;
	}
	
	/**
	 * 判断是横屏还是竖屏
	 * */
	public void confirmOrientation() {
		DisplayMetrics mMetrics = getResources().getDisplayMetrics();
		if (mMetrics.widthPixels <= mMetrics.heightPixels) {
			SpaceCalculator.setIsPortrait(true);
			SpaceCalculator.getInstance(this).calculateItemViewInfo();
		} else {
			SpaceCalculator.setIsPortrait(false);
			SpaceCalculator.getInstance(this).calculateItemViewInfo();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		DeskSettingConstants.selfDestruct(getWindow().getDecorView());
		//解除下载服务的绑定
		if (mHasBindService) {
			DownloadControllerProxy.getInstance().setDownloadController(null);
			ApplicationProxy.getContext().unbindService(mConnenction);
			mHasBindService = false;
		}
		unregisterReceiver(mInstalledReceiver);
		if (mDownloadReceiver != null) {
			unregisterReceiver(mDownloadReceiver);
		}
		mPluginManageView.deleteUpdateInfos();
		mPluginManageView.clearup();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		confirmOrientation();
		if (mPluginManageView != null) {
			mPluginManageView.changeOrientation();
		}
	}

	@Override
	public void finishSelf() {
		finish();
	}
}
