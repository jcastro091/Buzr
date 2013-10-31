package com.majestyk.buzr.fragments;

import it.sephiroth.android.library.widget.HListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.polidea.webimageview.WebImageView;
import us.feras.ecogallery.EcoGallery;
import us.feras.ecogallery.EcoGalleryAdapterView;
import us.feras.ecogallery.EcoGalleryAdapterView.OnItemClickListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.majestyk.buzr.BASE64;
import com.majestyk.buzr.Emojis;
import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.R;
import com.majestyk.buzr.activities.BUZRAction_ImageProfile;
import com.majestyk.buzr.activities.BUZRAction_UserProfile;
import com.majestyk.buzr.adapter.EmojiAdapter;
import com.majestyk.buzr.adapter.NotificationAdapter;
import com.majestyk.buzr.apis.API_GetProfile;
import com.majestyk.buzr.apis.API_GetRandom;
import com.majestyk.buzr.apis.API_GetSettings;
import com.majestyk.buzr.apis.API_Notifications;
import com.majestyk.buzr.apis.API_Rank;
import com.majestyk.buzr.apis.API_Submit;
import com.majestyk.buzr.apis.API_Suggested;
import com.majestyk.buzr.apis.OnTaskCompleteListener;
import com.majestyk.buzr.apis.OnTaskCompleteResultListener;
import com.majestyk.buzr.objects.Image_Random;
import com.majestyk.buzr.objects.Image_Suggested;
import com.majestyk.buzr.objects.NotificationItem;
import com.majestyk.buzr.social.FacebookUtils;
import com.majestyk.buzr.social.TwitterUtils;

public class HomeFragment extends Fragment implements OnClickListener, OnItemClickListener {
	private final String TAG = getClass().getSimpleName();
	private Context mContext;
	private View v;

	public final static String ARG_IMAGE_URL = "imageURL";
	public final static String ARG_IMAGE_BMP = "imageBMP";
	public final static String ARG_IMAGE_DES = "descript";
	public final static String ARG_IMAGE_LOC = "location";
	public final static String ARG_SHARE_FB = "fACEbOOK";
	public final static String ARG_SHARE_TW = "tWITTER";
	public final static String ARG_SHARE_TU = "tUMBLR";

	private boolean share_facebook, share_twitter, share_tumblr;

	private String user_id, upload_id;
	private EcoGallery emojiGallery;
	private LinkedList<Image_Random> list; 

	private long current_time = System.nanoTime();
	private boolean click = false;
	private int count = 0;

	// Image
	private WebImageView image;

	private RelativeLayout layout;
	private TextView tvUsername;
	private TextView tvDescription;
	private TextView tvLocation;
	private TextView tvTimestamp;
	private TextView tvBuzrTotal;
	private TextView tvCommentTotal;
	private WebImageView ivProfileImage;

	// Explore
	private RelativeLayout explore;
	private WebImageView iv1, iv2, iv3, iv4, iv5, iv6;
	private ImageButton button1, button2;
	private LinkedList<Image_Suggested> exploreList;

	// Upload
	private RelativeLayout upload;
	private WebImageView upload_image;
	private TextView upload_message;
	private ImageButton upload_button, upload_retry;
	private String imageURI, imageBMP, descript, location;

	// Notifications
	private HListView lvNotifications;
	private ImageView ivEmptyNotifiations;

	public final static HomeFragment init(
			String imageURL, String imageBMP, String imageDescript, String imageLocation,
			Boolean shareFacebook, Boolean shareTwitter, Boolean shareTumblr) {
		HomeFragment fragment = new HomeFragment();

		Bundle bundle = new Bundle();
		bundle.putString(ARG_IMAGE_URL, imageURL);
		bundle.putString(ARG_IMAGE_BMP, imageBMP);
		bundle.putString(ARG_IMAGE_DES, imageDescript);
		bundle.putString(ARG_IMAGE_LOC, imageLocation);
		bundle.putBoolean(ARG_SHARE_FB, shareFacebook);
		bundle.putBoolean(ARG_SHARE_TW, shareTwitter);
		bundle.putBoolean(ARG_SHARE_TU, shareTumblr);

		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		Bundle args = getArguments();

		imageURI = args.getString(ARG_IMAGE_URL);
		imageBMP = args.getString(ARG_IMAGE_BMP);
		descript = args.getString(ARG_IMAGE_DES);
		location = args.getString(ARG_IMAGE_LOC);
		share_facebook = args.getBoolean(ARG_SHARE_FB);
		share_twitter = args.getBoolean(ARG_SHARE_TW);
		share_tumblr = args.getBoolean(ARG_SHARE_TU);

		super.onCreate(savedInstanceState);
	}

