package com.jiubang.ggheart.apps.desks.diy.filter;

import java.io.IOException;
import java.util.Locale;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.filter.core.FilterService;
import com.jiubang.ggheart.apps.desks.diy.filter.view.FilterChooser;
import com.jiubang.ggheart.apps.desks.diy.filter.view.FilterPreview;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.components.DeskActivity;
import com.jiubang.ggheart.components.DeskResources;
import com.jiubang.ggheart.components.DeskResourcesConfiguration;
import com.jiubang.ggheart.components.GoProgressBar;

/**
 * 滤镜界面
 * 
 * @author zouguiquan
 * 
 */
public class FilterActivity extends DeskActivity implements Callback,
		View.OnClickListener {

	private FilterChooser mFilterChooser;
	private FilterPreview mFilterPreview;
	private View mBack;
	private GoProgressBar mGoProgressBar;

	private Handler mHandler = new Handler(this);
	private int mEntranceId;

	public static final String PAY_ENTRANCE_ID_INDEX = "pay_extra_id_index";

	public static final int HANDER_MESSAGE_LOAD_WALLPAPER = 1;
	public static final int HANDER_MESSAGE_SHOW_WALLPAPER_PREVIEW = 2;
	public static final int HANDER_MESSAGE_ON_RENDER_FINISH = 3;
	public static final int HANDER_MESSAGE_SET_WALLPAPER = 4;
	public static final int HANDER_MESSAGE_UPDATE_PROGRESS_STATE = 5;

	private BroadcastReceiver mWallpaperReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_WALLPAPER_CHANGED)) {
				mGoProgressBar.setVisibility(View.GONE);
				mBlockBackKey = false;
			}
		}
	};
	protected boolean mIsInit;
	private boolean mBlockBackKey;
	private int mPayExtraIdIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerWallpaperBroadcast();
		setContentView(R.layout.diy_filter_main_view);

		if (getIntent() != null) {
			mPayExtraIdIndex = getIntent().getIntExtra(PAY_ENTRANCE_ID_INDEX, 0);
		}

		initView();
		if (!testFilterLib()) {
			return;
		}
		initData();
		loadWallpaper();
	}

	@Override
	public Resources getResources() {
		DeskResourcesConfiguration configuration = DeskResourcesConfiguration.createInstance(this
				.getApplicationContext());
		if (null != configuration) {
			Resources resources = configuration.getDeskResources();
			if (null != resources) {
				return resources;
			}
		}
		return super.getResources();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Resources res = getResources();
		if (res instanceof DeskResources) {
			res.updateConfiguration(super.getResources().getConfiguration(), super.getResources()
					.getDisplayMetrics());

			try {
				Configuration config = res.getConfiguration(); // 获得设置对象
				DisplayMetrics dm = res.getDisplayMetrics(); // 获得屏幕参数：主要是分辨率，像素等。
				PreferencesManager preferences = new PreferencesManager(this,
						IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
				String currentlanguage = preferences.getString(
						IPreferencesIds.CURRENT_SELECTED_LANGUAGE, "");
				if (currentlanguage != null && !currentlanguage.equals("")) {
					if (currentlanguage.length() == 5) {
						String language = currentlanguage.substring(0, 2);
						String country = currentlanguage.substring(3, 5);
						config.locale = new Locale(language, country);
					} else {
						config.locale = new Locale(currentlanguage);
					}
					res.updateConfiguration(config, dm);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mFilterChooser != null) {
			mFilterChooser.postDelayed(new Runnable() {

				@Override
				public void run() {
					setPayState();
				}
			}, 150);
		}
	}

	public boolean testFilterLib() {
		if (FilterService.sLoadLibError) {
			Toast.makeText(this, R.string.filter_unsupport, Toast.LENGTH_LONG).show();
			finish();
			return false;
		}
		return true;
	}
	
	void setPayState() {
		if (FunctionPurchaseManager.getInstance(getApplicationContext())
				.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_FILTER)) {
			if (mFilterChooser != null) {
				mFilterChooser.setPayied(true);
			} 
			if (mFilterPreview != null) {
				mFilterPreview.setPayied(true);
			} 
		} else {
			if (mFilterChooser != null) {
				mFilterChooser.setPayied(false);
			} 
			if (mFilterPreview != null) {
				mFilterPreview.setPayied(false);
			} 
		}
	}

	private void registerWallpaperBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_WALLPAPER_CHANGED);
		registerReceiver(mWallpaperReceiver, intentFilter);
	}

	private void unRegisterWallpaperBoradcast() {
		if (mWallpaperReceiver != null) {
			unregisterReceiver(mWallpaperReceiver);
			mWallpaperReceiver = null;
		}
	}

	private void initData() {
		mFilterChooser.initData();
		mFilterPreview.initData();
	}

	private void initView() {
		mFilterPreview = (FilterPreview) findViewById(R.id.filter_preview);
		mFilterChooser = (FilterChooser) findViewById(R.id.filter_chooser);
		mFilterChooser.initView();
		mFilterPreview.initView();
		mFilterChooser.setHandler(mHandler);
		mFilterPreview.setHandler(mHandler);

		mBack = findViewById(R.id.image_back);
		mBack.setOnClickListener(this);

		mGoProgressBar = (GoProgressBar) findViewById(R.id.progressBar);
		mGoProgressBar.setVisibility(View.VISIBLE);
	}

	private void loadWallpaper() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Bitmap wallpaper = FilterManager.getInstance().getWallpaper(
						FilterActivity.this);
				Message message = mHandler
						.obtainMessage(HANDER_MESSAGE_LOAD_WALLPAPER);
				message.obj = wallpaper;
				mHandler.sendMessage(message);
			}
		}).start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			
			if (mBlockBackKey) {
				return true;
			}

			if (mFilterPreview.getVisibility() == View.VISIBLE) {
				mFilterPreview.hide();
				mFilterChooser.show(mFilterPreview.getScreenCount(),
						mFilterPreview.getCurrentScreen());
				return true;
			}
			if (mFilterChooser.getVisibility() == View.VISIBLE) {
				if (mFilterChooser.onKeyDown(keyCode, event)) {
					return true;
				} else {
					finish();
				}
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case HANDER_MESSAGE_LOAD_WALLPAPER:
			if (msg.obj != null) {
				mFilterChooser.setWallpaper((Bitmap) msg.obj);

				// mFilterChooser保证已经layout完,才能获取宽高
				if (mFilterChooser.hasCompleteLayout()) {
					mFilterChooser.initThumbFilter();
				} else {
					mFilterChooser.getViewTreeObserver()
							.addOnGlobalLayoutListener(
									new OnGlobalLayoutListener() {

										@Override
										public void onGlobalLayout() {
											if (!mIsInit) {
												mIsInit = true;
												mFilterChooser
														.initThumbFilter();
											}
										}
									});
				}

				mGoProgressBar.setVisibility(View.GONE);
			}
			break;

		case HANDER_MESSAGE_SHOW_WALLPAPER_PREVIEW:
			mFilterChooser.setVisibility(View.GONE);
			mFilterPreview.show();
			break;

		case HANDER_MESSAGE_ON_RENDER_FINISH:
			if (msg.obj != null && msg.obj instanceof Bitmap) {
				Bitmap bitmap = (Bitmap) msg.obj;
				FilterManager.getInstance().setFilterBitmap(bitmap);
				mFilterChooser.setWallpaper(bitmap);
				mGoProgressBar.setVisibility(View.GONE);
			}
			break;

		case HANDER_MESSAGE_UPDATE_PROGRESS_STATE:
			if (msg.arg1 == View.VISIBLE) {
				mGoProgressBar.setVisibility(View.VISIBLE);
			} else if (msg.arg1 == View.GONE) {
				mGoProgressBar.setVisibility(View.GONE);
			}
			break;

		case HANDER_MESSAGE_SET_WALLPAPER:
			if (FunctionPurchaseManager.getInstance(getApplicationContext())
					.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_FILTER)) {

				// 当前选择为原图,以为当前壁纸
				if (mFilterChooser.getFilterThumbIndex() == 0) {
					// Toast.makeText(this, "He is the activity wallpaper now!",
					// Toast.LENGTH_SHORT).show();
					finish();
					return true;
				} else {
					mBlockBackKey = true;
					mGoProgressBar.setVisibility(View.VISIBLE);
					new Thread(new Runnable() {

						@Override
						public void run() {
							Bitmap bitmap = FilterManager.getInstance().getFilterBitmap();
							WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
							try {
								manager.setBitmap(bitmap);
							} catch (IOException e) {
								e.printStackTrace();
								mBlockBackKey = false;
							}
							finish();
						}
					}).start();
				}
			} else {
				if (mFilterChooser != null) {
					mEntranceId = mFilterChooser.getPayExtraId(mPayExtraIdIndex);
				}
				DeskSettingUtils.showPayDialog(this, mEntranceId);
			}

			break;

		default:
			break;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		postKillSelf(50);
	}

	private void postKillSelf(int delay) {
		unRegisterWallpaperBoradcast();
		FunctionPurchaseManager.destory();
		mFilterChooser.postDelayed(new Runnable() {

			@Override
			public void run() {
				// mFilterChooser.release();
				// FilterManager.getInstance().release();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}, delay);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_back:
			finish();
			break;

		default:
			break;
		}
	}
}
