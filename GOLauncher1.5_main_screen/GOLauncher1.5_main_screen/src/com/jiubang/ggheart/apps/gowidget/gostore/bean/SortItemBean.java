package com.jiubang.ggheart.apps.gowidget.gostore.bean;

/**
 * 分类列表项的BEAN
 * 
 * @author wangzhuobin
 * 
 */
public class SortItemBean {
	// 分类ID
	private int mId;
	// 类别名称
	private String mSortItemNameString;
	// 类别信息
	private String mInforString;
	// 操作数
	private String mOperator;
	// 子类型
	private byte mType;

	private String mImageId;

	private int mFunid = 18;

	public byte getType() {
		return mType;
	}

	public void setType(byte type) {
		this.mType = type;
	}

	public String getOperator() {
		return mOperator;
	}

	public void setOperator(String operator) {
		this.mOperator = operator;
	}

	public String getSortItemNameString() {
		return mSortItemNameString;
	}

	public void setSortItemNameString(String sortItemNameString) {
		this.mSortItemNameString = sortItemNameString;
	}

	public String getInforString() {
		return mInforString;
	}

	public void setmInforString(String inforString) {
		this.mInforString = inforString;
	}

	public String getImageId() {
		return mImageId;
	}

	public void setImageId(String imageId) {
		this.mImageId = imageId;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public int getFunId() {
		return mFunid;
	}

	public void setFunId(int id) {
		this.mFunid = id;
	}

	public void recycle() {
		mSortItemNameString = null;
		mInforString = null;
	}
}
