package com.Leon.lejian.fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.Leon.lejian.ContactProfileActivity;
import com.Leon.lejian.R;
import com.Leon.lejian.adapter.ContactListviewAdapter;
import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.FriendUser;
import com.Leon.lejian.service.DatabaseService;

public class ContactFragment extends Fragment implements OnItemClickListener {
	private ListView listView = null;
	private static ArrayList<FriendUser> allFriend = null;
	private ContactListviewAdapter contAdapter = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragent_contact, container, false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		listView = (ListView) view.findViewById(R.id.contact_listview);
//		updateListView(Constants.contactUserList);
		updateListView(getContactFromDatabase());
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onResume() {
//		updateListView(Constants.contactUserList);
		updateListView(getContactFromDatabase());
		super.onResume();
	}
	
	private void updateListView(ArrayList<FriendUser> contactUserList) {
		contAdapter = new ContactListviewAdapter(this.getActivity(), contactUserList);
		listView.setAdapter(contAdapter);
	}
	
	private ArrayList<FriendUser> getContactFromDatabase(){
		DatabaseService dbService = new DatabaseService(this.getActivity());
		dbService.createFriendTable();
		allFriend =  dbService.findAllFriendInfo();
		dbService.close();
		return allFriend;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		FriendUser user = allFriend.get(position);
		Intent intent = new Intent(getActivity(), ContactProfileActivity.class);
		Bundle friendBundle = new Bundle();
		friendBundle.putSerializable("contactUser", user);
		intent.putExtra("showContactUserInfo", friendBundle);
		startActivity(intent);
	}
	
}
