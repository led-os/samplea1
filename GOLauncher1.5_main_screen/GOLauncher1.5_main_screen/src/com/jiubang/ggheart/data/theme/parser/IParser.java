package com.jiubang.ggheart.data.theme.parser;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

import com.go.commomidentify.IGoLauncherClassName;
import com.go.util.AppUtils;
import com.jiubang.ggheart.data.theme.XmlParserFactory;
import com.jiubang.ggheart.data.theme.bean.ThemeBean;

/**
 * 
 * 
 * @author huyong
 * 
 */
public abstract class IParser {
	public final static String VERSION = "version";
	protected String mAutoParserFileName = null;

	public abstract void parseXml(final XmlPullParser xmlPullParser, ThemeBean bean);
	private ThemeBean mThemeBean;
	public ThemeBean autoParseAppThemeXml(Context context, String themePackage, boolean isEncrypt) {
		ThemeBean themeBean = getBean(context, themePackage, isEncrypt);
		//该功能暂时不上
//		if (themeBean != null && themeBean.getBeanType() == ThemeBean.THEMEBEAN_TYPE_APPDATA) {
//			try {
////				if (ThemeManager.canBeUsedTheme(context, themePackage)) {
//					mAutoParserFileName = ThemeConfig.APPFILTERFILENAME_UPGRADE;
//					AppDataThemeBean upgradeBean = (AppDataThemeBean) getBean(context, themePackage,
//							isEncrypt);
//					if (upgradeBean != null && upgradeBean.getFilterAppsMap() != null
//							&& !upgradeBean.getFilterAppsMap().isEmpty()) {
//						((AppDataThemeBean) themeBean).getFilterAppsMap().putAll(
//								upgradeBean.getFilterAppsMap());
//					}
////				}
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//
//		}
		//end
		return themeBean;
	}

	private ThemeBean getBean(Context context, String themePackage, boolean isEncrypt) {
		if (mThemeBean != null && themePackage.equals(mThemeBean.getPackageName())) {
			return mThemeBean;
		}
		Log.i("Test", this.getClass().getSimpleName() + " getBean");
		// public ThemeBean autoParseAppThemeXml(Context context, String
		// themePackage) {
		// 解析应用程序过滤器信息
		if (mAutoParserFileName == null) {
			Log.i("IParser", "Auto Parse failed, you should init mAutoParserFileName first");
			return null;
		}
		Log.i("ThemeManager", "begin parserTheme " + mAutoParserFileName);
		InputStream inputStream = null;
		XmlPullParser xmlPullParser = null;
		Context ctx = null;
		if (!themePackage.equals(IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3)) {
			ctx = AppUtils.getAppContext(context, themePackage);
		}
		if (isEncrypt) {
			inputStream = XmlParserFactory.createEncryptXmlInputStream(context, themePackage,
					mAutoParserFileName);
		} else {
			int end = mAutoParserFileName.indexOf(".xml");
			if (end <= 0) {
				return null;
			}
			String xmlName = mAutoParserFileName.substring(0, end);
			inputStream = XmlParserFactory.createInputStream(context, themePackage,
					mAutoParserFileName);
			if (inputStream == null) {
				if (ctx != null && mAutoParserFileName != null) {

					int id = ctx.getResources().getIdentifier(xmlName, "xml", themePackage);
					if (id != 0) {
						try {
							xmlPullParser = ctx.getResources().getXml(id);
						} catch (NotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						id = ctx.getResources().getIdentifier(xmlName, "raw", themePackage);
						if (id != 0) {
							inputStream = ctx.getResources().openRawResource(id);
						}
					}
				}
			}
		}
		if (xmlPullParser == null) {
			xmlPullParser = XmlParserFactory.createXmlParser(inputStream);
		}
		if (xmlPullParser != null) {
			mThemeBean = createThemeBean(themePackage);
			if (mThemeBean == null) {
				Log.i("IParser", "Auto Parse failed, you should override createThemeBean() method");
				return mThemeBean;
			}
			parseXml(xmlPullParser, mThemeBean);
		}
		// 关闭inputStream
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				Log.i("ThemeManager", "IOException for close inputSteam");
			}
		}

		return mThemeBean;
	}

	protected ThemeBean createThemeBean(String pkgName) {
		return null;
	}
}
