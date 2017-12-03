package com.jiubang.ggheart.apps.appfunc.theme;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.SettingProxy;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.appfunc.help.AppFuncConstants;
import com.jiubang.ggheart.apps.desks.appfunc.help.AppFuncUtils;
import com.jiubang.ggheart.apps.desks.appfunc.model.DeliverMsgManager;
import com.jiubang.ggheart.apps.desks.diy.OutOfMemoryHandler;
import com.jiubang.ggheart.data.theme.ImageExplorer;
import com.jiubang.ggheart.data.theme.ThemeConfig;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.data.theme.XmlParserFactory;
import com.jiubang.ggheart.data.theme.bean.AppFuncThemeBean;
import com.jiubang.ggheart.data.theme.bean.ThemeBean;
import com.jiubang.ggheart.data.theme.parser.DodolThemeResourceParser;
import com.jiubang.ggheart.data.theme.parser.FuncThemeParser;
/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2012-9-13]
 */
public class AppFuncThemeController {
	private static final Pattern PATTERN = Pattern.compile("\\d+");
	private AppFuncThemeBean mAppFuncThemeBean;
	private Context mContext;
	private ThemeManager mThemeManager;
	private ImageExplorer mImageExplorer;

	/**
	 * 是否使用主题
	 */
	private boolean mIsHasTheme;
	
	private static AppFuncThemeController sInstance;
	
