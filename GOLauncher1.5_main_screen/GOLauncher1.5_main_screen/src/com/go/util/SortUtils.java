package com.go.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.Build;

import com.jb.util.pylib.Hanzi2Pinyin;

/**
 * 
 * <br>类描述: 排序工具类
 * <br>功能详细描述:
 * 
 * @author  yangguanxiang
 * @date  [2012-11-22]
 */
public class SortUtils {

	public static final int COMPARE_TYPE_STRING = 0;
	public static final int COMPARE_TYPE_INT = 1;
	public static final int COMPARE_TYPE_LONG = 2;

	// 这个TMD抗不住了
	@SuppressWarnings("unchecked")
	public static <T> void sort(List<T> list, final String method, final Class[] methodArgsClass,
			final Object[] methodArgs, final String order) {
		// TODO: 宽松汉字拼音排序
		Collections.sort(list, new Comparator() {

			@Override
			public int compare(Object object1, Object object2) {
				return doCompare(method, methodArgsClass, methodArgs, order, object1, object2,
						COMPARE_TYPE_STRING);
			}
		});
	}

	private static <T> int doCompare(final String method, final Class[] methodArgsClass,
			final Object[] methodArgs, final String order, Object object1, Object object2,
			int compareType) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			Method compareMethod1 = object1.getClass().getMethod(method, methodArgsClass);
			Method compareMethod2 = object2.getClass().getMethod(method, methodArgsClass);

			if (null == compareMethod1.invoke(object1, methodArgs)
					|| null == compareMethod2.invoke(object2, methodArgs)) {
				return result;
			}

