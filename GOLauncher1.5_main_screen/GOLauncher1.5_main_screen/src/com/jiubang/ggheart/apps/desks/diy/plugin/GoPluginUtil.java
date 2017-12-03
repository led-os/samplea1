package com.jiubang.ggheart.apps.desks.diy.plugin;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import com.gau.utils.net.HttpAdapter;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.go.util.AppUtils;
import com.jiubang.ggheart.apps.desks.appfunc.appsupdate.AppsListUpdateManager;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.gowidget.gostore.net.SimpleHttpAdapter;
import com.jiubang.ggheart.apps.gowidget.gostore.net.databean.AppsBean;
import com.jiubang.ggheart.apps.gowidget.gostore.net.databean.BaseBean;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.data.statistics.StatisticsAppsInfoData;
import com.jiubang.ggheart.launcher.ICustomAction;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 */
public class GoPluginUtil {
	
	public static void checkPluginUpdate(Context context) {
		AppsListUpdateManager appsListUpdateManager = AppsListUpdateManager.getInstance(context);
		if (appsListUpdateManager != null) {
			// 设置监听者
			HttpAdapter httpAdapter = SimpleHttpAdapter.getHttpAdapter(context);
			IConnectListener receiver = getConnectListener(context);
			appsListUpdateManager.startCheckUpdate(httpAdapter, receiver, false, 0, AppsListUpdateManager.sAPPSTYPE_GOPLUGIN);
		}
	}
	
	public static String getGoPlufins(Context context) {
		ArrayList<GoPluginOrWidgetInfo> allInfos = new ArrayList<GoPluginOrWidgetInfo>();
		ArrayList<GoPluginOrWidgetInfo> installInfos = new ArrayList<GoPluginOrWidgetInfo>();
		ArrayList<GoPluginOrWidgetInfo> noinstallInfos = new ArrayList<GoPluginOrWidgetInfo>();
		PluginXmlParser parser = new PluginXmlParser();
		parser.parsePlugin(false, context, true, allInfos, installInfos, noinstallInfos);
		parser.parsePlugin(true, context, true, allInfos, installInfos, noinstallInfos);

		PackageManager pm = context.getPackageManager();
		final String comma = ",";
		final String sharp = "#";
		StringBuffer sb = new StringBuffer();
		final int count = installInfos.size();
		for (int i = 0; i < count; i++) {
			GoPluginOrWidgetInfo goPluginOrWidgetInfo = installInfos.get(i);

			String pkg = goPluginOrWidgetInfo.mWidgetPkgName;
			int versioncode = AppUtils.getVersionCodeByPkgName(context, pkg);
			String versionname = AppUtils.getVersionNameByPkgName(context, pkg);
					
			// 包名
			sb.append(pkg);
			sb.append(comma);
			// 点击数
			sb.append(1);
			sb.append(comma);
			// 版本号
			sb.append(versioncode);
			sb.append(comma);
			// 版本名称
			sb.append(versionname);
			sb.append(comma);

			// 增加是否内置标志
			sb.append(0);

			sb.append(comma);
			// 非200的渠道，要添加apk签名
			if (!GoStorePhoneStateUtil.is200ChannelUid(context)) {
				// 对于2.2机型以下，不取签名信息
				if (Build.VERSION.SDK_INT >= 8) {
					sb.append(StatisticsAppsInfoData.getAppSignature(pm, pkg));
				}
			}

			// 上传apk size
			sb.append(comma);
			sb.append(StatisticsAppsInfoData.getApkSize(pm, pkg));

			sb.append(sharp);
		}
		
		return sb.toString();
	}
	
	private static IConnectListener getConnectListener(final Context context) {
		IConnectListener receiver = new IConnectListener() {

			@Override
			public void onStart(THttpRequest arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onFinish(THttpRequest arg0, IResponse arg1) {
				// 1、清理统计数据
				// StatisticsAppsInfoData.resetStatisticsAllDataInfos(mContext);
				// 2、可以更新的数据bean
				AppsBean appsBean = null;
				if (arg1 != null) {
					ArrayList<BaseBean> listBeans = (ArrayList<BaseBean>) arg1.getResponse();
					if (listBeans != null && listBeans.size() > 0) {
						StringBuffer sb = new StringBuffer();
						if (listBeans != null && listBeans.size() > 0) {
							appsBean = (AppsBean) listBeans.get(0);
							if (appsBean != null && appsBean.mListBeans != null
									&& appsBean.mListBeans.size() > 0) {
								for (AppsBean.AppBean bean : appsBean.mListBeans) {
									sb.append(bean.mPkgName);
									sb.append("#");
								}
								//更新sharepreference
								PreferencesManager preferences = new PreferencesManager(context,
										IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
								preferences.putString(IPreferencesIds.NEED_UPDATE_GOPLUGINS, sb.toString());
								preferences.commit();
								//通知更新列表界面
								context.sendBroadcast(new Intent(ICustomAction.ACTION_NEED_UPDATE_GOPLUGINS));
							}
						}
					}
				}
			}

			@Override
			public void onException(THttpRequest arg0, int arg1) {
				// do nothing
			}
		};

		return receiver;
	}
}
