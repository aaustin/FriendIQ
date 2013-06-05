package com.friendiq.android;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	Context context;
	
	SplitImageMatrix splitImage;
	MatrixReady imageMatrixReady;
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		imageMatrixReady = new MatrixReady();
	}
	

	
	// interface implementation image matrix ready callback
  	public class MatrixReady implements CallBack {
  		public void callback(int threadID) {
  			
  		}
  	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        splitImage = new SplitImageMatrix(context, holder, width);	
        splitImage.prepare_matrix(index, preparedReady)
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	

}
