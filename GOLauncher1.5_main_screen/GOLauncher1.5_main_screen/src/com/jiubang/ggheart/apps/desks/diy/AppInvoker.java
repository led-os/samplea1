package com.jiubang.ggheart.apps.desks.diy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.util.AppUtils;
import com.go.util.ConvertUtils;
import com.go.util.GotoMarketIgnoreBrowserTask;
import com.go.util.device.Machine;
import com.go.util.file.media.ThumbnailManager;
import com.go.util.market.MarketConstant;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IScreenAdvertMsgId;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.download.DefaultDownloadListener;
import com.jiubang.ggheart.appgame.download.DownloadTask;
import com.jiubang.ggheart.appgame.download.ServiceCallbackDownload;
import com.jiubang.ggheart.apps.appfunc.controler.SwitchControler;
import com.jiubang.ggheart.apps.desks.dock.DockUtil;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStoreOperatorUtil;
import com.jiubang.ggheart.common.controler.CommonControler;
import com.jiubang.ggheart.common.controler.InvokeLockControler;
import com.jiubang.ggheart.common.password.PasswordActivity.ActionResultCallBack;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.components.advert.AdvertConstants;
import com.jiubang.ggheart.components.folder.advert.FolderAdController;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.DataProvider;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.IItemType;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.StatisticsAppsInfoData;
import com.jiubang.ggheart.data.statistics.StatisticsData;
import com.jiubang.ggheart.iconconfig.AppIconConfigController;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.plugin.mediamanagement.MediaPluginFactory;
import com.jiubang.ggheart.plugin.mediamanagement.inf.AppFuncContentTypes;
import com.jiubang.ggheart.recommend.localxml.XmlRecommendedApp;
import com.jiubang.ggheart.recommend.localxml.XmlRecommendedAppInfo;

/**
 * 应用启动模块 1.统一做桌面所有Activitiy跳转的工作 2.过滤特殊应用的处理，如内部事件等 3.对外层提供启动监听
 * 
 * @author yuankai
 * @version 1.0
 */
// CHECKSTYLE:OFF
public class AppInvoker {
	
	// 定义从哪里触发的启动
	public static final int TYPE_UNKNOW = 100; // 未知
	public static final int TYPE_APPDRAWER = 101; // 功能表启动的程序
	public static final int TYPE_SCREEN = 102; // 屏幕启动的程序
	
	public static boolean sIsClickFromDockBrowser;
	
	public interface IAppInvokerListener {
		void onInvokeApp(Intent intent);
	}

	private Activity mActivity;
	private ArrayList<IAppInvokerListener> mListener = new ArrayList<IAppInvokerListener>();

	//store the command list
	private Map<String, ICommand> mCommandsMap = new HashMap<String, ICommand>();
	/**
	 * 构造方法
	 * 
	 * @param messageSender
	 *            消息发送接口
	 * @param appmanagerFacade
	 *            后台启动接口
	 * @param activity
	 *            上下文
	 * @throws IllegalArgumentException
	 *             参数为空时抛的异常
	 */
	public AppInvoker(Activity activity, IAppInvokerListener... listeners) {
		mActivity = activity;
		for (IAppInvokerListener listener : listeners) {
			mListener.add(listener);
		}
	}

	/**
	 * 设置回调观察者
	 * 
	 * @param listener
	 */
	public void addListener(IAppInvokerListener listener) {
		mListener.add(listener);
	}

	public void removeListener(IAppInvokerListener listener) {
		mListener.remove(listener);
	}

	/**
	 * 通过Intent启动一个应用
	 * 
	 * @param launchIntent
	 *            对应的Intent
	 * @return 是否启动成功
	 */
	public boolean invokeApp(final Intent launchIntent) {
		return invokeApp(launchIntent, null, null);
	}
	
	public boolean invokeApp(final Intent launchIntent, final Rect rect, boolean[] result) {
		return invokeApp(launchIntent, rect, result, TYPE_UNKNOW);
	}

