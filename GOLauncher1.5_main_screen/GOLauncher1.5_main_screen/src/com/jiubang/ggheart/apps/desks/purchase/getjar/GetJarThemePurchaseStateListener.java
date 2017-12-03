package com.jiubang.ggheart.apps.desks.purchase.getjar;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.gau.go.launcherex.theme.aidl.IPurchaseTheme;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeConstants;
import com.jiubang.ggheart.data.ContentProvider.GoContentProvider;
import com.jiubang.ggheart.data.tables.PurchaseTable;
import com.jiubang.ggheart.data.theme.bean.ThemeInfoBean;
/**
 * 
 * <br>类描述:getjar主题付费类型查询工具类
 * <br>功能详细描述:绑定到getjar主题的srvice查询主题是否已付过费，如果未付费通过AIDL访问主题付费接口
 * 
 * @author  rongjinsong
 * @date  [2014年3月18日]
 */
public class GetJarThemePurchaseStateListener {
	public static final int THEME_PURCHASED = 2; //主题已付过费
	private IPurchaseTheme mPurchaseManager;
	private BindListener mListener;
	private Context mContext;
	private ThemeInfoBean mThemeInfo;
	public void bindService(Context context, ThemeInfoBean info, BindListener listener) {
		mContext = context;
		mThemeInfo = info;
		mListener = listener;
		Intent intent = new Intent();
		intent.setComponent(new ComponentName(info.getPackageName(),
				info.getPackageName() + ThemeConstants.GET_JAR_THEME_SERVICE));
		context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mPurchaseManager = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, final IBinder service) {
			// TODO Auto-generated method stub
			mPurchaseManager = IPurchaseTheme.Stub.asInterface(service);
			try {
				if (mPurchaseManager.getPurchaseStatus() == THEME_PURCHASED) {
					try {
						if(name.getPackageName() != null){
							ContentResolver resolver = mContext.getContentResolver();
							ContentValues values = new ContentValues();
							values.put(PurchaseTable.PRODUCT_ID, name.getPackageName());
							resolver.insert(GoContentProvider.CONTENT_PURCHASE_URI, values);
						}
						if (mListener != null) {
							mListener.applyTheme(mThemeInfo);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally {
						unbindService();
					}
				} else {
					new Thread() {
						public void run() {
							String result;
							try {
								result = mPurchaseManager.startPurchase();
								if (result != null) {
									if (result.equals(ThemeConstants.THEME_PURCHASE_RESULT_OK)) {
										if (mListener != null) {
											mListener.applyTheme(mThemeInfo);
										}
									} else if (result
											.startsWith(ThemeConstants.THEME_PURCHASE_RESULT_CHOOSE_GETJAR)) {
										int price = 0;
										int index = result.indexOf("#");
										if (index > 0 && index < result.length()) {
											try {
												price = Integer.valueOf(result.substring(result
														.indexOf("#") + 1));
											} catch (NumberFormatException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}

										}
										GetJarManager.buildInstance().requestPurchase(
												mThemeInfo.getPackageName(),
												mThemeInfo.getThemeName(),
												mThemeInfo.getThemeName(), price,
												new IGetJarPurchaseListener() {

													@Override
													public void handlePurchaseResult(int result) {
														// TODO Auto-generated method stub
														if (result == GetJarPurchaseActivity.PURCHSE_RESULT_OK) {
															if (mListener != null) {
																mListener.applyTheme(mThemeInfo);
															}
														}
													}
												});
									}
								}
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
								unbindService();
							}
						}
					}.start();
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	};
	public static interface BindListener {
		public void applyTheme(ThemeInfoBean info);
	}

	private void unbindService(){
		if (mContext != null) {
			try {
				mContext.unbindService(mConnection);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
