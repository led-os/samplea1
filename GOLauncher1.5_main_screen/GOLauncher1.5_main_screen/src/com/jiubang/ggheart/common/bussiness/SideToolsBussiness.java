package com.jiubang.ggheart.common.bussiness;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.jiubang.ggheart.bussiness.BaseBussiness;
import com.jiubang.ggheart.common.data.SideToolsDataModel;
import com.jiubang.ggheart.components.sidemenuadvert.tools.SideToolsDataInfo;
import com.jiubang.ggheart.components.sidemenuadvert.tools.SideToolsInfo;
import com.jiubang.ggheart.data.DatabaseException;

/**
 * 
 * @author wuziyi
 *
 */
public class SideToolsBussiness extends BaseBussiness {

	private SideToolsDataModel mDataModel;
	/**
	 * @param context
	 */
	public SideToolsBussiness(Context context) {
		super(context);
		mDataModel = new SideToolsDataModel(context);
	}
	
	public ArrayList<SideToolsDataInfo> getAllInstalledTools() {
		return  mDataModel.getAllInstalledTools();
	}
	
	public void addInstalledTools(SideToolsDataInfo toolsInfo) {
		if (toolsInfo != null) {
			try {
				mDataModel.addInstalledTools(toolsInfo);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void removeUninstalledTools(SideToolsInfo toolsInfo) {
		if (toolsInfo != null) {
			try {
				mDataModel.removeUninstalledTools(toolsInfo.getToolsPkgName());
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void removeUninstalledTools(String toolsPkgName) {
		if (toolsPkgName != null) {
			try {
				mDataModel.removeUninstalledTools(toolsPkgName);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void addInstalledTools(List<SideToolsDataInfo> infos) {
		try {
			mDataModel.beginTransaction();
			for (SideToolsDataInfo info : infos) {
				addInstalledTools(info);
			}
			mDataModel.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			mDataModel.endTransaction();
		}
	}
	
	
	public void removeUninstalledTools(List<SideToolsDataInfo> infos) {
		try {
			mDataModel.beginTransaction();
			for (SideToolsDataInfo info : infos) {
				removeUninstalledTools(info);
			}
			mDataModel.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			mDataModel.endTransaction();
		}
	}
	
	public boolean updataToolsInfo(SideToolsDataInfo info) {
		boolean ret = false;
		try {
			int row = mDataModel.updataToolsInfo(info);
			if (row > 0) {
				ret = true;
			}
		} catch (DatabaseException e) {
			ret = false;
		}
		return ret;
	}
	
	public void updataToolsInfo(List<SideToolsDataInfo> infos) {
		try {
			mDataModel.beginTransaction();
			for (SideToolsDataInfo info : infos) {
				updataToolsInfo(info);
			}
			mDataModel.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			mDataModel.endTransaction();
		}
	}
	
	public void saveAppExtraAtturibute(SideToolsDataInfo info) {
		if (!updataToolsInfo(info)) {
			addInstalledTools(info);
		}
	}
}
