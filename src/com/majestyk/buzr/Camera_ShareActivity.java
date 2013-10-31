package com.majestyk.buzr;

import java.io.BufferedInputStream;
//import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.majestyk.buzr.activities.SearchLocationsActivity;
import com.majestyk.buzr.activities.SearchUsersActivity;
import com.majestyk.buzr.fragments.BUZRFragmentManagerActivity;
import com.majestyk.buzr.social.FacebookLoginView;
import com.majestyk.buzr.social.FacebookUtils;
import com.majestyk.buzr.social.TumblrUtils;
import com.majestyk.buzr.social.TumblrWebView;
import com.majestyk.buzr.social.TwitterUtils;
import com.majestyk.buzr.social.TwitterWebView;

public class Camera_ShareActivity extends Activity implements OnClickListener, TextWatcher {

	private final String TAG = this.getClass().getSimpleName();

	private static Uri imageUri;
	private static File imageFile; 
	private static Bitmap imageBmp;
	private static String encodedImageBmp;
	private static EditText ETdescription;
	private static ImageView IVthumbnail;
	private static TextView TVlocation;

	private static ImageButton shareFacebook;
	private static ImageButton shareTwitter;
	private static ImageButton shareTumblr;
	private static ImageButton shareEmail;

	private OAuthConsumer twitterConsumer;
	private OAuthConsumer tumblrConsumer;

