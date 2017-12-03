package com.jiubang.ggheart.apps.desks.diy;

import android.app.Activity;

/**
 * define the interface for Operation
 * 
 * @author Hanson
 * 
 */
public interface IOperation {

	
	public boolean perform(Activity activity, String method);
	public void setmCommandParam(CommandParam param);
}
