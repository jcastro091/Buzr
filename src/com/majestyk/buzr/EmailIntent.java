package com.majestyk.buzr;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;

public class EmailIntent {

	private Context mContext;
	private String email;
	private String subject;
	private String body;

	public EmailIntent(Context c, String email, String subject, String body) {

		this.mContext = c;

		this.email = email;
		this.subject = subject;
		this.body = body;

		initShareIntent("mail");
		/*
			Intent intent = new Intent(Intent.ACTION_SEND);

			intent.setType("text/plain");

			intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"bug@buzrapp.com"});
		    intent.putExtra(android.content.Intent.EXTRA_SUBJECT,  "Bug Report");
		    intent.putExtra(android.content.Intent.EXTRA_TEXT,     "");

			// Create and start the chooser
			Intent chooser = Intent.createChooser(intent, "Complete action using");
			startActivity(chooser);
		 */
	}

	public EmailIntent(Context c, String email, String subject, String body, File file) {

		this.mContext = c;

		this.email = email;
		this.subject = subject;
		this.body = body;

		initShareIntent("mail", file);

	}

	@SuppressLint("DefaultLocale")
	private final void initShareIntent(String type) {
		boolean found = false;
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");

		// gets the list of intents that can be loaded.
		List<ResolveInfo> resInfo = mContext.getPackageManager().queryIntentActivities(share, 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				if (info.activityInfo.packageName.toLowerCase().contains(type) ||
						info.activityInfo.name.toLowerCase().contains(type) ) {
					share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					share.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
					share.putExtra(Intent.EXTRA_SUBJECT,  subject);
					share.putExtra(Intent.EXTRA_TEXT,     body);
					share.setPackage(info.activityInfo.packageName);
					found = true;
					break;
				}
			}
			if (!found)
				return;

			mContext.startActivity(Intent.createChooser(share, "Select"));
		}
	}

	@SuppressLint("DefaultLocale")
	private final void initShareIntent(String type, File image) {
		boolean found = false;
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");

		// gets the list of intents that can be loaded.
		List<ResolveInfo> resInfo = mContext.getPackageManager().queryIntentActivities(share, 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				if (info.activityInfo.packageName.toLowerCase().contains(type) ||
						info.activityInfo.name.toLowerCase().contains(type) ) {
					share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					share.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
					share.putExtra(Intent.EXTRA_SUBJECT,  subject);
					share.putExtra(Intent.EXTRA_TEXT,     body);

					share.setType("image/jpeg");
					share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));

					share.setPackage(info.activityInfo.packageName);
					found = true;
					break;
				}
			}
			if (!found)
				return;

			mContext.startActivity(Intent.createChooser(share, "Select"));
		}

	}

}