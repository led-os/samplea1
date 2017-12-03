package com.jiubang.ggheart.apps.desks.diy;

import java.io.File;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.proxy.DataChangeListenerProxy;
import com.jiubang.ggheart.apps.desks.Preferences.BackupRestoreUtil;
import com.jiubang.ggheart.apps.desks.backup.AutoBackUpTool;
import com.jiubang.ggheart.apps.desks.backup.ExportDatabaseTask;
import com.jiubang.ggheart.components.DeskActivity;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.data.DataProvider;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 
 * 类描述: 自動備份選擇框:
 * 
 * @date [2012-10-12]
 */
public class RestoryBackupDialogActivity extends DeskActivity implements
		OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		GuiThemeStatistics.pluginStaticData(GuiThemeStatistics.OPTION_CODE_PLUGIN_AUTOBACKUP_POPUP);
	}

	public void initView() {
		setContentView(R.layout.desk_restorebackup_confirm_dialog);
		Button cancle_Button = (Button) findViewById(R.id.cancle_restore);
		cancle_Button.setOnClickListener(this);
		Button restore_Button = (Button) findViewById(R.id.restore);
		restore_Button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancle_restore:
			GuiThemeStatistics.pluginStaticData(GuiThemeStatistics.OPTION_CODE_PLUGIN_AUTOBACKUP_CANCLE);
			finish();
			break;

		case R.id.restore:
			ImportDatabaseTask task = new ImportDatabaseTask();
			GuiThemeStatistics.pluginStaticData(GuiThemeStatistics.OPTION_CODE_PLUGIN_AUTOBACKUP_RESTORE);
			task.execute();
			break;

		default:
			break;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initView();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		GuiThemeStatistics.pluginStaticData(GuiThemeStatistics.OPTION_CODE_PLUGIN_AUTOBACKUP_CANCLE);
	}
	
/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2013-6-20]
 */
	public class ImportDatabaseTask extends AsyncTask<Void, Void, String> {

		private ProgressDialog mDialog = null;
		private boolean mIsRestart = false;

		@Override
		protected void onPreExecute() {
			this.mDialog = new ProgressDialog(RestoryBackupDialogActivity.this);
			this.mDialog.setMessage(getResources().getString(
					R.string.dbfile_import_dialog));
			this.mDialog.show();
			DataProvider.getInstance(RestoryBackupDialogActivity.this).close();

		}

		@Override
		protected String doInBackground(final Void... args) {

			synchronized (DataProvider.DB_LOCK) {

				String path = LauncherEnv.Path.DB_AUTO_BACKUP_PATH;
				String guidePreferencesName = IPreferencesIds.USERTUTORIALCONFIG
						+ ".xml";
				BackupRestoreUtil.importSharedPreferences(
						RestoryBackupDialogActivity.this, path + "/preference",
						guidePreferencesName);
				AutoBackUpTool.restoreDiygesture();

				File dbBackupFile = null;
				dbBackupFile = new File(path + "/db" + "/androidheart.db");

				if (dbBackupFile == null || !dbBackupFile.exists()) {
					return getResources().getString(R.string.dbfile_not_found);
				} else if (!dbBackupFile.canRead()) {
					return getResources().getString(
							R.string.dbfile_not_readable);
				}

				AutoBackUpTool.deleteDbFile(Environment.getDataDirectory()
						+ "/data/" + getPackageName()
						+ "/databases/androidheart.db");
				AutoBackUpTool.deleteDbWalFiles(path + "/db"
						+ "/androidheart.db");
				try {
					String srcFolderPath = LauncherEnv.Path.DB_AUTO_BACKUP_PATH
							+ "/db";
					String destFolderpath = Environment.getDataDirectory()
							+ "/data/" + getPackageName() + "/databases";
					ExportDatabaseTask.copyFolder(srcFolderPath,
							destFolderpath, false,
							ExportDatabaseTask.ENCRYPTBYTE);
					//check new db interity add by zhangxi @2013.07.19
					if (DataProvider.getInstance(RestoryBackupDialogActivity.this).checkRecDB()) {
						DataProvider.getInstance(RestoryBackupDialogActivity.this)
						.checkBackDB();
					} else {
						//delete original db file
						AutoBackUpTool.deleteDbFile(Environment.getDataDirectory()
								+ "/data/" + getPackageName()
								+ "/databases/androidheart.db");
						AutoBackUpTool.deleteDbWalFiles(path + "/db"
								+ "/androidheart.db");
						
						//set PREFERENCE_SETTING_CFG_ITEM_AUTOBACKUP_RESTOREDEFAULT true
						PreferencesManager sharedPreferences = new PreferencesManager(RestoryBackupDialogActivity.this,
								IPreferencesIds.PREFERENCE_SETTING_CFG, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
						sharedPreferences.putBoolean(
								IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_AUTOBACKUP_RESTOREDEFAULT, true);
						sharedPreferences.commit();
					}
					mIsRestart = true;
					return null;
				} catch (IOException e) {
					return getResources().getString(
							R.string.dbfile_import_error);
				}

			}
		}

		@Override
		protected void onPostExecute(final String msg) {
			if (this.mDialog.isShowing()) {
				try {
					this.mDialog.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (msg != null && msg.length() > 0) {
				DeskToast.makeText(RestoryBackupDialogActivity.this, msg,
						Toast.LENGTH_SHORT).show();
			}
			if (mIsRestart) {
				exitAndRestart();
			}

		}

		/**
		 * <br>
		 * 功能简述:退出重新启动桌面 <br>
		 * 功能详细描述: <br>
		 * 注意:
		 */
		private void exitAndRestart() {
			Intent i = getIntent();
			Bundle b = new Bundle();
			b.putInt("exit", 1);
			i.putExtras(b);
			setResult(RESULT_OK, i);
			finish();
			DataChangeListenerProxy.getInstance().exit(true);
		}
	}
}
