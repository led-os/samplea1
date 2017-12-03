package com.jiubang.ggheart.data;

import java.io.File;
import java.util.ArrayList;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import com.gau.go.launcherex.R;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.commomidentify.IOpenAppAnimationType;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.util.AppUtils;
import com.go.util.ConvertUtils;
import com.go.util.androidsys.ClearDefaultIntent;
import com.go.util.device.ConfigurationInfo;
import com.go.util.file.FileUtil;
import com.go.util.graphics.effector.united.EffectorDataModel;
import com.go.util.graphics.effector.united.IEffectorIds;
import com.go.util.log.Loger;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.admob.AdTable;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.frames.dock.DefaultStyle.DockLogicControler;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.ScreenIndicator;
import com.jiubang.ggheart.apps.desks.diy.pref.PrefConst;
import com.jiubang.ggheart.apps.desks.diy.pref.PrivatePreference;
import com.jiubang.ggheart.apps.desks.dock.DockUtil;
import com.jiubang.ggheart.apps.desks.imagepreview.ImagePreviewResultType;
import com.jiubang.ggheart.apps.security.modle.AllEngineCheckResultsTable;
import com.jiubang.ggheart.apps.security.modle.CheckResultTable;
import com.jiubang.ggheart.components.diygesture.model.DiyGestureTable;
import com.jiubang.ggheart.data.SqliteNative.SqlitedbForCK;
import com.jiubang.ggheart.data.info.AppSettingDefault;
import com.jiubang.ggheart.data.info.GestureSettingInfo;
import com.jiubang.ggheart.data.info.GoWidgetBaseInfo;
import com.jiubang.ggheart.data.info.IItemType;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.data.tables.AppExtraAttributeTable;
import com.jiubang.ggheart.data.tables.AppFuncSettingTable;
import com.jiubang.ggheart.data.tables.AppHideListTable;
import com.jiubang.ggheart.data.tables.AppSettingTable;
import com.jiubang.ggheart.data.tables.AppTable;
import com.jiubang.ggheart.data.tables.AppWhiteListTable;
import com.jiubang.ggheart.data.tables.ConfigTable;
import com.jiubang.ggheart.data.tables.DeskLockable;
import com.jiubang.ggheart.data.tables.DeskMenuTable;
import com.jiubang.ggheart.data.tables.DesktopTable;
import com.jiubang.ggheart.data.tables.DynamicEffectTable;
import com.jiubang.ggheart.data.tables.EffectorTable;
import com.jiubang.ggheart.data.tables.FolderTable;
import com.jiubang.ggheart.data.tables.FontTable;
import com.jiubang.ggheart.data.tables.GestureTable;
import com.jiubang.ggheart.data.tables.GoWidgetTable;
import com.jiubang.ggheart.data.tables.GravityTable;
import com.jiubang.ggheart.data.tables.IconControllTable;
import com.jiubang.ggheart.data.tables.LockInfoTable;
import com.jiubang.ggheart.data.tables.MediaManagementHideTable;
import com.jiubang.ggheart.data.tables.MediaManagementPlayListFileTable;
import com.jiubang.ggheart.data.tables.MediaManagementPlayListTable;
import com.jiubang.ggheart.data.tables.MessageCenterTable;
import com.jiubang.ggheart.data.tables.NoPromptUpdateAppTable;
import com.jiubang.ggheart.data.tables.NotificationAppSettingTable;
import com.jiubang.ggheart.data.tables.PartToFolderTable;
import com.jiubang.ggheart.data.tables.PartToScreenTable;
import com.jiubang.ggheart.data.tables.PartsTable;
import com.jiubang.ggheart.data.tables.PurchaseTable;
import com.jiubang.ggheart.data.tables.RecentAppTable;
import com.jiubang.ggheart.data.tables.ScreenSettingTable;
import com.jiubang.ggheart.data.tables.ScreenStyleConfigTable;
import com.jiubang.ggheart.data.tables.ScreenTable;
import com.jiubang.ggheart.data.tables.SettingTable;
import com.jiubang.ggheart.data.tables.ShortcutSettingTable;
import com.jiubang.ggheart.data.tables.ShortcutTable;
import com.jiubang.ggheart.data.tables.ShortcutUnfitTable;
import com.jiubang.ggheart.data.tables.SideToolsTable;
import com.jiubang.ggheart.data.tables.SideWidgetTable;
import com.jiubang.ggheart.data.tables.SmartCardFolderStateTable;
import com.jiubang.ggheart.data.tables.SpecialApplicationTable;
import com.jiubang.ggheart.data.tables.StatisticsTable;
import com.jiubang.ggheart.data.tables.SysShortcutTable;
import com.jiubang.ggheart.data.tables.ThemeTable;
import com.jiubang.ggheart.data.tables.UsedFontTable;
import com.jiubang.ggheart.data.tables.VideoCenterInfoTable;
import com.jiubang.ggheart.data.tables.ZeroScreenAdSuggestSiteTable;
import com.jiubang.ggheart.data.tables.ZeroScreenAdTable;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.iconconfig.AppIconConfigInfoTable;
import com.jiubang.ggheart.launcher.AppIdentifier;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.plugin.mediamanagement.MediaManagementOpenChooser;
import com.jiubang.ggheart.switchwidget.SwitchTable;
import com.jiubang.ggheart.themeicon.ThemeIconInfoTable;

/**
 * 数据库具体操作
 * 
 * @author HuYong
 * @version 1.0
 */
//CHECKSTYLE:OFF
public class DatabaseHelper extends SQLiteOpenHelper {
	
	private final static int DB_VERSION_ONE = 1;

	private final static int DB_VERSION_MAX = 109;
	private final static String DATABASE_NAME = "androidheart.db";

	private final static String TYPE_NUMERIC = "numeric";
	private final static String TYPE_TEXT = "text";

	public final static String EXCEPTION_KEY ="Watch Init DatabaseHelper has exception in process = ";
	// just for test
	private boolean mIsNewDB = false;

	// 可以执行多表关联查询
	SQLiteQueryBuilder msqlQB = null;

	private final Context mContext;

	private boolean mUpdateResult = true; // 更新数据库结果，默认是成功的。
	private static DatabaseHelper sDatabaseHelper;
	
	public static DatabaseHelper getInstance(Context context) {
		if (sDatabaseHelper == null) {
			int curDBVersion = DatabaseHelper.getDB_CUR_VERSION();
			String dBName = DatabaseHelper.getDBName();
			sDatabaseHelper = new DatabaseHelper(context, dBName, curDBVersion);
		}
		return sDatabaseHelper;
	}
	
	/**
	 * version2.16加入标记位： 标记当前一次启动程序是刚刚升级完数据库，因为有些操作具体执行需要区分是不是第一次升级,默认-1
	 */
	// public static int sIsLastUpdateThisRun = -1;

	public DatabaseHelper(Context context, String dataBaseName, int dataBaseVersion) {
		super(context, dataBaseName, null, dataBaseVersion);
		mContext = context;
		// just for test
		msqlQB = new SQLiteQueryBuilder();
		SQLiteDatabase db = null;
		
		try {
			//首先尝试用writeable模式来获取db，若取不到，则再用readable模式来取，若仍未取到，则沿用原有逻辑。
			try {
				db = getWritableDatabase();
			} catch (Exception e) {
				db = getReadableDatabase();
			}
			
			if (!mUpdateResult) {
				// 更新失败，则删除数据库，再行创建。
				if (db != null) {
					db.close();
				}
				mContext.deleteDatabase(DATABASE_NAME);
				getWritableDatabase();
			}

			//成功拿到DB，则更新DB异常信息为0.
			updateDBExceptionPrefer(context, 0);

		} catch (IllegalStateException ie) {
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
					ICommonMsgId.RESTART_GOLAUNCHER, -1, null, null);
		} catch (Exception ex) {
			
			//NOTE：just for test
			StringBuilder builder = new StringBuilder();
			Exception exc = new RuntimeException("just for test autopostlog", ex);
			builder.append("process name:")
			.append(AppUtils.getCurProcessName(mContext))
			.append(",process uid:")
			.append(Process.myUid())
			.append(",db info:")
			.append(collectDBInfo(db))
			.append(",call stack:")
			.append(Log.getStackTraceString(exc));
			GoAppUtils.postLogInfo("AutoPostLog-db", builder.toString());
			
			int count = getDBExceptionPrefer(context);
			if (count > 1) {
				//第3次抛出异常，首先清除默认桌面，然后删除本身db文件
				ClearDefaultIntent.clearCurrentPkgDefault(context);
				if (db != null) {
					db.close();
				}
				mContext.deleteDatabase(DATABASE_NAME);
				updateDBExceptionPrefer(context, 0);
			} else if (count > 0) {
				++count;
				updateDBExceptionPrefer(context, count);
				//第2次抛出异常，则等待500ms
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else {
				//第1次抛出异常
				++count;
				updateDBExceptionPrefer(context, count);

				//				ErrorReporter.reportNeedDB(mContext);
				StringBuffer sb = new StringBuffer();
				sb.append(collectDBInfo(db));
				sb.append(EXCEPTION_KEY
						+ AppUtils.getCurProcessName(mContext));
				sb.append(Loger.sBuffer);
				String detailMessage = sb.toString();
				throw new RuntimeException(detailMessage, ex);
			}
		} finally {
			Loger.stopWriteLog();
		}
	}
	
