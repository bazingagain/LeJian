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
import android.content.Intent;
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

public class SignatureActivity extends Activity implements OnClickListener {
	private Button saveBtn = null;
	private EditText signatureText = null;
	private ProgressDialog myDialog = null;
	private HttpUtils httpUtils = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionbarBackUtil.setActionbarBack(this, R.string.signature,
				R.drawable.back_n);
		setContentView(R.layout.signature);
		saveBtn = (Button) findViewById(R.id.save_signature);
		saveBtn.setOnClickListener(this);
		signatureText = (EditText) findViewById(R.id.editText_signature);
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
		if (v.getId() == R.id.save_signature) {
			if (isNetworkAvailable(this)) {
				if(!signatureText.getText().toString().isEmpty()){
					circle(this);
				}
				else if(signatureText.getText().toString().isEmpty()){
					SharedPreferences share = getSharedPreferences(
							Constants.SHARE_USERINFO, MODE_PRIVATE);
					
					if(share.getString("app_user_signature", null).isEmpty()){
						finish();
						return ;
					}else{
						//����Ϊ�գ�����Ϊ��
						circle(this);
					}
				}
			} else {
				Toast.makeText(this, "��ǰû�ÿ�������", Toast.LENGTH_LONG).show();
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
		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// ���ó�ʱʱ��
		SharedPreferences share = activity.getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
//		TODO�ų�ûע������
		if(share.getString("app_user", null)==null || (share.getString("app_user", null).length()==0))
		{
			Toast.makeText(this, "���¼", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		RequestParams params = new RequestParams();
		JSONObject json = new JSONObject();
		try {
			json.put("clientName", share.getString("app_user", null));
			json.put("signature", signatureText.getText().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.addBodyParameter("userSignature", json.toString());
		httpUtils.send(HttpMethod.POST, Constants.HOST
				+ Constants.SET_PROFILE_SIGNATURE, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0,
							String arg1) {
						myDialog.dismiss();
						Toast.makeText(activity, "����ʧ��", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.i("TEST_REC", "���յ��Ľ��Ϊ---��" + arg0.result);
						JSONObject info;
							try {
								info = new JSONObject(arg0.result);
								if(info.getString("saveSignature").equals("true")){
									myDialog.dismiss();
									changeUserInfo(signatureText.getText().toString());
									Toast.makeText(activity, "����ǩ���ѱ���", Toast.LENGTH_LONG).show();
									RootUser rootUser = RootUser.getInstance();
									rootUser.setSignature(signatureText.getText().toString());
									finish();
								}else{
									myDialog.dismiss();
									Toast.makeText(activity, "����ʧ��", Toast.LENGTH_LONG).show();
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
		SharedPreferences.Editor edit = share.edit(); // �༭�ļ�
		edit.putString("app_user_signature", signature);
		edit.commit();
	}

	public boolean isNetworkAvailable(Activity activity) {
		Context context = activity.getApplicationContext();
		// ��ȡ�ֻ��������ӹ�����󣨰�����wi-fi,net�����ӵĹ���
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager == null) {
			return false;
		} else {
			// ��ȡNetworkInfo����
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					// �жϵ�ǰ����״̬�Ƿ�Ϊ����״̬
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
