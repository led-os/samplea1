package com.jiubang.ggheart.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.util.device.Machine;
import com.go.util.lib.AppWidgetManagerWrapper;
import com.go.util.xml.XmlUtils;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.config.ChannelConfig;
import com.jiubang.ggheart.apps.config.GOLauncherConfig;
import com.jiubang.ggheart.apps.gowidget.GoWidgetManager;
import com.jiubang.ggheart.data.info.FavoriteInfo;
import com.jiubang.ggheart.data.info.GoWidgetBaseInfo;
import com.jiubang.ggheart.data.info.ItemInfo;
import com.jiubang.ggheart.data.info.ScreenAppWidgetInfo;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.data.info.UserFolderInfo;
import com.jiubang.ggheart.data.statistics.StaticScreenSettingInfo;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.launcher.UrlLocator;
/**
 * 推荐widget相关类
 * @author
 *
 */
public class FavoriteProvider {
	private static final String TAG_FAVORITES = "favorites";
	private static final String TAG_APPWIDGET = "appwidget";
	private static final String TAG_SHORTCUT = "shortcut";
	private static final String TAG_GOWIDGET = "gowidget";
	private static final String TAG_FOLDER = "folder";

	private Context mContext;
	private ArrayList<ItemInfo> mFavList;
	private AppWidgetHost mAppWidgetHost;

	public FavoriteProvider(Context context, AppWidgetHost widgetHost) {
		mContext = context;
		mAppWidgetHost = widgetHost;
	}

	/**
	 * 从default_gowidget.xml中加载推荐的widget信息
	 * 
	 * @return
	 */
	public ArrayList<ItemInfo> loadFavorite() {
		if (mFavList != null) {
			return mFavList;
		}

		HashMap<Integer, String> urlMap = UrlLocator.getUrlList(mContext);
		try {
			GoWidgetManager goWidgetManager = AppCore.getInstance().getGoWidgetManager();
			PackageManager packageManager = mContext.getPackageManager();
			XmlResourceParser parser = mContext.getResources().getXml(R.xml.default_workspace);
			AttributeSet attrs = Xml.asAttributeSet(parser);
			XmlUtils.beginDocument(parser, TAG_FAVORITES);

			final int depth = parser.getDepth();
			int type;
			int childDepth;
			
			//原有 ： 应用widget ， 除GO 天气外,其余已安装的不推 ； 现在：保留现有规则的前提下,如果用户未安装电子市场,则，应用 widget:只推送 GO Market 和 GO 天气
			boolean isRecommend = true;
			if (GoAppUtils.isMarketNotExitAnd200Channel(mContext)) {
			    isRecommend = false;
            }
			
			while (((type = parser.next()) != XmlPullParser.END_TAG || (parser.getDepth()) > depth)
					&& type != XmlPullParser.END_DOCUMENT) {
				childDepth = parser.getDepth();
				final String name = parser.getName();
				TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.Favorite);
				if (type != XmlPullParser.START_TAG || a == null) {
					continue;
				}

				if (TAG_GOWIDGET.equals(name)) {
					final FavoriteInfo info = getFavoriteInfo(a, urlMap);
					if (info != null && info.mWidgetInfo != null && goWidgetManager != null) {
					    
					    //原有 ： 应用widget ， 除GO 天气外,其余已安装的不推 ； 现在：保留现有规则的前提下,如果用户未安装电子市场,则，应用 widget:只推送 GO Market 和 GO 天气
                        if (!isRecommend
                                && !PackageName.RECOMMAND_GOWEATHEREX_PACKAGE
                                        .equals(info.mWidgetInfo.mPackage)) {

                            if (GoWidgetBaseInfo.PROTOTYPE_APPGAME != info.mWidgetInfo.mPrototype) {
                                continue;
                            }

                        }
					    
						// update by yejijiong 已经安装的widget只有设置了安装也显示的属性才会加进屏幕，否则不加
						String pkgName = goWidgetManager.getWidgetPackage(info.mWidgetInfo);
						if (GoAppUtils.isAppExist(mContext, pkgName) && !info.mShowInstall) {
							continue;
						}

						// update by zhoujun 353渠道，不需要应用游戏中心的widget
						if (info.mWidgetInfo.mPrototype == GoWidgetBaseInfo.PROTOTYPE_APPGAME) {
							final ChannelConfig channelConfig = GOLauncherConfig.getInstance(ApplicationProxy.getContext()).getChannelConfig();
							if (channelConfig != null
									&& (!channelConfig.isNeedAppCenter() || !channelConfig
											.isNeedGameCenter())) {
								continue;
							}
						}
						// update by zhoujun 2012-08-11 end

						if (isFavoriteGoWidgetValid(info)) {

							addFavorite(info);

							GoWidgetBaseInfo baseInfo = info.mWidgetInfo;
							int widgetId = baseInfo.mWidgetId;
							
							if (goWidgetManager != null) {
								// 占用widgetid，防止手动添加时被重复使用
								goWidgetManager.takeWidgetId(widgetId);
							}
						}
					}
				} else if (TAG_APPWIDGET.equals(name)) {
					final ScreenAppWidgetInfo info = getAppWidgetInfo(a, packageManager);
					addFavorite(info);
				} else if (TAG_FOLDER.equals(name)) {
					// 扫描子节点
					UserFolderInfo folderInfo = getUserFolderInfo(a);
					parseFolderContent(parser, folderInfo, attrs);
					addFavorite(folderInfo);
				} else if (TAG_SHORTCUT.equals(name)) {
					final ShortCutInfo info = getShortCutInfo(a);
					addFavorite(info);
				}

				a.recycle();
			}
			parser.close();
			parser = null;
		} catch (XmlPullParserException e) {
			Log.w("favorite", "Got exception parsing favorites.", e);
		} catch (IOException e) {
			Log.w("favorite", "Got exception parsing favorites.", e);
		}

