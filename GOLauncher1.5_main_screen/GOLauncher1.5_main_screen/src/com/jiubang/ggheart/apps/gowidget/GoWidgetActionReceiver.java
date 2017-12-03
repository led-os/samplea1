package com.jiubang.ggheart.apps.gowidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.proxy.MsgMgrProxy;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.message.ICommonMsgId;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.launcher.ICustomAction;

/**
 * 
 * @author zhouxuewen
 *
 */
public class GoWidgetActionReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Bundle bundle = intent.getExtras();
		if (action.equals(ICustomAction.ACTION_CONFIG_FINISH)) {
			int widgetid = bundle.getInt(GoWidgetConstant.GOWIDGET_ID, 0);
			if (GoWidgetManager.isGoWidget(widgetid)) {
				// 添加到桌面上
//				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_FRAME, IDiyMsgIds.ADD_GO_WIDGET,
//						-1, bundle, null);

			}
		} else if (action.equals(ICustomAction.ACTION_REQUEST_FOCUS)) {
			int widgetId = bundle.getInt(GoWidgetConstant.GOWIDGET_ID, 0);
			if (GoWidgetManager.isGoWidget(widgetId)) {
				// 返回到桌面
				MsgMgrProxy.sendBroadcast(this, ICommonMsgId.BACK_TO_MAIN_SCREEN, -1);

				// 通知屏幕跳转到widget所在的屏幕
//				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_FRAME,
//						IDiyMsgIds.SCREEN_WIDGET_REQUEST_FOCUS, widgetId, null, null);
			}
		} else if (action.equals(ICustomAction.ACTION_CHANGE_WIDGETS_THEME)) {
			// 大主题，通知桌面，所有放在桌面的widget更换皮肤
			String pkgName = intent.getStringExtra(ICustomAction.WIDGET_THEME_KEY);
			MsgMgrProxy.sendBroadcastHandler(this, IAppCoreMsgId.EVENT_CHANGE_WIDGET_THEME, 0, pkgName,
					null);
		} else if (action.equals(ICustomAction.ACTION_GOTO_GOWIDGET_FRAME)) {
			// 退出主题预览界面
			// 改为跳转至添加界面
//			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_FRAME,
//					IScreenEditMsgId.SCREENEDIT_ENTER_SCREEN_EDIT_LAYOUT, 1, ScreenEditConstants.TAB_GOWIDGET, null);

		} else if (action.equals(ICustomAction.ACTION_GOSTORE_DESTORY)) {
			//退出GO精品，显示Toast
			DeskToast.makeText(context,
					context.getString(R.string.gostore_destory_toast), Toast.LENGTH_LONG)
					.show();	
		}
	}
}
