package com.jiubang.ggheart.apps.desks.diy;

import android.app.Activity;

/**
 * define the interface for message command
 * 
 * @author Hanson
 * 
 */

public class MessageCommand implements ICommand {
	private IOperation mOperation;
	private String mMethodName;

	@Override
	public boolean execute(Activity activity) {
		// pass param to operation
		return mOperation.perform(activity, mMethodName);
	}

	public MessageCommand() {

	}

	public MessageCommand(IOperation moduleOperation, String mMethodName) {
		this.mOperation = moduleOperation;
		this.mMethodName = mMethodName;
	}

	public IOperation getOperation() {
		return mOperation;
	}

	public void setOperation(IOperation operation) {
		this.mOperation = operation;
	}

	@Override
	public void setmCommandParam(CommandParam param) {
		mOperation.setmCommandParam(param);
	}

}
