package com.jiubang.ggheart.apps.appfunc.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.go.util.ConvertUtils;
import com.jiubang.ggheart.data.BaseDataModel;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.PersistenceManager;
import com.jiubang.ggheart.data.tables.AppTable;
import com.jiubang.ggheart.data.tables.FolderTable;
import com.jiubang.ggheart.data.tables.PartToScreenTable;
import com.jiubang.ggheart.data.tables.ShortcutTable;

/**
 * 文件夹数据库访问处理
 * @author wuziyi
 *
 */
public class FolderDataModel extends BaseDataModel {

	public FolderDataModel(Context context) {
		super(context, PersistenceManager.DB_ANDROID_HEART);
	}

	/**
	 * 取得文件夹中的元素
	 * 
	 * @param folderId
	 *            文件夹id
	 * @return 游标
	 */
	public Cursor getAppsInFolder(final long folderId) {
		// 表名
		String tables = FolderTable.TABLENAME;
		// 查询列数
		String columns[] = { FolderTable.INTENT, FolderTable.INDEX, FolderTable.USERTITLE,
				FolderTable.TIMEINFOLDER };

		// 排序条件
		String sortOrder = FolderTable.INDEX + " ASC";
		String where = FolderTable.FOLDERID + " = " + folderId;

		return mManager.query(tables, columns, where, null, sortOrder);

	}
	
	/**
	 * 在文件夹中删除一个程序
	 * 
	 * @param folderId
	 *            文件夹id
	 * @param intent
	 * @throws DatabaseException
	 */
	public void removeFunAppFromFolder(final long folderId, final Intent intent)
 {
		if (null == intent) {
			return;
		}

		// 获取index
		int index = getFunAppIndexByIntentInFolder(folderId, intent);
		if (index < 0) {
			// TODO:打日志
			return;
		}
		// 删除
		String str = ConvertUtils.intentToString(intent);
		String selection = FolderTable.FOLDERID + " = " + folderId + " and " + FolderTable.INTENT
				+ " = " + "'" + str + "'";
		mManager.beginTransaction();
		// 删除元素
		try {
			mManager.delete(FolderTable.TABLENAME, selection, null);
			// 维护index
			String updateSql = "update " + FolderTable.TABLENAME + " set " + FolderTable.INDEX
					+ " = " + FolderTable.INDEX + " - 1 " + " where " + FolderTable.FOLDERID
					+ " = " + folderId + " and " + FolderTable.INDEX + " > " + index + ";";
			mManager.exec(updateSql);
			mManager.setTransactionSuccessful();

		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			mManager.endTransaction();
		}
	}
	
	/**
	 * 获取应用在文件夹中的index
	 * @param folderId
	 * @param intent
	 * @return
	 */
	private int getFunAppIndexByIntentInFolder(final long folderId, final Intent intent) {
		if (null == intent) {
			return -1;
		}

		String str = ConvertUtils.intentToString(intent);
		String selection = FolderTable.FOLDERID + " = " + folderId + " and " + FolderTable.INTENT
				+ " = " + "'" + str + "'";

		// 查询列数
		String columns[] = { FolderTable.INDEX };

		int retIndex = -1;
		Cursor cursor = mManager.query(FolderTable.TABLENAME, columns, selection, null, null);
		if (null != cursor) {
			try {
				if (cursor.moveToFirst()) {
					int idx = cursor.getColumnIndex(FolderTable.INDEX);
					retIndex = cursor.getInt(idx);
				}
			} finally {
				cursor.close();
			}
		}

		return retIndex;
	}
	
	/**
	 * 获取文件夹中元素个数
	 * 
	 * @param folderId
	 *            文件夹id
	 * @return 元素个数
	 */
	public int getSizeOfFolder(final long folderId) {
		String table = FolderTable.TABLENAME;
		String where = FolderTable.FOLDERID + " = " + folderId;
		Cursor cursor = mManager.query(table, null, where, null, null);
		int count = 0;
		if (cursor != null) {
			try {
				count = cursor.getCount();
			} finally {
				cursor.close();
			}
		}
		return count;
	}
	
