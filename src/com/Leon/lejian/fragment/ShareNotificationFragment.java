package com.Leon.lejian.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.Leon.lejian.R;
import com.Leon.lejian.ShareLocationActivity;
import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.FriendUser;

public class ShareNotificationFragment  extends Fragment implements OnItemClickListener, OnItemLongClickListener {
	private ListView listView = null;
	private Dialog alertDialog = null;
	SimpleAdapter adapter = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_sharnoti, container, false);
		initView(view);
		return view;
	}
	
	private void initView(View view){
		listView  = (ListView) view.findViewById(R.id.sharenoti_listview);
		updateListView();
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
	}
	
	private void updateListView() {
		List<Map<String, Object>> itemps = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < Constants.requestShareUserList.size(); i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			// TODO 更新用户头像
			listItem.put("friend_icon", android.R.drawable.ic_menu_gallery);
			listItem.put("friend_name", Constants.requestShareUserList.get(i).getName());
			listItem.put("share_status", "共享位置中...");
			itemps.add(listItem);
		}

		adapter = new SimpleAdapter(getActivity(), itemps,
				R.layout.sharnoti_listview_item, new String[] {
						"friend_icon", "friend_name",  "share_status"}, new int[] {
						R.id.share_contact_pic, R.id.share_contact_name, R.id.share_status});
		listView.setAdapter(adapter);
	}
	@Override
	public void onResume() {
		updateListView();
		super.onResume();
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//TODO 后期改为数据库
		//
			FriendUser user = Constants.requestShareUserList.get(position);
			Intent intent = new Intent(getActivity(), ShareLocationActivity.class);
			Bundle shareBundle = new Bundle();
			shareBundle.putSerializable("contactUser", user);
			intent.putExtra("contactUser", shareBundle);
			startActivity(intent);
//			Intent intent = new Intent(getActivity(), ShareLocationActivity.class);
//			Bundle shareBundle = new Bundle();
//			shareBundle.putInt("contactUser", position);
//			intent.putExtra("contactUser", shareBundle);
//			startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {
		alertDialog = new AlertDialog.Builder(getActivity()).setNegativeButton("删除共享",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//可以发送退出共享
						alertDialog.dismiss();
						Constants.requestShareUserList.remove(position);
						adapter.notifyDataSetChanged();
					}
				}).create();
		alertDialog.show();
		return true;
	}
}
