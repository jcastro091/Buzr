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

import com.majestyk.buzr.FontManager;
import com.majestyk.buzr.R;
import com.majestyk.buzr.activities.BUZRAction_UserProfile;
import com.majestyk.buzr.adapter.AdapterManager.MyAdapterInterface;
import com.majestyk.buzr.objects.Ranking;

public class RankingAdapter extends BaseAdapter implements MyAdapterInterface<Ranking> {

	private Context mContext;
	private List<Ranking> items;

	public RankingAdapter(Context c, List<Ranking> list1) {
		mContext = c;
		items = list1;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Ranking getItem(int pos) {
		return items.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup parent) {

		final ViewHolder tag;

		if (convertView == null) {

			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.ranking, null);

			tag = new ViewHolder(
					(TextView) convertView.findViewById(R.id.rank_rank),
					(TextView) convertView.findViewById(R.id.rank_name),
					(TextView) convertView.findViewById(R.id.rank_count),
					(WebImageView) convertView.findViewById(R.id.rank_image),
					items.get(pos));

			convertView.setTag(tag);

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(mContext, BUZRAction_UserProfile.class);
					intent.putExtra("user_id", tag.item.getUserId());
					mContext.startActivity(intent);
				}
			});

		} else {
			tag = (ViewHolder) convertView.getTag();
		}

		tag.item = items.get(pos);

		FontManager.setTypeFace(tag.tv1);
		FontManager.setTypeFace(tag.tv2);
		FontManager.setTypeFace(tag.tv3);

		tag.tv1.setText(Integer.toString(pos+1));
		tag.tv2.setText(tag.item.getUsername());
		tag.tv3.setText(tag.item.getCount());

		if(tag.previousHash != tag.item.getHash()) {
			tag.iv1.setImageResource(R.drawable.temp_profile_image_100);
			tag.iv1.setImageURL(tag.item.getImage());
		}
		tag.previousHash = tag.item.getHash();

		return convertView;
	}

	@Override
	public void addValues(List<Ranking> values) {
		items.addAll(values);
		notifyDataSetChanged();
	}

	@Override
	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}

	private class ViewHolder {

		private final TextView tv1;
		private final TextView tv2;
		private final TextView tv3;
		private final WebImageView iv1;
		private long previousHash;
		private Ranking item;

		public ViewHolder(TextView tv1, TextView tv2, TextView tv3, WebImageView iv1, Ranking item) {
			this.tv1 = tv1;
			this.tv2 = tv2;
			this.tv3 = tv3;
			this.iv1 = iv1;
			this.item = item;
		}

	}

}
