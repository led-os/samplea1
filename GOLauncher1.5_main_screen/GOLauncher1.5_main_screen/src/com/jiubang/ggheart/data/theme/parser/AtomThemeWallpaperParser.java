package com.jiubang.ggheart.data.theme.parser;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

import com.jiubang.ggheart.data.theme.ThemeConfig;
import com.jiubang.ggheart.data.theme.bean.DeskThemeBean;
import com.jiubang.ggheart.data.theme.bean.ThemeBean;
/**
 * 解析atom主题wallpaper.xml文件类
 * @author liulixia
 *
 */
public class AtomThemeWallpaperParser extends IParser {
	private static String sITEM = "item";
	private static String sNAME = "name";

	public AtomThemeWallpaperParser() {
		mAutoParserFileName = ThemeConfig.ATOMWALLPAPER;
	}

	@Override
	protected ThemeBean createThemeBean(String pkgName) {
		// TODO Auto-generated method stub
		return new DeskThemeBean(pkgName);
	}

	@Override
	public void parseXml(XmlPullParser xmlPullParser, ThemeBean bean) {
		if (xmlPullParser == null || bean == null) {
			Log.i("praseXml", "ThemeInfoPraser.praseXml"
					+ " xmlPullParser == null || bean == null");
			return;
		}

		DeskThemeBean deskbean = (DeskThemeBean) bean;
		try {
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					String attrName = xmlPullParser.getName();
					if (attrName != null) {
						if (attrName.equals(sITEM)) {
							String drawableName = xmlPullParser
									.getAttributeValue(null, sNAME);
							if (drawableName != null) {
								deskbean.mWallpaper.mResName = drawableName;
								break;
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
