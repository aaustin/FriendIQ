package com.friendiq.android.store;

import com.friendiq.android.CallBack;
import com.friendiq.android.PrefHelper;
import com.friendiq.android.R;
import com.friendiq.android.billutil.IabHelper;
import com.friendiq.android.billutil.IabHelper.OnIabSetupFinishedListener;
import com.friendiq.android.billutil.IabResult;
import com.friendiq.android.helpers.NetworkProgressBar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class StoreActivity extends Activity {
   
    NetworkProgressBar progBar;
    
    StoreItemAdapter itemAdapter;
    
	PrefHelper pHelper;
	TextView txtCoins;
	ListView lstStore;
	IabHelper iapHelper;
	
	CoinsUpdatedCallback callback;
	
	Activity context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store);
		        
		context = this;
		pHelper = new PrefHelper(this);
		progBar = new NetworkProgressBar(this);
		
		progBar.show("loading items for purchase..");
		callback = new CoinsUpdatedCallback();
		lstStore = (ListView) findViewById(R.id.store_list);
		
		txtCoins = (TextView) findViewById(R.id.txtCoins);
		txtCoins.setText(pHelper.get_coin_count() + " coins");
		
		Button cmdExit = (Button) findViewById(R.id.cmdExit);
		cmdExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}			
		});
		
		String ran1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2gRuh9aCrhiu5cazHODVHNBwgbfRS3/JiNOJIWFx";
		String ran2 = "jCdBEjkS4SXjb/0U2JjQRBmV7rTLzTSza7ciGVpX3XJfIFyrTikvTgGCWZYYXij";
		String ran3 = "E8oy8hgzCjydhlUqSrpE5V7YDxT7L2rKXF+Tfj/xOHgcsJ0lqc5i79MM5xLjbnK8TojRqFKOXS+a8+WVHU";
		String ran4 = "XpSqSgbrgYOEpujnq1/np/qyzedcWY360s8RvOqLAj+wR8p7dto4Wym0cb6/t9iQC+YLeyro79ONKq6T0J4yjG5a";
		String ran5 = "C9yAxYAsMzxG/1YYOCxBfQUfoCbY1OMaV2Nz23pipZ4B0FVCUQZXc51cWXtJJEl7iVJxQIDAQAB";		
		String base64PubKey = ran1 + ran2 + ran3 + ran4 + ran5;
		iapHelper = new IabHelper(this, base64PubKey);
		iapHelper.enableDebugLogging(true);
		iapHelper.startSetup(new OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					progBar.change_message("failed");
					Log.d(getClass().toString(), "Problem setting up In-app Billing: " + result);
				} else {         
					progBar.change_message("success!");					
					itemAdapter = new StoreItemAdapter(context, iapHelper, callback, progBar);
					lstStore.setAdapter(itemAdapter);
				}
				// Hooray, IAB is fully set up!
				progBar.hide();
			}			
		});
	}
	
	public class CoinsUpdatedCallback implements CallBack {
		@Override
		public void callback(int threadID) {
			txtCoins.setText(pHelper.get_coin_count() + " coins");
		}		
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {        
        // Pass on the activity result to the helper for handling
        if (!iapHelper.handleActivityResult(requestCode, resultCode, data)) {          
            super.onActivityResult(requestCode, resultCode, data);
        }       
    }
	
	@Override
	public void onDestroy() {
	   super.onDestroy();
	   if (iapHelper != null) iapHelper.dispose();
	   iapHelper = null;
	}
	
}