			if (compareType == COMPARE_TYPE_INT) {
				// 按int类型比较
				int value1 = (Integer) compareMethod1.invoke(object1, methodArgs);
				int value2 = (Integer) compareMethod2.invoke(object2, methodArgs);
				if (value1 == value2) {
					result = 0;
					return result;
				}
				if (order != null && "DESC".equals(order)) {
					result = value2 > value1 ? 1 : -1;
				} else {
					result = value1 > value2 ? 1 : -1;
				}

			} else if (compareType == COMPARE_TYPE_LONG) {
				// 按long类型比较
				long value1 = (Long) compareMethod1.invoke(object1, methodArgs);
				long value2 = (Long) compareMethod2.invoke(object2, methodArgs);
				if (value1 == value2) {
					result = 0;
					return result;
				}
				if (order != null && "DESC".equals(order)) {
					result = value2 > value1 ? 1 : -1;
				} else {
					result = value1 > value2 ? 1 : -1;
				}
			} else {
				// 按字符串类型比较
				String str1 = compareMethod1.invoke(object1, methodArgs).toString();
				String str2 = compareMethod2.invoke(object2, methodArgs).toString();
				Collator collator = null;

				/**
				 * @edit by huangshaotao
				 * @date 2012-7-31
				 *       在4.1的系统使用Locale.CHINESE按名称排序时会把汉字排在英文前面（原因未明），
				 *       因此针对4.1或以上系统，使用Locale.ENGLISH
				 */
				if (Build.VERSION.SDK_INT < 16) {
					collator = Collator.getInstance(Locale.CHINESE);
				} else {
					collator = Collator.getInstance(Locale.ENGLISH);
				}

				if (collator == null) {
					collator = Collator.getInstance(Locale.getDefault());
				}
				//
				// collator = Collator.getInstance(Locale.getDefault());
				if (order != null && "DESC".equals(order)) {
					result = collator.compare(str2.toUpperCase(), str1.toUpperCase());
				} else {
					result = collator.compare(str1.toUpperCase(), str2.toUpperCase());
				}
			}

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			// e.printStackTrace();
		}
		return result;
	}


	/**
	 * <br>功能简述:将一个包含有汉字的字符串转换成拼音字符串（如果有的话）
	 * <br>功能详细描述:将一个包含有汉字的字符串转换成拼音字符串（如果有的话）
	 * <br>注意:
	 * @param text 
	 * @return 有可能为null，使用前先判断
	 */
	public static String changeChineseToSpell(final Context context, String text, int rowId) {
		if (text == null || "".equals(text)) {
			return text;
		}

		StringBuffer results = new StringBuffer();
		Hanzi2Pinyin pInstance = Hanzi2Pinyin.getInstance(context, rowId);
		if (pInstance == null) {
			return text;
		}

		int size = text.length();
		for (int i = 0; i < size; i++) {
			// 是汉字则转成拼音，不是则原样转成数组返回
			char key = text.charAt(i);
			if (isChinese(key)) {
				int code = key;
				String[] temp = pInstance.GetPinyin(code);
				if (temp != null && temp.length > 0) {
					results.append(temp[0]);
				}
			} else {
				results.append(String.valueOf(key));
			}
		}
		return results.toString();
	}

	/**
	 * <br>功能简述:判断一个字符是否汉字
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param key
	 * @return
	 */
	private static boolean isChinese(char key) {
		boolean isHanzi = false;
		if (key >= 0x4e00 && key <= 0x9fa5) {
			isHanzi = true;
		}
		return isHanzi;
	}

	private static boolean isContainChinese(String text) {
		boolean ret = false;
		if (text == null || "".equals(text)) {
			ret = false;
		} else {
			int size = text.length();
			for (int i = 0; i < size; i++) {
				if (isChinese(text.charAt(i))) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	/**
	 * <br>功能简述:功能表应用程序排序，汉字会按拼音进行排序
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param list
	 * @param pritMethod
	 * @param method
	 * @param methodArgsClass
	 * @param methodArgs
	 * @param order
	 */
	public static <T> void sortForApps(final Context context, List<T> list,
			final String pritMethod, final String method, final Class[] methodArgsClass,
			final Object[] methodArgs, final String order, final int rawId) {
		Collections.sort(list, new Comparator() {

			@Override
			public int compare(Object object1, Object object2) {
				try {
					Method compareMethod1 = object1.getClass().getMethod(pritMethod);
					Method compareMethod2 = object2.getClass().getMethod(pritMethod);

					// if (null == compareMethod1.invoke(object1, null)
					// || null == compareMethod2.invoke(object2, null)) {
					// return -1;
					// }
					if (null == compareMethod1.invoke(object1)
							|| null == compareMethod2.invoke(object2)) {
						return 0;
					}

					boolean prit1 = (Boolean) compareMethod1.invoke(object1);
					boolean prit2 = (Boolean) compareMethod2.invoke(object2);
					if (prit1 && !prit2) {
						return -1;
					} else if (!prit1 && prit2) {
						return 1;
					}

				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return doCompareForApps(context, method, methodArgsClass, methodArgs, order,
						object1, object2, rawId);
			}
		});
	}
	public static <T> void sortFolderApps(final Context context, List<T> list, final String method,
			final Class[] methodArgsClass, final Object[] methodArgs, final String order, final int rawId) {
		Collections.sort(list, new Comparator() {
			@Override
			public int compare(Object object1, Object object2) {
				return doCompareForApps(context, method, methodArgsClass, methodArgs, order,
						object1, object2, rawId);
			}
		});
	}
	
		private static <T> int doCompareForApps(final Context context, final String method,
			final Class[] methodArgsClass, final Object[] methodArgs, final String order,
			Object object1, Object object2, int rawId) {
		int result = 0;
		try {
			Method compareMethod1 = object1.getClass().getMethod(method, methodArgsClass);
			Method compareMethod2 = object2.getClass().getMethod(method, methodArgsClass);

			if (null == compareMethod1.invoke(object1, methodArgs)
					|| null == compareMethod2.invoke(object2, methodArgs)) {
				return result;
			}

			// 按字符串类型比较
			String str1 = compareMethod1.invoke(object1, methodArgs).toString();
			String str2 = compareMethod2.invoke(object2, methodArgs).toString();
			Collator collator = null;

			//			//如果两个字符串都包含有汉字，则将其转成拼音
			//			if (isContainHanzi(str1)&&isContainHanzi(str2)&&str1.length()>0&&str2.length()>0) {
			//				if (!isChinese(str1.charAt(0))||!isChinese(str2.charAt(0))) {
			//					if (!isChinese(str1.charAt(0))&&!isChinese(str2.charAt(0))) {
			//						str1 = changeChineseToSpell(context, str1);
			//						str2 = changeChineseToSpell(context, str2);
			//					}else if(!isChinese(str1.charAt(0))&&isChinese(str2.charAt(0))){
			//						return 1;
			//					}else if(isChinese(str1.charAt(0))&&!isChinese(str2.charAt(0))){
			//						return -1;
			//					}
			//				}else {
			//					str1 = changeChineseToSpell(context, str1);
			//					str2 = changeChineseToSpell(context, str2);
			//				}				
			//			}

			if (str1.length() > 0 && str2.length() > 0) {
				if (isChinese(str1.charAt(0)) && isChinese(str2.charAt(0))) {
					str1 = changeChineseToSpell(context, str1, rawId);
					str2 = changeChineseToSpell(context, str2, rawId);
				} else if (!isChinese(str1.charAt(0)) && isChinese(str2.charAt(0))) {
					if (order != null && "DESC".equals(order)) {
						return 1;
					} else {
						return -1;
					}
				} else if (isChinese(str1.charAt(0)) && !isChinese(str2.charAt(0))) {
					if (order != null && "DESC".equals(order)) {
						return -1;
					} else {
						return 1;
					}
				} else if (isContainChinese(str1) && isContainChinese(str2)) {
					str1 = changeChineseToSpell(context, str1, rawId);
					str2 = changeChineseToSpell(context, str2, rawId);
				}
			}

			/**
			 * @edit by huangshaotao
			 * @date 2012-7-31
			 *       在4.1的系统使用Locale.CHINESE按名称排序时会把汉字排在英文前面（原因未明），
			 *       因此针对4.1或以上系统，使用Locale.ENGLISH
			 */
			if (Build.VERSION.SDK_INT < 16) {
				collator = Collator.getInstance(Locale.CHINESE);
			} else {
				collator = Collator.getInstance(Locale.ENGLISH);
			}

			if (collator == null) {
				collator = Collator.getInstance(Locale.getDefault());
			}
			//
			// collator = Collator.getInstance(Locale.getDefault());
			if (order != null && "DESC".equals(order)) {
				result = collator.compare(str2.toUpperCase(), str1.toUpperCase());
			} else {
				result = collator.compare(str1.toUpperCase(), str2.toUpperCase());
			}

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			// e.printStackTrace();
		}
		return result;
	}
}