	/**
	 * 更新
	 * 
	 * @param folderId
	 *            文件夹id
	 * @param values
	 *            新值
	 * @throws DatabaseException
	 */
	public void updateFunAppItem(final long folderId, final ContentValues values)
			throws DatabaseException {
		String whereStr = AppTable.FOLDERID + " = " + folderId;
		mManager.update(AppTable.TABLENAME, values, whereStr, null);
	}
	
	/**
	 * 在文件夹中添加一个程序,并维护index
	 * 
	 * @param folderId
	 *            文件夹id
	 * @param index
	 *            插入位置
	 * @param intent
	 *            程序唯一标识
	 * @throws DatabaseException
	 */
	public void addFunAppToFolder(final long folderId, final int index, final Intent intent,
			final String title, long timeInFolder) throws DatabaseException {
		if (null == intent) {
			return;
		}
		if (index < 0) {
			return;
		}

		//		// 获取folderId对于的文件夹的size
		//		int size = getSizeOfFolder(folderId);

		// 维护index
		String updateSql = "update " + FolderTable.TABLENAME + " set " + FolderTable.INDEX + " = "
				+ FolderTable.INDEX + " + 1 " + " where " + FolderTable.FOLDERID + " = " + folderId
				+ " and " + FolderTable.INDEX + " >= " + index + ";";
		mManager.exec(updateSql);

		//		// 处理特殊情况
		//		int idx = index > size ? size : index;
		// 添加
		ContentValues contentValues = new ContentValues();
		contentValues.put(FolderTable.FOLDERID, folderId);
		contentValues.put(FolderTable.INDEX, index);
		contentValues.put(FolderTable.INTENT, ConvertUtils.intentToString(intent));
		contentValues.put(FolderTable.USERTITLE, title);
		contentValues.put(FolderTable.TIMEINFOLDER, timeInFolder);
		mManager.insert(FolderTable.TABLENAME, contentValues);
	}
	
	/**
	 * 文件夹内部移动
	 * 
	 * @param folderId
	 * @param srcIndex
	 * @param desIndex
	 */
	public boolean moveDrawerFolderInsideItem(long folderId, int srcIndex, int desIndex)
			throws DatabaseException {
		boolean result = false;
		String add = srcIndex > desIndex ? " + 1 " : " - 1 ";
		String comp1 = srcIndex > desIndex ? " >= " : " <= ";
		String comp2 = srcIndex > desIndex ? " < " : " > ";

		String table = FolderTable.TABLENAME;
		String mindex = FolderTable.INDEX;
		String addCondition = " and " + FolderTable.FOLDERID + " = " + folderId;

		try {
			beginTransaction();
			try {
				String sql = "update " + table + " set " + mindex + " = " + " -1" + " where "
						+ mindex + " = " + srcIndex + addCondition + ";";
				// 先更新成-1，方便查找更新。
				mManager.exec(sql);
				sql = "update " + table + " set " + mindex + " = " + mindex + add + " where "
						+ mindex + comp1 + desIndex + " and " + mindex + comp2 + srcIndex
						+ addCondition + ";";
				mManager.exec(sql);
				sql = " update " + table + " set " + mindex + " = " + desIndex + " where " + mindex
						+ " = " + " -1" + addCondition + ";";
				mManager.exec(sql);

				setTransactionSuccessful();
				result = true;
			} finally {
				endTransaction();
			}
		} catch (Exception e) {
			result = false;
			if (e instanceof DatabaseException) {
				throw (DatabaseException) e;
			} else {
				throw new DatabaseException(e);
			}
		}
		return result;
	}
	
