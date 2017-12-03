package com.jiubang.ggheart.common.data;

import java.util.List;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jiubang.ggheart.data.BaseDataModel;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.PersistenceManager;
import com.jiubang.ggheart.data.info.SpecialAppItemInfo;
import com.jiubang.ggheart.data.tables.SpecialApplicationTable;

/**
 * 
 * @author yangguanxiang
 *
 */
public class SpecialApplicationDataModel extends BaseDataModel {

	public SpecialApplicationDataModel(Context context) {
		super(context, PersistenceManager.DB_ANDROID_HEART);
	}

	public int removeSpecialApp(SpecialAppItemInfo info) throws DatabaseException {
		String selection = SpecialApplicationTable.COMPONENT_NAME + "=?";
		String[] selectionArgs = new String[] { info.mIntent.getComponent().flattenToString() };
		return mManager.delete(SpecialApplicationTable.TABLE_NAME, selection, selectionArgs);
	}

	public void removeSpecialApps(List<SpecialAppItemInfo> infos) throws DatabaseException {
		mManager.beginTransaction();
		try {
			for (SpecialAppItemInfo appItemInfo : infos) {
				removeSpecialApp(appItemInfo);
			}
			mManager.setTransactionSuccessful();
		} finally {
			mManager.endTransaction();
		}
	}

	public long addSpecialApp(SpecialAppItemInfo info) throws DatabaseException {
		ContentValues values = new ContentValues();
		values.put(SpecialApplicationTable.COMPONENT_NAME, info.mIntent.getComponent()
				.flattenToString());
		return mManager.insert(SpecialApplicationTable.TABLE_NAME, values);
	}

	public void addSpecialApps(List<SpecialAppItemInfo> infos) throws DatabaseException {
		mManager.beginTransaction();
		try {
			for (SpecialAppItemInfo appItemInfo : infos) {
				addSpecialApp(appItemInfo);
			}
			mManager.setTransactionSuccessful();
		} finally {
			mManager.endTransaction();
		}
	}
	public Cursor getSpecialApp(ComponentName cn) {
		String selection = SpecialApplicationTable.COMPONENT_NAME + "=?";
		String[] selectionArgs = new String[] { cn.flattenToString() };
		return mManager.query(SpecialApplicationTable.TABLE_NAME, null, selection, selectionArgs,
				null);
	}

	public Cursor getAllSpecialApps() {
		return mManager.query(SpecialApplicationTable.TABLE_NAME, null, null, null, null);
	}
}
