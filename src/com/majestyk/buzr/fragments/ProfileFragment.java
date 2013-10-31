package com.majestyk.buzr.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.majestyk.buzr.BASE64;
import com.majestyk.buzr.CameraActivity;
import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.R;
import com.majestyk.buzr.activities.Action_FriendsFollow;
import com.majestyk.buzr.adapter.ImageAdapter;
import com.majestyk.buzr.adapter.ImageGridAdapterManager;
import com.majestyk.buzr.adapter.ImageListAdapterManager;
import com.majestyk.buzr.apis.API_GetImages;
import com.majestyk.buzr.apis.API_GetProfile;
import com.majestyk.buzr.apis.API_Update;
import com.majestyk.buzr.apis.GetBitmapFromURL;
import com.majestyk.buzr.apis.GetBitmapFromURL.BitmapListener;
import com.majestyk.buzr.apis.OnTaskCompleteListener;
import com.majestyk.buzr.apis.OnTaskCompleteResultListener;
import com.majestyk.buzr.objects.JSONImage;

public class ProfileFragment extends Fragment implements OnClickListener, OnCheckedChangeListener {
	private final String TAG = getClass().getSimpleName();
	private Context mContext;
	private View v;

	private ArrayList<JSONImage> list;
	private ImageAdapter gridAdapter;
	private ImageAdapter listAdapter;
	private PullToRefreshGridView gv;
	private PullToRefreshListView lv;

	// Profile
	private TextView profUsername;
	private TextView profDescription;
	private ImageView profImage;
	private TextView profFollowers;
	private TextView profFollowing;
	private TextView profPhoto_num;
	
	// Edit Profile
	private EditText ETusername;
	private EditText ETdescription;
	private ImageView IVthumbnail;
	private LinearLayout expandable;

	private Boolean isOpen;
	private int visibility;
	private static Uri imageUri;
	private static Bitmap imageBmp;
	private static String encodedImageBmp;

	private RadioButton rb1, rb2;

	public final static ProfileFragment init() {
		ProfileFragment fragment = new ProfileFragment();
		return fragment;
	}

	@Override
	public final View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {

		mContext = super.getActivity();
		v = (View) inflater.inflate(R.layout.buzr_profile, container, false);

		imageUri = Uri.EMPTY;

		isOpen = false;

		getProfile();

		// Profile
		profUsername = (TextView)v.findViewById(R.id.prof_username);
		profDescription = (TextView)v.findViewById(R.id.prof_description);
		profImage = (ImageView)v.findViewById(R.id.prof_image);
		profFollowers = (TextView)v.findViewById(R.id.prof_followers);
		profFollowing = (TextView)v.findViewById(R.id.prof_following);
		profPhoto_num = (TextView)v.findViewById(R.id.prof_photos);

		// Edit Profile
		ETusername = (EditText)v.findViewById(R.id.textView1);
		ETdescription = (EditText)v.findViewById(R.id.textView2);
		IVthumbnail = (ImageView)v.findViewById(R.id.imageView1);
		expandable = (LinearLayout)v.findViewById(R.id.edit_profile);

		list = new ArrayList<JSONImage>();
		gridAdapter = new ImageAdapter(mContext, list, true, true);
		listAdapter = new ImageAdapter(mContext, list, false, true);
		gv = (PullToRefreshGridView)v.findViewById(R.id.gridContent);
		lv = (PullToRefreshListView)v.findViewById(R.id.listContent);
		gv.setMode(Mode.PULL_FROM_END);
		lv.setMode(Mode.PULL_FROM_END);
		gv.setAdapter(gridAdapter);
		lv.setAdapter(listAdapter);

		rb1 = ((RadioButton)v.findViewById(R.id.radio1));
		rb2 = ((RadioButton)v.findViewById(R.id.radio2));
		rb1.setOnCheckedChangeListener(this);
		rb2.setOnCheckedChangeListener(this);
		rb1.setChecked(true);

		v.findViewById(R.id.button1).setOnClickListener(this);
		v.findViewById(R.id.button2).setOnClickListener(this);
		v.findViewById(R.id.button3).setOnClickListener(this);
		v.findViewById(R.id.followers).setOnClickListener(this);
		v.findViewById(R.id.are_following).setOnClickListener(this);
		v.findViewById(R.id.imageView1).setOnClickListener(this);

		@SuppressWarnings("unused")
		MyListAdapterManager listManager = new MyListAdapterManager(lv, listAdapter, mContext);
		@SuppressWarnings("unused")
		MyGridAdapterManager gridManager = new MyGridAdapterManager(gv, gridAdapter, mContext);

		return v;
	}

