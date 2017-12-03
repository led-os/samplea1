package com.jiubang.ggheart.components.sidemenuadvert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.go.proxy.ApplicationProxy;
import com.go.proxy.DownloadControllerProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.util.device.Machine;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.jiubang.core.message.IMessageHandler;
import com.jiubang.ggheart.appgame.download.IDownloadService;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.components.sidemenuadvert.tools.SideToolsDataInfo;
import com.jiubang.ggheart.components.sidemenuadvert.tools.SideToolsInfo;
import com.jiubang.ggheart.components.sidemenuadvert.utils.SideAdvertUtils;
import com.jiubang.ggheart.components.sidemenuadvert.widget.SideWidgetDataInfo;
import com.jiubang.ggheart.components.sidemenuadvert.widget.SideWidgetInfo;
import com.jiubang.ggheart.data.Controler;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.launcher.ICustomAction;

/**
 * @describe 侧边栏广告控制器
 * @author zhangxi
 * @date [2013-10-9]
 */
public class SideAdvertControl extends Controler implements IMessageHandler {

	private static SideAdvertControl sInstance;
	private BroadcastReceiver mNetWorkReceiver = null; // 网络监听器

//	public ArrayList<SideAdvertDataInfo> mOutAdArrayList; // 对外保存的广告数据
//	public ArrayList<SideAdvertInfo> mAdvertInfosList; // 下载列表
//	public ArrayList<SideAdvertInfo> mUnfinishedDownloadList; // 保存未下载完成图片列表
//	public TreeMap<Integer, CopyOnWriteArrayList<SideAdvertInfo>> mCurrSideAdHashmap; // 当前下载Hash，用于分类

	private ArrayList<SideWidgetInfo> mOutGoWidgetList; // 对外保存的widget数据
	private SideWidgetReceiver mSideWidgetReceiver; //gwidget数据接收类
	private ArrayList<SideToolsInfo> mOutGoToolsList; // 对外保存的widget数据
	private SideToolsReceiver mSideToolsReceiver; //gotools数据接收类

//	public int mAddSize = 0; // 一共需下载多少个图标
//	public int mImageDownSize = 0; // 已经完成下载的个数（包括成功和失败）


	public static final String DOWNLOAD_TIME = "/download_time";
	public static final String INSTALL_TIME = "/install_time";
	public static final String DOWNLOAD_AD_MAPID = "/mapid";
	public static final String DOWNLOAD_AD_ADID = "/adid";

	public static final int APP_CHANGE = 1; // 广播通知应用发生变化
//	public static final int ADVERT_APP_CHANGE = 0; // 广播通知精品推荐应用发生变化

	public static final long THIRTY_MINS = 30 * 60 * 1000; // 统计应用程序安装超时时间

//	public static boolean sIsFinishedRequestData; // 侧边栏广告包头数据是否请求结束
	private boolean mIsNeedGWRequest = true; // 是否需要下载widget的数据包头信息

	private boolean mHasBindService = false;

