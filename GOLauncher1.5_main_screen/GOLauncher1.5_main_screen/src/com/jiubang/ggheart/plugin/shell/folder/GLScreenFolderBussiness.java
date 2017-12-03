package com.jiubang.ggheart.plugin.shell.folder;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.ContentValues;
import android.database.Cursor;

import com.go.util.ConvertUtils;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.ItemInfo;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.data.info.UserFolderInfo;
import com.jiubang.ggheart.data.tables.FolderTable;
import com.jiubang.ggheart.data.tables.PartToScreenTable;
/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  dingzijian
 * @date  [2013-3-11]
 */
public class GLScreenFolderBussiness extends AbsFolderBussiness {

	public void moveFolderInnerItem(long folderID, ArrayList<ItemInfo> itemInfos)
			throws DatabaseException {
		int count = itemInfos.size();
		try {
			mDataModel.beginTransaction();
			try {
				for (int i = 0; i < count; i++) {
					mDataModel
							.moveScreenFolderInsideItem(folderID, itemInfos.get(i).mInScreenId, i);
				}
				mDataModel.setTransactionSuccessful();
			} finally {
				mDataModel.endTransaction();
			}
		} catch (Exception e) {
			if (e instanceof DatabaseException) {
				throw (DatabaseException) e;
			} else {
				throw new DatabaseException(e);
			}
		}
	}

	public void removeAppFromFolder(long mId, long folderId) throws DatabaseException {
		mDataModel.removeAppFromFolder(mId, folderId);
	}
	
	public void updateScreenItem(final UserFolderInfo info) {
		ContentValues values = new ContentValues();
		info.writeObject(values, PartToScreenTable.TABLENAME);
		mDataModel.updateScreenItem(info.mInScreenId, values);
	}
	
	public void updateFolderItem(long folderID, ItemInfo info) {
		ContentValues values = new ContentValues();
		info.writeObject(values, FolderTable.TABLENAME);
		mDataModel.updateFolderItem(folderID, info.mInScreenId, values);
	}
	
	public int getUserFolderCount(long folderId) {
		int count = 0;
		Cursor cursor = mDataModel.getScreenFolderItems(folderId);
		if (null != cursor) {
			count = cursor.getCount();
			cursor.close();
		}

		return count;
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
		mDataModel.addFolderItem(values);
	}
	/**
	 * 增加用户文件夹 将数据写入文件夹表 * 在此函数前必须addDesktopItem增加一个屏幕文件夹项
	 * 
	 * @param itemInfos
	 * @return
	 */
	public synchronized boolean addUserFolderContent(final long folderId,
			ArrayList<ItemInfo> itemInfos, boolean fromDrawer) {
		if (null == itemInfos) {
			return false;
		}
		int count = getUserFolderCount(folderId);
		int sz = itemInfos.size();
		for (int i = 0; i < sz; i++) {
			ItemInfo info = itemInfos.get(i);
			if (null == info) {
				continue;
			}
			info.mInScreenId = System.currentTimeMillis();
			addItemToFolder(info, folderId, i + count, fromDrawer);
		}
		return true;
	}
	public synchronized void removeItemFromFolder(ItemInfo info, long folderId, boolean fromDrawer) throws DatabaseException {
		if (info.mInScreenId != 0) {
			mDataModel.removeAppFromFolder(info.mInScreenId, folderId);
		} else {
			if (info instanceof ShortCutInfo) {
				mDataModel.removeAppFromFolder(((ShortCutInfo) info).mIntent, fromDrawer, folderId);
			}
		}
	}
	synchronized boolean removeUserFolderContent(final long folderId,
			ArrayList<ShortCutInfo> itemInfos, boolean fromDrawer) throws DatabaseException {
		if (null == itemInfos) {
			return false;
		}
		int sz = itemInfos.size();
		for (int i = 0; i < sz; i++) {
			ItemInfo info = itemInfos.get(i);
			if (null == info) {
				continue;
			}
			removeItemFromFolder(info, folderId, fromDrawer);
			// 移除时取出绑定
			info.selfDestruct();
		}
		return true;
	}
	

	
	@Override
	public ArrayList<GLAppFolderInfo> onFolderAppUninstall(ArrayList<AppItemInfo> uninstallapps) throws DatabaseException {
		ArrayList<GLAppFolderInfo> folderInfos = new ArrayList<GLAppFolderInfo>();
		for (AppItemInfo appItemInfo : uninstallapps) {
			Iterator<Long> iterator = mFolderInfos.keySet().iterator();
			while (iterator.hasNext()) {
				Long long1 = (Long) iterator.next();
				GLAppFolderInfo folderInfo = mFolderInfos.get(long1);
				ArrayList<ItemInfo> infos = folderInfo.getScreenFoIderInfo().getContents();
				for (ItemInfo info : infos) {
					ShortCutInfo shortCutInfo = (ShortCutInfo) info;
					if (ConvertUtils.intentCompare(shortCutInfo.mIntent, appItemInfo.mIntent)) {
						folderInfo.getScreenFoIderInfo().remove(shortCutInfo);
						mDataModel.removeAppFromFolder(shortCutInfo.mInScreenId,
								folderInfo.folderId);
						if (!folderInfos.contains(folderInfo)) {
							folderInfos.add(folderInfo);
						}
						break;
					}
				}
			}
		}
		return folderInfos;

	}
}
