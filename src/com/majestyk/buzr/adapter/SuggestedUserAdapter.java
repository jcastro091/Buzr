package com.majestyk.buzr.adapter;

import java.util.ArrayList;
import java.util.List;

import pl.polidea.webimageview.WebImageView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.majestyk.buzr.R;
import com.majestyk.buzr.objects.JSONImage;
import com.majestyk.buzr.objects.SuggestedUser;

public class SuggestedUserAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<SuggestedUser> items;
	private OnFollowClickListener listener;

	public SuggestedUserAdapter(Context c, ArrayList<SuggestedUser> l, OnFollowClickListener listener) {
		mContext = c;
		items = l;
		this.listener = listener;
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

		final ViewHolder tag;

		if (convertView == null) {

			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.suggested_user, null);

			tag = new ViewHolder(
					(WebImageView) convertView.findViewById(R.id.suggested_image), 
					(TextView) convertView.findViewById(R.id.suggested_user), 
					(TextView) convertView.findViewById(R.id.suggested_text), 
					(WebImageView) convertView.findViewById(R.id.WebImageView1), 
					(WebImageView) convertView.findViewById(R.id.WebImageView2), 
					(WebImageView) convertView.findViewById(R.id.WebImageView3), 
					(ImageButton) convertView.findViewById(R.id.suggested_follow), 
					(RelativeLayout) convertView.findViewById(R.id.RelativeLayout1), 
					items.get(pos));

			tag.suggestButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (tag.view.isSelected()) {
						listener.onAre_FollowClick(tag.item.UserID);
						tag.suggestButton.setBackgroundResource(R.drawable.button_follow_friends);
						tag.view.setSelected(false);
						tag.item.setIsFollowing(false);
					} else if (!tag.view.isSelected()) {
						listener.onNot_FollowClick(tag.item.UserID);
						tag.suggestButton.setBackgroundResource(R.drawable.button_following_friends);
						tag.view.setSelected(true);
						tag.item.setIsFollowing(true);
					}
				}
			});

			convertView.setTag(tag);

		} else {
			tag = (ViewHolder) convertView.getTag();
		}

		tag.item = items.get(pos);

		tag.suggestPic.setImageResource(R.drawable.temp_profile_image_080);
		tag.suggest1.setImageResource(R.drawable.temp_content_image_640);
		tag.suggest2.setImageResource(R.drawable.temp_content_image_640);
		tag.suggest3.setImageResource(R.drawable.temp_content_image_640);

		tag.suggestPic.setImageURL(tag.item.Image);
		tag.suggestName.setText(tag.item.Username);
		tag.suggestText.setText(tag.item.Description);

		List<JSONImage> images = tag.item.getImages();
		tag.suggest1.setImageURL(images.get(0).getURL());
		tag.suggest2.setImageURL(images.get(1).getURL());
		tag.suggest3.setImageURL(images.get(2).getURL());
		
		tag.view.setSelected(tag.item.getIsFollowing());
		
		if (tag.item.getIsFollowing()) {
			tag.suggestButton.setBackgroundResource(R.drawable.button_following_friends);
		} else {
			tag.suggestButton.setBackgroundResource(R.drawable.button_follow_friends);
		}
		
		return convertView;
	}

	private final class ViewHolder {

		private final RelativeLayout view;
		private final WebImageView suggestPic;
		private final TextView suggestName, suggestText;
		private final WebImageView suggest1, suggest2, suggest3;
		private final ImageButton suggestButton;
		private SuggestedUser item;

		public ViewHolder (
				WebImageView suggestPic, TextView suggestName, TextView suggestText,
				WebImageView suggest1, WebImageView suggest2, WebImageView suggest3, 
				ImageButton suggestButton, RelativeLayout view, SuggestedUser item) {

			this.suggestPic = suggestPic;
			this.suggestName = suggestName;
			this.suggestText = suggestText;

			this.suggest1 = suggest1;
			this.suggest2 = suggest2;
			this.suggest3 = suggest3;

			this.suggestButton = suggestButton;

			this.view = view;
			this.item = item;

		}

	}

}