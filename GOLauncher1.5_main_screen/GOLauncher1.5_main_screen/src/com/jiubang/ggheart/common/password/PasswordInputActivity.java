package com.jiubang.ggheart.common.password;

import java.util.List;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.gau.go.gostaticsdk.utiltool.DrawUtils;
import com.gau.go.launcherex.R;
import com.go.proxy.SettingProxy;
import com.go.util.device.Machine;
import com.go.util.window.OrientationControl;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;
import com.jiubang.ggheart.common.controler.InvokeLockControler;
import com.jiubang.ggheart.common.controler.InvokeLockControler.IconInfo;
import com.jiubang.ggheart.common.password.LockPatternView.Cell;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.launcher.IconUtilities;

/**
 * 安全锁解锁界面
 * @author wuziyi
 *
 */
public class PasswordInputActivity extends PasswordActivity
		implements
			View.OnClickListener,
			LockPatternView.OnPatternListener,
			InvokeLockControler.CountDownListener {

	private TextView mAppInfo;
	private View mForgetPassword;

	private String mTopIconText;
	private int mTopIconResId;
	private Bitmap mTopIconBitmap;
	
	private int mErrorCount = 0;
	private boolean mHasPostedSuccess = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.security_keylock);
		super.onCreate(savedInstanceState);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setContentView(R.layout.password_input_layout_port);
		} else {
			setContentView(R.layout.password_input_layout_land);
		}
		
	}

	@Override
	protected void onStart() {
		setupView();
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		updateTopIcon();
		super.onResume();
	}
	
	private void updateTopIcon() {
//		if (null != getIntent()) {
//			mTopIconText = getIntent().getStringExtra(EXTRA_TOP_ICON_TEXT);
//			mTopIconResId = getIntent().getIntExtra(EXTRA_TOP_ICON_RESID, 0);
//			mTopIconBitmap = getIntent().getParcelableExtra(EXTRA_TOP_ICON_BITMAP);
//		}
		IconInfo iconInfo = mControler.getIconInfo();
		mTopIconText = iconInfo.getTitle();
		mTopIconResId = iconInfo.getResourceId();
		mTopIconBitmap = iconInfo.getIcon();

		if (null != mTopIconText && !mTopIconText.equals("")) {
			mAppInfo.setText(mTopIconText);
		}

		Drawable drawable = null;

		if (mTopIconResId > 0) {
			drawable = getResources().getDrawable(mTopIconResId);
		} else if (null != mTopIconBitmap) {
			drawable = new BitmapDrawable(mTopIconBitmap);
		} else {
			drawable = getResources().getDrawable(R.drawable.lock_icon_default);
		}

		if (drawable != null) {
			drawable.setBounds(0, 0, IconUtilities.getIconSize(getApplicationContext()),
					IconUtilities.getIconSize(getApplicationContext()));
			mAppInfo.setCompoundDrawables(null, drawable, null, null);
		}
	}
	
	private void setupView() {

		mAppInfo = (TextView) findViewById(R.id.appInfo);
		mLockSummary = (TextView) findViewById(R.id.lockSummary);
		mLockPatternView = (LockPatternView) findViewById(R.id.lockPattern);
		mForgetPassword = findViewById(R.id.lockForgetPassword);
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			LayoutParams layoutParams = (LayoutParams) mForgetPassword.getLayoutParams();
			int margin = Machine.isTablet(this) ? DrawUtils.dip2px(200) : 0;
			layoutParams.bottomMargin = margin;
		} else if (Machine.isTablet(this)) {
			LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			mLockPatternView.setLayoutParams(layoutParams);
		}
		autoFitLockPatternSzie();
		
		mLockPatternView.clearPattern();
		mLockPatternView.setOnPatternListener(this);
		mForgetPassword.setOnClickListener(this);

		//验证密码
		mLockPatternView.setIsUseSystemBitmap(true);
		updateStage(Stage.CheckPassword);

		if (mControler.getCountNumber() > 0) {
			mLockSummary.setText(getString(R.string.lockpattern_error_summary,
					mControler.getCountNumber()));
			mLockPatternView.disableInput();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			setContentView(R.layout.password_input_layout_port);
		} else {
			setContentView(R.layout.password_input_layout_land);
		}
		
		setupView();
		updateTopIcon();
	}
	
	@Override
	protected void onDestroy() {
//		if (!mHasPostedSuccess) {
//			Log.i("wuziyi", "onDestroy");
//			mResultCallBack.onUnlockFail(mActionId);
//		}
		int oriType = SettingProxy.getGravitySettingInfo().mOrientationType;
		OrientationControl.setOrientation(this, oriType);
		mControler.setConutDownListener(null);
//		if (null != mTopIconBitmap) {
//			mTopIconBitmap.recycle();
//			mTopIconBitmap = null;
//		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (!mHasPostedSuccess) {
					mResultCallBack.onUnlockFail(mActionId);
				}
				break;
	
			default:
				break;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	static final int RESULT_FINISHED = RESULT_FIRST_USER;

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.lockForgetPassword :
				forgetPassord();
				break;
			default :
				break;
		}
	}

	/**
	 * 忘记密码
	 */
	private void forgetPassord() {
			showFindPassWordDialog();
	}

	@Override
	public void onPatternCellAdded(List<Cell> pattern) {
	}

	@Override
	public void onPatternDetected(List<Cell> pattern) {
		String password = mControler.getPassWord();
		if (password == null || password.equals("")) {
			updateStage(Stage.CheckPasswordFaild);
			mErrorCount++;
			postClearPatternRunnable();
		} else {
			if (password.equals(getNumericPwd(pattern))) {
				mHasPostedSuccess = true;
				mResultCallBack.onUnlockSuccess(mActionId);
				finish();
			} else {
				updateStage(Stage.CheckPasswordFaild);
				mErrorCount++;
				postClearPatternRunnable();
			}
		}

		if (mErrorCount >= InvokeLockControler.MAX_ERROR_TIME) {
			mLockPatternView.disableInput();
			mControler.setCountNumber(InvokeLockControler.COOL_DOWN_TIME);
			mControler.startCountDown();
		}
	}


	/**
	 * <br>
	 * 功能简述:没有垃圾数据提示对话框 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	private void showFindPassWordDialog() {
		final String email = InvokeLockControler.getInstance(getApplicationContext()).getEmail();
		final String password = InvokeLockControler.getInstance(getApplicationContext())
				.getPassWord();
		final DialogConfirm findPasswordDialog = new DialogConfirm(this);
		findPasswordDialog.show();
		findPasswordDialog.setTitle(getString(R.string.desksetting_find_password));
		findPasswordDialog.setMessage(getString(R.string.desksetting_send_email) + "(" + email
				+ ")?");
		findPasswordDialog.setNegativeButton(getString(R.string.cancel), new OnClickListener() {

			@Override
			public void onClick(View v) {
				findPasswordDialog.dismiss();
			}
		});
		findPasswordDialog.setPositiveButton(getString(R.string.desksetting_send), new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Machine.isNetworkOK(getApplicationContext())) {
					InvokeLockControler.getInstance(getApplicationContext()).sendData(email,
							password);
					DeskToast.makeText(PasswordInputActivity.this,
							R.string.desksetting_has_been_send, 500).show();
				} else {
					DeskToast.makeText(PasswordInputActivity.this, R.string.desksetting_net_error,
							500).show();
				}
			}
		});
	}


	@Override
	public void updateCount(int number) {
		mLockSummary.setText(getString(R.string.lockpattern_error_summary, number));
		if (number == 0) {
			mLockSummary.setText(R.string.lockscreen_pattern_instructions);
			mLockPatternView.enableInput();
			mErrorCount = 0;
		}
	}

}
