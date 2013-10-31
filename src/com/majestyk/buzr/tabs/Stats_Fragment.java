package com.majestyk.buzr.tabs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.majestyk.buzr.Emojis;
import com.majestyk.buzr.R;
import com.twotoasters.jazzylistview.JazzyHListView;

public class Stats_Fragment extends Fragment {
	private Bundle mArguments;
	private JazzyHListView listview;
	private String highest;
	private Integer[] buzrEmojis	= Emojis.getPics();
	private String [] buzrColors	= Emojis.getColors();
	private String [] buzrCount		= new String[buzrEmojis.length]; //{ "1", "2", "3", "4", "5", "5", "4", "3", "2", "1" };
	private Integer[] buzrHeight;

	private String bpm, status, total;

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View V = inflater.inflate(R.layout.fragment_stats, container, false);

		listview = (JazzyHListView)V.findViewById(R.id.listview);
		listview.setSelector(R.color.clear);

		mArguments = savedInstanceState;
		String result = getArguments().getString("result");

		if (!result.equals("")) {
			JSONObject jObject;
			try {
				jObject = new JSONObject(result.trim());
				System.out.println(jObject);

				JSONObject j = jObject.getJSONObject("stats");

				buzrCount[0] = j.getString("n1");	buzrCount[1] = j.getString("n2"); 
				buzrCount[2] = j.getString("n3");	buzrCount[3] = j.getString("n4"); 
				buzrCount[4] = j.getString("n5");	buzrCount[5] = j.getString("n6");
				buzrCount[6] = j.getString("n7");	buzrCount[7] = j.getString("n8");
				buzrCount[8] = j.getString("n9");	buzrCount[9] = j.getString("n10");

				bpm = j.getString("bpm");
				status = j.getString("status");
				total = j.getString("total");

				List<String> b = Arrays.asList(buzrCount);
				highest = (Collections.max(b));
				if(highest.equals("0")) 
					highest = "1";

				buzrHeight = new Integer[buzrCount.length];
				updateSizeInfo();

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return V;
	}

	private final class bsAdapter extends BaseAdapter {
		Fragment mContext;
		String[] array;

		public bsAdapter(Fragment context, String[] arr) {
			this.mContext = context;
			this.array = arr;
		}

		public int getCount() {
			return array.length;
		}

		public Object getItem(int position) {
			return array[position];
		}

		public long getItemId(int position) {
			return array.length;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {

			final ViewHolder tag;

			if (convertView == null) {

				LayoutInflater inflater = mContext.getLayoutInflater(mArguments);
				convertView = inflater.inflate(R.layout.simplerow, null);

				tag = new ViewHolder(
						(ImageView)convertView.findViewById(R.id.title), 
						(TextView)convertView.findViewById(R.id.count), 
						(TextView)convertView.findViewById(R.id.colortext01));

				convertView.setTag(tag);

			} else {
				tag = (ViewHolder) convertView.getTag();
			}

			tag.title.setImageDrawable(getResources().getDrawable(buzrEmojis[Emojis.getRealPosition(position)]));
			tag.column.setHeight(buzrHeight[Emojis.getRealPosition(position)]);
			tag.count.setText(buzrCount[Emojis.getRealPosition(position)]);

			tag.column.setBackgroundColor(Color.parseColor(buzrColors[Emojis.getRealPosition(position)]));

			return convertView;
		}
	}

	private final void updateSizeInfo() {
		int h = 235;

		for(int i = 0; i < buzrEmojis.length; i++) {
			buzrHeight[i] = (int)((h*Integer.parseInt(buzrCount[i]))/Integer.parseInt(highest));
			System.out.println("gross width[i] " + buzrHeight[i]);
		}

		LayoutInflater inflater = getLayoutInflater(mArguments);
		View header = inflater.inflate(R.layout.list_header, (ViewGroup)getActivity().findViewById(R.id.header_layout_root));
		listview.addHeaderView(header, null, false);

		listview.setAdapter(new bsAdapter(this, buzrCount));

		if(status.equals("Weak"))
			((TextView)header.findViewById(R.id.status)).setTextColor(Color.parseColor("#568EFF"));
		else if(status.equals("Solid"))
			((TextView)header.findViewById(R.id.status)).setTextColor(Color.parseColor("#952BFF"));
		else if(status.equals("Cool"))
			((TextView)header.findViewById(R.id.status)).setTextColor(Color.parseColor("#EB46FF"));
		else if(status.equals("Sick"))
			((TextView)header.findViewById(R.id.status)).setTextColor(Color.parseColor("#FF5656"));
		else if(status.equals("Legendary"))
			((TextView)header.findViewById(R.id.status)).setTextColor(Color.parseColor("#FFA700"));

		((TextView)header.findViewById(R.id.bpm)).setText(bpm);
		((TextView)header.findViewById(R.id.status)).setText(status);
		((TextView)header.findViewById(R.id.buzr_total)).setText(total);
	}

	private final class ViewHolder {

		private final ImageView title;
		private final TextView count;
		private final TextView column;

		public ViewHolder (ImageView title, TextView count, TextView column) {
			this.title = title;
			this.count = count;
			this.column = column;
		}

	}

}
