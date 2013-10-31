package com.majestyk.buzr.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import pl.polidea.webimageview.WebImageView;
import us.feras.ecogallery.EcoGallery;
import us.feras.ecogallery.EcoGalleryAdapterView;
import us.feras.ecogallery.EcoGalleryAdapterView.OnItemClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.majestyk.buzr.BASE64;
import com.majestyk.buzr.Emojis;
import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.R;
import com.majestyk.buzr.adapter.EmojiAdapter;
import com.majestyk.buzr.apis.API_DeletePost;
import com.majestyk.buzr.apis.API_FlagPost;
import com.majestyk.buzr.apis.API_GetUpload;
import com.majestyk.buzr.apis.API_Rank;
import com.majestyk.buzr.apis.OnTaskCompleteListener;
import com.majestyk.buzr.apis.OnTaskCompleteResultListener;
import com.majestyk.buzr.tabs.Comments_Fragment;
import com.majestyk.buzr.tabs.Stats_Fragment;

@SuppressWarnings("deprecation")
public class BUZRAction_ImageProfile extends FragmentActivity implements OnClickListener, OnItemClickListener {

	private SlidingDrawer menuDrawer;
	private String json = "";
	private String upload_id;
	private String user_id;
	private String rating;
	private Uri imageUri;

	private Button stats, comments;
	private Button more, rate, profile;
	private Button flag, share, cancel;

	private EcoGallery emojiGallery;
	long current_time = System.nanoTime();
	boolean click = false;

