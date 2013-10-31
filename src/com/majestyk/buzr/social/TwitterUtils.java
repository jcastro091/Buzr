package com.majestyk.buzr.social;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.majestyk.buzr.GlobalValues;

public class TwitterUtils {

	// Consumer Key and Secret
	private static final String TWITTER_CONSUMER_KEY = "FEmepjkrAfFLE8eQ2mYw";
	private static final String TWITTER_CONSUMER_SECRET = "jipBbz1SPg4TLMW3JlA1vq8nInOaB6jtNHYx92TehQ";

	// Callback URL
	private static final String TWITTER_CALLBACK_URL = "http://oauthbuzr.com";

	// Request Code
	public static final int TWITTER_REQUEST_CODE = 8948837;

	// Preference Constants
	public static final String PREF_TWITTER_ACCESS = "twitter_access_token";
	public static final String PREF_TWITTER_SECRET = "twitter_secret_token";
	
	private static OAuthConsumer twitterConsumer;

	public static String getConsumerKey() {
		return TWITTER_CONSUMER_KEY;
	}

	public static String getConsumerSecret() {
		return TWITTER_CONSUMER_SECRET;
	}

	public static String getCallbackUrl() {
		return TWITTER_CALLBACK_URL;
	}

	public static OAuthConsumer getTwitterConsumer() {
		return twitterConsumer;
	}

	public static void setTwitterConsumer(OAuthConsumer twitterConsumer) {
		TwitterUtils.twitterConsumer = twitterConsumer;
	}
	
	public static void TwitterShareTask (OAuthConsumer consumer, File file, String message) {
		TwitterUploadTask task = new TwitterUploadTask(consumer, file, message);
		task.execute();
	}
	
	private static class TwitterUploadTask extends AsyncTask<String, Void, String> {
		
		private File image;
		private String message;
		private OAuthConsumer twitterConsumer;
		
		public TwitterUploadTask(OAuthConsumer consumer, File file, String string) {
			this.image = file;
			this.message = string;
			this.twitterConsumer = consumer;
		}

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			HttpClient httpclient = GlobalValues.getHttpClient();
			HttpPost request = new HttpPost("https://api.twitter.com/1.1/statuses/update_with_media.json");

			ByteArrayInputStream bais = null;
			try {
				FileInputStream fis = new FileInputStream(image);
				BufferedInputStream bis = new BufferedInputStream(fis, 8192);
				Bitmap bm = BitmapFactory.decodeStream(bis);
				bis.close();
				fis.close();

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
				byte[] myTwitterByteArray = baos.toByteArray();
				bais = new ByteArrayInputStream(myTwitterByteArray);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				MultipartEntity entity = new MultipartEntity();
				entity.addPart("status", new StringBody(message));
				entity.addPart("media[]", new InputStreamBody(bais, image.getName()));
				request.setEntity(entity);
				twitterConsumer.sign(request);

				HttpResponse response = httpclient.execute(request, GlobalValues.getLocalContext());
				HttpEntity httpentity = response.getEntity();
				InputStream instream = httpentity.getContent();
				result = GlobalValues.convertStreamToString(instream);
				Log.i("statuses/update_with_media", result);
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

		public void onPostExecute(String result) {
			try {
				JSONObject jObject = new JSONObject(result.trim());
				System.out.println(jObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}			
		}
	}

}