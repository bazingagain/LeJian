package com.Leon.lejian;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.FriendUser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class ContactProfileActivity extends Activity implements OnClickListener {
	private ImageView contact_pic = null;
	private String pic_url = null;
	private TextView contact_name = null;
	private TextView contact_nickname = null;
	private TextView contact_sex = null;
	private TextView contact_address = null;
	private TextView contact_signature = null;
	private Button shareLocBtn = null;

	private Intent intent = null;
	private Bundle bundle = null;
	private FriendUser user = null;

	Bitmap bitmap = null;
	byte[] userIconByte = null;
	String lejianUserPicPath = null;
	String fileName = null;
	File sdCardDir = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contactprofile);
		intent = getIntent();
		bundle = intent.getBundleExtra("showContactUserInfo");
		user = (FriendUser) bundle.getSerializable("contactUser");
		initComponent(user);
	}

	private void initComponent(FriendUser user) {
		contact_pic = (ImageView) findViewById(R.id.contact_pic);
		contact_name = (TextView) findViewById(R.id.contact_name);
		contact_nickname = (TextView) findViewById(R.id.contact_nickname);
		contact_sex = (TextView) findViewById(R.id.contact_sex);
		contact_address = (TextView) findViewById(R.id.contact_address);
		contact_signature = (TextView) findViewById(R.id.contact_signature);
		shareLocBtn = (Button) findViewById(R.id.comShareLocBtn);
		shareLocBtn.setOnClickListener(this);
		setValue(user);
	}

	private void setValue(FriendUser user) {
		contact_pic.setImageBitmap(getUserIcon(user.getName()));
		contact_name.setText("������" + user.getName());
		if(user.getNickname() == null)
			contact_nickname.setText("�ǳƣ�");
		else 
			contact_nickname.setText("�ǳƣ�" + user.getNickname());
		pic_url = user.getPic_url();
		contact_sex.setText(user.getSex());
		contact_address.setText(user.getAddress());
		contact_signature.setText(user.getSignature());
	}

	@Override
	public void onClick(View v) {
		// ʵʱ����λ��
		if (v.getId() == R.id.comShareLocBtn) {
			// TODO ��������(������λ��)�����ּ�Fragmentһ�����ɵ�ͼ����Ự��
			// ���֪ͨ��ֱ��������ͼ�ỰActivity�������ּ�Fragmentһ�����ɵ�ͼ����Ự��
			if (!check())
				return;
			SharedPreferences share = getSharedPreferences(
					Constants.SHARE_USERINFO, MODE_PRIVATE);
			RequestParams params = new RequestParams();
			JSONObject json = new JSONObject();
			try {
				json.put("clientName", share.getString("app_user", null));
				json.put("contactName", user.getName());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			params.addBodyParameter("requsetShareLoc", json.toString());
			HttpUtils httpUtils = new HttpUtils();
			httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// ���ó�ʱʱ��
			httpUtils.send(HttpMethod.POST, Constants.HOST
					+ Constants.SHARE_LOCATION_PATH, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO
							Toast.makeText(getApplicationContext(), "�û�������",
									Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							Log.i("TEST_REC", "���յ��Ľ��Ϊ---��" + arg0.result);
							try {
								JSONObject json = new JSONObject(arg0.result);
								Toast.makeText(getApplicationContext(),
										json.getString("message"),
										Toast.LENGTH_SHORT).show();
								if (json.getString("shareLoc").equals("true")) {
									// ���͵��������ɹ�
									// ����ʵʱλ�÷���Activity
									// �ȴ����Ѽ���
									Log.d(Constants.DEBUG, "�ȴ��û����빲��");
									// ����һ������Ի�
									Intent shareIntent = new Intent(
											ContactProfileActivity.this,
											ShareLocationActivity.class);
									Bundle shareBundle = new Bundle();
									// ��������Ϣ��ȥ
									user.setStatus_share(Constants.SELF_REQUEST_OTHER);
									Constants.requestShareUserList.add(user);
									shareBundle.putSerializable("contactUser",
											user);
									shareIntent.putExtra("contactUser",
											shareBundle);
									startActivity(shareIntent);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					});
		}
	}

	private boolean check() {
		if (!Constants.isNetworkAvailable(this)) {
			Toast.makeText(this, "�޿�������", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private Bitmap getUserIcon(String userName) {
		// ������ͼƬ�Ļ� �����Բ�����contact_pic
		sdCardDir = Environment.getExternalStorageDirectory();
		fileName = "USER_" + Constants.md5(userName)
				+ ".jpg";
		try {
			lejianUserPicPath = sdCardDir.getCanonicalPath()
					+ "/LeJianTempUserPic/" + fileName;
		} catch (IOException e) {
			e.printStackTrace();
		}
		userIconByte = Constants.getBytesFromFile(new File(lejianUserPicPath));
		if (userIconByte != null) {
			bitmap = BitmapFactory.decodeByteArray(userIconByte, 0,
					userIconByte.length);
		}
		return bitmap;
	}
}
