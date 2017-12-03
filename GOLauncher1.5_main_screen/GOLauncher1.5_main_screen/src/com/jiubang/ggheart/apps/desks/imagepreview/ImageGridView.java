package com.jiubang.ggheart.apps.desks.imagepreview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.jiubang.ggheart.apps.desks.imagepreview.BladeView.OnColumnChangeListener;

/**
 * 
 */
public class ImageGridView extends GridView {

	private ImageGridParam mParam;
	private OnColumnChangeListener mListener = null;
	private int mNumcols = 0;
	public ImageGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ImageGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ImageGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ImageGridView(Context context, ImageGridParam param) {
		super(context);

		mParam = param;
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		setLayoutParams(rlp);
		// setBackgroundColor(0xB31f1f1f);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		initGridAttr(MeasureSpec.getSize(widthMeasureSpec));
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private void initGridAttr(int w) {
		if (null == mParam) {
			return;
		}
		// int w = getWidth();
		// int h = getHeight();
		int columns = w / mParam.mWidth;
		// int griditemhspace = (w - (columns * mParam.mWidth)) / (columns - 1);
		// int rows = h / mParam.mHeight;
		// int griditemvspace = (h - rows * mParam.mHeight) / (rows - 1);

		setNumCols(columns);
		// setHorizontalSpacing(griditemhspace);
		// setVerticalSpacing(20);
		if (mListener != null) {
			mListener.onColumnChange(columns);
		}
	}

	public void setOnColumnChangeListener(OnColumnChangeListener listener) {
		mListener = listener;
	}

	public void setParams(ImageGridParam param) {
		mParam = param;
	}
	
	private void setNumCols(int numColumns) {
		setNumColumns(numColumns);
		mNumcols = numColumns;
	}
	
	public int getNumCols() {
		return mNumcols;
	}
}
