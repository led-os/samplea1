package com.go.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

/**
 * 
 * @author  guoyiqing
 * @date  [2013-9-23]
 */
public class ProcessKiller {

	private static final String PS_USER_TAG = "USER";
	private static final String PS_PID_TAG = "PID";
	private static final String PS_PPID_TAG = "PPID";
	protected static final int KILL_DELAY_MSG_CODE = 0xF;
	private static Handler sHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case KILL_DELAY_MSG_CODE :
					kill((String) msg.obj);
					break;

				default :
					break;
			}
		};
	};

	/**
	 * <br>注意: 先杀子进程,后杀自己
	 * @param myProcessName 进程名, delay 延迟
	 * @return
	 */
	public static void killSelf(Context context, int delay) {
		if (sHandler != null) {
			Message msg = sHandler.obtainMessage(KILL_DELAY_MSG_CODE);
			msg.obj = getCurProcessName(context);
			sHandler.sendMessageDelayed(msg, delay);
		}
	}

	public static String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

	/**
	 * 根据传入的进程名杀死该进程
	 * <br>注意: 先杀子进程,后杀自己
	 * @param myProcessName
	 * @return
	 */
	public static void kill(String myProcessName) {
		if (myProcessName == null || myProcessName.equals("")) {
			return;
		}
		java.lang.Process process = null;
		try {
			int userIndex = -1;
			int pidIndex = -1;
			int ppidIndex = -1;
			process = Runtime.getRuntime().exec("ps");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = reader.readLine();
			String[] texts = null;
			if (line != null) {
				texts = line.split("\\W+", Integer.MAX_VALUE);
				if (texts.length > 0) {
					int length = texts.length;
					for (int i = 0; i < length; i++) {
						if (PS_USER_TAG.equals(texts[i].trim())) {
							userIndex = i;
						} else if (PS_PID_TAG.equals(texts[i].trim())) {
							pidIndex = i;
						} else if (PS_PPID_TAG.equals(texts[i].trim())) {
							ppidIndex = i;
						}
					}
				}
			} else {
				return;
			}
			while ((line = reader.readLine()) != null) {
				if (line.contains(myProcessName)) {
					texts = line.split("\\W+", Integer.MAX_VALUE);
					int pid = Integer.parseInt(texts[pidIndex]);
					if (!String.valueOf(Process.myPid()).equals(texts[pidIndex])) {
						Process.killProcess(pid);
					}
				}
			}
//			SparseArray<String> killPidToUser = new SparseArray<String>();
//					if (userIndex != -1) {
//						killPidToUser.put(pid, texts[userIndex]);
//					}
//			String myUser = killPidToUser.get(Process.myPid());
//			if (myUser != null) {
//				int length = killPidToUser.size();
//				for (int i = 0; i < length; i++) {
//					if (myUser.equals(killPidToUser.valueAt(i))) {
//						if (Process.myPid() != killPidToUser.keyAt(i)) {
//							Process.killProcess(killPidToUser.keyAt(i));
//						}
//					}
//				}
//			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
				process = null;
			}
			if (sHandler != null) {
				sHandler.removeCallbacksAndMessages(null);
				sHandler = null;
			}
			Process.killProcess(Process.myPid());
		}
	}

}
