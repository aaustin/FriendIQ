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
	
	private static final double BOT_MARGIN = 0.06;
	private static final double SIDE_MARGIN = 0.03;
	private static final double BUTTON_KEY_MARGIN = 0.02;
	private static final double BOT_KEY_MARGIN = 0.01;
	private static final double GUESS_BOT_KEY_RATIO = 0.8;
	private static final double GUESS_KEY_MARGIN = 0.008;
	private static final double GUESS_BOT_MARGIN = 0.05;
	
	private int botMargin;
	private int sideMargin;
	private int buttonKeyMargin;
	private int botKeyMargin;
	private int guessKeyToBotRatio;
	private int guessKeyMargin;
	private int guessBottomMargin;
	
	Context context;
	private int screenWidth;
	private int screenHeight;
	
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
	
	private String firstname;
	
	private int botKeySide;
	private int guessKeySide;
	
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
		this.guessKeyToBotRatio = (int) (screenWidth*GUESS_BOT_KEY_RATIO);
		this.guessKeyMargin = (int) (screenWidth*GUESS_KEY_MARGIN);
		this.guessBottomMargin = (int) (screenHeight*GUESS_BOT_MARGIN);
		
		this.botKeySide = (screenWidth - 2 * this.sideMargin - this.buttonKeyMargin - (NUM_LETTERS_IN_ROW - 1) * this.botKeyMargin) / (NUM_LETTERS_IN_ROW + 1);
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
		
		top = top + guessBottomMargin + guessKeySide;
		left = sideMargin;
		for (int i = 0; i < NUM_LETTERS_IN_ROW; i++) {
			sourceLetters[i] = new Rect(left, top, left + botKeySide, top + botKeySide);
			left = left + botKeySide + botKeyMargin;
		}
		
		left = left - botKeyMargin + buttonKeyMargin;
		cmdGiveLetter = new Rect(left, top, left + botKeySide, top + botKeySide);
		
		top = top + botKeyMargin + botKeySide;
		left = sideMargin;
		for (int i = NUM_LETTERS_IN_ROW; i < (NUM_LETTER_ROWS * NUM_LETTERS_IN_ROW); i++) {
			sourceLetters[i] = new Rect(left, top, left + botKeySide, top + botKeySide);
			left = left + botKeySide + botKeyMargin;
		}
		
		left = left - botKeyMargin + buttonKeyMargin;
		cmdDeleteLetter = new Rect(left, top, left + botKeySide, top + botKeySide);				
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
