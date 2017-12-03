package com.jiubang.ggheart.data.info;

import com.gau.go.launcherex.R;
import com.go.proxy.MsgMgrProxy;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.plugin.shell.IViewId;

/**
 * 最近打开假图标info
 * @author yangguanxiang
 *
 */
public class RecentAppItemInfo extends SpecialAppItemInfo {

	@Override
	public boolean doInvoke(int from) {
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME, ICommonMsgId.SHOW_EXTEND_FUNC_VIEW,
				1, IViewId.RECENT_APP);
		return true;
	}

	@Override
	public int getDefaultIconResId() {
		return R.drawable.recentapp_4_def3;
	}

	@Override
	public int getDefaultTitleResId() {
		return R.string.recentapp_title;
	}
	
	@Override
	public String getPackageName() {
		return PackageName.RECENTAPP_PACKAGE_NAME;
	}

	@Override
	public String getAction() {
		return ICustomAction.ACTION_RECENTAPP;
	}
}
