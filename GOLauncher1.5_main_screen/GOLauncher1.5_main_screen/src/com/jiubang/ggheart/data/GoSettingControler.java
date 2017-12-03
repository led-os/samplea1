package com.jiubang.ggheart.data;

import java.util.ArrayList;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.ISettingsCallback;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.proxy.VersionControl;
import com.go.util.device.Machine;
import com.go.util.graphics.effector.united.EffectorControler;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IDockMsgId;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.core.framework.ICleanable;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.ScreenIndicator;
import com.jiubang.ggheart.apps.desks.dock.DockUtil;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.apps.font.FontBean;
import com.jiubang.ggheart.data.info.AppSettingDefault;
import com.jiubang.ggheart.data.info.DeskLockSettingInfo;
import com.jiubang.ggheart.data.info.DeskMenuSettingInfo;
import com.jiubang.ggheart.data.info.DesktopSettingInfo;
import com.jiubang.ggheart.data.info.EffectSettingInfo;
import com.jiubang.ggheart.data.info.GestureSettingInfo;
import com.jiubang.ggheart.data.info.GravitySettingInfo;
import com.jiubang.ggheart.data.info.ScreenSettingInfo;
import com.jiubang.ggheart.data.info.ShortCutSettingInfo;
import com.jiubang.ggheart.data.info.ThemeSettingInfo;
import com.jiubang.ggheart.data.model.GoContentProviderUtil;
import com.jiubang.ggheart.data.model.GoSettingDataModel;
import com.jiubang.ggheart.data.theme.ThemeManager;

//CHECKSTYLE:OFF
public class GoSettingControler implements ICleanable, Parcelable {
	// 桌面设置
	private DeskMenuSettingInfo mDeskMenuSettingInfo; // 弹出菜单设置
	private DesktopSettingInfo mDesktopSettingInfo; // 桌面设置
	private EffectSettingInfo mEffectSettingInfo; // 桌面特效设置
	private GravitySettingInfo mGravitySettingInfo; // 重力感应设置
	private ScreenSettingInfo mScreenSettingInfo; // 屏幕设置
	private ThemeSettingInfo mThemeSettingInfo; // 主题设置
	private  DeskLockSettingInfo mLockSettingInfo; // 安全锁设置

	// 手势设置
	private GestureSettingInfo[] mGestureSettingInfos = new GestureSettingInfo[GestureSettingInfo.MAX_GESTURE_COUNT]; // home键设置
//	private GestureSettingInfo mHomeGestureSettingInfo; // home键设置
//	private GestureSettingInfo mUpGestureSettingInfo; // 上滑手势
//	private GestureSettingInfo mDownGestureSettingInfo; // 下滑手势
//	private GestureSettingInfo mDoubleClickGestureSettingInfo; // 双击空白处手势
//	private GestureSettingInfo mPinchOutGestureSettingInfo; // 打开手势
//	private GestureSettingInfo mSwipeUpGestureSettingInfo; //双指上划手势
//	private GestureSettingInfo mRotateCCWGestureSettingInfo; // 逆时针旋转手势
//	private GestureSettingInfo mmRotateCWGestureSettingInfo; // 顺时针旋转手势

	// 快捷条设置
	private ShortCutSettingInfo mDockSettingInfo;

	/**
	 * @deprecated
	 * replace by {@link com.jiubang.ggheart.apps.font.FontControler }
	 * @return
	 */
	// 字体
	private FontBean mUsedFontBean;

	private GoSettingDataModel mSettingDataModel = null;

	private Context mContext = null;

	private ArrayList<ISettingsCallback> mCallbacks = new ArrayList<ISettingsCallback>();

	public void addSettingCallback(ISettingsCallback callback) {
		mCallbacks.add(callback);
	}
	
	public void removeSettingCallback(ISettingsCallback callback) {
		mCallbacks.remove(callback);
	}
	
	public GoSettingControler(Context context) {
		mContext = context;
		mSettingDataModel = new GoSettingDataModel(context);
	}
	
	public GoSettingDataModel getDataModle() {
		return mSettingDataModel;
	}

	// DONE
	@Override
	public void cleanup() {
		mContext = null;
	}
	
	// TODO:无用？
	// 桌面菜单设置控制
	private DeskMenuSettingInfo getDeskMenuSettingInfo() {
		if (null == mDeskMenuSettingInfo) {
			mDeskMenuSettingInfo = createDeskMenuSettingInfo();
		}
		return mDeskMenuSettingInfo;
	}

