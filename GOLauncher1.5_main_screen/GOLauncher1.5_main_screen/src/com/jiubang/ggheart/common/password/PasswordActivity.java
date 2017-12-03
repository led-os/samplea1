package com.jiubang.ggheart.common.password;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.gau.go.gostaticsdk.utiltool.DrawUtils;
import com.gau.go.launcherex.R;
import com.go.proxy.SettingProxy;
import com.go.util.device.Machine;
import com.go.util.window.OrientationControl;
import com.jiubang.ggheart.common.controler.InvokeLockControler;
import com.jiubang.ggheart.common.password.LockPatternView.Cell;
import com.jiubang.ggheart.common.password.LockPatternView.DisplayMode;

/**
 * 安全锁解锁界面
 * @author wuziyi
 *
 */
public abstract class PasswordActivity extends Activity
		implements
			View.OnClickListener,
			LockPatternView.OnPatternListener,
			InvokeLockControler.CountDownListener {

	public static final String ACTION_ID = "action_id";
	protected int mActionId;
	protected InvokeLockControler mControler;
	protected ActionResultCallBack mResultCallBack;

	protected TextView mLockSummary;
	protected LockPatternView mLockPatternView;

	protected Stage mUiStage = Stage.Introduction;
	protected List<LockPatternView.Cell> mChosenPattern = null;

//	public static final String EXTRA_TOP_ICON_TEXT = "topIconText";
//	public static final String EXTRA_TOP_ICON_RESID = "topIconResId";
//	public static final String EXTRA_TOP_ICON_BITMAP = "topIconResBitmap";
//	public static final String EXTRA_USE_WALLPAPER = "useWallPaper";

	private static final String KEY_UI_STAGE = "uiStage";
	private static final String KEY_PATTERN_CHOICE = "chosenPattern";

	protected static final int ID_EMPTY_MESSAGE = -1;
	protected static final int WRONG_PATTERN_CLEAR_TIMEOUT_MS = 1500;

	private boolean mHasPostedSuccess = false;

	private Runnable mClearPatternRunnable = new Runnable() {
		@Override
		public void run() {
			if (mLockPatternView != null) {
				mLockPatternView.clearPattern();
			}
		}
	};

	private Runnable mDismessRunnable = new Runnable() {
		public void run() {
			if (mLockPatternView != null) {
				mLockPatternView.clearPattern();
			}
			mResultCallBack.onUnlockFail(mActionId);
			finish();
		};
	};
	
	protected void autoFitLockPatternSzie() {
		if (Machine.isTablet(this)) {
			mLockPatternView.getLayoutParams().height = DrawUtils.dip2px(263);
			mLockPatternView.getLayoutParams().width = DrawUtils.dip2px(267);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.security_keylock);
		super.onCreate(savedInstanceState);

		mActionId = getIntent().getIntExtra(ACTION_ID, -1);
		mControler = InvokeLockControler.getInstance(this);
		mControler.setConutDownListener(this);
		mResultCallBack = mControler.getResultListner();
		if (mResultCallBack == null) {
			finish();
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		final String patternString = savedInstanceState.getString(KEY_PATTERN_CHOICE);
		if (patternString != null) {
			mChosenPattern = LockPatternUtils.stringToPattern(patternString);
		}
		updateStage(Stage.values()[savedInstanceState.getInt(KEY_UI_STAGE)]);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_UI_STAGE, mUiStage.ordinal());
		if (mChosenPattern != null) {
			outState.putString(KEY_PATTERN_CHOICE, LockPatternUtils.patternToString(mChosenPattern));
		}
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
				finish();
				return true;
	
			default:
				break;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	/**
	 * 这个是留给解锁界面，解锁成功／失败后，回调给各模块继续执行后续的业务
	 * @author wuziyi
	 *
	 */
	public interface ActionResultCallBack {
		public void onUnlockSuccess(int actionId);
		public void onUnlockFail(int actionId);
	}

	static final int RESULT_FINISHED = RESULT_FIRST_USER;

	// 将图案密码转换成1-9的数字字符串
	protected String getNumericPwd(List<LockPatternView.Cell> pattern) {
		String password = "";
		for (LockPatternView.Cell cell : pattern) {
			password += cell.mRow * 3 + cell.mColumn + 1;
		}
		return password;
	}

	@Override
	public void onPatternStart() {
		mLockPatternView.removeCallbacks(mClearPatternRunnable);
	}

	@Override
	public void onPatternCleared() {
		mLockPatternView.removeCallbacks(mClearPatternRunnable);
	}

	@Override
	public void onPatternCellAdded(List<Cell> pattern) {
	}

	protected void updateStage(Stage stage) {

		mUiStage = stage;

		if (stage == Stage.ChoiceTooShort) {
			mLockSummary.setText(getResources().getString(stage.mHeaderMessage,
					LockPatternUtils.MIN_LOCK_PATTERN_SIZE));
		} else {
			mLockSummary.setText(stage.mHeaderMessage);
		}

		// same for whether the patten is enabled
		if (stage.mPatternEnabled) {
			mLockPatternView.enableInput();
		} else {
			mLockPatternView.disableInput();
		}

		// the rest of the stuff varies enough that it is easier just to handle
		// on a case by case basis.
		mLockPatternView.setDisplayMode(DisplayMode.Correct);

		switch (mUiStage) {
			case Introduction :
				mLockPatternView.clearPattern();
				break;
			case ChoiceTooShort :
				mLockPatternView.setDisplayMode(DisplayMode.Wrong);
				postClearPatternRunnable();
				break;
			case FirstChoiceValid :
				break;
			case NeedToConfirm :
				mLockPatternView.clearPattern();
				break;
			case ConfirmWrong :
				mLockPatternView.setDisplayMode(DisplayMode.Wrong);
				postClearPatternRunnable();
				break;
			case ChoiceConfirmed :
				break;
			case CheckPasswordFaild :
				mLockPatternView.setDisplayMode(DisplayMode.Wrong);
				break;
			case CheckPassword :
				break;
		}
	}

	// clear the wrong pattern unless they have started a new one
	// already
	protected void postClearPatternRunnable() {
		mLockPatternView.removeCallbacks(mClearPatternRunnable);
		mLockPatternView.postDelayed(mClearPatternRunnable, WRONG_PATTERN_CLEAR_TIMEOUT_MS);
	}

	protected void postDismess() {
		mLockPatternView.disableInput();
		mLockPatternView.postDelayed(mDismessRunnable, WRONG_PATTERN_CLEAR_TIMEOUT_MS);
	}

	/**
	 * 
	 * @author zouguiquan
	 *
	 */
	protected enum Stage {

		Introduction(R.string.lockscreen_pattern_instructions, LeftButtonMode.Cancel,
				RightButtonMode.ContinueDisabled, true), ChoiceTooShort(
				R.string.lockpattern_recording_incorrect_too_short, LeftButtonMode.Retry,
				RightButtonMode.ContinueDisabled, true), FirstChoiceValid(
				R.string.lockpattern_pattern_entered_header, LeftButtonMode.Retry,
				RightButtonMode.Continue, false), NeedToConfirm(
				R.string.lockpattern_need_to_confirm, LeftButtonMode.Cancel,
				RightButtonMode.ConfirmDisabled, true), ConfirmWrong(
				R.string.lockscreen_pattern_wrong, LeftButtonMode.Cancel,
				RightButtonMode.ConfirmDisabled, true), ChoiceConfirmed(
				R.string.lockpattern_pattern_confirmed_header, LeftButtonMode.Cancel,
				RightButtonMode.Confirm, false), CheckPasswordFaild(
				R.string.lockpattern_check_password_faile, LeftButtonMode.Gone,
				RightButtonMode.Gone, true), CheckPassword(
				R.string.lockscreen_pattern_instructions, LeftButtonMode.Gone,
				RightButtonMode.Gone, true);

		/**
		 * @param headerMessage
		 *            The message displayed at the top.
		 * @param leftMode
		 *            The mode of the left button.
		 * @param rightMode
		 *            The mode of the right button.
		 * @param footerMessage
		 *            The footer message.
		 * @param patternEnabled
		 *            Whether the pattern widget is enabled.
		 */
		Stage(int headerMessage, LeftButtonMode leftMode, RightButtonMode rightMode,
				boolean patternEnabled) {
			this.mHeaderMessage = headerMessage;
			this.mLeftMode = leftMode;
			this.mRightMode = rightMode;
			this.mPatternEnabled = patternEnabled;
		}

		final int mHeaderMessage;
		final LeftButtonMode mLeftMode;
		final RightButtonMode mRightMode;
		final boolean mPatternEnabled;
	}

	/**
	 * The states of the left footer button.
	 */
	enum LeftButtonMode {
		Cancel(R.string.cancel, true), CancelDisabled(R.string.cancel, false), Retry(
				R.string.lockpattern_retry_button_text, true), RetryDisabled(
				R.string.lockpattern_retry_button_text, false), Gone(ID_EMPTY_MESSAGE, false);

		/**
		 * @param text
		 *            The displayed text for this mode.
		 * @param enabled
		 *            Whether the button should be enabled.
		 */
		LeftButtonMode(int text, boolean enabled) {
			this.mText = text;
			this.mEnabled = enabled;
		}

		final int mText;
		final boolean mEnabled;
	}

	/**
	 * The states of the right button.
	 */
	enum RightButtonMode {
		Continue(R.string.lockpassword_continue_label, true), ContinueDisabled(
				R.string.lockpassword_continue_label, false), Confirm(
				R.string.lockpattern_confirm_button_text, true), ConfirmDisabled(
				R.string.lockpattern_confirm_button_text, false), Ok(android.R.string.ok, true), Gone(
				ID_EMPTY_MESSAGE, false);

		/**
		 * @param text
		 *            The displayed text for this mode.
		 * @param enabled
		 *            Whether the button should be enabled.
		 */
		RightButtonMode(int text, boolean enabled) {
			this.mText = text;
			this.mEnabled = enabled;
		}

		final int mText;
		final boolean mEnabled;
	}

}

