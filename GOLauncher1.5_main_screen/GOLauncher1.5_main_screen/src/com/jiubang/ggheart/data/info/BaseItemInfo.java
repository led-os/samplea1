package com.jiubang.ggheart.data.info;

import com.go.util.BroadCaster;

/**
 * 数据抽象
 * 
 * @author guodanyang
 * 
 */
public class BaseItemInfo extends BroadCaster implements BroadCaster.BroadCasterObserver {
	@Override
	public void onBCChange(int msgId, int param, Object ...object) {
		broadCast(msgId, param, object);
	}
}
