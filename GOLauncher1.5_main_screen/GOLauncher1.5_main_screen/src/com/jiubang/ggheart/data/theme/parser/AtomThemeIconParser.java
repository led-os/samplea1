package com.jiubang.ggheart.data.theme.parser;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

import com.jiubang.ggheart.data.theme.ThemeConfig;
import com.jiubang.ggheart.data.theme.bean.AppDataThemeBean;
import com.jiubang.ggheart.data.theme.bean.ThemeBean;
/**
 * 解析atom主题appmap.xml文件类
 * @author liulixia
 *
 */
public class AtomThemeIconParser extends IParser {
	private static String sITEM = "item";
	private static String sCLASS = "class";
	private static String sNAME = "name";

	public AtomThemeIconParser() {
		mAutoParserFileName = ThemeConfig.ATOMAPPMAP;
	}

	@Override
	protected ThemeBean createThemeBean(String pkgName) {
		// TODO Auto-generated method stub
		return new AppDataThemeBean(pkgName);
	}

	@Override
	public void parseXml(XmlPullParser xmlPullParser, ThemeBean bean) {
		if (xmlPullParser == null || bean == null) {
			Log.i("praseXml", "ThemeInfoPraser.praseXml"
					+ " xmlPullParser == null || bean == null");
			return;
		}

		AppDataThemeBean appThemeBean = (AppDataThemeBean) bean;
		try {
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					String attrName = xmlPullParser.getName();
					if (attrName != null) {
						if (attrName.equals(sITEM)) {
							String className = xmlPullParser.getAttributeValue(
									null, sCLASS);
							String drawableName = xmlPullParser
									.getAttributeValue(null, sNAME);
							if (className != null && drawableName != null) {
								appThemeBean.getFilterAppsMap().put(className,
										drawableName);
							}
						}
					}

				}
				eventType = xmlPullParser.next();
			}
			appThemeBean.getIconbackNameList().add("appicon_background");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return;

	}
}
