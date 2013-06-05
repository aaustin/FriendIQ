package com.friendiq.android;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import com.friendiq.android.SplitImageMatrix.ImageAcquired;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageGrabber {

	private static final String url = "https://graph.facebook.com/";
	private static final String url2 = "/picture?width=400&height=400";
	
	public Contact contact;
	
	PrefHelper pHelper;
	Context context;
	private int userid;
			
	public ImageGrabber(Context context) {
		pHelper = new PrefHelper(context);
		this.context = context;			
	}
	
	public void grab_image(int index, ImageAcquired callback) {		
		final int userid = index;
		final ImageAcquired refcallback = callback;
		
		Runnable runnable = new Runnable() {
            public void run() {
            	Bitmap bm = null;
        		
        		Contact toBeGuessed = new Contact();
        		while (toBeGuessed.datasourceid.equals("none")) {
        			toBeGuessed = grab_random_contact(userid);
        		}
        		
        		String imageURL = url + toBeGuessed.datasourceid.toString() + url2;
        		try {
        			URL aURL = new URL(imageURL);
        			URLConnection conn = aURL.openConnection();
                    conn.setConnectTimeout(500);
                    conn.connect();
                    InputStream is = conn.getInputStream();            
                    bm = BitmapFactory.decodeStream(new FlushedInputStream(is));
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        		
        		toBeGuessed.bm = bm;
        		
        		contact = toBeGuessed.clone();
        		
        		refcallback.callback(1);
            }
		};
		new Thread(runnable).start();			
	}
	
	private Contact grab_random_contact(int index) {
		ContactDataAdapter cda = new ContactDataAdapter(context);
		cda.open();
		
		if (index < 0) {
			Random ran = new Random();
			userid = ran.nextInt(pHelper.get_friend_count());			
		} else {
			userid = index;
		}		
		
		Contact toBeDLed = cda.get_contact(userid);
		
		cda.close();
		
		return toBeDLed;
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
