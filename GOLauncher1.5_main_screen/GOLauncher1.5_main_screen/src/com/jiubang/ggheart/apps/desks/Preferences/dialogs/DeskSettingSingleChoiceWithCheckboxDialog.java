package com.jiubang.ggheart.apps.desks.Preferences.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingInfo;

/**
 * 
 * <br>类描述:带checkbox的单选对话框
 * <br>功能详细描述:
 * 
 * @author  chenguanyu
 * @date  [2012-9-12]
 */
public class DeskSettingSingleChoiceWithCheckboxDialog extends DeskSettingSingleChoiceDialog {
	

	public DeskSettingSingleChoiceWithCheckboxDialog(Context context,
			DeskSettingInfo deskSettingInfo, OnDialogSelectListener onDialogSelectListener) {
		super(context, deskSettingInfo, onDialogSelectListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initCheckBox();
		setPositiveButtonVisible(View.VISIBLE);
	}
	
	/**
	 * <br>功能简述:初始化CheckBox信息
	 * <br>功能详细描述:判断是否带CheckBox对话框
	 * <br>注意:要修改一下高度和相对位置
	 * @param view
	 */
	private void initCheckBox() {
		mTipCheckBoxLayout.setVisibility(View.VISIBLE);
		mTipCheckBox.setVisibility(View.GONE);
		String checkBoxString = mDeskSettingInfo.getSingleInfo().getCheckBoxString();
		if (checkBoxString != null || !TextUtils.isEmpty(checkBoxString)) {
			mTipCheckBox.setVisibility(View.VISIBLE);
			mTipCheckBoxText.setText(checkBoxString);
		}

		mTipCheckBox.setChecked(mDeskSettingInfo.getSingleInfo().getCheckBoxIsCheck());
	}
	
	@Override
	protected void onOk() {
		mDeskSettingInfo.getSingleInfo().setCheckBoxIsCheck(mTipCheckBox.isChecked());
	}
	
	/**
	 * 重写closeDialog,由于带CheckBox的不需要关闭对话框
	 */
	@Override
	public void closeDialog() {
		//mDingleChoiceDialog.dismiss();
	}

	/**
	 * <br>功能简述:刷新View
	 * <br>功能详细描述:选择自定义后，前一个dialog没有关闭。需要刷新最后一项自定义的显示内容
	 * <br>注意:
	 */
	public void updateView() {
		int custonPostion = mListView.getAdapter().getCount() - 2; //自定义放在最后一个位置
		CharSequence[] entries = mDeskSettingInfo.getSingleInfo().getEntries();
		View view = mListView.getChildAt(custonPostion);
		if (view != null) {
			TextView textView = (TextView) view.findViewById(R.id.desk_setting_dialog_item_text);
			if (textView != null) {
				textView.setText(entries[custonPostion]);
			}
		}
	}
}
