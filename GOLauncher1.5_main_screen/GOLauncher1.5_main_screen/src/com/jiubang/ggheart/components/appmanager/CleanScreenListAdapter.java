package com.jiubang.ggheart.components.appmanager;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.gau.go.launcherex.R;
import com.go.util.graphics.BitmapUtility;
import com.jiubang.ggheart.components.DeskTextView;
import com.jiubang.ggheart.data.info.FavoriteInfo;
import com.jiubang.ggheart.data.info.ItemInfo;
import com.jiubang.ggheart.data.info.ShortCutInfo;

/**
 * 
 * <br>类描述:清理屏幕结构适配器
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2013-5-10]
 */
public class CleanScreenListAdapter extends BaseAdapter {
	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private ArrayList<CleanScreenInfo> mDataSource = new ArrayList<CleanScreenInfo>();

	public CleanScreenListAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if (mDataSource == null) {
			return 0;
		}
		return mDataSource.size();
	}

	@Override
	public CleanScreenInfo getItem(int position) {
		try {
			if (mDataSource != null && position < mDataSource.size()) {
				return mDataSource.get(position);
			}
		} catch (Exception e) {

		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		//重要：can not return int value larger than getViewTypeCount().
		return getItem(position).mType;
	}

	@Override
	public int getViewTypeCount() {
		return 6;
	}

	public void updateDataSource(ArrayList<CleanScreenInfo> dataSource) {
		mDataSource = dataSource;
		notifyDataSetChanged();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mDataSource == null || position < 0 || position >= mDataSource.size()) {
			return convertView;
		}
		

		int type = getItemViewType(position);
		switch (type) {
			case AppManagerUtils.TYPE_FARTHER_TITLE :
				convertView = getViewHolderTitle(position, convertView, parent);
				break;
				
			case AppManagerUtils.TYPE_NO_RESULT :
				convertView = getViewHolderNoResult(position, convertView, parent);
				break;

			case AppManagerUtils.TYPE_FAVOURITE :
				convertView = getViewHolderWidget(position, convertView, parent);
				break;
				
			case AppManagerUtils.TYPE_DESK_SHORTCUT :
			case AppManagerUtils.TYPE_FOLDER_SHORTCUT :
				convertView = getViewHolderIcon(position, convertView, parent);
				break;
				
			default :
				//TODO:返回加载失败页面
				break;

		}
		return convertView;
	}
	
	
	
	
	
	
	public View getViewHolderTitle(final int position, View convertView, ViewGroup parent) {
		ViewHolderTitle holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.clean_screen_list_item_title, null);
			holder = new ViewHolderTitle();
			holder.mTitle = (DeskTextView) convertView.findViewById(R.id.title);
			holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkbox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderTitle) convertView.getTag();
		}
		CleanScreenInfo cleanScreenInfo = mDataSource.get(position);
		holder.mTitle.setText(cleanScreenInfo.mFartherTitle);
		
		if (cleanScreenInfo.mIsShowCheckBox) {
			holder.mCheckBox.setVisibility(View.VISIBLE);
			holder.mCheckBox.setChecked(cleanScreenInfo.mIsCheck);
		} else {
			holder.mCheckBox.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	public View getViewHolderWidget(final int position, View convertView, ViewGroup parent) {
		ViewHolderWidget holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.clean_screen_list_item_widget, null);
			holder = new ViewHolderWidget();
			holder.mIconImage = (ImageView) convertView.findViewById(R.id.icon_iamge);
			holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkbox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderWidget) convertView.getTag();
		}
		CleanScreenInfo cleanScreenInfo = mDataSource.get(position);
		ItemInfo itemInfo = cleanScreenInfo.mItemInfo;
	
		if (itemInfo != null && itemInfo instanceof FavoriteInfo) {
			FavoriteInfo favoriteInfo = (FavoriteInfo) itemInfo;
			Bitmap iconBitmap = BitmapFactory.decodeResource(mContext.getResources(), favoriteInfo.mPreview);
			float width = mContext.getResources().getDimension(R.dimen.clean_screen_widget_icon_width);
			float scale = width / iconBitmap.getWidth();
			float height = iconBitmap.getHeight() * scale;
			holder.mIconImage.setImageBitmap(BitmapUtility.createScaledBitmap(iconBitmap,
					(int) width, (int) height));
		}
		
		holder.mCheckBox.setChecked(cleanScreenInfo.mIsCheck);
		return convertView;
	}
	
	
	public View getViewHolderIcon(final int position, View convertView, ViewGroup parent) {
		ViewHolderIcon holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.clean_screen_list_item_icon, null);
			holder = new ViewHolderIcon();
			holder.mIconImage = (ImageView) convertView.findViewById(R.id.icon_iamge);
			holder.mIconName = (DeskTextView) convertView.findViewById(R.id.icon_name);
			holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkbox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderIcon) convertView.getTag();
		}
		CleanScreenInfo cleanScreenInfo = mDataSource.get(position);
		ItemInfo itemInfo = cleanScreenInfo.mItemInfo;
		if (itemInfo != null && itemInfo instanceof ShortCutInfo) {
			ShortCutInfo shortCutInfo = (ShortCutInfo) itemInfo;
			holder.mIconImage.setImageDrawable(shortCutInfo.mIcon);
			holder.mIconName.setText(shortCutInfo.mTitle);
		}
	
		holder.mCheckBox.setChecked(cleanScreenInfo.mIsCheck);
		return convertView;
	}
	
	public View getViewHolderNoResult(final int position, View convertView, ViewGroup parent) {
		ViewHolderNoResult holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.clean_screen_list_item_no_result, null);
			holder = new ViewHolderNoResult();
			holder.mTitle = (DeskTextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderNoResult) convertView.getTag();
		}
		CleanScreenInfo cleanScreenInfo = mDataSource.get(position);
		holder.mTitle.setText(cleanScreenInfo.mNoResultTitle);
		return convertView;
	}
	
	/**
	 * 标题
	 */
	public class ViewHolderTitle {
		public DeskTextView mTitle;
		public CheckBox mCheckBox;
	}
	
	/**
	 * 普通图标
	 */
	public class ViewHolderWidget {
		public ImageView mIconImage;
		public CheckBox mCheckBox;
	}
	
	/**
	 * 普通图标
	 */
	public class ViewHolderIcon {
		public ImageView mIconImage;
		public DeskTextView mIconName;
		public CheckBox mCheckBox;
	}
	
	/**
	 * 没有结果
	 */
	public class ViewHolderNoResult {
		public DeskTextView mTitle;
	}
}
