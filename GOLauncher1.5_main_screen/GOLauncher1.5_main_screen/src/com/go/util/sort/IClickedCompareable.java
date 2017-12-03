package com.go.util.sort;

import android.content.Context;

/**
 * 想比较点击次数的Info，请接上我吧
 * @author wuziyi
 *
 */
public interface IClickedCompareable extends IBaseCompareable {
	public int getClickedCount(Context context);
}