	final Integer[] emojis = Emojis.getEmojis();
	final Integer[] pics = Emojis.getPics();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_profile);

		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		menuDrawer = ((SlidingDrawer)findViewById(R.id.myMenuDrawer));

		upload_id = getIntent().getStringExtra("upload_id");
		getProfile(upload_id);

		stats = ((Button)findViewById(R.id.stats));
		comments = ((Button)findViewById(R.id.comments));

		more = (Button)findViewById(R.id.button_more);
		flag = (Button)findViewById(R.id.flag);
		share = (Button)findViewById(R.id.share);
		cancel = (Button)findViewById(R.id.cancel);

		rate = (Button)findViewById(R.id.button_rate);
		profile = (Button)findViewById(R.id.button_profile);
		emojiGallery = (EcoGallery)findViewById(R.id.emoji_gallery);

		stats.setOnClickListener(this);
		comments.setOnClickListener(this);

		more.setOnClickListener(this);
		flag.setOnClickListener(this);
		share.setOnClickListener(this);
		cancel.setOnClickListener(this);

		rate.setOnClickListener(this);
		profile.setOnClickListener(this);

		if(getIntent().hasExtra("user") && getIntent().getBooleanExtra("user", false)) {
			flag.setText("Delete this post");
		} else {
			flag.setText("Flag this post");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_more:
			menuDrawer.toggle();
			break;
		case R.id.stats:
			System.out.println("STATS");
			stats.setSelected(true);
			comments.setSelected(false);

			Bundle sArguments = new Bundle();
			sArguments.putString("result", json);
			sArguments.putString("upload_id", upload_id);
			Stats_Fragment sFragment = new Stats_Fragment();
			sFragment.setArguments(sArguments);
			getSupportFragmentManager().beginTransaction()
			.add(R.id.realcontent, sFragment).commit();
			System.out.println(sFragment.getArguments());
			break;
		case R.id.comments:
			System.out.println("COMMENTS");
			stats.setSelected(false);
			comments.setSelected(true);

			Bundle cArguments = new Bundle();
			cArguments.putString("result", json);
			cArguments.putString("upload_id", upload_id);
			Comments_Fragment cFragment = new Comments_Fragment();
			cFragment.setArguments(cArguments);
			getSupportFragmentManager().beginTransaction()
			.add(R.id.realcontent, cFragment).commit();
			break;
		case R.id.flag:
			if(getIntent().hasExtra("user") && getIntent().getBooleanExtra("user", false)) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(BUZRAction_ImageProfile.this);
				alertDialog.setMessage("Are you sure you want to delete this post?");
				alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						new API_DeletePost(BUZRAction_ImageProfile.this, new OnTaskCompleteListener() {
							@Override
							public void onComplete(boolean success) {
								if(success) {
									findViewById(R.id.cancel).performClick();
									finish();
								}
							}
						}).execute("upload/deactivate", upload_id);
					}});
				alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						findViewById(R.id.cancel).performClick();
					}});
				alertDialog.create().show();
			} else {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(BUZRAction_ImageProfile.this);
				alertDialog.setMessage("Are you sure you want to flag this post?");
				alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						new API_FlagPost(BUZRAction_ImageProfile.this, new OnTaskCompleteListener() {
							@Override
							public void onComplete(boolean success) {
								if(success)
									findViewById(R.id.cancel).performClick();
							}
						}).execute("upload/flag", upload_id);
					}});
				alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						findViewById(R.id.cancel).performClick();
					}});
				alertDialog.create().show();
			}
			break;
		case R.id.share:
			new shareIntent().execute();
			break;
		case R.id.cancel:
			menuDrawer.toggle();
			break;
		case R.id.button_rate:
			if(emojiGallery.getVisibility()==View.INVISIBLE || emojiGallery.getVisibility()==View.GONE)
				if (rating.equals(""))
					showEmojiGallery();
				else
					showRatedNotification();
			else
				hideEmojiGallery();
			break;
		case R.id.button_profile:
			Intent intent = new Intent (this, BUZRAction_UserProfile.class);
			intent.putExtra("user_id", user_id);
			startActivity(intent);
			break;
		}		
	}

	@Override
	public void onItemClick(EcoGalleryAdapterView<?> parent, View view,	int position, long id) {
		if (click) {
			System.out.println("2nd click");
			System.out.println(System.nanoTime() - current_time);
			if ((System.nanoTime() - current_time) <= 250000000) {
				getEmoji(EmojiAdapter.getRealPosition(position+1));
				new API_Rank(BUZRAction_ImageProfile.this, new OnTaskCompleteListener() {
					@Override
					public void onComplete(boolean success) {
						if(success) {
							getProfile(upload_id);
							GlobalValues.upload_id = upload_id;
							hideEmojiGallery();
							finish();
						}
					}
				}).execute("rating/add", upload_id, Integer.toString(EmojiAdapter.getRealPosition(position)));
			} else {
				current_time = System.nanoTime();
			}
		} else {
			System.out.println("1st click");
			current_time = System.nanoTime();
			click = true;
		}
	}

	private final void getProfile(final String id) {

		new API_GetUpload(BUZRAction_ImageProfile.this, new OnTaskCompleteResultListener() {
			final WebImageView imageView = (WebImageView)findViewById(R.id.imageView1);

			public void onComplete(boolean success, String result) {
				json = result;
				JSONObject jObject;

				if(success)
					try {
						jObject = new JSONObject(result.trim());
						System.out.println(jObject);

						user_id = jObject.getString("user_id");
						final String username = jObject.getString("username");
						final String description = BASE64.decodeBase64(jObject.getString("description"));
						final String location = jObject.getString("location");
						final String timestamp = jObject.getString("timestamp");
						final String image = jObject.getString("image");
						rating = jObject.getString("rated");
						imageUri = Uri.parse(image);

						Display  display = getWindowManager().getDefaultDisplay();
						int screenWidth = display.getWidth();
						LayoutParams params = imageView.getLayoutParams();

						params.width = screenWidth;
						params.height = screenWidth;
						imageView.setLayoutParams(params);

						imageView.setImageURL(image);
						imageView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(BUZRAction_ImageProfile.this, Action_ImageProfileFull.class)
								.putExtra("image", image)
								.putExtra("username", username)
								.putExtra("description", description)
								.putExtra("location", location)
								.putExtra("timestamp", timestamp);
								startActivity(intent);
							}

						});

						if(getIntent().getStringExtra("mode").equals("stats")) {
							stats.performClick();
							stats.setSelected(true);
						} else if(getIntent().getStringExtra("mode").equals("comments")) {
							comments.performClick();
							comments.setSelected(true);
						}
						else;

					} catch (JSONException e) {
						e.printStackTrace();
					}
			}

		}).execute("upload/get", id);

	}

	private final class shareIntent extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			Intent shareIntent = new Intent(Intent.ACTION_SEND);

			File tempFile = new File(getExternalCacheDir(),"BUZR_Image.jpg");
			URL tempUrl;

			try {
				tempUrl = new URL (imageUri.toString());
				InputStream input = tempUrl.openStream();
				try {
					OutputStream output = new FileOutputStream (tempFile);
					try {
						byte[] buffer = new byte[1024];
						int bytesRead = 0;
						while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
							output.write(buffer, 0, bytesRead);
						}
					} finally {
						output.close();
					}
				} finally {
					input.close();
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Uri tempUri = Uri.fromFile(tempFile);

			System.out.println(tempUri);

			shareIntent.setType("image/jpeg");
			shareIntent.putExtra(Intent.EXTRA_STREAM, tempUri);
			startActivity(Intent.createChooser(shareIntent, "Share image via"));

			//			ShareHelper shareHelper = new ShareHelper(BUZRAction_ImageProfile.this,
			//					"Check out my photo on Buzr!", //Subject
			//					"Check it out!\n\n", //Body
			//					"Check it out!\n\n", //HTML Body
			//					"", //Twitter
			//					"" //Facebook
			//					);
			//			shareHelper.share();

			tempFile.deleteOnExit();
			return null;
		}

	}

	private final void showEmojiGallery() {
		emojiGallery.setVisibility(View.VISIBLE);
		emojiGallery.setAdapter(new EmojiAdapter(this));
		emojiGallery.setSelection((Integer.MAX_VALUE / 2)-3);
		emojiGallery.setUnselectedAlpha(0.90f);
		emojiGallery.setOnItemClickListener(this);
		rate.setBackgroundResource(R.drawable.button_close);
	}

	private final void hideEmojiGallery() {
		emojiGallery.setVisibility(View.INVISIBLE);
		rate.setBackgroundResource(R.drawable.button_rate);
	}

	private final void getEmoji(int i) {

		final ImageView iv = (ImageView)findViewById(R.id.emoji);

		iv.setImageDrawable(getResources().getDrawable(emojis[EmojiAdapter.getRealPosition(i+emojis.length-1)]));

		Animation expand = null; 

		expand = AnimationUtils.loadAnimation(this, R.anim.expand_from_center);

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
	
	private final void showRatedNotification() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(BUZRAction_ImageProfile.this);
		LayoutInflater factory = LayoutInflater.from(BUZRAction_ImageProfile.this);
		final View view = factory.inflate(R.layout.dialog_rated, null);
		((TextView)view.findViewById(R.id.textView1)).setText("You already rated this picture with");
		((ImageView)view.findViewById(R.id.imageView1)).setBackground(getResources().getDrawable(
				pics[EmojiAdapter.getRealPosition(Integer.parseInt(rating)+pics.length-1)]));
		alertDialog.setView(view);
		alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {
				dialog.cancel();
			}
		});
		alertDialog.show();
	}

}