package com.jiubang.ggheart.data.theme.bean;

import com.gau.go.launcherex.R;
import com.go.commomidentify.IGoLauncherClassName;
import com.jiubang.ggheart.apps.desks.appfunc.help.AppFuncConstants;

/**
 * 功能表主题的Bean
 * 
 * @author wenjiaming
 * 
 */
public class AppFuncThemeBean extends AppFuncBaseThemeBean {

	public AppFuncThemeBean() {
		this(IGoLauncherClassName.DEFAULT_THEME_PACKAGE);
	}

	public AppFuncThemeBean(String pkgName) {
		super(pkgName);
		mBeanType = THEMEBEAN_TYPE_FUNCAPP;
		mWallpaperBean = new WallpaperBean();
		mFoldericonBean = new FoldericonBean();
		mGLAppDrawTopBean = new GLAppDrawTopBean();
		mIndicatorBean = new IndicatorBean();
		mAppIconBean = new AppIconBean();
		mAllAppMenuBean = new AllAppMenuBean();
		mAllAppDockBean = new AllAppDockBean();
		mRuningDockBean = new RuningDockBean();
		mSwitchMenuBean = new SwitchMenuBean();
		mSwitchButtonBean = new SwitchButtonBean();
		mBeautyBean = new BeautyBean();
		mSidebarBean = new SidebarBean();
	}

	/**
	 * 壁纸bean
	 * @author yangguanxiang
	 *
	 */
	public class WallpaperBean extends AbsWallpaperBean {

		@Override
		protected void init() {
			mBackgroudColor = AppFuncConstants.DEFAULT_BG_COLOR;
			mImagePath = "";
		}

	}
	/**
	 * 对应XML的Foldericon的Tag
	 * 
	 * @author wenjiaming
	 * 
	 */
	public class FoldericonBean extends AbsFoldericonBean {

		@Override
		protected void init() {
			mPackageName = IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
			mFolderIconBottomPath = "-1";
			mFolderIconTopOpenPath = "-1";
			mFolderIconTopClosedPath = "-1";
		}

	}

	/**
	 * 
	 * @author yangguanxiang
	 *
	 */
	public class GLAppDrawTopBean extends AbsGLAppDrawTopBean {

		@Override
		protected void init() {
			mGLAppDrawTopBgBottomVerPath = "-1";
			mGLAppDrawTopBgDrawingWay = (byte) 1;
		}
	}
	
	/**
	 * 对应XML的AllAppDock的Tag
	 * 
	 */
	public class AllAppDockBean extends AbsAllAppDockBean {

		@Override
		protected void init() {
			mHomeMenu = "-1";
			mHomeMenuSelected = "-1";
		}
	}

	/**
	 * 对应XML的AllAppMenu的Tag
	 * 
	 */
	public class AllAppMenuBean extends AbsAllAppMenuBean {

		@Override
		protected void init() {
			mMenuBgV = "-1";
			mMenuDividerV = "-1";
			mMenuTextColor = 0xffffffff;
			mMenuItemSelected = "-1";
			
			mMenuMemoryCleanBtnNormal = "-1";
			mMenuMemoryCleanBtnTensity = "-1";
			mMenuMemoryCleanBtnSeriousness = "-1";
			mMenuMemoryCleanProgressBarNormal = 0x44B419;
			mMenuMemoryCleanProgressBarTensity = 0xFF8800;
			mMenuMemoryCleanProgressBarSeriousness = 0xFF2A2A;
			mMenuForwardPromanage = "-1";
			
		}
	}

	/**
	 * 对应XML的RuningDock的Tag
	 * 
	 */
	public class RuningDockBean extends AbsRuningDockBean {

		@Override
		protected void init() {
			mHomeLockListNormal = "-1";
			mHomeLockListLight = "-1";
			mHomeRunningInfoImg = "-1";
			mHomeRunningLockImg = "-1";
			mHomeEditDockBgV = "-1";
			mHomeEditDockTouchBgV = "-1";
			mHomeRunningUnLockImg = "-1";
		}
	}

	/**
	 * 滚动条指示器
	 * 
	 * @author wenjiaming
	 * 
	 */
	public class IndicatorBean extends AbsIndicatorBean {

		@Override
		protected void init() {
			// 换新的指示器样式，by dengdazhong 2013-06-24
			indicatorCurrentHor = "-1";
			indicatorHor = "-1";
		}
	}

	/**
	 * 应用程序
	 * 
	 * @author wenjiaming
	 * 
	 */
	public class AppIconBean extends AbsAppIconBean {

		@Override
		protected void init() {
			mTextColor = AppFuncConstants.ICON_TEXT_COLOR;
			mIconBgColor = AppFuncConstants.ICON_BG_FOCUSED;
			mDeletApp = "-1";
			mDeletHighlightApp = "-1";
			mNewApp = "-1";
			mUpdateIcon = "-1";
			mLockApp = "-1";
			mKillApp = "-1";
			mKillAppLight = "-1";
		}
	}

