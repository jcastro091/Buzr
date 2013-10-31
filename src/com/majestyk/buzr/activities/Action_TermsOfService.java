package com.majestyk.buzr.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.majestyk.buzr.R;

public class Action_TermsOfService extends Activity {
	
	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_webview);

		WebView webView = (WebView)findViewById(R.id.webView1);
		webView.loadUrl("file:///android_asset/terms.html");
		
	}
}
