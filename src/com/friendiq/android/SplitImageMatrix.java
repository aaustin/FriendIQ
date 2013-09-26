package com.friendiq.android;


import java.util.ArrayList;
import java.util.Random;

import com.flurry.android.FlurryAgent;
import com.friendiq.android.GameView.MatrixReady;
import com.friendiq.android.helpers.KindredAlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplitImageMatrix {	
	private static final double SECTION_MARGIN = 0.01;
	private static final double IMAGE_WIDTH = 0.75;
	private static final double TOP_MARGIN = 0.2;
	
	private static final double FLASH_BUTTON = 1.5;
	private static final double FLASH_MARGIN = 0.02;
	private static final double FLASH_HEIGHT = 0.4;

	
	public static final int NUMBER_SQUARE = 4;
	private static final int MIX_PASSES = 1;
	
	public int sectionMargin;
	public int imageWidth;
	public int topMargin;
	
	GameView gameView;
	ImageView portraitView;
	TextView txtCoins;
	PrefHelper pHelper;
	Contact contact;
	
	Context context;
	int screenWidth;
	int screenHeight;
	
	int sectionSideLength;
	
	Rect cmdFlashPicture;
	ImageSection[][] imgMatrix;
	
	MatrixReady preparedCallback;
	Bitmap basePicture;
	
	Rect slidingRect;
	int srcX;
	int srcY;
	int destX;
	int destY;
	boolean sliding;
	
	boolean trackCompletion = false;
	
	public SplitImageMatrix(Context context, int screenHeight, int screenWidth, ImageView portraitView, GameView gameView, TextView txtCoins) {
		this.gameView = gameView;
		this.context = context;
		this.txtCoins = txtCoins;
		this.portraitView = portraitView;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.pHelper = new PrefHelper(context);
		
		sliding = false;
		
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
  		((Activity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				RelativeLayout.LayoutParams lp =  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				lp.width = NUMBER_SQUARE*sectionSideLength + sectionMargin*(NUMBER_SQUARE-1);
				lp.height = NUMBER_SQUARE*sectionSideLength + sectionMargin*(NUMBER_SQUARE-1);
				lp.setMargins((screenWidth - imageWidth)/2, topMargin, 0, 0);
				portraitView.setLayoutParams(lp);	
				//portraitView.setVisibility(View.INVISIBLE);		
			}  			
  		});
  		Log.i(SplitImageMatrix.class.getName(),"just created new bmp with " + imageWidth + "x" + imageWidth);
  		
  		if (contact.bm == null)
  			Log.i(SplitImageMatrix.class.getName(),"NULL BMP");
  		// cut up the base image
  		int left = 0;
  		int top = 0;
  		ArrayList<ImageSection> tempSections = new ArrayList<ImageSection>();
  		int originCounter = 0;
  		for (int x = 0; x < NUMBER_SQUARE; x++) {
  			for (int y = 0; y < NUMBER_SQUARE; y++) {
  				left = x*sectionSideLength;
  				top = y*sectionSideLength;
  				ImageSection img = new ImageSection();
  				img.index = originCounter;
  	  			img.source = new Rect(left, top, left + sectionSideLength, top + sectionSideLength);
				//Log.i(getClass().getSimpleName(), "x, y (" + x + ", " + y + ")");
  	  			//Log.i(SplitImageMatrix.class.getName(),"source section = " + img.source.toShortString());
  	  			tempSections.add(img);
  	  			originCounter = originCounter + 1;
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
				//Log.i(getClass().getSimpleName(), "x, y (" + x + ", " + y + ")");
  				//Log.i(SplitImageMatrix.class.getName(),"Dests section = " + imgMatrix[x][y].dest.toShortString());
  	  			//Log.i(SplitImageMatrix.class.getName(),"source section = " + imgMatrix[x][y].source.toShortString());

  			}
  		}
  		left = (NUMBER_SQUARE-1)*sectionSideLength + baseX + (NUMBER_SQUARE-1)*sectionMargin;
  		top = baseY - (int) (FLASH_MARGIN*screenHeight+FLASH_HEIGHT*sectionSideLength);
  		cmdFlashPicture = new Rect(
  				left,
  				top,
  				left + (int) (FLASH_BUTTON*sectionSideLength),
  				top + (int) (FLASH_HEIGHT*sectionSideLength));
  		
  		preparedCallback.callback(1);
  	}
  	
  	public void press_section(int x, int y) {
  		if (!sliding && !imgMatrix[x][y].isBlank) {
  			srcX = -1;
  			srcY = -1;
  			destX = -1;
  			destY = -1;
  			
  			Log.i(getClass().getSimpleName(), "pressed section (" + x + ", " + y + ")");
  			
  			for (int tempX = Math.max(0, x-1); tempX <= Math.min(NUMBER_SQUARE-1, x+1); tempX++) {
  				if (imgMatrix[tempX][y].isBlank) {
  					Log.i(getClass().getSimpleName(), "found a next door blank at (" + tempX + ", " + y + ")");
  					srcX = x;
  					srcY = y;
  					destX = tempX;
  					destY = y;
  					slidingRect = new Rect(
  							imgMatrix[x][y].dest.left, 
  							imgMatrix[x][y].dest.top,
  							imgMatrix[x][y].dest.right,
  							imgMatrix[x][y].dest.bottom);
  					break;
  				}
  			}
  			if (destX < 0) {
				for (int tempY = Math.max(0, y-1); tempY <= Math.min(NUMBER_SQUARE-1, y+1); tempY++) {
					//Log.i(getClass().getSimpleName(), "scanning (" + tempX + ", " + tempY + ")");					
					if (imgMatrix[x][tempY].isBlank) {
						Log.i(getClass().getSimpleName(), "found a next door blank at (" + x + ", " + tempY + ")");
		
						srcX = x;
						srcY = y;
						destX = x;
						destY = tempY;
						slidingRect = new Rect(
	  							imgMatrix[x][y].dest.left, 
	  							imgMatrix[x][y].dest.top,
	  							imgMatrix[x][y].dest.right,
	  							imgMatrix[x][y].dest.bottom);
						break;
					}
				}	  			
  			}
  			
  			if (destX >= 0) {
				Log.i(getClass().getSimpleName(), "starting animation!!");
				imgMatrix[srcX][srcY].sliding = true;
  				sliding = true;
				((Activity)context).runOnUiThread(new Runnable() {
					@Override
					public void run() {	
		  				Animation ani = new Animation() {
		  					@Override
		  					protected void applyTransformation(float interpolatedTime, Transformation t) {
		  						int deltaX = imgMatrix[destX][destY].dest.left - imgMatrix[srcX][srcY].dest.left;
		  						int deltaY = imgMatrix[destX][destY].dest.top - imgMatrix[srcX][srcY].dest.top;
		  						//Log.i(getClass().getSimpleName(), "animation interp!!");
		  						slidingRect.offsetTo(imgMatrix[srcX][srcY].dest.left + (int)(deltaX*interpolatedTime), imgMatrix[srcX][srcY].dest.top+(int)(deltaY*interpolatedTime));
		  			  			gameView.drawingThread.redraw();

		  					}
		  					 @Override
		  			        public boolean willChangeBounds() {
		  			            return true;
		  			        }
		  				};
		  				ani.setAnimationListener(new SlidingAnimationListener());
		  				ani.setDuration(200);
		  				gameView.startAnimation(ani);
					}
				});
  				
  			}
  		}
  	}

  	private boolean check_image_puzzle() {
  		boolean isFinished = true;
  		int originCounter = 0;
  		for (int x = 0; x < NUMBER_SQUARE; x++) {
  			for (int y = 0; y < NUMBER_SQUARE; y++) {
  				if (imgMatrix[x][y].index != originCounter) {
  					isFinished = false;
  					break;
  				}
  				originCounter = originCounter + 1;
  			}
  			if (!isFinished)
  				break;
  		}
  		
  		return isFinished;
  	}
  	
  	public void flash_image() {
  		if (pHelper.get_coin_count() >= PrefHelper.FLASH_COST) {
  			KindredAlertDialog kad = new KindredAlertDialog(context, "Flashing the image costs " + PrefHelper.FLASH_COST + " coins.\n\nDo you want to see the image briefly?", true);
  			kad.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					trackCompletion = true;
					FlurryAgent.logEvent("Flash_Image_Completed");
					((Activity)context).runOnUiThread(new Runnable() {
						@Override
						public void run() {			
							portraitView.setVisibility(View.INVISIBLE);		
							//Log.i(getClass().getSimpleName(), "starting fade in ani");
					  		Animation viewFadeinAni = AnimationUtils.loadAnimation(context, R.anim.fadein);
					  		viewFadeinAni.setAnimationListener(new FadeInViewAnimationListener());
					  		portraitView.startAnimation(viewFadeinAni);
					  		portraitView.setImageBitmap(basePicture);		  		
						}
			  		});
			  		pHelper.add_to_coin_count(-1*PrefHelper.FLASH_COST);
			  		txtCoins.setText(pHelper.get_coin_count() + " coins");
				}  			
  			});  		
  			kad.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					if (!trackCompletion)
						FlurryAgent.logEvent("Flash_Image_Canceled");
					else
						trackCompletion = false;
				}  				
  			});
  			kad.show();  		
  		} else {
  			FlurryAgent.logEvent("Flash_Image_Not_Enough_Coins");
  			show_not_enough_coins();
  		}
  	}
  	
  	private class SlidingAnimationListener implements AnimationListener {
  		@Override
		public void onAnimationEnd(Animation animation) {
			Log.i(getClass().getSimpleName(), "finished animation!!");  		
  			
  			ImageSection temp = imgMatrix[srcX][srcY].copy();
  			temp.dest = slidingRect;
  			
  			imgMatrix[destX][destY].dest = imgMatrix[srcX][srcY].dest;
  			imgMatrix[srcX][srcY] = imgMatrix[destX][destY].copy();
  			imgMatrix[destX][destY] = temp;
  			
  			imgMatrix[destX][destY].sliding = false;  
  			sliding = false;
  			
  			if (check_image_puzzle())
  				gameView.game_is_pic_finished();
  				
		}
		@Override
		public void onAnimationRepeat(Animation animation) {}
		@Override
		public void onAnimationStart(Animation animation) {}	
  	}
  	
  	private class FadeOutViewAnimationListener implements AnimationListener {
		@Override
		public void onAnimationEnd(Animation animation) {
			Log.i(getClass().getSimpleName(), "finishing fade out ani");

			portraitView.setVisibility(View.INVISIBLE);		
		}
		@Override
		public void onAnimationRepeat(Animation animation) {}
		@Override
		public void onAnimationStart(Animation animation) {}		
	}
	private class FadeInViewAnimationListener implements AnimationListener {
		@Override
		public void onAnimationEnd(Animation animation) {
			Log.i(getClass().getSimpleName(), "finishing fade in ani");
			portraitView.setVisibility(View.VISIBLE);		
			Animation fadeoutAni = AnimationUtils.loadAnimation(context, R.anim.fadeout);
			fadeoutAni.setAnimationListener(new FadeOutViewAnimationListener());
			portraitView.startAnimation(fadeoutAni);
			Log.i(getClass().getSimpleName(), "starting fade out ani");

		}
		@Override
		public void onAnimationRepeat(Animation animation) {}
		@Override
		public void onAnimationStart(Animation animation) {}		
	}
	
	private void show_not_enough_coins() {
		KindredAlertDialog kad = new KindredAlertDialog(context, "You don't have enough coins for that!", false);
		kad.show();
	}
}
