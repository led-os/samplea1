package com.jiubang.ggheart.admob;

/**
 * 
 * @author  guoyiqing
 * @date  [2013-9-14]
 */
public interface OnAdDeleteListener {

	public void onDelete(GoAdView view);
	//通过pid以及posid删除view
	public void onDeleteView(GoAdView view, int pId, int posId);
	
}
