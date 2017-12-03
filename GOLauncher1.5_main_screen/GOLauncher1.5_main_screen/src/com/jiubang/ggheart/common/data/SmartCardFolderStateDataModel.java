package com.jiubang.ggheart.common.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jiubang.ggheart.data.BaseDataModel;
import com.jiubang.ggheart.data.PersistenceManager;
import com.jiubang.ggheart.data.tables.SmartCardFolderStateTable;

/**
 * 
 * @author guoyiqing
 * 
 */
public class SmartCardFolderStateDataModel extends BaseDataModel {

	public SmartCardFolderStateDataModel(Context context) {
		super(context, PersistenceManager.DB_ANDROID_HEART);
	}

	public Cursor getFolderState(int folderId) {
		Cursor cursor = null;
		String selection = SmartCardFolderStateTable.FOLDERID + "=? ";
		try {
			cursor = mManager.query(SmartCardFolderStateTable.TABLENAME,
					new String[] { SmartCardFolderStateTable.FOLDERID,
							SmartCardFolderStateTable.STATE, }, selection,
					new String[] { String.valueOf(folderId) }, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}

	/**
	 * 仅用于插入
	 * 
	 * @param values
	 */
	public long updateFolderState(ContentValues values) {
		try {
			String selection = SmartCardFolderStateTable.FOLDERID + "=? ";
			String[] args = new String[] { String.valueOf(values
					.getAsInteger(SmartCardFolderStateTable.FOLDERID)) };
			return mManager.update(SmartCardFolderStateTable.TABLENAME, values,
					selection, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 仅用于插入
	 * 
	 * @param values
	 */
	public long insertFolderState(ContentValues values) {
		try {
			return mManager.insert(SmartCardFolderStateTable.TABLENAME, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void deleteFolderState(int folderId) {
		String selection = SmartCardFolderStateTable.FOLDERID + "=? ";
		try {
			mManager.delete(SmartCardFolderStateTable.TABLENAME, selection,
					new String[] { String.valueOf(folderId) });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
