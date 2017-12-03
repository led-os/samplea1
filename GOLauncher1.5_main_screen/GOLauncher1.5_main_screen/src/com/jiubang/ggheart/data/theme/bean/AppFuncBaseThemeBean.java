package com.jiubang.ggheart.data.theme.bean;

import com.go.commomidentify.IGoLauncherClassName;

/**
 * 功能表主题Bean基类
 * @author yangguanxiang
 *
 */
public abstract class AppFuncBaseThemeBean extends ThemeBean {
	public final static String ALLAPPS_TAB_NAME = "AllApps";
	public final static String RECENTAPPS_TAB_NAME = "RecentApps";
	public final static String PROCESS_TAB_NAME = "Process";

	public AbsWallpaperBean mWallpaperBean;
	public AbsFoldericonBean mFoldericonBean;
	public AbsGLAppDrawTopBean mGLAppDrawTopBean;
//	public AbsAllTabsBean mAppfuncButtomBean;
	public AbsAllAppMenuBean mAllAppMenuBean;
	public AbsAllAppDockBean mAllAppDockBean;
	public AbsRuningDockBean mRuningDockBean;

	public AbsIndicatorBean mIndicatorBean;
	public AbsAppIconBean mAppIconBean;

	public AbsSwitchMenuBean mSwitchMenuBean;
	//	public CommonEditActionBarBean mCommonEditActionBarBean;
	public AbsSwitchButtonBean mSwitchButtonBean;
	
	public AbsBeautyBean mBeautyBean;
	
	public AbsSidebarBean mSidebarBean;

	public String mTabThemePkg;
	
	public AppFuncBaseThemeBean() {
		this(IGoLauncherClassName.DEFAULT_THEME_PACKAGE);
	}

	public AppFuncBaseThemeBean(String pkgName) {
		super(pkgName);
		mTabThemePkg = pkgName;
	}

	/**
	 * 对应XML的Wallpaper的Tag
	 * 
	 * @author wenjiaming
	 * 
	 */
	public abstract class AbsWallpaperBean {
		/**
		 * 墙纸
		 */
		public String mImagePath;
		/**
		 * 墙纸背景颜色及透明度
		 */
		public int mBackgroudColor;

		public AbsWallpaperBean() {
			init();
		}

		protected abstract void init();
	}

	/**
	 * 对应XML的Foldericon的Tag
	 * 
	 * @author wenjiaming
	 * 
	 */
	public abstract class AbsFoldericonBean {

		public String mPackageName;
		/**
		 * 文件夹缩略图底图
		 */
		public String mFolderIconBottomPath;
		/**
		 * 文件夹缩略图顶罩(打开)
		 */
		public String mFolderIconTopOpenPath;
		/**
		 * 文件夹缩略图顶罩(关闭)
		 */
		public String mFolderIconTopClosedPath;

		public AbsFoldericonBean() {
			init();
		}

		protected abstract void init();
	}

	/**
	 * 
	 * @author yangguanxiang
	 *
	 */
	public abstract class AbsGLAppDrawTopBean {
		/**
		 * 整个Tab栏背景最底层图片(竖屏)
		 */
		public String mGLAppDrawTopBgBottomVerPath;
		/**
		 * 整个Tab栏背景最底层图片的绘制方式
		 * <p>
		 * 0：平铺 1：拉伸 2：居中
		 * </p>
		 */
		public byte mGLAppDrawTopBgDrawingWay;

		public AbsGLAppDrawTopBean() {
			init();
		}
		protected abstract void init();
	}

	/**
	 * 对应XML的AllAppDock的Tag
	 * 
	 */
	public abstract class AbsAllAppDockBean {

		/**
		 * home栏菜单按钮未选中背景图
		 */
		public String mHomeMenu;
		/**
		 * home栏菜单按钮选中背景图
		 */
		public String mHomeMenuSelected;

		public AbsAllAppDockBean() {
			init();
		}
		protected abstract void init();
	}

	/**
	 * 对应XML的AllAppMenu的Tag
	 * 
	 */
	public abstract class AbsAllAppMenuBean {

