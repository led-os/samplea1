package com.jiubang.ggheart.apps.desks.diy.pref;

import java.util.List;

import android.content.Context;

/**
 * 
 * @author guoyiqing
 * 
 */
public class PrefMigrateManager {

	private static final String UPGRADE_SP_THREAD = "upgrade_sp_thread";
	private static PrefMigrateManager sInstance;

	public static synchronized PrefMigrateManager getManager() {
		if (sInstance == null) {
			sInstance = new PrefMigrateManager();
		}
		return sInstance;
	}
	
	public void upgradeAsync(final Context context) {
		if (PrivatePreference.getPreference(context).isUpgraded()) {
			return;
		}
		new Thread(UPGRADE_SP_THREAD) {
			public void run() {
				OnPrefUpgradListener listener = PrivatePreference
						.getPreference(context);
				PrefMigrater migrater = new PrefMigrater(
						PrefKeyMap.SP_BACKUP_PRIVATE);
				migrater.migrate(PrefKeyMap.BACKUP_SP_NAME_MIGRATE_LIST,
						context);

				migrater = new PrefMigrater(PrefKeyMap.SP_UNBACKUP_PRIVATE);
				migrater.migrate(PrefKeyMap.UNBACKUP_SP_NAME_MIGRATE_LIST,
						context);
				if (listener != null) {
					listener.onUpgradeFinish(PrefMigrateManager.this);
				}
			};
		}.start();
	}

	public void upgrade(List<String> prefs, String dst, Context context,
			OnPrefUpgradListener listener) {
		PrefMigrater migrater = new PrefMigrater(dst);
		migrater.migrate(prefs, context);
		if (listener != null) {
			listener.onUpgradeFinish(this);
		}
	}

}
