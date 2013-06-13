package com.friendiq.android;


import java.util.ArrayList;
import java.util.Random;

import com.friendiq.android.GameView.MatrixReady;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;

public class SplitImageMatrix {
	private static final int GAP_WIDTH = 5;
	private static final int NUMBER_SQUARE = 5;
	private static final int MIX_PASSES = 3;
		
	SurfaceHolder surfaceHolder;
	Context context;
	int screenWidth;
		
	int sectionSideLength;
	
	ImageSection[][] imgMatrix;
	
	ImageGrabber imgGrabber;
	ImageAcquired acquiredCallback;
	MatrixReady preparedCallback;
	Bitmap basePicture;
	
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
  		sectionSideLength = (screenWidth - (NUMBER_SQUARE + 1)*GAP_WIDTH)/NUMBER_SQUARE;
  		int imageWidth = sectionSideLength * NUMBER_SQUARE;  	
  		imgMatrix = new ImageSection[NUMBER_SQUARE][NUMBER_SQUARE];
  		
  		basePicture = Bitmap.createScaledBitmap(imgGrabber.contact.bm, imageWidth, imageWidth, false);
  		
  		// cut up the base image
  		int left = 0;
  		int top = 0;
  		ArrayList<ImageSection> tempSections = new ArrayList<ImageSection>();
  		for (int x = 0; x < NUMBER_SQUARE; x++) {
  			for (int y = 0; y < NUMBER_SQUARE; y++) {
  				left = x*sectionSideLength;
  				top = y*sectionSideLength;
  				ImageSection img = new ImageSection();
  	  			img.source = new Rect(left, top, left + sectionSideLength, top + sectionSideLength);
  	  			tempSections.add(img);
  			}  			
  		}
  		tempSections.get(tempSections.size()-1).isBlank = true;
  		
  		// randomize the sections
  		Random ran = new Random();
  		ArrayList<ImageSection> mixupSections;
  		int mixCount = 0;
  		while (mixCount < MIX_PASSES) {
  			mixupSections = new ArrayList<ImageSection>(); 
  			while (!tempSections.isEmpty()) {  				
  				mixupSections.add(tempSections.remove(ran.nextInt(tempSections.size())));
  			}
  			tempSections = null;
  			tempSections = mixupSections;  			
  			mixupSections = null;
  		}
  		
  		// pick the layout destination
  		int base = GAP_WIDTH;
  		for (int x = 0; x < NUMBER_SQUARE; x++) {
  			for (int y = 0; y < NUMBER_SQUARE; y++) {
  				left = x*sectionSideLength + base + x*GAP_WIDTH;
  				top = y*sectionSideLength + base + y*GAP_WIDTH;
  				imgMatrix[x][y] = tempSections.remove(0);
  				imgMatrix[x][y].dest = new Rect(left, top, left + sectionSideLength, top + sectionSideLength);
  			}
  		}
  		
  		
  		preparedCallback.callback(1);
  	}

  	private void draw_sections() {  		
    	Canvas c = null;
        
        try {
            c = surfaceHolder.lockCanvas(null);
            synchronized (surfaceHolder) {
            	for (int x = 0; x < NUMBER_SQUARE; x++) {
            		for (int y = 0; y < NUMBER_SQUARE; y++) {
            			if (!imgMatrix[x][y].isBlank)            			
            				c.drawBitmap(basePicture, imgMatrix[x][y].source, imgMatrix[x][y].dest, null);
            		}
            	}
            	
            }
        } finally {                
            if (c != null) {
            	surfaceHolder.unlockCanvasAndPost(c);
            }
        }
  	}
}
