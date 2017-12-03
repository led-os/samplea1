package com.jiubang.ggheart.admob;


import android.content.ContentValues;
import android.database.Cursor;

/**
 * 
 * @author  guoyiqing
 * @date  [2013-9-12]
 */
public class AdInfo {

	private static final int SWITCH_STATE_CLOSE = 0;
	private static final int SWITCH_STATE_OPEN = SWITCH_STATE_CLOSE + 1;
	
	public static final int PROVITY_ADMOB = 0;
	public static final int PROVITY_GETJAR = 1;
	
	public  int mId = -1;

	public  int mPid;

	public  int mPosId;
	
	public  boolean mSwitchState;
	public boolean mGetJarEnable;
	public int mProvity; // 默认0  admob优先  1  getjar优先  
	public int getSparseId() {
		return getSparseId(mPid, mPosId);
	}
	
	public static int getSparseId(int pid, int posId) {
		return pid * 1000000 + posId;
	}
	
	public ContentValues backup() {
		ContentValues values = new ContentValues();
		if (mId != -1) {
			values.put(AdTable.ID, mId);
		}
		values.put(AdTable.POSITION_ID, mPosId);
		values.put(AdTable.PRODUCT_ID, mPid);
		values.put(AdTable.SWICH_STATE, mSwitchState ? SWITCH_STATE_OPEN : SWITCH_STATE_CLOSE);
		values.put(AdTable.GETJAR_ENABLE, mGetJarEnable ? SWITCH_STATE_OPEN : SWITCH_STATE_CLOSE);
		values.put(AdTable.PROVITY, mProvity);
		return values;
	}
	
	public void restore(Cursor cursor) {
		int index = cursor.getColumnIndex(AdTable.ID);
		if (index != -1) {
			mPid = cursor.getInt(index);
		}
		index = cursor.getColumnIndex(AdTable.POSITION_ID);
		if (index != -1) {
			mPosId = cursor.getInt(index);
		}
		index = cursor.getColumnIndex(AdTable.PRODUCT_ID);
		if (index != -1) {
			mPid = cursor.getInt(index);
		}
		index = cursor.getColumnIndex(AdTable.SWICH_STATE);
		if (index != -1) {
			mSwitchState = cursor.getInt(index) == SWITCH_STATE_OPEN ? true : false;
		}
		index = cursor.getColumnIndex(AdTable.GETJAR_ENABLE);
		if (index != -1) {
			mGetJarEnable = cursor.getInt(index) == SWITCH_STATE_OPEN ? true : false;
		}
		index = cursor.getColumnIndex(AdTable.PROVITY);
		if (index != -1) {
			mProvity = cursor.getInt(index);
		}
	}
	
}
