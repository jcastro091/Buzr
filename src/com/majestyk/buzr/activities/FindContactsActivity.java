package com.majestyk.buzr.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.widget.ImageButton;
import android.widget.ListView;

import com.majestyk.buzr.R;
import com.majestyk.buzr.adapter.ContactAdapter;
import com.majestyk.buzr.adapter.OnContactClickListener;
import com.majestyk.buzr.apis.API_Find;
import com.majestyk.buzr.apis.OnTaskCompleteResultListener;
import com.majestyk.buzr.objects.ContactItem;
import com.majestyk.buzr.objects.Friend;
import com.majestyk.buzr.objects.FriendHeader;

public class FindContactsActivity extends Activity implements OnContactClickListener {

	private ArrayList<Long> contactIds;
	private HashMap<Long, Contact> contactList;
	private LinkedList<ContactItem> areFollowingList;
	private LinkedList<ContactItem> notFollowingList;
	private LinkedList<ContactItem> areRequestedList;
	private LinkedList<ContactItem> unregisteredList;

	private ContactAdapter friendAdapter;
	private LinkedList<Friend> friendList;
	private ListView friendListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends_facebook_twitter);

		contactIds = new ArrayList<Long>();
		contactList = new HashMap<Long, Contact>();

		areFollowingList = new LinkedList<ContactItem>();
		notFollowingList = new LinkedList<ContactItem>();
		areRequestedList = new LinkedList<ContactItem>();
		unregisteredList = new LinkedList<ContactItem>();

		friendList = new LinkedList<Friend>();
		friendAdapter = new ContactAdapter(this, friendList, this);

		friendListView = ((ListView)findViewById(R.id.friends));
		friendListView.setAdapter(friendAdapter);

		getContactList();
		checkEmailList();

	}

	public void getContactList() {
		ContentResolver resolver = getContentResolver();
		Cursor c = resolver.query(
		        Data.CONTENT_URI,
		        null,
		        Data.HAS_PHONE_NUMBER + "!=0 AND (" + Data.MIMETYPE + "=? OR " + Data.MIMETYPE + "=?)", 
		        new String[] {Email.CONTENT_ITEM_TYPE, Phone.CONTENT_ITEM_TYPE},
		        Data.CONTACT_ID);

		while (c.moveToNext()) {
		    long id = c.getLong(c.getColumnIndex(Data.CONTACT_ID));
		    String name = c.getString(c.getColumnIndex(Data.DISPLAY_NAME));
		    String data1 = c.getString(c.getColumnIndex(Data.DATA1));

		    System.out.println(id + ", name=" + name + ", data1=" + data1);
		    if(!contactList.containsKey(id)) {
		    	Contact item = new Contact(name);
		    	item.email = data1;
		    	contactList.put(id, item);
		    	contactIds.add(id);
		    } else {
		    	contactList.get(id).phone = data1;
		    }
		}
		System.out.println(contactList);
	}

	public void checkEmailList() {

		String input = "";
		
		for (Contact contact : contactList.values()) {
			input += contact.email + ",";
		}
		
		input = input.substring(0, input.lastIndexOf(","));
		
		API_Find task = new API_Find(this, new OnTaskCompleteResultListener() {

			@Override
			public void onComplete(boolean success, String result) {
				if(success)
					getList(result);
			}
			
		});

		task.execute("user/find", input);

	}

	public void getList(String result) {
		areFollowingList.clear();
		notFollowingList.clear();
		areRequestedList.clear();
		unregisteredList.clear();
		friendList.clear();
		
		System.out.println("absefhalf");

		try {
			JSONObject j = new JSONObject(result.trim());
			JSONArray are_following = new JSONArray(
					j.getString("are_following"));

			for (int i = 0; i < are_following.length(); i++) {
				JSONObject jObject = are_following.getJSONObject(i); 

				System.out.println(jObject);

				areFollowingList.add(new ContactItem(
						jObject.getString("user_id"),
						jObject.getString("username"),
						jObject.getString("email"),
						jObject.getString("image"),
						jObject.getString("privacy"),
						findContact(jObject.getString("username")),
						true, true));

			}

			friendList.add(new FriendHeader("following"));
			friendList.addAll(areFollowingList);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < contactList.size(); i++) {
			unregisteredList.add(new ContactItem(
					"",
					contactList.get(contactIds.get(i)).name,
					"",
					"",
					contactList.get(contactIds.get(i)).email,
					contactList.get(contactIds.get(i)).phone,
					false, false));
		}
		
		if(!unregisteredList.isEmpty()) {
			friendList.add(new FriendHeader("invite"));
			friendList.addAll(unregisteredList);
		}

		try {
			JSONObject j = new JSONObject(result.trim());
			JSONArray are_following = new JSONArray(
					j.getString("requested"));

			for (int i = 0; i < are_following.length(); i++) {
				JSONObject jObject = are_following.getJSONObject(i); 

				System.out.println(jObject);

				areRequestedList.add(new ContactItem(
						jObject.getString("user_id"),
						jObject.getString("username"),
						jObject.getString("email"),
						jObject.getString("image"),
						jObject.getString("privacy"),
						findContact(jObject.getString("username")),
						true, false));

			}

			friendList.add(new FriendHeader("requested"));
			friendList.addAll(areRequestedList);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			JSONObject j = new JSONObject(result.trim());
			JSONArray are_following = new JSONArray(
					j.getString("not_following"));

			for (int i = 0; i < are_following.length(); i++) {
				JSONObject jObject = are_following.getJSONObject(i); 

				System.out.println(jObject);

				notFollowingList.add(new ContactItem(
						jObject.getString("user_id"),
						jObject.getString("username"),
						jObject.getString("email"),
						jObject.getString("image"),
						jObject.getString("privacy"),
						findContact(jObject.getString("username")),
						false, true));

			}

			friendList.add(new FriendHeader("not following"));
			friendList.addAll(notFollowingList);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		friendAdapter.notifyDataSetChanged();
		System.out.println("MY LIST IS " + friendList.isEmpty());

	}

	@Override
	public void onAre_FollowClick(ContactItem user, ImageButton button) {
		button.setBackgroundResource(R.drawable.button_follow_friends);
		user.setFollowing(false);
	}

	@Override
	public void onNot_FollowClick(ContactItem user, ImageButton button) {
		button.setBackgroundResource(R.drawable.button_following_friends);
		user.setFollowing(true);
	}

	@Override
	public void onInviteUserClick(String phone) {
		Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone));
		sendIntent.putExtra("sms_body", "Explore my photos on buzr! \nDownload the free app from the app store http://www.buzrapp.com/download");
		startActivity(sendIntent);		
	}
	
	private final class Contact {
		public final String name;
		public String phone;
		public String email;
		
		private Contact(String name) {
			this.name = name;
		}
		
		private Contact(String name, String phone, String email) {
			this.name = name;
			this.phone = phone;
			this.email = email;
		}
		
		public String toString() {
			return "name: " + this.name + ", email: " + this.email + ", phone: " + this.phone;
		}

	}
	
	public String findContact(String name) {
		
		String phone = "";
		
		for(Contact person : contactList.values()) {
			if (person.name == name) {
				phone = person.phone; 
			}
		}
		
		return phone;
	}
	
}
