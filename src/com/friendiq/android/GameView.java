package com.friendiq.android;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.friendiq.android.GameActivity.SurfaceViewReady;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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
	Timer contactTimer;
	boolean contactReady;
	
	
	SplitImageMatrix splitImage;
	MatrixReady imageMatrixReady;

	DrawingThread drawingThread;
	
	KeyManager keyManager;
	KeyboardReady keyboardReady;
	
	SurfaceViewReady gameReady;
		
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		
		this.context = context;
		contactReady = false;
		created = false;
		prepared = false;
		pHelper = new PrefHelper(context);
		
		imageMatrixReady = new MatrixReady();
		keyboardReady = new KeyboardReady();
		
		drawingThread = new DrawingThread(context, getHolder(), splitImage, keyManager);
	}
	
	public void initialize_game(int index, SurfaceViewReady gameReady) {
	
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
		
		Log.i(getClass().getSimpleName(), "about to download pic for name = " + contact.firstname);
		imgReady = new ImageReady();
		new Thread(new Runnable() {
			@Override
			public void run() {
				contact.download_photo(context, imgReady);
			}
		}).start();
		
		contactTimer = new Timer();
		contactTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (contactReady && created) {
					contactTimer.cancel();
					
					Log.i(DatabaseHelper.class.getName(),"timer hit, creating rects and launching thread");
					
					splitImage = new SplitImageMatrix(context, height, width);	 
			        keyManager = new KeyManager(context, height, width);  	
					
					splitImage.prepare_matrix(contact, imageMatrixReady);
					keyManager.prepare_keys(contact.firstname, keyboardReady);
					
					drawingThread.start_drawing();
					drawingThread.start();
				}
			}				
		}, 0, 200);

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
  				Log.i(getClass().getSimpleName(), "failed, next = " + contact.firstname);
  				new Thread(new Runnable() {
					@Override
					public void run() {
						contact.download_photo(context, imgReady);
					}  					
  				}).start();
  				
  			} else {
  				Log.i(getClass().getSimpleName(), "contact downloaded, name = " + contact.firstname);
  				contactReady = true;
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
	public boolean onTouchEvent(MotionEvent ev) {		
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				break;
		}
		return true;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(DatabaseHelper.class.getName(),"surface changed");
		this.width = width;
		this.height = height;
		created = true;		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(getClass().getSimpleName(), "surface created");
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(getClass().getSimpleName(), "surface destroyed");
		drawingThread.stop_drawing();
		
	}
	

}
