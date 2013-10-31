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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class API_Logout extends AsyncTask<String, Void, String> {

	Context c;
	OnTaskCompleteListener complete;
	ProgressDialog dialog;
	
	public API_Logout (Context c, OnTaskCompleteListener complete) {
		this.c = c;
		this.complete = complete;
//		this.dialog = new ProgressDialog(c);
	}
	
	protected void onPreExecute() {
//		dialog.setMessage("Logging out ...");  
//		dialog.show();
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
//		dialog.dismiss();
		try {
			
			if (result.trim().equals("true")) {
				Toast.makeText(c, "Logout successful", Toast.LENGTH_SHORT).show();
				complete.onComplete(true);
			} else { 
				JSONObject jObject = new JSONObject(result.trim());

				if(jObject.has("error")) {
					Toast.makeText(c, jObject.getString("error"), Toast.LENGTH_SHORT).show();
					complete.onComplete(false);
				}
			}
			
		} catch (JSONException e) {
			complete.onComplete(false);
			e.printStackTrace();
		}
	}
}
