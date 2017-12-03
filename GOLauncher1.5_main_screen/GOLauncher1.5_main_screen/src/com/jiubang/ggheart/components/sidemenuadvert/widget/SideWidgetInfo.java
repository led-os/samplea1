package com.jiubang.ggheart.components.sidemenuadvert.widget;

/**
 * @author zhangxi
 * @data 2013-10-16
 */
public abstract class SideWidgetInfo {
	
	protected int mType; //1 system 2 go market
	protected String mTitle; //Widget Title
	protected String mWidgetPkgName; //Widget包名

	public SideWidgetInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}
	
	public String getWidgetPkgName() {
		return mWidgetPkgName;
	}

	public void setWidgetPkgName(String mWidgetPkgName) {
		this.mWidgetPkgName = mWidgetPkgName;
	}

	public int getType() {
		return mType;
	}

	public void setType(int mType) {
		this.mType = mType;
	}

}
