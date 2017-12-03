1.由于build.xml中用到相对路径，为避免不必要的纠错，请分别将GOLauncher／Shellplugin／plugin_inf／ShellEngine四个工程放在同一根目录下。
2.Shellplugin工程修改点：
2.1.gl_progressbar.xml文件中的命名空间修改为桌面主包名称，即xmlns:shell="http://schemas.android.com/apk/res/com.gau.golauncherex.plugin.shell"修改为xmlns:shell="http://schemas.android.com/apk/res/com.gau.go.launcherex"
2.2.Go3DWidgetManager.java文件中接口createGLView()需要修改classloader
DexClassLoader cl = new DexClassLoader(sourceDir, output, null,
						/*mLauncherContext.getClassLoader()*/VMStack.getCallingClassLoader());修改为DexClassLoader cl = new DexClassLoader(sourceDir, output, null,
						mLauncherContext.getClassLoader()/*VMStack.getCallingClassLoader()*/);

3.GOLauncherEX工程修改点：
3.1GOLauncher需要用4.0打包，因此，首先请确保当前环境为4.0；
3.2修改ShellPluginFactory.java中的sUseEngineFlag字段为true，同时修改字段sContainEngine为true，以便默认启动便使用内置3DEngine。
3.3用附件中的proguard.cfg替换当前工程下的proguard.cfg，（不能成功提交上去，提示正处于冲突状态，不知为何，后续会提交上去）。

4.打包在bin目录下生成Launcher_release.apk。

注意：
1.build-3d-proguard.xml中的变量值依据具体环境而定，它包括：
sdk-platform-folder
android-jar
android-8-jar
/usr/android-sdks/tools/support/annotations.jar

2.文件中使用了相对路径及project名称，最好能统一四个工程的名称。如下：GOLauncherEX_for_main／GOLauncherEX_ShellPlugin／GOLauncherEX_Plugin_Inf／ShellEngine。



