package com.friendiq.android;


import java.util.ArrayList;
import java.util.Random;
import com.friendiq.android.GameView.MatrixReady;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

public class SplitImageMatrix {
	
	private static final double SECTION_MARGIN = 0.01;
	private static final double IMAGE_WIDTH = 0.75;
	private static final double TOP_MARGIN = 0.2;
	
	public static final int NUMBER_SQUARE = 5;
	private static final int MIX_PASSES = 3;
	
	public int sectionMargin;
	public int imageWidth;
	public int topMargin;
	
	Contact contact;
	
	Context context;
	int screenWidth;
	int screenHeight;
	
	int sectionSideLength;
	
	ImageSection[][] imgMatrix;
	
	MatrixReady preparedCallback;
	Bitmap basePicture;
	
	public SplitImageMatrix(Context context, int screenHeight, int screenWidth) {
		this.context = context;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		this.sectionMargin = (int) (screenWidth*SECTION_MARGIN);
		this.imageWidth = (int) (screenWidth*IMAGE_WIDTH);
		this.topMargin = (int) (screenHeight*TOP_MARGIN);
	}
	
	public void prepare_matrix(Contact contact, MatrixReady preparedReady) {
		this.preparedCallback = preparedReady;
		this.contact = contact;
		init_matrix();
	}
	
	 	
  	private void init_matrix() {
  		sectionSideLength = (imageWidth - (NUMBER_SQUARE - 1)*sectionMargin)/NUMBER_SQUARE;  	
  		imgMatrix = new ImageSection[NUMBER_SQUARE][NUMBER_SQUARE];
  		
  		basePicture = Bitmap.createScaledBitmap(contact.bm, imageWidth, imageWidth, false);
  		Log.i(DatabaseHelper.class.getName(),"just created new bmp with " + imageWidth + "x" + imageWidth);
  		
  		if (contact.bm == null)
  			Log.i(DatabaseHelper.class.getName(),"NULL BMP");
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
  	  			Log.i(DatabaseHelper.class.getName(),"source section = " + img.source.toShortString());
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
  			mixCount = mixCount + 1;
  		}
  		  		
  		// pick the layout destination
  		int baseX = (screenWidth - imageWidth)/2;
  		int baseY = topMargin;
  		for (int x = 0; x < NUMBER_SQUARE; x++) {
  			for (int y = 0; y < NUMBER_SQUARE; y++) {
  				left = x*sectionSideLength + baseX + x*sectionMargin;
  				top = y*sectionSideLength + baseY + y*sectionMargin;
  				imgMatrix[x][y] = tempSections.remove(0);
  				imgMatrix[x][y].dest = new Rect(left, top, left + sectionSideLength, top + sectionSideLength);
  				Log.i(DatabaseHelper.class.getName(),"Dests section = " + imgMatrix[x][y].dest.toShortString());
  			}
  		}
  		
  		preparedCallback.callback(1);
  	}

}
