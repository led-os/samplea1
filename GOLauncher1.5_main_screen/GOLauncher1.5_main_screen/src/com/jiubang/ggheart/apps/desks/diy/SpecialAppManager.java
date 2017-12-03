package com.jiubang.ggheart.apps.desks.diy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.golauncher.message.IAppCoreMsgId;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager.AsyncImageLoadedCallBack;
import com.jiubang.ggheart.apps.desks.diy.AppInvoker.IAppInvokerListener;
import com.jiubang.ggheart.common.controler.CommonControler;
import com.jiubang.ggheart.common.data.AppExtraAttribute;
import com.jiubang.ggheart.common.data.SpecialApplicationDataModel;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.GoThemeAppItemInfo;
import com.jiubang.ggheart.data.info.LightGameAppItemInfo;
import com.jiubang.ggheart.data.info.ProManageAppItemInfo;
import com.jiubang.ggheart.data.info.RecentAppItemInfo;
import com.jiubang.ggheart.data.info.SpecialAppItemInfo;
import com.jiubang.ggheart.data.tables.SpecialApplicationTable;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.smartcard.Recommanditem;

/**
 * 假图标info管理类
 * @author yangguanxiang
 *
 */
public class SpecialAppManager {
	private static SpecialAppManager sInstance;

	private Context mContext;
	private HashMap<ComponentName, SpecialAppItemInfo> mMap;
	private SpecialApplicationDataModel mDataModel;
	private CommonControler mCommonControler;
	private AppDataEngine mAppDataEngine;
	public static SpecialAppManager getInstance() {
		if (sInstance == null) {
			sInstance = new SpecialAppManager();
		}
		return sInstance;
	}

	public SpecialAppManager() {
		mContext = ApplicationProxy.getContext();
		mMap = new HashMap<ComponentName, SpecialAppItemInfo>();
		mDataModel = new SpecialApplicationDataModel(ApplicationProxy.getContext());
		mCommonControler = CommonControler.getInstance(mContext);
		mAppDataEngine = AppDataEngine.getInstance(mContext);
	}

	public void initSpecialApps() {
		Cursor cursor = mDataModel.getAllSpecialApps();
		if (cursor != null) {
			try {
				final int cnIndex = cursor.getColumnIndex(SpecialApplicationTable.COMPONENT_NAME);
				if (cursor.moveToFirst()) {
					do {
						String cName = cursor.getString(cnIndex);
						ComponentName com = ComponentName.unflattenFromString(cName);
						String action = com.getClassName();
						SpecialAppItemInfo appItemInfo = produce(action);
						if (appItemInfo != null) {
							AppExtraAttribute attr = mCommonControler.getAppExtraAtturibute(com);
							if (attr != null) {
								appItemInfo.setExtraAtturibute(attr);
							}
							mMap.put(com, appItemInfo);
							mAppDataEngine.addSpecialAppInfo(appItemInfo);
						}
					} while (cursor.moveToNext());
				}
			} finally {
				cursor.close();
			}
		}
	}

	public SpecialAppItemInfo getAppItemInfo(Intent intent) {
		SpecialAppItemInfo info = null;
		if (intent != null) {
			ComponentName com = intent.getComponent();
			if (com != null) {
				if (mMap.containsKey(com)) {
					info = mMap.get(com);
				}
			}
		}
		return info;
	}

	public ArrayList<SpecialAppItemInfo> getAppItemInfos(String action) {
		ArrayList<SpecialAppItemInfo> infoList = new ArrayList<SpecialAppItemInfo>();
		Set<ComponentName> keySet = mMap.keySet();
		for (ComponentName com : keySet) {
			if (com.getClassName().equals(action) || com.getClassName().startsWith(action)) {
				infoList.add(mMap.get(com));
			}
		}
		return infoList;
	}
	
	public ArrayList<SpecialAppItemInfo> getAllAppItemInfos() {
		ArrayList<SpecialAppItemInfo> infoList = new ArrayList<SpecialAppItemInfo>();
		Set<ComponentName> keySet = mMap.keySet();
		for (ComponentName com : keySet) {
			infoList.add(mMap.get(com));
		}
		return infoList;
	}

