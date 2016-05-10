package com.Leon.lejian;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.FriendUser;
import com.Leon.lejian.bean.RootUser;
import com.Leon.lejian.service.DatabaseService;
import com.Leon.lejian.util.Util;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class LoginActivity extends Activity implements OnClickListener {
	RootUser rootUser= null;
	Button loginBtn = null;
	Button registerBtn = null;
	EditText userName = null;
	EditText password = null;
	private TextView mUserInfo;
	private ImageView mUserLogo;
	ImageView qqImg = null;
	private final String APP_ID = "1105305633";// 测试时使用，真正发布的时候要换成自己的APP_ID
	private Tencent mTencent = null;
	public static QQAuth mQQAuth;
	private UserInfo mInfo;
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
		mQQAuth = QQAuth.createInstance(APP_ID, getApplicationContext());
		mTencent = Tencent.createInstance(APP_ID, this);
		initView();
		loginActivity = this;
	}

	public void initView() {
		mUserInfo = (TextView) findViewById(R.id.user_nickname);
		mUserLogo = (ImageView) findViewById(R.id.user_logo);
		
		userName = (EditText) findViewById(R.id.loginUser);
		password = (EditText) findViewById(R.id.loginPassword);
		qqImg  =(ImageView) findViewById(R.id.img_qq);
		qqImg.setOnClickListener(this);
		loginBtn = (Button) findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(this);
		registerBtn = (Button) findViewById(R.id.login_registerBtn);
		registerBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_qq:
			onClickLogin();
			break;
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
										//TODO  暂时用JPG  
										download(userNameStr);
										Intent intent = new Intent(
												LoginActivity.this,
												MainActivity.class);
										startActivity(intent);
										createAndSyncRelationTable();
										overridePendingTransition(R.anim.create_zoomin, R.anim.create_zoomout);
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
	
	private void onClickLogin() {
		if (!mQQAuth.isSessionValid()) {
			IUiListener listener = new BaseUiListener() {
				@Override
				protected void doComplete(JSONObject values) {
					updateUserInfo();
				}
			};
			mQQAuth.login(this, "all", listener);
			// mTencent.loginWithOEM(this, "all",
			// listener,"10000144","10000144","xxxx");
			mTencent.login(this, "all", listener);
		} else {
			//退出
			mQQAuth.logout(this);
			updateUserInfo();
		}
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.finish_zoomin, R.anim.finish_zoomout);
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
		edit.putInt("app_user_sharenum", 0);
		edit.putBoolean("app_user_tempshare", false);
		edit.commit();
		rootUser = RootUser.getInstance();
		rootUser.setName(userNameStr);
		rootUser.setNickname(app_user_nickname);
		rootUser.setSex(app_user_sex);
		rootUser.setAddress(app_user_address);
		rootUser.setSignature(app_user_signature);
		rootUser.setShareNum(0);
		//同步 用户好友信息。
		
	}

	private void download(final String userName){
		//md5
		 File sdCardDir = Environment.getExternalStorageDirectory();
		 String lejianUserPicPath = null;
		 File userPicFile =null;
		 final String fileName = "USER_"+Constants.md5(userName)+".jpg";
			try {
				File picDirFile = new File(sdCardDir+"/LeJianUserPic");
				if(!picDirFile.exists())
					picDirFile.mkdir();
				userPicFile = new File(sdCardDir+"/LeJianUserPic/"+fileName);
				if(userPicFile.exists()){
					//每次下载都更新
					userPicFile.delete();
				}
				lejianUserPicPath = sdCardDir.getCanonicalPath()+"/LeJianUserPic/"+fileName;
			} catch (IOException e) {
				e.printStackTrace();
			}
		HttpUtils http = new HttpUtils();
		HttpHandler handler = http.download(Constants.HOST_PIC_RESOURCE+fileName,
				lejianUserPicPath,
		    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
		    true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
		    new RequestCallBack<File>() {
		        @Override
		        public void onSuccess(ResponseInfo<File> responseInfo) {
		        	Log.d("DOWNLOAD", responseInfo.result.getPath());
		        	File userPicFile = new File(Environment.getExternalStorageDirectory()+"/LeJianUserPic/"+fileName);
		        	Constants.userPic = Constants.getBytesFromFile(userPicFile);
		        	Log.e("DOWNLOAD", "login下载用户图片成功");
		        }
		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	Log.e("DOWNLOAD", "下载用户图片失败");
		        }
		});
		//调用cancel()方法停止下载
