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
import android.widget.ImageButton;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.majestyk.buzr.BASE64;
import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.R;
import com.majestyk.buzr.adapter.OnFollowClickListener;
import com.majestyk.buzr.adapter.SuggestedUserAdapter;
import com.majestyk.buzr.apis.API_Follow;
import com.majestyk.buzr.apis.API_Suggested;
import com.majestyk.buzr.apis.OnTaskCompleteListener;
import com.majestyk.buzr.apis.OnTaskCompleteResultListener;
import com.majestyk.buzr.fragments.BUZRFragmentManagerActivity;
import com.majestyk.buzr.objects.JSONImage;
import com.majestyk.buzr.objects.SuggestedUser;

public class NewUserActivity extends Activity implements OnClickListener, OnFollowClickListener {

	private int count;
	private ImageButton done;
	private ArrayList<SuggestedUser> list;
	private SuggestedUserAdapter adapter;
	private PullToRefreshListView listView;
	private ArrayList<String> selectedUsers;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_to_follow);

		count = 0;
		selectedUsers = new ArrayList<String>();

		done = (ImageButton)findViewById(R.id.button1);
		done.setOnClickListener(this);
		
		list = new ArrayList<SuggestedUser>();
		adapter = new SuggestedUserAdapter(this, list, this);
		listView = (PullToRefreshListView)findViewById(R.id.listContent);
		listView.setMode(Mode.PULL_FROM_END);
		listView.setAdapter(adapter);

		getSuggestedUsers();

	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button1:
			onFinish();
			Intent intent = new Intent(this, BUZRFragmentManagerActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			
			GlobalValues.MODE_TUTORIAL = true;
			
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onAre_FollowClick(String ID) {
		count--;
		canContinue();
		selectedUsers.remove(ID);
	}

	@Override
	public void onNot_FollowClick(String ID) {
		count++;
		canContinue();
		selectedUsers.add(ID);
	}

	private void canContinue() {
		if (count < 3)
			done.setVisibility(View.GONE);
		else
			done.setVisibility(View.VISIBLE);
	}

	public void getSuggestedUsers() {

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

	private void onFinish() {
		for (String UserID : selectedUsers) {
			new API_Follow(this, new OnTaskCompleteListener() {
				public void onComplete(boolean success) {
					if(success) { }
				}
			}).execute("user/follow", UserID);
		}
	}
}
