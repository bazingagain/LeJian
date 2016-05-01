package com.Leon.lejian;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.a.a.g;

import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.RootUser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfile extends Activity implements OnClickListener {
	LinearLayout user_icon = null;
	LinearLayout user_nickname = null;
	LinearLayout user_sex = null;
	LinearLayout user_address = null;
	LinearLayout user_two_dimension_code = null;
	LinearLayout user_signature = null;
	ImageView imgIcon = null;
	TextView textNickname = null;
	TextView textSex = null;
	TextView textAddress = null;
	TextView textSignature = null;
	Dialog alertDialog = null;
	private HttpUtils httpUtils = null;
	private ProgressDialog myDialog = null;
	byte[] clipPic = null;
	Bitmap clipBitmap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);
		initComponent();

	}

	@Override
	protected void onResume() {
		super.onResume();
		updateProfile();
	}

	private void initComponent() {
		user_icon = (LinearLayout) findViewById(R.id.user_icon);
		user_nickname = (LinearLayout) findViewById(R.id.user_nickname);
		user_sex = (LinearLayout) findViewById(R.id.user_sex);
		user_address = (LinearLayout) findViewById(R.id.user_address);
		user_two_dimension_code = (LinearLayout) findViewById(R.id.user_two_dimension_code);
		user_signature = (LinearLayout) findViewById(R.id.user_signature);
		user_icon.setOnClickListener(this);
		user_nickname.setOnClickListener(this);
		user_sex.setOnClickListener(this);
		user_address.setOnClickListener(this);
		user_two_dimension_code.setOnClickListener(this);
		user_signature.setOnClickListener(this);

		imgIcon = (ImageView) findViewById(R.id.icon);
		textNickname = (TextView) findViewById(R.id.nickname);
		textSex = (TextView) findViewById(R.id.sex);
		textAddress = (TextView) findViewById(R.id.address);
		textSignature = (TextView) findViewById(R.id.signature);
		updateProfile();
	}

	private void updateProfile() {
		SharedPreferences share = this.getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
		if (check(share.getString("app_user", null))) {
			textNickname.setText("未设置");
			textSex.setText("未设置");
			textAddress.setText("未设置");
			textSignature.setText("未设置");
			changeUserInfo("app_user_nickname", null);
			changeUserInfo("app_user_sex", null);
			changeUserInfo("app_user_address", null);
			changeUserInfo("app_user_signature", null);
		} else {

			File sdCardDir = Environment.getExternalStorageDirectory();
			File picDirFile = new File(sdCardDir + "/LeJianUserPic");
			if (!picDirFile.exists())
				picDirFile.mkdir();
			File userPicFile = new File(
					Environment.getExternalStorageDirectory()
							+ "/LeJianUserPic/"
							+ "USER_"+Constants.md5(share.getString("app_user", null))+".jpg");
			Constants.userPic = Constants.getBytesFromFile(userPicFile);
			
			clipPic = Constants.userPic;// 裁剪传过来的头像
			if (clipPic != null) {
				clipBitmap = BitmapFactory.decodeByteArray(clipPic, 0,
						clipPic.length);
			}
			if (clipBitmap != null) {
				imgIcon.setImageBitmap(clipBitmap);
			}
			textNickname
					.setText(check(share.getString("app_user_nickname", null)) ? "未设置"
							: share.getString("app_user_nickname", null));
			textSex.setText(check(share.getString("app_user_sex", null)) ? "未设置"
					: share.getString("app_user_sex", null));
			textAddress
					.setText(check(share.getString("app_user_address", null)) ? "未设置"
							: share.getString("app_user_address", null));
			textSignature.setText(check(share.getString("app_user_signature",
					null)) ? "未设置" : share
					.getString("app_user_signature", null));
		}
	}

	private boolean check(String str) {
		if (str == null || str.length() == 0)
			return true;
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_icon:
			Intent iconIntent = new Intent(UserProfile.this,
					ShowPicActivity.class);
			startActivity(iconIntent);
			break;
		case R.id.user_nickname:
			Intent intentNickname = new Intent(UserProfile.this,
					NicknameActivity.class);
			startActivity(intentNickname);
			break;
		case R.id.user_sex:
			final SharedPreferences share = getSharedPreferences(
					Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
			// TODO排除没登录的情况
			if (share.getString("app_user", null) == null
					|| (share.getString("app_user", null).length() == 0)) {
				Toast.makeText(this, "请登录", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!Constants.isNetworkAvailable(this)) {
				Toast.makeText(this, "没有可用网络", Toast.LENGTH_SHORT).show();
				return;
			}
			int defaultSex = 0;
			// sex未设置直接跳过
			if (!check(share.getString("app_user_sex", null))) {
				defaultSex = share.getString("app_user_sex", null).equals("男") ? 0
						: 1;
			}
			alertDialog = new AlertDialog.Builder(this)
					.setSingleChoiceItems(new String[] { "男", "女" },
							defaultSex, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										alertDialog.dismiss();
										// 注册后第一次修改性别 或者 设置和上次一样（即性别没有改变,怎不联网更新）
										if (check(share.getString(
												"app_user_sex", null))
												|| (!share.getString(
														"app_user_sex", null)
														.equals("男"))) {
											circle(UserProfile.this, "男");
										}
										onResume();
										break;
									case 1:
										alertDialog.dismiss();
										if (check(share.getString(
												"app_user_sex", null))
												|| (!share.getString(
														"app_user_sex", null)
														.equals("女"))) {
											circle(UserProfile.this, "女");
										}
										onResume();
										break;
									default:
										break;
									}
								}
							}).setTitle("性别").create();
			alertDialog.show();

			break;
		case R.id.user_address:
			Intent intentAddress = new Intent(this, AddressActivity.class);
			startActivity(intentAddress);
			break;
		case R.id.user_two_dimension_code:
			// Intent intentBarcode = new Intent(this, ScanActivity.class);
			Intent intentBarcode = new Intent(this, ShowBarCodeActivity.class);
			startActivity(intentBarcode);
			break;
		case R.id.user_signature:
			Intent intentSignature = new Intent(this, SignatureActivity.class);
			startActivity(intentSignature);
			break;

		default:
			break;
		}
	}

	public void circle(Activity activity, String sexStr) {
		myDialog = android.app.ProgressDialog.show(activity, null, null);
		updateSex(activity, myDialog, sexStr);
	}

	private void updateSex(final Activity activity,
			final ProgressDialog myDialog, final String sexStr) {
		httpUtils = new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// 设置超时时间
		SharedPreferences share = activity.getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
		RequestParams params = new RequestParams();
		JSONObject json = new JSONObject();
		try {
			json.put("clientName", share.getString("app_user", null));
			json.put("sex", sexStr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.addBodyParameter("userSex", json.toString());
		httpUtils.send(HttpMethod.POST, Constants.HOST
				+ Constants.SET_PROFILE_SEX, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						myDialog.dismiss();
						Toast.makeText(activity, "设置失败", Toast.LENGTH_SHORT)
								.show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.i("TEST_REC", "接收到的结果为---》" + arg0.result);
						JSONObject info;
						try {
							info = new JSONObject(arg0.result);
							if (info.getString("saveSex").equals("true")) {
								myDialog.dismiss();
								changeUserInfo("app_user_sex", sexStr);
								RootUser rootUser = RootUser.getInstance();
								rootUser.setSex(sexStr);
								onResume();
								Toast.makeText(activity, "设置成功",
										Toast.LENGTH_SHORT).show();
							} else {
								myDialog.dismiss();
								Toast.makeText(activity, "设置失败",
										Toast.LENGTH_SHORT).show();
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
