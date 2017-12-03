package com.jiubang.ggheart.apps.desks.diy.frames.screen;

import com.jiubang.ggheart.data.info.ScreenAppWidgetInfo;
import com.jiubang.ggheart.data.info.UserFolderInfo;

/**
 * 屏幕UI回调接口
 * @author yejijiong
 *
 */
public interface IScreenObserver {

	public void updateFolderIconAsync(final UserFolderInfo folderInfo, boolean reload,
			boolean checkDel);
	
	public void restoreAppWidget(ScreenAppWidgetInfo screenAppWidgetInfo);
	
}
