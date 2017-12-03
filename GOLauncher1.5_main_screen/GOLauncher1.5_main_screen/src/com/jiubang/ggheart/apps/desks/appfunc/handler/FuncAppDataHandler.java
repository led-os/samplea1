package com.jiubang.ggheart.apps.desks.appfunc.handler;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.go.launcher.taskmanager.TaskMgrControler;
import com.go.launcher.taskmanager._APPINFOR;
import com.go.proxy.SettingProxy;
import com.go.util.BroadCaster.BroadCasterObserver;
import com.golauncher.message.IAppCoreMsgId;
import com.jiubang.ggheart.apps.appfunc.controler.AppDrawerControler;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.apps.desks.appfunc.help.AppFuncConstants;
import com.jiubang.ggheart.apps.desks.appfunc.help.AppFuncConstants.MessageID;
import com.jiubang.ggheart.apps.desks.appfunc.model.IBackgroundInfoChangedObserver;
import com.jiubang.ggheart.apps.desks.diy.OutOfMemoryHandler;
import com.jiubang.ggheart.data.AppCore;
import com.jiubang.ggheart.data.ISettingObserver;
import com.jiubang.ggheart.data.info.FunTaskItemInfo;

/**
 * 前端后台数据交互控制器
 * 
 * @author tanshu
 * 
 */
public class FuncAppDataHandler implements BroadCasterObserver {
	private static FuncAppDataHandler sInstance;
	
	public static final String PATTERN = "\\d*";
	// /**
	// * Frame控制器
	// */
	// private AppFuncFrameController mController;
	private Context mContext;
	/**
	 * 应用程序安装卸载监听者
	 */
	private ArrayList<IBackgroundInfoChangedObserver> mBgInfoObserverList;
	// /**
	// * 行数
	// */
	// private int mNumRows;
	// /**
	// * 列数
	// */
	// private int mNumColumns;
	/**
	 * 功能表设置项数组
	 */
	private boolean[] mSettingItem;
	/**
	 * 风格：暂时没有使用
	 */
	private int mStyle;
	/**
	 * 滑动方向
	 */
	private int mSlideDirection;
	/**
	 * 摆放规则
	 */
	private int mStandard;
	/**
	 * 是否显示应用程序名称
	 */
	private int mShowName;
	/**
	 * 背景图路径
	 */
	private String mBgPath = null;
	/**
	 * 
	 * 背景显示方式
	 */
	private int mShowBg;
	/**
	 * 是否更新最近打开列表
	 */
	private boolean mUpdateRecentApps;
	/**
	 * 设置的排序方式
	 */
	private int mSortType;
	/**
	 * 进出特效
	 */
	private int mInoutEffect;
	/**
	 * 图标特效
	 */
	private int mIconEffect;
	/**
	 * 竖向滑动特效
	 */
	private int mVerticalScrollEffect;
	/**
	 * 后台设置监听者
	 */
	private ISettingObserver mSettingObserver;
	/**
	 * 最近打开监听者
	 */
	private BroadCasterObserver mRecentObsever;
	/**
	 * 进程管理监听者
	 */
	private BroadCasterObserver mTaskObsever;
	/**
	 * 是否要更新背景
	 */
	// private boolean mBgUpdateTag = false;
	/**
	 * 横向循环滚屏
	 */
	private int mScrollLoop;
	/**
	 * 是否模糊背景
	 */
	private int mBlurBackground;
	/**
	 * 是否显示Tab栏
	 */
	private int mShowTabRow;

	/**
	 * 是否显示搜素界面
	 */
	private int mShowSearch;

	/**
	 * 是否已经初始化任务管理监听器
	 */
	public boolean mHasInitTaskMgrHandler = false;
	/**
	 * 是否已经初始化最近打开监听器
	 */
	public boolean mHasinitRecentHandler = false;
	/**
	 * 是否在应用图标上显示更新提示
	 */
	private int mAppUpdate;

	/**
	 * 是否点击了应用图标上的显示更新提示
	 */
	private boolean mClickAppupdate;

	/**
	 * 是否只显示Home键
	 */
	private int mShowHomeKeyOnly;

