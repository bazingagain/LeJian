package com.Leon.lejian;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.RootUser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

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

public class LoginActivity extends Activity implements OnClickListener {
	RootUser rootUser= null;
	Button loginBtn = null;
	Button registerBtn = null;
	EditText userName = null;
	EditText password = null;
	String userNameStr = null;
	String passwordStr = null;
	
	String app_user_nickname = null;
	String app_user_sex = null;
	String app_user_address = null;
	String app_user_signature = null;
	private HttpUtils httpUtils;
	public static LoginActivity loginActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
		loginActivity = this;
	}

	public void initView() {
		userName = (EditText) findViewById(R.id.loginUser);
		password = (EditText) findViewById(R.id.loginPassword);
		loginBtn = (Button) findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(this);
		registerBtn = (Button) findViewById(R.id.login_registerBtn);
		registerBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginBtn:
			if (hasError())
				return;
			httpUtils = new HttpUtils();
			try {
				RequestParams params = new RequestParams();
				JSONObject json = new JSONObject();
				json.put("clientName", userNameStr);
				json.put("password", passwordStr);
				params.addBodyParameter("login", json.toString());
				httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// 设置超时时间
				httpUtils.send(HttpMethod.POST, Constants.HOST
						+ Constants.LOGIN_PATH, params,
						new RequestCallBack<String>() {

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
								Toast.makeText(getApplicationContext(), "登录失败",
										Toast.LENGTH_LONG).show();
							}

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								Log.i("TEST_REC", "接收到的结果为---》" + arg0.result);
								JSONObject info;
								try {
									info = new JSONObject(arg0.result);
									Toast.makeText(getApplicationContext(),
											info.getString("message"),
											Toast.LENGTH_LONG).show();
									if (info.getString("login").equals("true")) {
										app_user_nickname = info.getString("nickname");
										app_user_sex = info.getString("sex");
										app_user_address = info.getString("address");
										app_user_signature = info.getString("signature");
										
										Intent intent = new Intent(
												LoginActivity.this,
												MainActivity.class);
										startActivity(intent);
										//回复  JPush  服务
										if (!JPushInterface
												.getConnectionState(getApplicationContext())) {
											Log.d(Constants.DEBUG,
													"未连接，重新连接Jpusn Server服务器");
											JPushInterface
													.init(getApplicationContext());
											JPushInterface.resumePush(getApplicationContext());
										}
										JPushInterface.setAlias(getApplicationContext(), userNameStr,new TagAliasCallback(){
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
										finish();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			break;
		case R.id.login_registerBtn:
			Intent intent = new Intent(LoginActivity.this,
					RegisterActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	public boolean hasError() {
		userNameStr = userName.getText().toString().trim();
		passwordStr = password.getText().toString().trim();
		if (userNameStr.isEmpty()) {
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			return true;
		} else if (passwordStr.isEmpty()) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

	private void changeUserInfo() {
		SharedPreferences share = getSharedPreferences(
				Constants.SHARE_USERINFO, MODE_PRIVATE);
		SharedPreferences.Editor edit = share.edit(); // 编辑文件
		edit.putString("app_user", userNameStr);
		edit.putString("app_user_password", passwordStr);
		edit.putString("app_user_nickname", app_user_nickname);
		edit.putString("app_user_sex", app_user_sex);
		edit.putString("app_user_address", app_user_address);
		edit.putString("app_user_signature", app_user_signature);
		edit.commit();
		rootUser = RootUser.getInstance();
		rootUser.setName(userNameStr);
		rootUser.setNickname(app_user_nickname);
		rootUser.setSex(app_user_sex);
		rootUser.setAddress(app_user_address);
		rootUser.setSignature(app_user_signature);
	}

}