	@SuppressWarnings("deprecation")
	@Override
	public final View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {

		mContext = super.getActivity();
		v = (View) inflater.inflate(R.layout.activity_home, container, false);

		user_id = new String();
		upload_id = new String();

		list = new LinkedList<Image_Random>();
		exploreList = new LinkedList<Image_Suggested>();

		emojiGallery = (EcoGallery)v.findViewById(R.id.emoji_gallery);
		emojiGallery.setAdapter(new EmojiAdapter(mContext));
		emojiGallery.setSelection((Integer.MAX_VALUE / 2)-3);
		emojiGallery.setUnselectedAlpha(0.90f);

		emojiGallery.setOnItemClickListener(this);

		setUpUIElements();

		Display display = getActivity().getWindowManager().getDefaultDisplay();
		int screenWidth = display.getWidth();
		LayoutParams params = image.getLayoutParams();

		params.width = screenWidth;
		params.height = screenWidth;
		image.setLayoutParams(params);

		image.setOnClickListener(this);
		tvBuzrTotal.setOnClickListener(this);
		tvCommentTotal.setOnClickListener(this);

		animateImageLoader();

		if (GlobalValues.MODE_TUTORIAL) {
			showTutorial();
		} else if (GlobalValues.MODE_SUBMIT) {
			getUpload();
		}

		getRandom();
		getNotifications();
		getSettings();

		return v;
	}