	/**
	 * 显示操作栏
	 */
	private int mShowActionBar;

	/**
	 * 上滑手势
	 */
	private int mGlideUpAction;

	/**
	 * 下滑手势
	 */
	private int mGlideDownAction;

	public synchronized static FuncAppDataHandler getInstance(Context context) {
		if (sInstance == null && context != null) {
			sInstance = new FuncAppDataHandler(context);
		}
		return sInstance;
	}

	private FuncAppDataHandler(Context context) {
		mContext = context;
		mSettingItem = new boolean[FunAppSetting.INDEX_MAX];
		mBgInfoObserverList = new ArrayList<IBackgroundInfoChangedObserver>();
		getAppFuncConfigInfo();
		// 注册后台监听者
		
		AppDrawerControler.getInstance(mContext).registerObserver(this);

		// 监听功能表设置
		initFunSettingHandler();

		// 监听最近打开
		// initRecentHandler();

		// 监听进程管理
		// initTaskMgrHandler();
	}

	public void onDestroy() {
		// 退出时的清理工作
		// 1.反注册后台监听
		// 2.反注册监听设置
		SettingProxy.getFunAppSetting().removeFunAppSettingObserver(mSettingObserver);
		// 4.反注册进程管理
		AppCore.getInstance().getTaskMgrControler().unRegisterObserver(mTaskObsever);
	}

	/**
	 * 监听主题
	 */
	public void handleMessage(int msgId, int param, Object ...obj) {
		switch (msgId) {
			case IAppCoreMsgId.EVENT_THEME_CHANGED : {
				// 根据主题重设设置
				getAppFuncConfigInfo();
				// 需要设置模糊效果的标志位使功能表重新取一次
				mSettingItem[FunAppSetting.INDEX_BLUR_BACKGROUND - 1] = true;
			}
				break;
			default :
				break;
		}
	}

