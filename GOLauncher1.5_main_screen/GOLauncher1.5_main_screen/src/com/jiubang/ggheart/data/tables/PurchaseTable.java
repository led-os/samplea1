package com.jiubang.ggheart.data.tables;
/**
 * 
 * <br>类描述:已付费表
 * <br>功能详细描述:保存已付费的商品ID
 * 
 * @author  rongjinsong
 * @date  [2014年3月20日]
 */
public class PurchaseTable {
	public static final String TABLE_NAME = "purchase";
	
	public static final String PRODUCT_ID = "product_id"; //商品ID
	/**
	 * 表语句
	 */
	public static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS purchase " + "(" + "product_id TEXT NOT NULL " + ");";
}
