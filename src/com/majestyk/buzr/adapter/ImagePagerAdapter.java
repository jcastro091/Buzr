package com.majestyk.buzr.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.majestyk.buzr.R;

public class ImagePagerAdapter extends PagerAdapter {

	private Context mContext;

	public ImagePagerAdapter(Context context) {
		mContext = context;
	}

	private int[] mImages = new int[] {
			R.drawable.tutorial1,
			R.drawable.tutorial2,
			R.drawable.tutorial3,
			R.drawable.tutorial4,
			R.drawable.tutorial5,
			R.drawable.tutorial6,
			R.drawable.tutorial7,
			R.drawable.tutorial8
	};

	@Override
	public int getCount() {
		return mImages.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((ImageView) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imageView = new ImageView(mContext);
		imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setImageResource(mImages[position]);
		((ViewPager) container).addView(imageView, 0);
		return imageView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((ImageView) object);
	}
}