//		handler.cancel();
	}
	
	private void createAndSyncRelationTable(){
		RequestParams params = new RequestParams();
		JSONObject json = new JSONObject();
		try {
			json.put("clientName", userNameStr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.addBodyParameter("sync", json.toString());
//		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// 
		
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST,Constants.HOST+Constants.SYNC_RELATION_TABLE, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Log.e("SYNC", "登录时同步下载用户好友关系出错");
				
			}

			@SuppressLint("NewApi") @Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Log.d("SYNC", "登录时同步下载用户好友关系成功");
				try {
					JSONArray jsonArray = new JSONArray(arg0.result);
					DatabaseService dbService = new DatabaseService(LoginActivity.this);
					dbService.createFriendTable();
					for(int i = 0; i< jsonArray.length(); i++){
						JSONObject info = jsonArray.getJSONObject(i);
						Log.i("FRIEND", info.toString());
						//先只下朋友的姓名
						FriendUser friendUser = new FriendUser(info.getString("friendName"), info.getString("nick_name"), info.getString("pic_url"), info.getString("sex"), info.getString("address"), info.getString("signature"));
						dbService.saveFriendInfo(friendUser);
						download(info.getString("friendName"), "sync");
					}
					dbService.close();
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	
	private void download(final String userName, String type){
		//md5
		 File sdCardDir = Environment.getExternalStorageDirectory();
		 String lejianUserPicPath = null;
		 File userPicFile =null;
		 final String fileName = "USER_"+Constants.md5(userName)+".jpg";
			try {
				File picDirFile = new File(sdCardDir+"/LeJianTempUserPic");
				if(!picDirFile.exists())
					picDirFile.mkdir();
				userPicFile = new File(sdCardDir+"/LeJianTempUserPic/"+fileName);
				if(userPicFile.exists()){
					//每次下载都更新
					userPicFile.delete();
				}
				lejianUserPicPath = sdCardDir.getCanonicalPath()+"/LeJianTempUserPic/"+fileName;
			} catch (IOException e) {
				e.printStackTrace();
			}
		HttpUtils http = new HttpUtils();
		HttpHandler handler = http.download(Constants.HOST_PIC_RESOURCE+fileName,
				lejianUserPicPath,
		    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
		    true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
		    new RequestCallBack<File>() {
		        @Override
		        public void onSuccess(ResponseInfo<File> responseInfo) {
		        	Log.d("DOWNLOAD", responseInfo.result.getPath());
		        	Log.d("DOWNLOAD", "下载用户图片成功");
		        }
		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	Log.e("DOWNLOAD", "下载用户图片失败");
		        }
		});
		//调用cancel()方法停止下载
//		handler.cancel();
	}
	
	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			Util.showResultDialog(LoginActivity.this, response.toString(),
					"登录成功");
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			Util.toastMessage(LoginActivity.this, "错误: " + e.errorDetail);
			Util.dismissDialog();
		}

		@Override
		public void onCancel() {
//			Util.toastMessage(LoginActivity.this, "取消 ");
			Util.dismissDialog();
		}
	}
	
	private void updateUserInfo() {
		if (mQQAuth != null && mQQAuth.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				@Override
				public void onError(UiError e) {
				}

				@Override
				public void onComplete(final Object response) {
					Message msg = new Message();
					msg.obj = response;
					Log.d("QQ", ""+response);
					msg.what = 0;
					mHandler.sendMessage(msg);
					new Thread() {
						@Override
						public void run() {
							JSONObject json = (JSONObject) response;
							if (json.has("figureurl")) {
								Bitmap bitmap = null;
								try {
									bitmap = Util.getbitmap(json
											.getString("figureurl_qq_2"));
								} catch (JSONException e) {

								}
								Message msg = new Message();
								msg.obj = bitmap;
								msg.what = 1;
								mHandler.sendMessage(msg);
							}
						}

					}.start();
				}

				@Override
				public void onCancel() {
				}
			};
			mInfo = new UserInfo(this, mQQAuth.getQQToken());
			mInfo.getUserInfo(listener);

		} else {
			mUserInfo.setText("");
			mUserInfo.setVisibility(android.view.View.GONE);
			mUserLogo.setVisibility(android.view.View.GONE);
		}
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				JSONObject response = (JSONObject) msg.obj;
				if (response.has("nickname")) {
					try {
						mUserInfo.setVisibility(android.view.View.VISIBLE);
						mUserInfo.setText(response.getString("nickname"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(response.has("gender")){
					try {
						Log.d("QQ", ""+response.getString("gender"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(response.has("privince")){
					try {
						Log.d("QQ", ""+response.getString("privince"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (msg.what == 1) {
				Bitmap bitmap = (Bitmap) msg.obj;
				mUserLogo.setImageBitmap(bitmap);
				mUserLogo.setVisibility(android.view.View.VISIBLE);
			}
		}

	};
}
