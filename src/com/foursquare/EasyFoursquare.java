package com.foursquare;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.SharedPreferences;
import com.foursquare.constants.FoursquareConstants;
import com.foursquare.criterias.CheckInCriteria;
import com.foursquare.criterias.TipsCriteria;
import com.foursquare.criterias.TrendingVenuesCriteria;
import com.foursquare.criterias.VenuesCriteria;
import com.foursquare.listeners.AccessTokenRequestListener;
import com.foursquare.models.Checkin;
import com.foursquare.models.Tip;
import com.foursquare.models.User;
import com.foursquare.models.Venue;
import com.foursquare.models.Venues;
import com.foursquare.tasks.TipsNearbyRequest;
import com.foursquare.tasks.checkins.CheckInRequest;
import com.foursquare.tasks.users.GetCheckInsRequest;
import com.foursquare.tasks.users.GetFriendsRequest;
import com.foursquare.tasks.users.GetUserVenuesHistoryRequest;
import com.foursquare.tasks.users.SelfInfoRequest;
import com.foursquare.tasks.venues.FoursquareTrendingVenuesNearbyRequest;
import com.foursquare.tasks.venues.FoursquareVenueDetailsRequest;
import com.foursquare.tasks.venues.FoursquareVenuesNearbyRequest;
import com.foursquare.tasks.venues.FoursquareVenuesNearbyRequest_UserlessAccess;

/**
 * Class to handle methods used to perform requests to FoursquareAPI and respond
 * SYNChronously.
 * 
 * @author Felipe Conde <condesales@gmail.com>
 * 
 */
public class EasyFoursquare {

	private Activity mActivity;
	private FoursquareDialog mDialog;
	private String mAccessToken = "";

	public EasyFoursquare(Activity activity) {
		mActivity = activity;
	}

	/**
	 * Requests the access to API
	 */
	public void requestAccess(AccessTokenRequestListener listener) {
		if (!hasAccessToken()) {
			loginDialog(listener);
		} else {
			listener.onAccessGrant(getAccessToken());
		}
	}

	/**
	 * Requests logged user information asynchronously.
	 * 
	 * @return The user information
	 */
	public User getUserInfo() {
		SelfInfoRequest request = new SelfInfoRequest(mActivity);
		request.execute(getAccessToken());
		User user = null;
		try {
			user = request.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return user;
	}

	/**
	 * Requests nearby Venues.
	 * 
	 * @param criteria
	 *            The criteria to your search request
	 */
	public ArrayList<Venue> getVenuesNearby(VenuesCriteria criteria) {
		FoursquareVenuesNearbyRequest request = new FoursquareVenuesNearbyRequest(
				mActivity, criteria);
		request.execute(getAccessToken());
		ArrayList<Venue> venues = new ArrayList<Venue>();
		try {
			venues = request.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return venues;
	}

	public ArrayList<Venue> getUserlessVenuesNearby(VenuesCriteria criteria) {
		FoursquareVenuesNearbyRequest_UserlessAccess request = new FoursquareVenuesNearbyRequest_UserlessAccess(
				mActivity, criteria);
		request.execute();
		ArrayList<Venue> venues = new ArrayList<Venue>();
		try {
			venues = request.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return venues;
	}

	/**
	 * Requests nearby Tip.
	 * 
	 * @param criteria
	 *            The criteria to your search request
	 */
	public ArrayList<Tip> getTipsNearby(TipsCriteria criteria) {
		TipsNearbyRequest request = new TipsNearbyRequest(
				mActivity, criteria);
		request.execute(getAccessToken());
		ArrayList<Tip> tips = new ArrayList<Tip>();
		try {
			tips = request.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return tips;
	}
	
	
	/**
	 * Requests nearby Venues that are trending.
	 * @param criteria
	 * 				The criteria to your search request
	 * 
	 */
	public ArrayList<Venue> getTrendingVenuesNearby(TrendingVenuesCriteria criteria) {
		FoursquareTrendingVenuesNearbyRequest request = new FoursquareTrendingVenuesNearbyRequest(mActivity, criteria);
		request.execute(getAccessToken());
		ArrayList<Venue> venues = new ArrayList<Venue>();
		try {
			venues = request.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return venues;
	}
	
	
	public void getVenueDetail(String venueID){
		FoursquareVenueDetailsRequest request = new FoursquareVenueDetailsRequest(mActivity, venueID);
		request.execute(getAccessToken());
	}

	/**
	 * Checks in at a venue.
	 * 
	 * @param criteria
	 *            The criteria to your search request
	 */
	public Checkin checkIn(CheckInCriteria criteria) {
		CheckInRequest request = new CheckInRequest(mActivity, criteria);
		request.execute(getAccessToken());
		Checkin checkin = null;
		try {
			checkin = request.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return checkin;
	}

	public ArrayList<Checkin> getcheckIns() {
		GetCheckInsRequest request = new GetCheckInsRequest(mActivity);
		request.execute(getAccessToken());
		ArrayList<Checkin> checkins = null;
		try {
			checkins = request.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return checkins;
	}

	public ArrayList<Checkin> getcheckIns(String userID) {
		GetCheckInsRequest request = new GetCheckInsRequest(mActivity, userID);
		request.execute(getAccessToken());
		ArrayList<Checkin> checkins = null;
		try {
			checkins = request.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return checkins;
	}

	public ArrayList<User> getFriends() {
		GetFriendsRequest request = new GetFriendsRequest(mActivity);
		request.execute(getAccessToken());
		ArrayList<User> users = null;
		try {
			users = request.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return users;
	}

	public ArrayList<Venues> getVenuesHistory() {
		GetUserVenuesHistoryRequest request = new GetUserVenuesHistoryRequest(
				mActivity);
		request.execute(getAccessToken());
		ArrayList<Venues> venues = null;
		try {
			venues = request.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return venues;
	}

	public ArrayList<Venues> getVenuesHistory(String userID) {
		GetUserVenuesHistoryRequest request = new GetUserVenuesHistoryRequest(
				mActivity, userID);
		request.execute(getAccessToken());
		ArrayList<Venues> venues = null;
		try {
			venues = request.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return venues;
	}

	public ArrayList<User> getFriends(String userID) {
		GetFriendsRequest request = new GetFriendsRequest(mActivity, userID);
		request.execute(getAccessToken());
		ArrayList<User> users = null;
		try {
			users = request.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return users;
	}

	private boolean hasAccessToken() {
		String token = getAccessToken();
		return !token.equals("");
	}

	/**
	 * Gets the access token used to perform requests.
	 * 
	 * @return the token
	 */
	private String getAccessToken() {
		if (mAccessToken.equals("")) {
			SharedPreferences settings = mActivity.getSharedPreferences(
					FoursquareConstants.SHARED_PREF_FILE, 0);
			mAccessToken = settings.getString(FoursquareConstants.ACCESS_TOKEN,
					"");
		}
		return mAccessToken;
	}

	/**
	 * Requests the Foursquare login though a dialog.
	 */
	private void loginDialog(AccessTokenRequestListener listener) {
		String url = "https://foursquare.com/oauth2/authenticate"
				+ "?client_id=" + FoursquareConstants.CLIENT_ID
				+ "&response_type=code" + "&redirect_uri="
				+ FoursquareConstants.CALLBACK_URL;

		mDialog = new FoursquareDialog(mActivity, url, listener);
		mDialog.show();
	}
	
	
	

}
