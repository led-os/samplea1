package com.jiubang.ggheart.plugin.shell.folder;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.ContentValues;

import com.go.util.ConvertUtils;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.DockItemInfo;
import com.jiubang.ggheart.data.info.ItemInfo;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.data.tables.ShortcutTable;
import com.jiubang.ggheart.launcher.PackageName;
/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  dingzijian
 * @date  [2013-3-16]
 */
public class GLDockFolderBussiness extends AbsFolderBussiness {
	public void updateDockItem(final long id, final DockItemInfo info) {
		ContentValues values = new ContentValues();
		info.writeObject(values, ShortcutTable.TABLENAME);
		mDataModel.updateShortCutItem(id, values, PackageName.PACKAGE_NAME);
	}

	@Override
	public ArrayList<GLAppFolderInfo> onFolderAppUninstall(ArrayList<AppItemInfo> uninstallapps) throws DatabaseException {
		ArrayList<GLAppFolderInfo> folderInfos = new ArrayList<GLAppFolderInfo>();
		for (AppItemInfo appItemInfo : uninstallapps) {
			Iterator<Long> iterator = mFolderInfos.keySet().iterator();
			while (iterator.hasNext()) {
				Long long1 = (Long) iterator.next();
				GLAppFolderInfo folderInfo = mFolderInfos.get(long1);
				ArrayList<ItemInfo> infos = folderInfo.getScreenFoIderInfo().getContents();
				for (ItemInfo info : infos) {
					ShortCutInfo shortCutInfo = (ShortCutInfo) info;
					if (ConvertUtils.intentCompare(shortCutInfo.mIntent, appItemInfo.mIntent)) {
						folderInfo.getScreenFoIderInfo().remove(shortCutInfo);
						mDataModel.removeAppFromFolder(shortCutInfo.mInScreenId,
								folderInfo.folderId);
						if (!folderInfos.contains(folderInfo)) {
							folderInfos.add(folderInfo);
						}
						break;
					}
				}
			}
		}
		return folderInfos;
	}
}
