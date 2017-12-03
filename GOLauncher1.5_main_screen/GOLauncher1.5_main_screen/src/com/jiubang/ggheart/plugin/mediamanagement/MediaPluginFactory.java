package com.jiubang.ggheart.plugin.mediamanagement;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.util.AppUtils;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;
import com.jiubang.ggheart.launcher.CheckApplication;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.plugin.BasePluginFactory;
import com.jiubang.ggheart.plugin.mediamanagement.inf.IMediaManager;
import com.jiubang.ggheart.plugin.mediamanagement.inf.IMediaMessageManager;
import com.jiubang.ggheart.plugin.mediamanagement.inf.IMediaUIManager;
import com.jiubang.ggheart.plugin.mediamanagement.inf.ISwitchMenuControler;
import com.jiubang.ggheart.plugin.mediamanagement.inf.MediaContext;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  yangguanxiang
 * @date  [2012-11-16]
 */
public class MediaPluginFactory extends BasePluginFactory {
	private static final String ADMIN_NAME = "com.jiubang.ggheart.plugin.mediamanagement.MediaManagementAdmin";
	private static int sMediaPluginExist = -1;
	private static IMediaManager sMediaManager;
	private static IMediaUIManager sMediaUIManager;
	private static ISwitchMenuControler sSwitchMenuControler;

	private static MediaContext getMediaContext(Context remoteContext, ClassLoader dexLoader) {
		MediaContext mediaContext = null;
		if (remoteContext != null && dexLoader != null) {
			mediaContext = new MediaContext(remoteContext, dexLoader);
		}
		return mediaContext;
	}

	public static void buildSwitchMenuControler(Activity activity, View rootView) {
		if (sSwitchMenuControler == null) {
			sSwitchMenuControler = new SwitchMenuControler(activity, rootView);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void buildMediaPlugin(Activity activity) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		Context ctx = getRemoteContext(activity, PackageName.MEDIA_PLUGIN);
		ClassLoader loader = createDexClassLoader(activity, ctx, PackageName.MEDIA_PLUGIN, null);
		Class clazz = getPluginAdminClass(activity, ADMIN_NAME, loader);
//		try {
			Constructor constructor = clazz.getConstructor(MediaContext.class, Activity.class,
					IMediaMessageManager.class);
			Object pluginMain = constructor.newInstance(getMediaContext(ctx, loader), activity,
					new MediaMessageManager());
			Method method = clazz.getMethod("getMediaManager");
			sMediaManager = (IMediaManager) method.invoke(pluginMain);
			method = clazz.getMethod("getMediaUIManager");
			sMediaUIManager = (IMediaUIManager) method.invoke(pluginMain);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public static IMediaManager getMediaManager() {
		return sMediaManager;
	}

	public static IMediaUIManager getMediaUIManager() {
		return sMediaUIManager;
	}

	public static ISwitchMenuControler getSwitchMenuControler() {
		return sSwitchMenuControler;
	}

	public static boolean isMediaPluginExist(Context context) {
		if (sMediaPluginExist == -1) {
			sMediaPluginExist = GoAppUtils.isAppExist(context, PackageName.MEDIA_PLUGIN) ? 1 : 0;
		}
		return sMediaPluginExist == 1 ? true : false;
	}

	/**
	 * 设置资源管理插件是否存在
	 * @param isExist
	 */
	public static void setMediaPluginExist(int isExist) {
		sMediaPluginExist = isExist;
	}

	public static float sMediaPluginHavePlayBarVersion = 1.2f;
	private static int sMediaPluginHavePlayingBar = -1;
	/**
	 * 检测插件包当前版本是否拥有了正在播放条
	 * @return
	 */
	public static boolean isMediaPluginHavePlayingBar() {
		if (sMediaPluginHavePlayingBar == -1) {
			String versionName = AppUtils.getVersionNameByPkgName(ApplicationProxy.getContext(),
					PackageName.MEDIA_PLUGIN);
			float mediaPluginCurrentVersion = AppUtils.changeVersionNameToFloat(versionName);
			if (mediaPluginCurrentVersion >= sMediaPluginHavePlayBarVersion) {
				sMediaPluginHavePlayingBar = 1;
			} else {
				sMediaPluginHavePlayingBar = 0;
			}
		}
		return sMediaPluginHavePlayingBar == 1 ? true : false;
	}
	
	public static void showMediaPluginDownloadDialog() {
		final Context context = ApplicationProxy.getContext();
		String textFirst = context
				.getString(R.string.download_mediamanagement_plugin_dialog_text_first);
		String textMiddle = context
				.getString(R.string.download_mediamanagement_plugin_dialog_text_middle);
		String textLast = context
				.getString(R.string.download_mediamanagement_plugin_dialog_text_last);
		SpannableStringBuilder messageText = new SpannableStringBuilder(textFirst + textMiddle
				+ textLast);
		messageText.setSpan(new RelativeSizeSpan(0.8f), textFirst.length(),
				messageText.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		messageText.setSpan(
				new ForegroundColorSpan(context.getResources().getColor(
						R.color.snapshot_tutorial_notice_color)), textFirst.length(),
				textFirst.length() + textMiddle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置提示为绿色

		DialogConfirm dialog = new DialogConfirm(GoLauncherActivityProxy.getActivity());
		dialog.show();
		dialog.setTitle(R.string.download_mediamanagement_plugin_dialog_title);
		dialog.setMessage(messageText);
		dialog.setPositiveButton(R.string.download_mediamanagement_plugin_dialog_download_btn_text,
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// 跳转进行下载
						Context context = ApplicationProxy.getContext();
						String packageName = PackageName.MEDIA_PLUGIN;
						String url = LauncherEnv.Url.MEDIA_PLUGIN_FTP_URL; // 插件包ftp地址
						String linkArray[] = { packageName, url };
						String title = context
								.getString(R.string.mediamanagement_plugin_download_title);
						boolean isCnUser = GoAppUtils.isCnUser(context);

						CheckApplication.downloadAppFromMarketFTPGostore(context, "", linkArray,
								LauncherEnv.GOLAUNCHER_GOOGLE_REFERRAL_LINK, title,
								System.currentTimeMillis(), isCnUser,
								CheckApplication.FROM_MEDIA_DOWNLOAD_DIGLOG, 0, null);
					}
				});
		dialog.setNegativeButton(R.string.download_mediamanagement_plugin_dialog_later_btn_text,
				null);
	}
}
