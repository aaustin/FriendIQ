package com.friendiq.android.setup;

import com.facebook.Session;

import java.util.Timer;
import java.util.TimerTask;
import com.flurry.android.FlurryAgent;
import com.friendiq.android.GameActivity;
import com.friendiq.android.PrefHelper;
import com.friendiq.android.R;
import com.friendiq.android.R.id;
import com.friendiq.android.R.layout;
import com.friendiq.android.R.string;
import com.friendiq.android.helpers.NetworkProgressBar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class FacebookSignupActivity extends Activity {
	private static final long UPDATE_INTERVAL = 200;
	
	
	Context context;
	
	NetworkProgressBar progBar;
	Timer fbtimer;
	PrefHelper pHelper;
	FacebookContacts fbContacts;
	ParseContacts parser;
	
	TextView cmdPhoneContacts;
	Button cmdFacebookLogin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook_signup);
		pHelper = new PrefHelper(this);
		context = this;
				
		parser = new ParseContacts(context, null);		
		if (!pHelper.get_phone_download_status()) {
			if (!pHelper.get_phone_download_status()) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						parser.download_phone_contacts();
						pHelper.set_phone_download_status(true);
					}				
				}).start();
			}
		}
		
		cmdPhoneContacts = (TextView) findViewById(R.id.txtContacts);
		cmdPhoneContacts.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				start_game();
			}
		});
		
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
		
		if (!pHelper.get_first_bootup_status()) {			
			if (pHelper.get_facebook_enable())
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
             			if (!pHelper.get_facebook_contact_service_status()) {	
             				progBar.show("finished!");
             				Log.i(DatabaseHelper.class.getName(),"FINISHED FB DOWNLOAD");
             				start_game();
             				pHelper.set_first_bootup_status(false);
             				progBar.hide();
             				fbtimer.cancel();
             			} else if (pHelper.get_facebook_failed()) {
             				progBar.show("failed to connect!");
             				Log.i(DatabaseHelper.class.getName(),"FAILED FB DOWNLOAD");             				
             				progBar.hide();
             				fbtimer.cancel();
             			}
             		}
  				});  					
  			}
  		}, 0, UPDATE_INTERVAL);
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
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
    
    // required by the facebook SDK :(
  	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
  		super.onActivityResult(requestCode, resultCode, data);
        //Log.i(this.getClass().getName(),"ON ACTIVITY RESULT CALLED");
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
  	}
}
