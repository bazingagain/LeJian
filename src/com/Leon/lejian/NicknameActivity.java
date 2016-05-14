package com.Leon.lejian;

import org.json.JSONException;
import org.json.JSONObject;

import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.RootUser;
import com.Leon.lejian.util.ActionbarBackUtil;
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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NicknameActivity extends Activity implements OnClickListener{
	private Button saveBtn = null;
	private EditText nicknameText = null;
	private ProgressDialog myDialog = null;
	private HttpUtils httpUtils = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nickname);
		ActionbarBackUtil.setActionbarBack(this, R.string.nickname,
				R.drawable.back_n);
		saveBtn = (Button) findViewById(R.id.save_nickname);
		saveBtn.setOnClickListener(this);
		nicknameText = (EditText) findViewById(R.id.editText_nickname);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.save_nickname) {
			if (isNetworkAvailable(this)) {
				if(!nicknameText.getText().toString().isEmpty()){
					circle(this);
				}
				else if(nicknameText.getText().toString().isEmpty()){
					SharedPreferences share = getSharedPreferences(
							Constants.SHARE_USERINFO, MODE_PRIVATE);
					
					if(share.getString("app_user_nickname", null).isEmpty()){
						finish();
						return ;
					}else{
						//本身不为空，设置为空
						circle(this);
					}
				}
			} else {
				Toast.makeText(this, "当前没用可用网络", Toast.LENGTH_LONG).show();
			}
		}
	}

	public void circle(Activity activity) {
		myDialog = android.app.ProgressDialog.show(
				activity, null, null);
		updateSignature(activity,myDialog);
	}
	
	private void updateSignature(final Activity activity,final ProgressDialog myDialog){
		httpUtils = new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// 设置超时时间
		SharedPreferences share = activity.getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
//		TODO排除没注册的情况
		if(share.getString("app_user", null)==null || (share.getString("app_user", null).length()==0))
		{
			Toast.makeText(this, "请登录", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		RequestParams params = new RequestParams();
		JSONObject json = new JSONObject();
		try {
			json.put("clientName", share.getString("app_user", null));
			json.put("nickname", nicknameText.getText().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.addBodyParameter("userNickname", json.toString());
		httpUtils.send(HttpMethod.POST, Constants.HOST
				+ Constants.SET_PROFILE_NICKNAME, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0,
							String arg1) {
						myDialog.dismiss();
						Toast.makeText(activity, "保存失败", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.i("TEST_REC", "接收到的结果为---》" + arg0.result);
						JSONObject info;
							try {
								info = new JSONObject(arg0.result);
								if(info.getString("saveNickname").equals("true")){
									myDialog.dismiss();
									changeUserInfo(nicknameText.getText().toString());
									Toast.makeText(activity, "昵称已保存", Toast.LENGTH_LONG).show();
									RootUser rootUser = RootUser.getInstance();
									rootUser.setNickname(nicknameText.getText().toString());
									finish();
								}else{
									myDialog.dismiss();
									Toast.makeText(activity, "保存失败", Toast.LENGTH_LONG).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
					}
				});
	}

	private void changeUserInfo(String signature) {
		SharedPreferences share = getSharedPreferences(
				Constants.SHARE_USERINFO, MODE_PRIVATE);
		SharedPreferences.Editor edit = share.edit(); // 编辑文件
		edit.putString("app_user_nickname", signature);
		edit.commit();
	}

	public boolean isNetworkAvailable(Activity activity) {
		Context context = activity.getApplicationContext();
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
