package com.majestyk.buzr.apis;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.MyMultipartEntity;
import com.majestyk.buzr.MyMultipartEntity.ProgressListener;

public class API_Update extends AsyncTask<String, Void, String> {

	Context c;
	OnTaskCompleteListener complete;
	ProgressDialog dialog;
	private String encodedImageBmp;
	private String description;
	private String username;

	public API_Update (Context c, String s1, String s2, String s3, OnTaskCompleteListener complete) {
		this.c = c;
		this.encodedImageBmp = s1;
		this.description = s2;
		this.username = s3;
		this.complete = complete;
	}

	protected String doInBackground(String... params) {
		String result = "";
		HttpClient httpclient = GlobalValues.getHttpClient();
		HttpPost httppost = new HttpPost(String.format(GlobalValues.getServiceUrl() + "%s", params[0]));

		try {
			MyMultipartEntity multipartContent = new MyMultipartEntity(new ProgressListener() {
				@Override
				public void transferred(long num) {	}
			});

			if(encodedImageBmp != null)
				multipartContent.addPart("image", new StringBody(encodedImageBmp));
			multipartContent.addPart("username", new StringBody(username));
			multipartContent.addPart("description", new StringBody(description));

			httppost.setEntity(multipartContent);
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

			if(jObject.has("user_id")) {
				complete.onComplete(true);
			} else if(jObject.has("error")) {
				complete.onComplete(false);
			} else complete.onComplete(false);

		} catch (JSONException e) {
			complete.onComplete(false);
			e.printStackTrace();
		}
	}
}
