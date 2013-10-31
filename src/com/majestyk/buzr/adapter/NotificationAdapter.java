package com.majestyk.buzr.adapter;

import java.util.List;

import pl.polidea.webimageview.WebImageView;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.majestyk.buzr.R;
import com.majestyk.buzr.activities.BUZRAction_ImageProfile;
import com.majestyk.buzr.activities.BUZRAction_UserProfile;
import com.majestyk.buzr.objects.NotificationItem;

public class NotificationAdapter extends BaseAdapter {

	private Context mContext;
	private List<NotificationItem> items;

	public NotificationAdapter(Context c, List<NotificationItem> l) {
		mContext = c;
		items = l;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public NotificationItem getItem(int pos) {
		return items.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup parent) {

		final WebImageView iv1;
		final TextView notifUser;
		final TextView notifText;
		final TextView notifTime;
		final WebImageView iv2;
		final NotificationItem item;

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.notification_item, parent, false);
			iv1 = (WebImageView) convertView.findViewById(R.id.notif_image1);
			notifUser = (TextView) convertView.findViewById(R.id.notif_user);
			notifText = (TextView) convertView.findViewById(R.id.notif_text);
			notifTime = (TextView) convertView.findViewById(R.id.notif_time);
			iv2 = (WebImageView) convertView.findViewById(R.id.notif_image2);
			item = items.get(pos);

			final ViewHolder tag = new ViewHolder(iv1, notifUser, notifText, notifTime, iv2, item);

			convertView.setTag(tag);
			convertView.findViewById(R.id.clickable).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					NotificationItem item = tag.item;

					if(item.getScenario().equals("a")) {
						Intent intent = new Intent(mContext, BUZRAction_UserProfile.class);
						intent.putExtra("user_id", item.getUserID());
						mContext.startActivity(intent);
					} else if(item.getScenario().equals("b")) {
						Intent intent = new Intent(mContext, BUZRAction_ImageProfile.class);
						intent.putExtra("upload_id", item.getUploadID());
						intent.putExtra("mode", "stats");
						mContext.startActivity(intent);
					} else {
						Intent intent = new Intent(mContext, BUZRAction_ImageProfile.class);
						intent.putExtra("upload_id", item.getUploadID());
						intent.putExtra("mode", "comments");
						mContext.startActivity(intent);
					}

				}
			});

		} else {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			iv1 = viewHolder.iv1;
			notifUser = viewHolder.notifUser;
			notifText = viewHolder.notifText;
			notifTime = viewHolder.notifTime;
			iv2 = viewHolder.iv2;
			item = items.get(pos);
			viewHolder.item = item;
		}

		if(item.getScenario().equals("a")) { //Follow
			iv1.setImageResource(R.drawable.empty_image_080);
			iv1.setImageURL(item.getUser());
			iv2.setImageDrawable(null);
		} else if(item.getScenario().equals("b")) { //Rated photo
			iv1.setImageResource(R.drawable.empty_image_138);
			iv1.setImageURL(item.getTarget());
			iv2.setImageResource(item.getEmoji());
		} else if(item.getScenario().equals("c")) { //Comment on photo
			iv1.setImageResource(R.drawable.empty_image_138);
			iv1.setImageURL(item.getTarget());
			iv2.setImageDrawable(null);
		}

		notifUser.setText(item.getUsername());
		notifText.setText(item.getEvent());
		notifTime.setText(item.getTime());

		return convertView;
	}

	private final static class ViewHolder {

		private final WebImageView iv1;
		private final TextView notifUser;
		private final TextView notifText;
		private final TextView notifTime;
		private final WebImageView iv2;
		private NotificationItem item;

		public ViewHolder(WebImageView iv1, TextView tv1,
				TextView tv2, TextView tv3, WebImageView iv2, NotificationItem notification) {
			this.iv1 = iv1;
			this.iv2 = iv2;
			this.notifUser = tv1;
			this.notifText = tv2;
			this.notifTime = tv3;
			this.item = notification;
		}

	}

}