	@Override
	public final void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.button1:
			translate();
			break;
		case R.id.button2:
			translate();
			update();
			break;
		case R.id.button3:
			translate();
			break;
		case R.id.imageView1:
			intent = new Intent(mContext, CameraActivity.class);
			intent.putExtra("mode", false);
			startActivityForResult(intent, GlobalValues.ACTION_REQUEST_FEATHER);
			break;
		case R.id.followers:
			intent = new Intent(mContext, Action_FriendsFollow.class);
			intent.putExtra("mode", "Followers");
			intent.putExtra("user_id", GlobalValues.getUserId());
			startActivity(intent);
			break;
		case R.id.are_following:
			intent = new Intent(mContext, Action_FriendsFollow.class);
			intent.putExtra("mode", "Following");
			intent.putExtra("user_id", GlobalValues.getUserId());
			startActivity(intent);
			break;
		}
	}

	@Override
	public final void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		list.clear();
		listAdapter.notifyDataSetChanged();
		gridAdapter.notifyDataSetChanged();

		if(isChecked) {
			if(buttonView == rb1) {
				gv.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
				getProfileImages();
			}
			if(buttonView == rb2) {
				lv.setVisibility(View.VISIBLE);
				gv.setVisibility(View.GONE);
				getProfileImages();
			}
		}
	}
	
	@Override
	public final void onResume() {
		getProfile();
		getProfileImages();
		super.onResume();
	}
	
	private final void getProfile() {

		new API_GetProfile(mContext, new OnTaskCompleteResultListener() {

			public void onComplete(boolean success, String result) {
				JSONObject jObject;

				if(success)
					try {
						jObject = new JSONObject(result.trim());
						Log.i(TAG, jObject.toString());

						String username = jObject.getString("username");
						String description = BASE64.decodeBase64(jObject.getString("description"));
						String followers = jObject.getString("followers");
						String following = jObject.getString("following");
						String uploads = jObject.getString("uploads");
						String profile_image = jObject.getString("profile_image");

						profUsername.setText(username);
						profDescription.setText(description);
						ETusername.setText(username);
						ETdescription.setText(description);

						profFollowers.setText(followers);
						profFollowing.setText(following);
						profPhoto_num.setText(uploads);

						new GetBitmapFromURL(profile_image, new BitmapListener() {
							@Override
							public void onComplete(Bitmap result) {
								profImage.setImageBitmap(result);
							}
						}).execute();
					} catch (JSONException e) {
						e.printStackTrace();
					}
			}

		}).execute("user/get", GlobalValues.getUserId());
	}

	private final void getProfileImages() {

		new API_GetImages(mContext, new OnTaskCompleteResultListener() {

			public void onComplete(boolean success, String result) {
				JSONArray jArray;
				JSONObject jObject;

				list.clear();

				if(success)
					try {
						jArray = new JSONArray(result.trim());

						for(int i = 0; i < jArray.length(); i++) {
							jObject = jArray.getJSONObject(i);
							Log.i(TAG, jObject.toString());

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

		}).execute("upload/getAll", GlobalValues.getUserId(), Integer.toString(0), Integer.toString(18));

	}

	private final void update() {

		String username = ETusername.getText().toString();
		String description = ETdescription.getText().toString();

		new API_Update(mContext,
				encodedImageBmp,
				BASE64.encodeBase64(description),
				username,
				new OnTaskCompleteListener() {
			public void onComplete(boolean success) {
				if(success) {
					getProfile();
					profImage.setImageResource(R.drawable.empty_image_080);
				}
				
			}
		}).execute("user/update");

	}

	@Override
	public final void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		imageBmp = null;
		encodedImageBmp = "";
		if(resultCode != Activity.RESULT_CANCELED) {
			if(requestCode == GlobalValues.ACTION_REQUEST_FEATHER) {
				if (data.hasExtra("URI")) {
					imageUri = (Uri)data.getParcelableExtra("URI");
					if ((new File(GlobalValues.getRealPathFromURI(mContext, imageUri))).length() > 1) {
						Log.i(TAG, "Creating imageView: " + imageUri);

						try {
							imageBmp = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imageUri);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (OutOfMemoryError e) {
							e.printStackTrace();
							System.gc();
						}

						Bitmap bmThumbnail = ThumbnailUtils.extractThumbnail(imageBmp, 192, 192);
						IVthumbnail.setImageBitmap(bmThumbnail);
						encodedImageBmp = GlobalValues.BitMapToString(imageBmp);
					}
				}
			}
		}
	}

	private final void translate() {

		if(isOpen) {
			isOpen = false;			
			visibility = View.GONE;
		}
		else {
			isOpen = true;
			visibility = View.VISIBLE;
		}

		expandable.setVisibility(visibility);

	}

	private final class MyListAdapterManager extends ImageListAdapterManager<JSONImage> {
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
								Log.i(TAG, jObject.toString());

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
			task.execute("upload/getAll", GlobalValues.getUserId(), Integer.toString(getStart()), Integer.toString(getPageSize()));
			getNextPage();
		}
	}

	private final class MyGridAdapterManager extends ImageGridAdapterManager<JSONImage> {
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
								Log.i(TAG, jObject.toString());

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
			task.execute("upload/getAll", GlobalValues.getUserId(), Integer.toString(getStart()), Integer.toString(getPageSize()));
			getNextPage();
		}
	}
}