package com.Leon.lejian.listener;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.Toast;

import com.Leon.lejian.LoginActivity;
import com.Leon.lejian.adapter.AddFriendListviewAdapter;
import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.FriendUser;
import com.Leon.lejian.service.DatabaseService;
import com.Leon.lejian.view.MyButton;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 单例模式
 * 
 * @author xiao
 * 
 */
public class MyOnClickListener implements OnClickListener {
	private static MyOnClickListener instance = null;
	private static Context context;
	private static FriendUser friendUser;
	private static AddFriendListviewAdapter adapter;
	
	private ProgressDialog myDialog = null;
	private HttpUtils httpUtils = null;
	private MyOnClickListener() {
	}

	public static MyOnClickListener getInstance(AddFriendListviewAdapter adapter, Context context, FriendUser friendUser) {
		if (instance == null)
			instance = new MyOnClickListener();
		MyOnClickListener.context = context;
		MyOnClickListener.friendUser = friendUser;
		MyOnClickListener.adapter = adapter;
		return instance;
	}

	@Override
	public void onClick(View v) {
		int index = ((MyButton)v).getIndex();
		if(!Constants.isNetworkAvailable(MyOnClickListener.context)){
			Toast.makeText(context, "无可用网络, 确认需联网", Toast.LENGTH_SHORT).show();
			return ;
		}
		circle(context, friendUser.getName(), index);
	}
	
	public void circle(Context context2, String requestUserName, int index) {
		myDialog = android.app.ProgressDialog.show(
				context2, null, null);
		agreeAddRequest(context2,myDialog, requestUserName, index);
	}
	
	private void agreeAddRequest(final Context activity,final ProgressDialog myDialog, final String requestUserName,final int index){
		httpUtils = new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// 设置超时时间
		SharedPreferences share = activity.getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
		RequestParams params = new RequestParams();
		JSONObject json = new JSONObject();
		try {
			json.put("clientName", share.getString("app_user", null));
			json.put("requsetUserName", requestUserName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.addBodyParameter("userAgree", json.toString());
		httpUtils.send(HttpMethod.POST, Constants.HOST
				+ Constants.AGREE_PATH, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0,
							String arg1) {
						myDialog.dismiss();
						//TODO 服务器验证
//						Constants.contactUserList.add(friendUser);
//						Constants.requestUserList.remove(index);
						Toast.makeText(activity, "用户不在线", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.i("TEST_REC", "接收到的结果为---》" + arg0.result);
						JSONObject info;
							try {
								info = new JSONObject(arg0.result);
								if(info.getString("saveAgree").equals("true")){
									myDialog.dismiss();
									
									DatabaseService dbService = new DatabaseService(context);
									dbService.createFriendTable();
									dbService.saveFriendInfo(friendUser);
									dbService.close();
//									Constants.contactUserList.add(friendUser);
									Constants.requestUserList.remove(index);
									adapter.notifyDataSetChanged();
									//TODO 保存新朋友到本地数据库, 删除这个朋友在数据库中的请求 
									Toast.makeText(activity, "已添加", Toast.LENGTH_SHORT).show();
								}else{
									myDialog.dismiss();
									Toast.makeText(activity, info.getString("message"), Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
					}
				});
	}

}
