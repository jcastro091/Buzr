package com.majestyk.buzr.adapter;

import us.feras.ecogallery.EcoGallery;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.majestyk.buzr.Emojis;

public class EmojiAdapter extends BaseAdapter {

	private Context mContext;

	private static final Integer[] pics = Emojis.getPics();

	public EmojiAdapter(Context c) {
		mContext = c;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	public int getRealCount() {
		return pics.length;
	}

	public static int getRealPosition(int position) {
		return position % pics.length;
	}

	@Override
	public Object getItem(int position) {
		return pics[position];
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ImageView iv = new ImageView(mContext);
		iv.setImageResource(pics[getRealPosition(arg0)]);
		iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		iv.setLayoutParams(new EcoGallery.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		iv.setPadding(0, 16, 0, 16);
		return iv;
	}

}