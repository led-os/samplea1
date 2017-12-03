/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jiubang.ggheart.billing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jiubang.ggheart.launcher.ICustomAction;

/**
 * 接收付费请求的广播
 * @author zhoujun
 *
 */
public class AppInBillingRequestReceiver extends BroadcastReceiver {
//	public static final String THEME_PARCHASE_ACTION = "go.launcherex.purchase.state.REQUEST";
	//	public static final String THEME_SUPPORTED_ACTION = "go.launcherex.purchase.supported.REQUEST";
	public static final String EXTRA_FOR_ITEMID = "itemId";
	public static final String EXTRA_FOR_PACKAGENAME = "packageName";
	public static final String EXTRA_FOR_PRODUCTUNMANAGED = "productUnmanaged";
	public static final String EXTRA_FOR_THEMETYPE = "themeType";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null) {
			String action = intent.getAction();
//			AppInBillingManager billingManager = AppInBillingManager.getInstance();
			if (ICustomAction.ACTION_THEME_PARCHASE.equals(action)) {
				String itemId = intent.getStringExtra(EXTRA_FOR_ITEMID);
				String packageName = intent.getStringExtra(EXTRA_FOR_PACKAGENAME);
				Intent startIntent = new Intent();
				startIntent.putExtra(EXTRA_FOR_ITEMID, itemId);
				startIntent.putExtra(EXTRA_FOR_PACKAGENAME, packageName);
				startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startIntent.setClass(context, AppInBillingActivity.class);
				context.startActivity(startIntent);
				
			} else if (ICustomAction.ACTION_THEME_STOP_SERVICE.equals(action)) {
				//关闭内购的付费服务,减少占用内存
//				if (billingManager != null) {
//					billingManager.destory();
//				}
			} 
		}

	}

}
