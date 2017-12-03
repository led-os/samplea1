package com.jiubang.ggheart.zeroscreen.search.contact.util;
import java.util.Comparator;

import com.jiubang.ggheart.zeroscreen.search.contact.Content;

/**
 * 按关键字key 降序排列
 * @author zhouchaohong
 *
 */
public class ContentDescComparator implements Comparator<Content> 
{
	@Override
	public int compare(Content left, Content right) 
	{
		if (left.getKey() > right.getKey()) 
		{
			return -1;
		} else if (left.getKey() == right.getKey()) {
			return 0;
		} else { 
			return 1;
		} 
	}
}
