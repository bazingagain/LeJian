package com.Leon.lejian.fragment;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.Leon.lejian.ContactProfileActivity;
import com.Leon.lejian.FriendProfileActivity;
import com.Leon.lejian.R;
import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.FriendUser;
import com.Leon.lejian.service.DatabaseService;

public class ContactFragment extends Fragment implements OnItemClickListener{
	private ListView listView = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragent_contact, container, false);
		initView(view);
		return view;
	}
	
	private void initView(View view){
		listView  = (ListView) view.findViewById(R.id.contact_listview);
		updateContact();
		listView.setOnItemClickListener(this);
	}
	@Override
	public void onResume() {
		updateContact();
		super.onResume();
	}
	
	private void updateContact(){
		List<Map<String, Object>> itemps = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < Constants.contactUserList.size(); i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			// TODO 更新用户头像
			listItem.put("friend_icon", android.R.drawable.ic_menu_gallery);
			listItem.put("friend_name", Constants.contactUserList.get(i).getName());
			itemps.add(listItem);
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), itemps,
				R.layout.contact_listview_item, new String[] {
						"friend_icon", "friend_name" }, new int[] {
						R.id.contact_pic, R.id.contact_name });
		listView.setAdapter(simpleAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//TODO 后期改为数据库
		FriendUser user = Constants.contactUserList.get(position);
		Intent intent = new Intent(getActivity(), ContactProfileActivity.class);
		Bundle friendBundle = new Bundle();
		friendBundle.putSerializable("contactUser", user);
		intent.putExtra("showContactUserInfo", friendBundle);
		startActivity(intent);
	}
	
}
