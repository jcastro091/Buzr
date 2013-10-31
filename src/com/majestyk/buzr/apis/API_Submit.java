package com.majestyk.buzr.apis;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.MyMultipartEntity;
import com.majestyk.buzr.MyMultipartEntity.ProgressListener;
import com.majestyk.buzr.R;

public class API_Submit extends AsyncTask<String, Integer, String> {

	View v;
	Context c;
	OnTaskCompleteResultListener complete;
	private String encodedImgBmp;
	private String description;
	private String location;
	TextView upload_status;
	ProgressBar dialog;
	long totalSize;

	public API_Submit (Context c, String s1, String s2, String s3, 
			View v, OnTaskCompleteResultListener complete) {
		this.c = c;
		this.v = v;
		this.encodedImgBmp = s1;
		this.description = s2;
		this.location = s3;
		this.complete = complete;

		upload_status = (TextView)v.findViewById(R.id.upload_status);
	}

	@Override
	protected void onPreExecute() {
		dialog = (ProgressBar)v.findViewById(R.id.upload_progress);
	}

	@Override
	protected String doInBackground(String... params) {
		String result = "";
		HttpClient httpclient = GlobalValues.getHttpClient();
		HttpPost httppost = new HttpPost(String.format(GlobalValues.getServiceUrl() + "%s", params[0]));

		try {
			MyMultipartEntity multipartContent = new MyMultipartEntity(new ProgressListener() {
				@Override
				public void transferred(long num) {
					publishProgress((int) ((num / (float) totalSize) * 100));
				}
			});

			multipartContent.addPart("image", new StringBody(encodedImgBmp));
			multipartContent.addPart("description", new StringBody(description));
			multipartContent.addPart("location", new StringBody(location));

			totalSize = multipartContent.getContentLength();

			httppost.setEntity(multipartContent);
			HttpResponse response = httpclient.execute(httppost, GlobalValues.getLocalContext());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				result = GlobalValues.convertStreamToString(instream);
				Log.i(params[0], params[0] + ": " + result);
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return result;
	}

	public void onPostExecute(String result) {
		try {
			JSONObject jObject = new JSONObject(result.trim());

			if(jObject.has("upload") || jObject.has("upload_id")) {
				complete.onComplete(true, result);
			} else if(jObject.has("error")) {
				complete.onComplete(false, result);
			} else complete.onComplete(false, result);

		} catch (JSONException e) {
			complete.onComplete(false, result);
			e.printStackTrace();
		}
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		dialog.setProgress((int) (progress[0]));
		upload_status.setText(Integer.toString(progress[0]) + "%");
	}

}
