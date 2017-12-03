package com.jiubang.ggheart.data.info;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.util.device.ConfigurationInfo;
/**
 * 
 * <br>类描述:功能表默认初始值
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2013-6-4]
 */
public class AppSettingDefault {
	public final static int MENUAPPSTYLE = 0;
	public final static int TURNSCREENDIRECTION = 1;
	public final static int APPNAMEVISIABLE = 1;
	public final static int APP_SEARCH_VISIABLE = 0;
	public final static int BACKGROUNDPICPATH = 0;
	public final static int BACKGROUNDVISIABLE = 3;
	public final static int SORTTYPE = 0;
	public final static int SHOWNEGLECTAPPS = 1;
	public final static int INOUTEFFECT = 1;
	public final static int ICONEFFECT = 0;
	public final static int SCROLL_LOOP = 1;
	public final static int BLUR_BACKGROUND = 0;
	public final static int SHOW_TAB_ROW = 1;
	public final static int VERTICAL_SCROLL_EFFECT = 0;
	public final static int DISABLEUPDATEAPP = 0;
	public final static int SELECTINOUTEFFECT = -1;
	public final static int SELECTICONEFFECT = -1;
	public final static int SELECT_VERTICAL_SCROLL_EFFECT = -1;
	public final static int SHOW_HOME_KEY_ONLY = 0;
	public final static int SHOW_ACTION_BAR = 1;
	public final static int ENABLE_GLIDE_UP_ACTION = 0;
	public final static int ENABLE_GLIDE_DOWN_ACTION = 0;

	/**
	 * 功能表默认行列数的样式
	 */
	public final static int LINECOLUMNNUM = ApplicationProxy.getContext().getResources()
			.getInteger(R.integer.appfunc_col_row_style);
	/**
	 * 功能表默认行数
	 */
	public final static int APPFUNC_ROWNUM = ApplicationProxy.getContext().getResources()
			.getInteger(R.integer.appfunc_default_row);
	/**
	 * 功能表默认列数
	 */
	public final static int APPFUNC_COLNUM = ApplicationProxy.getContext().getResources()
			.getInteger(R.integer.appfunc_default_column);
	/**
	 * 功能表是否是否开启动画特效
	 */
	public final static boolean APPFUNC_OPEN_EFFECT = ConfigurationInfo.getDeviceLevel() == ConfigurationInfo.LOW_DEVICE
			? false
			: true;
}
