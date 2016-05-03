package com.Leon.lejian;

import org.json.JSONException;
import org.json.JSONObject;

import com.Leon.lejian.api.Constants;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ModifyPasswordActivity extends Activity implements OnClickListener{
	EditText et_oldPassword = null;
	EditText et_newPassword = null;
	Button modifyBtn = null;
	String oldPasswrod;
	String newPasswrod;
	private ProgressDialog myDialog = null;
	private HttpUtils httpUtils = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_password);
		initComponent();
	}
	
	private void initComponent(){
		et_oldPassword = (EditText) findViewById(R.id.editText_oldPassword);
		et_newPassword = (EditText) findViewById(R.id.editText_newPassword);
		modifyBtn = (Button) findViewById(R.id.modifyBtn);
		modifyBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		if(v.getId()==R.id.modifyBtn){
			oldPasswrod = et_oldPassword.getText().toString();
			newPasswrod = et_newPassword.getText().toString();
			if(check()){
				circle(this);
			}
		}
	}
	private boolean check(){
		if(oldPasswrod.isEmpty()){
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
			return false;
		}else if(newPasswrod.isEmpty()){
			Toast.makeText(this, "新密码不能为空", Toast.LENGTH_LONG).show();
			return false;
		}else if(!Constants.isNetworkAvailable(this)){
			Toast.makeText(this, "没有可用网络", Toast.LENGTH_LONG).show();;
			return false;
		}else if(!isLogin(this)){
			Toast.makeText(this, "请登录", Toast.LENGTH_LONG).show();
			return false;
		}else if(isRightPassword(this)){
			Toast.makeText(this, "密码不正确，请重新输入", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	
	private boolean isLogin(Activity activity){
		SharedPreferences share = activity.getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
//		TODO排除没注册的情况
		if(share.getString("app_user", null)==null || (share.getString("app_user", null).length()==0))
		{
			return false;
		}
		return true;
	}
	private boolean isRightPassword(Activity activity){
		SharedPreferences share = activity.getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
//		TODO排除没注册的情况
		if(share.getString("app_user_password", null)==null || (share.getString("app_user_password", null).length()==0))
		{
			return false;
		}else if(!share.getString("app_user_password", null).equals(oldPasswrod)){
			return false;
		}
		return true;
	}
	public void circle(Activity activity) {
		myDialog = android.app.ProgressDialog.show(
				activity, null, null);
		updatePassword(activity,myDialog);
	}
	
	private void updatePassword(final Activity activity,final ProgressDialog myDialog){
		httpUtils = new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// 设置超时时间
		SharedPreferences share = activity.getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
		//在已登录的情况下
		RequestParams params = new RequestParams();
		JSONObject json = new JSONObject();
		try {
			json.put("clientName", share.getString("app_user", null));
			json.put("newPassword", newPasswrod);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.addBodyParameter("userModifyPassword", json.toString());
		httpUtils.send(HttpMethod.POST, Constants.HOST
				+ Constants.MODIFY_USER_PASSWORD, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0,
							String arg1) {
						myDialog.dismiss();
						Toast.makeText(activity, "修改失败", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.i("TEST_REC", "接收到的结果为---》" + arg0.result);
						JSONObject info;
							try {
								info = new JSONObject(arg0.result);
								if(info.getString("savePassword").equals("true")){
									myDialog.dismiss();
									changeUserInfo("app_user_password", newPasswrod);
									Toast.makeText(activity, "密码已修改", Toast.LENGTH_LONG).show();
									finish();
								}else{
									myDialog.dismiss();
									Toast.makeText(activity, "修改失败", Toast.LENGTH_LONG).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
					}
				});
	}
	private void changeUserInfo(String key, String value) {
		SharedPreferences share = getSharedPreferences(
				Constants.SHARE_USERINFO, MODE_PRIVATE);
		SharedPreferences.Editor edit = share.edit(); // 编辑文件
		edit.putString(key, value);
		edit.commit();
	}
	
}
