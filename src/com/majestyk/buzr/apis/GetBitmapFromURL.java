package com.majestyk.buzr.apis;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class GetBitmapFromURL extends AsyncTask<String, Void, Bitmap> {

	public interface BitmapListener {
		public void onComplete(Bitmap result);
	}

	String URL;
	BitmapListener complete;

	public GetBitmapFromURL(String imageUrl, BitmapListener listener) {
		this.URL = imageUrl;
		this.complete = listener;
	}

	protected Bitmap doInBackground(String... params) {
		System.gc();
		Bitmap myBitmap = null;
		try {
			URL url = new URL(URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();

			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inSampleSize = 1;

			myBitmap = BitmapFactory.decodeStream(input, null, options);

			connection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
		}
		return myBitmap;
	}

	protected void onPostExecute (Bitmap bitmap) {
		complete.onComplete(bitmap);
	}
}
