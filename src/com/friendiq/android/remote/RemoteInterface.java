package com.friendiq.android.remote;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.friendiq.android.finish.FinishActivity.ImageUploadCallback;

import android.util.Log;

public class RemoteInterface {

	public void post_image(String filename, ImageUploadCallback callback) {
		JSONObject obj = new JSONObject();
		callback.finished(make_restful_post_with_image(obj, filename, "http://www.friendiq.com/photo/upload"));
	}
	
	private HttpClient getGenericHttpClient() {
		int timeout = 10000;
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
		HttpConnectionParams.setSoTimeout(httpParams, timeout);
		return new DefaultHttpClient(httpParams);
	}

	private JSONObject processEntityForJSON (HttpEntity entity, int statusCode, String url, long startTime) {
		JSONObject jsonreturn;
		try {
			if (entity != null) {
		    	InputStream instream = entity.getContent();
		    	
		    	BufferedReader rd = new BufferedReader(new InputStreamReader(instream));
		    	
		    	// convert the entity sent from the server to a JSON object
		    	String line = rd.readLine();
		    	
	    		Log.i(getClass().getSimpleName(), "Called url: " + url);
	    		Log.i(getClass().getSimpleName(), "Took " + (Calendar.getInstance().getTimeInMillis() - startTime) + " ms");
	    		Log.i(getClass().getSimpleName(), "Returned: " + line);
	    		Log.i(getClass().getSimpleName(), "Status code: " + statusCode);
		    	
		    	
		    	if (line != null) {
		    		jsonreturn = new JSONObject(line);		
		    		jsonreturn.put("httpcode", statusCode);
		    		return jsonreturn;
			    } 
		    }
		} catch (JSONException ex) {
	   		Log.i(getClass().getSimpleName(), "JSON exception: " + ex.getMessage());
	   	} catch (IOException ex) { 
    		Log.i(getClass().getSimpleName(), "IO exception: " + ex.getMessage());
		}
		jsonreturn = new JSONObject();
		try {
			jsonreturn.put("httpcode", statusCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonreturn;
	}
	
	public JSONObject make_restful_get(String url) {
		try {    	
			long startTime = Calendar.getInstance().getTimeInMillis();
			//Log.i(getClass().getSimpleName(), url);
		    HttpGet request = new HttpGet(url);
		    request.setHeader("Content-type", "application/json");
		    HttpResponse response = getGenericHttpClient().execute(request);
		    
		    return processEntityForJSON(response.getEntity(), response.getStatusLine().getStatusCode(), url, startTime);
		} catch (ClientProtocolException ex) {
	    		Log.i(getClass().getSimpleName(), "Client protocol exception: " + ex.getMessage());
		} catch (IOException ex) { 
    		Log.i(getClass().getSimpleName(), "IO exception: " + ex.getMessage());
		}
		return null;
	}

	public JSONObject make_restful_post_with_image(JSONObject obj, String imageLoc, String url) {
		try {
			long startTime = Calendar.getInstance().getTimeInMillis();
			
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			entity.addPart("image", new FileBody(new File(imageLoc)));
			entity.addPart("json", new StringBody(obj.toString()));			
			HttpPost request = new HttpPost(url);
			request.setEntity(entity);
			HttpResponse response = getGenericHttpClient().execute(request);
			
			return processEntityForJSON(response.getEntity(), response.getStatusLine().getStatusCode(), url, startTime);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	public JSONObject make_restful_post(JSONObject body, String url) {
		try {    	
			long startTime = Calendar.getInstance().getTimeInMillis();
			//Log.i(getClass().getSimpleName(), url);
		    HttpPost request = new HttpPost(url);
		    request.setEntity(new ByteArrayEntity(body.toString().getBytes("UTF8")));
		    //request.setHeader("Content-type", "application/json");
		    HttpResponse response = getGenericHttpClient().execute(request);

		    return processEntityForJSON(response.getEntity(), response.getStatusLine().getStatusCode(), url, startTime);
		} catch (ClientProtocolException ex) {
	    		Log.i(getClass().getSimpleName(), "Client protocol exception: " + ex.getMessage());
		} catch (IOException ex) { 
    		Log.i(getClass().getSimpleName(), "IO exception: " + ex.getMessage());
		}
		return null;
	}
}
