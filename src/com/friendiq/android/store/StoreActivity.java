package com.friendiq.android.store;

import com.friendiq.android.PrefHelper;
import com.friendiq.android.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class StoreActivity extends Activity {

	PrefHelper pHelper;
	TextView txtCoins;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store);
		
		pHelper = new PrefHelper(this);
	
		txtCoins = (TextView) findViewById(R.id.txtCoins);
		txtCoins.setText(pHelper.get_coin_count() + " coins");
		
		Button cmdExit = (Button) findViewById(R.id.cmdExit);
		cmdExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}			
		});
	}

	
}
