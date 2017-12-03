package com.jiubang.ggheart.apps.desks.purchase.getjar;

import com.getjar.sdk.GetjarConstants;

import android.content.Intent;

/** A simple wrapper class for {@link Intent}s that come form Getjar, providing basic equality comparison. */
public class GetjarIntentWrapper {

	private final Intent mWrappedIntent;

	/** Constructs an instance wrapping the given {@link Intent}. */
	public GetjarIntentWrapper(Intent intent) {
		if (intent == null) {
			throw (new IllegalArgumentException("'intent' can not be NULL or empty"));
		}
		if (!intent.getBooleanExtra(GetjarConstants.INTENT_KEY, false)) {
			throw (new IllegalArgumentException("'intent' must be a Getjar Intent"));
		}
		this.mWrappedIntent = intent;
	}

	/** Returns the {@link Intent} wrapped by this instance. */
	public Intent getIntent() {
		return (this.mWrappedIntent);
	}

	@Override
	public boolean equals(Object other) {
		if ((other == null) || (!(other instanceof GetjarIntentWrapper))) {
			return (false);
		}
		return (this.hashCode() == other.hashCode());
	}

	@Override
	public int hashCode() {
		return (this.toString().hashCode());
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder("");
		for (String key : this.mWrappedIntent.getExtras().keySet()) {
			buffer.append(key);
			buffer.append("=");
			Object value = this.mWrappedIntent.getExtras().get(key);
			if (value != null) {
				buffer.append(value.toString());
			}
			buffer.append(",");
		}
		return (buffer.toString());
	}

}
