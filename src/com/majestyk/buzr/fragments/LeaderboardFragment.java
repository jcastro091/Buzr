package com.majestyk.buzr.fragments;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.majestyk.buzr.Emojis;
import com.majestyk.buzr.R;
import com.majestyk.buzr.adapter.AdapterManager;
import com.majestyk.buzr.adapter.RankingAdapter;
import com.majestyk.buzr.apis.API_GetRankings;
import com.majestyk.buzr.apis.OnTaskCompleteResultListener;
import com.majestyk.buzr.objects.Ranking;

public class LeaderboardFragment extends Fragment implements OnClickListener, OnCheckedChangeListener {
	private final String TAG = getClass().getSimpleName();
	private FragmentActivity mContext;
	private View v;

	private LinkedList<Ranking> list;
	private RankingAdapter adapter;
	private PullToRefreshListView lv;

	private RadioButton rb1, rb2;
	private static int count = 0;
	private static int flag = 0; //false
	private final Integer[] categories = Emojis.getCategories();
	private ImageView carousel, iv;
	
	private MyAdapterManager manager;
	
	public final static LeaderboardFragment init() {
		LeaderboardFragment fragment = new LeaderboardFragment();
		return fragment;
	}

	@Override
	public final View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {

		mContext = super.getActivity();
		v = (View) inflater.inflate(R.layout.buzr_leaderboard, container, false);

		carousel = (ImageView)v.findViewById(R.id.imageView1);

		v.findViewById(R.id.button_left).setOnClickListener(this);
		v.findViewById(R.id.button_right).setOnClickListener(this);

		list = new LinkedList<Ranking>();
		adapter = new RankingAdapter(mContext, list);
		lv = ((PullToRefreshListView)v.findViewById(R.id.tabcontent));
		lv.setMode(Mode.PULL_FROM_END);
		lv.setAdapter(adapter);
		iv = ((ImageView)v.findViewById(R.id.empty));
		manager = new MyAdapterManager(lv, adapter, mContext);
		
		rb1 = ((RadioButton)v.findViewById(R.id.radio1));
		rb2 = ((RadioButton)v.findViewById(R.id.radio2));
		rb1.setOnCheckedChangeListener(this);
		rb2.setOnCheckedChangeListener(this);
		rb1.setChecked(true);

		return v;
	}
	
	@Override
	public final void onClick(View v) {
		manager.reset();
		switch(v.getId()) {
		case R.id.button_left:
			Log.i(TAG, "Current tab - " + count);
			count -= 1;
			if(count<0) count+=categories.length;
			Log.i(TAG, Integer.toString(count));
			carousel.setImageResource(categories[count]);
			getRankings(0, 20, count, flag);
			break;
		case R.id.button_right:
			Log.i(TAG, "Current tab - " + count);
			count += 1;	
			count %= categories.length;
			Log.i(TAG, Integer.toString(count));
			carousel.setImageResource(categories[count]);
			getRankings(0, 20, count, flag);
			break;
		}
	}

	@Override
	public final void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		list.clear();
		adapter.notifyDataSetChanged();
		manager.reset();

		if(isChecked) {
			if(buttonView == rb1) {
				flag = 0; //false
				getRankings(0, 20, count, flag);
			}

			if(buttonView == rb2) {
				flag = 1; //true
				getRankings(0, 20, count, flag);
			}			
		}
	}
	
	private final void getRankings(final int i, final int j, final int count, final int flag) {
		lv.setVisibility(View.VISIBLE);

		new API_GetRankings(mContext, new OnTaskCompleteResultListener() {

			public void onComplete(boolean success, String result) {
				JSONArray jArray;

				list.clear();

				if(success)
					try {

						jArray = new JSONArray(result.trim());
						for (int i = 0; i < jArray.length(); i++) {
							JSONObject j = jArray.getJSONObject(i);

							list.add(new Ranking (
									j.getString("user_id"),
									j.getString("username"),
									j.getString("count"),
									j.getString("image"))
									);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

				adapter.notifyDataSetChanged();

				if(list.isEmpty()) {
					lv.setVisibility(View.GONE);
					iv.setImageResource(R.drawable.no_leaderboard);
				}

			}

		}).execute("user/getRankings", Integer.toString(i), Integer.toString(j), Integer.toString(count), Integer.toString(flag));

	}

	private final class MyAdapterManager extends AdapterManager<Ranking> {
		private Context context;

		public MyAdapterManager(
				PullToRefreshListView listView, MyAdapterInterface<Ranking> adapter, Context context) {
			super(listView, adapter);

			this.context = context.getApplicationContext();
		}

		@Override
		public void launchApiCall() {

			API_GetRankings task = new API_GetRankings(context, new OnTaskCompleteResultListener() {

				@Override
				public void onComplete(boolean success, String result) {
					List<Ranking> list = new ArrayList<Ranking>();
					JSONArray jArray;

					if(success) {
						try {
							jArray = new JSONArray(result.trim());
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject j = jArray.getJSONObject(i);
								Log.i(TAG, j.toString());

								list.add(new Ranking (
										j.getString("user_id"),
										j.getString("username"),
										j.getString("count"),
										j.getString("image"))
										);
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
			task.execute("user/getRankings", Integer.toString(getStart()), Integer.toString(getPageSize()), Integer.toString(count), Integer.toString(flag));
			getNextPage();
		}

		@Override
		public void reset() {
			getFirstPage();
		}
	}	
}