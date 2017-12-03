package com.jiubang.ggheart.apps.desks.purchase.getjar;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.gau.go.launcherex.R;
import com.getjar.sdk.GetjarClient;
import com.getjar.sdk.GetjarConstants;
import com.getjar.sdk.GetjarLicense;
import com.getjar.sdk.GetjarVoucherRedeemedResult;
import com.getjar.sdk.OnGetjarLicensesReceivedListener;
import com.getjar.sdk.OnGetjarVoucherRedeemedListener;
import com.getjar.sdk.OnGetjarWorkFinishedListener;
import com.jiubang.ggheart.apps.desks.purchase.getjar.GetJarManager.IConnectCallBack;
import com.jiubang.ggheart.data.ContentProvider.GoContentProvider;
import com.jiubang.ggheart.data.tables.PurchaseTable;
/**
 * 
 * <br>类描述:getjar 付费处理页面
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2014年3月20日]
 */
public class GetJarPurchaseActivity extends Activity {

	public static final String PRODUCT_ID = "id";
	public static final String PRODUCT_NAME = "name";
	public static final String PRODUCT_INFO = "info";
	public static final String PRODUCT_PRICE = "price";
	public static final String PURCHSE_RESULT = "result";
	public static final String PURCHASE_RANDOWM_CODE = "code";
	public static final int PURCHSE_RESULT_OK = 0;
	public static final int PURCHSE_RESULT_FAILE = 1;
	private static final String TAG = "GetJarPurchaseActivity";
	private final static int REQUEST_PURCHASE = 3;
	private final static int REQUEST_RESOLUTION = 4;

	public final static String PURCHASE_REQUEST_ACTION = "com.gau.go.GETJAR_PURCHASE_REQUEST";
	public final static String PURCHASE_RESULT_ACTION = "com.gau.go.GETJAR_PURCHASE_RESULT";
	/** A set of voucher tokens that have already been redeemed by this process. */
	private ConcurrentLinkedQueue<String> mRedeemedVouchers = new ConcurrentLinkedQueue<String>();
	/** 
	 * This will queue up all the Intents that are received from Getjar. These will be processed after making sure that the app 
	 * has initialized Getjar properly using {@link GetjarClient#connect()} 
	 */
	private LinkedBlockingQueue<GetjarIntentWrapper> mReceivedIntents = new LinkedBlockingQueue<GetjarIntentWrapper>();
	/** The lock object used to make sure we process one Getjar intent at a time */
	private final Object mProcessIntentsLock = new Object();

