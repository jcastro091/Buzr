package com.majestyk.buzr;

import android.content.Context;
import android.util.AttributeSet;
//import android.view.MotionEvent;
import android.widget.ListView;

public class MyListView extends ListView {

//	private float mInitialX;
//	private float mInitialY;
	
	public MyListView(Context context) {
		super(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		switch( ev.getActionMasked() ){
//		case MotionEvent.ACTION_DOWN:
//			mInitialX = ev.getX();
//			mInitialY = ev.getY();             
//			return false;
//		case MotionEvent.ACTION_MOVE:
//			float deltaX = Math.abs(ev.getX() - mInitialX);
//			float deltaY = Math.abs(ev.getY() - mInitialY);
//			return ( deltaX > 5 || deltaY > 5 );
//		default:
//			return super.onInterceptTouchEvent(ev);
//		}
//	}

}
