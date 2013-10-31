package com.majestyk.buzr.apis;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.majestyk.buzr.GlobalValues;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class API_Login extends AsyncTask<String, Void, String> {

	Context c;
	OnTaskCompleteListener complete;
	ProgressDialog dialog;
	
	public API_Login (Context c, OnTaskCompleteListener complete) {
		this.c = c;
		this.complete = complete;
//		this.dialog = new ProgressDialog(c);
	}
	
	protected void onPreExecute() {
//		dialog.setMessage("Logging in ...");  
//		dialog.show();
	}

	protected String doInBackground(String... params) {
		String result = "";
		HttpClient httpclient = GlobalValues.getHttpClient();
		HttpPost httppost = new HttpPost(String.format(GlobalValues.getServiceUrl() + "%s", params[0]));
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("username",	  params[1]));
		nameValuePairs.add(new BasicNameValuePair("password",	  params[2]));
		nameValuePairs.add(new BasicNameValuePair("android_token",params[3]));
		nameValuePairs.add(new BasicNameValuePair("os",			  "android"));

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
//		dialog.dismiss();
		try {
			JSONObject jObject = new JSONObject(result.trim());

			if(jObject.has("user_id")) {
				Toast.makeText(c, "Login successful", Toast.LENGTH_SHORT).show();
				GlobalValues.setUserId((String) jObject.get("user_id"));
				System.out.println(GlobalValues.getUserId());
				complete.onComplete(true);
			} else if(jObject.has("error")) {
				Toast.makeText(c, jObject.getString("error"), Toast.LENGTH_SHORT).show();
				complete.onComplete(false);
			} else complete.onComplete(false);

		} catch (JSONException e) {
			complete.onComplete(false);
			e.printStackTrace();
		}
	}
}
