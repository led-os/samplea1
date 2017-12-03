package com.jiubang.ggheart.uninstallcheck;
 

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import com.go.util.device.Machine;

/**
 * <br>
 * Class description:listen uninstall event <br>
 * Description:use the independent process to monitor the uninstall event
 * 
 * @author hanson
 * @date [2013年12月10日]
 * 
 * <br>
 *       类描述:卸载监听 <br>
 *       功能详细描述:
 * 
 * @author rongjinsong
 * @date [2013年10月28日]
 */
public class UninstallCheck {
	private static final String LOG_TAG = "UninstallCheck";
	static {
		try {
			if (isSupportCheck()) {
				Log.d(LOG_TAG, "load jni lib init");
				System.loadLibrary("uninstalled");
				Log.d(LOG_TAG, "-------load jni lib init end -----");
			}
		} catch (Throwable e) {
			e.printStackTrace();
			Log.d(LOG_TAG, "-------failed to load uninstall lib -----");
		}
	}
	
	// 开启卸载推荐广告功能
	@SuppressLint("SdCardPath")
	public static int stardAD(Context context, String httpuri) {
		int result = 0;
		String datapath = "/data/data/" + context.getPackageName();
		String obserfile = datapath + "/obserfile";
		String lockfile = datapath + "/lockfile";
		try {
			result = init(context.getPackageCodePath(), datapath, httpuri,
					obserfile, lockfile, Build.VERSION.SDK_INT);
			Log.d(LOG_TAG, "load jni lib init with pid=" + result);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return result;
	}

	// httpuri改变
	// @Pid 已经创建的子进程的进程号
	// @httpuri 需要传入的新的httpuri地址
	// 处理过程：1.先将已经创建的子进程杀掉。
	// 2.根据新的httpuri创建新的子进程
	public static int changeUri(Context context, int Pid, String httpuri) {
		Process.killProcess(Pid);
		return stardAD(context, httpuri);
	}

	// 启动动态库，子进程初始化
	// @AppPath 安装包APK所在的路径
	// @DataPath 资源文件所在的路径
	// @HttpUri 广告的Uri地址
	// @VersionInt 系统版本号，用来进行版本过滤（4.0以下的版本不开启卸载推荐广告功能。
	// 4.2以上的版本由于权限要求不同与4.2一下机器的am指令权限不同，通过版本号进行区分
	public static native int init(String AppPath, String DataPath,
			String HttpUri, String ObserFile, String lockfile, int VersionInt);

	public static int checkLockFile(Context context) {
		String datapath = "/data/data/" + context.getPackageName();
		String lockfile = datapath + "/lockfile";
		try {	
			return check(lockfile);
		} catch (Throwable t) {
			t.printStackTrace();
			//indicate not need to perform
			return -1;
		}
	}

	public static native int check(String lockfile);
	
	private static boolean isSupportCheck() {
		return Machine.IS_FROYO;
	}
}
