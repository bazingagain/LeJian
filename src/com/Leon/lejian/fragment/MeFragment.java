package com.Leon.lejian.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.Leon.lejian.LoginActivity;
import com.Leon.lejian.R;
import com.Leon.lejian.UserProfile;
import com.Leon.lejian.UserSetting;
import com.Leon.lejian.api.Constants;
import com.Leon.lejian.service.DatabaseService;

public class MeFragment extends Fragment implements OnClickListener {
	LinearLayout userProfile = null;
	LinearLayout userSetting = null;
	LinearLayout userLogout = null;
	TextView userName = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_me, container, false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		userName = (TextView) view.findViewById(R.id.user_name);
		SharedPreferences share = getActivity().getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
		userName.setText(check(share.getString("app_user", null))?"用户名":share.getString("app_user", null));
		userProfile = (LinearLayout) view.findViewById(R.id.user_profile);
		userProfile.setOnClickListener(this);
		userSetting = (LinearLayout) view.findViewById(R.id.user_setting);
		userSetting.setOnClickListener(this);
		userLogout = (LinearLayout) view.findViewById(R.id.user_logout);
		userLogout.setOnClickListener(this);
	}
	
	private boolean check(String str){
		if(str ==null || str.length()==0)
			return true;
		return false;
	}
	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences share = getActivity().getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
		userName.setText(check(share.getString("app_user", null))?"用户名":share.getString("app_user", null));
	}


	@Override
	public void onClick(View v) {
		Intent intent = null;
		Log.d(Constants.DEBUG, "COM");
		if(v.getId()== R.id.user_profile){
			
			Log.d(Constants.DEBUG, "USER_PROFILE");
			intent = new Intent(getActivity(), UserProfile.class);
			startActivity(intent);
		}
		else if(v.getId()==R.id.user_setting){
			
			intent = new Intent(getActivity(), UserSetting.class);
			startActivity(intent);
		}
		
		else if(v.getId() == R.id.user_logout){
			
			new AlertDialog.Builder(getActivity())
					.setMessage("确定退出登录？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									
									Intent intent = new Intent(getActivity(),
											LoginActivity.class);
									startActivity(intent);
									//停止接收
									JPushInterface.stopPush(getActivity());
									changeUserInfo();
									deleteRelationTable();
									getActivity().finish();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).show();
		}
	}
	private void changeUserInfo() {
		SharedPreferences share = getActivity().getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = share.edit(); // 编辑文件
		edit.putString("app_user", null);
		edit.commit();
	}
	
	private void deleteRelationTable(){
		DatabaseService dbService = new DatabaseService(this.getActivity());
		dbService.dropTable("friends");  //退出时删除用户 关系表
		dbService.close();
	}

}
