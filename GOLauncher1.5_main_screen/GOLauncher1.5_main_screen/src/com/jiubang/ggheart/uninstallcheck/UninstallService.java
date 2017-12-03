package com.jiubang.ggheart.uninstallcheck;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 
 * @author hanson
 * 
 */
public class UninstallService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		
		//a thread to avoid service ANR in case, as this feature are not critical
		new Thread("UninstallServiceThread") {
			public void run() {
				try {
					UninstallCheck.stardAD(getApplicationContext(),
							LauncherEnv.Url.NEXT_LAUNCHER_HOME_URL);
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("UninstallService", "Failed to start the startavd in service");
				}
				
			}
		}.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		stopSelf();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
