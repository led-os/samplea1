package com.jiubang.ggheart.common.data;

import java.util.List;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
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
public class AppExtraInfoDataModel extends BaseDataModel {
	
	public AppExtraInfoDataModel(Context context) {
		super(context, PersistenceManager.DB_ANDROID_HEART);
	}

	public int removeAppExtraInfo(AppExtraAttribute info) throws DatabaseException {
		String selection = AppExtraAttributeTable.COMPONENT_NAME + "=?";
		String[] selectionArgs = new String[] { info.getComponentName().flattenToString() };
		return mManager.delete(AppExtraAttributeTable.TABLE_NAME, selection, selectionArgs);
	}
	
	public void removeAppExtraInfos(List<AppExtraAttribute> infos) throws DatabaseException {
		mManager.beginTransaction();
		for (AppExtraAttribute appExtraAtturibute : infos) {
			removeAppExtraInfo(appExtraAtturibute);
		}
		mManager.setTransactionSuccessful();
		mManager.endTransaction();
	}
	
	public long addAppExtraInfo(AppExtraAttribute info) throws DatabaseException {
		ContentValues values = new ContentValues();
		values.put(AppExtraAttributeTable.ISLOCK, info.isLock());
		values.put(AppExtraAttributeTable.ISNEW, info.isNew());
		values.put(AppExtraAttributeTable.COMPONENT_NAME, info.getComponentName().flattenToString());
		values.put(AppExtraAttributeTable.CLICK_OPEN_TIME, info.getClickTime());
		values.put(AppExtraAttributeTable.DATA, info.getData());
		return mManager.insert(AppExtraAttributeTable.TABLE_NAME, values);
	}
	
	public void addAppExtraInfos(List<AppExtraAttribute> infos) throws DatabaseException {
		mManager.beginTransaction();
		for (AppExtraAttribute appExtraAtturibute : infos) {
			addAppExtraInfo(appExtraAtturibute);
		}
		mManager.setTransactionSuccessful();
		mManager.endTransaction();
	}
	
	public Cursor getAppExtraInfo(ComponentName cn) {
		String selection = AppExtraAttributeTable.COMPONENT_NAME + "=?";
		String[] selectionArgs = new String[] { cn.flattenToString() };
		return mManager.query(AppExtraAttributeTable.TABLE_NAME, null, selection, selectionArgs, null);
	}
	
	public Cursor getAllAppExtraInfo() {
		return mManager.query(AppExtraAttributeTable.TABLE_NAME, null, null, null, null);
	}
	
	public int updateAppExtraInfo(AppExtraAttribute info) throws DatabaseException {
		String selection = AppExtraAttributeTable.COMPONENT_NAME + "=?";
		ContentValues values = new ContentValues();
		String[] selectionArgs = new String[] { info.getComponentName().flattenToString() };
		values.put(AppExtraAttributeTable.ISLOCK, info.isLock());
		values.put(AppExtraAttributeTable.ISNEW, info.isNew());
		values.put(AppExtraAttributeTable.CLICK_OPEN_TIME, info.getClickTime());
		values.put(AppExtraAttributeTable.DATA, info.getData());
		return mManager.update(AppExtraAttributeTable.TABLE_NAME, values, selection, selectionArgs);
	}
	
	public void updateAppExtraInfos(List<AppExtraAttribute> infos) throws DatabaseException {
		mManager.beginTransaction();
		for (AppExtraAttribute appExtraAtturibute : infos) {
			updateAppExtraInfo(appExtraAtturibute);
		}
		mManager.setTransactionSuccessful();
		mManager.endTransaction();
	}
	
}
