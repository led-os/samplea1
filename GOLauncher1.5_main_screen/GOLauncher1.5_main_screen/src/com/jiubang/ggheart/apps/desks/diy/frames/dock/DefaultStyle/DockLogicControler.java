package com.jiubang.ggheart.apps.desks.diy.frames.dock.DefaultStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.proxy.VersionControl;
import com.go.util.ConvertUtils;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IDockMsgId;
import com.jiubang.ggheart.apps.desks.diy.frames.dock.DirtyDataException;
import com.jiubang.ggheart.apps.desks.dock.DockDataModel;
import com.jiubang.ggheart.apps.desks.dock.DockUtil;
import com.jiubang.ggheart.apps.desks.imagepreview.ImagePreviewResultType;
import com.jiubang.ggheart.data.AppCore;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.DockItemControler;
import com.jiubang.ggheart.data.SysShortCutControler;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.DockItemInfo;
import com.jiubang.ggheart.data.info.IItemType;
import com.jiubang.ggheart.data.info.ItemInfo;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.data.info.ShortCutSettingInfo;
import com.jiubang.ggheart.data.info.UserFolderInfo;
import com.jiubang.ggheart.data.theme.DeskThemeControler;
import com.jiubang.ggheart.data.theme.ImageExplorer;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.launcher.AppIdentifier;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.plugin.shell.folder.GLAppFolderController;
import com.jiubang.ggheart.plugin.shell.folder.GLAppFolderInfo;

/**
 * dock逻辑控制器，负责缓存mDockInfosHashMap及数据库访问操作
 * 
 * @author ruxueqin
 * 
 */
public class DockLogicControler {
	private Context mContext;

	private DockDataModel mDataModel; // 数据库访问类

	private DeskThemeControler mDeskThemeControler; // 桌面主题控制器

	private ConcurrentHashMap<Integer, ArrayList<DockItemInfo>> mDockInfosHashMap; // 公共数据

	private ConcurrentHashMap<Integer, ArrayList<Integer>> mBlanksHashMap; // 非自适应模式，空白

	private static volatile DockLogicControler sInstance;

	private Comparator<DockItemInfo> mCompatatorIndexInDock = new Comparator<DockItemInfo>() { // 按index进行排序
		
		@Override
		public int compare(DockItemInfo lhs, DockItemInfo rhs) {
			int retval = 0;
			if (lhs.getmIndexInRow() > rhs.getmIndexInRow()) {
				retval = 1;
			} else if (lhs.getmIndexInRow() < rhs.getmIndexInRow()) {
				retval = -1;
			}
			return retval;
		}
	};
	
	private DockLogicControler() {

	}
	public static DockLogicControler getInstance() {
		if (sInstance == null) {
			sInstance = new DockLogicControler(ApplicationProxy.getContext(),
					AppDataEngine.getInstance(ApplicationProxy.getContext()), AppCore.getInstance()
							.getSysShortCutControler());
		}
		return sInstance;
	}

	private DockLogicControler(Context context, AppDataEngine engine,
			SysShortCutControler shortCutControler) {
		mContext = context;

		mDataModel = new DockDataModel(context, engine, shortCutControler);

		mDeskThemeControler = AppCore.getInstance().getDeskThemeControler();
	}

	/**
	 * 获取快捷条数据
	 * 
	 * @return 快捷条列表
	 */
	public synchronized ConcurrentHashMap<Integer, ArrayList<DockItemInfo>> getShortCutItems() {
		if (null == mDockInfosHashMap) {
			if (mDataModel.checkNeedInit()) {
				// 新安装，初始化数据
				mDataModel.cleanShortCutItem();
				mDataModel.initShortcutItem();
			}

			final int numOfRowInSetting = SettingProxy.getShortCutSettingInfo().mRows;
			if (numOfRowInSetting <= 0 || numOfRowInSetting > DockUtil.TOTAL_ROWS) {
				throw new IllegalArgumentException("when initialing dock, setting row = "
						+ numOfRowInSetting);
			}

			mDockInfosHashMap = new ConcurrentHashMap<Integer, ArrayList<DockItemInfo>>();
			for (int i = 0; i < numOfRowInSetting; i++) {
				ArrayList<DockItemInfo> list = mDataModel.getShortCutItems(i);
				mDockInfosHashMap.put(i, list);
			}

			HashSet<Integer> dirtyDataRowIds = new HashSet<Integer>();
			if (hasDirtyData(dirtyDataRowIds)) {
				// NOTE:如果数量有异常，则弹出提示框提示用户“清理桌面垃圾数据
				//				mDataModel.cleanShortCutItem();
				//				mDataModel.initShortcutItem();
				//				mDockInfosHashMap.clear();
				//
				//				for (int i = 0; i < numOfRowInSetting; i++) {
				//					ArrayList<DockItemInfo> list = mDataModel.getShortCutItems(i);
				//					mDockInfosHashMap.put(i, list);
				//				}
				for (Integer rowId : dirtyDataRowIds) {
					correctDirtyData(rowId);
				}
				throw new DirtyDataException("dock has dirty data: " + sDirtyDataStr
						+ "; last version code is " + VersionControl.getLastVersionCode());
			}
		}

		return mDockInfosHashMap;
	}

	public void doWithRowChange() throws IllegalArgumentException {
		ShortCutSettingInfo settingInfo = SettingProxy.getShortCutSettingInfo();
		int numOfRowInSetting = settingInfo.mRows;
		if (numOfRowInSetting <= 0 || numOfRowInSetting > DockUtil.TOTAL_ROWS) {
			throw new IllegalArgumentException("setting row is wrong.row = " + numOfRowInSetting);
		}
		int oldRow = getShortCutItems().size();
		if (numOfRowInSetting > oldRow) {
			// 加行
			do {
				ArrayList<DockItemInfo> list = mDataModel.getShortCutItems(oldRow);
				mDockInfosHashMap.put(oldRow, list);
				oldRow++;
				// 文件夹要刷新图标
				for (DockItemInfo dockItemInfo : list) {
					if (dockItemInfo.mItemInfo.mItemType == IItemType.ITEM_TYPE_USER_FOLDER) {
						mDataModel.updateFolderIconAsync(dockItemInfo,
								DockUtil.TYPE_REFRASH_FOLDER_CONTENT_NORMAL, false);
					}
				}
			} while (numOfRowInSetting > oldRow);
		} else if (numOfRowInSetting < oldRow) {
			// 减行
			do {
				oldRow--;
				mDockInfosHashMap.remove(oldRow);
			} while (numOfRowInSetting < oldRow);
		}
	}

	/**
	 * 获取非自适应模式的空白显示
	 * 
	 * @return
	 */
	public ConcurrentHashMap<Integer, ArrayList<Integer>> getShortCutUnfitBlanks() {
		if (null == mBlanksHashMap) {
			mBlanksHashMap = mDataModel.getShortCutUnfitBlanks();
		}
		return mBlanksHashMap;
	}

	/**
	 * 更新单个Dock图标
	 * 
	 * @param positionIndex
	 *            对应索引
	 * @param userIcontype
	 *            图标类型（资源、文件、默认）
	 * @param userIconid
	 *            资源ID
	 * @param userIconpath
	 *            文件路径
	 * @return 返回值
	 */
	public synchronized int updateShortCutItemIcon(long id, int userIcontype, int userIconid,
			String userIconpackage, String userIconpath) {
		String packageStr = userIconpackage;
		if (null == packageStr) {
			packageStr = IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
		}
		return updateShortCutItemIcon(id, userIcontype, mDataModel.getThemeName1(), userIconid,
				packageStr, userIconpath);
	}

	public synchronized int updateShortCutItemIcon(long id, int userIcontype, String usePackage,
			int userIconid, String userIconpackage, String userIconpath) {
		// 找对应的DockItemInfo
		// 看是否存在
		DockItemInfo info = findDockItemInfo(id);
		if (null == info) {
			return DockUtil.ERROR_BAD_PARAM;
		}

		// 插入数据库
		info.mItemInfo
				.setFeatureIcon(null, userIcontype, userIconpackage, userIconid, userIconpath);
		info.mItemInfo.prepareFeatureIcon();
		info.mUsePackage = usePackage;
		mDataModel.updateDockItem(id, info);

		return DockUtil.ERROR_NONE;
	}

