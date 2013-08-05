package com.friendiq.android;

import java.util.ArrayList;
import java.util.Random;
import com.friendiq.android.GameView.KeyboardReady;
import android.content.Context;
import android.graphics.Rect;
import android.widget.TextView;

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
	//private int guessKeyToBotRatio;
	private int guessKeyMargin;
	private int guessBottomMargin;
	
	TextView txtCoins;
	
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
	
	int botKeySide;
	int guessKeySide;
		
	Rect cmdGiveLetter;
	Rect cmdDeleteLetter;
	
	Rect[] guessNameLetters;
	Rect[] sourceLetters;
	
	String[] guessLetters;
	String[] availableLetters;
	ArrayList<String> randomLetters;
	
	boolean[] guessPressed;
	boolean[] availablePressed;
	
	KeyboardReady keysReady;
	
	public KeyManager(Context context, int screenHeight, int screenWidth, TextView txtCoins) {
		this.txtCoins = txtCoins;
		this.context = context;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.botMargin = (int) (screenHeight*BOT_MARGIN);
		this.sideMargin = (int) (screenWidth*SIDE_MARGIN);
		this.buttonKeyMargin = (int) (screenWidth*BUTTON_KEY_MARGIN);
		this.botKeyMargin = (int) (screenWidth*BOT_KEY_MARGIN);
		//this.guessKeyToBotRatio = (int) (screenWidth*GUESS_BOT_KEY_RATIO);
		this.guessKeyMargin = (int) (screenWidth*GUESS_KEY_MARGIN);
		this.guessBottomMargin = (int) (screenHeight*GUESS_BOT_MARGIN);
		
		this.botKeySide = (screenWidth - 2 * this.sideMargin - this.buttonKeyMargin - (NUM_LETTERS_IN_ROW - 1) * this.botKeyMargin) / (NUM_LETTERS_IN_ROW + 1);
		this.guessKeySide = (int) (this.botKeySide * GUESS_BOT_KEY_RATIO);//this.guessKeyToBotRatio;
	}
	
	private void init_rects() {
		// guess letter rects
		int top = screenHeight - botMargin - 2 * botKeySide - botKeyMargin - guessBottomMargin - guessKeySide;
		int left = screenWidth/2 - (firstname.length() * (guessKeySide + guessKeyMargin) - guessKeyMargin)/2;
		//Log.i(getClass().getSimpleName(), "starting top =" + top + ", left = " + left);

		for (int i = 0; i < firstname.length(); i++) {
			guessNameLetters[i] = new Rect(left, top, left + guessKeySide, top + guessKeySide);
			left = left + guessKeySide + guessKeyMargin;
			//Log.i(getClass().getSimpleName(), "starting top =" + top + ", left = " + left);
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
		randomLetters = new ArrayList<String>();
		ArrayList<String> firstAL = new ArrayList<String>();
		ArrayList<String> secondAL;
		for (int i = 0; i < firstname.length(); i++)
			firstAL.add(String.valueOf(firstname.charAt(i)));
		
		int count = NUM_LETTERS_IN_ROW*NUM_LETTER_ROWS - firstname.length();
		while (count > 0) {
			String ranLetter = allLetters[ran.nextInt(allLetters.length)];
			randomLetters.add(ranLetter);
			firstAL.add(ranLetter);
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
  		for (int i = 0; i < availableLetters.length; i++) {
  			availableLetters[i] = firstAL.remove(0);
  			availablePressed[i] = false;
  		}
  		
  		for (int i = 0; i < guessLetters.length; i++) {
  			guessLetters[i] = "-1";
  			guessPressed[i] = false;
  		}

	}
	
	public void prepare_keys(String firstname, KeyboardReady keysReady) {
		this.firstname = firstname;
		
		guessNameLetters = new Rect[firstname.length()];
		guessLetters = new String[firstname.length()];
		guessPressed = new boolean[firstname.length()];
		
		sourceLetters = new Rect[NUM_LETTERS_IN_ROW*NUM_LETTER_ROWS];
		availableLetters = new String[NUM_LETTERS_IN_ROW*NUM_LETTER_ROWS];
		availablePressed = new boolean[NUM_LETTERS_IN_ROW*NUM_LETTER_ROWS];
		
		init_rects();		
		init_letters();
		  		
		keysReady.callback(1);		
	}
	
	private int find_first_blank_guess() {
		int blank = -1;
		for (int i = 0; i < guessLetters.length; i++) {
			if (guessLetters[i].equals("-1")) {
				blank = i;
				break;
			}
		}		 
		return blank;
	}
	
	private int find_first_blank_avail() {
		int blank = -1;
		for (int i = 0; i < availableLetters.length; i++) {
			if (availableLetters[i].equals("-1")) {
				blank = i;
				break;
			}
		}		 
		return blank;
	}
	
	public boolean press_available(int index) {
		if (!availableLetters[index].equals("-1")) {
			int blankIndex = find_first_blank_guess();
			if (blankIndex >= 0) {
				guessLetters[blankIndex] = availableLetters[index];
				availableLetters[index] = "-1";
			}
			if (find_first_blank_guess() < 0) {
				return true;
			}
		}
		return false;
	}
	
	public void press_guess(int index) {
		if (!guessLetters[index].equals("-1")) {
			int blankIndex = find_first_blank_avail();
			if (blankIndex >= 0) {
				availableLetters[blankIndex] = guessLetters[index];
				guessLetters[index] = "-1";
			}
		}
	}
	
	public void delete_letter() {
		if (!randomLetters.isEmpty()) {
			String deletter = randomLetters.remove(0);
			for (int i = 0; i < availableLetters.length; i++) {
				if (availableLetters[i].equals(deletter)) {
					availableLetters[i] = "-1";
					break;
				}
			}
		}
	}
	public boolean give_letter() {
		boolean letterAwarded = false;
		for (int i = 0; i < availableLetters.length; i++) {
			if (firstname.contains(availableLetters[i])) {
				for (int j = 0; j < guessLetters.length; j++) {
					if (String.valueOf(firstname.charAt(j)).equals(availableLetters[i])) {
						if (guessLetters[j].equals("-1")) {
							guessLetters[j] = availableLetters[i];
							availableLetters[i] = "-1";
							letterAwarded = true;
							break;
						}
					}
				}
			}
			if (letterAwarded)
				break;
		}
		if (find_first_blank_guess() < 0)
			return true;
		else
			return false;
	}
}
