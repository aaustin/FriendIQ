package com.friendiq.android;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.friendiq.android.GameView.ImageReady;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

public class Contact {
	public int index;	
	public String firstname;
	public String lastname;
	public String datasourceid;
	public String datasource;
	public Bitmap bm;
	
	public Contact() {
		this.index = -1;
		this.firstname = "none";
		this.lastname = "none";
		this.datasourceid = "none";
		this.datasource = "none";
		this.bm = null;
	}
	
	public Contact(int index, String firstname, String lastname, String datasourceid, String datasource) {
		this.index = index;		
		this.firstname = firstname;
		this.lastname = lastname;		
		this.datasourceid = datasourceid;		
		this.datasource = datasource;
	}
	
	public Contact clone() {
		Contact con = new Contact();
		con.index = this.index;
		con.firstname = this.firstname;
		con.lastname = this.lastname;
		con.datasourceid = this.datasourceid;
		con.datasource = this.datasource;
		con.bm = this.bm;
		return con;
	}	
	
	public void download_photo(Context context, ImageReady callback) {
		int status = 1;
		
		if (datasource.equals("fb")) {
			String url = "http://graph.facebook.com/";
            url = url + datasourceid;
            url = url + "/picture?width=500&height=500";
            Log.i(getClass().getSimpleName(), "FB PHOTO");
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.setConnectTimeout(500);
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(new FlushedInputStream(is));
                bis.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
                status = 0;
            } 
            
            if (bm == null)
            	status = 0;
		} else {
			// local image
			/*String[] PROJECTION = new String[] {
	    			"data15"
	    	};*/
			if (!datasourceid.equals("none")) {
				Log.i(getClass().getSimpleName(), "PHONE PHOTO");
				//byte[] portrait = null;
				ContentResolver contentResolver = context.getContentResolver();
				/*Cursor data = contentResolver.query(ContactsContract.Data.CONTENT_URI, PROJECTION, 
		    			ContactsContract.Data.CONTACT_ID + "=? AND (" 
		    					+ ContactsContract.Data.MIMETYPE + "=?)", 
		    			new String[]{datasourceid, 
							Photo.CONTENT_ITEM_TYPE}, 
		    			null);*/
				Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, Long.parseLong(datasourceid));
				Uri displayPhotoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.DISPLAY_PHOTO);
				try {
					AssetFileDescriptor fd = contentResolver.openAssetFileDescriptor(displayPhotoUri, "r");
					InputStream is = fd.createInputStream();
					BufferedInputStream bis = new BufferedInputStream(is);
		   			bm = BitmapFactory.decodeStream(new FlushedInputStream(is));
				} catch (IOException e) {
					status = 0;
				}
	   			
	   			//portrait = IOUtils.toByteArray(fd.createInputStream());
				/*
				if (data.getCount() > 0) {
			   		while(data.moveToNext()) {
			   			
			   			//portrait = data.getBlob(data.getColumnIndex(Photo.PHOTO));
			   			if (portrait != null)
			   				break;
			   		}
				}		
				
				data.close();*/
				
				
				if (bm == null)
					status = 0;
					//bm = BitmapFactory.decodeByteArray(portrait, 0, portrait.length);
				//else
			} else {
				status = 0;
			}
				
		}
		
		callback.callback(status);
	}
	
	static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break; // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }	
}
