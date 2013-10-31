package com.majestyk.buzr.social;

import oauth.signpost.OAuthConsumer;

public class TumblrUtils {

	// Consumer Key and Secret
	private static final String TUMBLR_CONSUMER_KEY = "Uw37i3lbNNv38LNVAyLkPJ0dp1IijsTk1O1RXA71CAj7x2UDDk";
	private static final String TUMBLR_CONSUMER_SECRET = "glyDR236FNOQuGAn6IwDg7Kisf7GVqVDgG4oxNwJMSLihzGY9c";

	// Callback URL
	private static final String TUMBLR_CALLBACK_URL = "http://oauthbuzr.com";

	// Request Code
	public static final int TUMBLR_REQUEST_CODE = 886257;

	// Preference Constants
	public static final String PREF_TUMBLR_ACCESS = "tumblr_access_token";
	public static final String PREF_TUMBLR_SECRET = "tumblr_secret_token";

	public static final String TUMBLR_REQUEST_URL = "http://www.tumblr.com/oauth/request_token";
	public static final String TUMBLR_ACCESS_URL = "http://www.tumblr.com/oauth/access_token";
	public static final String TUMBLR_AUTHORIZE_URL = "http://www.tumblr.com/oauth/authorize";
	
	private static OAuthConsumer tumblrConsumer;

	public static String getConsumerKey() {
		return TUMBLR_CONSUMER_KEY;
	}

	public static String getConsumerSecret() {
		return TUMBLR_CONSUMER_SECRET;
	}

	public static String getCallbackUrl() {
		return TUMBLR_CALLBACK_URL;
	}

	public static String getRequestUrl() {
		return TUMBLR_REQUEST_URL;
	}

	public static String getAccessUrl() {
		return TUMBLR_ACCESS_URL;
	}

	public static String getAuthorizeUrl() {
		return TUMBLR_AUTHORIZE_URL;
	}

	public static OAuthConsumer getTumblrConsumer() {
		return tumblrConsumer;
	}

	public static void setTumblrConsumer(OAuthConsumer tumblrConsumer) {
		TumblrUtils.tumblrConsumer = tumblrConsumer;
	}

}
