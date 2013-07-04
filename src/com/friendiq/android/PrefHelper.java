package com.friendiq.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class PrefHelper {
	private static final String APP_SHARED_PREFS = "com.friendiq.mainpref";

	private static final String FIRST_BOOTUP = "first_bootup";
	private static final String FRIEND_COUNT = "friend_count";
	private static final String GAME_IN_PROGRESS = "game_in_progress";
    private static final String FACEBOOK_ENABLE = "facebook_enable";
    private static final String FACEBOOK_FAILED = "facebook_failed";
    private static final String FACEBOOK_CONTACTS_SERVICE = "facebook_contacts_service";
	
	private SharedPreferences appSharedPrefs;
	private Editor prefsEditor;	
	
	Context ctx;
	
	public PrefHelper(Context context) {
		this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
		
		ctx = context;
		this.prefsEditor = appSharedPrefs.edit();
	}
		
	// check if internet is enabled
 	public boolean check_enable_online() {
 	    ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
 	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
 	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
 	        return true;
 	    }
 	    return false;
 	}
 		
 	// FIRST BOOT UP
 	public boolean get_first_bootup_status() {
 		return appSharedPrefs.getBoolean(FIRST_BOOTUP, true);
 	}
 		
 	public void set_first_bootup_status(boolean check) {
 		prefsEditor.putBoolean(FIRST_BOOTUP, check);
 		prefsEditor.commit();
 	}
 	
 	// GAME IN PROGRESS
 	public boolean get_game_in_progress_status() {
 		return appSharedPrefs.getBoolean(GAME_IN_PROGRESS, false);
 	}
 	
 	public void set_game_in_progress_status(boolean status) {
 		prefsEditor.putBoolean(GAME_IN_PROGRESS, status);
 		prefsEditor.commit();
 	}

 	// COUNT OF FRIENDS
 	public int get_friend_count() {
 		return appSharedPrefs.getInt(FRIEND_COUNT, 0);
 	}
 	
 	public void set_friend_count(int count) {
 		prefsEditor.putInt(FRIEND_COUNT, get_friend_count() + count);
 		prefsEditor.commit();
 	}
 	
	// FACEBOOK SESSION
	
	public boolean get_facebook_enable() {
		return appSharedPrefs.getBoolean(FACEBOOK_ENABLE, false);
	}
		
	public void set_facebook_enable(boolean check) {
		prefsEditor.putBoolean(FACEBOOK_ENABLE, check);
		prefsEditor.commit();
	}
	
	public boolean get_facebook_failed() {
		return appSharedPrefs.getBoolean(FACEBOOK_FAILED, false);
	}
		
	public void set_facebook_failed(boolean check) {
		prefsEditor.putBoolean(FACEBOOK_FAILED, check);
		prefsEditor.commit();
	}
		
	public boolean get_facebook_contact_service_status() {
		return appSharedPrefs.getBoolean(FACEBOOK_CONTACTS_SERVICE, false);
	}
		
	public void set_facebook_contacts_service_status(boolean check) {
		prefsEditor.putBoolean(FACEBOOK_CONTACTS_SERVICE, check);
		prefsEditor.commit();
	}	
			
}
