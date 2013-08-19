package com.friendiq.android;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
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
	private Paint flashPaint;
	private TextPaint textPaint;
	private Paint clickPaint;
	private StaticLayout txtLayout;
	
	Drawable bg;
	Drawable dAddLetter;
	Drawable dDelLetter;
	Drawable dFlashPic;

	boolean run;
	boolean reDraw;
	boolean save;
	
	public DrawingThread(Context context, SurfaceHolder holder, SplitImageMatrix imageMatrix, KeyManager keyManager) {
		this.context = context;
		this.holder = holder;
		this.imageMatrix = imageMatrix;
		this.keyManager = keyManager;
		
		keyPaint = new Paint();
		keyPaint.setAntiAlias(true);
		keyPaint.setARGB(255, 196, 239, 255);
		
		flashPaint = new Paint();
		flashPaint.setAntiAlias(true);
		flashPaint.setARGB(255, 210, 210, 210);
		
		addLetterPaint = new Paint();
		addLetterPaint.setAntiAlias(true);
		addLetterPaint.setARGB(255, 6, 207, 6);
		
		removeLetterPaint = new Paint();
		removeLetterPaint.setAntiAlias(true);
		removeLetterPaint.setARGB(255, 242, 101, 34);
		
		textPaint = new TextPaint();
		textPaint.setAntiAlias(true);
		textPaint.setARGB(255, 30, 30, 30);
		
		clickPaint = new Paint();
		clickPaint.setAntiAlias(true);
		clickPaint.setARGB(80, 100, 100, 100);
		
		bg = context.getResources().getDrawable(R.drawable.bg_gradient);
		bg.setBounds(0, 0, imageMatrix.screenWidth, imageMatrix.screenHeight);
		
		dAddLetter = context.getResources().getDrawable(R.drawable.ic_add);
		dDelLetter = context.getResources().getDrawable(R.drawable.ic_trash);
		dFlashPic = context.getResources().getDrawable(R.drawable.ico_flash);
		//redrawTimer = new Timer();
		
		this.save = false;
		this.run = false;
		this.reDraw = true;
	}
	
	private void draw_pictures(Canvas canvas) {
		for (int x = 0; x < SplitImageMatrix.NUMBER_SQUARE; x++) {
			for (int y = 0; y < SplitImageMatrix.NUMBER_SQUARE; y++) {			
				if (!imageMatrix.imgMatrix[x][y].isBlank) {
					if (imageMatrix.imgMatrix[x][y].sliding)
						canvas.drawBitmap(imageMatrix.basePicture, imageMatrix.imgMatrix[x][y].source, imageMatrix.slidingRect, null);
					else
						canvas.drawBitmap(imageMatrix.basePicture, imageMatrix.imgMatrix[x][y].source, imageMatrix.imgMatrix[x][y].dest, null);
				}
					
			}
		}
	}
	
	private void draw_letter(Canvas canvas, int index, boolean guess) {
		canvas.save();
		String letter;
		Rect dest;
		int width;
		if (guess) {
			width = keyManager.guessKeySide;
			textPaint.setTextSize(keyManager.guessKeySide);
			letter = keyManager.guessLetters[index];
			dest = keyManager.guessNameLetters[index];
		} else {
			width = keyManager.botKeySide;
			textPaint.setTextSize(keyManager.botKeySide);
			letter = keyManager.availableLetters[index];
			dest = keyManager.sourceLetters[index];
		}
		
		
		if (!letter.equals("-1")) {
			txtLayout = new StaticLayout(
					letter, 
					textPaint, 
					width, 
					Layout.Alignment.ALIGN_CENTER, 
					1.0f, 
					0, 
					false);
			canvas.translate(dest.left,(int) (dest.top-0.05*dest.height()));
			txtLayout.draw(canvas);
		}
		
		canvas.restore();
	}
	
	private void draw_letters(Canvas canvas) {
		//Log.i(getClass().getSimpleName(), "drawing letters");
		
		for (int i = 0; i < keyManager.guessNameLetters.length; i++) {
			if (keyManager.guessPressed[i])
				canvas.drawRoundRect(new RectF(keyManager.guessNameLetters[i]), 10, 10, clickPaint);
			else
				canvas.drawRoundRect(new RectF(keyManager.guessNameLetters[i]), 10, 10, keyPaint);
			draw_letter(canvas, i, true);
			//Log.i(getClass().getSimpleName(), keyManager.guessNameLetters[i].toShortString());
		}
		
		for (int i = 0; i < keyManager.sourceLetters.length; i++) {
			if (keyManager.availablePressed[i])
				canvas.drawRoundRect(new RectF(keyManager.sourceLetters[i]), 10, 10, clickPaint);
			else
				canvas.drawRoundRect(new RectF(keyManager.sourceLetters[i]), 10, 10, keyPaint);
			draw_letter(canvas, i, false);
		}
		
		int scaledSide = (int)(imageMatrix.cmdFlashPicture.height()*0.7);
		int offsetX = (int)((imageMatrix.cmdFlashPicture.height()*0.3)/2);
		int offsetY = offsetX;
		
		canvas.drawRoundRect(new RectF(imageMatrix.cmdFlashPicture), 10, 10, flashPaint);
		dFlashPic.setBounds(
				imageMatrix.cmdFlashPicture.left+3*offsetX, 
				imageMatrix.cmdFlashPicture.top+offsetY, 
				imageMatrix.cmdFlashPicture.left + offsetX + scaledSide, 
				imageMatrix.cmdFlashPicture.top + offsetY + scaledSide);
		dFlashPic.draw(canvas);
		
		canvas.save();
		textPaint.setTextSize(scaledSide);
		txtLayout = new StaticLayout(
				"flash", 
				textPaint, 
				imageMatrix.cmdFlashPicture.width(), 
				Layout.Alignment.ALIGN_CENTER, 
				1.0f, 
				0, 
				false);
		canvas.translate(imageMatrix.cmdFlashPicture.left+2*offsetX, imageMatrix.cmdFlashPicture.top+offsetY);
		txtLayout.draw(canvas);
		canvas.restore();
				
		
		scaledSide = (int)(keyManager.cmdGiveLetter.height()*0.7);
		offsetX = (int)((keyManager.cmdGiveLetter.height()*0.3)/2);
		offsetY = offsetX;	

		canvas.drawRoundRect(new RectF(keyManager.cmdGiveLetter), 10, 10, addLetterPaint);
		dAddLetter.setBounds(keyManager.cmdGiveLetter.left + offsetX, 
				keyManager.cmdGiveLetter.top + offsetY, 
				keyManager.cmdGiveLetter.left + offsetX + scaledSide, 
				keyManager.cmdGiveLetter.top + offsetY + scaledSide);
		dAddLetter.draw(canvas);
		canvas.drawRoundRect(new RectF(keyManager.cmdDeleteLetter), 10, 10, removeLetterPaint);
		dDelLetter.setBounds(keyManager.cmdDeleteLetter.left + offsetX, 
				keyManager.cmdDeleteLetter.top + offsetY, 
				keyManager.cmdDeleteLetter.left + offsetX + scaledSide, 
				keyManager.cmdDeleteLetter.top + offsetY + scaledSide);
		dDelLetter.draw(canvas);
		
	}
	
	private void draw(Canvas canvas) {		
		if (canvas != null) {
			bg.draw(canvas);
			//canvas.draw(Color.BLACK);
			draw_pictures(canvas);
			draw_letters(canvas);
		}			
	}
	
	public void redraw() {
		reDraw = true;	
	}
	
	// start the drawing
	public void start_drawing() {
		run = true;
		redrawTimer = new Timer();
		redrawTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				reDraw = true;				
			}		
		}, 0, 2000);
	}
	// pause the drawing
	public void stop_drawing() {
		run = false;
		redrawTimer.cancel();
	}
	
	public void save_current_grid_to_file() {
		int side = SplitImageMatrix.NUMBER_SQUARE*imageMatrix.sectionSideLength + (SplitImageMatrix.NUMBER_SQUARE-1)*imageMatrix.sectionMargin;
		Bitmap bm = Bitmap.createBitmap(side, side, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bm);
  		int baseX = (imageMatrix.screenWidth - imageMatrix.imageWidth)/2;
  		int baseY = imageMatrix.topMargin;
		for (int x = 0; x < SplitImageMatrix.NUMBER_SQUARE; x++) {
			for (int y = 0; y < SplitImageMatrix.NUMBER_SQUARE; y++) {			
				if (!imageMatrix.imgMatrix[x][y].isBlank) {		
					Rect adjustedRect = new Rect(
							imageMatrix.imgMatrix[x][y].dest.left-baseX,
							imageMatrix.imgMatrix[x][y].dest.top-baseY,
							imageMatrix.imgMatrix[x][y].dest.right-baseX,
							imageMatrix.imgMatrix[x][y].dest.bottom-baseY);
					canvas.drawBitmap(imageMatrix.basePicture, imageMatrix.imgMatrix[x][y].source, adjustedRect, null);
				}					
			}
		}
		File selectedPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File fo = new File(selectedPath, imageMatrix.contact.datasourceid + ".jpg");
		try {
			FileOutputStream fOut = new FileOutputStream(fo);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
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
