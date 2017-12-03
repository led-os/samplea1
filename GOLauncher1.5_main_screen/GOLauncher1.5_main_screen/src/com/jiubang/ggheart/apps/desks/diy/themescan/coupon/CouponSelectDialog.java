package com.jiubang.ggheart.apps.desks.diy.themescan.coupon;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.launcherex.R;
import com.gau.utils.cache.CacheManager;
import com.gau.utils.cache.impl.FileCacheImpl;
import com.gau.utils.cache.utils.CacheUtil;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.ApplicationProxy;
import com.go.util.BroadCaster.BroadCasterObserver;
import com.go.util.device.Machine;
import com.go.util.file.FileUtil;
import com.jiubang.ggheart.appgame.appcenter.help.RecommAppsUtils;
import com.jiubang.ggheart.appgame.base.net.DownloadUtil;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.MessageCenterActivity;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.MessageManager;
import com.jiubang.ggheart.apps.desks.diy.themescan.BannerDetailActivity;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeConstants;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemePurchaseManager;
import com.jiubang.ggheart.apps.desks.net.CryptTool;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.billing.AppInBillingActivity;
import com.jiubang.ggheart.billing.AppInBillingRequestReceiver;
import com.jiubang.ggheart.billing.ThemeAppInBillingManager;
import com.jiubang.ggheart.billing.base.Consts;
import com.jiubang.ggheart.components.DeskActivity;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.data.theme.bean.ThemeInfoBean;
import com.jiubang.ggheart.data.theme.broadcastReceiver.MyThemeReceiver;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
/**
 * 
 * <br>类描述:优惠劵选择框
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2013年8月23日]
 */
