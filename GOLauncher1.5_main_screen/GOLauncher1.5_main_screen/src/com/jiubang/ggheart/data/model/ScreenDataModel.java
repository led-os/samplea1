package com.jiubang.ggheart.data.model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;

import com.go.proxy.ApplicationProxy;
import com.go.util.ConvertUtils;
import com.jiubang.ggheart.data.AppCore;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.BaseDataModel;
import com.jiubang.ggheart.data.DataProvider;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.PersistenceManager;
import com.jiubang.ggheart.data.SysShortCutControler;
import com.jiubang.ggheart.data.info.ItemInfo;
import com.jiubang.ggheart.data.info.ItemInfoFactory;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.data.tables.FolderTable;
import com.jiubang.ggheart.data.tables.PartToScreenTable;
import com.jiubang.ggheart.data.tables.PartsTable;
import com.jiubang.ggheart.data.tables.ScreenTable;

/**
 * <br>
 * 类描述: <br>
 * 功能详细描述:
 */
public class ScreenDataModel extends BaseDataModel {
	private static final String LOG_TAG = "ScreenDataModelLog";
	private DataProvider mDataProvider;
	
	public ScreenDataModel(final Context context) {
		super(context, PersistenceManager.DB_ANDROID_HEART);
		mDataProvider = DataProvider.getInstance(context);
	}

	/**
	 * 
	 * @author huyong
	 * @return
	 */
	public void getScreenItems(SparseArray<ArrayList<ItemInfo>> itemInfoSparseArray) {
		// 找出所有屏幕
		ArrayList<Long> screenIDList = new ArrayList<Long>();
		Cursor cursor = getScreenIDList();
		if (null == cursor) {
			Log.i(LOG_TAG, "query screen index, data cursor is null");
			return;
		}
		// add begin rongjinsong 2011-06-21
		// this block add for fix getColumnIndex return -1
		// if cursor do not contain SCREENID,try 10 times
		int colIndex = cursor.getColumnIndex(ScreenTable.SCREENID);
		int nCount = 10;
		while (-1 == colIndex && 0 < nCount--) {

			cursor.close();
			SystemClock.sleep(50);
			cursor = getScreenIDList();
			if (null == cursor) {
				Log.i(LOG_TAG, "query screen index, data cursor is null");
				return;
			}
			colIndex = cursor.getColumnIndex(ScreenTable.SCREENID);
		}
		// if still can not get index ,throw IllegalStateException
		if (-1 == colIndex) {
			StringBuffer erroMsg = new StringBuffer("ColumName:");
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				erroMsg.append(cursor.getColumnName(i));
				erroMsg.append(";");
			}
			Log.i(LOG_TAG, erroMsg.toString());

		}
		// add end

		convertCursorToScreenId(cursor, screenIDList);
		if (screenIDList == null || screenIDList.size() <= 0) {
			Log.i(LOG_TAG, "no screen");
			return;
		}

