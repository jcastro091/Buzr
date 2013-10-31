package com.majestyk.buzr.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.majestyk.buzr.Camera_ShareActivity;
import com.majestyk.buzr.FontManager;
import com.majestyk.buzr.R;
import com.majestyk.buzr.activities.Action_FindFriends;
import com.majestyk.buzr.adapter.ImagePagerAdapter;
import com.majestyk.buzr.apis.API_NewNotifications;
import com.majestyk.buzr.apis.OnTaskCompleteResultListener;

public class BUZRFragmentManagerActivity extends FragmentActivity implements OnClickListener {
	private Context mContext;

	private ImageButton btnHome;
	private ImageButton btnFriend;
	private ImageButton btnCamera;
	private ImageView arrow;
	private ImageView logo;
	private TextView label;
	private TextView notifications;

	private View expandable;

	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	private Button button5;

	private Boolean isOpen;
	private int visibility;
	private View clearView;

	private int count;
	private String time;

	private TimerTask scanTask;
	private final Handler handler = new Handler();
	private final Timer t = new Timer();

	private Boolean intentTutorial;
	private String intentImageURL, intentImageBMP, intentDescript, intentLocation;
	private Boolean intentFacebook, intentTwitter, intentTumblr;
	private String intentTab;
	
	private ViewPager viewPager;
	private Button viewPagerBtn;

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragmentmanager_activity);
		mContext = this;
		isOpen = false;

		intentTutorial	= getIntent().getBooleanExtra("tutorial", false);
		intentImageURL	= getIntent().getStringExtra("imageURL");
		intentImageBMP	= getIntent().getStringExtra("imageBMP");
		intentDescript	= getIntent().getStringExtra("description");
		intentLocation	= getIntent().getStringExtra("location");
		intentFacebook	= getIntent().getBooleanExtra("fACEbOOK", false);
		intentTwitter	= getIntent().getBooleanExtra("tWITTER", false);
		intentTumblr	= getIntent().getBooleanExtra("tUMBLR", false);

		setUpButtons();
		setActionLogo();

		intentTab = "home";
		if(getIntent().hasExtra("tab")) {
			intentTab = getIntent().getStringExtra("tab");
			if(intentTab.equals("home")) {
				button1.performClick();
			} else if(intentTab.equals("profile")) {
				button2.performClick();
			} else if(intentTab.equals("find")) {
				button3.performClick();
			} else if(intentTab.equals("leaderboard")) {
				button4.performClick();
			} else if(intentTab.equals("settings")) {
				button5.performClick();
			}
			translate();
		} else {
			HomeFragment fragment = HomeFragment.init( 
					intentImageURL, intentImageBMP, intentDescript, intentLocation,
					intentFacebook, intentTwitter, intentTumblr);
			getSupportFragmentManager().beginTransaction()
			.add(R.id.tabactivity_detail_container, fragment).commit();
		}

		if(intentTutorial) {
			viewPager = (ViewPager) findViewById(R.id.tutorial);
			viewPagerBtn = (Button) findViewById(R.id.tutorial_button);
			viewPagerBtn.setOnClickListener(this);
			tutorial();
		}

	}

	@Override
	public final void onClick(View v) {
		Intent intent = new Intent();
		switch(v.getId()) {
		case R.id.action_btn_home:
			translate();
			break;
		case R.id.action_btn_friend:
			intent = new Intent(mContext, Action_FindFriends.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			mContext.startActivity(intent);
			break;
		case R.id.action_btn_camera:
			intent = new Intent(mContext, Camera_ShareActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			mContext.startActivity(intent);
			break;
		case R.id.button1:
			translate();
			setActionLogo();
			setActionButton(R.drawable.actionbar_button_home);
			HomeFragment fragment1 = HomeFragment.init( 
					intentImageURL, intentImageBMP, intentDescript, intentLocation,
					intentFacebook, intentTwitter, intentTumblr);
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.tabactivity_detail_container, fragment1).commit();
			break;
		case R.id.button2:
			translate();
			setActionLabel("my profile");
			setActionButton(R.drawable.actionbar_button_profile);
			ProfileFragment fragment2 = ProfileFragment.init();
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.tabactivity_detail_container, fragment2).commit();
			break;
		case R.id.button3:
			translate();
			setActionLabel("find");
			setActionButton(R.drawable.actionbar_button_find);
			FindFragment fragment3 = FindFragment.init(count);
			clearNewNotifications();
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.tabactivity_detail_container, fragment3).commit();
			break;
		case R.id.button4:
			translate();
			setActionLogo();
			setActionLabel("leaderboard");
			setActionButton(R.drawable.actionbar_button_leaderboard);
			LeaderboardFragment fragment4 = LeaderboardFragment.init();
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.tabactivity_detail_container, fragment4).commit();
			break;
		case R.id.button5:
			translate();
			setActionLabel("settings");
			setActionButton(R.drawable.actionbar_button_settings);
			SettingsFragment fragment5 = SettingsFragment.init();
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.tabactivity_detail_container, fragment5).commit();
			break;
		case R.id.tutorial_button:
			intentTutorial = false;
			viewPager.setVisibility(View.GONE);
			viewPagerBtn.setVisibility(View.GONE);
			viewPagerBtn.setOnClickListener(null);
			translate(); button1.performClick();
			break;
		case R.id.clearView:
			translate();
		}
	}
	
	@Override
	public final void onDestroy() {
		stopTimer();
		super.onDestroy();
	}

	private final void setUpButtons() {
		btnHome		= (ImageButton)findViewById(R.id.action_btn_home);
		btnFriend	= (ImageButton)findViewById(R.id.action_btn_friend);
		btnCamera	= (ImageButton)findViewById(R.id.action_btn_camera);
		arrow		= (ImageView)findViewById(R.id.action_btn_arrow);
		logo		= (ImageView)findViewById(R.id.action_logo);
		label		= (TextView)findViewById(R.id.action_label);
		notifications = (TextView)findViewById(R.id.action_notification);

		expandable	= (View)findViewById(R.id.expandable);

		button1 = (Button)findViewById(R.id.button1);
		button2 = (Button)findViewById(R.id.button2);
		button3 = (Button)findViewById(R.id.button3);
		button4 = (Button)findViewById(R.id.button4);
		button5 = (Button)findViewById(R.id.button5);
		
		clearView = (View)findViewById(R.id.clearView);

		btnHome.setOnClickListener(this);
		btnFriend.setOnClickListener(this);
		btnCamera.setOnClickListener(this);

		FontManager.setTypeFace(button1);
		FontManager.setTextColor(button1);
		button1.setOnClickListener(this);

		FontManager.setTypeFace(button2);
		FontManager.setTextColor(button2);
		button2.setOnClickListener(this);

		FontManager.setTypeFace(button3);
		FontManager.setTextColor(button3);
		button3.setOnClickListener(this);

		FontManager.setTypeFace(button4);
		FontManager.setTextColor(button4);
		button4.setOnClickListener(this);

		FontManager.setTypeFace(button5);
		FontManager.setTextColor(button5);
		button5.setOnClickListener(this);
		
		clearView.setOnClickListener(this);

		if (scanTask == null) {
			startTimer();
		}
	}

	private final void setActionButton(int res) {
		btnHome.setBackgroundResource(res);
	}

	private final void setActionLabel(String str) {
		label.setText(str);
		label.setVisibility(View.VISIBLE);
		logo.setVisibility(View.GONE);
	}

	private final void setActionLogo() {
		logo.setVisibility(View.VISIBLE);
		label.setVisibility(View.GONE);
	}

	private final void translate() {
		Animation rotate = null;
		Animation translate = null; 

		if(isOpen) {
			isOpen = false;
			rotate = AnimationUtils.loadAnimation(mContext, R.anim.rotate_counterclockwise);
			translate = AnimationUtils.loadAnimation(mContext, R.anim.translate_up);			
			visibility = View.GONE;
		}
		else {
			isOpen = true;
			rotate = AnimationUtils.loadAnimation(mContext, R.anim.rotate_clockwise);
			translate = AnimationUtils.loadAnimation(mContext, R.anim.translate_down);
			visibility = View.VISIBLE;
		}

		arrow.startAnimation(rotate);

		translate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				expandable.setVisibility(visibility);
			}

			@Override
			public void onAnimationRepeat(Animation animation) { }

			@Override
			public void onAnimationStart(Animation animation) {
				expandable.setVisibility(View.VISIBLE);
			}

		});

		expandable.startAnimation(translate);
		
		clearView.setVisibility(visibility);

	}

	private final int getNewNotifications() {

		System.out.println("time: " + time);
		new API_NewNotifications(mContext, new OnTaskCompleteResultListener() {

			public void onComplete(boolean success, String result) {
				JSONArray jArray;

				if(success) {
					try {
						System.out.println("New notifications: " + result);
						jArray = new JSONArray(result.trim());
						count = jArray.length();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				System.out.println("Timer executed with " + count + " notifications");

				if (count > 0) {
					notifications.setVisibility(View.VISIBLE);
					notifications.setText(Integer.toString(count));
					button3.setSelected(true);
				}

			}

		}).execute("user/newsFeed", time);
		return count;
	}

	private final String getTime() {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		inputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		Date timestamp = new Date();
		return inputFormat.format(timestamp);
	}

	private final void startTimer() {
		count = 0;
		time = getTime();
		scanTask = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						getNewNotifications();
					}
				});
			}};

			t.schedule(scanTask, 15000, 30000);
			Log.d("TIMER", "timer started");
	}

	private final void clearNewNotifications() {
		notifications.setVisibility(View.INVISIBLE);
		button3.setSelected(false);
		time = getTime();
		count = 0;
	}

	private final void stopTimer() {
		if(scanTask != null) {
			Log.d("TIMER", "timer stopped");
			scanTask.cancel();
		}
	}

	private final void tutorial () {
		viewPager.setVisibility(View.VISIBLE);
		viewPagerBtn.setVisibility(View.VISIBLE);
		ImagePagerAdapter adapter = new ImagePagerAdapter(mContext);
		viewPager.setAdapter(adapter);
	}

}
