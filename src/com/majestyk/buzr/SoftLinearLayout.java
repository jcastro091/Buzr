package com.majestyk.buzr;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SoftLinearLayout extends LinearLayout {

	Context mContext;

	public SoftLinearLayout(Context context) {
		super(context);
		mContext = context;
	}

	public SoftLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public SoftLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int proposedheight = MeasureSpec.getSize(heightMeasureSpec);
		final int actualHeight = getHeight();

		if (actualHeight > proposedheight){
			// Keyboard is shown
		} else {
			// Keyboard is hidden
			((Activity)mContext).finish();
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
