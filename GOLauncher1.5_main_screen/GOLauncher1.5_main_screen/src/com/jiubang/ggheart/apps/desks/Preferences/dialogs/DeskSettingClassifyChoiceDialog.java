package com.jiubang.ggheart.apps.desks.Preferences.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.gau.go.launcherex.R;
import com.go.proxy.GoLauncherActivityProxy;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingClassifyInfo;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingInfo;
import com.jiubang.ggheart.components.DeskButton;
import com.jiubang.ggheart.components.DeskTextView;

/**
 * 分类选择对话框
 * 
 * @author yejijiong
 * 
 */
public class DeskSettingClassifyChoiceDialog extends DeskSettingBaseDialog
		implements OnCheckedChangeListener, OnClickListener,
		OnClassifyDialogSelectListener {

	private View mView;
	private CheckBox mCheckBox;
	private ListView mListView;
	private DeskButton mSingleCancelBtn;
	private DeskButton mMultiOkBtn;
	private DeskButton mMultiCancelBtn;
	private View mSingleBtnLayout;
	private View mMultiBtnLayout;
	/**
	 * 点击一项时是否马上关闭dialog
	 */
	private boolean mNeedDismissDialog = true;

	public DeskSettingClassifyChoiceDialog(Context context,
			DeskSettingInfo deskSettingInfo,
			OnDialogSelectListener onDialogSelectListener) {
		super(context, deskSettingInfo, onDialogSelectListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mView != null && mView.getParent() != null) {
			((ViewGroup) (mView.getParent())).removeView(mView);
		}
		setContentView(mView);
		setDialogWidth(mView, getContext());
		int limit = 0;
		if (getContext().getResources().getConfiguration().orientation 
				== Configuration.ORIENTATION_PORTRAIT) {
			limit = (int) (GoLauncherActivityProxy.getScreenHeight() * 0.1f);
		} else {
			limit = (int) (GoLauncherActivityProxy.getScreenWidth() * 0.1f);
		}
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mView
				.getLayoutParams();
		params.bottomMargin = limit >> 1;
		params.topMargin = limit >> 1;
	}

	@Override
	public View getContentView() {
		DeskSettingClassifyInfo deskSettingClassifyInfo = mDeskSettingInfo
				.getClassifyInfo();
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = inflater.inflate(
				R.layout.desk_setting_dialog_for_classify_choice, null);
		DeskTextView title = (DeskTextView) mView.findViewById(R.id.desk_setting_classify_choice_dialog_title);
		title.setText(deskSettingClassifyInfo.getTitle());
		mSingleCancelBtn = (DeskButton) mView
				.findViewById(R.id.desk_setting_classify_choice_dialog_single_cancel_btn);
		mSingleCancelBtn.setOnClickListener(this);

		mMultiOkBtn = (DeskButton) mView
				.findViewById(R.id.desk_setting_classify_choice_dialog_multi_ok_btn);
		mMultiOkBtn.setOnClickListener(this);

		mMultiCancelBtn = (DeskButton) mView
				.findViewById(R.id.desk_setting_classify_choice_dialog_multi_cancel_btn);
		mMultiCancelBtn.setOnClickListener(this);

		mSingleBtnLayout = mView
				.findViewById(R.id.desk_setting_classify_choice_dialog_single_button);
		mMultiBtnLayout = mView
				.findViewById(R.id.desk_setting_classify_choice_dialog_multi_button);

		mListView = (ListView) mView
				.findViewById(R.id.desk_setting_dialog_classify_choice_dialog_listview);
		DeskSettingClassifyDialogAdapter adapter = new DeskSettingClassifyDialogAdapter(
				mContext, deskSettingClassifyInfo, this);
		mListView.setAdapter(adapter);

		mCheckBox = (CheckBox) mView
				.findViewById(R.id.desk_setting_classify_choice_dialog_checkbox);
		if (deskSettingClassifyInfo.getMultipleValue() == null
				|| deskSettingClassifyInfo.getMultipleValueText().equals("")) {
			mCheckBox.setVisibility(View.GONE);
		} else {
			mCheckBox.setText(deskSettingClassifyInfo.getMultipleValueText());
			mCheckBox.setOnCheckedChangeListener(this);
			if (mDeskSettingInfo.getClassifyInfo().getIsInMultiple()) {
				mCheckBox.setChecked(true);
			} else {
				mCheckBox.setChecked(false);
			}
		}
		return mView;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == mCheckBox) {
			Adapter adapter = mListView.getAdapter();
			if (adapter != null) {
				((DeskSettingClassifyDialogAdapter) adapter)
						.checkBoxStatusChange(isChecked);
			}
			updateCurrentBtn();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mSingleCancelBtn || v == mMultiCancelBtn) {
			dismiss();
		} else if (v == mMultiOkBtn) {
			Adapter adapter = mListView.getAdapter();
			if (adapter != null) {
				DeskSettingClassifyInfo info = mDeskSettingInfo
						.getClassifyInfo();
				CharSequence value = info.getMultipleValue();
				CharSequence[] multiValues = ((DeskSettingClassifyDialogAdapter) adapter)
						.getMultiChooseVlaue();
				info.setCurMultipleChooseValue(multiValues);
				setCurSingleChooseValue(value, false);
				mOnDialogSelectListener.onDialogSelectValue(value);
			}
			dismiss();
		}
	}

	public void setCurSingleChooseValue(CharSequence value, boolean needUpdate) {
		DeskSettingClassifyInfo classifyInfo = mDeskSettingInfo
				.getClassifyInfo();
		classifyInfo.setCurSingleChooseValue(value);
		if (needUpdate) {
			Adapter adapter = mListView.getAdapter();
			if (adapter != null) {
				((DeskSettingClassifyDialogAdapter) adapter)
						.updateChooseImage(value);
			}
		}
	}

	private void updateCurrentBtn() {
		if (mCheckBox.isChecked()) {
			mSingleBtnLayout.setVisibility(View.INVISIBLE);
			mMultiBtnLayout.setVisibility(View.VISIBLE);
		} else {
			mSingleBtnLayout.setVisibility(View.VISIBLE);
			mMultiBtnLayout.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public boolean onDialogSelectValue(Object value) {
		if (value instanceof CharSequence) {
			CharSequence v = (CharSequence) value;
			DeskSettingClassifyInfo classifyInfo = mDeskSettingInfo
					.getClassifyInfo();
			setCurSingleChooseValue(v, false);
			mOnDialogSelectListener.onDialogSelectValue(v);
			if (classifyInfo.getMultipleValue() != null
					&& v != classifyInfo.getMultipleValue()) {
				classifyInfo.setCurMultipleChooseValue(null);
			}
			if (mNeedDismissDialog) {
				dismiss();
			} else {
				mNeedDismissDialog = true;
			}
			return true;
		}
		return false;
	}

	public void setNeedDismissDialog(boolean needDismiss) {
		mNeedDismissDialog = needDismiss;
	}

	@Override
	public void onMultiSelectChange() {
		updateMultiOkBtn();
	}

	private void updateMultiOkBtn() {
		Adapter adapter = mListView.getAdapter();
		CharSequence[] multiValues = ((DeskSettingClassifyDialogAdapter) adapter)
				.getMultiChooseVlaue();
		if (multiValues != null && multiValues.length > 0) {
			mMultiOkBtn.setEnabled(true);
			mMultiOkBtn.setTextColor(mContext.getResources().getColor(
					R.color.desk_setting_button_color));
		} else {
			mMultiOkBtn.setEnabled(false);
			mMultiOkBtn.setTextColor(mContext.getResources().getColor(
					R.color.desk_setting_item_summary_color));
		}
	}

	public void removePrimeImage() {
		Adapter adapter = mListView.getAdapter();
		if (adapter != null) {
			((DeskSettingClassifyDialogAdapter) adapter).removePrimeImage();
		}
	}
}