		/**
		 * 背景(竖屏)
		 */
		public String mMenuBgV;
		/**
		 * 分割线（竖屏）
		 */
		public String mMenuDividerV;
		/**
		 * 字体颜色
		 */
		public int mMenuTextColor;
		/**
		 * 菜单项被选中后的背景图
		 */
		public String mMenuItemSelected;
		/**
		 * 清理内存按钮图片（普通、紧张、严重）
		 */
		public String mMenuMemoryCleanBtnNormal;
		public String mMenuMemoryCleanBtnTensity;
		public String mMenuMemoryCleanBtnSeriousness;
		/**
		 * 清理内存进度条图片颜色值（普通、紧张、严重）
		 */
		public int mMenuMemoryCleanProgressBarNormal;
		public int mMenuMemoryCleanProgressBarTensity;
		public int mMenuMemoryCleanProgressBarSeriousness;
		/**
		 * 进入正在运行的图标
		 */
		public String mMenuForwardPromanage;

		public AbsAllAppMenuBean() {
			init();
		}
		protected abstract void init();
	}

	/**
	 * 对应XML的RuningDock的Tag
	 * 
	 */
	public abstract class AbsRuningDockBean {
		/**
		 * home栏正在运行锁定所有按钮未选中背景图
		 */
		public String mHomeLockListNormal;
		/**
		 * home栏正在运行锁定所有按钮选中背景图
		 */
		public String mHomeLockListLight;
		/**
		 * home栏正在运行信息图标
		 */
		public String mHomeRunningInfoImg;
		/**
		 * home栏正在运行锁定单个程序图标
		 */
		public String mHomeRunningLockImg;
		/**
		 * home栏正在运行编辑条背景图片(竖屏)
		 */
		public String mHomeEditDockBgV;
		/**
		 * home栏正在运行编辑条被触摸时背景图片(竖屏)
		 */
		public String mHomeEditDockTouchBgV;

		/**
		 * home栏正在运行解锁单个程序图标
		 */
		public String mHomeRunningUnLockImg;

		public AbsRuningDockBean() {
			init();
		}
		protected abstract void init();
	}

	/**
	 * 滚动条指示器
	 * 
	 * @author wenjiaming
	 * 
	 */
	public abstract class AbsIndicatorBean {
		/**
		 * 横向指示器当前屏图片路径
		 */
		public String indicatorCurrentHor;
		/**
		 * 横向指示器图片路径
		 */
		public String indicatorHor;

		public AbsIndicatorBean() {
			init();
		}

		protected abstract void init();
	}

	/**
	 * 应用程序
	 * 
	 * @author wenjiaming
	 * 
	 */
	public abstract class AbsAppIconBean {
		/**
		 * 应用程序文本名称的颜色
		 */
		public int mTextColor;
		/**
		 * 应用程序聚焦时的背景颜色
		 */
		public int mIconBgColor;
		/**
		 * 应用程序卸载图标
		 */
		public String mDeletApp;
		/**
		 * 应用程序卸载高亮图标
		 */
		public String mDeletHighlightApp;

		/**
		 * 新安装程序
		 */
		public String mNewApp;
		/**
		 * 可更新
		 */
		public String mUpdateIcon;
		/**
		 * 锁定
		 */
		public String mLockApp;

		/**
		 * 停止
		 */
		public String mKillApp;
		/**
		 * 停止高亮
		 */
		public String mKillAppLight;

		public AbsAppIconBean() {
			init();
		}

		protected abstract void init();
	}

	/**
	 *  @author zhangxi
	 *  美化中心
	 */
	public abstract class AbsBeautyBean {
		
		/**
		 * 功能表美化中心图标
		 */
		public String mBeautyIcon;

		public AbsBeautyBean() {
			init();
		}
		
		protected abstract void init();
		
	}
	
	
	/**
	 *  @author zhangxi
	 *  侧边栏按钮
	 */
	public abstract class AbsSidebarBean {
		
		/**
		 * 功能表美化中心图标
		 */
		public int mTitleColor;
		public String mSidebarIcon;
		public String mLogoIcon;

		public AbsSidebarBean() {
			init();
		}
		
		protected abstract void init();
		
	}

	/**
	 * 
	 * 主题Bean
	 */
	public abstract class AbsSwitchMenuBean {
		public String mPackageName;
		/**
		 * 背景(竖屏)
		 */
		public String mMenuBgV;
		/**
		 * 背景(横屏)
		 */
		public String mMenuBgH;
		/**
		 * 分割线（竖屏）
		 */
		public String mMenuDividerV;
		/**
		 * 分割线（横屏）
		 */
		public String mMenuDividerH;
		/**
		 * 字体颜色
		 */
		public int mMenuTextColor;
		/**
		 * 菜单项被选中后的背景图
		 */
		public String mMenuItemSelected;
		public String[] mItemLabelAppSelectors;
		public String[] mItemLabelImageSelectors;
		public String[] mItemLabelAudioSelectors;
		public String[] mItemLabelVedioSelectors;
		public String[] mItemLabelSearchSelectors;

