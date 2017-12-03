package com.jiubang.ggheart.analytic;

import java.util.HashMap;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.gostaticsdk.utiltool.Machine;
import com.gau.go.gostaticsdk.utiltool.UtilTool;
import com.gau.go.launcherex.R;
import com.google.android.apps.analytics.AnalyticsReceiver;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.launcher.ICustomAction;

/**
 * 
 * @author guoyiqing
 *
 */
public class ReferrerInfoReceiver extends BroadcastReceiver {

	private static final String AND_SPLIT = "&";

	private static final String EQUAL_SPLIT = "=";

	private static final String PROTOCOL_DIVIDER = "||";

	private static final String TAG = "ReferrerInfoReceiver";

	private static final String GO_GA_ITEMS_PRE = "gokey_";

	private static final String GA_THREAD = "ga_thread";
	
	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (intent != null
				&& ICustomAction.ACTION_INSTALL_REFERRER.equals(intent
						.getAction())) {
			final String referrer = intent.getStringExtra("referrer");
			Log.i(TAG, "Referrer Info: " + referrer);
			if (referrer != null) {
				if (referrer.contains(GO_GA_ITEMS_PRE)) {
					new Thread(GA_THREAD) {

						@Override
						public void run() {
							super.run();
							String upload = makeInfo(context, referrer);
							StatisticsManager.getInstance(context)
									.upLoadStaticData(upload);
						}
					}.start();
					String filteGa = filteGA(referrer);
					intent.putExtra("referrer", filteGa);
					Log.e(TAG, "filterGa:" + filteGa);
				}
				InstallationUtils.needStoreRefInfo(context);
				AnalyticsReceiver receiver = new AnalyticsReceiver();
				Log.i(TAG, "Pass intent to AnalyticsReceiver");
				try {
					receiver.onReceive(context, intent);
				} catch (Exception e) {
					Log.e(TAG, "AnalyticsReceiver Error", e);
				}
			}
		} else {
			Log.i(TAG,
					"Invalid intent: "
							+ (intent == null ? intent : intent.getAction()));
		}
	}

	private String filteGA(String referrer) {
		if (referrer != null) {
			String[] splits = referrer.split(AND_SPLIT);
			if (splits != null && splits.length >= 2) {
				HashMap<String, String> maps = new HashMap<String, String>();
				String[] subSplits = null;
				for (String string : splits) {
					subSplits = string.split(EQUAL_SPLIT);
					if (subSplits != null && subSplits.length >= 2) {
						maps.put(subSplits[0], subSplits[1]);
					}
				}
				Set<String> keys = maps.keySet();
				for (String string : keys) {
					if (string.contains(GO_GA_ITEMS_PRE)) {
						referrer = removeStr(referrer, string + EQUAL_SPLIT
								+ maps.get(string), AND_SPLIT);
					}
				}
			}
		}
		return referrer;
	}

	private String removeStr(String all, String sub, String split) {
		if (all == null || sub == null || split == null) {
			return "";
		}
		int index = all.indexOf(sub);
		if (index != -1) {
			if (index != 0) {
				sub = split + sub;
				index = all.indexOf(sub);
				if (index != -1) {
					return all.substring(0, index)
							+ all.substring(index + sub.length());
				}
			} else {
				return all.substring(0, index)
						+ all.substring(index + sub.length() + split.length());
			}
		}
		return all;
	}

	private String makeInfo(Context context, String ga) {
		StringBuilder sb = new StringBuilder(20);
		sb.append("45").append(PROTOCOL_DIVIDER);
		sb.append(Machine.getAndroidId(context)).append(PROTOCOL_DIVIDER);
		sb.append(UtilTool.getBeiJinTime()).append(PROTOCOL_DIVIDER);
		sb.append("96").append(PROTOCOL_DIVIDER);
		sb.append(ga).append(PROTOCOL_DIVIDER);
		sb.append("k001").append(PROTOCOL_DIVIDER);
		sb.append("1").append(PROTOCOL_DIVIDER);
		sb.append(Machine.getSimCountryIso(context)).append(PROTOCOL_DIVIDER);
		sb.append(Statistics.getUid(context)).append(PROTOCOL_DIVIDER);
		sb.append(Statistics.buildVersionCode(context))
				.append(PROTOCOL_DIVIDER);
		sb.append(context.getString(R.string.curVersion)).append(
				PROTOCOL_DIVIDER);
		sb.append("").append(PROTOCOL_DIVIDER);
		sb.append("").append(PROTOCOL_DIVIDER);
		sb.append("").append(PROTOCOL_DIVIDER);
		sb.append(GoStorePhoneStateUtil.getVirtualIMEI(context)).append(
				PROTOCOL_DIVIDER);
		sb.append(StatisticsManager.getGOID(context)).append(PROTOCOL_DIVIDER);
		sb.append("").append(PROTOCOL_DIVIDER);
		sb.append("");
		return sb.toString();
	}

}
