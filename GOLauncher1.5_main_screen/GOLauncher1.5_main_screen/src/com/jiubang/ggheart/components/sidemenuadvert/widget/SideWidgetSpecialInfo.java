package com.jiubang.ggheart.components.sidemenuadvert.widget;


/**
 * @author zhangxi
 * @data 2013-10-16
 */
public class SideWidgetSpecialInfo extends SideWidgetInfo {
	
	public static final int SIDEWIDGET_SYSTEM_INFO = 1;
	public static final int SIDEWIDGET_GOMARKET_INFO = 2;
	
	private int mPreViewResID; //Widget浏览图在本地保持的图片名称 = mPreViewUrl.hashcode()
	private Object mObject;
	
	public SideWidgetSpecialInfo() {
		// TODO Auto-generated constructor stub
	}

	public int getPreViewResID() {
		return mPreViewResID;
	}

	public void setPreViewResID(int mPreViewResID) {
		this.mPreViewResID = mPreViewResID;
	}

	public Object getObject() {
		return mObject;
	}

	public void setObject(Object mObject) {
		this.mObject = mObject;
	}

}
