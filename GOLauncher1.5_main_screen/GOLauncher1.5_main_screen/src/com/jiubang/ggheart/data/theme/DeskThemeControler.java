package com.jiubang.ggheart.data.theme;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.SettingProxy;
import com.go.util.window.WindowControl;
import com.golauncher.message.IAppCoreMsgId;
import com.jiubang.core.framework.ICleanable;
import com.jiubang.ggheart.apps.desks.diy.OutOfMemoryHandler;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.data.AppCore;
import com.jiubang.ggheart.data.info.DesktopSettingInfo;
import com.jiubang.ggheart.data.theme.bean.DeskThemeBean;
import com.jiubang.ggheart.data.theme.bean.ThemeBean;

/**
 * 
 * @author yangguanxiang
 *
 */
public class DeskThemeControler implements ICleanable {
	private ThemeManager mThemeManager;
	private DeskThemeBean mDeskThemeBean;
	private ImageExplorer mImageExplorer;
	private Context mContext;

	public DeskThemeControler(Context context) {
		mContext = context;

		// 监听主题
		mThemeManager = ThemeManager.getInstance(ApplicationProxy.getContext());

		initDeskThemeBean();

		// 主题资源提供器
		mImageExplorer = AppCore.getInstance().getImageExplorer();
	}

	@Override
	public void cleanup() {
	}

	private void initDeskThemeBean() {
		// 获取数据
		mDeskThemeBean = null;
		if (mThemeManager.isUsedTheme()) {
			ThemeBean bean = mThemeManager.getThemeBean(ThemeBean.THEMEBEAN_TYPE_DESK);
			if (null != bean && bean instanceof DeskThemeBean) {
				mDeskThemeBean = (DeskThemeBean) bean;
			}
		}
		if (null == mDeskThemeBean) {
			mDeskThemeBean = new DeskThemeBean(IGoLauncherClassName.DEFAULT_THEME_PACKAGE);
		}
	}

	public void handleLauncherEvent(int msgId, int param, Object ...object) {
		switch (msgId) {
			case IAppCoreMsgId.EVENT_THEME_CHANGED : {
				initDeskThemeBean();
				doThemeChanged(param);
			}
				break;

			default :
				break;
		}
	}

	public boolean isUsedTheme() {
		return mThemeManager.isUsedTheme();
	}

	public DeskThemeBean getDeskThemeBean() {
		return mDeskThemeBean;
	}

	public Drawable getThemeResDrawable(String resName) {
		return mImageExplorer.getDrawable(resName);
	}

	public int getDrawableIdentifier(String drawableName) {
		return mImageExplorer.getResourceId(drawableName);
	}

	/**
	 * 获取皮肤包中的图片资源
	 * 
	 * @param resName
	 *            皮肤包中的资源名称
	 * @param defaultId
	 *            默认主题的Drawable id
	 * @return Drawable
	 */
	public Drawable getDrawable(String resName, int defaultId) {
		Drawable drawable = null;
		try {
			drawable = mImageExplorer.getDrawable(resName);
			if (drawable == null) {
				drawable = mContext.getResources().getDrawable(defaultId);
			}
		} catch (OutOfMemoryError e) {
			OutOfMemoryHandler.handle();
		}
		return drawable;
	}

	/**
	 * 获取皮肤包中的图片资源
	 * 
	 * @param resName
	 *            皮肤包中的资源名称
	 * @param defaultId
	 *            默认主题的Drawable id
	 * @return Drawable
	 */
	public Drawable getDrawable(String resName, int defaultId, String packageName) {
		Drawable drawable = null;
		try {
			drawable = mImageExplorer.getDrawable(packageName, resName);
			if (drawable == null) {
				drawable = mContext.getResources().getDrawable(defaultId);
			}
		} catch (OutOfMemoryError e) {
			OutOfMemoryHandler.handle();
		}
		return drawable;
	}

	/**
	 * 获取皮肤包中的图片资源
	 * 
	 * @param resName
	 *            皮肤包中的资源名称
	 * @param localPath
	 *            图片路径
	 * @return Drawable
	 */
	public Drawable getDrawable(String resName, String localPath) {
		Drawable drawable = null;
		try {
			drawable = mImageExplorer.getDrawable(resName);
			if (drawable == null) {
				drawable = Drawable.createFromPath(localPath);
			}
		} catch (OutOfMemoryError e) {
			OutOfMemoryHandler.handle();
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// 路径找不到文件
			e.printStackTrace();
		}
		return drawable;
	}

	/*
	 * flag: 1 -- change theme 0 -- sdcard ok or 2 minite later
	 */
	private void doThemeChanged(int flag) {
		// modify by yangbing 2012-06-30 for 使用默认主题时，需要同时应用ui3.0主题的的壁纸
		DeskThemeBean currentThemeBean = mDeskThemeBean;
		if (!isUsedTheme()) {
			// 使用的默认主题，根据最新需求，使用默认主题时，需要同时应用成ui3.0主题的的壁纸。
			// NOTE:此处直接写死ui3.0主题不加密。若后续有修改，需要修改此处。
			currentThemeBean = (DeskThemeBean) ThemeManager.getInstance(mContext)
					.getDeskThemeParser()
					.autoParseAppThemeXml(mContext, IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3, false);
		}
		if (currentThemeBean != null && currentThemeBean.mWallpaper != null
				&& currentThemeBean.mWallpaper.mResName != null) {
			final int wallpaperId = getDrawableIdentifier(currentThemeBean.mWallpaper.mResName);
			final Resources themeResources = getThemeResources();
			if (wallpaperId > 0 && themeResources != null) {
				boolean ret = WindowControl.setWallpaper(mContext, themeResources, wallpaperId);
				if (!ret) {
					try {
						DeskToast.makeText(mContext, mContext.getString(R.string.set_wallpaper_error),
								Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		}
		// modify by yangbing 2012-06-30 for 使用默认主题时，需要同时应用ui3.0主题的的壁纸 end

		if (1 == flag) {
			// 更新主题图标设置
			DesktopSettingInfo info = SettingProxy.getDesktopSettingInfo();
			info.mThemeIconStylePackage = mThemeManager.getCurThemePackage();
			info.mFolderThemeIconStylePackage = mThemeManager.getCurThemePackage();
			info.mGGmenuThemeIconStylePackage = mThemeManager.getCurThemePackage();
			SettingProxy.updateDesktopSettingInfo(info);

			// ScreenSettingInfo screen = controler.getScreenSettingInfo();
			// screen.mIndicatorShowmode =
			// mThemeManager.getCurThemePackage();
			// controler.updateScreenSettingInfo(screen);
		}
	}

	public Resources getThemeResources() {
		return mThemeManager.getCurThemeResources();
	}
}
