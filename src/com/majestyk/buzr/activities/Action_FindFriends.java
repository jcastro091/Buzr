package com.majestyk.buzr.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.majestyk.buzr.BASE64;
import com.majestyk.buzr.R;
import com.majestyk.buzr.adapter.OnFollowClickListener;
import com.majestyk.buzr.adapter.SuggestedUserAdapter;
import com.majestyk.buzr.apis.API_Follow;
import com.majestyk.buzr.apis.API_Suggested;
import com.majestyk.buzr.apis.OnTaskCompleteListener;
import com.majestyk.buzr.apis.OnTaskCompleteResultListener;
import com.majestyk.buzr.objects.JSONImage;
import com.majestyk.buzr.objects.SuggestedUser;

public class Action_FindFriends extends Activity implements OnClickListener, OnFollowClickListener {

	private ArrayList<SuggestedUser> list;
	private SuggestedUserAdapter adapter;
	private PullToRefreshListView listView;

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_find_friends);

		findViewById(R.id.imageButton1).setOnClickListener(this);
		findViewById(R.id.imageButton2).setOnClickListener(this);
		findViewById(R.id.imageButton3).setOnClickListener(this);

		list = new ArrayList<SuggestedUser>();
		adapter = new SuggestedUserAdapter(this, list, this);
		listView = (PullToRefreshListView)findViewById(R.id.listContent);
		listView.setMode(Mode.PULL_FROM_END);
		listView.setAdapter(adapter);

		getSuggestedUsers();

	}

	@Override
	public final void onClick(View view) {
		Intent intent;
		switch (view.getId()) {
		case R.id.imageButton1:
			intent = new Intent(Action_FindFriends.this, FindFriendsActivity.class);
			intent.putExtra("account", "facebook");
			startActivity(intent);
			break;
		case R.id.imageButton2:
			intent = new Intent(Action_FindFriends.this, FindFriendsActivity.class);
			intent.putExtra("account", "twitter");
			startActivity(intent);
			break;
		case R.id.imageButton3:
			intent = new Intent(Action_FindFriends.this, FindContactsActivity.class);
			startActivity(intent);
			break;
		}
	}

	private final void getSuggestedUsers() {

		new API_Suggested(this, new OnTaskCompleteResultListener() {

			@Override
			public void onComplete(boolean success, String result) {
				JSONArray jArray;
				JSONObject jObject;

				list.clear();

				if(success) {
					try {
						jObject = new JSONObject(result.trim());
						jArray = jObject.getJSONArray("users");

						for(int i = 0; i < jArray.length(); i++) {
							try {
								JSONObject userObject = jArray.getJSONObject(i);

								JSONArray uArray = userObject.getJSONArray("uploads");

								ArrayList<JSONImage> imageList = new ArrayList<JSONImage>();
								for(int j = 0; j < uArray.length(); j++) {
									try {
										JSONObject uploadObject = uArray.getJSONObject(j);

										imageList.add(new JSONImage(
												uploadObject.getString("upload_id"),
												uploadObject.getString("image")));

									} catch(JSONException e) {
										e.printStackTrace();
									}
								}

								list.add(new SuggestedUser(
										userObject.getString("user_id"),
										userObject.getString("username"),
										BASE64.decodeBase64(userObject.getString("description")),
										userObject.getString("image"),
										false, imageList));

							} catch(JSONException e) {
								e.printStackTrace();
							}
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				adapter.notifyDataSetChanged();

			}

		}).execute("suggested/get", "2", "0", "10");

	}

	@Override
	public final void onAre_FollowClick(String UserID) {
		new API_Follow(this, new OnTaskCompleteListener() {
			public void onComplete(boolean success) {
				if(success) { }
			}
		}).execute("user/unfollow", UserID);
	}

	@Override
	public final void onNot_FollowClick(String UserID) {
		new API_Follow(this, new OnTaskCompleteListener() {
			public void onComplete(boolean success) {
				if(success) { }
			}
		}).execute("user/follow", UserID);
	}

}
