package com.Leon.lejian;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.RootUser;
import com.Leon.lejian.service.DatabaseService;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class RegisterActivity extends Activity implements OnClickListener {
	Button registerBt = null;
	EditText userEdt = null;
	EditText passwordEdt = null;
	private HttpUtils httpUtils;
	String user = null;
	String password = null;
	RootUser rootUser = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
	}

	private void initView() {
		userEdt = (EditText) findViewById(R.id.registerName);
		passwordEdt = (EditText) findViewById(R.id.RegisterPassword);
		registerBt = (Button) findViewById(R.id.registerBtn);
		registerBt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.registerBtn){
			if (hasError())
				return;
			httpUtils = new HttpUtils();
			try {
				RequestParams params = new RequestParams();
				JSONObject json = new JSONObject();
				json.put("clientName", user);
				json.put("password", password);
				params.addBodyParameter("json", json.toString());
				httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// 设置超时时间
				httpUtils.send(HttpMethod.POST, Constants.HOST+Constants.REGISTER_PATH, params,
						new RequestCallBack<String>() {
					
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(getApplicationContext(), "注册失败",
								Toast.LENGTH_LONG).show();
					}
					
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.i("TEST_REC", "接收到的结果为---》" + arg0.result);
						try {
							JSONObject json = new JSONObject(arg0.result);
							Toast.makeText(getApplicationContext(), json.getString("message"),
									Toast.LENGTH_LONG).show();
							if(json.getString("register").equals("true")){
								Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
								startActivity(intent);
								overridePendingTransition(R.anim.create_zoomin, R.anim.create_zoomout);
								if(!JPushInterface.getConnectionState(getApplicationContext())){
									Log.d(Constants.DEBUG, "未连接，重新连接Jpusn Server服务器");
									JPushInterface.init(getApplicationContext());
									JPushInterface.resumePush(getApplicationContext());
								}
								JPushInterface.setAlias(getApplicationContext(), user,new TagAliasCallback(){
									@Override
									public void gotResult(int arg0,
											String arg1, Set<String> arg2) {
										if(arg0 == 0){
											Toast.makeText(getApplicationContext(), "设置别名成功》别名："+arg1, Toast.LENGTH_SHORT).show();
											Log.d(Constants.DEBUG, "像JPush注册别名成功");
										}
									}
									
								});
								changeUserInfo();
								creeateRelationTable();
								LoginActivity.loginActivity.finish();
								RegisterActivity.this.finish();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	}

	public boolean hasError() {
		user = userEdt.getText().toString().trim();
		password = passwordEdt.getText().toString().trim();
		if (user.isEmpty()) {
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			return true;
		} else if (password.isEmpty()) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return true;
		} 
		return false;
	}
	private void changeUserInfo() {
		SharedPreferences share = getSharedPreferences(
				Constants.SHARE_USERINFO, MODE_PRIVATE);
		SharedPreferences.Editor edit = share.edit(); // 编辑文件
		edit.putString("app_user", user);
		edit.putString("app_user_password", password);
		
		edit.putString("app_user_nickname", null);
		edit.putString("app_user_sex", null);
		edit.putString("app_user_address", null);
		edit.putString("app_user_signature", null);
		edit.putInt("app_user_sharenum", 0);
		edit.putBoolean("app_user_tempshare", false);
		edit.commit();
		
		rootUser = RootUser.getInstance();
		rootUser.setName(user);
		rootUser.setNickname(null);
		rootUser.setSex(null);
		rootUser.setAddress(null);
		rootUser.setSignature(null);
		rootUser.setShareNum(0);
	}
	
	private void creeateRelationTable(){
		DatabaseService dbService = new DatabaseService(this);
//		dbService.dropTable();  //退出时删除用户 关系表
		dbService.createFriendTable();
		dbService.close();
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.finish_zoomin, R.anim.finish_zoomout);
	}
}
