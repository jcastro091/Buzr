package com.majestyk.buzr.activities;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import com.majestyk.buzr.BASE64;
import com.majestyk.buzr.R;
import com.majestyk.buzr.adapter.SearchAdapter;
import com.majestyk.buzr.apis.API_Search;
import com.majestyk.buzr.apis.OnTaskCompleteResultListener;
import com.majestyk.buzr.objects.SearchItem;

public class SearchActivity extends Activity implements OnCheckedChangeListener, TextWatcher {

	LinkedList<SearchItem> list;
	SearchAdapter adapter;
	private ListView lv;
	private EditText et;

	RadioButton rb1, rb2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		list = new LinkedList<SearchItem>();
		adapter = new SearchAdapter(this, list);
		lv = ((ListView)findViewById(R.id.tabcontent));
		et = ((EditText)findViewById(R.id.searchBar));
		lv.setAdapter(adapter);

		rb1 = ((RadioButton)findViewById(R.id.radio1));
		rb2 = ((RadioButton)findViewById(R.id.radio2));
		rb1.setOnCheckedChangeListener(this);
		rb2.setOnCheckedChangeListener(this);
		rb1.setChecked(true);

		et.addTextChangedListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		et.setText("");
		if(isChecked) {
			if(buttonView == rb1) {
				allUserApiTask("");
				lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						Intent intent = new Intent(SearchActivity.this, BUZRAction_UserProfile.class);
						intent.putExtra("user_id", list.get(arg2).userId);
						startActivity(intent);
					}
				});
			}
			if(buttonView == rb2) {
				hashTagApiTask("# ");
				lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						Intent intent = new Intent(SearchActivity.this, BUZRAction_ImageProfile.class);
						intent.putExtra("upload_id", list.get(arg2).uploadId);
						intent.putExtra("mode", "stats");
						startActivity(intent);
					}
				});
			}
		}
	}

	@Override
	public void afterTextChanged(Editable s) { }

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String str = et.getText().toString();

		if(rb1.isChecked()) {
			allUserApiTask(str);
		}
		if(rb2.isChecked()) {
			hashTagApiTask(str);
		}
	}

	protected void allUserApiTask(String str) {
		list.clear();

		new API_Search(this, false, new OnTaskCompleteResultListener() {

			@Override
			public void onComplete(boolean success, String result) {

				JSONArray jArray;

				if(success) {
					try {

						jArray = new JSONArray(result.trim());
						for (int i = 0; i < jArray.length(); i++) {
							JSONObject j = jArray.getJSONObject(i);

							list.add(new SearchItem (
									null,
									j.getString("user_id"),
									j.getString("username"),
									BASE64.decodeBase64(j.getString("description")),
									j.getString("image"))
									);

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					list.clear();
				}

				adapter.notifyDataSetChanged();

			}

		}).execute("user/getAll", str);

	}

	protected void hashTagApiTask (String str) {
		list.clear();

		new API_Search(this, true, new OnTaskCompleteResultListener() {

			@Override
			public void onComplete(boolean success, String result) {

				JSONArray jArray;

				if(success) {
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
				} else { 
					list.clear();
				}

				adapter.notifyDataSetChanged();

			}

		}).execute("upload/search", str);

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		hideSoftKeyboard(this);
	}

	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		activity.finish();
	}

}
