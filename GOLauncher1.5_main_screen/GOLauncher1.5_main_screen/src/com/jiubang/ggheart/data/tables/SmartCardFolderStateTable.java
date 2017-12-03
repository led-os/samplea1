package com.jiubang.ggheart.data.tables;

/**
 * 
 * @author guoyiqing
 * 
 */
public class SmartCardFolderStateTable {

	public static final String TABLENAME = "smartcard_recom";

	public static final String FOLDERID = "folderId";

	public static final String STATE = "state";

	public static final String CREATE_SQL = "create table IF NOT EXISTS "
			+ TABLENAME + "(" + FOLDERID + " INTEGER, " + STATE + " INTEGER);";

}
