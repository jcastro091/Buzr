package com.majestyk.buzr;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class GlobalValues {
	
	// Preferences
	public static final String PREFS = "BUZRPrefs";
	public static final String PREF_LOGIN = "log_status";
	public static final String PREF_USERNAME = "username";
	public static final String PREF_PASSWORD = "password";
	public static final String PREF_TOKEN = "android_token";
	public static final String STATUS_LOG = "logged_in";
	
	// Server
	protected static final String Service_URL = "http://buzr-dev.elasticbeanstalk.com/api/";
	protected static final String SERVER_ADDRESS = "";
	protected static DefaultHttpClient httpclient;
	protected static HttpContext localContext;

	// Request Codes
	public static final int ACTION_REQUEST_FEATHER = 100;
	public static final int REQUEST_CODE_IMAGE_CAPTURE = 100;
	public static final int REQUEST_CODE_IMAGE_MEDIA_TYPE = 1;
	public static final int REQUEST_CODE_IMAGE_CROP = 2;
	public static final int REQUEST_CODE_SEARCH_LOCATION = 12345;
	public static final int REQUEST_CODE_SEARCH_USER = 13579;

	// Home Activity Modes
	public static boolean MODE_TUTORIAL = false;
	public static boolean MODE_SUBMIT = false;

	protected static String user_id;
	public static String upload_id = "-1";
	
	public static GPSTracker gps;

	public static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append((line + "\n"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}

	public static String getServiceUrl() {
		return Service_URL;
	}

	public static DefaultHttpClient getHttpClient() {
		return httpclient;
	}

	public static HttpContext getLocalContext() {
		return localContext;
	}

	public static String getUserId() {
		return user_id;
	}

	public static void setHttpClient(DefaultHttpClient httpclient) {
		GlobalValues.httpclient = httpclient;
	}

	public static void setLocalContext(HttpContext localContext) {
		GlobalValues.localContext = localContext;
	}

	public static void setUserId(String user_id) {
		GlobalValues.user_id = user_id;
	}

	public static String BitMapToString(Bitmap bitmap) {
		ByteArrayOutputStream baos;
		byte [] b;
		String temp=null;
		try {
			System.gc();
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			b = baos.toByteArray();
			temp = Base64.encodeToString(b, Base64.DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
			b = baos.toByteArray();
			temp = Base64.encodeToString(b, Base64.DEFAULT);
			Log.i("BitMapToString", "Out of Memory");
			System.gc();
		}
		return temp;
	}

	public static String getRealPathFromURI(Context mContext, Uri contentURI) {
		Cursor cursor = mContext.getContentResolver().query(contentURI, null, null, null, null);
		if (cursor == null) {
			return contentURI.getPath();
		} else { 
			cursor.moveToFirst(); 
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
			return cursor.getString(idx); 
		}
	}

	public static Bitmap decodeFile(File f) {
		try {
			//Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			//The new size we want to scale to
			final int REQUIRED_SIZE=200;

			//Find the correct scale value. It should be the power of 2.
			int scale=1;
			while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
				scale*=2;

			//Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize=scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {}
		return null;
	}

	public static String formatTimestamp (String old_timestamp) {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		inputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		Date new_timestamp = new Date();
		long timeInterval = new_timestamp.getTime();

		try {
			new_timestamp = inputFormat.parse(old_timestamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		timeInterval -= new_timestamp.getTime();
		timeInterval /= 1000;

		long mins	= timeInterval / 60;
		long hours	= timeInterval / 3600;
		long days	= timeInterval / 86400;
		long weeks	= timeInterval / 604800;

		String timePosted;

		if(weeks >= 1)
			if(weeks == 1)	timePosted = weeks + " w";
			else			timePosted = weeks + " w";

		else if(days >= 1)
			if(days == 1)	timePosted = days + " d";
			else			timePosted = days + " d";

		else if (hours >= 1)
			if(hours == 1)	timePosted = hours + " h";
			else			timePosted = hours + " h";

		else
			if(mins == 1)	timePosted = mins + " m";
			else if (mins == 0)	timePosted = "Just now";
			else			timePosted = mins + " m";

		return timePosted;

	}

}