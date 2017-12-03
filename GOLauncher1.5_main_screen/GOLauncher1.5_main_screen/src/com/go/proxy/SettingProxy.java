package com.go.proxy;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.go.util.BroadCaster;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.message.IAppManagerMsgId;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.apps.font.FontBean;
import com.jiubang.ggheart.data.CoreDataService;
import com.jiubang.ggheart.data.DataType;
import com.jiubang.ggheart.data.GoSettingControler;
import com.jiubang.ggheart.data.ICoreDataService;
import com.jiubang.ggheart.data.info.DeskLockSettingInfo;
import com.jiubang.ggheart.data.info.DeskMenuSettingInfo;
import com.jiubang.ggheart.data.info.DesktopSettingInfo;
import com.jiubang.ggheart.data.info.EffectSettingInfo;
import com.jiubang.ggheart.data.info.GestureSettingInfo;
import com.jiubang.ggheart.data.info.GravitySettingInfo;
import com.jiubang.ggheart.data.info.ScreenSettingInfo;
import com.jiubang.ggheart.data.info.ShortCutSettingInfo;
import com.jiubang.ggheart.data.info.ThemeSettingInfo;
import com.jiubang.ggheart.data.model.GoSettingDataModel;
import com.jiubang.ggheart.data.theme.ThemeManager;

/**
 * 设置代理
 * FunAppSetting中的观察者需要处理,后续处理
 * @author liuheng
 * 
 */
public class SettingProxy extends BroadCaster implements ISettingsCallback, BroadCaster.BroadCasterObserver {
	private ICoreDataService mBinder;
	private Context mContext;
	private boolean mIsBound;
	private boolean mBinding = false;

	private static SettingProxy sInstance;
	
	private static FunAppSetting sFunAppSetting;
	private static GoSettingControler sSettings;

	private SettingProxy(Context context) {
		mContext = context;
	}

	public static SettingProxy getInstance(Context context) {
		if (null == sInstance) {
			synchronized (SettingProxy.class) {
				if (null == sInstance) {
					sInstance = new SettingProxy(context);
				}
			}
		}

		return sInstance;
	}
	/*
	private static GoSettingControler getSettingControler() {
		if (null == sSettings) {
			synchronized (SettingProxy.class) {
				if (null == sSettings && null != sInstance) {
					sSettings =  new GoSettingControler(sInstance.mContext);
					sInstance.doBindService();
				}
			}
		}
		
		return sSettings;
	}
	*/
	
