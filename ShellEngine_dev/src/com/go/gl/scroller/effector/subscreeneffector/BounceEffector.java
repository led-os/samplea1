package com.go.gl.scroller.effector.subscreeneffector;

import com.go.gl.graphics.GLCanvas;
import com.go.gl.scroller.ScreenScroller;

/**
 * 
 */
public class BounceEffector extends MSubScreenEffector {
	float mRatio;

	@Override
	protected boolean onDrawScreen(GLCanvas canvas, int screen, int offset, boolean first) {
		mNeedQuality = false;
		float tranY = -offset * offset * mRatio;
		if (mOrientation == ScreenScroller.HORIZONTAL) {
			canvas.translate(mScroll + offset, tranY);
		} else {
			canvas.translate(tranY, mScroll + offset);
		}
		return true;
	}

	@Override
	public void onSizeChanged() {
		super.onSizeChanged();
		if (mOrientation == ScreenScroller.HORIZONTAL) {
			mRatio = (float) mHeight / mWidth / mWidth;
		} else {
			mRatio = (float) mWidth / mHeight / mHeight;
		}
	}
}
