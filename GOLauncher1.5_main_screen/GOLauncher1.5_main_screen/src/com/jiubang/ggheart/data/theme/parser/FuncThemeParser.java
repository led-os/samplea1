package com.jiubang.ggheart.data.theme.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.Color;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.data.theme.ThemeConfig;
import com.jiubang.ggheart.data.theme.bean.AppFuncThemeBean;
import com.jiubang.ggheart.data.theme.bean.ThemeBean;

/**
 * 
 * 类说明
 */
public class FuncThemeParser extends IParser {
	/************************ TAG ************************/
	// private static final String THEME = "Theme";
	private static final String WALLPAPER = "Wallpaper";
	private static final String FOLDERICON = "Foldericon";
	private static final String FOLDER = "Folder";
	private static final String ALLAPP = "AllAppDock";
	private static final String ALLAPPMENU = "AllAppMenu";
	private static final String RUNINGAPP = "RuningDock";
	private static final String INDICATOR = "Indicator";
	private static final String APPICON = "AppIcon";
	private static final String SWITCHMENUBEAN = "SwitchMenuBean";
	private static final String SWITCHBUTTONBEAN = "SwitchButtonBean";
	private static final String SIDEBAR = "Sidebar";
	private static final String BEAUTY = "Beauty";
	private static final String APPDRAWTOPBG = "AppDrawTopBg";
	
	/************************ Wall paper Attribute ***************************/
	private static final String BGC = "bg_color";
	private static final String IMAGE = "image";
	/************************ Folder Icon Attribute **************************/
	private static final String BOTTOM = "bottom";
	private static final String TOP_OPEN = "top_open";
	private static final String TOP_CLOSED = "top_closed";
	/************************ Folder Attribute ******************************/
	private static final String BG_FRAME_IMAGE = "bg_frame_image";
	private static final String BG_FRAME_WAY_OF_DRAWING = "bg_frame_way_of_drawing";
	private static final String EDITBOX = "editbox";
	// private static final String UP_BUTTON = "up_button";
	// private static final String UP_BUTTON_SELECTED = "up_button_selected";
	private static final String LINE_ENABLED = "line_enabled";
	private static final String FOLDER_OPEN_BG_COLOR = "folder_open_bg_color";
	private static final String FOLDER_EDIT_TEXT_COLOR = "edittext_color";
	private static final String BG_FRAME_IMAGE_BOTTOM_HEIGHT = "bg_frame_image_bottom_heigth";
	private static final String ADD_BUTON = "add_buton";
	private static final String SORT_BUTTON = "sort_button";
	/************************ Indicator Attribute ******************************/
	private static final String INDICATOR_H_CURRENT = "indicator_h_current";
	private static final String INDICATOR_H = "indicator_h";
	/************************ AppIcon Attribute ******************************/
	private static final String DELETE_APP = "delete_app";
	private static final String DELETE_APP_HIGHLIGHT = "delete_app_highlight";
	private static final String TEXT_COLOR = "text_color";
	private static final String TEXT_BG_COLOR = "text_bg_color";
	private static final String NEW_APP_ICON = "new_app_icon";
	private static final String UPDATE_ICON = "update_icon";
	private static final String LOCKER_ICON = "locker_icon";
	private static final String CLOSE_APP_ICON = "close_app_icon";
	private static final String CLOSE_APP_LIGHT = "close_app_light";
	/************************ AllAppMenuBean ******************************/
	private static final String MENU_BG_V = "menu_bg_v";
	private static final String MENU_BG_H = "menu_bg_h";
	private static final String MENU_DIVIDER_V = "menu_divider_v";
	private static final String MENU_DIVIDER_H = "menu_divider_h";
	private static final String MENU_TEXT_COLOR = "menu_text_color";
	private static final String MENU_ITEM_SELECTED = "menu_item_selected";
	
