package com.jiubang.ggheart.admob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.go.util.device.Machine;

/**
 * 
 * @author guoyiqing
 * 
 */
public class AdViewInfoReceiver extends BroadcastReceiver {

	public static final String UPLOAD_ADINFO_ACTION = "com.jiubang.ggheart.admob.upload.action";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(UPLOAD_ADINFO_ACTION)) {
			final Context context2 = context;
			if (!Machine.isNetworkOK(context)) {
				return;
			}
			Log.e("guoyiqing", "onReceive");
			new Thread("upload_adstatistics_thread") {
				@Override
				public void run() {
					super.run();
					uploadAdFile(context2);
				}
			}.start();
		}
	}

	private void uploadAdFile(Context context) {
		synchronized (AdViewInfoReceiver.class) {
			BufferedReader reader = null;
			try {
				File file = new File(context.getFilesDir() + "/"
						+ AdViewStatistics.ADVIEW_STATISTICS_FILE_NAME);
				if (!file.exists()) {
					return;
				}
				reader = new BufferedReader(
						new InputStreamReader(
								context.openFileInput(AdViewStatistics.ADVIEW_STATISTICS_FILE_NAME)));
				StringBuilder builder = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (!line.equals("")) {
						builder.append(line + AdViewStatistics.ITEM_DIVIDER);
					}
				}
				line = builder.toString().trim();
				if (!line.equals("")) {
					StatisticsManager.getInstance(context).upLoadStaticData(line);
					file.delete();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