	public static AppFuncThemeController getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new AppFuncThemeController(context);
		}
		return sInstance;
	}

	private AppFuncThemeController(Context context) {
		this.mContext = context;
		mThemeManager = ThemeManager.getInstance(ApplicationProxy.getContext());
		mImageExplorer = ImageExplorer.getInstance(context);
		initThemeData();
	}

	/**
	 * 是否为默认主题
	 * 
	 * @return
	 */
	public boolean isDefaultTheme() {
		if (mThemeManager != null && mThemeManager.getCurThemeInfoBean() != null) {
			String curThemeName = mThemeManager.getCurThemeInfoBean().getPackageName();
			if (ThemeManager.isAsDefaultThemeToDo(curThemeName)) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 主题内容初始化或者重置时，重新获取最新主题的数据
	 */
	private void initThemeData() {
		if (isUsedTheme()) {
			mIsHasTheme = true;
			mAppFuncThemeBean = (AppFuncThemeBean) mThemeManager
					.getThemeBean(ThemeBean.THEMEBEAN_TYPE_FUNCAPP);
			// 保护代码
			if (mAppFuncThemeBean == null) {
				mAppFuncThemeBean = new AppFuncThemeBean();
			}

		} else {
			mIsHasTheme = false;
			mAppFuncThemeBean = new AppFuncThemeBean();
		}
		parseTabHomeTheme(true);
		parseIndicatorTheme();
		parseFolderTheme();

	}

	private void parseIndicatorTheme() {

		String themePackage = SettingProxy.getFunAppSetting()
				.getIndicatorSetting();
		if (themePackage.equals(IGoLauncherClassName.DEFAULT_THEME_PACKAGE)) {
			mAppFuncThemeBean.mIndicatorBean = mAppFuncThemeBean.new IndicatorBean();
		} else {
			if (!GoAppUtils.isAppExist(mContext, themePackage)) {
				themePackage = ThemeManager.getInstance(ApplicationProxy.getContext()).getCurThemePackage();
			}
			InputStream inputStream = null;
			XmlPullParser xmlPullParser = null;
			AppFuncThemeBean themeBean = null;
			ThemeManager thememanager = ThemeManager.getInstance(ApplicationProxy.getContext());
			boolean isAtomTheme = thememanager.isAtomTheme(themePackage);
			if (isAtomTheme) { //如果是atom主题，不用解析
				return;
			}
			String fileName = ThemeConfig.APPFUNCTHEMEFILENAME;
			boolean isDodolTheme = thememanager.isDodolTheme(themePackage);
			if (isDodolTheme) {
				fileName = ThemeConfig.DODOLTHEMERESOURCE;
			}
			inputStream = thememanager.createParserInputStream(themePackage,
					fileName);
			if (inputStream != null) {
				xmlPullParser = XmlParserFactory.createXmlParser(inputStream);
			} else {
				xmlPullParser = XmlParserFactory.createXmlParser(mContext,
						fileName, themePackage);
			}

			if (xmlPullParser != null) {
				themeBean = new AppFuncThemeBean(themePackage);
				if (isDodolTheme) {
					DodolThemeResourceParser parser = new DodolThemeResourceParser();
					parser.parseIndicatorXml(xmlPullParser, null, themeBean);
					parser = null;
				} else {
					FuncThemeParser parser = new FuncThemeParser();
					parser.parseIndicatorXml(xmlPullParser, themeBean);
					parser = null;
				}
				mAppFuncThemeBean.mIndicatorBean = themeBean.mIndicatorBean;
			}
			// 关闭inputStream
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.i("ThemeManager", "IOException for close inputSteam");
				}
			}
		}

	}

	public void parseTabHomeTheme(boolean bFromeInit) {

		String themePackage = SettingProxy.getFunAppSetting()
				.getTabHomeBgSetting();
		if (mAppFuncThemeBean.mTabThemePkg.equals(themePackage)) {
			return;
		}
		if (ThemeManager.isAsDefaultThemeToDo(themePackage)) {
			mAppFuncThemeBean.initTabHomeBean();
		} else {
			if (!ThemeManager.isInstalledTheme(mContext, themePackage)) {
				themePackage = ThemeManager.getInstance(ApplicationProxy.getContext()).getCurThemePackage();
			}
			if (ThemeManager.getInstance(ApplicationProxy.getContext()).isAtomTheme(themePackage)) {
				return;
			}
			InputStream inputStream = null;
			XmlPullParser xmlPullParser = null;
			AppFuncThemeBean themeBean = null;
			ThemeManager thememanager = ThemeManager.getInstance(ApplicationProxy.getContext());
			String fileName = ThemeConfig.APPFUNCTHEMEFILENAME;
			boolean isDodolTheme = ThemeManager.getInstance(ApplicationProxy.getContext()).isDodolTheme(themePackage);
			if (isDodolTheme) {
				fileName = ThemeConfig.DODOLTHEMERESOURCE;
			}
			inputStream = thememanager.createParserInputStream(themePackage,
					fileName);
			if (inputStream != null) {
				xmlPullParser = XmlParserFactory.createXmlParser(inputStream);
			} else {
				xmlPullParser = XmlParserFactory.createXmlParser(mContext,
						fileName, themePackage);
			}
			if (xmlPullParser != null) {
				themeBean = new AppFuncThemeBean(themePackage);
				if (isDodolTheme) {
					DodolThemeResourceParser parser = new DodolThemeResourceParser();
					parser.parseTabHomeXml(xmlPullParser, themeBean);
					parser = null;
				} else {
					FuncThemeParser parser = new FuncThemeParser();
					parser.parseTabHomeXml(xmlPullParser, themeBean);
					parser = null;
				}
				mAppFuncThemeBean.mTabThemePkg = themePackage;
				mAppFuncThemeBean.mAllAppDockBean = themeBean.mAllAppDockBean;
				mAppFuncThemeBean.mSwitchButtonBean = themeBean.mSwitchButtonBean;
				mAppFuncThemeBean.mRuningDockBean = themeBean.mRuningDockBean;
				mAppFuncThemeBean.mAllAppMenuBean = themeBean.mAllAppMenuBean;
				mAppFuncThemeBean.mSidebarBean = themeBean.mSidebarBean;
				mAppFuncThemeBean.mBeautyBean = themeBean.mBeautyBean;
				mAppFuncThemeBean.mGLAppDrawTopBean = themeBean.mGLAppDrawTopBean;
			}
			// 关闭inputStream
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.i("ThemeManager", "IOException for close inputSteam");
				}
			}
		}
	}

	public boolean parseFolderTheme() {

		String themePackage = ThemeManager.getInstance(ApplicationProxy.getContext()).getScreenStyleSettingInfo()
				.getFolderStyle();
		if (mAppFuncThemeBean.mFoldericonBean.mPackageName != null
				&& mAppFuncThemeBean.mFoldericonBean.mPackageName.equals(themePackage)
				|| ThemeManager.getInstance(ApplicationProxy.getContext()).isAtomTheme(themePackage)) {
			return false;
		}
		mAppFuncThemeBean.initFolderThemeBean();
		if (!themePackage.equals(IGoLauncherClassName.DEFAULT_THEME_PACKAGE)) {
			if (!ThemeManager.isInstalledTheme(mContext, themePackage)) {
				themePackage = ThemeManager.getInstance(ApplicationProxy.getContext()).getCurThemePackage();
			}
			InputStream inputStream = null;
			XmlPullParser xmlPullParser = null;
			AppFuncThemeBean themeBean = null;
			String fileName = ThemeConfig.APPFUNCTHEMEFILENAME;
			boolean isDodolTheme = ThemeManager.getInstance(ApplicationProxy.getContext()).isDodolTheme(themePackage);
			if (isDodolTheme) {
				fileName = ThemeConfig.DODOLTHEMERESOURCE;
			}
			inputStream = ThemeManager.getInstance(ApplicationProxy.getContext())
					.createParserInputStream(themePackage, fileName);
			if (inputStream != null) {
				xmlPullParser = XmlParserFactory.createXmlParser(inputStream);
			} else {
				xmlPullParser = XmlParserFactory.createXmlParser(mContext,
						fileName, themePackage);
			}
			if (xmlPullParser != null) {
				themeBean = new AppFuncThemeBean(themePackage);
				if (isDodolTheme) {
					DodolThemeResourceParser parser = new DodolThemeResourceParser();
					parser.parseFolderXml(xmlPullParser, themeBean);
					parser = null;
				} else {
					FuncThemeParser parser = new FuncThemeParser();
					parser.parseFolderXml(xmlPullParser, themeBean);
					parser = null;
				}
				mAppFuncThemeBean.mFoldericonBean = themeBean.mFoldericonBean;
			}
			// 关闭inputStream
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.i("ThemeManager", "IOException for close inputSteam");
				}
			}
		}
		mAppFuncThemeBean.mFoldericonBean.mPackageName = themePackage;
		return true;
	}

	/**
	 * 如果是桌面文件夹的图片就不需做recycle，所以不添加
	 * 
	 * @param drawableName
	 * @return
	 */
	public Drawable getDrawable(String drawableName) {
		return getDrawable(drawableName, true);
	}

	/**
	 * 如果是桌面文件夹的图片就不需做recycle，所以不添加
	 * 
	 * @param drawableName
	 * @return
	 */
	public Drawable getDrawable(String drawableName, String packageName) {
		if (null == packageName) {
			return getDrawable(drawableName, true);
		}
		return getDrawable(drawableName, false, packageName);

	}

	/**
	 * 通过AppFuncThemeBean的字符串键值获取像对应的资源
	 * 
	 * @param drawableName
	 * @return
	 */
	public Drawable getDrawable(String drawableName, String packageName, boolean addToHashMap) {
		if (null == packageName) {
			return getDrawable(drawableName, addToHashMap);
		} else {
			return getDrawable(drawableName, addToHashMap, packageName);
		}
	}

	/**
	 * 通过AppFuncThemeBean的字符串键值获取像对应的资源
	 * 
	 * @param drawableName
	 * @param addToHashMap
	 *            是否添加到缓存的hashMap里面
	 * @return Drawable
	 */
	public Drawable getDrawable(String drawableName, boolean addToHashMap) {
		Drawable drawable = null;
		if (drawableName.equals(AppFuncConstants.NONE)) {
			return drawable;
		}

		AppFuncUtils funcUtil = AppFuncUtils.getInstance(mContext);
		if (drawableName.trim().compareTo("") != 0) {
			try {
				if (mIsHasTheme) {
					// 先从功能表主题资源管理器取图片
					boolean matches = false;
					try {
						matches = PATTERN.matcher(drawableName).matches();
					} catch (PatternSyntaxException e) {
						// e.printStackTrace();
						Log.i("XViewFrame", "match pattern error, drawableName =" + drawableName);
					}

					if (!matches) {
						int resourceId = mImageExplorer.getResourceId(drawableName);
						if (resourceId > 0) {
							drawable = funcUtil.getDrawableFromPicManager(resourceId);
							// 如果没有，再从图片资源管理器取
							if (drawable == null) {
								drawable = mImageExplorer.getDrawable(drawableName);
								if (drawable != null && addToHashMap) {
									// 加入功能表主题资源管理器
									funcUtil.addToPicManager(resourceId, drawable);
								}
							}
						}
					} else {
						// 特殊处理删除图标和文件夹编辑图标
						int drawableId = Integer.valueOf(drawableName).intValue();
						// if (drawableId == R.drawable.kill
						// || drawableId == R.drawable.kill_light
						// ||drawableId == R.drawable.eidt_folder
						// ||drawableId == R.drawable.eidt_folder_light) {
						// // 主题未提供，直接从主程序包里面取
						// drawable =
						// funcUtil.getDrawableFromMainPkg(drawableId);
						// } else {
						// drawable = funcUtil.getDrawable(drawableId,
						// addToHashMap);
						// }

						// 主题未提供，直接从主程序包里面取
						drawable = funcUtil.getDrawableFromMainPkg(drawableId);
					}
				} else {
					drawable = funcUtil.getDrawable(Integer.valueOf(drawableName).intValue(),
							addToHashMap);
				}
			} catch (OutOfMemoryError e) {
				OutOfMemoryHandler.handle();
			} catch (NumberFormatException e) {
				Log.i("XViewFrame", "AppFuncThemeController getDrawable error, drawableName ="
						+ drawableName);
			}
		}
		return drawable;
	}

	/**
	 * 通过AppFuncThemeBean的字符串键值获取像对应的资源
	 * 
	 * @param drawableName
	 * @param addToHashMap
	 *            是否添加到缓存的hashMap里面
	 * @return Drawable
	 */
	public Drawable getDrawable(String drawableName, boolean addToHashMap, String packageName) {
		Drawable drawable = null;
		if (drawableName == null || drawableName.equals(AppFuncConstants.NONE)) {
			return drawable;
		}

		AppFuncUtils funcUtil = AppFuncUtils.getInstance(mContext);
		if (drawableName.trim().compareTo("") != 0) {
			try {
				if (!packageName.equals(IGoLauncherClassName.DEFAULT_THEME_PACKAGE)) {
					// 先从功能表主题资源管理器取图片
					boolean matches = false;
					try {
						matches = PATTERN.matcher(drawableName).matches();
					} catch (PatternSyntaxException e) {
						// e.printStackTrace();
						Log.i("XViewFrame", "match pattern error, drawableName =" + drawableName);
					}

					if (!matches) {
						int resourceId = mImageExplorer.getResourceId(drawableName, packageName);
						if (resourceId > 0) {
							// 如果没有，再从图片资源管理器取
							if (drawable == null) {
								drawable = mImageExplorer.getDrawable(packageName, drawableName);
							}
						}
					} else {
						// 特殊处理删除图标和文件夹编辑图标
						int drawableId = Integer.valueOf(drawableName).intValue();
						// if (drawableId == R.drawable.kill
						// || drawableId == R.drawable.kill_light
						// ||drawableId == R.drawable.eidt_folder
						// ||drawableId == R.drawable.eidt_folder_light) {
						// // 主题未提供，直接从主程序包里面取
						// drawable =
						// funcUtil.getDrawableFromMainPkg(drawableId);
						// } else {
						// drawable = funcUtil.getDrawable(drawableId,
						// addToHashMap);
						// }
						// 主题未提供，直接从主程序包里面取
						drawable = funcUtil.getDrawableFromMainPkg(drawableId);
					}
				} else {
					if (PATTERN.matcher(drawableName).matches()) {

						drawable = funcUtil.getDrawableFromMainPkg(Integer.valueOf(drawableName)
								.intValue());
					} else {
						int resourceId = mImageExplorer.getResourceId(drawableName, packageName);
						if (resourceId > 0) {
							// 如果没有，再从图片资源管理器取
							if (drawable == null) {
								drawable = mImageExplorer.getDrawable(packageName, drawableName);
							}
						}
					}
				}
			} catch (OutOfMemoryError e) {
				OutOfMemoryHandler.handle();
			} catch (NumberFormatException e) {
				Log.i("XViewFrame", "AppFuncThemeController getDrawable error, drawableName ="
						+ drawableName);
			}
		}
		return drawable;
	}

	/**
	 * 获取主题的数据结构Bean
	 * 
	 * @return AppFuncThemeBean
	 */
	public AppFuncThemeBean getThemeBean() {
		return mAppFuncThemeBean;
	}

	/**
	 * 判断是否使用主题
	 * 
	 * @return true for 使用其他主题，false for 默认主题
	 */
	private boolean isUsedTheme() {
		return mThemeManager.isUsedTheme();
	}

	public void reloadThemeData() {
		// 通知释放资源
		preLoadResources();
		// 清空资源
		AppFuncUtils.getInstance(mContext).clearResources();
		ImageExplorer.getInstance(mContext).clearData();
		// 重新加载资源
		initThemeData();
		// 通知注册此消息的组件重新获取主题资源
		DeliverMsgManager.getInstance().sendMsg(AppFuncConstants.LOADTHEMERES, null);
		Log.d("XViewFrame", "Received EVENT_THEME_CHANGED");
	}

	/**
	 * 预处理图片
	 */
	private void preLoadResources() {
		// 通知组件释放资源
		DeliverMsgManager.getInstance().sendMsg(AppFuncConstants.THEME_CHANGE, null);
	}
}