	private static final String MENU_MEMORY_CLEAN_BTN_NORMAL = "menu_memory_clean_btn_normal";
	private static final String MENU_MEMORY_CLEAN_BTN_TENSITY = "menu_memory_clean_btn_tensity";
	private static final String MENU_MEMORY_CLEAN_BTN_SERIOUSNESS = "menu_memory_clean_btn_seriousness";
	private static final String MENU_MEMORY_CLEAN_PROGRESSBAR_NORMAL = "menu_memory_clean_progressbar_normal";
	private static final String MENU_MEMORY_CLEAN_PROGRESSBAR_TENSITY = "menu_memory_clean_progressbar_tensity";
	private static final String MENU_MEMORY_CLEAN_PROGRESSBAR_SERIOUSNESS = "menu_memory_clean_progressbar_seriousness";
	private static final String MENU_FORWARD_PROMANAGE = "menu_forward_promanage";
	/************************ RuningDockBean ******************************/
	private static final String HOME_MEMORY_BG = "home_memory_bg";
	private static final String HOME_LOCK_LIST_NORMAL = "home_lock_list_normal";
	private static final String HOME_LOCK_LIST_LIGHT = "home_lock_list_light";
	private static final String HOME_RUNNING_INFO_IMG = "home_running_info_img";
	private static final String HOME_RUNNING_LOCK_IMG = "home_running_lock_img";
	private static final String HOME_EDIT_DOCK_TOUCH_BG_V = "home_edit_dock_touch_bg_v";
	private static final String HOME_EDIT_DOCK_BG_V = "home_edit_dock_bg_v";
	private static final String HOME_RUNNING_UNLOCK_IMG = "home_running_unlock_img";
	/************************ AllAppDock ****************************/
	private static final String MENU_UNSELECTED = "menu_unselected";
	private static final String MENU_SELECTED = "menu_selected";
	/************************ SwitchMenuBean ****************************/
	private static final String MEDIA_MENU_BG_V = "media_menu_bg_v";
	private static final String MEDIA_MENU_BG_H = "media_menu_bg_h";
	private static final String MEDIA_MENU_DIVIDER_V = "media_menu_divider_v";
	private static final String MEDIA_MENU_DIVIDER_H = "media_menu_divider_h";
	private static final String MEDIA_MENU_ITEM_SEARCH_SELECTOR = "media_menu_item_search_selector";
	private static final String MEDIA_MENU_ITEM_GALLERY_SELECTOR = "media_menu_item_gallery_selector";
	private static final String MEDIA_MENU_ITEM_MUSIC_SELECTOR = "media_menu_item_music_selector";
	private static final String MEDIA_MENU_ITEM_VIDEO_SELECTOR = "media_menu_item_video_selector";
	private static final String MEDIA_MENU_ITEM_APP_SELECTOR = "media_menu_item_app_selector";
	private static final String MEDIA_MENU_TEXT_COLOR = "media_menu_text_color";
	/************************ SwitchButtonBean ****************************/
	private static final String BUTTON_GALLERYICON = "button_galleryicon";
	private static final String BUTTON_GALLERYLIGHTICON = "button_gallerylighticon";
	private static final String BUTTON_MUSICICON = "button_musicicon";
	private static final String BUTTON_MUSICLIGHTICON = "button_musiclighticon";
	private static final String BUTTON_VIDEOICON = "button_videoicon";
	private static final String BUTTON_VIDEOLIGHTICON = "button_videolighticon";
	private static final String BUTTON_APPICON = "button_appicon";
	private static final String BUTTON_APPICONLIGHT = "button_appiconlight";
	private static final String BUTTON_SEARCH = "button_search";
	private static final String BUTTON_SEARCHLIGHT = "button_searchlight";
	/************************ SIDEBAR Attribute ****************************/
	private static final String SIDEBAR_ICON = "sidebar_icon";
	private static final String LOGO_ICON = "logo_icon";
	private static final String TITLE_COLOR = "title_color";
	/************************ BEAUTY Attribute ****************************/
	private static final String BEAUTY_ICON = "beauty_icon";
	/************************ APPDRAWTOPBG Attribute ****************************/
	private static final String DRAW_TOP_BG_VAR = "draw_top_bg_var";
	

	public FuncThemeParser() {
		mAutoParserFileName = ThemeConfig.APPFUNCTHEMEFILENAME;
	}

	@Override
	protected ThemeBean createThemeBean(String pkgName) {
		// TODO Auto-generated method stub
		return new AppFuncThemeBean(pkgName);
	}

