package com.jiubang.ggheart.apps.desks.diy.filter.view;

/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.gau.go.launcherex.R;
import com.go.util.graphics.BitmapUtility;
import com.go.util.graphics.DrawUtils;
import com.go.util.graphics.HolographicOutlineHelper;
import com.jiubang.ggheart.components.CounterUtil;
import com.jiubang.ggheart.components.ISelfObject;
import com.jiubang.ggheart.components.TextFont;
import com.jiubang.ggheart.components.TextFontInterface;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.ItemInfo;
import com.jiubang.ggheart.data.info.ScreenFolderInfo;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.plugin.notification.NotificationType;

/**
 * TextView that draws a bubble behind the text. We cannot use a
 * LineBackgroundSpan because we want to make the bubble taller than the text
 * and TextView's clip is too aggressive.
 */
//CHECKSTYLE:OFF
public class FilterBubbleTextView extends View
		implements
			ISelfObject,
			TextFontInterface {
	// 自定义字体相关
	private TextFont mTextFont;
	private Typeface mTypeface;
	private int mStyle;

	private static Paint sGradientPaint = null;
	private Paint.FontMetrics mFontMetrics;
	private Paint mTextPaint = new Paint();

	private Canvas mCacheCanvas = new Canvas();
	private float mLineLeft;
	private float mLineRight;
	private float mLineTop;
	private float mLineBottom;

	private Bitmap mTextCache;
	boolean mIsTextCacheDirty;

	private int mTextHeight;
	private int mTextLength;
	private Drawable mIcon;
	private String mTitle;

	private static final float SHADOW_RADIUS = 3.0f;
	private static final float CORNER_RADIUS = 8.0f;
	private static final float PADDING_H = 5.0f;
	public static final float PADDING_V = 1.0f;
	private static final float PADDING_LARGEICON_H = 3.0f;

	private int mGradientSize;

	private final RectF mRect = new RectF();
	private int mDrawablePadding; // 图标和文字的间隔

	private boolean mBackgroundSizeChanged;
	private Drawable mBackground;
	private float mCornerRadius;
	private float mPaddingH;
	private float mPaddingV;

	private int mLabelShadowColor; // 标签后面的圆角矩形阴影的颜色
	private boolean mShowShadow = true; // 是否显示阴影的标识
	private String mCounter = null;
	private final Rect mCounterRect = new Rect();
	private final Rect mNewAppRect = new Rect();
	private int mCounterColor = 0xFFFFFFFF; // 统计文字颜色
	private int mCounterFontSize = 14;
	private Drawable mCounterDrawable;
	private Drawable mNewAppDrawable;
	private int mNotifyWidth = 0;
	private int mNotifyHeight = 0;
	private int mCounterPadding = 0;
	// Paint.ANTI_ALIAS_FLAG 为了抗锯齿
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	// 文字缓存的画笔，只要是用来改变透明度
	private Paint mTextCachePaint = new Paint();

	// 统计信息状态值
	private int mCounterType = NotificationType.IS_NOT_NOTIFICSTION;
//	private DeskThemeControler themeControler;

	// 透明度参数X
	private int mAlpha = 255;

	public FilterBubbleTextView(Context context) {
		this(context, null);
	}

	public FilterBubbleTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FilterBubbleTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BubbleTextView, defStyle,
				0);
//		mDrawablePadding = a.getDimensionPixelSize(R.styleable.BubbleTextView_drawablePadding, 0);
		mDrawablePadding =  DrawUtils.dip2px(2);
		a.recycle();

		mGradientSize = DrawUtils.dip2px(8);
		mAlpha = 255;
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextAlign(Paint.Align.LEFT);

		selfConstruct();
		init();
		initOutLine(context);
