package com.friendiq.android.store;

import java.util.ArrayList;
import java.util.Calendar;

import com.friendiq.android.billutil.IabResult;
import com.friendiq.android.billutil.Purchase;
import com.friendiq.android.PrefHelper;
import com.friendiq.android.R;
import com.friendiq.android.billutil.IabHelper;
import com.friendiq.android.helpers.NetworkProgressBar;
import com.friendiq.android.store.StoreActivity.CoinsUpdatedCallback;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class StoreItemAdapter extends BaseAdapter {

	static final int RC_REQUEST = 10001;

	static final String SKU_300 = "coins_300";
	static final String SKU_700 = "coins_700";
	static final String SKU_1500 = "coins_1500";
	static final String SKU_4500 = "coins_4500";
	static final String SKU_10000 = "coins_10000";
	
	static final double SKU_300_COST = 0.99;
	static final double SKU_700_COST = 1.99;
	static final double SKU_1500_COST = 2.99;
	static final double SKU_4500_COST = 9.99;
	static final double SKU_10000_COST = 19.99;
	
	public int selectedIndex;
	
	PrefHelper pHelper;
	
	String payload;
	IabHelper iapHelper;
	
	ArrayList<String> skus;

	CoinsUpdatedCallback callback;
	NetworkProgressBar progBar;
	
	Activity context;
	
	public StoreItemAdapter(Activity context, IabHelper iapHelper, CoinsUpdatedCallback callback, NetworkProgressBar progBar) {
		this.skus = new ArrayList<String>();
		skus.add(SKU_300);
		skus.add(SKU_700);
		skus.add(SKU_1500);
		skus.add(SKU_4500);
		skus.add(SKU_10000);
		this.pHelper = new PrefHelper(context);
		this.iapHelper = iapHelper;
		this.context = context;
		this.callback = callback;
		this.progBar = progBar;
	}
	
	@Override
	public int getCount() {
		return skus.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View optionView;
		if (convertView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			optionView = inflater.inflate(R.layout.list_item_image_text, null, true);
		} else {
			optionView = convertView;
		}
		
		TextView title = (TextView) optionView.findViewById(R.id.text);
		Button button = (Button) optionView.findViewById(R.id.cmdBuy);		
		
		if (skus.get(position).equals("coins_300"))	{
			button.setText("$" + String.format("%.2f", SKU_300_COST));
			title.setText("300 coins");
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {		
					progBar.show("purchasing..");
					payload = Calendar.getInstance().toString();
					iapHelper.launchPurchaseFlow(context, SKU_300, RC_REQUEST, 
			                PurchaseFinishedListener, payload);
				}			
			});
		} else if (skus.get(position).equals("coins_700"))	{
			button.setText("$" + String.format("%.2f", SKU_700_COST));
			title.setText("700 coins");
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {		
					progBar.show("purchasing..");
					payload = Calendar.getInstance().toString();
					iapHelper.launchPurchaseFlow(context, SKU_700, RC_REQUEST, 
			                PurchaseFinishedListener, payload);
				}			
			});
		} else if (skus.get(position).equals("coins_1500"))	{
			button.setText("$" + String.format("%.2f", SKU_1500_COST));
			title.setText("1500 coins");
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {		
					progBar.show("purchasing..");
					payload = Calendar.getInstance().toString();
					iapHelper.launchPurchaseFlow(context, SKU_1500, RC_REQUEST, 
			                PurchaseFinishedListener, payload);
				}			
			});
		} else if (skus.get(position).equals("coins_4500"))	{
			button.setText("$" + String.format("%.2f", SKU_4500_COST));
			title.setText("4500 coins");
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {		
					progBar.show("purchasing..");
					payload = Calendar.getInstance().toString();
					iapHelper.launchPurchaseFlow(context, SKU_4500, RC_REQUEST, 
			                PurchaseFinishedListener, payload);
				}			
			});
		} else if (skus.get(position).equals("coins_10000"))	{
			button.setText("$" + String.format("%.2f", SKU_10000_COST));
			title.setText("10000 coins");
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {		
					progBar.show("purchasing..");
					payload = Calendar.getInstance().toString();
					iapHelper.launchPurchaseFlow(context, SKU_10000, RC_REQUEST, 
			                PurchaseFinishedListener, payload);
				}			
			});
		}

		return optionView;
	}

	/** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        
        if (payload.equals(this.payload))
        	return true;
        else
        	return false;
    }
    
    IabHelper.OnIabPurchaseFinishedListener PurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {     
        	if (result.isFailure()) {
				progBar.change_message("failed");
        	} else if (!verifyDeveloperPayload(purchase)) {
				progBar.change_message("failed");
        	} else {        	
				progBar.change_message("incrementing coins..");        	
	        	iapHelper.consumeAsync(purchase, ConsumeFinishedListener);	        	
	        	return;
        	}
        	
			progBar.hide();
        }
    };
    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener ConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
        	if (result.isSuccess()) {
				progBar.change_message("success!");
				if (purchase.getSku().equals(SKU_300)) {
					pHelper.add_to_coin_count(300);
				} else if (purchase.getSku().equals(SKU_700))	{
					pHelper.add_to_coin_count(700);
	    		} else if (purchase.getSku().equals(SKU_1500))	{
					pHelper.add_to_coin_count(1500);
	    		} else if (purchase.getSku().equals(SKU_4500))	{
					pHelper.add_to_coin_count(4500);
	    		} else if (purchase.getSku().equals(SKU_10000))	{
					pHelper.add_to_coin_count(10000);
	    		}
				callback.callback(1);
        	} else {
				progBar.change_message("failed");
        	}
			progBar.hide();
        }
    };
}