	/**
	 * 收集一些db文件相关信息
	 */
	private String collectDBInfo(SQLiteDatabase db) {
		StringBuffer sb = new StringBuffer();
		sb.append("some critical info of db\n");
		int uid = AppUtils.getUidByPkgName(mContext, mContext.getPackageName());
		sb.append("GOLauncher uid=" + uid);
		try {
			sb.append("sqlite.version=");
			String sqliteVer = new SqlitedbForCK().getDBVersion();
			sb.append(sqliteVer + "\n");
		} catch (Throwable e) {
			e.printStackTrace();
			sb.append("there is some excepiton in get sqlite version" + Log.getStackTraceString(e) + "\n");
		}
		try {
			sb.append("mContext.getDatabasePath begin\n");
			File dbFile = mContext.getDatabasePath(DATABASE_NAME);
			if (dbFile == null) {
				sb.append("dbFile == null\n");
			} else if (dbFile.exists()) {
				sb.append("databaseFile.exe=" + dbFile.canExecute() + ", read=" + dbFile.canRead() + ", write=" + dbFile.canWrite()
						+ ", size=" + dbFile.length() + "\n");
			} else {
				sb.append("dbFile is not exist\n");
			}
			
			String dataPath = Environment.getDataDirectory() + "/data/" + mContext.getPackageName();
			File dataFile = new File(dataPath);
			File[] files = dataFile.listFiles();
			if (files == null) {
				sb.append("files = null");
			} else {
				for (File file : files) {
					if (file.isDirectory()) {
						File[] sonFiles = file.listFiles();
						for (File file2 : sonFiles) {
							String filePath = file2.getAbsolutePath();
							if (filePath != null && filePath.contains("shared_prefs")) {
								//shared_prefs文件太多，不需统计。
								break;
							}
							sb.append(" file == " + filePath + ", size=" + file2.length());
							sb.append("  exe=" + file2.canExecute() + ", read=" + file2.canRead() + ", write=" + file2.canWrite());
							sb.append(" ls file == " + AppUtils.getFileOption(filePath) + "\n");
						}
					} else {
						sb.append(" file ==" + file.getAbsolutePath() + ", size=" + file.length()+ "\n");
					}
				}
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
			sb.append("getDatabasePath has exception " + Log.getStackTraceString(e) + "\n");
		}
		try {
			if (db == null) {
				sb.append("db == null for getReadableDatabase\n");
				db = getReadableDatabase();
				sb.append("getReadableDatabase is ok\n");
			}
			if (db != null) {
				String dbPath = db.getPath();
				sb.append("  dbPath=" + dbPath);
				sb.append("  isDbLockedByCurrentThread=" + db.isDbLockedByCurrentThread());
				sb.append("  inTransaction=" + db.inTransaction());
				File file = new File(dbPath);
				sb.append("  dbFile.exe=" + file.canExecute() + ", read=" + file.canRead() + ", write=" + file.canWrite() + "\n");
			}
		} catch (Throwable e) {
			e.printStackTrace();
			sb.append("there is some excepiton" + Log.getStackTraceString(e));
		}
		try {
			sb.append("mContext.getDatabasePath begin2\n");
			File dbFile = mContext.getDatabasePath(DATABASE_NAME);
			if (dbFile == null) {
				sb.append("dbFile == null\n");
			} else if (dbFile.exists()) {
				sb.append("databaseFile.exe=" + dbFile.canExecute() + ", read=" + dbFile.canRead() + ", write=" + dbFile.canWrite()
						+ ", size=" + dbFile.length() + "\n");
			} else {
				sb.append("dbFile is not exist2 and want to openOrCreateDB\n");
				SQLiteDatabase db2 = mContext.openOrCreateDatabase(DATABASE_NAME, 0, null);
				String dbPath = db2.getPath();
				sb.append("  dbPath=" + dbPath);
				sb.append("  isDbLockedByCurrentThread=" + db2.isDbLockedByCurrentThread());
				sb.append("  inTransaction=" + db2.inTransaction());
			}
		} catch (Throwable e) {
			e.printStackTrace();
			sb.append("getDatabasePath or openCreate has exception " + Log.getStackTraceString(e) + "\n");
		}
		sb.append("\n");
		return sb.toString();
	}

	private void updateDBExceptionPrefer(Context context, int count) {
		try {
			PrivatePreference pref = PrivatePreference.getPreference(mContext);
			pref.putInt(PrefConst.KEY_REPORT, count);
			pref.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getDBExceptionPrefer(Context context) {
		int count = 0;
		try {
			PrivatePreference pref = PrivatePreference.getPreference(mContext);
			count = pref.getInt(PrefConst.KEY_REPORT, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		mUpdateResult = true;
		mIsNewDB = true;
		// 创建数据库之前，删除所有SharedPreferences文件
		new PreferencesManager(mContext).clear();

		db.beginTransaction();
		try {
			db.execSQL(FontTable.CREATETABLESQL);
			db.execSQL(UsedFontTable.CREATETABLESQL);

			db.execSQL(ShortcutSettingTable.CREATETABLESQL);
			db.execSQL(DesktopTable.CREATETABLESQL);
			db.execSQL(GravityTable.CREATETABLESQL);
			db.execSQL(GestureTable.CREATETABLESQL);
			db.execSQL(DynamicEffectTable.CREATETABLESQL);
			db.execSQL(ScreenSettingTable.CREATETABLESQL);
			db.execSQL(ThemeTable.CREATETABLESQL);
			db.execSQL(DeskMenuTable.CREATETABLESQL);
			db.execSQL(StatisticsTable.CREATETABLESQL);

			db.execSQL(SysShortcutTable.CREATETABLESQL);

			db.execSQL(ScreenTable.CREATETABLESQL);
			db.execSQL(PartsTable.CREATETABLESQL);
			db.execSQL(PartToScreenTable.CREATETABLESQL);
			db.execSQL(PartToFolderTable.CREATETABLESQL);
			db.execSQL(ShortcutTable.CREATETABLESQL);
			db.execSQL(ShortcutUnfitTable.CREATETABLESQL);
			db.execSQL(RecentAppTable.CREATETABLESQL);
			db.execSQL(AppTable.CREATETABLESQL);
			db.execSQL(FolderTable.CREATETABLESQL);

			db.execSQL(AppSettingTable.CREATETABLESQL);
			db.execSQL(AppSettingTable.INSERTAPPSETTINGVALUES);

			db.execSQL(AppWhiteListTable.CREATETABLESQL);
			db.execSQL(AppHideListTable.CREATETABLESQL);

			db.execSQL(ConfigTable.CREATETABLESQL);

			db.execSQL(DiyGestureTable.CREATETABLESQL);

			db.execSQL(MediaManagementPlayListTable.CREATETABLESQL);
			db.execSQL(MediaManagementPlayListFileTable.CREATETABLESQL);
			// 初始化默认播放列表数据

			String inittablesql = "insert into " + ConfigTable.TABLENAME + "("
					+ ConfigTable.THEMENAME + ", " + ConfigTable.TIPFRAMETIMECURVERSION + ", "
					+ ConfigTable.VERSIONCODE + ", " + ConfigTable.ISVERSIONBEFORE312 + ")"
					+ " values(" + "'" + ThemeManager.getPackageNameFromSharedpreference(mContext)
					+ "', '" + LauncherEnv.UNKNOWN_VERSION + "', "
					+ LauncherEnv.UNKNOWN_VERSIONCODE + ", " + 0 + ")";
			db.execSQL(inittablesql);

			// GO widget表
			db.execSQL(GoWidgetTable.CREATETABLESQL);

			// 桌面个性配置表
			db.execSQL(ScreenStyleConfigTable.CREATETABLESQL);
			db.execSQL(ScreenStyleConfigTable.INSERTDEFAULTVALUES);
			db.execSQL(MessageCenterTable.CREATETABLESQL);

			// 忽略更新表
			db.execSQL(NoPromptUpdateAppTable.CREATETABLESQL);

			// 创建功能表隐藏文件表
			db.execSQL(MediaManagementHideTable.CREATETABLESQL);

			// 创建新的功能表设置表
			db.execSQL(AppFuncSettingTable.CREATETABLESQL);
			AppFuncSettingTable.initDefaultDatas(db);
			// 应用游戏中心的设置
			// db.execSQL(AppGameSettingTable.CREATETABLESQL);
			// db.execSQL(AppGameSettingTable.INITSQL);

			//通讯统计应用设置表
			db.execSQL(NotificationAppSettingTable.CREATETABLESQL);

			db.execSQL(SettingTable.CREATETABLESQL);
			db.execSQL(SettingTable.INITTABLESQL);

			//创建保存云安全扫描结果的表格
			db.execSQL(AllEngineCheckResultsTable.CREATETABLESQL);
			db.execSQL(CheckResultTable.CREATETABLESQL);

			//创建安全锁表
			db.execSQL(DeskLockable.CREATETABLESQL);
			db.execSQL(LockInfoTable.CREATETABLESQL);
			db.execSQL(AppExtraAttributeTable.CREATETABLESQL);
			// 创建滚屏特效表
			db.execSQL(EffectorTable.CREATETABLESQL);
			EffectorTable.initDefaultDatas(db); // 写入初始数据
			
				//侧边栏
			String updateSql = "update " + DesktopTable.TABLENAME + " set "
						+ DesktopTable.SIDEDOCKPOSITION + " = " + "1";
			db.execSQL(updateSql);

			//创建0屏广告表
			db.execSQL(ZeroScreenAdTable.CREATE_SQL);
			db.execSQL(ZeroScreenAdSuggestSiteTable.CREATE_SQL);
			ZeroScreenAdTable.initZeroScreenDefaultAD(db);

			db.execSQL(AdTable.CREATE_TABLE_SQL);
			
			//创建侧边栏widget表
			db.execSQL(SideWidgetTable.CREATETABLESQL);
			
			//创建影视中心表
			db.execSQL(VideoCenterInfoTable.CREATE_SQL);
			
			//创建主题Icon表
			db.execSQL(ThemeIconInfoTable.CREATE_SQL);
			
			db.execSQL(PurchaseTable.CREATE_SQL);
			
			//创建应用图标配置信息表
			db.execSQL(AppIconConfigInfoTable.CREATE_SQL);
			
			// smartcard 下发的应用推荐及轻游戏
			db.execSQL(SmartCardFolderStateTable.CREATE_SQL);
			db.execSQL(SideToolsTable.CREATETABLESQL);
			
			//创建特殊应用表
			db.execSQL(SpecialApplicationTable.CREATE_SQL);
			SpecialApplicationTable.initData(db);
			
			db.setTransactionSuccessful();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
		PreferencesManager sharedPreferences = new PreferencesManager(mContext,
				IPreferencesIds.PREFERENCE_SETTING_CFG, Context.MODE_PRIVATE
						| Context.MODE_MULTI_PROCESS);
		if (sharedPreferences.getBoolean(
				IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_AUTOBACKUP_RESTOREDEFAULT, true)) {
			sharedPreferences.putBoolean(
					IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_AUTOBACKUP_RESTOREDEFAULT, false);// 恢复默认或清除數據時不需要显示异常信息
			sharedPreferences.putBoolean(
					IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_AUTOBACKUP_CHECKDB, false);
		} else {
			sharedPreferences.putBoolean(
					IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_AUTOBACKUP_CHECKDB, true);
		}
		sharedPreferences.commit();

	}

	/**
	 * 检查指定的表是否存在
	 * 
	 * @author huyong
	 * @param tableName
	 * @return
	 */
	private boolean isExistTable(final SQLiteDatabase db, String tableName) {
		boolean result = false;
		Cursor cursor = null;
		String where = "type='table' and name='" + tableName + "'";
		try {
			cursor = db.query("sqlite_master", null, where, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return result;
	}

	/**
	 * 检查表中是否存在该字段
	 * 
	 * @author huyong
	 * @param db
	 * @param tableName
	 * @param columnName
	 * @return
	 */
	private boolean isExistColumnInTable(SQLiteDatabase db, String tableName, String columnName) {
		boolean result = false;
		Cursor cursor = null;
		try {
			// 查询列数
			String columns[] = { columnName };
			cursor = db.query(tableName, columns, null, null, null, null, null);
			if (cursor != null && cursor.getColumnIndex(columnName) >= 0) {
				result = true;
			}
		} catch (Exception e) {
			// e.printStackTrace();
			Log.i("DatabaseHelper", "isExistColumnInTable has exception");
			result = false;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return result;
	}

	/**
	 * 新添加字段到表中
	 * 
	 * @author huyong
	 * @param db
	 * @param tableName
	 *            : 修改表名
	 * @param columnName
	 *            ：新增字段名
	 * @param columnType
	 *            ：新增字段类型
	 * @param defaultValue
	 *            ：新增字段默认值。为null，则不提供默认值
	 */
	private void addColumnToTable(SQLiteDatabase db, String tableName, String columnName,
			String columnType, String defaultValue) {
		if (!isExistColumnInTable(db, tableName, columnName)) {
			db.beginTransaction();
			try {
				// 增加字段
				String updateSql = "ALTER TABLE " + tableName + " ADD " + columnName + " "
						+ columnType;
				db.execSQL(updateSql);

				// 提供默认值
				if (defaultValue != null) {
					if (columnType.equals(TYPE_TEXT)) {
						// 如果是字符串类型，则需加单引号
						defaultValue = "'" + defaultValue + "'";
					}

					updateSql = "update " + tableName + " set " + columnName + " = " + defaultValue;
					db.execSQL(updateSql);
				}

				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}
		}
	}

	// 只针对安卓3.0系统以上
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 默认支持向下兼容。（oldVersion = 2, newVersion = 1）
		// 后期在做版本降级处理时，在此可根据需要做相应处理
		Log.i("DatabaseHelper", "onDowngrade oldVersion=" + oldVersion + ", newVersion="
				+ newVersion);
		return;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO:根据版本号，更新数据库表结构
		onUpgrade2(db, oldVersion, newVersion);
	}

	private void onUpgrade2(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < DB_VERSION_ONE || oldVersion > newVersion || newVersion > DB_VERSION_MAX) {
			Log.i("zj", "onUpgrade() false oldVersion = " + oldVersion + ", newVersion = "
					+ newVersion);
			// note：由于新修改数据仅仅是增加字段，未删除或重命名以前数据库中表字段，因此不必重新生成数据库，直接用当下数据库即可
			// 后期，若需要修改老版数据库中字段或删除字段，则必须将该字段字段设为false.
			// 以上规则对现已发布1.50及以后的版本均有效。1.50-2.02对应数据库版本号为7th，2.10-2.12对应数据库版本号为8th，
			// 2.13对应数据库版本号为9th.
			// mUpdateResult = false;
			return;
		}
		ArrayList<UpgradeDB> upgradeDBFuncS = new ArrayList<DatabaseHelper.UpgradeDB>();
		upgradeDBFuncS.add(new UpgradeDBOneToTwo());
		upgradeDBFuncS.add(new UpgradeDBTwoToThree());
		upgradeDBFuncS.add(new UpgradeDBThreeToFour());
		upgradeDBFuncS.add(new UpgradeDBFourToFive());
		upgradeDBFuncS.add(new UpgradeDBFiveToSix());
		upgradeDBFuncS.add(new UpgradeDBSixToSeven());
		upgradeDBFuncS.add(new UpgradeDBSevenToEight());
		upgradeDBFuncS.add(new UpgradeDBEightToNine());
		upgradeDBFuncS.add(new UpgradeDBNineToTen());
		upgradeDBFuncS.add(new UpgradeDBTenToEleven());
		upgradeDBFuncS.add(new UpgradeDBElevenToTwelve());
		upgradeDBFuncS.add(new UpgradeDBTwelveToThirteen());
		upgradeDBFuncS.add(new UpgradeDBThirteenToFourteen());
		upgradeDBFuncS.add(new UpgradeDBFourteenToFifteen());
		upgradeDBFuncS.add(new UpgradeDBFifteenToSixteen());
		upgradeDBFuncS.add(new UpgradeDBSixteenToSeventeen());
		upgradeDBFuncS.add(new UpgradeDBSeventeenToEighteen());
		upgradeDBFuncS.add(new UpgradeDBEighteenToNineteen());
		upgradeDBFuncS.add(new UpgradeDBNineteenTOTwenty());
		upgradeDBFuncS.add(new UpgradeDBTwentyTOTwentyOne());
		upgradeDBFuncS.add(new UpgradeDBTwentyOneTOTwentyTwo());
		upgradeDBFuncS.add(new UpgradeDBTwentyTwoTOTwentyThree());
		upgradeDBFuncS.add(new UpgradeDBTwentyThreeToTwentyFour());
		upgradeDBFuncS.add(new UpgradeDBTwentyFourToTwentyFive());
		upgradeDBFuncS.add(new UpgradeDBTwentyFiveToTwentySix());
		upgradeDBFuncS.add(new UpgradeDBTwentySixToTwentySeven());
		upgradeDBFuncS.add(new UpgradeDBTwentySevenToTwentyEight());
		upgradeDBFuncS.add(new TwentyEightToTwentyNine());
		upgradeDBFuncS.add(new TwentyNineToThirty());
		upgradeDBFuncS.add(new ThirtyToThirtyOne());
		upgradeDBFuncS.add(new ThirtyOneToThirtyTwo());
		upgradeDBFuncS.add(new ThirtyTwoToThirtyThree());
		upgradeDBFuncS.add(new ThirtyThreeToThirtyFour());
		upgradeDBFuncS.add(new ThirtyFourToThirtyFive());
		upgradeDBFuncS.add(new ThirtyFiveToThirtySix());
		upgradeDBFuncS.add(new ThirtySixToThirtySeven());
		upgradeDBFuncS.add(new ThirtySevenToThirtyEight());
		upgradeDBFuncS.add(new ThirtyEightToThirtyNine());
		upgradeDBFuncS.add(new ThirtyNineToForty());
		upgradeDBFuncS.add(new FortyToFortyOne());
		upgradeDBFuncS.add(new FortyOneToFortyTwo());
		upgradeDBFuncS.add(new FortyTwoToFortyThree());
		upgradeDBFuncS.add(new FortyThreeToFortyFour());
		upgradeDBFuncS.add(new FortyFourToFortyFive());
		upgradeDBFuncS.add(new FortyFiveToFortySix());
		upgradeDBFuncS.add(new FortySixToFortySeven());
		upgradeDBFuncS.add(new FortySevenToFortyEight());
		upgradeDBFuncS.add(new FortyEightToFortyNine());
		upgradeDBFuncS.add(new FortyNineToFifty());
		upgradeDBFuncS.add(new FiftyToFiftyOne());
		upgradeDBFuncS.add(new FiftyOneToFiftyTwo());
		upgradeDBFuncS.add(new FiftyTwoToFiftyThree());
		upgradeDBFuncS.add(new FiftyThreeToFiftyFour());
		upgradeDBFuncS.add(new FiftyFourToFiftyFive());
		upgradeDBFuncS.add(new FiftyFiveToFiftySix());
		upgradeDBFuncS.add(new FiftySixToFiftySeven());
		upgradeDBFuncS.add(new FiftySevenToFiftyEight());
		upgradeDBFuncS.add(new FiftyEightToFiftyNight());
		upgradeDBFuncS.add(new FiftyNineToSixty());
		upgradeDBFuncS.add(new SixtyToSixtyOne());
		upgradeDBFuncS.add(new SixtyOneToSixtyTwo());
		upgradeDBFuncS.add(new SixtyTwoToSixtyThree());
		upgradeDBFuncS.add(new SixtyThreeToSixtyFour());
		upgradeDBFuncS.add(new SixtyFourToSixtyFive());
		upgradeDBFuncS.add(new SixtyFiveToSixtySix());
		upgradeDBFuncS.add(new SixtySixToSixtySeven());
		upgradeDBFuncS.add(new SixtySevenToSixtyEight());
		upgradeDBFuncS.add(new SixtyEightToSixtyNine());
		upgradeDBFuncS.add(new SixtyNineToSeventy());
		upgradeDBFuncS.add(new SeventyToSeventyOne());
		upgradeDBFuncS.add(new SeventyOneToSeventyTwo());
		upgradeDBFuncS.add(new SeventyTwoToSeventyThree());
		upgradeDBFuncS.add(new SeventyThreeToSeventyFour());
		upgradeDBFuncS.add(new SeventyThFourToSeventyFive());
		upgradeDBFuncS.add(new SeventyFiveToSeventySix());
		upgradeDBFuncS.add(new SeventySixToSeventySeven());
		upgradeDBFuncS.add(new SeventySevenToSeventyEight());
		upgradeDBFuncS.add(new SeventyEightToSeventyNine());
		upgradeDBFuncS.add(new SeventyNineToEighty());
		upgradeDBFuncS.add(new EightyToEightyOne());
		upgradeDBFuncS.add(new EightyOneToEightyTwo());
		upgradeDBFuncS.add(new EightyTwoToEightyThree());
		upgradeDBFuncS.add(new EightyThreeToEightyFour());
		upgradeDBFuncS.add(new EightyFourToEightyFive());
		upgradeDBFuncS.add(new EightyFiveToEightySix());
		upgradeDBFuncS.add(new EightySixToEightySeven());
		upgradeDBFuncS.add(new EightySevenToEightyEight());
		upgradeDBFuncS.add(new EightyEightToEightyNine());
		upgradeDBFuncS.add(new EightyNineToNinety());
		upgradeDBFuncS.add(new NinetyToNinetyOne());
		upgradeDBFuncS.add(new NinetyOneToNinetyTwo());
		upgradeDBFuncS.add(new NinetyTwoToNinetyThree());
		upgradeDBFuncS.add(new NinetyThreeToNinetyFour());
		upgradeDBFuncS.add(new NinetyFourToNinetyFive());
		upgradeDBFuncS.add(new NinetyFiveToNinetySix());
		upgradeDBFuncS.add(new NinetySixToNinetySeven());
		upgradeDBFuncS.add(new NinetySevenToNinetyEight());
		upgradeDBFuncS.add(new NinetyEightToNinetyNine());
		upgradeDBFuncS.add(new NinetyNineToOneHundred());
		upgradeDBFuncS.add(new OneHundredToOneHundredAndOne());
		upgradeDBFuncS.add(new OneHundredAndOneToOneHundredAndTwo());
		upgradeDBFuncS.add(new OneHundredAndTwoToOneHundredAndThree());
		upgradeDBFuncS.add(new OneHundredAndThreeToOneHundredAndFour());
		upgradeDBFuncS.add(new OneHundredAndFourToOneHundredAndFive());
		upgradeDBFuncS.add(new OneHundredAndFiveToOneHundredAndSix());
		upgradeDBFuncS.add(new OneHundredAndSixToOneHundredAndSeven());
		upgradeDBFuncS.add(new OneHundredAndSevenToOneHundredAndEight());
		upgradeDBFuncS.add(new OneHundredAndEightToOneHundredAndNight());
		
		Log.i("testUpdate", "onupgrade");
		for (int i = oldVersion - 1; i < newVersion - 1; i++) {
			mUpdateResult = upgradeDBFuncS.get(i).onUpgradeDB(db);
			if (!mUpdateResult) {
				// 中间有任何一次升级失败，则直接返回

				String detailMessage = "update database has exception in " + i + ", when "
						+ upgradeDBFuncS.get(i).toString();
				throw new RuntimeException(detailMessage);

				//				break;
			}
		}
		upgradeDBFuncS.clear();
	}

	/**
	 * 用于单表查询
	 */
	public Cursor query(String tableName, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
	}

	/**
	 * 用于单表查询
	 */
	public Cursor query(String tableName, String[] projection, String selection,
			String[] selectionArgs, String groupBy, String having, String sortOrder) {
		Cursor result = null;
		try {
			SQLiteDatabase db = getReadableDatabase();
			result = db.query(tableName, projection, selection, selectionArgs, groupBy, having,
					sortOrder);
		} catch (SQLException e) {
			Log.i("data", "SQLException when query in " + tableName + ", " + selection);
		} catch (IllegalStateException e) {
			Log.i("data", "IllegalStateException when query in " + tableName + ", " + selection);
		}
		return result;
	}

	// public Cursor query(String tableName, String[] projection,
	// String selection, String[] selectionArgs, String groupBy,
	// String having, String sortOrder) {
	// Cursor result = null;
	// msqlQB.setTables(tableName);
	// if(tableName.equals(FolderTable.TABLENAME)){
	// try {
	// Thread.sleep(2000);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// try {
	// SQLiteDatabase db = getReadableDatabase();
	// result = msqlQB.query(db, projection, selection, selectionArgs,
	// null, null, sortOrder);
	// } catch (SQLException e) {
	// e.printStackTrace();
	// } catch (IllegalStateException e) {
	// e.printStackTrace();
	// }
	// return result;
	// }

	/**
	 * 用于多表查询
	 */
	public Cursor queryCrossTables(String tableName, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor result = null;
		synchronized (msqlQB) {
			msqlQB.setTables(tableName);
			try {
				SQLiteDatabase db = getReadableDatabase();
				result = msqlQB.query(db, projection, selection, selectionArgs, null, null,
						sortOrder);
			} catch (SQLException e) {
				// e.printStackTrace();
				Log.i("data", "SQLException when query in " + tableName + ", " + selection);
			} catch (IllegalStateException e) {
				// e.printStackTrace();
				Log.i("data", "IllegalStateException when query in " + tableName + ", " + selection);
			}
		}
		return result;
	}

	public long insert(String tableName, ContentValues initialValues) throws DatabaseException {
		SQLiteDatabase db = getWritableDatabase();
		long rowId = 0;
		try {
			rowId = db.insert(tableName, null, initialValues);
		} catch (Exception e) {
			Log.i("data", "Exception when insert in " + tableName);
			throw new DatabaseException(e);
		}
		return rowId;
	}

	public int delete(String tableName, String selection, String[] selectionArgs)
			throws DatabaseException {
		SQLiteDatabase db = getWritableDatabase();
		int count = 0;
		try {
			count = db.delete(tableName, selection, selectionArgs);
		} catch (Exception e) {
			Log.i("data", "Exception when delete in " + tableName + ", " + selection);
			throw new DatabaseException(e);
		}
		return count;
	}

	public int update(String tableName, ContentValues values, String selection,
			String[] selectionArgs) throws DatabaseException {
		SQLiteDatabase db = getWritableDatabase();
		int count = 0;
		try {
			count = db.update(tableName, values, selection, selectionArgs);
		} catch (Exception e) {
			Log.i("data", "Exception when update in " + tableName + ", " + selection);
			throw new DatabaseException(e);
		}
		return count;
	}

	/**
	 * 
	 * @author huyong
	 * @param sql
	 * @throws DatabaseException
	 */
	public void exec(String sql) throws DatabaseException {
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			Log.i("data", "Exception when exec " + sql);
			throw new DatabaseException(e);
		}
	}

	public boolean isNewDB() {
		return mIsNewDB;
	}

	public static String getDBName() {
		return DATABASE_NAME;
	}

	/**
	 * <br>
	 * 功能简述: <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public boolean openDBWithWorldReadable() {
		try {
			close();
			if (mContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_WORLD_READABLE, null) == null) {
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			close();
			return false;
		}
		return true;
	}

	/**
	 * 一版升级到二版
	 * 
	 * @author huyong
	 * @param db
	 */
	private boolean onUpgrade1To2(SQLiteDatabase db) {
		boolean result = false;
		String textType = TYPE_TEXT;
		String numericType = TYPE_NUMERIC;
		db.beginTransaction();
		try {

			/**
			 * *************************************************
			 * appSettingTable表升级，增加一条字段
			 */
			db.execSQL(AppSettingTable.INSERTSORTTYPESQL);

			/**
			 * *************************************************application表升级
			 */
			// 同步更新app表intent字段
			String appTableSql = "ALTER TABLE " + AppTable.TABLENAME + " ADD " + AppTable.INTENT
					+ " " + textType;
			db.execSQL(appTableSql);

			appTableSql = "update " + AppTable.TABLENAME + " set " + AppTable.INTENT + " = ("
					+ "select " + PartsTable.INTENT + " from " + PartsTable.TABLENAME + " where "
					+ PartsTable.TABLENAME + "." + PartsTable.ID + " = " + AppTable.PARTID + ")";
			db.execSQL(appTableSql);

			appTableSql = "ALTER TABLE " + AppTable.TABLENAME + " ADD " + AppTable.FOLDERID + " "
					+ numericType;
			db.execSQL(appTableSql);

			// 同步更新app表intent字段
			appTableSql = "ALTER TABLE " + AppTable.TABLENAME + " ADD " + AppTable.TITLE + " "
					+ textType;
			db.execSQL(appTableSql);

			appTableSql = "update " + AppTable.TABLENAME + " set " + AppTable.TITLE + " = ("
					+ "select " + PartsTable.TITLE + " from " + PartsTable.TABLENAME + " where "
					+ PartsTable.TABLENAME + "." + PartsTable.ID + " = " + AppTable.PARTID + ")";
			db.execSQL(appTableSql);

			appTableSql = "ALTER TABLE " + AppTable.TABLENAME + " ADD " + AppTable.FOLDERICONPATH
					+ " " + textType;
			db.execSQL(appTableSql);

			/**
			 * ****************************************recentapp表升级
			 */
			String recentAppSql = "ALTER TABLE " + RecentAppTable.TABLENAME + " ADD "
					+ RecentAppTable.INTENT + " " + textType;
			db.execSQL(recentAppSql);

			/**
			 * ****************************************shortcut表升级
			 */
			String shortcutSql = "ALTER TABLE " + ShortcutTable.TABLENAME + " ADD "
					+ ShortcutTable.INTENT + " " + textType;
			db.execSQL(shortcutSql);

			// 同步更新intent字段
			shortcutSql = "update " + ShortcutTable.TABLENAME + " set " + ShortcutTable.INTENT
					+ " = (" + "select " + PartsTable.INTENT + " from " + PartsTable.TABLENAME
					+ " where " + PartsTable.ID + " = " + ShortcutTable.PARTID + ")";
			db.execSQL(shortcutSql);

			/**
			 * ***************************************增加folder表、sysfolder表、
			 * sysshortcut表升级
			 */
			db.execSQL(FolderTable.CREATETABLESQL);

			db.execSQL(SysShortcutTable.CREATETABLESQL);

			// 更新sysshortcut表
			onUpgradeSysShortcutTableOneToTwo(db);

			// 更新parttoscreen表
			onUpgradePartToScreenOneToTwo(db);

			// 迁移数据
			onUpgradeFolderTableOneToTwo(db);

			// 清除先前数据
			String clearSql = "drop table " + PartsTable.TABLENAME;
			db.execSQL(clearSql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	private void onUpgradePartToScreenOneToTwo(SQLiteDatabase db) {

		String tmpTable = "tmp_" + PartToScreenTable.TABLENAME;

		String createTmpSql = "create Table " + tmpTable + " as select " + PartToScreenTable.ID
				+ ", " + PartToScreenTable.PARTID + ", " + PartToScreenTable.SCREENID + ", "
				+ PartToScreenTable.SCREENX + ", " + PartToScreenTable.SCREENY + ", "
				+ PartToScreenTable.SPANX + ", " + PartToScreenTable.SPANY + ", "
				+ PartToScreenTable.USERTITLE + " from " + PartToScreenTable.TABLENAME;
		db.execSQL(createTmpSql);

		String textType = TYPE_TEXT;
		String numericType = TYPE_NUMERIC;

		/**
		 * *****************************************parttoscreen表升级
		 */
		// 同步更新itemtype字段
		String partToScreenSql = "ALTER TABLE " + tmpTable + " ADD " + PartToScreenTable.ITEMTYPE
				+ " " + numericType;
		db.execSQL(partToScreenSql);

		partToScreenSql = "update " + tmpTable + " set " + PartToScreenTable.ITEMTYPE + " = ("
				+ "select " + PartsTable.ITEMTYPE + " from " + PartsTable.TABLENAME + " where "
				+ PartsTable.TABLENAME + "." + PartsTable.ID + " = " + PartToScreenTable.PARTID
				+ ")";
		db.execSQL(partToScreenSql);

		// 同步更新widgetid字段
		partToScreenSql = "ALTER TABLE " + tmpTable + " ADD " + PartToScreenTable.WIDGETID + " "
				+ numericType;
		db.execSQL(partToScreenSql);

		partToScreenSql = "update " + tmpTable + " set " + PartToScreenTable.WIDGETID + " = ("
				+ "select " + PartsTable.WIDGETID + " from " + PartsTable.TABLENAME + " where "
				+ PartsTable.TABLENAME + "." + PartsTable.ID + " = " + PartToScreenTable.PARTID
				+ ")";
		db.execSQL(partToScreenSql);

		// 同步更新intent字段
		partToScreenSql = "ALTER TABLE " + tmpTable + " ADD " + PartToScreenTable.INTENT + " "
				+ textType;
		db.execSQL(partToScreenSql);

		partToScreenSql = "update " + tmpTable + " set " + PartToScreenTable.INTENT + " = ("
				+ "select " + PartsTable.INTENT + " from " + PartsTable.TABLENAME + " where "
				+ PartsTable.TABLENAME + "." + PartsTable.ID + " = " + PartToScreenTable.PARTID
				+ ")";
		db.execSQL(partToScreenSql);

		// 同步更新uri字段
		partToScreenSql = "ALTER TABLE " + tmpTable + " ADD " + PartToScreenTable.URI + " "
				+ textType;
		db.execSQL(partToScreenSql);

		partToScreenSql = "update " + tmpTable + " set " + PartToScreenTable.URI + " = ("
				+ "select " + PartsTable.URI + " from " + PartsTable.TABLENAME + " where "
				+ PartsTable.TABLENAME + "." + PartsTable.ID + " = " + PartToScreenTable.PARTID
				+ ")";
		db.execSQL(partToScreenSql);

		// 同步更新usericontype字段
		partToScreenSql = "ALTER TABLE " + tmpTable + " ADD " + PartToScreenTable.USERICONTYPE
				+ " " + numericType;
		db.execSQL(partToScreenSql);

		partToScreenSql = "update " + tmpTable + " set " + PartToScreenTable.USERICONTYPE + " = 2";
		db.execSQL(partToScreenSql);

		// 同步更新usericonid字段
		partToScreenSql = "ALTER TABLE " + tmpTable + " ADD " + PartToScreenTable.USERICONID + " "
				+ numericType;
		db.execSQL(partToScreenSql);

		partToScreenSql = "update " + tmpTable + " set " + PartToScreenTable.USERICONID + " = 0";
		db.execSQL(partToScreenSql);

		// 同步更新usericonpath字段
		partToScreenSql = "ALTER TABLE " + tmpTable + " ADD " + PartToScreenTable.USERICONPATH
				+ " " + numericType;
		db.execSQL(partToScreenSql);

		// 更新自定义图片字段
		onUpgradeGetUserIcon(db, tmpTable);

		// 删除原先的表
		String sql = "drop table " + PartToScreenTable.TABLENAME;
		db.execSQL(sql);

		// 重命名临时表
		sql = "ALTER TABLE " + tmpTable + " rename to " + PartToScreenTable.TABLENAME;
		db.execSQL(sql);

	}

	private void onUpgradeGetUserIcon(SQLiteDatabase db, final String tableName) {
		String tables = PartToScreenTable.TABLENAME;
		// 查询列数
		String columns[] = { PartToScreenTable.ID, PartToScreenTable.USERICON };

		// 查询条件
		msqlQB.setTables(tables);
		Cursor cursor = msqlQB.query(db, columns, null, null, null, null, null);
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					do {
						int itemIdIndex = cursor.getColumnIndex(PartToScreenTable.ID);
						long itemInScreenId = cursor.getLong(itemIdIndex);
						int iconIndex = cursor.getColumnIndex(PartToScreenTable.USERICON);
						byte[] iconBytes = cursor.getBlob(iconIndex);
						if (iconBytes == null || iconBytes.length <= 0) {
							Log.i("testDatabase", "continue to " + itemInScreenId);
							continue;
						}
						String iconPath = FileUtil.saveIconToSDFile(iconBytes, null);

						Log.i("testDatabase", "update to " + itemInScreenId);
						String whereStr = PartToScreenTable.ID + " = " + itemInScreenId;
						ContentValues values = new ContentValues();
						values.put(PartToScreenTable.USERICONTYPE, 1);
						values.put(PartToScreenTable.USERICONPATH, iconPath);
						db.update(tableName, values, whereStr, null);
						values.clear();
						values = null;
					} while (cursor.moveToNext());
				}
			} finally {
				cursor.close();
			}
		}
	}

	/**
	 * 
	 * @author huyong
	 * @param db
	 */
	private void onUpgradeFolderTableOneToTwo(SQLiteDatabase db) {

		ArrayList<Long> folderIdArrayList = new ArrayList<Long>();
		String tables = PartsTable.TABLENAME;
		msqlQB.setTables(tables);
		String columns[] = { PartsTable.ID };
		String selection = PartsTable.ITEMTYPE + " = " + IItemType.ITEM_TYPE_USER_FOLDER;
		Cursor cursor = msqlQB.query(db, columns, selection, null, null, null, null);
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					do {
						int idIndex = cursor.getColumnIndex(PartsTable.ID);
						Long folderId = cursor.getLong(idIndex);
						folderIdArrayList.add(folderId);
					} while (cursor.moveToNext());
				}
			} finally {
				cursor.close();
			}
		}
		int folderSize = folderIdArrayList.size();
		for (int i = 0; i < folderSize; i++) {
			long folderId = folderIdArrayList.get(i);

			tables = PartToFolderTable.TABLENAME;
			msqlQB.setTables(tables);
			String columns2[] = { PartToFolderTable.PARTID };
			ArrayList<Long> partIdArrayList = new ArrayList<Long>();
			selection = PartToFolderTable.FOLDERID + " = " + folderId;
			cursor = msqlQB.query(db, columns2, selection, null, null, null, null);
			if (cursor != null) {
				try {
					if (cursor.moveToFirst()) {
						do {
							int idIndex = cursor.getColumnIndex(PartToFolderTable.PARTID);
							Long partId = cursor.getLong(idIndex);
							partIdArrayList.add(partId);
						} while (cursor.moveToNext());
					}
				} finally {
					cursor.close();
				}
			}

			int partSize = partIdArrayList.size();
			String updateSql = "update " + PartToFolderTable.TABLENAME + " set "
					+ PartToFolderTable.MINDEX + " = ";
			String where = " where " + PartToFolderTable.PARTID + " = ";
			String sql = null;
			for (int j = 0; j < partSize; j++) {
				sql = updateSql + j + where + partIdArrayList.get(j);
				db.execSQL(sql);
			}

		}
		String tmp1 = "create table tmpfolder as "
				+ "select id, folderid, intent, mindex, parts.itemtype "
				+ "from parts, parttofolder where id = parttofolder.partid";
		db.execSQL(tmp1);
		String tmp2 = "create table tmpuser as "
				+ "select partid, usertitle, usericontype, usericonid, usericonpath from parttoscreen";
		db.execSQL(tmp2);
		String tmp3 = "create table tmpfolderanduser as " + "select * from"
				+ " tmpfolder left join tmpuser on id = partid";

		db.execSQL(tmp3);

		tables = "tmpfolderanduser";
		msqlQB.setTables(tables);

		cursor = msqlQB.query(db, null, null, null, null, null, null);
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					do {
						Long itemId = cursor.getLong(0);
						Long folderId = cursor.getLong(1);
						String intentString = cursor.getString(2);
						int index = cursor.getInt(3);
						int itemType = cursor.getInt(4);
						// 第5个是partid，不需要
						String userTitle = cursor.getString(6);
						int userIconType = cursor.getInt(7);
						int userIconId = cursor.getInt(8);
						String userIconPath = cursor.getString(9);
						ContentValues contentValues = new ContentValues();
						contentValues.put(FolderTable.ID, itemId);
						contentValues.put(FolderTable.FOLDERID, folderId);
						contentValues.put(FolderTable.INTENT, intentString);
						contentValues.put(FolderTable.INDEX, index);
						contentValues.put(FolderTable.TYPE, itemType);
						contentValues.put(FolderTable.USERTITLE, userTitle);
						contentValues.put(FolderTable.USERICONTYPE, userIconType);
						contentValues.put(FolderTable.USERICONID, userIconId);
						contentValues.put(FolderTable.USERICONPATH, userIconPath);

						db.insert(FolderTable.TABLENAME, null, contentValues);

					} while (cursor.moveToNext());
				}
			} finally {
				cursor.close();
			}
		}

		// 删除临时表
		String delTable = "drop table " + "tmpfolder";
		db.execSQL(delTable);

		delTable = "drop table " + "tmpuser";
		db.execSQL(delTable);

		delTable = "drop table " + "tmpfolderanduser";
		db.execSQL(delTable);

	}

