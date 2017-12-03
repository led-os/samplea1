package com.jiubang.ggheart.zeroscreen.search.bean;

import java.util.ArrayList;
/**
 * 搜索返回监听类
 * @author liulixia
 *
 */
public interface OnSearchListener {
	public void onSearchStart();
	public void onSearchFinish(String searchText, ArrayList<SearchResultInfo> results);
	public void onReloadHistoryText(boolean isAdded, String history);
	public void onRefreshList(ArrayList<SearchResultInfo> results);
}