	private Boolean flagFacebook = false, flagTwitter = false, flagTumblr = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_share);

		imageUri = Uri.EMPTY;

		ETdescription = (EditText) findViewById(R.id.description);
		IVthumbnail = (ImageView)findViewById(R.id.imageView);
		TVlocation = (TextView) findViewById(R.id.location);
		findViewById(R.id.share).setOnClickListener(this);
		ETdescription.addTextChangedListener(this);
		TVlocation.setOnClickListener(this);

		shareFacebook = (ImageButton)findViewById(R.id.share_facebook);
		shareTwitter = (ImageButton)findViewById(R.id.share_twitter);
		shareTumblr = (ImageButton)findViewById(R.id.share_tumblr);
		shareEmail = (ImageButton)findViewById(R.id.share_email);

		shareFacebook.setOnClickListener(this);
		shareTwitter.setOnClickListener(this);
		shareTumblr.setOnClickListener(this);
		shareEmail.setOnClickListener(this);

		Intent intent = new Intent(this, CameraActivity.class);
		startActivityForResult(intent, GlobalValues.ACTION_REQUEST_FEATHER);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.share:
			submit();
			break;
		case R.id.location:
			Intent intent = new Intent(getApplicationContext(), SearchLocationsActivity.class);
			startActivityForResult(intent, GlobalValues.REQUEST_CODE_SEARCH_LOCATION);
			break;
		case R.id.share_facebook:
			if(!shareFacebook.isSelected()) {
				FacebookUtils.setFacebookSession(Session.getActiveSession());

				if (FacebookUtils.getFacebookSession() != null) {
					shareFacebook.setSelected(true);
					flagFacebook = true;
				} else {
					Intent facebookIntent = new Intent(Camera_ShareActivity.this, FacebookLoginView.class);
					facebookIntent.putExtra(FacebookLoginView.PARAM_REQUEST, FacebookLoginView.ARG_LOGIN);
					startActivityForResult(facebookIntent, FacebookUtils.FACEBOOK_REQUEST_CODE);
				}
			} else {
				shareFacebook.setSelected(false);
				flagFacebook = false;
			}
			break;
		case R.id.share_twitter:
			if(!shareTwitter.isSelected()) {
				if (TwitterUtils.getTwitterConsumer() != null) {
					shareTwitter.setSelected(true);
					flagTwitter = true;
				} else {
					Intent twitterIntent = new Intent(Camera_ShareActivity.this, TwitterWebView.class);
					twitterIntent.putExtra("CONSUMER_KEY", TwitterUtils.getConsumerKey());
					twitterIntent.putExtra("CONSUMER_SECRET", TwitterUtils.getConsumerSecret());
					twitterIntent.putExtra("CALLBACK", TwitterUtils.getCallbackUrl());
					startActivityForResult(twitterIntent, TwitterUtils.TWITTER_REQUEST_CODE);
				}
			} else {
				shareTwitter.setSelected(false);
				flagTwitter = false;
			}

			//			shareTwitter.setSelected(true);
			break;
		case R.id.share_tumblr:
			Intent tumblrIntent = new Intent(Camera_ShareActivity.this, TumblrWebView.class);
			tumblrIntent.putExtra("CONSUMER_KEY", TumblrUtils.getConsumerKey());
			tumblrIntent.putExtra("CONSUMER_SECRET", TumblrUtils.getConsumerSecret());
			tumblrIntent.putExtra("CALLBACK", TumblrUtils.getCallbackUrl());
			startActivityForResult(tumblrIntent, TumblrUtils.TUMBLR_REQUEST_CODE);

			//			shareTumblr.setSelected(true);
			break;
		case R.id.share_email:
			new EmailIntent(Camera_ShareActivity.this, "", "Check out my photo on buzr!", "", imageFile);
			shareEmail.setSelected(true);
			break;
		}
	}

	@Override
	public void afterTextChanged(Editable s) { }

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(s.length() > 0) {
			String testString = s.toString().substring(start, count + start);
			if(testString.equals("@") || testString.endsWith("@")) {
				Intent intent = new Intent (this, SearchUsersActivity.class);
				startActivityForResult(intent, GlobalValues.REQUEST_CODE_SEARCH_USER);
			}
		}
	}

	public void submit() {
		String description = ETdescription.getText().toString();
		String location = TVlocation.getText().toString();

		try {
			Intent intent = new Intent(this, BUZRFragmentManagerActivity.class);
			intent.putExtra("tab", "home");
			intent.putExtra("imageURL", imageUri.toString());
			intent.putExtra("imageBMP", encodedImageBmp);
			intent.putExtra("description", description);
			intent.putExtra("location", location);
			intent.putExtra("fACEbOOK", flagFacebook);
			intent.putExtra("tWITTER", flagTwitter);
			intent.putExtra("tUMBLR", flagTumblr);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			GlobalValues.MODE_SUBMIT = true;

			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		imageBmp = null;
		if(resultCode != RESULT_CANCELED) {
			switch(requestCode) {
			case (GlobalValues.ACTION_REQUEST_FEATHER):
				if (data.hasExtra("URI")) {
					imageUri = (Uri)data.getParcelableExtra("URI");
					imageFile = new File(GlobalValues.getRealPathFromURI(this, imageUri));
					if (imageFile.length() > 1) {

						Log.i(TAG, "Creating imageView: " + imageUri);

						try {
							imageBmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
							Log.i(TAG, "Bitmap-"+imageBmp.getByteCount());
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (OutOfMemoryError e) {
							e.printStackTrace();
							System.gc();
						}

						IVthumbnail.setImageBitmap(imageBmp);
						IVthumbnail.setPadding(10, 10, 10, 10);
						encodedImageBmp = GlobalValues.BitMapToString(imageBmp);
					}

					if(!imageFile.exists())
						Toast.makeText(Camera_ShareActivity.this, "Invalid URL", Toast.LENGTH_SHORT).show();
				}
			break;
			case (GlobalValues.REQUEST_CODE_SEARCH_LOCATION):
				TVlocation.setText((String)data.getStringExtra("GPS"));
			break;
			case (GlobalValues.REQUEST_CODE_SEARCH_USER):
				ETdescription.append((String)data.getStringExtra("NAME") + " ");
			break;
			case (FacebookUtils.FACEBOOK_REQUEST_CODE):
				FacebookUtils.setFacebookSession(Session.getActiveSession());

			if(!shareFacebook.isSelected()) {
				shareFacebook.setSelected(true);
				flagFacebook = true;
			} else {
				shareFacebook.setSelected(false);
				flagFacebook = false;
			}

			break;
			case (TwitterUtils.TWITTER_REQUEST_CODE):

				twitterConsumer = new CommonsHttpOAuthConsumer(
						TwitterUtils.getConsumerKey(),
						TwitterUtils.getConsumerSecret());

			twitterConsumer.setTokenWithSecret(
					data.getStringExtra(TwitterUtils.PREF_TWITTER_ACCESS),
					data.getStringExtra(TwitterUtils.PREF_TWITTER_SECRET));

			TwitterUtils.setTwitterConsumer(twitterConsumer);

			if(!shareTwitter.isSelected()) {
				shareTwitter.setSelected(true);
				flagTwitter = true;
			} else {
				shareTwitter.setSelected(false);
				flagTwitter = false;
			}

			break;
			case (TumblrUtils.TUMBLR_REQUEST_CODE):

				tumblrConsumer = new CommonsHttpOAuthConsumer(
						TumblrUtils.getConsumerKey(),
						TumblrUtils.getConsumerSecret());

			tumblrConsumer.setTokenWithSecret(
					data.getStringExtra(TumblrUtils.PREF_TUMBLR_ACCESS),
					data.getStringExtra(TumblrUtils.PREF_TUMBLR_SECRET));

			TumblrUtils.setTumblrConsumer(tumblrConsumer);

			new TumblrTask().execute();

			}
		} else {
			switch(requestCode) {
			case (GlobalValues.ACTION_REQUEST_FEATHER):
				finish();
			break;
			case (GlobalValues.REQUEST_CODE_SEARCH_LOCATION):
				break;
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putString("imageURL", imageUri.toString());
		savedInstanceState.putString("imageBMP", encodedImageBmp);
		savedInstanceState.putString("description",	ETdescription.getText().toString());
		savedInstanceState.putString("location",	TVlocation.getText().toString());
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		imageUri = Uri.parse(savedInstanceState.getString("imageURL"));
		encodedImageBmp = savedInstanceState.getString("imageBMP");
		ETdescription.setText(savedInstanceState.getString("description"));
		TVlocation.setText(savedInstanceState.getString("location"));
	}

	private class TumblrTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			HttpClient httpclient = GlobalValues.getHttpClient();
			HttpRequestBase request = new HttpGet("https://api.tumblr.com/v2/user/info");

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
				Log.i("user/info", "user/info: " + result);
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

				if(jObject.has("response")) {
					JSONObject jResponse = jObject.getJSONObject("response");

					if(jResponse.has("user")) {
						JSONObject jUser = jResponse.getJSONObject("user");

						new TumblrShareTask().execute(jUser.getString("name"));

					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private class TumblrShareTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			HttpClient httpclient = GlobalValues.getHttpClient();
			HttpPost request = new HttpPost("http://api.tumblr.com/v2/blog/" + params[0] + ".tumblr.com/post");

			Bitmap bm = null;
			String encodedImage = null;
			byte[] myTumblrByteArray = null;
			// ByteArrayInputStream bais = null;
			try {
				FileInputStream fis = new FileInputStream(new File(GlobalValues.getRealPathFromURI(Camera_ShareActivity.this, imageUri)));
				BufferedInputStream bis = new BufferedInputStream(fis, 8192);
				bm = BitmapFactory.decodeStream(bis);
				bis.close();
				fis.close();

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
				myTumblrByteArray = baos.toByteArray();
				encodedImage = Base64.encodeToString(myTumblrByteArray, Base64.URL_SAFE);
				// bais = new ByteArrayInputStream(myTumblrByteArray);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				ArrayList<NameValuePair> nameValuesPairs = new ArrayList<NameValuePair>();
				nameValuesPairs.add(new BasicNameValuePair("type",		"photo"));
				nameValuesPairs.add(new BasicNameValuePair("caption",	ETdescription.getText().toString()));
				nameValuesPairs.add(new BasicNameValuePair("data",		encodedImage));
				request.setEntity(new UrlEncodedFormEntity(nameValuesPairs));
				TumblrUtils.getTumblrConsumer().sign(request);

				HttpResponse response = httpclient.execute(request, GlobalValues.getLocalContext());
				HttpEntity httpentity = response.getEntity();
				InputStream instream = httpentity.getContent();
				result = GlobalValues.convertStreamToString(instream);
				Log.i("blog/" + params[0] + ".tumblr.com/post", params[0] + ": " + result);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			}

			return result;
		}
	}
}

// http://stackoverflow.com/questions/16648475/how-to-post-an-image-to-twitter-using-statuses-update-with-media-in-android