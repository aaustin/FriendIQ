package com.friendiq.android;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.friendiq.android.GameActivity.SurfaceViewReady;
import com.friendiq.android.finish.FinishActivity;
import com.friendiq.android.setup.ContactDataAdapter;
import com.friendiq.android.setup.DatabaseHelper;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

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
	
	int selectedKey;
	int selectedGuessKey;
	int selectedSectionX;
	int selectedSectionY;	
	
	SplitImageMatrix splitImage;
	MatrixReady imageMatrixReady;
	GameView gameView;
	TextView txtCoins;

	DrawingThread drawingThread;
	
	KeyManager keyManager;
	KeyboardReady keyboardReady;
	
	SurfaceViewReady gameReady;
		
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		gameView = this;
		
		this.context = context;
		contactReady = false;
		created = false;
		prepared = false;
		pHelper = new PrefHelper(context);
		
		imageMatrixReady = new MatrixReady();
		keyboardReady = new KeyboardReady();
	}
	
	public void initialize_game(int index, SurfaceViewReady gameReady, final ImageView portraitView, final TextView txtCoins) {
		this.gameReady = gameReady;			
		this.txtCoins = txtCoins;
		
		ContactDataAdapter cda = new ContactDataAdapter(context);
		cda.open_for_read();
		if (index < 0) {
			boolean loop = true;
			while (loop) {
				Random ran = new Random();
				contactIndex = ran.nextInt(pHelper.get_friend_count());
				Log.i(getClass().getSimpleName(), "trying index = " + contactIndex);
				contact = cda.get_contact(contactIndex);
				if (contact.firstname.length() <= KeyManager.NUM_LETTERS_IN_ROW)
					loop = false;
			}
		} else {
			contact = cda.get_contact(index);
		}
		cda.close();
		
		Log.i(getClass().getSimpleName(), "about to download pic for name = " + contact.firstname + " and index = " + contact.index);
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
					
					splitImage = new SplitImageMatrix(context, height, width, portraitView, gameView, txtCoins);	 
			        keyManager = new KeyManager(context, height, width, txtCoins);  	
			        
					drawingThread = new DrawingThread(context, getHolder(), splitImage, keyManager);

					splitImage.prepare_matrix(contact, imageMatrixReady);
					keyManager.prepare_keys(contact.firstname, keyboardReady);					
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
					Log.i(getClass().getSimpleName(), "trying index = " + contactIndex);
					contact = cda.get_contact(contactIndex);
					if (contact.firstname.length() <= KeyManager.NUM_LETTER_ROWS*KeyManager.NUM_LETTERS_IN_ROW-3 && !contact.firstname.contains(" "))
						loop = false;
				}
  				cda.close();
  				imgReady = new ImageReady();
  				Log.i(getClass().getSimpleName(), "failed, next = " + contact.firstname + " at index = " + contact.index);
  				new Thread(new Runnable() {
					@Override
					public void run() {
						contact.download_photo(context, imgReady);
					}  					
  				}).start();
  				
  			} else {
  				Log.i(getClass().getSimpleName(), "contact downloaded, name = " + contact.firstname);
  				contact.firstname = contact.firstname.toUpperCase();
  				contactReady = true;
  			}
  		}
  	}
  	
  	public boolean already_prepared_but_not_drawing() {
  		return prepared && !drawingThread.run;
  	}
  	
  	// all items are prepared - start drawing
  	private void game_is_ready() {
  		if (prepared) {  			
  			gameReady.callback(contactIndex);
  			drawingThread.start_drawing();
			drawingThread.start();
  		} else
  			prepared = true;
  	}
  	
  	public void resume_game() {
		drawingThread = new DrawingThread(context, getHolder(), splitImage, keyManager);
  		drawingThread.start_drawing();
  		drawingThread.start();
  	}
  	
  	private void game_is_word_finished() {
  		// handle finished game stuff
		Log.i(getClass().getSimpleName(), "FINISHED GAME");
		Intent intent = new Intent(context, FinishActivity.class);
		intent.putExtra("userid", contact.index);
		intent.putExtra("firstname", contact.firstname);
		String name = "";
		for (int i = 0; i < keyManager.guessLetters.length; i++)
			name = name + keyManager.guessLetters[i];
		intent.putExtra("guessname", name);
		intent.putExtra("image", false);
		context.startActivity(intent);
  	}
  	
  	private void game_is_pic_finished() {
  		Intent intent = new Intent(context, FinishActivity.class);
		intent.putExtra("userid", contact.datasourceid);
		intent.putExtra("firstname", contact.firstname);
		String name = "";
		for (int i = 0; i < keyManager.guessLetters.length; i++)
			name = name + keyManager.guessLetters[i];
		intent.putExtra("guessname", name);
		intent.putExtra("image", true);
		context.startActivity(intent);
  	}
	
  	@Override
	public boolean onTouchEvent(MotionEvent ev) {		
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.i(getClass().getSimpleName(), "down press");

				selectedKey = -1;
				selectedSectionX = -1;
				selectedSectionY = -1;
				int xDown = (int) ev.getX();
				int yDown = (int) ev.getY();
				
				for (int i = 0; i < keyManager.sourceLetters.length; i++)
					if (keyManager.sourceLetters[i].contains(xDown, yDown)) {
						Log.i(getClass().getSimpleName(), "hit source letter");
						selectedKey = i;
						keyManager.availablePressed[i] = true;
						break;
					}
				if (selectedKey < 0) {
					for (int i = 0; i < keyManager.guessLetters.length; i++) {
						if (keyManager.guessNameLetters[i].contains(xDown, yDown)) {
							Log.i(getClass().getSimpleName(), "hit guess letter");
							selectedGuessKey = i;
							keyManager.guessPressed[i] = true;
							break;
						}
					}
				}
				if (selectedKey < 0 && selectedGuessKey < 0) {
					for (int x = 0; x < SplitImageMatrix.NUMBER_SQUARE; x++) {
						for (int y = 0; y < SplitImageMatrix.NUMBER_SQUARE; y++) {		
							if (splitImage.imgMatrix[x][y].dest.contains(xDown, yDown)) {
								Log.i(getClass().getSimpleName(), "selected img section");
								selectedSectionX = x;
								selectedSectionY = y;
								break;
							}
						}
						if (selectedSectionX >= 0)
							break;
					}
				}				
				
				if (selectedSectionX < 0 && selectedKey < 0 && selectedGuessKey < 0) {
					Log.i(getClass().getSimpleName(), "didn't hit any main parts");

					if (keyManager.cmdDeleteLetter.contains(xDown, yDown)) {
						Log.i(getClass().getSimpleName(), "hit delete letter");
						keyManager.delete_letter();
						//drawingThread.redraw();
					} else if (keyManager.cmdGiveLetter.contains(xDown, yDown)) {
						Log.i(getClass().getSimpleName(), "hit give letter");
						boolean finish = keyManager.give_letter();
						//drawingThread.redraw();
						if (finish)
							game_is_word_finished();						
					} else if (splitImage.cmdFlashPicture.contains(xDown, yDown)) {
						Log.i(getClass().getSimpleName(), "hit flash image");
						splitImage.flash_image();
					}
					
				}
				
				drawingThread.redraw();
				break;
			case MotionEvent.ACTION_UP:
				if (selectedKey >= 0) {
					boolean finish = keyManager.press_available(selectedKey);
					keyManager.availablePressed[selectedKey] = false;					
					selectedKey = -1;
					if (finish)
						game_is_word_finished();
				} else if (selectedGuessKey >= 0) {
					keyManager.press_guess(selectedGuessKey);
					keyManager.guessPressed[selectedGuessKey] = false;
					selectedGuessKey = -1;
				} else if (selectedSectionX >= 0) {
					splitImage.press_section(selectedSectionX, selectedSectionY);
					selectedSectionX = -1;
					selectedSectionY = -1;
				}
				
				drawingThread.redraw();
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
