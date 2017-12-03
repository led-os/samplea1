package com.jiubang.ggheart.appgame.gostore.base.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.appgame.base.bean.ClassificationDataBean;
import com.jiubang.ggheart.appgame.base.component.AppGameADAdapter;
import com.jiubang.ggheart.appgame.base.component.AppGameADBanner;

/**
 * 广告推荐位
 * 
 * @author panghuajuan
 * 
 */
public class AppGameAdBannerHeadView extends FrameLayout {
	private Context mContext;
	private LayoutInflater mInflater = null;

	/**
	 * 广告推荐位
	 */
	private AppGameADBanner mADBanner = null;
	/**
	 * 广告位adapter
	 */
	private AppGameADAdapter mADAdapter;
	/**
	 * 广告位选中位置
	 */
	private int mADSelection = Integer.MAX_VALUE / 2;

	public AppGameAdBannerHeadView(Context context) {
		super(context);
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		init();
		// TODO Auto-generated constructor stub
	}

	void init() {
		if (mADAdapter == null) {
			mADAdapter = new AppGameADAdapter(getContext());
		}
		if (mADBanner == null) {
			mADBanner = new AppGameADBanner(getContext());
			mADBanner.setOnItemSelectedListener(mADSelectedListener);
		}
		mADBanner.setVisibility(View.VISIBLE);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				DrawUtils.sWidthPixels * 2,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		this.addView(mADBanner, lp);
	}

	public void updateContent(ClassificationDataBean bean) {
		mADAdapter.update(bean.featureList);
		mADBanner.setAdapter(mADAdapter);
	}

	private OnItemSelectedListener mADSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			mADSelection = position;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	};

}
