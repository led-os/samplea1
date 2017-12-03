package com.jiubang.ggheart.apps.appfunc.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.go.util.ConvertUtils;
import com.jiubang.ggheart.data.BaseDataModel;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.PersistenceManager;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.FunAppItemInfo;
import com.jiubang.ggheart.data.info.FunFolderItemInfo;
import com.jiubang.ggheart.data.info.FunItemInfo;
import com.jiubang.ggheart.data.tables.AppDrawerFolderTableBackup;
import com.jiubang.ggheart.data.tables.AppTable;
import com.jiubang.ggheart.data.tables.AppTableBackup;
import com.jiubang.ggheart.data.tables.FolderTable;

/**
 * 
 * <br>类描述: 所有程序数据管理器
 * <br>功能详细描述: 封装所有程序模块访问数据的操作
 * 
 * @author  yangguanxiang
 * @date  [2012-12-27]
 */
public class AllAppDataModel extends BaseDataModel {

	public AllAppDataModel(Context context) {
		super(context, PersistenceManager.DB_ANDROID_HEART);
	}

	/**
	 * 获取功能表元素数据
	 * 
	 * @return
	 */
	public Cursor getFunItems() {
		// 表名
		String tables = AppTable.TABLENAME;
		// 查询列数
		String columns[] = { AppTable.INDEX, AppTable.INTENT, AppTable.FOLDERID, AppTable.TITLE,
				AppTable.FOLDERTYPE };
		// 排序条件
		String sortOrder = AppTable.INDEX + " ASC";

		return mManager.query(tables, columns, null, null, sortOrder);
	}
	
	/**
	 * 获取功能表一个元素数据
	 * @param intent
	 * @return
	 */
	public Cursor getFunItem(final Intent intent) {
		if (null == intent) {
			return null;
		}
		// 表名
		String tables = AppTable.TABLENAME;
		// 查询列数
		String columns[] = { AppTable.INDEX };
		// 排序条件
		String where = AppTable.INTENT + " = " + "'" + ConvertUtils.intentToString(intent) + "'";

		return mManager.query(tables, columns, where, null, null);
	}
	
