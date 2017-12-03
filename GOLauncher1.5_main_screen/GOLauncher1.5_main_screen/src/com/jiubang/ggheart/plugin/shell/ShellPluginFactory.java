package com.jiubang.ggheart.plugin.shell;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.go.util.device.Machine;
import com.go.util.file.FileUtil;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.diy.FrameControl;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.plugin.BasePluginFactory;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  yangguanxiang
 * @date  [2012-11-16]
 */
public class ShellPluginFactory extends BasePluginFactory {
	public static boolean sUseEngineFlag = true;
	private static final String ADMIN_NAME = "com.jiubang.shell.ggheart.plugin.ShellAdmin";
	private static final String[] DEX_ZIP_FILE_NAMES = null/*{ "package_files_1" }*/;
	private static int sShellPluginExist = -1;
	private static String sContainEngine = checkShellEngineMode();
	
	private static final String MODE_CONTAINENGINE = "containshell"; //合成3D到桌面包模式
	private static final String MODE_CONTAINENGINE_DEX = "containshelldex"; //合成3D资源到桌面包模式，但3Dclass文件单独成dex
	private static final String MODE_CONTAINENGINE_PLUGIN = "containpluginapk"; //插件包形式，3D的class文件在插件包中

	//存放在assets目录下的shell的dex文件
	private static final String SHELLFILE_IN_ASSETS = "shellplugin.apk";
	private static final String TAG_SHELL = "ShellPluginFactory"; 
			
	
	private static IShellManager sShellManager;

	@SuppressWarnings("rawtypes")
	public static void buildShellPlugin(Activity activity, FrameControl frameControl) throws Exception {
		Context context = activity;
		ClassLoader loader = activity.getClassLoader();
		if (MODE_CONTAINENGINE_PLUGIN.equals(sContainEngine)) {
			//是安装版本
			context = getRemoteContext(activity, PackageName.SHELL_PLUGIN);
			loader = createDexClassLoader(activity, context, PackageName.SHELL_PLUGIN,
					DEX_ZIP_FILE_NAMES);
		} else if (MODE_CONTAINENGINE_DEX.equals(sContainEngine)) {
			//是合成版本，但dex未合成
			context = activity.getApplicationContext();
			//从dex中去load
			loader = createClassLoaderFromDex(context, PackageName.SHELL_PLUGIN, SHELLFILE_IN_ASSETS);
		}
		
		Log.i(TAG_SHELL, "============loader=" + loader + ", parent=" + loader.getParent());
		Class clazz = getPluginAdminClass(activity, ADMIN_NAME, loader);
		Log.i(TAG_SHELL, "============clazz=" + clazz);
		
		buildShellPlugin(activity, context, loader, clazz, frameControl);

	}

	private static void buildShellPlugin(Activity activity, Context context, ClassLoader loader,
			Class clazz, FrameControl frameControl) throws Exception {
		Constructor constructor = clazz.getConstructor(Activity.class, Context.class,
				ClassLoader.class, FrameControl.class);
		Object pluginMain = constructor.newInstance(activity, context, loader, frameControl);
		Method method = clazz.getMethod("getShellManager");
		sShellManager = (IShellManager) method.invoke(pluginMain);
	}

	public static IShellManager getShellManager() {
		return sShellManager;
	}
	
	/**
	 * <br>功能简述:检查ShellEngine合成模式的方法
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	private static String checkShellEngineMode() {
		String rusultMode = null;
		if (checkContainShellEngine()) {
			rusultMode = MODE_CONTAINENGINE;
		} else if (checkContainShellEngineDex()) {
			rusultMode = MODE_CONTAINENGINE_DEX;
		} else {
			rusultMode = MODE_CONTAINENGINE_PLUGIN;
		}
		Log.i(TAG_SHELL, "=============shellmode = " + rusultMode);
		return rusultMode;
	}
	
	
	private static boolean checkContainShellEngineDex() {
		return FileUtil.checkFileInAssets(MODE_CONTAINENGINE_DEX);
	}

	private static boolean checkContainShellEngine() {
		return FileUtil.checkFileInAssets(MODE_CONTAINENGINE);
	}
	
	public static boolean isShellPluginExist(Context context) {
		//暂时没有shellplugin插件版本，不需要判断，避免不必要的插件判断问题
		if (!sContainEngine.equals(MODE_CONTAINENGINE_PLUGIN)) {
			return true;
		}

		if (sShellPluginExist == -1) {
			sShellPluginExist = GoAppUtils.isAppExist(context, PackageName.SHELL_PLUGIN) ? 1 : 0;
		}
		return sShellPluginExist == 1 ? true : false;
	}

	public static boolean isSupportShellPlugin(Context context) {
		return Machine.IS_FROYO && Machine.isSupportGLES20(context) && isShellPluginExist(context);
	}
	
	/**
	 * 默认三款机型,付费用户和收到显示3D设置项消息的机型显示3D设置项
	 * @param context
	 * @return
	 */
	private static boolean isNeedToShow3DSetting(Context context) {
		PreferencesManager sharedPreferences = new PreferencesManager(context,
				IPreferencesIds.PREFERENCE_ENGINE, Context.MODE_PRIVATE);
		boolean isShowedShellgine = sharedPreferences.getBoolean(
				IPreferencesIds.PREFERENCE_ENGINE_SHOW_SETTING, false);
		return Machine.needToOpen3DCore()
				|| FunctionPurchaseManager.getInstance(context.getApplicationContext())
						.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_FULL)
				|| isShowedShellgine;
	}
}
