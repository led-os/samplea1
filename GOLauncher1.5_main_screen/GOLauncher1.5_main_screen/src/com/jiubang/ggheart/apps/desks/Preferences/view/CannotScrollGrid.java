package com.jiubang.ggheart.apps.desks.Preferences.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 不可滚动的grid
 */
public class CannotScrollGrid extends GridView {

	public CannotScrollGrid(Context context) {
		super(context);
	}

	public CannotScrollGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CannotScrollGrid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
	
}
