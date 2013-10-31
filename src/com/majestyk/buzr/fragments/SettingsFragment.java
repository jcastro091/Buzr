package com.majestyk.buzr.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.majestyk.buzr.EmailIntent;
import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.R;
import com.majestyk.buzr.activities.Action_ChangePassword;
import com.majestyk.buzr.activities.Action_FindFriends;
import com.majestyk.buzr.activities.Action_PrivacyOfInfo;
import com.majestyk.buzr.activities.Action_ShareSettings;
import com.majestyk.buzr.activities.Action_TermsOfService;
import com.majestyk.buzr.activities.Activity_Main;
import com.majestyk.buzr.apis.API_Logout;
import com.majestyk.buzr.apis.API_Toggle;
import com.majestyk.buzr.apis.OnTaskCompleteListener;

import de.ankri.views.Switch;

@SuppressWarnings("unused")
public class SettingsFragment extends Fragment implements OnClickListener, OnCheckedChangeListener {
	private final String TAG = getClass().getSimpleName();
	private Context mContext;
	private View v;

	private Boolean fPhotosPrivacy;
	private Boolean fNotifications;
	private Boolean fPhotosCamRoll;
	private Boolean fPhotosSharing;

	private View expandable;
	private Boolean isOpen;
	private int visibility;

	public final static SettingsFragment init() {
		SettingsFragment fragment = new SettingsFragment();
		return fragment;
	}

	@Override
	public final View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {

		mContext = super.getActivity();
		v = (View) inflater.inflate(R.layout.buzr_settings, container, false);

		setUpButtons();
		setUpSwitches();

		expandable = v.findViewById(R.id.privacy_information);
		visibility = View.GONE;
		isOpen = false;

		return v;
	}

	@Override
	public final void onResume() {
		super.onResume();
		setUpSwitches();
	}
	
