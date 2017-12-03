package com.jiubang.ggheart.apps.desks.Preferences.dialogs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.components.DeskTextView;

/**
 * 
 * @author zouguiquan
 *
 */
public class EmailInputDialog extends BaseDialog {

	private DeskTextView mMessageText;
	private DeskTextView mSummaryText;
	private EditText mEditText;
	private String mTextColorError = "#ff0000";
	private String mRegularStr = "[a-zA-Z0-9-_\\.]+@[a-zA-Z0-9-_\\.]+[a-z]{2,3}";
//	private String mRegularStr = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
	private Pattern mPattern;

	public EmailInputDialog(Context context) {
		super(context);
		mPattern = Pattern.compile(mRegularStr);
	}

	public EmailInputDialog(Context context, int theme) {
		super(context, theme);
		mPattern = Pattern.compile(mRegularStr);
	}


	@Override
	public View getContentView() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.desk_setting_edittext_dialog, null);
		mEditText = (EditText) view.findViewById(R.id.editText);
		mMessageText = (DeskTextView) view.findViewById(R.id.dialog_message);
		mSummaryText = (DeskTextView) view.findViewById(R.id.dialog_summary);
		return view;
	}

	public void setMessage(String message) {
		if (null != mMessageText) {
			mMessageText.setText(message);
		}
	}

	public void setMessage(int messageRes) {
		if (null != mMessageText) {
			mMessageText.setText(messageRes);
		}
	}

	public String getEditText() {
		if (null != mEditText) {
			return mEditText.getText().toString();
		}
		return null;
	}

	public boolean isEmailAddress() {
		String address = getEditText();
		if (null == address || address.equals("")) {
			return false;
		} else {
			return checkEmailFormat(address);
		}
	}

	public void showErrorSummary() {
		mEditText.setTextColor(Color.parseColor(mTextColorError));
		mSummaryText.setVisibility(View.VISIBLE);
		String editText = getEditText();
		if (editText == null || editText.equals("")) {
			mSummaryText.setText(R.string.lockpattern_email_empty);
		} else {
			mSummaryText.setText(R.string.lockpattern_email_format_error);
		}
	}

	public boolean checkEmailFormat(String email) {
		if (null == mPattern) {
			mPattern = Pattern.compile(mRegularStr);
		}
		Matcher m = mPattern.matcher(email);
		return m.matches();
	}

	public void setDefaultText(String text) {
		mEditText.setText(text);
	}
	
	public void setPositiveButton(int textId, View.OnClickListener listener) {
		if (mChildOkButton != null) {
			mChildOkButton.setText(getContext().getText(textId));
		}
		mOkButtonLayout.setOnClickListener(listener);
	}
	
	public void setNegativeButton(int textId, View.OnClickListener listener) {
		if (mChildCancelButton != null) {
			mChildCancelButton.setText(getContext().getText(textId));
		}
		mCancelButtonLayout.setOnClickListener(listener);
	}
	
}
