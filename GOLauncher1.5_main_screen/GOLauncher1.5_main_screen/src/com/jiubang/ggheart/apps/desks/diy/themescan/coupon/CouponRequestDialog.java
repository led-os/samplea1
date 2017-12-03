package com.jiubang.ggheart.apps.desks.diy.themescan.coupon;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.BaseDialog;

/**
 * 根据优惠券号获取优惠券的对话框
 * @author liulixia
 *
 */
public class CouponRequestDialog extends BaseDialog implements android.view.View.OnClickListener {
	public EditText mCouponId; //优惠券号
	private ImageButton mClearBtn;
	public CouponRequestDialog(Context context) {
		super(context);
	}

	public CouponRequestDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.coupon_get_tilte);
	}
	
	@Override
	public View getContentView() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.coupon_request_dialog, null);
		mCouponId = (EditText) view.findViewById(R.id.coupon_id);
//		mOkButton = (Button) view.findViewById(R.id.dialog_ok);
//		mCancelButton = (Button) view.findViewById(R.id.dialog_cancel);
		mClearBtn = (ImageButton) view.findViewById(R.id.clear_coupon);
		mClearBtn.setOnClickListener(this);;
		mCouponId.setInputType(InputType.TYPE_CLASS_NUMBER);
		mCouponId.addTextChangedListener(new InputTextWatcher());
		mCouponId.requestFocus();
		return view;
	}
/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2013年8月23日]
 */
	private class InputTextWatcher implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (mCouponId.getText().toString().equals("")) {
				mClearBtn.setVisibility(View.GONE);
			} else {
				mClearBtn.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.clear_coupon :
				mCouponId.setText("");
				mClearBtn.setVisibility(View.GONE);
				break;

			default :
				break;
		}
	}

}