	@Override
	public final void onClick(View v) {
		Intent intent;
		switch(v.getId()) {
		case R.id.setting_find_friends:
			intent = new Intent(mContext, Action_FindFriends.class);
			startActivity(intent);					
			break;
		case R.id.setting_change_password:
			intent = new Intent(mContext, Action_ChangePassword.class);
			startActivity(intent);
			break;
		case R.id.setting_share_settings:
			intent = new Intent(mContext, Action_ShareSettings.class);
			startActivity(intent);
			break;
		case R.id.setting_report_a_bug:
			new EmailIntent(mContext, "bug@buzrapp.com", "Bug Report", "");
			break;
		case R.id.setting_privacy_policy:
			intent = new Intent(mContext, Action_PrivacyOfInfo.class);
			startActivity(intent);
			break;
		case R.id.setting_terms_of_service:
			intent = new Intent(mContext, Action_TermsOfService.class);
			startActivity(intent);
			break;
		case R.id.setting_log_out:
			new API_Logout(mContext, new OnTaskCompleteListener() {
				@Override
				public void onComplete(boolean success) {
					if(success) {
						CookieSyncManager.createInstance(mContext); 
						CookieManager cookieManager = CookieManager.getInstance();
						cookieManager.removeAllCookie();

						SharedPreferences settings = mContext.getSharedPreferences(GlobalValues.PREFS, 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.clear();
						editor.commit();

						Intent intent = new Intent(mContext, Activity_Main.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				}
			}).execute("user/logout");
			break;
		case R.id.setting_app_tutorial:
			intent = new Intent(mContext, BUZRFragmentManagerActivity.class);
			intent.putExtra("tutorial", true);
			startActivity(intent);
			break;
		case R.id.imageButton1:
			translate();
		}
	}
	
	@Override
	public final void onCheckedChanged(CompoundButton v, final boolean flag) {

		final SharedPreferences settings = mContext.getSharedPreferences(GlobalValues.PREFS, 0);
		final SharedPreferences.Editor editor = settings.edit();

		switch(v.getId()) {
		case R.id.setting_private_photos:
			new API_Toggle(mContext, new OnTaskCompleteListener() {
				public void onComplete(boolean success) {
					if(success) { }
					else fPhotosPrivacy = !flag;
				}
			}).execute("user/togglePrivacy");
			fPhotosPrivacy = flag;
			if (settings.getString("PRIVACY", "0").equals("0"))
				editor.putString("PRIVACY", "1");
			else
				editor.putString("PRIVACY", "0");
			break;
		case R.id.setting_push_notifications:
			new API_Toggle(mContext, new OnTaskCompleteListener() {
				public void onComplete(boolean success) {
					if(success) { }
					else fNotifications = !flag;
				}
			}).execute("user/toggleNotifications");
			fNotifications = flag;
			if (settings.getString("NOTIFICATIONS", "0").equals("0"))
				editor.putString("NOTIFICATIONS", "1");
			else
				editor.putString("NOTIFICATIONS", "0");
			break;
		case R.id.setting_camera_roll:
			new API_Toggle(mContext, new OnTaskCompleteListener() {
				public void onComplete(boolean success) {
					if(success) { }
					else fPhotosCamRoll = !flag;
				}
			}).execute("user/toggleCameraRoll");
			fPhotosCamRoll = flag;
			if (settings.getString("CAMERA_ROLL", "0").equals("0"))
				editor.putString("CAMERA_ROLL", "1");
			else
				editor.putString("CAMERA_ROLL", "0");
			break;
		case R.id.setting_photo_sharing:
			new API_Toggle(mContext, new OnTaskCompleteListener() {
				public void onComplete(boolean success) {
					if(success) { }
					else fPhotosSharing = !flag;
				}
			}).execute("user/toggleSharing");
			fPhotosSharing = flag;
			if (settings.getString("SHARING", "0").equals("0"))
				editor.putString("SHARING", "1");
			else
				editor.putString("SHARING", "0");
			break;
		}
		editor.commit();
	}

	private final void setUpButtons() {
		((TextView)v.findViewById(R.id.setting_find_friends)).setOnClickListener(this);
		((TextView)v.findViewById(R.id.setting_change_password)).setOnClickListener(this);
		((TextView)v.findViewById(R.id.setting_share_settings)).setOnClickListener(this);
		((TextView)v.findViewById(R.id.setting_report_a_bug)).setOnClickListener(this);
		((TextView)v.findViewById(R.id.setting_privacy_policy)).setOnClickListener(this);
		((TextView)v.findViewById(R.id.setting_terms_of_service)).setOnClickListener(this);
		((TextView)v.findViewById(R.id.setting_log_out)).setOnClickListener(this);
		((TextView)v.findViewById(R.id.setting_app_tutorial)).setOnClickListener(this);
		v.findViewById(R.id.imageButton1).setOnClickListener(this);
	}

	private final void setUpSwitches() {
		final Switch sPhotosPrivacy = (Switch)v.findViewById(R.id.setting_private_photos);
		final Switch sNotifications = (Switch)v.findViewById(R.id.setting_push_notifications);
		final Switch sPhotosCamRoll = (Switch)v.findViewById(R.id.setting_camera_roll);
		final Switch sPhotosSharing = (Switch)v.findViewById(R.id.setting_photo_sharing);

		SharedPreferences settings = mContext.getSharedPreferences(GlobalValues.PREFS, 0);

		if (settings.getString("PRIVACY", "0").equals("0")) {
			sPhotosPrivacy.setChecked(false);
		} else {
			sPhotosPrivacy.setChecked(true);
		}
		if (settings.getString("SHARING", "0").equals("0")) {
			sPhotosSharing.setChecked(false);
		} else {
			sPhotosSharing.setChecked(true);
		}
		if (settings.getString("CAMERA_ROLL", "0").equals("0")) {
			sPhotosCamRoll.setChecked(false);
		} else {
			sPhotosCamRoll.setChecked(true);
		}
		if (settings.getString("NOTIFICATIONS", "0").equals("0")) {
			sNotifications.setChecked(false);
		} else {
			sNotifications.setChecked(true);
		}

		sPhotosPrivacy.setOnCheckedChangeListener(this);
		sNotifications.setOnCheckedChangeListener(this);
		sPhotosCamRoll.setOnCheckedChangeListener(this);
		sPhotosSharing.setOnCheckedChangeListener(this);

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

}
