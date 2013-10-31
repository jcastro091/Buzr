package com.majestyk.buzr.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.R;

public class CreateOrFindLocations extends Activity implements OnClickListener, TextWatcher {

	private EditText search;
	private TextView tv1, tv2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_or_find_locations);
		
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		RelativeLayout option1 = (RelativeLayout)findViewById(R.id.option1);
		RelativeLayout option2 = (RelativeLayout)findViewById(R.id.option2);
		option1.setOnClickListener(this);
		option2.setOnClickListener(this);

		tv1 = (TextView)findViewById(R.id.textView1);
		tv2 = (TextView)findViewById(R.id.textView2);

		search = (EditText)findViewById(R.id.search_bar);
		search.addTextChangedListener(this);
		
		String str = search.getText().toString();
		tv1.setText("Create ''" + str + "''");
		tv2.setText("Find ''" + str + "''");
	}

	@Override
	public void afterTextChanged(Editable s) { }

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String str = search.getText().toString();
		tv1.setText("Create ''" + str + "''");
		tv2.setText("Find ''" + str + "''");
	}

	@Override
	public void onClick(View arg0) {
		Intent res = new Intent();
		switch(arg0.getId()) {
		case R.id.option1:
			res = new Intent()
			.putExtra("OPTION", "1")
			.putExtra("TEXT", search.getText().toString());
			setResult(GlobalValues.REQUEST_CODE_SEARCH_LOCATION + 1, res);
			finish();
			break;
		case R.id.option2:
			res = new Intent()
			.putExtra("OPTION", "2")
			.putExtra("TEXT", search.getText().toString());
			setResult(GlobalValues.REQUEST_CODE_SEARCH_LOCATION + 1, res);
			finish();
			break;
		}
	}
}