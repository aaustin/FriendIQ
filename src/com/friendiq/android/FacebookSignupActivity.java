package com.friendiq.android;

import com.facebook.Session;
import java.util.Timer;
import java.util.TimerTask;
import com.flurry.android.FlurryAgent;
import android.os.Bundle;
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
	PhoneCallback phoneCallback;
	ParseContacts parser;
	
	TextView cmdPhoneContacts;
	Button cmdFacebookLogin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook_signup);
		pHelper = new PrefHelper(this);
		context = this;
		
		if (pHelper.get_first_bootup_status()) {
			//fbContacts = new FacebookContacts(this, savedInstanceState);	
			progBar = new NetworkProgressBar(this);
			phoneCallback = new PhoneCallback();
			
			cmdPhoneContacts = (TextView) findViewById(R.id.txtContacts);
			cmdPhoneContacts.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					progBar.show("connecting to contacts...");
					pHelper.set_facebook_contacts_service_status(true);
					parser = new ParseContacts(context, null, phoneCallback);
					parser.download_phone_contacts();
					wait_on_fb();
				}
			});
			cmdFacebookLogin = (Button) findViewById(R.id.cmdFBlogin);		
			cmdFacebookLogin.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					progBar.show("connecting to Facebook...");
					pHelper.set_facebook_contacts_service_status(true);
					//fbContacts.login_and_download_facebook();
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
             			if (!pHelper.get_facebook_contact_service_status()) {	
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
	
	public class PhoneCallback implements CallBack {
		@Override
		public void callback(int threadID) {
			// TODO Auto-generated method stub
			
		}		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		//FlurryAgent.onStartSession(this, getString(R.string.flurry_id));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		//FlurryAgent.onEndSession(this);
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
