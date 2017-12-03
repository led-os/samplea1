/**
 * 
 */
package com.jiubang.ggheart.appgame.gostore.base.component;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.appgame.base.bean.CategoriesDataBean;
import com.jiubang.ggheart.appgame.base.bean.ClassificationDataBean;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager.AsyncImageLoadedCallBack;
import com.jiubang.ggheart.appgame.base.manage.TabController;
import com.jiubang.ggheart.appgame.base.utils.MD5;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 九宫格分类Adapter
 * 
 * @author zhouxuewen
 * 
 */
public class GridSortAdapter extends BaseAdapter {
	/**
	 * 不同的布局类型总数
	 */
	private final int mTYPE_COUNT = 2;
	/**
	 * 固定的头部布局类型
	 */
	private final int mTYPE_HEAD = 0;
	/**
	 * 显示APP的布局类型
	 */
	private final int mTYPE_APP = 1;

	private List<CategoriesDataBean> mRecommAppCtgList = new ArrayList<CategoriesDataBean>();
	private Context mContext = null;
	private AsyncImageManager mImgManager = AsyncImageManager.getInstance();
	/**
	 * 列表默认图标
	 */
	private Drawable mDefaultIcon = null;
	/**
	 * 该分类推荐页是否在激活状态
	 */
	private boolean mIsActive = false;
	private LayoutInflater mInflater;
	private AppGameAdBannerHeadView mHeadView;
	/**
	 * 头部banner的数据
	 */
	private ClassificationDataBean mAdBanner;
	/**
	 * 标志是否带头部view　true:有头部
	 */
	private boolean mHasHeadView = false;

	public GridSortAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * 刷新数据并调用notifyDataSetChanged
	 */
	public void refreshData(List<CategoriesDataBean> recommAppCtgList) {
		mRecommAppCtgList.clear();
		if (recommAppCtgList == null) {
			return;
		}
		for (CategoriesDataBean category : recommAppCtgList) {
			mRecommAppCtgList.add(category);
		}
		// notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mRecommAppCtgList == null) {
			return 0;
		} else if (mHasHeadView) {
			return mRecommAppCtgList.size() / 2 + 1;
		} else {
			return mRecommAppCtgList.size() / 2;
		}
	}

	@Override
	public Object getItem(int position) {
		if (mRecommAppCtgList == null || position < 0
				|| position >= mRecommAppCtgList.size()) {
			return null;
		}
		return mRecommAppCtgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return mTYPE_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		return (mHasHeadView && position == 0) ? mTYPE_HEAD : mTYPE_APP;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mRecommAppCtgList != null && position < mRecommAppCtgList.size()) {
			int type = getItemViewType(position);
			if (convertView == null) {
				if (type == mTYPE_HEAD) {
					mHeadView = new AppGameAdBannerHeadView(mContext);
					convertView = mHeadView;
					updateAdBanner();
				} else {
					convertView = mInflater.inflate(
							R.layout.app_game_grid_sort_item, null);
				}
			}
			if (type == mTYPE_APP) {
				for (int i = 0; i < 2; i++) {
					int nPosition = position * 2 + i;
					if (mHasHeadView) {
						nPosition = (position - 1) * 2 + i;
					}
					if (nPosition < mRecommAppCtgList.size()) {
						final CategoriesDataBean recommAppCtg = mRecommAppCtgList.get(nPosition);
						GridSortItem item;
						if (i == 0) {
							item = (GridSortItem) convertView
									.findViewById(R.id.item1);
						} else {
							item = (GridSortItem) convertView
									.findViewById(R.id.item2);
						}
						TextView title = item.getTextView();
						String name = recommAppCtg.name;
						if (name != null) {
							name = name.trim();
						}
						title.setText(name);
						String apps = recommAppCtg.desc;
						if (apps != null) {
							apps = apps.trim();
						}
						ImageView image = item.getImageView();
						if (mIsActive) {
							if (recommAppCtg.pic != null
									&& !recommAppCtg.pic.trim().equals("")) {
								String fileName = MD5.encode(recommAppCtg.pic);
								String localPath = LauncherEnv.Path.APP_MANAGER_ICON_PATH;
								setIcon(image, recommAppCtg.pic, localPath,
										fileName);
							} else {
								image.setImageDrawable(mDefaultIcon);
							}
						} else {
							image.setImageDrawable(mDefaultIcon);
						}
						item.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if (recommAppCtg == null) {
									return;
								}
								switch (recommAppCtg.feature) {
									case CategoriesDataBean.FEATURE_FOR_DEFAULT:
										// 进入下一级tab栏
										TabController.skipToTheNextTab(recommAppCtg.typeId, recommAppCtg.name, -1, true, -1, -1, null);
										break;
								}
							}
						});
					}

				}
			}
		}
		return convertView;
	}

	public void setAdBanner(ClassificationDataBean bean) {
		mAdBanner = bean;
		updateAdBanner();
	}

	/**
	 * 设置是否带头部view
	 * @param flag
	 */
	public void setHasHeadView(boolean flag) {
		mHasHeadView = flag;
	}

	public void updateAdBanner() {
		if (mAdBanner != null && mHeadView != null) {
			mHeadView.updateContent(mAdBanner);
		}
	}
	
	public View getHeadView() {
		return mHeadView;
	}

	/**
	 * 读取图标，然后设到imageview里
	 */
	private void setIcon(final ImageView imageView, String imgUrl,
			String imgPath, String imgName) {
		imageView.setTag(imgUrl);
		Bitmap bm = mImgManager.loadImage(imgPath, imgName, imgUrl, true,
				false, null, new AsyncImageLoadedCallBack() {
					@Override
					public void imageLoaded(Bitmap imageBitmap, String imgUrl) {
						if (imageView.getTag().equals(imgUrl) && mIsActive) {
							imageView.setImageBitmap(imageBitmap);
						} else {
							imageBitmap = null;
							imgUrl = null;
						}
					}
				});
		if (bm != null) {
			imageView.setImageBitmap(bm);
		} else {
			imageView.setImageDrawable(mDefaultIcon);
		}
	}

	/**
	 * 更改激活状态，如果是true则getview时会加载图标，否则不加载图标
	 * 
	 * @param isActive
	 *            是否为激活状态
	 */
	public void onActiveChange(boolean isActive) {
		mIsActive = isActive;
	}

	/**
	 * 设置列表默认的图标
	 */
	public void setDefaultIcon(Drawable icon) {
		mDefaultIcon = icon;
	}

}
