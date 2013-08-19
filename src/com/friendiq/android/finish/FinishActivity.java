package com.friendiq.android.finish;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.friendiq.android.GameActivity;
import com.friendiq.android.PrefHelper;
import com.friendiq.android.R;
import com.friendiq.android.helpers.NetworkProgressBar;
import com.friendiq.android.remote.NetworkCallback;
import com.friendiq.android.remote.RemoteInterface;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class FinishActivity extends Activity {
	private int userid;
	boolean picFinished;
	String guessName;
	String actualName;
	String filename;
	
	ImageUploadCallback uploadCallback;
	NetworkProgressBar progBar;
	
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish);
		this.context = this;
		Bundle extras = getIntent().getExtras();
        this.userid = extras.getInt("userid");
        this.guessName = extras.getString("firstname");
        this.actualName = extras.getString("guessname");
        this.picFinished = extras.getBoolean("image");
        this.filename = extras.getString("filename");
        
        uploadCallback = new ImageUploadCallback();
        
		Log.i(getClass().getSimpleName(), "user id " + this.userid);

		PrefHelper pHelper = new PrefHelper(this);
        
		progBar = new NetworkProgressBar(this);
		
		final CheckBox chkAskForHelp = (CheckBox) findViewById(R.id.chkAskForHelp);
        TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
        ImageView imgCorrect = (ImageView) findViewById(R.id.imgCorrect);
        Button cmdNext = (Button) findViewById(R.id.cmdNext);        
        
        if (this.guessName.equals(this.actualName) || this.picFinished) {
        	chkAskForHelp.setVisibility(View.INVISIBLE);
        	if (this.picFinished)
        		pHelper.add_to_coin_count(PrefHelper.IMAGE_SUCCESS_AWARD);
        	else
        		pHelper.add_to_coin_count(PrefHelper.SUCCESS_AWARD);
        	pHelper.add_to_friend_iq(1);
        	
        	imgCorrect.setImageDrawable(this.getResources().getDrawable(R.drawable.ico_correct));
        	txtMessage.setText("Great Job!");
        	txtMessage.setTextColor(this.getResources().getColor(R.color.correct_text));
        	cmdNext.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, GameActivity.class);
					intent.putExtra("userid", "-1");
					context.startActivity(intent);
				}        		
        	});
        } else {
        	chkAskForHelp.setVisibility(View.VISIBLE);

        	imgCorrect.setImageDrawable(this.getResources().getDrawable(R.drawable.ico_wrong));
        	txtMessage.setText("Wrong!");
        	txtMessage.setTextColor(this.getResources().getColor(R.color.wrong_red));
        	
        	cmdNext.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (chkAskForHelp.isChecked()) {
						progBar.show("requesting Facebook connection..");			
						new Thread(new Runnable() {
							@Override
							public void run() {
								File selectedPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
								RemoteInterface ri = new RemoteInterface();
								ri.post_image(selectedPath.getAbsolutePath() + "/" + filename, uploadCallback);
							}							
						}).start();
						
					} else {
						Intent intent = new Intent(context, GameActivity.class);
						intent.putExtra("userid", String.valueOf(userid));
						context.startActivity(intent);
					}
					
				}        		
        	});
        }
    	

	}	

	public class ImageUploadCallback implements NetworkCallback {
		@Override
		public void finished(JSONObject serverResponse) {
			if (serverResponse != null) {
				try {
					String urlEnd = serverResponse.getString("url");
					
					final Bundle params = new Bundle();					
				    params.putString("name", "Can you name this person?");
				    params.putString("caption", "Unscramble the puzzle and find your Friend IQ.");
				    params.putString("description", "Can you name this facebook friend? Help solve the puzzle on iPhone and Android!");
				    params.putString("link", "http://www.friendiq.com");
				    params.putString("picture", "http://www.friendiq.com/static/img/" + urlEnd + ".jpg");
				    
				    runOnUiThread(new Runnable() {
						@Override
						public void run() {
							WebDialog feedDialog = (
							        new WebDialog.FeedDialogBuilder(context,
							            Session.getActiveSession(),
							            params))
							        .setOnCompleteListener(new OnCompleteListener() {
							            @Override
							            public void onComplete(Bundle values, FacebookException error) {
							                if (error == null) {
							                    final String postId = values.getString("post_id");
							                    if (postId != null) {
							                    	Intent intent = new Intent(context, GameActivity.class);
													intent.putExtra("userid", String.valueOf(userid));
													context.startActivity(intent);
							                    } 
							                } 
							            }

							        })
							        .build();
							feedDialog.show();		
						}				    
				    });				
					
				} catch (JSONException e) {					
					e.printStackTrace();
				}
				
				progBar.hide();
			}
		}		
	}
	
	@Override
	public void onBackPressed() { }
}
