package com.friendiq.android;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;

public class DrawingThread extends Thread {
	Context context;
	SurfaceHolder holder;
	SplitImageMatrix imageMatrix;
	KeyManager keyManager;
	
	Timer redrawTimer;
	
	private Paint keyPaint;
	private Paint addLetterPaint;
	private Paint removeLetterPaint;
	
	boolean run;
	boolean reDraw;
	
	public DrawingThread(Context context, SurfaceHolder holder, SplitImageMatrix imageMatrix, KeyManager keyManager) {
		this.context = context;
		this.holder = holder;
		this.imageMatrix = imageMatrix;
		this.keyManager = keyManager;
		
		keyPaint = new Paint();
		keyPaint.setAntiAlias(true);
		keyPaint.setARGB(255, 196, 239, 255);
		
		addLetterPaint = new Paint();
		addLetterPaint.setAntiAlias(true);
		addLetterPaint.setARGB(255, 6, 207, 6);
		
		removeLetterPaint = new Paint();
		removeLetterPaint.setAntiAlias(true);
		removeLetterPaint.setARGB(255, 242, 101, 34);
		
		redrawTimer = new Timer();
		
		this.run = false;
		this.reDraw = true;
	}
	
	private void draw_pictures(Canvas canvas) {
		for (int x = 0; x < SplitImageMatrix.NUMBER_SQUARE; x++) {
			for (int y = 0; y < SplitImageMatrix.NUMBER_SQUARE; y++) {
				canvas.drawBitmap(imageMatrix.contact.bm, imageMatrix.imgMatrix[x][y].source, imageMatrix.imgMatrix[x][y].dest, null);
			}
		}
	}
	
	private void draw_letters(Canvas canvas) {
		//Log.i(getClass().getSimpleName(), "drawing letters");
		
		for (int i = 0; i < keyManager.guessNameLetters.length; i++) {
			canvas.drawRoundRect(new RectF(keyManager.guessNameLetters[i]), 10, 10, keyPaint);
			//Log.i(getClass().getSimpleName(), keyManager.guessNameLetters[i].toShortString());
		}
		
		for (int i = 0; i < keyManager.sourceLetters.length; i++)
			canvas.drawRoundRect(new RectF(keyManager.sourceLetters[i]), 10, 10, keyPaint);
		
		canvas.drawRoundRect(new RectF(keyManager.cmdGiveLetter), 10, 10, addLetterPaint);
		canvas.drawRoundRect(new RectF(keyManager.cmdDeleteLetter), 10, 10, removeLetterPaint);
	}
	
	private void draw(Canvas canvas) {		
		canvas.drawColor(Color.BLACK);
		draw_pictures(canvas);
		draw_letters(canvas);
		
	}
	
	// start the drawing
	public void start_drawing() {
		run = true;
		redrawTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				reDraw = true;				
			}		
		}, 0, 200);
	}
	// pause the drawing
	public void stop_drawing() {
		run = false;
		redrawTimer.cancel();
	}

	
	@Override
	public void run() {
		super.run();
		Canvas canvas = null;
		while (run) {
			if (reDraw) {
				try {
					canvas = holder.lockCanvas(null);
					synchronized (holder) {
						draw(canvas);
					}
				} finally {
					if (canvas != null) {
						holder.unlockCanvasAndPost(canvas);
					}
				}
				reDraw = false;	
			}
		}
	}
}
