package com.friendiq.android;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;

public class FacebookSignupActivity extends Activity {
	private static final long UPDATE_INTERVAL = 200;
	
	NetworkProgressBar progBar;
	Timer fbtimer;
	PrefHelper pHelper;
	FacebookContacts fbContacts;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook_signup);
	}

	private void wait_on_fb() {
		fbtimer = new Timer();
		fbtimer.scheduleAtFixedRate(new TimerTask() {
  			@Override
  			public void run() {				
  				runOnUiThread(new Runnable() {
             		public void run() { 		 			
             			if (!pHelper.get_facebook_contact_service_status() && pHelper.get_facebook_enable()) {	
             				progBar.show("finished!");
             				//Log.i(DatabaseHelper.class.getName(),"FINISHED FB DOWNLOAD");
             				// LOAD NEXT ACTIVITY
             				progBar.hide();
             				fbtimer.cancel();
             			} else if (pHelper.get_facebook_failed()) {
             				progBar.show("failed to connect!");
             				//Log.i(DatabaseHelper.class.getName(),"FINISHED FB DOWNLOAD");             				
             				progBar.hide();
             				fbtimer.cancel();
             			}
             		}
  				});  					
  			}
  		}, 0, UPDATE_INTERVAL);
	}
	
}