	private DeskMenuSettingInfo createDeskMenuSettingInfo() {
		DeskMenuSettingInfo info = mSettingDataModel.getDeskMenuSettingInfo();
		if (null == info) {
			info = new DeskMenuSettingInfo();
			mSettingDataModel.cleanDeskMenuSettingInfo();
			mSettingDataModel.insertDeskMenuSettingInfo(info);
		}
		return info;
	}

	public void updateDeskMenuSettingInfo(DeskMenuSettingInfo info) {
		mDeskMenuSettingInfo = info;
		
		mSettingDataModel.updateDeskMenuSettingInfo(info);
	}

	// DONE
	// 桌面设置
	public DesktopSettingInfo getDesktopSettingInfo() {
		if (null == mDesktopSettingInfo) {
			mDesktopSettingInfo = createDesktopSettingInfo();
		}
		return mDesktopSettingInfo;
	}

	private DesktopSettingInfo createDesktopSettingInfo() {
		DesktopSettingInfo info = mSettingDataModel.getDesktopSettingInfo();
		if (null == info) {
			info = new DesktopSettingInfo();
			mSettingDataModel.cleanDesktopSettingInfo();
			mSettingDataModel.insertDesktopSettingInfo(info);
		}
		return info;
	}

	public void updateDesktopSettingInfo(DesktopSettingInfo info) {
		updateDesktopSettingInfo2(info, true);
	}

	public void updateDesktopSettingInfo2(DesktopSettingInfo info, boolean broadCast) {
		mDesktopSettingInfo = info;
		
		mSettingDataModel.updateDesktopSettingInfo(info);
		// if(bReCreatGGmenu){
		// MsgMgrProxy.sendBroadcastHandler(this,
		// IDiyMsgIds.REFRESH_GGMENU_THEME, -1, null, null);
		// }
	}

	// 特效设置 DONE
	public EffectSettingInfo getEffectSettingInfo() {
		if (null == mEffectSettingInfo) {
			mEffectSettingInfo = createEffectSettingInfo();
		}
		return mEffectSettingInfo;
	}

	private EffectSettingInfo createEffectSettingInfo() {
		EffectSettingInfo info = mSettingDataModel.getEffectSettingInfo();
		if (null == info) {
			info = new EffectSettingInfo();
			mSettingDataModel.cleanEffectSettingInfo();
			mSettingDataModel.insertEffectSettingInfo(info);
		}
		return info;
	}

	public void updateEffectSettingInfo(EffectSettingInfo info) {
		mEffectSettingInfo = info;
		mSettingDataModel.updateEffectSettingInfo(info);
	}

	// DONE
	// 手势设置
	public GestureSettingInfo getGestureSettingInfo(int type) {
		GestureSettingInfo info = getGestureSetting(type);
		if (null == info) {
			info = createGestureSettingInfo(type);
			setGestureSetting(type, info);
		}
		return info;
	}

	private GestureSettingInfo createGestureSettingInfo(int type) {
		GestureSettingInfo info = mSettingDataModel.getGestureSettingInfo(type);
		if (null == info) {
			info = new GestureSettingInfo();
			info.mGestureId = type;
			switch (type) {
				case GestureSettingInfo.GESTURE_HOME_ID :
					info.mGestureAction = GlobalSetConfig.GESTURE_GOSHORTCUT;
					info.mGoShortCut = GlobalSetConfig.GESTURE_SHOW_MAIN_SCREEN;
					break;

				case GestureSettingInfo.GESTURE_UP_ID :
					info.mGestureAction = GlobalSetConfig.GESTURE_GOSHORTCUT;
					info.mGoShortCut = GlobalSetConfig.GESTURE_SHOW_MENU;
					break;

				case GestureSettingInfo.GESTURE_DOWN_ID :
					info.mGestureAction = GlobalSetConfig.GESTURE_GOSHORTCUT;
					info.mGoShortCut = GlobalSetConfig.GESTURE_SHOW_HIDE_NOTIFICATIONEXPAND;
					break;
				case GestureSettingInfo.GESTURE_DOUBLLE_CLICK_ID :
					info.mGestureAction = GlobalSetConfig.GESTURE_GOSHORTCUT;
					info.mGoShortCut = GlobalSetConfig.GESTURE_SHOW_DIYGESTURE;
					break;
				default :
					break;
			}
			mSettingDataModel.cleanGestureSettingInfo(type);
			mSettingDataModel.insertGestureSettingInfo(type, info);
		}
		return info;
	}

