package com.jiubang.ggheart.apps.gowidget.gostore.bean;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  lijunye
 * @date  [2012-10-29]
 */
public class MainBannerItemBean {
	// 软件或连接ID
	private int mId;
	// 详情风格
	public int mStyle;
	// 软件类型
	public static final int DATA_TYPE_SOFTWARE = 1;
	// 链接类型
	public static final int DATA_TYPE_LINKED = 2;
	// 专题类型
	public static final int DATA_TYPE_TOPIC = 3;
	// 分类类型
	public static final int DATA_TYPE_SORT = 4;
	// 壁纸类型
	public static final int DATA_TYPE_WALLPAPER = 5;

	// 操作数据
	// 点击分类项时，所做操作的数据
	// 如果是点击下载的话这操作数据就是链接地址
	private String mOperator;
	// 如果是专题类型，会有标题内容
	private String mTitle;
	// banner将要打开的界面类型
	private byte mChildType = 0;
	private String mPkgName;

	private int mSource;
	private String mCallbackUrl;
	public byte getChildType() {
		return mChildType;
	}

	public void setChildType(byte childType) {
		this.mChildType = childType;
	}

	private String mImageId = null;

	public String getImageId() {
		return mImageId;
	}

	public void setImageId(String mImageId) {
		this.mImageId = mImageId;
	}

	public String getOperator() {
		return mOperator;
	}

	public void setOperator(String operator) {
		this.mOperator = operator;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public void recycle() {
		mImageId = null;
		mOperator = null;
	}

	public String getPkgName() {
		return mPkgName;
	}

	public void setPkgName(String pkgName) {
		this.mPkgName = pkgName;
	}

	public void setSource(int source) {
		this.mSource = source;
	}

	public int getSource() {
		return this.mSource;
	}

	public void setCallbackUrl(String url) {
		this.mCallbackUrl = url;
	}

	public String getCallbackUrl() {
		return this.mCallbackUrl;
	}

	public int getStyle() {
		return mStyle;
	}

	public void setStyle(int mStyle) {
		this.mStyle = mStyle;
	}
	
}
