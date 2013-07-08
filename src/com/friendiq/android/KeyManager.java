package com.friendiq.android;

import java.util.ArrayList;
import java.util.Random;

import com.friendiq.android.GameView.KeyboardReady;

import android.content.Context;
import android.graphics.Rect;

public class KeyManager {
	static final int NUM_LETTERS_IN_ROW = 6;
	static final int NUM_LETTER_ROWS = 2;

	private static final int MIX_PASSES = 3;
	
	static final double BOT_MARGIN = 0.06;
	static final double SIDE_MARGIN = 0.03;
	static final double BUTTON_KEY_MARGIN = 0.02;
	static final double BOT_KEY_MARGIN = 0.01;
	static final double BOT_KEY_SIZE = 0.5;
	static final double GUESS_BOT_KEY_RATIO = 0.8;
	static final double GUESS_KEY_MARGIN = 0.008;
	static final double GUESS_BOT_MARGIN = 0.05;
	
	int botMargin;
	int sideMargin;
	int buttonKeyMargin;
	int botKeyMargin;
	int botKeySize;
	int guessKeyToBotRatio;
	int guessKeyMargin;
	int guessBottomMargin;
	
	Context context;
	int screenWidth;
	int screenHeight;
	
	static String[] allLetters = {
		"A", 
		"B", 
		"C", 
		"D", 
		"E", 
		"F",
		"G",
		"H",
		"I",
		"J",
		"K",
		"L",
		"M",
		"N",
		"O",
		"P",
		"R",
		"S",
		"T",
		"U"};
	
	String firstname;
	
	int botKeySide;
	int guessKeySide;
	
	Rect cmdGiveLetter;
	Rect cmdDeleteLetter;
	
	Rect[] guessNameLetters;
	Rect[] sourceLetters;
	
	String[] guessLetters;
	String[] availableLetters;
	
	KeyboardReady keysReady;
	
	public KeyManager(Context context, int screenHeight, int screenWidth) {
		this.context = context;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.botMargin = (int) (screenHeight*BOT_MARGIN);
		this.sideMargin = (int) (screenWidth*SIDE_MARGIN);
		this.buttonKeyMargin = (int) (screenWidth*BUTTON_KEY_MARGIN);
		this.botKeyMargin = (int) (screenWidth*BOT_KEY_MARGIN);
		this.botKeySize = (int) (screenWidth*BOT_KEY_SIZE);
		this.guessKeyToBotRatio = (int) (screenWidth*GUESS_BOT_KEY_RATIO);
		this.guessKeyMargin = (int) (screenWidth*GUESS_KEY_MARGIN);
		this.guessBottomMargin = (int) (screenHeight*GUESS_BOT_MARGIN);
		
		this.botKeySide = botKeySize / NUM_LETTERS_IN_ROW;
		this.guessKeySide = this.botKeySide * this.guessKeyToBotRatio;
	}
	
	private void init_rects() {
		// guess letter rects
		int top = screenHeight - botMargin - 2 * botKeySide - botKeyMargin - guessBottomMargin - guessKeySide;
		int left = screenWidth/2 - (firstname.length() * (guessKeySide + guessKeyMargin) - guessKeyMargin)/2;
		for (int i = 0; i < firstname.length(); i++) {
			guessNameLetters[i] = new Rect(left, top, left + guessKeySide, top + guessKeySide);
			left = left + guessKeySide + guessKeyMargin;
		}
		
		
	}
	
	private void init_letters() {
		Random ran = new Random();
		ArrayList<String> firstAL = new ArrayList<String>();
		ArrayList<String> secondAL;
		for (int i = 0; i < firstname.length(); i++)
			firstAL.add(String.valueOf(firstname.charAt(i)));
		
		int count = NUM_LETTERS_IN_ROW*NUM_LETTER_ROWS - firstname.length();
		while (count > 0) {
			firstAL.add(allLetters[ran.nextInt(allLetters.length)]);
			count = count - 1;
		}
		
		// mix it up
		int mixCount = 0;
  		while (mixCount < MIX_PASSES) {
  			secondAL = new ArrayList<String>();
  			while (!firstAL.isEmpty()) {  				
  				secondAL.add(firstAL.remove(ran.nextInt(firstAL.size())));
  			}
  			firstAL = null;
  			firstAL = secondAL;  			
  			secondAL = null;
  			mixCount = mixCount + 1;
  		}

  		// assign the letters
  		for (int i = 0; i < availableLetters.length; i++)
  			availableLetters[i] = firstAL.remove(0);
  		
  		for (int i = 0; i < guessLetters.length; i++)
  			guessLetters[i] = "-1";

	}
	
	public void prepare_keys(String firstname, KeyboardReady keysReady) {
		this.firstname = firstname;
		
		guessNameLetters = new Rect[firstname.length()];
		guessLetters = new String[firstname.length()];
		
		sourceLetters = new Rect[NUM_LETTERS_IN_ROW*NUM_LETTER_ROWS];
		availableLetters = new String[NUM_LETTERS_IN_ROW*NUM_LETTER_ROWS];
		
		init_rects();		
		init_letters();
		  		
		keysReady.callback(1);		
	}

}
