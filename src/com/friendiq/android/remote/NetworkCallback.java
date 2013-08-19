package com.friendiq.android.remote;

import org.json.JSONObject;

public interface NetworkCallback {
	public void finished(JSONObject serverResponse);
}
