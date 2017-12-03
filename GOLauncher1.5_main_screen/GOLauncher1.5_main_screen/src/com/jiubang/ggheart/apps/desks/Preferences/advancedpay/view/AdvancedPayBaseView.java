package com.jiubang.ggheart.apps.desks.Preferences.advancedpay.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.IAdvancedPayListener;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve.Elve;

/**
 * 动态广告页基类View组件
 * @author yejijiong
 *
 */
public class AdvancedPayBaseView extends View {
	
	protected Context mContext;
	/**
	 * 是否需要做动画的标识
	 */
	protected boolean mNeedDoAnimate = false;
	/**
	 * 当前动画进行的比例(0~1)
	 */
	protected float mCurAnimatePercent = 0;
	/**
	 * 动画开始的时间
	 */
	protected long mAnimateStartTime;
	/**
	 * 动画持续的时间
	 */
	protected int mAnimateDuration = 500;
	/**
	 * 精灵队列
	 */
	protected List<Elve> mElveList = new ArrayList<Elve>();
	/**
	 * 按xhdpi算出的屏幕密度为1时的标准屏幕宽度
	 */
	private static final int STANDARD_WIDTH = 240;
	/**
	 * 按xhdpi算出的屏幕密度为1时的标准屏幕高度
	 */
	private static final int STANDARD_HEIGHT = 200;
	/**
	 * 基础缩放比
	 */
	protected float mBaseScale = 1.0f;
	/**
	 * 基础X轴偏移
	 */
	protected float mBaseTranslateX = 0;
	/**
	 * 基础Y轴偏移
	 */
	protected float mBaseTranslateY = 0;
	
	/**
	 * 动画完成后处理类型
	 */
	private int mAnimateFinishType = FINISH_TYPE_NONE;
	/**
	 * 动画完成后无特殊处理
	 */
	public static final int FINISH_TYPE_NONE = 0;
	/**
	 * 动画完成后保持在动画结束状态持续刷新界面
	 */
	public static final int FINISH_TYPE_CONTINUE_REFRESH = 1;
	/**
	 * 动画完成后重新开始动画
	 */
	public static final int FINISH_TYPE_RESATRT_ANIMATE = 2;
	/**
	 * 是否已经初始化
	 */
	protected boolean mIsInit = false;
	private Handler mHandler;
	private final static int LOAD_FINISH = 0;
	protected IAdvancedPayListener mListener;
	private int mCurAnimateCount = 0;
	private int mMaxAnimateCount = 1;
	/**
	 * 是否已经做了整页进入动画
	 */
	private boolean mHasDoEnterAnimate = false;
	/**
	 * 进入整页动画开始的时间
	 */
	protected long mEnterAnimateStartTime;
	/**
	 * 当前整页进入动画进行的比例(0~1)
	 */
	protected float mCurEnterAnimatePercent = 0;
	/**
	 * 整页进入动画持续的时间
	 */
	protected int mEnterAnimateDuration = 300;
	protected final static int ENTER_ANIMATE_MIN_ALPHA = 0;
	protected final static int ENTER_ANIMATE_MIN_SCALE = 0;
	
	private boolean mIsPromotion = false;
	
	public AdvancedPayBaseView(Context context) {
		super(context);
		mContext = context;
		initHandler();
	}
	
	public AdvancedPayBaseView(Context context, int animateFinishType, IAdvancedPayListener listener) {
		super(context);
		mContext = context;
		mAnimateFinishType = animateFinishType;
		mListener = listener;
		initHandler();
	}
	
