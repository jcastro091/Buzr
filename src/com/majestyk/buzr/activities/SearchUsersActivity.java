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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.majestyk.buzr.BASE64;
import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.R;
import com.majestyk.buzr.adapter.SearchAdapter;
import com.majestyk.buzr.apis.API_Search;
import com.majestyk.buzr.apis.OnTaskCompleteResultListener;
import com.majestyk.buzr.objects.SearchItem;

public class SearchUsersActivity extends Activity implements TextWatcher {

	View.OnClickListener ocl;
	LinkedList<SearchItem> list;
	SearchAdapter adapter;
	EditText search_bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_users);

		list = new LinkedList<SearchItem>();
		adapter = new SearchAdapter (this, list);
		ListView listView = (ListView)findViewById(R.id.listView1);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent res = new Intent().putExtra("NAME", list.get(arg2).username);
				setResult(GlobalValues.REQUEST_CODE_SEARCH_USER, res);
				finish();				
			}

		});

		search_bar = (EditText)findViewById(R.id.search_bar);
		search_bar.addTextChangedListener(this);
		
		getUsers("");

	}
	
	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		getUsers(s.toString());
	}
	
	public void getUsers(String str) {
		list.clear();

		new API_Search(this, false, new OnTaskCompleteResultListener() {

			@Override
			public void onComplete(boolean success, String result) {

				JSONArray jArray;

				if(success)
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

				adapter.notifyDataSetChanged();

			}

		}).execute("user/getAll", str);
	}
}