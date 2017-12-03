package com.jiubang.ggheart.common.controler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jiubang.ggheart.common.data.SmartCardFolderStateDataModel;
import com.jiubang.ggheart.data.tables.SmartCardFolderStateTable;

/**
 * 
 * @author guoyiqing
 * 
 */
public class SmartCardFolderStateControler {

	private static SmartCardFolderStateControler sSelfObject;
	private SmartCardFolderStateDataModel mDataModel;

	private SmartCardFolderStateControler(Context context) {
		mDataModel = new SmartCardFolderStateDataModel(context);
	}

	public synchronized static SmartCardFolderStateControler getInstance(
			Context Context) {
		if (sSelfObject == null) {
			sSelfObject = new SmartCardFolderStateControler(Context);
		}
		return sSelfObject;
	}

	public void deleteFolder(int folderId) {
		mDataModel.deleteFolderState(folderId);
	}

	public long updateFolderState(FolderState info) {
		if (info != null) {
			Cursor cursor = mDataModel.getFolderState(info.getFolderId());
			if (cursor != null && cursor.moveToFirst()) {
				cursor.close();
				ContentValues values = new ContentValues();
				values.put(SmartCardFolderStateTable.STATE, info.getState());
				values.put(SmartCardFolderStateTable.FOLDERID,
						info.getFolderId());
				return mDataModel.updateFolderState(values);
			} else {
				ContentValues values = new ContentValues();
				values.put(SmartCardFolderStateTable.STATE, info.getState());
				values.put(SmartCardFolderStateTable.FOLDERID,
						info.getFolderId());
				return mDataModel.insertFolderState(values);
			}
		}
		return -1;
	}

	public FolderState getFolderState(int folderId) {
		Cursor cursor = mDataModel.getFolderState(folderId);
		FolderState info = null;
		try {
			if (cursor != null && cursor.moveToFirst()) {
				info = new FolderState();
				int indexState = cursor
						.getColumnIndex(SmartCardFolderStateTable.STATE);
				info.setState(cursor.getInt(indexState));
				info.setFolderId(folderId);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return info;
	}

	/**
	 * 
	 * @author guoyiqing
	 * 
	 */
	public class FolderState {

		public static final int SMARTCARD_TYPE_APP = 1;

		public static final int SMARTCARD_TYPE_LESS = 1 << 1;

		public static final int SMARTCARD_TYPE_UPDATE = 1 << 2;

		public static final int SMARTCARD_TYPE_MEM = 1 << 3;

		public static final int SMARTCARD_TYPE_LIGHTGAME = 1 << 4;

		private int mFolderId;

		private int mState;

		public int getFolderId() {
			return mFolderId;
		}

		public void setFolderId(int mFolderId) {
			this.mFolderId = mFolderId;
		}

		private boolean isOpen(int typeId) {
			return (getState() & typeId) == typeId;
		}

		private void setOpen(int typeId, boolean open) {
			if (open) {
				setState(getState() | typeId);
			} else {
				setState(getState() & ~typeId);
			}
		}

		public boolean isMemOpen() {
			return isOpen(SMARTCARD_TYPE_MEM);
		}

		public void setMemState(boolean open) {
			setOpen(SMARTCARD_TYPE_MEM, open);
		}

		public boolean isLessOpen() {
			return isOpen(SMARTCARD_TYPE_LESS);
		}

		public void setLessState(boolean open) {
			setOpen(SMARTCARD_TYPE_LESS, open);
		}

		public boolean isUpdateOpen() {
			return isOpen(SMARTCARD_TYPE_UPDATE);
		}

		public void setUpdateState(boolean open) {
			setOpen(SMARTCARD_TYPE_UPDATE, open);
		}

		public boolean isAppOpen() {
			return isOpen(SMARTCARD_TYPE_APP);
		}

		public void setAppState(boolean open) {
			setOpen(SMARTCARD_TYPE_APP, open);
		}

		public boolean isLightGameOpen() {
			return isOpen(SMARTCARD_TYPE_LIGHTGAME);
		}

		public void setLightGameState(boolean open) {
			setOpen(SMARTCARD_TYPE_LIGHTGAME, open);
		}

		protected int getState() {
			return mState;
		}

		protected void setState(int mState) {
			this.mState = mState;
		}

	}

}
