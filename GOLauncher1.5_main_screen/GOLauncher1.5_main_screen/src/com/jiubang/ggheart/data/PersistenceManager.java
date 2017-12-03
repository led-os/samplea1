package com.jiubang.ggheart.data;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Handler;
import android.os.HandlerThread;

import com.jiubang.ggheart.appgame.base.database.AppGameDataBaseHelper;
import com.jiubang.ggheart.data.statistics.StatisticsDataBaseHelper;

/**
 * 
 * <br>类描述: 持久化管理者
 * <br>功能详细描述: 负责桌面持久化管理，包括异步和同步持久化机制
 * 
 * @author  yangguanxiang
 * @date  [2012-12-14]
 */
public class PersistenceManager {
	private static HashMap<String, PersistenceManager> sInstancePool = new HashMap<String, PersistenceManager>();
	public static final String DB_ANDROID_HEART = "androidheart.db";
	public static final String DB_APPGAME_CENTER = "appgamecenter.db";
	public static final String DB_LAUNCHERS = "launchers.db";
	public final static String DB_APP_CLASSIFY = "appclassify.db";
	private SQLiteOpenHelper mDbHelper;
	private String mDbName;
	private HandlerThread mDbThread;
	private Handler mDbHandler;
	// 可以执行多表关联查询
	private SQLiteQueryBuilder mSqlQB = null;
		
	private PersistenceManager(String dbName, SQLiteOpenHelper helper) {
		mDbName = dbName;
		mDbHelper = helper;
		mDbThread = new HandlerThread("Thread-db-" + mDbName);
		mDbThread.start();
		mDbHandler = new Handler(mDbThread.getLooper());
		mSqlQB = new SQLiteQueryBuilder();
	}

	public synchronized static PersistenceManager getInstance(Context context, String dbName) {
		if (!sInstancePool.containsKey(dbName)) {
			if (DB_ANDROID_HEART.equals(dbName)) {
				//完全舍弃DataProvider之前，都不能单独new DatabaseHelper，必须经由DataProvider获取DatabaseHelper对象，否则DB可能被锁
				//				sInstancePool.put(dbName, new PersistenceManager(dbName, new DatabaseHelper(
				//						context, dbName, DatabaseHelper.getDB_CUR_VERSION())));
				SQLiteOpenHelper helper = DataProvider.getInstance(context).getDatabaseHelper();
				sInstancePool.put(dbName, new PersistenceManager(dbName, helper));
			} else if (DB_APPGAME_CENTER.equals(dbName)) {
				sInstancePool.put(dbName, new PersistenceManager(dbName, new AppGameDataBaseHelper(
						context)));
			} else if (DB_LAUNCHERS.equals(dbName)) {
				sInstancePool.put(dbName, new PersistenceManager(dbName,
						new StatisticsDataBaseHelper(context)));
			} else if (DB_APP_CLASSIFY.equals(dbName)) {
				sInstancePool.put(dbName, new PersistenceManager(dbName,
						new AppClassifyDatabaseHelper(context)));
			}
		}
		return sInstancePool.get(dbName);
	}

	/**
	 * <br>功能简述: 把数据库设置成MODE_WORLD_READABLE
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	public boolean openDBWithWorldReadable(Context context) {
		try {
			mDbHelper.close();
			if (context.openOrCreateDatabase(mDbName, Context.MODE_WORLD_READABLE, null) == null) {
				return false;
			}
		} catch (Exception e) {
			mDbHelper.close();
			return false;
		}
		return true;
	}

	/**
	 * <br>功能简述: 返回当前数据库是否是新创建的
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public boolean isNewDB() {
		boolean isNewDB = false;
		if (DB_ANDROID_HEART.equals(mDbName)) {
			isNewDB = ((DatabaseHelper) mDbHelper).isNewDB();
		}
		return isNewDB;
	}

	/**
	 * <br>功能简述: 单表查询
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param tableName
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	public Cursor query(String tableName, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
	}

	/**
	 * <br>功能简述: 单表查询
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param tableName
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param sortOrder
	 * @return
	 * @throws DatabaseException 
	 */
	public Cursor query(String tableName, String[] projection, String selection,
			String[] selectionArgs, String groupBy, String having, String sortOrder) {
		Cursor cur = null;
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		if (db != null) {
			try {
				cur = db.query(tableName, projection, selection, selectionArgs, groupBy, having,
						sortOrder);
			} catch (Throwable t) {
				DatabaseAccessErrorHandler.handle(mDbName, t);
			}
		}
		return cur;
	}

	/**
	 * 用于多表查询
	 */
	public Cursor queryCrossTables(String tableNames, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor result = null;
		synchronized (mSqlQB) {
			mSqlQB.setTables(tableNames);
			try {
				SQLiteDatabase db = mDbHelper.getReadableDatabase();
				result = mSqlQB.query(db, projection, selection, selectionArgs, null, null,
						sortOrder);
			} catch (Throwable t) {
				DatabaseAccessErrorHandler.handle(mDbName, t);
			}
		}
		return result;
	}
	
	/**
	 * <br>功能简述: 插入新数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param tableName
	 * @param values
	 * @return
	 * @throws DatabaseException
	 */
	public synchronized long insert(final String tableName, final ContentValues values)
			throws DatabaseException {
		if (values != null) {
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			if (db != null) {
				try {
					return db.insert(tableName, null, values);
				} catch (Exception e) {
					throw new DatabaseException(e);
				} catch (Throwable t) {
					DatabaseAccessErrorHandler.handle(mDbName, t);
				}
			}
		}
		return -1;
	}