	/**
	 * 监听功能表设置
	 */
	private void initFunSettingHandler() {
		mSettingObserver = new ISettingObserver() {

			@Override
			public void onSettingChange(int index, int value, Object object) {
				// TODO Auto-generated method stub
				switch (index) {
					case FunAppSetting.RESETALL : {
						// 恢复了默认设置
						// TODO: To be added in v2.0
						if (mSettingItem != null) {
							for (int i = 0; i < mSettingItem.length; i++) {
								mSettingItem[i] = true;
							}
						}
						getAppFuncConfigInfo();
						// 通知前台需要重新取背景图片
						sendInfoToForeground(AppFuncConstants.MessageID.BG_SHOWED, null, null);
						break;
					}
					case FunAppSetting.INDEX_MENUAPPSTYLE : {
						mSettingItem[FunAppSetting.INDEX_MENUAPPSTYLE - 1] = true;
						// TODO: To be added in v2.0
						break;
					}
					case FunAppSetting.INDEX_TURNSCREENDIRECTION : {
						mSettingItem[FunAppSetting.INDEX_TURNSCREENDIRECTION - 1] = true;
						mSlideDirection = value;
						sendInfoToForeground(AppFuncConstants.MessageID.SLIDEDIRECTION_CHANGED, null,
								null);
						break;
					}
					case FunAppSetting.INDEX_APPNAMEVISIABLE : {
						mSettingItem[FunAppSetting.INDEX_APPNAMEVISIABLE - 1] = true;
						mShowName = value;
						sendInfoToForeground(AppFuncConstants.MessageID.SHOWNAME_CHANGED, null,
								null);
						break;
					}
					case FunAppSetting.INDEX_SHOW_SEARCH : {
						mSettingItem[FunAppSetting.INDEX_SHOW_SEARCH - 1] = true;
						mShowSearch = value;
						break;
					}
					case FunAppSetting.INDEX_APP_UPDATE : {
						mSettingItem[FunAppSetting.INDEX_APP_UPDATE - 1] = true;
						mAppUpdate = value;
						sendInfoToForeground(AppFuncConstants.MessageID.SHOW_APP_UPDATE_CHANGE, null,
								null);
						break;
					}
					case FunAppSetting.INDEX_LINECOLUMNNUM : {
						mSettingItem[FunAppSetting.INDEX_LINECOLUMNNUM - 1] = true;
						mStandard = value;
						sendInfoToForeground(AppFuncConstants.MessageID.STANDARD_CHANGED,
								 null, null);
						break;
					}
					case FunAppSetting.INDEX_BACKGROUNDPICPATH : {
						mSettingItem[FunAppSetting.INDEX_BACKGROUNDPICPATH - 1] = true;
						setBgImageByPath((String) object);
						sendInfoToForeground(AppFuncConstants.MessageID.BG_CHANGED, null, null);
						break;
					}
					case FunAppSetting.INDEX_BGSWITCH : {
						mSettingItem[FunAppSetting.INDEX_BGSWITCH - 1] = true;
						mShowBg = value;
						sendInfoToForeground(AppFuncConstants.MessageID.BG_SHOWED, null, null);
						break;
					}
					case FunAppSetting.INDEX_SORTTYPE : {
						mSettingItem[FunAppSetting.INDEX_SORTTYPE - 1] = true;
						mSortType = value;
						break;
					}
					case FunAppSetting.INDEX_INOUTEFFECT : {
						mSettingItem[FunAppSetting.INDEX_INOUTEFFECT - 1] = true;
						mInoutEffect = value;
						break;
					}
					case FunAppSetting.INDEX_ICONEFFECT : {
						mSettingItem[FunAppSetting.INDEX_ICONEFFECT - 1] = true;
						mIconEffect = value;
						sendInfoToForeground(AppFuncConstants.MessageID.ICONEFFECT_CHANGED,
								new Integer(mIconEffect), null);
						break;
					}
					case FunAppSetting.INDEX_SCROLL_LOOP : {
						mSettingItem[FunAppSetting.INDEX_SCROLL_LOOP - 1] = true;
						mScrollLoop = value;
						sendInfoToForeground(AppFuncConstants.MessageID.SCROLL_LOOP_CHANGED,
								new Integer(mScrollLoop), null);
						break;
					}
					case FunAppSetting.INDEX_BLUR_BACKGROUND : {
						mSettingItem[FunAppSetting.INDEX_BLUR_BACKGROUND - 1] = true;
						mBlurBackground = value;
						sendInfoToForeground(AppFuncConstants.MessageID.BLUR_BACKGROUND_CHANGED,
								new Integer(mBlurBackground), null);
						break;
					}
					case FunAppSetting.INDEX_SHOW_TAB_ROW : {
						mSettingItem[FunAppSetting.INDEX_SHOW_TAB_ROW - 1] = true;
						mShowTabRow = value;
						sendInfoToForeground(AppFuncConstants.MessageID.SHOW_TAB_ROW_CHANGED,
								new Integer(mShowTabRow), null);
						break;
					}
					case FunAppSetting.INDEX_VERTICAL_SCROLL_EFFECT : {
						mSettingItem[index - 1] = true;
						mVerticalScrollEffect = value;
						sendInfoToForeground(
								AppFuncConstants.MessageID.VERTICAL_SCROLL_EFFECT_CHANGED,
								new Integer(mVerticalScrollEffect), null);
						break;
					}
					case FunAppSetting.INDEX_SHOW_HOME_KEY_ONLY : {
						mSettingItem[FunAppSetting.INDEX_SHOW_HOME_KEY_ONLY - 1] = true;
						mShowHomeKeyOnly = value;
						sendInfoToForeground(
								AppFuncConstants.MessageID.SHOW_HOME_KEY_ONLY_CHANGE,
								new Integer(mShowActionBar), null);
						break;
					}
					case FunAppSetting.INDEX_SHOW_ACTION_BAR : {
						mSettingItem[FunAppSetting.INDEX_SHOW_ACTION_BAR - 1] = true;
						mShowActionBar = value;
						sendInfoToForeground(
								AppFuncConstants.MessageID.SHOW_ACTION_BAR_CHANGE,
								new Integer(mShowActionBar), null);
						break;
					}
					case FunAppSetting.INDEX_GLIDE_UP_ACTION : {
						mSettingItem[FunAppSetting.INDEX_GLIDE_UP_ACTION - 1] = true;
						mGlideUpAction = value;
						break;
					}
					case FunAppSetting.INDEX_GLIDE_DOWN_ACTION : {
						mSettingItem[FunAppSetting.INDEX_GLIDE_DOWN_ACTION - 1] = true;
						mGlideDownAction = value;
						break;
					}
					case FunAppSetting.INDEX_TAB_BOTTOM_BG : {
						sendInfoToForeground(
								AppFuncConstants.MessageID.TAB_BOTTOM_BG_CHANGE,
								null, null);
					}
					default :
						break;
				}
			}
		};
		SettingProxy.getFunAppSetting().addFunAppSettingObserver(mSettingObserver);
	}

