package com.jiubang.ggheart.apps.desks.appfunc.help;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;

import com.gau.go.launcherex.R;
import com.go.proxy.SettingProxy;
import com.go.util.AppUtils;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.SpecialAppManager;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.MonitorSver;
import com.jiubang.ggheart.launcher.ICustomAction;

/**
 * 功能表卸载处罚处理
 * @author wuziyi
 *
 */
public class AppFuncUninstallHelper {
	
	public static boolean uninstallApp(Context context, Intent intent, ActiveNotFoundCallBack callBack) {
		if (AppUtils.isAppExist(context, intent)) {
			String pkg = intent.getComponent().getPackageName();
			boolean goToQuestionnaire = GoAppUtils.goToQuestionnaire(context, pkg);
			if (!goToQuestionnaire) {
				AppUtils.uninstallPackage(context, pkg);
			}
			return true;
		} else {
			showActiveNotFoundTip(context, intent, callBack);
			return false;
		}
	}
	
	public static void showActiveNotFoundTip(final Context context, final Intent intent, final ActiveNotFoundCallBack callBack) {
		DialogConfirm dialog = new DialogConfirm(context);
		Resources res = context.getResources();
		dialog.show();
		dialog.setTitle(res.getString(R.string.dlg_promanageTitle));
		dialog.setMessage(res.getString(R.string.dlg_activityNotFound));
		dialog.setPositiveButton(null, new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (uninstallFakeApp(context, intent)) {
					// 如果是特殊图标的话，会发出正常的卸载消息，后续的逻辑按照卸载的流程执行
					AppDataEngine.getInstance(context).onBCChange(MonitorSver.APPCHANGE,
							MonitorSver.FLAG_UNINSTALL, intent, null);
				} else {
					boolean ret = SpecialAppManager.getInstance().uninstallSpecialApp(intent);
					if (!ret) {
						// 交给外部处理
						if (callBack != null) {
							callBack.noActiveCallBack(intent);
						}
					}
				}
			}
		});
	}
	
	private static boolean uninstallFakeApp(Context context, Intent intent) {
		boolean specialRecord = false;
		// 检测如果是应用中心/游戏中心，则记录标志，避免下次程序启动时再次加入该组图标
		// add by songzhaochun, 2012.06.19
		if (intent != null) {
			String action = intent.getAction();
			if (action != null) {
				// 应用中心
				if (ICustomAction.ACTION_FUNC_SHOW_RECOMMENDCENTER.equals(action)) {
					specialRecord = true;
				}
				// 游戏中心
				else if (ICustomAction.ACTION_FUNC_SHOW_GAMECENTER.equals(action)) {
					specialRecord = true;
				}
				// 记录
				if (specialRecord) {
					PreferencesManager sp = new PreferencesManager(context,
							IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
					if (sp != null) {
						sp.putBoolean(intent.getAction(), true);
						sp.commit();
					}
				} else if (handleDeletedGoStoreAndGoThemeIcon(context, action)) {
					specialRecord = true;
				}
			}
		}
		return specialRecord;
	}
	
	private static boolean handleDeletedGoStoreAndGoThemeIcon(Context context, String action) {
		boolean ret = false;
		if (action != null) {
			if (ICustomAction.ACTION_FUNC_SPECIAL_APP_GOSTORE.equals(action)) {
				// 如果是gostore或者go精品图标则将AppDataEngine中的数据也一起删掉
				// 保存删除状态
				FunAppSetting funAppSetting = SettingProxy.getFunAppSetting();
				if (funAppSetting != null) {
					funAppSetting.setShowGoStoreAndGoTheme(false,
							FunAppSetting.FUNC_APP_TYPE_GOSTORE);
				}
				ret = true;
			} else if (ICustomAction.ACTION_FUNC_SPECIAL_APP_GOTHEME.equals(action)) {
				// 如果是GO主题图标则将AppDataEngine中的数据也一起删掉
				// 保存删除状态
				FunAppSetting funAppSetting = SettingProxy.getFunAppSetting();
				if (funAppSetting != null) {
					funAppSetting.setShowGoStoreAndGoTheme(false,
							FunAppSetting.FUNC_APP_TYPE_GOTHEME);
				}
				ret = true;
			} else if (ICustomAction.ACTION_FUNC_SPECIAL_APP_GOWIDGET.equals(action)) {
				// 如果是gowidget图标则将AppDataEngine中的数据也一起删掉
				// 保存删除状态
				FunAppSetting funAppSetting = SettingProxy.getFunAppSetting();
				if (funAppSetting != null) {
					funAppSetting.setShowGoStoreAndGoTheme(false,
							FunAppSetting.FUNC_APP_TYPE_GOWIDGET);
				}
				ret = true;
			} else if (ICustomAction.ACTION_FREE_THEME_ICON.equals(action)) {
				// 如果是限免主题图标则将AppDataEngine中的数据也一起删掉
				FunAppSetting funAppSetting = SettingProxy.getFunAppSetting();
				if (funAppSetting != null) {
					funAppSetting.setShowGoStoreAndGoTheme(false,
							FunAppSetting.FUNC_APP_TYPE_FREETHEME);
				}
				ret = true;
			} 
		}
		return ret;
	}
	
	/**
	 * 找不到应用时的回调接口
	 * @author wuziyi
	 *
	 */
	public interface ActiveNotFoundCallBack {
		/**
		 * 要求卸载的intent不是假图标，不是已安装应用
		 * @param intent
		 */
		public void noActiveCallBack(Intent intent);
	}
	
}
