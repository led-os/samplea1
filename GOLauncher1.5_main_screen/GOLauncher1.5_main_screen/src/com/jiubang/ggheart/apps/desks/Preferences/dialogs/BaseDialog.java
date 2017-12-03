package com.jiubang.ggheart.apps.desks.Preferences.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.go.proxy.GoLauncherActivityProxy;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.components.DeskButton;

/**
 * 
 * @author guoyiqing
 *
 */
public abstract class BaseDialog extends Dialog implements IDialog {

	protected LinearLayout mDialogLayout;
	protected TextView mTitle; //标题
	protected LinearLayout mOkButtonLayout; //确认按钮
	protected LinearLayout mCancelButtonLayout; //取消按钮
	protected LinearLayout mOtherButtonLayout; //附加中间按钮
	protected DeskButton mChildOkButton; //确认按钮
	protected DeskButton mChildCancelButton; //取消按钮
	protected DeskButton mChildOtherButton; //附加中间按钮
	protected View.OnClickListener mOkOnClickListener;
	protected View.OnClickListener mCancleOnClickListener;
	private View.OnClickListener mCenterOnClickListener;
	protected LinearLayout mTipCheckBoxLayout;
	protected CheckBox mTipCheckBox;
	protected TextView mTipCheckBoxText;
	private ColorStateList mOkButtonColor;
	
	public BaseDialog(Context context) {
		this(context, R.style.Dialog);
	}