	private static GoSettingControler getSettingControler() {
		if (null == sSettings) {
			synchronized (SettingProxy.class) {
				if (null == sSettings) {
					sSettings =  new GoSettingControler(ApplicationProxy.getContext());
				}
			}
		}
		
		return sSettings;
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mBinder = ICoreDataService.Stub.asInterface(service);			
			sSettings = null;
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			mBinder = null;
			mIsBound = false;
		}
	};

	public void doBindService() {
		if (mIsBound) {
			return;
		}
		
		if (mBinding) {
			return;
		}
		
		mBinding = true;
		try {
			mContext.bindService(new Intent(mContext, CoreDataService.class),
					mConnection, Context.BIND_AUTO_CREATE);
			mIsBound = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		mBinding = false;
	}

	public void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			mContext.unbindService(mConnection);
			mIsBound = false;
		}
	}
	
	public static void addSettingCallback(ISettingsCallback callback) {
		if (null == sInstance) {
			return;
		}
		
		if (null == sInstance.mBinder) {
			sInstance.doBindService();
			return;
		}
		
		try {
			sInstance.mBinder.addSettingCallback(callback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * 强行写死不用Service的方式
	 * @return
	 */
	private static boolean isServiceBinded() {
		/*
		if (null == sInstance) {
			return false;
		}

		if (null == sInstance.mBinder) {
			sInstance.doBindService();
			return false;
		}

		return true;
		*/
		return false;
	}
	
	public static ScreenSettingInfo getScreenSettingInfo() {
		if (isServiceBinded()) {
			try {
				return sInstance.mBinder.getScreenSettingInfo();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
			
		return getSettingControler().getScreenSettingInfo();
	}
	
	public static FunAppSetting getFunAppSetting() {
		if (null == sFunAppSetting) {
			synchronized (SettingProxy.class) {
				if (null == sFunAppSetting) {
					ThemeManager themeManager = ThemeManager.getInstance(ApplicationProxy.getContext());
					GoSettingDataModel modle = new GoSettingDataModel(ApplicationProxy.getContext());
					sFunAppSetting = new FunAppSetting(ApplicationProxy.getContext(), modle, themeManager);
				}
			}
		}
		
		return sFunAppSetting;
	}

	public static DesktopSettingInfo getDesktopSettingInfo() {
		if (isServiceBinded()) {
			try {
				return sInstance.mBinder.getDesktopSettingInfo();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		return getSettingControler().getDesktopSettingInfo();
	}

	public static EffectSettingInfo getEffectSettingInfo() {
		if (isServiceBinded()) {
			try {
				return sInstance.mBinder.getEffectSettingInfo();
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}
		
		return getSettingControler().getEffectSettingInfo();
	}

	public static ThemeSettingInfo getThemeSettingInfo() {
		if (isServiceBinded()) {
			try {
				return sInstance.mBinder.getThemeSettingInfo();
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		return getSettingControler().getThemeSettingInfo();
	}

	public static ShortCutSettingInfo getShortCutSettingInfo() {
		if (isServiceBinded()) {
			try {
				return sInstance.mBinder.getShortCutSettingInfo();
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		return getSettingControler().getShortCutSettingInfo();
	}

	public static GravitySettingInfo getGravitySettingInfo() {
		if (isServiceBinded()) {
			try {
				return sInstance.mBinder.getGravitySettingInfo();
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		return getSettingControler().getGravitySettingInfo();
	}

	public static GestureSettingInfo getGestureSettingInfo(int gestureId) {
		if (isServiceBinded()) {
			try {
				return sInstance.mBinder.getGestureSettingInfo(gestureId);
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		return getSettingControler().getGestureSettingInfo(gestureId);
	}

	public static DeskLockSettingInfo getDeskLockSettingInfo() {
		if (isServiceBinded()) {
			try {
				return sInstance.mBinder.getDeskLockSettingInfo();
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		return getSettingControler().getDeskLockSettingInfo();
	}

	public static ShortCutSettingInfo getDefaultThemeShortCutSettingInfo(String pkg) {
		if (isServiceBinded()) {
			try {
				return sInstance.mBinder.getDefaultThemeShortCutSettingInfo(pkg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		return getSettingControler().getDefaultThemeShortCutSettingInfo(pkg);
	}

	public static void cleanup() {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.cleanup();
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().cleanup();
	}
	
	public static void updateDesLockSettingInfo(DeskLockSettingInfo info) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateDesLockSettingInfo(info);
				sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_DESKTOPSETING, info);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().updateDesLockSettingInfo(info);
		sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_DESKTOPSETING, info);
	}
	
	public static void updateShortCutStyleByPackage(String packageName, String style) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateShortCutStyleByPackage(packageName, style);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		};

		getSettingControler().updateShortCutStyleByPackage(packageName, style);		
	}
	
	public static void clearDockSettingInfo() {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.clearDockSettingInfo();
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().clearDockSettingInfo();		
	}
	
	public static void resetShortCutBg(String useThemeName, String targetThemeName, String resName) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.resetShortCutBg(useThemeName, targetThemeName, resName);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().resetShortCutBg(useThemeName, targetThemeName, resName);		
	}
	
	public static void clearDirtyStyleSetting(String uninstallPackageName) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.clearDirtyStyleSetting(uninstallPackageName);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().clearDirtyStyleSetting(uninstallPackageName);		
	}
	
	public static void addScreenStyleSetting(String packageName) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.addScreenStyleSetting(packageName);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().addScreenStyleSetting(packageName);		
	}
	
	public static void updateEnable(boolean bool) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateEnable(bool);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().updateEnable(bool);		
	}
	
	public static boolean updateShortCutCustomBg(boolean iscustom) {
		if (isServiceBinded()) {
			try {
				return sInstance.mBinder.updateShortCutCustomBg(iscustom);
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		return getSettingControler().updateShortCutCustomBg(iscustom);
	}
	
	public static boolean updateShortCutBg(String useThemeName, String targetThemeName, String resName, boolean isCustomPic) {
		if (isServiceBinded()) {
			try {
				return sInstance.mBinder.updateShortCutBg(useThemeName, targetThemeName, resName, isCustomPic);
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		return getSettingControler().updateShortCutBg(useThemeName, targetThemeName, resName, isCustomPic);
	}
	
	public static void updateCurThemeShortCutSettingCustomBgSwitch(boolean isOn) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateCurThemeShortCutSettingCustomBgSwitch(isOn);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		getSettingControler().updateCurThemeShortCutSettingCustomBgSwitch(isOn);		
	}
	
	public static void updateCurThemeShortCutSettingBgSwitch(boolean isOn) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateCurThemeShortCutSettingBgSwitch(isOn);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().updateCurThemeShortCutSettingBgSwitch(isOn);		
	}
	
	public static void updateCurThemeShortCutSettingStyle(String style) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateCurThemeShortCutSettingStyle(style);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().updateCurThemeShortCutSettingStyle(style);		
	}
	
	public static int updateShortCutSettingNonIndepenceTheme(ShortCutSettingInfo info) {
		if (isServiceBinded()) {
			try {
				return sInstance.mBinder.updateShortCutSettingNonIndepenceTheme(info);
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		return getSettingControler().updateShortCutSettingNonIndepenceTheme(info);
	}
	
	public static void updateShortcutSettingInfo() {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateShortcutSettingInfo();
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().updateShortcutSettingInfo();		
	}
	
	@Deprecated
	public static void updateUsedFontBean(FontBean bean) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateUsedFontBean(bean);
				sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_DESKFONTCHANGED, bean);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().updateUsedFontBean(bean);	
		sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_DESKFONTCHANGED, bean);	
	}
	
	public static void updateScreenIndicatorThemeBean(String packageName) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateScreenIndicatorThemeBean(packageName);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().updateScreenIndicatorThemeBean(packageName);		
	}
	
	public static void updateThemeSettingInfo(ThemeSettingInfo info) {
		updateThemeSettingInfo2(info, true);		
	}
	
	public static void updateThemeSettingInfo2(ThemeSettingInfo info, boolean broadCast) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateThemeSettingInfo2(info, broadCast);
				if (broadCast) {
					sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_THEMESETTING, info);
				}
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().updateThemeSettingInfo2(info, broadCast);
		if (broadCast) {
			sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_THEMESETTING, info);
		}
	}
	
	public static void updateScreenSettingInfo(ScreenSettingInfo info) {
		updateScreenSettingInfo2(info, true);		
	}
	
	public static void updateScreenSettingInfo2(ScreenSettingInfo info, boolean broadCaset) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateScreenSettingInfo2(info, broadCaset);
				if (broadCaset) {
					sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_SCREENSETTING, info);
				}
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().updateScreenSettingInfo2(info, broadCaset);
		if (broadCaset) {
			sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_SCREENSETTING, info);
		}
	}
	
	public static void updateGravitySettingInfo(GravitySettingInfo info) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateGravitySettingInfo(info);
				sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_GRAVITYSETTING, info);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().updateGravitySettingInfo(info);	
		sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_GRAVITYSETTING, info);	
	}
	
	public static void updateGestureSettingInfo(int type, GestureSettingInfo info) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateGestureSettingInfo(type, info);
				sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_GESTURESETTING, info);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().updateGestureSettingInfo(type, info);		
		sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_GESTURESETTING, info);
	}
	
	public static void updateEffectSettingInfo(EffectSettingInfo info) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateEffectSettingInfo(info);
				sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_EFFECTSETTING, info);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().updateEffectSettingInfo(info);	
		sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_EFFECTSETTING, info);	
	}
	
	public static void updateDesktopSettingInfo2(DesktopSettingInfo info, boolean broadCast) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateDesktopSettingInfo2(info, broadCast);
				if (broadCast) {
					sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_DESKTOPSETING, info);
				}
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().updateDesktopSettingInfo2(info, broadCast);
		if (broadCast) {
			sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_DESKTOPSETING, info);
		}
	}
	
	public static void updateDesktopSettingInfo(DesktopSettingInfo info) {
		updateDesktopSettingInfo2(info, true);
	}
	
	public static void updateDeskMenuSettingInfo(DeskMenuSettingInfo info) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateDeskMenuSettingInfo(info);
				sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_DESKMENUSETTING, info);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().updateDeskMenuSettingInfo(info);
		sInstance.broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_DESKMENUSETTING, info);
	}
	
	@Deprecated
	public static void updateFontBeans(ArrayList<FontBean> beans) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.updateFontBeans(beans);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().updateFontBeans(beans);		
	}
	
	public static void enablePurchaseFunction(int functionId) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.enablePurchaseFunction(functionId);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().enablePurchaseFunction(functionId);		
	}
	
	public static void onFunctionTrialExpired(int forceCLear) {
		if (isServiceBinded()) {
			try {
				sInstance.mBinder.onFunctionTrialExpired(forceCLear);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}

		getSettingControler().onFunctionTrialExpired(forceCLear);		
	}
	
	public static boolean addAppFuncSettingBinder(String pkgName) {
		if (isServiceBinded()) {
			try {
				return sInstance.mBinder.addAppFuncSetting(pkgName);
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}
		
		GoSettingDataModel modle = getSettingControler().getDataModle();
		modle.addAppFuncSetting(pkgName);
		return true;
	}
	
	public static String getAppFuncSettingBinder(String pkname, int key) {
		if (isServiceBinded()) {
			try {
				return sInstance.mBinder.getAppFuncSetting(pkname, key);
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}
		
		GoSettingDataModel modle = getSettingControler().getDataModle();
		return modle.getAppFuncSetting(pkname, key);
	}
	
	public static boolean setAppFuncSetting(String pkname, int key, String value) {
		if (isServiceBinded()) {
			try {
				return sInstance.mBinder.setAppFuncSetting(pkname, key, value);
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}
		
		GoSettingDataModel modle = getSettingControler().getDataModle();
		modle.setAppFuncSetting(pkname, key, value);
		return true;
	}

	@Override
	public IBinder asBinder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void settingsChagedInt(int settingId, int value)
			throws RemoteException {
	}

	@Override
	public void settingsChagedBoolean(int settingId, boolean value)
			throws RemoteException {
	}

	@Override
	public void settingsChagedString(int settingId, String value)
			throws RemoteException {
	}

	@Override
	public void settingsChagedBundle(int settingId, Bundle value)
			throws RemoteException {
	}
	
	public void broadCastEffectChanged(int msgId, int param, EffectSettingInfo info) {
		if (null != sInstance) {
			sInstance.broadCast(msgId, param, info);
		}
	}
	
	// 
	@Override
	public void onBCChange(int msgId, int param, Object ...object) {
		switch (msgId) {
			case IAppCoreMsgId.EVENT_THEME_CHANGED :
				SettingProxy.getFunAppSetting().checkAppSetting();
				break;
			case IAppManagerMsgId.FUNCTION_ITEM_PURCHASED:
				SettingProxy.enablePurchaseFunction(param);
				break;
			case IAppManagerMsgId.FUNCTION_TRIALEXPIRED:
				SettingProxy.onFunctionTrialExpired(param);
				break;
			default :
				break;
		}
	}
}