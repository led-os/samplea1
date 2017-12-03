package com.jiubang.ggheart.plugin;

import java.lang.reflect.Field;

import android.util.Log;

/**
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  huanglun
 * @date  [2013-2-4]
 */
public class PluginClassLoader extends ClassLoader {
	private static final String TAG = "PluginClassLoader";

//	private static Field sDexsField;
	private static Field sParent;
	private final ClassLoader mClassLoader;
//	private DexFile[] mDexFiles;

	static {
		try {
//			sDexsField = PathClassLoader.class.getDeclaredField("mDexs");
//			sDexsField.setAccessible(true);
			
			sParent = ClassLoader.class.getDeclaredField("parent");
			sParent.setAccessible(true);
			
		} catch (SecurityException e) {
			Log.w(TAG, "get parent filed from ClassLoader failed", e);
		} catch (NoSuchFieldException e) {
			Log.w(TAG, "get parent filed from ClassLoader failed", e);
		}
	}

	/**
	 *  对于插件来说，self为Package Context的ClassLoader，
	 *  通过指定parent来加载共享的代码
	 */
	public PluginClassLoader(ClassLoader self, ClassLoader parent) {
		super(parent);
		mClassLoader = self;
		if (mClassLoader != parent) {
			setParent(self, parent); // 设置self的parent
		}
	}
	
	private static void setParent(ClassLoader self, ClassLoader parent) {
		try {
			sParent.set(self, parent);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

//	private DexFile[] getDexFiles() {
//		if (mDexFiles == null) {
//			try {
//				mClassLoader.loadClass("xxx.xxx.xxx");
//				mDexFiles = (DexFile[]) sDexsField.get(mClassLoader);
//			} catch (ClassNotFoundException e) {
//				Log.w(TAG, "get mDexs failed", e);
//			} catch (IllegalArgumentException e) {
//				Log.w(TAG, "get mDexs failed", e);
//			} catch (IllegalAccessException e) {
//				Log.w(TAG, "get mDexs failed", e);
//			}
//		}
//		return mDexFiles;
//	}

//	public Class<?> getCacheClass(String className) {
//		DexFile[] dexFiles = getDexFiles();
//		if (dexFiles != null) {
//			int length = dexFiles.length;
//			Class<?> clazz = null;
//			for (int i = 0; i < length && clazz == null; i++) {
//				if (dexFiles[i] != null) {
//					clazz = dexFiles[i].loadClassBinaryName(className, this);
//				}
//			}
//			return clazz;
//		}
//		return null;
//	}

	@Override
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		//		Class<?> clazz = null;
		//		if (mClassLoader instanceof PathClassLoader) {
		//			clazz = getCacheClass(className);
		//		}
		//		
		//		if (clazz == null) {
		//			try {
		//				clazz = mClassLoader.loadClass(className);
		//			} catch (ClassNotFoundException e) {
		//				Log.w(TAG, "findClass " + className + " error", e);
		//			}
		//		}
		//		return clazz;
		return mClassLoader.loadClass(className);
	}

//	@Override
//	protected Class<?> loadClass(String className, boolean resolve) {
//		Class<?> clazz;
//		try {
//			clazz = getParent().loadClass(className);
//			if (clazz == null) {
//				clazz = findClass(className);
//			}
//			return clazz;
//		} catch (ClassNotFoundException e) {
//			Log.w(TAG, "loadClass " + className + " error", e);
//			return findLoadedClass(className);
//		}
//	}

}
