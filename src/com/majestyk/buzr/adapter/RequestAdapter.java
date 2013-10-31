package com.majestyk.buzr.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.majestyk.buzr.R;
import com.majestyk.buzr.objects.Request;

public class RequestAdapter extends BaseAdapter {

	public interface onAcceptOrDeclineListener {
		public void onClick(boolean flag, int pos, String id);
	}
	
	private Context mContext;
	private LinkedList<Request> items;
	private onAcceptOrDeclineListener listener;

	public RequestAdapter(Context c, LinkedList<Request> l, onAcceptOrDeclineListener listener) {
		this.mContext = c;
		this.items = l;
		this.listener = listener;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Request getItem(int pos) {
		return items.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup parent) {

		final TextView notifUser;
		final TextView notifDesc;
		final ImageView iv1;
		final ImageView iv2;

	    if (convertView == null) {
	    	convertView = LayoutInflater.from(mContext)
	    			.inflate(R.layout.request, null);
	    	notifUser = (TextView) convertView.findViewById(R.id.request_username);
	    	notifDesc = (TextView) convertView.findViewById(R.id.request_description);
	    	iv1 = (ImageView) convertView.findViewById(R.id.accept);
	    	iv2 = (ImageView) convertView.findViewById(R.id.decline);
	        
	    	convertView.setTag(new ViewHolder(notifUser, notifDesc, iv1, iv2));
	        
	    } else {
	        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
	        notifUser = viewHolder.notifUser;
	        notifDesc = viewHolder.notifDesc;
	        iv1 = viewHolder.iv1;
	        iv2 = viewHolder.iv2;
	    }

		notifUser.setText(items.get(pos).getUsername());
		notifDesc.setText(items.get(pos).getDescription());

		final View view = convertView;
		
		iv1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClick(true, pos, items.get(pos).getFollowID());
			}
		});
		iv2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClick(false, pos, items.get(pos).getFollowID());
			}
		});
		return view;
	}

	private class ViewHolder {

		private final TextView notifUser;
		private final TextView notifDesc;
		private final ImageView iv1;
		private final ImageView iv2;

		public ViewHolder(TextView notifUser, TextView notifDesc,
				ImageView iv1, ImageView iv2) {
			this.notifUser = notifUser;
			this.notifDesc = notifDesc;
			this.iv1 = iv1;
			this.iv2 = iv2;
		}

	}

}