	/**
	 * 下载服务的控制接口Connector
	 */
	private ServiceConnection mConnenction = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			IDownloadService mController = IDownloadService.Stub
					.asInterface(service);
			// 设置整个进程通用的下载控制接口
			DownloadControllerProxy.getInstance().setDownloadController(mController);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i("", "Theme onServiceDisconnected");
			DownloadControllerProxy.getInstance().setDownloadController(null);
		}
	};

	public static synchronized SideAdvertControl getAdvertControlInstance(
			Context context) {
		if (sInstance == null) {
			sInstance = new SideAdvertControl(context);
		}
		return sInstance;
	}
	
	public static void destroy() {
		MsgMgrProxy.unRegistMsgHandler(sInstance);
		sInstance = null;
	}

	private SideAdvertControl(Context context) {
		super(context);

		mContext = context;
		// 注册安装事件
		MsgMgrProxy.registMsgHandler(this);
		if (!Statistics.is200ChannelUid(context)) {
			startDownloadServices();
		}
	}

	/**
	 * @describe 请求服务器获取widget数据
	 * @author zhangxi
	 * @data 2013-10-9
	 */
	public void requestWidgetData() {
		mIsNeedGWRequest = false;
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (checkNeedRequestData()) {
					mSideWidgetReceiver = SideWidgetReceiver
							.getSideWidgetReceiverInstance(mContext);
					mSideWidgetReceiver.requestSideWidgetData();
				}
			}
		}.start();
	}
	
	/**
	 * @describe 请求服务器获取widget数据
	 * @author zhangxi
	 * @data 2013-10-9
	 */
	public void requestToolsData() {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (checkNeedRequestData()) {
					mSideToolsReceiver = SideToolsReceiver
							.getSideToolsReceiverInstance(mContext);
					mSideToolsReceiver.requestSideToolsData();
				}
			}
		}.start();
	}

	/**
	 * @describe 注册网络监听器，监听网络的联通
	 * @author zhangxi
	 * @data 2013-10-9
	 */
	private void registerNetWorkReceiver() {
		if (mNetWorkReceiver == null) {
			SideAdvertUtils.log("注册网络状态监听！");
			mNetWorkReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					if (intent.getAction().equals(
							ConnectivityManager.CONNECTIVITY_ACTION)) {
						if (Machine.isNetworkOK(mContext)) {
							SideAdvertUtils.log("监听到网络联通！");
							unRegisterNetWorkReceiver(); // 取消册网络状态监听
						}
					}
				}
			};
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			mContext.registerReceiver(mNetWorkReceiver, filter);
		}
	}

	/**
	 * @describe 取消册网络状态监听
	 * @author zhangxi
	 * @data 
	 */
	private void unRegisterNetWorkReceiver() {
		if (mNetWorkReceiver != null) {
			SideAdvertUtils.log("取消册网络状态监听！");
			mContext.unregisterReceiver(mNetWorkReceiver);
			mNetWorkReceiver = null;
		}
	}

	/**
	 * @describe 检查是否需要请求服务器获取数据
	 * 如果没有网络时就会注册网络状态监听,存在网络代表会请求服务器获取数据。不管是否获取成功。否代表已经请求过。那下次就不会再请求了
	 * @author zhangxi
	 * @data 2013-10-9
	 */
	private boolean checkNeedRequestData() {
		// 判断是否SD卡存在
		if (!Machine.isSDCardExist()) {
			SideAdvertUtils.log("没有SD卡！");
			return false;
		}

		// 判断是否有网络
		boolean isHasNetWork = Machine.isNetworkOK(mContext);
		if (!isHasNetWork) {
			SideAdvertUtils.log("没有网络！");
			mIsNeedGWRequest = true;
			registerNetWorkReceiver();
			return false;
		}

		return true;
	}

	private void sortInstallByInfo(SideWidgetDataInfo info,
			List<SideWidgetInfo> installList,
			List<SideWidgetInfo> unInstallList) {
		if (installList == null || unInstallList == null || info == null) {
			return;
		}

		if (info.isIsInstalled()) {
			boolean isSame = false;
			for (SideWidgetInfo tmpInfo : installList) {
				if (tmpInfo.getWidgetPkgName()
						.equals(info.getWidgetPkgName())) {
					isSame = true;
				}
			}
			if (!isSame) {
				installList.add(info);
			}
		} else {
			boolean isSame = false;
			for (SideWidgetInfo tmpInfo : unInstallList) {
				if (tmpInfo.getWidgetPkgName()
						.equals(info.getWidgetPkgName())) {
					isSame = true;
				}
			}
			if (!isSame) {
				unInstallList.add(info);
			}
		}
	}
			
	
	/**
	 * @describe 输出并排序wideget列表
	 * @param outGoWidgetInfoList
	 * @author zhangxi
	 * @data 2013-10-9
	 */
	private void sortWidgetInfoList(
			List<SideWidgetInfo> outGoWidgetInfoList) {
		if (outGoWidgetInfoList == null) {
			return;
		}

		// 从本地XML获取数据
		mSideWidgetReceiver = SideWidgetReceiver
				.getSideWidgetReceiverInstance(mContext);
		ArrayList<SideWidgetInfo> installedArrayList = new ArrayList<SideWidgetInfo>();
		ArrayList<SideWidgetInfo> unInstalledArrayList = new ArrayList<SideWidgetInfo>();
		
		ArrayList<SideWidgetDataInfo> whiteListArrayList;
		whiteListArrayList = mSideWidgetReceiver.getWhiteListWidgetList();
		
		if (whiteListArrayList != null) {
			for (SideWidgetDataInfo goWidgetInfo : whiteListArrayList) {
				// 未安装的一定显示
				sortInstallByInfo(goWidgetInfo, installedArrayList,
						unInstalledArrayList);
			}
		}
		
		CopyOnWriteArrayList<SideWidgetDataInfo> netArrayList;
		netArrayList = mSideWidgetReceiver.getGoWidgetNetList();

		if (netArrayList != null) {
			// 网络获取数据
			for (SideWidgetDataInfo goWidgetInfo : netArrayList) {
				// 未安装的一定显示
				if (!goWidgetInfo.isIsInstalled()) {
					sortInstallByInfo(goWidgetInfo, installedArrayList,
							unInstalledArrayList);
				} 
			}
		} else {
			// 获取缓存数据
			CopyOnWriteArrayList<SideWidgetDataInfo> cacheArrayList;
			cacheArrayList = mSideWidgetReceiver.getGoWidgetCacheList();
			if (!cacheArrayList.isEmpty()) {
				for (SideWidgetDataInfo goWidgetInfo : cacheArrayList) {
					// 未安装的一定显示
					if (!goWidgetInfo.isIsInstalled()) {
						sortInstallByInfo(goWidgetInfo, installedArrayList,
								unInstalledArrayList);
					} 
				}
			}
		}
		
		//for inner widget
		SideWidgetInfo innerWidgetInfo = mSideWidgetReceiver.getInnerWidgetInfo();
		installedArrayList.add(innerWidgetInfo);
		
		CopyOnWriteArrayList<SideWidgetDataInfo> xmlArrayList;
		xmlArrayList = mSideWidgetReceiver.getGoWidgetXmlList();

		// 处理本地XML的安装和未安装排序
		if (xmlArrayList != null) {
			for (SideWidgetDataInfo xmlwidgetinfo : xmlArrayList) {
				sortInstallByInfo(xmlwidgetinfo, installedArrayList,
						unInstalledArrayList);
			}
		}
		// for system widget
		if (Build.VERSION.SDK_INT >= 16) {
			SideWidgetInfo sysWidgetInfo = mSideWidgetReceiver.getSysWidgetInfo();
			outGoWidgetInfoList.add(sysWidgetInfo);
		}

		outGoWidgetInfoList.addAll(installedArrayList);
		outGoWidgetInfoList.addAll(unInstalledArrayList);
	}

	/**
	 * @describe 外部接口获取widget列表
	 * @author zhangxi
	 * @return ArrayList<GoWidgetProviderInfo>
	 * @data 2013-10-9
	 */
	public ArrayList<SideWidgetInfo> getGoWidgetInfo() {
		// 装载数据
		if (mOutGoWidgetList == null) {
			mOutGoWidgetList = new ArrayList<SideWidgetInfo>();
		} else {
			mOutGoWidgetList.clear();
		}

		sortWidgetInfoList(mOutGoWidgetList);

		return mOutGoWidgetList;
	}
	
	/**
	 * @describe 外部接口获取widget列表
	 * @author zhangxi
	 * @return ArrayList<GoWidgetProviderInfo>
	 * @data 2013-10-9
	 */
	public ArrayList<SideToolsInfo> getGoToolsInfo() {
		// 装载数据
		if (mOutGoToolsList == null) {
			mOutGoToolsList = new ArrayList<SideToolsInfo>();
		} else {
			mOutGoToolsList.clear();
		}
		mSideToolsReceiver = SideToolsReceiver
				.getSideToolsReceiverInstance(mContext);
		List<SideToolsDataInfo> xml = mSideToolsReceiver.getGoWidgetXmlList();
		List<SideToolsDataInfo> net = mSideToolsReceiver.getGoWidgetNetList();
//		List<SideToolsDataInfo> cache = mSideToolsReceiver.getGoWidgetCacheList();
		mOutGoToolsList.addAll(xml);
		mOutGoToolsList.addAll(net);
		return mOutGoToolsList;
	}

	public void onDestory() {
		// 解除下载服务的绑定
		if (mHasBindService) {
			ApplicationProxy.getContext().unbindService(mConnenction);
			DownloadControllerProxy.getInstance().setDownloadController(null);
			mHasBindService = false;
		}
	}

	private void startDownloadServices() {
		// 先启动下载服务
		ApplicationProxy.getContext().startService(new Intent(ICustomAction.ACTION_DOWNLOAD_SERVICE));
		// 再bind服务
		if (!mHasBindService) {
			mHasBindService = ApplicationProxy.getContext().bindService(
					new Intent(ICustomAction.ACTION_DOWNLOAD_SERVICE), mConnenction,
					Context.BIND_AUTO_CREATE);
		}
	}

	/**
	 * @author zhangxi
	 * @data 2013-10-9
	 */
	@Override
	public int getMsgHandlerId() {
		// TODO Auto-generated method stub
		return IDiyFrameIds.SIDE_ADVERT;
	}

	/**
	 * @author zhangxi
	 * @data 2013-10-9
	 */
	@Override
	public boolean handleMessage(Object who, int msgId, int param, Object ...obj) {
		switch (msgId) {
		case IAppCoreMsgId.EVENT_INSTALL_APP:
			SideAdvertUtils.log("进来的是:" + msgId);

			ArrayList<AppItemInfo> appItemInfos = (ArrayList<AppItemInfo>) obj[1];
			if (appItemInfos == null || appItemInfos.isEmpty()) {
				return true;
			}

			for (AppItemInfo itemInfo : appItemInfos) {
				PreferencesManager manager = new PreferencesManager(
						ApplicationProxy.getContext(),
						IPreferencesIds.SIDEADVERT_DOWNLOAD_PREFERENCES,
						Context.MODE_PRIVATE);
				String keyTime = itemInfo.getAppPackageName() + DOWNLOAD_TIME;
				String keyMapId = itemInfo.getAppPackageName()
						+ DOWNLOAD_AD_MAPID;
				String keyAdId = itemInfo.getAppPackageName()
						+ DOWNLOAD_AD_ADID;
				long downLoadTime = manager.getLong(keyTime, -1);
				String downLoadMapid = manager.getString(keyMapId, "");
				String downLoadAdid = manager.getString(keyAdId, "");
				if (downLoadTime != -1 && downLoadMapid.length() > 0
						&& downLoadAdid.length() > 0) {
					if (System.currentTimeMillis() - downLoadTime < THIRTY_MINS) {
						SideAdvertUtils.log("b000统计提交");
						GuiThemeStatistics.sideAdStaticData(downLoadMapid,
								SideAdvertConstants.STATS_ADVERT_INSTALL,
								SideAdvertConstants.STATS_ADVERT_RST_SUCCESS,
								downLoadAdid);
						manager.putLong(itemInfo.getAppPackageName()
								+ INSTALL_TIME, System.currentTimeMillis());
						manager.commit();
						//更新消息
//						broadCast(ADVERT_APP_CHANGE, 0, null, null);
					}
					manager.remove(keyTime);
					manager.remove(keyMapId);
					manager.remove(keyAdId);
				}
			}
			
		case IAppCoreMsgId.EVENT_INSTALL_PACKAGE:
		case IAppCoreMsgId.EVENT_UNINSTALL_PACKAGE:
		case IAppCoreMsgId.EVENT_UNINSTALL_APP:
		case IAppCoreMsgId.EVENT_UPDATE_APP :
		case IAppCoreMsgId.EVENT_UPDATE_PACKAGE:
			SideAdvertUtils.log("进来的是:" + msgId);
			String widgetString = (String) obj[0];
			if (widgetString == null || widgetString.length() == 0) {
				return true;
			}

			SideAdvertUtils.log("下载包名:" + widgetString);
			boolean tmpFlag = false;
			try {
				mSideWidgetReceiver = SideWidgetReceiver
						.getSideWidgetReceiverInstance(mContext);

				if (msgId == IAppCoreMsgId.EVENT_INSTALL_APP
						|| msgId == IAppCoreMsgId.EVENT_INSTALL_PACKAGE
						|| msgId == IAppCoreMsgId.EVENT_UPDATE_PACKAGE) {
					tmpFlag = mSideWidgetReceiver.refreshDataInfo(widgetString,
							true);
				} else if (msgId == IAppCoreMsgId.EVENT_UNINSTALL_PACKAGE
						|| msgId == IAppCoreMsgId.EVENT_UNINSTALL_APP) {
					tmpFlag = mSideWidgetReceiver.refreshDataInfo(widgetString,
							false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (tmpFlag) {
				SideAdvertUtils.log("收到widget消息，发广播要求处理");
				broadCast(APP_CHANGE, 0, obj[0], null);
			}

			break;
		default:
			break;
		}
		return false;
	}
}
