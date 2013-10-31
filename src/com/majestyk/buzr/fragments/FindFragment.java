package com.majestyk.buzr.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.R;
import com.majestyk.buzr.activities.SearchActivity;
import com.majestyk.buzr.adapter.AdapterManager;
import com.majestyk.buzr.adapter.FeedItemAdapter;
import com.majestyk.buzr.adapter.RequestAdapter;
import com.majestyk.buzr.adapter.RequestAdapter.onAcceptOrDeclineListener;
import com.majestyk.buzr.apis.API_Notifications;
import com.majestyk.buzr.apis.API_RequestAD;
import com.majestyk.buzr.apis.API_Requests;
import com.majestyk.buzr.apis.OnTaskCompleteListener;
import com.majestyk.buzr.apis.OnTaskCompleteResultListener;
import com.majestyk.buzr.objects.Notification;
import com.majestyk.buzr.objects.NotificationHeader;
import com.majestyk.buzr.objects.NotificationItem;
import com.majestyk.buzr.objects.Request;

public class FindFragment extends Fragment implements onAcceptOrDeclineListener, OnClickListener, OnCheckedChangeListener {
	private final String TAG = getClass().getSimpleName();
	private final static String ARG_COUNT = "count";

	private Context mContext;
	private View v;

	private LinkedList<Notification> list1;
	private LinkedList<Request> list2;
	private FeedItemAdapter adapter1;
	private RequestAdapter adapter2;
	private PullToRefreshListView lv;
	private ImageView iv;
	private EditText et;

	private RadioButton rb1, rb2, rb3;
	private boolean flag = false;
	private int count;
	private static String mode = "";

	private MyAdapterManager manager;

	public final static FindFragment init(int count) {
		FindFragment fragment = new FindFragment();

		Bundle bundle = new Bundle();
		bundle.putInt(ARG_COUNT, count);
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		Bundle args = getArguments();

		count = args.getInt(ARG_COUNT, 0);
		Log.i(TAG, "Count = " + count);

		super.onCreate(savedInstanceState);
	}

	@Override
	public final View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {

		mContext = super.getActivity();
		v = (View) inflater.inflate(R.layout.buzr_find, container, false);

		list1 = new LinkedList<Notification>();
		list2 = new LinkedList<Request>();
		adapter1 = new FeedItemAdapter(mContext, list1);
		adapter2 = new RequestAdapter(mContext, list2, this);

		lv = ((PullToRefreshListView)v.findViewById(R.id.tabcontent));
		lv.setMode(Mode.PULL_FROM_END);
		iv = ((ImageView)v.findViewById(R.id.empty));
		manager = new MyAdapterManager(lv, adapter1, mContext);

		et = ((EditText)v.findViewById(R.id.searchBar));
		et.setOnClickListener(this);

		rb1 = (RadioButton)v.findViewById(R.id.radio1);
		rb2 = (RadioButton)v.findViewById(R.id.radio2);
		rb3 = (RadioButton)v.findViewById(R.id.radio3);
		rb1.setOnCheckedChangeListener(this);
		rb2.setOnCheckedChangeListener(this);
		rb3.setOnCheckedChangeListener(this);
		rb1.setChecked(true);

		SharedPreferences settings = mContext.getSharedPreferences(GlobalValues.PREFS, 0);
		if (settings.getString("PRIVACY", "0").equals("0"))
			rb3.setVisibility(View.INVISIBLE);
		else rb3.setVisibility(View.VISIBLE);

		return v;
	}

	@Override
	public final void onResume() {
		super.onResume();
		SharedPreferences settings = mContext.getSharedPreferences(GlobalValues.PREFS, 0);

		rb1 = (RadioButton)v.findViewById(R.id.radio1);
		rb2 = (RadioButton)v.findViewById(R.id.radio2);
		rb3 = (RadioButton)v.findViewById(R.id.radio3);
		rb1.setOnCheckedChangeListener(this);
		rb2.setOnCheckedChangeListener(this);
		rb3.setOnCheckedChangeListener(this);
		rb1.setChecked(true);

		if (settings.getString("PRIVACY", "0").equals("0"))
			rb3.setVisibility(View.INVISIBLE);
		else rb3.setVisibility(View.VISIBLE);

	}

	@Override
	public void onClick(boolean flag, int pos, String id) {
		AcceptOrDecline(flag, pos, id);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(mContext, SearchActivity.class);
		startActivity(intent);		
	}
	
