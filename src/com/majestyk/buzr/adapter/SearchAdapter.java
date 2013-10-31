package com.majestyk.buzr.adapter;

import java.util.LinkedList;

import pl.polidea.webimageview.WebImageView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.majestyk.buzr.R;
import com.majestyk.buzr.objects.SearchItem;

public class SearchAdapter extends BaseAdapter {

	private Context mContext;
	private LinkedList<SearchItem> items;

	public SearchAdapter(Context c, LinkedList<SearchItem> l) {
		mContext = c;
		items = l;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public SearchItem getItem(int pos) {
		return items.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup parent) {

		final WebImageView iv1;
		final TextView user;
		final TextView text;

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.search_item, null);
			iv1 = (WebImageView) convertView.findViewById(R.id.image);
			user = (TextView) convertView.findViewById(R.id.user);
			text = (TextView) convertView.findViewById(R.id.desc);
			convertView.setTag(new ViewHolder(iv1, user, text));
		} else {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			iv1 = viewHolder.iv1;
			user = viewHolder.tv1;
			text = viewHolder.tv2;
		}
		
		iv1.setImageResource(R.drawable.temp_profile_image_080);

		iv1.setImageURL(items.get(pos).image);
		user.setText(items.get(pos).username);
		text.setText(items.get(pos).description);

		return convertView;
	}

	private class ViewHolder {

		private final WebImageView iv1;
		private final TextView tv1;
		private final TextView tv2;

		public ViewHolder(WebImageView iv1, TextView tv1, TextView tv2) {
			this.iv1 = iv1;
			this.tv1 = tv1;
			this.tv2 = tv2;
		}

	}

}