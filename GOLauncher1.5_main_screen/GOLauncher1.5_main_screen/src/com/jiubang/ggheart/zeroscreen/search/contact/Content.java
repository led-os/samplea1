package com.jiubang.ggheart.zeroscreen.search.contact;

/**
 * 用户排序的数据结构 key的值可由外部指定
 * @author zhouchaohong
 *
 */
public class Content {

	private int mKey;
	private Object mValue;
	public Content(int key, Object value) {
		this.mKey = key;
		this.mValue = value;
	}
	public int getKey() {
		return mKey;
	}
	public void setKey(int key) {
		this.mKey = key;
	}
	public Object getValue() {
		return mValue;
	}
	public void setValue(Object value) {
		this.mValue = value;
	}
}
