package com.jiubang.ggheart.data.theme.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.Color;
import android.util.Log;

import com.jiubang.ggheart.data.theme.ThemeConfig;
import com.jiubang.ggheart.data.theme.bean.AppDataThemeBean;
import com.jiubang.ggheart.data.theme.bean.AppFuncThemeBean;
import com.jiubang.ggheart.data.theme.bean.DeskFolderThemeBean;
import com.jiubang.ggheart.data.theme.bean.DeskThemeBean;
import com.jiubang.ggheart.data.theme.bean.DeskThemeBean.FolderStyle;
import com.jiubang.ggheart.data.theme.bean.DodolThemeResourceBean;
import com.jiubang.ggheart.data.theme.bean.ThemeBean;
/**
 * dodol主题theme_resource.xml文件解析器
 * @author liulixia
 *
 */
//CHECKSTYLE:OFF
public class DodolThemeResourceParser extends IParser {
	private static String HOME = "home";
	private static String WALLPAPERS = "wallpapers"; //壁纸
	private static String WALLPAPER = "wallpaper";
	private static String IMG = "img";
	private static String INDICATOR = "indicator";
	private static String SELECTED = "selected";
	private static String UNSELECTED = "unselected";
	
	private static String TRASH = "trash";
	private static String ICON = "icon";
	private static String BACKGROUND = "background";
	private static String ACTIVATE = "activate";
	private static String NORMAL = "normal";
	
	private static String DOCK = "dock";
	private static String DIAL = "dial";
	private static String CONTACTS = "contacts";
	private static String SMS = "sms";
	private static String BROWSER = "browser";
	private static String PLUS = "plus";
	
	private static String APPDRAWER = "appDrawer";
	private static String CELL = "cell";
	private static String FONT = "font";
	private static String BAR = "bar";
	private static String SEARCH = "search";
	private static String MENU = "menu";
	private static String DIVIDING = "dividing";
	
	private static String FOLDER = "folder";
	private static String BASE = "base";
	private static String ADD = "add";
	private static String PRESS = "press";
	
	private static String SCALE = "scale";
	private static String FACTOR = "factor";
	private static String COLOR = "color";
	
	private static String SCREEN = "screen";
	private static String DELETE = "delete";
	private static String CURRENT = "current";
	private static String DEFAULT = "default";
	private static String MOVE = "move";
	

	public DodolThemeResourceParser() {
		mAutoParserFileName = ThemeConfig.DODOLTHEMERESOURCE;
	}

	@Override
	protected ThemeBean createThemeBean(String pkgName) {
		// TODO Auto-generated method stub
		return new DodolThemeResourceBean(pkgName);
	}

