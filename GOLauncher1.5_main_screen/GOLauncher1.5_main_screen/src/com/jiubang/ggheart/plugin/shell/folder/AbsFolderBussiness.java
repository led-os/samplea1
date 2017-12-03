package com.jiubang.ggheart.plugin.shell.folder;

import java.util.ArrayList;
import java.util.HashMap;

import com.go.proxy.ApplicationProxy;
import com.jiubang.ggheart.apps.appfunc.data.FolderDataModel;
import com.jiubang.ggheart.bussiness.BaseBussiness;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.info.AppItemInfo;
/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  dingzijian
 * @date  [2013-2-25]
 */
public abstract class AbsFolderBussiness extends BaseBussiness {
	protected HashMap<Long, GLAppFolderInfo> mFolderInfos;
	protected FolderDataModel mDataModel;

	public AbsFolderBussiness() {
		super(ApplicationProxy.getContext());
		mDataModel = new FolderDataModel(mContext);
		mFolderInfos = new HashMap<Long, GLAppFolderInfo>();
	}
	public boolean addFolderInfo(long id, GLAppFolderInfo appFolderInfo) {
		if (mFolderInfos.containsKey(id)) {
			return false;
		}
		mFolderInfos.put(id, appFolderInfo);
		return true;
	}

	public GLAppFolderInfo getFolderInfo(long id) {
		return mFolderInfos.get(id);
	}
	
	public boolean removeFolderInfo(long id) {
		return mFolderInfos.remove(id) != null;
	}
	
	public void clearFolderInfos() {
		mFolderInfos.clear();
	}
	
	public HashMap<Long, GLAppFolderInfo> getFolderInfos() {
		return mFolderInfos;
	}
	
	public abstract ArrayList<GLAppFolderInfo> onFolderAppUninstall(
			ArrayList<AppItemInfo> uninstallapps) throws DatabaseException;
	
	/**
	 * 处理收到的消息
	 * 
	 * @param msgId
	 * @param param
	 * @param object
	 * @param objects
	 */
	@SuppressWarnings("rawtypes")
	protected void onHandleBCChange(int msgId, int param, Object ...object) {
		return;
	}
	
}
