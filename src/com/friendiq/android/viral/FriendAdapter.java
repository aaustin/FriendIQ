package com.friendiq.android.viral;

import java.util.ArrayList;
import java.util.Calendar;

import com.friendiq.android.billutil.IabResult;
import com.friendiq.android.billutil.Purchase;
import com.friendiq.android.Contact;
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
import android.widget.CheckBox;
import android.widget.TextView;

public class FriendAdapter extends BaseAdapter {

	public ArrayList<Contact> contacts;
	public boolean[] selected;
	Activity context;
	
	boolean selectAll;
	
	public FriendAdapter(Activity context, ArrayList<Contact> contacts) {
		this.context = context;
		this.contacts = contacts;
		selected = new boolean[contacts.size()];
		selectAll = true;
	}
	
	public ArrayList<Contact> get_selected() {
		ArrayList<Contact> selectedContacts = new ArrayList<Contact>();
		for (int i = 0; i < contacts.size(); i++)
			if (selected[i])
				selectedContacts.add(contacts.get(i));
		
		return selectedContacts;
	}
	
	public void toggle_select_all() {
		if (selectAll) selectAll = false;
		else selectAll = true;
		for (int i = 0; i < contacts.size(); i++)
			selected[i] = selectAll;
		
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return contacts.size();
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
			optionView = inflater.inflate(R.layout.list_item_checkbox_text, null, true);
		} else {
			optionView = convertView;
		}
	
		CheckBox chk = (CheckBox) optionView.findViewById(R.id.checked);
		if (selected[position])
			chk.setChecked(true);
		else
			chk.setChecked(false);
		chk.setText(contacts.get(position).firstname + " " + contacts.get(position).lastname);
		
		return optionView;
	}
}