package com.majestyk.buzr.social;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androauth.api.TwitterApi;
import com.androauth.oauth.OAuth10Service;
import com.androauth.oauth.OAuth10Service.OAuth10ServiceCallback;
import com.androauth.oauth.OAuth10Token;
import com.androauth.oauth.OAuthService;
import com.majestyk.buzr.R;
import com.twotoasters.android.hoot.HootResult;

public final class TwitterWebView extends Activity {
	OAuth10Service service;

	public final static String PARAMETER_CONSUMER_KEY = "CONSUMER_KEY";
	public final static String PARAMETER_CONSUMER_SECRET = "CONSUMER_SECRET";
	public final static String PARAMETER_CALLBACK_URL = "CALLBACK";

	private String consumerKey;
	private String consumerSecret;
	private String callbackUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_web_view);

		/*
		 * Get params
		 */
		Intent intent = getIntent();
		consumerKey = intent.getStringExtra(PARAMETER_CONSUMER_KEY);
		consumerSecret = intent.getStringExtra(PARAMETER_CONSUMER_SECRET);
		callbackUrl = intent.getStringExtra(PARAMETER_CALLBACK_URL);

		service = OAuthService.newInstance(new TwitterApi(), consumerKey, consumerSecret, new OAuth10ServiceCallback() {

			@Override
			public void onOAuthAccessTokenReceived(OAuth10Token token) {
				complete(token);

				System.out.println("token recieved " + token.getAccessToken());
				System.out.println("token recieved " + token.getUserSecret());
			}

			@Override
			public void onOAuthRequestTokenReceived() {
				loadWebView();
			}

			@Override
			public void onOAuthRequestTokenFailed(HootResult result) {
				System.out.println("Token request failed " + result.getException());
			}

			@Override
			public void onOAuthAccessTokenFailed(HootResult result) {
				System.out.println("Token access failed " + result);

			}

		});
		service.start();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void loadWebView() {
		final WebView webview = (WebView) findViewById(R.id.twitter_web_view);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				System.out.println("twitter callback url " + url);
				
				// Checking for our successful callback
				if(url.startsWith(callbackUrl)) {
					webview.setVisibility(View.GONE);
					System.out.println("token url " + url + " " + service.getOAuthAccessToken(url));
				} else {
					System.out.println("token url");
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

		});

		webview.loadUrl(service.getAuthorizeUrl());
	}

	private void complete(OAuth10Token token) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(TwitterUtils.PREF_TWITTER_ACCESS, token.getAccessToken());
		resultIntent.putExtra(TwitterUtils.PREF_TWITTER_SECRET, token.getUserSecret());
		setResult(RESULT_OK, resultIntent);
		finish();
	}
}
