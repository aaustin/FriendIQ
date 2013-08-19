package com.friendiq.android;

import android.graphics.Rect;

public class ImageSection {

	int index;
	boolean sliding;
	boolean isBlank;
	public Rect source;
	public Rect dest;
	
	public ImageSection() {
		isBlank = false;
		sliding = false;
	}
	
	public ImageSection copy() {
		ImageSection newGuy = new ImageSection();
		newGuy.index = this.index;
		newGuy.sliding = this.sliding;
		newGuy.isBlank = this.isBlank;
		newGuy.source = new Rect(
				this.source.left, 
				this.source.top,
				this.source.right,
				this.source.bottom);				
		newGuy.dest = new Rect(
				this.dest.left, 
				this.dest.top,
				this.dest.right,
				this.dest.bottom);
		return newGuy;
	}
	
}
