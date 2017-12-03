package com.jiubang.ggheart.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.Log;

import com.go.util.debug.DebugState;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.plugin.BasePluginFactory;

import dalvik.system.DexClassLoader;


/**
 * 通过classloader来动态加载第三方程序，实现在桌面外，可通过编写调试桌面包，对桌面进行针对性的调试，为外部调试桌面提供入口。
 * 对应支持3种调试模式，中间以#分割
 * 1.apk插件安装包形式：格式为：apk#包名#入口类名
 * 2.dex文件形式，格式为：dex#文件路径#入口类名
 * 3.net网络形式，格式为：net#网址#入口类名
 * @author huyong
 *
 */
public class DebugApp {

	//对应格式：模式:地址:入口类
	private static final String DEBUG_MODE_APK = "apk";
	private static final String DEBUG_MODE_DEX = "dex";
	private static final String DEBUG_MODE_NET = "net";
	
	//debug方法入口
	private static final String DEBUG_METHOD_ENTRY = "main";
	
	/**
	 * 开始执行debug模式
	 * @param context
	 */
	public void start(Context context) {
		if (!DebugState.isDebugLauncher()) {
			return;
		}
		
		startDebug(context);
//暂停几秒种，方便调试
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}	

	}
	
	private void startDebug(Context context) {
		try {
			//加载包名及入口类
			BufferedReader reader = new BufferedReader(new FileReader(DebugState.getDebugLauncherFile()));
			String strData = null;
			String debugMode = null;
			String path = null;
			String mainClassName = null;
			DebugPluginFactory debugFactory = new DebugPluginFactory();
			while ((strData = reader.readLine()) != null) {
				try {
					String[] debugInfo = strData.split("#");
					if (debugInfo != null && debugInfo.length == 3) {
						debugMode = debugInfo[0];
						path = debugInfo[1];
						mainClassName = debugInfo[2];
						debugFactory.executeDebugByInfo(context, debugMode, path, mainClassName);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	} 
	
	/**
	 * 内部继承BasePluginFactory的类，便于统一classloader的output路径
	 * @author huyong
	 *
	 */
	private class DebugPluginFactory extends BasePluginFactory {
		
		public void executeDebugByInfo(Context context, String debugMode, String path, String mainClassName) {
			ClassLoader debugLoader = null;
			if (DEBUG_MODE_APK.equals(debugMode)) {
				//apk形式
				String pkgName = path;
				if (GoAppUtils.isAppExist(context, pkgName)) {
					debugLoader = createApkClassLoader(context, path);
				} else {
					Log.i("tag_dexclassloader", "-----------no installed pkgName: " + pkgName);
				}
			} else if (DEBUG_MODE_DEX.equals(debugMode)) {
				//dex文件形式
				debugLoader = createDexClassLoader(context, path, mainClassName);
				
			} else if (DEBUG_MODE_NET.equals(debugMode)) {
				//网络获取形式
				debugLoader = createNetClassLoader(context, path, mainClassName);
			}
			
			//使用上文创建的classloader执行
			executeDebug(context, mainClassName, debugLoader);
		}
		
		private ClassLoader createApkClassLoader(Context context, String debugPkgName) {
			Context apkContext = getRemoteContext(context, debugPkgName);
			ClassLoader apkClassLoader = createDexClassLoader(context, apkContext, debugPkgName,
					null);
			return apkClassLoader;
		}
		
		private ClassLoader createDexClassLoader(Context context, String dexPath, String mainClassName) {
			ClassLoader loader = null;
			try {
				//此处之所以需要传入mainClassName，是由于在创建输出dex路径时，将用该名称作为输出路径名，以便区分各个不同的调试包
				String dexOutputDir = getDexOutputDir(context, mainClassName);
				Log.i("tag_dexclassloader", "-----------dexPath: " + dexPath);
				File file = new File(dexOutputDir);
				if (file.exists()) {
					file.delete();
				}
				file.mkdirs();
				
				loader = new DexClassLoader(dexPath, dexOutputDir, null, context.getClassLoader());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return loader;
		}
		
		private ClassLoader createNetClassLoader(Context context, String dexPath, String mainClassName) {
			//TODO:
			return null;
		}
		
		private void executeDebug(Context context, String debugMainClassName, ClassLoader debugLoader) {
			try {
				Class mainClass = getPluginAdminClass(context, debugMainClassName, debugLoader);
				Constructor constructor = mainClass.getConstructor();
				Object pluginMain = constructor.newInstance();
				Method method = mainClass.getMethod(DEBUG_METHOD_ENTRY);
				method.invoke(pluginMain);
				Log.i("tag_dexclassloader", "=============DebugApp.executeDebug ===== ok");
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

}
