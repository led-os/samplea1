package com.jiubang.ggheart.plugin.migrate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.jiubang.ggheart.apps.desks.diy.IRequestCodeIds;
import com.jiubang.ggheart.components.ISelfObject;
import com.jiubang.ggheart.launcher.ICustomAction;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 */
public class MigrateControler implements ISelfObject {
	private Context mContext;
	private MigrateReceiver mReceiver;

	public MigrateControler(Context context) {
		mContext = context;
		selfConstruct();
	}

	@Override
	public void selfConstruct() {
		register();
	}

	@Override
	public void selfDestruct() {
		unregister();
		mContext = null;
	}

	private void register() {
		if (null == mReceiver) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(ICustomAction.ACTION_DESK_MIGRATE_PREPARED);

			mReceiver = new MigrateReceiver();

			try {
				mContext.registerReceiver(mReceiver, filter);
			} catch (Exception e) {
				// 注册异常
				e.printStackTrace();
			}
		}
	}

	private void unregister() {
		if (null != mReceiver) {
			try {
				mContext.unregisterReceiver(mReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
			mReceiver = null;
		}
	}

	/**
	 * 
	 * <br>类描述:
	 * <br>功能详细描述:
	 */
	class MigrateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (null != intent) {
				Bundle bundle = intent.getExtras();
				if (null != bundle) {
					int code = bundle.getInt("code");
					if (1000 == code || IRequestCodeIds.REQUEST_MIGRATE_DESK == code) {
						String uriStr = bundle.getString("uri");
						boolean clearflag = IRequestCodeIds.REQUEST_MIGRATE_DESK == code;
						if (null != uriStr) {
							Intent myIntent = new Intent(context, MigrateActivity.class);
							myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							myIntent.putExtra("uri", uriStr);
							myIntent.putExtra("clearflag", clearflag);
							context.startActivity(myIntent);
						}
					}
				}
			}
		}
	}
}
