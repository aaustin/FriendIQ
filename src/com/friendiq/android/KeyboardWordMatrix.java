package com.friendiq.android;

import com.friendiq.android.GameView.KeyboardReady;

import android.content.Context;
import android.view.SurfaceHolder;

public class KeyboardWordMatrix {
	
	int screenWidth;
	SurfaceHolder surfaceHolder;
	Context context;
	
	public KeyboardWordMatrix(Context context, SurfaceHolder surfaceHolder, int screenWidth) {
		this.context = context;
		this.surfaceHolder = surfaceHolder;
		this.screenWidth = screenWidth;
	}
	
	public void prepare_keyboard(int index, KeyboardReady finishedCallback) {
	
		
		finishedCallback.callback(1);
	}

}
