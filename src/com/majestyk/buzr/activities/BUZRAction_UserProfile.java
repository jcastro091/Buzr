package com.majestyk.buzr.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.polidea.webimageview.WebImageView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.majestyk.buzr.BASE64;
import com.majestyk.buzr.R;
import com.majestyk.buzr.adapter.ImageAdapter;
import com.majestyk.buzr.adapter.ImageGridAdapterManager;
import com.majestyk.buzr.adapter.ImageListAdapterManager;
import com.majestyk.buzr.apis.API_Follow;
import com.majestyk.buzr.apis.API_GetImages;
import com.majestyk.buzr.apis.API_GetProfile;
import com.majestyk.buzr.apis.OnTaskCompleteListener;
import com.majestyk.buzr.apis.OnTaskCompleteResultListener;
import com.majestyk.buzr.objects.JSONImage;

public class BUZRAction_UserProfile extends Activity implements OnClickListener, OnCheckedChangeListener {

	private static Boolean flag;
	private static String uid;
	private ArrayList<JSONImage> list;
	private ImageAdapter gridAdapter;
	private ImageAdapter listAdapter;
	private PullToRefreshGridView gv;
	private PullToRefreshListView lv;
	private ImageView empty;

	private Button button;
	private String privateUser ="0";

	private RadioButton rb1, rb2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);

		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		button = ((Button)findViewById(R.id.button3));

		uid = getIntent().getStringExtra("user_id");
		flag = false;

		list = new ArrayList<JSONImage>();
		gridAdapter = new ImageAdapter(this, list, true, false);
		listAdapter = new ImageAdapter(this, list, false, false);
		empty = (ImageView)findViewById(R.id.emptyImage);
		gv = (PullToRefreshGridView)findViewById(R.id.gridContent);
		lv = (PullToRefreshListView)findViewById(R.id.listContent);
		gv.setMode(Mode.PULL_FROM_END);
		lv.setMode(Mode.PULL_FROM_END);
		gv.setAdapter(gridAdapter);
		lv.setAdapter(listAdapter);

		getProfile(uid);

		rb1 = ((RadioButton)findViewById(R.id.radio1));
		rb2 = ((RadioButton)findViewById(R.id.radio2));
		rb1.setOnCheckedChangeListener(this);
		rb2.setOnCheckedChangeListener(this);
		rb1.setChecked(true);

		button.setOnClickListener(this);
		findViewById(R.id.followers).setOnClickListener(this);
		findViewById(R.id.are_following).setOnClickListener(this);

		@SuppressWarnings("unused")
		MyListAdapterManager listManager = new MyListAdapterManager(lv, listAdapter, this);
		@SuppressWarnings("unused")
		MyGridAdapterManager gridManager = new MyGridAdapterManager(gv, gridAdapter, this);

	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.button3:
			follow();
			break;
		case R.id.followers:
			intent = new Intent(BUZRAction_UserProfile.this, Action_FriendsFollow.class);
			intent.putExtra("mode", "Followers");
			intent.putExtra("user_id", uid);
			startActivity(intent);
			break;
		case R.id.are_following:
			intent = new Intent(BUZRAction_UserProfile.this, Action_FriendsFollow.class);
			intent.putExtra("mode", "Following");
			intent.putExtra("user_id", uid);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {		

		list.clear();
		listAdapter.notifyDataSetChanged();
		gridAdapter.notifyDataSetChanged();

		if(isChecked) {
			if(buttonView == rb1) {
				gv.setVisibility(View.VISIBLE);
				lv.setVisibility(View.INVISIBLE);
				empty.setVisibility(View.INVISIBLE);
				getProfileImages();
			}
			if(buttonView == rb2) {
				lv.setVisibility(View.VISIBLE);
				gv.setVisibility(View.INVISIBLE);
				empty.setVisibility(View.INVISIBLE);
				getProfileImages();
			}
		}
	}

	private final void getProfile(final String id) {

		new API_GetProfile(BUZRAction_UserProfile.this, new OnTaskCompleteResultListener() {

			public void onComplete(boolean success, String result) {
				JSONArray jArray;
				JSONObject jObject;

				if(success)
					try {

						jObject = new JSONObject(result.trim());
						System.out.println(jObject);

						String user_id = jObject.getString("user_id");
						String username = jObject.getString("username");
						String description = BASE64.decodeBase64(jObject.getString("description"));
						String isPrivate = jObject.getString("private");
						String followers = jObject.getString("followers");
						String following = jObject.getString("following");
						String uploads = jObject.getString("uploads");
						String requested = jObject.getString("requested");
						String are_following = jObject.getString("are_following");
						String profile_image = jObject.getString("profile_image");

						jArray = jObject.getJSONArray("images");
						for (int i = 0; i < jArray.length(); i++) {
							JSONObject j = jArray.getJSONObject(i);
							list.add(new JSONImage(
									j.getString("upload_id"),
									j.getString("url")
									));
						}

						uid = user_id;
						((TextView)findViewById(R.id.username)).setText(username);
						((TextView)findViewById(R.id.prof_username)).setText(username);
						((TextView)findViewById(R.id.prof_description)).setText(description);

						((TextView)findViewById(R.id.prof_followers)).setText(followers);
						((TextView)findViewById(R.id.prof_following)).setText(following);
						((TextView)findViewById(R.id.prof_photos)).setText(uploads);

						WebImageView imageView = (WebImageView)findViewById(R.id.prof_image); 
						imageView.setImageURL(profile_image);

						if(are_following.equals("0")) {
							button.setBackgroundResource(R.drawable.button_follow_user);
						} else {
							if(requested.equals("0")) {
								button.setBackgroundResource(R.drawable.button_following_user);
							} else {
								button.setBackgroundResource(R.drawable.button_requested_user);
							}
						}

						privateUser = isPrivate;
						if(isPrivate.equals("1") && are_following.equals("0")) {
							rb1.setOnCheckedChangeListener(null);
							rb2.setOnCheckedChangeListener(null);
							empty.setVisibility(View.VISIBLE);
							gv.setVisibility(View.INVISIBLE);
							lv.setVisibility(View.INVISIBLE);
						} else {
							rb1.setOnCheckedChangeListener(BUZRAction_UserProfile.this);
							rb2.setOnCheckedChangeListener(BUZRAction_UserProfile.this);
						};

					} catch (JSONException e) {
						e.printStackTrace();
					}
			}

		}).execute("user/get", id);

	}

	private final void getProfileImages() {
		
		new API_GetImages(BUZRAction_UserProfile.this, new OnTaskCompleteResultListener() {

			public void onComplete(boolean success, String result) {
				JSONArray jArray;
				JSONObject jObject;
		
				list.clear();
				
				if(success)
					try {
						jArray = new JSONArray(result.trim());

						for(int i = 0; i < jArray.length(); i++) {
							jObject = jArray.getJSONObject(i);
							System.out.println(jObject);

							list.add(new JSONImage(
									jObject.getString("upload_id"),
									jObject.getString("image")
									));

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

				listAdapter.notifyDataSetChanged();
				gridAdapter.notifyDataSetChanged();

			}

		}).execute("upload/getAll", uid, Integer.toString(0), Integer.toString(18));

	}

	private final void follow () {
		System.out.println(flag);
		if(flag)
			new API_Follow(BUZRAction_UserProfile.this, new OnTaskCompleteListener() {
				public void onComplete(boolean success) {
					if(success) {
						flag = false;
						button.setBackgroundResource(R.drawable.button_follow_user);
					}
				}
			}).execute("user/unfollow", uid);
		else
			new API_Follow(BUZRAction_UserProfile.this, new OnTaskCompleteListener() {
				public void onComplete(boolean success) {
					if(success) {
						flag = true;
						if(privateUser.equals("0"))
							button.setBackgroundResource(R.drawable.button_following_user);
						else
							button.setBackgroundResource(R.drawable.button_requested_user);
					}
				}
			}).execute("user/follow", uid);
	}

	private final static class MyListAdapterManager extends ImageListAdapterManager<JSONImage> {
		private Context context;

		public MyListAdapterManager(
				PullToRefreshListView listView, MyListAdapterInterface<JSONImage> adapter, Context context) {
			super(listView, adapter);

			this.context = context.getApplicationContext();
		}

		@Override
		public void launchApiCall() {

			API_GetImages task = new API_GetImages(context, new OnTaskCompleteResultListener() {

				@Override
				public void onComplete(boolean success, String result) {
					List<JSONImage> list = new ArrayList<JSONImage>();
					JSONArray jArray;

					if(success) {
						try {
							jArray = new JSONArray(result.trim());

							for(int i = 0; i < jArray.length(); i++) {
								JSONObject jObject = jArray.getJSONObject(i);
								System.out.println(jObject);

								list.add(new JSONImage(
										jObject.getString("upload_id"),
										jObject.getString("image")
										));
							}

							onReportResult(true, list);

						} catch (JSONException e) {
							e.printStackTrace();
						}

					} else {
						onReportResult(false, list);
					}

				}

			});
			task.execute("upload/getAll", uid, Integer.toString(getStart()), Integer.toString(getPageSize()));
			getNextPage();
		}
	}

	private final static class MyGridAdapterManager extends ImageGridAdapterManager<JSONImage> {
		private Context context;

		public MyGridAdapterManager(
				PullToRefreshGridView gridView, MyGridAdapterInterface<JSONImage> adapter, Context context) {
			super(gridView, adapter);

			this.context = context.getApplicationContext();
		}

		@Override
		public void launchApiCall() {

			API_GetImages task = new API_GetImages(context, new OnTaskCompleteResultListener() {

				@Override
				public void onComplete(boolean success, String result) {
					List<JSONImage> list = new ArrayList<JSONImage>();
					JSONArray jArray;

					if(success) {
						try {
							jArray = new JSONArray(result.trim());

							for(int i = 0; i < jArray.length(); i++) {
								JSONObject jObject = jArray.getJSONObject(i);
								System.out.println(jObject);

								list.add(new JSONImage(
										jObject.getString("upload_id"),
										jObject.getString("image")
										));
							}

							onReportResult(true, list);

						} catch (JSONException e) {
							e.printStackTrace();
						}

					} else {
						onReportResult(false, list);
					}
				}

			});
			task.execute("upload/getAll", uid, Integer.toString(getStart()), Integer.toString(getPageSize()));
			getNextPage();
		}
	}

}