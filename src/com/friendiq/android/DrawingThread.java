package com.friendiq.android;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class DrawingThread extends Thread {
	Context context;
	SurfaceHolder holder;
	SplitImageMatrix imageMatrix;
	KeyManager keyManager;
	
	boolean run;
	boolean reDraw;
	
	public DrawingThread(Context context, SurfaceHolder holder, SplitImageMatrix imageMatrix, KeyManager keyManager) {
		this.context = context;
		this.holder = holder;
		this.imageMatrix = imageMatrix;
		this.keyManager = keyManager;
		
		this.run = false;
		this.reDraw = true;
	}
	
	private void draw_pictures(Canvas canvas) {
		for (int x = 0; x < SplitImageMatrix.NUMBER_SQUARE; x++) {
			for (int y = 0; y < SplitImageMatrix.NUMBER_SQUARE; y++) {
				canvas.drawBitmap(imageMatrix.basePicture, imageMatrix.imgMatrix[x][y].source, imageMatrix.imgMatrix[x][y].dest, null);
			}
		}
	}
	
	private void draw(Canvas canvas) {
		if (reDraw) {
			draw_pictures(canvas);
			reDraw = false;
		}		
	}
	
	// start the drawing
	public void start_drawing() {
		run = true;
	}
	// pause the drawing
	public void stop_drawing() {
		run = false;
	}

	
	@Override
	public void run() {
		super.run();
		Canvas canvas = null;
		while (run) {
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
		}
	}
}
