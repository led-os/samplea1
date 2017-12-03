package com.golauncher.message;

import com.jiubang.ggheart.data.info.GoWidgetBaseInfo;

/**
 * 
 * @author liuheng
 *
 */
public interface IWidgetMsgId {
	public final static int BASE_ID = 43000;
	
	/**
	 * widget皮肤
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            {@link GoWidgetBaseInfo}
	 * @param objects
	 *            null
	 */
	public final static int WIDGETCHOOSE_SKIN = 43001;


	/**
	 * 卸载开关gowidget
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int GOWIDGET_UNINSTALL_GOWIDGET_SWITCH = 43002;
	
	/**
	 * 添加widget
	 */
	public final static int PICK_WIDGET = 43003;
	
	public final static int SHOW_DOWNLOAD_GOSWITCHWIDGET_DIALOG = 43004;
	
	/**
	 * 删除gowidget后替代为errorview
	 */
	public static final int UPDATE_GOWIDGET = 43005;
}