//		setBackgroundColor(Color.GREEN);
	}

	/**
	 * 设置透明度 由于往后版本View新增了setAlpha方法，所以改方法名为setAlphaValue
	 * 
	 * @param alpha
	 */
	public void setAlphaValue(int alpha) {
		if (mAlpha != alpha) {
			mAlpha = alpha;
			mTextCachePaint.setAlpha(alpha);
			if (mIcon != null) {
				mIcon.setAlpha(alpha);
			}
		}
	}

	/**
	 * 获取当前图标的透明度 由于往后版本View新增了getAlpha方法，所以改方法名为getAlphaValue
	 * 
	 * @return
	 */
	public int getAlphaValue() {
		return mAlpha;
	}

	private synchronized void initGradientPaint() {
		if (sGradientPaint == null) {
			sGradientPaint = new Paint();
			int startColor = Color.argb(0, 0, 0, 0);
			int endColor = Color.argb(255, 0, 0, 0);
			LinearGradient gradient = new LinearGradient(0f, 0f, mGradientSize, 0f, startColor,
					endColor, Shader.TileMode.CLAMP);
			sGradientPaint.setShader(gradient);
			sGradientPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		}

	}

	// 初始化主题
	public void init() {
		setFocusable(true);
		setBackgroundDrawable(null);
		setWillNotCacheDrawing(true);

		initGradientPaint();

		if (mBackground != null) {
			mBackground.setCallback(null);
		}
		mBackground = null;
		mBackgroundSizeChanged = true;

		Resources res = getResources();
		mLabelShadowColor = res.getColor(R.color.bubble_dark_background);
//		themeControler = AppCore.getInstance().getDeskThemeControler();
//		if (themeControler != null && themeControler.isUsedTheme()) {
//			DeskThemeBean themeBean = themeControler.getDeskThemeBean();
//			if (themeBean != null && themeBean.mScreen != null) {
//				if (themeBean.mScreen.mIconStyle != null) {
//					if (themeBean.mScreen.mIconStyle.mIconBackgroud != null) {
//						if (themeBean.mScreen.mIconStyle.mIconBackgroud.mColor != 0) {
//							mLabelShadowColor = themeBean.mScreen.mIconStyle.mIconBackgroud.mColor;
//						}
//						// mDarkBackground = themeControler.getThemeResDrawable(
//						// themeBean.mScreen.mIconStyle.mIconBackgroud.mResName);
//					}
//					if (themeBean.mScreen.mIconStyle.mTextBackgroud != null) {
//						mBackground = themeControler
//								.getThemeResDrawable(themeBean.mScreen.mIconStyle.mTextBackgroud.mResName);
//					}
//				}
//			}
//		}

		// 使用默认值
		if (mBackground == null) {
			mBackground = res.getDrawable(R.drawable.shortcut_selector);
		}

		final float scale = DrawUtils.sDensity;
		mCornerRadius = CORNER_RADIUS * scale;
//		mPaddingH = scale * (GoLauncherLogicProxy.isLargeIcon() ? PADDING_LARGEICON_H : PADDING_H);
		mPaddingH = scale * PADDING_LARGEICON_H;
		mPaddingV = PADDING_V * scale;

		mCounterPadding = getResources().getDimensionPixelSize(R.dimen.counter_circle_padding);
		mNotifyWidth = getResources().getDimensionPixelSize(R.dimen.dock_notify_width);
		mNotifyHeight = getResources().getDimensionPixelSize(R.dimen.dock_notify_height);
		if (mCounterDrawable == null) {
			mCounterDrawable = getResources().getDrawable(R.drawable.stat_notify);
		}
		mCounterFontSize = getResources().getDimensionPixelSize(R.dimen.dock_notify_font_size);
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaint.setTextSize(mCounterFontSize);
		mPaint.setTypeface(Typeface.DEFAULT_BOLD);

		setFontSize();
		setTitleColor();
		
		initNewAppDrawable();
		invalidate();
	}
	
	private void initNewAppDrawable() {
//		AppItemInfo itemInfo = null;
//		Object tag = getTag();
//		if (tag != null && tag instanceof ShortCutInfo) {
//			itemInfo = ((ShortCutInfo) tag).getRelativeItemInfo();
//		}
//		if (itemInfo != null && itemInfo.mIsNewRecommendApp) {
//			mNewAppDrawable = getDrawableById(AppFuncFrame.getThemeController().getThemeBean().mAppIconBean.mNewApp);
//			if (mNewAppDrawable == null) {
//				mNewAppDrawable = getResources().getDrawable(R.drawable.new_install_app);
//			}
//			if (mNewAppDrawable != null) {
//				mNewAppRect.top = 0;
//				mNewAppRect.bottom = mNewAppRect.top + mNewAppDrawable.getIntrinsicHeight();
//				mNewAppRect.right = (int) (getWidth() * 0.95);
//				mNewAppRect.left = mNewAppRect.right - mNewAppDrawable.getIntrinsicWidth();
//				mNewAppDrawable.setBounds(mNewAppRect.left, mNewAppRect.top, 
//						mNewAppRect.right, mNewAppRect.bottom);
				
//				int iconSize = mIcon.getBounds().width(); // 图标大小
//				int iconWidth = CounterUtil.getCounterIconSize(iconSize); // mIcon.getBounds().width(); // 右上角通知图标大小
//				mNewAppRect.top = - CounterUtil.getXRightMargin(iconSize);
//				mNewAppRect.bottom = mNewAppRect.top + iconWidth;
//				mNewAppRect.left = CounterUtil.getXRightMargin(iconSize);
//				mNewAppRect.right = mNewAppRect.left + iconWidth;
//				mNewAppDrawable.setBounds(mNewAppRect.left, mNewAppRect.top, mNewAppRect.right,
//						mNewAppRect.bottom);
//			}
//		}
	}


	@Override
	protected boolean verifyDrawable(Drawable who) {
		return who == mBackground || super.verifyDrawable(who);
	}

	@Override
	protected void drawableStateChanged() {
		if (isPressed()) {
			// In this case, we have already created the pressed outline on
			// ACTION_DOWN,
			// so we just need to do an invalidate to trigger draw
		} else {
			// Otherwise, either clear the pressed/focused background, or create
			// a background for the focused state
			final boolean backgroundEmptyBefore = mPressedOrFocusedBackground == null;
			if (!mStayPressed) {
				mPressedOrFocusedBackground = null;
			}
			if (isFocused()) {
				if (mIcon == null && TextUtils.isEmpty(mTitle)) {
					// In some cases, we get focus before we have been layed
					// out. Set the
					// background to null so that it will get created when the
					// view is drawn.
					mPressedOrFocusedBackground = null;
				} else {
					mPressedOrFocusedBackground = createGlowingOutline(mTempCanvas, mGlowColor,
							mOutlineColor);
				}
				mStayPressed = false;
			}
			final boolean backgroundEmptyNow = mPressedOrFocusedBackground == null;
		}
		Drawable d = mBackground;
		if (d != null && d.isStateful()) {
			d.setState(getDrawableState());
		}
		super.drawableStateChanged();
	}

	public void setCounter(int count) {
		if (count > 0) {
			mCounter = String.valueOf(count);
		} else {
			mCounter = null;
		}
		final Object tag = getTag();
		if (tag != null && tag instanceof ShortCutInfo) {
			ShortCutInfo info = (ShortCutInfo) tag;
			info.mCounter = count;
		}
		int res = R.drawable.stat_notify_no_nine;
		if (count > 99) {
			res = R.drawable.stat_notify;
//			mCounterDrawable = getResources().getDrawable(R.drawable.stat_notify);
//		} else {
//			mCounterDrawable = getResources().getDrawable(R.drawable.stat_notify_no_nine);
		}
		int padding = getResources().getDimensionPixelSize(
				R.dimen.notify_padding);
		mCounterDrawable = BitmapUtility.composeDrawableTextExpend(
				getContext(), getResources()
				.getDrawable(res),
				mCounter, mCounterFontSize, padding);
		requestLayout();
		invalidate();
	}

	public int getCounter() {
		if (null == mCounter) {
			return 0;
		} else {
			return Integer.valueOf(mCounter);
		}
	}

	public int getCounterType() {
		return mCounterType;
	}

	/**
	 * 设置统计信息状态 {@link NotificationType#NOTIFICATIONTYPE_SMS}
	 * {@link NotificationType#NOTIFICATIONTYPE_CALL}
	 * {@link NotificationType#NOTIFICATIONTYPE_GMAIL}
	 * 
	 * @param type
	 *            统计类型
	 */
	public void setCounterType(int type) {
		mCounterType = type;
	}


	private void computeTextSize() {
		final int width = getWidth();
		if (width == 0) {
			return;
		}

		if (!TextUtils.isEmpty(mTitle)) {
			mTextLength = (int) mTextPaint.measureText(mTitle);
		} else {
			mTextLength = 0;
		}

		mLineLeft = 0;
		if (mTextLength < width) {
			mLineLeft = (width - mTextLength) / 2f;
		}
		mLineLeft = Math.max(mPaddingH, mLineLeft);
		mLineRight = Math.min(width - mPaddingH, mLineLeft + mTextLength);
		int compoundPaddingTop = getPaddingTop();
		if (mIcon != null) {
			compoundPaddingTop += mIcon.getBounds().height();
		}

		mLineTop = compoundPaddingTop + mDrawablePadding;
		mLineBottom = mLineTop + mTextHeight;

		final RectF rect = mRect;
		rect.left = Math.max(0, mLineLeft - mPaddingH);
		rect.top = mLineTop - mPaddingV;
		rect.right = Math.min(mLineRight + mPaddingH, width);
		rect.bottom = Math.min(mLineBottom + mPaddingV, getHeight());
		mIsTextCacheDirty = true;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		computeTextSize();

		initNewAppDrawable();
		/*AppItemInfo itemInfo = null;
		Object tag = getTag();
		if (tag != null && tag instanceof ShortCutInfo) {
			itemInfo = ((ShortCutInfo) tag).getRelativeItemInfo();
		}
		if (itemInfo != null && itemInfo.mIsNewRecommendApp) {
			if (mNewAppDrawable != null) {
				mNewAppRect.top = 0;
				mNewAppRect.bottom = mNewAppRect.top + mNewAppDrawable.getIntrinsicHeight();
				mNewAppRect.right = (int) (getWidth() * 0.95);
				mNewAppRect.left = mNewAppRect.right - mNewAppDrawable.getIntrinsicWidth();
				mNewAppDrawable.setBounds(mNewAppRect.left, mNewAppRect.top, 
						mNewAppRect.right, mNewAppRect.bottom);
			}
		}
		else */
		if (mCounter != null && mIcon != null) {
			int iconSize = mIcon.getBounds().width(); // 图标大小
			int iconWidth = CounterUtil.getCounterIconSize(iconSize); // mIcon.getBounds().width(); // 右上角通知图标大小
			int counterTextSize =  (int) (iconWidth / 56f * mCounterFontSize); //
			mCounterPadding = (int) (getResources().getDimensionPixelSize(R.dimen.counter_circle_padding) * iconWidth / 56f);
			mPaint.setTextSize(counterTextSize);
			int stringLength = (int) mPaint.measureText(mCounter, 0, mCounter.length() - 1);
			stringLength = Math.max(stringLength, iconWidth) ;
			if (mCounterDrawable != null) {
//				mCounterRect.top = getPaddingTop() - CounterUtil.getXRightMargin(iconSize);
//				mCounterRect.bottom = mCounterRect.top + iconWidth;
////				mCounterRect.left = (int) ((getWidth() + iconWidth) / 2f + scrollX - iconWidth * 0.2f);
////				mCounterRect.right = mCounterRect.left + stringLength + mNotifyWidth;
//				mCounterRect.right = (int) ((getWidth() + iconSize) / 2f + scrollX + CounterUtil.getXRightMargin(iconSize) + getPaddingLeft());
//				mCounterRect.left = mCounterRect.right - stringLength;
				mCounterRect.top = - CounterUtil.getXRightMargin(iconSize);
				mCounterRect.bottom = mCounterRect.top + iconWidth;
				mCounterRect.left = CounterUtil.getXRightMargin(iconSize);
				mCounterRect.right = mCounterRect.left + stringLength;
				mCounterDrawable.setBounds(mCounterRect.left, mCounterRect.top, mCounterRect.right,
						mCounterRect.bottom);
			}
		}
	}
	
	@Override
	public int getPaddingTop() {
		int top = super.getPaddingTop();
		if(mIcon != null){
			final int centerTop = (getHeight() - mIcon.getBounds().height() - mDrawablePadding - mTextHeight) >> 1;
			if (centerTop > top) {
				top = centerTop;
			}
		}
		return top;
	}
	
	@Override
	public void draw(Canvas canvas) {
		// canvas.drawColor(Color.RED);

		final Drawable background = mBackground;
		final int scrollX = getScrollX();
		final int scrollY = getScrollY();

		final int width = getWidth();
		final int height = getHeight();

		if (background != null) {
			if (mBackgroundSizeChanged) {
				background.setBounds(0, 0, width, height);
				mBackgroundSizeChanged = false;
			}

			canvas.translate(scrollX, scrollY);
			if ((scrollX | scrollY) == 0) {
				background.draw(canvas);
			} else {
				background.draw(canvas);
			}
			canvas.translate(-scrollX, -scrollY);
		}

		// 调用父类的方法
		super.draw(canvas);

		canvas.save();
		if (mIsTextCacheDirty) {
			buildAndUpdateCache();
		}

		float paddingTop = getPaddingTop();
		// 绘制图标
		float tx = 0;
		float ty = 0;

		int iconWidth = mIcon.getBounds().width();
		tx = (width - iconWidth) / 2f + scrollX;
		ty = paddingTop + scrollY;
		canvas.translate(tx, ty);
		// Rect iconRect = mIcon.getBounds();
		// mPaint.setColor(Color.BLUE);
		// canvas.drawRect(iconRect, mPaint);
		mIcon.draw(canvas);
		canvas.translate(-tx, -ty);

		if (!TextUtils.isEmpty(mTitle)) {

			// 绘制圆角文字框
			final RectF rect = mRect;
			if (mShowShadow) {
				if (mAlpha < 255) {
					final int shadowAlpha = mLabelShadowColor >>> 24;
					final int newColor = mAlpha * shadowAlpha >> 8 << 24;
					mPaint.setColor(newColor);
				} else {
					mPaint.setColor(mLabelShadowColor);
				}
				canvas.drawRoundRect(rect, mCornerRadius, mCornerRadius, mPaint);
				// canvas.drawRect(mLineLeft, mLineTop, mLineRight, mLineBottom,
				// mPaint);
			}
			if (mTextCache == null) {
				buildAndUpdateCache();
			}
			if (mTextCache != null) {
				tx = (width - mTextCache.getWidth()) / 2f + scrollX;
				ty = rect.top + scrollY;
				canvas.translate(tx, ty);
				canvas.drawBitmap(mTextCache, 0, 0, mTextCachePaint);
				canvas.translate(-tx, -ty);
			}
			// canvas.drawText(mTitle, mLineLeft, mLineTop -
			// mFontMetrics.ascent, mTextPaint);

			// if (rect.right >= width) {
			// tx = scrollX + width - mGradientSize;
			// ty = scrollY + mLineTop;
			// canvas.translate(tx, ty);
			// canvas.drawRect(0, 0, mGradientSize, mTextHeight,
			// sGradientPaint);
			// canvas.translate(-tx, -ty);
			// }
		}

		AppItemInfo itemInfo = null;
		Object tag = getTag();
		if (tag != null && tag instanceof ShortCutInfo) {
			itemInfo = ((ShortCutInfo) tag).getRelativeItemInfo();
		}
		if (itemInfo != null && itemInfo.isNew()) {
			if (mNewAppDrawable != null) {
				tx = (width + iconWidth) / 2f + scrollX
						- mNewAppDrawable.getBounds().width();
				ty = paddingTop + scrollY;
				canvas.translate(tx, ty);
				mNewAppDrawable.setAlpha(mAlpha);
				mNewAppDrawable.draw(canvas);
				canvas.translate(-tx, -ty);
			}
		} else if (mCounter != null) {
			if (mCounterDrawable != null) {
				tx = (width + iconWidth) / 2f + scrollX
						- mCounterDrawable.getBounds().width();
				ty = paddingTop + scrollY;
				canvas.translate(tx, ty);
				mCounterDrawable.setAlpha(mAlpha);
				mCounterDrawable.draw(canvas);
				if (mAlpha < 255) {
					// final int shadowAlpha = mCounterColor >>> 24;
					// final int newColor = mAlpha * shadowAlpha >> 8 << 24;
					// modified by guoyiqing
					final int shadowAlpha = mCounterColor << 8 >>> 8;
					final int newColor = (mAlpha << 24) + shadowAlpha;
					mPaint.setColor(newColor);
				} else {
					mPaint.setColor(mCounterColor);
				}
//				canvas.drawText(mCounter, mCounterRect.centerX(),
//						mCounterRect.centerY() + mCounterPadding, mPaint);
				canvas.translate(-tx, -ty);
			}
		}
		canvas.restore();
	}

	public int getLineTop() {
		/*
		 * float yOffset = getCompoundPaddingTop() + mDrawablePadding +
		 * mTextHeight; return (int) (yOffset + mFontMetrics.ascent);
		 */
		return (int) mLineTop;
	}

	public int getLineBottom() {
		// return getLineTop() + mTextHeight;
		return (int) mLineBottom;
	}

	public float getTextRectTopLine() {
		return mRect.top;
	}

	@Override
	protected int getSuggestedMinimumHeight() {
		int minHeight = super.getSuggestedMinimumHeight();
		if (mIcon != null) {
			int iconHeight = mIcon.getBounds().height();
			int needHeight = iconHeight + mTextHeight + getPaddingTop() + getPaddingBottom();
			minHeight = Math.max(minHeight, needHeight);
		}
		return minHeight;
	}

	@Override
	protected int getSuggestedMinimumWidth() {
		int minWidth = super.getSuggestedMinimumWidth();
		if (mIcon != null) {
			int iconWidth = mIcon.getBounds().width();
			int needWidth = iconWidth + getPaddingLeft() + getPaddingRight();
			minWidth = Math.max(minWidth, needWidth);
		}
		return minWidth;
	}

	public void setDarkBackgroundColor(int color) {
		if (mPaint != null) {
			mPaint.setColor(color);
		}
	}

	@SuppressLint("Override")
	public void setBackground(Drawable drawable) {
		mBackground = drawable;
		if (mBackground != null) {
			mBackground.setCallback(this);
		}
	}

	private void updateShadowLayer() {
		int color = mTextPaint.getColor();
		if (color == Color.WHITE && !mShowShadow) {
			mTextPaint.setShadowLayer(SHADOW_RADIUS, 0.0f, 2.0f, 0xff000000);
		} else {
			mTextPaint.clearShadowLayer();
		}
	}

	public void setShowShadow(boolean showShadow) {
		mShowShadow = showShadow;
		updateShadowLayer();
	}

