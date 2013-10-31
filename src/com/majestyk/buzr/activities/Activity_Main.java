package com.majestyk.buzr.activities;

import static com.majestyk.buzr.notifications.CommonUtilities.SENDER_ID;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.google.android.gcm.GCMRegistrar;
import com.majestyk.buzr.FontManager;
import com.majestyk.buzr.GPSTracker;
import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.R;
import com.majestyk.buzr.apis.API_Login;
import com.majestyk.buzr.apis.API_Login_Facebook;
import com.majestyk.buzr.apis.API_Login_Twitter;
import com.majestyk.buzr.apis.OnTaskCompleteListener;
import com.majestyk.buzr.fragments.BUZRFragmentManagerActivity;
import com.majestyk.buzr.social.FacebookUtils;
import com.majestyk.buzr.social.TwitterUtils;

public class Activity_Main extends Activity implements OnClickListener {

	private DefaultHttpClient httpclient;
	private HttpContext localContext;

	private String registerId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			registerId = regId;
			System.out.println(registerId);
			Log.v("BunkED", "Already registered");
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		FontManager.loadFont(getAssets());
		GlobalValues.gps = new GPSTracker(this);
		
		httpclient = new DefaultHttpClient();
		localContext = new BasicHttpContext();
		GlobalValues.setHttpClient(httpclient);
		GlobalValues.setLocalContext(localContext);
		CookieStore cookieStore = new BasicCookieStore();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        SharedPreferences settings = getSharedPreferences(GlobalValues.PREFS, 0);

        if (settings.getString(GlobalValues.PREF_LOGIN, "").equals(GlobalValues.STATUS_LOG)) {
        	
        	if (!settings.getString(FacebookUtils.PREF_FACEBOOK_ACCESS, "").equals("")) {
        		
        		new API_Login_Facebook(Activity_Main.this, new OnTaskCompleteListener() {

        			@Override
        			public void onComplete(boolean success) {
        				if (success) {
        					startHomeActivity();
        				}
        			}

        		}).execute("user/fbLogin",
        				settings.getString(FacebookUtils.PREF_FACEBOOK_ACCESS, ""), registerId);

        	} else if (!settings.getString(TwitterUtils.PREF_TWITTER_ACCESS, "").equals("")) {
        		
        		new API_Login_Twitter(Activity_Main.this, new OnTaskCompleteListener() {

        			@Override
        			public void onComplete(boolean success) {
        				if (success) {
        					startHomeActivity();
        				}
        			}

        		}).execute("user/tLogin",
        				settings.getString(TwitterUtils.PREF_TWITTER_ACCESS, ""),
        				settings.getString(TwitterUtils.PREF_TWITTER_SECRET, ""), registerId);

        	} else {

	        	new API_Login(this, new OnTaskCompleteListener() {
	        		@Override
	        		public void onComplete(boolean success) {
	        			if(success) {
	        				startHomeActivity();
	        			}
					}
				}).execute("user/login",
						settings.getString(GlobalValues.PREF_USERNAME, ""), 
						settings.getString(GlobalValues.PREF_PASSWORD, ""),
						settings.getString(GlobalValues.PREF_TOKEN, "")
				);
	        	
        	}

    	} 

		ImageButton signIn = (ImageButton)findViewById(R.id.imageButton1);
		ImageButton signUp = (ImageButton)findViewById(R.id.imageButton2);
		
		signIn.setOnClickListener(this);
		signUp.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch(v.getId()) {
		case R.id.imageButton1:
			intent = new Intent(Activity_Main.this, Activity_SignIn.class);
			intent.putExtra("register", registerId);
			startActivity(intent);
			break;
		case R.id.imageButton2:
			intent = new Intent(Activity_Main.this, Activity_SignUp.class);
			intent.putExtra("register", registerId);
			startActivity(intent);
			break;
		}
	}

	private final void startHomeActivity() {
		Intent intent = new Intent(Activity_Main.this, BUZRFragmentManagerActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

}
