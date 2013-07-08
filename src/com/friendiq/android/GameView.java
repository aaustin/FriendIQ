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
	
	int contactIndex;
	Contact contact;
	ImageReady imgReady;
	
	SplitImageMatrix splitImage;
	MatrixReady imageMatrixReady;

	DrawingThread drawingThread;
	
	KeyManager keyManager;
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
		if (!created) {
			this.gameReady = gameReady;
			
			ContactDataAdapter cda = new ContactDataAdapter(context);
			cda.open_for_read();
			if (index < 0) {
				boolean loop = true;
				while (loop) {
					Random ran = new Random();
					contactIndex = ran.nextInt(pHelper.get_friend_count());
					contact = cda.get_contact(contactIndex);
					if (contact.firstname.length() <= KeyManager.NUM_LETTER_ROWS*KeyManager.NUM_LETTERS_IN_ROW)
						loop = false;
				}
			} else {
				contact = cda.get_contact(index);
			}
			cda.close();
			imgReady = new ImageReady();
			contact.download_photo(context, imgReady);
			
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
  	
  	public class ImageReady implements CallBack {
  		public void callback(int threadID) {
  			if (threadID == 0) {
  				ContactDataAdapter cda = new ContactDataAdapter(context);
  				cda.open_for_read();  				
  				boolean loop = true;
				while (loop) {
					Random ran = new Random();
					contactIndex = ran.nextInt(pHelper.get_friend_count());
					contact = cda.get_contact(contactIndex);
					if (contact.firstname.length() <= KeyManager.NUM_LETTER_ROWS*KeyManager.NUM_LETTERS_IN_ROW)
						loop = false;
				}
  				cda.close();
  				imgReady = new ImageReady();
  				contact.download_photo(context, imgReady);
  			} else {
  				imageMatrixReady = new MatrixReady();
  				keyboardReady = new KeyboardReady();
  				
  				splitImage = new SplitImageMatrix(context, height, width);	
  		        splitImage.prepare_matrix(contact, imageMatrixReady);
  		        
  		        keyManager = new KeyManager(context, height, width);
  		        keyManager.prepare_keys(contact.firstname, keyboardReady);
  			}
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
