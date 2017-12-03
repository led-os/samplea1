package com.jiubang.ggheart.apps.desks.diy;

import android.app.Activity;

/**
 * define the interface for command
 * 
 * @author Hanson
 * 
 */
public interface ICommand {

	public boolean execute(Activity activity);
	public void setmCommandParam(CommandParam param);
}
