package com.jiubang.ggheart.common.data;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.jiubang.ggheart.data.BaseDataModel;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.PersistenceManager;
import com.jiubang.ggheart.data.tables.AppExtraAttributeTable;

/**
 * 
 * @author wuziyi
 *
 */
public class AppInvokeLockDataModel extends BaseDataModel {

	public AppInvokeLockDataModel(Context context) {
		super(context, PersistenceManager.DB_ANDROID_HEART);
	}

	/**
	 * 初始化应用锁，从数据库中读取，启动桌面后
	 */
	public void initAppLockItems() {

	}

	/**
	 * 添加启动应用锁的程序
	 * 
	 * @author wuziyi
	 * @param intent
	 * @throws DatabaseException
	 */
	public void addAppLockItem(final Intent intent) throws DatabaseException {
		if (intent == null) {
			return;
		}
		ComponentName cn = intent.getComponent();
		if (cn != null) {
			String cnStr = cn.flattenToString();
			ContentValues values = new ContentValues();
			values.put(AppExtraAttributeTable.COMPONENT_NAME, cnStr);
			values.put(AppExtraAttributeTable.ISLOCK, 1);
			mManager.insert(AppExtraAttributeTable.TABLE_NAME, values);
		}
	}

	/**
	 * 删除启动应用锁的程序
	 * 
	 * @param intent
	 * @throws DatabaseException
	 */
	public void delAppLockItem(Intent intent) throws DatabaseException {
		ComponentName cn = intent.getComponent();
		delAppLockItem(cn);
	}
	
	/**
	 * 删除启动应用锁的程序
	 * 
	 * @param intent
	 * @throws DatabaseException
	 */
	public void delAppLockItem(ComponentName cn) throws DatabaseException {
		if (cn != null) {
			String where = AppExtraAttributeTable.COMPONENT_NAME + "='" + cn.flattenToString()
					+ "' and " + AppExtraAttributeTable.ISLOCK + "=1";
			mManager.delete(AppExtraAttributeTable.TABLE_NAME, where, null);
		}
	}

	/**
	 * 获取所有启动应用锁的程序
	 * 
	 * @return
	 */
	public ArrayList<ComponentName> getAppLockItems() {
		ArrayList<ComponentName> list = new ArrayList<ComponentName>();
		String where = AppExtraAttributeTable.ISLOCK + "=1";
		Cursor cursor = mManager.query(AppExtraAttributeTable.TABLE_NAME, null, where, null, null,
				null, null);
		if (cursor != null) {
			try {
				int componentNameIndex = cursor.getColumnIndex(AppExtraAttributeTable.COMPONENT_NAME);
				while (cursor.moveToNext()) {
					String cName = cursor.getString(componentNameIndex);
					if (cName != null) {
						ComponentName cn = ComponentName.unflattenFromString(cName);
						if (cn != null) {
							list.add(cn);
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				cursor.close();
			}
			return list;
		} else {
			return null;
		}
		
	}
	/**
	 * 批量删除应用启动锁
	 * @param list
	 * @throws DatabaseException 
	 */
	public void removeAppInvokeLockItems(ArrayList<Intent> list) throws DatabaseException {
		for (Intent intent : list) {
			ComponentName cn = intent.getComponent();
			if (cn != null) {
				String where = AppExtraAttributeTable.COMPONENT_NAME + "='" + cn.flattenToString()
						+ "' and " + AppExtraAttributeTable.ISLOCK + "=1";
//				mManager.delete(AppExtraAttributeTable.TABLE_NAME, where, null);
				ContentValues values = new ContentValues();
				values.put(AppExtraAttributeTable.ISLOCK, 0);
				mManager.update(AppExtraAttributeTable.TABLE_NAME, values, where, null);
			}
		}
	}

	/**
	 * 清空启动应用锁的程序
	 * 
	 * @throws DatabaseException
	 */
	public void clearAppLockItems() throws DatabaseException {
		String where = AppExtraAttributeTable.ISLOCK + "=1";
		ContentValues values = new ContentValues();
		values.put(AppExtraAttributeTable.ISLOCK, 0);
		mManager.update(AppExtraAttributeTable.TABLE_NAME, values, where, null);
//		mManager.delete(AppExtraAttributeTable.TABLE_NAME, where, null);
	}
}
