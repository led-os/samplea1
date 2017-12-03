package com.jiubang.ggheart.apps.desks.backup;

/**
 * 
 * @author guoyiqing
 * 
 */
public class BackupRestoreItem {

	public String mBackupPath;

	public String mRestorePath;

	public boolean mIsFolder;

	public boolean mIsDataToExternal;

	@Override
	public String toString() {
		return "mBackupPath:" + mBackupPath + " mRestorePath:" + mRestorePath
				+ " mIsFolder:" + mIsFolder + " mIsDataToExternal:"
				+ mIsDataToExternal;
	}

}
