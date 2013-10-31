package com.majestyk.buzr.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.Session;
import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.R;
import com.majestyk.buzr.apis.API_Login;
import com.majestyk.buzr.apis.API_Login_Facebook;
import com.majestyk.buzr.apis.API_Login_Twitter;
import com.majestyk.buzr.apis.OnTaskCompleteListener;
import com.majestyk.buzr.fragments.BUZRFragmentManagerActivity;
import com.majestyk.buzr.social.FacebookUtils;
import com.majestyk.buzr.social.FacebookLoginView;
import com.majestyk.buzr.social.TwitterUtils;
import com.majestyk.buzr.social.TwitterWebView;

public class Activity_SignIn extends Activity implements OnClickListener {

	private SharedPreferences mPrefs;

	private API_Login mAuthTask = null;

	private String mUsername;
	private String mPassword;

	private EditText mUsernameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private String registerId;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_signin);

		mUsernameView = (EditText) findViewById(R.id.username);
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.facebook).setOnClickListener(this);
		findViewById(R.id.sign_in_button).setOnClickListener(this);
		findViewById(R.id.twitter).setOnClickListener(this);
		findViewById(R.id.forgot).setOnClickListener(this);
		
		registerId = getIntent().getStringExtra("register");

	}

	@Override
	public void onResume() {
		super.onResume();
		showProgress(false);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.facebook:
			Intent facebookIntent = new Intent(Activity_SignIn.this, FacebookLoginView.class);
			facebookIntent.putExtra(FacebookLoginView.PARAM_REQUEST, FacebookLoginView.ARG_LOGIN);
			startActivityForResult(facebookIntent, FacebookUtils.FACEBOOK_REQUEST_CODE);
			break;
		case R.id.twitter:
			Intent twitterIntent = new Intent(Activity_SignIn.this, TwitterWebView.class);
			twitterIntent.putExtra("CONSUMER_KEY", TwitterUtils.getConsumerKey());
			twitterIntent.putExtra("CONSUMER_SECRET", TwitterUtils.getConsumerSecret());
			twitterIntent.putExtra("CALLBACK", TwitterUtils.getCallbackUrl());
			startActivityForResult(twitterIntent, TwitterUtils.TWITTER_REQUEST_CODE);
			break;
		case R.id.sign_in_button:
			attemptLogin();
			break;
		case R.id.forgot:
			Intent intent = new Intent(Activity_SignIn.this, Action_ForgotPassword.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent data ) {
		super.onActivityResult(requestCode, resultCode, data);

		System.out.println(requestCode + ", " + resultCode + ", " + data);

		if (resultCode == RESULT_OK) {
			switch(requestCode) {
			case FacebookUtils.FACEBOOK_REQUEST_CODE:
				fbLoginTask(Session.getActiveSession());
				break;
			case TwitterUtils.TWITTER_REQUEST_CODE:
				tLoginTask(
						data.getStringExtra(TwitterUtils.PREF_TWITTER_ACCESS),
						data.getStringExtra(TwitterUtils.PREF_TWITTER_SECRET)
				);
				break;
			}
		}
	}
	
	private final void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		mUsernameView.setError(null);
		mPasswordView.setError(null);

		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new API_Login(this, new OnTaskCompleteListener() {
				public void onComplete(boolean success) {
					showProgress(false);
					if(success) {
						mPrefs = getSharedPreferences(GlobalValues.PREFS, MODE_PRIVATE);
						SharedPreferences.Editor editor = mPrefs.edit();
						editor.putString(GlobalValues.PREF_LOGIN, GlobalValues.STATUS_LOG);
						editor.putString(GlobalValues.PREF_USERNAME, mUsername);
						editor.putString(GlobalValues.PREF_PASSWORD, mPassword);
						editor.putString(GlobalValues.PREF_TOKEN, registerId);
						editor.commit();
						startHomeActivity();
					}
				}
			});
			mAuthTask.execute("user/login", mUsername, mPassword, registerId);
			mAuthTask = null;
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private final void showProgress(final boolean show) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});
		} else {
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	private final void fbLoginTask(final Session session) {
		System.out.println(session);

		new API_Login_Facebook(Activity_SignIn.this, new OnTaskCompleteListener() {

			@Override
			public void onComplete(boolean success) {
				if (success) {
					mPrefs = getSharedPreferences(GlobalValues.PREFS, MODE_PRIVATE);
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString(GlobalValues.PREF_LOGIN, GlobalValues.STATUS_LOG);
					editor.putString(FacebookUtils.PREF_FACEBOOK_ACCESS, session.getAccessToken());
					editor.commit();
					startHomeActivity();
				}
			}

		}).execute("user/fbLogin", session.getAccessToken(), registerId);
	}

	private final void tLoginTask(final String token, final String secret) {
		new API_Login_Twitter(Activity_SignIn.this, new OnTaskCompleteListener() {

			@Override
			public void onComplete(boolean success) {
				if (success) {
					mPrefs = getSharedPreferences(GlobalValues.PREFS, MODE_PRIVATE);
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString(GlobalValues.PREF_LOGIN, GlobalValues.STATUS_LOG);
					editor.putString(TwitterUtils.PREF_TWITTER_ACCESS, token);
					editor.putString(TwitterUtils.PREF_TWITTER_SECRET, secret);
					editor.commit();
					startHomeActivity();
				}
			}

		}).execute("user/tLogin", token, secret, registerId);
	}
	
	private final void startHomeActivity() {
		Intent intent = new Intent(Activity_SignIn.this, BUZRFragmentManagerActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

}
