package com.jiubang.ggheart.apps.desks.Preferences.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.OnClassifyDialogSelectListener;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingClassifyGridItemInfo;

/**
 * 分类选择框子组建
 * @author yejijiong
 *
 */
public class DeskSettingClassifyDialogGridItemView extends RelativeLayout implements OnClickListener {
	private Context mContext;
	private DeskSettingClassifyGridItemInfo mItemInfo;
	private TextView mTitle;
	private ImageView mChooseImageView;
	private ImageView mPrimeImageView;
	private ImageView mIconView;
	private OnClassifyDialogSelectListener mListener;
	
	public DeskSettingClassifyDialogGridItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setOnClickListener(this);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mTitle = (TextView) findViewById(R.id.title);
		mChooseImageView = (ImageView) findViewById(R.id.choose_image_view);
		mPrimeImageView = (ImageView) findViewById(R.id.prime_image_view);
		mIconView = (ImageView) findViewById(R.id.image);
		mIconView.setBackgroundResource(R.drawable.desk_setting_effector_icon_bg);
	}
	
	public void setDialogSelectListener(OnClassifyDialogSelectListener listener) {
		mListener = listener;
	}
	
	public void setItemInfo(DeskSettingClassifyGridItemInfo itemInfo) {
		mItemInfo = itemInfo;
		mIconView.setImageDrawable(itemInfo.getIcon());
		mIconView.setBackgroundDrawable(itemInfo.getIconBg());
		mTitle.setText(itemInfo.getTitle());
		updateChooseStatus();
	}
	
	@Override
	public void onClick(View v) {
		if (mItemInfo.getCurStatus() == DeskSettingClassifyGridItemInfo.STATUS_MULTI_CHOOSE
				&& mItemInfo.isMultipleCanChoose()) {
			mItemInfo.setIsCurMultipleChoose(!mItemInfo.isCurMultipleChoose());
			if (mListener != null) {
				mListener.onMultiSelectChange();
			}
			updateChooseStatus();
		} else if (mItemInfo.getCurStatus() == DeskSettingClassifyGridItemInfo.STATUS_SINGLE_CHOOSE) {
			mItemInfo.setIsCurSingleChoose(true);
			if (mListener != null) {
				mListener.onDialogSelectValue(mItemInfo.getEntryValue());
			}
			updateChooseStatus();
		}
	}
	
	private void updateChooseStatus() {
		if (mItemInfo != null) {
			setEnabled(true);
			if (mItemInfo.isPrime()) {
				mPrimeImageView.setVisibility(View.VISIBLE);
			} else {
				mPrimeImageView.setVisibility(View.GONE);
			}
			if (mItemInfo.getCurStatus() == DeskSettingClassifyGridItemInfo.STATUS_SINGLE_CHOOSE
					&& mItemInfo.isCurSingleChoose()) {
				// 单选状态并且选中
				mChooseImageView.setVisibility(View.VISIBLE);
			} else if (mItemInfo.getCurStatus() == DeskSettingClassifyGridItemInfo.STATUS_MULTI_CHOOSE) {
				if (!mItemInfo.isMultipleCanChoose()) {
					setEnabled(false);
				} else if (mItemInfo.isCurMultipleChoose()) {
					// 多选下可选并且选中的
					mChooseImageView.setVisibility(View.VISIBLE);
				} else {
					mChooseImageView.setVisibility(View.INVISIBLE);
				}
			} else {
				mChooseImageView.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (enabled) {
			mTitle.setTextColor(Color.parseColor("#FF6f6f6f"));
			mIconView.setImageDrawable(mItemInfo.getIcon());
		} else {
			mTitle.setTextColor(Color.parseColor("#806f6f6f"));
			mChooseImageView.setVisibility(View.INVISIBLE);
			Bitmap bitmap = mItemInfo.getUnableIcon();
			if (bitmap == null) {
				bitmap = DeskSettingUtils.filteDrwableColor(
						mContext.getResources(),
						((BitmapDrawable) mItemInfo.getIcon()).getBitmap());
				if (bitmap != null) {
					mItemInfo.setUnableIcon(bitmap);
					mIconView.setImageBitmap(bitmap);
				}
			} else {
				mIconView.setImageBitmap(bitmap);
			}
		}
	}
	
}