	/**
	 * <br>功能简述: 异步插入新数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param tableName
	 * @param values
	 * @param callback
	 */
	public synchronized void insertAsync(final String tableName, final ContentValues values,
			final IAsyncPersistenceCallback callback) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		if (db.inTransaction()) {
			try {
				insert(tableName, values);
			} catch (DatabaseException e) {
				//do nothing
			}
		} else {
			Runnable run = new Runnable() {

				@Override
				public void run() {
					boolean success = false;
					long rowId = -1;
					DatabaseException exception = null;
					try {
						rowId = insert(tableName, values);
						success = true;
					} catch (DatabaseException e) {
						exception = e;
					}
					if (callback != null) {
						callback.callback(success, 0, rowId, exception);
					}
				}
			};
			postPersistenceJob(run);
		}
	}

	/**
	 * <br>功能简述: 更新数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param tableName
	 * @param values
	 * @param selection
	 * @param selectionArgs
	 * @return
	 * @throws DatabaseException
	 */
	public synchronized int update(final String tableName, final ContentValues values,
			final String selection, final String[] selectionArgs) throws DatabaseException {
		if (values != null) {
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			if (db != null) {
				try {
					return db.update(tableName, values, selection, selectionArgs);
				} catch (Exception e) {
					throw new DatabaseException(e);
				} catch (Throwable t) {
					DatabaseAccessErrorHandler.handle(mDbName, t);
				}
			}
		}
		return -1;
	}

	/**
	 * <br>功能简述: 异步更新数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param tableName
	 * @param values
	 * @param selection
	 * @param selectionArgs
	 * @param callback
	 */
	public synchronized void updateAsync(final String tableName, final ContentValues values,
			final String selection, final String[] selectionArgs,
			final IAsyncPersistenceCallback callback) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		if (db.inTransaction()) {
			try {
				update(tableName, values, selection, selectionArgs);
			} catch (DatabaseException e) {
				//do nothing
			}
		} else {
			Runnable run = new Runnable() {

				@Override
				public void run() {
					boolean success = false;
					int rows = 0;
					DatabaseException exception = null;
					try {
						rows = update(tableName, values, selection, selectionArgs);
						success = true;
					} catch (DatabaseException e) {
						exception = e;
					}
					if (callback != null) {
						callback.callback(success, rows, -1, exception);
					}
				}
			};
			postPersistenceJob(run);
		}
	}

	/**
	 * <br>功能简述: 删除数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param tableName
	 * @param selection
	 * @param selectionArgs
	 * @return
	 * @throws DatabaseException
	 */
	public synchronized int delete(final String tableName, final String selection,
			final String[] selectionArgs) throws DatabaseException {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		if (db != null) {
			try {
				return db.delete(tableName, selection, selectionArgs);
			} catch (Exception e) {
				throw new DatabaseException(e);
			} catch (Throwable t) {
				DatabaseAccessErrorHandler.handle(mDbName, t);
			}
		}
		return 0;
	}

	/**
	 * <br>功能简述: 异步删除数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param tableName
	 * @param selection
	 * @param selectionArgs
	 * @param callback
	 */
	public synchronized void deleteAsync(final String tableName, final String selection,
			final String[] selectionArgs, final IAsyncPersistenceCallback callback) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		if (db.inTransaction()) {
			try {
				delete(tableName, selection, selectionArgs);
			} catch (DatabaseException e) {
				//do nothing
			}
		} else {
			Runnable run = new Runnable() {

				@Override
				public void run() {
					boolean success = false;
					int rows = 0;
					DatabaseException exception = null;
					try {
						rows = delete(tableName, selection, selectionArgs);
						success = true;
					} catch (DatabaseException e) {
						exception = e;
					}
					if (callback != null) {
						callback.callback(success, rows, -1, exception);
					}
				}
			};
			postPersistenceJob(run);
		}
	}

	/**
	 * <br>功能简述: 执行相应的SQL语句
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param sql
	 * @throws DatabaseException
	 */
	public synchronized void exec(final String sql) throws DatabaseException {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} catch (Throwable t) {
			DatabaseAccessErrorHandler.handle(mDbName, t);
		}
	}

	/**
	 * <br>功能简述: 异步执行相应的SQL语句
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param sql
	 * @param callback
	 */
	public synchronized void execAsync(final String sql, final IAsyncPersistenceCallback callback) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		if (db.inTransaction()) {
			try {
				exec(sql);
			} catch (DatabaseException e) {
				//do nothing
			}
		} else {
			Runnable run = new Runnable() {

				@Override
				public void run() {
					boolean success = false;
					DatabaseException exception = null;
					try {
						exec(sql);
						success = true;
					} catch (DatabaseException e) {
						exception = e;
					}
					if (callback != null) {
						callback.callback(success, 0, -1, exception);
					}
				}
			};
			postPersistenceJob(run);
		}
	}

	public synchronized void beginTransaction() {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.beginTransaction();
	}

	public synchronized void endTransaction() {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.endTransaction();
	}

	public synchronized void setTransactionSuccessful() {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.setTransactionSuccessful();
	}
	
	/**
	 * 表是否存在
	 * @param tableName
	 * @return
	 */
	public boolean isTableExist(String tableName) {
		boolean result = false;
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		if (db != null) {
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
		}
		return result;
	}

	/**
	 * 把一个持久化过程post到DB异步线程上跑，常用于整个事务过程放到异步线程中执行
	 * @param run
	 */
	public void postPersistenceJob(Runnable run) {
		mDbHandler.post(run);
	}

	/**
	 * 
	 * <br>类描述: 异步持久化回调接口
	 * <br>功能详细描述: 每次异步操作执行后都会回调该接口的callback方法
	 * 
	 * @author  yangguanxiang
	 * @date  [2012-12-14]
	 */
	public static interface IAsyncPersistenceCallback {
		public void callback(boolean success, int rowsAffected, long rowId,
				DatabaseException exception);
	}

}
