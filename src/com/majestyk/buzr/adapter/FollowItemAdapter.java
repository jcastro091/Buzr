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

import com.majestyk.buzr.FontManager;
import com.majestyk.buzr.R;
import com.majestyk.buzr.activities.BUZRAction_UserProfile;
import com.majestyk.buzr.adapter.AdapterManager.MyAdapterInterface;
import com.majestyk.buzr.objects.FollowItem;

public class FollowItemAdapter extends BaseAdapter implements MyAdapterInterface<FollowItem> {

	private Context mContext;
	private LinkedList<FollowItem> items;
	private OnFollowClickListener listener;

	public FollowItemAdapter(Context c, LinkedList<FollowItem> l, OnFollowClickListener listener) {
		this.mContext = c;
		this.items = l;
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
					.inflate(R.layout.follow_item, null);
			
			tag = new ViewHolder(
					(WebImageView) convertView.findViewById(R.id.image),
					(TextView) convertView.findViewById(R.id.user),
					(TextView) convertView.findViewById(R.id.desc),
					(ImageView) convertView.findViewById(R.id.imageButton1));

			convertView.setTag(tag);

		} else {
			tag = (ViewHolder) convertView.getTag();
		}
		
		tag.item = items.get(pos);

		FontManager.setTypeFace(tag.profName);
		FontManager.setTypeFace(tag.profDesc);

		if(tag.item.getFLG().equals("0"))
			tag.followBtn.setBackgroundResource(R.drawable.button_follow_friends);

		if(tag.item.getFLG().equals("1"))
			tag.followBtn.setBackgroundResource(R.drawable.button_following_friends);

		tag.followBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(tag.item.getFLG().equals("0")) {
					tag.followBtn.setBackgroundResource(R.drawable.button_following_friends);
					tag.item.setFLG("1");
					listener.onNot_FollowClick(tag.item.getUID());
				} else if(tag.item.getFLG().equals("1")) {
					tag.followBtn.setBackgroundResource(R.drawable.button_follow_friends);
					tag.item.setFLG("0");
					listener.onAre_FollowClick(tag.item.getUID());
				}
			}
		});

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, BUZRAction_UserProfile.class);
				intent.putExtra("user_id", tag.item.getUID());
				mContext.startActivity(intent);
			}
		});

		tag.profPic.setImageResource(R.drawable.temp_profile_image_080);
		tag.profPic.setImageURL(tag.item.getURL());
		tag.profName.setText(tag.item.getUSR());
		tag.profDesc.setText(tag.item.getDES());

		return convertView;
	}

	@Override
	public void addValues(List<FollowItem> values) {
		items.addAll(values);
		notifyDataSetChanged();
	}

	@Override
	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}

	private final class ViewHolder {

		private final WebImageView profPic;
		private final TextView profName;
		private final TextView profDesc;
		private final ImageView followBtn;
		private FollowItem item;

		public ViewHolder(WebImageView profPic, TextView profName,
				TextView profDesc, ImageView followBtn) {
			this.profPic = profPic;
			this.profName = profName;
			this.profDesc = profDesc;
			this.followBtn = followBtn;
		}

	}

}