	/**
	 * 监听进程管理
	 */
	// private void initTaskMgrHandler() {
	public void initTaskMgrHandler() {
		mTaskObsever = new BroadCasterObserver() {
			@Override
			public void onBCChange(int msgId, int param, Object ...object) {
				switch (msgId) {
				// 结束单个进程
					case TaskMgrControler.TERMINATE_SINGLE : {
						sendInfoToForeground(MessageID.SINGLE_TASKMANAGE, param, null);
					}
						break;
					// 结束所有进程
					case TaskMgrControler.TERMINATE_ALL : {
						sendInfoToForeground(MessageID.ALL_TASKMANAGE, null, null);
					}
						break;
					case TaskMgrControler.ADDWHITEITEM :
					case TaskMgrControler.ADDWHITEITEMS :
					case TaskMgrControler.DELETEWHITEITEM : {
						sendInfoToForeground(MessageID.LOCK_LIST_CHANGED, null, null);
						Log.i("pl", "list changed.");
					}
						break;
					default :
						break;
				}
			}
		};
		AppCore.getInstance().getTaskMgrControler().registerObserver(mTaskObsever);
	}

	/**
	 * 重置设置项标志位
	 */
	public void resetSettingItem() {
		if (mSettingItem != null) {
			for (int i = 0; i < mSettingItem.length; i++) {
				mSettingItem[i] = false;
			}
		}
	}

	/**
	 * 根据下标查询对应的设置是否被改变
	 * 
	 * @param index
	 * @return
	 */
	public boolean querySettingItem(int index) {
		if (mSettingItem != null) {
			if ((index >= 0) && (index < mSettingItem.length)) {
				return mSettingItem[index];
			}
		}
		return false;
	}

	/**
	 * 结束进程；无法直接调用FuncAppDataEngine里的方法，故转接一次
	 * 
	 * @param pid
	 *            进程唯一的标识
	 */
	public void terminateApp(final int pid) {
		AppCore.getInstance().getTaskMgrControler().terminateApp(pid);
	}

	/**
	 * 结束所有进程；无法直接调用FuncAppDataEngine里的方法，故转接一次
	 */
	public void terminateAll() {
		AppCore.getInstance().getTaskMgrControler().terminateAll();
	}

	public void terminateAll(ArrayList<FunTaskItemInfo> infos) {
		AppCore.getInstance().getTaskMgrControler().terminateAll(infos);
	}

	public void releaseTaskMgrControler() {
		AppCore.getInstance().releaseTaskMgrControler();
	}

	/**
	 * 获取当前运行的进程列表
	 * 
	 * @return
	 */
	public List<_APPINFOR> getCurProgress() {
		return AppCore.getInstance().getTaskMgrControler().retriveAppList();
	}

	/**
	 * 获取当前运行的进程列表
	 * 
	 * @return
	 */
	public ArrayList<FunTaskItemInfo> getProgresses() {
		return AppCore.getInstance().getTaskMgrControler().getProgresses();
	}

	/**
	 * 获取总内存
	 * 
	 * @return
	 */
	public long retriveTotalMemory() {
		return AppCore.getInstance().getTaskMgrControler().retriveTotalMemory();
	}

	/**
	 * 获取可用内存
	 * 
	 * @return
	 */
	public long retriveAvailableMemory() {
		return AppCore.getInstance().getTaskMgrControler().retriveAvailableMemory();
	}

