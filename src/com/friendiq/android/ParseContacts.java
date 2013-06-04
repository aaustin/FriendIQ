package com.friendiq.android;

import java.util.List;
import com.facebook.model.GraphUser;
import com.friendiq.android.FacebookContacts.ImportDoneFB;
import android.content.Context;

public class ParseContacts {
	
	Context context;
	ContactDataAdapter cda;
	ImportDoneFB fbcallback;
	PrefHelper pHelper;
	
	public ParseContacts(Context context, ImportDoneFB fbcallback) {
		this.context = context;
		this.pHelper = new PrefHelper(context);
		this.fbcallback = fbcallback;
		cda = new ContactDataAdapter(context);
	}
	
	public void download_facebook_contacts(List<GraphUser> users) {
		cda.open();
		cda.begin_transactions();		

		int count = 0;
		Contact curr = new Contact();	
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getName().length() > 0) {				
				curr = solve_names(users.get(i).getName(), curr);			
				if (curr != null) {
					curr.datasourceid = users.get(i).getId();
					cda.add_new_contact(curr);
					count = count + 1;
				}
			}
		}
		pHelper.set_friend_count(count);
		
		cda.end_transactions();		
		cda.close();		

		users = null;
		
		fbcallback.callback(1); // parse contacts done
	}
	
	private Contact solve_names(String name, Contact contact) {
		//Log.i(DatabaseHelper.class.getName(),"name: " + name);
		String[] split = name.split(" ");
		String firstname = "";
		String lastname = split[split.length-1];		
		for(int i = 0; i < split.length-1; i++)
			firstname = firstname + split[i] + " ";
		
		if (contact.firstname.length() > 0 && contact.lastname.length() > 0) {
			contact.firstname = firstname.trim();
			contact.lastname = lastname;
			
			contact.firstname = contact.firstname.substring(0, 1).toUpperCase() + contact.firstname.substring(1);
			contact.lastname = contact.lastname.substring(0, 1).toUpperCase() + contact.lastname.substring(1);
			contact.firstname = contact.firstname.replace("'", "''");
			contact.lastname = contact.lastname.replace("'", "''");		
				
			return contact;
		} else
			return null;
		
	}
}
