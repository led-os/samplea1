package com.jiubang.ggheart.zeroscreen.search.contact.util;

/**
 * 可拼音搜索的接口
 * @author zhouchaohong
 *
 */
public abstract class PinyinSearchable implements Searchable {
	public String getPinyin() {
		return getSearchField();
	} //获取拼音
	public String getFirstLetters() { 
		return getPinyin();
	} //获取首字母
}