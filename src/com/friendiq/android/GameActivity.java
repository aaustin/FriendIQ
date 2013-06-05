package com.friendiq.android;

import com.flurry.android.FlurryAgent;

import android.os.Bundle;
import android.view.Display;
import android.app.Activity;
import android.graphics.Point;

public class GameActivity extends Activity {

	GameView gameView;
	private int userid;		
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		gameView = (GameView) findViewById(R.id.gameView);
		
		Bundle extras = getIntent().getExtras();
        this.userid = Integer.valueOf(extras.getString("userid"));
               

       
        
	}	

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, getString(R.string.flurry_id));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}	
}