	/**
	 * 读取功能表配置信息
	 */
	private void getAppFuncConfigInfo2() {
		// FunAppSetting funAppSetting =
		// mGlobalContext.getGlobalSettings().getFunAppSetting();
		FunAppSetting funAppSetting = SettingProxy.getFunAppSetting();
		mStandard = funAppSetting.getLineColumnNum();
		mSlideDirection = funAppSetting.getTurnScreenDirection();
		mShowName = funAppSetting.getAppNameVisiable();
		
		mShowBg = funAppSetting.getBgSetting();
		
		mInoutEffect = funAppSetting.getInOutEffect();
		mIconEffect = funAppSetting.getIconEffect();
		mSortType = funAppSetting.getSortType();
		mScrollLoop = funAppSetting.getScrollLoop();
		
		mBlurBackground = funAppSetting.getBlurBackground();
		
		mShowTabRow = funAppSetting.getShowTabRow();
		mShowSearch = funAppSetting.getShowSearch();
		mAppUpdate = funAppSetting.getAppUpdate();
		mVerticalScrollEffect = funAppSetting.getVerticalScrollEffect();
		mShowHomeKeyOnly = funAppSetting.getShowHomeKeyOnly();
		
		setBgImageByPath(funAppSetting.getBackgroundPicPath());
		
		mShowActionBar = funAppSetting.getShowActionBar();
		mGlideUpAction = funAppSetting.getGlideUpAction();
		mGlideDownAction = funAppSetting.getGlideDownAction();
	}

	private void getAppFuncConfigInfo() {
		FunAppSetting funAppSetting = SettingProxy.getFunAppSetting();
		FunAppSetting.FunSettingBean defaultData = funAppSetting.getDefaultData();
		mStandard = defaultData.getLineColumnNum();
		mSlideDirection = defaultData.getTurnScreenDirection();
		mShowName = defaultData.getAppNameVisiable();
		
		mShowBg = funAppSetting.getBgSetting();
		
		mInoutEffect = defaultData.getInOutEffect();
		mIconEffect = defaultData.getIconEffect();
		mSortType = defaultData.getSortType();
		mScrollLoop = defaultData.getScrollLoop();
		
		mBlurBackground = funAppSetting.getBlurBackground();
		
		mShowTabRow = defaultData.getShowTabRow();
		mShowSearch = defaultData.getShowSearch();
		mAppUpdate = defaultData.getAppUpdate();
		mVerticalScrollEffect = defaultData.getVerticalScrollEffect();
		mShowHomeKeyOnly = defaultData.getShowHomeKeyOnly();
		
		setBgImageByPath(funAppSetting.getBackgroundPicPath());
		
		mShowActionBar = defaultData.getShowActionBar();
		mGlideUpAction = defaultData.getGlideUpAction();
		mGlideDownAction = defaultData.getGlideDownAction();
		
	}
	
	/**
	 * 获得功能表行列数布局信息
	 */
	public int getStandard() {
		return mStandard;
	}

	/**
	 * 滑动方向
	 * 
	 * @return
	 */
	public int getSlideDirection() {
		return mSlideDirection;
	}

	/**
	 * 是否显示名称
	 * 
	 * @return
	 */
	public int getShowName() {
		return mShowName;
	}

	public int getShowSearch() {
		return mShowSearch;
	}

	public boolean isShowAppUpdate() {
		return mAppUpdate == FunAppSetting.ON;
	}

	public boolean isShowHomeKeyOnly() {
		return mShowHomeKeyOnly == FunAppSetting.ON;
	}

	public boolean ismClickAppupdate() {
		return mClickAppupdate;
	}

	public void setmClickAppupdate(boolean mClickAppupdate) {
		this.mClickAppupdate = mClickAppupdate;
	}

	public boolean isShowActionBar() {
		return mShowActionBar == FunAppSetting.ON;
	}

	public boolean isGlideUpActionEnable() {
		return mGlideUpAction == FunAppSetting.ON;
	}

