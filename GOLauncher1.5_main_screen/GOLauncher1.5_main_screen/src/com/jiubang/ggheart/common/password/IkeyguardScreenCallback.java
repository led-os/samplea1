package com.jiubang.ggheart.common.password;

/**
 * 屏保密码锁检查回调接口
 * 
 *
 */
public interface IkeyguardScreenCallback
{
	/**
	 * 验证屏幕密码锁成功
	 */
	void reportSuccessUnlockAttempt();
	/**
	 * 验证屏幕密码锁失败
	 */
	void reportFailedUnlockAttempt();
	
	void reportBackLockAttempt();
}
