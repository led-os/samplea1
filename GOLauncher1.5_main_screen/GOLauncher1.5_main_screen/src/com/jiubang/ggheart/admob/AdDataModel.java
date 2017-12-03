package com.jiubang.ggheart.admob;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jiubang.ggheart.data.ContentProvider.GoContentProvider;

/**
 * 
 * @author  guoyiqing
 * @date  [2013-9-12]
 */
public class AdDataModel {
	private ContentResolver mContentResolver;

	public AdDataModel(Context context) {
		mContentResolver = context.getContentResolver();
	}

	public boolean updateOrInsertAdInfos(List<AdInfo> infos) {
		boolean result = false;
		if (infos == null || infos.size() <= 0) {
			return result;
		}
		for (AdInfo adInfo : infos) {
			if (!isExist(adInfo)) {
				insertAdInfo(adInfo.backup());
			} else {
				updateAdInfo(adInfo.backup());
			}
		}
		return result;
	}

	private boolean isExist(AdInfo info) {
		if (info == null) {
			return false;
		}
		String selection = AdTable.PRODUCT_ID + "=" + info.mPid + " and " + AdTable.POSITION_ID
				+ "=" + info.mPosId;
		Cursor cursor = getAdInfo(selection);
		if (cursor != null) {
			try {
				if (cursor.getCount() > 0) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return false;
	}

	public List<AdInfo> getAllAdInfos() {
		List<AdInfo> allAdInfos = new ArrayList<AdInfo>();
		Cursor cursor = getAdInfo(null);
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					AdInfo info = null;
					do {
						info = new AdInfo();
						info.restore(cursor);
						allAdInfos.add(info);
					} while (cursor.moveToNext());
				};
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return allAdInfos;
	}
	
	public void updateAdInfo(ContentValues values) {
		if (values != null) {
			try {
				String selection = AdTable.POSITION_ID + "=" + values.getAsString(AdTable.POSITION_ID);
				selection += " and " + AdTable.PRODUCT_ID + "=" + values.getAsString(AdTable.PRODUCT_ID);
				mContentResolver.update(GoContentProvider.CONTENT_ADMOB_URI, values, selection, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Cursor getAdInfo(String selection) {
		return mContentResolver.query(GoContentProvider.CONTENT_ADMOB_URI, null, selection, null, AdTable.ID);
	}

	public void insertAdInfo(ContentValues values) {
		if (values != null) {
			try {
				mContentResolver.insert(GoContentProvider.CONTENT_ADMOB_URI, values);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