	/** Used to execute potentially blocking operations */
	private static ExecutorService sExecutorService = Executors.newSingleThreadExecutor();
	private String mProductId;
	private String mProductName;
	private String mProductInfo;
	private long mProductPrice;
	private IGetJarPurchaseListener mPurchaseListener;
	private GetJarManager mGetJarManager;
	private static final int DEFAULT_PRICE = 100;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getjar_loading_layout);
		mProductId = getIntent().getStringExtra(PRODUCT_ID);
		mProductName = getIntent().getStringExtra(PRODUCT_NAME);
		mProductInfo = getIntent().getStringExtra(PRODUCT_INFO);
		mProductPrice = getIntent().getIntExtra(PRODUCT_PRICE, DEFAULT_PRICE);
		mProductPrice = mProductPrice <= 0 ? DEFAULT_PRICE : mProductPrice;
		if (mProductId == null || mProductId.equals("") || mProductName == null) {
			finish();
			return;
		}
		mGetJarManager = GetJarManager.buildInstance();
		if (mGetJarManager.getCurrentAccount() == null) {
			mGetJarManager.addConnectCallBack(mConnectCallback);
			GetJarManager.showSelectAccountDialog(this);
		} else {
			mGetJarManager.connect(mConnectCallback);
		}
		

	}

	private IConnectCallBack mConnectCallback = new  IConnectCallBack() {

		@Override
		public void onConnect(boolean connect, Intent result) {
			// TODO Auto-generated method stub
			if (connect) {
				mGetJarManager.getLicense(mProductId, mLicenseListener);
			} else if (result != null) {
				startActivityForResult(result, REQUEST_RESOLUTION);
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {

			case REQUEST_PURCHASE :
				if (resultCode == Activity.RESULT_OK) {
					enqueueGetjarIntent(data);
				} else {
					finish();
				}
				break;
			case REQUEST_RESOLUTION :
				if (resultCode == Activity.RESULT_OK) {
					mGetJarManager.connect(new IConnectCallBack() {

						@Override
						public void onConnect(boolean connect, Intent result) {
							// TODO Auto-generated method stub
							if (connect) {
								mGetJarManager.getLicense(mProductId, mLicenseListener);
							} else {
								finish();
							}
						}
					});
				} else {
					finish();
				}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * This method queues up the intents that are received from Getjar through any of {@link Activity#onCreate}, {@link Activity#onNewIntent},
	 * {@link Activity#onActivityResult} or {@link OnGetjarWorkFinishedListener} entry points.
	 */
	private void enqueueGetjarIntent(Intent intent) {
		if (intent.getBooleanExtra(GetjarConstants.INTENT_KEY, false)) {
			GetjarIntentWrapper wrappedIntent = new GetjarIntentWrapper(intent);
			if (!mReceivedIntents.contains(wrappedIntent)) {
				mReceivedIntents.add(wrappedIntent);
			}
		}
		processReceivedIntents();
	}

	/**
	 * This method processes the intents that are received from Getjar through any of {@link Activity#onCreate}, {@link Activity#onNewIntent},
	 * {@link Activity#onActivityResult} or {@link OnGetjarWorkFinishedListener} entry points.
	 */
	private void processReceivedIntents() {
		sExecutorService.execute(new Runnable() {

			@Override
			public void run() {
				synchronized (mProcessIntentsLock) {

					while (!mReceivedIntents.isEmpty()) {
						Intent intent = mReceivedIntents.remove().getIntent();
						String intentType = intent.getStringExtra(GetjarConstants.INTENT_TYPE_KEY);
						if (!TextUtils.isEmpty(intentType)) {
							if (GetjarConstants.INTENT_TYPE_REDEEM_VOUCHER_VALUE.equals(intentType)) {
								/**
								 * Get the Voucher token from the intent and call the redeemVoucher method using that voucher token
								 */
								String voucherToken = intent
										.getStringExtra(GetjarConstants.INTENT_VOUCHER_TOKEN_KEY);
								String devPayload = "A custom string typically used to identify your user and/or transaction";
								mGetJarManager.redeemVoucher(voucherToken, devPayload,
										mRedeemListener);
								Log.d(TAG, String.format(Locale.US,
										"GetjarClient.redeemVoucher() called [%1$s]", voucherToken));
							}
						}
					}
				}
			}
		});
	}
	/**
	 * This listener receiver a callback when a voucher is redeemed successfully or an error occurs while redemption.
	 */
	private OnGetjarVoucherRedeemedListener mRedeemListener = new OnGetjarVoucherRedeemedListener() {

		@Override
		public void onVoucherRedeemed(final int statusCode,
				final GetjarVoucherRedeemedResult voucherRedeemedResult) {
			Log.d(TAG, String.format(Locale.US, "onVoucherRedeemed() statusCode:%1$d", statusCode));
			try {

				if (statusCode == GetjarConstants.STATUS_OK) {
					JSONObject signedPayloadJson = new JSONObject(
							voucherRedeemedResult.getSignedTransactionData());

					//**********************************************
					// TODO: It is highly recommended that you verify the signed data using the signature on your server
					// before granting anything to the user to make sure that this is a legitimate transaction.
					//
					// More information available at: https://developer.getjar.com/android/signature-verification/
					//
					// NOTE: If you detect on your server-side validation call that you have already processed the given
					// voucher token then it is critical that you indicate this back to you client and have the client
					// make the "confirmVoucher()" call seen below. This ensures that the voucher has been "consumed" and
					// will no longer be returned to your app for redemption work.
					//
					//**********************************************

					// Get the relevant voucher token that was redeemed
					String voucherToken = signedPayloadJson
							.getString(GetjarConstants.JSON_VOUCHER_TOKEN_KEY);
					if (mRedeemedVouchers.contains(voucherToken)) {

						//**********************************************
						// NOTE: Tracking of already redeemed voucher token values should be done server-side as part of the validation
						// work recommended above. For purposes of this example, we do some simple client-side tracking here.
						Log.d(TAG, "onVoucherRedeemed() already redeemed.");

					} else {

						String productId = signedPayloadJson.getString("developer_product_id");
						sendPurchaseResult(productId, PURCHSE_RESULT_OK);
						// Confirm the redemption
						mRedeemedVouchers.add(voucherToken);
						mGetJarManager.confirmVoucher(voucherToken);

						//**********************************************
						// NOTE: The "confirmVoucher()" above should also be made if your server validation 
						// indicates that you have already process the voucher token in question (see above).

					}
				} else {
					String msg;
					if (statusCode == GetjarConstants.STATUS_ALREADY_REDEEMED_FAILURE) {
						msg = "Voucher Already Redeemed";
					} else {
						msg = String.format(Locale.US, "Redeem failed (statusCode:%1$d)",
								statusCode);
					}

					Log.d(TAG, String.format(Locale.US, " onVoucherRedeemed() %1$s", msg));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private OnGetjarLicensesReceivedListener mLicenseListener = new OnGetjarLicensesReceivedListener() {

		@Override
		public void onLicensesReceived(int statusCode, final List<GetjarLicense> licenses) {
			if (GetjarConstants.STATUS_OK == statusCode && licenses != null) {
				try {
					for (GetjarLicense license : licenses) {
						Log.e(GetJarManager.TAG, "LicenseListener item id " + license.getItemId());
						if (mProductId.equals(license.getItemId())) {
							Log.d(GetJarManager.TAG, "onLicensesReceived,PURCHSE_RESULT_OK");
							sendPurchaseResult(mProductId, PURCHSE_RESULT_OK);
							finish();
							return;
						}
					}
				} catch (Exception e) {
				}//no-op ok
			}
			Intent intent = mGetJarManager.getPurchaseIntent(mProductId, mProductName,
					mProductInfo, mProductPrice);
			startActivityForResult(intent, REQUEST_PURCHASE);
		};
	};

	private void sendPurchaseResult(String productId, int result) {
		if (mPurchaseListener != null) {
			mPurchaseListener.handlePurchaseResult(result);
		}
		Intent intent = new Intent(PURCHASE_RESULT_ACTION);
		intent.putExtra(PRODUCT_ID, productId);
		intent.putExtra(PURCHSE_RESULT, result);
		sendBroadcast(intent);
		if (result == PURCHSE_RESULT_OK) {
			ContentResolver resolver = getContentResolver();
			ContentValues values = new ContentValues();
			values.put(PurchaseTable.PRODUCT_ID, productId);
			resolver.insert(GoContentProvider.CONTENT_PURCHASE_URI, values);
		}
		finish();
	}
}
