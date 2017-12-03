package com.jiubang.ggheart.apps.desks.Preferences.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.components.DeskResourcesConfiguration;
import com.jiubang.ggheart.data.statistics.Statistics;

/**
 * 
 * <br>类描述:每项控件的基础View
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2012-9-13]
 */
@SuppressLint("ResourceAsColor")
public class DeskSettingItemBaseView extends RelativeLayout
		implements
			OnClickListener,
			IDeskSettingI18nSupporter {
	private Context mContext;
	private Intent mIntent; //执行的Intent
	private int mRequestCode = -1;
	protected View mBaseView; //布局View
	private TextView mTitleTextView; //主要内容
	private TextView mSummagyTextView; //summary内容
	private ImageView mImageNew; //新标识的图片
	private ImageView mImagePrime; //付费标识的图片
	private ImageView mBottomLine; //底部线条
	
	//设置页面的促销引导标识
	private ImageView mDeskSettingPrime;

	public DeskSettingItemBaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DeskSettingItemView);

		//显示图片
		Drawable imageDrawable = a.getDrawable(R.styleable.DeskSettingItemView_image);

		//标题内容
		CharSequence titleText = a.getText(R.styleable.DeskSettingItemView_titleText);

		//标题颜色
		int titleTextColor = a.getColor(R.styleable.DeskSettingItemView_titleTextColor,
				getResources().getColor(R.color.desk_setting_item_title_color));

		//标题字体大小，获取的字体大小为PX
		float titleTextSize = a.getDimension(R.styleable.DeskSettingItemView_titleTextSize, context
				.getResources().getDimension(R.dimen.desk_setting_item_title_text_default_size));

		//注解内容
		CharSequence summaryText = a.getText(R.styleable.DeskSettingItemView_summaryText);

		//注解字体颜色
		int summaryTextColor = a.getColor(R.styleable.DeskSettingItemView_summaryTextColor, context
				.getResources().getColor(R.color.desk_setting_item_summary_color));

		//注解字体大小
		float summaryTextSize = a.getDimension(
				R.styleable.DeskSettingItemView_summaryTextSize,
				context.getResources().getDimension(
						R.dimen.desk_setting_item_summary_text_default_size));

		//是否隐藏底部白线
		Boolean isHiddenBottomLine = a.getBoolean(
				R.styleable.DeskSettingItemView_isHiddenBottomLine, false);
		
		Boolean isTitleTextBold = a.getBoolean(R.styleable.DeskSettingItemView_titleTextIsBold, false);
		
		int itemHeight = (int) a.getDimension(R.styleable.DeskSettingItemView_titleHeight, -1);

		a.recycle();

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mBaseView = inflater.inflate(R.layout.desk_setting_item_base_view, this);
		mBaseView.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.change_icon_tab_selector));
		
		View rootView = mBaseView.findViewById(R.id.rootView);
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rootView.getLayoutParams();
		if (itemHeight != -1) {
			layoutParams.height = itemHeight;
		}

		//图片
		ImageView imageView = (ImageView) mBaseView.findViewById(R.id.image);
		imageView.setImageDrawable(imageDrawable);

		//内容
		mTitleTextView = (TextView) mBaseView.findViewById(R.id.title);
		mTitleTextView.setTextColor(titleTextColor);
		if (titleText != null) {
			mTitleTextView.setText(titleText);
		}
		mTitleTextView.setTextSize(DrawUtils.px2sp(titleTextSize)); //需要把PX转化成SP
		if (isTitleTextBold) {
			mTitleTextView.getPaint().setFakeBoldText(true);
		}
		

		//summary
		mSummagyTextView = (TextView) mBaseView.findViewById(R.id.summary);
		mSummagyTextView.setTextSize(DrawUtils.px2sp(summaryTextSize)); //需要把PX转化成SP
		mSummagyTextView.setTextColor(summaryTextColor);
		if (summaryText != null && !summaryText.equals("")) {
			mSummagyTextView.setVisibility(View.VISIBLE);
			mSummagyTextView.setText(summaryText);
		} else {
			mSummagyTextView.setVisibility(View.GONE);
		}

		mImageNew = (ImageView) mBaseView.findViewById(R.id.new_image);
		
		mImagePrime = (ImageView) mBaseView.findViewById(R.id.prime_image);
		mDeskSettingPrime = (ImageView) mBaseView.findViewById(R.id.desk_setting_prime_image);
		//是否隐藏底部分割线
		if (isHiddenBottomLine) {
			mBottomLine = (ImageView) mBaseView.findViewById(R.id.bottomLine);
			mBottomLine.setVisibility(View.INVISIBLE);
		}

		setOnClickListener(this);
		
		DeskResourcesConfiguration resConfig = DeskResourcesConfiguration.getInstance();
		if (resConfig != null) {
			resConfig.configurationDeskSettingI18nSupporter(this, attrs);
		}
	}

	@Override
	public void onClick(View v) {
		if (mIntent != null) {
			if (mRequestCode != -1 && mContext instanceof Activity) {
				((Activity) mContext).startActivityForResult(mIntent, mRequestCode);
			} else {
				mContext.startActivity(mIntent);
			}
		}
	}

	/**
	 * <br>功能简述:设置需要打开的Intent
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param intent
	 */
	public void setOpenIntent(Intent intent) {
		mIntent = intent;
	}

	public void setOpenIntent(Intent intent, int requestCode) {
		mIntent = intent;
		mRequestCode = requestCode;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (enabled) {
			setTitleColor(R.color.desk_setting_item_title_color);
		} else {
			setTitleColor(R.color.desk_setting_item_summary_color); //设置title颜色变灰
		}
	}

	/**
	 * <br>功能简述:设置底部线条是否显示
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param visible
	 */
	public void setBottomLineVisible(int visible) {
		if (mBottomLine != null) {
			mBottomLine.setVisibility(visible);
		}
	}

	/**
	 * <br>功能简述:设置标题颜色
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param color
	 */
	public void setTitleColor(int colorRes) {
		if (mTitleTextView != null) {
			mTitleTextView.setTextColor(mContext.getResources().getColor(colorRes));
		}
	}

	/**
	 * <br>功能简述:设置summary颜色
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param color
	 */
	public void setSummaryColor(int color) {
		if (mSummagyTextView != null) {
			mSummagyTextView.setTextColor(mContext.getResources().getColor(color));
		}
	}
	
	/**
	 * <br>功能简述:设置标题
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param resId 资源ID
	 */
	@Override
	public void setTitleText(int resId) {
		setTitleText(mContext.getString(resId));
	}

	/**
	 * <br>功能简述:设置标题
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param summaryString 显示string
	 */
	public void setTitleText(CharSequence titleString) {
		if (mTitleTextView != null && !titleString.equals("")) {
			mTitleTextView.setText(titleString);
		}
	}
	
	/**
	 * 获取title的textview
	 * @return
	 */
	public TextView getTitleTextView() {
		return mTitleTextView;
	}

	/**
	 * 获取summary
	 */
	public String getSummaryText() {
		String summary = null;
		if (mSummagyTextView != null) {
			summary = mSummagyTextView.getText().toString();
		}
		return summary;
	}
	
	/**
	 * <br>功能简述:设置简介的显示
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param resId 资源ID
	 */
	@Override
	public void setSummaryText(int resId) {
		setSummaryText(mContext.getString(resId));
	}

	/**
	 * <br>功能简述:设置简介的显示
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param summaryString 显示string
	 */
	public void setSummaryText(CharSequence summaryString) {
		if ((summaryString == null && mSummagyTextView != null)
				|| (summaryString != null && !summaryString.equals(mSummagyTextView.getText()))) {
			mSummagyTextView.setText(summaryString);
			int visibility = summaryString == null ? View.GONE : View.VISIBLE;
			mSummagyTextView.setVisibility(visibility);
		}
	}

	/**
	 * <br>功能简述:设置summary可见性
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param enable
	 */
	public void setSummaryEnabled(boolean enable) {
		if (enable) {
			mSummagyTextView.setVisibility(View.VISIBLE);
		} else {
			mSummagyTextView.setVisibility(View.GONE);
		}
	}
	
	/**
	 * <br>功能简述:设置new标识的显示状态
	 * <br>功能详细描述:
	 * <br>注意: 韩国区 去掉 NEW 标签
	 * @param visibility
	 */
	public void setImageNewVisibile(int visibility) {
//		if (/*!Machine.isKorea(mContext)
//				||*/ FunctionPurchaseManager.getInstance(mContext.getApplicationContext())
//						.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_FULL)) {
			if (mImageNew != null) {
				mImageNew.setVisibility(visibility);
			}
//		}
		
	}
	
	/**
	 * <br>功能简述:设置new标识的显示状态   200渠道不显示Prime
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param visibility
	 */
	public void setImagePrimeVisibile(int visibility) {
		if (Statistics.is200ChannelUid(mContext)) {
			if (mImagePrime != null) {
				mImagePrime.setVisibility(visibility);
			}
		}
		
	}
	
	/**
     * <br>功能简述:设置new标识的显示状态   200渠道不显示Prime
     * <br>功能详细描述:
     * <br>注意:
     * @param visibility
     */
    public void setImageDeskSettingPrimeVisibile(int visibility, int resourceId) {
        if (Statistics.is200ChannelUid(mContext)) {
            if (mDeskSettingPrime != null) {
                mDeskSettingPrime.setImageDrawable(mContext.getResources().getDrawable(resourceId));
                mDeskSettingPrime.setVisibility(visibility);
            }
        }
        
    }
	
	public void setSummaryImage(int resId) {
		LinearLayout checkBoxLayout = (LinearLayout) findViewById(R.id.checkbox_layout);
		if (checkBoxLayout != null) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			int paddingRight = (int) mContext.getResources().getDimension(
					R.dimen.desk_setting_checkbox_padding_right);
			if (resId > 0) {
				ImageView imageView = new ImageView(getContext());
				checkBoxLayout.setPadding(0, 0, paddingRight, 0);
				imageView.setBackgroundResource(resId);
				imageView.setLayoutParams(params);
				checkBoxLayout.addView(imageView);
			}
		}
	}
	
}
