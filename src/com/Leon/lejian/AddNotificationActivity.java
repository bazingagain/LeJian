package com.Leon.lejian;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.Leon.lejian.adapter.AddFriendListviewAdapter;
import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.FriendUser;

public class AddNotificationActivity extends Activity implements
		OnItemLongClickListener, OnItemClickListener {
	ListView listview = null;
	Dialog alertDialog = null;
	AddFriendListviewAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnotification);
		listview = (ListView) findViewById(R.id.notify_listview);
		updateListView(Constants.requestUserList);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, FriendProfileActivity.class);
		Bundle friendBundle = new Bundle();
		friendBundle.putSerializable("friend",
				Constants.requestUserList.get(position));
		intent.putExtra("showRequstUserInfo", friendBundle);
		startActivity(intent);
	}

	private void updateListView(ArrayList<FriendUser> requestUserList) {
		adapter = new AddFriendListviewAdapter(this, requestUserList);
		listview.setAdapter(adapter);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {

		alertDialog = new AlertDialog.Builder(this).setNegativeButton("É¾³ýÇëÇó",
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
						Constants.requestUserList.remove(position);
						adapter.notifyDataSetChanged();
					}
				}).create();
		alertDialog.show();
		return true;
	}

}