public class CouponSelectDialog extends DeskActivity
		implements
			BroadCasterObserver,
			OnItemClickListener,
			android.view.View.OnClickListener {

	private TextView mProgressText;
	private View mProgressBar;
	private CouponAdapter mAdapter;
	private GridView mGridView;
	private String mThemePkgName; //主题包名
	private float mThemePrice; //主题价格
	private String mThemeType; //主题类型，是普通付费主题，getjar主题还是超级主题

	private CacheManager mCacheManager;
	private MessageManager mMsgManager;

	private BroadcastReceiver mPurchaseStateReceiver = null;

	private boolean mIsGettingCoupon = false;

	private String mCouponCode = "";

	private ThemeInfoBean mThemeInfoBean = null;

	private LayoutInflater mInflater;

	private Bitmap mCoupon1Gray;
	private Bitmap mCoupon2Gray;

	private static final int MSG_SCAN_COUPON_FINISH = 0x01;
	private static final int GET_COUPON_NO_NETWORK = 0x02;
	private static final int GET_COUPON_FINISH_SUCCESS = 0x03;
	private static final int GET_COUPON_FINISH_FAIL = 0x04;
	private static final int GET_COUPON_ERROR = 0x05;

	//android.test.purchased
	//普通和getjar主题使用不同面值的购买id
	private static final String COUPON_PURCHASEID_GETJAR_099 = "com_gau_go_launcherex_coupons"; //使用0.99元
	private static final String COUPON_PURCHASEID_GETJAR_199 = "com_gau_go_launcherex_coupons_advanced"; //使用1.99元
	private static final String COUPON_PURCHASEID_GETJAR_299 = "com_gau_go_launcherex_coupons_advanced299"; //使用2.99元
	private static final String COUPON_PURCHASEID_GETJAR_399 = "com_gau_go_launcherex_coupons_advanced399"; //使用3.99元

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coupon_choice_dialog);

		Intent intent = getIntent();
		mThemePkgName = intent.getStringExtra(ThemeConstants.PACKAGE_NAME_EXTRA_KEY);
		mThemePrice = intent.getFloatExtra(MyThemeReceiver.THEME_PRICE_INAPPBILLING, 1.99f);
		mThemeType = intent.getStringExtra(MyThemeReceiver.THEME_TYPE);
		mThemeInfoBean = intent.getParcelableExtra(MyThemeReceiver.USE_COUPON_THEME_INFO);
		if (mThemePkgName == null || mThemePkgName.equals("")) {
			finish();
			return;
		}

		init();
	}
	private void init() {
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mGridView = (GridView) findViewById(R.id.coupon_chose_list);
		//		mGridView.setVisibility(View.GONE);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mProgressBar = findViewById(R.id.progressbargroup);
		mProgressText = (TextView) findViewById(R.id.themestore_btmProgress_text);
		//		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.dialog_cancle).setOnClickListener(this);
		mCacheManager = new CacheManager(new FileCacheImpl(LauncherEnv.Path.COUPON_PATH));
		mMsgManager = MessageManager.getMessageManager(getApplicationContext());
		mMsgManager.registerObserver(this);
		registerPurchaseReceiver();
		scanCouponList();
	}

	/**
	 * 注册监听付费成功的广播
	 */
	private void registerPurchaseReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ICustomAction.ACTION_PURCHASE_STATE_RESULT);
		intentFilter.addAction(ICustomAction.ACTION_PURCHASE_SUPPORTED_RESULT);
		mPurchaseStateReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent != null) {
					String action = intent.getAction();
					if (ICustomAction.ACTION_PURCHASE_STATE_RESULT.equals(action)) {
						String itemId = intent
								.getStringExtra(ThemeAppInBillingManager.EXTRA_FOR_ITEMID);
						int state = intent.getIntExtra(ThemeAppInBillingManager.EXTRA_FOR_STATE, 0);
						String pkgName = intent
								.getStringExtra(ThemeAppInBillingManager.EXTRA_FOR_PACKAGENAME);
						if (Consts.DEBUG) {
							Log.i(Consts.TAG,
									"CouponMananerActivity  mPurchaseStateReceiver itemId:"
											+ itemId + ",state:" + state);
						}
						if (pkgName.equals(mThemePkgName) && state == 1) {
							mCacheManager.clearCache(mCouponCode);
							GuiThemeStatistics.guiStaticData(String.valueOf(mThemeInfoBean.getFeaturedId()),
									"j009", 1, "-1",
									String.valueOf(BannerDetailActivity.COUPON_SPEC_ID), "-1",
									"-1");
							//付费成功，关掉页面
							//							sendBillingStatisticsData(mThemePkgName, ThemeAppInBillingManager.STATISTICS_CLICK_COUPON, 1);
							if (mThemeType.equals("getjar")) {
								if (Consts.DEBUG) {
									Log.i(Consts.TAG, "getjar主题.....直接应用");
								}
								applyTheme(mThemePkgName);
							} else if (mThemeType.equals("common")) {
								ThemePurchaseManager mPurchaseManager = ThemePurchaseManager
										.getInstance(getApplicationContext());
								//								mPurchaseManager.purchaseState(state, pkgName);
								if (mThemeInfoBean != null) {
									if (Consts.DEBUG) {
										Log.i(Consts.TAG, "普通主题.....下载应用, pkgName = "
												+ mThemeInfoBean.getPackageName() + ",url = "
												+ mThemeInfoBean.getDownLoadUrl() + ",themeName = "
												+ mThemeInfoBean.getThemeName());
									}
									mPurchaseManager.startDownload(mThemeInfoBean);
								}
							}
							finish();
						}
					}
				}
			}
		};
		registerReceiver(mPurchaseStateReceiver, intentFilter);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			mIsGettingCoupon = false;
			mProgressBar.setVisibility(View.GONE);
			mProgressText.setText(R.string.loading);
			switch (msg.what) {
				case MSG_SCAN_COUPON_FINISH :
					mGridView.setVisibility(View.VISIBLE);
					ArrayList<CouponBean> list = null;
					if (msg.obj != null && msg.obj instanceof ArrayList
							&& ((ArrayList<CouponBean>) msg.obj).size() > 0) {
						list = (ArrayList<CouponBean>) msg.obj;
						mAdapter = new CouponAdapter(list);
						mGridView.setAdapter(mAdapter);
						setGridViewNumColumns();
						mGridView.setOnItemClickListener(CouponSelectDialog.this);
					} else {
						mAdapter = new CouponAdapter();
						mGridView.setAdapter(mAdapter);
						setGridViewNumColumns();
						mGridView.setOnItemClickListener(CouponSelectDialog.this);
					}
					break;

				case GET_COUPON_ERROR :
					Toast.makeText(CouponSelectDialog.this, R.string.msgcenter_msg_update_erro,
							1000).show();
					break;
				case GET_COUPON_NO_NETWORK :
					Toast.makeText(CouponSelectDialog.this, R.string.http_exception, 1000).show();
					break;
				case GET_COUPON_FINISH_FAIL :
					int toastId = R.string.coupon_get_fail_incorrect;
					int status = msg.arg1;
					if (status == 3) {
						toastId = R.string.coupon_get_fail_repeat;
					} else if (status == 4) {
						toastId = R.string.coupon_get_fail_none;
					}
					Toast.makeText(CouponSelectDialog.this, toastId, 1000).show();
					break;
				case GET_COUPON_FINISH_SUCCESS :
					CouponBean bean = (CouponBean) msg.obj;
					if (bean == null) {
						Toast.makeText(CouponSelectDialog.this, R.string.coupon_get_fail_invalid,
								1000).show();
						return;
					}
					if (!FileUtil.isSDCardAvaiable()) {
						Toast.makeText(CouponSelectDialog.this, R.string.coupon_click_get_fail,
								1000).show();
						return;
					}
					mGridView.setVisibility(View.VISIBLE);
					Toast.makeText(CouponSelectDialog.this, R.string.coupon_get_success, 1000)
							.show();
					if (mAdapter == null) {
						mAdapter = new CouponAdapter();
					}
					ArrayList<CouponBean> couponList = mAdapter.getCouponList();
					if (couponList == null) {
						couponList = new ArrayList<CouponBean>();
						mAdapter.setCouponList(couponList);
						mGridView.setAdapter(mAdapter);
						setGridViewNumColumns();
						mGridView.setOnItemClickListener(CouponSelectDialog.this);
					}
					couponList.add(bean);
					mAdapter.notifyDataSetChanged();
					break;
				default :
					break;
			}
		}

	};

	/**
	 * 
	 * @author liulixia
	 *
	 */
	public class CouponAdapter extends BaseAdapter {

		private ArrayList<CouponBean> mCouponList;

		public CouponAdapter() {

		}

		public CouponAdapter(ArrayList<CouponBean> list) {
			mCouponList = list;
		}

		public ArrayList<CouponBean> getCouponList() {
			return mCouponList;
		}

		public void setCouponList(ArrayList<CouponBean> list) {
			mCouponList = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (mCouponList != null) {
				return mCouponList.size() + 1;
			}
			return 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (mCouponList != null && position < mCouponList.size()) {
				return mCouponList.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = null;

			if (convertView != null) {
				view = convertView;
			} else {
				view = mInflater.inflate(R.layout.couponview, null);
			}
			ImageView img = (ImageView) view.findViewById(R.id.couponimg);
			TextView txt = (TextView) view.findViewById(R.id.date);
			if (position == getCount() - 1) {
				img.setImageResource(R.drawable.add_coupon_icon);
				txt.setVisibility(View.INVISIBLE);
				return view;
			} else {
				txt.setVisibility(View.VISIBLE);

			}
			CouponBean bean = (CouponBean) getItem(position);
			if (bean.isVaild()) {
				int faceValue = bean.getFaceValue();
				if (faceValue == 2) {
					img.setImageResource(R.drawable.coupon_2);
				} else {
					img.setImageResource(R.drawable.coupon_1);
				}

				String msg = getString(R.string.coupon_expiredate);
				String stime = bean.getETime();
				String expireDate = stime.substring(0, 10);
				txt.setText(msg + expireDate);
			} else {
				txt.setText(R.string.coupon_expired);
				if (bean.getFaceValue() == 2) {
					if (mCoupon2Gray == null) {
						mCoupon2Gray = toGrayscale(((BitmapDrawable) getResources().getDrawable(
								R.drawable.coupon_2)).getBitmap());
					}
					img.setImageBitmap(mCoupon2Gray);
				} else {
					if (mCoupon1Gray == null) {
						mCoupon1Gray = toGrayscale(((BitmapDrawable) getResources().getDrawable(
								R.drawable.coupon_1)).getBitmap());
					}
					img.setImageBitmap(mCoupon1Gray);
				}
			}
			return view;
		}

	}

	private void scanCouponList() {

		new Thread() {

			@Override
			public void run() {
				ArrayList<BaseBean> couponBeans = null;
				//				if(true){
				//					couponBeans = getCouponBeanList(null);
				//				}else{
				File dir = new File(LauncherEnv.Path.COUPON_PATH);
				if (dir.exists()) {
					String[] coupons = dir.list();
					if (coupons != null && coupons.length > 0) {
						couponBeans = getCouponBeanList(Arrays.asList(coupons));
					}
				}
				//				}
				Message msg = mHandler.obtainMessage(MSG_SCAN_COUPON_FINISH);
				msg.obj = couponBeans;
				mHandler.sendMessage(msg);
			}

		}.start();

	}

	private ArrayList<BaseBean> getCouponBeanList(List<String> fileList) {
		ArrayList<BaseBean> beans = new ArrayList<BaseBean>();
		//		if(true){
		//			 beans.add(test());
		//			 beans.add(test());
		//			 return beans;
		//		}
		for (String path : fileList) {
			if (path != null) {
				BaseBean bean = parseBean(path);
				if (bean != null) {
					beans.add(bean);
				}
			}
		}
		return beans;
	}

	//	private BaseBean test(){
	//		return new CouponBean("sadadsasdasdasd", 1, String.valueOf("2013-08-22 16:14:40"), "2013-08-27 16:14:40");
	//	}

	private BaseBean parseBean(String couponName) {
		BaseBean bean = null;
		if (couponName != null) {
			if (mCacheManager.isCacheExist(couponName)) {
				byte[] cacheData = mCacheManager.loadCache(couponName);
				if (cacheData != null) {
					JSONObject obj = CacheUtil.byteArrayToJson(cacheData);
					int type = obj.optInt(BaseBean.TAG_TYPE);
					boolean used = obj.optBoolean(BaseBean.TAG_USED);
					String code = obj.optString(BaseBean.TAG_CODE);
					String stime = obj.optString(BaseBean.TAG_STIME);
					String etime = obj.optString(BaseBean.TAG_ETIME);
					if (type == BaseBean.TYPE_COUPON) {
						int value = obj.optInt(CouponBean.TAG_FACEVALE);
						bean = new CouponBean(code, value, stime, etime);
					} else {
						bean = new PromotionBean(code, stime, etime);
					}
					bean.setUsed(used);
				}
			}
		}
		return bean;
	}

	/**
	     * 图片去色,返回灰度图片
	     * @param bmpOriginal 传入的图片
	    * @return 去色后的图片
	    */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		if (bmpOriginal == null) {
			return null;
		}
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
			case R.id.dialog_cancle :
				finish();
				break;
			default :
				break;
		}
	}

	@Override
	public void onBCChange(int msgId, int param, Object ...object) {
		switch (msgId) {
			case MessageCenterActivity.GET_MSG_NO_NETWORK :  //没有网络
				mHandler.sendEmptyMessage(GET_COUPON_NO_NETWORK);
				break;
			case MessageCenterActivity.GET_MSG_CONTENT_FAILED : //服务器返回失败结果
				mHandler.sendEmptyMessage(GET_COUPON_ERROR);
				break;
			case MessageCenterActivity.GET_MSG_COUPON_FINISH :
				Message msg = Message.obtain();
				if (param != 1) { //获取不到优惠券
					msg.what = GET_COUPON_FINISH_FAIL;
					msg.arg1 = param;
				} else {
					msg.what = GET_COUPON_FINISH_SUCCESS;
					msg.obj = object[0];
				}
				mHandler.sendMessage(msg);
				break;
			default :
				break;
		}

	}

	public void onAddCoupon() {
		if (mIsGettingCoupon) {
			return;
		}
		//TODO 根据优惠券号获取优惠券信息
		final CouponRequestDialog dialog = new CouponRequestDialog(this);
		dialog.show();
		dialog.setPositiveButton(null, new OnClickListener() {

			@Override
			public void onClick(View v) {
				String couponId = dialog.mCouponId.getText().toString();
				if (couponId.equals("")) {
					Toast.makeText(CouponSelectDialog.this, R.string.coupon_code_not_empty, 1000)
							.show();
				} else {
					mIsGettingCoupon = true;
					mProgressBar.setBackgroundColor(Color.parseColor("#5c000000"));
					mProgressBar.setVisibility(View.VISIBLE);
					mProgressText.setText(R.string.coupon_requesting);
					if (mMsgManager != null) {
						mMsgManager.postGetCouponRequest(couponId);
					}
				}
			}
		});
		return;
	}

	public void onCouponSelect(CouponBean bean) {
		if (bean != null && bean.isVaild()) {
			int faceValue = bean.getFaceValue();
			GuiThemeStatistics.guiStaticData(String.valueOf(mThemeInfoBean.getFeaturedId()),
					"j009", 0, "-1",
					String.valueOf(BannerDetailActivity.COUPON_SPEC_ID), "-1",
					"-1");
			if (faceValue < mThemePrice) {
				// 优惠券必须小于主题价格才可使用
				String itemId = null;
				float remainder = mThemePrice - faceValue;
				if (remainder > 0 && remainder < 1) {
					itemId = COUPON_PURCHASEID_GETJAR_099;
				} else if (remainder > 1 && remainder < 2) {
					itemId = COUPON_PURCHASEID_GETJAR_199;
				} else if (remainder > 2 && remainder < 3) {
					itemId = COUPON_PURCHASEID_GETJAR_299;
				} else if (remainder > 3 && remainder < 4) {
					itemId = COUPON_PURCHASEID_GETJAR_399;
				}
				if (itemId != null) {
					mCouponCode = bean.getCode();
					// sendBillingStatisticsData(mThemePkgName,
					// ThemeAppInBillingManager.STATISTICS_CLICK_COUPON, 0);
					Intent startIntent = new Intent();
					startIntent.putExtra(
							AppInBillingRequestReceiver.EXTRA_FOR_ITEMID,
							itemId);
					startIntent.putExtra(
							AppInBillingRequestReceiver.EXTRA_FOR_PACKAGENAME,
							mThemePkgName);
					startIntent
							.putExtra(
									AppInBillingRequestReceiver.EXTRA_FOR_PRODUCTUNMANAGED,
									true);
					startIntent.putExtra(
							AppInBillingRequestReceiver.EXTRA_FOR_THEMETYPE,
							mThemeType);
					startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startIntent.setClass(this, AppInBillingActivity.class);
					startActivity(startIntent);
				}
			} else {
				Toast.makeText(CouponSelectDialog.this,
						R.string.coupon_higher_price, 800).show();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mIsGettingCoupon
				|| (mThemePkgName == null && position != mGridView.getAdapter().getCount() - 1)) {
			return;
		}
		if (position == mGridView.getAdapter().getCount() - 1) {
			onAddCoupon();
		} else {
			onCouponSelect((CouponBean) mGridView.getAdapter().getItem(position));
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setGridViewNumColumns();
	}

	private void setGridViewNumColumns() {
		DisplayMetrics mMetrics = getResources().getDisplayMetrics();
		if (mMetrics.widthPixels <= mMetrics.heightPixels) {
			mGridView.setNumColumns(2);
		} else {
			mGridView.setNumColumns(3);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mMsgManager != null) {
			mMsgManager.unRegisterObserver(this);
			mMsgManager = null;
		}

		if (mPurchaseStateReceiver != null) {
			unregisterReceiver(mPurchaseStateReceiver);
			mPurchaseStateReceiver = null;
		}
	}

	private void sendBillingStatisticsData(final String packageName, final int paidType,
			final int paidSucessed) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String data = createData(packageName, paidType, paidSucessed);
					if (Consts.DEBUG) {
						Log.i(Consts.TAG, "优惠券统计数据：" + data);
					}
					if (data != null) {
						String statisticsData = getStatisticsData(data);
						StringBuilder sb = new StringBuilder(
								ThemeAppInBillingManager.sStatisticsDataUrl);
						sb.append(Machine.getAndroidId());
						sb.append("&goid=");
						sb.append(StatisticsManager.getGOID(CouponSelectDialog.this));
						if (Consts.DEBUG) {
							Log.i(Consts.TAG, "优惠券url：" + sb.toString());
						}
						DownloadUtil.sendDataByPost(sb.toString(), statisticsData);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	private String createData(String packageName, int paidType, int paidSucessed) {
		if (packageName != null) {
			StringBuffer stringBuffer = new StringBuffer();
			String imei = Statistics.getVirtualIMEI(CouponSelectDialog.this);
			String country = RecommAppsUtils.local(CouponSelectDialog.this);
			stringBuffer
					.append(imei)
					.append(ThemeAppInBillingManager.STATISTICS_DATA_SEPARATE_STRING)
					.append(country)
					.append(ThemeAppInBillingManager.STATISTICS_DATA_SEPARATE_STRING)
					.append(packageName)
					.append(ThemeAppInBillingManager.STATISTICS_DATA_SEPARATE_STRING)
					.append(paidType)
					.append(ThemeAppInBillingManager.STATISTICS_DATA_SEPARATE_STRING)
					.append(paidSucessed)
					.append(ThemeAppInBillingManager.STATISTICS_DATA_SEPARATE_STRING)
					.append(ThemeAppInBillingManager.STATISTICS_DATA_DEFAULT_VALUE)
					//获取金币方式
					.append(ThemeAppInBillingManager.STATISTICS_DATA_SEPARATE_STRING)
					.append(ThemeAppInBillingManager.STATISTICS_DATA_DEFAULT_VALUE)
					//是否点击
					.append(ThemeAppInBillingManager.STATISTICS_DATA_SEPARATE_STRING)
					.append(ThemeAppInBillingManager.STATISTICS_DATA_DEFAULT_VALUE)
					//tapjoy是否成功返回金币
					.append(ThemeAppInBillingManager.STATISTICS_DATA_SEPARATE_STRING)
					.append(ThemeAppInBillingManager.STATISTICS_DATA_DEFAULT_VALUE)
					//是否安装桌面
					.append(ThemeAppInBillingManager.STATISTICS_DATA_SEPARATE_STRING)
					.append(ThemeAppInBillingManager.STATISTICS_DATA_DEFAULT_VALUE)
					//是否激活
					.append(ThemeAppInBillingManager.STATISTICS_DATA_SEPARATE_STRING)
					.append(ThemeAppInBillingManager.STATISTICS_DATA_DEFAULT_VALUE)
					//是否带桌面安装
					.append(ThemeAppInBillingManager.STATISTICS_DATA_SEPARATE_STRING)
					.append(ThemeAppInBillingManager.STATISTICS_DATA_DEFAULT_VALUE)
					//价格
					.append(ThemeAppInBillingManager.STATISTICS_DATA_SEPARATE_STRING)
					.append(ThemeAppInBillingManager.STATISTICS_DATA_DEFAULT_VALUE)
					//安装时间戳
					.append(ThemeAppInBillingManager.STATISTICS_DATA_SEPARATE_STRING)
					.append(ThemeAppInBillingManager.STATISTICS_DATA_DEFAULT_VALUE)
					//付费成功时间戳
					.append(ThemeAppInBillingManager.STATISTICS_DATA_SEPARATE_STRING)
					.append(GoStorePhoneStateUtil.getUid(CouponSelectDialog.this))
					//渠道号
					.append(ThemeAppInBillingManager.STATISTICS_DATA_SEPARATE_STRING)
					.append(mCouponCode); //优惠券号
			return stringBuffer.toString();
		}
		return null;
	}

	/**
	 * 对统计数据进行加密
	 * @param statistics
	 * @return
	 */
	private String getStatisticsData(String statistics) {
		byte[] statisticsByte = null;
		//对所有的统计数据进行加密
		if (statistics != null) {
			statistics = CryptTool.encrypt(statistics,
					ThemeAppInBillingManager.STATISTICS_DATA_ENCRYPT_KEY);
			try {
				statisticsByte = statistics.getBytes(ThemeAppInBillingManager.STATISTICS_DATA_CODE);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		String result = null;
		try {
			result = new String(statisticsByte, ThemeAppInBillingManager.STATISTICS_DATA_CODE);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private void applyTheme(String pkgName) {
		Context context = ApplicationProxy.getContext();
		if (null == context || null == ThemeManager.getInstance(context)) {
			return;
		}

		ThemeManager tmg = ThemeManager.getInstance(context);
		String curThemePackage = tmg.getCurThemePackage();
		if (pkgName != null && pkgName.equals(curThemePackage)) {
			// 如果当前已经应用该主题，则提示
			// Toast.makeText(GoLauncher.getContext(),R.string.theme_already_using,
			// Toast.LENGTH_SHORT).show();
		} else if (!ThemeManager.isInstalledTheme(context, pkgName)) {
			// new add
			Toast.makeText(this, "Theme is not installed on your phone",
					Toast.LENGTH_SHORT).show();
		} else {
			Intent intentGoLauncher = new Intent();
			intentGoLauncher.setClassName(context, IGoLauncherClassName.GOLAUNCHER_ACTIVITY);
			intentGoLauncher.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intentGoLauncher.putExtra(MyThemeReceiver.ACTION_TYPE_STRING,
					MyThemeReceiver.CHANGE_THEME);
			intentGoLauncher.putExtra(MyThemeReceiver.PKGNAME_STRING, pkgName);
			context.startActivity(intentGoLauncher);
		}
	}
}
