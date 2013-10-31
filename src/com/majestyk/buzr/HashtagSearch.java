package com.majestyk.buzr;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.majestyk.buzr.apis.API_Search;
import com.majestyk.buzr.apis.OnTaskCompleteResultListener;
import com.majestyk.buzr.objects.SearchItem;

public class HashtagSearch {

	private Context mContext;
	protected LinkedList<SearchItem> list;
	
	public HashtagSearch (Context c) {
		this.mContext = c;
		list = new LinkedList<SearchItem>();
	}

	public LinkedList<SearchItem> getAllUsers(String str) {
		list.clear();
		allUserApiTask(str);
		return list;
	}

	private LinkedList<SearchItem> allUserApiTask(String str) {
		list.clear();

		new API_Search(mContext, false, new OnTaskCompleteResultListener() {

			@Override
			public void onComplete(boolean success, String result) {

				JSONArray jArray;

				if(success)
					try {

						jArray = new JSONArray(result.trim());
						for (int i = 0; i < jArray.length(); i++) {
							JSONObject j = jArray.getJSONObject(i);

							list.add(new SearchItem (
									j.getString("upload_id"),
									j.getString("user_id"),
									j.getString("username"),
									j.getString("description"),
									j.getString("image"))
									);

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

			}

		}).execute("upload/getAll", str);

		return list;
	}

	public LinkedList<SearchItem> getHashtags(String str) {
		list.clear();
		hashTagApiTask(str);
		return list;
	}


	private LinkedList<SearchItem> hashTagApiTask (String str) {

		list.clear();

		new API_Search(mContext, true, new OnTaskCompleteResultListener() {

			@Override
			public void onComplete(boolean success, String result) {

				JSONArray jArray;

				if(success)
					try {

						jArray = new JSONArray(result.trim());
						for (int i = 0; i < jArray.length(); i++) {
							JSONObject j = jArray.getJSONObject(i);

							list.add(new SearchItem (
									j.getString("upload_id"),
									null,
									j.getString("username"),
									j.getString("description"),
									j.getString("image"))
									);

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

			}

		}).execute("upload/search", str);

		return list;
	}

}