	/**
	 * 通过Intent启动一个应用, 并加入最近打开
	 * 
	 * @param launchIntent
	 *            对应的Intent
	 * @param rect
	 *            intent 对应的图标所在的位置
	 * @param from
	 * 			  从哪里触发的启动程序  
	 * @return 是否启动成功
	 */
	public boolean invokeApp(final Intent launchIntent, final Rect rect, final boolean[] result, final int from) {
		// 保护
		if (launchIntent == null) {
			return false;
		}
		final Intent actionIntent = new Intent(launchIntent);
		boolean ret = false;
		// launchIntent.addFlags改变了传过来的intent，如果是快捷方式，
		// 原来的intent和改变后的比较会不同，导致向 sysshortcut多加了一个
		// launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		actionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		CommonControler controler = CommonControler.getInstance(mActivity); // 去除推荐应用new标识
		// modify begin by rongjinsong 2013-09-04 移到启动app后
//		controler.recommendAppBeOpen(launchIntent);
		AppDataEngine engine = AppDataEngine.getInstance(mActivity);
		AppItemInfo info = engine.getAppItem(launchIntent);
		if (info != null) {
			info.setIsNewApp(false);
		}
//		
//		//游戏文件夹激活统计
//		if (info != null) {
//			PreferencesManager preferencesManager = new PreferencesManager(
//					ApplicationProxy.getContext(), IPreferencesIds.FOLDER_AD_PREFERENCES,
//					Context.MODE_PRIVATE);
//			String key = info.getAppPackageName() + FolderAdController.INSTALL_TIME;
//			long installTime = preferencesManager.getLong(key, -1);
//			if (installTime != -1) {
//				GuiThemeStatistics.folderAdStaticData(info.getAppPackageName(), "k001", 1);
//				preferencesManager.remove(key);
//			}
//		}
		//游戏文件夹激活统计
		//modify end rongjinsong
		
		if (SettingProxy.getDeskLockSettingInfo().mAppLock && controler.isLockedApp(launchIntent)) {
			//启动应用锁
			final InvokeLockControler lockControler = InvokeLockControler.getInstance(mActivity);
			lockControler.startLockAction(InvokeLockControler.ACTION_ID_APPLOCK, new ActionResultCallBack() {
				
						@Override
						public void onUnlockSuccess(int actionId) {
							lockControler.ignorePkg(launchIntent);
							doAndFilterApp(rect, result, from, actionIntent);
						}

						@Override
						public void onUnlockFail(int actionId) {
							// TODO Auto-generated method stub

						}
					}, mActivity,
					info != null && info.mIcon != null ? ThumbnailManager.getInstance(mActivity)
							.getParcelableBitmap(info.mIcon.getBitmap()) : null, info != null
							? info.mTitle
							: null);
		} else {
			ret = doAndFilterApp(rect, result, from, actionIntent);
		}
		return ret;
	}

	private boolean doAndFilterApp(final Rect rect, boolean[] result,
			final int from, Intent actionIntent) {
		boolean ret;
		// 自定义的action返回false
		if (invokeFilter(actionIntent, from)) {
			if (result != null) {
				result[0] = true;
				return true;
			}
			return false;
		}

		ret = doInvoke(actionIntent, rect);
//		CommonControler controler = CommonControler.getInstance(mActivity); // 去除推荐应用new标识
//		controler.recommendAppBeOpen(actionIntent);
		AppDataEngine engine = AppDataEngine.getInstance(mActivity);
		AppItemInfo info = engine.getAppItem(actionIntent);
		
		//游戏文件夹激活统计
		if (info != null) {
			PreferencesManager preferencesManager = new PreferencesManager(
					ApplicationProxy.getContext(), IPreferencesIds.FOLDER_AD_PREFERENCES,
					Context.MODE_PRIVATE);
			String key = info.getAppPackageName() + FolderAdController.INSTALL_TIME;
			long installTime = preferencesManager.getLong(key, -1);
			if (installTime != -1) {
				GuiThemeStatistics.folderAdStaticData(info.getAppPackageName(), "k001", 1);
				preferencesManager.remove(key);
			}
		}
		//游戏文件夹激活统计
		
		//add by licanhui 15屏广告8小时请求
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_ADVERT_BUSINESS,
				IScreenAdvertMsgId.SET_ADVERT_APP_IS_OPEN, -1, actionIntent);
		//end by licanhui
		//update by caoyaming 2014-04-03
		AppIconConfigController.getInstance(mActivity).cancelUnReadCount(info);
		//end update by caoyaming 2014-04-03
		
