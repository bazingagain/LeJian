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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddFriendsActivity extends Activity implements OnClickListener {
	private Button findBtn = null;
	private EditText findUserName = null;
	private String friendName = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_friend);
		initView();
	}
	private void initView(){
		findBtn = (Button) findViewById(R.id.findBtn);
		findBtn.setOnClickListener(this);
		findUserName = (EditText) findViewById(R.id.find_username);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.findBtn) {
			friendName = findUserName.getText().toString().trim();
			if(friendName.isEmpty()){
				Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
				return ;
			}else{
				try {
					SharedPreferences share = getSharedPreferences(
							Constants.SHARE_USERINFO, MODE_PRIVATE);
					if(share.getString("app_user", null).isEmpty()||(!share.contains("app_user"))){
						Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
						return ;
					}
					RequestParams params = new RequestParams();
					JSONObject json = new JSONObject();
					json.put("clientName", share.getString("app_user", null));
					json.put("friendName", friendName);
					params.addBodyParameter("add", json.toString());
					HttpUtils httpUtils = new HttpUtils();
					httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// 设置超时时间
					httpUtils.send(HttpMethod.POST, Constants.HOST
							+ Constants.ADD_PATH, params,
							new RequestCallBack<String>() {

								@Override
								public void onFailure(HttpException arg0,
										String arg1) {
									Toast.makeText(getApplicationContext(), "添加失败",
											Toast.LENGTH_LONG).show();
								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									Log.i("TEST_REC", "接收到的结果为---》" + arg0.result);
									try {
										JSONObject json = new JSONObject(arg0.result);
										Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
										if(json.getString("add").equals("true")){
											Log.d(Constants.DEBUG, "添加用户成功");
											//TODO  更新好友通讯录
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
	}
}
