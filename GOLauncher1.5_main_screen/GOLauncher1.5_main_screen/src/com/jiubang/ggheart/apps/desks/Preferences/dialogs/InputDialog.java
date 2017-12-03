package com.jiubang.ggheart.apps.desks.Preferences.dialogs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.components.DeskTextView;
/**
 * 
 * <br>类描述:输入对话框控件
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2014年1月13日]
 */
public class InputDialog extends BaseDialog {

	private EditText mEditText;
	private DeskTextView mMessageText;
	private DeskTextView mSummaryText;
	public InputDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public InputDialog(Context context, int theme) {
		super(context, theme);
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.input_edittext_dialog, null);
		mEditText = (EditText) view.findViewById(R.id.editText);
		mMessageText = (DeskTextView) view.findViewById(R.id.dialog_message);
		mSummaryText = (DeskTextView) view.findViewById(R.id.dialog_summary);
		return view;
	}

	public void setMessage(String message) {
		if (mMessageText.getVisibility() != View.VISIBLE) {
			mMessageText.setVisibility(View.VISIBLE);
		}
		mMessageText.setText(message);
	}

	public void setMessage(int resid) {
		if (mMessageText.getVisibility() != View.VISIBLE) {
			mMessageText.setVisibility(View.VISIBLE);
		}
		mMessageText.setText(resid);
	}

	public String getEditText() {
		if (null != mEditText) {
			return mEditText.getText().toString();
		}
		return null;
	}

	public void setHint(int resid) {
		mEditText.setHint(resid);
	}

	public void setHintColor(int color) {
		mEditText.setHintTextColor(color);
	}

	public void setHintColor(ColorStateList colors) {
		mEditText.setHintTextColor(colors);
	}

	public void setEditTextColor(int color) {
		mEditText.setTextColor(color);
	}

	public void setEditTextSize(float size) {
		mEditText.setTextSize(size);
	}

	public void setInputType(int type) {
		mEditText.setInputType(type);
	}
	public void setMessageTextSize(float size) {
		mMessageText.setTextSize(size);
	}

	public void setMessageTextColor(int color) {
		mMessageText.setTextColor(color);
	}

	public void setMessageTextColor(ColorStateList color) {
		mMessageText.setTextColor(color);
	}

	public void setSummaryText(int resid) {
		if (mSummaryText.getVisibility() != View.VISIBLE) {
			mSummaryText.setVisibility(View.VISIBLE);
		}
		mSummaryText.setText(resid);
	}

	public void setSummaryText(String summery) {
		if (mSummaryText.getVisibility() != View.VISIBLE) {
			mSummaryText.setVisibility(View.VISIBLE);
		}
		mSummaryText.setText(summery);
	}

	public void setSummaryTextSize(float size) {
		mSummaryText.setTextSize(size);
	}

	public void setSummaryTextColor(int color) {
		mSummaryText.setTextColor(color);
	}

	public void setSummaryTextColor(ColorStateList color) {
		mSummaryText.setTextColor(color);
	}


	public void setMaxInputLength(int max) {
		mEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(max) });
	}
	
	public void addInputTextWatcher(TextWatcher watcher) {
		if (watcher != null) {
			mEditText.addTextChangedListener(watcher);
		}
	}

	public String getInputContent() {
		return mEditText.getText().toString();
	}

}
