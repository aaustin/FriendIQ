package com.friendiq.android;

import com.friendiq.android.GameView.KeyboardReady;

import android.content.Context;

public class KeyManager {
	Context context;
	int screenWidth;
	
	KeyboardReady keysReady;
	
	public KeyManager(Context context, int screenWidth) {
		this.context = context;
		this.screenWidth = screenWidth;
	}
	
	public void prepare_keys(String firstname, KeyboardReady keysReady) {
		
		keysReady.callback(1);		
	}

}
