package com.friendiq.android;

import java.util.Random;

import com.friendiq.android.GameActivity.SurfaceViewReady;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	PrefHelper pHelper;
	Context context;
	int width;
	int height;
	boolean created;
	boolean prepared;
	
	Contact contact;
	
	SplitImageMatrix splitImage;
	MatrixReady imageMatrixReady;
<<<<<<< HEAD
	DrawingThread drawingThread;
	KeyManager keyManager;
=======
	
	KeyboardWordMatrix keyboardMatrix;
>>>>>>> 299658c5559030b062ffb3916fd59a5396796d74
	KeyboardReady keyboardReady;
	
	SurfaceViewReady gameReady;
		
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		created = false;
		prepared = false;
		pHelper = new PrefHelper(context);
	}
	
	public boolean initialize_game(int index, SurfaceViewReady gameReady) {
		if (created) {
			this.gameReady = gameReady;
			
			ContactDataAdapter cda = new ContactDataAdapter(context);
			cda.open_for_read();
			if (index < 0) {
				Random ran = new Random();
				contact = cda.get_contact(ran.nextInt(pHelper.get_friend_count()));
			} else {
				contact = cda.get_contact(index);
			}
			cda.close();
			
			imageMatrixReady = new MatrixReady();
			keyboardReady = new KeyboardReady();
			
			splitImage = new SplitImageMatrix(context, width);	
	        splitImage.prepare_matrix(index, imageMatrixReady);
	        
<<<<<<< HEAD
	        keyManager = new KeyManager(context, width);
	        keyManager.prepare_keys(contact.firstname, keyboardReady);
=======
	        keyboardMatrix = new KeyboardWordMatrix(context, getHolder(), width);
	        keyboardMatrix.prepare_keyboard(index, keyboardReady);
>>>>>>> 299658c5559030b062ffb3916fd59a5396796d74
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
  		if (prepared) {
  			
  			gameReady.callback(1);
  		} else
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
