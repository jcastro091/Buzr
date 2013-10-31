package com.majestyk.buzr.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.majestyk.buzr.R;
import com.majestyk.buzr.objects.Location;

public class LocationAdapter extends BaseAdapter {

	private Context mContext;
	private List<Location> items;

	public LocationAdapter(Context c, List<Location> list1) {
		mContext = c;
		items = list1;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Location getItem(int pos) {
		return items.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup parent) {

		final TextView tv1;
		final TextView tv2;

	    if (convertView == null) {
	    	convertView = LayoutInflater.from(mContext)
	    			.inflate(R.layout.location, null);
	    	tv1 = (TextView) convertView.findViewById(R.id.action_notification);
	    	tv2 = (TextView) convertView.findViewById(R.id.textView2);
	    	convertView.setTag(new ViewHolder(tv1, tv2));
	        
	    } else {
	        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
	        tv1 = viewHolder.tv1;
			tv2 = viewHolder.tv2;
	    }

		tv1.setText(items.get(pos).getLocationName());
		tv2.setText(items.get(pos).getLocationAddress());
		return convertView;
	}

	private class ViewHolder {

		private final TextView tv1;
		private final TextView tv2;

		public ViewHolder(TextView tv1, TextView tv2) {
			this.tv1 = tv1;
			this.tv2 = tv2;
		}

	}
	
}
