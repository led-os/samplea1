package com.jiubang.ggheart.apps.desks.diy.filter.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gau.go.launcherex.R;

/**
 * 
 * @author zouguiquan
 *
 */
public class FilterWallpaperView extends RelativeLayout {
	private ImageView mFilterImage;

	public FilterWallpaperView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.diy_filter_chooser_wallpaper, this);

		initView();
	}

	private void initView() {
		mFilterImage = (ImageView) findViewById(R.id.filter_image);
	}

	public ImageView getFilterImage() {
		return mFilterImage;
	}

	public void setWallpaper(Bitmap bitmap, int displayHeight) {

		if (bitmap != null) {
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mFilterImage
					.getLayoutParams();
			params.height = bitmap.getHeight() / 2;
			mFilterImage.setImageBitmap(bitmap);
		}
	}
}