	public boolean isGlideDownActionEnable() {
		return mGlideDownAction == FunAppSetting.ON;
	}

	/**
	 * 排序类型
	 * 
	 * @return
	 */
	public int getSortType() {
		return mSortType;
	}

	/**
	 * 获得符合屏幕大小的背景图片
	 */
	public BitmapDrawable getBg() {
		BitmapDrawable bg = null;
		try {
			URI uri = new URI(mBgPath);
			String sc = uri.getScheme();
			if (null != sc && sc.equals(DeskSettingConstants.BGSETTINGTAG)) { // 主题包中的背景图片
				String packageName = uri.getRawSchemeSpecificPart();
				String idStr = uri.getFragment();
				boolean matches = Pattern.matches(PATTERN, idStr);
				if (matches) { // 全数字
					int resId = Integer.valueOf(idStr);
					Resources resources = mContext.getPackageManager().getResourcesForApplication(
							packageName);
					if (null != resources) {
						bg = (BitmapDrawable) resources.getDrawable(resId);
					}
				} else {
					Resources resources = mContext.getPackageManager().getResourcesForApplication(
							packageName);
					if (null != resources) {
						int identifier = resources.getIdentifier(idStr, "drawable", packageName);
						if (identifier != 0) {
							bg = (BitmapDrawable) resources.getDrawable(identifier);
						}
					}
				}
			} else { // 文件夹中的背景图片
				bg = (BitmapDrawable) Drawable.createFromPath(mBgPath);
			}
		} catch (Throwable e) {
			if (e instanceof OutOfMemoryError) {
				OutOfMemoryHandler.handle();
			}
			// 读取图片失败
			e.printStackTrace();
		}
		return bg;
	}

	/**
	 * 背景显示方式： 2 无 3 当前主题默认 4 GO主题 5 自定义
	 * 
	 * @return
	 */
	public int getShowBg() {
		return SettingProxy.getFunAppSetting().getBgSetting();
	}

	public int getInoutEffect() {
		return mInoutEffect;
	}

	public int getIconEffect() {
		return mIconEffect;
	}

	public int getScrollLoop() {
		return mScrollLoop;
	}

	public int getBlurBackground() {
		return mBlurBackground;
	}

	public boolean isShowTabRow() {
		return mShowTabRow == FunAppSetting.ON;
	}

	public int getVerticalScrollEffect() {
		return mVerticalScrollEffect;
	}

	/**
	 * 注册监听者
	 * 
	 * @param observer
	 */
	public synchronized void registerBgInfoChangeObserver(IBackgroundInfoChangedObserver observer) {
		for (IBackgroundInfoChangedObserver origObserver : mBgInfoObserverList) {
			if (origObserver == observer) {
				return;
			}
		}
		mBgInfoObserverList.add(observer);
	}

	/**
	 * 去注册监听者
	 * 
	 * @param observer
	 */
	public synchronized void deRegisterBgInfoChangeObserver(IBackgroundInfoChangedObserver observer) {
		for (IBackgroundInfoChangedObserver origObserver : mBgInfoObserverList) {
			if (origObserver == observer) {
				mBgInfoObserverList.remove(observer);
				return;
			}
		}
	}

	/**
	 * 发消息通知监听者
	 * 
	 * @param msgId
	 * @param obj1
	 * @param obj2
	 */
	public synchronized void sendInfoToForeground(AppFuncConstants.MessageID msgId, Object obj1,
			Object obj2) {
		for (IBackgroundInfoChangedObserver observer : mBgInfoObserverList) {
			if (observer.handleChanges(msgId, obj1, obj2)) {
				return;
			}
		}
	}

	// /**
	// * 第一次进入功能表获得背景图片
	// * @return
	// */
	// public MImage getBgImage(){
	// String picPath = mGlobalContext.getGlobalSettings().getFunAppSetting().
	// getBackgroundPicPath();
	// if (picPath == null){
	// return null;
	// }
	// return getBgImageByPath(picPath);
	// }

	/**
	 * 根据路径获取图片
	 * 
	 * @param path
	 * @return true 路径改变 false 路径不变
	 */
	private void setBgImageByPath(String path) {
		mBgPath = path;
	}

