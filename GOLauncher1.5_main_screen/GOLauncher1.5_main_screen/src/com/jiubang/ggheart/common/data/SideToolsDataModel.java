package com.jiubang.ggheart.common.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jiubang.ggheart.components.sidemenuadvert.tools.SideToolsDataInfo;
import com.jiubang.ggheart.components.sidemenuadvert.utils.SideAdvertUtils;
import com.jiubang.ggheart.data.BaseDataModel;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.PersistenceManager;
import com.jiubang.ggheart.data.tables.AppExtraAttributeTable;
import com.jiubang.ggheart.data.tables.SideToolsTable;

/**
 * 
 * @author wuziyi
 *
 */
public class SideToolsDataModel extends BaseDataModel {
	/**
	 * @param context
	 * @param dbName
	 */
	public SideToolsDataModel(Context context) {
		super(context, PersistenceManager.DB_ANDROID_HEART);
	}
	
	public ArrayList<SideToolsDataInfo> getAllInstalledTools() {
		Cursor cursor = mManager.query(SideToolsTable.TABLE_NAME, null, null, null, null, null, null);
		ArrayList<SideToolsDataInfo> installedToolsList = new ArrayList<SideToolsDataInfo>();
		if (cursor != null) {
			try {
				int pkgNameIdx = cursor.getColumnIndex(SideToolsTable.TOOLS_PKGNAME);
				int isInstalleIdx = cursor.getColumnIndex(SideToolsTable.IS_FORCE_SHOW);
				int isSelectIdx = cursor.getColumnIndex(SideToolsTable.IS_SELECT);
				if (cursor.moveToFirst()) {
					do {
						SideToolsDataInfo whiteListInfo = new SideToolsDataInfo();
						boolean isInstalle = cursor.getInt(isInstalleIdx) == 1 ? true : false;
						boolean isSelected = cursor.getInt(isSelectIdx) == 1 ? true : false;
						whiteListInfo.setToolsPkgName(cursor.getString(pkgNameIdx));
						whiteListInfo.setIsInstalled(isInstalle);
						whiteListInfo.setSelect(isSelected);
//						whiteListInfo.setType(SideToolsDataInfo.SIDEWIDGET_DOWNLOAD_INFO);
						installedToolsList.add(whiteListInfo);
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return installedToolsList;
	}
	
	public void addInstalledTools(SideToolsDataInfo info)
			throws DatabaseException {
		ContentValues contentValues = new ContentValues();
		contentValues.put(SideToolsTable.TOOLS_PKGNAME, info.getToolsPkgName());
		contentValues.put(SideToolsTable.IS_FORCE_SHOW, info.isInstalled());
		contentValues.put(SideToolsTable.IS_SELECT, info.isSelect());
		mManager.insert(SideToolsTable.TABLE_NAME, contentValues);
	}
	
	public int updataToolsInfo(SideToolsDataInfo info) throws DatabaseException {
		String selection = AppExtraAttributeTable.COMPONENT_NAME + "=?";
		ContentValues values = new ContentValues();
		String[] selectionArgs = new String[] { info.getToolsPkgName() };
		values.put(SideToolsTable.IS_FORCE_SHOW, info.isInstalled());
		values.put(SideToolsTable.IS_SELECT, info.isSelect());
		return mManager.update(SideToolsTable.TABLE_NAME, values, selection, selectionArgs);
	}
	
	public void removeUninstalledTools(String widgetPkgName) throws DatabaseException {
		SideAdvertUtils.log("卸载操作，删除包名为:" + widgetPkgName);
		String selection = SideToolsTable.TOOLS_PKGNAME + "=?";
		String[] selectionArgs = new String[] { widgetPkgName };
		mManager.delete(SideToolsTable.TABLE_NAME, selection, selectionArgs);
	}
	
	public Cursor getInstalledTools(String widgetPkgName) {
		String[] args = new String[1];
		args[0] = widgetPkgName;
		String where = SideToolsTable.TOOLS_PKGNAME + "=?";
		return mManager.query(SideToolsTable.TABLE_NAME, null, where, args,
				null, null, null);
	}

}
