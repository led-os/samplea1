package com.jiubang.ggheart.common.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jiubang.ggheart.data.BaseDataModel;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.PersistenceManager;
import com.jiubang.ggheart.data.tables.LockInfoTable;

/**
 * 安全锁信息的数据库操作业务
 * @author wuziyi
 *
 */
public class LockInfoDataModel extends BaseDataModel {

	public LockInfoDataModel(Context context) {
		super(context, PersistenceManager.DB_ANDROID_HEART);
	}

	public void addLockInfo(int keyId, String password, String email) throws DatabaseException {
		ContentValues contentValues = new ContentValues();
		contentValues.put(LockInfoTable.KEY_ID, keyId);
		contentValues.put(LockInfoTable.PASSWORD, password);
		contentValues.put(LockInfoTable.EMAIL_ADDRESS, email);
		mManager.insert(LockInfoTable.TABLE_NAME, contentValues);
	}
	
	public Cursor getLockInfos() {
		String columns[] = { LockInfoTable.KEY_ID, LockInfoTable.PASSWORD, LockInfoTable.EMAIL_ADDRESS };
		return mManager.query(LockInfoTable.TABLE_NAME, columns, null, null, null);
	}
	
	public void removeLockInfo(int keyId) throws DatabaseException {
		String selection = LockInfoTable.KEY_ID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(keyId) };
		mManager.delete(LockInfoTable.TABLE_NAME, selection, selectionArgs);
	}
	
	public Cursor getPassword(int keyId) {
		String columns[] = { LockInfoTable.KEY_ID, LockInfoTable.PASSWORD, LockInfoTable.EMAIL_ADDRESS };
		String selection = LockInfoTable.KEY_ID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(keyId) };
		return mManager.query(LockInfoTable.TABLE_NAME, columns, selection, selectionArgs, null);
	}
	
	public void updataPassword(int keyId, String password) throws DatabaseException {
		ContentValues contentValues = new ContentValues();
		contentValues.put(LockInfoTable.PASSWORD, password);
		String selection = LockInfoTable.KEY_ID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(keyId) };
		mManager.update(LockInfoTable.TABLE_NAME, contentValues, selection, selectionArgs);
	}
	
	public Cursor getEmail(int keyId) {
		String columns[] = { LockInfoTable.EMAIL_ADDRESS };
		return mManager.query(LockInfoTable.TABLE_NAME, columns, null, null, null);
	}
	
	public void updataEmail(int keyId, String email) throws DatabaseException {
		ContentValues contentValues = new ContentValues();
		contentValues.put(LockInfoTable.EMAIL_ADDRESS, email);
		String selection = LockInfoTable.KEY_ID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(keyId) };
		mManager.update(LockInfoTable.TABLE_NAME, contentValues, selection, selectionArgs);
	}
}