	@Override
	public final void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		manager.reset();
		if(isChecked) {
			if(buttonView == rb1) {
				flag = true;
				list1.clear();
				adapter1.notifyDataSetChanged();
				getFeed(0, 20, count, flag);
			}

			if(buttonView == rb2) {
				flag = false;
				list1.clear();
				adapter1.notifyDataSetChanged();
				getFeed(0, 20, 0, flag);
			}

			if(buttonView == rb3) {
				getRequests();
			}
		}
	}

	private final void getFeed(final int i, final int j, final int count, final Boolean flag) {
		lv.setVisibility(View.VISIBLE);
		lv.setAdapter(adapter1);

		mode = "";
		if (flag) mode = "news";
		if (!flag) mode = "following";

		new API_Notifications(mContext, new OnTaskCompleteResultListener() {

			public void onComplete(boolean success, String result) {
				JSONArray jArray;

				if(success)
					try {
						jArray = new JSONArray(result.trim());
						for(int i = 0; i < jArray.length(); i++) {
							JSONObject jObject = jArray.getJSONObject(i);
							Log.i(TAG, jObject.toString());

							list1.add(new NotificationItem(
									jObject.getString("scenario"),
									jObject.getString("user_id"),
									jObject.getString("username"),
									jObject.getString("event"),
									formatTimestamp(jObject.getString("timestamp")),
									jObject.getString("target"),
									jObject.getString("emoji"),
									jObject.getString("upload_id"),
									jObject.getString("user")
									));

						}

						if(flag && count > 0) {
							list1.add(count, new NotificationHeader("old"));
							list1.add(0, new NotificationHeader("new"));
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

				adapter1.notifyDataSetChanged();

				if(list1.isEmpty()) {
					lv.setVisibility(View.GONE);
					iv.setImageResource(R.drawable.no_following);
				}

			}

		}).execute("user/"+mode+"Feed", Integer.toString(i), Integer.toString(j));

	}

	private final void getRequests() {
		lv.setVisibility(View.VISIBLE);
		lv.setAdapter(adapter2);

		new API_Requests(mContext, new OnTaskCompleteResultListener() {

			public void onComplete(boolean success, String result) {
				JSONArray jArray;

				if(success)
					try {
						jArray = new JSONArray(result.trim());
						for(int i = 0; i < jArray.length(); i++) {
							JSONObject jObject = jArray.getJSONObject(i);
							Log.i(TAG, jObject.toString());

							list2.add(new Request(
									jObject.getString("follow_id"),
									jObject.getString("user_id"),
									jObject.getString("username"),
									jObject.getString("description")
									));

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

				adapter2.notifyDataSetChanged();

				if(list2.isEmpty()) {
					lv.setVisibility(View.GONE);
					iv.setImageResource(R.drawable.no_requests);
				}

			}

		}).execute("user/getRequests");

	}

	private final void AcceptOrDecline (boolean flag, final int pos, String follow_id) {
		String api_call;
		if (flag) api_call = "user/approveFollow";
		else api_call = "user/declineFollow";

		new API_RequestAD(mContext, new OnTaskCompleteListener() {

			@Override
			public void onComplete(boolean success) {
				if(success) {
					list2.remove(pos);
					adapter2.notifyDataSetChanged();
				}
			}

		}).execute(api_call, follow_id);
	}

	private final String formatTimestamp (String old_timestamp) {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		inputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		Date new_timestamp = new Date();
		long timeInterval = new_timestamp.getTime();

		try {
			new_timestamp = inputFormat.parse(old_timestamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		timeInterval -= new_timestamp.getTime();
		timeInterval /= 1000;

		long mins	= timeInterval / 60;
		long hours	= timeInterval / 3600;
		long days	= timeInterval / 86400;
		long weeks	= timeInterval / 604800;

		String timePosted;

		if(weeks >= 1)
			if(weeks == 1)	timePosted = weeks + "w";
			else			timePosted = weeks + "w";

		else if(days >= 1)
			if(days == 1)	timePosted = days + "d";
			else			timePosted = days + "d";

		else if (hours >= 1)
			if(hours == 1)	timePosted = hours + "h";
			else			timePosted = hours + "h";

		else
			if(mins == 1)	timePosted = mins + "m";
			else if (mins == 0)	timePosted = "Just now";
			else			timePosted = mins + "m";

		return timePosted;

	}

	private final class MyAdapterManager extends AdapterManager<Notification> {
		private Context context;

		public MyAdapterManager(
				PullToRefreshListView listView, MyAdapterInterface<Notification> adapter, Context context) {
			super(listView, adapter);

			this.context = context.getApplicationContext();
		}

		@Override
		public void launchApiCall() {
			API_Notifications task = new API_Notifications(context, new OnTaskCompleteResultListener() {

				public void onComplete(boolean success, String result) {
					List<Notification> list1 = new ArrayList<Notification>();
					JSONArray jArray;

					if(success) {
						try {
							jArray = new JSONArray(result.trim());
							for(int i = 0; i < jArray.length(); i++) {
								JSONObject jObject = jArray.getJSONObject(i);
								Log.i(TAG, jObject.toString());

								list1.add(new NotificationItem(
										jObject.getString("scenario"),
										jObject.getString("user_id"),
										jObject.getString("username"),
										jObject.getString("event"),
										formatTimestamp(jObject.getString("timestamp")),
										jObject.getString("target"),
										jObject.getString("emoji"),
										jObject.getString("upload_id"),
										jObject.getString("user")
										));

							}

							onReportResult(true, list1);

						} catch (JSONException e) {
							e.printStackTrace();
						}

					} else {
						onReportResult(false, list1);
					}

				}

			});
			task.execute("user/"+mode+"Feed", Integer.toString(getStart()), Integer.toString(getPageSize()));
			getNextPage();
		}

		@Override
		public void reset() {
			getFirstPage();
		}
	}
}