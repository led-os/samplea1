package com.jiubang.ggheart.apps.desks.diy.frames.tipsforgl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.golauncher.message.IDiyFrameIds;
import com.jiubang.core.framework.IFrameworkMsgId;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.data.statistics.StaticTutorial;

/**
 * 
 * 类描述:DOCK栏拖动删除 /添加 icon提示自定义视图
 * 功能详细描述:
 * 
 * @author  zhengxiangcan
 * @date  [2012-10-18]
 */
public class GuideDockBarIconView extends RelativeLayout implements OnClickListener {

	private Button mButton; // 按钮got it

	public GuideDockBarIconView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mButton = (Button) findViewById(R.id.got_it);
		mButton.setOnClickListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v == mButton) {
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.REMOVE_FRAME,
					IDiyFrameIds.GUIDE_GL_FRAME, null, null);
			StaticTutorial.sCheckDockBarIcon = false;
			PreferencesManager sharedPreferences = new PreferencesManager(ApplicationProxy.getContext(),
					IPreferencesIds.USERTUTORIALCONFIG, Context.MODE_PRIVATE);
			sharedPreferences.putBoolean(IPreferencesIds.SHOULD_SHOW_DOCK_BAR_ICON_GESTURE, false);
			sharedPreferences.commit();
		}
	}
}
