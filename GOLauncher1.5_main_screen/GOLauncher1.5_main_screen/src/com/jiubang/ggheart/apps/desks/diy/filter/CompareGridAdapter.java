package com.jiubang.ggheart.apps.desks.diy.filter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.diy.filter.view.FilterBubbleTextView;

/**
 * 
 * @author zouguiquan
 *
 */
public class CompareGridAdapter extends BaseAdapter {
	private List<Drawable> mData;
	private LayoutInflater mInflater;
	private Context mContext;

	public CompareGridAdapter(Context context, List<Drawable> data) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mData = data;
	}

	@Override
	public int getCount() {
		if (mData != null) {
			return mData.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mData != null) {
			return mData.get(position);
		}
		return null;
	}
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FilterBubbleTextView view = null;
		if (convertView == null) {
//			final int appLayoutId = GoLauncherLogicProxy.isLargeIcon()
//					? R.layout.application_large_port
//					: R.layout.application_port;
//			view = (BubbleTextView) mInflater.inflate(R.layout.application_large_port, null);
			view = new FilterBubbleTextView(mContext);
		} else {
			view = (FilterBubbleTextView) convertView;
		}

		Drawable icon = mData.get(position);
		if (icon != null) {
			view.setIcon(mData.get(position));
		} else {
			view.setIcon(mContext.getResources().getDrawable(R.drawable.ic_launcher_application));
		}
		
		view.setText(mContext.getResources().getString(R.string.filter_preview_compare_item_title));
		view.setShowShadow(false);
		view.setEnabled(false);
		view.setClickable(false);
		view.setPressed(false);

		return view;
	}
}