	/**
	 * 获取功能表根目录元素个数
	 * 
	 * @return 个数
	 */
	public int getSizeOfApps() {
		String table = AppTable.TABLENAME;
		Cursor cursor = mManager.query(table, null, null, null, null);
		int count = -1;
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
	 * 获取元素在功能表根目录中的索引
	 * 
	 * @param intent
	 *            唯一标识Intent
	 * @return 索引
	 */
	public int getFunItemIndex(final Intent intent) {
		Cursor cursor = getFunItem(intent);
		int idx = -1;
		if (null != cursor) {
			try {
				final int idxIndex = cursor.getColumnIndex(AppTable.INDEX);
				if (cursor.moveToFirst()) {
					idx = cursor.getInt(idxIndex);
				}
			} finally {
				cursor.close();
			}
		}
		return idx;
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
	public void updateFunItem(final long folderId, final ContentValues values)
			throws DatabaseException {
		String whereStr = AppTable.FOLDERID + " = " + folderId;
		mManager.update(AppTable.TABLENAME, values, whereStr, null);
	}
	
	/**
	 * 更新元素
	 * 注意：不会维护index
	 * 
	 * @param intent
	 *            唯一标识Intent
	 * @param values
	 *            新值
	 * @throws DatabaseException
	 */
	public void updateFunItem(final Intent intent, final ContentValues values)
			throws DatabaseException {
		String whereStr = AppTable.INTENT + " = " + "'"	+ ConvertUtils.intentToString(intent) + "'";
		mManager.update(AppTable.TABLENAME, values, whereStr, null);
	}
	
	/**
	 * 更新元素
	 * 
	 * @param values
	 * @throws DatabaseException
	 */
	public void updateFunItem(ContentValues values) throws DatabaseException {
		mManager.update(AppTable.TABLENAME, values, null, null);
	}
	
	/**
	 * 添加功能表元素（添加后会自动维护数据库index）
	 * 注意：数据库操作，没有排重保护，可以添加重复数据
	 * 
	 * @param funItemInfo
	 * @throws DatabaseException
	 */
	public void addFunItem(FunItemInfo funItemInfo) throws DatabaseException {
		if (null == funItemInfo) {
			return;
		}
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppTable.INDEX, funItemInfo.getIndex());

		if (funItemInfo instanceof FunFolderItemInfo) {
			// TODO:统一intent.toUri(0)
			FunFolderItemInfo folderItemInfo = (FunFolderItemInfo) funItemInfo;
			contentValues.put(AppTable.INTENT, funItemInfo.getIntent().toUri(0));
			contentValues.put(AppTable.FOLDERID, funItemInfo.getFolderId());
			contentValues.put(AppTable.TITLE, funItemInfo.getTitle());
			contentValues.put(AppTable.FOLDERTYPE, folderItemInfo.getFolderType());
		} else {
			// 不是文件夹，使用真正的intent
			AppItemInfo appItemInfo = ((FunAppItemInfo) funItemInfo).getAppItemInfo();
			if (null == appItemInfo) {
				return;
			}
			// TODO:统一intent.toUri(0)
			Intent intent = appItemInfo.mIntent;
			if (null == intent) {
				return;
			}
			String title = appItemInfo.mTitle;
			contentValues.put(AppTable.INTENT, intent.toUri(0));
			contentValues.put(AppTable.TITLE, title);
		}
		addFunItem(contentValues);
	}
	
	/**
	 * 添加一个元素，以ContentValues的index为添加位置，若越界则添加到末尾
	 * 
	 * @param values
	 * @throws DatabaseException
	 */
	private void addFunItem(ContentValues values) throws DatabaseException {
		// 更新index
		int index = values.getAsInteger(AppTable.INDEX);
		// 获取size, -1表示查询失败
//		int size = getSizeOfApps();

		// 维护index
		String updateSql = "update " + AppTable.TABLENAME + " set "
				+ AppTable.INDEX + " = " + AppTable.INDEX + " + 1 " + " where "
				+ FolderTable.INDEX + " >= " + index + ";";
		mManager.exec(updateSql);

//		int idx = (index > size && size != -1) ? size : index;
		// 添加
//		values.put(AppTable.INDEX, idx);
		mManager.insert(AppTable.TABLENAME, values);
	}
	
	public void clearFunItem() throws DatabaseException {
		String sql = "delete from " + AppTable.TABLENAME + "; ";
		mManager.exec(sql);
	}
	
	/**
	 * 根据intent删除对应的元素，并维护数据库中的mIndex
	 * 
	 * @param intent
	 * @throws DatabaseException
	 */
	public void removeFunItem(Intent intent) throws DatabaseException {
		if (intent == null) {
			return;
		}
		int index = getFunItemIndex(intent);
		if (index < 0) {
			return;
		}
		String str = ConvertUtils.intentToString(intent);
		String selection = AppTable.INTENT + " = " + "'" + str + "'";
		// 删除元素
		mManager.delete(AppTable.TABLENAME, selection, null);
		// 更新mItemInAppIndex
		String sql = "update application set " + AppTable.INDEX + " = "
				+ AppTable.INDEX + " - 1 " + " where " + AppTable.INDEX + " > "
				+ index + ";";
		mManager.exec(sql);
	}
	
	public Cursor getCurrentFolderItems() {
		// 表名
		String tables = AppTable.TABLENAME;
		// 查询列数
		String columns[] = { AppTable.FOLDERID };

		String where = AppTable.FOLDERID + " <> " + "0";

		return mManager.query(tables, columns, where, null, null);
	}
	
	public Cursor getBackupFolderItems() {
		// 表名
		String tables = AppTableBackup.TABLENAME;
		// 查询列数
		String columns[] = { AppTableBackup.FOLDERID, };

		String where = AppTableBackup.FOLDERID + " <> " + "0";

		return mManager.query(tables, columns, where, null, null);
	}
	
	public void backupAppTable() {
		try {
			mManager.beginTransaction();
			mManager.exec("DROP TABLE IF EXISTS " + AppTableBackup.TABLENAME);
			mManager.exec("CREATE TABLE " + AppTableBackup.TABLENAME + " AS SELECT * FROM "
					+ AppTable.TABLENAME);
			mManager.exec("DROP TABLE IF EXISTS " + AppDrawerFolderTableBackup.TABLENAME);
			mManager.exec("CREATE TABLE " + AppDrawerFolderTableBackup.TABLENAME
					+ " AS SELECT * FROM " + FolderTable.TABLENAME);
			mManager.setTransactionSuccessful();
		} catch (DatabaseException e) {
			e.printStackTrace();
		} finally {
			mManager.endTransaction();
		}
	}
	
	public boolean checkSupportAppTableRestore() {
		return mManager.isTableExist(AppDrawerFolderTableBackup.TABLENAME);
	}
	
	public void restoreAppTable() {
		try {
			mManager.beginTransaction();
			Cursor currentCursor = getCurrentFolderItems();
			Cursor backupCursor = getBackupFolderItems();
			if (backupCursor == null || currentCursor == null) {
				return;
			}
			//获取当前数据库中所有的文件夹，并把这些文件夹的数据从FOLDERTABLE中删除。
			StringBuilder builder = new StringBuilder();
			for (currentCursor.moveToFirst(); !currentCursor.isAfterLast(); currentCursor
					.moveToNext()) {
				long folderId = currentCursor.getLong(currentCursor.getColumnIndex(AppTable.FOLDERID));
				builder.append(folderId + ",");
			}
			if (builder.length() > 0) {
				builder.deleteCharAt(builder.length() - 1);
				String sql = "delete from " + FolderTable.TABLENAME + " where " + FolderTable.FOLDERID
						+ " in (" + builder.toString() + ") ";
				mManager.exec(sql);
			}
			//获取备份的数据库中所有的文件夹 ，并把这些数据插入到FOLDERTABLE中。
			StringBuilder builder2 = new StringBuilder();
			for (backupCursor.moveToFirst(); !backupCursor.isAfterLast(); backupCursor.moveToNext()) {
				long folderId = backupCursor.getLong(backupCursor
						.getColumnIndex(AppTableBackup.FOLDERID));
				builder2.append(folderId + ",");
			}
			if (builder2.length() > 0) {
				builder2.deleteCharAt(builder2.length() - 1);
				String spl2 = "INSERT INTO " + FolderTable.TABLENAME + " SELECT * FROM "
						+ AppDrawerFolderTableBackup.TABLENAME + " WHERE "
						+ AppDrawerFolderTableBackup.FOLDERID + " IN (" + builder2.toString() + ") ";
				mManager.exec(spl2);
			}
			//把目前的apptable表dorp掉，再把backup的apptable表覆盖过去。
			mManager.exec("DROP TABLE IF EXISTS " + AppTable.TABLENAME);
			mManager.exec("CREATE TABLE " + AppTable.TABLENAME + " AS SELECT * FROM "
					+ AppTableBackup.TABLENAME);

			mManager.setTransactionSuccessful();
		} catch (DatabaseException e) {
			e.printStackTrace();
		} finally {
			mManager.endTransaction();
		}
	}
	
	public void moveFunItem(int resIndex, int tarIndex) throws DatabaseException {
		String add = resIndex > tarIndex ? " + 1 " : " - 1 ";
		String comp1 = resIndex > tarIndex ? " >= " : " <= ";
		String comp2 = resIndex > tarIndex ? " < " : " > ";
		try {
			mManager.beginTransaction();
			try {
				String mindex = AppTable.INDEX;
				String sql = "update application set " + mindex + " = " + " -1 " + " where "
						+ mindex + " = " + resIndex + ";";
				mManager.exec(sql);
				sql = "update application set " + mindex + " = " + mindex + add + " where "
						+ mindex + comp1 + tarIndex + " and " + mindex + comp2 + resIndex + ";";
				mManager.exec(sql);
				sql = " update application set " + mindex + " = " + tarIndex + " where " + mindex
						+ " = " + " -1 " + ";";
				mManager.exec(sql);
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

	public boolean isNewDB() {
		return mManager.isNewDB();
	}
	
}
