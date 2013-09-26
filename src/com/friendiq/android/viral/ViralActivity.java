package com.friendiq.android.viral;

import java.util.ArrayList;

import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.flurry.android.FlurryAgent;
import com.friendiq.android.Contact;
import com.friendiq.android.GameActivity;
import com.friendiq.android.PrefHelper;
import com.friendiq.android.R;
import com.friendiq.android.setup.ContactDataAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

public class ViralActivity extends Activity {
	private static int MAX_INVITE_SIZE = 50;
	
	ArrayList<Contact> selectedContacts;
	Button cmdNext;
	CheckBox chkSelectAll;
	FriendAdapter fAdapter;
	
	PrefHelper pHelper;
	
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viral);
		pHelper = new PrefHelper(this);
		this.context = this;
		chkSelectAll = (CheckBox) findViewById(R.id.chkSelectAll);
		chkSelectAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {			
				fAdapter.toggle_select_all();
			}			
		});
		
		cmdNext = (Button) findViewById(R.id.cmdNext);
		cmdNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				selectedContacts = fAdapter.get_selected();
				if (selectedContacts.size() > 0) {
					batch_request(0);
				} else {
					Intent i = new Intent(getApplicationContext(), GameActivity.class);
					i.putExtra("userid", "-1");
					startActivity(i);
				}
			}			
		});
		
		ListView friendList = (ListView) findViewById(R.id.friend_list);
		ContactDataAdapter cda = new ContactDataAdapter(this);
		cda.open_for_read();
		ArrayList<Contact> contacts = cda.get_all_contacts();
		cda.close();
		fAdapter = new FriendAdapter(this, contacts);
		friendList.setAdapter(fAdapter);
	}

	private void batch_request(final int startIndex) {
		String ids = "";
		//selectedContacts.size()
		for (int i = startIndex; i < Math.min(selectedContacts.size(), startIndex+MAX_INVITE_SIZE); i++) {
			if (selectedContacts.get(i).datasource.equals("fb"))
				ids = ids + selectedContacts.get(i).datasourceid + ",";
		}
		ids = ids.substring(0, ids.length()-1);
		
		Bundle params = new Bundle();
		params.putString("to", ids);
		params.putString("title", "Friend IQ puzzle challenge");
	    params.putString("message", "Can you beat my friend IQ?");
	    params.putString("frictionless", "1");
	    
	    WebDialog requestsDialog = (
	        new WebDialog.RequestsDialogBuilder(context,
	            Session.getActiveSession(),
	            params))
	            .setOnCompleteListener(new OnCompleteListener() {
	                @Override
	                public void onComplete(Bundle values, FacebookException error) {
	                    if (error != null) {
	                        
	                    } else {
	                        final String requestId = values.getString("request");
	                        if (requestId != null) {
	        					pHelper.set_invited_friend_status(true);
	                        	if (startIndex+MAX_INVITE_SIZE > selectedContacts.size()) {
	                        		Intent i = new Intent(getApplicationContext(), GameActivity.class);
		        					i.putExtra("userid", "-1");
		        					startActivity(i);
	                        	} else
	                        		batch_request(startIndex + MAX_INVITE_SIZE);
	                        } 
	                    }   
	                }

	            })
	            .build();
	    requestsDialog.show();
	}
	
	@Override
	public void onBackPressed() { }
}
