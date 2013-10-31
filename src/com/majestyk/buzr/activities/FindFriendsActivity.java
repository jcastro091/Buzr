package com.majestyk.buzr.activities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.R;
import com.majestyk.buzr.adapter.FriendAdapter;
import com.majestyk.buzr.adapter.OnFriendClickListener;
import com.majestyk.buzr.apis.API_Follow;
import com.majestyk.buzr.apis.OnTaskCompleteListener;
import com.majestyk.buzr.apis.OnTaskCompleteResultListener;
import com.majestyk.buzr.objects.Friend;
import com.majestyk.buzr.objects.FriendHeader;
import com.majestyk.buzr.objects.FriendItem;
import com.majestyk.buzr.social.FacebookLoginView;
import com.majestyk.buzr.social.FacebookUtils;
import com.majestyk.buzr.social.TwitterUtils;
import com.majestyk.buzr.social.TwitterWebView;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

public class FindFriendsActivity extends Activity implements OnFriendClickListener {

	private LinkedList<Friend> areFollowingList;
	private LinkedList<Friend> notFollowingList;
	private LinkedList<Friend> areRequestedList;
	private LinkedList<Friend> unregisteredList;

	private FriendAdapter friendAdapter;
	private LinkedList<Friend> friendList;
	private ListView friendListView;

