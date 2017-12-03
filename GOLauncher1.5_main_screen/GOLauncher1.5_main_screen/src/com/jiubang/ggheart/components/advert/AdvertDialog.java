package com.jiubang.ggheart.components.advert;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.BaseDialog;

/**
 * 带图片的广告弹窗（如清理大师，next浏览器）
 * @author wuziyi
 *
 */
public class AdvertDialog extends BaseDialog {
	private static final int TRANSLATE_TEXT = 0;
	private TextView mMsgView; //提示内容
	private TextView mMsgViewScroll; //滚动提示用的后备TextView
	private ImageView mImageView;
	private List<String> mTextList;
	private Handler mHandler;
	private boolean mIsStartScroll;
	
	private final static int TEXT_HOLDING_TIME = 1500;

	public AdvertDialog(Context context) {
		super(context);
		init();
	}

	public AdvertDialog(Context context, int theme) {
		super(context, theme);
		init();
	}
	
	private void init() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int key = msg.what;
				switch (key) {
					case TRANSLATE_TEXT:
						TranslateAnimation translateToLeft = new TranslateAnimation(0, -mMsgView.getWidth(), 0, 0);
						final TranslateAnimation translatefromRight = new TranslateAnimation(mMsgView.getWidth(), 0, 0, 0);
						translateToLeft.setDuration(500);
						translatefromRight.setDuration(500);
						translatefromRight.setStartOffset(250);
						mMsgView.startAnimation(translateToLeft);
						int i = mTextList.indexOf(mMsgView.getText());
						int size = mTextList.size();
						i++;
						if (i >= size) {
							i = 0;
						} else if (i == -1) {
							throw new IllegalAccessError();
						}
						String nextText = mTextList.get(i);
						mMsgViewScroll.setText(nextText);
						mMsgViewScroll.setVisibility(View.VISIBLE);
						mMsgViewScroll.startAnimation(translatefromRight);
						translateToLeft.setAnimationListener(new AnimationListener() {
							
							@Override
							public void onAnimationStart(Animation animation) {
								
							}
							
							@Override
							public void onAnimationRepeat(Animation animation) {
								
							}
							
							@Override
							public void onAnimationEnd(Animation animation) {
								mMsgView.setVisibility(View.GONE);
							}
						});
						translatefromRight.setAnimationListener(new AnimationListener() {
							
							@Override
							public void onAnimationStart(Animation animation) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationEnd(Animation animation) {
								if (mIsStartScroll) {
									mMsgView.setVisibility(View.VISIBLE);
									mMsgView.setText(mMsgViewScroll.getText());
									mMsgViewScroll.setVisibility(View.GONE);
									sendEmptyMessageDelayed(TRANSLATE_TEXT, TEXT_HOLDING_TIME);
								}
							}
						});
						
						break;
	
					default:
						break;
				}
			}
		};
	}

	@Override
	public View getContentView() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = null;
//		Configuration configuration = getContext().getResources().getConfiguration();
//		if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
			view = inflater.inflate(R.layout.advert_dialog, null);
//		} else {
//			view = inflater.inflate(R.layout.advert_dialog_land, null);
//		}
//		mDialogLayout = (LinearLayout) view.findViewById(R.id.dialog_layout);
//		mTitle = (TextView) view.findViewById(R.id.dialog_title);
		mMsgView = (TextView) view.findViewById(R.id.dialog_msg);
//		mOkButton = (Button) view.findViewById(R.id.dialog_ok);
//		mCancelButton = (Button) view.findViewById(R.id.dialog_cancel);
		mImageView = (ImageView) view.findViewById(R.id.image);
		mMsgViewScroll = (TextView) view.findViewById(R.id.dialog_msg_scroll);
//		findTipCheckBox(view);

		return view;
	}

	/**
	 * 设置图片
	 * @param bitmap
	 */
	public void setImageView(Bitmap bitmap) {
		if (mImageView != null & bitmap != null) {
			mImageView.setImageBitmap(bitmap);
		}
	}
	
	/**
	 * 设置图片
	 * @param bitmap
	 */
	public void setImageView(int resId) {
		if (mImageView != null) {
			mImageView.setImageResource(resId);
		}
	}
	
	public void setImageLayoutParams(int wight, int height) {
		mImageView.setLayoutParams(new LayoutParams(wight, height));
	}
	
	public void setImageScaleType(ScaleType scaleType) {
		if (mImageView != null) {
			mImageView.setScaleType(scaleType);
		}
	}


	/**
	 * <br>功能简述:设置显示内容
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param msg
	 */
	public void setMessage(String msg) {
		if (mMsgView != null) {
			mMsgView.setText(msg);
		}
	}

	/**
	 * <br>功能简述:设置显示内容
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param textId
	 */
	public void setMessage(int textId) {
		if (mMsgView != null) {
			mMsgView.setText(getContext().getText(textId));
		}
	}

	/**
	 * <br>功能简述:显示checkBox
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void showTipCheckBox() {
		if (mTipCheckBoxLayout != null) {
			mTipCheckBoxLayout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * <br>功能简述:获取提示框
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public CheckBox getTipCheckBox() {
		return mTipCheckBox;
	}

	/**
	 * <br>功能简述:设置checkBox提示框内容
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param text
	 */
	public void setTipCheckBoxText(CharSequence text) {
		if (mTipCheckBoxText != null) {
			if (text != null) {
				mTipCheckBoxText.setText(text);
			}
		}
	}

	/**
	 * <br>功能简述:设置checkBox提示框内容
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param textId ID
	 */
	public void setTipCheckBoxText(int textId) {
		if (mTipCheckBoxText != null) {
			mTipCheckBoxText.setText(textId);
		}
	}
	
	public boolean isCheckBoxChecked() {
		return mTipCheckBox.isChecked();
	}
	
	public void setScrollText(List<String> textList) {
		mTextList = textList;
	}
	
	public void startScrollText() {
		mIsStartScroll = true;
		mHandler.sendEmptyMessageDelayed(TRANSLATE_TEXT, TEXT_HOLDING_TIME);
	}
	
	public void stopScrollText() {
		mIsStartScroll = false;
		mHandler.removeMessages(TRANSLATE_TEXT);
	}
	
	@Override
	public void dismiss() {
		stopScrollText();
		super.dismiss();
	}
	
	public void setTextLine(int lines) {
		mMsgView.setLines(lines);
		mMsgViewScroll.setLines(lines);
	}
	
	public void setTextViewGravity(int gravity) {
		mMsgView.setGravity(gravity);
		mMsgViewScroll.setGravity(gravity);
	}

}
