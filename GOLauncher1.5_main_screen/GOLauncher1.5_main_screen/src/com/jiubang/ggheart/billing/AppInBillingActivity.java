package com.jiubang.ggheart.billing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.billing.base.Consts;
import com.jiubang.ggheart.billing.base.IabHelper;
import com.jiubang.ggheart.billing.base.IabResult;
import com.jiubang.ggheart.billing.base.Inventory;
import com.jiubang.ggheart.billing.base.Purchase;
import com.jiubang.ggheart.data.theme.zip.ZipResources;
import com.jiubang.ggheart.launcher.ICustomAction;
/**
 * 内购3版应用内付费处理类
 * @author liulixia
 *
 */
//CHECKSTYLE:OFF
public class AppInBillingActivity extends Activity {
	/**
	 * 应用内付费
	 */
	private IabHelper mHelper;
	static final int RC_REQUEST = 10001;
	/**
	 * 保存付费状态的类
	 */
	private PurchaseStateManager mStateManager;
	
	private String mItemId = null;
	private String mPackageName = null;
	private boolean mUnManagedProduct = false;
	private String mThemeType = null;
	public static final String LICENSEKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg6IJqCRoGEysdDcCZbNIpxH5wauWry0/03jyBdroWWUGrOtpsKHoFmR0asnb9kOskFbmi/MF7bTkCFPKgumOL/R5s4K2kP/hRVI0XQefRE56+dk3cIZAS/GTHoiepaitYDaZld2zC0WyI90QA0nJfas0K7s+oLAF4Hj3+/Mqy6li8V24+2Qdlmy0TbPuXnMwKgKMQHH3gh3E2XnEp9/OwuyFfEYAFheRA22mbBBhqwN/rDHDiD3wZZIU/w+U84prlypcaxSFRSfZ0m/cgD0GHHkGXv0Ey9TYM5YZcqOQeGNKrMBi+XK+xfSeAfOnIp2XuaQOD+mQmygAeTU3YErrGQIDAQAB";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_inbilling_layout);
		Intent intent = getIntent();
		mItemId = intent.getStringExtra(AppInBillingRequestReceiver.EXTRA_FOR_ITEMID);
		mPackageName = intent.getStringExtra(AppInBillingRequestReceiver.EXTRA_FOR_PACKAGENAME);
		mUnManagedProduct = intent.getBooleanExtra(AppInBillingRequestReceiver.EXTRA_FOR_PRODUCTUNMANAGED, false);
		mThemeType = intent.getStringExtra(AppInBillingRequestReceiver.EXTRA_FOR_THEMETYPE);
		if (mItemId == null || mItemId.equals("") || mPackageName == null || mPackageName.equals("")) {
			finish();
			return;
		}
		mStateManager = new PurchaseStateManager(this);
		//TODO 设置google密钥
		mHelper = new IabHelper(this, LICENSEKEY);
		mHelper.enableDebugLogging(true);
		Log.d(Consts.DEBUG_TAG, "Starting setup.");
		if (!mUnManagedProduct) {
			//托管产品走的付费流程
			goToAppInBilling();
		} else {
			//非托管产品走的付费流程
			goToAppInBillingUnmanagedProducts();
		}
	}
	
	private void goToAppInBilling() {
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
            	if(Consts.DEBUG) {
            		Log.d(Consts.DEBUG_TAG, "Setup finished.");
            	}
                if (!result.isSuccess()) {
                	PurchaseSupportedManager.saveSupported(AppInBillingActivity.this, false);
                    Toast.makeText(AppInBillingActivity.this, "Problem setting up in-app billing: " + result, 1000).show();
                    finish();
                    return;
                }
                
                PurchaseSupportedManager.saveSupported(AppInBillingActivity.this, true);
                // Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
                if(Consts.DEBUG) {
                	Log.d(Consts.DEBUG_TAG, "Setup successful. Querying inventory.");
                }
                if (mHelper != null) {  //测试主题时会报这种错误，所以在此处也做了判空
                	mHelper.queryInventoryAsync(false, mGotInventoryListener);
                }
            }
        });
	}

	private void goToAppInBillingUnmanagedProducts() {
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
            	if(Consts.DEBUG) {
            		Log.d(Consts.DEBUG_TAG, "Setup finished.");
            	}
                if (!result.isSuccess()) {
                	PurchaseSupportedManager.saveSupported(AppInBillingActivity.this, false);
                    Toast.makeText(AppInBillingActivity.this, "Problem setting up in-app billing: " + result, 1000).show();
                    finish();
                    return;
                }
                
                PurchaseSupportedManager.saveSupported(AppInBillingActivity.this, true);
                // Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
                if(Consts.DEBUG) {
                	Log.d(Consts.DEBUG_TAG, "Setup successful. Querying inventory.");
                }
                if (mHelper != null) {  //测试主题时会报这种错误，所以在此处也做了判空
                	mHelper.queryInventoryAsync(false, mUnmanagedGotInventoryListener);
//                	try {
//                		mHelper.launchPurchaseFlow(AppInBillingActivity.this, mItemId, RC_REQUEST, mUnmanagedPurchaseFinishedListener);
//                	} catch (Exception e) {
//                		e.printStackTrace();
//                	}
                }
            }
        });
	}
	
	 // Listener that's called when we finish querying the items we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        	if(Consts.DEBUG) {
        		Log.d(Consts.DEBUG_TAG, "Query inventory finished.");
        	}
        	if (inventory == null || mHelper == null) {
        		Log.d(Consts.DEBUG_TAG, ">>>inventory = null || mHelper = null");
        		finish();
        		return;
        	}
            if (result.isFailure()) {
            	if (result.getResponse() != IabHelper.IABHELPER_SERVICE_DISCONNECTED) {
            		Toast.makeText(AppInBillingActivity.this, "Failed to query inventory: " + result, 600).show();
            	}
                finish();
                return;
            }

            if(Consts.DEBUG) {
            	Log.d(Consts.DEBUG_TAG, "Query inventory was successful.");
            }

            if (inventory.hasPurchase(mItemId)) {
            	if(Consts.DEBUG) {
            		Log.d(Consts.DEBUG_TAG, "We have "+ mItemId + ".");
            	}
            	sendPurchaseResponse(ThemeAppInBillingManager.PURCHASE_STATE_PURCHASED);
            	return;
            } else {
            	mHelper.launchPurchaseFlow(AppInBillingActivity.this, mItemId, RC_REQUEST, mPurchaseFinishedListener);
            }
            if(Consts.DEBUG) {
            	Log.d(Consts.DEBUG_TAG, "Initial inventory query finished; enabling main UI.");
            }
        }
    };
    
    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        	if(Consts.DEBUG) {
        		Log.d(Consts.DEBUG_TAG, "Purchase finished: " + result + ", purchase: " + purchase);
        	}
            if (result.isFailure()) {
            	if(Consts.DEBUG) {
            		Log.d(Consts.DEBUG_TAG, "Error purchasing: " + result);
            	}
                // Oh noes!
            	if (result.getResponse() != IabHelper.IABHELPER_USER_CANCELLED 
            			&& !result.getMessage().startsWith("Null data in IAB result")) {
            		Toast.makeText(AppInBillingActivity.this, "Error purchasing: " + result, 1000).show();
            	}
            	sendPurchaseResponse(ThemeAppInBillingManager.PURCHASE_STATE_CANCELED);
                return;
            }

            if(Consts.DEBUG) {
            	Log.d(Consts.DEBUG_TAG, "Purchase successful.");
            }

            if (purchase.getSku().equals(mItemId)) {
            	//TODO 付费成功
            	sendPurchaseResponse(ThemeAppInBillingManager.PURCHASE_STATE_PURCHASED);
            }
        }
    };	
    
    IabHelper.QueryInventoryFinishedListener mUnmanagedGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        	if(Consts.DEBUG) {
        		Log.d(Consts.DEBUG_TAG, "Unmanaged Query inventory finished.");
        	}
        	if (inventory == null || mHelper == null) {
        		if (inventory == null) {
        			Log.d(Consts.DEBUG_TAG, ">>>Unmanaged inventory = null");
        		}
        		if (mHelper == null) {
        			Log.d(Consts.DEBUG_TAG, ">>>Unmanaged mHelper = null");
        		}
        		finish();
        		return;
        	}
            if (result.isFailure()) {
            	if (result.getResponse() != IabHelper.IABHELPER_SERVICE_DISCONNECTED) {
            		Toast.makeText(AppInBillingActivity.this, "Failed to query inventory: " + result, 600).show();
            	}
                finish();
                return;
            }

            if(Consts.DEBUG) {
            	Log.d(Consts.DEBUG_TAG, "Unmanaged Query inventory was successful.");
            }

            if (inventory.hasPurchase(mItemId)) {
            	if(Consts.DEBUG) {
            		Log.d(Consts.DEBUG_TAG, "Unmanaged We have "+ mItemId + ".");
            	}
            	mHelper.consumeAsync(inventory.getPurchase(mItemId), mConsumeListener);
            } else {
            	mHelper.launchPurchaseFlow(AppInBillingActivity.this, mItemId, RC_REQUEST, mUnmanagedPurchaseFinishedListener);
            }
            if(Consts.DEBUG) {
            	Log.d(Consts.DEBUG_TAG, "Initial inventory query finished; enabling main UI.");
            }
        }
    };
    
    IabHelper.OnIabPurchaseFinishedListener mUnmanagedPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        	if(Consts.DEBUG) {
        		Log.d(Consts.DEBUG_TAG, "Unmanaged Purchase finished: " + result + ", purchase: " + purchase);
        	}
            if (result.isFailure()) {
            	if(Consts.DEBUG) {
            		Log.d(Consts.DEBUG_TAG, "Unmanaged Error purchasing: " + result);
            	}
                // Oh noes!
            	if (result.getResponse() != IabHelper.IABHELPER_USER_CANCELLED 
            			&& !result.getMessage().startsWith("Null data in IAB result")) {
            		Toast.makeText(AppInBillingActivity.this, "Error purchasing: " + result, 1000).show();
            	}
            	sendPurchaseResponse(ThemeAppInBillingManager.PURCHASE_STATE_CANCELED);
                return;
            }

            if(Consts.DEBUG) {
            	Log.d(Consts.DEBUG_TAG, "Purchase successful.");
            }

            if (purchase.getSku().equals(mItemId)) {
            	//付费成功后直接消息
            	mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            	sendPurchaseResponse(ThemeAppInBillingManager.PURCHASE_STATE_PURCHASED);
            }
        }
    };	

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
        	 if(Consts.DEBUG) {
             	Log.d(Consts.DEBUG_TAG, "First Consumption finished. Purchase: " + purchase + ", result: " + result);
             }
            if (result.isSuccess()) {
            	 if(Consts.DEBUG) {
                 	Log.d(Consts.DEBUG_TAG, "First Consumption successful. Provisioning.");
                 }
            	 try {
            		 mHelper.launchPurchaseFlow(AppInBillingActivity.this, mItemId, RC_REQUEST, mUnmanagedPurchaseFinishedListener);
            	 } catch (Exception e) {
            		 e.printStackTrace();
            	 }
            } else {
            	Toast.makeText(AppInBillingActivity.this, "Purchase failed", 1000).show();
            	AppInBillingActivity.this.finish();
            }
        }
    };
    
    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
        	 if(Consts.DEBUG) {
             	Log.d(Consts.DEBUG_TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
             }
            if (result.isSuccess()) {
            	 if(Consts.DEBUG) {
                 	Log.d(Consts.DEBUG_TAG, "Consumption successful. Provisioning.");
                 }
            }
            //TODO 付费成功
//         	sendPurchaseResponse(ThemeAppInBillingManager.PURCHASE_STATE_PURCHASED);
        }
    };
    
    private void sendPurchaseResponse(int state) {
		if (Consts.DEBUG) {
			Log.e(Consts.TAG, "AppInBillingActivity  sendPurchaseResponse itemId:" + mItemId + ",state:" + state);
		}
		if (state == ThemeAppInBillingManager.PURCHASE_STATE_PURCHASED) {
			if (mThemeType == null || !mThemeType.equalsIgnoreCase("getjar")) {
				//getjar主题不在桌面保存付费信息
				savePurchaseState(mPackageName);
			}
		}
		Intent intent = new Intent();
		intent.setAction(ICustomAction.ACTION_PURCHASE_STATE_RESULT);
		intent.putExtra(ThemeAppInBillingManager.EXTRA_FOR_ITEMID, mItemId);
		intent.putExtra(ThemeAppInBillingManager.EXTRA_FOR_STATE, state);
		intent.putExtra(ThemeAppInBillingManager.EXTRA_FOR_PACKAGENAME, mPackageName);
		sendBroadcast(intent);
		finish();
	}
    
    private void savePurchaseState(String packageName) {
		if (mStateManager != null) {
			mStateManager.save(packageName, packageName + ZipResources.ZIP_POSTFIX);
		}
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Consts.DEBUG) {
			Log.d(Consts.DEBUG_TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data + ")");
		}
        if (requestCode == RC_REQUEST) {
        	if (mHelper != null && !mHelper.handleActivityResult(requestCode, resultCode, data)) {
        		super.onActivityResult(requestCode, resultCode, data);
        	}
        	else {
        		if(Consts.DEBUG) {
        			Log.d(Consts.DEBUG_TAG, "onActivityResult handled by IABUtil.");
        		}
        	}
        }
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mHelper != null) {
			mHelper.dispose();
		}
        mHelper = null;
	}

}
