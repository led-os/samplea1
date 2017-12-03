package com.jiubang.ggheart.apps.desks.diy;

import android.app.Activity;

import com.golauncher.message.IDiyFrameIds;
import com.jiubang.core.framework.AbstractFrame;
import com.jiubang.core.framework.IFrameManager;
import com.jiubang.ggheart.apps.desks.diy.frames.dock.DefaultStyle.autofit.DockAddIconFrame;
import com.jiubang.ggheart.apps.desks.diy.frames.tipsforgl.GuideForGlFrame;
import com.jiubang.ggheart.apps.gowidget.widgetThemeChoose.WidgetThemeChooseFrame;
import com.jiubang.ggheart.plugin.mediamanagement.MediaManagementFrame;
import com.jiubang.ggheart.plugin.mediamanagement.MediaPluginFactory;

/**
 * 帧产生工厂
 * 
 * @author yuankai
 * @version 1.0
 */
class FrameFactory {
	/**
	 * 通过ID产生相应的frame
	 * 
	 * @param frameId
	 *            id
	 * @param theme
	 *            创建frame所需的主题数据
	 * @param globalContext
	 *            全局上下文
	 * @return 创建之后的对象，如果创建失败为return null;
	 */
	static AbstractFrame produce(Activity activity, IFrameManager frameManager, int frameId) {
		AbstractFrame ret = null;
		switch (frameId) {
			case IDiyFrameIds.WIDGET_THEME_CHOOSE : {
				ret = new WidgetThemeChooseFrame(activity, frameManager, frameId);
			}
				break;

			case IDiyFrameIds.GUIDE_GL_FRAME : {
				ret = new GuideForGlFrame(activity, frameManager, frameId);
			}
				break;

			case IDiyFrameIds.IMAGE_BROWSER_FRAME : {
				ret = MediaPluginFactory.getMediaManager().openImageBrowser(activity, frameManager,
						frameId);
			}
				break;
			case IDiyFrameIds.DOCK_ADD_ICON_FRAME : {
				ret = new DockAddIconFrame(activity, frameManager, frameId);
			}
				break;
//			case IDiyFrameIds.SHELL_FRAME : {
//				ret = ShellPluginFactory.getShellManager().getShellFrame(activity, frameManager,
//						frameId);
//			}
//				break;
			case IDiyFrameIds.MEDIA_MANAGEMENT_FRAME : {
				ret = new MediaManagementFrame(activity, frameManager, frameId);
			}
				break;
			default :
				break;
		}
		return ret;
	}
}
