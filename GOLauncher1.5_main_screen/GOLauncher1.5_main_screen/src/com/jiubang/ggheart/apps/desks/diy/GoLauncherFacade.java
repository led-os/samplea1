package com.jiubang.ggheart.apps.desks.diy;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.go.commomidentify.IGoLauncherClassName;

/**
 * 一个简易的门面模式，以处理4.4系统引起桌面双实例问题
 * @author wuziyi
 *
 */
public class GoLauncherFacade extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent launcherIntent = new Intent();
		// 模拟Home键启动桌面的intent
		Intent intent = getIntent();
		if (intent != null) {
			launcherIntent.putExtras(intent);
		}
		launcherIntent.setAction(Intent.ACTION_MAIN);
		launcherIntent.addCategory(Intent.CATEGORY_HOME);
		launcherIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		launcherIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
		launcherIntent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
		launcherIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		try {
			launcherIntent.setPackage(getPackageName());
			startActivity(launcherIntent);
		} catch (Throwable e) {
			// 再试一次，不行就爆吧，想不懂
			// 如果该问题发生在4.4上的话，双实例的问题仍然会出现
			Log.i("GoLauncherFacade", "ActivityNotFoundException SDK Version:" + Build.VERSION.SDK_INT);
			launcherIntent.setComponent(new ComponentName(getApplicationContext(), IGoLauncherClassName.GOLAUNCHER_ACTIVITY));
			startActivity(launcherIntent);
		} finally {
			finish();
		}
	}
	
}