	private SpecialAppItemInfo produce(String action) {
		SpecialAppItemInfo info = null;
		if (action != null) {
			if (ICustomAction.ACTION_RECENTAPP.equals(action)) {
				info = new RecentAppItemInfo();
			} else if (ICustomAction.ACTION_PROMANAGE.equals(action)) {
				info = new ProManageAppItemInfo();
			} else if (ICustomAction.ACTION_FUNC_SPECIAL_APP_GOTHEME.equals(action)) {
				info = new GoThemeAppItemInfo();
			} else if (action.startsWith(ICustomAction.ACTION_LIGHTGAME)) {
				info = new LightGameAppItemInfo();
			}
		}
		return info;
	}

	public boolean invoke(Intent intent, ArrayList<IAppInvokerListener> listeners, int from) {
		SpecialAppItemInfo info = mMap.get(intent.getComponent());
		if (info != null) {
			return info.invoke(listeners, from);
		} else { //如果从缓存拿不到，就从AppDataEngine拿
			AppItemInfo appItemInfo = AppDataEngine.getInstance(ApplicationProxy.getContext())
					.getAppItem(intent);
			if (appItemInfo instanceof SpecialAppItemInfo) {
				return ((SpecialAppItemInfo) appItemInfo).doInvoke(from);
			}
			return false;
		}
	}

	public boolean isSpecialAppExist(String packageName, String action) {
		ComponentName com = new ComponentName(packageName, action);
		return mMap.containsKey(com);
	}

	public boolean installSpecialApp(String packageName, String action, Object source,
			Object... objs) {
		boolean installSuccess = false;
		boolean isExist = true;
		Recommanditem item = null;
		if (PackageName.RECOMMEND_APP_PACKAGE.equals(packageName)
				&& ICustomAction.ACTION_LIGHTGAME.equals(action)) {
			item = (Recommanditem) objs[0];
			isExist = isSpecialAppExist(packageName,
					LightGameAppItemInfo.combineLightGameAction(item.getMapId()));
		}
		if (!isExist) {
			SpecialAppItemInfo appItemInfo = produce(action);
			if (item != null && appItemInfo instanceof LightGameAppItemInfo) {
				((LightGameAppItemInfo) appItemInfo).putExtra(item.getMapId(), item.getAppName(),
						item.getDownloadUrl(), item.getIconUrl());
			}
			try {
				appItemInfo.setIsNewApp(true);
				mDataModel.addSpecialApp(appItemInfo);
				boolean success = AppDataEngine.getInstance(mContext)
						.addSpecialAppInfo(appItemInfo);
				if (success) {
					appItemInfo.setIsNewApp(true);
					ArrayList<AppItemInfo> appItemInfos = new ArrayList<AppItemInfo>();
					appItemInfos.add(appItemInfo);
					MsgMgrProxy.sendBroadcast(this, IAppCoreMsgId.EVENT_INSTALL_APP, 0,
							packageName, appItemInfos, source);
					installSuccess = true;
				}
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
			if (installSuccess) {
				DeskToast.makeText(mContext, R.string.add_special_app_success, Toast.LENGTH_SHORT)
						.show();
			} else {
				DeskToast.makeText(mContext, R.string.add_special_app_failed, Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			DeskToast.makeText(mContext, R.string.app_exist, Toast.LENGTH_SHORT).show();
		}
		return installSuccess;
	}

	public boolean uninstallSpecialApp(Intent intent) {
		boolean ret = false;
		AppDataEngine appDataEngine = AppDataEngine.getInstance(mContext);
		AppItemInfo info = mMap.remove(intent.getComponent());
		if (info == null) {
			info = appDataEngine.getAppItem(intent);
		}
		if (info != null && info instanceof SpecialAppItemInfo) {
			AppExtraAttribute attr = info.getExtraAtturibute();
			try {
				mDataModel.removeSpecialApp((SpecialAppItemInfo) info);
				mCommonControler.removeAppExtraAtturibute(attr);
				ret = appDataEngine.removeSpecialAppInfo(intent.getComponent());
				if (ret) {
					ArrayList<AppItemInfo> removeAppItems = new ArrayList<AppItemInfo>();
					removeAppItems.add(info);
					// 对外广播，通知观察者卸载了程序
					MsgMgrProxy.sendBroadcast(this, IAppCoreMsgId.EVENT_UNINSTALL_APP, 0,
							intent.getPackage(), removeAppItems);
				}
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public Drawable getSpecialAppIcon(Intent intent, AsyncImageLoadedCallBack callback) {
		SpecialAppItemInfo appItemInfo = getAppItemInfo(intent);
		if (appItemInfo != null) {
			return appItemInfo.getSpecialAppIcon(callback);
		}
		return null;
	}
}
