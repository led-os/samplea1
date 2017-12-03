package com.jiubang.ggheart.data.SqliteNative;

/**
 * 
 * <br>类描述:support sqlite3 dump and import function
 * <br>功能详细描述: dumpFileFromDB dump data from db; readFileToDB import data from dumpfile;
 * 				   getDBVersion get db version; checkDBinterity no implement;
 * 
 * @author  zhangxi
 * @date  [2013年7月23日]
 */
public class SqlitedbForCK {

	public native boolean dumpFileFromDB(String sourceFile, String destFile);

	public native boolean readFileToDB(String sourceFile, String destFile);

	public native String getDBVersion();
	
	public String getDBVersionSafely() {
		String version = "0.0";
		try {
			version = getDBVersion();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return version;
	}

	public native boolean checkDBinterity(String strError);

	static {
		try {
			System.loadLibrary("sqliteDB");
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException("can not load libsqliteDB.");
		}
	}
}
