package com.go.gl.scroller.effector.subscreeneffector;

import com.go.gl.graphics.GLCanvas;
import com.go.gl.scroller.ScreenScroller;

/**
 * 
 * 类描述:波浪特效
 * 功能详细描述:
 * 
 * @author  songsiyu
 * @date  [2012-9-3]
 */
public class WaveEffector extends MSubScreenEffector {

	final static int RADIUS = 1;
	final static float SCALEMIN = 0f;

	float mRatio;
	float mScaleMin = SCALEMIN;
	float mScaleMax = 1.0f;

	public WaveEffector() {
		mCombineBackground = false;
	}

	@Override
	protected boolean onDrawScreen(GLCanvas canvas, int screen, int offset, boolean first) {
		mNeedQuality = false;
		float t = (float) Math.cos(offset * mRatio);
		float s = (mScaleMax - mScaleMin) * t * t + mScaleMin;
		float leftTop;
		if (first) {
			leftTop = offset + mScreenSize * (1 - s);
		} else {
			leftTop = offset;
		}

		if (mOrientation == ScreenScroller.HORIZONTAL) {
			canvas.translate(mScroll + leftTop, (1 - s) * HALF * mHeight);
		} else {
			canvas.translate((1 - s) * HALF * mWidth, mScroll + leftTop);
		}
		canvas.scale(s, s);
		return true;
	}

	@Override
	public void onSizeChanged() {
		super.onSizeChanged();
		mRatio = (float) Math.PI / (RADIUS * 2 + 1) / mScreenSize;
	}

}
