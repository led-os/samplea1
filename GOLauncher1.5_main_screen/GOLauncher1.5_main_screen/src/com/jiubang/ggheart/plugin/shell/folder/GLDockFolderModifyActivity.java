package com.jiubang.ggheart.plugin.shell.folder;

import com.gau.go.launcherex.R;
/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  dingzijian
 * @date  [2013-3-22]
 */
public class GLDockFolderModifyActivity extends GLScreenFolderModifyActivity {
	@Override
	protected void initData() {
		mUserFolderInfo = mFolderController.getFolderInfoById(mFolderID,
				GLAppFolderInfo.FOLDER_FROM_DOCK).getScreenFoIderInfo();
		mFolderName = mUserFolderInfo.getFeatureTitle();
		if (mFolderName == null) {
			mFolderName = getString(R.string.folder_name);
		}
		loadAppListForModify();
	}
}
