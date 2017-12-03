package com.jiubang.ggheart.components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.BaseDialog;
/**
 * 
 * <br>类描述:單選框
 * <br>功能详细描述:帶有小寫說明文字的單選框
 * 
 * @author  rongjinsong
 * @date  [2013-6-3]
 */
public class SingleChoiceDialog extends BaseDialog {

	private View mView;
	private ListView mListView;
	private OnClickListener mOnClickListener;
	private int mDisableItemPosition = -1;
	private int  mItemMinHeight = -1;
	
	public SingleChoiceDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public SingleChoiceDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getContentView() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = inflater.inflate(R.layout.desk_setting_dialog_for_list, null);

//		mDialogLayout = (LinearLayout) mView.findViewById(R.id.dialog_layout);
//		mTitle = (TextView) mView.findViewById(R.id.desk_setting_dialog_singleormulti_title);

		//默认隐藏
//		mOkButton = (Button) mView.findViewById(R.id.desk_setting_dialog_singleormulti_ok_btn);
		setPositiveButtonVisible(View.GONE); //设置默认隐藏
//		mCancelButton = (Button) mView 
//				.findViewById(R.id.desk_setting_dialog_singleormulti_cancel_btn);

		return mView;
	}

	public void setDisableItemPosition(int position) {
		mDisableItemPosition = position;
	}
	/**
	 * <br>功能简述:listview内容
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param items	内容数组
	 * @param imageId	图片资源数组
	 * @param checkItem	checkBox选择的位置 -1:代表不勾选
	 * @param isShowCheckBox 是否显示checkBox
	 */
	public void setItemData(CharSequence[] items, CharSequence[] summary, int[] imageId,
			int checkItem, boolean isShowCheckBox) {
		SingleChoiceAdapter adapter = new SingleChoiceAdapter(getContext(), items, summary, imageId,
				null, checkItem, isShowCheckBox);
		mListView = (ListView) mView.findViewById(R.id.desk_setting_dialog_singleormulti_list);
		mListView.setAdapter(adapter);
	}
	/**
	 * <br>功能简述:listview内容
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param items	内容数组
	 * @param imageId	图片资源数组
	 * @param checkItem	checkBox选择的位置 -1:代表不勾选
	 * @param isShowCheckBox 是否显示checkBox
	 */
	public void setItemData(CharSequence[] items, CharSequence[] summary, Drawable[] drawables,
			int checkItem, boolean isShowCheckBox) {
		SingleChoiceAdapter adapter = new SingleChoiceAdapter(getContext(), items, summary, null,
				drawables, checkItem, isShowCheckBox);
		mListView = (ListView) mView.findViewById(R.id.desk_setting_dialog_singleormulti_list);
		mListView.setAdapter(adapter);
	}

	public void setOnItemClickListener(OnClickListener listener) {
		if (listener == null || mListView == null) {
			return;
		}

		mOnClickListener = listener;
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mDisableItemPosition == position) {
					return;
				}

				//设置点击的时候单选按钮跳到对应的位置
				RadioButton radioButton = (RadioButton) view
						.findViewById(R.id.singlechoice_dialog_item_radiobtn);
				radioButton.setChecked(true);

				final int count = parent.getChildCount();
				for (int i = 0; i < count; i++) {
					View child = parent.getChildAt(i);
					if (child != null && child != view) {
						radioButton = (RadioButton) child
								.findViewById(R.id.singlechoice_dialog_item_radiobtn);
						radioButton.setChecked(false);
					}
				}
				mOnClickListener.onClick(null, position);	//返回监听方法
				dismiss();
			}
		});
	}
	
	public void setItemMinHeight(int minHeight) {
		mItemMinHeight = minHeight;
	}
	/**
	 * 
	 * <br>类描述:单选和多选对话框的适配器
	 * <br>功能详细描述:
	 * 
	 * @author  licanhui
	 * @date  [2012-9-12]
	 */
	public class SingleChoiceAdapter extends BaseAdapter {
		private Context mContext;
		private CharSequence[] mTitles;
		private int[] mImageId;
		private Drawable[] mImageDrawable;
		private int mCheckItem = -1;
		private boolean mIsShowCheckBox = false;
		private CharSequence[] mSummary;
		/**
		 * <默认构造函数>
		 */
		public SingleChoiceAdapter(Context context, CharSequence[] titles, CharSequence[] summary,
				int[] imageId, Drawable[] imageDrawable, int checkItem, boolean isShowCheckBox) {
			if (context == null || titles == null) {
				return;
			}
			mContext = context;
			mTitles = titles;
			mImageId = imageId;
			mImageDrawable = imageDrawable;
			mCheckItem = checkItem;
			mIsShowCheckBox = isShowCheckBox;
			mSummary = summary;
		}

		@Override
		public int getCount() {
			int count = 0;
			if (mTitles != null) {
				count = mTitles.length;
			}
			return count;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(getContext());
				convertView = inflater.inflate(R.layout.singlechoicedialog_item, null);
			}
			ImageView iconImg = (ImageView) convertView
					.findViewById(R.id.singlechoice_dialog_item_icon);
			TextView textView = (TextView) convertView
					.findViewById(R.id.singlechoice_dialog_item_text);
			TextView summaryTxt = (TextView) convertView
					.findViewById(R.id.singlechoice_dialog_summary);
			if (mSummary == null) {
				textView.setGravity(Gravity.CENTER_VERTICAL);
				summaryTxt.setVisibility(View.GONE);
			}
			final RadioButton singleChoiceButton = (RadioButton) convertView
					.findViewById(R.id.singlechoice_dialog_item_radiobtn);
			if (mItemMinHeight != -1) {
				convertView.setMinimumHeight(mItemMinHeight);
			}
			if (mIsShowCheckBox) {
				singleChoiceButton.setVisibility(View.VISIBLE);
				if (mCheckItem >= 0 && mCheckItem == position) {
					singleChoiceButton.setChecked(true);
				} else {
					singleChoiceButton.setChecked(false);
				}
			} else {
				singleChoiceButton.setVisibility(View.GONE);
			}

			if (mSummary != null && mSummary.length > position) {
				summaryTxt.setText(mSummary[position]);
				DeskSettingConstants.setTextViewTypeFace(summaryTxt);
			}
			if (position < mTitles.length) {
				textView.setText(mTitles[position]);
				//手动更改字体，不用DeskTextView。因为getView会重复注册。导致无法注销
				DeskSettingConstants.setTextViewTypeFace(textView);
			}
			if (position == mDisableItemPosition) {
				textView.setTextColor(Color.GRAY);
				summaryTxt.setTextColor(Color.GRAY);
			} else {
				textView.setTextColor(0xff343434);
				summaryTxt.setTextColor(0xff343434);
			}
			//先判断数组是否有资源
			if (mImageId != null && mImageId.length != 0) {
				if (position < mImageId.length) {
					iconImg.setImageDrawable(getContext().getResources()
							.getDrawable(mImageId[position]));
				} else {
					iconImg.setVisibility(View.GONE);
				}
			} else {
				//判断图片Drawable数组
				if (mImageDrawable == null || mImageDrawable.length == 0) {
					iconImg.setVisibility(View.GONE);
				} else {
					if (position < mImageDrawable.length) {
						iconImg.setImageDrawable(mImageDrawable[position]);
					} else {
						iconImg.setVisibility(View.GONE);
					}
				}
			}
			return convertView;
		}
	}
}
