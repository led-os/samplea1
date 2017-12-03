package com.jiubang.ggheart.data.theme.parser;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

import com.jiubang.ggheart.data.theme.ThemeConfig;
import com.jiubang.ggheart.data.theme.bean.AppDataThemeBean;
import com.jiubang.ggheart.data.theme.bean.ThemeBean;
/**
 * dodol主题theme_icon.xml文件解析器
 * @author liulixia
 *
 */
public class DodolThemeIconParser extends IParser {

	private static String sCOMPONENT = "component";
	private static String sIMG = "img";
	private static String sICON = "icon";

	public DodolThemeIconParser() {
		mAutoParserFileName = ThemeConfig.DODOLTHEMEICON;
	}

	@Override
	protected ThemeBean createThemeBean(String pkgName) {
		// TODO Auto-generated method stub
		return new AppDataThemeBean(pkgName);
	}

	@Override
	public void parseXml(XmlPullParser xmlPullParser, ThemeBean bean) {
		Log.i("praseXml", "ThemeInfoPraser.praseXml");

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
						if (attrName.equals(sICON)) {
							String component = xmlPullParser.getAttributeValue(
									null, sCOMPONENT);
							String drawableName = xmlPullParser
									.getAttributeValue(null, sIMG);
							if (component != null && drawableName != null) {
								appThemeBean.getFilterAppsMap().put(component,
										drawableName);
							}
						}
					}

				}
				eventType = xmlPullParser.next();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return;

	}
}