	public void moveScreenFolderInsideItem(long folderID, long itemID, int index) {
//		beginTransaction();
		// TODO:更新索引
		String table = FolderTable.TABLENAME;
		String mindex = FolderTable.INDEX;

		String selection = FolderTable.FOLDERID + " = " + folderID + " and " + FolderTable.ID
				+ " = " + itemID;

		String sql = "update " + table + " set " + mindex + " = " + index + " where " + selection
				+ ";";
		try {
			mManager.exec(sql);
//			setTransactionSuccessful();
		} catch (DatabaseException e) {
			e.printStackTrace();
		} finally {
//			try {
//				endTransaction();
//			} catch (DatabaseException e) {
//				e.printStackTrace();
//			}
		}
	}
	/**
	 * 屏幕文件夹操作：从文件夹删除项
	 * 
	 * @param screenItemId
	 * @param folderId
	 */
	public void removeAppFromFolder(long screenItemId, long folderId) throws DatabaseException {
		int index = getFolderItemIndex(screenItemId, folderId);
		if (index < 0) {
			// TODO LOG 异常
			return;
		}

		try {
			mManager.beginTransaction();
			try {
				// 删除
				String sql = "delete from " + FolderTable.TABLENAME + " where "
						+ FolderTable.FOLDERID + " = " + folderId + " and " + FolderTable.ID
						+ " = " + screenItemId;
				mManager.exec(sql);

				// 维护index
				String updateSql = "update " + FolderTable.TABLENAME + " set " + FolderTable.INDEX
						+ " = " + FolderTable.INDEX + " - 1 " + " where " + FolderTable.FOLDERID
						+ " = " + folderId + " and " + FolderTable.INDEX + " > " + index + ";";
				mManager.exec(updateSql);
				mManager.setTransactionSuccessful();
			} finally {
				mManager.endTransaction();
			}
		} catch (Exception e) {
			if (e instanceof DatabaseException) {
				throw (DatabaseException) e;
			} else {
				throw new DatabaseException(e);
			}
		}
	}
	
	public void removeAppFromFolder(Intent intent, boolean fromDrawer, long folderId)
			throws DatabaseException {
		int index = getFolderItemIndex(intent, fromDrawer, folderId);
		if (index < 0) {
			// TODO LOG 异常
			return;
		}
		try {
			mManager.beginTransaction();
			try {
				// 删除
				String sql = "delete from " + FolderTable.TABLENAME + " where "
						+ FolderTable.FOLDERID + " = " + folderId + " and "
						+ FolderTable.FROMAPPDRAWER + " = " + (fromDrawer ? 1 : 0) + " and "
						+ FolderTable.INTENT + " = " + "'" + ConvertUtils.intentToString(intent)
						+ "'";
				mManager.exec(sql);

				// 维护index
				String updateSql = "update " + FolderTable.TABLENAME + " set " + FolderTable.INDEX
						+ " = " + FolderTable.INDEX + " - 1 " + " where " + FolderTable.FOLDERID
						+ " = " + folderId + " and " + FolderTable.INDEX + " > " + index + ";";
				mManager.exec(updateSql);
				mManager.setTransactionSuccessful();
			} finally {
				mManager.endTransaction();
			}
		} catch (Exception e) {
			if (e instanceof DatabaseException) {
				throw (DatabaseException) e;
			} else {
				throw new DatabaseException(e);
			}
		}
	}
	