	public void updateGestureSettingInfo(int type, GestureSettingInfo info) {
		setGestureSetting(type, info);
		mSettingDataModel.updateGestureSettingInfo(type, info);
	}

	private GestureSettingInfo getGestureSetting(int type) {
		/*	GestureSettingInfo info = null;
		
		switch (type) {
			case GestureSettingInfo.GESTURE_HOME_ID :
				info = mHomeGestureSettingInfo;
				break;

			case GestureSettingInfo.GESTURE_UP_ID :
				info = mUpGestureSettingInfo;
				break;

			case GestureSettingInfo.GESTURE_DOWN_ID :
				info = mDownGestureSettingInfo;
				break;
			case GestureSettingInfo.GESTURE_DOUBLLE_CLICK_ID :
				info = mDoubleClickGestureSettingInfo;
				break;
			default :
				break;
		}*/
		if (type < 1 && type > mGestureSettingInfos.length) {
			return null;
		}
		
		return mGestureSettingInfos[type-1];
	}

	private void setGestureSetting(int type, GestureSettingInfo info) {
		/*switch (type) {
			case GestureSettingInfo.GESTURE_HOME_ID :
				mHomeGestureSettingInfo = info;
				break;

			case GestureSettingInfo.GESTURE_UP_ID :
				mUpGestureSettingInfo = info;
				break;

			case GestureSettingInfo.GESTURE_DOWN_ID :
				mDownGestureSettingInfo = info;
				break;
			case GestureSettingInfo.GESTURE_DOUBLLE_CLICK_ID :
				mDoubleClickGestureSettingInfo = info;
				break;
			default :
				break;
		}*/
		if (type < 1 && type > mGestureSettingInfos.length) {
			return;
		}
		mGestureSettingInfos[type-1] = info;
	}
	
	// DONE
	// 重力感应设置
	public GravitySettingInfo getGravitySettingInfo() {
		if (null == mGravitySettingInfo) {
			mGravitySettingInfo = createGravitySettingInfo();
		}
		return mGravitySettingInfo;
	}

	private GravitySettingInfo createGravitySettingInfo() {
		GravitySettingInfo info = mSettingDataModel.getGravitySettingInfo();
		if (null == info) {
			info = new GravitySettingInfo(mContext);
			mSettingDataModel.cleanGravitySettingInfo();
			mSettingDataModel.insertGravitySettingInfo(info);
		}
		return info;
	}

	public void updateGravitySettingInfo(GravitySettingInfo info) {
		mGravitySettingInfo = info;
		mSettingDataModel.updateGravitySettingInfo(info);
	}

	// 屏幕设置,Service Done
	public ScreenSettingInfo getScreenSettingInfo() {
		if (null == mScreenSettingInfo) {
			mScreenSettingInfo = createScreenSettingInfo();
		}
		return mScreenSettingInfo;
	}

	private ScreenSettingInfo createScreenSettingInfo() {
		ScreenSettingInfo info = mSettingDataModel.getScreenSettingInfo();
		if (null == info) {
			info = new ScreenSettingInfo();
			mSettingDataModel.cleanScreenSettingInfo();
			mSettingDataModel.insertScreenSettingInfo(info);
		}
		return info;
	}

	public void updateScreenSettingInfo(ScreenSettingInfo info) {
		updateScreenSettingInfo2(info, true);
	}

	public void updateScreenIndicatorThemeBean(String packageName) {
		if (!packageName.equals(ScreenIndicator.SHOWMODE_NORMAL)
				&& !packageName.equals(ScreenIndicator.SHOWMODE_NUMERIC)) {
			AppDataEngine.getInstance(mContext).onHandleScreenIndicatorThemeIconStyleChanged(
					packageName);
		}
	}

	public void updateScreenSettingInfo2(ScreenSettingInfo info, boolean broadCaset) {
		mScreenSettingInfo = info;
		mSettingDataModel.updateScreenSettingInfo(info);
	}

	// DONE
	// 主题设置
	public ThemeSettingInfo getThemeSettingInfo() {
		if (null == mThemeSettingInfo) {
			mThemeSettingInfo = createThemeSettingInfo();
		}
		return mThemeSettingInfo;
	}