	@Override
	public final void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.image:
			intent = new Intent(mContext, BUZRAction_ImageProfile.class);
			intent.putExtra("upload_id", upload_id);
			intent.putExtra("mode", "stats");
			startActivity(intent);
			break;
		case R.id.user:
			intent = new Intent(mContext, BUZRAction_UserProfile.class);
			intent.putExtra("user_id", user_id);
			startActivity(intent);
			break;
		case R.id.buzr_total:
			intent = new Intent(mContext, BUZRAction_ImageProfile.class);
			intent.putExtra("upload_id", upload_id);
			intent.putExtra("mode", "stats");
			startActivity(intent);
			break;
		case R.id.comment_total:
			intent = new Intent(mContext, BUZRAction_ImageProfile.class);
			intent.putExtra("upload_id", upload_id);
			intent.putExtra("mode", "comments");
			startActivity(intent);
			break;
		case R.id.WebImageView1:
			intent = new Intent(mContext, BUZRAction_ImageProfile.class);
			intent.putExtra("upload_id", exploreList.get(0).upload_id);
			intent.putExtra("mode", "stats");
			startActivity(intent);
			break;
		case R.id.WebImageView2:
			intent = new Intent(mContext, BUZRAction_ImageProfile.class);
			intent.putExtra("upload_id", exploreList.get(1).upload_id);
			intent.putExtra("mode", "stats");
			startActivity(intent);
			break;
		case R.id.WebImageView3:
			intent = new Intent(mContext, BUZRAction_ImageProfile.class);
			intent.putExtra("upload_id", exploreList.get(2).upload_id);
			intent.putExtra("mode", "stats");
			startActivity(intent);
			break;
		case R.id.WebImageView4:
			intent = new Intent(mContext, BUZRAction_ImageProfile.class);
			intent.putExtra("upload_id", exploreList.get(3).upload_id);
			intent.putExtra("mode", "stats");
			startActivity(intent);
			break;
		case R.id.WebImageView5:
			intent = new Intent(mContext, BUZRAction_ImageProfile.class);
			intent.putExtra("upload_id", exploreList.get(4).upload_id);
			intent.putExtra("mode", "stats");
			startActivity(intent);
			break;
		case R.id.WebImageView6:
			intent = new Intent(mContext, BUZRAction_ImageProfile.class);
			intent.putExtra("upload_id", exploreList.get(5).upload_id);
			intent.putExtra("mode", "stats");
			startActivity(intent);
			break;
		case R.id.explore_refresh:
			refreshExplore();
			break;
		case R.id.explore_close:
			closeExplore();
			break;
		case R.id.explore:
			break;
		case R.id.tutorial:
			break;
		case R.id.upload_retry:
			submitUpload();
		case R.id.upload_button:
			hideUpload();
			break;
		}
	}

	@Override
	public final void onItemClick(EcoGalleryAdapterView<?> parent, View view,	int position, long id) {
		if (click) {
			Log.i(TAG, "2nd click");
			Log.i(TAG, Long.toString(System.nanoTime() - current_time));
			if ((System.nanoTime() - current_time) <= 250000000) {
				getEmoji(EmojiAdapter.getRealPosition(position+1));
				if(!GlobalValues.MODE_TUTORIAL) {
					new API_Rank(mContext, new OnTaskCompleteListener() {
						@Override
						public void onComplete(boolean success) {
							if(success) {
								list.remove(0);
								getRandom();
							}
						}
					}).execute("rating/add", upload_id, Integer.toString(EmojiAdapter.getRealPosition(position)));
				} else {
					hideTutorial();
				}
			} else {
				current_time = System.nanoTime();
			}
		} else {
			Log.i(TAG, "1st click");
			current_time = System.nanoTime();
			click = true;
		}
	}

	private final void setUpUIElements() {
		// Image
		image		   = (WebImageView)v.findViewById(R.id.image);
		layout		   = (RelativeLayout)v.findViewById(R.id.user);
		tvUsername	   = ((TextView)v.findViewById(R.id.username));
		tvDescription  = ((TextView)v.findViewById(R.id.description));
		tvLocation	   = ((TextView)v.findViewById(R.id.location));
		tvTimestamp	   = ((TextView)v.findViewById(R.id.timestamp));
		tvBuzrTotal	   = ((TextView)v.findViewById(R.id.buzr_total));
		tvCommentTotal = ((TextView)v.findViewById(R.id.comment_total));
		ivProfileImage = (WebImageView)v.findViewById(R.id.profile_image);

		// Explore
		iv1	= (WebImageView)v.findViewById(R.id.WebImageView1);
		iv2 = (WebImageView)v.findViewById(R.id.WebImageView2);
		iv3 = (WebImageView)v.findViewById(R.id.WebImageView3);
		iv4 = (WebImageView)v.findViewById(R.id.WebImageView4);
		iv5 = (WebImageView)v.findViewById(R.id.WebImageView5);
		iv6 = (WebImageView)v.findViewById(R.id.WebImageView6);

		button1 = (ImageButton)v.findViewById(R.id.explore_close);
		button2 = (ImageButton)v.findViewById(R.id.explore_refresh);

		// Upload
		upload = (RelativeLayout)v.findViewById(R.id.upload);
		upload_image   = (WebImageView)v.findViewById(R.id.upload_image);
		upload_message = (TextView)v.findViewById(R.id.upload_message);
		upload_retry   = (ImageButton)v.findViewById(R.id.upload_retry);
		upload_button  = (ImageButton)v.findViewById(R.id.upload_button);

		// Notifications
		lvNotifications = (HListView)v.findViewById(R.id.Notifications);
		ivEmptyNotifiations = (ImageView)v.findViewById(R.id.empty_notifications);
	}

	@Override
	public final void onResume() {
		Log.i(TAG, "onResume");
		if(!list.isEmpty())
			if(GlobalValues.upload_id == list.get(0).upload_id) {
				list.remove(0);
				getRandom();
			}
		super.onResume();
	}

	private final void getRandom() {
		click = false;

		if (list.size() < 3) {
			String exclude = ",";

			for (Image_Random img : list) {
				exclude += img.upload_id + ",";
			}
			exclude = exclude.substring(0, exclude.lastIndexOf(","));

			getRandomImages(exclude);			
		} else if (list.size() >= 3) {
			getImage();
		}

	}

	@SuppressWarnings("unused")
	private final void getRandomImages() {
		getRandomImages("");
	}

	private final void getRandomImages(String exclude) {
		image.setImageResource(R.drawable.empty_image_640);
		ivProfileImage.setImageResource(R.drawable.empty_image_080);
		new API_GetRandom(mContext, new OnTaskCompleteResultListener() {
			public void onComplete(boolean success, String result) {
				JSONArray jArray;
				JSONObject jObject;

				if (success)
					try {
						jArray = new JSONArray(result.trim());

						for (int i = 0; i < jArray.length(); i++) {
							jObject = jArray.getJSONObject(i);
							Log.i(TAG, jObject.toString());

							if (jObject.has("suggested")) {
								JSONArray jArray2 = jObject.getJSONArray("suggested");
								exploreList.clear();

								list.add(new Image_Random(
										"",
										"",
										"",
										"",
										"",
										"",
										"",
										"",
										"",
										"",
										1));

								for (int j = 0; j < jArray2.length(); j++) {
									JSONObject jObject2 = jArray2.getJSONObject(j);
									Log.i(TAG, jObject2.toString());

									getExploreImages(jObject2);

								}

							} else {
								list.add(new Image_Random(
										jObject.getString("user_id"),
										jObject.getString("upload_id"),
										jObject.getString("username"),
										BASE64.decodeBase64(jObject.getString("description")),
										jObject.getString("location"),
										jObject.getString("timestamp"),
										jObject.getString("total"),
										jObject.getString("comments"),
										jObject.getString("image"),
										jObject.getString("profile_image"),
										0));
							}

							getImage();

						}
						
						count += jArray.length();
						Log.i(TAG, "Count = " + count);

					} catch (JSONException e) {
						e.printStackTrace();
						
						if (list.size() == 0) {
							getExplore();
							refreshExplore();
						}

					}

			}
		}, exclude).execute("upload/getRandom", Integer.toString(count));
	}

	private final void getImage() {
		Image_Random IMG = list.get(0);
		image.setImageResource(R.drawable.empty_image_640);
		ivProfileImage.setImageResource(R.drawable.empty_image_080);

		Log.i(TAG, "Suggested: " + IMG.suggested);
		if(IMG.suggested > 0) {
			getExplore();
		} else {
			layout.setVisibility(View.VISIBLE);

			user_id = IMG.user_id;
			upload_id = IMG.upload_id;

			v.findViewById(R.id.user).setOnClickListener(this);
			tvUsername.setText(IMG.username + " - ");
			tvDescription.setText(IMG.description);

			if(!IMG.location.equals("")) {
				tvLocation.setVisibility(View.VISIBLE);
				tvLocation.setText(IMG.location);
			} else {
				tvLocation.setVisibility(View.INVISIBLE);
			}

			tvTimestamp.setText(GlobalValues.formatTimestamp(IMG.timestamp));
			tvBuzrTotal.setText(IMG.buzr_total);
			tvCommentTotal.setText(IMG.comment_total);

			image.setImageURL(IMG.image);
			ivProfileImage.setImageURL(IMG.profile_image);
		}
	}

	private final void getNotifications() {

		new API_Notifications(mContext, new OnTaskCompleteResultListener() {
			public void onComplete(boolean success, String result) {
				JSONArray jArray;
				ArrayList<NotificationItem> list = new ArrayList<NotificationItem>();

				if(success)
					try {
						jArray = new JSONArray(result.trim());
						for(int i = 0; i < jArray.length(); i++) {
							JSONObject jObject = jArray.getJSONObject(i);
							Log.i(TAG, jObject.toString());

							String scenario = jObject.getString("scenario");
							String user_id = jObject.getString("user_id");
							String username = jObject.getString("username");
							String event = jObject.getString("event");
							String timestamp = GlobalValues.formatTimestamp(jObject.getString("timestamp"));
							String target = jObject.getString("target");
							String emoji = jObject.getString("emoji");
							String upload_id = jObject.getString("upload_id");
							String user = jObject.getString("user");

							list.add(0, new NotificationItem(
									scenario,
									user_id,
									username,
									event,
									timestamp,
									target,
									emoji,
									upload_id,
									user
									));

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

				if(list.isEmpty()) {
					showEmptyNotifications();
				} else {
					hideEmptyNotifications();
					NotificationAdapter adapter = new NotificationAdapter(mContext, list);
					lvNotifications.setAdapter(adapter);
					lvNotifications.setPadding(5, 20, 5, 20);

					lvNotifications.setSelection(list.size()-1);
				}

			}
		}).execute("user/newsFeed", "0", "10");		
	}

	private final void showEmptyNotifications() {
		ivEmptyNotifiations.setVisibility(View.VISIBLE);
	}

	private final void hideEmptyNotifications() {
		ivEmptyNotifiations.setVisibility(View.GONE);
	}

	private final void getEmoji(int i) {

		final ImageView iv = (ImageView)v.findViewById(R.id.emoji);

		final Integer[] pics = Emojis.getEmojis();

		iv.setImageDrawable(getResources().getDrawable(pics[EmojiAdapter.getRealPosition(i+pics.length-1)]));

		Animation expand = null; 

		expand = AnimationUtils.loadAnimation(mContext, R.anim.expand_from_center);

		expand.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				iv.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) { }

			@Override
			public void onAnimationStart(Animation animation) {
				iv.setVisibility(View.VISIBLE);
			}

		});

		iv.startAnimation(expand);

	}

	private final void getSettings() {
		new API_GetSettings(mContext, new OnTaskCompleteResultListener() {
			public void onComplete(boolean success, String result) {
				JSONObject jObject;

				if(success)
					try {
						jObject = new JSONObject(result.trim());
						Log.i(TAG, jObject.toString());

						SharedPreferences settings = mContext.getSharedPreferences(GlobalValues.PREFS, 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString("PRIVACY", (String)jObject.get("privacy"));
						editor.putString("SHARING", (String)jObject.get("sharing"));
						editor.putString("CAMERA_ROLL", (String)jObject.get("cameraroll"));
						editor.putString("NOTIFICATIONS", (String)jObject.get("notifications"));
						editor.commit();
					} catch (JSONException e) {
						e.printStackTrace();
					}

			}
		}).execute("user/getSettings");

	}

	private final void getExplore() {
		emojiGallery.setOnItemClickListener(null);

		explore = (RelativeLayout)v.findViewById(R.id.explore);
		explore.setVisibility(View.VISIBLE);
		explore.setOnClickListener(this);

		iv1.setImageURL(exploreList.get(0).image);
		iv2.setImageURL(exploreList.get(1).image);
		iv3.setImageURL(exploreList.get(2).image);
		iv4.setImageURL(exploreList.get(3).image);
		iv5.setImageURL(exploreList.get(4).image);
		iv6.setImageURL(exploreList.get(5).image);

		button1.setOnClickListener(this);
		button2.setOnClickListener(this);

		iv1.setOnClickListener(this);
		iv2.setOnClickListener(this);
		iv3.setOnClickListener(this);
		iv4.setOnClickListener(this);
		iv5.setOnClickListener(this);
		iv6.setOnClickListener(this);
	}

	private final void closeExplore() {
		emojiGallery.setOnItemClickListener(this);

		explore = (RelativeLayout)v.findViewById(R.id.explore);
		explore.setVisibility(View.GONE);
		explore.setOnClickListener(null);

		exploreList.clear();
		list.remove(0);
		getRandom();
	}

	private final void getExploreImages(JSONObject jObject) {
		try {
			JSONObject jObject2 = jObject.getJSONArray("uploads").getJSONObject(0);

			exploreList.add(new Image_Suggested(
					jObject.getString("user_id"),
					jObject2.getString("upload_id"),
					jObject2.getString("username"),
					jObject2.getString("description"),
					jObject2.getString("location"),
					jObject2.getString("image")));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private final void refreshExplore() {

		exploreList.clear();

		new API_Suggested(mContext, new OnTaskCompleteResultListener() {

			@Override
			public void onComplete(boolean success, String result) {
				JSONArray jArray;
				JSONObject jObject;

				if(success)
					try {
						jObject = new JSONObject(result.trim());
						jArray = jObject.getJSONArray("users");
						Log.i(TAG, jObject.toString());

						for(int i = 0; i < jArray.length(); i++) {
							try {
								JSONObject userObject = jArray.getJSONObject(i);
								Log.i(TAG, userObject.toString());

								getExploreImages(userObject);
							} catch(JSONException e) {
								e.printStackTrace();
							}
						}

						iv1.setImageURL(exploreList.get(0).image);
						iv2.setImageURL(exploreList.get(1).image);
						iv3.setImageURL(exploreList.get(2).image);
						iv4.setImageURL(exploreList.get(3).image);
						iv5.setImageURL(exploreList.get(4).image);
						iv6.setImageURL(exploreList.get(5).image);

					} catch (JSONException e) {
						e.printStackTrace();
					}

			}

		}).execute("suggested/get", "3", "0", "6");

	}

	private final void getUpload() {
		showUpload();

		upload_image.setImageURI(Uri.parse(imageURI));

		submitUpload();

		upload_button.setOnClickListener(this);
		upload_retry.setOnClickListener(this);
	}

	private final void hideUpload() {
		GlobalValues.MODE_SUBMIT = false;
		upload.setVisibility(View.GONE);
	}

	private final void showUpload() {
		GlobalValues.MODE_SUBMIT = true;
		upload.setVisibility(View.VISIBLE);
	}

	private final void submitUpload() {
		upload_message.setText("Uploading photo...");
		new API_Submit(mContext, imageBMP, BASE64.encodeBase64(descript), location, upload, 
				new OnTaskCompleteResultListener() {
			public void onComplete(boolean success, String result) {
				if(success) {
					upload_message.setText("Uploading complete");
					upload_button.setBackgroundResource(R.drawable.upload_button_check);
					upload_retry.setVisibility(View.GONE);

					String upload_id = "";
					try {
						JSONObject jObject = new JSONObject(result.trim());
						JSONObject  upload = jObject.getJSONObject("upload");
						upload_id = upload.getString("upload_id");
					} catch (JSONException e) {
						e.printStackTrace();
					}

					getProfile(upload_id);

					hideUpload();

					// Share to Facebook
					if (share_facebook) { 
						new Thread(new Runnable() {
							@Override
							public void run() {
								FacebookUtils.FacebookShareTask((Activity)mContext, FacebookUtils.getFacebookSession(),
										new File(GlobalValues.getRealPathFromURI(mContext, Uri.parse(imageURI))), descript);
							}
						}).start();
					}

					// Share to Twitter
					if (share_twitter) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								TwitterUtils.TwitterShareTask(TwitterUtils.getTwitterConsumer(),
										new File(GlobalValues.getRealPathFromURI(mContext, Uri.parse(imageURI))), descript);					
							}
						}).start();
					}

				} else {
					upload_message.setText("Uploading failed!");
					upload_button.setBackgroundResource(R.drawable.upload_button_cancel);
					upload_retry.setVisibility(View.VISIBLE);
				}
			}
		}).execute("upload/create");
	}

	private final void getProfile(final String upload_id) {

		new API_GetProfile(mContext, new OnTaskCompleteResultListener() {

			public void onComplete(boolean success, String result) {
				JSONObject jObject;

				if(success)
					try {
						jObject = new JSONObject(result.trim());
						Log.i(TAG, jObject.toString());

						list.addFirst(new Image_Random(
								GlobalValues.getUserId(),
								upload_id,
								jObject.getString("username"),
								descript, location,
								GlobalValues.formatTimestamp(new Date().toString()),
								"0", "0", imageURI,
								jObject.getString("profile_image"), 0));

					} catch (JSONException e) {
						e.printStackTrace();
					}

				getImage();
				image.setImageURI(Uri.parse(imageURI));
			}

		}).execute("user/get", GlobalValues.getUserId());
	}

	private final void animateImageLoader() {
		ImageView spinner = (ImageView)v.findViewById(R.id.loader);
		Animation rotate = null;
		rotate = AnimationUtils.loadAnimation(mContext, R.anim.loading);
		spinner.startAnimation(rotate);
	}

	private final void showTutorial() {
		v.findViewById(R.id.tutorial).setVisibility(View.VISIBLE);
		v.findViewById(R.id.tutorial).setOnClickListener(this);
		showEmptyNotifications();
	}

	private final void hideTutorial() {
		v.findViewById(R.id.tutorial).setVisibility(View.GONE);
		hideEmptyNotifications();
		GlobalValues.MODE_TUTORIAL = false;
	}

}
