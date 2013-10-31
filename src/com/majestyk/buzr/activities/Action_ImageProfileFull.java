package com.majestyk.buzr.activities;

import pl.polidea.webimageview.WebImageView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.R;

public class Action_ImageProfileFull extends Activity implements OnClickListener {

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_full);
		
		WebImageView imageView = (WebImageView)findViewById(R.id.image);
		
		String image = getIntent().getStringExtra("image");
		String username = getIntent().getStringExtra("username");
		String description = getIntent().getStringExtra("description");
		String location = getIntent().getStringExtra("location");
		String timestamp = getIntent().getStringExtra("timestamp");
		
		((TextView)findViewById(R.id.username)).setText(username+" - ");
		((TextView)findViewById(R.id.timestamp)).setText(GlobalValues.formatTimestamp(timestamp));
		((TextView)findViewById(R.id.description)).setText(description);
		if(location.equals(""))
			((TextView)findViewById(R.id.location)).setVisibility(View.GONE);
		else
			((TextView)findViewById(R.id.location)).setText(location);
		
		imageView.setImageURL(image);

		imageView.setOnClickListener(this);

	}

	@Override
	public final void onClick(View v) {
		finish();
	}

}