	/**
	 * 美化中心
	 * @author zhangxi
	 */
	public class BeautyBean extends AbsBeautyBean {
		
		protected void init() {
			mBeautyIcon = "-1";
		}
		
	}
	/**
	 * 
	 * <br>类描述:功能表拉动侧边栏图标
	 * <br>功能详细描述:功能表拉动侧边栏图标
	 * 
	 * @author  zhangxi
	 * @date  [2013年9月5日]
	 */
	public class SidebarBean extends AbsSidebarBean {
		
		protected void init() {
			mTitleColor = 0xFFFFFFFF;
			mSidebarIcon = "-1";
			mLogoIcon = "-1";
		}
	}

	public void initTabHomeBean() {
		mGLAppDrawTopBean = new GLAppDrawTopBean();
		mAllAppMenuBean = new AllAppMenuBean();
		mAllAppDockBean = new AllAppDockBean();
		mSwitchButtonBean = new SwitchButtonBean();
		mRuningDockBean = new RuningDockBean();
		mSidebarBean = new SidebarBean();
		mBeautyBean = new BeautyBean();
		mGLAppDrawTopBean = new GLAppDrawTopBean();
	}

	public void initFolderThemeBean() {
		if (mFoldericonBean == null) {
			mFoldericonBean = new FoldericonBean();
		} else {
			mFoldericonBean.mFolderIconBottomPath = "-1";
			mFoldericonBean.mFolderIconTopOpenPath = "-1";
			mFoldericonBean.mFolderIconTopClosedPath = "-1";
		}
	}

	/**
	 * 
	 * 主题Bean
	 */
	public class SwitchMenuBean extends AbsSwitchMenuBean {

		@Override
		protected void init() {
			mPackageName = IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
			mMenuBgV = Integer.toString(R.drawable.appfunc_switch_menu_bg);
			mMenuBgH = Integer.toString(R.drawable.appfunc_switch_menu_bg_h);
			mMenuDividerV = Integer.toString(R.drawable.allfunc_allapp_menu_line);
			mMenuDividerH = Integer.toString(R.drawable.allfunc_allapp_menu_line);
			mMenuTextColor = 0xff000000;
			mMenuItemSelected = Integer.toString(R.drawable.transparent);

			mItemLabelAppSelectors = new String[] {
					Integer.toString(R.drawable.switch_menu_image_selector),
					Integer.toString(R.drawable.switch_menu_audio_selector),
					Integer.toString(R.drawable.switch_menu_video_selector),
					Integer.toString(R.drawable.switch_menu_search_selector) };

			mItemLabelImageSelectors = new String[] {
					Integer.toString(R.drawable.switch_menu_app_selector),
					Integer.toString(R.drawable.switch_menu_audio_selector),
					Integer.toString(R.drawable.switch_menu_video_selector),
					Integer.toString(R.drawable.switch_menu_search_selector) };

			mItemLabelAudioSelectors = new String[] {
					Integer.toString(R.drawable.switch_menu_app_selector),
					Integer.toString(R.drawable.switch_menu_image_selector),
					Integer.toString(R.drawable.switch_menu_video_selector),
					Integer.toString(R.drawable.switch_menu_search_selector) };
			mItemLabelVedioSelectors = new String[] {
					Integer.toString(R.drawable.switch_menu_app_selector),
					Integer.toString(R.drawable.switch_menu_image_selector),
					Integer.toString(R.drawable.switch_menu_audio_selector),
					Integer.toString(R.drawable.switch_menu_search_selector) };
			mItemLabelSearchSelectors = new String[] {
					Integer.toString(R.drawable.switch_menu_app_selector),
					Integer.toString(R.drawable.switch_menu_image_selector),
					Integer.toString(R.drawable.switch_menu_audio_selector),
					Integer.toString(R.drawable.switch_menu_video_selector) };
		}
	}

	/**
	 * 
	 * 选项卡按钮Bean
	 */
	public class SwitchButtonBean extends AbsSwitchButtonBean {

		@Override
		protected void init() {
			mGalleryIcon = Integer.toString(R.drawable.appfunc_switch_button_gallery);
			mGalleryLightIcon = Integer.toString(R.drawable.appfunc_switch_button_gallery_light);
			mMusicIcon = Integer.toString(R.drawable.appfunc_switch_button_music);
			mMusicLightIcon = Integer.toString(R.drawable.appfunc_switch_button_music_light);
			mVideoIcon = Integer.toString(R.drawable.appfunc_switch_button_video);
			mVideoLightIcon = Integer.toString(R.drawable.appfunc_switch_button_video_light);
			mAppIcon = Integer.toString(R.drawable.appfunc_switch_button_app);
			mAppIconLight = Integer.toString(R.drawable.appfunc_switch_button_app_light);
			mSearchIcon = Integer.toString(R.drawable.appfunc_switch_button_search);
			mSearchIconLight = Integer.toString(R.drawable.appfunc_switch_button_search_light);
		}
	}
}
