package com.jiubang.ggheart.apps.desks.appfunc;

import android.util.Log;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.jiubang.ggheart.components.DeskToast;

/**
 * 
 * @author liuheng
 *
 */
public class AppFuncExceptionHandler {
	public static void handle(Exception e, Object who) {
		if (e != null) {
//			e.printStackTrace();
			Log.e("AppFuncExceptionHandler", Log.getStackTraceString(e));
		}
		DeskToast.makeText(ApplicationProxy.getContext(), R.string.operation_failed, Toast.LENGTH_SHORT).show();
	}
}
