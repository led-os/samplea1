package com.go.util;


/**
 * 对数组进行操作的工具类
 * 
 * @author wangzhuobin
 * 
 */
public class ArrayUtils {

	/**
	 * 从int数组里面删除某一项 返回一个全新的数组引用 如果源数组没有包含要删除的项，就返回源数组
	 * 
	 * @param array
	 * @param deletValue
	 * @return
	 */
	public static int[] delete(int[] array, int deletValue) {
		int[] result = null;
		if (array != null) {
			int i = 0;
			int length = array.length;
			// 先找找有没有那一项
			for (; i < length; i++) {
				if (deletValue == array[i]) {
					break;
				}
			}
			if (i < length) {
				// 如果有那一项
				result = new int[length - 1];
				for (int j = 0, k = 0; j < length; j++) {
					if (j != i) {
						result[k] = array[j];
						k++;
					} else {
						continue;
					}
				}
			} else {
				// 如果没有那一项
				result = array;
			}
		}
		return result;
	}
	
	public static CharSequence[] stringArrayToCharSequenceArray(String[] stringArray) {
		if (stringArray == null) {
			return null;
		}
		CharSequence[] charSequenceArray = new CharSequence[stringArray.length];
		for (int i = 0; i < stringArray.length; i++) {
			charSequenceArray[i] = stringArray[i];
		}
		return charSequenceArray;
	}
	
	public static CharSequence[] intArrayToCharSequenceArray(int[] intArray) {
		if (intArray == null) {
			return null;
		}
		CharSequence[] charSequenceArray = new CharSequence[intArray.length];
		for (int i = 0; i < intArray.length; i++) {
			charSequenceArray[i] = String.valueOf(intArray[i]);
		}
		return charSequenceArray;
	}
	
	public static int[] charSequenceArrayToIntArray(CharSequence[] charSequenceArray) {
		if (charSequenceArray == null) {
			return null;
		}
		int[] intArray = new int[charSequenceArray.length];
		for (int i = 0; i < charSequenceArray.length; i++) {
			intArray[i] = Integer.parseInt(charSequenceArray[i].toString());
		}
		return intArray;
	}
	
	public static int[] stringArrayToIntArray(String[] stringArray) {
		if (stringArray == null) {
			return null;
		}
		int[] intArray = new int[stringArray.length];
		for (int i = 0; i < stringArray.length; i++) {
			intArray[i] = Integer.parseInt(stringArray[i]);
		}
		return intArray;
	}
}
