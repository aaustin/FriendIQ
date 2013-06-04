package com.friendiq.android;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;

public class FacebookSignupActivity extends Activity {
	private static final long UPDATE_INTERVAL = 200;
	
	NetworkProgressBar progBar;
	Timer fbtimer;
	PrefHelper pHelper;
	FacebookContacts fbContacts;
	
	Button cmdFacebookLogin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook_signup);
		pHelper = new PrefHelper(this);
		
		if (pHelper.get_first_bootup_status()) {
			fbContacts = new FacebookContacts(this, savedInstanceState);
			progBar = new NetworkProgressBar(this);
					
			cmdFacebookLogin = (Button) findViewById(R.id.cmdFBlogin);		
			cmdFacebookLogin.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					progBar.show("connecting to Facebook...");
					pHelper.set_facebook_contacts_service_status(true);
					fbContacts.login_and_download_facebook();
					wait_on_fb();
				}
			});
		} else {
			start_game();
		}
	}

	private void start_game() {
		Intent i = new Intent(getApplicationContext(), GameActivity.class);
		i.putExtra("userid", "-1");
		startActivity(i);
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
             				start_game();
             				pHelper.set_first_bootup_status(false);
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
