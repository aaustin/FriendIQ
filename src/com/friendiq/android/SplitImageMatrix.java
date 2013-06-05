package com.friendiq.android;


import com.friendiq.android.GameView.MatrixReady;

import android.content.Context;
import android.view.SurfaceHolder;

public class SplitImageMatrix {
	private static final int GAP_WIDTH = 5;
	
	SurfaceHolder surfaceHolder;
	Context context;
	int screenWidth;
	
	ImageGrabber imgGrabber;
	ImageAcquired acquiredCallback;
	MatrixReady preparedCallback;
	
	public SplitImageMatrix(Context context, SurfaceHolder surfaceHolder, int screenWidth) {
		this.surfaceHolder = surfaceHolder;
		this.context = context;
		this.screenWidth = screenWidth;
		
		acquiredCallback = new ImageAcquired();
		
		imgGrabber = new ImageGrabber(context);
	}
	
	public void prepare_matrix(int index, MatrixReady preparedReady) {
		imgGrabber.grab_image(index, acquiredCallback);
		this.preparedCallback = preparedReady;
	}
	
	// interface implementation image download callback
  	public class ImageAcquired implements CallBack {
  		public void callback(int threadID) {
  			init_matrix();
  		}
  	}
  	
  	private void init_matrix() {
  		
  		
  		preparedCallback.callback(1);
  	}

}
