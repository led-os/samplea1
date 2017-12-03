package com.jiubang.ggheart.apps.desks.Preferences.dialogs;

import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import com.gau.go.launcherex.R;
import com.go.proxy.GoLauncherActivityProxy;
import com.jiubang.ggheart.components.DeskActivity;

/**
 * Dialog样式的Activity都继承这个类
 * @author yangguanxiang
 *
 */
public class DialogActivity extends DeskActivity implements IDialog {

	private boolean mIsShowing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (GoLauncherActivityProxy.getActivity() == null) {
			finish();
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTheme(R.style.msg_dialog);
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * @param dialogLayout
	 */
	protected void limitDialogLayout(ViewGroup dialogLayout) {
		LauncherDialogUtils.setDialogWidth(dialogLayout, this);
		LauncherDialogUtils.limitHeight(dialogLayout, this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mIsShowing = true;
		DialogStatusObserver.getInstance().onDialogShow(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mIsShowing = false;
		DialogStatusObserver.getInstance().onDialogDismiss(this);
	}

	@Override
	public boolean isShowing() {
		return mIsShowing;
	}

	@Override
	public void dismiss() {
		finish();
	}

}