	@Override
	public void parseXml(XmlPullParser xmlPullParser, ThemeBean bean) {

		if (xmlPullParser == null || bean == null) {
			return;
		}

		DodolThemeResourceBean appThemeBean = (DodolThemeResourceBean) bean;
		ConcurrentHashMap<Integer, ThemeBean> tmpMap = appThemeBean.getThemeResourceMap();
		AppDataThemeBean appDataThemeBean = (AppDataThemeBean) tmpMap.get(ThemeBean.THEMEBEAN_TYPE_APPDATA);
		AppFuncThemeBean appFuncThemeBean = (AppFuncThemeBean) tmpMap.get(ThemeBean.THEMEBEAN_TYPE_FUNCAPP);
		appFuncThemeBean.mFoldericonBean.mPackageName = bean.getPackageName();
		DeskThemeBean deskThemeBean = (DeskThemeBean) tmpMap.get(ThemeBean.THEMEBEAN_TYPE_DESK);
		try {
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					String attrName = xmlPullParser.getName();
					if (attrName != null) {
						if (attrName.equals(HOME)) {
							parseHomeTag(xmlPullParser, deskThemeBean, appFuncThemeBean);
						} else if (attrName.equals(ICON)) {
							parseIconTag(xmlPullParser, appDataThemeBean, appFuncThemeBean);
						} else if (attrName.equals(FOLDER)) {
							parseFolderTag(xmlPullParser, deskThemeBean, appFuncThemeBean);
						} else if (attrName.equals(APPDRAWER)) {
							parseAppDrawerTag(xmlPullParser, appFuncThemeBean);
						} else if (attrName.equals(SCREEN)) {
							parseScreenTag(xmlPullParser, deskThemeBean);
						}
						
					}
				}
				eventType = xmlPullParser.next();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * 解析home标签
	 * @param xmlPullParser
	 * @param deskBean
	 */
	private void parseHomeTag(XmlPullParser xmlPullParser, DeskThemeBean deskBean, AppFuncThemeBean appFuncThemeBean){
		try {
			String parceTagName = xmlPullParser.getName();
			int eventType = xmlPullParser.next();
			String attributeValue = null;
			while (XmlPullParser.END_DOCUMENT != eventType) {
				String tagName = xmlPullParser.getName();
				if (XmlPullParser.END_TAG == eventType) {
					// 解析完毕
					if (tagName.equals(parceTagName)) {
						break;
					}
				} else if (XmlPullParser.START_TAG == eventType) {
					if (tagName.equals(WALLPAPERS)) { //壁纸
						parseWallPaper(xmlPullParser, deskBean);
					} else if (tagName.equals(INDICATOR)) { //指示器
						attributeValue = xmlPullParser
								.getAttributeValue(null, SELECTED);
						DeskThemeBean.IndicatorItem indicatorItem = deskBean
								.createIndicatorItem();
						if (attributeValue != null) {
							DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
							wallpaper.mResName = attributeValue;
							indicatorItem.mSelectedBitmap = wallpaper;
							
							appFuncThemeBean.mIndicatorBean.indicatorCurrentHor = attributeValue;
						}
						attributeValue = xmlPullParser
								.getAttributeValue(null, UNSELECTED);
						if (attributeValue != null) {
							DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
							wallpaper.mResName = attributeValue;
							indicatorItem.mUnSelectedBitmap = wallpaper;
							
							appFuncThemeBean.mIndicatorBean.indicatorHor = attributeValue;
						}
						deskBean.mIndicator.mDots = indicatorItem;
						deskBean.mIndicator.setPackageName(deskBean.getPackageName());
					} else if (tagName.equals(TRASH)) { //垃圾桶
						DeskThemeBean.TrashStyle trashStyle = deskBean.createTrashStyle();
						parseTrashStyle(xmlPullParser, deskBean, trashStyle);
						deskBean.mScreen.mTrashStyle = trashStyle;
					} else if (tagName.equals(DOCK)) {
						DeskThemeBean.Layer layer = deskBean.createLayer();
						deskBean.mDock.mIconStyle.add(layer);
						parseDockTag(xmlPullParser, deskBean);
					}
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseWallPaper(XmlPullParser xmlPullParser, DeskThemeBean deskBean){
		try{
			String parceTagName = xmlPullParser.getName();
			int eventType = xmlPullParser.next();
			while (XmlPullParser.END_DOCUMENT != eventType) {
				String attrName = xmlPullParser.getName();
				if (XmlPullParser.END_TAG == eventType) {
					// 解析完毕
					if (attrName.equals(parceTagName)) {
						break;
					}
				} else if (XmlPullParser.START_TAG == eventType) { 
					if (attrName.equals(WALLPAPER)) { // 解析mask蒙版
						String attributeValue = xmlPullParser
								.getAttributeValue(null, IMG);
						if (attributeValue != null && !attributeValue.equals("")) {
							deskBean.mWallpaper.mResName = attributeValue;
							break;
						}
					}  
				}
				eventType = xmlPullParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 解析垃圾箱
	 * @param xmlPullParser
	 * @param deskBean
	 * @param trashStyle
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void parseTrashStyle(XmlPullParser xmlPullParser, DeskThemeBean deskBean,
			DeskThemeBean.TrashStyle trashStyle) throws XmlPullParserException, IOException {
		// 自身属性
		String attributeValue = null;
		attributeValue = xmlPullParser.getAttributeValue(null, AttributeSet.COLOR);
		trashStyle.mIconForeColor = DeskThemeParser.parseColor(attributeValue);

		// 子属性
		String parceTagName = xmlPullParser.getName();
		int eventType = xmlPullParser.next();
		while (XmlPullParser.END_DOCUMENT != eventType) {
			String tagName = xmlPullParser.getName();
			if (XmlPullParser.END_TAG == eventType) {
				// 解析完毕
				// if (tagName.equals(TagSet.TRASHSTYLE))
				if (tagName.equals(parceTagName)) {
					break;
				}
			} else if (XmlPullParser.START_TAG == eventType) {
				String activeImg;
				String normalImg;
				String activeBgImg;
				String normalBgImg;
				if (tagName.equals(ICON)) {
					activeImg = xmlPullParser.getAttributeValue(null, ACTIVATE);
					normalImg = xmlPullParser.getAttributeValue(null, NORMAL);
					if (activeImg != null && normalImg != null) {
						DeskThemeBean.TrashLayer layer = deskBean.createTrashLayer();
						layer.mTrashing = true;
						layer.mResImage = normalImg;
						trashStyle.mTrashingLayer = layer;
						
						layer = deskBean.createTrashLayer();
						layer.mTrashing = false;
						layer.mResImage = activeImg;
						trashStyle.mTrashedLayer = layer;
					}
				} else if (tagName.equals(BACKGROUND)) {
					activeBgImg = xmlPullParser.getAttributeValue(null, ACTIVATE);
					normalBgImg = xmlPullParser.getAttributeValue(null, NORMAL);
					DeskThemeBean.TrashLayer trashingLayer = trashStyle.mTrashingLayer;
					DeskThemeBean.TrashLayer trashedLayer = trashStyle.mTrashedLayer;
					if (activeBgImg != null && normalBgImg != null
							&& trashingLayer != null && trashedLayer != null) {
						DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
						wallpaper.mResName = normalBgImg;
						trashingLayer.mBackImage = wallpaper;
						
						wallpaper = deskBean.createWallpaperBean();
						wallpaper.mResName = activeBgImg;
						trashedLayer.mBackImage = wallpaper;
					}
				}
			}
			eventType = xmlPullParser.next();
		}
	}
	
	
	private void parseIconTag(XmlPullParser xmlPullParser, AppDataThemeBean appThemeBean, AppFuncThemeBean appFuncThemeBean) {
		try {
			String parceTagName = xmlPullParser.getName();
			int eventType = xmlPullParser.next();
			while (XmlPullParser.END_DOCUMENT != eventType) {
				String attrName = xmlPullParser.getName();
				if (XmlPullParser.END_TAG == eventType) {
					// 解析完毕
					if (attrName.equals(parceTagName)) {
						break;
					}
				} else if (XmlPullParser.START_TAG == eventType) {
					if (attrName.equals(BASE)) {
						int attributeCount = xmlPullParser
								.getAttributeCount();
						String img = null;
						ArrayList<String> iconbackList = appThemeBean
								.getIconbackNameList();
						if (iconbackList != null) {
							for (int i = 0; i < attributeCount; i++) {
								img = xmlPullParser.getAttributeValue(i);
								iconbackList.add(img);
							}
						}
					} 
//					else if (attrName.equals(MASK)) { // 解析mask蒙版
//						int attributeCount = xmlPullParser
//								.getAttributeCount();
//						String img = null;
//						ArrayList<String> iconmaskList = appThemeBean
//								.getmIconmaskNameList();
//						if (iconmaskList != null) {
//							for (int i = 0; i < attributeCount; i++) {
//								img = xmlPullParser.getAttributeValue(i);
//								Log.v("llx", "mark img = " + img);
//								iconmaskList.add(img);
//							}
//						}
//					} 
					else if (attrName.equals(SCALE)) {
						String factor = xmlPullParser.getAttributeValue(
								null, FACTOR);
						if (factor != null) {
							try {
								float scaleFactor = Float.valueOf(factor);
								appThemeBean.setScaleFactor(scaleFactor);
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
						}
					} else if (attrName.equals(FONT)) {
						String attributeValue = xmlPullParser.getAttributeValue(
								null, COLOR);
						if (attributeValue != null) {
							try {
								int color = Color.parseColor(attributeValue);
								appFuncThemeBean.mAppIconBean.mTextColor = color;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else if (attrName.equals(BACKGROUND)) {
						String attributeValue = xmlPullParser.getAttributeValue(
								null, COLOR);
						if (attributeValue != null) {
							try {
								int color = Color.parseColor(attributeValue);
								appFuncThemeBean.mAppIconBean.mIconBgColor = color;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				eventType = xmlPullParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 解析dock
	 * @param xmlPullParser
	 * @param deskBean
	 */
	private void parseDockTag(XmlPullParser xmlPullParser, DeskThemeBean deskBean) {
		try {
			String parceTagName = xmlPullParser.getName();
			int eventType = xmlPullParser.next();
			while (XmlPullParser.END_DOCUMENT != eventType) {
				String attrName = xmlPullParser.getName();
				if (XmlPullParser.END_TAG == eventType) {
					// 解析完毕
					if (attrName.equals(parceTagName)) {
						break;
					}
				} else if (XmlPullParser.START_TAG == eventType) { 
					if (attrName.equals(DIAL)) {
						generateSystemDefualtItme(xmlPullParser, deskBean, 0);
					} else if (attrName.equals(CONTACTS)) {
						generateSystemDefualtItme(xmlPullParser, deskBean, 1);
					} else if (attrName.equals(APPDRAWER)) {
						generateSystemDefualtItme(xmlPullParser, deskBean, 2);
					} else if (attrName.equals(SMS)) {
						generateSystemDefualtItme(xmlPullParser, deskBean, 3);
					} else if (attrName.equals(BROWSER)) {
						generateSystemDefualtItme(xmlPullParser, deskBean, 4);
					} else if (attrName.equals(PLUS)) {
						generateSystemDefualtItme(xmlPullParser, deskBean, -1);
					} else if (attrName.equals(BACKGROUND)) {
						String img = xmlPullParser.getAttributeValue(null, IMG);
						if (img != null) {
							DeskThemeBean.DockSettingBean setting = deskBean.createDockSettingBean();
							setting.mBackground = img;
							setting.mIsBackground = true;
							deskBean.mDock.mDockSetting = setting;
						}
					}
				}
				eventType = xmlPullParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateSystemDefualtItme(XmlPullParser xmlPullParser, DeskThemeBean deskBean, int index) {
		String img = xmlPullParser.getAttributeValue(null, IMG);
		DeskThemeBean.SystemDefualtItem item = deskBean.createSystemDefualt();
		DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
		wallpaper.mResName = img;
		item.mIcon = wallpaper;
		if (index > -1) {
			item.mIndex = index;
			deskBean.mDock.mSymtemDefualt.add(item);
		} else {
			item.mIndex = 0;
			deskBean.mDock.mNoApplicationIcon = item;
		}
	}
	
	/**
	 * 解析folder元素
	 * @param xmlPullParser
	 * @param deskBean
	 * @param appFuncThemeBean
	 */
	private void parseFolderTag(XmlPullParser xmlPullParser, DeskThemeBean deskBean, AppFuncThemeBean themeBean){
		try{
			String parceTagName = xmlPullParser.getName();
			String attributeValue;
			int eventType = xmlPullParser.next();
			while (XmlPullParser.END_DOCUMENT != eventType) {
				String attrName = xmlPullParser.getName();
				if (XmlPullParser.END_TAG == eventType) {
					// 解析完毕
					if (attrName.equals(parceTagName)) {
						break;
					}
				} else if (XmlPullParser.START_TAG == eventType) { 
					if (attrName.equals(BASE)) {
						attributeValue = xmlPullParser.getAttributeValue(null, IMG);
						if (attributeValue != null) {
							if (deskBean != null) {
								DeskThemeBean.FolderStyle folderStyle = deskBean.createFolderStyle();
								DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
								wallpaper.mResName = attributeValue;
								folderStyle.mBackground = wallpaper;
								deskBean.mScreen.mFolderStyle = folderStyle;
								deskBean.mScreen.mFolderStyle.mPackageName = deskBean.getPackageName();
							}
							
							themeBean.mFoldericonBean.mFolderIconBottomPath = attributeValue;
						}
					}
				}
				eventType = xmlPullParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseAppDrawerTag(XmlPullParser xmlPullParser, AppFuncThemeBean appFuncThemeBean) {
		if (xmlPullParser == null || appFuncThemeBean == null) {
			return;
		}
		try{
			String parceTagName = xmlPullParser.getName();
			String attributeValue;
			int eventType = xmlPullParser.next();
			while (XmlPullParser.END_DOCUMENT != eventType) {
				String attrName = xmlPullParser.getName();
				if (XmlPullParser.END_TAG == eventType) {
					// 解析完毕
					if (attrName.equals(parceTagName)) {
						break;
					}
				} else if (XmlPullParser.START_TAG == eventType) { 
					if (attrName.equals(BACKGROUND)) {
						attributeValue = xmlPullParser.getAttributeValue(null, IMG);
						if (attributeValue != null) {
							appFuncThemeBean.mWallpaperBean.mImagePath = attributeValue;
						}
					} else if (attrName.equals(BAR)) {
						parseBarTag(xmlPullParser, appFuncThemeBean);
					} else if (attrName.equals(MENU)) {
						parseMenuTag(xmlPullParser, appFuncThemeBean);
					}
				}
				eventType = xmlPullParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseBarTag(XmlPullParser xmlPullParser, AppFuncThemeBean appFuncThemeBean) {
		if (xmlPullParser == null || appFuncThemeBean == null) {
			return;
		}
		try{
			String parceTagName = xmlPullParser.getName();
			String attributeValue;
			int eventType = xmlPullParser.next();
			while (XmlPullParser.END_DOCUMENT != eventType) {
				String attrName = xmlPullParser.getName();
				if (XmlPullParser.END_TAG == eventType) {
					// 解析完毕
					if (attrName.equals(parceTagName)) {
						break;
					}
				} else if (XmlPullParser.START_TAG == eventType) { 
					if (attrName.equals(SEARCH)) {
						attributeValue = xmlPullParser.getAttributeValue(null, NORMAL);
						if (attributeValue != null) {
							appFuncThemeBean.mSwitchButtonBean.mSearchIcon = attributeValue;
						}
						
						attributeValue = xmlPullParser.getAttributeValue(null, PRESS);
						if (attributeValue != null) {
							appFuncThemeBean.mSwitchButtonBean.mSearchIconLight = attributeValue;
						}
					} else if (attrName.equals(MENU)) {
						attributeValue = xmlPullParser.getAttributeValue(null, NORMAL);
						if (attributeValue != null) {
							appFuncThemeBean.mAllAppDockBean.mHomeMenu = attributeValue;
						}
						
						attributeValue = xmlPullParser.getAttributeValue(null, PRESS);
						if (attributeValue != null) {
							appFuncThemeBean.mAllAppDockBean.mHomeMenuSelected = attributeValue;
						}
					}
				}
				eventType = xmlPullParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseMenuTag(XmlPullParser xmlPullParser, AppFuncThemeBean appFuncThemeBean) {
		if (xmlPullParser == null || appFuncThemeBean == null) {
			return;
		}
		try{
			String parceTagName = xmlPullParser.getName();
			String attributeValue;
			int eventType = xmlPullParser.next();
			while (XmlPullParser.END_DOCUMENT != eventType) {
				String attrName = xmlPullParser.getName();
				if (XmlPullParser.END_TAG == eventType) {
					// 解析完毕
					if (attrName.equals(parceTagName)) {
						break;
					}
				} else if (XmlPullParser.START_TAG == eventType) { 
					if (attrName.equals(BACKGROUND)) {
						attributeValue = xmlPullParser.getAttributeValue(null, IMG);
						if (attributeValue != null) {
							appFuncThemeBean.mAllAppMenuBean.mMenuBgV = attributeValue;
						}
					} else if (attrName.equals(DIVIDING)) {
						attributeValue = xmlPullParser.getAttributeValue(null, IMG);
						if (attributeValue != null) {
							appFuncThemeBean.mAllAppMenuBean.mMenuDividerV = attributeValue;
						}
					} else if (attrName.equals(CELL)) {
						attributeValue = xmlPullParser.getAttributeValue(null, PRESS);
						if (attributeValue != null) {
							appFuncThemeBean.mAllAppMenuBean.mMenuItemSelected = attributeValue;
						}
					} else if (attrName.equals(FONT)) {
						attributeValue = xmlPullParser.getAttributeValue(null, COLOR);
						if (attributeValue != null) {
							try {
								int color = Color.parseColor(attributeValue);
								appFuncThemeBean.mAllAppMenuBean.mMenuTextColor = color;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				eventType = xmlPullParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private void parseScreenTag(XmlPullParser xmlPullParser, DeskThemeBean deskBean){
		if (null == xmlPullParser || null == deskBean) {
			return;
		}

		// 解析
		try {
			deskBean.mPreview.mLineItemCount = 3;
			String parceTagName = xmlPullParser.getName();
			int eventType = xmlPullParser.next();
			String attrValue = null;
			while (XmlPullParser.END_DOCUMENT != eventType) {
				String attrName = xmlPullParser.getName();
				if (XmlPullParser.END_TAG == eventType) {
					// 解析完毕
					if (attrName.equals(parceTagName)) {
						break;
					}
				} else if (XmlPullParser.START_TAG == eventType) { 
					if (attrName.equals(BACKGROUND)) {
						attrValue = xmlPullParser.getAttributeValue(null, CURRENT);
						if (attrValue != null) {
							DeskThemeBean.Card card = deskBean.creaCard();
							DeskThemeBean.CardItem cardItem = deskBean.createCardItem();
							DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
							wallpaper.mResName = attrValue;
							cardItem.mBackground = wallpaper;
							card.mItem = cardItem;
							deskBean.mPreview.mCurrScreen = card;
							
							
						}
						
						attrValue = xmlPullParser.getAttributeValue(null, DEFAULT);
						if (attrValue != null) {
							DeskThemeBean.Card card = deskBean.creaCard();
							DeskThemeBean.CardItem cardItem = deskBean.createCardItem();
							DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
							wallpaper.mResName = attrValue;
							cardItem.mBackground = wallpaper;
							card.mItem = cardItem;
							deskBean.mPreview.mScreen = card;
						}
						
						attrValue = xmlPullParser.getAttributeValue(null, MOVE);
						if (attrValue != null) {
							DeskThemeBean.Card card = deskBean.creaCard();
							DeskThemeBean.CardItem cardItem = deskBean.createCardItem();
							DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
							wallpaper.mResName = attrValue;
							cardItem.mBackground = wallpaper;
							card.mItem = cardItem;
							deskBean.mPreview.mFucosScreen = card;
						}
						
					} else if (attrName.equals(ADD)) {
						attrValue = xmlPullParser.getAttributeValue(null, NORMAL);
						if (attrValue != null) {
							DeskThemeBean.Card card = deskBean.creaCard();
							DeskThemeBean.CardItem cardItem = deskBean.createCardItem();
							DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
							wallpaper.mResName = attrValue;
							cardItem.mBackground = wallpaper;
							card.mItem = cardItem;
							deskBean.mPreview.mAddScreen = card;
						}
						
						attrValue = xmlPullParser.getAttributeValue(null, PRESS);
						if (attrValue != null) {
							DeskThemeBean.Card card = deskBean.creaCard();
							DeskThemeBean.CardItem cardItem = deskBean.createCardItem();
							DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
							wallpaper.mResName = attrValue;
							cardItem.mBackground = wallpaper;
							card.mItem = cardItem;
							deskBean.mPreview.mFocusAddScreen = card;
						}
					} else if (attrName.equals(HOME)) {
						attrValue = xmlPullParser.getAttributeValue(null, DEFAULT);
						if (attrValue != null) {
							DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
							wallpaper.mResName = attrValue;
							deskBean.mPreview.mNotHome = wallpaper;
						}
						
						attrValue = xmlPullParser.getAttributeValue(null, ACTIVATE);
						if (attrValue != null) {
							DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
							wallpaper.mResName = attrValue;
							deskBean.mPreview.mHome = wallpaper;
						}
					} else if (attrName.equals(DELETE)) {
						attrValue = xmlPullParser.getAttributeValue(null, NORMAL);
						if (attrValue != null) {
							DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
							wallpaper.mResName = attrValue;
							deskBean.mPreview.mColsed = wallpaper;
						}
						
						attrValue = xmlPullParser.getAttributeValue(null, PRESS);
						if (attrValue != null) {
							DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
							wallpaper.mResName = attrValue;
							deskBean.mPreview.mColsing = wallpaper;
						}
					}
					
					
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) {
			Log.i("llx",
					"parseScreenTag() has XmlPullParserException = " + e.getMessage());
		} catch (IOException e) {
			Log.i("llx", "parseScreenTag() has IOException = " + e.getMessage());
		} catch (Exception e) {
			Log.i("llx", "parseScreenTag() has Exception = " + e.getMessage());
		}
	}
	
	public void parseXmlToDockBeanPics(XmlPullParser xmlPullParser, DeskThemeBean bean) {
		// 数据验证
		if (null == xmlPullParser || null == bean) {
			return;
		}

		// 解析
		try {
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					// 标签名
					String tagName = xmlPullParser.getName();

					if (tagName.equals(DOCK)) {
						parseDockTag(xmlPullParser, bean);
					} 
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) {
			Log.i("llx",
					"parseXmlToDockBeanPics() has XmlPullParserException = " + e.getMessage());
		} catch (IOException e) {
			Log.i("llx", "parseXmlToDockBeanPics() has IOException = " + e.getMessage());
		} catch (Exception e) {
			Log.i("llx", "parseXmlToDockBeanPics() has Exception = " + e.getMessage());
		}
	}
	
	public void parseFolderXml(XmlPullParser xmlPullParser, AppFuncThemeBean themeBean) {
		if (null == xmlPullParser || null == themeBean) {
			return;
		}

		// 解析
		try {
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					// 标签名
					String tagName = xmlPullParser.getName();

					if (tagName.equals(FOLDER)) {
						parseFolderTag(xmlPullParser, null, themeBean);
						break;
					} 
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) {
			Log.i("llx",
					"parseXmlToDockBeanPics() has XmlPullParserException = " + e.getMessage());
		} catch (IOException e) {
			Log.i("llx", "parseXmlToDockBeanPics() has IOException = " + e.getMessage());
		} catch (Exception e) {
			Log.i("llx", "parseXmlToDockBeanPics() has Exception = " + e.getMessage());
		}
	}
	
	public void parseIndicatorXml(XmlPullParser xmlPullParser, DeskThemeBean deskBean, AppFuncThemeBean appFuncThemeBean) {
		if (xmlPullParser == null){
			return;
		}
		try {
			int eventType = xmlPullParser.getEventType();
			DeskThemeBean.IndicatorItem indicatorItem = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					String tagName = xmlPullParser.getName();
					if (tagName.equals(INDICATOR)) {
						String attributeValue = xmlPullParser
								.getAttributeValue(null, SELECTED);
						
						if (attributeValue != null) {
							if (deskBean != null) {
								indicatorItem = deskBean.createIndicatorItem();
								DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
								wallpaper.mResName = attributeValue;
								indicatorItem.mSelectedBitmap = wallpaper;
							}
							
							if (appFuncThemeBean != null) {
								appFuncThemeBean.mIndicatorBean.indicatorCurrentHor = attributeValue;
							}
						}
						
						attributeValue = xmlPullParser
								.getAttributeValue(null, UNSELECTED);
						if (attributeValue != null) {
							if (deskBean != null && indicatorItem != null) {
								DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
								wallpaper.mResName = attributeValue;
								indicatorItem.mUnSelectedBitmap = wallpaper;
							}
							
							if (appFuncThemeBean != null) {
								appFuncThemeBean.mIndicatorBean.indicatorHor = attributeValue;
							}
						}
						
						if (deskBean != null) {
							if (indicatorItem != null) {
								deskBean.mIndicator.mDots = indicatorItem;
							}
							deskBean.mIndicator.setPackageName(deskBean.getPackageName());
						}
						
//						String attributeValue = xmlPullParser
//								.getAttributeValue(null, SELECTED);
//						if (attributeValue != null) {
//							themeBean.mIndicatorBean.indicatorCurrentHor = attributeValue;
//						}
//						
//						attributeValue = xmlPullParser
//								.getAttributeValue(null, UNSELECTED);
//						if (attributeValue != null) {
//							themeBean.mIndicatorBean.indicatorHor = attributeValue;
//						}
						break;
					}
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void parseTabHomeXml(XmlPullParser xmlPullParser, AppFuncThemeBean themeBean) {
		if (xmlPullParser == null || themeBean == null){
			return;
		}
		try {
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					String attrName = xmlPullParser.getName();
					if (attrName.equals(BAR)) {
						parseBarTag(xmlPullParser, themeBean);
					} 
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void parseDeskFolderTheme(XmlPullParser xmlPullParser, DeskFolderThemeBean folderBean) {
		if (xmlPullParser == null || folderBean == null){
			return;
		}
		try {
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					String attrName = xmlPullParser.getName();
					if (attrName.equals(FOLDER)) {
						FolderStyle folderStyle = folderBean.createFolderStyle();
						parseFolderStyle(xmlPullParser, folderBean, folderStyle);
						folderBean.mFolderStyle = folderStyle;
						break;
					}
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void parseFolderStyle(XmlPullParser xmlPullParser, DeskFolderThemeBean deskBean,
			FolderStyle folderStyle) throws XmlPullParserException, IOException {
		// 子属性
		String parceTagName = xmlPullParser.getName();
		int eventType = xmlPullParser.next();
		while (XmlPullParser.END_DOCUMENT != eventType) {
			String tagName = xmlPullParser.getName();
			if (XmlPullParser.END_TAG == eventType) {
				// 解析完毕
				if (tagName.equals(parceTagName)) {
					break;
				}
			} else if (XmlPullParser.START_TAG == eventType) {
				if (tagName.equals(BASE)) {
					String attributeValue = xmlPullParser.getAttributeValue(null, IMG);
					if (attributeValue != null) {
						DeskThemeBean.WallpaperBean wallpaper = deskBean.createWallpaperBean();
						wallpaper.mResName = attributeValue;
						folderStyle.mBackground = wallpaper;
						break;
					}
				}
			}
			eventType = xmlPullParser.next();
		}
	}
}
