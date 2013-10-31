package com.majestyk.buzr.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.majestyk.buzr.EmailIntent;
import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.R;
import com.majestyk.buzr.social.FacebookLoginView;
import com.majestyk.buzr.social.FacebookUtils;
import com.majestyk.buzr.social.TumblrUtils;
import com.majestyk.buzr.social.TumblrWebView;
import com.majestyk.buzr.social.TwitterUtils;
import com.majestyk.buzr.social.TwitterWebView;

public class Action_ShareSettings extends Activity implements OnClickListener {

	private SharedPreferences mPrefs;
	private ImageButton ibFacebook;
	private ImageButton ibTwitter;
	private ImageButton ibTumblr;
	private TextView tvFacebook;
	private TextView tvTwitter;
	private TextView tvTumblr;

	private String twitter_name, tumblr_name;
	private Button ibShareBuzr;
	private AlertDialog shareDialog;
	private String shareMessage; 
	private String sharePicture;

	private Boolean shareFlag = false;

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_share_settings);

		mPrefs = getSharedPreferences(GlobalValues.PREFS, MODE_PRIVATE);

		ibFacebook = (ImageButton)findViewById(R.id.imageButton1);
		ibTwitter = (ImageButton)findViewById(R.id.imageButton2);
		ibTumblr = (ImageButton)findViewById(R.id.imageButton3);
		ibShareBuzr = (Button)findViewById(R.id.imageButton4);

		tvFacebook = (TextView)findViewById(R.id.textView1);
		tvTwitter = (TextView)findViewById(R.id.textView2);
		tvTumblr = (TextView)findViewById(R.id.textView3);

		ibShareBuzr.setOnClickListener(this);
		ibFacebook.setOnClickListener(this); //facebook
		ibTwitter.setOnClickListener(this); //twitter
		ibTumblr.setOnClickListener(this); //tumblr

		shareMessage = "Explore my photos on buzr. Download the free app from the app store! http://www.buzrapp.com/download";
		
		File f = new File(getCacheDir()+"/buzr_logo.png");
		if (!f.exists()) try {
			InputStream is = getAssets().open("ic_launcher.png");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();

			FileOutputStream fos = new FileOutputStream(f);
			fos.write(buffer);
			fos.close();
		} catch (Exception e) { throw new RuntimeException(e); }

		sharePicture = f.getPath();

	}

	@Override
	public final void onClick(View arg0) {
		switch(arg0.getId()) {
		case R.id.imageButton1:
			Intent facebookIntent = new Intent(Action_ShareSettings.this, FacebookLoginView.class);
			facebookIntent.putExtra(FacebookLoginView.PARAM_REQUEST, FacebookLoginView.ARG_LOGIN);
			startActivityForResult(facebookIntent, FacebookUtils.FACEBOOK_REQUEST_CODE);
			break;
		case R.id.imageButton2:
			Intent twitterIntent = new Intent(Action_ShareSettings.this, TwitterWebView.class);
			twitterIntent.putExtra("CONSUMER_KEY", TwitterUtils.getConsumerKey());
			twitterIntent.putExtra("CONSUMER_SECRET", TwitterUtils.getConsumerSecret());
			twitterIntent.putExtra("CALLBACK", TwitterUtils.getCallbackUrl());
			startActivityForResult(twitterIntent, TwitterUtils.TWITTER_REQUEST_CODE);
			break;
		case R.id.imageButton3:
			Intent tumblrIntent = new Intent(Action_ShareSettings.this, TumblrWebView.class);
			tumblrIntent.putExtra("CONSUMER_KEY", TumblrUtils.getConsumerKey());
			tumblrIntent.putExtra("CONSUMER_SECRET", TumblrUtils.getConsumerSecret());
			tumblrIntent.putExtra("CALLBACK", TumblrUtils.getCallbackUrl());
			startActivityForResult(tumblrIntent, TumblrUtils.TUMBLR_REQUEST_CODE);
			break;
		case R.id.imageButton4:
			AlertDialog.Builder builder = new AlertDialog.Builder(Action_ShareSettings.this);
			LayoutInflater factory = LayoutInflater.from(Action_ShareSettings.this);
			final View view = factory.inflate(R.layout.dialog_share, null);
			view.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			((Button)view.findViewById(R.id.share1)).setOnClickListener(this);
			((Button)view.findViewById(R.id.share2)).setOnClickListener(this);
			((Button)view.findViewById(R.id.share3)).setOnClickListener(this);
			((Button)view.findViewById(R.id.share4)).setOnClickListener(this);
			((Button)view.findViewById(R.id.share5)).setOnClickListener(this);
			builder.setView(view);
			shareDialog = builder.create();
			shareDialog.show();
			break;
		case R.id.share1:
			shareFlag = true;
			Intent facebookIntent2 = new Intent(Action_ShareSettings.this, FacebookLoginView.class);
			facebookIntent2.putExtra(FacebookLoginView.PARAM_REQUEST, FacebookLoginView.ARG_WRITE);
			startActivityForResult(facebookIntent2, FacebookUtils.FACEBOOK_REQUEST_CODE);
			break;
		case R.id.share2:
			shareFlag = true;
			if (TwitterUtils.getTwitterConsumer() != null) {
				TwitterUtils.TwitterShareTask(TwitterUtils.getTwitterConsumer(), new File(
						sharePicture), 
						shareMessage);
			} else {
				Intent twitterIntent2 = new Intent(Action_ShareSettings.this, TwitterWebView.class);
				twitterIntent2.putExtra("CONSUMER_KEY", TwitterUtils.getConsumerKey());
				twitterIntent2.putExtra("CONSUMER_SECRET", TwitterUtils.getConsumerSecret());
				twitterIntent2.putExtra("CALLBACK", TwitterUtils.getCallbackUrl());
				startActivityForResult(twitterIntent2, TwitterUtils.TWITTER_REQUEST_CODE);
			}
			break;
		case R.id.share3:
			new EmailIntent(Action_ShareSettings.this,
					"",
					"Explore my photos on buzr!",
					"Download the free app from the app store! http://www.buzrapp.com/download",
					new File(sharePicture));
			break;
		case R.id.share4:
			Intent sendIntent = new Intent(Intent.ACTION_SEND);
	        sendIntent.putExtra("sms_body", "Explore my photos on buzr! "
	        		+ "Download the free app from the app store! http://www.buzrapp.com/download");
//	        Uri uri = Uri.parse("android.resource:// com.majestyk.buzr/drawable/ic_launcher.png");
//	        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
//	        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(sharePicture)));
	        sendIntent.setType("image/png");
	        startActivity(Intent.createChooser(sendIntent, "Send"));
			break;
		case R.id.share5:
			shareDialog.dismiss();
			break;
		}
	}

	@Override
	public final void onActivityResult( int requestCode, int resultCode, Intent data ) {
		super.onActivityResult(requestCode, resultCode, data);

		System.out.println(requestCode + ", " + resultCode + ", " + data);

		if (resultCode == RESULT_OK) {
			switch(requestCode) {
			case FacebookUtils.FACEBOOK_REQUEST_CODE:
				Session session = (Session.getActiveSession());
				if (session.isOpened()) {

					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString(FacebookUtils.PREF_FACEBOOK_ACCESS, session.getAccessToken()); 
					editor.commit();

					Request.executeMeRequestAsync(session, new GraphUserCallback() {
						@Override
						public void onCompleted(GraphUser user, Response response) {
							if (user != null) {
								System.out.println("User=" + user);
								tvFacebook.setText(user.getName());
								ibFacebook.setSelected(true);
							}
							if (response != null) {
								System.out.println("Response=" + response);
							}
						}

					});
					
					if(shareFlag) {
						FacebookUtils.FacebookShareTask(Action_ShareSettings.this, session, new File(
								sharePicture), 
								shareMessage);
						shareFlag = false;						
					}
				}
				break;
			case TwitterUtils.TWITTER_REQUEST_CODE:

				SharedPreferences.Editor twitter_editor = mPrefs.edit();
				twitter_editor.putString(TwitterUtils.PREF_TWITTER_ACCESS, 
						data.getStringExtra(TwitterUtils.PREF_TWITTER_ACCESS));
				twitter_editor.putString(TwitterUtils.PREF_TWITTER_SECRET, 
						data.getStringExtra(TwitterUtils.PREF_TWITTER_SECRET));
				twitter_editor.commit();

				new TwitterTask("https://api.twitter.com/1.1/account/verify_credentials.json").execute(
						data.getStringExtra(TwitterUtils.PREF_TWITTER_ACCESS),
						data.getStringExtra(TwitterUtils.PREF_TWITTER_SECRET));

				break;
			case TumblrUtils.TUMBLR_REQUEST_CODE:

				SharedPreferences.Editor tumblr_editor = mPrefs.edit();
				tumblr_editor.putString(TumblrUtils.PREF_TUMBLR_ACCESS, 
						data.getStringExtra(TumblrUtils.PREF_TUMBLR_ACCESS));
				tumblr_editor.putString(TumblrUtils.PREF_TUMBLR_SECRET, 
						data.getStringExtra(TumblrUtils.PREF_TUMBLR_SECRET));
				tumblr_editor.commit();

				new TumblrTask("https://api.tumblr.com/v2/user/info").execute(
						data.getStringExtra(TumblrUtils.PREF_TUMBLR_ACCESS),
						data.getStringExtra(TumblrUtils.PREF_TUMBLR_SECRET));

				break;
			}
		}
	}

	private final class TwitterTask extends AsyncTask<String, Void, String> {

		OAuthConsumer consumer;
		String url;

		public TwitterTask(String url) {
			this.url = url;

			try {
				this.consumer = new CommonsHttpOAuthConsumer(
						TwitterUtils.getConsumerKey(),
						TwitterUtils.getConsumerSecret());
			} catch (Exception e) { 
				e.printStackTrace();
			}

		}

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			HttpClient httpclient = GlobalValues.getHttpClient();

			String token = params[0];
			String secret = params[1];

			consumer.setTokenWithSecret(token, secret);
			TwitterUtils.setTwitterConsumer(consumer);

			HttpRequestBase request = new HttpGet(url);

			try {
				TwitterUtils.getTwitterConsumer().sign(request);
			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			}

			try {
				HttpResponse response = httpclient.execute(request, GlobalValues.getLocalContext());
				HttpEntity entity = response.getEntity();
				InputStream instream = entity.getContent();
				result = GlobalValues.convertStreamToString(instream);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				System.out.println(result);
				JSONObject jObject = new JSONObject(result.trim());
				System.out.println(jObject);

				if(jObject.has("name")) {
					twitter_name = jObject.getString("name");
					tvTwitter.setText(twitter_name);
					ibTwitter.setSelected(true);
				}
				
				if(shareFlag) {
					TwitterUtils.TwitterShareTask(TwitterUtils.getTwitterConsumer(), new File(
							sharePicture), 
							shareMessage);
					shareFlag = false;
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	} 

	private final class TumblrTask extends AsyncTask<String, Void, String> {

		OAuthConsumer consumer;
		String url;

		public TumblrTask(String url) {
			this.url = url;

			try {
				this.consumer = new CommonsHttpOAuthConsumer(
						TumblrUtils.getConsumerKey(),
						TumblrUtils.getConsumerSecret());
			} catch (Exception e) { 
				e.printStackTrace();
			}

		}

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			HttpClient httpclient = GlobalValues.getHttpClient();

			String token = params[0];
			String secret = params[1];

			consumer.setTokenWithSecret(token, secret);
			TumblrUtils.setTumblrConsumer(consumer);

			HttpRequestBase request = new HttpGet(url);

			try {
				TumblrUtils.getTumblrConsumer().sign(request);
			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			}

			try {
				HttpResponse response = httpclient.execute(request, GlobalValues.getLocalContext());
				HttpEntity entity = response.getEntity();
				InputStream instream = entity.getContent();
				result = GlobalValues.convertStreamToString(instream);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				JSONObject jObject = new JSONObject(result.trim());
				System.out.println(jObject);

				if(jObject.has("response")) {
					JSONObject jResponse = jObject.getJSONObject("response");
					System.out.println(jResponse);

					if(jResponse.has("user")) {
						JSONObject jUser = jResponse.getJSONObject("user");
						System.out.println(jUser);

						tumblr_name = jUser.getString("name");
						tvTumblr.setText(tumblr_name);
						ibTumblr.setSelected(true);
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}