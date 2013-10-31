package com.majestyk.buzr.adapter;

import java.util.List;

import pl.polidea.webimageview.WebImageView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView.ScaleType;

import com.majestyk.buzr.R;
import com.majestyk.buzr.activities.BUZRAction_ImageProfile;
import com.majestyk.buzr.adapter.ImageGridAdapterManager.MyGridAdapterInterface;
import com.majestyk.buzr.adapter.ImageListAdapterManager.MyListAdapterInterface;
import com.majestyk.buzr.objects.JSONImage;

public class ImageAdapter extends BaseAdapter implements MyListAdapterInterface<JSONImage>, MyGridAdapterInterface<JSONImage> {

	private Context mContext;
	private List<JSONImage> items;
	private Boolean grid_or_list;
	private Boolean user;
	private int screenWidth;

	public ImageAdapter(Context c, List<JSONImage> list, Boolean flag) {
		this(c, list, flag, false);
	}

	@SuppressWarnings("deprecation")
	public ImageAdapter(Context c, List<JSONImage> list, Boolean flag, Boolean user) {
		this.mContext = c;
		this.items = list;
		this.user = user;
		this.grid_or_list = flag;

		Display display = ((Activity)mContext).getWindowManager().getDefaultDisplay();
		screenWidth = display.getWidth();
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public JSONImage getItem(int pos) {
		return items.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup parent) {

		for (JSONImage i : items) {
			System.out.println(i.getUID() + ": " + i.getURL());
		}

		final WebImageView iv;

		if(convertView == null) {
			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.web_image_view, null);
			iv = (WebImageView) convertView.findViewById(R.id.image);

			convertView.setTag(new ViewHolder(iv));
		} else {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			iv = viewHolder.iv;
		}

		if(!grid_or_list) {
			LayoutParams params = iv.getLayoutParams();

			params.width = screenWidth;
			params.height = screenWidth;
			iv.setLayoutParams(params);
		}

		iv.setImageResource(R.drawable.temp_content_image_640);
		iv.setImageURL(items.get(pos).getURL());
		iv.setAdjustViewBounds(true);
		iv.setScaleType(ScaleType.FIT_CENTER);

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mContext, BUZRAction_ImageProfile.class);
				intent.putExtra("upload_id", items.get(pos).getUID());
				intent.putExtra("mode", "stats");
				intent.putExtra("user", user);
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

	@Override
	public void addValues(List<JSONImage> values) {
		for(JSONImage i : values) {
			items.add(new JSONImage(i.getUID(), i.getURL()));
			notifyDataSetChanged();
		}
	}

	@Override
	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}

	private class ViewHolder {

		private final WebImageView iv;

		public ViewHolder(WebImageView iv) {
			this.iv = iv;
		}

	}

}
