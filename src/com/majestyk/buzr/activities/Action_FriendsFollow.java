package com.majestyk.buzr.activities;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.majestyk.buzr.BASE64;
import com.majestyk.buzr.R;
import com.majestyk.buzr.adapter.AdapterManager;
import com.majestyk.buzr.adapter.FollowItemAdapter;
import com.majestyk.buzr.adapter.OnFollowClickListener;
import com.majestyk.buzr.apis.API_Follow;
import com.majestyk.buzr.apis.API_GetFollow;
import com.majestyk.buzr.apis.OnTaskCompleteListener;
import com.majestyk.buzr.apis.OnTaskCompleteResultListener;
import com.majestyk.buzr.objects.FollowItem;

public class Action_FriendsFollow extends Activity implements OnFollowClickListener {

	private LinkedList<FollowItem> list;
	private FollowItemAdapter adapter;
	private PullToRefreshListView listView;

	private static String mode, userID;

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_friends_follow);

		((TextView)findViewById(R.id.label)).setText(getIntent().getStringExtra("mode"));

		list = new LinkedList<FollowItem>();
		adapter = new FollowItemAdapter(Action_FriendsFollow.this, list, this);
		listView = (PullToRefreshListView)findViewById(R.id.ListView1);
		listView.setMode(Mode.PULL_FROM_END);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(Action_FriendsFollow.this, BUZRAction_UserProfile.class);
				intent.putExtra("user_id", list.get(arg2).getUID());
				startActivity(intent);								
			}

		});

		mode = getIntent().getStringExtra("mode");
		userID = getIntent().getStringExtra("user_id");

		get();

		@SuppressWarnings("unused")
		MyAdapterManager manager = new MyAdapterManager(listView, adapter, this);

	}

	private final void get() {

		new API_GetFollow(this, new OnTaskCompleteResultListener() {

			@Override
			public void onComplete(boolean success, String result) {
				list.clear();

				if(success) {
					try {
						JSONArray jArray = new JSONArray(result.trim());

						for (int i = 0; i < jArray.length(); i++) {
							JSONObject jObject = jArray.getJSONObject(i); 

							System.out.println(jObject);

							list.add(new FollowItem(
									jObject.getString("follow_id"),
									jObject.getString("user_id"),
									jObject.getString("username"),
									BASE64.decodeBase64(jObject.getString("description")),
									jObject.getString("are_following"),
									jObject.getString("image")
									));
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

				adapter.notifyDataSetChanged();

			}

		}).execute("user/get" + mode, userID, Integer.toString(0), Integer.toString(20));

	}

	private final static class MyAdapterManager extends AdapterManager<FollowItem> {
		private Context context;

		public MyAdapterManager(
				PullToRefreshListView listView, MyAdapterInterface<FollowItem> adapter, Context context) {
			super(listView, adapter);

			this.context = context.getApplicationContext();
		}

		@Override
		public void launchApiCall() {
			API_GetFollow task = new API_GetFollow(context, new OnTaskCompleteResultListener() {

				@Override
				public void onComplete(boolean success, String result) {
					LinkedList<FollowItem> list = new LinkedList<FollowItem>();
					JSONArray jArray;

					if(success) {
						try {
							jArray = new JSONArray(result.trim());

							for (int i = 0; i < jArray.length(); i++) {
								JSONObject jObject = jArray.getJSONObject(i);
								System.out.println(jObject);

								list.add(new FollowItem(
										jObject.getString("follow_id"),
										jObject.getString("user_id"),
										jObject.getString("username"),
										BASE64.decodeBase64(jObject.getString("description")),
										jObject.getString("are_following"),
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
			task.execute("user/get" + mode, userID,	Integer.toString(getStart()), Integer.toString(getPageSize()));
			getNextPage();
		}

		@Override
		public void reset() {
			// TODO Auto-generated method stub
		}
	}

	@Override
	public final void onAre_FollowClick(String ID) {
		new API_Follow(this, new OnTaskCompleteListener() {
			public void onComplete(boolean success) {
				if(success) {
					get();
				}
			}
		}).execute("user/unfollow", ID);
	}

	@Override
	public final void onNot_FollowClick(String ID) {
		new API_Follow(this, new OnTaskCompleteListener() {
			public void onComplete(boolean success) {
				if(success) {
					get();
				}
			}
		}).execute("user/follow", ID);
	}
}