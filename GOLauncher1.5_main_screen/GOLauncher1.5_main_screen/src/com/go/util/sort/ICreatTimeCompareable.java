package com.go.util.sort;

import android.content.pm.PackageManager;

/**
 * 想比较创建时间的Info，请接上我吧
 * @author wuziyi
 *
 */
public interface ICreatTimeCompareable extends IBaseCompareable {
	public long getTime(PackageManager pm);
}
