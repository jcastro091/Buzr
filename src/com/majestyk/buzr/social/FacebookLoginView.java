package com.majestyk.buzr.social;

import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.internal.SessionTracker;
import com.facebook.internal.Utility;

public final class FacebookLoginView extends Activity  {
	public final static String PARAM_REQUEST = "request";
	public final static String ARG_LOGIN = "login";
	public final static String ARG_WRITE = "write";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String request = getIntent().getStringExtra(PARAM_REQUEST);
		if(request.equals(ARG_LOGIN)) {
			fLoginFacebook(this);
		} else if(request.equals(ARG_WRITE)) {
			fWriteFacebook(this);
		}
	}

	private SessionTracker mSessionTracker;
	private Session mCurrentSession;

	void fWriteFacebook(Activity activity) {
		if(Session.getActiveSession() != null && Session.getActiveSession().isOpened() && hasPublishPermission()) {
			setResult(RESULT_OK);
			finish();
			return;
		} else if(Session.getActiveSession() == null || Session.getActiveSession().isClosed()) {
			Intent loginIntent = new Intent(this, FacebookLoginView.class);
			loginIntent.putExtra(PARAM_REQUEST, ARG_LOGIN);
			startActivityForResult(loginIntent, FacebookUtils.FACEBOOK_REQUEST_CODE);
			return;
		}

		Session.OpenRequest openRequest = null;
		openRequest = new Session.OpenRequest(activity);

		if (openRequest != null) {
			openRequest.setDefaultAudience(SessionDefaultAudience.FRIENDS);
			openRequest.setPermissions(Arrays.asList("email", "publish_stream", "publish_actions"));
			openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
			openRequest.setCallback(new StatusCallback() {
				@Override
				public void call(Session session, SessionState state, Exception exception) {       
					if(state.isOpened()) {
						setResult(RESULT_OK);
						finish();
					}
				}
			});

			mCurrentSession.openForPublish(openRequest);
		}

	}

	boolean hasPublishPermission() {
		for(String permission : Session.getActiveSession().getPermissions())
			if(permission.equals("publish_stream")) 
				return true;
		return false;
	}

	void fLoginFacebook(Activity activity) {
		if(Session.getActiveSession() != null && Session.getActiveSession().isOpened()) {
			setResult(RESULT_OK);
			finish();
			return;
		}

		mSessionTracker = new SessionTracker(activity.getBaseContext(), new StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if(session != null && state.isOpened()) {
					System.out.println("We've got an open session!");
				}
			}
		}, null, false);

		String applicationId = Utility.getMetadataApplicationId(activity.getBaseContext());
		mCurrentSession = mSessionTracker.getSession();

		if (mCurrentSession == null || mCurrentSession.getState().isClosed()) {
			mSessionTracker.setSession(null);
			Session session = new Session.Builder(activity.getBaseContext()).setApplicationId(applicationId).build();
			Session.setActiveSession(session);
			mCurrentSession = session;
		}

		if (!mCurrentSession.isOpened()) {
			Session.OpenRequest openRequest = null;
			openRequest = new Session.OpenRequest(activity);

			if (openRequest != null) {
				openRequest.setDefaultAudience(SessionDefaultAudience.FRIENDS);
				openRequest.setPermissions(Arrays.asList("email"));
				openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
				openRequest.setCallback(new StatusCallback() {
					@Override
					public void call(Session session, SessionState state, Exception exception) {       
						if(state.isOpened()) {
							setResult(RESULT_OK);
							finish();
						}
					}
				});

				mCurrentSession.openForRead(openRequest);
			}
		} else {  }
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == FacebookUtils.FACEBOOK_REQUEST_CODE) {
			fWriteFacebook(getParent());
		} else {
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		}
	}
}