	private void onUpgradeSysShortcutTableOneToTwo(SQLiteDatabase db) {
		String tables = PartsTable.TABLENAME + ", " + PartToScreenTable.TABLENAME;
		msqlQB.setTables(tables);
		String columns[] = { PartsTable.INTENT, PartsTable.TITLE, PartsTable.ICON,
				PartToScreenTable.USERTITLE, PartToScreenTable.USERICON };
		String selection = PartsTable.ITEMTYPE + " = " + IItemType.ITEM_TYPE_SHORTCUT + " and "
				+ PartsTable.ID + " = " + PartToScreenTable.PARTID;
		Cursor cursor = msqlQB.query(db, columns, selection, null, null, null, null);
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					do {
						String intentString = cursor.getString(0);
						int refcount = 0;

						// 查找更新
						msqlQB.setTables(SysShortcutTable.TABLENAME);
						String sysColumns[] = { SysShortcutTable.REFCOUNT };
						String where = SysShortcutTable.INTENT + " = '" + intentString + "'";
						Cursor sysCursor = msqlQB.query(db, sysColumns, where, null, null, null,
								null);
						if (sysCursor != null) {
							try {
								if (sysCursor.moveToFirst()) {
									do {
										refcount = sysCursor.getInt(0) + 1;
									} while (sysCursor.moveToNext());

									ContentValues values = new ContentValues();
									values.put(SysShortcutTable.REFCOUNT, refcount);
									db.update(SysShortcutTable.TABLENAME, values, where, null);

								}
							} finally {
								sysCursor.close();
							}
						}
						if (refcount > 0) {
							continue;
						} else {
							refcount = 1;
						}
						String titleString = cursor.getString(1);
						byte[] iconData = cursor.getBlob(2);
						String userTitleString = cursor.getString(3);
						byte[] userIconData = cursor.getBlob(4);

						ContentValues contentValues = new ContentValues();
						contentValues.put(SysShortcutTable.INTENT, intentString);
						if (userTitleString != null) {
							contentValues.put(SysShortcutTable.NAME, userTitleString);
						} else {
							contentValues.put(SysShortcutTable.NAME, titleString);
						}

						if (userIconData != null) {
							contentValues.put(SysShortcutTable.ICON, userIconData);
						} else {
							contentValues.put(SysShortcutTable.ICON, iconData);
						}
						contentValues.put(SysShortcutTable.REFCOUNT, refcount);

						db.insert(SysShortcutTable.TABLENAME, null, contentValues);

					} while (cursor.moveToNext());
				}
			} finally {
				cursor.close();
			}
		}
	}

	/**
	 * 从2版升级到3版
	 * 
	 * @author huyong
	 * @param db
	 * @return
	 */
	private boolean onUpgrade2To3(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * ***************************************增加程序白名单表升级****************
			 * ***
			 */
			db.execSQL(AppWhiteListTable.CREATETABLESQL);

			/**
			 * ***************************************Theme表添加字段升级**************
			 * *****
			 */
			String numericType = TYPE_NUMERIC;
			String themeTableSql = "ALTER TABLE " + ThemeTable.TABLENAME + " ADD "
					+ ThemeTable.PREVENTFORCECLOSE + " " + numericType;
			db.execSQL(themeTableSql);

			themeTableSql = "update " + ThemeTable.TABLENAME + " set "
					+ ThemeTable.PREVENTFORCECLOSE + " = 1";
			db.execSQL(themeTableSql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 从3版升级到4版
	 * 
	 * @author huyong
	 * @param db
	 */
	public boolean onUpgrade3To4(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 功能表设置，添加字段值
			 */
			db.execSQL(AppSettingTable.INSERTSHOWNEGLECTAPPSSQL);

			/**
			 * 屏幕设置表，增加字段
			 */
			String numericType = TYPE_NUMERIC;
			String screenSettingTableSql = "ALTER TABLE " + ScreenSettingTable.TABLENAME + " ADD "
					+ ScreenSettingTable.WALLPAPER_SCROLL + " " + numericType;
			db.execSQL(screenSettingTableSql);

			/**
			 * 修改操作手势的索引值
			 */
			String gestureActionColumn = GestureTable.GESTURACTION;
			String gestureActionSql = "update " + GestureTable.TABLENAME + " set "
					+ gestureActionColumn + " = " + gestureActionColumn + " + 1" + " where "
					+ gestureActionColumn + " > 0";
			db.execSQL(gestureActionSql);

			gestureActionSql = "update " + GestureTable.TABLENAME + " set " + gestureActionColumn
					+ " = " + 1 + " where " + gestureActionColumn + " > 8";
			db.execSQL(gestureActionSql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 从4版升级到5版
	 * 
	 * @author huyong
	 * @param db
	 */
	private boolean onUpgrade4To5(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 新增apphidelist表
			 */
			if (!isExistTable(db, AppHideListTable.TABLENAME)) {
				db.execSQL(AppHideListTable.CREATETABLESQL);
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 从5版升级到6版
	 * 
	 * @author huyong
	 * @param db
	 * @return
	 */
	private boolean onUpgrade5To6(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * ***************修改动态效果表
			 */
			String scrollspeedColumn = DynamicEffectTable.SCROLLSPEED;
			String dynamicEffectSql = "update " + DynamicEffectTable.TABLENAME + " set "
					+ scrollspeedColumn + " = " + "cast (" + scrollspeedColumn + " * 0.625"
					+ " as int );";
			db.execSQL(dynamicEffectSql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 从6版升级到7版
	 * 
	 * @author huyong
	 * @param db
	 * @return
	 */
	private boolean onUpgrade6To7(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {

			/**
			 * 升级shortcutsetting表
			 */
			String numericType = TYPE_NUMERIC;
			String shortcutSettingTableSql = "ALTER TABLE " + ShortcutSettingTable.TABLENAME
					+ " ADD " + ShortcutSettingTable.THEMENAME + " " + numericType;
			db.execSQL(shortcutSettingTableSql);
			// 初始化默认值
			shortcutSettingTableSql = "update " + ShortcutSettingTable.TABLENAME + " set "
					+ ShortcutSettingTable.THEMENAME + " = '" + IGoLauncherClassName.DEFAULT_THEME_PACKAGE
					+ "'";
			db.execSQL(shortcutSettingTableSql);

			boolean isInstalledNotification = GoAppUtils.isAppExist(mContext,
					PackageName.NOTIFICATION_PACKAGE_NAME);
			if (!isInstalledNotification) {
				// 修改值未读短信、未接来电、未读Gmail的值
				shortcutSettingTableSql = "update " + ShortcutSettingTable.TABLENAME + " set "
						+ ShortcutSettingTable.AUTOMESSAGESTATICS + " = 0 ";
				db.execSQL(shortcutSettingTableSql);

				shortcutSettingTableSql = "update " + ShortcutSettingTable.TABLENAME + " set "
						+ ShortcutSettingTable.AUTOMISSCALLSTATICS + " = 0 ";
				db.execSQL(shortcutSettingTableSql);

				shortcutSettingTableSql = "update " + ShortcutSettingTable.TABLENAME + " set "
						+ ShortcutSettingTable.AUTOMISSMAILSTATICS + " = 0 ";
				db.execSQL(shortcutSettingTableSql);
			}

			/**
			 * 升级shortcut表
			 */
			String shortcutTableSql = "ALTER TABLE " + ShortcutTable.TABLENAME + " ADD "
					+ ShortcutTable.THEMENAME + " " + numericType;
			db.execSQL(shortcutTableSql);
			// 初始化默认值
			shortcutTableSql = "update " + ShortcutTable.TABLENAME + " set "
					+ ShortcutTable.THEMENAME + " = '" + IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "'";
			db.execSQL(shortcutTableSql);

			String textType = TYPE_TEXT;
			shortcutTableSql = "ALTER TABLE " + ShortcutTable.TABLENAME + " ADD "
					+ ShortcutTable.USERICONPACKAGE + " " + textType;
			db.execSQL(shortcutTableSql);

			shortcutTableSql = "ALTER TABLE " + ShortcutTable.TABLENAME + " ADD "
					+ ShortcutTable.USEPACKAGE + " " + textType;
			db.execSQL(shortcutTableSql);

			// TODO: 增加默认值

			/**
			 * 升级parttoscreen folder表
			 */
			String parttoscreenTableSql = "ALTER TABLE " + PartToScreenTable.TABLENAME + " ADD "
					+ PartToScreenTable.USERICONPACKAGE + " " + textType;
			db.execSQL(parttoscreenTableSql);
			// TODO: 增加默认值

			String folderTableSql = "ALTER TABLE " + FolderTable.TABLENAME + " ADD "
					+ FolderTable.USERICONPACKAGE + " " + textType;
			db.execSQL(folderTableSql);
			// TODO: 增加默认值

			/**
			 * 升级AppSettingTable表
			 */
			String defaultTheme = IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
			String tmpSql = "insert into " + AppSettingTable.TABLENAME + " values(100, '"
					+ defaultTheme + "');";
			db.execSQL(tmpSql);

			String tmpTable = "tmpapp";
			String infoType = AppSettingTable.INFOTYPE;
			String infoValue = AppSettingTable.INFOVALUE;
			String appsettingTableSql = "create table " + tmpTable + " as select " + "case "
					+ infoType + " when '100' then " + infoValue + " else '" + defaultTheme
					+ "' end " + AppSettingTable.THEMEPACKAGENAME + "," + "max(case " + infoType
					+ " when '0' then " + infoValue + " else 0 end) "
					+ AppSettingTable.MENUAPPSTYLE + "," + "max(case " + infoType
					+ " when '1' then " + infoValue + " else 0 end) "
					+ AppSettingTable.TURNSCREENDIRECTION + "," + "max(case " + infoType
					+ " when '2' then " + infoValue + " else 0 end) "
					+ AppSettingTable.APPNAMEVISIABLE + "," + "max(case " + infoType
					+ " when '3' then " + infoValue + " else 0 end) "
					+ AppSettingTable.LINECOLUMNNUM + "," + "max(case " + infoType
					+ " when '4' then " + infoValue + " else 0 end) "
					+ AppSettingTable.BACKGROUNDPICPATH + "," + "max(case " + infoType
					+ " when '5' then " + infoValue + " else 0 end) "
					+ AppSettingTable.BACKGROUNDVISIABLE + "," + "max(case " + infoType
					+ " when '6' then " + infoValue + " else 0 end) " + AppSettingTable.SORTTYPE
					+ "," + "max(case " + infoType + " when '7' then " + infoValue
					+ " else 0 end) " + AppSettingTable.SHOWNEGLECTAPPS + " from "
					+ AppSettingTable.TABLENAME;
			db.execSQL(appsettingTableSql);
			// 删除旧表
			appsettingTableSql = "drop table " + AppSettingTable.TABLENAME;
			db.execSQL(appsettingTableSql);
			// 更名临时表
			appsettingTableSql = "alter table " + tmpTable + " rename to "
					+ AppSettingTable.TABLENAME;
			db.execSQL(appsettingTableSql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 从7版升级到8版
	 * 
	 * @author huyong
	 * @param db
	 * @return
	 */
	private boolean onUpgrade7To8(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 修改动态效果表，增加字段
			 */
			String tableName = DynamicEffectTable.TABLENAME;
			String columnName = DynamicEffectTable.EFFECTORTYPE;
			if (!isExistColumnInTable(db, tableName, columnName)) {

				String numericType = TYPE_NUMERIC;
				String dynamicEffectSql = "ALTER TABLE " + tableName + " ADD " + columnName + " "
						+ numericType;
				db.execSQL(dynamicEffectSql);

				dynamicEffectSql = "update " + tableName + " set " + columnName + " = 0";
				db.execSQL(dynamicEffectSql);

			}

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 从第8版升到第9版
	 * 
	 * @author huyong
	 * @param db
	 * @return
	 */
	private boolean onUpgrade8To9(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 修改屏幕设置表，增加字段
			 */
			String tableName = ScreenSettingTable.TABLENAME;
			String columnName = ScreenSettingTable.SCREEN_LOOPING;
			if (!isExistColumnInTable(db, tableName, columnName)) {
				// 增加字段
				String numericType = TYPE_NUMERIC;
				String screenSettingSql = "ALTER TABLE " + tableName + " ADD " + columnName + " "
						+ numericType;
				db.execSQL(screenSettingSql);

				screenSettingSql = "update " + tableName + " set " + columnName + " = 0";
				db.execSQL(screenSettingSql);
			}

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 从第9版升到第10版
	 * 
	 * @author huyong
	 * @param db
	 * @return
	 */
	public boolean onUpgrade9To10(SQLiteDatabase db) {
		boolean result = false;
		Log.i("testUpdate", "begin to upgrade");
		db.beginTransaction();
		try {
			/**
			 * 升级AppSettingTable表，增加字段。
			 */
			// 增加字段
			String tableName = AppSettingTable.TABLENAME;
			String columnName = AppSettingTable.INOUTEFFECT;
			if (!isExistColumnInTable(db, tableName, columnName)) {
				String textType = TYPE_TEXT;
				String appSettingSql = "ALTER TABLE " + tableName + " ADD " + columnName + " "
						+ textType;
				db.execSQL(appSettingSql);

				appSettingSql = "update " + tableName + " set " + columnName + " = "
						+ AppSettingDefault.INOUTEFFECT;
				db.execSQL(appSettingSql);
			}

			/*
			 * 1.创建临时shortcut表
			 */
			String createSql = "create table tmp_shortcut (resId int, resName text); ";
			db.execSQL(createSql);

			String array[] = new String[14];
			array[0] = "_0_phone";
			array[1] = "_1_contacts";
			array[2] = "_2_funclist";
			array[3] = "_3_sms";
			array[4] = "_4_browser";
			array[5] = "_addicon";
			array[6] = "_application";
			array[7] = "_calendar";
			array[8] = "_camera";
			array[9] = "_gmail";
			array[10] = "_market";
			array[11] = "_music";
			array[12] = "_picture";
			array[13] = "_setting";

			String resName = "shortcut_";
			int startId = 2130837669;
			String updateSql = null;
			for (int i = 0; i <= 1; i++) {
				for (int j = 0; j <= 13; j++) {
					updateSql = "insert into tmp_shortcut values( " + startId + ", '" + resName + i
							+ array[j] + "' )";
					db.execSQL(updateSql);
					++startId;
				}
			}

			startId = 2130837755;
			String prefix = "zz_t";
			for (int j = 0; j <= 13; j++) {
				updateSql = "insert into tmp_shortcut values( " + startId + ", '" + prefix
						+ resName + 2 + array[j] + "' )";
				db.execSQL(updateSql);
				++startId;
			}

			/**
			 * 2.级联更新parttoscreen表
			 */
			// 更新usericonpath字段
			updateSql = "update " + PartToScreenTable.TABLENAME + " set "
					+ PartToScreenTable.USERICONPATH + " = "
					+ "( select resName from tmp_shortcut where tmp_shortcut.resId = "
					+ PartToScreenTable.USERICONID + ")" + " where "
					+ PartToScreenTable.USERICONTYPE + " = "
					+ ImagePreviewResultType.TYPE_RESOURCE_ID;
			db.execSQL(updateSql);

			String pkgName = mContext.getPackageName();
			// 更新usericonpackage字段
			updateSql = "update " + PartToScreenTable.TABLENAME + " set "
					+ PartToScreenTable.USERICONPACKAGE + " = '" + pkgName + "' where "
					+ PartToScreenTable.USERICONTYPE + " = "
					+ ImagePreviewResultType.TYPE_RESOURCE_ID;
			db.execSQL(updateSql);

			// 更新usericontype字段
			updateSql = "update " + PartToScreenTable.TABLENAME + " set "
					+ PartToScreenTable.USERICONTYPE + " = "
					+ ImagePreviewResultType.TYPE_PACKAGE_RESOURCE + " where "
					+ PartToScreenTable.USERICONTYPE + " = "
					+ ImagePreviewResultType.TYPE_RESOURCE_ID;
			db.execSQL(updateSql);

			/**
			 * 3.级联更新shortcut表
			 */
			// 更新usericonpath字段
			updateSql = "update " + ShortcutTable.TABLENAME + " set " + ShortcutTable.USERICONPATH
					+ " = " + "( select resName from tmp_shortcut where tmp_shortcut.resId = "
					+ ShortcutTable.USERICONID + ")" + " where " + ShortcutTable.USERICONTYPE
					+ " = " + ImagePreviewResultType.TYPE_RESOURCE_ID;
			db.execSQL(updateSql);
			// 更新usericonpackage字段
			updateSql = "update " + ShortcutTable.TABLENAME + " set "
					+ ShortcutTable.USERICONPACKAGE + " = '" + pkgName + "' where "
					+ ShortcutTable.USERICONTYPE + " = " + ImagePreviewResultType.TYPE_RESOURCE_ID;
			db.execSQL(updateSql);
			// 更新usericontype字段
			updateSql = "update " + ShortcutTable.TABLENAME + " set " + ShortcutTable.USERICONTYPE
					+ " = " + ImagePreviewResultType.TYPE_PACKAGE_RESOURCE + " where "
					+ ShortcutTable.USERICONTYPE + " = " + ImagePreviewResultType.TYPE_RESOURCE_ID;
			db.execSQL(updateSql);

			/**
			 * 删除临时表
			 */
			updateSql = "drop table tmp_shortcut";
			db.execSQL(updateSql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		Log.i("testUpdate", "end to upgrade");
		return result;
	}

	/**
	 * 从第10版升到第11版
	 * 
	 * @author huyong
	 * @param db
	 * @return
	 */
	public boolean onUpgrade10To11(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			String tableName = DynamicEffectTable.TABLENAME;
			String columnName = DynamicEffectTable.AUTOTWEAKELASTICITY;
			String numericType = TYPE_NUMERIC;
			String defaultValue = "1";
			// 升级修改屏幕动态设置表，增加字段
			addColumnToTable(db, tableName, columnName, numericType, defaultValue);

			/**
			 * 升级快捷条设置表，增加字段
			 */
			String textType = TYPE_TEXT;
			tableName = ShortcutSettingTable.TABLENAME;
			columnName = ShortcutSettingTable.BG_TARGET_THEME_NAME;
			defaultValue = null;
			// 新增bgtargetthemename字段
			addColumnToTable(db, tableName, columnName, textType, defaultValue);

			// 新增bgresname字段
			columnName = ShortcutSettingTable.BG_RESNAME;
			addColumnToTable(db, tableName, columnName, textType, defaultValue);

			// 新增bgiscustompic字段
			columnName = ShortcutSettingTable.CUSTOM_PIC_OR_NOT;
			defaultValue = "0";
			addColumnToTable(db, tableName, columnName, numericType, defaultValue);

			/**
			 * 升级快捷条表，修改字段值
			 */
			tableName = ShortcutTable.TABLENAME;
			columnName = ShortcutTable.USERICONPATH;
			String userIconId = ShortcutTable.USERICONID;
			String iconId = "2130837808";
			String value = "'zzzzzz_dock_blank'";
			String updateSql = "update " + tableName + " set " + columnName + " = " + value + ", "
					+ ShortcutTable.USERICONTYPE + " = "
					+ ImagePreviewResultType.TYPE_PACKAGE_RESOURCE + ", "
					+ ShortcutTable.USEPACKAGE + " = " + "'" + IGoLauncherClassName.DEFAULT_THEME_PACKAGE
					+ "', " + ShortcutTable.USERICONPACKAGE + " = " + "'"
					+ IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "'" + " where " + userIconId + " = "
					+ iconId + " and " + columnName + " is null";
			db.execSQL(updateSql);

			// 标记此次是升级完的第一次启动桌面
			// sIsLastUpdateThisRun = DB_VERSION_ELEVEN;

			// 第一次升级2.16进入的特殊操作:对默认主题的新加字段进行修改
			String dockBgpath = DockLogicControler
					.getDockBgReadFilePath(IGoLauncherClassName.DEFAULT_THEME_PACKAGE);
			File file = new File(dockBgpath);
			int custom_pic_or_not = ConvertUtils.boolean2int(true);
			if (!(file.exists())) {
				dockBgpath = "";
				custom_pic_or_not = ConvertUtils.boolean2int(false);
			}

			tableName = ShortcutSettingTable.TABLENAME;
			updateSql = "update " + tableName + " set " + ShortcutSettingTable.BG_TARGET_THEME_NAME
					+ " = ''," + ShortcutSettingTable.BG_RESNAME + " = '" + dockBgpath + "', "
					+ ShortcutSettingTable.CUSTOM_PIC_OR_NOT + " = " + custom_pic_or_not
					+ " where " + ShortcutSettingTable.THEMENAME + " = " + "'"
					+ IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "'";

			db.execSQL(updateSql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 从第11版升到第12版
	 * 
	 * @author huyong
	 * @param db
	 * @return
	 */
	public boolean onUpgrade11To12(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// 建表
			if (!isExistTable(db, ConfigTable.TABLENAME)) {
				db.execSQL(ConfigTable.CREATETABLESQL);
				String inittablesql = "insert into " + ConfigTable.TABLENAME + "("
						+ ConfigTable.THEMENAME + ", " + ConfigTable.TIPFRAMETIMECURVERSION + ")"
						+ " values(" + "'"
						+ ThemeManager.getPackageNameFromSharedpreference(mContext) + "', '"
						+ LauncherEnv.UNKNOWN_VERSION + "')";

				db.execSQL(inittablesql);
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第12版升到第13版
	 * 
	 * @author huyong
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB12To13(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// 建表
			if (!isExistTable(db, FontTable.TABLENAME)) {
				db.execSQL(FontTable.CREATETABLESQL);
			}

			if (!isExistTable(db, UsedFontTable.TABLENAME)) {
				db.execSQL(UsedFontTable.CREATETABLESQL);
			}

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第13版升到第14版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB13To14(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// 新增GoWidgetTable表
			if (!isExistTable(db, GoWidgetTable.TABLENAME)) {
				db.execSQL(GoWidgetTable.CREATETABLESQL);
			}

			// 修改ShortcutSettingTable中值
			String tableName = ShortcutSettingTable.TABLENAME;
			String columnName = ShortcutSettingTable.CUSTOMBGPICSWITCH;
			String columnType = TYPE_NUMERIC;
			String defaultValue = null;
			addColumnToTable(db, tableName, columnName, columnType, defaultValue);
			String sql = "update " + tableName + " set " + columnName + " = "
					+ ShortcutSettingTable.BGPICSWITCH;
			db.execSQL(sql);

			columnName = ShortcutSettingTable.STYLE_STRING;
			columnType = TYPE_TEXT;
			addColumnToTable(db, tableName, columnName, columnType, defaultValue);
			sql = "update " + tableName + " set " + columnName + " = " + " case "
					+ ShortcutSettingTable.STYLE + " when 1 then '"
					+ DockUtil.DOCK_DEFAULT_STYLE_STRING + "'" + " when 2 then '"
					+ DockUtil.DOCK_TRANSPARENT_STYLE_STRING + "'"
					// + " when 3 then '" + IGoLauncherClassName.DEFAULT_THEME_PACKAGE +
					// "'"
					// + " when 4 then '" + IGoLauncherClassName.DEFAULT_THEME_PACKAGE +
					// "'"
					+ " end ";
			db.execSQL(sql);

			// 修改DesktopTable表增加themeiconstyle 字段
			tableName = DesktopTable.TABLENAME;
			columnName = DesktopTable.THEMEICONSTYLE;
			columnType = TYPE_NUMERIC;
			defaultValue = String.valueOf(1);
			addColumnToTable(db, tableName, columnName, columnType, defaultValue);

			// 修改FolderTable表增加文件加图标是否从功能表来字段
			tableName = FolderTable.TABLENAME;
			columnName = FolderTable.FROMAPPDRAWER;
			columnType = TYPE_NUMERIC;
			defaultValue = String.valueOf(0);
			addColumnToTable(db, tableName, columnName, columnType, defaultValue);

			// 修改动态设置弹性值Min(100, * 2.5)
			sql = "update " + DynamicEffectTable.TABLENAME + " set " + DynamicEffectTable.BACKSPEED
					+ " = " + "min(100, cast (" + DynamicEffectTable.BACKSPEED + " * 2.5"
					+ " as int ));";
			db.execSQL(sql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第14版升到第15版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB14To15(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// 修改ShortcutSettingTable中ENABLE的值
			String tableName = ShortcutSettingTable.TABLENAME;
			String columnName = ShortcutSettingTable.ENABLE;
			String defaultValue = String.valueOf(ConvertUtils.boolean2int(true));
			String sql = "update " + tableName + " set " + columnName + " = " + defaultValue;
			db.execSQL(sql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第15版升到第16版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB15To16(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级AppSettingTable表，增加字段。
			 */
			// 增加字段
			String tableName = AppSettingTable.TABLENAME;
			String columnName = AppSettingTable.ICONEFFECT;
			if (!isExistColumnInTable(db, tableName, columnName)) {
				String textType = TYPE_TEXT;
				String appSettingSql = "ALTER TABLE " + tableName + " ADD " + columnName + " "
						+ textType;
				db.execSQL(appSettingSql);

				appSettingSql = "update " + tableName + " set " + columnName + " = "
						+ AppSettingDefault.ICONEFFECT;
				db.execSQL(appSettingSql);
			}

			/**
			 * 升级ShortcutSettingTable表，增加字段。
			 */
			tableName = ShortcutSettingTable.TABLENAME;
			columnName = ShortcutSettingTable.AUTOMISSK9MAILSTATICS;
			if (!isExistColumnInTable(db, tableName, columnName)) {
				String textType = TYPE_NUMERIC;
				String shortcutSettingSql = "ALTER TABLE " + tableName + " ADD " + columnName + " "
						+ textType;
				db.execSQL(shortcutSettingSql);

				shortcutSettingSql = "update " + tableName + " set " + columnName + " = " + 0;
				db.execSQL(shortcutSettingSql);
			}

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第16版升到第17版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB16To17(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级AppSettingTable表，增加字段。
			 */
			// 增加字段
			String tableName = AppSettingTable.TABLENAME;
			String columnName = AppSettingTable.SCROLL_LOOP;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC,
					String.valueOf(AppSettingDefault.SCROLL_LOOP));

			columnName = AppSettingTable.BLUR_BACKGROUND;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC,
					String.valueOf(AppSettingDefault.BLUR_BACKGROUND));

			/**
			 * 升级ShortcutTable表，修改第一条记录intent字段的值
			 */
			// 读取原来的值
			tableName = ShortcutTable.TABLENAME;
			columnName = ShortcutTable.INTENT;
			String projection[] = { ShortcutTable.INTENT, ShortcutTable.MINDEX };
			String sortOrder = ShortcutTable.MINDEX + " ASC";
			String columnValue = null;
			int mIndexValue = -1;
			Cursor cursor = null;
			try {
				cursor = db.query(tableName, projection, null, null, null, null, sortOrder);
				if (cursor != null && cursor.moveToFirst()) {
					int intentColumnIndex = cursor.getColumnIndex(columnName);
					columnValue = cursor.getString(intentColumnIndex);
					int mIndexColumnIndex = cursor.getColumnIndex(ShortcutTable.MINDEX);
					mIndexValue = cursor.getInt(mIndexColumnIndex);
				}
			} catch (SQLiteException e) {
				e.printStackTrace();
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}

			// 判断是否修改，如果要修改，就写回原来的位置覆盖
			if (null != columnValue) {
				Intent intent = ConvertUtils.stringToIntent(columnValue);
				Uri uri = Uri.parse("tel:");
				if (null != intent && Intent.ACTION_VIEW.equals(intent.getAction())
						&& uri.equals(intent.getData())) {
					intent = AppIdentifier.createSelfDialIntent(mContext);
					columnValue = intent.toURI();
					// TODO:在这里把columnValue写回原来的位置
					ContentValues contentValues = new ContentValues();
					contentValues.put(columnName, columnValue);
					String whereString = ShortcutTable.MINDEX + " = " + mIndexValue;
					db.update(tableName, contentValues, whereString, null);
				}
			}

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第17版升到第18版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB17To18(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级GoWidgetTable表，增加字段。
			 */
			String tableName = GoWidgetTable.TABLENAME;
			String columnName = GoWidgetTable.THEME;
			// 增加字段
			addColumnToTable(db, tableName, columnName, TYPE_TEXT,
					IGoLauncherClassName.DEFAULT_THEME_PACKAGE);

			/**
			 * 升级DesktopTable表，增加字段。
			 */
			tableName = DesktopTable.TABLENAME;
			// 先获取原表中的style
			String projection[] = { DesktopTable.STYLE };
			int mIndexValue = -1;
			Cursor cursor = db.query(tableName, projection, null, null, null, null, null);
			try {
				if (cursor != null && cursor.moveToFirst()) {
					int mIndexColumnIndex = cursor.getColumnIndex(DesktopTable.STYLE);
					mIndexValue = cursor.getInt(mIndexColumnIndex);
				}
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
			// 行列数的默认值
			int mDefaultRow = 4, mDefaultColumn = 4;
			switch (mIndexValue) {
				case 1 :
					mDefaultRow = 4;
					mDefaultColumn = 4;
					break;
				case 2 :
					mDefaultRow = 5;
					mDefaultColumn = 4;
					break;
				case 3 :
					mDefaultRow = 5;
					mDefaultColumn = 5;
					break;
				default :
					break;
			}
			// 增加字段
			columnName = DesktopTable.ROW;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC, String.valueOf(mDefaultRow));

			columnName = DesktopTable.COLUMN;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC,
					String.valueOf(mDefaultColumn));

			/**
			 * 添加高质量绘图字段
			 */
			tableName = ThemeTable.TABLENAME;
			columnName = ThemeTable.HIGHQUALITYDRAWING;
			// 默认关闭高质量绘图
			// 在2.30开启高质量绘图，可能会导致滑屏比较慢，所以注释掉
			// boolean hqDrawing = Build.VERSION.SDK_INT >= 9;
			boolean hqDrawing = false;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC,
					String.valueOf(ConvertUtils.boolean2int(hqDrawing)));

			/**
			 * 升级shortcutTable表，在符合没修改过默认主题背景的前提下修改默认主题背景开关为打开
			 */
			tableName = ShortcutSettingTable.TABLENAME;
			columnName = ShortcutSettingTable.BGPICSWITCH;
			// 升级条件：查找columnName =
			// ShortcutSettingTable.THEMENAME;columnName为IGoLauncherClassName.DEFAULT_THEME_PACKAGE的项，
			// 如果此项中ShortcutSettingTable.CUSTOMBGPICSWITCH的值为0,则把此项的ShortcutSettingTable.BGPICSWITCH改为1
			String shortcutSettingSql = "update " + tableName + " set " + columnName + " = " + 1
					+ " where " + ShortcutSettingTable.THEMENAME + " = '"
					+ IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "'" + " and "
					+ ShortcutSettingTable.CUSTOMBGPICSWITCH + " = " + 0;
			db.execSQL(shortcutSettingSql);

			// 去除“透明风格”，如果原来选择了“透明风格”，则改为“默认风格”
			columnName = ShortcutSettingTable.STYLE_STRING;
			shortcutSettingSql = "update " + tableName + " set " + columnName + " = '"
					+ DockUtil.DOCK_DEFAULT_STYLE_STRING + "'" + " where "
					+ ShortcutSettingTable.STYLE_STRING + " = '"
					+ DockUtil.DOCK_TRANSPARENT_STYLE_STRING + "'";
			db.execSQL(shortcutSettingSql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第18版升到第19版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB18To19(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// 升级AppSettingTable，重新设置backgroundVisiable字段
			String tableName = AppSettingTable.TABLENAME;
			String columns[] = { AppSettingTable.THEMEPACKAGENAME,
					AppSettingTable.BACKGROUNDVISIABLE, AppSettingTable.BACKGROUNDPICPATH };

			Cursor cursor = db.query(tableName, columns, null, null, null, null, null);
			String pkgName = null;
			String visible = null;
			String path = null;
			String updateStr = null;
			int valueIndex;
			int bgSetting;
			if (cursor != null) {
				try {
					if (cursor.moveToFirst()) {
						do {
							valueIndex = cursor.getColumnIndex(AppSettingTable.BACKGROUNDVISIABLE);
							visible = cursor.getString(valueIndex);
							valueIndex = cursor.getColumnIndex(AppSettingTable.THEMEPACKAGENAME);
							pkgName = cursor.getString(valueIndex);
							if (visible != null) {
								if (visible.contains("0")) {
									// 未设置壁纸
									if (pkgName != null) {
										if (pkgName.contains("theme")) {
											// 主题默认壁纸
											bgSetting = FunAppSetting.BG_DEFAULT;
										} else {
											// 默认主题没有壁纸
											bgSetting = FunAppSetting.BG_NON;
										}
									} else {
										bgSetting = FunAppSetting.BG_NON;
									}
									updateStr = "update " + AppSettingTable.TABLENAME + " set "
											+ AppSettingTable.BACKGROUNDVISIABLE + " = '"
											+ bgSetting + "'" + " where "
											+ AppSettingTable.THEMEPACKAGENAME + " = '" + pkgName
											+ "'";
									db.execSQL(updateStr);
								} else if (visible.contains("1")) {
									// 已经设置过壁纸
									valueIndex = cursor
											.getColumnIndex(AppSettingTable.BACKGROUNDPICPATH);
									path = cursor.getString(valueIndex);
									if (path != null) {
										if (path.contains(DeskSettingConstants.BGSETTINGTAG)) {
											// 图片来自于GO主题包
											bgSetting = FunAppSetting.BG_GO_THEME;
										} else if (path.contains(LauncherEnv.Path.SDCARD)) {
											// 图片来自于用户设置
											bgSetting = FunAppSetting.BG_CUSTOM;
										} else {
											bgSetting = FunAppSetting.BG_NON;
										}
									} else {
										bgSetting = FunAppSetting.BG_NON;
									}
									updateStr = "update " + AppSettingTable.TABLENAME + " set "
											+ AppSettingTable.BACKGROUNDVISIABLE + " = '"
											+ bgSetting + "'" + " where "
											+ AppSettingTable.THEMEPACKAGENAME + " = '" + pkgName
											+ "'";
									db.execSQL(updateStr);
								}
							}
						} while (cursor.moveToNext());
					}
				} finally {
					cursor.close();
				}
				// 修改高质量绘图开关字段的值，改为false；
				ContentValues contentValues = new ContentValues();
				contentValues.put(ThemeTable.HIGHQUALITYDRAWING, false);
				db.update(ThemeTable.TABLENAME, contentValues, null, null);

				// 操作设置里面桌面菜单字段的值，改为true；
				ContentValues ctValues = new ContentValues();
				ctValues.put(DeskMenuTable.ENABLE, true);
				db.update(DeskMenuTable.TABLENAME, ctValues, null, null);

				// screensettingtable加入锁屏字段：lockscreen
				tableName = ScreenSettingTable.TABLENAME;
				String columnName = ScreenSettingTable.LOCK_SCREEN;
				addColumnToTable(db, tableName, columnName, TYPE_NUMERIC, String.valueOf(0)); // 默认不锁屏

				db.setTransactionSuccessful();
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;

	}

	/**
	 * 从第19版升到第20版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB19To20(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级DesktopTable表，增加字段。
			 */
			// 增加字段
			String tableName = DesktopTable.TABLENAME;
			String columnName = DesktopTable.THEMEICONPACKAGE;
			addColumnToTable(db, tableName, columnName, TYPE_TEXT, String.valueOf("error")); // 保证不修改当前主题

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第20版升到第21版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB20To21(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级ThemeTable表，增加字段。
			 */
			// 增加字段
			String tableName = ThemeTable.TABLENAME;
			// 是否为第一次运行GO桌面的默认标识改为true -by Yugi 2012.7.28
			final int value = isNewDB() ? 1 : 0;
			addColumnToTable(db, tableName, ThemeTable.FIRSTRUN, TYPE_NUMERIC,
					String.valueOf(value));
			addColumnToTable(db, tableName, ThemeTable.TIPCANCELDEFAULTDESK, TYPE_NUMERIC,
					String.valueOf(1));

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第21版升到第22版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB21To22(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级ScreenSetting表，增加页面指示器类型字段。
			 */
			// 增加字段
			String tableName = ScreenSettingTable.TABLENAME;
			addColumnToTable(db, tableName, ScreenSettingTable.INDICATOR_SHOWMODE, TYPE_NUMERIC,
					String.valueOf(0));

			// /**
			// * 取消掉了指示器显示/隐藏设置，设置该字段为true
			// */
			// String updateStr = "update " + ScreenSettingTable.TABLENAME
			// + " set " + ScreenSettingTable.ENABLE + " = '" + 1 + "'";
			// db.execSQL(updateStr);
			//
			/**
			 * 升级gowidget表，添加themeid字段
			 */
			addColumnToTable(db, GoWidgetTable.TABLENAME, GoWidgetTable.THEMEID, TYPE_NUMERIC,
					String.valueOf(-1));

			/**
			 * 修改gowidget表的type字段，任务管理器、sms
			 */
			// update gowidget set type = 1 where
			// package='com.gau.go.launcherex' and layout='task_running_4_1'
			String sql = "update " + GoWidgetTable.TABLENAME + " set " + GoWidgetTable.TYPE
					+ " = 1 where " + GoWidgetTable.PACKAGE + " ='" + PackageName.PACKAGE_NAME
					+ "' and " + GoWidgetTable.LAYOUT + "='task_running_4_1'";
			db.execSQL(sql);

			// update gowidget set type = 1 where
			// package='com.gau.go.launcherex.gowidget.smswidget' and
			// layout='sms_4_4'
			sql = "update " + GoWidgetTable.TABLENAME + " set " + GoWidgetTable.TYPE
					+ " = 1 where " + GoWidgetTable.PACKAGE
					+ "='com.gau.go.launcherex.gowidget.smswidget' and " + GoWidgetTable.LAYOUT
					+ "='sms_4_4'";
			db.execSQL(sql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第22版升到第23版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB22To23(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级数据库，避免已经添加的gostore换不了皮肤
			 */
			// update gowidget set type = 0 where
			// package='com.gau.go.launcherex' and layout='gostorewidget'
			String sql = "update " + GoWidgetTable.TABLENAME + " set " + GoWidgetTable.TYPE
					+ " = 0 where " + GoWidgetTable.PACKAGE + " ='" + PackageName.PACKAGE_NAME
					+ "' and " + GoWidgetTable.LAYOUT + "='gostorewidget'";
			db.execSQL(sql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第23版升到第24版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB23To24(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级AppSettingTable表，增加字段。
			 */
			// 增加字段
			String tableName = AppSettingTable.TABLENAME;
			String columnName = AppSettingTable.SHOW_TAB_ROW;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC,
					String.valueOf(AppSettingDefault.SHOW_TAB_ROW));

			// facebook statistics
			addColumnToTable(db, ShortcutSettingTable.TABLENAME,
					ShortcutSettingTable.AUTOMISSFACEBOOKSTATICS, TYPE_NUMERIC, String.valueOf(0));

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第24版升到第25版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB24To25(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级ShortcutTable表，增加字段。
			 */
			String tableName = ShortcutTable.TABLENAME;
			String columnName = ShortcutTable.ITEMTYPE;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC,
					String.valueOf(IItemType.ITEM_TYPE_APPLICATION));

			// 通过intent查找区分原来数据是1：application; 2: shortcut
			String columns[] = { ShortcutTable.INTENT, ShortcutTable.MINDEX };

			Cursor cursor = db.query(tableName, columns, null, null, null, null, null);

			Intent intent_dial = AppIdentifier.createSelfDialIntent(mContext);
			Intent intent_contacts = AppIdentifier.createSelfContactIntent(mContext);
			Intent intent_appdrawer = AppIdentifier.createAppdrawerIntent();
			Intent intent_sms = AppIdentifier.createSelfMessageIntent();
			Intent intent_browser = AppIdentifier.createSelfBrowseIntent(mContext
					.getPackageManager());
			if (cursor != null) {
				try {
					if (cursor.moveToFirst()) {
						do {
							int type = IItemType.ITEM_TYPE_APPLICATION;

							int intentColumn = cursor.getColumnIndex(ShortcutTable.INTENT);
							String intentString = cursor.getString(intentColumn);
							int indexColumn = cursor.getColumnIndex(ShortcutTable.MINDEX);
							int index = cursor.getInt(indexColumn);

							Intent intent = ConvertUtils.stringToIntent(intentString);
							// 拨号
							if (ConvertUtils.selfIntentCompare(intent, intent_dial)
							// 联系人
									|| ConvertUtils.selfIntentCompare(intent, intent_contacts)
									// 功能表
									|| ConvertUtils.selfIntentCompare(intent, intent_appdrawer)
									// 信息
									|| ConvertUtils.selfIntentCompare(intent, intent_sms)
									// +号
									|| null == intent
									// 空白
									|| ICustomAction.ACTION_BLANK.equals(intent.getAction())) {
								type = IItemType.ITEM_TYPE_SHORTCUT;
							} else if (ConvertUtils.selfIntentCompare(intent, intent_browser)) {
								// 浏览器
								type = IItemType.ITEM_TYPE_APPLICATION;
							} else {
								boolean findInApptable = false;

								String columns_appdrawer[] = { AppTable.INTENT };

								Cursor cursor_appdrawer = db.query(AppTable.TABLENAME,
										columns_appdrawer, null, null, null, null, null);
								if (cursor_appdrawer != null) {
									if (cursor_appdrawer.moveToFirst()) {
										int intentIndex_appdrawer = 0;
										String intentString_appdrawer = null;
										do {
											intentIndex_appdrawer = cursor_appdrawer
													.getColumnIndex(AppTable.INTENT);
											intentString_appdrawer = cursor_appdrawer
													.getString(intentIndex_appdrawer);
											if (null != intentString
													&& intentString.equals(intentString_appdrawer)) {
												// 在功能表找到，判断是application
												type = IItemType.ITEM_TYPE_APPLICATION;
												findInApptable = true;
												break;
											}
										} while (cursor_appdrawer.moveToNext());
									}
									cursor_appdrawer.close();
								}

								if (!findInApptable) {
									// 没在功能表找到，判断是shortcut
									type = IItemType.ITEM_TYPE_SHORTCUT;
								}
							}

							String sql = "update " + ShortcutTable.TABLENAME + " set "
									+ ShortcutTable.ITEMTYPE + " = " + type + " where "
									+ ShortcutTable.MINDEX + " = " + index;

							db.execSQL(sql);
						} while (cursor.moveToNext());
					}
				} finally {
					cursor.close();
				}
			}

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			Log.i("DatabaseHelper", "onUpgradeDB24To25 has exception = " + e.getMessage());
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第25版升到第26版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB25To26(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级ShortcutTable表，增加字段:usertitle,用于保存在桌面的组件拖动到dock后的名称
			 */
			String tableName = ShortcutTable.TABLENAME;
			String columnName = ShortcutTable.USERTITLE;
			addColumnToTable(db, tableName, columnName, TYPE_TEXT, null);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第26版升到第27版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB26To27(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级AppSettingTable表，增加字段。
			 */
			// 增加字段
			addColumnToTable(db, AppSettingTable.TABLENAME, AppSettingTable.VERTICAL_SCROLL_EFFECT,
					TYPE_NUMERIC, String.valueOf(AppSettingDefault.VERTICAL_SCROLL_EFFECT));

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第27版升到第28版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB27To28(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级GravityTable表，增加字段。
			 */
			// 增加字段
			String defaultValue = "0";
			addColumnToTable(db, GravityTable.TABLENAME, GravityTable.LANDSCAPE, TYPE_NUMERIC,
					defaultValue);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第28版升到第29版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB28To29(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级gowidget表，添加prototype字段
			 */
			addColumnToTable(db, GoWidgetTable.TABLENAME, GoWidgetTable.PROTOTYPE, TYPE_NUMERIC,
					String.valueOf(GoWidgetBaseInfo.PROTOTYPE_NORMAL));

			final String packageName = mContext.getPackageName();
			// update gowidget set prototype = 0 where package =
			// 'com.gau.go.launcherex' and layout like 'task_running%'
			String sql = "update " + GoWidgetTable.TABLENAME + " set " + GoWidgetTable.PROTOTYPE
					+ " = " + GoWidgetBaseInfo.PROTOTYPE_TASKMAN + " where "
					+ GoWidgetTable.PACKAGE + " ='" + packageName + "' and " + GoWidgetTable.LAYOUT
					+ " LIKE 'task_running%'";
			db.execSQL(sql);

			// update gowidget set prototype = 0 where package =
			// 'com.gau.go.launcherex' and layout = 'gostorewidget'
			sql = "update " + GoWidgetTable.TABLENAME + " set " + GoWidgetTable.PROTOTYPE + " = "
					+ GoWidgetBaseInfo.PROTOTYPE_GOSTORE + " where " + GoWidgetTable.PACKAGE
					+ " ='" + packageName + "' and " + GoWidgetTable.LAYOUT + " = 'gostorewidget'";
			db.execSQL(sql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第27版升到第28版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB29To30(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级ShortcutTable 增加字段REFID，功能表文件夹外键
			 */
			addColumnToTable(db, ShortcutTable.TABLENAME, ShortcutTable.REFID, TYPE_NUMERIC, null);

			/**
			 * 升级GravityTable表，删除原有字段,使用新字段ORIENTATIONTYPE。
			 */
			// 增加字段

			addColumnToTable(db, GravityTable.TABLENAME, GravityTable.ORIENTATIONTYPE,
					TYPE_NUMERIC, String.valueOf(0));
			Cursor cr = db.query(GravityTable.TABLENAME, null, null, null, null, null, null, null);
			int enable = 0;
			int landscape = 0;
			if (cr.moveToFirst()) {
				int enableIndex = cr.getColumnIndex(GravityTable.ENABLE);

				if (-1 != enableIndex) {
					enable = cr.getInt(enableIndex);
				}
				int landscapeIndex = cr.getColumnIndex(GravityTable.LANDSCAPE);

				if (-1 != landscapeIndex) {
					landscape = cr.getInt(landscapeIndex);
				}
				cr.close();
			}
			int orgType = 1;
			if (landscape == 1)// 横屏模式
			{
				orgType = 2;
			} else if (enable == 1)// 自动旋转
			{
				orgType = 0;
			}
			String sql = "update " + GravityTable.TABLENAME + " set "
					+ GravityTable.ORIENTATIONTYPE + " = " + orgType;
			db.execSQL(sql);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 从第30版升到第31版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB30To31(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级DesktopTable表，增加“自适应”和“图标文字透明化”字段。
			 */
			String tableName = DesktopTable.TABLENAME;
			boolean temp = false;
			// 增加自适应字段
			String columnName = DesktopTable.AUTOFITITEMS;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC,
					String.valueOf(ConvertUtils.boolean2int(temp)));

			// 先获取原表中的style
			String projection[] = { DesktopTable.SHOWTITLE };
			int titleStyle = 0;
			Cursor cursor = db.query(tableName, projection, null, null, null, null, null);
			try {
				if (cursor != null && cursor.moveToFirst()) {
					int mIndexColumnIndex = cursor.getColumnIndex(DesktopTable.SHOWTITLE);
					final boolean showTitle = ConvertUtils.int2boolean(cursor
							.getInt(mIndexColumnIndex));
					titleStyle = showTitle ? 0 : 2;
				}
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
			// 增加字段
			columnName = DesktopTable.TITLESTYLE;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC, String.valueOf(titleStyle));

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第31版升到第32版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB31To32(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级DesktopTable表
			 */
			String tableName = DesktopTable.TABLENAME;
			boolean temp = false;
			// 增加字段
			String columnName = DesktopTable.CUSTOMAPPBG;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC,
					String.valueOf(ConvertUtils.boolean2int(temp)));

			columnName = DesktopTable.PRESSCOLOR;
			int color = 0xffff9900;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC, String.valueOf(color));

			columnName = DesktopTable.FOCUSCOLOR;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC, String.valueOf(color));

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第32版升到第33版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB32To33(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级AppSettingTable表
			 */
			String tableName = AppSettingTable.TABLENAME;
			boolean temp = false;
			// 增加字段
			String columnName = AppSettingTable.SHOW_SEARCH;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC,
					String.valueOf(ConvertUtils.boolean2int(temp)));

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第33版升到第34版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB33To34(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			String tableName = DesktopTable.TABLENAME;
			boolean temp = false;
			// 增加自适应字段
			String columnName = DesktopTable.LARGEICON;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC,
					String.valueOf(ConvertUtils.boolean2int(temp)));

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第34版升到第35版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB34To35(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// desktop表
			String tableName = DesktopTable.TABLENAME;
			String columnName = DesktopTable.FOLDERTHEMEICONPACKAGE;
			addColumnToTable(db, tableName, columnName, TYPE_TEXT, PackageName.PACKAGE_NAME);

			columnName = DesktopTable.GGMENUTHEMEICONPACKAGE;
			addColumnToTable(db, tableName, columnName, TYPE_TEXT, PackageName.PACKAGE_NAME);

			String curTheme = null;
			String projection[] = { ConfigTable.THEMENAME };
			Cursor cursor = db.query(ConfigTable.TABLENAME, projection, null, null, null, null,
					null);
			try {
				if (cursor != null && cursor.moveToFirst()) {
					int mIndexColumnIndex = cursor.getColumnIndex(ConfigTable.THEMENAME);
					curTheme = cursor.getString(mIndexColumnIndex);
				}
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}

			if (curTheme == null) {
				curTheme = IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
			}
			String appTableSql = "update " + tableName + " set "
					+ DesktopTable.FOLDERTHEMEICONPACKAGE + " = '" + curTheme + "' , "
					+ DesktopTable.GGMENUTHEMEICONPACKAGE + " = '" + curTheme + "'";
			db.execSQL(appTableSql);

			// screensettingtable表
			columnName = ScreenSettingTable.INDICATORSTYLEPACKAGE;
			addColumnToTable(db, ScreenSettingTable.TABLENAME, columnName, TYPE_TEXT,
					ScreenIndicator.SHOWMODE_NORMAL);

			int indicatorMode = 0;
			cursor = db.query(ScreenSettingTable.TABLENAME,
					new String[] { ScreenSettingTable.INDICATOR_SHOWMODE }, null, null, null, null,
					null);
			try {
				if (cursor != null && cursor.moveToFirst()) {
					int mIndexColumnIndex = cursor
							.getColumnIndex(ScreenSettingTable.INDICATOR_SHOWMODE);
					indicatorMode = cursor.getInt(mIndexColumnIndex);
				}
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}

			if (indicatorMode == 1) {
				curTheme = ScreenIndicator.SHOWMODE_NUMERIC;
			}

			appTableSql = "update " + ScreenSettingTable.TABLENAME + " set "
					+ ScreenSettingTable.INDICATORSTYLEPACKAGE + " = '" + curTheme + "'";
			db.execSQL(appTableSql);

			// appsetting 表
			tableName = AppSettingTable.TABLENAME;
			columnName = AppSettingTable.TAB_HOME_BG;
			addColumnToTable(db, tableName, columnName, TYPE_TEXT, PackageName.PACKAGE_NAME);

			columnName = AppSettingTable.INDICATOR_STYLE;
			addColumnToTable(db, tableName, columnName, TYPE_TEXT, ScreenIndicator.SHOWMODE_NORMAL);

			appTableSql = "update " + tableName + " set " + AppSettingTable.TAB_HOME_BG + " = "
					+ AppSettingTable.THEMEPACKAGENAME + " , " + AppSettingTable.INDICATOR_STYLE
					+ " = " + AppSettingTable.THEMEPACKAGENAME;
			db.execSQL(appTableSql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第35版升到第36版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB35To36(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			String curTheme = null;
			String projection[] = { ConfigTable.THEMENAME };
			Cursor cursor = db.query(ConfigTable.TABLENAME, projection, null, null, null, null,
					null);
			try {
				if (cursor != null && cursor.moveToFirst()) {
					int mIndexColumnIndex = cursor.getColumnIndex(ConfigTable.THEMENAME);
					curTheme = cursor.getString(mIndexColumnIndex);

				}
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}

			if (curTheme == null) {
				curTheme = IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
			}

			// 桌面个性配置表
			if (!isExistTable(db, ScreenStyleConfigTable.TABLENAME)) {
				db.execSQL(ScreenStyleConfigTable.CREATETABLESQL);
			}

			// screensettingtable表
			String columnName = ScreenSettingTable.INDICATORSTYLEPACKAGE;
			addColumnToTable(db, ScreenSettingTable.TABLENAME, columnName, TYPE_TEXT,
					ScreenIndicator.SHOWMODE_NORMAL);

			// 获得原来指示器样式
			int indicatorMode = 0;
			cursor = db.query(ScreenSettingTable.TABLENAME,
					new String[] { ScreenSettingTable.INDICATOR_SHOWMODE }, null, null, null, null,
					null);
			try {
				if (cursor != null && cursor.moveToFirst()) {
					int mIndexColumnIndex = cursor
							.getColumnIndex(ScreenSettingTable.INDICATOR_SHOWMODE);
					indicatorMode = cursor.getInt(mIndexColumnIndex);
				}
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
			String indicatorPackage = curTheme;
			if (indicatorMode == 1) {
				indicatorPackage = ScreenIndicator.SHOWMODE_NUMERIC;
			}

			// 获得原来桌面图标样式
			String iconPackage = null;
			cursor = db.query(DesktopTable.TABLENAME,
					new String[] { DesktopTable.THEMEICONPACKAGE }, null, null, null, null, null);
			try {
				if (cursor != null && cursor.moveToFirst()) {
					int mIndexColumnIndex = cursor.getColumnIndex(DesktopTable.THEMEICONPACKAGE);
					iconPackage = cursor.getString(mIndexColumnIndex);
				}
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
			if (iconPackage == null) {
				iconPackage = curTheme;
			}
			String INSERTDEFAULTVALUES = "insert into " + ScreenStyleConfigTable.TABLENAME + "("
					+ ScreenStyleConfigTable.THEMEPACKAGE + ", "
					+ ScreenStyleConfigTable.ICONSTYLEPACKAGE + ", "
					+ ScreenStyleConfigTable.FOLDERSTYLEPACKAGE + ", "
					+ ScreenStyleConfigTable.GGMENUPACKAGE + ", "
					+ ScreenStyleConfigTable.INDICATOR + ")" + " values(" + "'" + curTheme + "','"
					+ iconPackage + "','" + curTheme + "','" + curTheme + "','" + indicatorPackage
					+ "')";

			db.execSQL(INSERTDEFAULTVALUES);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	public boolean onUpgradeDB36To37(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级AppSettingTable表，增加字段。
			 */
			// 增加字段
			// sinaweibo statistics
			addColumnToTable(db, ShortcutSettingTable.TABLENAME,
					ShortcutSettingTable.AUTOMISSSINAWEIBOSTATICS, TYPE_NUMERIC, String.valueOf(0));

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第37版升到第38版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB37To38(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			String tableName = DesktopTable.TABLENAME;
			// 增加自适应字段
			String columnName = DesktopTable.ICONSIZE;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC, String.valueOf(0));

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第38版升到第39版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB38To39(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			String tableName = DesktopTable.TABLENAME;
			String columnName = DesktopTable.SHOWICONBASE;
			boolean temp = true;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC,
					String.valueOf(ConvertUtils.boolean2int(temp)));

			columnName = DesktopTable.FONTSIZE;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC, String.valueOf(0));

			columnName = DesktopTable.CUSTOMTITLECOLOR;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC,
					String.valueOf(ConvertUtils.boolean2int(false)));

			columnName = DesktopTable.TITLECOLOR;
			int color = 0xffffffff;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC, String.valueOf(color));

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第39版升到第40版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB39To40(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			String tableName = AppSettingTable.TABLENAME;
			String columnName = AppSettingTable.ROWNUM;
			addColumnToTable(db, tableName, columnName, TYPE_TEXT, String.valueOf(4));

			columnName = AppSettingTable.COLNUM;
			addColumnToTable(db, tableName, columnName, TYPE_TEXT, String.valueOf(4));

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第40版升到第41版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB40To41(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级AppSettingTable表
			 */
			String tableName = AppSettingTable.TABLENAME;
			boolean temp = true;
			// 增加字段
			String columnName = AppSettingTable.PROUPDATEAPP;
			addColumnToTable(db, tableName, columnName, TYPE_NUMERIC,
					String.valueOf(ConvertUtils.boolean2int(temp)));

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第41版升到第42版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB41To42(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级DynamicEffectTable表
			 */
			String tableName = DynamicEffectTable.TABLENAME;
			// 增加字段
			addColumnToTable(db, tableName, DynamicEffectTable.EFFECTORRANDOMITEMS, TYPE_TEXT,
					"-1;");
			/**
			 * 升级AppSettingTable表
			 */
			tableName = AppSettingTable.TABLENAME;
			// 增加字段
			addColumnToTable(db, tableName, AppSettingTable.CUSTOMINOUTEFFECTITEMS, TYPE_TEXT,
					"-1;");
			/**
			 * 升级AppSettingTable表
			 */
			// 增加字段
			addColumnToTable(db, tableName, AppSettingTable.CUSTOMICONEFFECT, TYPE_TEXT, "-1;");

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	public boolean onUpgradeDB42To43(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级GestureTable
			 */
			addColumnToTable(db, GestureTable.TABLENAME, GestureTable.GOSHORTCUTITEM, TYPE_NUMERIC,
					null);
			String updateSql = "update " + GestureTable.TABLENAME + " set "
					+ GestureTable.GOSHORTCUTITEM + " = " + GestureTable.GESTURACTION + ","
					+ GestureTable.GESTURACTION + " = " + -1 + " where "
					+ GestureTable.GESTURACTION + " not in (0,8,1)";
			db.execSQL(updateSql);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第43版升到第44版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB43To44(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级恢复手势错误的数据到默认
			 */
			String updateSql = "update " + GestureTable.TABLENAME + " set "
					+ GestureTable.GOSHORTCUTITEM + " = "
					+ GlobalSetConfig.GESTURE_SHOW_MAIN_SCREEN + " where " + GestureTable.GESTUREID
					+ " = " + GestureSettingInfo.GESTURE_HOME_ID + " AND "
					+ GestureTable.GOSHORTCUTITEM + " = " + "-1";
			db.execSQL(updateSql);

			updateSql = "update " + GestureTable.TABLENAME + " set " + GestureTable.GOSHORTCUTITEM
					+ " = " + GlobalSetConfig.GESTURE_SHOW_PREVIEW + " where "
					+ GestureTable.GESTUREID + " = " + GestureSettingInfo.GESTURE_UP_ID + " AND "
					+ GestureTable.GOSHORTCUTITEM + " = " + "-1";
			db.execSQL(updateSql);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第44版升到第45版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB44To45(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			if (!isExistTable(db, MessageCenterTable.TABLENAME)) {
				db.execSQL(MessageCenterTable.CREATETABLESQL);
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第45版升到第46版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB45To46(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, ConfigTable.TABLENAME, ConfigTable.VERSIONCODE, TYPE_NUMERIC,
					String.valueOf(LauncherEnv.UNKNOWN_VERSIONCODE));
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第46版升到第47版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB46To47(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, ScreenSettingTable.TABLENAME,
					ScreenSettingTable.INDICATORPOSITION, TYPE_TEXT,
					ScreenIndicator.INDICRATOR_ON_TOP);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第47版升到第48版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB47To48(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, ShortcutTable.TABLENAME, ShortcutTable.ROWSID, TYPE_NUMERIC, "-1");

			String updateSql = null;
			// NOTE: shortcutTable加字段rowid,写入升级数值
			int index = 0;
			for (int i = 0; i < DockUtil.TOTAL_ROWS; i++) {
				for (int j = 0; j < DockUtil.ICON_COUNT_IN_A_ROW; j++) {
					index = i * DockUtil.ICON_COUNT_IN_A_ROW + j;
					updateSql = "update " + ShortcutTable.TABLENAME + " set "
							+ ShortcutTable.ROWSID + " = " + i + ", " + ShortcutTable.MINDEX
							+ " = " + j + " where " + ShortcutTable.MINDEX + " = " + index;
					db.execSQL(updateSql);
				}
			}

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第48版升到第49版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB48To49(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级AppSettingTable表
			 */
			String tableName = AppSettingTable.TABLENAME;
			// 增加字段
			addColumnToTable(db, tableName, AppSettingTable.SHOW_HOME_KEY_ONLY, TYPE_TEXT, "0");

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第49版升到第50版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB49To50(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级ThemeTable表
			 */
			String tableName = ThemeTable.TABLENAME;
			String defaultValue = String.valueOf(ConvertUtils.boolean2int(false));
			// 增加字段
			addColumnToTable(db, tableName, ThemeTable.TRANSPARENTSTATUSBAR, TYPE_NUMERIC,
					defaultValue);

			/**
			 * 应用管理，新增忽略更新应用表
			 */
			if (!isExistTable(db, NoPromptUpdateAppTable.TABLENAME)) {
				db.execSQL(NoPromptUpdateAppTable.CREATETABLESQL);
			}

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第50版升到第51版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB50To51(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级ShortcutTable表,加字段：autofit,赋默认值
			 */
			String[] projection = { ShortcutTable.MINDEX };
			Cursor cursor = null;
			int count = 0;
			try {
				cursor = db
						.query(ShortcutTable.TABLENAME, projection, null, null, null, null, null);

				if (cursor != null) { // 去掉DOCK条隐藏手势
					count = cursor.getCount();
					if (cursor.moveToFirst()) {
						Intent intentNone = new Intent(ICustomAction.ACTION_NONE);
						String intentNoneString = ConvertUtils.intentToString(intentNone);

						Intent intentDock = new Intent(ICustomAction.ACTION_SHOW_DOCK);
						String intentDockString = ConvertUtils.intentToString(intentDock);
						do {
							ContentValues contentValues = new ContentValues();
							contentValues.put(ShortcutTable.UPINTENT, intentNoneString);
							String whereString = ShortcutTable.UPINTENT + " = " + "'"
									+ intentDockString + "'";
							db.update(ShortcutTable.TABLENAME, contentValues, whereString, null);
						} while (cursor.moveToNext());
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
			// 如果用户原有15个图标，默认是非自适应模式；如果不是15个图标，则是自适应模式
			String defaultValue = (count == 15) ? "0" : "1";
			addColumnToTable(db, ShortcutSettingTable.TABLENAME, ShortcutSettingTable.AUTOFIT,
					TYPE_NUMERIC, defaultValue);

			if (Integer.valueOf(defaultValue) == 0) {
				// 非自适应模式,删除+号
				String selection = ShortcutTable.INTENT + " is null and " + ShortcutTable.ITEMTYPE
						+ " = 2";
				db.delete(ShortcutTable.TABLENAME, selection, null);
			}

			/**
			 * 升级ShortcutTable表,对非文件夹的pardid没赋值的数据进行赋值，因为老数据，除文件夹外，不保证这个值有赋值
			 */
			String updateSql = null;
			long time = System.currentTimeMillis();
			for (int i = 0; i < DockUtil.TOTAL_ROWS; i++) {
				for (int j = 0; j < DockUtil.ICON_COUNT_IN_A_ROW; j++) {
					long id = time + i * 5 + j;
					updateSql = "update " + ShortcutTable.TABLENAME + " set "
							+ ShortcutTable.PARTID + " = " + id + " where " + ShortcutTable.ROWSID
							+ " = " + i + " AND " + ShortcutTable.MINDEX + " = " + j + " AND "
							+ ShortcutTable.ITEMTYPE + " <> " + IItemType.ITEM_TYPE_USER_FOLDER;
					db.execSQL(updateSql);
				}
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第51版升到第52版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB51To52(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级AppSettingTable表
			 */
			String tableName = AppSettingTable.TABLENAME;
			// 增加字段
			addColumnToTable(db, tableName, AppSettingTable.SHOW_ACTION_BAR, TYPE_TEXT, "1");
			// 增加2个字段，默认图片打开方式和音频打开方式 add by yangbing 2012-06-29
			addColumnToTable(db, tableName, AppSettingTable.IMAGE_OPEN_WAY, TYPE_TEXT,
					MediaManagementOpenChooser.APP_NONE);
			addColumnToTable(db, tableName, AppSettingTable.AUDIO_OPEN_WAY, TYPE_TEXT,
					MediaManagementOpenChooser.APP_NONE);

			if (!isExistTable(db, MediaManagementHideTable.TABLENAME)) {
				db.execSQL(MediaManagementHideTable.CREATETABLESQL);
			}

			/**
			 * 添加表：diygesture
			 */
			if (!isExistTable(db, DiyGestureTable.TABLENAME)) {
				db.execSQL(DiyGestureTable.CREATETABLESQL);
			}

			/**
			 * 升级表gesture，如果双击屏幕是无响应，则改为起自定义手势
			 */
			String updateSql = "update " + GestureTable.TABLENAME + " set "
					+ GestureTable.GESTURACTION + " = " + GlobalSetConfig.GESTURE_GOSHORTCUT + ", "
					+ GestureTable.GOSHORTCUTITEM + " = 15" + " where " + GestureTable.GESTUREID
					+ " = " + GestureSettingInfo.GESTURE_DOUBLLE_CLICK_ID + " AND "
					+ GestureTable.GESTURACTION + " = " + GlobalSetConfig.GESTURE_DISABLE;
			db.execSQL(updateSql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第52版升到第53版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB52To53(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			/**
			 * 升级FolderTable表
			 */
			String tableName = FolderTable.TABLENAME;
			// 图标放进文件夹的次序
			final long timeInFolder = -1;
			// 增加字段
			addColumnToTable(db, tableName, FolderTable.TIMEINFOLDER, TYPE_NUMERIC,
					String.valueOf(timeInFolder));

			/**
			 * 升级ShortcutTable表
			 */
			tableName = ShortcutTable.TABLENAME;
			// 图标放进文件夹的次序
			int sortTpye = -1;
			// 增加字段
			addColumnToTable(db, tableName, ShortcutTable.SORTTYPE, TYPE_NUMERIC,
					String.valueOf(sortTpye));

			/**
			 * 升级PartToScreenTable表
			 */
			tableName = PartToScreenTable.TABLENAME;
			// 图标放进文件夹的次序
			sortTpye = -1;
			// 增加字段
			addColumnToTable(db, tableName, PartToScreenTable.SORTTYPE, TYPE_NUMERIC,
					String.valueOf(sortTpye));

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	public boolean onUpgradeDB53To54(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// 加假数据，用于判断dock条是否需要初始化。
			String insertDefaultValues = "insert into " + ShortcutTable.TABLENAME + "("
					+ ShortcutTable.ROWSID + ")" + " values (-1)";
			db.execSQL(insertDefaultValues);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;

	}

	/**
	 * 从第54版升到第55版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB54To55(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			Intent dial = AppIdentifier.createSelfDialIntent(ApplicationProxy.getContext());
			Intent contacts = AppIdentifier.createSelfContactIntent(ApplicationProxy.getContext());
			Intent appdraw = AppIdentifier.createAppdrawerIntent();
			Intent sms = AppIdentifier.createSelfMessageIntent();
			Intent sysbrowser = AppIdentifier.getSysBrowserIntent();
			Intent googleuri = AppIdentifier.getGoogleUriIntent();

			String dialStr = ConvertUtils.intentToString(dial);
			String contactsStr = ConvertUtils.intentToString(contacts);
			String appdrawStr = ConvertUtils.intentToString(appdraw);
			String smsStr = ConvertUtils.intentToString(sms);
			String sysbrowserStr = ConvertUtils.intentToString(sysbrowser);
			String googleuriStr = ConvertUtils.intentToString(googleuri);

			String updateSql = null;
			// 对dock5个特殊图标升级
			// 1:dock 改dock浏览器itemtype
			updateSql = "update " + ShortcutTable.TABLENAME + " set " + ShortcutTable.ITEMTYPE
					+ " = " + IItemType.ITEM_TYPE_SHORTCUT + " where " + ShortcutTable.INTENT
					+ " = " + "'" + sysbrowserStr + "'" + " AND " + ShortcutTable.ITEMTYPE + " <> "
					+ IItemType.ITEM_TYPE_USER_FOLDER + " AND " + ShortcutTable.USERICONTYPE
					+ " <> " + ImagePreviewResultType.TYPE_DEFAULT;
			db.execSQL(updateSql);

			updateSql = "update " + ShortcutTable.TABLENAME + " set " + ShortcutTable.ITEMTYPE
					+ " = " + IItemType.ITEM_TYPE_SHORTCUT + " where " + ShortcutTable.INTENT
					+ " = " + "'" + googleuriStr + "'" + " AND " + ShortcutTable.ITEMTYPE + " <> "
					+ IItemType.ITEM_TYPE_USER_FOLDER;
			db.execSQL(updateSql);

			// shortcuttable 加字段{@link #ShortcutTable.ICONTYPE}
			addColumnToTable(db, ShortcutTable.TABLENAME, ShortcutTable.ICONTYPE, TYPE_NUMERIC,
					String.valueOf(ImagePreviewResultType.TYPE_DEFAULT));
			updateSql = "update " + ShortcutTable.TABLENAME + " set " + ShortcutTable.ICONTYPE
					+ " = " + ShortcutTable.USERICONTYPE;
			db.execSQL(updateSql);
			// dock 改icon
			// 拨号
			updateSql = "update " + ShortcutTable.TABLENAME + " set " + ShortcutTable.ICONTYPE
					+ " = " + ImagePreviewResultType.TYPE_DEFAULT + " where "
					+ ShortcutTable.INTENT + " = " + "'" + dialStr + "'" + " AND "
					+ ShortcutTable.ICONTYPE + " = " + ImagePreviewResultType.TYPE_PACKAGE_RESOURCE
					+ " AND " + ShortcutTable.USERICONPACKAGE + " = " + "'"
					+ IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "'" + " AND " + "("
					+ ShortcutTable.USEPACKAGE + " is null or " + ShortcutTable.USEPACKAGE + " = "
					+ "'" + IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "'" + ")" + " AND "
					+ ShortcutTable.USERICONPATH + " = " + "'shortcut_0_0_phone'";
			db.execSQL(updateSql);

			// 联系人
			updateSql = "update " + ShortcutTable.TABLENAME + " set " + ShortcutTable.ICONTYPE
					+ " = " + ImagePreviewResultType.TYPE_DEFAULT + " where "
					+ ShortcutTable.INTENT + " = " + "'" + contactsStr + "'" + " AND "
					+ ShortcutTable.ICONTYPE + " = " + ImagePreviewResultType.TYPE_PACKAGE_RESOURCE
					+ " AND " + ShortcutTable.USERICONPACKAGE + " = " + "'"
					+ IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "'" + " AND " + "("
					+ ShortcutTable.USEPACKAGE + " is null or " + ShortcutTable.USEPACKAGE + " = "
					+ "'" + IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "'" + ")" + " AND "
					+ ShortcutTable.USERICONPATH + " = " + "'shortcut_0_1_contacts'";
			db.execSQL(updateSql);

			// 功能表
			updateSql = "update " + ShortcutTable.TABLENAME + " set " + ShortcutTable.ICONTYPE
					+ " = " + ImagePreviewResultType.TYPE_DEFAULT + " where "
					+ ShortcutTable.INTENT + " = " + "'" + appdrawStr + "'" + " AND "
					+ ShortcutTable.ICONTYPE + " = " + ImagePreviewResultType.TYPE_PACKAGE_RESOURCE
					+ " AND " + ShortcutTable.USERICONPACKAGE + " = " + "'"
					+ IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "'" + " AND " + "("
					+ ShortcutTable.USEPACKAGE + " is null or " + ShortcutTable.USEPACKAGE + " = "
					+ "'" + IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "'" + ")" + " AND "
					+ ShortcutTable.USERICONPATH + " = " + "'shortcut_0_2_funclist'";
			db.execSQL(updateSql);

			// 短信
			updateSql = "update " + ShortcutTable.TABLENAME + " set " + ShortcutTable.ICONTYPE
					+ " = " + ImagePreviewResultType.TYPE_DEFAULT + " where "
					+ ShortcutTable.INTENT + " = " + "'" + smsStr + "'" + " AND "
					+ ShortcutTable.ICONTYPE + " = " + ImagePreviewResultType.TYPE_PACKAGE_RESOURCE
					+ " AND " + ShortcutTable.USERICONPACKAGE + " = " + "'"
					+ IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "'" + " AND " + "("
					+ ShortcutTable.USEPACKAGE + " is null or " + ShortcutTable.USEPACKAGE + " = "
					+ "'" + IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "'" + ")" + " AND "
					+ ShortcutTable.USERICONPATH + " = " + "'shortcut_0_3_sms'";
			db.execSQL(updateSql);

			// sysbrowser浏览器
			updateSql = "update " + ShortcutTable.TABLENAME + " set " + ShortcutTable.ICONTYPE
					+ " = " + ImagePreviewResultType.TYPE_DEFAULT + " where "
					+ ShortcutTable.INTENT + " = " + "'" + sysbrowserStr + "'" + " AND "
					+ ShortcutTable.ICONTYPE + " = " + ImagePreviewResultType.TYPE_PACKAGE_RESOURCE
					+ " AND " + ShortcutTable.USERICONPACKAGE + " = " + "'"
					+ IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "'" + " AND " + "("
					+ ShortcutTable.USEPACKAGE + " is null or " + ShortcutTable.USEPACKAGE + " = "
					+ "'" + IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "'" + ")" + " AND "
					+ ShortcutTable.USERICONPATH + " = " + "'shortcut_0_4_browser'";
			db.execSQL(updateSql);

			// google　uri浏览器
			updateSql = "update " + ShortcutTable.TABLENAME + " set " + ShortcutTable.ICONTYPE
					+ " = " + ImagePreviewResultType.TYPE_DEFAULT + " where "
					+ ShortcutTable.INTENT + " = " + "'" + googleuriStr + "'" + " AND "
					+ ShortcutTable.ICONTYPE + " = " + ImagePreviewResultType.TYPE_PACKAGE_RESOURCE
					+ " AND " + ShortcutTable.USERICONPACKAGE + " = " + "'"
					+ IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "'" + " AND " + "("
					+ ShortcutTable.USEPACKAGE + " is null or " + ShortcutTable.USEPACKAGE + " = "
					+ "'" + IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "'" + ")" + " AND "
					+ ShortcutTable.USERICONPATH + " = " + "'shortcut_0_4_browser'";
			db.execSQL(updateSql);

			// 2:parttoscreen 改dock浏览器itemtype
			updateSql = "update " + PartToScreenTable.TABLENAME + " set "
					+ PartToScreenTable.ITEMTYPE + " = " + IItemType.ITEM_TYPE_SHORTCUT + " where "
					+ PartToScreenTable.INTENT + " = " + "'" + sysbrowserStr + "'" + " AND "
					+ PartToScreenTable.USERICONTYPE + " <> " + ImagePreviewResultType.TYPE_DEFAULT;
			db.execSQL(updateSql);

			updateSql = "update " + PartToScreenTable.TABLENAME + " set "
					+ PartToScreenTable.ITEMTYPE + " = " + IItemType.ITEM_TYPE_SHORTCUT + " where "
					+ PartToScreenTable.INTENT + " = " + "'" + googleuriStr + "'" + " AND "
					+ PartToScreenTable.USERICONTYPE + " <> " + ImagePreviewResultType.TYPE_DEFAULT;
			db.execSQL(updateSql);

			// 3:folder 改dock浏览器itemtype
			updateSql = "update " + FolderTable.TABLENAME + " set " + FolderTable.TYPE + " = "
					+ IItemType.ITEM_TYPE_SHORTCUT + " where " + FolderTable.INTENT + " = " + "'"
					+ sysbrowserStr + "'" + " AND " + FolderTable.USERICONTYPE + " <> "
					+ ImagePreviewResultType.TYPE_DEFAULT;
			db.execSQL(updateSql);

			updateSql = "update " + FolderTable.TABLENAME + " set " + FolderTable.TYPE + " = "
					+ IItemType.ITEM_TYPE_SHORTCUT + " where " + FolderTable.INTENT + " = " + "'"
					+ googleuriStr + "'" + " AND " + FolderTable.USERICONTYPE + " <> "
					+ ImagePreviewResultType.TYPE_DEFAULT;
			db.execSQL(updateSql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第55版升到第56版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB55To56(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			if (!isExistTable(db, AppFuncSettingTable.TABLENAME)) {
				db.execSQL(AppFuncSettingTable.CREATETABLESQL);
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 从第56版升到第57版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB56To57(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// 新建表ShortcutUnfitTable
			if (!isExistTable(db, ShortcutUnfitTable.TABLENAME)) {
				db.execSQL(ShortcutUnfitTable.CREATETABLESQL);
			}

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;

	}

	public boolean onUpgradeDB57To58(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			if (!isExistTable(db, MediaManagementPlayListTable.TABLENAME)) {
				db.execSQL(MediaManagementPlayListTable.CREATETABLESQL);
				db.execSQL(MediaManagementPlayListFileTable.CREATETABLESQL);
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;

	}

	/**
	 * 从第58版升到第59版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB58To59(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// 增加diygesture表字段：itemtype
			String table = DiyGestureTable.TABLENAME;
			String column = DiyGestureTable.TYPE;
			addColumnToTable(db, table, column, TYPE_NUMERIC, null);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;

	}

	/**
	 * 功能简述:数据库从59版升级到60版 功能详细描述: 注意:
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB59To60(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// if (!isExistTable(db, AppGameSettingTable.TABLENAME))
			// {
			// db.execSQL(AppGameSettingTable.CREATETABLESQL);
			// }
			if (!isExistTable(db, SettingTable.TABLENAME)) {
				// 插入表
				db.execSQL(SettingTable.CREATETABLESQL);
				// 插入打开app动画方式
				String table = SettingTable.TABLENAME;
				String sql;
				if (isNewDB()) {
					sql = "INSERT INTO " + table + "(" + SettingTable.APPOPENTYPE + ")"
							+ " VALUES(" + IOpenAppAnimationType.TYPE_OPEN_APP_NONE + ")";
				} else {
					sql = "INSERT INTO " + table + "(" + SettingTable.APPOPENTYPE + ")"
							+ " VALUES(" + IOpenAppAnimationType.TYPE_OPEN_APP_SMALL2BIG + ")";
				}
				try {
					db.execSQL(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 从第60版升到第61版
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB60To61(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// 1: MessageCenterTable：URL
			String table = MessageCenterTable.TABLENAME;
			String column = MessageCenterTable.URL;
			addColumnToTable(db, table, column, TYPE_TEXT, null);

			// 2: Config表加字段isversionbefore312,
			addColumnToTable(db, ConfigTable.TABLENAME, ConfigTable.ISVERSIONBEFORE312,
					TYPE_NUMERIC, "1");

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;
	}

	/**
	 * 功能简述:数据库从61版升级到62版 功能详细描述: 注意:
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB61To62(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			String table = SettingTable.TABLENAME;
			String column = SettingTable.FIRSTRUN;
			final int value = isNewDB() ? 1 : 0;
			addColumnToTable(db, table, column, TYPE_NUMERIC, String.valueOf(value));
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 合并功能表设置表到一张表格里面
	 * 
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB62To63(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();

		try {
			// 为新设置表添加一个包名字段，默认值为默认包名
			addColumnToTable(db, AppFuncSettingTable.TABLENAME, AppFuncSettingTable.PKNAME,
					TYPE_TEXT, IGoLauncherClassName.DEFAULT_THEME_PACKAGE);
			// 将旧表设置抽取到新表
			Cursor c = db.query(AppSettingTable.TABLENAME, null, null, null, null, null, null);
			AppFuncSettingTable.loadDataFromOldSettingTable(c, db);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 功能简述: 功能详细描述: 注意:
	 */
	public boolean onUpgradeDB63To64(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// 删除数据库表将会对降级的用户造成错误
			// 但由于64的版本已经发出删除数据库表的内测包
			// 发正式包之前修正，64版的数据库什么操作都不做
			// if (isExistTable(db, AppGameSettingTable.TABLENAME)){
			// String sql = "drop table " + AppGameSettingTable.TABLENAME;
			// db.execSQL(sql);
			// }
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	public boolean onUpgradeDB64To65(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// Config表加字段isversionbefore312
			addColumnToTable(db, ConfigTable.TABLENAME, ConfigTable.ISVERSIONBEFORE312,
					TYPE_NUMERIC, "0");

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 功能简述: 增加通讯统计表: 注意:
	 */
	public boolean onUpgradeDB65To66(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			if (!isExistTable(db, NotificationAppSettingTable.TABLENAME)) {
				db.execSQL(NotificationAppSettingTable.CREATETABLESQL);
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;

	}

	/**
	 * 功能简述: 	创建云安全扫描结果的表格
	 *				添加云安全设置字段
	 * 				添加是否更新过常驻内存字段
	 * 				将高端机的老用户常驻内存打开
	 * 
	 */
	public boolean onUpgradeDB66To67(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			//创建保存云安全扫描结果的表格
			if (!isExistTable(db, AllEngineCheckResultsTable.TABLENAME)) {
				db.execSQL(AllEngineCheckResultsTable.CREATETABLESQL);
			}
			if (!isExistTable(db, CheckResultTable.TABLENAME)) {
				db.execSQL(CheckResultTable.CREATETABLESQL);
			}

			addColumnToTable(db, ThemeTable.TABLENAME, ThemeTable.CLOUD_SECURITY, TYPE_NUMERIC, "1");

			addColumnToTable(db, SettingTable.TABLENAME, SettingTable.UPGRADEPEMANENTMEMORY,
					TYPE_NUMERIC, "0");
			Cursor cursor = db.query(ThemeTable.TABLENAME,
					new String[] { ThemeTable.ISPEMANENTMEMORY }, null, null, null, null, null);
			if (null != cursor) {
				cursor.moveToFirst();
				boolean noNeedToChange = ConvertUtils.int2boolean(cursor.getInt(cursor
						.getColumnIndex(ThemeTable.ISPEMANENTMEMORY)));
				// 如果常驻内存本来就是开启的就不需要修改
				if (!noNeedToChange) {
					if (ConfigurationInfo.getDeviceLevel() == ConfigurationInfo.HIGH_DEVICE) {
						ContentValues values = new ContentValues();
						values.put(ThemeTable.ISPEMANENTMEMORY, ConvertUtils.boolean2int(true));
						db.update(ThemeTable.TABLENAME, values, null, null);
						values.clear();
						values.put(SettingTable.UPGRADEPEMANENTMEMORY,
								ConvertUtils.boolean2int(true));
						db.update(SettingTable.TABLENAME, values, null, null);
					}
				}
				cursor.close();
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}

		return result;

	}

	/**
	 * <br>功能简述:消息中心增加动画重复字段
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB68To69(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.ZTIME,
					TYPE_NUMERIC, String.valueOf(60000));
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * <br>功能简述:消息中心增加是否可以关闭罩子层字段
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB69To70(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.ISCLOSED,
					TYPE_NUMERIC, String.valueOf(-1));
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.FILTER_PKGS,
					TYPE_TEXT, null);
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.CLICK_CLOSE,
					TYPE_NUMERIC, String.valueOf(-1));
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 添加通知栏全屏背景图相关字段
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB70To71(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.DYNAMIC,
					TYPE_NUMERIC, String.valueOf(-1));
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.FULL_SCREEN_ICON,
					TYPE_TEXT, null);
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.ICONPOS,
					TYPE_NUMERIC, String.valueOf(-1));
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.REMOVED,
					TYPE_NUMERIC, String.valueOf(-1));
			
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 加上白名单信息
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB71To72(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.WHITE_LIST,
					TYPE_TEXT, null);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 联合运营平台，加入wifi控制
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB72To73(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.ISWIFI,
					TYPE_NUMERIC, String.valueOf(0));
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.OUTDATE,
					TYPE_TEXT, null);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 3D widget方案调整以及增加消息中心删除标志
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB73To74(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, GoWidgetTable.TABLENAME, GoWidgetTable.ENTRY, TYPE_TEXT, null);
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.DELETED,
					TYPE_NUMERIC, String.valueOf(0));
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	/**
	 * 3D widget方案调整以及增加消息中心删除标志
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB74To75(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.COUPON,
					TYPE_TEXT, null);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 增加去广告功能字段
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB75To76(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, ThemeTable.TABLENAME, ThemeTable.NO_ADVERT, TYPE_NUMERIC,
					String.valueOf(0));
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 消息中心增加指令控制字段
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB76To77(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.INSTRUCT,
					TYPE_TEXT, null);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 桌面设置增加功能表指示器上下位置字段以及消息中心推荐应用信息
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB78To79(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, ScreenSettingTable.TABLENAME,
					ScreenSettingTable.APPDRAWERINDICATORPOSITION, TYPE_TEXT,
					ScreenIndicator.INDICRATOR_ON_TOP);
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.PACKAGENAME,
					TYPE_TEXT, null);
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.MAPID, TYPE_TEXT,
					null);
			if (!isExistTable(db, DeskLockable.TABLENAME)) {
				db.execSQL(DeskLockable.CREATETABLESQL);
			}

			String inittablesql = "insert into " + DeskLockable.TABLENAME + "("
					+ DeskLockable.APPLOCK + "," + DeskLockable.LOCKHIDEAPP + ","
					+ DeskLockable.RESTORESETTING + "," + DeskLockable.RESTOREDEFAULT + ")"
					+ " values(" + "0," + "0," + "0," + "0" + ")";
			db.execSQL(inittablesql);

			if (!isExistTable(db, LockInfoTable.TABLE_NAME)) {
				db.execSQL(LockInfoTable.CREATETABLESQL);
			}

			if (!isExistTable(db, AppExtraAttributeTable.TABLE_NAME)) {
				db.execSQL(AppExtraAttributeTable.CREATETABLESQL);
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 桌面设置增加功能表指示器上下位置字段以及消息中心推荐应用信息
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB79To80(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		Cursor cursor1 = null;
		Cursor cursor2 = null;
		try {
			// 获取2D功能表滚屏特效设置值
			String selection = AppFuncSettingTable.SETTINGKEY + "=" + Setting.ICONEFFECT;
			String columns[] = { AppFuncSettingTable.SETTINGVALUE, };
			cursor1 = db.query(AppFuncSettingTable.TABLENAME, columns, selection, null, null, null,
					null);
			String type = String.valueOf(AppSettingDefault.ICONEFFECT);
			if (null != cursor1) {
				if (cursor1.moveToFirst()) {
					type = cursor1.getString(cursor1
							.getColumnIndex(AppFuncSettingTable.SETTINGVALUE));
				}
			}
			// 插入3D功能表滚屏特效设置到表中
			String insertSql = "insert into " + AppFuncSettingTable.TABLENAME + "("
					+ AppFuncSettingTable.SETTINGKEY + "," + AppFuncSettingTable.SETTINGVALUE + ","
					+ AppFuncSettingTable.PKNAME + ")" + " values(" + Setting.ICON_EFFECT_FOR_3D
					+ ",'" + type + "','" + IGoLauncherClassName.DEFAULT_THEME_PACKAGE + "')";
			db.execSQL(insertSql);

			// 获取2D桌面滚屏特效设置值
			columns = new String[] { DynamicEffectTable.EFFECTORTYPE, };
			cursor2 = db.query(DynamicEffectTable.TABLENAME, columns, null, null, null, null, null);
			int effectType = 0;
			if (null != cursor2) {
				if (cursor2.moveToFirst()) {
					effectType = cursor2.getInt(cursor2
							.getColumnIndex(DynamicEffectTable.EFFECTORTYPE));
				}
			}
			// 插入3D桌面滚屏特效设置到表中
			// 增加字段
			addColumnToTable(db, DynamicEffectTable.TABLENAME,
					DynamicEffectTable.EFFECTOR_TYPE_FOR_3D, TYPE_NUMERIC,
					String.valueOf(effectType));

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
			if (cursor1 != null) {
				cursor1.close();
			}
			if (cursor2 != null) {
				cursor2.close();
			}
		}
		return result;
	}

	/**
	 * 消息中心增加是否广告状态
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB80To81(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.ADDACT,
					TYPE_NUMERIC, null);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 
	 * 2. 添加滚屏特效表
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB81To82(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// 添加滚屏特效表
			if (!isExistTable(db, EffectorTable.TABLE_NAME)) {
				db.execSQL(EffectorTable.CREATETABLESQL);
				EffectorTable.initDefaultDatas(db);
			}
			EffectorDataModel.mappingNewEffectorIdForAppDrawer(db); // 映射功能表滚屏特效旧ID到新ID
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	/**
	 * 1. 侧边栏
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB82To83(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// 侧边栏
			addColumnToTable(db, DesktopTable.TABLENAME, DesktopTable.ENABLESIDEDOCK, TYPE_NUMERIC,
					null);
			addColumnToTable(db, DesktopTable.TABLENAME, DesktopTable.SIDEDOCKPOSITION,
					TYPE_NUMERIC, null);
			//				addColumnToTable(db, DesktopTable.TABLENAME, DesktopTable.ENABLEMARGINS,
			//						TYPE_NUMERIC, null);
			//				
			String updateSql = "update " + DesktopTable.TABLENAME + " set "
					+ DesktopTable.SIDEDOCKPOSITION + " = " + "1";

				db.execSQL(updateSql);

			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	/**
	 * 1. 侧边栏
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB83To84(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, PartToScreenTable.TABLENAME, PartToScreenTable.FOLDERTYPE,
					TYPE_NUMERIC, null);
			addColumnToTable(db, AppTable.TABLENAME, AppTable.FOLDERTYPE, TYPE_NUMERIC, null);
			addColumnToTable(db, ShortcutTable.TABLENAME, ShortcutTable.FOLDERTYPE, TYPE_NUMERIC,
					null);
			//				String updateSql = "update " + DesktopTable.TABLENAME + " set "
			//						+ DesktopTable.SIDEDOCKPOSITION + " = " + "1";

//				db.execSQL(updateSql);
			addColumnToTable(db, DesktopTable.TABLENAME, DesktopTable.ENABLEMARGINS, TYPE_NUMERIC,
					null);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	/**
	 * 旧用户dock条默认如果是3.0图标变成默认主题风格
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB77To78(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			String sql = "update " + ShortcutSettingTable.TABLENAME + " set "
					+ ShortcutSettingTable.STYLE_STRING + " = '"
					+ DockUtil.DOCK_DEFAULT_STYLE_STRING + "' where "
					+ ShortcutSettingTable.STYLE_STRING + " = '"
					+ IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3 + "'";
			db.execSQL(sql);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	/**
	 * <br>功能简述:消息中心增加字段
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB67To68(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.STIME_START,
					TYPE_TEXT, null);
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.STIME_END,
					TYPE_TEXT, null);
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.ICON, TYPE_TEXT,
					null);
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.INTRO, TYPE_TEXT,
					null);
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.ACTTYPE,
					TYPE_NUMERIC, String.valueOf(-1));
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.ACTVALUE,
					TYPE_TEXT, null);
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.ZICON1,
					TYPE_TEXT, null);
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.ZICON2,
					TYPE_TEXT, null);
			addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.ZPOS,
					TYPE_NUMERIC, String.valueOf(-1));
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 0屏广告表
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB84To85(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			if (!isExistTable(db, ZeroScreenAdSuggestSiteTable.TABLE_NAME)) {
				db.execSQL(ZeroScreenAdSuggestSiteTable.CREATE_SQL);
			}
			if (!isExistTable(db, ZeroScreenAdTable.TABLE_NAME)) {
				db.execSQL(ZeroScreenAdTable.CREATE_SQL);
				ZeroScreenAdTable.initZeroScreenDefaultAD(db);
			}
			
			//0屏设置项
			addColumnToTable(db, ThemeTable.TABLENAME, ThemeTable.SHOW_ZERO_SCREEN, TYPE_NUMERIC, String.valueOf(1));
			
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	/**
	 * <br>功能详细描述:admob
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB85To86(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			if (!isExistTable(db, AdTable.TABLE_NAME)) {
				db.execSQL(AdTable.CREATE_TABLE_SQL);
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	/**
	 * 功能表应用更新提示默认不勾选
	 * @param db
	 * @return
	 */
	public boolean onUpgradeDB86To87(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			String pkg = null;
			int key = Setting.APPUPDATE;
			int value = AppSettingDefault.DISABLEUPDATEAPP;
			updateAppFunSetting(db, pkg, key, value);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	// 升级消息中心，增加版本升级内容
	public boolean onUpgradeDB87To88(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, MessageCenterTable.TABLENAME,
					MessageCenterTable.UP_PACKAGE_NAME, TYPE_TEXT, null);
			addColumnToTable(db, MessageCenterTable.TABLENAME,
					MessageCenterTable.UP_VERSION_CODE, TYPE_NUMERIC,
					String.valueOf(-1));
			addColumnToTable(db, MessageCenterTable.TABLENAME,
					MessageCenterTable.UP_VERSION, TYPE_TEXT, null);
			addColumnToTable(db, MessageCenterTable.TABLENAME,
					MessageCenterTable.UP_FILE_URL, TYPE_TEXT, null);
			addColumnToTable(db, MessageCenterTable.TABLENAME,
					MessageCenterTable.UP_DELTA_FILE_URL, TYPE_TEXT, null);
			addColumnToTable(db, MessageCenterTable.TABLENAME,
					MessageCenterTable.UP_DELTA_RESULT, TYPE_TEXT, null);
			addColumnToTable(db, MessageCenterTable.TABLENAME,
					MessageCenterTable.UP_UPDATES, TYPE_TEXT, null);
			addColumnToTable(db, MessageCenterTable.TABLENAME,
					MessageCenterTable.UP_SECOND_INTRO, TYPE_TEXT, null);
			addColumnToTable(db, MessageCenterTable.TABLENAME,
					MessageCenterTable.UP_UPDATE_MODE, TYPE_NUMERIC,
					String.valueOf(-1));
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}

	public boolean onUpgradeDB88To89(SQLiteDatabase db) {
		boolean result = false;
		PreferencesManager sharedPreferences = new PreferencesManager(
				ApplicationProxy.getApplication(), IPreferencesIds.PREFERENCE_ENGINE,
				Context.MODE_PRIVATE);
		boolean isUsedShellEngine = sharedPreferences.getBoolean(
				IPreferencesIds.PREFERENCE_ENGINE_SELECTED, false);
		if (isUsedShellEngine) {
			db.beginTransaction();
			try {
				String pkg = null;
				int key = Setting.SHOW_ACTION_BAR;
				int value = AppSettingDefault.SHOW_ACTION_BAR;
				updateAppFunSetting(db, pkg, key, value);
				key = Setting.SHOW_TAB_ROW;
				value = AppSettingDefault.SHOW_TAB_ROW;
				updateAppFunSetting(db, pkg, key, value);
				db.setTransactionSuccessful();
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
			} finally {
				db.endTransaction();
			}
		} else {
			result = true;
		}
		return result;
	}
	
//  0屏数据清除，插入新的默认数据
	public boolean onUpgradeDB89To90(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			if (isExistTable(db, ZeroScreenAdSuggestSiteTable.TABLE_NAME)) {
				db.delete(ZeroScreenAdSuggestSiteTable.TABLE_NAME, null, null);

			}
			if (isExistTable(db, ZeroScreenAdTable.TABLE_NAME)) {
				db.delete(ZeroScreenAdTable.TABLE_NAME, null, null);
				ZeroScreenAdTable.initZeroScreenDefaultAD(db);
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	public boolean onUpgradeDB90To91(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			if (!isExistTable(db, SideWidgetTable.TABLE_NAME)) {
				db.execSQL(SideWidgetTable.CREATETABLESQL);
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	public boolean onUpgradeDB91To92(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			if (!isExistTable(db, SideWidgetTable.TABLE_NAME)) {
				db.execSQL(SideWidgetTable.CREATETABLESQL);
			} else {
				//升级表字段
				if (!isExistColumnInTable(db, SideWidgetTable.TABLE_NAME,
						SideWidgetTable.WIDGET_PREVIEWNAME)) {
					addColumnToTable(db,SideWidgetTable.TABLE_NAME,SideWidgetTable.WIDGET_PREVIEWNAME,"INTEGER","0");
//					updateSideWidgetData(db,"com.touchtype.swiftkey",-272961604);
//					updateSideWidgetData(db,"com.tencent.mobileqq",-965049217);
				}
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	public boolean onUpgradeDB92To93(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// 2D功能表滚屏特效由随机切换为选择随机自定义
			boolean needUpdateEffectCustom = false;
			String iconEffectType2D = getAppFunSetting(db, Setting.ICONEFFECT,
					String.valueOf(AppSettingDefault.ICONEFFECT));
			if (Integer.parseInt(iconEffectType2D) == IEffectorIds.EFFECTOR_TYPE_RANDOM) {
				updateAppFunSetting(db, IGoLauncherClassName.DEFAULT_THEME_PACKAGE, Setting.ICONEFFECT,
						IEffectorIds.EFFECTOR_TYPE_RANDOM_CUSTOM);
				needUpdateEffectCustom = true;
			}
			
			// 3D功能表滚屏特效由随机切换为选择随机自定义
			String iconEffectType3D = getAppFunSetting(db, Setting.ICON_EFFECT_FOR_3D,
					String.valueOf(AppSettingDefault.ICONEFFECT));
			if (Integer.parseInt(iconEffectType3D) == IEffectorIds.EFFECTOR_TYPE_RANDOM) {
				updateAppFunSetting(db, IGoLauncherClassName.DEFAULT_THEME_PACKAGE, Setting.ICON_EFFECT_FOR_3D,
						IEffectorIds.EFFECTOR_TYPE_RANDOM_CUSTOM);
				needUpdateEffectCustom = true;
			}
			
			// 更新功能表随机自定义滚屏数组
			if (needUpdateEffectCustom) {
				String updateValue = "0;1;3;4;5;6;7;8;9;10;11;12;13;14;15;16;";
				updateAppFunSetting(db, IGoLauncherClassName.DEFAULT_THEME_PACKAGE,
						Setting.APPICONCUSTOMEFFECTSETTING, updateValue);
			}
			
			// 功能表进出特效选择随机自定义
			String inoutEffectType = getAppFunSetting(db, Setting.INOUTEFFECT,
					String.valueOf(AppSettingDefault.INOUTEFFECT));
			if (Integer.parseInt(inoutEffectType) == DeskSettingConstants.APP_FUN_IN_OUT_EFFECTS_RANDOM) {
				updateAppFunSetting(db, IGoLauncherClassName.DEFAULT_THEME_PACKAGE, Setting.INOUTEFFECT,
						DeskSettingConstants.APP_FUN_IN_OUT_EFFECTS_CUSTOM);
				String updateValue = "2;3;4;5;";
				updateAppFunSetting(db, IGoLauncherClassName.DEFAULT_THEME_PACKAGE,
						Setting.APPINOUTCUSTOMRANDOMEFFECT, updateValue);
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	public boolean onUpgradeDB93To94(SQLiteDatabase db) {
		boolean result = false;
		Cursor cursor = null;
		db.beginTransaction();
		try {
			// CN包由于要清除掉购物小部件，当旧用户升级上来时，清除掉用户先前保存的数据
			int shoppingWidgetId = getShoppingWidgetId(db);
			if (shoppingWidgetId != Integer.MIN_VALUE) {
				deleteShoppingWidget(db, shoppingWidgetId);
			}
						
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}
		return result;
	}
	
	private boolean onUpgradeDB94To95(SQLiteDatabase db) {
		boolean result = true;
		db.beginTransaction();
		try {
			ComponentName c = new ComponentName(mContext, IGoLauncherClassName.GOLAUNCHER_ACTIVITY);
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setComponent(c);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			removeFunItem(intent, db);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	private boolean onUpgradeDB95To96(SQLiteDatabase db) {
		boolean result = true;
		db.beginTransaction();
		try {
			if (!isExistTable(db, VideoCenterInfoTable.TABLE_NAME)) {
				db.execSQL(VideoCenterInfoTable.CREATE_SQL);
			} 
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	private boolean onUpgradeDB96To97(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, MessageCenterTable.TABLENAME,
					MessageCenterTable.UP_AUTO_DOWNLOAD, TYPE_NUMERIC, String.valueOf(0));
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	private boolean onUpgradeDB97To98(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			addColumnToTable(db, ThemeTable.TABLENAME,
					ThemeTable.IS_SHOW_STATUS_BAR_BG, TYPE_NUMERIC, String.valueOf(1));
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	/**
	 * <br>功能详细描述:内置开关widget
	 * @param db
	 * @return
	 */
	private boolean onUpgradeDB98To99(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			if (!isExistTable(db, SwitchTable.TABLE_NAME)) {
				db.execSQL(SwitchTable.CREATE_TABLE);
			}
			addColumnToTable(db, ThemeTable.TABLENAME, ThemeTable.KEYBACK_CHANGE_ZERO_SCREEN, TYPE_NUMERIC, String.valueOf(1));
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	/**
	 * <br>功能详细描述:CN包v5.0数据库升级，把百度及360搜索gowidget换为内置的搜索gowidget
	 * @param db
	 * @return
	 */
	private boolean onUpgradeDB99To100(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			ContentValues cv = new ContentValues();
			cv.put(GoWidgetTable.LAYOUT, "searchwidget41_3d");
			cv.put(GoWidgetTable.PROTOTYPE, "14");
			//百度
			String where = GoWidgetTable.LAYOUT + " = 'baidu_widget41'";
			db.update(GoWidgetTable.TABLENAME, cv, where, null);
			
			//360
			where = GoWidgetTable.LAYOUT + " = 'qihoo_hot_widget41'";
			db.update(GoWidgetTable.TABLENAME, cv, where, null);
			
			//360一键清除
			cv.clear();
			cv.put(GoWidgetTable.LAYOUT, "taskcircle_widget11_3d");
			cv.put(GoWidgetTable.PROTOTYPE, "11");
			where = GoWidgetTable.LAYOUT + " = 'qihoo_inner_task_1_1'";
			db.update(GoWidgetTable.TABLENAME, cv, where, null);
			db.setTransactionSuccessful();
			result = true; 
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	/**
	 * 全渠道开0屏
	 * @param db
	 * @return
	 */
	public final static String ABECHANNEL_UIDS = "353,563,312,331,311,333,334,315,316,357,338,504,546,362,500,303,374,501,361,325,313,355,523,360,215,524"; // ABE渠道

	private boolean onUpgradeDB100To101(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			String uid = Statistics.getUid(ApplicationProxy.getContext());
			if (ABECHANNEL_UIDS.contains(uid)) {
				ContentValues cv = new ContentValues();
				cv.put(ThemeTable.SHOW_ZERO_SCREEN, ConvertUtils.boolean2int(true));
				cv.put(ThemeTable.KEYBACK_CHANGE_ZERO_SCREEN, ConvertUtils.boolean2int(true));
				db.update(ThemeTable.TABLENAME, cv, null, null);
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	/**
	 * ＣＮ包升级业务插入，与200业务无关
	 * @param db
	 * @return
	 */
	private boolean onUpgradeDB101To102(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			if (!isExistTable(db, IconControllTable.TABLE_NAME)) {
				db.execSQL(IconControllTable.CREATE_TABLE);
			}
			// 删除新版有奖
			Intent intent = new Intent(
					ICustomAction.ACTION_NEW_VERSION_SHORTCUT_URL);
			String intentString = ConvertUtils.intentToString(intent);
			String whereStr = PartToScreenTable.INTENT + " = " + "'"
					+ intentString + "'";
			db.delete(PartToScreenTable.TABLENAME, whereStr, null);
			db.delete(ShortcutTable.TABLENAME, whereStr, null);
			db.delete(FolderTable.TABLENAME, whereStr, null);
			// 某些数据库版本漏了 是否显示零屏的字段， 在这次升级统一判断添加一次 add by zhengxiangcan
			addColumnToTable(db, ThemeTable.TABLENAME,
					ThemeTable.SHOW_ZERO_SCREEN, TYPE_NUMERIC,
					String.valueOf(1)); 
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	/**
	 * CN包v5.03数据库升级移入200包
	 * @param db
	 * @return
	 */
	private boolean onUpgradeDB102To103(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			// CN包清除亚马逊，当旧用户升级上来时，清除掉用户先前保存的数据(由于可以由GO小部件添加插件，所以可能存在多个id)
			int[] amazonWidgetIds = getGoWidgetIdByLayout(db, "iw_amazonwidget41");
			if (amazonWidgetIds != null && amazonWidgetIds.length > 0) {
				int amazonWidgetId = Integer.MIN_VALUE;
				for (int i = 0; i < amazonWidgetIds.length; i++) {
					amazonWidgetId = amazonWidgetIds[i];
					if (amazonWidgetId != Integer.MIN_VALUE
							&& amazonWidgetId != 0) {
						deleteGoWidget(db, amazonWidgetId);
					}
				}
			}
			// 删除放在屏幕，dock条，文件夹里面的亚马逊图标
			Intent intent = new Intent(ICustomAction.ACTION_APPFUNC_AMAZON);
			ComponentName amazonCN = new ComponentName(LauncherEnv.AMAZON_PACKAGE_NAME,
					ICustomAction.ACTION_APPFUNC_AMAZON);
			intent.setComponent(amazonCN);
			intent.setData(Uri.parse("package:" + LauncherEnv.AMAZON_PACKAGE_NAME));
			String intentString = ConvertUtils.intentToString(intent);
			String whereStr = PartToScreenTable.INTENT + " = " + "'"
					+ intentString + "'";
			db.delete(PartToScreenTable.TABLENAME, whereStr, null);
			db.delete(ShortcutTable.TABLENAME, whereStr, null);
			db.delete(FolderTable.TABLENAME, whereStr, null);
			db.delete(AppTable.TABLENAME, whereStr, null);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	private boolean onUpgradeDB103To104(SQLiteDatabase db) {
	    boolean allResult = false;
		boolean result = true;
		int iconSize = ApplicationProxy
				.getContext()
				.getResources()
				.getDimensionPixelSize(
						R.dimen.screen_folder_modify_icon_large_size);
		Cursor cursor = db.query(DesktopTable.TABLENAME,
				new String[] { DesktopTable.ICONSIZE, DesktopTable.LARGEICON}, null, null, null, null,
				null);
		if (cursor != null && cursor.moveToFirst()) {
			boolean isLargeIcon = ConvertUtils.int2boolean(cursor.getInt(cursor
					.getColumnIndex(DesktopTable.LARGEICON)));
			int iconSettingSize = cursor.getInt(cursor
					.getColumnIndex(DesktopTable.ICONSIZE));
			if (isLargeIcon && iconSettingSize != iconSize) {
				db.beginTransaction();
				try {
					PreferencesManager pm = new PreferencesManager(
							ApplicationProxy.getContext());
					pm.putBoolean(
							IPreferencesIds.PREFERENCE_IS_USING_OLD_CELLLAYOUT,
							true);
					pm.commit();
					ContentValues cv = new ContentValues();
					cv.put(DesktopTable.ICONSIZE, iconSize);
					cv.put(DesktopTable.LARGEICON, false);
					db.update(DesktopTable.TABLENAME, cv, null, null);
					db.setTransactionSuccessful();
					result = true;
				} catch (Exception e) {
					e.printStackTrace();
					result = false;
				} finally {
					db.endTransaction();
				}
			}
			if (!cursor.isClosed()) {
				cursor.close();
			}
		}
		
		boolean messageResult = onUpgradeDBMessageTable(db);
		
		//add by caoyaming 创建主题Icon表(Ui3.0)
		boolean createTableResult = false;
		db.beginTransaction();
		try {
			if (!isExistTable(db, ThemeIconInfoTable.TABLE_NAME)) {
				db.execSQL(ThemeIconInfoTable.CREATE_SQL);
			} 
			db.setTransactionSuccessful();
			createTableResult = true;
		} catch (Exception e) {
			e.printStackTrace();
			createTableResult = false;
		} finally {
			db.endTransaction();
		}
		//add by caoyaming end
		if (result && messageResult && createTableResult) {
		    allResult = true;
		}
		return allResult;
	}
	
	
	private boolean onUpgradeDB104To105(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			db.execSQL(PurchaseTable.CREATE_SQL);
			addColumnToTable(db, GoWidgetTable.TABLENAME, GoWidgetTable.REPLACEGROUP, TYPE_NUMERIC, "0");
			addColumnToTable(db, AdTable.TABLE_NAME, AdTable.GETJAR_ENABLE, TYPE_NUMERIC, "1");
			addColumnToTable(db, AdTable.TABLE_NAME, AdTable.PROVITY, TYPE_NUMERIC, "1");
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	/**
	 * 创建应用图标配置信息表
	 * @param db
	 * @return
	 */
	private boolean onUpgradeDB105To106(SQLiteDatabase db) {
		boolean result = false;
		try {
			db.beginTransaction();
			if (!isExistTable(db, AppIconConfigInfoTable.TABLE_NAME)) {
				db.execSQL(AppIconConfigInfoTable.CREATE_SQL);
			} 
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	private boolean onUpgradeDB106To107(SQLiteDatabase db) {
		boolean result = false;
		db.beginTransaction();
		try {
			if (isExistTable(db, AppTable.TABLENAME)) {
				db.execSQL("update " + AppTable.TABLENAME + " set " + AppTable.FOLDERTYPE
						+ " = 2 where " + AppTable.FOLDERTYPE + " = 1 ");
				db.execSQL("update " + AppTable.TABLENAME + " set " + AppTable.FOLDERTYPE
						+ " = -1 where " + AppTable.FOLDERTYPE + " = 0 ");
			}
			if (isExistTable(db, ShortcutTable.TABLENAME)) {
				db.execSQL("update " + ShortcutTable.TABLENAME + " set " + ShortcutTable.FOLDERTYPE
						+ " = 2 where " + ShortcutTable.FOLDERTYPE + " = 1 ");
				db.execSQL("update " + ShortcutTable.TABLENAME + " set " + ShortcutTable.FOLDERTYPE
						+ " = -1 where " + ShortcutTable.FOLDERTYPE + " = 0 ");
			}
			if (isExistTable(db, PartToScreenTable.TABLENAME)) {
				db.execSQL("update " + PartToScreenTable.TABLENAME + " set "
						+ PartToScreenTable.FOLDERTYPE + " = 2 where "
						+ PartToScreenTable.FOLDERTYPE + " = 1 ");
				db.execSQL("update " + PartToScreenTable.TABLENAME + " set "
						+ PartToScreenTable.FOLDERTYPE + " = -1 where "
						+ PartToScreenTable.FOLDERTYPE + " = 0 ");
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	private boolean onUpgradeDB107To108(SQLiteDatabase db) {
		boolean result = false;
		try {
			db.beginTransaction();
			//创建应用图标配置信息表
			db.execSQL(SmartCardFolderStateTable.CREATE_SQL);
			
			//添加最近打开和正在运行的图标到功能表第一二个位置
			String updateSql = "update " + AppTable.TABLENAME + " set "
					+ AppTable.INDEX + " = " + AppTable.INDEX + " + 2;";
			db.execSQL(updateSql);
			
			Resources res = ApplicationProxy.getContext().getResources();
			
			Intent recentAppIntent = new Intent(ICustomAction.ACTION_RECENTAPP);
			ComponentName recentAppCom = new ComponentName(PackageName.RECENTAPP_PACKAGE_NAME, ICustomAction.ACTION_RECENTAPP);
			recentAppIntent.setComponent(recentAppCom);
			recentAppIntent.setData(Uri.parse("package:" + PackageName.RECENTAPP_PACKAGE_NAME));
			ContentValues recentAppValues = new ContentValues();
			recentAppValues.put(AppTable.INDEX, 0);
			recentAppValues.put(AppTable.INTENT, recentAppIntent.toUri(0));
			recentAppValues.put(AppTable.TITLE, res.getString(R.string.recentapp_title));
			db.insert(AppTable.TABLENAME, null, recentAppValues);
			
			
			Intent proManageIntent = new Intent(ICustomAction.ACTION_PROMANAGE);
			ComponentName proManageCom = new ComponentName(PackageName.PROMANAGE_PACKAGE_NAME, ICustomAction.ACTION_PROMANAGE);
			proManageIntent.setComponent(proManageCom);
			proManageIntent.setData(Uri.parse("package:" + PackageName.PROMANAGE_PACKAGE_NAME));
			ContentValues proManageValues = new ContentValues();
			proManageValues.put(AppTable.INDEX, 1);
			proManageValues.put(AppTable.INTENT, proManageIntent.toUri(0));
			proManageValues.put(AppTable.TITLE, res.getString(R.string.promanage_title));
			db.insert(AppTable.TABLENAME, null, proManageValues);
			
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	private boolean onUpgradeDB108To109(SQLiteDatabase db) {
		boolean result = false;
		try {
			db.beginTransaction();
			addColumnToTable(db, AppExtraAttributeTable.TABLE_NAME, AppExtraAttributeTable.ISNEW, TYPE_NUMERIC, null);
			addColumnToTable(db, AppExtraAttributeTable.TABLE_NAME, AppExtraAttributeTable.CLICK_OPEN_TIME, TYPE_NUMERIC, null);
			addColumnToTable(db, AppExtraAttributeTable.TABLE_NAME, AppExtraAttributeTable.READ_UPDATA_INFO_TIME, TYPE_NUMERIC, null);
			addColumnToTable(db, AppExtraAttributeTable.TABLE_NAME, AppExtraAttributeTable.DATA, TYPE_TEXT, null);
			
			db.execSQL(SpecialApplicationTable.CREATE_SQL);
			SpecialApplicationTable.initData(db);
			db.execSQL(SideToolsTable.CREATETABLESQL);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	//消息中心新增promotion字段
	private boolean onUpgradeDBMessageTable(SQLiteDatabase db) {
	    boolean result = false;
        db.beginTransaction();
        try {
            if (!isExistTable(db, MessageCenterTable.TABLENAME)) {
                db.execSQL(MessageCenterTable.TABLENAME);
            }
            addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.PROMOTION, TYPE_TEXT, null);
            addColumnToTable(db, MessageCenterTable.TABLENAME, MessageCenterTable.ISADV, TYPE_NUMERIC, null);
            db.setTransactionSuccessful();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            db.endTransaction();
        }
        return result;
	}
	
	
	private void updateSideWidgetData(SQLiteDatabase db, String pkg,
			int previewId) throws DatabaseException {
		ContentValues values = new ContentValues();
		String selection = SideWidgetTable.WIDGET_PKGNAME + "=" + pkg;
		values.put(SideWidgetTable.WIDGET_PREVIEWNAME, previewId);
		db.update(SideWidgetTable.TABLE_NAME, values, selection, null);
	}

	
	private void updateAppFunSetting(SQLiteDatabase db, String pkg, int key, int value)
			throws DatabaseException {
		ContentValues values = new ContentValues();
		String selection = AppFuncSettingTable.SETTINGKEY + "=" + key;
		if (pkg != null && !pkg.trim().equals("")) {
			selection += " and " + AppFuncSettingTable.PKNAME + "='" + pkg + "'";
		}
		values.put(AppFuncSettingTable.SETTINGKEY, key);
		values.put(AppFuncSettingTable.SETTINGVALUE, value);
		values.put(AppFuncSettingTable.PKNAME, pkg);
		db.update(AppFuncSettingTable.TABLENAME, values, selection, null);
	}
	
	private void updateAppFunSetting(SQLiteDatabase db, String pkg, int key, String value)
			throws DatabaseException {
		ContentValues values = new ContentValues();
		String selection = AppFuncSettingTable.SETTINGKEY + "=" + key;
		if (pkg != null && !pkg.trim().equals("")) {
			selection += " and " + AppFuncSettingTable.PKNAME + "='" + pkg + "'";
		}
		values.put(AppFuncSettingTable.SETTINGKEY, key);
		values.put(AppFuncSettingTable.SETTINGVALUE, value);
		values.put(AppFuncSettingTable.PKNAME, pkg);
		db.update(AppFuncSettingTable.TABLENAME, values, selection, null);
	}
	
	private String getAppFunSetting(SQLiteDatabase db, int key,
			String defaultValue) {
		Cursor cursor = null;
		String value = defaultValue;
		try {
			String selection = AppFuncSettingTable.SETTINGKEY + "=" + key;
			String[] columns = new String[] { AppFuncSettingTable.SETTINGVALUE, };
			cursor = db.query(AppFuncSettingTable.TABLENAME, columns,
					selection, null, null, null, null);
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					value = cursor.getString(cursor
							.getColumnIndex(AppFuncSettingTable.SETTINGVALUE));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return value;
	}
	
	/**
	 * 获取购物小部件的widgetId
	 * @return
	 */
	private int getShoppingWidgetId(SQLiteDatabase db) {
		int widgetId = Integer.MIN_VALUE;
		Cursor cursor = null;
		String where = GoWidgetTable.LAYOUT + " = '" + "shoppingwidget44" + "'";
		try {
			cursor = db.query(GoWidgetTable.TABLENAME, null, where, null,null, null, null);
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					widgetId = cursor.getInt(cursor
							.getColumnIndex(GoWidgetTable.WIDGETID));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return widgetId;
	}
	
	/**
	 * 根据购物小部件的widgetId删除表数据
	 * @param db
	 * @param widgetId
	 * @return
	 */
	private int deleteShoppingWidget(SQLiteDatabase db, int widgetId) {
		String condition = GoWidgetTable.WIDGETID + " = " + widgetId;
		String whereStr = PartToScreenTable.WIDGETID + " = '" + widgetId + "'";
		int count = 0;
		try {
			count = db.delete(GoWidgetTable.TABLENAME, condition, null);
			db.delete(PartToScreenTable.TABLENAME, whereStr, null);
		} catch (Exception e) {
			Log.i("data", "Exception when delete in " + GoWidgetTable.TABLENAME + ", " + condition);
		}
		return count;
	}
	
	//check DB integrity  add by zhangxi @2013.07.19
	public boolean isDBCheckOK() {
		SQLiteDatabase db = getWritableDatabase();
		SQLiteStatement prog = null;
		try {
			prog = db.compileStatement("PRAGMA integrity_check;");
			String rslt = prog.simpleQueryForString();
			Log.e("DB", rslt);
			if (!rslt.equalsIgnoreCase("ok")) {
				// integrity_checker failed on main or attached database
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (prog != null)
				prog.close();
		}
		return true;
	}

	public void beginTransaction() {
		try {
			SQLiteDatabase db = getWritableDatabase();
			if (null != db) {
				db.beginTransaction();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTransactionSuccessful() {
		try {
			SQLiteDatabase db = getWritableDatabase();
			if (null != db) {
				db.setTransactionSuccessful();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void endTransaction() {
		try {
			SQLiteDatabase db = getWritableDatabase();
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getDB_CUR_VERSION() {
		return DB_VERSION_MAX;
	}

	/**
	 * 抽象升级类，只在每个连续的数据库版本间作升级
	 * 
	 * @author huyong
	 * 
	 */
	abstract class UpgradeDB {
		abstract boolean onUpgradeDB(SQLiteDatabase db);
	}

	/**
	 * 更新：1到2
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBOneToTwo extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade1To2(db);
		}
	}

	/**
	 * 更新：2到3
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBTwoToThree extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade2To3(db);
		}
	}

	/**
	 * 更新：3到4
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBThreeToFour extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade3To4(db);
		}
	}

	/**
	 * 更新：4到5
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBFourToFive extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade4To5(db);
		}
	}

	/**
	 * 更新：5到6
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBFiveToSix extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade5To6(db);
		}
	}

	/**
	 * 更新：6到7
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBSixToSeven extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade6To7(db);
		}
	}

	/**
	 * 更新：7到8
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBSevenToEight extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade7To8(db);
		}
	}

	/**
	 * 更新：8到9
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBEightToNine extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade8To9(db);
		}
	}

	/**
	 * 更新：9到10
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBNineToTen extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade9To10(db);
		}
	}

	/**
	 * 更新：10到11
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBTenToEleven extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade10To11(db);
		}
	}

	/**
	 * 更新：11到12
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBElevenToTwelve extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade11To12(db);
		}
	}

	/**
	 * 更新：12到13
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBTwelveToThirteen extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB12To13(db);
		}
	}

	/**
	 * 更新：13到14
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBThirteenToFourteen extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB13To14(db);
		}
	}

	/**
	 * 更新：14到15
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBFourteenToFifteen extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB14To15(db);
		}
	}

	/**
	 * 更新：15到16
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBFifteenToSixteen extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB15To16(db);
		}
	}

	/**
	 * 更新：16到17
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBSixteenToSeventeen extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB16To17(db);
		}
	}

	/**
	 * 更新：17到18
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBSeventeenToEighteen extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB17To18(db);
		}
	}

	/**
	 * 更新：18到19
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBEighteenToNineteen extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB18To19(db);
		}
	}

	/**
	 * 更新：19到20
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBNineteenTOTwenty extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB19To20(db);
		}
	}

	/**
	 * 更新：20到21
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBTwentyTOTwentyOne extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB20To21(db);
		}
	}

	/**
	 * 更新：21到22
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBTwentyOneTOTwentyTwo extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB21To22(db);
		}
	}

	/**
	 * 更新：22到23
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBTwentyTwoTOTwentyThree extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB22To23(db);
		}
	}

	/**
	 * 更新：23到24
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBTwentyThreeToTwentyFour extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB23To24(db);
		}
	}

	/**
	 * 更新：24到25
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBTwentyFourToTwentyFive extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB24To25(db);
		}
	}

	/**
	 * 更新：25到26
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBTwentyFiveToTwentySix extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB25To26(db);
		}
	}

	/**
	 * 更新：26到27
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBTwentySixToTwentySeven extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB26To27(db);
		}
	}

	/**
	 * 更新：27到28
	 * @author yangguanxiang
	 *
	 */
	class UpgradeDBTwentySevenToTwentyEight extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB27To28(db);
		}
	}

	/**
	 * 更新：28到29
	 * @author yangguanxiang
	 *
	 */
	class TwentyEightToTwentyNine extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB28To29(db);
		}
	}

	/**
	 * 更新：29到30
	 * @author yangguanxiang
	 *
	 */
	class TwentyNineToThirty extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB29To30(db);
		}
	}

	/**
	 * 更新：30到31
	 * @author yangguanxiang
	 *
	 */
	class ThirtyToThirtyOne extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB30To31(db);
		}
	}

	/**
	 * 更新：31到32
	 * @author yangguanxiang
	 *
	 */
	class ThirtyOneToThirtyTwo extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB31To32(db);
		}
	}

	/**
	 * 更新：32到33
	 * @author yangguanxiang
	 *
	 */
	class ThirtyTwoToThirtyThree extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB32To33(db);
		}
	}

	/**
	 * 更新：33到34
	 * @author yangguanxiang
	 *
	 */
	class ThirtyThreeToThirtyFour extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB33To34(db);
		}
	}

	/**
	 * 更新：34到35
	 * @author yangguanxiang
	 *
	 */
	class ThirtyFourToThirtyFive extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB34To35(db);
		}
	}

	/**
	 * 更新：35到36
	 * @author yangguanxiang
	 *
	 */
	class ThirtyFiveToThirtySix extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB35To36(db);
		}
	}

	/**
	 * 更新：36到37
	 * @author yangguanxiang
	 *
	 */
	class ThirtySixToThirtySeven extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB36To37(db);
		}
	}

	/**
	 * 更新：37到38
	 * @author yangguanxiang
	 *
	 */
	class ThirtySevenToThirtyEight extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB37To38(db);
		}
	}

	/**
	 * 更新：38到39
	 * @author yangguanxiang
	 *
	 */
	class ThirtyEightToThirtyNine extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB38To39(db);
		}
	}

	/**
	 * 更新：39到40
	 * @author yangguanxiang
	 *
	 */
	class ThirtyNineToForty extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB39To40(db);
		}
	}

	/**
	 * 更新：40到41
	 * @author yangguanxiang
	 *
	 */
	class FortyToFortyOne extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB40To41(db);
		}
	}

	/**
	 * 更新：41到42
	 * @author yangguanxiang
	 *
	 */
	class FortyOneToFortyTwo extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB41To42(db);
		}
	}

	/**
	 * 更新：42到43
	 * @author yangguanxiang
	 *
	 */
	class FortyTwoToFortyThree extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB42To43(db);
		}
	}

	/**
	 * 更新：43到44
	 * @author yangguanxiang
	 *
	 */
	class FortyThreeToFortyFour extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB43To44(db);
		}
	}

	/**
	 * 更新：44到45
	 * @author yangguanxiang
	 *
	 */
	class FortyFourToFortyFive extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB44To45(db);
		}
	}

	/**
	 * 更新：45到46
	 * @author yangguanxiang
	 *
	 */
	class FortyFiveToFortySix extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB45To46(db);
		}
	}

	/**
	 * 更新：46到47
	 * @author yangguanxiang
	 *
	 */
	class FortySixToFortySeven extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB46To47(db);
		}
	}

	/**
	 * 更新：47到48
	 * @author yangguanxiang
	 *
	 */
	class FortySevenToFortyEight extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB47To48(db);
		}
	}

	/**
	 * 更新：48到49
	 * @author yangguanxiang
	 *
	 */
	class FortyEightToFortyNine extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB48To49(db);
		}
	}

	/**
	 * 更新：49到50
	 * @author yangguanxiang
	 *
	 */
	class FortyNineToFifty extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB49To50(db);
		}
	}

	/**
	 * 更新：50到51
	 * @author yangguanxiang
	 *
	 */
	class FiftyToFiftyOne extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB50To51(db);
		}
	}

	/**
	 * 更新：51到52
	 * @author yangguanxiang
	 *
	 */
	class FiftyOneToFiftyTwo extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB51To52(db);
		}
	}

	/**
	 * 更新：52到53
	 * @author yangguanxiang
	 *
	 */
	class FiftyTwoToFiftyThree extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB52To53(db);
		}
	}

	/**
	 * 更新：53到54
	 * @author yangguanxiang
	 *
	 */
	class FiftyThreeToFiftyFour extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB53To54(db);
		}
	}

	/**
	 * 更新：54到55
	 * @author yangguanxiang
	 *
	 */
	class FiftyFourToFiftyFive extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB54To55(db);
		}
	}

	/**
	 * 更新：55到56
	 * @author yangguanxiang
	 *
	 */
	class FiftyFiveToFiftySix extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB55To56(db);
		}
	}

	/**
	 * 更新：56到57
	 * @author yangguanxiang
	 *
	 */
	class FiftySixToFiftySeven extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB56To57(db);
		}
	}

	/**
	 * 更新：57到58
	 * @author yangguanxiang
	 *
	 */
	class FiftySevenToFiftyEight extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB57To58(db);
		}
	}

	/**
	 * 更新：58到59
	 * @author yangguanxiang
	 *
	 */
	class FiftyEightToFiftyNight extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB58To59(db);
		}
	}

	/**
	 * 更新：59到60
	 * @author yangguanxiang
	 *
	 */
	class FiftyNineToSixty extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB59To60(db);
		}
	}

	/**
	 * 更新：60到61
	 * @author yangguanxiang
	 *
	 */
	class SixtyToSixtyOne extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB60To61(db);
		}
	}

	/**
	 * 更新：61到62
	 * @author yangguanxiang
	 *
	 */
	class SixtyOneToSixtyTwo extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB61To62(db);
		}
	}

	/**
	 * 更新：62到63
	 * @author yangguanxiang
	 *
	 */
	class SixtyTwoToSixtyThree extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB62To63(db);
		}
	}

	/**
	 * 更新：63到64
	 * @author yangguanxiang
	 *
	 */
	class SixtyThreeToSixtyFour extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB63To64(db);
		}
	}

	/**
	 * 更新：64到65
	 * @author yangguanxiang
	 *
	 */
	class SixtyFourToSixtyFive extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB64To65(db);
		}
	}

	/**
	 * 更新：65到66
	 * @author yangguanxiang
	 *
	 */
	class SixtyFiveToSixtySix extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB65To66(db);
		}
	}

	/**
	 * 更新：66到67
	 * @author yangguanxiang, dengdazhong
	 *
	 */
	class SixtySixToSixtySeven extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB66To67(db);
		}
	}
	/**
	 * 更新：67到68
	 * @author rongjinsong
	 *
	 */
	class SixtySevenToSixtyEight extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB67To68(db);
		}
	}

	/**
	 * 更新：68到69
	 * @author rongjinsong
	 *
	 */
	class SixtyEightToSixtyNine extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB68To69(db);
		}
	}

	/**
	 * 更新：69到70
	 * @author liulixia
	 *
	 */
	class SixtyNineToSeventy extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB69To70(db);
		}
	}

	/**
	 * 更新：70到71
	 * @author liulixia
	 *
	 */
	class SeventyToSeventyOne extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB70To71(db);
		}
	}

	class SeventyOneToSeventyTwo extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB71To72(db);
		}
	}

	class SeventyTwoToSeventyThree extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB72To73(db);
		}
	}

	class SeventyThreeToSeventyFour extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB73To74(db);
		}
	}

	class SeventyThFourToSeventyFive extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB74To75(db);
		}
	}

	class SeventyFiveToSeventySix extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB75To76(db);
		}
	}

	class SeventySixToSeventySeven extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB76To77(db);
		}
	}

	class SeventySevenToSeventyEight extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB77To78(db);
		}
	}

	class SeventyEightToSeventyNine extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB78To79(db);
		}
	}

	class SeventyNineToEighty extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB79To80(db);
		}
	}

	class EightyToEightyOne extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB80To81(db);
		}
	}

	class EightyOneToEightyTwo extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB81To82(db);
		}
	}

	class EightyTwoToEightyThree extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB82To83(db);
		}
	}

	class EightyThreeToEightyFour extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB83To84(db);
		}
	}

	class EightyFourToEightyFive extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB84To85(db);
		}
	}
	
	class EightyFiveToEightySix extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB85To86(db);
		}
	}
	
	class EightySixToEightySeven extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB86To87(db);
		}
	}
	
	class EightySevenToEightyEight extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB87To88(db);
		}
	}
	
	class EightyEightToEightyNine extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB88To89(db);
		}
	}
	
	class EightyNineToNinety extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB89To90(db);
		}
	}
	
	class NinetyToNinetyOne extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB90To91(db);
		}
	}
	
	class NinetyOneToNinetyTwo extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB91To92(db);
		}
	}
	
	class NinetyTwoToNinetyThree extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB92To93(db);
		}
	}
	
	class NinetyThreeToNinetyFour extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB93To94(db);
		}
	}
	
	class NinetyFourToNinetyFive extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB94To95(db);
		}
	}
	
	class NinetyFiveToNinetySix extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB95To96(db);
		}
	}
	
	class NinetySixToNinetySeven extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB96To97(db);
		}
	}
	
	class NinetySevenToNinetyEight extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB97To98(db);
		}
	}
	
	class NinetyEightToNinetyNine extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB98To99(db);
		}
	}
	
	class NinetyNineToOneHundred extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB99To100(db);
		}
	}
	
	class OneHundredToOneHundredAndOne extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB100To101(db);
		}
	}
	
	class OneHundredAndOneToOneHundredAndTwo extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB101To102(db);
		}
	}
	
	class OneHundredAndTwoToOneHundredAndThree extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB102To103(db);
		}
	}
	
	class OneHundredAndThreeToOneHundredAndFour extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB103To104(db);
		}
	}
	
	class OneHundredAndFourToOneHundredAndFive extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB104To105(db);
		}
	}
	
	class OneHundredAndFiveToOneHundredAndSix extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB105To106(db);
		}
	}
	class OneHundredAndSixToOneHundredAndSeven extends UpgradeDB {

		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB106To107(db);
		}

	}
	
	class OneHundredAndSevenToOneHundredAndEight extends UpgradeDB {

		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB107To108(db);
		}

	}
	
	class OneHundredAndEightToOneHundredAndNight extends UpgradeDB {

		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgradeDB108To109(db);
		}

	}
	
	/**
	 * 获取功能表一个元素数据
	 * @param intent
	 * @return
	 */
	private Cursor getFunItem(final Intent intent, SQLiteDatabase db) {
		if (null == intent) {
			return null;
		}
		// 表名
		String table = AppTable.TABLENAME;
		// 查询列数
		String columns[] = { AppTable.INDEX };
		// 排序条件
		String where = AppTable.INTENT + " = " + "'" + ConvertUtils.intentToString(intent) + "'";

		return db.query(table, columns, where, null,null, null, null);
	}
	
	/**
	 * 获取元素在功能表根目录中的索引
	 * 
	 * @param intent
	 *            唯一标识Intent
	 * @return 索引
	 */
	private int getFunItemIndex(final Intent intent, SQLiteDatabase db) {
		Cursor cursor = getFunItem(intent, db);
		int idx = -1;
		if (null != cursor) {
			try {
				final int idxIndex = cursor.getColumnIndex(AppTable.INDEX);
				if (cursor.moveToFirst()) {
					idx = cursor.getInt(idxIndex);
				}
			} finally {
				cursor.close();
			}
		}
		return idx;
	}
	
	/**
	 * 根据intent删除对应的元素，并维护数据库中的mIndex
	 * 
	 * @param intent
	 * @throws DatabaseException
	 */
	private void removeFunItem(Intent intent ,SQLiteDatabase db) throws DatabaseException {
		if (intent == null) {
			return;
		}
		int index = getFunItemIndex(intent, db);
		if (index < 0) {
			return;
		}
		String str = ConvertUtils.intentToString(intent);
		String selection = AppTable.INTENT + " = " + "'" + str + "'";
		// 删除元素
		db.delete(AppTable.TABLENAME, selection, null);
		// 更新mItemInAppIndex
		String sql = "update " + AppTable.TABLENAME + " set " + AppTable.INDEX + " = "
				+ AppTable.INDEX + " - 1 " + " where " + AppTable.INDEX + " > " + index + ";";
		db.execSQL(sql);
	}
	
	/**
	 * 获取内置widget的widgetId
	 * @return
	 */
	private int[] getGoWidgetIdByLayout(SQLiteDatabase db, String widgetLayout) {
		int[] widgetIds = null;
		int widgetId = Integer.MIN_VALUE;
		Cursor cursor = null;
		String where = GoWidgetTable.LAYOUT + " = '" + widgetLayout + "'";
		try {
			cursor = db.query(GoWidgetTable.TABLENAME, null, where, null,null, null, null);
			if (null != cursor) {
				int count = cursor.getCount();
				widgetIds = new int[count];
				cursor.moveToPosition(-1);
				int index = 0;
				while (cursor.moveToNext()) {
					widgetId = cursor.getInt(cursor
							.getColumnIndex(GoWidgetTable.WIDGETID));
					widgetIds[index] = widgetId;
					index++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return widgetIds;
	}

	
	/**
	 * 根据内置widget的widgetId删除表数据
	 * @param db
	 * @param widgetId
	 * @return
	 */
	private int deleteGoWidget(SQLiteDatabase db, int widgetId) {
		String condition = GoWidgetTable.WIDGETID + " = " + widgetId;
		String whereStr = PartToScreenTable.WIDGETID + " = '" + widgetId + "'";
		int count = 0;
		try {
			count = db.delete(GoWidgetTable.TABLENAME, condition, null);
			db.delete(PartToScreenTable.TABLENAME, whereStr, null);
		} catch (Exception e) {
			Log.i("data", "Exception when delete in " + GoWidgetTable.TABLENAME + ", " + condition);
		}
		return count;
	}
}