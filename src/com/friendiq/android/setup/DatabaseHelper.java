package com.friendiq.android.setup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	  public static final String TABLE_CONTACTS = "contacts";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_FIRSTNAME = "firstname";
	  public static final String COLUMN_LASTNAME = "lastname";
	  public static final String COLUMN_SOURCE_ID = "datasourceid";
	  public static final String COLUMN_SOURCE = "datasource";
		  
	  private static final String DATABASE_NAME = "friendiq.db";
	  private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	  private static final String DATATABLE_CONTACT_CREATE = "create table "
	      + TABLE_CONTACTS + "(" 
		  + COLUMN_ID + " integer primary key autoincrement, " 
	      + COLUMN_FIRSTNAME + " text not null, "
	      + COLUMN_LASTNAME + " text not null, "
	      + COLUMN_SOURCE_ID + " text not null, "
	      + COLUMN_SOURCE + " text not null"
	      + ");";
	
	  public DatabaseHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }
	  
	  @Override
	  public void onCreate(SQLiteDatabase database) {
		try {
			database.execSQL(DATATABLE_CONTACT_CREATE);
		} catch (SQLiteException e) {
			Log.i(DatabaseHelper.class.getName(), e.getMessage());
		}		
		
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    //Log.w(DatabaseHelper.class.getName(),
	    //    "Upgrading database from version " + oldVersion + " to "
	    //        + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);	    
	    onCreate(db);
	  }
}
