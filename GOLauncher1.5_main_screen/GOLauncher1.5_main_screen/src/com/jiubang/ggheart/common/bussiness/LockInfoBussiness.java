package com.jiubang.ggheart.common.bussiness;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseArray;

import com.go.util.DbUtil;
import com.jiubang.ggheart.bussiness.BaseBussiness;
import com.jiubang.ggheart.common.data.LockInfoDataModel;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.tables.LockInfoTable;

/**
 * 安全锁信息业务
 * @author wuziyi
 *
 */
public class LockInfoBussiness extends BaseBussiness {
	private LockInfoDataModel mDataModel;
	private SparseArray<LockInfo> mLockInfos;

	public LockInfoBussiness(Context context) {
		super(context);
		mDataModel = new LockInfoDataModel(context);
		initLockInfo();
	}
	
	public void initLockInfo() {
		Cursor cursor = mDataModel.getLockInfos();
		if (cursor != null) {
			try {
				mLockInfos = new SparseArray<LockInfo>(cursor.getCount());
				if (cursor.moveToLast()) {
					do {
						int keyId = DbUtil.getInt(cursor, LockInfoTable.KEY_ID);
						String password = DbUtil.getString(cursor, LockInfoTable.PASSWORD);
						String email = DbUtil.getString(cursor, LockInfoTable.EMAIL_ADDRESS);
						LockInfo info = new LockInfo();
						info.mPassword = password;
						info.mEmail = email;
						mLockInfos.put(keyId, info);
					} while (cursor.moveToPrevious());
				}
			} finally {
				cursor.close();
			}
		}
	}
	
	public LockInfo getLockInfo(int keyId) {
		return mLockInfos.get(keyId);
	}
	
	public void addLockInfo(int keyId, LockInfo lockInfo) {
		if (lockInfo != null) {
			mLockInfos.put(keyId, lockInfo);
			try {
				mDataModel.addLockInfo(keyId, lockInfo.mPassword, lockInfo.mEmail);
			} catch (DatabaseException e) {
				
			}
		}
	}
	
	public void removeLockInfo(int keyId) {
		mLockInfos.remove(keyId);
		try {
			mDataModel.removeLockInfo(keyId);
		} catch (DatabaseException e) {
			
		}
	}
	
	public String getPassword(int keyId) {
		LockInfo info = getLockInfo(keyId);
		if (info != null) {
			return info.mPassword;
		}
		return null;
	}
	
	public void updataPassword(int keyId, String password) {
		LockInfo info = getLockInfo(keyId);
		if (info != null) {
			if (info.mPassword == null || !info.mPassword.equals(password)) {
				info.mPassword = password;
				try {
					mDataModel.updataPassword(keyId, password);
				} catch (DatabaseException e) {
				}
			}
		}
	}
	
	public void updataEmail(int keyId, String email) {
		LockInfo info = getLockInfo(keyId);
		if (info != null) {
			if (info.mEmail == null || !info.mEmail.equals(email)) {
				info.mEmail = email;
				try {
					mDataModel.updataEmail(keyId, email);
				} catch (DatabaseException e) {
				}
			}
		}
	}
	
	public String getEmail(int keyId) {
		LockInfo info = getLockInfo(keyId);
		if (info != null) {
			return info.mEmail;
		}
		return null;
	}
	
	/**
	 * 安全锁信息对象
	 * @author wuziyi
	 *
	 */
	public static class LockInfo {
		public LockInfo(String password, String email) {
			mPassword = password;
			mEmail = email;
		}
		
		public LockInfo() {
		}
		
		public void setPasswrod(String password) {
			mPassword = password;
		}
		
		public void setEmail(String email) {
			mEmail = email;
		}
		
		protected String mPassword;
		protected String mEmail;
	}

	/**
	 * 删除邮箱
	 * @author zhujian
	 *
	 */
	public void delEmail(int keyId) {
		updataEmail(0, null);
	}
	
	/**
	 * 删除password
	 * @author zhujian
	 *
	 */
	public void delPassword(int keyId) {
		updataPassword(0, null);
	}
	
}