		public AbsSwitchMenuBean() {
			init();
		}

		protected abstract void init();
	}

	/**
	 * 
	 * 底部编辑操作栏Bean
	 */
	//	public class CommonEditActionBarBean {
	//		public String mSelectAll;
	//		public String mSelectAllPressd;
	//
	//		public String mDeselectAll;
	//		public String mDeselectAllPressd;
	//
	//		public String mDelete;
	//		public String mDeletePressd;
	//
	//		public String mShare;
	//		public String mSharePressd;
	//
	//		public String mDone;
	//		public String mDonePressd;
	//
	//		public String mRemove;
	//		public String mRemovePressd;
	//
	//		public String mAdd;
	//		public String mAddPressd;
	//
	//		public String mHeart;
	//		public String mHeartPressd;
	//
	//		public String mAddto;
	//		public String mAddtoPressd;
	//
	//		public String mRename;
	//		public String mRenamePressd;
	//
	//		public CommonEditActionBarBean() {
	//			mSelectAll = Integer.toString(R.drawable.appfunc_mediamanagement_button_selectall);
	//			mSelectAllPressd = Integer
	//					.toString(R.drawable.appfunc_mediamanagement_button_selectall_light);
	//
	//			mDeselectAll = Integer.toString(R.drawable.appfunc_mediamanagement_button_deselect);
	//			mDeselectAllPressd = Integer
	//					.toString(R.drawable.appfunc_mediamanagement_button_deselect_light);
	//
	//			mDelete = Integer.toString(R.drawable.appfunc_mediamanagement_button_delete);
	//			mDeletePressd = Integer
	//					.toString(R.drawable.appfunc_mediamanagement_button_delete_light);
	//
	//			mShare = Integer.toString(R.drawable.appfunc_mediamanagement_button_share);
	//			mSharePressd = Integer.toString(R.drawable.appfunc_mediamanagement_button_share_light);
	//
	//			mDone = Integer.toString(R.drawable.appfunc_mediamanagement_button_done);
	//			mDonePressd = Integer.toString(R.drawable.appfunc_mediamanagement_button_done_light);
	//
	//			mRemove = Integer.toString(R.drawable.appfunc_mediamanagement_button_remove);
	//			mRemovePressd = Integer
	//					.toString(R.drawable.appfunc_mediamanagement_button_remove_light);
	//			mAdd = Integer.toString(R.drawable.appfunc_mediamanagement_button_add);
	//			mAddPressd = Integer.toString(R.drawable.appfunc_mediamanagement_button_add_light);
	//			mAddto = Integer.toString(R.drawable.appfunc_mediamanagement_button_addto);
	//			mAddtoPressd = Integer.toString(R.drawable.appfunc_mediamanagement_button_addto_light);
	//			mRename = Integer.toString(R.drawable.appfunc_mediamanagement_button_rename);
	//			mRenamePressd = Integer
	//					.toString(R.drawable.appfunc_mediamanagement_button_rename_light);
	//			mHeart = Integer.toString(R.drawable.appfunc_mediamanagement_button_heart);
	//			mHeartPressd = Integer.toString(R.drawable.appfunc_mediamanagement_button_heart_light);
	//
	//		}
	//	}

	/**
	 * 
	 * 选项卡按钮Bean
	 */
	public abstract class AbsSwitchButtonBean {
		/**
		 * 图片浏览器图标
		 */
		public String mGalleryIcon;
		/**
		 * 图片浏览器图标高亮
		 */
		public String mGalleryLightIcon;
		/**
		 * 音乐浏览器图标
		 */
		public String mMusicIcon;
		/**
		 * 音乐浏览器图标高亮
		 */
		public String mMusicLightIcon;
		/**
		 * 视频浏览器图标
		 */
		public String mVideoIcon;
		/**
		 * 视频浏览器图标高亮
		 */
		public String mVideoLightIcon;
		/**
		 * 应用浏览器图标
		 */
		public String mAppIcon;
		/**
		 * 应用浏览器图标高亮
		 */
		public String mAppIconLight;
		/**
		 * 功能表搜索图标
		 */
		public String mSearchIcon;
		/**
		 * 功能表搜索图标高亮
		 */
		public String mSearchIconLight;

		public AbsSwitchButtonBean() {
			init();
		}

		protected abstract void init();
	}
}