	public BaseDialog(Context context, int theme) {
		super(context, theme);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_dialog_layout);
		mTitle = (TextView) findViewById(R.id.dialog_title);
		mOkButtonLayout = (LinearLayout) findViewById(R.id.dialog_ok);
		mChildOkButton = (DeskButton) findViewById(R.id.child_button_dialog_ok);
		mChildCancelButton = (DeskButton) findViewById(R.id.child_button_dialog_cancel);
		mChildOtherButton = (DeskButton) findViewById(R.id.child_button_dialog_other);
		mOkButtonColor = mChildOkButton.getTextColors();
		mOtherButtonLayout = (LinearLayout) findViewById(R.id.dialog_other);
		mCancelButtonLayout = (LinearLayout) findViewById(R.id.dialog_cancel);
		mDialogLayout = (LinearLayout) findViewById(R.id.dialog_layout);
		mTipCheckBoxLayout = (LinearLayout) findViewById(R.id.tip_layout);
		mTipCheckBox = (CheckBox) findViewById(R.id.tip_check_box);
		mTipCheckBoxText = (TextView) findViewById(R.id.tip_text);
		LinearLayout contentLayout = (LinearLayout) findViewById(R.id.base_dialog_content_layout);
		View view = getContentView();
		if (view != null) {
			contentLayout.addView(view);
		} else {
			throw new RuntimeException("Dialog content view must not null!");
		}
		setButtonListener();
		setDialogWidth(mDialogLayout, getContext());
		int limit = 0;
		if (getContext().getResources().getConfiguration().orientation 
				== Configuration.ORIENTATION_PORTRAIT) {
			limit = (int) (GoLauncherActivityProxy.getScreenHeight() * 0.1f);
		} else {
			limit = (int) (GoLauncherActivityProxy.getScreenWidth() * 0.1f);
		}
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mDialogLayout
				.getLayoutParams();
		params.bottomMargin = limit >> 1;
		params.topMargin = limit >> 1;
	}
	
	public abstract View getContentView();
	
	/**
	 * <br>功能简述:设置对话框的宽度，使用屏幕宽度为横竖屏的宽度
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	protected void setDialogWidth(final View layout, Context context) {
		if (layout != null) {
			int width = GoLauncherActivityProxy.getScreenWidth();
			int pddingWidth = (int) context.getResources().getDimension(
					R.dimen.dialog_padding_width);
			if (GoLauncherActivityProxy.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
				layout.getLayoutParams().width = width - pddingWidth * 2;
			} else {
				layout.getLayoutParams().width = (int) (width * 0.54f);
			}
		}
	}
	
	protected void limitHeight(final View layout, final float percent) {
		layout.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						int h = layout.getMeasuredHeight();
						int limit = 0;
						if (getContext().getResources().getConfiguration().orientation 
								== Configuration.ORIENTATION_PORTRAIT) {
							limit = (int) (GoLauncherActivityProxy.getScreenHeight() * percent);
						} else {
							limit = (int) (GoLauncherActivityProxy.getScreenWidth() * percent);
						}
						if (h > limit) {
							layout.getLayoutParams().height = limit;
							layout.setLayoutParams(layout.getLayoutParams());
							layout.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
						}
					}
				});
	}

	/**
	 * <br>功能简述:设置标题
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param titleString
	 */
	public void setTitle(String titleString) {
		if (mTitle != null) {
			mTitle.setText(titleString);
		}
	}

	/**
	 * <br>功能简述:设置标题
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param titleString
	 */
	public void setTitle(int resId) {
		if (mTitle != null) {
			mTitle.setText(resId);
		}
	}

	public void setTitleVisible(int visible) {
		if (mTitle != null) {
			mTitle.setVisibility(visible);
		}
	}

	
	/**
	 * <br>功能简述:设置确认按钮的visible
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param visible
	 */
	public void setPositiveButtonVisible(int visible) {
		if (mOkButtonLayout != null) {
			mOkButtonLayout.setVisibility(visible);
			findViewById(R.id.dialog_split).setVisibility(visible);
		}
		checkBottomLayout();
	}

	/**
	 * <br>功能简述:设置取消按钮的visible
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param visible
	 */
	public void setNegativeButtonVisible(int visible) {
		if (mCancelButtonLayout != null) {
			mCancelButtonLayout.setVisibility(visible);
		}
		checkBottomLayout();
	}

	private void checkBottomLayout() {
		if (mCancelButtonLayout != null && mCancelButtonLayout.getVisibility() == View.GONE
				&& mOkButtonLayout != null && mOkButtonLayout.getVisibility() == View.GONE
				&& mOtherButtonLayout != null
				&& mOtherButtonLayout.getVisibility() == View.GONE) {
			findViewById(R.id.layout_bottom).setVisibility(View.GONE);
		} else {
			findViewById(R.id.layout_bottom).setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * <br>功能简述:设置取消按钮的visible
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param visible
	 */
	public void setOtherButtonVisible(int visible) {
		if (mOtherButtonLayout != null) {
			mOtherButtonLayout.setVisibility(visible);
			findViewById(R.id.dialog_split_other).setVisibility(visible);
		}
		checkBottomLayout();
	}
	
	/**
	 * <br>功能简述:设置确定按钮点击事件
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param text 按钮显示内容-String
	 * @param listener 监听器
	 */
	public void setPositiveButton(CharSequence text, View.OnClickListener listener) {
		if (mChildOkButton != null) {
			if (text != null) {
				mChildOkButton.setText(text);
			}
			mOkOnClickListener = listener;
		}
	}

	/**
	 * <br>功能简述:设置确定按钮点击事件
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param textId  text 按钮显示内容 - ID
	 * @param listener 监听器
	 */
	public void setPositiveButton(int textId, View.OnClickListener listener) {
		if (mChildOkButton != null) {
			mChildOkButton.setText(getContext().getText(textId));
			mOkOnClickListener = listener;
		}
	}

	/**
	 * <br>功能简述:设置取消按钮点击事件
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param text 按钮显示内容-String
	 * @param listener 监听器
	 */
	public void setNegativeButton(CharSequence text, View.OnClickListener listener) {
		if (mChildCancelButton != null) {
			if (text != null) {
				mChildCancelButton.setText(text);
			}
			mCancleOnClickListener = listener;
		}
	}

	/**
	 * <br>功能简述:设置取消按钮点击事件
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param textId  text 按钮显示内容 - ID
	 * @param listener 监听器
	 */
	public void setNegativeButton(int textId, View.OnClickListener listener) {
		if (mChildCancelButton != null) {
			mChildCancelButton.setText(getContext().getText(textId));
			mCancleOnClickListener = listener;
		}
	}

	/**
	 * <br>功能简述:设置确定按钮点击事件
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param text 按钮显示内容-String
	 * @param listener 监听器
	 */
	public void setOtherButton(CharSequence text, View.OnClickListener listener) {
		if (mChildOtherButton != null) {
			if (text != null) {
				mChildOtherButton.setText(text);
			}
			mCenterOnClickListener = listener;
		}
	}

	/**
	 * <br>功能简述:设置确定按钮点击事件
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param textId  text 按钮显示内容 - ID
	 * @param listener 监听器
	 */
	public void setOtherButton(int textId, View.OnClickListener listener) {
		if (mChildOtherButton != null) {
			mChildOtherButton.setText(getContext().getText(textId));
			mCenterOnClickListener = listener;
		}
	}
	
	/**
	 * <br>功能简述:设置确认和取消按钮的基本点击事件
	 * <br>功能详细描述:关闭对话框
	 * <br>注意:
	 */
	public void setButtonListener() {
		if (mOkButtonLayout != null) {
			mOkButtonLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mOkOnClickListener != null) {
						mOkOnClickListener.onClick(v);	//调用外部设置的监听方法，然后关闭对话框
					}
					dismiss();
				}
			});
		}
		if (mCancelButtonLayout != null) {
			mCancelButtonLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mCancleOnClickListener != null) {
						mCancleOnClickListener.onClick(v);	//调用外部设置的监听方法，然后关闭对话框
					}
					dismiss();
				}
			});
		}
		if (mOtherButtonLayout != null) {
			mOtherButtonLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mCenterOnClickListener != null) {
						mCenterOnClickListener.onClick(v);	//调用外部设置的监听方法，然后关闭对话框
					}
					dismiss();
				}
			});
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		//对话框关闭时遍历所有控件，把DeskView和DeskButton反注册
		DeskSettingConstants.selfDestruct(getWindow().getDecorView());
	}

//	@Override
//	public void show() {
//		super.show();
//		DialogStatusObserver.getInstance().onDialogShow(this);
//	}

//	@Override
//	public void dismiss() {
//		super.dismiss();
//		DialogStatusObserver.getInstance().onDialogDismiss(this);
//	}
	
	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		DialogStatusObserver.getInstance().onDialogDismiss(this);
	}
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		DialogStatusObserver.getInstance().onDialogShow(this);
	}
	
	
	public void setPositiveButtonClickable(boolean clickable) {
		if (mOkButtonLayout != null) {
			if (mOkButtonLayout.isClickable() != clickable) {
				mOkButtonLayout.setClickable(clickable);
				if (clickable) {
					mChildOkButton.setTextColor(mOkButtonColor);
				} else {
					mChildOkButton.setTextColor(Color.GRAY);
				}
			}
		}
	}
}