	public DockItemInfo findDockItemInfo(long id) {
		try {
			int hashmapSize = mDockInfosHashMap.size();
			for (int i = 0; i < hashmapSize; i++) {
				ArrayList<DockItemInfo> list = mDockInfosHashMap.get(i);
				int size = list.size();
				for (int j = 0; j < size; j++) {
					DockItemInfo dockItemInfo = list.get(j);
					if (dockItemInfo.mItemInfo.mInScreenId == id) {
						return dockItemInfo;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过intent找dock中的索引
	 * 
	 * @param info
	 * @return　ArrayList<Integer>　程序在dock中的下标索引数组
	 */
	private ArrayList<Long> findDockItemIndex(final AppItemInfo info) {
		ArrayList<Long> positions = new ArrayList<Long>();
		if (info == null) {
			return positions;
		}
		int sz = mDockInfosHashMap.size();
		DockItemInfo dockInfo = null;
		for (int i = 0; i < sz; i++) {
			ArrayList<DockItemInfo> list = mDockInfosHashMap.get(i);
			int listSize = list.size();
			for (int j = 0; j < listSize; j++) {
				dockInfo = list.get(j);
				if (null == dockInfo || null == dockInfo.mItemInfo
						|| !(dockInfo.mItemInfo instanceof ShortCutInfo)) {
					continue;
				}
				if (ConvertUtils.intentCompare(((ShortCutInfo) dockInfo.mItemInfo).mIntent,
						info.mIntent)) {
					positions.add(dockInfo.mItemInfo.mInScreenId);
				}
			}
		}
		return positions;
	}

	public void doThemeChanged() {
		if (null != mDeskThemeControler) {
			SettingProxy.updateShortcutSettingInfo();
			ShortCutSettingInfo info = SettingProxy.getShortCutSettingInfo();

			// 5个特殊图标
			DockItemControler controler = AppCore.getInstance().getDockItemControler();
			controler.useStyle(info.mStyle);
		}
	}

	/**
	 * <br>功能简述:卸载程序，判断dock条内图标是否需要响应删除
	 * <br>功能详细描述:
	 * <br>注意:
	 * 		１.显示出来的dock行，会清理view及info及数据库；
	 * 		２.没显示出来的dock行，也会清理数据库中的指定intent数据;
	 * @param appInfo
	 */
	private void unInstallApp(AppItemInfo appInfo) {
		if (null == appInfo) {
			return;
		}
		// 判断是否卸载的是通讯统计程序
		uninstallNotification(appInfo);
		ArrayList<Long> indexs = findDockItemIndex(appInfo);
		if (null == indexs) {
			return;
		}
		int size = indexs.size();
		for (int i = 0; i < size; i++) {
			DockItemInfo dockInfo = findDockItemInfo(indexs.get(i));
			// Dock 删除数据
			Long id = dockInfo.mItemInfo.mInScreenId;
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK, IDockMsgId.DELETE_DOCK_ITEM, -1, id,
					null);
		}

		//删除数据库shortcut　table里这个intent所有值
		String intentStr = (appInfo.mIntent != null) ? appInfo.mIntent.toUri(0) : null;
		if (intentStr != null) {
			mDataModel.deleteShortcutItems(intentStr);
		}
	}

	/**
	 * 判断卸载程序是否是通读统计，若是，处理
	 * 
	 * @param appInfo
	 */
	private void uninstallNotification(AppItemInfo appInfo) {
		if (appInfo == null) {
			return;
		}

		Intent intent = appInfo.mIntent;
		if (intent == null) {
			return;
		}

		Context context = ApplicationProxy.getContext();

		// 判断只有是通讯统计程序才发送消息
		boolean isNotification = AppIdentifier.isMessge(context, intent)
				|| AppIdentifier.isDial(context, intent) || AppIdentifier.isGmail(context, intent)
				|| AppIdentifier.isK9mail(context, intent)
				|| AppIdentifier.isFacebook(context, intent);
		if (isNotification) {
			ArrayList<Long> list = findDockItemIndex(appInfo);
		}
	}

	public void unInstallApp(ArrayList<AppItemInfo> appInfos) {
		if (null == appInfos) {
			return;
		}
		int sz = appInfos.size();
		AppItemInfo info = null;
		for (int i = 0; i < sz; i++) {
			info = appInfos.get(i);
			unInstallApp(info);
		}
	}

	public boolean updateShortCutBg(String useThemeName, String targetThemeName, String resName,
			boolean isCustomPic) {
		return mDataModel.updateShortCutBG(useThemeName, targetThemeName, resName, isCustomPic);
	}

	/**
	 * 通过intent获取原生图，若未找到，则返回null
	 * 
	 * @author huyong
	 * @param intent
	 * @return
	 */
	public BitmapDrawable getOriginalIcon(final ShortCutInfo itemInfo) {
		return mDataModel.getOriginalIcon(itemInfo);
	}

	public String getShortCutTitle(final ShortCutInfo itemInfo) {
		return mDataModel.getShortCutTitle(itemInfo);
	}

	/**
	 * @return　指定主题应该保存的Dock背景文件路径
	 * @param packageName
	 *            主题包名
	 */
	public static String getDockBgSaveFilePath(String packageName) {
		// String packageName = mDataModel.getThemeName1();
		String path = null;
		if (null != packageName) {
			path = LauncherEnv.Path.SDCARD + LauncherEnv.Path.THEME_PATH + "/" + packageName
					+ LauncherEnv.Path.DOCK_FOLDER + LauncherEnv.Path.DOCK_BG;
		} else {
			path = LauncherEnv.Path.SDCARD + LauncherEnv.Path.THEME_PATH
					+ LauncherEnv.Path.DOCK_FOLDER + LauncherEnv.Path.DOCK_BG;
		}

		try {
			File file = new File(path);
			File folder = file.getParentFile();
			if (!folder.exists()) {
				folder.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "file://" + path;
	}

	/**
	 * @return　当前应该查找的Dock背景文件路径
	 */
	public static String getDockBgReadFilePath() {
		String packageName = ThemeManager.getInstance(ApplicationProxy.getContext())
				.getCurThemePackage();
		String path = null;
		if (null != packageName) {
			path = LauncherEnv.Path.SDCARD + LauncherEnv.Path.THEME_PATH + "/" + packageName
					+ LauncherEnv.Path.DOCK_FOLDER + LauncherEnv.Path.DOCK_BG;
		} else {
			path = LauncherEnv.Path.SDCARD + LauncherEnv.Path.THEME_PATH
					+ LauncherEnv.Path.DOCK_FOLDER + LauncherEnv.Path.DOCK_BG;
		}

		// 如果是默认主题，尝试老版本路径
		if (null != packageName && packageName.equals(IGoLauncherClassName.DEFAULT_THEME_PACKAGE)) {
			File file = new File(path);
			if (!file.exists()) {
				// 文件不存在，尝试老版本文件
				path = LauncherEnv.Path.DOCK_BG_PATH;
			}
		}
		return path;
	}

	/**
	 * @return　当前应该查找的Dock背景文件路径
	 */
	public static String getDockBgReadFilePath(String themeName) {
		String path = null;
		if (null != themeName) {
			path = LauncherEnv.Path.SDCARD + LauncherEnv.Path.THEME_PATH + "/" + themeName
					+ LauncherEnv.Path.DOCK_FOLDER + LauncherEnv.Path.DOCK_BG;
		} else {
			path = LauncherEnv.Path.SDCARD + LauncherEnv.Path.THEME_PATH
					+ LauncherEnv.Path.DOCK_FOLDER + LauncherEnv.Path.DOCK_BG;
		}

		// 如果是默认主题，尝试老版本路径
		if (null != themeName && themeName.equals(IGoLauncherClassName.DEFAULT_THEME_PACKAGE)) {
			File file = new File(path);
			if (!file.exists()) {
				// 文件不存在，尝试老版本文件
				path = LauncherEnv.Path.DOCK_BG_PATH;
			}
		}
		return path;
	}

	/**
	 * 功能简述:根据设置值，获取dock底座图片 <br>
	 * 功能详细描述: <br>
	 * 注意:如果设置关闭dock背景，返回null
	 * 
	 * @return
	 */
	public static Drawable getDockBgDrawable() {
		Drawable drawable = null;
		try {
			ShortCutSettingInfo info = SettingProxy.getShortCutSettingInfo();
			if (info.mBgPicSwitch) {
				if (info.mCustomBgPicSwitch) {
					// 自定义背景选项开
					if (info.mBgiscustompic) {
						drawable = Drawable.createFromPath(info.mBgresname);
					} else {
						drawable = ImageExplorer.getInstance(ApplicationProxy.getContext())
								.getDrawable(info.mBgtargetthemename, info.mBgresname);
					}
				} else {
					// 自定义背景选项关
					String resNameString = AppCore.getInstance().getDeskThemeControler()
							.getDeskThemeBean().mDock.mDockSetting.mBackground;

					drawable = ImageExplorer.getInstance(ApplicationProxy.getContext())
							.getDrawable(
									ThemeManager.getInstance(ApplicationProxy.getContext())
											.getCurThemePackage(), resNameString);
				}
			}
		} catch (OutOfMemoryError e) {
		} catch (Throwable e) {
		}

		if (null != drawable) {
			if (drawable instanceof BitmapDrawable) {
				BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
				bitmapDrawable.setTargetDensity(ApplicationProxy.getContext().getResources()
						.getDisplayMetrics());
			}
		}

		return drawable;
	}

	public static String sDirtyDataStr = null;

	/**
	 * <br>功能简述:
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param dirtyRowIds 外部同步传入参数，用于记录有脏数据的rowId
	 * @return
	 */
	public boolean hasDirtyData(HashSet<Integer> dirtyRowIds) {
		boolean hasDirtyData = false;
		int size = mDockInfosHashMap.size();
		for (int i = 0; i < size; i++) {
			ArrayList<DockItemInfo> rowInfos = mDockInfosHashMap.get(i);
			int rowsize = rowInfos.size();
			if (rowsize > DockUtil.ICON_COUNT_IN_A_ROW) {
				sDirtyDataStr = "rowsize > 5 rowsize = " + rowsize + " rowid = " + i;
				// 某一行多于5个图标
				hasDirtyData = true;
				if (dirtyRowIds == null) {
					// 无需记录rowId，直接返回结果
					return hasDirtyData;
				} else {
					// 记录脏数据rowId, 继续检查其他行的脏数据
					dirtyRowIds.add(rowInfos.get(0).getmRowId());
					hasDirtyData = false;
					continue;
				}
			}
			ArrayList<Boolean> indexHasExits = new ArrayList<Boolean>();
			for (int j = 0; j < DockUtil.ICON_COUNT_IN_A_ROW; j++) {
				indexHasExits.add(false);
			}
			for (int j = 0; j < rowsize; j++) {
				DockItemInfo itemInfo = rowInfos.get(j);
				int indexinrow = itemInfo.getmIndexInRow();
				if (indexinrow < 0 || indexinrow >= DockUtil.ICON_COUNT_IN_A_ROW) {
					/**
					 * 异常情况： 1：不在0~4范围； 2：同行出现两个相同indexinrow
					 */
					sDirtyDataStr = "indexinrow is wrong. indexinrow = " + indexinrow;
					hasDirtyData = true;
					break;
				} else if (indexHasExits.get(indexinrow)) {
					sDirtyDataStr = "two info index equal. indexinrow = " + indexinrow;
					hasDirtyData = true;
					break;
				} else {
					indexHasExits.set(indexinrow, true);
				}
			}
			if (hasDirtyData) {
				if (dirtyRowIds == null) {
					// 无需记录rowId，直接返回结果
					return hasDirtyData;
				} else {
					// 记录脏数据rowId, 继续检查其他行的脏数据
					dirtyRowIds.add(rowInfos.get(0).getmRowId());
					hasDirtyData = false;
					continue;
				}
			}
		}
		return hasDirtyData;
	}

	/**
	 * 判断DOCK是否有脏数据
	 * 
	 * @return
	 */
	public boolean hasDirtyData() {
		return hasDirtyData(null);
	}
	
	/**
	 * <br>功能简述: 整理缓存及数据库后，检查脏数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param rowId
	 * @param throwException
	 * @return
	 */
	public boolean checkDirtyDataAfterReArrange(int rowId, boolean throwException) {
		boolean retval = false;
		//step1: 检查数据库数据项数目是否超过上限
		retval = checkDirtyDataCount(rowId, throwException, new StringBuffer(
				"checkDirtyDataAfterReArrange:curDbItemInfoSize is out of bound:"));
		//step2: 检查索引是否合法
		retval |= checkDirtyDataIndex(rowId, throwException, new StringBuffer(
				"checkDirtyDataAfterReArrange:duplex index:"));
		return retval;
	}

	/**
	 * <br>功能简述:删除数据后,检查index正确性
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param rowId 行号，对应Dock条的一个页面
	 * @param throwException 是否抛出异常
	 * @return
	 */
	public boolean checkDirtyDataAfterDelete(int rowId, boolean throwException) {
		boolean retval = false;
		StringBuffer detailMsg = new StringBuffer(
				"checkDirtyDataAfterDelete, discontinuous indexInRow:");
		retval = checkDirtyDataIndex(rowId, throwException, detailMsg);
		return retval;
	}
	
	/**
	 * <br>功能简述: 插入数据后,判断数据库中记录数是否超过最大值
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param rowId 行号，对应Dock条的一个页面
	 * @param throwException 是否抛出异常
	 * @return
	 */
	public boolean checkDirtyDataAfterInsert(int rowId, boolean throwException) {
		StringBuffer detailMsg = new StringBuffer("checkDirtyDataAfterInsert, curDbItemInfoSize is out of bound:");
		return checkDirtyDataCount(rowId, throwException, detailMsg);
	}
	
	/**
	 * <br>功能简述:修改数据库后，判断数据库中记录的index是否重复
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param rowId 行号，对应Dock条的一个页面
	 * @param throwException 是否抛出异常
	 * @return
	 */
	public boolean checkDirtyDataAfterModifyIndex(int rowId, boolean throwException) {
		StringBuffer detailMsg = new StringBuffer("checkDirtyDataAfterModify, duplex indexInRow:");
		return checkDirtyDataIndex(rowId, throwException, detailMsg);
	}
	
	/**
	 * <br>功能简述:检查数据项数量是否超过上限
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param rowId
	 * @param throwException
	 * @param detailMsg
	 * @return
	 */
	private boolean checkDirtyDataCount(int rowId, boolean throwException, StringBuffer detailMsg) {
		boolean retval = false;
		ArrayList<DockItemInfo> curDbItemInfos = mDataModel.getShortCutItems(rowId);
		final int curDbItemInfoSize = curDbItemInfos.size();
		retval = curDbItemInfoSize > DockUtil.ICON_COUNT_IN_A_ROW;
		if (retval && throwException) {
			if (detailMsg == null) {
				detailMsg = new StringBuffer("checkDirtyDataCount, count out of bound: " + curDbItemInfoSize);
			} else {
				detailMsg.append(curDbItemInfoSize);
			}
			// 先从数据库中删除多余的数据项，再抛异常（注意，直接操作数据库，无需检查缓存一致性）
			for (int i = DockUtil.ICON_COUNT_IN_A_ROW; i < curDbItemInfoSize; i++) {
				DockItemInfo delInfo = curDbItemInfos.get(i);
				mDataModel.deleteShortcutItem(delInfo.mItemInfo.mInScreenId);
			}
			throw new DirtyDataException(detailMsg.toString());
		}
		return retval;
	}

	/**
	 * <br>功能简述: 检查index是否重复，是否连续
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param rowId
	 * @param throwException
	 * @param detailMsg
	 * @return
	 */
	private boolean checkDirtyDataIndex(int rowId, boolean throwException, StringBuffer detailMsg) {
		boolean retval = false;
		if (detailMsg == null) {
			detailMsg = new StringBuffer();
		}
		ArrayList<DockItemInfo> curDbItemInfos = mDataModel.getShortCutItems(rowId);
		final int defaultIndex = -2;
		int size = curDbItemInfos.size();
		ArrayList<Integer> indexOccupied = new ArrayList<Integer>();
		//step1: 初始化缺省值
		for (int i = 0; i < size; i++) {
			indexOccupied.add(defaultIndex);
		}
		//step2： 检查数据单一性及连续性
		for (int i = 0; i < size; i++) {
			DockItemInfo info = curDbItemInfos.get(i);
			int indexInRow = info.getmIndexInRow();
			if (indexInRow < 0 || indexInRow >= size
					|| indexOccupied.get(indexInRow) != defaultIndex) {
				retval = true;
				break;
			} else {
				indexOccupied.set(indexInRow, indexInRow);
			}
		}
		//step2: 抛出异常，重启桌面
		if (retval && throwException) {
			detailMsg.append("[");
			for (int i = 0; i < curDbItemInfos.size(); i++) {
				DockItemInfo info = curDbItemInfos.get(i);
				detailMsg.append(info.getmIndexInRow());
				if (i < curDbItemInfos.size() - 1) {
					detailMsg.append(",");
				} else {
					detailMsg.append("]");
				}
				// 先进行index纠错，再抛出异常（注意，直接操作数据库，无需检查缓存一致性）
				if (info.getmIndexInRow() != i) {
					mDataModel.updateDockItemIndex(info.mItemInfo.mInScreenId, i);
				}
			}
			throw new DirtyDataException(detailMsg.toString());
		}
		return retval;
	}

	/**
	 * <br>功能简述: 脏数据纠错
	 * <br>功能详细描述:
	 * <br>注意: 只对数据库进行纠错，内存数据未作纠错，纠错后立即抛出异常，重启桌面
	 * @param rowId
	 */
	private void correctDirtyData(int rowId) {
		ArrayList<DockItemInfo> curDbItemInfos = mDataModel.getShortCutItems(rowId);
		int curDbItemInfoSize = curDbItemInfos.size();
		boolean isCountOutOfBound = curDbItemInfoSize > DockUtil.ICON_COUNT_IN_A_ROW;
		// step1: 先从数据库中删除多余的数据项
		if (isCountOutOfBound) {
			for (int i = DockUtil.ICON_COUNT_IN_A_ROW; i < curDbItemInfoSize; i++) {
				DockItemInfo delInfo = curDbItemInfos.get(i);
				mDataModel.deleteShortcutItem(delInfo.mItemInfo.mInScreenId);
			}
		}
		// step2: 再纠正数据项的index（注意，直接操作数据库，无需检查缓存一致性）
		curDbItemInfoSize = isCountOutOfBound ? DockUtil.ICON_COUNT_IN_A_ROW : curDbItemInfoSize;
		for (int i = 0; i < curDbItemInfoSize; i++) {
			DockItemInfo info = curDbItemInfos.get(i);
			if (info.getmIndexInRow() != i) {
				mDataModel.updateDockItemIndex(info.mItemInfo.mInScreenId, i);
			}
		}
	}

	/**
	 * "DOCK清理垃圾数据"
	 */
	public void clearDockDirtyData() {
		mDataModel.cleanShortCutItem();
		mDataModel.initShortcutItem();
	}

	/**
	 * "默认图标"按钮
	 * 
	 * @param positionIndex
	 */
	public void resetDockItemIcon(DockItemInfo dockInfo) {
		if (null == dockInfo) {
			return;
		}

		// icon db
		dockInfo.mItemInfo.resetFeature();
		dockInfo.mUsePackage = null;
		updateDockItem(dockInfo.mItemInfo.mInScreenId, dockInfo);

		// icon UI
		if (dockInfo.mItemInfo instanceof ShortCutInfo) {
			BitmapDrawable icon = null;
			if (null != dockInfo.mItemInfo.getRelativeItemInfo()) {
				icon = dockInfo.mItemInfo.getRelativeItemInfo().getIcon();
			}
			ShortCutInfo shortCutInfo = (ShortCutInfo) dockInfo.mItemInfo;
			if (null == icon && null != shortCutInfo.mIcon
					&& shortCutInfo.mIcon instanceof BitmapDrawable) {
				if (null != shortCutInfo.mIcon && shortCutInfo.mIcon instanceof BitmapDrawable) {
					icon = (BitmapDrawable) shortCutInfo.mIcon;
				}
			}
			if (null == icon) {
				// 给机器人图标
				AppDataEngine engine = AppDataEngine.getInstance(ApplicationProxy.getContext());
				icon = engine.getSysBitmapDrawable();
			}
			shortCutInfo.setIcon(icon, false);
			dockInfo.broadCast(DockItemInfo.ICONCHANGED, 0, null, null);
		} else if (dockInfo.mItemInfo instanceof UserFolderInfo) {
			updateFolderIconAsync(dockInfo, false);
		}
	}

	/**
	 * 删除指定主题下的dock自定义背景
	 * 
	 * @param intent
	 *            主题的intent
	 */
	private void deleteSpecificThemeDockBg(String packageName) {
		if (packageName == null) {
			return;
		}

		String path = null;
		path = LauncherEnv.Path.SDCARD + LauncherEnv.Path.THEME_PATH + "/" + packageName
				+ LauncherEnv.Path.DOCK_FOLDER + LauncherEnv.Path.DOCK_BG;
		try {
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 换程序，包括点击与手势，手势信息跟位置相关，所以换程序不会换手势
	 * 
	 * @param id
	 *            inscreenid
	 * @param info
	 *            更换信息
	 * @param type
	 *            类型定义：详见DockConstant
	 */
	public void changeApp(long id, ItemInfo itemInfo, int type) {
		DockItemInfo dockItemInfo = findDockItemInfo(id);
		if (dockItemInfo == null) {
			return;
		}
		Intent intent = null;
		ShortCutInfo info = null;

		if (itemInfo instanceof ShortCutInfo) {
			info = (ShortCutInfo) itemInfo;
			intent = ((ShortCutInfo) itemInfo).mIntent;
		}
		switch (type) {
			case DockUtil.CHANGE_FROM_DELETEFOLER :
				prepareItemInfo(info);
				dockItemInfo.setInfo(info);
				dockItemInfo.mUsePackage = null;

				// 保存数据
				updateDockItem(id, dockItemInfo);
				dockItemInfo.broadCast(DockItemInfo.INTENTCHANGED, 0, null, null);
				dockItemInfo.broadCast(DockItemInfo.ICONCHANGED, 0, null, null);
				break;

			case DockUtil.CHANGE_FROM_GESTURE :
				dockItemInfo.mGestureInfo.mUpIntent = intent;
				updateDockItem(id, dockItemInfo);
				break;

			case DockUtil.CHANGE_FROM_FOLDER :
				if (itemInfo instanceof UserFolderInfo) {
					// dockItemInfo.mimOpen=false;
					UserFolderInfo folderInfo = (UserFolderInfo) itemInfo;
					folderInfo.mOpened = false;
					dockItemInfo.setInfo(folderInfo);
					final int count = folderInfo.getChildCount();
					for (int i = 0; i < count; i++) {
						ShortCutInfo item = folderInfo.getChildInfo(i);
						if (null != item) {
							item.clearAllObserver();
							// item.registerObserver(dockItemInfo);
						}
						if (0 != folderInfo.mRefId) {
							prepareItemInfo(item);
						}
					}
				}
				// 保存数据
				updateDockItem(id, dockItemInfo);
				dockItemInfo.broadCast(DockItemInfo.INTENTCHANGED, 0, null, null);
				updateFolderIconAsync(dockItemInfo, false);
				break;
			default :
				break;
		}
	}

	/**
	 * 处理事件－－定时扫描时间到，需要刷新
	 */
	public void handleEventReflushTimeIsUp() {
		// sd卡shared状态
		boolean isShared = Environment.getExternalStorageState().equals(Environment.MEDIA_SHARED);
		if (!isShared) {
			handleEventReflashSdcardIsOk();
		}
	}

	/**
	 * 处理事件－－处理ＳＤ卡扫描完毕消息
	 */
	public void handleEventReflashSdcardIsOk() {
		int size = mDockInfosHashMap.size();
		for (int i = 0; i < size; i++) {
			ArrayList<DockItemInfo> list = mDockInfosHashMap.get(i);
			// 重新校验
			// 1. 对重新绑定的，更换图标
			// 2. 依旧不能绑定的（卸载），更换图标，清理数据库
			mDataModel.contactOriginAppInfo(list);

			int listsize = list.size();
			for (int j = 0; j < listsize; j++) {
				DockItemInfo info = list.get(j);

				updateDockIcon(info);
			}
		}
	}

	/**
	 * 主动更新图标图片
	 * 
	 * @param dockItemInfo
	 */
	public void updateDockIcon(DockItemInfo dockItemInfo) {
		if (null == dockItemInfo || null == dockItemInfo.mItemInfo) {
			return;
		}

		BitmapDrawable icon = null;
		if (dockItemInfo.mItemInfo instanceof ShortCutInfo) {
			switch (dockItemInfo.mItemInfo.mFeatureIconType) {
				case ImagePreviewResultType.TYPE_IMAGE_FILE :
				case ImagePreviewResultType.TYPE_PACKAGE_RESOURCE :
				case ImagePreviewResultType.TYPE_IMAGE_URI :
				case ImagePreviewResultType.TYPE_APP_ICON :
					dockItemInfo.mItemInfo.prepareFeatureIcon();
					Drawable featureDrawable = dockItemInfo.mItemInfo.getFeatureIcon();
					if (null != featureDrawable && featureDrawable instanceof BitmapDrawable) {
						icon = (BitmapDrawable) featureDrawable;
					}
					break;

				case ImagePreviewResultType.TYPE_DEFAULT :
					icon = mDataModel.getOriginalIcon((ShortCutInfo) dockItemInfo.mItemInfo);
					break;

				default :
					break;

			}
			if (null == icon) {
				// 在桌面外御载了程序，给机器人图标
				icon = AppDataEngine.getInstance(ApplicationProxy.getContext())
						.getSysBitmapDrawable();
			}
			dockItemInfo.broadCast(DockItemInfo.ICONCHANGED, 0, null, null);
		} else if (dockItemInfo.mItemInfo instanceof UserFolderInfo) {
			updateFolderIconAsync(dockItemInfo, false);
		}

	}

	/**
	 * 处理事件：sd卡mount上
	 */
	public void handleEventSdMount() {
		handleEventReflashSdcardIsOk();
	}

	/**
	 * <br>功能简述:dock响应卸载程序处理
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param infos
	 */
	public void handleEventUninstallApps(ArrayList<AppItemInfo> infos) {
		//1:对dock图标进行清理
		unInstallApp(infos);
		//2:对dock文件夹内图标进行清理
		updateFolderItemsForUninstall(infos);
	}

	public void handleEventUninstallPackage(String pkgName) {
		// 响应主题删除，删dock背景
		deleteSpecificThemeDockBg(pkgName);
	}

	/**
	 * 保存数据库：dockItem
	 * 
	 * @param info
	 * @return
	 */
	public boolean updateDockItem(long id, DockItemInfo info) {
		try {
			mDataModel.updateDockItem(id, info);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void addItemToFolder(ItemInfo info, UserFolderInfo folderInfo) {
		final int count = folderInfo.getChildCount();
		// 先写入数据库，再修改内存数据
		mDataModel.addItemToFolder(info, folderInfo.mInScreenId, count, false);
		folderInfo.add(info);
	}

	public void addDrawerFolderToDock(UserFolderInfo userFolderInfo) {
		final int count = userFolderInfo.getChildCount();
		for (int i = 0; i < count; i++) {
			ShortCutInfo info = userFolderInfo.getChildInfo(i);
			info.mInScreenId = System.currentTimeMillis() + i;
			mDataModel.addItemToFolder(info, userFolderInfo.mInScreenId, i, true);
		}
	}

	/**
	 * 删除指定文件夹内指定intent的图标
	 * 
	 * @param folderInfo
	 * @param intent
	 */
	public void removeDockFolderItems(UserFolderInfo folderInfo, ShortCutInfo shortCutInfo) {
		// 1:删除缓存
		ArrayList<ShortCutInfo> list = folderInfo.remove(shortCutInfo);
		// 2:删除DB
		int size = list.size();
		if (size > 0) {
			final long folderId = folderInfo.mInScreenId;
			for (int i = 0; i < size; i++) {
				ShortCutInfo info = list.get(i);
				removeDockFolderItem(info.mInScreenId, folderId);
			}
		}
		list.clear();
		list = null;
	}

	/**
	 * 删除指定文件夹内的一个item
	 * 
	 * @param itemId
	 * @param folderId
	 */
	public void removeDockFolderItem(long itemId, long folderId) {
		mDataModel.removeDockFolderItem(itemId, folderId);
	}

	/**
	 * 删除文件夹
	 * 
	 * @param folderId
	 */
	public void removeDockFolder(long folderId) {
		GLAppFolderController folderController = GLAppFolderController.getInstance();
		folderController.removeFolderInfo(folderController.getFolderInfoById(folderId,
				GLAppFolderInfo.FOLDER_FROM_DOCK));
		Log.i("dzj", "Dock--->" + "removeFolderInfo");
		mDataModel.removeDockFolder(folderId);
	}

	/**
	 * 获取文件夹项
	 * 
	 * @param folderId
	 * @return
	 */
	public synchronized ArrayList<ItemInfo> getFolderItems(final long folderId) {
		return mDataModel.getDockFolderItems(folderId, -1, true);
	}

	public void prepareItemInfo(ItemInfo info) {
		mDataModel.prepareItemInfo(info);
	}

	/**
	 * 刷新文件夹图标
	 * 
	 * @param dockItemInfo
	 *            dock文件信息
	 * @param checkDel
	 *            刷新完后是否要检查删除文件夹
	 */
	public void updateFolderIconAsync(DockItemInfo dockItemInfo, boolean checkDel) {
		updateFolderIconAsync(dockItemInfo, DockUtil.TYPE_REFRASH_FOLDER_CONTENT_NORMAL, checkDel);
	}

	/**
	 * 刷新文件夹图标
	 * 
	 * @param dockItemInfo
	 *            dock文件信息
	 * @param checkDel
	 *            刷新完后是否要检查删除文件夹
	 */
	public void updateFolderIconAsync(DockItemInfo dockItemInfo, int type, boolean checkDel) {
		mDataModel.updateFolderIconAsync(dockItemInfo, type, checkDel);
	}

	public void updataAllFolder() {
		int size = getShortCutItems().size();
		for (int i = 0; i < size; i++) {
			ArrayList<DockItemInfo> infos = mDockInfosHashMap.get(i);
			for (DockItemInfo dockItemInfo : infos) {
				if (null != dockItemInfo && dockItemInfo.mItemInfo instanceof UserFolderInfo) {
					mDataModel.updateFolderIconAsync(dockItemInfo,
							DockUtil.TYPE_REFRASH_FOLDER_CONTENT_NORMAL, false);
				}
			}
		}
	}

	public void updateFolderIndex(long folderID, ArrayList<ItemInfo> infos) {
		mDataModel.updateFolderIndex(folderID, infos);
	}

	public void updateFolderItem(long folderID, ItemInfo info) {
		mDataModel.updateFolderItem(folderID, info);
		mDataModel.prepareItemInfo(info);
	}
	
	/**
	 * <br>功能简述: 获取指定的DockItemInfo
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param id 唯一标识
	 * @return
	 */
	public DockItemInfo getShortcutItem(long id) {
		DockItemInfo retval = null;
		int infosHashMapSize = mDockInfosHashMap.size();
		for (int i = 0; i < infosHashMapSize; i++) {
			ArrayList<DockItemInfo> infos = mDockInfosHashMap.get(i);
			for (DockItemInfo info : infos) {
				if (info.mItemInfo.mInScreenId == id) {
					retval = info;
					break;
				}
			}
			if (retval != null) {
				break;
			}
		}
		return retval;
	}
	
	/**
	 * <br>功能简述:删除dock的一个选项，同时对剩余项进行顺序重排
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param id
	 * @return
	 */
	public boolean deleteShortcutItemAndReArrange(int rowId, long id) {
		boolean ret = false;
		//step1: 删除对应项
		ret = deleteShortcutItem(id);
		//step2： 顺序重排
		if (ret) {
			ArrayList<DockItemInfo> infos = mDockInfosHashMap.get(rowId);
			if (infos != null) {
				for (int i = 0; i < infos.size(); i++) {
					DockItemInfo info = infos.get(i);
					if (info.getmIndexInRow() != i) {
						modifyShortcutItemIndex(info, i);
					}
				}
			}
			//step3: 脏数据检查
			checkDirtyDataAfterReArrange(rowId, true);
		}
		return ret;
	}

	public boolean deleteShortcutItem(long id) {
		boolean ret = false;
		try {
			int infosHashMapSize = mDockInfosHashMap.size();
			for (int i = 0; i < infosHashMapSize; i++) {
				ArrayList<DockItemInfo> infos = mDockInfosHashMap.get(i);
				int size = infos.size();
				for (int j = 0; j < size; j++) {
					DockItemInfo info = infos.get(j);
					if (info.mItemInfo.mInScreenId == id) {
						// 找到要删除的目标项
						// 删除缓存对应值
						boolean removed = infos.remove(info);
						// 删除数据库
						if (removed) {
							mDataModel.deleteShortcutItem(id);
							size--;
							j--;
							ret = true;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * <br>功能简述:删除dock的一个选项，同时对剩余项进行顺序重排
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param id
	 * @return
	 */
	public boolean insertShortcutItemAndReArrange(DockItemInfo dockItemInfo) {
		boolean ret = false;
		//step1: 插入项
		ret = insertShortcutItem(dockItemInfo);
		//step2： 顺序重排
		if (ret) {
			final int rowid = dockItemInfo.getmRowId();
			final int indexInRow = dockItemInfo.getmIndexInRow();
			ArrayList<DockItemInfo> infos = mDockInfosHashMap.get(rowid);
			// step2.1: 插入项后面的项index自增1
			for (DockItemInfo info : infos) {
				if (!info.equals(dockItemInfo) && info.getmIndexInRow() >= indexInRow) {
					modifyShortcutItemIndex(info, info.getmIndexInRow() + 1);
				}
			}
			// step2.2: 按照index从小到大进行排序
			Collections.sort(infos, mCompatatorIndexInDock);
			// step2.3: 排序后，再次检查index顺序并纠正
			if (infos != null) {
				for (int i = 0; i < infos.size(); i++) {
					DockItemInfo info = infos.get(i);
					if (info.getmIndexInRow() != i) {
						modifyShortcutItemIndex(info, i);
					}
				}
			}
			//step3: 脏数据检查
			checkDirtyDataAfterReArrange(rowid, true);
		}
		return ret;
	}

	/**
	 * 插入一项
	 * 
	 * @param info
	 *            插入项
	 * @return
	 */
	public boolean insertShortcutItem(DockItemInfo info) {
		boolean ret = false;
		try {
			int rowid = info.getmRowId();
			int indexinrow = info.getmIndexInRow();
			ArrayList<DockItemInfo> infos = mDockInfosHashMap.get(rowid);
			int infosSize = infos.size();
			if (infosSize < DockUtil.ICON_COUNT_IN_A_ROW) {
				if (0 <= indexinrow && indexinrow < DockUtil.ICON_COUNT_IN_A_ROW) {
					prepareItemInfo(info.mItemInfo);
					// 尾部插入缓存对应值
					infos.add(info);
					// 修改数据库
					mDataModel.addDockItem(info);
					// 如果此处原来是空白，要删除空白
					delBlank(info.getmRowId(), info.getmIndexInRow());
					ret = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * <br>功能简述: 挤压换位，并同步更新数据库
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param rowId
	 * @param indexL
	 * @param indexR
	 * @return
	 */
	public boolean extrudeShortcutItem(int rowId, int srcIndex, int desIndex) {
		boolean retval = false;
		ArrayList<DockItemInfo> infos = mDockInfosHashMap.get(rowId);
		int countComparable = 0;
		//step1: 检查用于换位操作位置上的info是否同时存在
		for (DockItemInfo info : infos) {
			if (info.getmIndexInRow() == srcIndex || info.getmIndexInRow() == desIndex) {
				countComparable++;
			}
		}
		if (countComparable == 2) {
			//step2： 进行index换位操作
			if (srcIndex > desIndex) {
				for (DockItemInfo info : infos) {
					if (info.getmIndexInRow() >= desIndex && info.getmIndexInRow() < srcIndex) {
						modifyShortcutItemIndex(info, info.getmIndexInRow() + 1);
					} else if (info.getmIndexInRow() == srcIndex) {
						modifyShortcutItemIndex(info, desIndex);
					}
				}
			} else if (srcIndex < desIndex) {
				for (DockItemInfo info : infos) {
					if (info.getmIndexInRow() <= desIndex && info.getmIndexInRow() > srcIndex) {
						modifyShortcutItemIndex(info, info.getmIndexInRow() - 1);
					} else if (info.getmIndexInRow() == srcIndex) {
						modifyShortcutItemIndex(info, desIndex);
					}
				}
			}
			//step3: 换位后，进行顺序重排
			Collections.sort(infos, mCompatatorIndexInDock);
			//step4: 排序后，再次检查index顺序并纠正
			if (infos != null) {
				for (int i = 0; i < infos.size(); i++) {
					DockItemInfo info = infos.get(i);
					if (info.getmIndexInRow() != i) {
						modifyShortcutItemIndex(info, i);
					}
				}
			}
			//step5: 脏数据检查
			checkDirtyDataAfterReArrange(rowId, true);
			retval = true;
		}
		return retval;
	}
	
	/**
	 * 非自适应模式，修改某一项位置
	 * 
	 * @param info
	 *            修改项
	 * @param indexinrow
	 *            新索引
	 */
	public void modifyShortcutItemIndex(DockItemInfo info, int newindexinrow) {
		try {
			if (info.getmIndexInRow() != newindexinrow) {
				// 更新缓存
				info.setmIndexInRow(newindexinrow);
				// 更新数据库
				mDataModel.updateDockItemIndex(info.mItemInfo.mInScreenId, newindexinrow);
				// 如果有空白显示，要删除
				delBlank(info.getmRowId(), newindexinrow);
			}
		} catch (Exception e) {
			// 如果出现异常，这个图标更新索引不成功
		}
	}
	
	/**
	 * <br>功能简述: 同步缓存和Db的index
	 * <br>功能详细描述: 从缓存同步到Db
	 * <br>注意:
	 * @param info
	 */
	public void synchronizeItemIndexCache2Db(DockItemInfo info) {
		// 更新数据库
		mDataModel.updateDockItemIndex(info.mItemInfo.mInScreenId, info.getmIndexInRow());
	}

	/**
	 * 设置数据统计
	 */
	public void controlNotification() {
		if (ShortCutSettingInfo.mAutoMessageStatistic) {
			AppCore.getInstance().getNotificationControler().startSMSMonitor();
		} else {
			AppCore.getInstance().getNotificationControler().stopSMSMonitor();
		}

		if (ShortCutSettingInfo.mAutoMisscallStatistic) {
			AppCore.getInstance().getNotificationControler().startCallMonitor();
		} else {
			AppCore.getInstance().getNotificationControler().stopCallMonitor();
		}

		if (ShortCutSettingInfo.mAutoMissmailStatistic) {
			AppCore.getInstance().getNotificationControler().startGmailMonitor();
		} else {
			AppCore.getInstance().getNotificationControler().stopGmailMonitor();
		}

		if (ShortCutSettingInfo.mAutoMissk9mailStatistic) {
			AppCore.getInstance().getNotificationControler().startK9mailMonitor();
		} else {
			AppCore.getInstance().getNotificationControler().stopK9mailMonitor();
		}

		if (ShortCutSettingInfo.mAutoMissfacebookStatistic) {
			AppCore.getInstance().getNotificationControler().startFacebookMonitor();
		} else {
			AppCore.getInstance().getNotificationControler().stopFacebookMonitor();
		}

		if (ShortCutSettingInfo.mAutoMissSinaWeiboStatistic) {
			AppCore.getInstance().getNotificationControler().startSinaWeiboMonitor();
		} else {
			AppCore.getInstance().getNotificationControler().stopSinaWeiboMonitor();
		}
	}

	/**
	 * <br>功能简述:卸载程序后刷新文件夹
	 * <br>功能详细描述:
	 * <br>注意:如果没显示出来的dock行包含文件夹，则不处理
	 * (因为如果删除文件夹内图标，还要涉及文件夹是否要替换为最后一个程序图标这些复杂的处理，行没显示出来处理起来太复杂)
	 * @param infos
	 */
	private void updateFolderItemsForUninstall(ArrayList<AppItemInfo> infos) {
		ConcurrentHashMap<Integer, ArrayList<DockItemInfo>> dockItems = getShortCutItems();
		int size = dockItems.size();
		for (AppItemInfo info : infos) {
			for (int i = 0; i < size; i++) {
				ArrayList<DockItemInfo> list = dockItems.get(i);
				for (DockItemInfo dockItem : list) {
					boolean needUpdata = false;
					if (!(dockItem.mItemInfo instanceof UserFolderInfo)) {
						continue;
					}
					UserFolderInfo folderInfo = (UserFolderInfo) dockItem.mItemInfo;

					for (int j = 0; j < folderInfo.getChildCount(); j++) {
						ItemInfo deskInfo = folderInfo.getChildInfo(j);
						if (null == deskInfo) {
							continue;
						}
						if (ConvertUtils.intentCompare(info.mIntent,
								((ShortCutInfo) deskInfo).mIntent)) {
							removeDockFolderItem(deskInfo.mInScreenId, folderInfo.mInScreenId);
							folderInfo.remove(deskInfo);
							needUpdata = true;
						}
					}
					if (needUpdata) {
						updateFolderIconAsync(dockItem,
								DockUtil.TYPE_REFRASH_FOLDER_CONTENT_UNINSTALLAPP, true);
					}
				}
			}
		}

		//TODO:上述方法只对当前显示出来的dock文件夹做删除文件夹内被卸载程序图标，而没处理没显示出来的行数的dock文件夹
	}

	/**
	 * 从数据库获取每个DockIconView的图片资源
	 * 
	 * @param rowid
	 *            第几行
	 * @param indexinrow
	 *            第几行第几位
	 * @return
	 */
	public Bitmap getIconBitmap(DockItemInfo info) {
		Bitmap bitmap = null;
		try {
			bitmap = info.getIcon().getBitmap();
		} catch (Throwable e) {
			// 后台数据异常，返回空
			return AppDataEngine.getInstance(mContext).getSysBitmapDrawable().getBitmap();
		}

		// 如果拿不到图标，就给一张默认图片
		if (null == bitmap) {
			// TODO：给机器人图片
			return AppDataEngine.getInstance(mContext).getSysBitmapDrawable().getBitmap();
		}
		return bitmap;
	}

	//	/**
	//	 * 检查是否需要弹出dock自适应提示
	//	 * 
	//	 * @return
	//	 */
	//	public boolean checkNeedShowDockAutoFitGuide(final IMessageHandler messageHandler) {
	//		boolean result = false;
	//		if (StaticTutorial.sCheckDockAutoFit) {
	//			StaticTutorial.sCheckDockAutoFit = false;
	//			PreferencesManager sharedPreferences = new PreferencesManager(mContext,
	//					IPreferencesIds.USERTUTORIALCONFIG, Context.MODE_PRIVATE);
	//			boolean shouldshowguide = sharedPreferences.getBoolean(
	//					IPreferencesIds.SHOULD_SHOW_DOCK_AUTO_FIT_GUIDE, true);
	//			if (shouldshowguide) {
	//				MsgMgrProxy.sendMessage(this, IDiyFrameIds.DRAG_FRAME, IDiyMsgIds.TRASH_GONE, -1,
	//						null, null);   // 在隐藏状态栏前先把垃圾箱隐藏否则某些手机上透明通知栏下来时会有残影
	//				AbstractFrame dockFrame = FrameControl.getFrameStatic(IDiyFrameIds.DOCK_FRAME);
	//				if (dockFrame != null) {
	//					dockFrame.getContentView().postDelayed(new Runnable() {
	//
	//						@Override
	//						public void run() {
	//							MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,    // 在showGuide之前先隐藏状态栏
	//									IDiyMsgIds.SHOW_HIDE_STATUSBAR, -2, false, null);
	//							GuideForGlFrame.setmGuideType(GuideForGlFrame.GUIDE_TYPE_DOCK_AUTO_FIT);
	//							MsgMgrProxy.sendMessage(messageHandler, IDiyFrameIds.SCHEDULE_FRAME,
	//									IFrameworkMsgId.SHOW_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null,
	//									null);
	//
	//						}
	//					}, 100);
	//				} else {
	//					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,    // 在showGuide之前先隐藏状态栏
	//							IDiyMsgIds.SHOW_HIDE_STATUSBAR, -2, false, null);
	//					GuideForGlFrame.setmGuideType(GuideForGlFrame.GUIDE_TYPE_DOCK_AUTO_FIT);
	//					MsgMgrProxy.sendMessage(messageHandler, IDiyFrameIds.SCHEDULE_FRAME,
	//							IFrameworkMsgId.SHOW_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null, null);
	//				}
	//				SharedPreferences.Editor editor = sharedPreferences.edit();
	//				editor.putBoolean(IPreferencesIds.SHOULD_SHOW_DOCK_AUTO_FIT_GUIDE, false);
	//				editor.commit();
	//				result = true;
	//			}
	//		}
	//		return result;
	//	}

	public void gestureDataChange(int msg, DockItemInfo info) {
		Intent intent = null;
		switch (msg) {
			case IDockSettingMSG.GES_ESTOP :
				intent = new Intent(ICustomAction.ACTION_NONE);
				info.mGestureInfo.mUpIntent = intent;
				break;

			case IDockSettingMSG.GES_SHOW_MAIN_SCREEN :
				intent = new Intent(ICustomAction.ACTION_SHOW_MAIN_SCREEN);
				info.mGestureInfo.mUpIntent = intent;
				break;

			case IDockSettingMSG.GES_MAIN_SCREEN_FIRST :
				intent = new Intent(ICustomAction.ACTION_SHOW_MAIN_OR_PREVIEW);
				info.mGestureInfo.mUpIntent = intent;
				break;

			case IDockSettingMSG.GES_SHOW_PREVIEW :
				intent = new Intent(ICustomAction.ACTION_SHOW_PREVIEW);
				info.mGestureInfo.mUpIntent = intent;
				break;

			case IDockSettingMSG.GES_SHOW_MENU :
				intent = new Intent(ICustomAction.ACTION_SHOW_MENU);
				info.mGestureInfo.mUpIntent = intent;
				break;

			case IDockSettingMSG.GES_SHOW_HIDE_FUNCLIST :
				intent = new Intent(ICustomAction.ACTION_SHOW_FUNCMENU);
				info.mGestureInfo.mUpIntent = intent;
				break;

			case IDockSettingMSG.GES_SHOW_EXPEND_BAR :
				intent = new Intent(ICustomAction.ACTION_SHOW_EXPEND_BAR);
				info.mGestureInfo.mUpIntent = intent;
				break;

			case IDockSettingMSG.GES_SHOW_HIDE_STATEBAR :
				intent = new Intent(ICustomAction.ACTION_SHOW_HIDE_STATUSBAR);
				info.mGestureInfo.mUpIntent = intent;
				break;
			case IDockSettingMSG.GES_LOCK_SCREEN :
				intent = new Intent(ICustomAction.ACTION_ENABLE_SCREEN_GUARD);
				info.mGestureInfo.mUpIntent = intent;
				break;
			case IDockSettingMSG.GES_OPEN_GOSTORE :
				intent = new Intent(ICustomAction.ACTION_FUNC_SPECIAL_APP_GOSTORE);
				info.mGestureInfo.mUpIntent = intent;
				break;
			case IDockSettingMSG.GES_OPEN_THEMESETTIGN :
				intent = new Intent(ICustomAction.ACTION_FUNC_SPECIAL_APP_GOTHEME);
				info.mGestureInfo.mUpIntent = intent;
				break;
			case IDockSettingMSG.GES_OPEN_PREFERENCE :
				intent = new Intent(ICustomAction.ACTION_SHOW_PREFERENCES);
				info.mGestureInfo.mUpIntent = intent;
				break;
			case IDockSettingMSG.GES_SHOW_DOCK :
				intent = new Intent(ICustomAction.ACTION_SHOW_DOCK);
				info.mGestureInfo.mUpIntent = intent;
				break;

			case IDockSettingMSG.GES_SHOW_DIYGESTURE :
				intent = new Intent(ICustomAction.ACTION_SHOW_DIYGESTURE);
				info.mGestureInfo.mUpIntent = intent;
				break;

			case IDockSettingMSG.GES_SHOW_PHOTO :
				intent = new Intent(ICustomAction.ACTION_SHOW_PHOTO);
				info.mGestureInfo.mUpIntent = intent;
				break;

			case IDockSettingMSG.GES_SHOW_MUSIC :
				intent = new Intent(ICustomAction.ACTION_SHOW_MUSIC);
				info.mGestureInfo.mUpIntent = intent;
				break;

			case IDockSettingMSG.GES_SHOW_VIDEO :
				intent = new Intent(ICustomAction.ACTION_SHOW_VIDEO);
				info.mGestureInfo.mUpIntent = intent;
				break;

			default :
				break;
		}
		updateDockItem(info.mItemInfo.mInScreenId, info);
	}

	/***
	 * 文件夹中删除一项
	 * 
	 * @param folderInfo
	 * @param infos
	 */
	public void removeFolderItem(UserFolderInfo folderInfo, ArrayList<AppItemInfo> infos) {
		if (null == folderInfo || null == infos) {
			return;
		}

		for (AppItemInfo info : infos) {
			final int count = folderInfo.getChildCount();
			for (int i = 0; i < count; i++) {
				ItemInfo deskInfo = folderInfo.getChildInfo(i);
				if (null == deskInfo) {
					continue;
				}
				if (ConvertUtils.intentCompare(info.mIntent, ((ShortCutInfo) deskInfo).mIntent)) {
					removeDockFolderItem(deskInfo.mInScreenId, folderInfo.mInScreenId);
					folderInfo.remove(deskInfo);
				}
			}
		}
	}

	/***
	 * 文件夹中添加一项
	 * 
	 * @param folderInfo
	 * @param infos
	 */
	public void addItemToFolder(UserFolderInfo folderInfo, ArrayList<AppItemInfo> infos) {
		if (null == folderInfo || null == infos) {
			return;
		}

		for (AppItemInfo info : infos) {
			if (null != info) {
				ShortCutInfo item = new ShortCutInfo();
				item.mIcon = info.mIcon;
				item.mIntent = info.mIntent;
				item.mItemType = IItemType.ITEM_TYPE_APPLICATION;
				item.mSpanX = 1;
				item.mSpanY = 1;
				item.mTitle = info.mTitle;
				item.mInScreenId = System.currentTimeMillis();
				// 图标去重
				removeDockFolderItems(folderInfo, item);
				addItemToFolder(item, folderInfo);
			}
		}
	}

	/***
	 * 文件夹重命名
	 */
	@SuppressWarnings("rawtypes")
	public boolean folderRename(List objects, Object object) {
		if (null != object && object instanceof Long && null != objects) {
			long id = (Long) object;
			String name = (String) objects.get(0);
			ConcurrentHashMap<Integer, ArrayList<DockItemInfo>> itemHashMap = getShortCutItems();
			int hashmapSize = itemHashMap.size();
			for (int i = 0; i < hashmapSize; i++) {
				ArrayList<DockItemInfo> list = itemHashMap.get(i);
				int listSize = list.size();
				for (int j = 0; j < listSize; j++) {
					DockItemInfo info = list.get(j);
					if (null != info && null != info.mItemInfo
							&& info.mItemInfo instanceof UserFolderInfo
							&& info.mItemInfo.mInScreenId == id) {
						UserFolderInfo userFolderInfo = (UserFolderInfo) info.mItemInfo;
						userFolderInfo.setFeatureTitle(name);
						userFolderInfo.mTitle = name;
						updateDockItem(userFolderInfo.mInScreenId, info);
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 强制刷新文件内容、图标
	 * 
	 * @param info
	 */
	public void reloadFolderContent() {
		int size = getShortCutItems().size();
		for (int i = 0; i < size; i++) {
			ArrayList<DockItemInfo> infos = getShortCutItems().get(i);
			for (DockItemInfo dockItemInfo : infos) {
				final ItemInfo intemInfo = dockItemInfo.mItemInfo;
				if (null != intemInfo && intemInfo instanceof UserFolderInfo) {
					// 由于一些绑定的原因，这里直接重新设置初始化标志
					((UserFolderInfo) intemInfo).mContentsInit = false;
				}
			}
		}
	}

	/**
	 * 非自适应模式，添加空白
	 * 
	 * @param rowid
	 * @param indexinrow
	 * @return
	 */
	public boolean addBlank(int rowid, int indexinrow) {
		ArrayList<Integer> list = mBlanksHashMap.get(rowid);
		if (null != list) {
			boolean add = list.add(indexinrow);
			if (add) {
				return mDataModel.addBlank(rowid, indexinrow);
			}
		}
		return false;
	}

	/**
	 * 删除空白
	 * 
	 * @param rowid
	 * @param indexinrow
	 * @return
	 */
	private boolean delBlank(int rowid, int indexinrow) {
		ArrayList<Integer> list = getShortCutUnfitBlanks().get(rowid);
		if (null != list) {
			int size = list.size();
			boolean del = false;
			// 保证列表中所有此值都清除
			for (int i = 0; i < size; i++) {
				Integer value = list.get(i);
				if (value == indexinrow) {
					list.remove(i);
					i--;
					size--;
					del = true;
				}
			}

			if (del) {
				return mDataModel.delBlank(rowid, indexinrow);
			}
		}
		return false;
	}

	/**
	 * 用于3D插件 换程序，包括点击与手势，手势信息跟位置相关，所以换程序不会换手势
	 * 
	 * @param id
	 *            inscreenid
	 * @param info
	 *            更换信息
	 * @param type
	 *            类型定义：详见DockConstant
	 */
	public void changeAppForThreeD(DockItemInfo dockItemInfo, long id, ItemInfo itemInfo, int type) {
		if (dockItemInfo == null) {
			return;
		}
		Intent intent = null;
		ShortCutInfo info = null;

		if (itemInfo instanceof ShortCutInfo) {
			info = (ShortCutInfo) itemInfo;
			intent = ((ShortCutInfo) itemInfo).mIntent;
		}
		switch (type) {
			case DockUtil.CHANGE_FROM_DELETEFOLER :
				prepareItemInfo(info);
				dockItemInfo.setInfo(info);
				dockItemInfo.mUsePackage = null;

				// 保存数据
				updateDockItem(id, dockItemInfo);
				dockItemInfo.broadCast(DockItemInfo.INTENTCHANGED, 0, null, null);
				dockItemInfo.broadCast(DockItemInfo.ICONCHANGED, 0, null, null);
				break;

			case DockUtil.CHANGE_FROM_GESTURE :
				dockItemInfo.mGestureInfo.mUpIntent = intent;
				updateDockItem(id, dockItemInfo);
				break;

			case DockUtil.CHANGE_FROM_FOLDER :
				if (itemInfo instanceof UserFolderInfo) {
					// dockItemInfo.mimOpen=false;
					UserFolderInfo folderInfo = (UserFolderInfo) itemInfo;
					folderInfo.mOpened = false;
					dockItemInfo.setInfo(folderInfo);
					final int count = folderInfo.getChildCount();
					for (int i = 0; i < count; i++) {
						ShortCutInfo item = folderInfo.getChildInfo(i);
						if (null != item) {
							item.clearAllObserver();
							// item.registerObserver(dockItemInfo);
						}
						if (0 != folderInfo.mRefId) {
							prepareItemInfo(item);
						}
					}
				}
				// 保存数据
				updateDockItem(id, dockItemInfo);
				dockItemInfo.broadCast(DockItemInfo.INTENTCHANGED, 0, null, null);
				updateFolderIconAsync(dockItemInfo, false);
				break;
			default :
				break;
		}
	}
	/**
	 * 更新单个Dock图标
	 * 
	 * @param positionIndex
	 *            对应索引
	 * @param userIcontype
	 *            图标类型（资源、文件、默认）
	 * @param userIconid
	 *            资源ID
	 * @param userIconpath
	 *            文件路径
	 * @return 返回值
	 */
	public synchronized int updateShortCutItemIconForThreeD(DockItemInfo info, long id,
			int userIcontype, int userIconid, String userIconpackage, String userIconpath) {
		String packageStr = userIconpackage;
		if (null == packageStr) {
			packageStr = IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
		}
		return updateShortCutItemIconForThreeD(info, id, userIcontype, mDataModel.getThemeName1(),
				userIconid, packageStr, userIconpath);
	}

	public synchronized int updateShortCutItemIconForThreeD(DockItemInfo info, long id,
			int userIcontype, String usePackage, int userIconid, String userIconpackage,
			String userIconpath) {
		if (null == info) {
			return DockUtil.ERROR_BAD_PARAM;
		}
		// 插入数据库
		info.mItemInfo
				.setFeatureIcon(null, userIcontype, userIconpackage, userIconid, userIconpath);
		info.mItemInfo.prepareFeatureIcon();
		info.mUsePackage = usePackage;
		mDataModel.updateDockItem(id, info);

		return DockUtil.ERROR_NONE;
	}
	
	/**
	 * <br>功能简述: 获取对应Dock栏当前列数
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param rowId
	 * @return
	 */
	public int getColumnNum(int rowId) {
		int retval = DockUtil.ICON_COUNT_IN_A_ROW;
		ArrayList<DockItemInfo> infos = mDockInfosHashMap.get(rowId);
		if (infos != null) {
			retval = infos.size();
		}
		return retval;
	}
	
}
