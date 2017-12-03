package com.go.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.util.device.Machine;
import com.go.util.log.LogConstants;
import com.go.util.market.MarketConstant;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.diy.INotificationId;
import com.jiubang.ggheart.components.advert.untils.NoAdvertCheckReceiver;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 应用相关的工具类
 * @author yangguanxiang
 *
 */
public class AppUtils {
	public static final int NEW_MARKET_VERSION_CODE = 8006027;    
	private static final int ONT_TIME_COUNT = 30;
	public static final int WIDGET_CLICK_NONE = 0;  //widget点击事件不做处理
	public static final int WIDGET_CLICK_RETURN = 1; //widget点击事件拦截
	public static final int WIDGET_CLICK_PICK_APP_WIDGET = 2; //widget点击事件打开go系统小部件并拦截
	
	/**
	 * 检查是安装某包
	 * 
	 * @param context
	 * @param packageName
	 *            包名
	 * @return
	 */
	public static boolean isAppExist(final Context context, final String packageName) {
		if (context == null || packageName == null) {
			return false;
		}

		boolean result = false;
		try {
			// context.createPackageContext(packageName,
			// Context.CONTEXT_IGNORE_SECURITY);
			context.getPackageManager().getPackageInfo(packageName,
					PackageManager.GET_SHARED_LIBRARY_FILES);
			result = true;
		} catch (NameNotFoundException e) {
			// TODO: handle exception
			result = false;
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	public static boolean isAppExist(final Context context, final Intent intent) {
		List<ResolveInfo> infos = null;
		try {
			infos = context.getPackageManager().queryIntentActivities(intent, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (infos != null) && (infos.size() > 0);
	}

	/**
	 * 安全启动activity，捕获异常
	 * 
	 * @param context
	 * @param intent
	 *            启动的intent
	 * @return
	 */
	public static void safeStartActivity(final Context context, Intent intent) {
		try {
			if (context != null) {
				context.startActivity(intent);
			}
		} catch (ActivityNotFoundException e) {
			Log.e(LogConstants.HEART_TAG, "saveStartActivity err " + e.getMessage());
		} catch (SecurityException e) {
			Log.e(LogConstants.HEART_TAG, "saveStartActivity err " + e.getMessage());
		}
	}

	/**
	 * 安全启动activity for result，捕获异常
	 * 
	 * @param context
	 * @param intent
	 *            启动的intent
	 * @return
	 */
	public static void safeStartActivityForResult(final Activity activity, Intent intent,
			int requestCode) {
		try {
			if (activity != null) {
				activity.startActivityForResult(intent, requestCode);
			}
		} catch (ActivityNotFoundException e) {
			Log.e(LogConstants.HEART_TAG, "saveStartActivityForResult err " + e.getMessage());
		} catch (SecurityException e) {
			Log.e(LogConstants.HEART_TAG, "saveStartActivityForResult err " + e.getMessage());
		}
	}

	/**
	 * 获取app包信息
	 * 
	 * @param context
	 * @param packageName
	 *            包名
	 * @return
	 */
	public static PackageInfo getAppPackageInfo(final Context context, final String packageName) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(packageName, 0);
		} catch (Exception e) {
			info = null;
			e.printStackTrace();
		}
		return info;
	}
	
	/**
	 * 根据包名获取应用的uid
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static int getUidByPkgName(final Context context, final String packageName) {
		int uid = 0;
		try {
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_ACTIVITIES);
			uid = info.uid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uid;
	}
	
	/**
	 * 获取文件属性
	 * @param fileName
	 * @return
	 */
	public static String getFileOption(final String fileName) {
		String command = "ls -l " + fileName;
		StringBuffer sbResult = new StringBuffer();
		try {
			java.lang.Process proc = Runtime.getRuntime().exec(command);
			InputStream input = proc.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String tmpStr = null;
			while ((tmpStr = br.readLine()) != null) {
				sbResult.append(tmpStr);
			}
			if (input != null) {
				input.close();
			}
			if (br != null) {
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sbResult.toString();
	}

	/**
	 * 手机上是否有电子市场
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMarketExist(final Context context) {
		return isAppExist(context, MarketConstant.PACKAGE);
	}

	/**
	 * 服务是否正在运行
	 * 
	 * @param context
	 * @param packageName
	 *            包名
	 * @param serviceName
	 *            服务名
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String packageName, String serviceName) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		return isServiceRunning(activityManager, packageName, serviceName);
	}

	public static boolean isServiceRunning(ActivityManager activityManager, String packageName,
			String serviceName) {
		List<RunningServiceInfo> serviceTasks = activityManager.getRunningServices(Integer.MAX_VALUE);
		int sz = null == serviceTasks ? 0 : serviceTasks.size();
		for (int i = 0; i < sz; i++) {
			RunningServiceInfo info = serviceTasks.get(i);
			if (null != info && null != info.service) {
				final String pkgName = info.service.getPackageName();
				final String className = info.service.getClassName();

				if (pkgName != null && pkgName.contains(packageName) && className != null
						&& className.contains(serviceName)) {
					Log.i("Notification", "package = " + info.service.getPackageName()
							+ " class = " + info.service.getClassName());
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 发送通知栏信息(点击后打开Activity)
	 * 
	 * @param context
	 * @param intent
	 * @param iconId
	 *            图标id
	 * @param tickerText
	 *            通知栏显示的信息
	 * @param title
	 *            展开通知栏后显示的标题
	 * @param content
	 *            展开通知栏显示的文字
	 * @param notificationId
	 *            通知消息id {@link INotificationId}, 全局唯一
	 */
	public static void sendNotification(Context context, Intent intent, int iconId,
			CharSequence tickerText, CharSequence title, CharSequence content, int notificationId) {
		sendNotification(context, intent, iconId, tickerText, title, content, notificationId,
				Notification.FLAG_AUTO_CANCEL);
	}

	/**
	 * 发送通知栏信息(点击后打开Activity)
	 * 
	 * @param context
	 * @param intent
	 * @param iconId
	 *            图标id
	 * @param tickerText
	 *            通知栏显示的信息
	 * @param title
	 *            展开通知栏后显示的标题
	 * @param content
	 *            展开通知栏显示的文字
	 * @param notificationId
	 *            通知消息id {@link INotificationId}, 全局唯一
	 * @param flags
	 * 			  标志
	 */
	public static void sendNotification(Context context, Intent intent, int iconId,
			CharSequence tickerText, CharSequence title, CharSequence content, int notificationId,
			int flags) {
		sendIconNotification(context, intent, iconId, tickerText, title, content, notificationId,
				flags, null);
	}
	
	// LH TODO:放到对应的模块里面，不要把字符串R.id.等带进来
	/**
	 * 自定义图片发送通知栏信息(点击后打开Activity)
	 * @param context
	 * @param intent
	 * @param iconId
	 * @param tickerText
	 * @param title
	 * @param content
	 * @param notificationId
	 * @param icon 
	 * 			展示图片
	 */
	public static void sendIconNotification(Context context, Intent intent, int iconId,
			CharSequence tickerText, CharSequence title, CharSequence content, int notificationId,
			Bitmap icon) {
		if (icon != null) {
			RemoteViews contentView = new RemoteViews(context.getPackageName(),
					R.layout.msg_center_noitify_content);
			contentView.setTextViewText(R.id.theme_title, title);
			contentView.setTextViewText(R.id.theme_content, content);
			contentView.setImageViewBitmap(R.id.theme_view_image, icon);
			sendIconNotification(context, intent, iconId, tickerText, title, content,
					notificationId, Notification.FLAG_AUTO_CANCEL, contentView);
		} else {
			sendIconNotification(context, intent, iconId, tickerText, title, content,
					notificationId, Notification.FLAG_AUTO_CANCEL, null);
		}

	}
	
	// LH TODO:函数命名有问题，放到GOAppUtils里面去
	public static void sendIconNotification(Context context, Intent intent, int iconId,
			CharSequence tickerText, CharSequence title, CharSequence content, int notificationId,
			int flags, RemoteViews contentView) {
		try {
			Notification notification = new Notification(iconId, tickerText,
					System.currentTimeMillis());
			
			PendingIntent contentIntent = null;
			if (notificationId == INotificationId.GOTO_THEME_PREVIEW
					|| notificationId == INotificationId.GOTO_LOCKERTHEME_PREVIEW) {
				contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				
				//手动清除时发送广播,引导用户购买付费版
				Intent delIntent = new Intent(NoAdvertCheckReceiver.NOADVERT_CHECK_ACTION);
				notification.deleteIntent = PendingIntent.getBroadcast(context, 0, delIntent, 0);
			} else {
				contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
			}
			notification.setLatestEventInfo(context, title, content, contentIntent);
			if (contentView != null) {
				notification.contentIntent = contentIntent;
				notification.contentView = contentView;
			}
			// 设置标志
			notification.flags |= flags;
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(notificationId, notification);
		} catch (Exception e) {
			Log.i(LogConstants.HEART_TAG, "start notification error id = " + notificationId);
		}
	}
	
	/**
	 * 发送通知栏信息(点击后触发广播)
	 * 
	 * @param context
	 * @param intent
	 * @param iconId
	 *            图标id
	 * @param tickerText
	 *            通知栏显示的信息
	 * @param title
	 *            展开通知栏后显示的标题
	 * @param content
	 *            展开通知栏显示的文字
	 * @param notificationId
	 *            通知消息id {@link INotificationId}, 全局唯一
	 */
	public static void sendNotificationBCintent(Context context, Intent intent, int iconId,
			CharSequence tickerText, CharSequence title, CharSequence content, int notificationId, int flag) {
		try {
			PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, intent, flag);

			Notification notification = new Notification(iconId, tickerText,
					System.currentTimeMillis());
			notification.setLatestEventInfo(context, title, content, contentIntent);

			// 点击后自动消失
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(notificationId, notification);
		} catch (Exception e) {
			Log.i(LogConstants.HEART_TAG, "start notification error id = " + notificationId);
		}
	}

	/**
	 * 发送短暂显示的通知栏信息
	 * 
	 * @param context
	 * @param iconId
	 *            　图标id
	 * @param tickerText
	 *            　通知栏显示的信息
	 * @param notificationId
	 *            　通知消息id {@link INotificationId}, 全局唯一
	 */
	public static void sendNotificationDisplaySeconds(Context context, int iconId,
			CharSequence tickerText, int notificationId) {
		sendNotification(context, null, iconId, tickerText, null, null, notificationId);

		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(notificationId);
	}

	/**
	 * 取消指定的ID的Notificaiton
	 * @param context
	 * @param notificationId
	 */
	public static void cancelNotificaiton(Context context, int notificationId) {
		if (context != null) {
			try {
				NotificationManager nm = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				nm.cancel(notificationId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 跳转到Android Market
	 * 
	 * @param uriString
	 *            market的uri
	 * @return 成功打开返回true
	 */
	public static boolean gotoMarket(Context context, String uriString) {
		boolean ret = false;
		Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
		marketIntent.setPackage(MarketConstant.PACKAGE);
		if (context instanceof Activity) {
			marketIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		} else {
			marketIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		try {
			context.startActivity(marketIntent);
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(LogConstants.HEART_TAG, "gotoMarketForAPK error, uri = " + uriString);
			Toast.makeText(context, R.string.no_googlemarket_tip, Toast.LENGTH_SHORT).show();
		}
		return ret;
	}


	/**
	 * 浏览器直接访问uri
	 * 
	 * @param uriString
	 * @return 成功打开返回true
	 */
	public static boolean gotoBrowser(Context context, String uriString) {
		boolean ret = false;
		if (uriString == null) {
			return ret;
		}
		Uri browserUri = Uri.parse(uriString);
		if (null != browserUri) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
			browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				context.startActivity(browserIntent);
				ret = true;
			} catch (ActivityNotFoundException e) {
				Log.i(LogConstants.HEART_TAG, "gotoBrowser error, uri = " + uriString);
			} catch (Exception e) {
				Log.i(LogConstants.HEART_TAG, "gotoBrowser error, uri = " + uriString);
			}
		}
		return ret;
	}
	
	/**
	 * 优先跳转到market，如果失败则转到浏览器
	 * 
	 * @param context
	 * @param marketUrl
	 *            market地址
	 * @param browserUrl
	 *            浏览器地址
	 */
	public static void gotoBrowserIfFailtoMarket(Context context, String marketUrl,
			String browserUrl) {
		boolean toMarket = gotoMarket(context, marketUrl);
		if (!toMarket) {
			gotoBrowser(context, browserUrl);
		}
	}
	
	/** 获取重定向之后的网址信息
	 * HttpClient缺省会自动处理客户端重定向
	 * 即访问网页A后,假设被重定向到了B网页,那么HttpClient将自动返回B网页的内容
	 * 若想取得B网页的地址,就需要借助HttpContext对象,HttpContext实际上是客户端用来在多次请求响应的交互中,保持状态信息的
	 * 可以利用HttpContext来存放一些我们需要的信息,以便下次请求的时候能够取出这些信息来使用
	*/
	public static String getHttpRedirectUrl(String url) {
		String resultUrl = null;
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(url);
		try {
			//设置超时的时间为30秒
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
			//读取数据超时时间30秒
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
			//将HttpContext对象作为参数传给execute()方法,则HttpClient会把请求响应交互过程中的状态信息存储在HttpContext中
			httpClient.execute(httpGet, httpContext);
			//获取重定向之后的主机地址信息
			HttpHost targetHost = (HttpHost) httpContext
					.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
			//获取实际的请求对象的URI
			HttpUriRequest realRequest = (HttpUriRequest) httpContext
					.getAttribute(ExecutionContext.HTTP_REQUEST);
			// host + request 就是最终需要的跳转地址
			String targetHostUrl = targetHost.toURI().toString();
			if (TextUtils.isEmpty(targetHostUrl)) {
				return null;
			}
			String realRequestUrl = realRequest.getURI().toString();
			if (TextUtils.isEmpty(realRequestUrl)) {
				return null;
			}
			resultUrl = targetHostUrl + realRequestUrl;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return resultUrl;
	}

	/**
	 * 获取在功能菜单出现的程序列表
	 * 
	 * @param context
	 *            上下文
	 * @return 程序列表，类型是 List<ResolveInfo>
	 */
	public static List<ResolveInfo> getLauncherApps(Context context) {
		List<ResolveInfo> infos = null;
		PackageManager packageMgr = context.getPackageManager();
		Intent intent = new Intent(ICustomAction.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 这里OOM了，你应用都不全了，后面还玩个球，爆掉吧
//		try {
			infos = packageMgr.queryIntentActivities(intent, 0);
//		} catch (OutOfMemoryError e) {
//			OutOfMemoryHandler.handle();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		packageMgr = null;
		return infos;
	}

	/**
	 * 卸载程序
	 * 
	 * @param context
	 *            上下文
	 * @param packageURI
	 *            需要卸载的程序的Uri
	 */
	public static void uninstallApp(Context context, Uri packageURI) {
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		context.startActivity(uninstallIntent);
		uninstallIntent = null;
	}

	/**
	 * 卸载包
	 * 
	 * @param context
	 *            上下文
	 * @param packageURI
	 *            需要卸载的程序的Uri
	 */
	public static void uninstallPackage(Context context, String pkgName) {
		Uri packageURI = Uri.parse("package:" + pkgName);
		GoAppUtils.uninstallApp(context, packageURI);
//		uninstallApp(context, packageURI);
	}
	
	/**
	 * 是否激活设备
	 * @param pkg
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi")
	private static boolean isAdminActive(String pkg, Context context) {
		boolean isActive = false;
		Intent intent = new Intent("android.app.action.DEVICE_ADMIN_ENABLED");
		PackageManager packageManager = context.getPackageManager();
		intent.setPackage(pkg);
		List<ResolveInfo> list = packageManager.queryBroadcastReceivers(intent,
				0);
		if (list != null && list.size() != 0) {
			if (Build.VERSION.SDK_INT > 7) {
				DevicePolicyManager devicepolicymanager = (DevicePolicyManager) context 
						.getSystemService(Context.DEVICE_POLICY_SERVICE);
				isActive = devicepolicymanager.isAdminActive(new ComponentName(
						pkg, list.get(0).activityInfo.name));
			}
		}
		return isActive;
	}
	
	/**
	 * 获取默认运行桌面包名（注：存在多个桌面时且未指定默认桌面时，该方法返回Null,使用时需处理这个情况）
	 * @param context
	 * @return
	 */
	public static String getDefaultLauncher(Context context) {
		
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        String defaultLauncherPkg = null;
        if (res.activityInfo == null) {
            // should not happen. A home is always installed, isn't it?
        	defaultLauncherPkg = null;
        } else if (res.activityInfo.packageName.equals("android")) {
            // 有多个桌面程序存在，且未指定默认项时；     
        	defaultLauncherPkg = null;
        } else {
        	defaultLauncherPkg = res.activityInfo.packageName;
        }
        
        if (defaultLauncherPkg == null 
        		|| !defaultLauncherPkg.equals(PackageName.PACKAGE_NAME)) {
			//已经查找到某个默认桌面，但不是GO桌面，则采用另一种方式再次核对，防止上述方法在某些特别的机器或ROM（如ALCATEL）上返回错误
        	String oldDefaultPkg = getDefaultLauncherPackage(context);
        	if (oldDefaultPkg == null || oldDefaultPkg.equals(PackageName.PACKAGE_NAME)) {
				//老的查找方式能够获取到默认桌面，且默认桌面为null或者为GO桌面，则用老的方式来获取。
        		defaultLauncherPkg = oldDefaultPkg;
			}
		}
        
        return defaultLauncherPkg;
	 }
	
	
	/**
	 * 存在逻辑问题，不推荐使用。
	 * 当前逻辑是，首先获取launcher列表，其次获取默认响应程序列表，然后取该两列表中数据的包名进行循环比较，若重复则为默认桌面。
	 * 但存在逻辑漏洞是，launcher列表中某launcher的包名，与某其他默认响应程序列表包名同名，例如N4手机的launcher。
	 * @deprecated
	 * @param context
	 * @return
	 */
	public static String getDefaultLauncherPackage(Context context) {
		
		PackageManager pm = context.getPackageManager();

		// 默认列表
		List<ComponentName> componentNames = new ArrayList<ComponentName>();
		List<IntentFilter> intentFilters = new ArrayList<IntentFilter>();
		//NOTE：这个接口，有可能返回的值不可靠。在NOTE3手机中，已发现未设置GO桌面为默认桌面时，也将GO桌面作为默认数据返回。
		pm.getPreferredActivities(intentFilters, componentNames, null);

		// Launcher
		Intent intent = new Intent(ICustomAction.ACTION_MAIN);
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);

		int launcherSz = infos.size();
		int preferredSz = componentNames.size();
		for (int i = 0; i < launcherSz; i++) {
			ResolveInfo info = infos.get(i);
			if (null == info) {
				continue;
			}
			String packageStr = info.activityInfo.packageName;
			if (null == packageStr) {
				continue;
			}

			for (int j = 0; j < preferredSz; j++) {
				ComponentName componentName = componentNames.get(j);
				if (null == componentName) {
					continue;
				}
				if (packageStr.equals(componentName.getPackageName())) {
					return packageStr;
				}
			}
		}

		return null;
	}
	
	public static void showAppDetails(Context context, String packageName) {
		final String scheme = "package";
		/**
		 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
		 */
		final String appPkgName21 = "com.android.settings.ApplicationPkgName";
		/**
		 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
		 */
		final String appPkgName22 = "pkg";
		/**
		 * InstalledAppDetails所在包名
		 */
		final String appDetailsPackageName = "com.android.settings";
		/**
		 * InstalledAppDetails类名
		 */
		final String appDetailsClassName = "com.android.settings.InstalledAppDetails";

		Intent intent = new Intent();
		final int apiLevel = Build.VERSION.SDK_INT;
		if (apiLevel >= 9) {
			// 2.3（ApiLevel 9）以上，使用SDK提供的接口
			intent.setAction(ICustomAction.ACTION_SETTINGS);
			Uri uri = Uri.fromParts(scheme, packageName, null);
			intent.setData(uri);
		} else {
			// 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
			// 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
			final String appPkgName = apiLevel == 8 ? appPkgName22 : appPkgName21;
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName(appDetailsPackageName, appDetailsClassName);
			intent.putExtra(appPkgName, packageName);
		}

		try {
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取包名
	 * 
	 * @param intent
	 * @return
	 */
	public static String getPackage(Intent intent) {
		if (intent != null) {
			final ComponentName cn = intent.getComponent();
			if (cn != null) {
				return cn.getPackageName();
			}
		}
		return null;
	}

	/**
	 * 杀手当前进程
	 */
	public static void killProcess() {
		killProcess(Process.myPid());
	}
	/**
	 * 杀死进程
	 */
	public static void killProcess(int pid) {
		Log.i("ggheart", "=========killprocess " + Process.myPid() + " for some reason:");
		new Exception().printStackTrace();
		Process.killProcess(pid);
	}

	/**
	 * 跳到电子市场的我的应用界面
	 * @param context
	 * @return
	 */
	public static boolean gotoMarketMyApp(Context context) {
		boolean result = false;
		if (context == null) {
			return result;
		}
		String marketPkgName = "com.android.vending";
		int versionCode = getVersionCodeByPkgName(context, marketPkgName);
		Intent emarketIntent = null;
		if (versionCode >= NEW_MARKET_VERSION_CODE) {
			// 直接跳到电子市场我的应用界面
			emarketIntent = new Intent("com.google.android.finsky.VIEW_MY_DOWNLOADS");
			emarketIntent.setClassName(marketPkgName,
					"com.google.android.finsky.activities.MainActivity");
		} else {
			//跳转至电子市场首界面
			PackageManager packageMgr = context.getPackageManager();
			emarketIntent = packageMgr.getLaunchIntentForPackage(marketPkgName);
		}
		try {
			emarketIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(emarketIntent);
			result = true;
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取指定包的版本号
	 * 
	 * @author huyong
	 * @param context
	 * @param pkgName
	 */
	public static int getVersionCodeByPkgName(Context context, String pkgName) {
		int versionCode = 0;
		if (pkgName != null) {
			PackageManager pkgManager = context.getPackageManager();
			try {
				PackageInfo pkgInfo = pkgManager.getPackageInfo(pkgName, 0);
				versionCode = pkgInfo.versionCode;
			} catch (NameNotFoundException e) {
				Log.i("AppUtils", "getVersionCodeByPkgName=" + pkgName + " has " + e.getMessage());
			}
		}
		return versionCode;
	}

	/**
	 * 获取指定包的版本名称
	 * 
	 * @author huyong
	 * @param context
	 * @param pkgName
	 */
	public static String getVersionNameByPkgName(Context context, String pkgName) {
		String versionName = "0.0";
		if (pkgName != null) {
			PackageManager pkgManager = context.getPackageManager();
			try {
				PackageInfo pkgInfo = pkgManager.getPackageInfo(pkgName, 0);
				versionName = pkgInfo.versionName;
			} catch (NameNotFoundException e) {
				//NOT to do anything
				//e.printStackTrace();
			}
		}
		return versionName;
	}

	/**
	 * 将版本名称转换为一位小数点的float型数据
	 * 
	 * @param context
	 * @param pkgName
	 */
	public static float changeVersionNameToFloat(String versionName) {
		float versionNumber = 0.0f;
		if (versionName != null && !versionName.equals("")) {
			try {
				String underLine = "_";
				if (versionName.contains(underLine)) {
					versionName = versionName.substring(0, versionName.indexOf(underLine));
				}
				String beta = "beta";
				if (versionName.contains(beta)) {
					versionName = versionName.replace(beta, "");
				}
				int firstPoint = versionName.indexOf(".");
				int secondPoint = versionName.indexOf(".", firstPoint + 1);
				if (secondPoint != -1) {
					String temp = versionName.substring(0, secondPoint)
							+ versionName.substring(secondPoint + 1, versionName.length());
					versionNumber = Float.parseFloat(temp);
				} else {
					versionNumber = Float.parseFloat(versionName);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return versionNumber;
	}

	/**
	 * market上查看程序信息
	 * 
	 * @param packageName
	 */
	public static void viewAppDetail(Context context, String packageName) {
		String keyword = MarketConstant.APP_DETAIL + packageName;
		Uri uri = Uri.parse(keyword);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		keyword = null;
	}


	/**
	 * 判断应用是否安装在手机内存里
	 * 
	 * @author kingyang
	 * @param context
	 * @param intent
	 * @return
	 */
	public static boolean isInternalApp(Context context, Intent intent) {
		if (context != null) {
			PackageManager pkgMgr = context.getPackageManager();
			try {
				String internalPath = Environment.getDataDirectory().getAbsolutePath();
				String dir = pkgMgr.getActivityInfo(intent.getComponent(), 0).applicationInfo.publicSourceDir;
				if (dir != null && dir.length() > 0) {
					return dir.startsWith(internalPath);
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 判断应用是否是系统应用
	 * 
	 * @author kingyang
	 * @param context
	 * @param intent
	 * @return
	 */
	public static boolean isSystemApp(Context context, Intent intent) {
		boolean isSystemApp = false;
		if (context != null) {
			PackageManager pkgMgr = context.getPackageManager();
			try {
				ApplicationInfo applicationInfo = pkgMgr.getActivityInfo(intent.getComponent(), 0).applicationInfo;
				if (applicationInfo != null) {
					isSystemApp = ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
							|| ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return isSystemApp;
	}

	/**
	 * 获取指定应用的Context
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static Context getAppContext(Context context, String packageName) {
		Context ctx = null;
		try {
			ctx = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return ctx;
	}

	/**
	 * Check if the installed Gmail app supports querying for label information.
	 * 
	 * @param c
	 *            an application Context
	 * @return true if it's safe to make label API queries
	 */
	public static boolean canReadGmailLabels(Context c) {
		/**
		 * Permission required to access this
		 * {@link android.content.ContentProvider}
		 */
		final String permission = "com.google.android.gm.permission.READ_CONTENT_PROVIDER";
		/**
		 * Authority for the Gmail content provider.
		 */
		final String authority = "com.google.android.gm";
		String gmailPackageName = "com.google.android.gm";

		boolean supported = false;

		try {
			final PackageInfo info = c.getPackageManager().getPackageInfo(gmailPackageName,
					PackageManager.GET_PROVIDERS | PackageManager.GET_PERMISSIONS);
			boolean allowRead = false;
			if (info.permissions != null) {
				for (int i = 0, len = info.permissions.length; i < len; i++) {
					final PermissionInfo perm = info.permissions[i];
					if (permission.equals(perm.name)
							&& perm.protectionLevel < PermissionInfo.PROTECTION_SIGNATURE) {
						allowRead = true;
						break;
					}
				}
			}
			if (allowRead && info.providers != null) {
				for (int i = 0, len = info.providers.length; i < len; i++) {
					final ProviderInfo provider = info.providers[i];
					if (authority.equals(provider.authority)
							&& TextUtils.equals(permission, provider.readPermission)) {
						supported = true;
					}
				}
			}
		} catch (NameNotFoundException e) {
			// Gmail app not found
		}
		return supported;
	}
	
	public static String getCurProcessName(Context context) {
		try {
			int pid = android.os.Process.myPid();
			ActivityManager activityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
					.getRunningAppProcesses()) {
				if (appProcess.pid == pid) {
					return appProcess.processName;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	public static boolean isBrowser(Context context, String packageName) {
		if (packageName == null) {
			return false;
		}
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("http://3g.cn"));
        intent.setPackage(packageName);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        if (infos != null && infos.size() > 0) {
            return true;
        }
        return false;
    }

    /**
	 * <br>功能简述:在列表里过滤删除指定包名的应用
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param resolveList
	 * @param pkgs
	 * @return
	 */
	public static List<ResolveInfo> filterPkgs(List<ResolveInfo> resolveList, String[] pkgs) {
		if (resolveList == null || resolveList.size() == 0 || pkgs == null || pkgs.length == 0) {
			return resolveList;
		}
		
		ArrayList<ResolveInfo> list = new ArrayList<ResolveInfo>(resolveList);
		
		for (ResolveInfo resolveInfo : resolveList) {
			if (resolveInfo != null && resolveInfo.activityInfo != null && resolveInfo.activityInfo.packageName != null) {
				for (String pkg : pkgs) {
					if (resolveInfo.activityInfo.packageName.equals(pkg)) {
						list.remove(resolveInfo);
						break;
					}
				}
			}
		}
		
		return list;
	}
	
	/**
	 * Calculates the free memory of the device. This is based on an inspection
	 * of the filesystem, which in android devices is stored in RAM.
	 * 
	 * @return Number of bytes available.
	 */
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	/**
	 * Calculates the total memory of the device. This is based on an inspection
	 * of the filesystem, which in android devices is stored in RAM.
	 * 
	 * @return Total number of bytes.
	 */
	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}
	
	
	/**
	 * 获取设备基本信息
	 * @param context
	 * @return
	 */
	public static String getBaseDeviceInfo() {
		StringBuilder baseInfo = new StringBuilder();
		try {
			String product = "Product=" + android.os.Build.PRODUCT;
			String phoneModel = "\nPhoneModel=" + android.os.Build.MODEL;
			String kernel = "\nKernel=" + Machine.getLinuxKernel();
			String rom = "\nROM=" + android.os.Build.DISPLAY;
			String board = "\nBoard=" + android.os.Build.BOARD;
			String device = "\nDevice=" + android.os.Build.DEVICE;
//			String density = "\nDensity="
//					+ String.valueOf(context.getResources().getDisplayMetrics().density);
//			String packageName = "\nPackageName=" + context.getPackageName();
			String androidVersion = "\nAndroidVersion="
					+ android.os.Build.VERSION.RELEASE;
			String totalMemSize = "\nTotalMemSize="
					+ (getTotalInternalMemorySize() / 1024 / 1024)
					+ "MB";
			String freeMemSize = "\nFreeMemSize="
					+ (getAvailableInternalMemorySize() / 1024 / 1024)
					+ "MB";
			String romAppHeapSize = "\nRom App Heap Size="
					+ Integer
					.toString((int) (Runtime.getRuntime().maxMemory() / 1024L / 1024L))
					+ "MB";
			baseInfo.append(product).append(phoneModel).append(kernel).append(rom)
			.append(board).append(device)
			.append(androidVersion).append(totalMemSize).append(freeMemSize)
			.append(romAppHeapSize);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return baseInfo.toString();
	}	
	
	
	public static ServiceInfo getServiceInfo(Context context, ComponentName component) {
		try {
			PackageManager packageManager = context.getPackageManager();
			return packageManager.getServiceInfo(component, PackageManager.GET_SERVICES);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
