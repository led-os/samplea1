package com.jiubang.ggheart.apps.desks.diy.plugin;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * go插件和小部件配置文件解析器
 * @author liulixia
 *
 */
//CHECKSTYLE:OFF
public class PluginXmlParser {
	private final static String RECOMMEND_GOWIDGET = "recommend_gowidget";
	private final static String ITEM = "item";
	private final static String PKGNAME = "pkgname";
	private final static String WIDGETNAME = "widgetname";
	private final static String IMGNAME = "imgname";
	private final static String FTP = "ftp";
	private final static String MARKET = "market";
	private final static String IMGURL = "imgurl";
	
	public final static String GOPLUGIN_XML = "plugin_manage_goplugin"; //GO插件配置文件
	public final static String GOWIDGET_XML = "plugin_manage_gowidget"; //GO小部件配置文件
	
	public void parsePlugin(boolean isPlugin, Context context, boolean isCnUser, ArrayList<GoPluginOrWidgetInfo> allInfos,
			ArrayList<GoPluginOrWidgetInfo> installInfos, ArrayList<GoPluginOrWidgetInfo> notInstallInfos) {
		Log.d("llx", "parsePlugin");
		if (context == null) {
			return;
		}
		
		if (installInfos == null) {
			installInfos = new ArrayList<GoPluginOrWidgetInfo>();
		}
		
		if (allInfos == null) {
			allInfos = new ArrayList<GoPluginOrWidgetInfo>();
		}
		
		if (notInstallInfos == null) {
			notInstallInfos = new ArrayList<GoPluginOrWidgetInfo>();
		}
		try {
			XmlPullParser xmlPullParser = null;
			String fileName = GOPLUGIN_XML;
			if (!isPlugin) {
				fileName = GOWIDGET_XML;
			}
			int id = context.getResources().getIdentifier(fileName, "xml", context.getPackageName());
			if (id != 0) {
				Log.d("llx", "parsePlugin");
				xmlPullParser = context.getResources().getXml(id);
				if (xmlPullParser != null) {
					Resources resources = context.getResources();
					String attrName = null;
					String value = null;
					while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
						attrName = xmlPullParser.getName();
						if (attrName == null || attrName.equals(RECOMMEND_GOWIDGET)
								|| xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
							continue;
						}
						if (attrName.equals(ITEM)) {
							GoPluginOrWidgetInfo widgetInfo = new GoPluginOrWidgetInfo();
							if (isPlugin) {
								widgetInfo.mWidgetType = GoPluginOrWidgetInfo.TYPE_PLUGIN;
							} else {
								widgetInfo.mWidgetType = GoPluginOrWidgetInfo.TYPE_WIDGET;
							}
							// 解析widget包名
							value = xmlPullParser.getAttributeValue(null, PKGNAME);
							if (value != null) {
								widgetInfo.mWidgetPkgName = value;
							}

							// 解析widget名字
							value = xmlPullParser.getAttributeValue(null, WIDGETNAME);
							if (value != null) {
								int stringId = resources.getIdentifier(value, "string",
								        PackageName.PACKAGE_NAME);
								if (stringId != 0) {
									widgetInfo.mWidgetName = resources.getString(stringId);
								}
							}

							// 解析widget图片名字
							value = xmlPullParser.getAttributeValue(null, IMGNAME);
							if (value != null) {
								int imgId = resources.getIdentifier(value, "drawable",
								        PackageName.PACKAGE_NAME);
								if (imgId != 0) {
									widgetInfo.mWidgetImgId = imgId;
								}
							}
							
							// 解析widget图片地址
							value = xmlPullParser.getAttributeValue(null, IMGURL);
							if (value != null) {
								widgetInfo.mWidgetImgUrl = value;
							}

							value = xmlPullParser.getAttributeValue(null, FTP);
							if (value != null) {
								widgetInfo.mWidgetFtpUrl = value;
							}
							
							value = xmlPullParser.getAttributeValue(null, MARKET);
							if (value != null) {
								widgetInfo.mWidgetMarket = value;
							}
							
							if (!isCnUser && (widgetInfo.mWidgetMarket == null ||widgetInfo.mWidgetMarket.equals(""))) {
								continue;
							}
							if (GoAppUtils.isAppExist(context, widgetInfo.mWidgetPkgName)) {
								widgetInfo.mState = GoPluginOrWidgetInfo.INSTALLED;
								installInfos.add(widgetInfo);
							} else {
								if (!filterSpecialPkg(widgetInfo.mWidgetPkgName)) {
									widgetInfo.mState = GoPluginOrWidgetInfo.NOT_INSTALLED;
									notInstallInfos.add(widgetInfo);
								}
							}
							allInfos.add(widgetInfo);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean filterSpecialPkg(String widgetPkgName) {
		if (widgetPkgName.equals(PackageName.OLD_CALENDAR_PKG)) {
			return true;
		}
		return false;
	}

	public void getInfosByType(Context context, ArrayList<GoPluginOrWidgetInfo> allInfos,
			ArrayList<GoPluginOrWidgetInfo> installInfos, ArrayList<GoPluginOrWidgetInfo> notInstallInfos) {
		if (context == null || allInfos == null || allInfos.size() == 0) {
			return;
		} 
		if (installInfos == null) {
			installInfos = new ArrayList<GoPluginOrWidgetInfo>();
		}
		if (notInstallInfos == null) {
			notInstallInfos = new ArrayList<GoPluginOrWidgetInfo>();
		}
		for (int i = 0; i < allInfos.size(); i++) {
			GoPluginOrWidgetInfo widgetInfo = allInfos.get(i);
			if (widgetInfo != null) {
				if (GoAppUtils.isAppExist(context, widgetInfo.mWidgetPkgName)) {
					installInfos.add(widgetInfo);
					widgetInfo.mState = GoPluginOrWidgetInfo.INSTALLED;
				} else {
					notInstallInfos.add(widgetInfo);
					widgetInfo.mState = GoPluginOrWidgetInfo.NOT_INSTALLED;
				}
			}
		}
	}
}
