package com.jiubang.ggheart.apps.font;

import java.util.ArrayList;

import android.content.Context;

import com.golauncher.message.IAppCoreMsgId;
import com.jiubang.core.framework.ICleanable;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.data.Controler;
import com.jiubang.ggheart.data.DataProvider;
import com.jiubang.ggheart.data.DataType;
/**
 * 
 * <br>类描述:字体设置相关
 * <br>功能详细描述:正在使用的字体使用Sharepreference保存。
 * 
 * @author  rongjinsong
 * @date  [2013-7-3]
 */
public class FontControler extends Controler implements ICleanable {

	private FontBean mUsedFontBean;
	private static FontControler sInstance;
	private FontControler(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		createUsedFontBean();
	}
	public synchronized static FontControler getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new FontControler(context);
		}
		return sInstance;
	}
	public FontBean createUsedFontBean() {
		FontBean bean = null;
		bean = new FontBean();
		PreferencesManager pm = new PreferencesManager(mContext,
				IPreferencesIds.PREFERENCE_SETTING_CFG, Context.MODE_PRIVATE
						| Context.MODE_MULTI_PROCESS);
		bean.mFontFileType = pm.getInt(IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_FONTFILETYPE,
				FontBean.FONTFILETYPE_SYSTEM);
		bean.mPackageName = pm.getString(IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_FONTPACKAGE,
				FontBean.SYSTEM);
		bean.mApplicationName = pm.getString(IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_FONTTITLE,
				FontBean.SYSTEM);
		bean.mFileName = pm.getString(IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_FONTFILE,
				FontTypeface.DEFAULT);
		bean.mStyle = pm.getString(IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_FONTSTYLE,
				FontStyle.NORMAL);
		return bean;
	}

	public void updateUsedFontBean(FontBean bean) {
		if (null == bean) {
			return;
		}
		if (null != mUsedFontBean && mUsedFontBean.equals(bean)) {
			// 没有修改
			return;
		}

		mUsedFontBean = bean;
		mUsedFontBean.initTypeface(mContext);
		PreferencesManager pm = new PreferencesManager(mContext,
				IPreferencesIds.PREFERENCE_SETTING_CFG, Context.MODE_PRIVATE
						| Context.MODE_MULTI_PROCESS);
		pm.putInt(IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_FONTFILETYPE, bean.mFontFileType);
		pm.putString(IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_FONTPACKAGE, bean.mPackageName);
		pm.putString(IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_FONTTITLE, bean.mApplicationName);
		pm.putString(IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_FONTFILE, bean.mFileName);
		pm.putString(IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_FONTSTYLE, bean.mStyle);
		pm.commit();
		// 不发广播，改为直接重启桌面，原因是在TextFont注册了广播监听后，避免忘记反注册而导致内存泄漏，重启桌面后，直接从sharepreferences读取
//		broadCast(IDiyMsgIds.APPCORE_DATACHANGE, DataType.DATATYPE_DESKFONTCHANGED, mUsedFontBean,
//				null);
//		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, ICommonMsgId.RESTART_GOLAUNCHER, -1, null, null);
	}

	// 字体
	public FontBean getUsedFontBean() {
		if (null != mUsedFontBean) {
			mUsedFontBean.initTypeface(mContext);
			return mUsedFontBean;
		}

		mUsedFontBean = createUsedFontBean();
		mUsedFontBean.initTypeface(mContext);
		return mUsedFontBean;
	}

	public ArrayList<FontBean> createFontBeans() {
		ArrayList<FontBean> beans = DataProvider.getInstance(mContext).getAllFont();
		if (null == beans || beans.size() == 0) {
			// 初始化系统字体
			beans = new ArrayList<FontBean>();
			FontBean bean = new FontBean();
			bean.mFileName = FontTypeface.DEFAULT;
			beans.add(bean);
			bean = new FontBean();
			bean.mFileName = FontTypeface.DEFAULT_BOLD;
			beans.add(bean);
			bean = new FontBean();
			bean.mFileName = FontTypeface.SANS_SERIF;
			beans.add(bean);
			bean = new FontBean();
			bean.mFileName = FontTypeface.SERIF;
			beans.add(bean);
			bean = new FontBean();
			bean.mFileName = FontTypeface.MONOSPACE;
			beans.add(bean);
			updateFontBeans(beans);
		}
		return beans;
	}

	public void updateFontBeans(ArrayList<FontBean> beans) {
		DataProvider.getInstance(mContext).updateAllFont(beans);
	}

	public void syncFontCfgFromDB() {
		PreferencesManager preferencesManager = new PreferencesManager(mContext,
				IPreferencesIds.PREFERENCE_SETTING_CFG, Context.MODE_PRIVATE
						| Context.MODE_MULTI_PROCESS);
		boolean hasSync = preferencesManager.getBoolean(
				IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_SYNC_FONT_CFG, false);
		if (!hasSync) {
			FontBean bean = DataProvider.getInstance(mContext).getUsedFont();
			if (bean != null && (mUsedFontBean == null || !mUsedFontBean.equals(bean))) {
				mUsedFontBean = bean;
				broadCast(IAppCoreMsgId.APPCORE_DATACHANGE, DataType.DATATYPE_DESKFONTCHANGED,
						mUsedFontBean, null);
			}
			preferencesManager.putBoolean(
					IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_SYNC_FONT_CFG, true);
			preferencesManager.commit();
		}
	}
	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		clearAllObserver();
	}

}