	public AdvancedPayBaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initHandler();
	}
	
	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case LOAD_FINISH:
					mIsInit = true;
					requestLayout();
					invalidate();
					break;
				default:
					break;
				}
			}
		};
	}
	
	public void startLoad() {
		if (!mIsInit) {
			init();
			initElve();
			mHandler.sendEmptyMessage(LOAD_FINISH);
		}
	}
	
	protected void init() {
	}
	
	private void calculateBaseScaleAndTranslate() {
		int standardWidth = (int) (STANDARD_WIDTH * DrawUtils.sDensity);
		int standradHeight = (int) (STANDARD_HEIGHT * DrawUtils.sDensity);
		int actualWidth = getWidth();
		int actualHeight = getHeight();
		float scaleX = (actualWidth + 0.01f) / standardWidth; 
		float scaleY = (actualHeight + 0.01f) / standradHeight;
		//这里算出的另一个值不准确，先赋0，如果后面有需要重新计算
		if (scaleX < scaleY) {
			if (scaleX >= 1.0f) { // 只缩小，不放大
				return;
			}
			mBaseScale = scaleX;
			mBaseTranslateX = actualWidth * (1 - mBaseScale) / 2.0f;
			mBaseTranslateY = actualHeight * (1 - mBaseScale) / 2.0f;
		} else {
			if (scaleY >= 1.0f) { // 只缩小，不放大
				return;
			}
			mBaseScale = scaleY;
			mBaseTranslateY = (standradHeight - actualHeight) / 2.0f;
			mBaseTranslateX = (standardWidth - actualWidth) / 2.0f;
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		calculateBaseScaleAndTranslate();
		calculateAnimateValue(mCurAnimatePercent);
		setEnterAlpha(ENTER_ANIMATE_MIN_ALPHA);
		setEnterScale(ENTER_ANIMATE_MIN_SCALE);
	}
	
	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		if (mBaseScale != 1.0f) {
			canvas.translate(getWidth() / 2.0f, getHeight() / 2.0f);
			canvas.scale(mBaseScale, mBaseScale);
			canvas.translate(-getWidth() / 2.0f, -getHeight() / 2.0f);
		}
		super.draw(canvas);
		if (mNeedDoAnimate) {
			if (mHasDoEnterAnimate) { // 做真正的动画
				if (mCurAnimatePercent >= 1) {
					mCurAnimateCount++;
					if (mAnimateFinishType == FINISH_TYPE_NONE) {
						finishAnimate();
					} else if (mAnimateFinishType == FINISH_TYPE_RESATRT_ANIMATE) {
						if (mCurAnimateCount == mMaxAnimateCount) {
							mListener.onAnimateFinish();
						}
						resetAnimationFactor();
						restartAnimate();
					} else if (mAnimateFinishType == FINISH_TYPE_CONTINUE_REFRESH) {
						invalidate();
					}
				} else {
					invalidate();
				}
				calculateCurAnimatePercent();
			} else { // 做进入动画
				calculateCurEnterAnimatePercent();
				int alpha = (int) (ENTER_ANIMATE_MIN_ALPHA + mCurEnterAnimatePercent
						* (255 - ENTER_ANIMATE_MIN_ALPHA));
				setEnterAlpha(alpha);
				float scale = ENTER_ANIMATE_MIN_SCALE + mCurEnterAnimatePercent
						* (1.0f - ENTER_ANIMATE_MIN_SCALE);
				setEnterScale(scale);
				if (mCurEnterAnimatePercent >= 1) { // 进入动画结束
					startAnimate();
				}
				invalidate();
			}
		}
		drawChildren(canvas);
		canvas.restore();
	}
	
	private void calculateCurAnimatePercent() {
		float percent = 1.0f * (System.currentTimeMillis() - mAnimateStartTime) / mAnimateDuration;
		if (percent > 1.0f) {
			percent = 1.0f;
		} else if (percent < 0.0f) {
			percent = 0.0f;
		}
		setCurAnimatePercent(percent);
	}
	
	private void calculateCurEnterAnimatePercent() {
		float percent = 1.0f * (System.currentTimeMillis() - mEnterAnimateStartTime)
				/ mEnterAnimateDuration;
		if (percent > 1.0f) {
			percent = 1.0f;
		} else if (percent < 0.0f) {
			percent = 0.0f;
		}
		setCurEnterAnimatePercent(percent);
	}
	
	protected void initElve() {
	}
	
	protected void addElve(Elve elve) {
		mElveList.add(elve);
	}
	
	protected void drawChildren(Canvas canvas) {
		if (mIsInit) {
			for (Elve elve : mElveList) {
				elve.drawElve(canvas);
			}
		}
	}
	
	protected void calculateAnimateValue(float curAnimatePercent) {
		if (mIsInit) {
			for (Elve elve : mElveList) {
				elve.calculateAnimateValue(curAnimatePercent);
			}
		}
	}
	
	public void startAnimate() {
		mCurAnimateCount = 0;
		if (mHasDoEnterAnimate) {
			restartAnimate();
		} else {
			startEnterAnimate();
		}
	}
	
	public void restartAnimate() {
		mNeedDoAnimate = true;
		mAnimateStartTime = System.currentTimeMillis()
				- (long) (mAnimateDuration * mCurAnimatePercent);
		invalidate();
	}
	
	/**
	 * 整页进入动画
	 */
	public void startEnterAnimate() {
		mNeedDoAnimate = true;
		mEnterAnimateStartTime = System.currentTimeMillis()
				- (long) (mEnterAnimateDuration * mCurEnterAnimatePercent);
		invalidate();
	}
	
	public void finishAnimate() {
		mNeedDoAnimate = false;
	}
	
	protected void setCurAnimatePercent(float percent) {
		if (mCurAnimatePercent != percent) {
			if (percent == 1.0f && mAnimateFinishType != FINISH_TYPE_RESATRT_ANIMATE) {
				mListener.onAnimateFinish();
			}
			mCurAnimatePercent = percent;
			calculateAnimateValue(percent);
		} else if (mAnimateFinishType == FINISH_TYPE_CONTINUE_REFRESH) {
			calculateAnimateValue(percent);
		}
	}
	
	private void setCurEnterAnimatePercent(float percent) {
		mCurEnterAnimatePercent = percent;
		if (percent == 0) {
			mHasDoEnterAnimate = false;
		} else if (percent == 1) {
			mHasDoEnterAnimate = true;
		}
	}
	
	public void resetAnimationFactor() {
		setCurAnimatePercent(0);
		setCurEnterAnimatePercent(0);
		setEnterAlpha(ENTER_ANIMATE_MIN_ALPHA);
		setEnterScale(ENTER_ANIMATE_MIN_SCALE);
		invalidate();
	}
	
	public String getMessageTip() {
		return "";
	}
	
	public String getMessageSummary() {
		return "";
	}
	
	public String getSingleMessage() {
		return "";
	}
	
	public int getBgColor() {
		return 0x00000000;
	}
	
	/**
	 * 进入时总体进行的Alpha动画，里面需要复写刚开始就存在的精灵的setAlpha
	 * @param alpha
	 */
	public void setEnterAlpha(int alpha) {
		
	}
	
	/**
	 * 进入时总体进行的Scale动画，里面需要复写刚开始就存在的经理的setScale
	 * @param scale
	 */
	public void setEnterScale(float scale) {
		
	}
	
	/**
	 * <br>功能简述:是否促销的标志
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param isPromotion
	 * @return
	 */
	public void setIsPromotion(boolean isPromotion) {
	    mIsPromotion = isPromotion;
	}
	
	public boolean isPromotion() {
	    return mIsPromotion;
	}
}
