package com.friendiq.android.viral;

import java.util.ArrayList;

import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.friendiq.android.Contact;
import com.friendiq.android.GameActivity;
import com.friendiq.android.R;

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

public class ViralActivity extends Activity {

	Button cmdNext;
	CheckBox chkSelectAll;
	FriendAdapter fAdapter;
	
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viral);
		this.context = this;
		chkSelectAll = (CheckBox) findViewById(R.id.checked);
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
				ArrayList<Contact> selectedContacts = fAdapter.get_selected();
				if (selectedContacts.size() > 0) {
					String ids = "";
					for (int i = 0; i < selectedContacts.size(); i++) {
						if (selectedContacts.get(i).datasource.equals("fb"))
							ids = ids + selectedContacts.get(i).datasourceid + ",";
					}
					ids = ids.substring(0, ids.length()-1);
					
					Bundle params = new Bundle();
					params.putString("to", ids);
					params.putString("title", "Friend IQ puzzle challenge");
				    params.putString("message", "Can you beat my friend IQ?");

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
				        					Intent i = new Intent(getApplicationContext(), GameActivity.class);
				        					i.putExtra("userid", "-1");
				        					startActivity(i);
				                        } 
				                    }   
				                }

				            })
				            .build();
				    requestsDialog.show();
				} else {
					Intent i = new Intent(getApplicationContext(), GameActivity.class);
					i.putExtra("userid", "-1");
					startActivity(i);
				}
			}			
		});
	}

}