	public int getFolderItemIndex(Intent intent, boolean fromDrawer, long folderId) {
		String selection = FolderTable.FOLDERID + " = " + folderId + " and "
				+ FolderTable.FROMAPPDRAWER + " = " + (fromDrawer ? 1 : 0) + " and "
				+ FolderTable.INTENT + " = " + "'" + ConvertUtils.intentToString(intent) + "'";

		// 查询列数
		String columns[] = { FolderTable.INDEX };
		int retIndex = -1;
		Cursor cursor = mManager.query(FolderTable.TABLENAME, columns, selection, null, null);
		if (null != cursor) {
			try {
				if (cursor.moveToFirst()) {
					int idx = cursor.getColumnIndex(FolderTable.INDEX);
					retIndex = cursor.getInt(idx);
				}
			} finally {
				cursor.close();
			}
		}

		return retIndex;
	}
	/**
	 * 屏幕文件夹操作：通过ItemID获取索引值
	 * 
	 * @param screenItemId
	 * @param folderId
	 * @return
	 */
	public int getFolderItemIndex(long screenItemId, long folderId) {
		String selection = FolderTable.FOLDERID + " = " + folderId + " and " + FolderTable.ID
				+ " = " + screenItemId;

		// 查询列数
		String columns[] = { FolderTable.INDEX };

		int retIndex = -1;
		Cursor cursor = mManager.query(FolderTable.TABLENAME, columns, selection, null, null);
		if (null != cursor) {
			try {
				if (cursor.moveToFirst()) {
					int idx = cursor.getColumnIndex(FolderTable.INDEX);
					retIndex = cursor.getInt(idx);
				}
			} finally {
				cursor.close();
			}
		}

		return retIndex;
	}
	/**
	 * 
	 * @author huyong
	 * @param values
	 */
	public void updateScreenItem(final long desktopItemInScreenId, ContentValues values) {
		// TODO:where条件
		String whereStr = PartToScreenTable.ID + " = " + desktopItemInScreenId;
		mManager.beginTransaction();
		try {
			mManager.update(PartToScreenTable.TABLENAME, values, whereStr, null);
			mManager.setTransactionSuccessful();
		} catch (DatabaseException e) {
			e.printStackTrace();

		} finally {
			mManager.endTransaction();
		}
	}
	
	/**
	 * 更新指定文件夹内指定一项的值
	 * 
	 * @param folderID
	 * @param itemID
	 * @param values
	 */
	public void updateFolderItem(long folderID, long itemID, ContentValues values) {
		try {
			String selection = FolderTable.FOLDERID + " = " + folderID + " and " + FolderTable.ID
					+ " = " + itemID;
			mManager.update(FolderTable.TABLENAME, values, selection, null);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	public void updateShortCutItem(final long id, final ContentValues values, final String themeName) {
		if (values == null) {
			return;
		}
		String tableName = ShortcutTable.TABLENAME;
		String selection = ShortcutTable.PARTID + " = " + id + " and " + ShortcutTable.THEMENAME
				+ " = " + "'" + themeName + "'";
		mManager.beginTransaction();
		try {
			mManager.update(tableName, values, selection, null);
			mManager.setTransactionSuccessful();
		} catch (DatabaseException e) {
			e.printStackTrace();
		} finally {
			mManager.endTransaction();
		}
	}

	/**
	 * 清除文件夹中元素数据
	 * 
	 * @param
	 * @throws DatabaseException
	 */
	public void clearFolderAppItems(final long folderId) throws DatabaseException {
		String sql = "delete from " + FolderTable.TABLENAME + " where " + FolderTable.FOLDERID
				+ " = " + folderId + "; ";
		mManager.exec(sql);
	}
	/**
	 * 更新元素
	 * 
	 * @param intent
	 *            唯一标识Intent
	 * @param values
	 *            新值
	 * @throws DatabaseException
	 */
	public void updateAppDrawerFolderIndex(final long folderId, final Intent intent, final int index)
			throws DatabaseException {
		if (null == intent) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put(FolderTable.INDEX, index);
		String whereStr = FolderTable.FOLDERID + " = " + folderId + " and " + FolderTable.INTENT
				+ " = " + "'" + ConvertUtils.intentToString(intent) + "'";
		mManager.update(FolderTable.TABLENAME, values, whereStr, null);
	}
	
	/**
	 * 遍历文件夹得所有项
	 * 
	 * @param folderId
	 * @return
	 */
	public Cursor getScreenFolderItems(long folderId) {
		String condition = FolderTable.FOLDERID + " = " + folderId;
		String orderby = FolderTable.INDEX + " ASC";
		return mManager.query(FolderTable.TABLENAME, null, condition, null, orderby);
	}
	/**
	 * 在文件夹表中添加一个程序
	 * 
	 * @param values
	 * @throws DatabaseException
	 */
	public void addFolderItem(ContentValues values) {
		try {
			mManager.insert(FolderTable.TABLENAME, values);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
	
}
