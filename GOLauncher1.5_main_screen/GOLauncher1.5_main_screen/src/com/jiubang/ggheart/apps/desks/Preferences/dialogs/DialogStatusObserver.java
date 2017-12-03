package com.jiubang.ggheart.apps.desks.Preferences.dialogs;

import android.app.Dialog;

/**
 * Dialog状态观察者，用于Home键事件处理中
 * @author yangguanxiang
 *
 */
public class DialogStatusObserver {

	private static DialogStatusObserver sInstance;

	private IDialog mDialog;

	private Dialog mSimpleDialog;

	public static DialogStatusObserver getInstance() {
		if (sInstance == null) {
			sInstance = new DialogStatusObserver();
		}
		return sInstance;
	}

	public void onDialogShow(IDialog dialog) {
		mDialog = dialog;
	}

	public void onDialogDismiss(IDialog dialog) {
		if (mDialog == dialog) {
			mDialog = null;
		}
	}

	public void onSimpleDialogShow(Dialog dialog) {
		mSimpleDialog = dialog;
	}

	public void onSimpleDialogDismiss(Dialog dialog) {
		if (mSimpleDialog == dialog) {
			mSimpleDialog = null;
		}
	}

	public boolean isDialogShowing() {
		return (mDialog != null && mDialog.isShowing())
				|| (mSimpleDialog != null && mSimpleDialog.isShowing());
	}

	public void dismissDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			try {
				mDialog.dismiss();
			} catch (Exception e) {
				mDialog = null;
			}
		}
		if (mSimpleDialog != null && mSimpleDialog.isShowing()) {
			try {
				mSimpleDialog.dismiss();
			} catch (Exception e) {
				mSimpleDialog = null;
			}
		}
	}
}
