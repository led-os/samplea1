package com.jiubang.ggheart.data;

import java.lang.reflect.Field;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.gau.go.launcherex.R;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.SettingProxy;
import com.go.util.device.Machine;
import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingAdvancedActivity;
import com.jiubang.ggheart.common.controler.ScreenOnOffReciver;
import com.jiubang.ggheart.data.info.ThemeSettingInfo;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.ThreadName;

/**
 * 
 * @author zhujian
 *
 */
public class AppService extends Service {
	// 定义个一个Tag标签
	private static final String TAG = "DemoService";
	// 这里定义吧一个Binder类，用在onBind()有方法里，这样Activity那边可以获取到
	private MyBinder mBinder = new MyBinder();
	private ScreenOnOffReciver mScreenOnOffReciver;
	
	@Override
	public IBinder onBind(Intent intent) {
		// Log.d(TAG, "start IBinder~~~");
		return mBinder;
	}

	@Override
	public void onCreate() {
		// Log.d(TAG, "DemoService onCreate before load~~~");
		super.onCreate();

		startForeground();

		DrawUtils.resetDensity(this);
		if (GoLauncherActivityProxy.getActivity() == null) {
			// 桌面尚未启动起来，则表示当前是系统启动的service，可以去加载图标
			// Log.d(TAG, "DemoService onCreate to loadicon for golauncher is null~~");
			final AppDataEngine aDE = AppDataEngine.getInstance(getApplicationContext());
			// 使用线程load图标
			// 如果这个时候点击进入桌面，不会出现卡死现象
			new Thread(ThreadName.APP_SERVICE_LOAD_DATA) {
				@Override
				public void run() {
					aDE.loadInitDataInService();
					Log.d(TAG, "DemoService Load done");
				}
			}.start();
		};
		Log.d(TAG, "DemoService onCreate end load~~~");
		
		initReciver();
	}
	
	protected void initReciver() {
		mScreenOnOffReciver = new ScreenOnOffReciver();
		//屏幕点着和关闭广播接收必须使用代码注册
		IntentFilter recevierFilter = new IntentFilter();  
		recevierFilter.addAction(Intent.ACTION_SCREEN_ON);  
//		recevierFilter.addAction(Intent.ACTION_SCREEN_OFF); 
//		recevierFilter.addAction(ICustomAction.ACTION_LAUNCHER_START); 
//		recevierFilter.addAction(ICustomAction.ACTION_LAUNCHER_EXIT); 
		recevierFilter.addAction(ICustomAction.ACTION_LAUNCHER_STOP); 
//		recevierFilter.addAction(ICustomAction.ACTION_LAUNCHER_ONSTART);
//		recevierFilter.addAction(ICustomAction.ACTION_LAUNCHER_RESETDEFAULT);
		registerReceiver(mScreenOnOffReciver, recevierFilter);
//		registerReceiver(mStartSideBarReceiver, filter);
		
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "DemoService onStart~~~");
		super.onStart(intent, startId);
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// Log.d(TAG, "start onDestroy~~~");
		unregisterReceiver(mScreenOnOffReciver);
		super.onDestroy();
	}

	public void startForeground() {
		// SharedPreferences spf =
		// getApplicationContext().getSharedPreferences("Foregound", 0);
		// boolean bNeedForegound = spf.getBoolean("NEEDFOREGOUND", true);
		ThemeSettingInfo mThemeInfo = SettingProxy.getThemeSettingInfo();

		if (!mThemeInfo.mIsPemanentMemory) {
			return;
		}
		
		Notification notification = null;
		if (Machine.IS_JELLY_BEAN_3) {
			Intent intent = new Intent(this, DeskSettingAdvancedActivity.class);
			intent.putExtra(DeskSettingAdvancedActivity.ISCOMEFROMNOTIFICATION, true);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK),
					PendingIntent.FLAG_UPDATE_CURRENT);
			notification = new Notification.Builder(this).setContentIntent(contentIntent)
					.setSmallIcon(R.drawable.icon).setWhen(System.currentTimeMillis())
					.setAutoCancel(false)
					.setContentTitle(getString(R.string.permanentmemory_enablesummary_title))
					.setContentText(getString(R.string.permanentmemory_enablesummary_touch))
					.getNotification();
			try {
				Field priority = notification.getClass().getField("priority");
				priority.setInt(notification, -2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			notification = new Notification(0, "GO" +
					"Launcher",
					System.currentTimeMillis());
			notification.setLatestEventInfo(this, "GOLauncher", "GOLauncher Running",
					PendingIntent.getActivity(this, 0, new Intent(this, ForegroundDialog.class).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK), 0));			
		}

		// 0x123456 Notification对应的id
		startForeground(0x123456, notification);
		
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// Log.d(TAG, "start onUnbind~~~");
		return super.onUnbind(intent);
	}

	/*
	 * public String getSystemTime(){ Time t = new Time(); t.setToNow(); return
	 * t.toString(); }
	 */

	/**
	 *
	 */
	public class MyBinder extends Binder {
		public AppService getService() {
			return AppService.this;
		}
	}

}
