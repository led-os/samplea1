package com.go.util.log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.util.Log;

/**
 * @author zengyongping
 * 
 */
public final class Loger {
	private final static int LOGCLOSE = 100;
	private final static int LOGLEVEL = 0; // 配置文件中log等级
	private final static boolean DEBUG = false;

	public static final boolean isLoggable(String tag, int level) {
		return Log.isLoggable(tag, level);
	}

	private static boolean isLogable(int level) {
		if (DEBUG && level > LOGLEVEL) {
			return true;
		}
		return false;
	}

	public static final int println(int priority, String tag, String msg) {
		if (isLogable(priority)) {
			return Log.println(priority, tag, msg);
		}
		return -1;
	}

	public static final String getStackTraceString(Throwable tr) {
		return Log.getStackTraceString(tr);
	}

	/**
	 * 
	 * @param tag
	 *            标识符
	 * @param msg
	 *            打印信息
	 */
	public static final void v(String tag, String msg) {
		if (isLogable(Log.VERBOSE)) {
			Log.v(tag, msg);
		}
	}

	/**
	 * @param tag
	 *            标识符
	 * @param msg
	 *            打印信息
	 * @param classRef
	 *            要打印对象类名所对应的对象指针
	 */
	public static final void v(String tag, String msg, boolean dump) {
		if (isLogable(Log.VERBOSE)) {
			if (dump) {
				msg = dumpError(msg);
			}
			Log.v(tag, msg);
		}
	}

	/**
	 * @param tag
	 *            标识符
	 * @param msg
	 *            打印信息
	 * @param Throwable
	 *            tr 抛出的异常
	 */
	public static final void v(String tag, String msg, Throwable tr) {
		if (isLogable(Log.VERBOSE)) {
			Log.v(tag, msg, tr);
		}
	}

	/**
	 * 
	 * @param tag
	 * @param msg
	 */
	public static final void d(String tag, String msg) {
		if (isLogable(Log.DEBUG)) {
			Log.d(tag, msg);
		}
	}

	/**
	 * @param tag
	 *            标识符
	 * @param msg
	 *            打印信息
	 * @param Throwable
	 *            tr 抛出的异常
	 */
	public static final void d(String tag, String msg, Throwable tr) {
		if (isLogable(Log.DEBUG)) {
			Log.d(tag, msg, tr);
		}
	}

	/**
	 * 
	 * @param tag
	 * @param msg
	 * @param classRef
	 */
	public static final void d(String tag, String msg, boolean dump) {
		if (isLogable(Log.DEBUG)) {
			if (dump) {
				msg = dumpError(msg);
			}
			Log.d(tag, msg);
		}
	}

	/**
	 * 
	 * @param tag
	 * @param msg
	 */
	public static final void i(String tag, String msg) {
		if (isLogable(Log.INFO)) {
			Log.i(tag, msg);
		}
	}

	/**
	 * @param tag
	 *            标识符
	 * @param msg
	 *            打印信息
	 * @param Throwable
	 *            tr 抛出的异常
	 */
	public static final void i(String tag, String msg, Throwable tr) {
		if (isLogable(Log.INFO)) {
			Log.i(tag, msg, tr);
		}
	}

	/**
	 * @param tag
	 *            标识符
	 * @param msg
	 *            打印信息
	 * @param dump
	 *            打印当前
	 */
	public static final void i(String tag, String msg, boolean dump) {
		if (isLogable(Log.INFO)) {
			if (dump) {
				msg = dumpError(msg);
			}
			Log.i(tag, msg);
		}
	}

	/**
	 * 
	 * @param tag
	 * @param msg
	 */
	public static final void w(String tag, String msg) {
		if (isLogable(Log.WARN)) {
			Log.w(tag, msg);
		}
	}

	/**
	 * 
	 * @param tag
	 * @param msg
	 * @param dump
	 */
	public static final void w(String tag, String msg, boolean dump) {
		if (isLogable(Log.WARN)) {
			if (dump) {
				msg = dumpError(msg);
			}
			Log.w(tag, msg);
		}
	}

	/**
	 * @param tag
	 *            标识符
	 * @param msg
	 *            打印信息
	 * @param Throwable
	 *            tr 抛出的异常
	 */
	public static final void w(String tag, String msg, Throwable tr) {
		if (isLogable(Log.WARN)) {
			Log.w(tag, msg, tr);
		}
	}

	/**
	 * 
	 * @param tag
	 * @param msg
	 */
	public static final void e(String tag, String msg) {
		if (isLogable(Log.ERROR)) {
			Log.e(tag, msg);
		}
	}

	/**
	 * 
	 * @param tag
	 * @param msg
	 * @param dump
	 */
	public static final void e(String tag, String msg, boolean dump) {
		if (isLogable(Log.ERROR)) {
			if (dump) {
				msg = dumpError(msg);
			}
			Log.e(tag, msg);
		}
	}

	/**
	 * @param tag
	 *            标识符
	 * @param msg
	 *            打印信息
	 * @param Throwable
	 *            tr 抛出的异常
	 */
	public static final void e(String tag, String msg, Throwable tr) {
		if (isLogable(Log.ERROR)) {
			Log.e(tag, msg, tr);
		}
	}

	/**
	 * 
	 * @param tag
	 * @return
	 */
	private static final String dumpError(String msg) {
		String errmsg = msg + "[";
		try {
			StackTraceElement element = Thread.currentThread().getStackTrace()[4];
			errmsg += element.toString();
			// errmsg += element.getClassName();
			// errmsg += "::";
			// errmsg += element.getMethodName();
			// errmsg += "#";
			// errmsg += element.getLineNumber();
			// element = null;
		} catch (Exception e) {
		}
		errmsg += "]";
		return errmsg;
	}
	
	/**
	 * 导出当前log信息前100条
	 * @param sBuffer
	 */
	public static void writeLog(StringBuffer sBuffer) {
		try {   
		    ArrayList<String> commandLine = new ArrayList<String>();   
		    commandLine.add("logcat");
		    commandLine.add("-v");   
		    commandLine.add("time");
//		    commandLine.add("-t");
//		    commandLine.add("600");
		    
		    Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));
		    int size = 24 * 1024;
		    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()), size);
		    String line = null;   
		    while ((line = bufferedReader.readLine()) != null  && sIsNeedLog) {
		    	sBuffer.append(line);
		    	sBuffer.append("\r");
		    }   
		   } catch (Exception e) {
			   e.printStackTrace();
		   } catch (Throwable e) {
			   e.printStackTrace();
		}
	}
	
	public static StringBuffer sBuffer = new StringBuffer();
	public static boolean sIsNeedLog = false;
	public static void startWritLog() {
		Thread writeThread = new Thread("write_log") {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				writeLog(sBuffer);
			}
		};
		sIsNeedLog = true;
		writeThread.setPriority(Thread.MAX_PRIORITY);
		writeThread.start();
	}
	
	public static void stopWriteLog() {
		sIsNeedLog = false;
		sBuffer = null;
	}
	
}
