package com.majestyk.buzr.adapter;

import java.util.LinkedList;

import pl.polidea.webimageview.WebImageView;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.majestyk.buzr.FontManager;
import com.majestyk.buzr.R;
import com.majestyk.buzr.activities.BUZRAction_UserProfile;
import com.majestyk.buzr.objects.Friend;
import com.majestyk.buzr.objects.FriendHeader;
import com.majestyk.buzr.objects.FriendItem;

public class FriendAdapter extends BaseAdapter {

	private Context mContext;
	private LinkedList<Friend> items;
	private OnFriendClickListener listener;

	public FriendAdapter(Context c, LinkedList<Friend> list, OnFriendClickListener oicl) {
		mContext = c;
		items = list;
		listener = oicl;
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
		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			switch (getItemViewType(pos)) {
			case 0:
				final FriendItem friendItem = (FriendItem) items.get(pos);
				convertView = LayoutInflater.from(mContext).inflate(R.layout.friend_item, null);
				holder.profPic = (WebImageView) convertView.findViewById(R.id.image);
				holder.profName = (TextView) convertView.findViewById(R.id.user);
				holder.followBtn = (ImageButton) convertView.findViewById(R.id.imageButton1);
				
				final ImageButton btn = holder.followBtn;
				holder.followBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {						
						if (friendItem.getRegistered()) {
							if (friendItem.getFollowing()) {
								listener.onAre_FollowClick(pos, btn);
							} else if (!friendItem.getFollowing()) {
								listener.onNot_FollowClick(pos, btn);
							}
						} else if (!friendItem.getRegistered()) {
							if (friendItem.getFollowing()) { }
							else if (!friendItem.getFollowing()) {
								listener.onInviteUserClick(friendItem.getSocialID());
							}
						}
					}
				});
				
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (friendItem.getRegistered() || friendItem.getFollowing()) {
							Intent intent = new Intent(mContext, BUZRAction_UserProfile.class);
							intent.putExtra("user_id", friendItem.getUserID());
							mContext.startActivity(intent);
						}
					}
				});

				FontManager.setTypeFace(holder.profName);

				holder.profPic.setImageResource(R.drawable.empty_image_136);
				holder.profPic.setImageURL(friendItem.getImage());
				holder.profName.setText(friendItem.getName());

				if (friendItem.getRegistered()) {
					if (friendItem.getFollowing()) {
						holder.followBtn.setBackgroundResource(R.drawable.button_following_friends);
					} else if (!friendItem.getFollowing()) {
						holder.followBtn.setBackgroundResource(R.drawable.button_follow_friends);
					}
				} else if (!friendItem.getRegistered()) {
					if (friendItem.getFollowing()) {
						holder.followBtn.setBackgroundResource(R.drawable.requested);
					} else if (!friendItem.getFollowing()) {
						holder.followBtn.setBackgroundResource(R.drawable.invite);
					}
				}
				break;
			case 1:
				final FriendHeader headerItem = (FriendHeader) items.get(pos);
				convertView = LayoutInflater.from(mContext).inflate(R.layout.friend_header, null);
				holder.profName = (TextView) convertView.findViewById(R.id.list_header_title);
				holder.profName.setText(headerItem.Text);
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
			switch(getItemViewType(pos)) {
			case 0:
				final FriendItem friendItem = (FriendItem) items.get(pos); 

				final ImageButton btn = holder.followBtn;
				holder.followBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {						
						if (friendItem.getRegistered()) {
							if (friendItem.getFollowing()) {
								listener.onAre_FollowClick(pos, btn);
							} else if (!friendItem.getFollowing()) {
								listener.onNot_FollowClick(pos, btn);
							}
						} else if (!friendItem.getRegistered()) {
							if (friendItem.getFollowing()) { }
							else if (!friendItem.getFollowing()) {
								listener.onInviteUserClick(friendItem.getSocialID());
							}
						}
					}
				});
				
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (friendItem.getRegistered() || friendItem.getFollowing()) {
							Intent intent = new Intent(mContext, BUZRAction_UserProfile.class);
							intent.putExtra("user_id", friendItem.getUserID());
							mContext.startActivity(intent);
						}
					}
				});

				FontManager.setTypeFace(holder.profName);

				holder.profPic.setImageResource(R.drawable.empty_image_136);
				holder.profPic.setImageURL(friendItem.getImage());
				holder.profName.setText(friendItem.getName());

				if (friendItem.getRegistered()) {
					if (friendItem.getFollowing()) {
						holder.followBtn.setBackgroundResource(R.drawable.button_following_friends);
					} else if (!friendItem.getFollowing()) {
						holder.followBtn.setBackgroundResource(R.drawable.button_follow_friends);
					}
				} else if (!friendItem.getRegistered()) {
					if (friendItem.getFollowing()) {
						holder.followBtn.setBackgroundResource(R.drawable.requested);
					} else if (!friendItem.getFollowing()) {
						holder.followBtn.setBackgroundResource(R.drawable.invite);
					}
				}
				break;
			case 1:
				FriendHeader headerItem = (FriendHeader) items.get(pos);
				holder.profName.setText(headerItem.Text);
				break;
			}
		}
		return convertView;	//http://bartinger.at/listview-with-sectionsseparators/
							//http://chrislee.kr/wp/tag/getitemviewtype-tutorial/
	}

	private class ViewHolder {

		private WebImageView profPic;
		private TextView profName;
		private ImageButton followBtn;

		public ViewHolder() { }

	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (items.get(position) instanceof FriendItem) {
			return 0;
		} else  { 			  //instanceof FriendHeader) 
			return 1;
		}
	}

}