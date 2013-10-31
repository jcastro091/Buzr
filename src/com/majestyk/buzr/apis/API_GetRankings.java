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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.majestyk.buzr.GlobalValues;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class API_GetRankings extends AsyncTask<String, Void, String> {

	Context c;
	OnTaskCompleteResultListener complete;

	public API_GetRankings (Context c, OnTaskCompleteResultListener complete) {
		this.c = c;
		this.complete = complete;
	}

	protected String doInBackground(String... params) {
		String result = "";
		HttpClient httpclient = GlobalValues.getHttpClient();
		HttpPost httppost = new HttpPost(String.format(GlobalValues.getServiceUrl() + "%s", params[0]));

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("start",		params[1]));
		nameValuePairs.add(new BasicNameValuePair("limit",		params[2]));
		nameValuePairs.add(new BasicNameValuePair("rating",		params[3]));
		nameValuePairs.add(new BasicNameValuePair("friends",	params[4]));

		System.out.println(params[0] + " " + params[1] + " " + params[2] + " " + params[3] + " " + params[4]);

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
			JSONArray jArray = new JSONArray(result.trim());
			JSONObject jObject = jArray.getJSONObject(0);

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