	private ThemeSettingInfo createThemeSettingInfo() {
		ThemeSettingInfo info = mSettingDataModel.getThemeSettingInfo();
		if (null == info) {
			info = new ThemeSettingInfo();
			mSettingDataModel.cleanThemeSettingInfo();
			mSettingDataModel.insertThemeSettingInfo(info);
		}
		if (VersionControl.getNewVeriosnFirstRun()
				&& VersionControl.getLastVersionCode() == 306 //4.10 versionCode
				&& Machine.isSupportAPITransparentStatusBar()
				&& !Machine.isKorea(mContext)) {
			if (!info.mTransparentStatusbar) { // 4.10升级上来，支持透明通知栏，且非韩国用户，打开透明通知栏选项
				info.mTransparentStatusbar = true;
			} 
			if (info.mIsShowStatusbarBg) { // 4.10升级上来，支持透明通知栏，且非韩国用户，关闭透明背景选项
				info.mIsShowStatusbarBg = false;
			}
			updateThemeSettingInfo2(info, false);
		}
		return info;
	}

	/**
	 * 
	 * @param info
	 * @param broadCast 是否需要发广播通知的标示
	 */
	public void updateThemeSettingInfo2(ThemeSettingInfo info, boolean broadCast) {
		mThemeSettingInfo = info;
		
		mSettingDataModel.updateThemeSettingInfo(info);
	}
	
	public void updateThemeSettingInfo(ThemeSettingInfo info) {
		updateThemeSettingInfo2(info, true);
	}
/**
 * @deprecated
 * replace by {@link com.jiubang.ggheart.apps.font.FontControler#getUsedFontBean() }
 * @return
 */
	// 字体
	private FontBean getUsedFontBean() {
		if (null != mUsedFontBean) {
			mUsedFontBean.initTypeface(mContext);
			return mUsedFontBean;
		}

		mUsedFontBean = createUsedFontBean();
		mUsedFontBean.initTypeface(mContext);
		return mUsedFontBean;
	}
	/**
	 * @deprecated
	 * replace by {@link com.jiubang.ggheart.apps.font.FontControler#createUsedFontBean() }
	 * @return
	 */
	private FontBean createUsedFontBean() {
		return mSettingDataModel.createUsedFontBean();
	}
	/**
	 * @deprecated
	 * replace by {@link com.jiubang.ggheart.apps.font.FontControler#updateUsedFontBean(FontBean bean) }
	 * @return
	 */
	public void updateUsedFontBean(FontBean bean) {
		if (null != mUsedFontBean && mUsedFontBean.equals(bean)) {
			// 没有修改
			return;
		}

		mUsedFontBean = bean;
		mUsedFontBean.initTypeface(mContext);
		mSettingDataModel.updateUsedFontBean(bean);
	}
	/**
	 * @deprecated
	 * replace by {@link com.jiubang.ggheart.apps.font.FontControler#createFontBeans() }
	 * @return
	 */
	private ArrayList<FontBean> createFontBeans() {
		return mSettingDataModel.createFontBeans();
	}
	/**
	 * @deprecated
	 * replace by {@link com.jiubang.ggheart.apps.font.FontControler#updateFontBeans(ArrayList) }
	 * @return
	 */
	public void updateFontBeans(ArrayList<FontBean> beans) {
		mSettingDataModel.updateFontBeans(beans);
	}

	/********************
	 * 快捷条设置控制部分*
	 ********************/

	public ShortCutSettingInfo getShortCutSettingInfo() {
		if (null == mDockSettingInfo) {
			mDockSettingInfo = createShortcutsettingInfo();
		}
		return mDockSettingInfo;
	}

