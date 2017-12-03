package com.jiubang.ggheart.common.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jiubang.ggheart.data.BaseDataModel;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.PersistenceManager;
import com.jiubang.ggheart.data.tables.AppClassifyFolderTable;
import com.jiubang.ggheart.data.tables.AppClassifyTable;
/**
 * 应用分类数据库查询操作
 */
public class AppClassifyDataModel extends BaseDataModel {

	public AppClassifyDataModel(Context context) {
		super(context, PersistenceManager.DB_APP_CLASSIFY);
	}
	
	// 只提供两个方法，单个查询，多个查询, 查不到map可能会为empty喔
	public HashMap<String, Integer> getAllAppClassifyItems(Set<String> packageNames) {
		HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
		Cursor cursor = null;
		cursor = getAllAppsClassify(packageNames);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					int pkgIndex = cursor.getColumnIndex(AppClassifyTable.PACKAGE_NAME);
					int classificationIndex = cursor
							.getColumnIndex(AppClassifyTable.CLASSIFICATION);
					String packageName = cursor.getString(pkgIndex);

					int classification = cursor.getInt(classificationIndex);
					resultMap.put(packageName, classification);
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return resultMap;
	}
	
	/**
	 * 查询单个包名的分类
	 * @param packageName
	 * @return
	 */
	public HashMap<String, Integer> getAppClassify(String packageName) {
		Cursor cursor = null;
		HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
		String[] args = new String[1];
		args[0] = packageName;
		String where = AppClassifyTable.PACKAGE_NAME + "=?";
		cursor = mManager.query(AppClassifyTable.TABLE_NAME, null, where, args, null, null, null);
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					do {
						int pkgIndex = cursor.getColumnIndex(AppClassifyTable.PACKAGE_NAME);
						int classificationIndex = cursor
								.getColumnIndex(AppClassifyTable.CLASSIFICATION);
						String pkg = cursor.getString(pkgIndex);

						int classification = cursor.getInt(classificationIndex);
						resultMap.put(pkg, classification);
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				cursor.close();
			}
		}
		return resultMap;
	}
	
	/**
	 * 根据包名列表获得分类列表
	 * @param packageNames
	 * @return
	 */
	public Cursor getAllAppsClassify(Set<String> packageNames) {
		if (packageNames.isEmpty()) {
			return null;
		}
		String where = null;
		String[] args = new String[packageNames.size()];
		StringBuilder whereBuf = new StringBuilder();
		whereBuf.append(AppClassifyTable.PACKAGE_NAME).append(" in (");
		int i = 0;
		for (String pkg : packageNames) {
			whereBuf.append("?,");
			args[i++] = pkg;
		}
		whereBuf.delete(whereBuf.lastIndexOf(","), whereBuf.length()).append(")");
		where = whereBuf.toString();
		
		return mManager.query(AppClassifyTable.TABLE_NAME, null, where, args, null, null, null);
	}
	
	public void updateAppClassifyDB(HashMap<String, Integer> newAppInfoData, HashMap<Integer, HashMap<String, String>> newTypeInfoData) {
		try {
			mManager.beginTransaction();
			mManager.exec("DROP TABLE IF EXISTS " + AppClassifyTable.TABLE_NAME);
			mManager.exec(AppClassifyTable.CREATETABLESQL);
			Set<String> set = newAppInfoData.keySet();
			for (String pkgName : set) {
				ContentValues contentValues = new ContentValues();
				contentValues.put(AppClassifyTable.PACKAGE_NAME, pkgName);
				contentValues.put(AppClassifyTable.CLASSIFICATION, newAppInfoData.get(pkgName));
				mManager.insert(AppClassifyTable.TABLE_NAME, contentValues);
			}
			mManager.exec("DROP TABLE IF EXISTS " + AppClassifyFolderTable.TABLE_NAME);
			mManager.exec(AppClassifyFolderTable.CREATETABLESQL);
			Set<Entry<Integer, HashMap<String, String>>> types = newTypeInfoData.entrySet();
			boolean addColumn = true;
			for (Entry<Integer, HashMap<String, String>> entry : types) {
				if (addColumn) {
					Set<String> keyset = entry.getValue().keySet();
					for (String languageColumn : keyset) {
						addLanguageColumn(languageColumn);
					}
					addColumn = false;
				}
				addFolderType(entry.getKey(), entry.getValue());
			}
			mManager.setTransactionSuccessful();
		} catch (DatabaseException e) {
			e.printStackTrace();
		} finally {
			mManager.endTransaction();
		}
	}
	
	public void addLanguageColumn(String languageColumn) throws DatabaseException {
		// 由于加了字段，再减少字段，会很复杂，请考虑清楚
		String updateSql = "ALTER TABLE " + AppClassifyFolderTable.TABLE_NAME + " ADD " + languageColumn + " text";
		mManager.exec(updateSql);
	}
	
	public void addLanguageColumns(List<String> languageColumns) {
		// 由于加了字段，再减少字段，会很复杂，请考虑清楚
		try {
			mManager.beginTransaction();
			for (String languageColumn : languageColumns) {
				addLanguageColumn(languageColumn);
			}
			mManager.setTransactionSuccessful();
		} catch (DatabaseException e) {
			e.printStackTrace();
		} finally {
			mManager.endTransaction();
		}
	}
	
	public HashMap<Integer, HashMap<String, String>> getFolderNames() {
		String tables = AppClassifyFolderTable.TABLE_NAME;
		Cursor cursor = mManager.query(tables, null, null, null, null);
		HashMap<Integer, HashMap<String, String>> folderMap = new HashMap<Integer, HashMap<String, String>>();
		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> nameMap = new HashMap<String, String>();
				String[] columnNames = cursor.getColumnNames();
				for (int i = 0; i < columnNames.length; i++) {
					String columnName = columnNames[i];
					if (columnName.equals(AppClassifyFolderTable.ID)) {
						int type = cursor.getInt(i);
						folderMap.put(type, nameMap);
					} else {
						String language = cursor.getString(i);
						nameMap.put(columnName, language);
					}
				}
			} while (cursor.moveToNext());
		}
		cursor.close();
		return folderMap;
	}
	
	public HashMap<Integer, HashMap<String, String>> getFolderName(int id) {
		String tables = AppClassifyFolderTable.TABLE_NAME;
		String where = AppClassifyFolderTable.ID + " = " + id;
		Cursor cursor = mManager.query(tables, null, where, null, null);
		HashMap<Integer, HashMap<String, String>> folderMap = new HashMap<Integer, HashMap<String, String>>();
		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> nameMap = new HashMap<String, String>();
				String[] columnNames = cursor.getColumnNames();
				for (int i = 0; i < columnNames.length; i++) {
					String columnName = columnNames[i];
					if (columnName.equals(AppClassifyFolderTable.ID)) {
						int type = cursor.getInt(i);
						folderMap.put(type, nameMap);
					} else {
						String language = cursor.getString(i);
						nameMap.put(columnName, language);
					}
				}
			} while (cursor.moveToNext());
		}
		cursor.close();
		return folderMap;
	}
	
	public int updateFolderType(int id, HashMap<String, String> nameMap) throws DatabaseException {
		String tables = AppClassifyFolderTable.TABLE_NAME;
		String whereStr = AppClassifyFolderTable.ID + " = " + "'" + id + "'";
		ContentValues values = new ContentValues();
		Iterator<Entry<String, String>> it = nameMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			values.put(entry.getKey(), entry.getValue());
		}
		return mManager.update(tables, values, whereStr, null);
	}
	
	public void updateAllFolderType(HashMap<Integer, HashMap<String, String>> nameMap) {
		Iterator<Entry<Integer, HashMap<String, String>>> it = nameMap.entrySet().iterator();
		try {
			mManager.beginTransaction();
			while (it.hasNext()) {
				Entry<Integer, HashMap<String, String>> entry = it.next();
				updateFolderType(entry.getKey(), entry.getValue());
			}
			mManager.setTransactionSuccessful();
		} catch (Exception e) {
			
		} finally {
			mManager.endTransaction();
		}
	}
	
	public void addFolderType(int id, HashMap<String, String> nameMap) throws DatabaseException {
		String tables = AppClassifyFolderTable.TABLE_NAME;
		ContentValues values = new ContentValues();
		values.put(AppClassifyFolderTable.ID, id);
		Iterator<Entry<String, String>> it = nameMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			values.put(entry.getKey(), entry.getValue());
		}
		mManager.insert(tables, values);
	}
	
	public void addFolderTypes(HashMap<Integer, HashMap<String, String>> nameMap) {
		Iterator<Entry<Integer, HashMap<String, String>>> it = nameMap.entrySet().iterator();
		try {
			mManager.beginTransaction();
			while (it.hasNext()) {
				Entry<Integer, HashMap<String, String>> entry = it.next();
				addFolderType(entry.getKey(), entry.getValue());
			}
			mManager.setTransactionSuccessful();
		} catch (Exception e) {
			
		} finally {
			mManager.endTransaction();
		}
	}
	
	public void updateOrAddFolderType(int id, HashMap<String, String> nameMap) throws DatabaseException {
		int ret = updateFolderType(id, nameMap);
		if (ret == -1) {
			addFolderType(id, nameMap);
		}
	}
	
	public void updateOrAddFolderTypes(HashMap<Integer, HashMap<String, String>> nameMap) {
		Iterator<Entry<Integer, HashMap<String, String>>> it = nameMap.entrySet().iterator();
		try {
			mManager.beginTransaction();
			while (it.hasNext()) {
				Entry<Integer, HashMap<String, String>> entry = it.next();
				updateOrAddFolderType(entry.getKey(), entry.getValue());
			}
			mManager.setTransactionSuccessful();
		} catch (Exception e) {
			
		} finally {
			mManager.endTransaction();
		}
	}
	
}
