package com.jiubang.ggheart.common.password;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.go.util.window.OrientationControl;

/**
 * 遮罩型密码输入
 * @author wuziyi
 *
 */
public class PasswordInputCoverActivity extends PasswordInputActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
		OrientationControl.keepCurrentOrientation(this);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
					mResultCallBack.onUnlockFail(mActionId);
				return true;
	
			default:
				break;
		}
		return super.onKeyUp(keyCode, event);
	}
	
}
