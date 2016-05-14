package com.Leon.lejian;

import org.json.JSONException;
import org.json.JSONObject;

import com.Leon.lejian.api.Constants;
import com.Leon.lejian.util.ActionbarBackUtil;
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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserSetting extends Activity implements OnClickListener{
	LinearLayout modify_password = null;
	LinearLayout feedback = null;
	LinearLayout about = null;
	LinearLayout tempshare = null;
	TextView tempshareText = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_setting);
		initComponent();
	}
	
	private void initComponent(){
		ActionbarBackUtil.setActionbarBack(this, R.string.feedback,
				R.drawable.back_n);
		modify_password = (LinearLayout) findViewById(R.id.modify_password);
		modify_password.setOnClickListener(this);
		feedback = (LinearLayout) findViewById(R.id.feedback);
		feedback.setOnClickListener(this);
		about = (LinearLayout) findViewById(R.id.about);
		about.setOnClickListener(this);
		tempshare = (LinearLayout) findViewById(R.id.tempshare);
		tempshare.setOnClickListener(this);
		tempshareText = (TextView) findViewById(R.id.tempusertext);
		SharedPreferences share = getSharedPreferences(
				Constants.SHARE_USERINFO, MODE_PRIVATE);
		if(share.getBoolean("app_user_tempshare", false)){
			tempshareText.setText("�ر���ʱ����");
		}else{
			tempshareText.setText("������ʱ����");
		}
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
		switch (v.getId()) {
		case R.id.modify_password:
			Intent intentModifyPassword = new Intent(UserSetting.this, ModifyPasswordActivity.class);
			startActivity(intentModifyPassword);
			break;
		case R.id.tempshare:
			if(!Constants.isNetworkAvailable(this)){
				Toast.makeText(this, "�޿�������", Toast.LENGTH_SHORT).show();
				return ;
			}
			SharedPreferences share = getSharedPreferences(
					Constants.SHARE_USERINFO, MODE_PRIVATE);
			if(share.getBoolean("app_user_tempshare", false)){
				updateTempshare(share, false, "������ʱ����", "��ʱ�����ѹر�");
			}else{
				updateTempshare(share, true, "�ر���ʱ����", "��ʱ�����ѿ���");
			}
			break;
		case R.id.feedback:
			Intent intentFeedback = new Intent(UserSetting.this, FeedbackActivity.class);
			startActivity(intentFeedback);
			break;
		case R.id.about:
			Intent intentAbout = new Intent(UserSetting.this, AboutActivity.class);
			startActivity(intentAbout);
			break;
			
		default:
			break;
		}
	}
	private void updateTempshare(final SharedPreferences share, final boolean tempshare, final String showtext, final String toasttext){
		RequestParams params = new RequestParams();
		JSONObject json = new JSONObject();
		try {
			json.put("clientName", share.getString("app_user", null));
			json.put("tempshare", tempshare);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.addBodyParameter("updateTempshare", json.toString());
//		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// 
		
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST,Constants.HOST+Constants.UPDATE_TEMPSHARE, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Log.e("UPDATE", "������ʱ�������");
				
			}

			public void onSuccess(ResponseInfo<String> arg0) {
				Log.d("UPDATE", "������ʱ����ɹ�");
				try {
					JSONObject json = new JSONObject(arg0.result);
					if(json.getString("saveTempshare").equals("true")){
						SharedPreferences share = getSharedPreferences(
								Constants.SHARE_USERINFO, UserSetting.MODE_PRIVATE);
						SharedPreferences.Editor edit = share.edit(); // �༭�ļ�
						edit.putBoolean("app_user_tempshare", tempshare);
						edit.commit();
						tempshareText.setText(showtext);
						Toast.makeText(UserSetting.this, toasttext, Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		});
	}
}