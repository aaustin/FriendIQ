package com.friendiq.android;

import java.util.List;
import com.facebook.model.GraphUser;
import com.friendiq.android.FacebookContacts.ImportDoneFB;
import com.friendiq.android.FacebookSignupActivity.PhoneCallback;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.util.Log;

public class ParseContacts {
	
	Context context;
	ContactDataAdapter cda;
	ImportDoneFB fbcallback;
	PhoneCallback phoneCallback;
	PrefHelper pHelper;
	
	public ParseContacts(Context context, ImportDoneFB fbcallback, PhoneCallback phoneCallback) {
		this.context = context;
		this.pHelper = new PrefHelper(context);
		this.fbcallback = fbcallback;
		this.phoneCallback = phoneCallback;
		cda = new ContactDataAdapter(context);
	}
	
	private boolean has_photo(int id) {
		String[] PROJECTION = new String[] {
    			"data15"
    	};
		
		ContentResolver contentResolver = context.getContentResolver();
		Cursor data = contentResolver.query(ContactsContract.Data.CONTENT_URI, PROJECTION, 
    			ContactsContract.Data.CONTACT_ID + "=? AND (" 
    					+ ContactsContract.Data.MIMETYPE + "=?)", 
    			new String[]{String.valueOf(id), 
					Photo.CONTENT_ITEM_TYPE}, 
    			null);
		
		if (data.getCount() > 0) {
			data.close();
			return true;
		} else {
			data.close();
			return false;
		}
	}
	
	public void download_phone_contacts() {
		cda.open_for_write();
		cda.begin_transactions();
		
		ContentResolver contentResolver = context.getContentResolver();
		
		String[] PROJECTION = new String[] {
				ContactsContract.Data.CONTACT_ID,"data3", "data2"
    	};
    	
    	Cursor data = contentResolver.query(ContactsContract.Data.CONTENT_URI, PROJECTION, 
    			ContactsContract.Data.MIMETYPE + "=?", 
    			new String[]{StructuredName.CONTENT_ITEM_TYPE},    					
    			null);
    	
    	boolean goodData = true;
    	
		int count = 0;
    	Contact currContact = new Contact();
		currContact.datasource = "phone";
    	if (data.getCount() > 0) {
    		int idCol = data.getColumnIndex(StructuredName.CONTACT_ID);
    		int firstCol = data.getColumnIndex(StructuredName.GIVEN_NAME);
    		int lastCol = data.getColumnIndex(StructuredName.FAMILY_NAME);
    		int imgCol = data.getColumnIndex(Photo.PHOTO);
    		
	   		while(data.moveToNext()) { 
	   			currContact.datasourceid = String.valueOf(data.getInt(idCol));
	   			currContact.firstname = data.getString(firstCol);
	   			currContact.lastname = data.getString(lastCol);
	   			if (currContact.firstname == null || currContact.lastname == null || has_photo(data.getInt(idCol))) {
	   				Log.i(getClass().getName(),"Bad contact");
	   				goodData = false;
	   			} else if (currContact.firstname.length() > 0 && currContact.lastname.length() > 0){
	   				Log.i(getClass().getName(),"Assigned name: " + currContact.firstname + " " + currContact.lastname);
		   			currContact.firstname = currContact.firstname.substring(0, 1).toUpperCase() + currContact.firstname.substring(1);
		   			currContact.lastname = currContact.lastname.substring(0, 1).toUpperCase() + currContact.lastname.substring(1);
		   			currContact.firstname = currContact.firstname.replace("'", "''");
		   			currContact.lastname = currContact.lastname.replace("'", "''");		   				   			  
	   			} else
	   				goodData = false;
	   			
	   			if (goodData) { 	            	
	   				cda.add_new_contact(currContact);
	   				count = count + 1;
	   			}
	   			
	   			goodData = true;
   		   	 }   		   	 
   		}            		
   	 	           	
    	data.close();
    	
		//Log.i(DatabaseHelper.class.getName(),"FINISHED PARSING");
		cda.end_transactions();		
		cda.close();		
	
		pHelper.set_friend_count(count);
		pHelper.set_facebook_contacts_service_status(false);
		
    	phoneCallback.callback(1);
	}
	
	public void download_facebook_contacts(List<GraphUser> users) {
		cda.open_for_write();
		cda.begin_transactions();		

		int count = 0;
		Contact curr = new Contact();
		curr.datasource = "fb";
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
