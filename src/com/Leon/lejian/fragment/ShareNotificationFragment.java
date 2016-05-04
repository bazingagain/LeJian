package com.Leon.lejian.fragment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.Leon.lejian.R;
import com.Leon.lejian.ShareLocationActivity;
import com.Leon.lejian.adapter.ShareLocListViewAdapter;
import com.Leon.lejian.api.Constants;
import com.Leon.lejian.service.DatabaseService;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class ShareNotificationFragment extends Fragment implements
		OnItemClickListener, OnItemLongClickListener {
	private ListView listView = null;
	private Dialog alertDialog = null;
	ShareLocListViewAdapter adapter = null;
	HttpUtils httpUtils = null;
	SharedPreferences share = null;
	private static ArrayList<String> allShareFriend = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sharnoti, container,
				false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		listView = (ListView) view.findViewById(R.id.sharenoti_listview);
		updateListView();
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
	}

	private ArrayList<String> getShareFriendFromDatabase() {
		ArrayList<String> allFriendName = null;
		DatabaseService dbService = new DatabaseService(this.getActivity());
		dbService.createShareLocationTable();
		allFriendName = dbService.findAllShareLocationName();
		dbService.close();
		return allFriendName;
	}

	private void updateListView() {
		allShareFriend = getShareFriendFromDatabase();
		adapter = new ShareLocListViewAdapter(getActivity(), allShareFriend);
		listView.setAdapter(adapter);
		httpUtils = new HttpUtils();
		share = getActivity().getSharedPreferences(Constants.SHARE_USERINFO,
				getActivity().MODE_PRIVATE);
	}

	@Override
	public void onResume() {
		super.onResume();
		updateListView();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (!Constants.isNetworkAvailable(getActivity())) {
			Toast.makeText(getActivity(), "无可用网络", Toast.LENGTH_SHORT).show();
			return;
		}
		// TODO 后期改为数据库
		// FriendUser user = Constants.requestShareUserList.get(position);
		DatabaseService dbService = new DatabaseService(getActivity());
			dbService.createShareLocationTable();
			String shareName = allShareFriend.get(position);
			if (dbService.getShareLocationType(shareName) == Constants.TYPE_FROME_ME) {
				// TODO 我发起的请求
				dbService.close();
				comeShareActivity(shareName);

			} else if (dbService.getShareLocationType(shareName) == Constants.TYPE_FROME_OTHER) {
				if (dbService.getShareLocationStatus(shareName) == Constants.STATUS_OFFLINE) {
					showComeShareDialog(dbService, shareName);
				} else if (dbService.getShareLocationStatus(shareName) == Constants.STATUS_ONLINE) {
					dbService.close();
					comeShareActivity(shareName);
				}
			}
		

	}

	private void comeShareActivity(String name) {
		Intent intent = new Intent(getActivity(), ShareLocationActivity.class);
		Bundle shareBundle = new Bundle();
		shareBundle.putString("contactUserName", name);
		intent.putExtra("contactUserName", shareBundle);
		startActivity(intent);
	}

	private void showComeShareDialog(final DatabaseService dbService,
			final String name) {
		new AlertDialog.Builder(getActivity())
				.setMessage("加入位置分享？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dbService.updateShareLocationStatus(name,
								Constants.STATUS_ONLINE);
						dbService.close();
						comeShareActivity(name);
						agreeAddRequest(getActivity(), name);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {
		alertDialog = new AlertDialog.Builder(getActivity()).setNegativeButton(
				"删除共享", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 可以发送退出共享
						alertDialog.dismiss();
						// Constants.requestShareUserList.remove(position);
						DatabaseService dbService = new DatabaseService(
								getActivity());
						dbService.createShareLocationTable();
						dbService.deleteShareLocationInfo(position);
						dbService.close();
						// 更新allshare
						allShareFriend = getShareFriendFromDatabase();
						adapter.notifyDataSetChanged();
						updateListView();
					}
				}).create();
		alertDialog.show();
		return true;
	}

	private void agreeAddRequest(final Context activity, String name) {
		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// 设置超时时间
		RequestParams params = new RequestParams();
		JSONObject json = new JSONObject();
		try {
			json.put("clientName", share.getString("app_user", null));
			json.put("agreeShareUserName", name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.addBodyParameter("agreeShareLoc", json.toString());
		httpUtils.send(HttpMethod.POST, Constants.HOST
				+ Constants.AGREE_SHARE_MY_LOCATION, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO 服务器验证
						Toast.makeText(activity, "失败", Toast.LENGTH_SHORT)
								.show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.i("TEST_REC", "接收到的结果为---》" + arg0.result);
						JSONObject info;
						try {
							info = new JSONObject(arg0.result);
							if (info.getString("agreeShareLoc").equals("true")) {
								Toast.makeText(activity, "已同意共享",
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}
}
