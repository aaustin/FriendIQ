package com.friendiq.android;

import com.flurry.android.FlurryAgent;

import android.os.Bundle;
import android.app.Activity;

public class GameActivity extends Activity {
	
	static NetworkProgressBar progBar;
	GameView gameView;
	private int userid;		
	SurfaceViewReady gameReady;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		progBar = new NetworkProgressBar(this);
		progBar.show("initializing..");
		
		gameView = (GameView) findViewById(R.id.gameView);
		
		Bundle extras = getIntent().getExtras();
        this.userid = Integer.valueOf(extras.getString("userid"));
     
        gameReady = new SurfaceViewReady();        
        gameView.initialize_game(this.userid, gameReady);
	}	
	
	// interface implementation image matrix ready callback
  	public class SurfaceViewReady implements CallBack {
  		public void callback(int threadID) {
  			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progBar.change_message("finished!");
					progBar.hide();
				}  				
  			});  			
  		}
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