	@Override
	public void parseXml(XmlPullParser xmlPullParser, ThemeBean bean) {
		XmlPullParser parser = xmlPullParser;
		AppFuncThemeBean themeBean = (AppFuncThemeBean) bean;
		// 测试代码
		// AppFuncThemeBean themeBean = new AppFuncThemeBean();
		// XmlResourceParser parser = mActivity.getResources().getXml(
		// R.xml.app_func_theme);
		themeBean.mFoldericonBean.mPackageName = bean.getPackageName();
		themeBean.mTabThemePkg = bean.getPackageName();
		try {
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					String tagName = parser.getName();
					// System.out.println("Start tag " + tagName);
					if (tagName.equals(WALLPAPER)) {
						String attributeValue = parser.getAttributeValue(null, BGC);
						if (attributeValue != null) {
							try {
								int color = Color.parseColor(attributeValue);
								themeBean.mWallpaperBean.mBackgroudColor = color;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						attributeValue = parser.getAttributeValue(null, IMAGE);
						if (attributeValue != null) {
							themeBean.mWallpaperBean.mImagePath = attributeValue;
						}
					} else if (tagName.equals(APPICON)) {
						String attributeValue = parser.getAttributeValue(null, TEXT_COLOR);
						if (attributeValue != null) {
							try {
								int color = Color.parseColor(attributeValue);
								themeBean.mAppIconBean.mTextColor = color;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						attributeValue = parser.getAttributeValue(null, TEXT_BG_COLOR);
						if (attributeValue != null) {
							try {
								int color = Color.parseColor(attributeValue);
								themeBean.mAppIconBean.mIconBgColor = color;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						attributeValue = parser.getAttributeValue(null, DELETE_APP);
						if (attributeValue != null) {
							themeBean.mAppIconBean.mDeletApp = attributeValue;
						}
						attributeValue = parser.getAttributeValue(null, DELETE_APP_HIGHLIGHT);
						if (attributeValue != null) {
							themeBean.mAppIconBean.mDeletHighlightApp = attributeValue;
						}
						attributeValue = parser.getAttributeValue(null, NEW_APP_ICON);
						if (attributeValue != null) {
							themeBean.mAppIconBean.mNewApp = attributeValue;
						}
						attributeValue = parser.getAttributeValue(null, UPDATE_ICON);
						if (attributeValue != null) {
							themeBean.mAppIconBean.mUpdateIcon = attributeValue;
						}
						attributeValue = parser.getAttributeValue(null, LOCKER_ICON);
						if (attributeValue != null) {
							themeBean.mAppIconBean.mLockApp = attributeValue;
						}
						attributeValue = parser.getAttributeValue(null, CLOSE_APP_ICON);
						if (attributeValue != null) {
							themeBean.mAppIconBean.mKillApp = attributeValue;
						}
						attributeValue = parser.getAttributeValue(null, CLOSE_APP_LIGHT);
						if (attributeValue != null) {
							themeBean.mAppIconBean.mKillAppLight = attributeValue;
						}
					} else if (tagName.equals(SWITCHMENUBEAN)) {
						themeBean.mSwitchMenuBean.mPackageName = bean.getPackageName();
						String attributeValue = parser.getAttributeValue(null, MEDIA_MENU_BG_V);
						if (attributeValue != null) {
							themeBean.mSwitchMenuBean.mMenuBgV = attributeValue;
						}
						attributeValue = parser.getAttributeValue(null, MEDIA_MENU_BG_H);
						if (attributeValue != null) {
							themeBean.mSwitchMenuBean.mMenuBgH = attributeValue;
						}
						attributeValue = parser.getAttributeValue(null, MEDIA_MENU_DIVIDER_V);
						if (attributeValue != null) {
							themeBean.mSwitchMenuBean.mMenuDividerV = attributeValue;
						}
						attributeValue = parser.getAttributeValue(null, MEDIA_MENU_DIVIDER_H);
						if (attributeValue != null) {
							themeBean.mSwitchMenuBean.mMenuDividerH = attributeValue;
						}
						String search = null;
						String gallery = null;
						String video = null;
						String audio = null;
						String app = null;
						attributeValue = parser.getAttributeValue(null,
								MEDIA_MENU_ITEM_SEARCH_SELECTOR);
						if (attributeValue != null) {
							search = attributeValue;
						} else {
							search = Integer.toString(R.drawable.switch_menu_search_selector);
						}
						attributeValue = parser.getAttributeValue(null,
								MEDIA_MENU_ITEM_GALLERY_SELECTOR);
						if (attributeValue != null) {
							gallery = attributeValue;
						} else {
							gallery = Integer.toString(R.drawable.switch_menu_image_selector);
						}
						attributeValue = parser.getAttributeValue(null,
								MEDIA_MENU_ITEM_MUSIC_SELECTOR);
						if (attributeValue != null) {
							audio = attributeValue;
						} else {
							audio = Integer.toString(R.drawable.switch_menu_audio_selector);
						}
						attributeValue = parser.getAttributeValue(null,
								MEDIA_MENU_ITEM_VIDEO_SELECTOR);
						if (attributeValue != null) {
							video = attributeValue;
						} else {
							video = Integer.toString(R.drawable.switch_menu_video_selector);
						}
						attributeValue = parser.getAttributeValue(null,
								MEDIA_MENU_ITEM_APP_SELECTOR);
						if (attributeValue != null) {
							app = attributeValue;
						} else {
							app = Integer.toString(R.drawable.switch_menu_app_selector);
						}
						themeBean.mSwitchMenuBean.mItemLabelAppSelectors = new String[] { gallery,
								audio, video, search };
						themeBean.mSwitchMenuBean.mItemLabelImageSelectors = new String[] { app,
								audio, video, search };
						themeBean.mSwitchMenuBean.mItemLabelAudioSelectors = new String[] { app,
								gallery, video, search };
						themeBean.mSwitchMenuBean.mItemLabelVedioSelectors = new String[] { app,
								gallery, audio, search };
						themeBean.mSwitchMenuBean.mItemLabelSearchSelectors = new String[] { app,
								gallery, audio, video };
						attributeValue = parser.getAttributeValue(null, MEDIA_MENU_TEXT_COLOR);
						if (attributeValue != null) {
							try {
								int color = Color.parseColor(attributeValue);
								themeBean.mSwitchMenuBean.mMenuTextColor = color;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else if (parseTabHomeValue(themeBean, tagName, parser)) {

					} else if (parseFolderValue(themeBean, tagName, parser)) {

					} else if (parseIndicatorValue(themeBean, tagName, parser)) {

					}
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean parseIndicatorValue(AppFuncThemeBean themeBean, String tagName,
			XmlPullParser parser) {
		boolean ret = false;
		if (tagName.equals(INDICATOR)) {
			ret = true;
			String attributeValue = parser.getAttributeValue(null, INDICATOR_H_CURRENT);
			if (attributeValue != null) {
				themeBean.mIndicatorBean.indicatorCurrentHor = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, INDICATOR_H);
			if (attributeValue != null) {
				themeBean.mIndicatorBean.indicatorHor = attributeValue;
			}
		}
		return ret;
	}

	public void parseIndicatorXml(XmlPullParser xmlPullParser, ThemeBean bean) {
		XmlPullParser parser = xmlPullParser;
		AppFuncThemeBean themeBean = (AppFuncThemeBean) bean;

		try {
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					String tagName = parser.getName();
					parseIndicatorValue(themeBean, tagName, parser);
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean parseTabHomeValue(AppFuncThemeBean themeBean, String tagName,
			XmlPullParser parser) {
		boolean tabElement = false;
		if (tagName.equals(ALLAPPMENU)) {
			tabElement = true;
			String attributeValue = parser.getAttributeValue(null, MENU_BG_V);
			if (attributeValue != null) {
				themeBean.mAllAppMenuBean.mMenuBgV = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, MENU_DIVIDER_V);
			if (attributeValue != null) {
				themeBean.mAllAppMenuBean.mMenuDividerV = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, MENU_ITEM_SELECTED);
			if (attributeValue != null) {
				themeBean.mAllAppMenuBean.mMenuItemSelected = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, MENU_TEXT_COLOR);
			if (attributeValue != null) {
				try {
					int color = Color.parseColor(attributeValue);
					themeBean.mAllAppMenuBean.mMenuTextColor = color;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			attributeValue = parser.getAttributeValue(null, MENU_MEMORY_CLEAN_BTN_NORMAL);
			if (attributeValue != null) {
				themeBean.mAllAppMenuBean.mMenuMemoryCleanBtnNormal = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, MENU_MEMORY_CLEAN_BTN_TENSITY);
			if (attributeValue != null) {
				themeBean.mAllAppMenuBean.mMenuMemoryCleanBtnTensity = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, MENU_MEMORY_CLEAN_BTN_SERIOUSNESS);
			if (attributeValue != null) {
				themeBean.mAllAppMenuBean.mMenuMemoryCleanBtnSeriousness = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, MENU_MEMORY_CLEAN_PROGRESSBAR_NORMAL);
			if (attributeValue != null) {
				try {
					int color = Color.parseColor(attributeValue);
					themeBean.mAllAppMenuBean.mMenuMemoryCleanProgressBarNormal = color;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			attributeValue = parser.getAttributeValue(null, MENU_MEMORY_CLEAN_PROGRESSBAR_TENSITY);
			if (attributeValue != null) {
				try {
					int color = Color.parseColor(attributeValue);
					themeBean.mAllAppMenuBean.mMenuMemoryCleanProgressBarTensity = color;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			attributeValue = parser.getAttributeValue(null, MENU_MEMORY_CLEAN_PROGRESSBAR_SERIOUSNESS);
			if (attributeValue != null) {
				try {
					int color = Color.parseColor(attributeValue);
					themeBean.mAllAppMenuBean.mMenuMemoryCleanProgressBarSeriousness = color;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			attributeValue = parser.getAttributeValue(null, MENU_FORWARD_PROMANAGE);
			if (attributeValue != null) {
				themeBean.mAllAppMenuBean.mMenuForwardPromanage = attributeValue;
			}
			
		} else if (tagName.equals(ALLAPP)) {
			tabElement = true;
			String attriValue = parser.getAttributeValue(null, MENU_UNSELECTED);
			if (attriValue != null) {
				themeBean.mAllAppDockBean.mHomeMenu = attriValue;
			}

			attriValue = parser.getAttributeValue(null, MENU_SELECTED);
			if (attriValue != null) {
				themeBean.mAllAppDockBean.mHomeMenuSelected = attriValue;
			}
		} else if (tagName.equals(SWITCHBUTTONBEAN)) {
			tabElement = true;
			String attributeValue = parser.getAttributeValue(null, BUTTON_GALLERYICON);
			if (attributeValue != null) {
				themeBean.mSwitchButtonBean.mGalleryIcon = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, BUTTON_GALLERYLIGHTICON);
			if (attributeValue != null) {
				themeBean.mSwitchButtonBean.mGalleryLightIcon = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, BUTTON_MUSICICON);
			if (attributeValue != null) {
				themeBean.mSwitchButtonBean.mMusicIcon = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, BUTTON_MUSICLIGHTICON);
			if (attributeValue != null) {
				themeBean.mSwitchButtonBean.mMusicLightIcon = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, BUTTON_VIDEOICON);
			if (attributeValue != null) {
				themeBean.mSwitchButtonBean.mVideoIcon = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, BUTTON_VIDEOLIGHTICON);
			if (attributeValue != null) {
				themeBean.mSwitchButtonBean.mVideoLightIcon = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, BUTTON_APPICON);
			if (attributeValue != null) {
				themeBean.mSwitchButtonBean.mAppIcon = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, BUTTON_APPICONLIGHT);
			if (attributeValue != null) {
				themeBean.mSwitchButtonBean.mAppIconLight = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, BUTTON_SEARCH);
			if (attributeValue != null) {
				themeBean.mSwitchButtonBean.mSearchIcon = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, BUTTON_SEARCHLIGHT);
			if (attributeValue != null) {
				themeBean.mSwitchButtonBean.mSearchIconLight = attributeValue;
			}
		} else if (tagName.equals(RUNINGAPP)) {
			tabElement = true;
			// 对应正在运行dock
			String attributeValue = parser.getAttributeValue(null, HOME_MEMORY_BG);
			attributeValue = parser.getAttributeValue(null, HOME_LOCK_LIST_NORMAL);
			if (attributeValue != null) {
				themeBean.mRuningDockBean.mHomeLockListNormal = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, HOME_LOCK_LIST_LIGHT);
			if (attributeValue != null) {
				themeBean.mRuningDockBean.mHomeLockListLight = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, HOME_RUNNING_INFO_IMG);
			if (attributeValue != null) {
				themeBean.mRuningDockBean.mHomeRunningInfoImg = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, HOME_RUNNING_LOCK_IMG);
			if (attributeValue != null) {
				themeBean.mRuningDockBean.mHomeRunningLockImg = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, HOME_EDIT_DOCK_BG_V);
			if (attributeValue != null) {
				themeBean.mRuningDockBean.mHomeEditDockBgV = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, HOME_EDIT_DOCK_TOUCH_BG_V);
			if (attributeValue != null) {
				themeBean.mRuningDockBean.mHomeEditDockTouchBgV = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, HOME_RUNNING_UNLOCK_IMG);
			if (attributeValue != null) {
				themeBean.mRuningDockBean.mHomeRunningUnLockImg = attributeValue;
			}
		} else if (tagName.equals(SIDEBAR)) {
			tabElement = true;
			String attributeValue = parser.getAttributeValue(null, SIDEBAR_ICON);
			if (attributeValue != null) {
				themeBean.mSidebarBean.mSidebarIcon = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, LOGO_ICON);
			if (attributeValue != null) {
				themeBean.mSidebarBean.mLogoIcon = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, TITLE_COLOR);
			if (attributeValue != null) {
				try {
					int color = Color.parseColor(attributeValue);
					themeBean.mSidebarBean.mTitleColor = color;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (tagName.equals(BEAUTY)) {
			tabElement = true;
			String attributeValue = parser.getAttributeValue(null, BEAUTY_ICON);
			if (attributeValue != null) {
				themeBean.mBeautyBean.mBeautyIcon = attributeValue;
			}
		} else if (tagName.equals(APPDRAWTOPBG)) {
			tabElement = true;
			String attributeValue = parser.getAttributeValue(null, DRAW_TOP_BG_VAR);
			if (attributeValue != null) {
				themeBean.mGLAppDrawTopBean.mGLAppDrawTopBgBottomVerPath = attributeValue;
			}
		}
		return tabElement;
	}
	
	public void parseTabHomeXml(XmlPullParser xmlPullParser, ThemeBean bean) {
		XmlPullParser parser = xmlPullParser;
		AppFuncThemeBean themeBean = (AppFuncThemeBean) bean;
		themeBean.mTabThemePkg = themeBean.getPackageName();
		try {
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					String tagName = parser.getName();
					parseTabHomeValue(themeBean, tagName, parser);
				} 
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private boolean parseFolderValue(AppFuncThemeBean themeBean, String tagName,
			XmlPullParser parser) {
		boolean ret = false;
		if (tagName.equals(FOLDERICON)) {
			ret = true;
			String attributeValue = parser.getAttributeValue(null, BOTTOM);
			if (attributeValue != null) {
				themeBean.mFoldericonBean.mFolderIconBottomPath = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, TOP_OPEN);
			if (attributeValue != null) {
				themeBean.mFoldericonBean.mFolderIconTopOpenPath = attributeValue;
			}
			attributeValue = parser.getAttributeValue(null, TOP_CLOSED);
			if (attributeValue != null) {
				themeBean.mFoldericonBean.mFolderIconTopClosedPath = attributeValue;
			}
		}
		return ret;
	}
	
	public void parseFolderXml(XmlPullParser xmlPullParser, ThemeBean bean) {
		XmlPullParser parser = xmlPullParser;
		AppFuncThemeBean themeBean = (AppFuncThemeBean) bean;
		try {
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					String tagName = parser.getName();
					// System.out.println("Start tag " + tagName);
					parseFolderValue(themeBean, tagName, parser);
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}