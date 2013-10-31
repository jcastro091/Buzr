package com.majestyk.buzr.social;

import java.io.File;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

public class FacebookUtils {

	// Consumer Key and Secret
	private static final String FACEBOOK_CONSUMER_KEY = "365138376931084";
	private static final String FACEBOOK_CONSUMER_SECRET = "adf34a724260cfae278e7d22f171c8ef";

	// Request Code
	public static final int FACEBOOK_REQUEST_CODE = 32665;

	// Preference Constants
	public static final String PREF_FACEBOOK_ACCESS = "facebook_access_token";
	public static final String PREF_FACEBOOK_SECRET = "facebook_secret_token";
	
	private static Session facebookSession;

	public static String getConsumerKey() {
		return FACEBOOK_CONSUMER_KEY;
	}

	public static String getConsumerSecret() {
		return FACEBOOK_CONSUMER_SECRET;
	}
	
	public static Session getFacebookSession() {
		return facebookSession;
	}

	public static void setFacebookSession(Session facebookSession) {
		FacebookUtils.facebookSession = facebookSession;
	}
	
	public static void FacebookShareTask (final Activity mContext, Session facebookSession, File file, String message) {
		facebookSession = Session.getActiveSession();

		if (facebookSession != null) {

			// Part 1: create callback to get URL of uploaded photo
			Request.Callback uploadPhotoRequestCallback = new Request.Callback() {
				@Override
				public void onCompleted(Response response) {
					if (mContext.isFinishing()) {
						return;
					}
					if (response.getError() != null) {
						Log.d("Facebook Share", "photo upload problem. Error="+response.getError() );
					}

					Object graphResponse = response.getGraphObject().getProperty("id");
					if (graphResponse == null || !(graphResponse instanceof String) || 
							TextUtils.isEmpty((String) graphResponse)) {
						Log.d("Facebook Share", "failed photo upload/no response");
					} else { }
				}
			}; 

			//Part 2: upload the photo
			Request request;
			try {
		        ParcelFileDescriptor descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
		        Bundle parameters = new Bundle(2);
		        parameters.putParcelable("picture", descriptor);
		        	parameters.putString("message", message);

		        request = new Request(facebookSession, "me/photos", parameters, HttpMethod.POST, uploadPhotoRequestCallback);
				request.executeAsync();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