	SharedPreferences settings;
	WebDialog dialog = null;
	Bundle dialogParams = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends_facebook_twitter);
		settings = getSharedPreferences(GlobalValues.PREFS, 0);

		areFollowingList = new LinkedList<Friend>();
		notFollowingList = new LinkedList<Friend>();
		areRequestedList = new LinkedList<Friend>();
		unregisteredList = new LinkedList<Friend>();

		friendList = new LinkedList<Friend>();
		friendAdapter = new FriendAdapter(this, friendList, this);

		friendListView = ((ListView)findViewById(R.id.friends));
		friendListView.setAdapter(friendAdapter);

		getActivityIntent();

	}

	private void getActivityIntent() {

		if(getIntent().getStringExtra("account").equals("facebook")) {

			if (!settings.getString(FacebookUtils.PREF_FACEBOOK_ACCESS, "").equals("")) {
				getFacebookFriends();
			} else {
				Intent facebookIntent = new Intent(FindFriendsActivity.this, FacebookLoginView.class);
				facebookIntent.putExtra(FacebookLoginView.PARAM_REQUEST, FacebookLoginView.ARG_LOGIN);
				startActivityForResult(facebookIntent, FacebookUtils.FACEBOOK_REQUEST_CODE);
			}

		} else if (getIntent().getStringExtra("account").equals("twitter")) {

			if (!settings.getString(TwitterUtils.PREF_TWITTER_ACCESS, "").equals("")) {
				getTwitterFriends();
			} else {
				Intent twitterIntent = new Intent(FindFriendsActivity.this, TwitterWebView.class);
				twitterIntent.putExtra("CONSUMER_KEY", TwitterUtils.getConsumerKey());
				twitterIntent.putExtra("CONSUMER_SECRET", TwitterUtils.getConsumerSecret());
				twitterIntent.putExtra("CALLBACK", TwitterUtils.getCallbackUrl());
				startActivityForResult(twitterIntent, TwitterUtils.TWITTER_REQUEST_CODE);
			}

		}

	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		super.onActivityResult(requestCode, resultCode, data);

		System.out.println(requestCode + ", " + resultCode + ", " + data);

		if (resultCode == RESULT_OK) {
			switch(requestCode) {
			case FacebookUtils.FACEBOOK_REQUEST_CODE:
				Session session = (Session.getActiveSession());
				if (session.isOpened()) {
					final String access_token = session.getAccessToken();
					Editor editor = settings.edit();
					editor.putString(FacebookUtils.PREF_FACEBOOK_ACCESS, access_token);
					editor.commit();
					getFacebookFriends(access_token);
				}
				break;
			case TwitterUtils.TWITTER_REQUEST_CODE:
				final String access_token = data.getStringExtra(TwitterUtils.PREF_TWITTER_ACCESS);
				final String secret_token = data.getStringExtra(TwitterUtils.PREF_TWITTER_ACCESS);
				Editor editor = settings.edit();
				editor.putString(TwitterUtils.PREF_TWITTER_ACCESS, access_token);
				editor.putString(TwitterUtils.PREF_TWITTER_SECRET, secret_token);
				editor.commit();
				getTwitterFriends(access_token, secret_token);
			}
		}
	}

	@Override
	public void onAre_FollowClick(final int pos, final ImageButton button) {
		new API_Follow(FindFriendsActivity.this, new OnTaskCompleteListener() {
			public void onComplete(boolean success) {
				if(success) {
					button.setBackgroundResource(R.drawable.button_follow_friends);
					((FriendItem)friendList.get(pos)).setFollowing(false);
					getActivityIntent();
				}
			}
		}).execute("user/unfollow", ((FriendItem)friendList.get(pos)).getUserID());			
	}

	@Override
	public void onNot_FollowClick(final int pos, final ImageButton button) {
		new API_Follow(FindFriendsActivity.this, new OnTaskCompleteListener() {
			public void onComplete(boolean success) {
				if(success) {
					button.setBackgroundResource(R.drawable.button_following_friends);
					((FriendItem)friendList.get(pos)).setFollowing(true);
					getActivityIntent();
				}
			}
		}).execute("user/follow", ((FriendItem)friendList.get(pos)).getUserID());			
	}

	@Override
	public void onInviteUserClick(String id) {
		if(getIntent().getStringExtra("account").equals("facebook")) {
			String message = "Download buzr (buzrapp.com) and like photos with emojis!";

			SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
			.setAppId(getResources().getString(R.string.app_id))
			.setNamespace("Buzr")
			.build();

			SimpleFacebook simpleFacebook = SimpleFacebook.getInstance(getApplicationContext());
			simpleFacebook.setConfiguration(configuration);
			simpleFacebook.invite(FindFriendsActivity.this, id, message, 
					new SimpleFacebook.OnInviteListener() {

				@Override
				public void onFail() {
					Log.w("Simple Facebook", "Failed to invite");
				}

				@Override
				public void onException(Throwable throwable) {
					Log.e("Simple Facebook", "Bad thing happened", throwable);
				}

				@Override
				public void onComplete() {
					Log.i("Simple Facebook", "Invitation was sent");
				}

				@Override
				public void onCancel() {
					Log.i("Simple Facebook", "Invitation was cancelled");
				}

			});
			// inviteFriend(id, message);
		}

	}

	private void getFacebookFriends() {
		getFacebookFriends("");
	}

	private void getFacebookFriends(String token) {
		new API_FacebookFriends(this, token, new OnTaskCompleteResultListener() {

			@Override
			public void onComplete(boolean success, String result) {

				if(success) {
					getList(result);
				}

			}

		}).execute("user/fbFriends");
	}

	public class API_FacebookFriends extends AsyncTask<String, Void, String> {

		Context c;
		String token;
		OnTaskCompleteResultListener complete;

		public API_FacebookFriends (Context c, String token, OnTaskCompleteResultListener complete) {
			this.c = c;
			this.token = token;
			this.complete = complete;
		}

		protected String doInBackground(String... params) {
			String result = "";
			HttpClient httpclient = GlobalValues.getHttpClient();
			HttpPost httppost = new HttpPost(String.format(GlobalValues.getServiceUrl() + "%s", params[0]));

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			if(!token.equals("")) {
				System.out.println(token);
				nameValuePairs.add(new BasicNameValuePair("fb_token", token));
			}

			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpclient.execute(httppost, GlobalValues.getLocalContext());
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream instream = entity.getContent();
					result = GlobalValues.convertStreamToString(instream);
					Log.i(params[0], params[0] + ": " + result);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		public void onPostExecute(String result) {
			try {
				JSONObject jObject = new JSONObject(result.trim());

				if(jObject.has("error")) {
					Toast.makeText(c, jObject.getString("error"), Toast.LENGTH_SHORT).show();
					complete.onComplete(false, result);
				} else {
					complete.onComplete(true, result);
				}

			} catch (JSONException e) {
				complete.onComplete(false, result);
				e.printStackTrace();
			}
		}

	}

	private void getTwitterFriends() {
		getTwitterFriends("", "");
	}

	private void getTwitterFriends(String token, String token_secret) {
		new API_TwitterFriends(this, token, token_secret, 
				new OnTaskCompleteResultListener() {

			@Override
			public void onComplete(boolean success, String result) {

				if(success) {
					getList(result);
				}

			}

		}).execute("user/tFriends");
	}

	public class API_TwitterFriends extends AsyncTask<String, Void, String> {

		Context c;
		String token;
		String token_secret;
		OnTaskCompleteResultListener complete;

		public API_TwitterFriends (Context c, String token, String token_secret,
				OnTaskCompleteResultListener complete) {
			this.c = c;
			this.token = token;
			this.token_secret = token_secret;
			this.complete = complete;
		}

		protected String doInBackground(String... params) {
			String result = "";
			HttpClient httpclient = GlobalValues.getHttpClient();
			HttpPost httppost = new HttpPost(String.format(GlobalValues.getServiceUrl() + "%s", params[0]));

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			if(!token.equals("")) {
				System.out.println(token + token_secret);
				nameValuePairs.add(new BasicNameValuePair("twitter_token", token));
				nameValuePairs.add(new BasicNameValuePair("twitter_token_secret", token_secret));
			}

			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpclient.execute(httppost, GlobalValues.getLocalContext());
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream instream = entity.getContent();
					result = GlobalValues.convertStreamToString(instream);
					Log.i(params[0], params[0] + ": " + result);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		public void onPostExecute(String result) {
			try {
				JSONObject jObject = new JSONObject(result.trim());

				if(jObject.has("error")) {
					Toast.makeText(c, jObject.getString("error"), Toast.LENGTH_SHORT).show();
					complete.onComplete(false, result);
				} else {
					complete.onComplete(true, result);
				}

			} catch (JSONException e) {
				complete.onComplete(false, result);
				e.printStackTrace();
			}
		}
	}

	private void getList(String result) {
		areFollowingList.clear();
		notFollowingList.clear();
		areRequestedList.clear();
		unregisteredList.clear();
		friendList.clear();

		try {
			JSONObject j = new JSONObject(result.trim());
			JSONArray are_following = new JSONArray(
					j.getString("are_following"));

			for (int i = 0; i < are_following.length(); i++) {
				JSONObject jObject = are_following.getJSONObject(i); 

				System.out.println(jObject);

				areFollowingList.add(new FriendItem(
						jObject.getString("fb_id"),
						jObject.getString("name"),
						jObject.getString("image"),
						jObject.getString("user_id"),
						jObject.getString("username"),
						jObject.getString("privacy"),
						true, true));

			}

			friendList.add(new FriendHeader("following"));
			friendList.addAll(areFollowingList);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			JSONObject j = new JSONObject(result.trim());
			JSONArray are_following = new JSONArray(
					j.getString("unregistered"));

			for (int i = 0; i < are_following.length(); i++) {
				JSONObject jObject = are_following.getJSONObject(i); 

				System.out.println(jObject);

				unregisteredList.add(new FriendItem(
						jObject.getString("fb_id"),
						jObject.getString("name"),
						jObject.getString("image"),
						"",
						"",
						"",
						false, false));

			}

			friendList.add(new FriendHeader("invite"));
			friendList.addAll(unregisteredList);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			JSONObject j = new JSONObject(result.trim());
			JSONArray are_following = new JSONArray(
					j.getString("requested"));

			for (int i = 0; i < are_following.length(); i++) {
				JSONObject jObject = are_following.getJSONObject(i); 

				System.out.println(jObject);

				areRequestedList.add(new FriendItem(
						jObject.getString("fb_id"),
						jObject.getString("name"),
						jObject.getString("image"),
						jObject.getString("user_id"),
						jObject.getString("username"),
						jObject.getString("privacy"),
						true, false));

			}

			friendList.add(new FriendHeader("requested"));
			friendList.addAll(areRequestedList);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			JSONObject j = new JSONObject(result.trim());
			JSONArray are_following = new JSONArray(
					j.getString("not_following"));

			for (int i = 0; i < are_following.length(); i++) {
				JSONObject jObject = are_following.getJSONObject(i); 

				System.out.println(jObject);

				notFollowingList.add(new FriendItem(
						jObject.getString("fb_id"),
						jObject.getString("name"),
						jObject.getString("image"),
						jObject.getString("user_id"),
						jObject.getString("username"),
						jObject.getString("privacy"),
						false, true));

			}

			friendList.add(new FriendHeader("not following"));
			friendList.addAll(notFollowingList);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		friendAdapter.notifyDataSetChanged();

	}

	public void inviteFriend(String id, String message) {
		Bundle params = new Bundle();
		params.putString("to", id);
		params.putString("message", message);

		Session session = (Session.getActiveSession());
		if (session.isOpened()) {
			dialog = new WebDialog.Builder(this, session, "apprequests", params).
					setOnCompleteListener(new WebDialog.OnCompleteListener() {
						@Override
						public void onComplete(Bundle values, FacebookException error) {
							if (error != null && !(error instanceof FacebookOperationCanceledException)) {
								Toast.makeText(FindFriendsActivity.this, "network error", Toast.LENGTH_SHORT).show();
								Log.e("WebDialog", "network error");
							}
							dialog = null;
							dialogParams = null;
						}
					}).build();

			Window dialog_window = dialog.getWindow();
			dialog_window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);

			dialogParams = params;

			dialog.show();
		}
	}
}