		return ret;
	}

	/**
	 * 通过DBItemInfo启动一个应用， 并加入最近打开
	 * 
	 * @param dbItemInfo
	 *            对应的数据结构
	 * @return 是否启动成功
	 */
	public boolean invokeApp(final AppItemInfo dbItemInfo) {
		if (dbItemInfo == null || dbItemInfo.mIntent == null) {
			return false;
		}

		boolean ret = false;
		// 自定义的Action，返回false
		if (invokeFilter(dbItemInfo.mIntent)) {
			return false;
		}

		ret = doInvoke(dbItemInfo);
		return ret;
	}

	/**
	 * 带回调的启动应用
	 * 
	 * @param requestCode
	 *            请求码
	 * @param launchIntent
	 *            对应的Intent
	 * @return 是否启动成功
	 */
	public boolean invokeAppForResult(final int requestCode, final Intent launchIntent) {
		if (launchIntent == null) {
			return false;
		}

		boolean ret = false;
		// TODO 带回调的启动是否要加入到统计和最新打开之中？？
		try {
			mActivity.startActivityForResult(launchIntent, requestCode);
			ret = true;
		} catch (ActivityNotFoundException e) {
			DeskToast.makeText(mActivity, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
		} catch (SecurityException e) {
			DeskToast.makeText(mActivity, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
		} catch (Throwable e) {
			DeskToast.makeText(mActivity, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
		}
		return ret;
	}

	private boolean doInvoke(final Intent intent, final Rect bounds) {
		Intent copyIntent = intent;
		boolean ret = false;
		if (bounds != null) {
			copyIntent = new Intent(intent);
			try {
				// API LEVEL = 7
				copyIntent.setSourceBounds(bounds);
			} catch (NoSuchMethodError e) {
				e.printStackTrace();
			}
		}

		try {
			mActivity.startActivity(copyIntent);
			ret = true;
		} catch (ActivityNotFoundException e) {
			// NOTE判断电子市场
			String pkgString = null;
			if (copyIntent != null && copyIntent.getComponent() != null) {
				pkgString = copyIntent.getComponent().getPackageName();
			}
			if (null != pkgString && pkgString.equals("com.android.vending")) {
				PackageManager pm = ApplicationProxy.getContext().getPackageManager();
				try {
					Intent intent2 = pm.getLaunchIntentForPackage(pkgString);
					if (null != intent2) {
						// NOTE:修改DB
						String oldMarketIntentStr = ConvertUtils.intentToString(intent);
						String newMarketIntentStr = ConvertUtils.intentToString(intent2);

						DataProvider dataProvider = DataProvider.getInstance(ApplicationProxy.getContext());
						dataProvider.replaceOldMarketToNewMarket(oldMarketIntentStr,
								newMarketIntentStr);

						mActivity.startActivity(intent2);
					}
				} catch (Throwable e2) {
				}
			}

			DeskToast.makeText(mActivity, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
		} catch (SecurityException e) {
			boolean showToast = true;
			try {
				if (copyIntent != null && copyIntent.getAction() != null) {
					/*
					 * 在galaxyS 3机子上创建的直接拨号快捷方式，action为
					 * android.intent.action.CALL_PRIVILEGED,
					 * 但是android.permission
					 * .CALL_PRIVILEGED权限只允许系统app使用，所以要copy一份intent出来，改变action
					 */
					if (copyIntent.getAction().equals(ICustomAction.ACTION_CALL_PRIVILEGED)) {
						Intent intent2 = new Intent(copyIntent);
						intent2.setAction(ICustomAction.ACTION_CALL);
						mActivity.startActivity(intent2);
						showToast = false;
					}
				}
			} catch (Throwable e2) {
			}
			if (showToast) {
				DeskToast.makeText(mActivity, R.string.activity_not_found, Toast.LENGTH_SHORT)
						.show();
			}
		} catch (Exception e) {
			DeskToast.makeText(mActivity, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
		}

		// add by huyong 2011-12-27 for all appsinfo
		if (ret) {
			// 防止ANR问题
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					addAppActiveCount(intent);
					AppItemInfo info = AppDataEngine.getInstance(mActivity).getAppItem(intent);
					if (info != null) {
						info.setClickTime(mActivity, System.currentTimeMillis());
					}
				}
			});
			thread.start();
		}
		// add by huyong 2011-12-27 for all appsinfo end

		
		// 通知
		if (null != mListener) {
			for (IAppInvokerListener listener : mListener) {
				listener.onInvokeApp(intent);
			}
		}
		return ret;
	}

	private void addAppActiveCount(final Intent intent) {
		AppItemInfo info = AppDataEngine.getInstance(mActivity).getAppItem(intent);
		if (info != null) {
			info.addActiveCount(mActivity, 1);
		}
		StatisticsAppsInfoData.addAppInfoClickedCount(intent, mActivity);
	}

	private boolean doInvoke(AppItemInfo itemInfo) {
		if (null == itemInfo || null == itemInfo.mIntent) {
			return false;
		}

		// 若是文件夹或widget，则不启动
		if (IItemType.ITEM_TYPE_USER_FOLDER == itemInfo.mID
				|| IItemType.ITEM_TYPE_APP_WIDGET == itemInfo.mID) {
			return false;
		}
		return doInvoke(itemInfo.mIntent, null);
	}
	
	private boolean invokeFilter(final Intent launchIntent) {
		return invokeFilter(launchIntent, TYPE_UNKNOW);
	}

	/**
	 * 过滤启动的Intent
	 * 
	 * @param launchIntent
	 *            被过滤的Intent
	 * @return 是否被过滤，如果是，外部不需要再启动
	 */
	private boolean invokeFilter(final Intent launchIntent, final int from) {
		// 此处不用判空，提高效率
		final String action = launchIntent.getAction();

		// 判断是否为中国用户，作为下载推荐应用的参数 add by chenguanyu
		boolean isCnUser = GoAppUtils.isCnUser(mActivity);

		if (action == null) {
			return false;
		}
		
		// 浏览器类应用统计启动次数
		PreferencesManager ps = new PreferencesManager(mActivity);
		int openCount = ps.getInt(IPreferencesIds.PREFERENCES_OPEN_BROWSER_COUNT, 0);
		ComponentName cn = launchIntent.getComponent();
		if (cn != null) {
			String launchPackage = cn.getPackageName();
			if (launchPackage != null && launchPackage.equals(LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
				FunctionPurchaseManager.getInstance(mActivity).startPayPage("4");
				return true;
			} else if (40 > openCount && !sIsClickFromDockBrowser) {
				if (AppUtils.isBrowser(mActivity, launchPackage) && Machine.isNetworkOK(mActivity)) {
					ps.putInt(IPreferencesIds.PREFERENCES_OPEN_BROWSER_COUNT, ++openCount);
					ps.commit();
					// 
					GuiThemeStatistics
							.guiStaticData(51, null, "browser", 1, "-1", "-1", "-1", "-1");
				}
			}
		}
		sIsClickFromDockBrowser = false;
		
		Log.d("action", ""+action);	 
		if (mCommandsMap.size()==0) {
			//init the command list
			ModuleOperation mo = new ModuleOperation(launchIntent, mListener,  from,  isCnUser);
			this.initCommandList(mo);
		} 
		
		boolean result = SpecialAppManager.getInstance().invoke(launchIntent, mListener, from);
		if (result) {
			return result;
		}
		//invoke the command which stored in hashmap before
		ICommand command=mCommandsMap.get(action);
		if (command!=null) {
			CommandParam mo = new CommandParam(launchIntent, mListener,  from,  isCnUser);
			command.setmCommandParam(mo);
			result = command.execute(mActivity);
			return result;
		}
		
			
		 if (DockUtil.isDockSms(launchIntent)) {
			if (Machine.IS_SDK_ABOVE_KITKAT) {
				PackageManager pm = mActivity.getPackageManager();
				List<ResolveInfo> infos = pm.queryIntentActivities(launchIntent, 0);
		        if (infos != null && infos.size() > 0) {
		            return false;
		        } else {
//					launchIntent.setPackage(pkg);
//		        	String pkg = Telephony.Sms.getDefaultSmsPackage(mActivity);
		        	try {
		        		Intent intent = pm.getLaunchIntentForPackage(PackageName.GOOGLE_TALK_ANDROID_TALK);
						mActivity.startActivity(intent);
					} catch (Exception e) {
						// TODO: handle exception
					}
//		        	if (pkg != null) {
//						Intent intent = pm.getLaunchIntentForPackage(pkg);
//						mActivity.startActivity(intent);
//					}
//					launchIntent.setAction(Intent.ACTION_MAIN);
//					launchIntent.setType("vnd.android-dir/mms-sms");
//					launchIntent.setType(null);
					return true;
				}
			}
		} else {
			XmlRecommendedAppInfo info = XmlRecommendedApp.getRecommededAppInfoByAction(action);
			if (info != null) {
				// 统计,走15屏广告统计
				StatisticsData.updateAppClickData(mActivity.getApplicationContext(),
						info.mPackagename,AdvertConstants.ADVERT_STATISTICS_TYPE, info.mMapId, info.mId);
				
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_ADVERT_BUSINESS,
						IScreenAdvertMsgId.REQUEST_ADVERT_STAT_CLICK_ACTION, -1, info.mPackagename,
						info.mId, info.mClickurl, info.mMapId);
				
				if (info.mActType == XmlRecommendedAppInfo.GOTO_MARKET) {
					if (GoAppUtils.isMarketExist(mActivity)) {
						GoAppUtils.gotoMarket(mActivity, MarketConstant.APP_DETAIL
								+ info.mDownloadUrl);
					} else {
						AppUtils.gotoBrowser(mActivity, MarketConstant.BROWSER_APP_DETAIL
								+ info.mDownloadUrl);
					}
				} else if (info.mActType == XmlRecommendedAppInfo.GOTO_BROWSER) {
					if (GotoMarketIgnoreBrowserTask.isRedirectUrl(info.mDownloadUrl)) {
						GotoMarketIgnoreBrowserTask.startExecuteTask(mActivity, info.mDownloadUrl);
					} else {
						GoAppUtils.gotoBrowserInRunTask(mActivity, info.mDownloadUrl);
//						startBrowser(mActivity, info.mDownloadUrl);
					}
				} else {
					showDownLoadConfirmDialog(info);
				}
				return true;
			}
		}
		/*
		 * else if (ICustomAction.ACTION_GOOGLE_URI.equals(action)) { try {
		 * PackageManager pm = mActivity.getPackageManager(); Uri uri =
		 * Uri.parse("http://www.google.com"); Intent intent = new
		 * Intent(Intent.ACTION_VIEW, uri); //试图拿默认浏览器 ResolveInfo info =
		 * pm.resolveActivity(intent, 0); String pkg =
		 * info.activityInfo.applicationInfo.packageName;
		 * 
		 * //"android".equals(pkg) = true 意思是用户没设置默认浏览器 ResolveInfo resolveInfo
		 * = ("android".equals(pkg)) ? pm.queryIntentActivities(intent,
		 * 0).get(0) : info; String resolveInfoPkg =
		 * resolveInfo.activityInfo.applicationInfo.packageName; String
		 * resolveInfoCls = resolveInfo.activityInfo.name; Intent newIntent =
		 * new Intent(); ComponentName componentName = new
		 * ComponentName(resolveInfoPkg, resolveInfoCls);
		 * newIntent.setComponent(componentName);
		 * mActivity.startActivity(newIntent); } catch (Exception e) {
		 * DeskToast.makeText(mActivity, "Fail to start default browser",
		 * Toast.LENGTH_SHORT).show(); } return true; }
		 */

		// TODO 此处可添加任意多的过滤项
		// 过滤资源管理插件图标的响应事件，使用包名判断
		ComponentName componentName = launchIntent.getComponent();
		if (componentName != null) {
			String pkgName = componentName.getPackageName();
			if (pkgName.equals(PackageName.MEDIA_PLUGIN)) {
				if (MediaPluginFactory.isMediaPluginExist(ApplicationProxy.getContext())) {
					switch (AppFuncContentTypes.sType_for_setting) {
						case AppFuncContentTypes.IMAGE :
							SwitchControler.getInstance(mActivity).showMediaManagementImageContent();
							break;
						case AppFuncContentTypes.MUSIC :
							SwitchControler.getInstance(mActivity).showMediaManagementMusicContent();
							break;
						case AppFuncContentTypes.VIDEO :
							SwitchControler.getInstance(mActivity).showMediaManagementVideoContent();
							break;
						default :
							SwitchControler.getInstance(mActivity).showMediaManagementImageContent();
							break;
					}
				}
//				CommonControler.getInstance(mActivity).recommendAppBeOpen(launchIntent);
				return true;
			}
			
			else if (pkgName.equals(PackageName.MULTIPLEWALLPAPER_PKG_NAME)) {
				// 在桌面启动多屏多壁纸，则启动壁纸设置界面；如果第三方桌面则启动引导页
				if (GoAppUtils.isAppExist(mActivity, PackageName.MULTIPLEWALLPAPER_PKG_NAME)) {
					Intent intent = new Intent();
					intent.setClassName(AppUtils.getAppContext(mActivity, pkgName),
							"com.go.multiplewallpaper.MultipleWallpaperSettingActivity");
					intent.putExtra("isgolauncher", true);
					mActivity.startActivity(intent);
					return true;
				}
			}
			
		}
		return false;
	}
	
	private void showDownLoadConfirmDialog(final XmlRecommendedAppInfo info) {
		if (info == null) {
			return;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
//		String msg = mActivity.getString(R.string.recommended_download_dialog_msg);
//		msg = msg.replace("%s", mActivity.getString(info.mTitle));
		String detail = mActivity.getString(info.mDetail);
		builder.setMessage(detail).setTitle(R.string.recommended_download_dialog_title)
				.setCancelable(true)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						final String appName;
						if (info.mTitle > 0) {
							appName = mActivity.getString(info.mTitle);
						} else {
							appName = info.mPackagename;
						}
						//						IDownloadService mDownloadController = ApplicationProxy.getApplication()
						//								.getDownloadController();
						ServiceCallbackDownload.ServiceCallbackRunnable runnable = new ServiceCallbackDownload.ServiceCallbackRunnable() {
							@Override
							public void run() {
								boolean downloading = false;
								long trueTaskId = 0;
								if (mDownloadController == null) {
									return;
								}
								Map<Long, DownloadTask> downloadmap;
								try {
									downloadmap = mDownloadController
											.getDownloadConcurrentHashMap();
									for (DownloadTask task : downloadmap.values()) {
										if (info.mDownloadUrl.equals(task.getDownloadUrl())) {
											downloading = true;
											trueTaskId = task.getId();
											break;
										}
									}
									if (!downloading) {
//										StatisticsData.updateAppClickData(
//												mActivity.getApplicationContext(),
//												info.mPackagename);
										long taskId = System.currentTimeMillis();
										String fileName = appName.trim();
										String downloadUrl = info.mDownloadUrl.trim();
										String customDownloadFileName = taskId + "_"
												+ info.mPackagename + ".apk";
										String savePath = GoStoreOperatorUtil.DOWNLOAD_DIRECTORY_PATH
												+ customDownloadFileName;
										DownloadTask task = new DownloadTask(taskId, downloadUrl,
												fileName, savePath);
										task.setIsApkFile(true);
										taskId = mDownloadController.addDownloadTask(task);
										if (taskId != -1) {
											// 添加默认的下载监听器
											mDownloadController.addDownloadTaskListenerByName(
													taskId, DefaultDownloadListener.class.getName());
											mDownloadController.startDownload(taskId);
										}
									} else {
										mDownloadController.startDownload(trueTaskId);
									}
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						};
						ServiceCallbackDownload.callbackDownload(mActivity, runnable);
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.create().show();
	}
		
	/**
	 * init the command list
	 * @param ModuleOperation
	 */
	private void initCommandList(ModuleOperation mo){
		/*
		 * the following command is related to relevant function, one command contains one operation
		 * and the operation are invoked by method name
		 */
		
		ICommand openMenu = new MessageCommand(mo,ModuleOperation.MOPENFUNCTIONMENU);
		ICommand openNothing = new MessageCommand(mo,ModuleOperation.MOPENNOTHING); 
		ICommand openStatusBar = new MessageCommand(mo,ModuleOperation.MOPENSHOWHIDESTATUSBAR); 
		ICommand openMainScreenOrPreview = new MessageCommand(mo,ModuleOperation.MOPENSHOWMAINSCREENORPREVIEW); 
		ICommand openMainScreen = new MessageCommand(mo,ModuleOperation.MOPENSHOWMAINSCREEN); 
		ICommand showPreview = new MessageCommand(mo,ModuleOperation.MOPENSHOWPREVIEW); 
		ICommand showMenu = new MessageCommand(mo,ModuleOperation.MOPENSHOWMENU);
		ICommand openExpendBar = new MessageCommand(mo,ModuleOperation.MOPENEXPENDBAR); 
		ICommand openSpecAppGoTheme = new MessageCommand(mo,ModuleOperation.MOPENSPECIALAPPGOTHEME); 
		ICommand openSpecAppGoStore = new MessageCommand(mo,ModuleOperation.MOPENSPECIALAPPGOSTORE); 
		ICommand openSpecAppGoWidget = new MessageCommand(mo,ModuleOperation.MOPENSPECIALAPPGOWIDGET); 
		ICommand showPref = new MessageCommand(mo,ModuleOperation.MOPENSHOWPREFERENCES); 
		ICommand showFuncMenuForLauncher = new MessageCommand(mo,ModuleOperation.MOPENSHOWFUNCTIONMENUFORLAUNCHER); 
		ICommand showLockerSetting = new MessageCommand(mo,ModuleOperation.MOPENSHOWLOCKERSETTING); 
		ICommand openScreenGuard = new MessageCommand(mo,ModuleOperation.MOPENSCREENGUARD); 
		ICommand showDock = new MessageCommand(mo,ModuleOperation.MOPENSHOWDOCK);
		ICommand openRecommList = new MessageCommand(mo,ModuleOperation.MOPENSHOWRECOMMENDLIST); 
		ICommand openRecommGame = new MessageCommand(mo,ModuleOperation.MOPENSHOWRECOMMENDGAME); 
		ICommand openRecommCenter = new MessageCommand(mo,ModuleOperation.MOPENSHOWRECOMMENDCENTER); 
		ICommand openRecommGoSMS = new MessageCommand(mo,ModuleOperation.MOPENRECOMMANDGOSMS); 
		ICommand openRecommGoPowerMaster = new MessageCommand(mo,ModuleOperation.MOPENRECOMMANDGOPOWERMASTER); 
		ICommand openRecommGoTaskManager = new MessageCommand(mo,ModuleOperation.MOPENRECOMMANDGOTASKMANAGER); 
		ICommand openRecommGoKeyBoard = new MessageCommand(mo,ModuleOperation.MOPENRECOMMANDGOKEYBOARD); 
		ICommand openRecommGoLocker = new MessageCommand(mo,ModuleOperation.MOPENRECOMMANDGOLOCKER); 
		ICommand openRecommGoBackUp = new MessageCommand(mo,ModuleOperation.MOPENRECOMMANDGOBACKUP); 
		ICommand openRecommGoWeather = new MessageCommand(mo,ModuleOperation.MOPENRECOMMANDGOWEATHER); 
		ICommand openDiyGesture = new MessageCommand(mo,ModuleOperation.MOPENSHOWDIYGESTURE); 
		ICommand openHandBook = new MessageCommand(mo,ModuleOperation.MOPENSHOWGOHANDBOOK); 
		ICommand openPhoto = new MessageCommand(mo,ModuleOperation.MOPENSHOWPHOTO); 
		ICommand openMusic = new MessageCommand(mo,ModuleOperation.MOPENSHOWMUSIC); 
		ICommand openVideo = new MessageCommand(mo,ModuleOperation.MOPENSHOWVIDEO); 
		ICommand openRecommLockScreen = new MessageCommand(mo,ModuleOperation.MOPENRECOMMANDLOCKSCREEN); 
		ICommand openRecommMediaPlugin = new MessageCommand(mo,ModuleOperation.MOPENRECOMMANDMEDIAPLUGIN); 
		ICommand openFreeThemeIcon = new MessageCommand(mo,ModuleOperation.MOPENFREETHEMEICON);
		ICommand openAdvert = new MessageCommand(mo,ModuleOperation.MOPENSCREENADVERT);
		
		
		//the following action can be found in 'go app' folder and go shortcut
		mCommandsMap.put(ICustomAction.ACTION_SHOW_FUNCMENU, openMenu);
		mCommandsMap.put(ICustomAction.ACTION_SHOW_HIDE_STATUSBAR, openStatusBar);
		mCommandsMap.put(ICustomAction.ACTION_SHOW_MAIN_OR_PREVIEW, openMainScreenOrPreview);
		mCommandsMap.put(ICustomAction.ACTION_SHOW_MAIN_SCREEN, openMainScreen);
		mCommandsMap.put(ICustomAction.ACTION_SHOW_PREVIEW, showPreview);
		mCommandsMap.put(ICustomAction.ACTION_SHOW_MENU, showMenu);
		mCommandsMap.put(ICustomAction.ACTION_SHOW_EXPEND_BAR, openExpendBar);
		mCommandsMap.put(ICustomAction.ACTION_FUNC_SPECIAL_APP_GOTHEME, openSpecAppGoTheme);
		mCommandsMap.put(ICustomAction.ACTION_FUNC_SPECIAL_APP_GOSTORE, openSpecAppGoStore);
		mCommandsMap.put(ICustomAction.ACTION_FUNC_SPECIAL_APP_GOWIDGET, openSpecAppGoWidget);
		mCommandsMap.put(ICustomAction.ACTION_SHOW_PREFERENCES, showPref);
		mCommandsMap.put(ICustomAction.ACTION_SHOW_FUNCMENU_FOR_LAUNCHER_ACITON, showFuncMenuForLauncher);
		mCommandsMap.put(ICustomAction.ACTION_RECOMMAND_GOLOCKER_DOWNLOAD, openRecommGoLocker);
		mCommandsMap.put(ICustomAction.ACTION_SHOW_DOCK, showDock);
		mCommandsMap.put(ICustomAction.ACTION_ENABLE_SCREEN_GUARD, openScreenGuard);
		mCommandsMap.put(ICustomAction.ACTION_RECOMMAND_GOSMS_DOWNLOAD, openRecommGoSMS);
		mCommandsMap.put(ICustomAction.ACTION_RECOMMAND_GOPOWERMASTER_DOWNLOAD, openRecommGoPowerMaster);
		mCommandsMap.put(ICustomAction.ACTION_RECOMMAND_GOTASKMANAGER_DOWNLOAD, openRecommGoTaskManager);
		mCommandsMap.put(ICustomAction.ACTION_RECOMMAND_GOKEYBOARD_DOWNLOAD, openRecommGoKeyBoard);
		mCommandsMap.put(ICustomAction.ACTION_RECOMMAND_GOBACKUP_DOWNLOAD, openRecommGoBackUp);
		mCommandsMap.put(ICustomAction.ACTION_RECOMMAND_GOWEATHEREX_DOWNLOAD, openRecommGoWeather);
		mCommandsMap.put(ICustomAction.ACTION_SHOW_DIYGESTURE, openDiyGesture);
		mCommandsMap.put(ICustomAction.ACTION_SHOW_GO_HANDBOOK, openHandBook);
		mCommandsMap.put(ICustomAction.ACTION_SHOW_PHOTO, openPhoto);
		mCommandsMap.put(ICustomAction.ACTION_SHOW_MUSIC, openMusic);
		mCommandsMap.put(ICustomAction.ACTION_SHOW_VIDEO, openVideo);
		mCommandsMap.put(ICustomAction.ACTION_RECOMMAND_LOCKSCREEN_DOWNLOAD, openRecommLockScreen);
		mCommandsMap.put(ICustomAction.ACTION_SHOW_RECOMMENDCENTER, openRecommCenter);
		mCommandsMap.put(ICustomAction.ACTION_FUNC_SHOW_RECOMMENDCENTER, openRecommCenter);
		mCommandsMap.put(ICustomAction.ACTION_RECOMMAND_MEDIA_PLUGIN_DOWNLOAD, openRecommMediaPlugin);
		
		//the following action are not found any way to go
		mCommandsMap.put(ICustomAction.ACTION_FREE_THEME_ICON, openFreeThemeIcon);
		mCommandsMap.put(ICustomAction.ACTION_SCREEN_ADVERT, openAdvert);
		mCommandsMap.put(ICustomAction.ACTION_SHOW_LOCKER_SETTING, showLockerSetting);
		mCommandsMap.put(ICustomAction.ACTION_FUNC_SHOW_RECOMMENDLIST, openRecommList);
		mCommandsMap.put(ICustomAction.ACTION_FUNC_SHOW_RECOMMENDGAME, openRecommGame);
		mCommandsMap.put(ICustomAction.ACTION_NONE, openNothing);
		mCommandsMap.put(ICustomAction.ACTION_BLANK, openNothing);
	}
}
