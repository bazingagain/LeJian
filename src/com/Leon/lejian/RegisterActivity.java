package com.Leon.lejian;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.Leon.lejian.api.Constants;
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

public class RegisterActivity extends Activity implements OnClickListener {
	Button registerBt = null;
	EditText userEdt = null;
	EditText passwordEdt = null;
	EditText emailEdt = null;
	private HttpUtils httpUtils;
	String user = null;
	String password = null;
	String email = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
	}

	private void initView() {
		userEdt = (EditText) findViewById(R.id.registerName);
		passwordEdt = (EditText) findViewById(R.id.RegisterPassword);
		emailEdt = (EditText) findViewById(R.id.registerEmail);
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
								//
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
		email = emailEdt.getText().toString().trim();
		if (user.isEmpty()) {
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			return true;
		} else if (password.isEmpty()) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return true;
		} else if (email.isEmpty()) {
			Toast.makeText(this, "注册邮箱不能为空", Toast.LENGTH_SHORT).show();
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
		edit.commit();
	}
	
}
