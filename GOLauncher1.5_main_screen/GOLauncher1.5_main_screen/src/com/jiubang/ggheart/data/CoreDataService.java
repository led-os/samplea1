package com.jiubang.ggheart.data;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.RemoteException;

import com.go.proxy.ISettingsCallback;
import com.jiubang.ggheart.apps.desks.dock.DockUtil;
import com.jiubang.ggheart.apps.font.FontBean;
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

/**
 * 数据部分
 * 数据库相关操作部分
 * @author liuheng
 *
 */
public class CoreDataService extends Service {
	private GoSettingControler mGoSettingControler;
	
	ICoreDataService.Stub mStub = new ICoreDataService.Stub() {
		
		@Override
		public void addSettingCallback(ISettingsCallback callback)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.addSettingCallback(callback);
		}
		
		@Override
		public void removeSettingCallback(ISettingsCallback callback)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.removeSettingCallback(callback);
		}

		@Override
		public ScreenSettingInfo getScreenSettingInfo() throws RemoteException {
			if (null == mGoSettingControler) {
				return null;
			}
			
			return mGoSettingControler.getScreenSettingInfo();
		}

		@Override
		public DesktopSettingInfo getDesktopSettingInfo()
				throws RemoteException {
			if (null == mGoSettingControler) {
				return null;
			}
			
			return mGoSettingControler.getDesktopSettingInfo();
		}

		@Override
		public EffectSettingInfo getEffectSettingInfo() throws RemoteException {
			if (null == mGoSettingControler) {
				return null;
			}
			
			return mGoSettingControler.getEffectSettingInfo();
		}

		@Override
		public ThemeSettingInfo getThemeSettingInfo() throws RemoteException {
			if (null == mGoSettingControler) {
				return null;
			}
			
			return mGoSettingControler.getThemeSettingInfo();
		}

		@Override
		public ShortCutSettingInfo getShortCutSettingInfo()
				throws RemoteException {
			if (null == mGoSettingControler) {
				return null;
			}
			
			return mGoSettingControler.getShortCutSettingInfo();
		}

		@Override
		public GravitySettingInfo getGravitySettingInfo()
				throws RemoteException {
			if (null == mGoSettingControler) {
				return null;
			}
			
			return mGoSettingControler.getGravitySettingInfo();
		}

		@Override
		public ShortCutSettingInfo getDefaultThemeShortCutSettingInfo(String pkg)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return null;
			}
			
			return mGoSettingControler.getDefaultThemeShortCutSettingInfo(pkg);
		}

		@Override
		public DeskLockSettingInfo getDeskLockSettingInfo()
				throws RemoteException {
			if (null == mGoSettingControler) {
				return null;
			}
			
			return mGoSettingControler.getDeskLockSettingInfo();
		}

		@Override
		public GestureSettingInfo getGestureSettingInfo(int gestureId)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return null;
			}
			
			return mGoSettingControler.getGestureSettingInfo(gestureId);
		}

		@Override
		public void cleanup() throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.cleanup();
		}

		@Override
		public void updateDesLockSettingInfo(DeskLockSettingInfo info)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateDesLockSettingInfo(info);			
		}

		@Override
		public void updateShortCutStyleByPackage(String packageName,
				String style) throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateShortCutStyleByPackage(packageName, style);			
		}

		@Override
		public void clearDockSettingInfo() throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.clearDockSettingInfo();			
		}

		@Override
		public void resetShortCutBg(String useThemeName,
				String targetThemeName, String resName) throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.resetShortCutBg(useThemeName, targetThemeName, resName);			
		}

		@Override
		public void clearDirtyStyleSetting(String uninstallPackageName)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.clearDirtyStyleSetting(uninstallPackageName);			
		}

		@Override
		public void addScreenStyleSetting(String packageName)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.addScreenStyleSetting(packageName);				
		}

		@Override
		public void updateEnable(boolean val) throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateEnable(val);			
		}

		@Override
		public boolean updateShortCutCustomBg(boolean iscustom)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return false;
			}
			
			return mGoSettingControler.updateShortCutCustomBg(iscustom);
		}

		@Override
		public boolean updateShortCutBg(String useThemeName,
				String targetThemeName, String resName, boolean isCustomPic)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return false;
			}
			
			return mGoSettingControler.updateShortCutBg(useThemeName, targetThemeName, resName, isCustomPic);
		}

		@Override
		public void updateCurThemeShortCutSettingCustomBgSwitch(boolean isOn)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateCurThemeShortCutSettingCustomBgSwitch(isOn);			
		}

		@Override
		public void updateCurThemeShortCutSettingBgSwitch(boolean isOn)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateCurThemeShortCutSettingBgSwitch(isOn);			
		}

		@Override
		public void updateCurThemeShortCutSettingStyle(String style)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateCurThemeShortCutSettingStyle(style);			
		}

		@Override
		public int updateShortCutSettingNonIndepenceTheme(
				ShortCutSettingInfo info) throws RemoteException {
			if (null == mGoSettingControler) {
				return DockUtil.ERROR_BAD_PARAM;
			}
			
			return mGoSettingControler.updateShortCutSettingNonIndepenceTheme(info);
		}

		@Override
		public void updateShortcutSettingInfo() throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateShortcutSettingInfo();			
		}

		@Override
		public void updateUsedFontBean(FontBean bean) throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateUsedFontBean(bean);			
		}

		@Override
		public void updateScreenIndicatorThemeBean(String packageName)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateScreenIndicatorThemeBean(packageName);			
		}

		@Override
		public void updateThemeSettingInfo(ThemeSettingInfo info)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateThemeSettingInfo(info);				
		}

		@Override
		public void updateThemeSettingInfo2(ThemeSettingInfo info,
				boolean broadCast) throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateThemeSettingInfo2(info, broadCast);			
		}

		@Override
		public void updateScreenSettingInfo(ScreenSettingInfo info)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateScreenSettingInfo(info);				
		}

		@Override
		public void updateScreenSettingInfo2(ScreenSettingInfo info,
				boolean broadCaset) throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateScreenSettingInfo2(info, broadCaset);			
		}

		@Override
		public void updateGravitySettingInfo(GravitySettingInfo info)
				throws RemoteException {

			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateGravitySettingInfo(info);				
		}

		@Override
		public void updateGestureSettingInfo(int type, GestureSettingInfo info)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateGestureSettingInfo(type, info);			
		}

		@Override
		public void updateEffectSettingInfo(EffectSettingInfo info)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateEffectSettingInfo(info);			
		}

		@Override
		public void updateDesktopSettingInfo2(DesktopSettingInfo info,
				boolean broadCast) throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateDesktopSettingInfo2(info, broadCast);			
		}

		@Override
		public void updateDesktopSettingInfo(DesktopSettingInfo info)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateDesktopSettingInfo(info);			
		}

		@Override
		public void updateDeskMenuSettingInfo(DeskMenuSettingInfo info)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateDeskMenuSettingInfo(info);			
		}

		@Override
		public void updateFontBeans(List<FontBean> beans)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.updateFontBeans((ArrayList) beans);			
		}

		@Override
		public void enablePurchaseFunction(int functionId)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.enablePurchaseFunction(functionId);			
		}

		@Override
		public void onFunctionTrialExpired(int forceClear) throws RemoteException {
			if (null == mGoSettingControler) {
				return;
			}
			
			mGoSettingControler.onFunctionTrialExpired(forceClear);			
		}

		@Override
		public boolean addAppFuncSetting(String packageName)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return false;
			}
			
			GoSettingDataModel modle = mGoSettingControler.getDataModle();
			modle.addAppFuncSetting(packageName);
			return true;
		}

		@Override
		public String getAppFuncSetting(String pkname, int key)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return null;
			}
			
			GoSettingDataModel modle = mGoSettingControler.getDataModle();
			return modle.getAppFuncSetting(pkname, key);
		}

		@Override
		public boolean setAppFuncSetting(String pkname, int key, String value)
				throws RemoteException {
			if (null == mGoSettingControler) {
				return false;
			}
			
			GoSettingDataModel modle = mGoSettingControler.getDataModle();
			modle.setAppFuncSetting(pkname, key, value);
			return true;
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return mStub;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mGoSettingControler = new GoSettingControler(getApplicationContext());
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}
	
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
	}

}
