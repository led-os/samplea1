package com.jiubang.ggheart.data.tables;
/**
 * 安全锁
 * @author zhujian
 *
 */
public class DeskLockable {
	/**
	 * 表名
	 */
	final static public String TABLENAME = "desklock";

	/**
	 *  应用锁
	 */
	final static public String APPLOCK = "applock";
	/**
	 * 锁定隐藏程序
	 */
	final static public String LOCKHIDEAPP = "lockhideapp";
	/**
	 * 锁定恢复备份
	 */
	final static public String RESTORESETTING = "restoresetting";
	/**
	 * 锁定恢复默认
	 */
	final static public String RESTOREDEFAULT = "restoredefault";
	/**
	 * 表语句
	 */
	final static public String CREATETABLESQL = "create table desklock " + "(" + APPLOCK + " numeric, "
			+ LOCKHIDEAPP + " numeric, " + RESTORESETTING + " numeric, " + RESTOREDEFAULT
			+ " numeric" + ")";
}