package com.foursquare.tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.foursquare.constants.FoursquareConstants;
import com.foursquare.listeners.UserInfoRequestListener;
import com.foursquare.models.User;

import com.google.gson.Gson;

public class SelfInfoRequest extends AsyncTask<String, Integer, User> {

	private Activity mActivity;
	private ProgressDialog mProgress;
	private UserInfoRequestListener mListener;

	public SelfInfoRequest(Activity activity, UserInfoRequestListener listener) {
		mActivity = activity;
		mListener = listener;
	}

	public SelfInfoRequest(Activity activity) {
		mActivity = activity;
	}

	@Override
	protected void onPreExecute() {
		mProgress = new ProgressDialog(mActivity);
		mProgress.setCancelable(false);
		mProgress.setMessage("Getting user info ...");
		mProgress.show();
		super.onPreExecute();
	}

	@Override
	protected User doInBackground(String... params) {

		String token = params[0];
		User user = null;
		Gson gson = new Gson();
		// Check if there is a parameter called "code"
		if (token != null && retrieveUserInfo().equals("")) {
			try {
				// Get userdata of myself
				JSONObject userJson = executeHttpGet("https://api.foursquare.com/v2/"
						+ "users/self" + "?oauth_token=" + token);
				// Get return code
				int returnCode = Integer.parseInt(userJson
						.getJSONObject("meta").getString("code"));
				// 200 = OK
				if (returnCode == 200) {
					String json = userJson.getJSONObject("response")
							.getJSONObject("user").toString();
					saveUserInfo(json);
					user = gson.fromJson(json, User.class);
				} else {
					if (mListener != null)
						mListener.onError("Request Failed. Try again");
				}
			} catch (Exception exp) {
				if (mListener != null)
					mListener.onError(exp.toString());
			}
		} else {
			user = gson.fromJson(retrieveUserInfo(), User.class);
		}

		// request the user photo
		UserImageRequest request = new UserImageRequest(mActivity);
		Bitmap bitmap = request.getFileInCache();
		if (bitmap == null) {
			request.execute(user.getPhoto());
			try {
				Bitmap bmp = request.get();
				user.setBitmapPhoto(bmp);
			} catch (InterruptedException e) {
				e.printStackTrace();
				if (mListener != null)
					mListener.onError(e.toString());
			} catch (ExecutionException e) {
				e.printStackTrace();
				if (mListener != null)
					mListener.onError(e.toString());
			}
		} else {
			user.setBitmapPhoto(bitmap);
		}
		return user;
	}

	@Override
	protected void onPostExecute(User result) {
		mProgress.dismiss();
		if (mListener != null)
			mListener.onUserInfoFetched(result);
		super.onPostExecute(result);
	}

	/**
	 * Calls a URI and returns the answer as a JSON object.
	 * 
	 * @param uri
	 *            the uri to make the request
	 * @return The JSONObject containing the information
	 * @throws Exception
	 *             general exception
	 */
	private JSONObject executeHttpGet(String uri) throws Exception {
		HttpGet req = new HttpGet(uri);

		HttpClient client = new DefaultHttpClient();
		HttpResponse resLogin = client.execute(req);
		BufferedReader r = new BufferedReader(new InputStreamReader(resLogin
				.getEntity().getContent()));
		StringBuilder sb = new StringBuilder();
		String s = null;
		while ((s = r.readLine()) != null) {
			sb.append(s);
		}

		return new JSONObject(sb.toString());
	}

	private void saveUserInfo(String userJson) {
		SharedPreferences settings = mActivity.getSharedPreferences(
				FoursquareConstants.SHARED_PREF_FILE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(FoursquareConstants.USER_INFO, userJson);
		// Commit the edits!
		editor.commit();
	}

	private String retrieveUserInfo() {
		SharedPreferences settings = mActivity.getSharedPreferences(
				FoursquareConstants.SHARED_PREF_FILE, 0);
		return settings.getString(FoursquareConstants.USER_INFO, "");
	}

}