//	public void customBackground() {
//		GoSettingControler settingControler = GoSettingControler.getInstance(ApplicationProxy.getContext());
//		final DesktopSettingInfo info = settingControler.getDesktopSettingInfo();
//		if (info.mCustomAppBg) {
//			mBackground = IconHighlights.getDrawable(IconHighlights.TYPE_DESKTOP, info.mFocusColor,
//					info.mPressColor);
//		}
//	}

	public void setIcon(Drawable icon) {
		mIcon = icon;
		if (icon != null) {
			// final int iconSize = Utilities.getStandardIconSize(getContext());
			final Resources resources = getContext().getResources();
			int iconSize = (int) resources
					.getDimension(R.dimen.screen_icon_size);
			icon.setBounds(0, 0, iconSize, iconSize);
		}
		invalidate();
	}

	public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top,
			Drawable right, Drawable bottom) {
		setIcon(top);
	}

	public Drawable getIcon() {
		return mIcon;
	}

	public void setText(int resId) {
		String str = getResources().getString(resId);
		setText(str);
	}

	public void setText(CharSequence title) {
		if (title != null) {
			mTitle = String.valueOf(title);
			computeTextSize();
		} else {
			mTitle = null;
			mIsTextCacheDirty = true;
		}
		
		try {
			invalidate();
		} catch (Exception e) {
			postInvalidate();
		}
	}

	public CharSequence getText() {
		return mTitle;
	}

	/**
	 * 返回图标的名字（即使当前图标名字不显示也可得）
	 * 
	 * @return title
	 */
	public CharSequence getTitleIgnoreVisible() {
		ItemInfo itemInfo = (ItemInfo) getTag();
		CharSequence title = null;
		if (itemInfo == null) {
			return title;
		}
		if (itemInfo instanceof ShortCutInfo) {
			title = ((ShortCutInfo) itemInfo).mTitle;
		} else if (itemInfo instanceof ScreenFolderInfo) {
			title = ((ScreenFolderInfo) itemInfo).mTitle;
		}
		return title;
	}

	int getCompoundPaddingLeft() {
		return getPaddingLeft();
	}

	public int getCompoundPaddingTop() {
		if (mIcon == null) {
			return getPaddingTop();
		} else {
			return getPaddingTop() + mIcon.getBounds().height();
		}
	}

	/**
	 * Returns the padding between the compound drawables and the text.
	 */
	public int getCompoundDrawablePadding() {
		return mDrawablePadding;
	}

	public void setFontSize() {
//		int fontSize = DrawUtils.dip2px(GoLauncherLogicProxy.getAppFontSize());
		int fontSize = DrawUtils.sp2px(13.5f);
		setTextSize(fontSize);
	}

	public void setTextSize(float textSize) {
		mTextPaint.setTextSize(textSize);
		mFontMetrics = mTextPaint.getFontMetrics();
		mTextHeight = (int) Math.ceil(mFontMetrics.descent - mFontMetrics.ascent);
		computeTextSize();
	}

	public void setTitleColor() {
//		if (GoLauncherLogicProxy.getCustomTitleColor()) {
//			int color = GoLauncherLogicProxy.getAppTitleColor();
//			if (color != 0) {
//				setTextColor(color);
//			} else {
				setTextColor(Color.WHITE);
//			}
//		} else {
//			if (themeControler != null && themeControler.isUsedTheme()) {
//				DeskThemeBean themeBean = themeControler.getDeskThemeBean();
//				if (themeBean != null && themeBean.mScreen != null) {
//					if (null != themeBean.mScreen.mFont) {
//						int color = themeBean.mScreen.mFont.mColor;
//						if (color == 0) {
//							setTextColor(Color.WHITE);
//						} else {
//							setTextColor(color);
//						}
//					}
//				}
//			} else {
				setTextColor(Color.WHITE);
//			}
//		}

		mIsTextCacheDirty = true;
		computeTextSize();
	}

	public void setTextColor(int color) {
		mTextPaint.setColor(color);
		updateShadowLayer();
	}

	// @SuppressWarnings("unchecked")
	// @Override
	// public void onBCChange(int msgId, int param, Object object, List objects)
	// {
	// super.onBCChange(msgId, param, object, objects);
	// switch (msgId)
	// {
	// case AppItemInfo.INCONCHANGE:
	// {
	// post(new Runnable()
	// {
	// @Override
	// public void run()
	// {
	// init();
	// }
	// });
	// break;
	// }
	//
	// default:
	// break;
	// }
	// }

	/*-----------------------------以下code为点击效果-------------------------------*/
	private final Canvas mTempCanvas = new Canvas();
	private final Rect mTempRect = new Rect();
	private boolean mDidInvalidateForPressedState;
	private Bitmap mPressedOrFocusedBackground;
	private int mGlowColor;
	private int mOutlineColor;
	private boolean mStayPressed;

	private void initOutLine(Context context) {
		final Resources res = getContext().getResources();
		mGlowColor = res.getColor(R.color.icon_glow_color);
		mOutlineColor = res.getColor(R.color.icon_outline_color);
//		GoSettingControler settingControler = GoSettingControler.getInstance(ApplicationProxy.getContext());
//		final DesktopSettingInfo info = settingControler.getDesktopSettingInfo();
//		if (info.mCustomAppBg) {
//			mGlowColor = mOutlineColor = info.mPressColor;
//		}
		mBackground = null;
	}


	/**
	 * Draw this BubbleTextView into the given Canvas.
	 * 
	 * @param destCanvas
	 *            the canvas to draw on
	 * @param padding
	 *            the horizontal and vertical padding to use when drawing
	 */
	private void drawWithPadding(Canvas destCanvas, int padding) {
		final Rect clipRect = mTempRect;
		getDrawingRect(clipRect);

		// adjust the clip rect so that we don't include the text label
		clipRect.bottom = getLineTop() - (int) mPaddingV - mDrawablePadding;

		// Draw the View into the bitmap.
		// The translate of scrollX and scrollY is necessary when drawing
		// TextViews, because
		// they set scrollX and scrollY to large values to achieve centered text
		destCanvas.save();
		destCanvas.translate(-getScrollX() + padding / 2, -getScrollY() + padding / 2);
		destCanvas.clipRect(clipRect, Op.REPLACE);
		draw(destCanvas);
		destCanvas.restore();
	}

	/**
	 * Returns a new bitmap to be used as the object outline, e.g. to visualize
	 * the drop location. Responsibility for the bitmap is transferred to the
	 * caller.
	 */
	private Bitmap createGlowingOutline(Canvas canvas, int outlineColor, int glowColor) {
		final int padding = HolographicOutlineHelper.MAX_OUTER_BLUR_RADIUS;
		try {
			final Bitmap b = Bitmap.createBitmap(getWidth() + padding, getHeight() + padding,
					Bitmap.Config.ARGB_8888);
			canvas.setBitmap(b);
			drawWithPadding(canvas, padding);
			HolographicOutlineHelper outlineHelper = HolographicOutlineHelper.getInsance();
			outlineHelper.applyExtraThickExpensiveOutlineWithBlur(b, canvas, glowColor,
					outlineColor);
			// canvas.setBitmap(null);
			// String fileName = LauncherEnv.Path.SCREEN_CAPUTRE_PATH
			// + Long.toString(System.currentTimeMillis()) + ".png";
			// BitmapUtility.saveBitmap(b, fileName);
			return b;
		} catch (OutOfMemoryError e) {
		} catch (Exception e) {
		}
		return null;
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		// Call the superclass onTouchEvent first, because sometimes it changes
//		// the state to
//		// isPressed() on an ACTION_UP
//		boolean result = super.onTouchEvent(event);
//
//		switch (event.getAction()) {
//			case MotionEvent.ACTION_DOWN :
//				// So that the pressed outline is visible immediately when
//				// isPressed() is true,
//				// we pre-create it on ACTION_DOWN (it takes a small but
//				// perceptible amount of time
//				// to create it)
//				if (mPressedOrFocusedBackground == null) {
//					mPressedOrFocusedBackground = createGlowingOutline(mTempCanvas, mGlowColor,
//							mOutlineColor);
//				}
//				// Invalidate so the pressed state is visible, or set a flag so
//				// we know that we
//				// have to call invalidate as soon as the state is "pressed"
//				if (isPressed() || isClickable()) {
//					mDidInvalidateForPressedState = true;
//				} else {
//					mDidInvalidateForPressedState = false;
//				}
//				break;
//			case MotionEvent.ACTION_CANCEL :
//			case MotionEvent.ACTION_UP :
//				// If we've touched down and up on an item, and it's still not
//				// "pressed", then
//				// destroy the pressed outline
//				mPressedOrFocusedBackground = null;
//				//针对4.1的机型，桌面图标的选中效果无法及时消失
//				ViewParent parent =  getParent();
//				if(parent != null && parent instanceof CellLayout){
//				   CellLayout celllayout = (CellLayout) parent;
//				   celllayout.invalidate();
//				}
//				
//				break;
//		}
//
//		return result;
//	}

	public void setStayPressed(boolean stayPressed) {
		mStayPressed = stayPressed;
		if (!stayPressed) {
			mPressedOrFocusedBackground = null;
		}
	}

	public int getPressedOrFocusedBackgroundPadding() {
		return HolographicOutlineHelper.MAX_OUTER_BLUR_RADIUS / 2;
	}

	public Bitmap getPressedOrFocusedBackground() {
		return mPressedOrFocusedBackground;
	}

	private void buildAndUpdateCache() {
		int cacheWidth = (int) mRect.width();
		int cacheHeight = (int) mRect.height();

		if (TextUtils.isEmpty(mTitle)) {
			if (mTextCache != null) {
				mTextCache.recycle();
				mTextCache = null;
			}
		} else if (cacheWidth > 0 && cacheHeight > 0) {
			if (mTextCache != null) {
				if (mTextCache.getWidth() != cacheWidth || mTextCache.getHeight() != cacheHeight) {
					mTextCache.recycle();
					mTextCache = null;
				}
			}
			if (mTextCache == null) {
				try {
					mTextCache = Bitmap.createBitmap(cacheWidth, cacheHeight, Config.ARGB_8888);
					mCacheCanvas.setBitmap(mTextCache);
				} catch (OutOfMemoryError e) {
					Log.v("BubbleTextView",
							"BubbleTextView.buildAndUpdateCache() for mTextCache:OutOfMemorry");
				}
			} else {
				mCacheCanvas.drawColor(0, Mode.CLEAR);
			}

			final int width = getWidth();
			mCacheCanvas.save();
			float tx = Math.max(0, (cacheWidth - mTextLength) / 2f);
			float ty = mPaddingV;
			mCacheCanvas.translate(tx, ty);
			mCacheCanvas.drawText(mTitle, 0, mTextHeight - mFontMetrics.descent, mTextPaint);
			mCacheCanvas.translate(-tx, -ty);

			
			// 文件超出区域的渐隐效果
			if (mRect.width() >= width) {
				tx = mRect.width() - mGradientSize;
				mCacheCanvas.translate(tx, ty);
				mCacheCanvas.drawRect(0, 0, mGradientSize, mTextHeight, sGradientPaint);
				mCacheCanvas.translate(-tx, -ty);
			}

			mCacheCanvas.restore();
		}
		mIsTextCacheDirty = false;
	}


	@Override
	public void selfConstruct() {
		onInitTextFont();
	}

	@Override
	public void selfDestruct() {
		onUninitTextFont();
		if (mTextCache != null) {
			mTextCache.recycle();
			mTextCache = null;
		}
	}

	@Override
	public void onInitTextFont() {
		if (null == mTextFont) {
			mTextFont = new TextFont(this);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mIsTextCacheDirty = true;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		selfDestruct();
	}

	@Override
	public void onUninitTextFont() {
		if (null != mTextFont) {
			mTextFont.selfDestruct();
			mTextFont = null;
		}
	}

	@Override
	public void onTextFontChanged(Typeface typeface, int style) {
		mTypeface = typeface;
		mStyle = style;
		setTypeface(mTypeface, mStyle);
	}

	public void reInitTypeface() {
		setTypeface(mTypeface, mStyle);
	}

	public void setTypeface(Typeface tf, int style) {
		if (style > 0) {
			if (tf == null) {
				tf = Typeface.defaultFromStyle(style);
			} else {
				tf = Typeface.create(tf, style);
			}

			setTypeface(tf);
			// now compute what (if any) algorithmic styling is needed
			int typefaceStyle = tf != null ? tf.getStyle() : 0;
			int need = style & ~typefaceStyle;
			mTextPaint.setFakeBoldText((need & Typeface.BOLD) != 0);
			mTextPaint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);
		} else {
			mTextPaint.setFakeBoldText(false);
			mTextPaint.setTextSkewX(0);
			setTypeface(tf);
		}
	}

	public void setTypeface(Typeface tf) {
		if (mTextPaint.getTypeface() != tf) {
			mTextPaint.setTypeface(tf);
			requestLayout();
			invalidate();
		}
	}
	
}

