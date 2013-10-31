package com.majestyk.buzr.adapter;

import java.util.LinkedList;
import java.util.List;

import pl.polidea.webimageview.WebImageView;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.majestyk.buzr.R;
import com.majestyk.buzr.activities.BUZRAction_ImageProfile;
import com.majestyk.buzr.activities.BUZRAction_UserProfile;
import com.majestyk.buzr.adapter.AdapterManager.MyAdapterInterface;
import com.majestyk.buzr.objects.Notification;
import com.majestyk.buzr.objects.NotificationHeader;
import com.majestyk.buzr.objects.NotificationItem;

public class FeedItemAdapter extends BaseAdapter implements MyAdapterInterface<Notification> {

	private Context mContext;
	private LinkedList<Notification> items;

	public FeedItemAdapter(Context c, LinkedList<Notification> list1) {
		mContext = c;
		items = list1;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup parent) {

		if(items.get(pos) instanceof NotificationItem) {
			final NotificationItem item = (NotificationItem) items.get(pos);

			final ViewHolder tag;

			if (convertView == null) {

				convertView = LayoutInflater.from(mContext)
						.inflate(R.layout.feed_item, null);

				tag = new ViewHolder(
						(WebImageView) convertView.findViewById(R.id.notif_image1), 
						(TextView) convertView.findViewById(R.id.notif_user),
						(TextView) convertView.findViewById(R.id.notif_text),
						(TextView) convertView.findViewById(R.id.notif_time),
						(WebImageView) convertView.findViewById(R.id.notif_image2),
						(ImageView) convertView.findViewById(R.id.notif_emoji));

				convertView.setTag(tag);

				tag.iv1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(mContext, BUZRAction_UserProfile.class);
						intent.putExtra("user_id", (item).getUserID());
						mContext.startActivity(intent);
					}
				});

			} else {
				tag = (ViewHolder) convertView.getTag();
			}

			tag.notifUser.setText((item).getUsername());
			tag.notifText.setText((item).getEvent());
			tag.notifTime.setText((item).getTime());

			if((item).getScenario().equals("a")) { //Follow
				setImage1(tag, item);
				tag.iv2.setImageDrawable(null);
				tag.iv3.setVisibility(View.GONE);
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(mContext, BUZRAction_UserProfile.class);
						intent.putExtra("user_id", (item).getUserID());
						mContext.startActivity(intent);
					}
				});
			}
			else if((item).getScenario().equals("b")) { //Rated photo
				setImage1(tag, item);
				setImage2(tag, item);
				if ((item).getEmoji()==0)
					tag.iv3.setVisibility(View.GONE);
				else
					tag.iv3.setImageResource((item).getEmoji());
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(mContext, BUZRAction_ImageProfile.class);
						intent.putExtra("upload_id", (item).getUploadID());
						intent.putExtra("mode", "stats");
						mContext.startActivity(intent);
					}
				});
			}
			else if((item).getScenario().equals("c")) { //Comment on photo
				setImage1(tag, item);
				setImage2(tag, item);
				tag.iv3.setVisibility(View.GONE);
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(mContext, BUZRAction_ImageProfile.class);
						intent.putExtra("upload_id", (item).getUploadID());
						intent.putExtra("mode", "comments");
						mContext.startActivity(intent);
					}
				});
			}

		} else if(items.get(pos) instanceof NotificationHeader) {
			final NotificationHeader i = (NotificationHeader)items.get(pos);

			final TextView notifText;

			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.notification_header, null);
				notifText = (TextView) convertView.findViewById(R.id.list_header_title);
				convertView.setTag(new ViewHolder(notifText));
			} else {
				ViewHolder viewHolder = (ViewHolder) convertView.getTag();
				notifText = viewHolder.notifText;
			}

			notifText.setText(i.getText());

		}

		return convertView;
	}
	
	public void setImage1(ViewHolder tag, NotificationItem item) {
		if(tag.previousHash1 != (item).getUserHash()) {
			tag.iv1.setImageResource(R.drawable.temp_profile_image_080);
			tag.iv1.setImageURL((item).getUser());
		}
		tag.previousHash1 = (item).getUserHash();
	}
	
	public void setImage2(ViewHolder tag, NotificationItem item) {
		if(tag.previousHash2 != (item).getTargetHash()) {
			tag.iv2.setImageResource(R.drawable.temp_content_image_138);
			tag.iv2.setImageURL((item).getTarget());
		}
		tag.previousHash2 = (item).getTargetHash();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public void addValues(List<Notification> values) {
		items.addAll(values);
		notifyDataSetChanged();

	}

	@Override
	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}

	private class ViewHolder { 
		private final WebImageView iv1;
		private final TextView notifUser;
		private final TextView notifText;
		private final TextView notifTime;
		private final WebImageView iv2;
		private final ImageView iv3;
		private long previousHash1;
		private long previousHash2;

		public ViewHolder(TextView notifText) {
			this.iv1 = null;
			this.notifUser = null;
			this.notifText = notifText;
			this.notifTime = null;
			this.iv2 = null;
			this.iv3 = null;
		}

		public ViewHolder(WebImageView iv1, TextView notifUser, TextView notifText,
				TextView notifTime, WebImageView iv2, ImageView iv3) {
			this.iv1 = iv1;
			this.notifUser = notifUser;
			this.notifText = notifText;
			this.notifTime = notifTime;
			this.iv2 = iv2;
			this.iv3 = iv3;
		}

	}

}