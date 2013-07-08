package com.friendiq.android;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ContactDataAdapter {

	 private SQLiteDatabase db;
	 private DatabaseHelper dbHelper;
	 private String[] contactColumns;
	 
	 public ContactDataAdapter(Context context) {
		 dbHelper = new DatabaseHelper(context);
		 contactColumns = new String[]{
			DatabaseHelper.COLUMN_ID,
			DatabaseHelper.COLUMN_FIRSTNAME,
			DatabaseHelper.COLUMN_LASTNAME,
			DatabaseHelper.COLUMN_SOURCE_ID,
			DatabaseHelper.COLUMN_SOURCE
		 };		 		
	 }
	 
	 public void open_for_read() throws SQLException {
		 db = dbHelper.getReadableDatabase();
	 }
	 
	 public void open_for_write() throws SQLException {
		 db = dbHelper.getWritableDatabase();
	 }
	 
	 public void close() {		
		 db.close();
	 }
	 
	 public void begin_transactions() {
		 db.beginTransaction();
	 }
	 
	 public void end_transactions() {
		 db.setTransactionSuccessful();
		 db.endTransaction();
	 }
	 
	 public int add_new_contact(Contact contact) {
		 if (!db.isOpen())
			 open_for_write();
		 
		 Cursor cursor = db.query(DatabaseHelper.TABLE_CONTACTS,
			        contactColumns, 
			        DatabaseHelper.COLUMN_FIRSTNAME + "='" + contact.firstname + "' AND " 
			        + DatabaseHelper.COLUMN_LASTNAME + "='" + contact.lastname + "'", 
			        null, null, null, null);
		 int id = -1;
		 if(cursor.moveToFirst()) { // contact exists
			 id = cursor.getInt(0);
			 String filter = "_id=" + id;
			 ContentValues args = new ContentValues();			 
			
			 if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SOURCE_ID)).equals("none")
					 && contact.datasourceid != "none") 
				 args.put(DatabaseHelper.COLUMN_SOURCE_ID, contact.datasourceid);
			 
			 if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SOURCE)).equals("none")
					 && contact.datasource != "none") 
				 args.put(DatabaseHelper.COLUMN_SOURCE, contact.datasource);
			 
			 if (args.size() > 0)
				 db.update(DatabaseHelper.TABLE_CONTACTS, args, filter, null);
			 
		 } else { // contact does not exist
			 ContentValues values = new ContentValues();
			 values.put(DatabaseHelper.COLUMN_FIRSTNAME, contact.firstname);
			 values.put(DatabaseHelper.COLUMN_LASTNAME, contact.lastname);
			 values.put(DatabaseHelper.COLUMN_SOURCE_ID, contact.datasourceid);
			 values.put(DatabaseHelper.COLUMN_SOURCE, contact.datasource);
			 id = (int) db.insert(DatabaseHelper.TABLE_CONTACTS, null, values);
		 }
		 
		 cursor.close();
		 
		 return id;
	 }
	 
		 
	 public ArrayList<Contact> get_all_contacts() {
		 if (!db.isOpen())
			 open_for_read();
		 
		 ArrayList<Contact> cons = new ArrayList<Contact>();
		 Contact currContact;
		 Cursor cursor = null;
		 cursor = db.query(DatabaseHelper.TABLE_CONTACTS,
					 contactColumns, null, null, null, null, DatabaseHelper.COLUMN_FIRSTNAME + ", " + DatabaseHelper.COLUMN_LASTNAME);
		 
		 if(cursor.moveToFirst()) { 
			 int idCol = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
			 int firstCol = cursor.getColumnIndex(DatabaseHelper.COLUMN_FIRSTNAME);
			 int lastCol = cursor.getColumnIndex(DatabaseHelper.COLUMN_LASTNAME);
			 int sourceidCol = cursor.getColumnIndex(DatabaseHelper.COLUMN_SOURCE_ID);
			 int sourceCol = cursor.getColumnIndex(DatabaseHelper.COLUMN_SOURCE);
			 do {
				 currContact = new Contact();
				 currContact.index = cursor.getInt(idCol);				 
				 currContact.firstname = cursor.getString(firstCol);
				 currContact.lastname = cursor.getString(lastCol);				
				 currContact.datasourceid = cursor.getString(sourceidCol);
				 currContact.datasource = cursor.getString(sourceCol);
				 cons.add(currContact);
			 } while (cursor.moveToNext());
		 }
		 
		 cursor.close();
		 return cons;
	 }
	 
	 public Contact get_contact(int index) {
		 
		 if (!db.isOpen())
			 open_for_read();
		 
		 Contact currContact = new Contact();
		 
		 Cursor currcursor = db.query(DatabaseHelper.TABLE_CONTACTS,
			        contactColumns, DatabaseHelper.COLUMN_ID + "=" + index, null, null, null, null); 
		 if (currcursor.moveToFirst()) {
			 currContact.index = index;
			 currContact.firstname = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_FIRSTNAME));
			 currContact.lastname = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_LASTNAME));		
			 currContact.datasourceid = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_SOURCE_ID));
			 currContact.datasource = currcursor.getString(currcursor.getColumnIndex(DatabaseHelper.COLUMN_SOURCE));
		 }
		 currcursor.close();
		 
		 return currContact;
	 }
}

