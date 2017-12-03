package com.jiubang.ggheart.common.password;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.gau.go.gostaticsdk.utiltool.DrawUtils;
import com.gau.go.launcherex.R;
import com.go.proxy.SettingProxy;
import com.go.util.device.Machine;
import com.go.util.window.OrientationControl;
import com.jiubang.ggheart.common.controler.InvokeLockControler;
import com.jiubang.ggheart.common.password.LockPatternView.Cell;

/**
 * 安全锁解锁界面
 * @author wuziyi
 *
 */
public class SetupPasswordActivity extends PasswordActivity {

	public static final String ACTION_ID = "action_id";

	private Button mLeftBtn;
	private Button mRightBtn;

	public static final String EXTRA_INIT_PASSWORD = "setPassword";
	public static final String EXTRA_TOP_ICON_TEXT = "topIconText";
	public static final String EXTRA_TOP_ICON_RESID = "topIconResId";
	public static final String EXTRA_TOP_ICON_BITMAP = "topIconResBitmap";
	public static final String EXTRA_USE_WALLPAPER = "useWallPaper";

	private static final int ID_EMPTY_MESSAGE = -1;

	private boolean mHasPostedSuccess = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setContentView(R.layout.password_edit_layout);
		

		setupView();
	}

	private void setupView() {

		mLockSummary = (TextView) findViewById(R.id.lockSummary);
		mLockPatternView = (LockPatternView) findViewById(R.id.lockPattern);
		mLeftBtn = (Button) findViewById(R.id.lockConfirm);
		mRightBtn = (Button) findViewById(R.id.lockRightBtn);
		LayoutParams layoutParams = (LayoutParams) mLockPatternView.getLayoutParams();
		layoutParams.bottomMargin = Machine.isTablet(this) ? DrawUtils.dip2px(200) : 0;
		autoFitLockPatternSzie();
		mLockPatternView.clearPattern();
		mLockPatternView.setOnPatternListener(this);
		if (null != mLeftBtn) {
			mLeftBtn.setOnClickListener(this);
		}
		if (null != mRightBtn) {
			mRightBtn.setOnClickListener(this);
		}

		//设置密码
		mLockPatternView.setIsUseSystemBitmap(false);
		updateStage(Stage.Introduction);

		mActionId = getIntent().getIntExtra(ACTION_ID, -1);
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
			case R.id.lockConfirm :

				if (mUiStage.mLeftMode == LeftButtonMode.Retry) {
					mChosenPattern = null;
					mLockPatternView.clearPattern();
					updateStage(Stage.Introduction);
				} else if (mUiStage.mLeftMode == LeftButtonMode.Cancel) {
					// They are canceling the entire wizard
					setResult(RESULT_FINISHED);
					finish();
				} else {
					throw new IllegalStateException("left footer button pressed, but stage of "
							+ mUiStage + " doesn't make sense");
				}
				break;
			case R.id.lockRightBtn :

				if (mUiStage.mRightMode == RightButtonMode.Continue) {
					if (mUiStage != Stage.FirstChoiceValid) {
						throw new IllegalStateException("expected ui stage "
								+ Stage.FirstChoiceValid + " when button is "
								+ RightButtonMode.Continue);
					}
					updateStage(Stage.NeedToConfirm);
				} else if (mUiStage.mRightMode == RightButtonMode.Confirm) {
					if (mUiStage != Stage.ChoiceConfirmed) {
						throw new IllegalStateException("expected ui stage "
								+ Stage.ChoiceConfirmed + " when button is "
								+ RightButtonMode.Confirm);
					}
					saveChosenPatternAndFinish();
				}
				break;
			default :
				break;
		}
	}

	private void saveChosenPatternAndFinish() {
		LockPatternUtils.clearLock();
		//LockPatternUtils.saveLockPattern(mChosenPattern);
		//SharedPreferences sharedPreferences = PreferenceManager
		//			.getDefaultSharedPreferences(getApplicationContext());
		//Editor editor = sharedPreferences.edit();
		//editor.putInt(LockPatternUtils.PREF_KEY_LOCK_TYPE, LockPatternUtils.LOCK_TYPE_PATTERN);
		//editor.commit();
		String passwordNum = getNumericPwd(mChosenPattern);
		mControler.setPassWord(InvokeLockControler.PASSWORD_SET_ONE, passwordNum);
		mResultCallBack.onUnlockSuccess(mActionId);

		//Intent intent = new Intent();
		//intent.putExtra(LockPatternUtils.LOCK_PASSWORD, passwordNum);
		//intent.putExtra(LockPatternUtils.REQUEST_TYPE, LockPatternUtils.LOCK_TYPE_PATTERN);
		//setResult(LockPatternUtils.REQUEST_SAVE_PASSWORD_OK, intent);
		finish();
	}

	@Override
	public void onPatternCellAdded(List<Cell> pattern) {
	}

	@Override
	public void onPatternDetected(List<Cell> pattern) {
		if (mUiStage == Stage.NeedToConfirm || mUiStage == Stage.ConfirmWrong) {
			if (mChosenPattern == null) {
				throw new IllegalStateException("null chosen pattern in stage 'need to confirm");
			}
			if (mChosenPattern.equals(pattern)) {
				updateStage(Stage.ChoiceConfirmed);
			} else {
				updateStage(Stage.ConfirmWrong);
			}
		} else if (mUiStage == Stage.Introduction || mUiStage == Stage.ChoiceTooShort) {
			if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
				updateStage(Stage.ChoiceTooShort);
			} else {
				mChosenPattern = new ArrayList<LockPatternView.Cell>(pattern);
				updateStage(Stage.FirstChoiceValid);
			}
		} else {
			throw new IllegalStateException("Unexpected stage " + mUiStage + " when "
					+ "entering the pattern.");
		}
	}

	@Override
	protected void updateStage(Stage stage) {
		super.updateStage(stage);
		if (null != mLeftBtn) {
			mLeftBtn.setText(stage.mLeftMode.mText);
			mLeftBtn.setEnabled(stage.mLeftMode.mEnabled);
		}
		if (null != mRightBtn) {
			mRightBtn.setText(stage.mRightMode.mText);
			mRightBtn.setEnabled(stage.mRightMode.mEnabled);
		}
	}

	@Override
	public void updateCount(int number) {
		mLockSummary.setText(getString(R.string.lockpattern_error_summary, number));
		if (number == 0) {
			mLockSummary.setText(R.string.lockscreen_pattern_instructions);
			mLockPatternView.enableInput();
		}
	}

}
