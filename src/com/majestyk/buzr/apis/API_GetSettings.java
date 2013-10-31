package com.majestyk.buzr.apis;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import com.majestyk.buzr.GlobalValues;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class API_GetSettings extends AsyncTask<String, Void, String> {

	Context c;
	OnTaskCompleteResultListener complete;
	
	public API_GetSettings (Context c, OnTaskCompleteResultListener complete) {
		this.c = c;
		this.complete = complete;
	}

	protected String doInBackground(String... params) {
		String result = "";
		HttpClient httpclient = GlobalValues.getHttpClient();
		HttpPost httppost = new HttpPost(String.format(GlobalValues.getServiceUrl() + "%s", params[0]));
		
		try {
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