		// 每个屏幕找到所有元素
		int sz = screenIDList.size();
		for (int index = 0; index < sz; index++) {
			ArrayList<ItemInfo> itemArray = new ArrayList<ItemInfo>();
			try {
				getScreenItems(screenIDList.get(index), itemArray);
			} catch (Exception e) {
			}
			itemInfoSparseArray.put(index, itemArray);
		}
	}

	public int getGoWidgetScreenIndexByWidgetId(int widgetId) {
		long screenId = getScreenIdByGowidgetId(widgetId);
		if (screenId != -1) {
			return getScreenIndexById(screenId);
		}

		return -1;
	}

	/**
	 * 
	 * @author huyong
	 * @param screenId
	 * @return
	 */
	public void getScreenItems(final long screenId, ArrayList<ItemInfo> itemArray) {
		Cursor itemCursor = getScreenItems(screenId);
		if (null != itemCursor) {
			convertCursorToItemInfo(itemCursor, itemArray);
		} else {
			Log.i(LOG_TAG, "get screen items, the data cursor is null");
		}
	}

	private void convertCursorToScreenId(final Cursor cursor, ArrayList<Long> screenList) {
		if (cursor.moveToFirst()) {
			do {
				int index = cursor.getColumnIndex(ScreenTable.SCREENID);
				if (-1 == index) {
					// masanbing 2011-05-31 11:24
					// 尝试解决数据库读取字段 -1 的问题
					// 中断次数 10
					// 中断主线程50ms
					int count = 10;
					long wait = 50;
					for (int i = 0; i < count; i++) {
						SystemClock.sleep(wait);
						index = cursor.getColumnIndex(ScreenTable.SCREENID);
						if (-1 != index) {
							break;
						}
					}
				}
				Long screenId = cursor.getLong(index);
				screenList.add(screenId);
			} while (cursor.moveToNext());
		}
		cursor.close();
	}

	private void convertCursorToItemInfo(final Cursor cursor, ArrayList<ItemInfo> itemArray) {
		if (cursor.moveToFirst()) {
			do {
				try {
					int itemType = cursor.getInt(cursor.getColumnIndex(PartToScreenTable.ITEMTYPE));
					ItemInfo info = ItemInfoFactory.createItemInfo(itemType);
					info.readObject(cursor, PartToScreenTable.TABLENAME);
					itemArray.add(info);
				} catch (Exception e) {
					// 获取元素过程中出现问题，将跳过继续获取下一个元素 -by Yugi 2012.9.6
				}
			} while (cursor.moveToNext());
		}
		cursor.close();
	}

	/*********************************** 屏幕操作 **************************************/
	/**
	 * 添加屏幕
	 * 
	 * @author huyong
	 * @param screenIndex
	 *            屏幕索引
	 * @return 新增的屏幕id
	 */
	public long addScreen(final int screenIndex) {
		ContentValues contentValues = new ContentValues();
		long screenId = System.currentTimeMillis();
		screenId += screenIndex;
		contentValues.put(ScreenTable.SCREENID, screenId);
		contentValues.put(ScreenTable.MINDEX, screenIndex);
		addScreen(contentValues);
		return screenId;
	}
	
	/**
	 * 新增一个屏幕
	 * 
	 * @author huyong
	 * @param values
	 */
	public void addScreen(ContentValues values) {
		try {
			mManager.insert(ScreenTable.TABLENAME, values);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 添加桌面组件
	 * 
	 * @author huyong
	 * @param values
	 */
	public void addItemToScreen(ContentValues values) {
		try {
			mManager.insert(PartToScreenTable.TABLENAME, values);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 移除指定屏幕
	 * 
	 * @author huyong
	 * @param screenIndex
	 */
	public void removeScreen(final int screenIndex) {
		// 级联更新操作
		// 1. 删除PartToScreen表
		// 2. 删除Screen表
		// 3. 更新表中index字段
		mManager.beginTransaction();
		try {
			long screenId = getScreenIDByIndex(screenIndex);
			String whereStr = PartToScreenTable.SCREENID + " = " + screenId;
			mManager.delete(PartToScreenTable.TABLENAME, whereStr, null);

			String table = ScreenTable.TABLENAME;
			whereStr = ScreenTable.MINDEX + " = " + screenIndex;
			mManager.delete(table, whereStr, null);

			String mindex = ScreenTable.MINDEX;
			String changeValue = " - 1";
			String sql = "update " + table + " set " + mindex + " = " + mindex + changeValue
					+ " where " + mindex + " >= " + screenIndex + ";";
			mManager.exec(sql);

			mManager.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			mManager.endTransaction();
		}
	}
	
	public void removeScreenContent(final int screenIndex) {
		long screenId = getScreenIDByIndex(screenIndex);
		String whereStr = PartToScreenTable.SCREENID + " = " + screenId;
		try {
			mManager.delete(PartToScreenTable.TABLENAME, whereStr, null);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	public long getScreenIDByIndex(final int screenIndex) {
		String table = ScreenTable.TABLENAME;
		String columns[] = { ScreenTable.SCREENID };
		String selection = ScreenTable.MINDEX + " = " + screenIndex;
		Cursor cursor = mManager.query(table, columns, selection, null, null);
		long screenId = -1;
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					screenId = cursor.getLong(cursor.getColumnIndex(ScreenTable.SCREENID));
				}
			} finally {
				cursor.close();
			}
		}
		return screenId;
	}

	/********************************* 屏幕元素操作 ************************************/
	/**
	 * 修改桌面元素
	 * 
	 * @param screenIndex
	 * @param itemInfo
	 */
	public void updateDesktopItem(final int screenIndex, final ItemInfo info) {
		long screenId = getScreenIDByIndex(screenIndex);
		if (screenId == -1) {
			Log.i(LOG_TAG, "update item position, can not find screen id from index");
			return;
		}
		ContentValues values = new ContentValues();
		info.writeObject(values, PartToScreenTable.TABLENAME);
		values.put(PartToScreenTable.SCREENID, screenId);
		updateScreen(info.mInScreenId, values);
	}

	public void updateDBItem(final ItemInfo info) {
		ContentValues values = new ContentValues();
		info.writeObject(values, PartToScreenTable.TABLENAME);
		updateScreen(info.mInScreenId, values);
	}

	public void updateFolderIndex(long folderID, ArrayList<ItemInfo> infos) {
		int count = infos.size();
		for (int i = 0; i < count; i++) {
			mDataProvider.updateFolderIndex(folderID, infos.get(i).mInScreenId, i);
		}
	}

	public void updateFolderItem(long folderID, ItemInfo info) {
		ContentValues values = new ContentValues();
		info.writeObject(values, FolderTable.TABLENAME);
		mDataProvider.updateFolderItem(folderID, info.mInScreenId, values);
	}

	/**
	 * 增加桌面元素
	 * 
	 * @param screenIndex
	 * @param itemInfo
	 */
	public void addDesktopItem(final int screenIndex, ItemInfo itemInfo) {
		long screenId = getScreenIDByIndex(screenIndex);
		if (screenId <= 0) {
			Log.i(LOG_TAG, "add desk item, the des screen id no exsit");
			return;
		}

		ContentValues values = new ContentValues();
		itemInfo.writeObject(values, PartToScreenTable.TABLENAME);
		values.put(PartToScreenTable.SCREENID, screenId);
		addItemToScreen(values);
	}

	/********************************* 文件夹操作 *************************************/
	/**
	 * 移动桌面元素到文件夹 1. 获取屏幕相关数据信息 2. 转化为文件夹项相关数据信息 3. 插入文件夹表 4. 从屏幕表删除
	 * 
	 * @param screenItemId
	 * @param folderId
	 * @param index
	 */
	// TODO 暂时不考虑拖到相应位子
	public void moveScreenItemToFolder(ItemInfo screenItem, long folderId, int index)
			throws Exception {
		ContentValues values = new ContentValues();
		screenItem.writeObject(values, FolderTable.TABLENAME);
		values.put(FolderTable.FOLDERID, folderId);
		int sz = mDataProvider.getSizeOfFolder(folderId);
		values.put(FolderTable.INDEX, sz);
		final long time = System.currentTimeMillis();
		values.put(FolderTable.TIMEINFOLDER, time);

		mDataProvider.addFolderItem(values);
		removeDesktopItem(screenItem.mInScreenId);
		if (screenItem instanceof ShortCutInfo) {
			((ShortCutInfo) screenItem).mTimeInFolder = time;
		}
	}

	/**
	 * 移动元素到文件夹 1. 获取屏幕相关数据信息 2. 转化为文件夹项相关数据信息 3. 插入文件夹表 4. 从屏幕表删除
	 * 
	 * @param screenItemId
	 * @param index
	 */
	public void addItemToFolder(ItemInfo itemInfo, long folderId) throws Exception {

		ContentValues values = new ContentValues();
		values.put(FolderTable.FOLDERID, folderId);
		boolean bTrans = itemInfoToFolderContentValues(itemInfo, values);
		if (!bTrans) {
			Log.i(LOG_TAG, "move screen to folder, tran the screen item info err");
			throw new Exception("move screen to folder, tran the screen item info err");
			// return;
		}
		int sz = mDataProvider.getSizeOfFolder(folderId);
		values.put(FolderTable.INDEX, sz);
		final long time = System.currentTimeMillis();
		values.put(FolderTable.TIMEINFOLDER, time);
		mDataProvider.addFolderItem(values);
		if (itemInfo instanceof ShortCutInfo) {
			((ShortCutInfo) itemInfo).mTimeInFolder = time;
		}
	}

	/**
	 * 移除桌面元素从文件夹 1. 获取文件夹表项信息 2. 转化为屏幕表信息 3. 插入屏幕表信息 4. 删除文件夹表信息
	 * 
	 * @param screenItemId
	 * @param folderId
	 * @param index
	 */
	public void moveScreenItemFromFolder(ItemInfo screenItem, long folderId) throws Exception {
		mDataProvider.removeAppFromFolder(screenItem.mInScreenId, folderId);
	}

	/**
	 * 增加文件夹项
	 * 
	 * @param folderId
	 * @param intent
	 */
	public void addItemToFolder(ItemInfo screenItem, long folderId, int index, boolean fromDrawer) {
		ContentValues values = new ContentValues();
		screenItem.writeObject(values, FolderTable.TABLENAME);
		values.put(FolderTable.FOLDERID, folderId);
		values.put(FolderTable.INDEX, index);
		int tempInt = fromDrawer ? 1 : 0;
		values.put(FolderTable.FROMAPPDRAWER, tempInt);
		final long time = System.currentTimeMillis();
		values.put(FolderTable.TIMEINFOLDER, time);
		if (screenItem instanceof ShortCutInfo) {
			((ShortCutInfo) screenItem).mTimeInFolder = time;
		}
		mDataProvider.addFolderItem(values);
	}

	/**
	 * 移除文件夹项
	 * 
	 * @param screenItemId
	 * @param folderId
	 */
	public void removeItemFromFolder(long screenItemId, long folderId) {
		mDataProvider.removeAppFromFolder(screenItemId, folderId);
	}

	public void removeItemFromFolder(Intent intent, boolean fromDrawer, long folderId) {
		mDataProvider.removeAppFromFolder(intent, fromDrawer, folderId);
	}

	/**
	 * 文件夹内部移动
	 * 
	 * @param folderId
	 * @param srcIndex
	 * @param desIndex
	 */
	public void moveFolderItem(long folderId, int srcIndex, int desIndex) {
		mDataProvider.moveFolderItem(folderId, srcIndex, desIndex);
	}

	/**
	 * 获取Folder列表
	 * 
	 * @param folderId
	 * @return
	 */
	public ArrayList<ItemInfo> getScreenFolderItems(long folderId, int count) {
		ArrayList<ItemInfo> folderItemList = new ArrayList<ItemInfo>();
		Cursor cursor = mDataProvider.getScreenFolderItems(folderId);
		convertCursorToFolderItem(cursor, folderItemList, count);
		return folderItemList;
	}

	public int getUserFolderCount(long folderId) {
		int count = 0;
		Cursor cursor = mDataProvider.getScreenFolderItems(folderId);
		if (null != cursor) {
			count = cursor.getCount();
			cursor.close();
		}

		return count;
	}

	public void removeUserFolder(long folderId) {
		mDataProvider.delScreenFolderItems(folderId);
	}

	private boolean itemInfoToFolderContentValues(ItemInfo itemInfo, ContentValues values) {
		boolean bRet = false;
		if (null != itemInfo) {
			ShortCutInfo myInfo = (ShortCutInfo) itemInfo;
			if (myInfo.mInScreenId == -1) {
				myInfo.mInScreenId = System.currentTimeMillis();
			}
			long id = myInfo.mInScreenId;
			String inttentStr = ConvertUtils.intentToString(myInfo.mIntent);
			int type = myInfo.mItemType;
			String userTitleStr = null;
			if (null != myInfo.mFeatureTitle) {
				userTitleStr = myInfo.mFeatureTitle.toString();
			} else if (null != myInfo.mTitle) {
				userTitleStr = myInfo.mTitle.toString();
			}
			int iconType = myInfo.mFeatureIconType;
			int iconId = myInfo.mFeatureIconId;
			String iconPackage = myInfo.mFeatureIconPackage;
			String iconPath = myInfo.mFeatureIconPath;

			values.put(FolderTable.ID, id);
			values.put(FolderTable.INTENT, inttentStr);
			values.put(FolderTable.TYPE, type);
			values.put(FolderTable.USERTITLE, userTitleStr);
			values.put(FolderTable.USERICONTYPE, iconType);
			values.put(FolderTable.USERICONID, iconId);
			values.put(FolderTable.USERICONPACKAGE, iconPackage);
			values.put(FolderTable.USERICONPATH, iconPath);
			bRet = true;
		}
		return bRet;
	}

	private void convertCursorToFolderItem(final Cursor cursor,
			ArrayList<ItemInfo> folderItemInfos, int count) {
		final AppDataEngine dataEngine = AppDataEngine.getInstance(ApplicationProxy.getContext());
		if (cursor == null || dataEngine == null) {
			return;
		}

		if (cursor.moveToFirst()) {
			int transCount = 0;
			do {
				int type = cursor.getInt(cursor.getColumnIndex(FolderTable.TYPE));
				ItemInfo info = ItemInfoFactory.createItemInfo(type);
				info.readObject(cursor, FolderTable.TABLENAME);

				folderItemInfos.add(info);

				// 个数判定
				transCount++;
				if (-1 != count) {
					if (transCount == count) {
						break;
					}
				}
			} while (cursor.moveToNext());
		}
		cursor.close();
	}

	public void cleanDesktopItem() {
		clearTable(PartToScreenTable.TABLENAME);
		clearTable(ScreenTable.TABLENAME);
	}
	
	/**
	 * 清除表中的所有数据
	 * 
	 * @param tableName
	 *            表名
	 */
	public void clearTable(String tableName) {
		String sql = "delete from " + tableName + "; ";
		try {
			mManager.exec(sql);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	/*** debug for lost icon **************************************/
	@Deprecated
	public void getScreenItems(final int index, ArrayList<ItemInfo> itemArray) {
		ArrayList<Long> screenIDList = new ArrayList<Long>();
		Cursor cursor = getScreenIDList();
		convertCursorToScreenId(cursor, screenIDList);
		long screenId = screenIDList.get(index);
		getScreenItems(screenId, itemArray);
	}

	/**
	 * 添加进快捷方式的表里
	 * 
	 * @param shortCutInfo
	 */
	public void writeToShortCutTable(ShortCutInfo shortCutInfo) {
		final SysShortCutControler shortCutControler = AppCore.getInstance()
				.getSysShortCutControler();
		if (null != shortCutControler) {
			// 添加保护，防止空指针的出现
			String itemNameString = shortCutInfo.mTitle == null ? null : shortCutInfo.mTitle
					.toString();
			BitmapDrawable itemIcon = (shortCutInfo.mIcon != null && shortCutInfo.mIcon instanceof BitmapDrawable)
					? (BitmapDrawable) shortCutInfo.mIcon
					: null;

			boolean bRet = shortCutControler.addSysShortCut(shortCutInfo.mIntent, itemNameString,
					itemIcon);
			if (!bRet) {
				Log.i(LOG_TAG, "add system shortcut is fail");
			}
		}
	}

	/**
	 * <br>功能简述:检查屏幕某个位置是否已被推荐图标占据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param recommedApps
	 * @param cellx
	 * @param celly
	 * @return
	 */
	boolean checkHasAddRecommendApp(ArrayList<ShortCutInfo> recommedApps, int cellx, int celly,
			int screen) {
		try {
			if (recommedApps != null && !recommedApps.isEmpty()) {
				for (ShortCutInfo info : recommedApps) {
					if (info.mCellX == cellx && info.mCellY == celly && info.mScreenIndex == screen) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	/**
	 * 通过屏幕screenId查找屏幕索引
	 * 
	 * @param screenId
	 * @return
	 */
	private int getScreenIndexById(final long screenId) {
		String table = ScreenTable.TABLENAME;
		String columns[] = { ScreenTable.MINDEX };
		String selection = ScreenTable.SCREENID + " = " + screenId;
		Cursor cursor = mManager.query(table, columns, selection, null, null);
		int screenIndex = -1;
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					screenIndex = cursor.getInt(cursor.getColumnIndex(ScreenTable.MINDEX));
				}
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				cursor.close();
			}
		}
		return screenIndex;
	}

	/**
	 * 返回屏幕信息
	 * 
	 * @return
	 */
	private Cursor getScreenIDList() {
		String table = ScreenTable.TABLENAME;
		String orderby = ScreenTable.MINDEX + " ASC";
		return mManager.query(table, null, null, null, orderby);

	}

	/**
	 * 获取所有组件信息
	 * 
	 * @author huyong
	 * @param itemInfos
	 */
	private Cursor getAllItems() {
		return mManager.query(PartsTable.TABLENAME, null, null, null, null);
		// convertCursorToItem(cursor, itemInfos);
	}

	/**
	 * 功能简述:屏幕上是否存在某个元素
	 * 功能详细描述:
	 * 注意:
	 * @param itemIntent
	 * @return
	 */
	private boolean isExistItemInScreen(final String itemIntent) {
		// 表名
		String table = PartToScreenTable.TABLENAME;
		// 返回一列数据，增加查询效率。
		String columns[] = { PartToScreenTable.ID };
		// 查询条件
		String selection = PartToScreenTable.INTENT + " = '" + itemIntent + "'";
		boolean isExist = false;
		try {
			Cursor cursor = mManager.query(table, columns, selection, null, null);
			if (cursor != null) {
				try {
					isExist = cursor.getCount() > 0;
				} finally {
					cursor.close();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return isExist;
	}

	/**
	 * 根据intent和widgetid来判断该数据项是否已存在
	 * 
	 * @author huyong
	 * @param itemIntent
	 * @param itemWidgetId
	 * @return 查找到的itemId，不存在则为-1
	 */
	private long isItemInTable(final String itemIntent, final int itemWidgetId) {
		// 表名
		String table = PartsTable.TABLENAME;
		// 返回一列数据，增加查询效率。
		String columns[] = { PartsTable.ID };
		// 查询条件
		String selection = PartsTable.INTENT + " = '" + itemIntent + "' and " + PartsTable.WIDGETID
				+ " = " + itemWidgetId;
		Cursor cursor = mManager.query(table, columns, selection, null, null);
		long itemId = -1;
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					itemId = cursor.getLong(cursor.getColumnIndex(PartsTable.ID));
				}
			} finally {
				cursor.close();
			}
		}
		return itemId;
	}

	/**
	 * 根据itemid来判断该数据项是否已存在
	 * 
	 * @author huyong
	 * @param itemIntent
	 * @param itemWidgetId
	 * @return
	 */
	private boolean isItemInTable(final Long itemId) {
		boolean result = false;
		String table = PartsTable.TABLENAME;
		String columns[] = { PartsTable.ID };
		// 查询条件
		String selection = PartsTable.ID + " = " + itemId;
		Cursor cursor = mManager.query(table, columns, selection, null, null);
		if (cursor != null) {
			try {
				result = cursor.getCount() > 0;
			} finally {
				cursor.close();
			}
		}
		return result;
	}

	/**
	 * 通过component在PartToScreenTable中查询桌面元素的ScreenID
	 * 
	 * @param component
	 * @return ScreenID
	 */
	private long getScreenIDByComponent(String component) {
		String table = PartToScreenTable.TABLENAME;
		String columns[] = { PartToScreenTable.ID };
		String selection = PartToScreenTable.INTENT + " LIKE '%" + component + "%'";
		Cursor cursor = mManager.query(table, columns, selection, null, null);
		long screenId = -1;
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					screenId = cursor.getLong(0);
				}
			} finally {
				cursor.close();
			}
		}
		return screenId;
	}

	/**
	 * 获取屏幕的表项
	 * 
	 * @param screenItemId
	 * @return
	 */
	private Cursor getScreenItem(long screenItemId) {
		String condition = PartToScreenTable.ID + " = " + screenItemId;
		return mManager.query(PartToScreenTable.TABLENAME, null, condition, null, null);
	}

	/**
	 * 通过GowidgetID查找添加到的那个屏幕的screenId
	 * 
	 * @param widgetId
	 * @return
	 */
	private long getScreenIdByGowidgetId(int widgetId) {
		String table = PartToScreenTable.TABLENAME;
		String columns[] = { PartToScreenTable.SCREENID };
		String selection = PartToScreenTable.WIDGETID + " = " + widgetId;
		Cursor cursor = mManager.query(table, columns, selection, null, null);
		long screenId = -1;
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					screenId = cursor.getLong(cursor.getColumnIndex(PartToScreenTable.SCREENID));
				}
			} catch (Exception e) {
			} finally {
				cursor.close();
			}
		}
		return screenId;
	}

	/**
	 * 
	 * @author huyong
	 * @param values
	 */
	private void updateScreen(final long desktopItemInScreenId, ContentValues values) {
		// TODO:where条件
		String whereStr = PartToScreenTable.ID + " = " + desktopItemInScreenId;
		try {
			mManager.update(PartToScreenTable.TABLENAME, values, whereStr, null);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	public void removeDesktopItem(final long desktopItemInScreenId) {
		String whereStr = PartToScreenTable.ID + " = " + desktopItemInScreenId;
		try {
			mManager.delete(PartToScreenTable.TABLENAME, whereStr, null);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 卸载程序后，删除桌面表中对应程序的数据
	 * 
	 * @author huyong
	 * @param itemId
	 */
	private void unInstallDesktopItem(final long itemId) {
		String whereStr = PartToScreenTable.PARTID + " = " + itemId;
		try {
			mManager.delete(PartToScreenTable.TABLENAME, whereStr, null);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取程序原生图片
	 * 
	 * @param itemInScreenId
	 * @return
	 */
	private Cursor getOriginalIcon(final long itemInScreenId) {
		String tables = PartToScreenTable.TABLENAME + ", " + PartsTable.TABLENAME;
		// 查询列数
		String columns[] = { PartsTable.INTENT, PartsTable.ICON };
		// 查询条件

		// 查询条件
		String condition = PartToScreenTable.ID + " = " + itemInScreenId + " and "
				+ PartToScreenTable.TABLENAME + "." + PartToScreenTable.PARTID + " = "
				+ PartsTable.TABLENAME + "." + PartsTable.ID;
		return mManager.queryCrossTables(tables, columns, condition, null, null);
	}

	// add by huyong 2011-03-17 for 清理桌面脏数据
	/**
	 * 判断桌面是否有脏数据
	 */
	public boolean isScreenDirtyData() {
		boolean result = false;
		String table = PartToScreenTable.TABLENAME;
		String columns[] = { PartToScreenTable.SCREENID };
		// 查询条件
		String groupBy = PartToScreenTable.SCREENID + ", " + PartToScreenTable.SCREENX + ", "
				+ PartToScreenTable.SCREENY;
		String having = "count(*) > 1";

		Cursor cursor = mManager.query(table, columns, null, null, groupBy, having, null);
		if (cursor != null) {
			try {
				result = cursor.getCount() > 0;
			} finally {
				cursor.close();
			}
		}
		if (result) {
			return result;
		}

		// 查询是否有screenid<0的脏数据.
		table = ScreenTable.TABLENAME;
		groupBy = ScreenTable.SCREENID;
		String screenidColumns[] = { ScreenTable.SCREENID };
		String selection = ScreenTable.SCREENID + " < 0";
		cursor = mManager.query(table, screenidColumns, selection, null, null, null, null);
		if (cursor != null) {
			try {
				result = cursor.getCount() > 0;
			} finally {
				cursor.close();
			}
		}
		if (result) {
			return result;
		}

		// TODO:查询是否有screenIndex不连续的脏数据
		table = ScreenTable.TABLENAME;
		String screenIndexColumns[] = { ScreenTable.MINDEX };
		String sortOrder = ScreenTable.MINDEX + " DESC";
		cursor = mManager.query(table, screenIndexColumns, null, null, null, null, sortOrder);
		if (cursor != null) {
			try {
				int count = cursor.getCount();
				if (cursor.moveToFirst()) {
					int maxIndex = cursor.getInt(0);
					if (maxIndex >= count) {
						result = true;
					}
				}
			} finally {
				cursor.close();
			}
		}

		return result;
	}

	/**
	 * 清理桌面表中脏数据
	 * 
	 * @author huyong
	 */
	public void clearScreenDirtyData() {
		// TODO:不采取hardcode
		String sql = "delete from parttoscreen where itemInScreenId in ( "
				+ "select itemInScreenId from parttoscreen inner join"
				+ "(select screenid, screenx, screeny from parttoscreen "
				+ "group by screenx, screeny, screenid having count(*)>1)" + " as T "
				+ "on parttoscreen.screenid = T.screenid "
				+ "and parttoscreen.screenx = T.screenx " + "and parttoscreen.screeny = T.screeny "
				+ " )";
		try {
			mManager.exec(sql);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		mManager.beginTransaction();
		try {
			// 清理screenid为负数的脏数据
			sql = "update screen set screenid = -screenid where screenid < 0;";
			String column = ScreenTable.SCREENID;
			sql = "update " + ScreenTable.TABLENAME + " set " + column + " = -" + column
					+ " where " + column + " < 0;";
			mManager.exec(sql);

			column = PartToScreenTable.SCREENID;
			sql = "update parttoscreen set screenid = -screenid where screenid < 0;";
			sql = "update " + PartToScreenTable.TABLENAME + " set " + column + " = -" + column
					+ " where " + column + " <0;";
			mManager.exec(sql);

			mManager.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			mManager.endTransaction();
		}

		// 清理screenindx不连续的问题
		String tableName = ScreenTable.TABLENAME;
		String projection[] = { ScreenTable.SCREENID };
		String sortOrder = ScreenTable.MINDEX + " ASC";
		ArrayList<Long> screenIdList = new ArrayList<Long>();
		Cursor cursor = mManager.query(tableName, projection, null, null, sortOrder);
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					do {
						Long screenId = cursor.getLong(0);
						screenIdList.add(screenId);
					} while (cursor.moveToNext());
				}
			} finally {
				cursor.close();
			}
		}

		int screenSize = screenIdList.size();
		String updateSql = "update " + tableName + " set " + ScreenTable.MINDEX + " = ";
		String tmpSql = null;
		String where = " where " + ScreenTable.SCREENID + " = ";
		for (int i = 0; i < screenSize; i++) {
			tmpSql = updateSql + i + where + screenIdList.get(i);
			try {
				mManager.exec(tmpSql);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 移动屏幕位置
	 * 
	 * @author huyong
	 * @param srcScreenIndex
	 *            屏幕源索引
	 * @param DescScreenIndex
	 *            屏幕目标索引
	 */
	public void moveScreen(final int srcScreenIndex, final int DescScreenIndex) {
		String add = srcScreenIndex > DescScreenIndex ? " + 1 " : " - 1 ";
		String comp1 = srcScreenIndex > DescScreenIndex ? " >= " : " <= ";
		String comp2 = srcScreenIndex > DescScreenIndex ? " < " : " > ";

		String table = ScreenTable.TABLENAME;
		String mindex = ScreenTable.MINDEX;
		String sql = "update " + table + " set " + mindex + " = " + " -1" + " where " + mindex
				+ " = " + srcScreenIndex + ";";

		mManager.beginTransaction();
		try {
			// 先更新成-1，方便查找更新。
			mManager.exec(sql);
			sql = "update " + table + " set " + mindex + " = " + mindex + add + " where " + mindex
					+ comp1 + DescScreenIndex + " and " + mindex + comp2 + srcScreenIndex + ";";
			mManager.exec(sql);
			sql = " update " + table + " set " + mindex + " = " + DescScreenIndex + " where "
					+ mindex + " = " + " -1" + ";";
			mManager.exec(sql);

			mManager.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			mManager.endTransaction();
		}
	}

	/**
	 * 更新屏幕后的索引值(仅仅更新屏幕索引值)
	 * 
	 * @author huyong
	 * @param screenIndex
	 *            要更新的屏幕索引位置
	 * @param isAddIndex
	 *            是否是增加屏幕索引
	 */
	private void updateScreenIndex(final int screenIndex, final boolean isAddIndex) {
		String table = ScreenTable.TABLENAME;
		String mindex = ScreenTable.MINDEX;
		String changeValue = null;
		if (isAddIndex) {
			changeValue = " + 1";
		} else {
			changeValue = " - 1";
		}
		String sql = "update " + table + " set " + mindex + " = " + mindex + changeValue
				+ " where " + mindex + " >= " + screenIndex + ";";
		try {
			mManager.exec(sql);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isNewDB() {
		return mManager.isNewDB();
	}
	
	/**
	 * 获取所有桌面组件信息
	 * 
	 * @author huyong
	 * @param desktopItemInfos
	 */
	public Cursor getScreenItems(final long screenId) {
		String tables = PartToScreenTable.TABLENAME;
		// 查询列数
		String columns[] = { PartToScreenTable.ID, PartToScreenTable.SCREENID,
				PartToScreenTable.PARTID, PartToScreenTable.SCREENX,
				PartToScreenTable.SCREENY,
				PartToScreenTable.SPANX,
				PartToScreenTable.SPANY,
				PartToScreenTable.USERTITLE,
				// PartToScreenTable.USERICON,
				PartToScreenTable.ITEMTYPE,
				PartToScreenTable.WIDGETID,
				PartToScreenTable.INTENT,
				PartToScreenTable.URI,
				// PartToScreenTable.FOLDERID,
				// PartToScreenTable.FOLDERINDEX,
				PartToScreenTable.USERICONTYPE, PartToScreenTable.USERICONID,
				PartToScreenTable.USERICONPACKAGE, PartToScreenTable.USERICONPATH,
				PartToScreenTable.SORTTYPE, PartToScreenTable.FOLDERTYPE };
		// 查询条件
		// TODO:需要修改
		// String condition = PartToScreenTable.SCREENID + " = " + screenId +
		// " and " + PartToScreenTable.FOLDERID + " <= 0";
		String condition = PartToScreenTable.SCREENID + " = " + screenId;

		// 查询
		return mManager.query(tables, columns, condition, null, null);
	}
	
	/**
	 * 获取屏幕数量
	 * 
	 * @return 屏幕数量
	 */
	public int getScreenCount() {
		String tableName = ScreenTable.TABLENAME;
		Cursor cursor = mManager.query(tableName, null, null, null, null);
		if (null == cursor) {
			return 0;
		}
		int count = 0;
		try {
			count = cursor.getCount();
		} finally {
			cursor.close();
		}
		return count;
	}
}
