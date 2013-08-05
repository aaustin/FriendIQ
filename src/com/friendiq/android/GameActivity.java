package com.friendiq.android;

import com.flurry.android.FlurryAgent;
import com.friendiq.android.helpers.NetworkProgressBar;
import com.friendiq.android.store.StoreActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class GameActivity extends Activity {
	
	ImageView portraitView;
	static NetworkProgressBar progBar;
	GameView gameView;
	private int userid;		
	SurfaceViewReady gameReady;
	
	TextView txtFriendIQ;
	TextView txtCoins;
	Button cmdBuyCoins;
	
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		this.context = this;
		
		PrefHelper pHelper = new PrefHelper(this);
		
		portraitView = (ImageView) findViewById(R.id.imgPortrait);
		txtFriendIQ = (TextView) findViewById(R.id.txtIQValue);
		txtCoins = (TextView) findViewById(R.id.txtCoins);
		txtCoins.setText(pHelper.get_coin_count() + " coins");
		txtFriendIQ.setText(pHelper.get_friend_iq() + " ");		
		
		progBar = new NetworkProgressBar(this);
		progBar.show("initializing..");
		
		gameView = (GameView) findViewById(R.id.gameView);
		
		Bundle extras = getIntent().getExtras();
        this.userid = Integer.valueOf(extras.getString("userid"));
		Log.i(getClass().getSimpleName(), "user id " + this.userid);

        gameReady = new SurfaceViewReady();        
        gameView.initialize_game(this.userid, gameReady, portraitView, txtCoins);
        
        cmdBuyCoins.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, StoreActivity.class);
				context.startActivity(intent);
			}        	
        });
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
