/**
 * @author zhangxi
 * @date 2013-10-17
 */
package com.jiubang.ggheart.common.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jiubang.ggheart.components.sidemenuadvert.utils.SideAdvertUtils;
import com.jiubang.ggheart.components.sidemenuadvert.widget.SideWidgetDataInfo;
import com.jiubang.ggheart.data.BaseDataModel;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.PersistenceManager;
import com.jiubang.ggheart.data.tables.SideWidgetTable;

/**
 * @author zhangxi
 *
 */
public class SideWidgetDataModel extends BaseDataModel {

	/**
	 * @param context
	 * @param dbName
	 */
	public SideWidgetDataModel(Context context) {
		super(context, PersistenceManager.DB_ANDROID_HEART);
	}
	
	public ArrayList<SideWidgetDataInfo> getAllInstalledWidgets() {
		Cursor cursor = mManager.query(SideWidgetTable.TABLE_NAME, null, null, null, null, null, null);
		ArrayList<SideWidgetDataInfo> installedWidgetList = new ArrayList<SideWidgetDataInfo>();
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					do {
						SideWidgetDataInfo whiteListInfo = new SideWidgetDataInfo();
						int titleIdx = cursor.getColumnIndex(SideWidgetTable.WIDGET_TITLE);
						whiteListInfo.setTitle(cursor.getString(titleIdx));
						int pkgNameIdx = cursor.getColumnIndex(SideWidgetTable.WIDGET_PKGNAME);
						whiteListInfo.setWidgetPkgName(cursor.getString(pkgNameIdx));
						int preViewIdx = cursor.getColumnIndex(SideWidgetTable.WIDGET_PREVIEWNAME);
						whiteListInfo.setPreViewName(cursor.getInt(preViewIdx));
						whiteListInfo.setIsInstalled(true);
						whiteListInfo.setType(SideWidgetDataInfo.SIDEWIDGET_DOWNLOAD_INFO);
						installedWidgetList.add(whiteListInfo);
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return installedWidgetList;
	}
	
	public void addInstalledWidget(String widgetTitle, String widgetPkgName, int widgetPreviewName)
			throws DatabaseException {
		ContentValues contentValues = new ContentValues();
		contentValues.put(SideWidgetTable.WIDGET_TITLE, widgetTitle);
		contentValues.put(SideWidgetTable.WIDGET_PKGNAME, widgetPkgName);
		contentValues.put(SideWidgetTable.WIDGET_PREVIEWNAME, widgetPreviewName);
		mManager.insert(SideWidgetTable.TABLE_NAME, contentValues);
	}
	
	public Cursor getInstalledWidget(String widgetPkgName) {
		String[] args = new String[1];
		args[0] = widgetPkgName;
		String where = SideWidgetTable.WIDGET_PKGNAME + "=?";
		return mManager.query(SideWidgetTable.TABLE_NAME, null, where, args,
				null, null, null);
	}
	
	public void removeUninstalledWidget(String widgetPkgName) throws DatabaseException {
		SideAdvertUtils.log("卸载操作，删除包名为:" + widgetPkgName);
		String selection = SideWidgetTable.WIDGET_PKGNAME + "=?";
		String[] selectionArgs = new String[] { widgetPkgName };
		mManager.delete(SideWidgetTable.TABLE_NAME, selection, selectionArgs);
	}

}
