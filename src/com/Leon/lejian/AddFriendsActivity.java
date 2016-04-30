package com.Leon.lejian;

import org.json.JSONException;
import org.json.JSONObject;

import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.FriendUser;
import com.Leon.lejian.helper.FriendDatabaseHelper;
import com.Leon.lejian.service.DatabaseService;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
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
	private FriendUser tempFriendInfo = null;
	Intent intent = null;
	Bundle bundle = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_friend);
		intent = getIntent();
		// 如果是二维码扫描的结果
		bundle = intent.getBundleExtra("barcode_result");
		initView();
	}

	private void initView() {
		findBtn = (Button) findViewById(R.id.findBtn);
		findBtn.setOnClickListener(this);
		findUserName = (EditText) findViewById(R.id.find_username);
		if(bundle!=null){
			//设置默认的搜索人为扫码到的人
			findUserName.setText(bundle.getString("barcode_result"));
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.findBtn) {
			SharedPreferences share = getSharedPreferences(
					Constants.SHARE_USERINFO, MODE_PRIVATE);
			if ((!share.contains("app_user"))
					|| share.getString("app_user", null) == null) {
				Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!Constants.isNetworkAvailable(this)) {
				Toast.makeText(this, "无可用网络", Toast.LENGTH_SHORT).show();
				return;
			}

			friendName = findUserName.getText().toString().trim();
			if (friendName == null || friendName.isEmpty()) {
				Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			// else if(share.getString("app_user", null).equals(friendName)){
			// Toast.makeText(this, "不能添加自己为好友", Toast.LENGTH_SHORT).show();
			// return ;
			// }
			else {
				try {
					// 本地检查要添加的朋友是否已在本地数据库中
					if (checkLocalHasFriendDatabase(friendName)) {
						Intent intent = new Intent(this,
								FriendProfileActivity.class);
						Bundle bundle = new Bundle();
						FriendUser friendUser = new FriendUser(
								tempFriendInfo.getName(),
								tempFriendInfo.getNickname(),
								tempFriendInfo.getPic_url(),
								tempFriendInfo.getSex(),
								tempFriendInfo.getAddress(),
								tempFriendInfo.getSignature());
						bundle.putSerializable("friend", friendUser);
						intent.putExtra("showRequstUserInfo", bundle);
						startActivity(intent);
						return;
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
									Toast.makeText(getApplicationContext(),
											"已发送，等待用户同意", Toast.LENGTH_SHORT)
											.show();
								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									Log.i("TEST_REC", "接收到的结果为---》"
											+ arg0.result);
									try {
										JSONObject json = new JSONObject(
												arg0.result);
										Toast.makeText(getApplicationContext(),
												json.getString("message"),
												Toast.LENGTH_SHORT).show();
										if (json.getString("add")
												.equals("true")) {
											Log.d(Constants.DEBUG, "添加请求发送成功");
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

	private boolean checkLocalHasFriendDatabase(String friendName) {
		DatabaseService dbService = new DatabaseService(this);
		dbService.createFriendTable();
		tempFriendInfo = dbService.findFriendInfo(friendName);
		dbService.close();
		if (tempFriendInfo == null) {
			return false;
		} else {
			return true;
		}

	}
}