	public void updateShortcutSettingInfo() {
		mDockSettingInfo = createShortcutsettingInfo();
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK, IDockMsgId.DOCK_SETTING_NEED_UPDATE,
				-1, null, null);
	}

	public ShortCutSettingInfo getDefaultThemeShortCutSettingInfo(String themeName) {
		return mSettingDataModel.getShortCurSetting(themeName);
	}

	/**
	 * 更新快捷设置,全局性，与主题无关的信息
	 * 
	 * @param info
	 *            设置信息
	 */
	public int updateShortCutSettingNonIndepenceTheme(ShortCutSettingInfo info) {
		if (null == info) {
			return DockUtil.ERROR_BAD_PARAM;
		}
		boolean bResult = mSettingDataModel.updateShortCutSettingNonIndepenceTheme(info);
		if (bResult) {
			mDockSettingInfo = info;
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
					IDockMsgId.DOCK_SETTING_CHANGED, -1, null, null);
		} else {
			return DockUtil.ERROR_KEEPDATA_FAILD;
		}
		return DockUtil.ERROR_NONE;
	}

	public void updateCurThemeShortCutSettingStyle(String style) {
		if (mDockSettingInfo != null) {
			mDockSettingInfo.mStyle = style;
		}
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
				IDockMsgId.DOCK_SETTING_CHANGED_STYLE, -1, style, null);
		final String themPkg = ThemeManager.getInstance(mContext).getCurThemePackage();
		mSettingDataModel.updateShortCutSettingStyle(themPkg, style);
	}

	public void updateCurThemeShortCutSettingBgSwitch(boolean isOn) {
		if (mDockSettingInfo != null) {
			mDockSettingInfo.mBgPicSwitch = isOn;
		}

		final String themPkg = ThemeManager.getInstance(mContext).getCurThemePackage();
		mSettingDataModel.updateShortCutSettingBgSwitch(themPkg, isOn);
	}

	public void updateCurThemeShortCutSettingCustomBgSwitch(boolean isOn) {
		if (mDockSettingInfo != null) {
			mDockSettingInfo.mCustomBgPicSwitch = isOn;
		}

		final String themPkg = ThemeManager.getInstance(mContext).getCurThemePackage();
		mSettingDataModel.updateShortCutSettingCustomBgSwitch(themPkg, isOn);
	}

	public boolean updateShortCutBg(String useThemeName, String targetThemeName, String resName,
			boolean isCustomPic) {
		if (mDockSettingInfo != null) {
			mDockSettingInfo.mBgtargetthemename = targetThemeName;
			mDockSettingInfo.mBgresname = resName;
			mDockSettingInfo.mBgiscustompic = isCustomPic;
		}
		return mSettingDataModel.updateShortCutBG(useThemeName, targetThemeName, resName,
				isCustomPic);
	}

	public boolean updateShortCutCustomBg(boolean iscustom) {
		if (mDockSettingInfo != null) {
			mDockSettingInfo.mBgiscustompic = iscustom;
		}
		return mSettingDataModel.updateIsCustomBg(iscustom);
	}

	public void updateEnable(boolean bool) {
		boolean updateResult = mSettingDataModel.updateShortCutSettingEnable(bool);
		if (updateResult) {
			ShortCutSettingInfo.setEnable(bool);
		}
	}

	private ShortCutSettingInfo createShortcutsettingInfo() {
		ShortCutSettingInfo info = null;
		final String themPkg = ThemeManager.getInstance(mContext).getCurThemePackage();
		info = mSettingDataModel.getShortCurSetting(themPkg);
		if (null == info) {
			info = new ShortCutSettingInfo();
			info.initWithDefaultData();
			mSettingDataModel.insertShortCutSetting(info);
		} else if (null == info.mStyle) {
			// 2.20数据库升级，加入text属性的style字段,赋默认值
			info.mStyle = (IGoLauncherClassName.DEFAULT_THEME_PACKAGE.equals(themPkg))
					? DockUtil.DOCK_DEFAULT_STYLE_STRING
					: themPkg;
			updateCurThemeShortCutSettingStyle(info.mStyle);
		}
		return info;
	}

	public void addScreenStyleSetting(String packageName) {
		GoContentProviderUtil.addScreenStyleSetting(mContext,packageName);
	}

	public void clearDirtyStyleSetting(String uninstallPackageName) {
		mSettingDataModel.clearDirtyScreenStyleSetting(uninstallPackageName);
	}
	
	private void onCallbacks(int msgId, int param, EffectSettingInfo info) {
		for (ISettingsCallback callback : mCallbacks) {
			try {
				callback.broadCastEffectChanged(msgId, param, info);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * <br>功能简述:功能试用过期后的处理
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void onFunctionTrialExpired(int forceClear) {
		EffectorControler effectorControler = EffectorControler.getInstance();
		boolean ret0 = effectorControler.checkEffectorIsPrime(getEffectSettingInfo().mEffectorType);
		boolean ret1 = !FunctionPurchaseManager.getInstance(mContext).queryItemPurchaseState(FunctionPurchaseManager.PURCHASE_ITEM_EFFECT);
		boolean ret = ret0 && ret1;
		ThemeSettingInfo themeSettingInfo = getThemeSettingInfo();
		if (ret && forceClear == 1) {
			getEffectSettingInfo().setDefaultType();
			onCallbacks(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_EFFECTSETTING,
					mEffectSettingInfo);
			
			mSettingDataModel.updateEffectSettingInfo(mEffectSettingInfo);
			FunAppSetting funAppSetting = SettingProxy.getFunAppSetting();
			funAppSetting.setIconEffect(AppSettingDefault.ICONEFFECT);
		}
		
		if (getDesktopSettingInfo().mEnableSideDock
				&& !FunctionPurchaseManager.getInstance(mContext).queryItemPurchaseState(
						FunctionPurchaseManager.PURCHASE_ITEM_QUICK_ACTIONS)) {
			GoAppUtils.stopSlideBar(mContext);
		}
		
		if (themeSettingInfo.mNoAdvert
				&& !FunctionPurchaseManager.getInstance(mContext).queryItemPurchaseState(
						FunctionPurchaseManager.PURCHASE_ITEM_AD)) {
			themeSettingInfo.mNoAdvert = false;
			updateThemeSettingInfo2(themeSettingInfo, false);
		}
	}
	
	/**
	 * <br>功能简述:购买功能后的处理
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param functionId
	 */
	public void enablePurchaseFunction(int functionId) {
		switch (functionId) {
			case FunctionPurchaseManager.PURCHASE_ITEM_AD :
				break;
			case FunctionPurchaseManager.PURCHASE_ITEM_EFFECT :
				break;
			case FunctionPurchaseManager.PURCHASE_ITEM_SECURITY :
				break;
			case FunctionPurchaseManager.PURCHASE_ITEM_FULL :
			case FunctionPurchaseManager.PURCHASE_ITEM_QUICK_ACTIONS :
				if (getDesktopSettingInfo().mEnableSideDock) {
					GoAppUtils.startSlideBar(mContext);
				}
				break;
			default :
				break;
		}
	}
	/**
	 * 根据包名获取ShortCut信息
	 * 
	 * @author yangbing
	 * */
	private ShortCutSettingInfo getShortCutSettingInfoByPackageName(String packageName) {

		ShortCutSettingInfo info = mSettingDataModel.getShortCurSetting(packageName);
		if (info == null) {
			info = new ShortCutSettingInfo();
		}
		return info;
	}

	/**
	 * 恢复快捷条背景
	 * 
	 * @author yangbing
	 * */
	public void resetShortCutBg(String useThemeName, String targetThemeName, String resName) {
		mSettingDataModel.resetShortCutBg(useThemeName, targetThemeName, resName);
	}

	/**
	 * 清除缓存
	 * 
	 * @author yangbing
	 * */
	public void clearDockSettingInfo() {
		mDockSettingInfo = null;
	}

	/**
	 * 获取快捷条图标风格
	 * 
	 * @author yangbing
	 * */
	private String getShortCutStyleByPackage(String packageName) {
		ShortCutSettingInfo info = mSettingDataModel.getShortCurSetting(packageName);
		return info == null ? null : info.mStyle;
	}

	/**
	 * 更新快捷条图标风格
	 * 
	 * @author yangbing
	 * */
	public void updateShortCutStyleByPackage(String packageName, String style) {
		mSettingDataModel.updateShortCutSettingStyle(packageName, style);
	}

	// DONE
	// 安全锁设置
	public DeskLockSettingInfo getDeskLockSettingInfo() {
		if (null == mLockSettingInfo) {
			mLockSettingInfo = createDeskLockSettingInfo();
		}
		return mLockSettingInfo;
	}

	private DeskLockSettingInfo createDeskLockSettingInfo() {
		DeskLockSettingInfo info = mSettingDataModel.getDeskLockSettingInfo();
		if (null == info) {
			info = new DeskLockSettingInfo();
			mSettingDataModel.cleanDeskLockSettingInfo();
			mSettingDataModel.insertDeskLockSettingInfo(info);
		}
		return info;
	}

	public void updateDesLockSettingInfo(DeskLockSettingInfo info) {
		mLockSettingInfo = info;
		mSettingDataModel.updateDeskLockSettingInfo(info);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
	}
}