		urlMap.clear();
		urlMap = null;
		centerDefaultWidgetForTablet();
		return mFavList;
	}

	private void parseFolderContent(XmlPullParser parser, UserFolderInfo parent,
			final AttributeSet attrs) throws XmlPullParserException, IOException {

		int type;
		final int depth = parser.getDepth();
		while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
				&& type != XmlPullParser.END_DOCUMENT) {

			TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.Favorite);
			if (type != XmlPullParser.START_TAG || a == null) {
				continue;
			}
			final String name = parser.getName();
			if (name.equals(TAG_SHORTCUT)) {
				final ShortCutInfo info = getShortCutInfo(a);
				if (parent != null) {
					parent.add(info);
				}
			}
			a.recycle();
		}
	}

	private String[] getArea(String areaStringInXml) {
		if (areaStringInXml == null) {
			return null;
		}
		String areaString = areaStringInXml.substring(1, areaStringInXml.length() - 1);
		String[] areaArray = areaString.split(",");
		return areaArray;
	}

	/**
	 * 判断从xml读出的推荐GoWidget是不是可用，例如考虑到不同地区推荐不同产品
	 * 
	 * @param GoWidgetPkg
	 * @return
	 */
	private boolean isFavoriteGoWidgetValid(FavoriteInfo favInfo) {
		if (null == favInfo) {
			return false;
		}

		boolean ret = false;
		String[] validAreaStrings = getArea(favInfo.mValidArea);
		String[] invalidAreaStrings = getArea(favInfo.mInvalidArea);
		if (validAreaStrings == null && invalidAreaStrings == null) {
			// 默认没有指定地区都合法
			return true;
		}

		boolean isValid = Machine.isLocalAreaCodeMatch(validAreaStrings);
		boolean isInvalid = Machine.isLocalAreaCodeMatch(invalidAreaStrings);
		if (isInvalid) {
			ret = false;
		} else if (isValid) {
			ret = true;
		} else {
			ret = false;
		}

		return ret;
	}

	/**
	 * 根据widgetid获取信息
	 * 
	 * @param widgetid
	 * @return
	 */
	public FavoriteInfo getFavoriteInfo(int widgetid) {
		if (mFavList != null) {
			int count = mFavList.size();
			for (int i = 0; i < count; i++) {
				ItemInfo info = mFavList.get(i);
				if (info != null && info instanceof FavoriteInfo) {
					FavoriteInfo favInfo = (FavoriteInfo) info;
					if (favInfo.mWidgetInfo != null && favInfo.mWidgetInfo.mWidgetId == widgetid) {
						return favInfo;
					}
				}
			}
		}
		return null;
	}

	private void addFavorite(ItemInfo info) {
		if (info != null) {
			if (mFavList == null) {
				mFavList = new ArrayList<ItemInfo>();
			}
			mFavList.add(info);
		}
	}
	
	/**
	 * <br>功能简述: 平板上居中默认布局(兼容同一屏幕同一行包含多个默认widget的情况)
	 * <br>功能详细描述: // ADT-16294 屏幕列数大于或等于6时，默认摆放的widget或widget截图居中显示
	 * <br>注意:默认布局同一行不能同时存在widget和icon
	 */
	private void centerDefaultWidgetForTablet() {
		if (mFavList == null || StaticScreenSettingInfo.sScreenCulumn < 6) {
			return;
		}
		for (ItemInfo info : mFavList) {
			// 只针对GoWidget或者系统Widget
			if (info instanceof ScreenAppWidgetInfo || info instanceof FavoriteInfo) {
				int cellXOffset = getCellXOffset(info.mScreenIndex, info.mCellY);
				info.mCellX += cellXOffset;
			}
		}
	}
	
	/**
	 * <br>功能简述: 获取默认widget布局x坐标偏移量
	 * <br>功能详细描述: 用于辅助居中默认布局
	 * <br>注意: 默认布局同一行不能同时存在widget和icon
	 * @param screenIndex 屏幕索引
	 * @param cellY 布局y坐标
	 * @return
	 */
	private int getCellXOffset(int screenIndex, int cellY) {
		if (mFavList == null || StaticScreenSettingInfo.sScreenCulumn < 6) {
			return 0;
		}
		int retval = 0;
		int cellXOccupied = 0;
		for (ItemInfo info : mFavList) {
			// 只针对GoWidget或者系统Widget
			if ((info instanceof ScreenAppWidgetInfo || info instanceof FavoriteInfo)
					&& (info.mScreenIndex == screenIndex && ((cellY < info.mCellY + info.mSpanY) && (cellY >= info.mCellY)))) {
				cellXOccupied += info.mSpanX;
			}
		}
		if (cellXOccupied > 0) {
			retval = (StaticScreenSettingInfo.sScreenCulumn - cellXOccupied) / 2;
		}
		return retval;
	}

	public void removeFavorite(ItemInfo info) {
		if (mFavList == null || info == null) {
			return;
		}
		mFavList.remove(info);
	}

	public void clearFavorite() {
		if (mFavList != null) {
			mFavList.clear();
			mFavList = null;
		}
	}

	private FavoriteInfo getFavoriteInfo(TypedArray a, HashMap<Integer, String> map) {
		FavoriteInfo info = new FavoriteInfo();
		info.mWidgetInfo.mWidgetId = a.getInt(R.styleable.Favorite_widgetid, 0);
		info.mWidgetInfo.mPackage = a.getString(R.styleable.Favorite_packageName);
		info.mWidgetInfo.mClassName = a.getString(R.styleable.Favorite_className);
		info.mWidgetInfo.mLayout = a.getString(R.styleable.Favorite_layout);
		info.mWidgetInfo.mType = a.getInt(R.styleable.Favorite_type, 0);
		info.mWidgetInfo.mTheme = a.getString(R.styleable.Favorite_theme);
		info.mWidgetInfo.mThemeId = a.getInt(R.styleable.Favorite_themeId, -1);
		info.mWidgetInfo.mPrototype = a.getInt(R.styleable.Favorite_prototype,
				GoWidgetBaseInfo.PROTOTYPE_NORMAL);
		info.mValidArea = a.getString(R.styleable.Favorite_validArea);
		info.mInvalidArea = a.getString(R.styleable.Favorite_invalidArea);

		info.mScreenIndex = a.getInt(R.styleable.Favorite_screen, 0);
		info.mCellX = a.getInt(R.styleable.Favorite_x, 0);
		info.mCellY = a.getInt(R.styleable.Favorite_y, 0);
		info.mSpanX = a.getInt(R.styleable.Favorite_spanX, 0);
		// 增加详情描述
		info.mDetailId = a.getResourceId(R.styleable.Favorite_detail, R.string.fav_app);

		// 实施统计相关
		info.mClickUrl = a.getString(R.styleable.Favorite_widgetclickurl);
		info.mAId = a.getString(R.styleable.Favorite_widgetaid);
		info.mMapId = a.getString(R.styleable.Favorite_widgetmapid);
		info.mGALink = a.getString(R.styleable.Favorite_GALink);
		/*
		 * change by dengdazhong
		 * 根据5x5默认布局修改widget大小
		 */
		if (StaticScreenSettingInfo.sScreenCulumn == 5) {
			info.mSpanX = 5;
		}
		info.mSpanY = a.getInt(R.styleable.Favorite_spanY, 0);

		info.mPreview = a.getResourceId(R.styleable.Favorite_preview, 0);
		info.mTitleId = a.getResourceId(R.styleable.Favorite_title, 0);
		if (info.mTitleId > 0) {
			if (map != null) {
				info.mUrl = map.get(info.mTitleId);
			}
		}
		info.mShowInstall = a.getBoolean(R.styleable.Favorite_showInstall, false);
		return info;
	}

	private ShortCutInfo getShortCutInfo(TypedArray a) {
		ShortCutInfo info = new ShortCutInfo();
		ComponentName cn = getComponentName(a);
		if (cn != null) {
			info.setActivity(cn, Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		info.mScreenIndex = a.getInt(R.styleable.Favorite_screen, 0);
		info.mCellX = a.getInt(R.styleable.Favorite_x, 0);
		info.mCellY = a.getInt(R.styleable.Favorite_y, 0);
		info.mSpanX = 1;
		info.mSpanY = 1;
		return info;
	}

	private UserFolderInfo getUserFolderInfo(TypedArray a) {
		UserFolderInfo info = new UserFolderInfo();
		info.mScreenIndex = a.getInt(R.styleable.Favorite_screen, 0);
		int titleid = a.getResourceId(R.styleable.Favorite_title, 0);
		if (titleid > 0) {
			String label = mContext.getString(titleid);
			info.mTitle = label;
			info.setFeatureTitle(label);
		}
		info.mCellX = a.getInt(R.styleable.Favorite_x, 0);
		info.mCellY = a.getInt(R.styleable.Favorite_y, 0);
		info.mSpanX = 1;
		info.mSpanY = 1;
		return info;
	}

	private ScreenAppWidgetInfo getAppWidgetInfo(TypedArray a, PackageManager packageManager) {
		String packageName = a.getString(R.styleable.Favorite_packageName);
		String className = a.getString(R.styleable.Favorite_className);

		if (packageName == null || className == null) {
			return null;
		}

		boolean hasPackage = true;
		ComponentName cn = new ComponentName(packageName, className);
		try {
			packageManager.getReceiverInfo(cn, 0);
		} catch (Exception e) {
			String[] packages = packageManager
					.currentToCanonicalPackageNames(new String[] { packageName });
			cn = new ComponentName(packages[0], className);
			try {
				packageManager.getReceiverInfo(cn, 0);
			} catch (Exception e1) {
				hasPackage = false;
			}
		}

		if (hasPackage) {
			int spanX = a.getInt(R.styleable.Favorite_spanX, 0);
			int spanY = a.getInt(R.styleable.Favorite_spanY, 0);

			boolean allocatedAppWidgets = false;
			final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
			try {
				int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
				allocatedAppWidgets = true;
				AppWidgetManagerWrapper.bindAppWidgetId(appWidgetManager, appWidgetId, cn);

				if (allocatedAppWidgets) {
					ScreenAppWidgetInfo info = new ScreenAppWidgetInfo(appWidgetId, cn);
					info.mScreenIndex = a.getInt(R.styleable.Favorite_screen, 0);
					info.mCellX = a.getInt(R.styleable.Favorite_x, 0);
					info.mCellY = a.getInt(R.styleable.Favorite_y, 0);
					info.mSpanX = spanX;
					info.mSpanY = spanY;
					return info;
				}

			} catch (RuntimeException ex) {
				Log.e("FavoriteProvider", "Problem allocating appWidgetId", ex);
			}
		}
		return null;
	}

	private ComponentName getComponentName(TypedArray a) {
		String packageName = a.getString(R.styleable.Favorite_packageName);
		String className = a.getString(R.styleable.Favorite_className);
		return new ComponentName(packageName, className);
	}

	/**
	 * Gets an appwidget provider from the given package. If the package
	 * contains more than one appwidget provider, an arbitrary one is returned.
	 */
	private ComponentName getProviderInPackage(String packageName) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
		List<AppWidgetProviderInfo> providers = appWidgetManager.getInstalledProviders();
		if (providers == null) {
			return null;
		}
		final int providerCount = providers.size();
		for (int i = 0; i < providerCount; i++) {
			ComponentName provider = providers.get(i).provider;
			if (provider != null && provider.getPackageName().equals(packageName)) {
				return provider;
			}
		}
		return null;
	}

}
