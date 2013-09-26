package com.friendiq.android.setup;

import com.facebook.Session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;
import com.flurry.android.FlurryAgent;
import com.friendiq.android.CallBack;
import com.friendiq.android.GameActivity;
import com.friendiq.android.PrefHelper;
import com.friendiq.android.R;
import com.friendiq.android.helpers.KindredAlertDialog;
import com.friendiq.android.helpers.NetworkProgressBar;
import com.friendiq.android.viral.ViralActivity;

import android.os.Bundle;
import android.util.Base64;
//import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;

public class FacebookSignupActivity extends Activity {
	private static final long UPDATE_INTERVAL = 200;
	
	
	Context context;
	
	NetworkProgressBar progBar;
	Timer fbtimer;
	PrefHelper pHelper;
	FacebookContacts fbContacts;
	ParseContacts parser;
	PhoneContactImportDone phoneDone;
	TextView cmdPhoneContacts;
	Button cmdFacebookLogin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook_signup);
		pHelper = new PrefHelper(this);
		context = this;
		progBar = new NetworkProgressBar(this);						

		phoneDone = new PhoneContactImportDone();
		parser = new ParseContacts(context, null);		
		if (!pHelper.get_phone_download_status()) {			
			progBar.show("initializing..");

			new Thread(new Runnable() {
				@Override
				public void run() {
					parser.download_phone_contacts(phoneDone);
					pHelper.set_phone_download_status(true);
				}				
			}).start();			
		}
		
		cmdPhoneContacts = (TextView) findViewById(R.id.txtContacts);
		cmdPhoneContacts.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FlurryAgent.logEvent("AddressBook_Play_Pressed");
				start_game();
			}
		});
		
		fbContacts = new FacebookContacts(this, savedInstanceState);	
			
		
		cmdFacebookLogin = (Button) findViewById(R.id.cmdFBlogin);		
		cmdFacebookLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {					
				FlurryAgent.logEvent("Facebook_Play_Pressed");
				progBar.show("connecting to Facebook...");
				pHelper.set_facebook_contacts_service_status(true);
				fbContacts.login_and_download_facebook();
				wait_on_fb();
			}
		});
		
		if (!pHelper.get_first_bootup_status()) {			
			if (pHelper.get_facebook_enable()) {
				if (pHelper.get_invited_friend_status())
					start_game();
				else 
					start_viral();
			}
		}
		
		if (!pHelper.check_enable_online()) {
			FlurryAgent.logEvent("Internets_Yes");
			KindredAlertDialog kad = new KindredAlertDialog(context, "Friend IQ requires a stable internet connection for use.\n\nPlease wait until you have better service or connect to wifi.\n\nSorry!", false);			
			kad.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					//Log.i(getClass().getSimpleName(), "DISMISSED");
					finish();
				}								
			});
			kad.show();
		} else 
			FlurryAgent.logEvent("Internets_No");

		FlurryAgent.logEvent("Intro_Duration" , null, true);
		//printHashKey();
	}
	
	public void printHashKey() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo ("com.friendiq.android",
                    PackageManager.GET_SIGNATURES );
            for (Signature signature : info.signatures ) {
                MessageDigest md = MessageDigest. getInstance("SHA");
                md.update (signature. toByteArray());
                //Log.i("TEMPTAGHASH KEY:",
                //        Base64.encodeToString( md.digest (), Base64. DEFAULT));
            }
        } catch (NameNotFoundException e) {
               e.printStackTrace ();
        } catch (NoSuchAlgorithmException e) {
               e.printStackTrace ();
        }

    }

	
	private void start_viral() {
		FlurryAgent.endTimedEvent("Intro_Duration");

		Intent i = new Intent(getApplicationContext(), ViralActivity.class);
		startActivity(i);
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
             				start_viral();
             				pHelper.set_first_bootup_status(false);
             				progBar.hide();
             				fbtimer.cancel();
             			} else if (pHelper.get_facebook_failed()) {
             				progBar.show("failed to connect!");
             				//Log.i(DatabaseHelper.class.getName(),"FAILED FB DOWNLOAD");             				
             				progBar.hide();
             				fbtimer.cancel();
             			}
             		}
  				});  					
  			}
  		}, 0, UPDATE_INTERVAL);
	}	
	
	public class PhoneContactImportDone implements CallBack {
		@Override
		public void callback(int threadID) {
			progBar.hide();
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
