package com.friendiq.android.finish;

import com.friendiq.android.GameActivity;
import com.friendiq.android.PrefHelper;
import com.friendiq.android.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FinishActivity extends Activity {
	private int userid;
	boolean picFinished;
	String guessName;
	String actualName;
	
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish);
		this.context = this;
		Bundle extras = getIntent().getExtras();
        this.userid = Integer.valueOf(extras.getString("userid"));
        this.guessName = extras.getString("firstname");
        this.actualName = extras.getString("guessname");
        this.picFinished = extras.getBoolean("image");
        
		Log.i(getClass().getSimpleName(), "user id " + this.userid);

		PrefHelper pHelper = new PrefHelper(this);
        
        TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
        ImageView imgCorrect = (ImageView) findViewById(R.id.imgCorrect);
        Button cmdNext = (Button) findViewById(R.id.cmdNext);        
        
        if (this.guessName.equals(this.actualName) || this.picFinished) {
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
        	imgCorrect.setImageDrawable(this.getResources().getDrawable(R.drawable.ico_wrong));
        	txtMessage.setText("Wrong!");
        	txtMessage.setTextColor(this.getResources().getColor(R.color.wrong_red));
        	cmdNext.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, GameActivity.class);
					intent.putExtra("userid", String.valueOf(userid));
					context.startActivity(intent);
				}        		
        	});
        }
    	

	}	

}
