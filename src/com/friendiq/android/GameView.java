package com.friendiq.android;

import com.friendiq.android.GameActivity.SurfaceViewReady;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	Context context;
	int width;
	int height;
	boolean created;
	boolean prepared;
	
	SplitImageMatrix splitImage;
	MatrixReady imageMatrixReady;
	
	
	KeyboardReady keyboardReady;
	
	SurfaceViewReady gameReady;
		
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		created = false;
		prepared = false;
	}
	
	public boolean initialize_game(int index, SurfaceViewReady gameReady) {
		if (created) {
			this.gameReady = gameReady;
			
			imageMatrixReady = new MatrixReady();
			keyboardReady = new KeyboardReady();
			
			splitImage = new SplitImageMatrix(context, getHolder(), width);	
	        splitImage.prepare_matrix(index, imageMatrixReady);
		}
        
		return created;
	}

	// interface implementation image matrix ready callback
  	public class KeyboardReady implements CallBack {
  		public void callback(int threadID) {
  			game_is_ready();
  		}
  	}
  	
  	// interface implementation image matrix ready callback
  	public class MatrixReady implements CallBack {
  		public void callback(int threadID) {
  			game_is_ready();
  		}
  	}
  	
  	private void game_is_ready() {
  		if (prepared)
  			gameReady.callback(1);
  		else
  			prepared = true;
  	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		this.width = width;
		this.height = height;
		created = true;
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