	// public void setUpdateBGtag() {
	// mBgUpdateTag = true;
	// }

	/**
	 * 检测功能表设置项是否有变化
	 */
	public void checkSettingItemChanged() {
		for (int i = 0; i < mSettingItem.length; i++) {
			switch (i + 1) {
				case FunAppSetting.INDEX_BGSWITCH : {
					// if (mSettingItem[i]) {
					// sendInfoToForeground(AppFuncConstants.MessageID.BG_SHOWED,
					// null, null);
					// mBgUpdateTag = false;
					// }
					break;
				}
				case FunAppSetting.INDEX_MENUAPPSTYLE : {
					// TODO:To be added in v2.0
					break;
				}
				case FunAppSetting.INDEX_TURNSCREENDIRECTION : {
					// if (mSettingItem[i]) {
					// sendInfoToForeground(AppFuncConstants.MessageID.SLIDEDIRECTION_CHANGED,
					// null, null);
					// }
					break;
				}
				case FunAppSetting.INDEX_APPNAMEVISIABLE : {
					//	if (mSettingItem[i]) {
					//		sendInfoToForeground(AppFuncConstants.MessageID.SHOWNAME_CHANGED, null,
					//		null);
					//	}
					break;
				}
				case FunAppSetting.INDEX_SHOW_SEARCH : {
					if (mSettingItem[i]) {
						sendInfoToForeground(AppFuncConstants.MessageID.SHOW_SEARCH, null, null);
					}
					break;
				}
				case FunAppSetting.INDEX_LINECOLUMNNUM : {
					// if (mSettingItem[i]) {
					// sendInfoToForeground(AppFuncConstants.MessageID.STANDARD_CHANGED,
					// null, null);
					// }
					break;
				}
				// 每次进入都检测背景图进行缩放
				case FunAppSetting.INDEX_BACKGROUNDPICPATH : {
					// if (mSettingItem[i] || mBgUpdateTag) {
					// sendInfoToForeground(AppFuncConstants.MessageID.BG_CHANGED,
					// null, null);
					// }
					break;
				}
				// 图标的排序设置改变
				case FunAppSetting.INDEX_SORTTYPE : {
					if (mSettingItem[i]) {
						sendInfoToForeground(AppFuncConstants.MessageID.ALL_SORTSETTING,
								new Integer(mSortType), null);
					}
					break;
				}
				case FunAppSetting.INDEX_INOUTEFFECT : {
					if (mSettingItem[i]) {
						sendInfoToForeground(AppFuncConstants.MessageID.INOUTEFFECT_CHANGED,
								new Integer(mInoutEffect), null);
					}
					break;
				}
				case FunAppSetting.INDEX_ICONEFFECT : {
					if (mSettingItem[i]) {
						sendInfoToForeground(AppFuncConstants.MessageID.ICONEFFECT_CHANGED,
								new Integer(mIconEffect), null);
					}
					break;
				}
				case FunAppSetting.INDEX_SCROLL_LOOP : {
					if (mSettingItem[i]) {
						sendInfoToForeground(AppFuncConstants.MessageID.SCROLL_LOOP_CHANGED,
								new Integer(mScrollLoop), null);
					}
					break;
				}
				case FunAppSetting.INDEX_BLUR_BACKGROUND : {
					if (mSettingItem[i]) {
						sendInfoToForeground(AppFuncConstants.MessageID.BLUR_BACKGROUND_CHANGED,
								new Integer(mBlurBackground), null);
					}
					break;
				}
				case FunAppSetting.INDEX_SHOW_TAB_ROW : {
					if (mSettingItem[i]) {
						sendInfoToForeground(AppFuncConstants.MessageID.SHOW_TAB_ROW_CHANGED,
								new Integer(mShowTabRow), null);
					}
					break;
				}
				case FunAppSetting.INDEX_VERTICAL_SCROLL_EFFECT : {
					if (mSettingItem[i]) {
						sendInfoToForeground(
								AppFuncConstants.MessageID.VERTICAL_SCROLL_EFFECT_CHANGED,
								new Integer(mVerticalScrollEffect), null);
					}
					break;
				}
				default :
					break;
			}
		}
		resetSettingItem();
	}

