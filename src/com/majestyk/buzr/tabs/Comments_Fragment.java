package com.majestyk.buzr.tabs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.R;
import com.majestyk.buzr.activities.SearchUsersActivity;
import com.majestyk.buzr.apis.API_Comment;
import com.majestyk.buzr.apis.API_GetUpload;
import com.majestyk.buzr.apis.API_RemoveComment;
import com.majestyk.buzr.apis.OnTaskCompleteListener;
import com.majestyk.buzr.apis.OnTaskCompleteResultListener;
import com.majestyk.buzr.apis.OnTaskListener;

public class Comments_Fragment extends Fragment implements OnClickListener, OnTaskListener, TextWatcher {

	private Bundle mArguments;
	private bsAdapter adapter;
	private LinkedList<Comment> list;
	private String upload_id;

	private EditText et;
	private ImageView iv;
	private ListView listView;

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View V = inflater.inflate(R.layout.fragment_comments, container, false);

		mArguments = savedInstanceState;
		String result = getArguments().getString("result");
		upload_id = getArguments().getString("upload_id");

		et = (EditText)V.findViewById(R.id.editText1);
		iv = (ImageView)V.findViewById(R.id.imageView1);
		listView = (ListView)V.findViewById(R.id.listview);

		et.addTextChangedListener(this);

		iv.setOnClickListener(this);

		list = new LinkedList<Comment>();
		adapter = new bsAdapter(this, list);
		listView.setAdapter(adapter);

		if (!result.equals("")) {
			JSONObject jObject;
			try {
				jObject = new JSONObject(result.trim());
				System.out.println(jObject);

				JSONArray jArray = jObject.getJSONArray("comments");
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject j = jArray.getJSONObject(i);
					list.add(new Comment(
							j.getString("comment_id"),
							j.getString("timestamp"),
							j.getString("username"),
							j.getString("text")));
				}

				adapter.notifyDataSetChanged();

				listView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setMessage("Are you sure you want to remove comment?")
						.setCancelable(true)
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								new API_RemoveComment(getActivity(), new OnTaskCompleteListener() {
									@Override
									public void onComplete(boolean success) {
										if(success) {
											getComments();
										}
									}
								}).execute("upload/removeComment", list.get(arg2).getCommentId());
							}
						})
						.setNegativeButton("No", null);
						builder.create().show();
						return false;
					}

				});

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return V;
	}

	@Override
	public final void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageView1:
			String comment = et.getText().toString();
			if (comment.length()!=0) {
				new API_Comment (getActivity(), this, new OnTaskCompleteListener() {
					@Override
					public void onComplete(boolean success) {
						et.setText("");
						GlobalValues.hideSoftKeyboard(getActivity());
						getComments();
					}
				}).execute("upload/comment", getArguments().getString("upload_id"), comment);
			}
			break;
		}
	}

	@Override
	public final void onTaskStart() {
		iv.setOnClickListener(null);
	}

	@Override
	public final void onTaskStop() {
		iv.setOnClickListener(this);
	}

	private final class bsAdapter extends BaseAdapter {
		Fragment cntx;
		LinkedList<Comment> array;

		public bsAdapter(Fragment context, LinkedList<Comment> arr) {
			this.cntx=context;
			this.array = arr;
		}

		public int getCount() {
			return array.size();
		}

		public Object getItem(int position) {
			return array.get(position);
		}

		public long getItemId(int position) {
			return array.size();
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			View v = null;
			LayoutInflater inflater=cntx.getLayoutInflater(mArguments);
			v = inflater.inflate(R.layout.comment, null);

			TextView comment_txt = (TextView)v.findViewById(R.id.comment_text);

			String str1 = array.get(position).getUsername();
			String str2 = formatTimestamp(array.get(position).getTimestamp());
			String str3 = array.get(position).getText();
			String str_txt = str1 + " - " + str2 + " " + str3;

			Spannable s = new SpannableString(str_txt);
			s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.buzr_orange)), 0, str_txt.indexOf(str3), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			comment_txt.setText(s);

			return v;
		}
	}

	private final class Comment {
		private String comment_id;
		private String timestamp;
		private String username;
		private String text;

		public Comment(String s1, String s2, String s3, String s4) {
			this.comment_id = s1;
			this.timestamp = s2;
			this.username = s3;
			this.text = s4;			
		}

		public String getCommentId() {
			return comment_id;
		}

		public String getTimestamp() {
			return timestamp;
		}

		public String getUsername() {
			return username;
		}

		public String getText() {
			return text;
		}

		public String toString() {
			return comment_id + ": " + username + " - " + timestamp + " " + text;
		}

	}

	private final String formatTimestamp (String old_timestamp) {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		inputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

		Date new_timestamp = new Date();

		try {
			new_timestamp = inputFormat.parse(old_timestamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return outputFormat.format(new_timestamp);
	}

	private final void getComments() {
		list.clear();
		new API_GetUpload(getActivity(), new OnTaskCompleteResultListener() {

			public void onComplete(boolean success, String result) {
				JSONObject jObject;

				if(success) {
					try {
						jObject = new JSONObject(result.trim());
						System.out.println(jObject);

						JSONArray jArray = jObject.getJSONArray("comments");
						for (int i = 0; i < jArray.length(); i++) {
							JSONObject j = jArray.getJSONObject(i);
							list.add(new Comment(
									j.getString("comment_id"),
									j.getString("timestamp"),
									j.getString("username"),
									j.getString("text")));
						}

						adapter.notifyDataSetChanged();

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

		}).execute("upload/get", upload_id);

	}

	@Override
	public final void afterTextChanged(Editable s) {
		//		if(s.toString().contains(cs))
	}

	@Override
	public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public final void onTextChanged(CharSequence s, int start, int before, int count) {
		if(s.length() > 0) {
			String testString = s.toString().substring(start, count + start);
			if(testString.equals("@") || testString.endsWith("@")) {
				Intent intent = new Intent (getActivity(), SearchUsersActivity.class);
				startActivityForResult(intent, GlobalValues.REQUEST_CODE_SEARCH_USER);
			}
		}
	}

	@Override
	public final void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != 0) {
			switch(requestCode) {
			case (GlobalValues.REQUEST_CODE_SEARCH_USER):
				et.append((String)data.getStringExtra("NAME"));
			break;
			}
		}
	}

}
