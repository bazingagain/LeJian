package com.Leon.lejian;

import com.Leon.lejian.api.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
	 Dialog alertDialog=null;

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
		textNickname
				.setText(check(share.getString("app_user_nickname", null)) ? "未设置"
						: share.getString("app_user_nickname", null));
		textSex.setText(check(share.getString("app_user_sex", null)) ? "未设置"
				: share.getString("app_user_sex", null));
		textAddress
				.setText(check(share.getString("app_user_address", null)) ? "未设置"
						: share.getString("app_user_address", null));
		textSignature
				.setText(check(share.getString("app_user_signature", null)) ? "未设置"
						: share.getString("app_user_signature", null));
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
			Intent intentIcon = new Intent(UserProfile.this,
					SignatureActivity.class);
			startActivity(intentIcon);
			break;
		case R.id.user_nickname:
			Intent intentNickname = new Intent(UserProfile.this,
					NicknameActivity.class);
			startActivity(intentNickname);
			break;
		case R.id.user_sex:
			SharedPreferences share = getSharedPreferences(
					Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
			// TODO排除没注册的情况
			if (share.getString("app_user", null) == null
					|| (share.getString("app_user", null).length() == 0)) {
				Toast.makeText(this, "请登录", Toast.LENGTH_LONG).show();
				return;
			}
			int  defaultSex =0;
			if(!check(share.getString("app_user_sex", null))){
				defaultSex = share.getString("app_user_sex", null).equals("男")?0:1;
			}
			alertDialog = new AlertDialog.Builder(this)
					.setSingleChoiceItems(new String[] { "男", "女" }, defaultSex,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									switch (which) {
									case 0:
										changeUserInfo("app_user_sex", "男");
										alertDialog.dismiss();
										onResume();
										break;
									case 1:
										changeUserInfo("app_user_sex", "女");
										alertDialog.dismiss();
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

			break;
		case R.id.user_two_dimension_code:

			break;
		case R.id.user_signature:
			Intent intentSignature = new Intent(this, SignatureActivity.class);
			startActivity(intentSignature);
			break;

		default:
			break;
		}
	}
	

	private void changeUserInfo(String key, String value) {
		SharedPreferences share = getSharedPreferences(
				Constants.SHARE_USERINFO, MODE_PRIVATE);
		SharedPreferences.Editor edit = share.edit(); // 编辑文件
		edit.putString(key, value);
		edit.commit();
	}
	
	

}