	@Override
	public void onBCChange(int msgId, int param, Object ...objects) {

		switch (msgId) {
			case AppDrawerControler.UNINSTALL_APP : {
				sendInfoToForeground(AppFuncConstants.MessageID.APP_REMOVED, objects[0], null);
			}
				break;
			case AppDrawerControler.INSTALL_APP : {
				sendInfoToForeground(AppFuncConstants.MessageID.APP_ADDED, objects[0], null);
			}
				break;
			case AppDrawerControler.SORTFINISH : {
				sendInfoToForeground(AppFuncConstants.MessageID.ALL_PROGRAMSORT, null, null);
			}
				break;
			case AppDrawerControler.FINISHLOADINGSDCARD : {
				sendInfoToForeground(AppFuncConstants.MessageID.SDLOADINGFINISH, null, null);
				break;
			}
			case AppDrawerControler.STARTSAVE : {
				break;
			}
			case AppDrawerControler.FINISHSAVE : {
				break;
			}
			case AppDrawerControler.SDCARDOK :
			case AppDrawerControler.TIMEISUP : {
				FunAppSetting funAppSetting = SettingProxy.getFunAppSetting();
				setBgImageByPath(funAppSetting.getBackgroundPicPath());
				sendInfoToForeground(AppFuncConstants.MessageID.BG_SHOWED, null, null);
			}
				break;
			case AppDrawerControler.BATADD : {
//				AppFuncHandler.getInstance().refreshAllAppGrid();
				sendInfoToForeground(AppFuncConstants.MessageID.ADD_BATCH_APP, null, null);
			}
				break;
			case AppDrawerControler.BATUPDATE : {
//				AppFuncHandler.getInstance().refreshAllAppGrid();
				sendInfoToForeground(AppFuncConstants.MessageID.UPDATE_BATCH_APP, null, null);
			}
				break;
			case AppDrawerControler.HIDE_APPS : {
				sendInfoToForeground(AppFuncConstants.MessageID.HIDE_APPS, objects[0], objects[1]);
			}
				break;
			case AppDrawerControler.ADDITEM : {
				sendInfoToForeground(AppFuncConstants.MessageID.ADD_ITEM, param, objects[0]);
			}
				break;
			case AppDrawerControler.ADDITEMS : {
				sendInfoToForeground(AppFuncConstants.MessageID.ADD_ITEMS, param, objects[0]);
			}
				break;
			case AppDrawerControler.REMOVEITEM : {
				sendInfoToForeground(AppFuncConstants.MessageID.REMOVE_ITEM, param, objects[0]);
			}
				break;
			case AppDrawerControler.REMOVEITEMS : {
				sendInfoToForeground(AppFuncConstants.MessageID.REMOVE_ITEMS, param, objects[0]);
			}
				break;
			case AppDrawerControler.REFREASH_APPDRAWER : {
				sendInfoToForeground(AppFuncConstants.MessageID.REFREASH_APPDRAWER, param, null);
			}
				break;
			case AppDrawerControler.REFREASH_FOLDERBAR_TARGET : {
				sendInfoToForeground(AppFuncConstants.MessageID.REFREASH_FOLDERBAR_TARGET, param, objects[0]);
			}
				break;
			case AppDrawerControler.FIRST_INIT_DONE : {
				sendInfoToForeground(AppFuncConstants.MessageID.FIRST_INIT_DONE, param, objects[0]);
			}
				break;
			case AppDrawerControler.RELOAD_INIT_FINISH : {
				sendInfoToForeground(AppFuncConstants.MessageID.RELOAD_INIT_DONE, param, objects[0]);
			}
				break;
			case AppDrawerControler.ARRANGE_END : {
				sendInfoToForeground(AppFuncConstants.MessageID.ARRANGE_END, param, objects[0]);
			}
				break;
			default :
				break;
			// 以上代码是兼容2D的消息，以后删掉，改为直接送到appdrawer即可
		}
	}
}
