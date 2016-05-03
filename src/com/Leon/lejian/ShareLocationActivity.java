package com.Leon.lejian;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.FriendUser;
import com.Leon.lejian.bean.RootUser;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 实时共享位置信息 自己先进入，等待另一个人的同意，同意之后，则显示另一个人的位置
 * 
 * 如果退出当前的activity，则结束共享
 * 
 * @author xiao
 * 
 */
public class ShareLocationActivity extends Activity implements OnClickListener {
	private Button logoutShareBtn = null;
	private Button addShareBtn = null;
	private TextView myNameTv = null;
	private TextView myLocTv = null;
	private TextView otherNameTv = null;
	private TextView otherLocTv = null;
	private MapView shareMapView = null;
	private LocationClient mLocClient = null;
	private LocationMode mCurrentMode = null;
	BitmapDescriptor mCurrentMarker = null;
	private BaiduMap mBaiduMap = null;
	private FriendUser contactUser = null;
	private RootUser selfUser = null;
	private HttpUtils httpUtils = null;
	private HttpUtils getLocHttpUtils = null;
	private double conatct_location_latitude;
	private double contact_location_lontitude;
	private boolean keepGetLoc = false;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			// 要做的事情
			switch (msg.what) {
			case 0:

				break;
			case 1:
				myLocTv.setText("" + selfUser.getLongitude());
				otherLocTv.setText("" + contactUser.getLongitude());
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	Handler getAgreeHandle = new Handler() {
		public void handleMessage(Message msg) {
			// 要做的事情
			switch (msg.what) {
			case 0:

				break;
			case 1:
				myLocTv.setText("" + selfUser.getLongitude());
				otherLocTv.setText("" + contactUser.getLongitude());
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	Thread thread = null;
	Thread getAgreethread = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_location);
		Intent intent = getIntent();

		Bundle bundle = intent.getBundleExtra("contactUser");
		contactUser = (FriendUser) bundle.getSerializable("contactUser");
		initComponent();
	}

	private void initComponent() {
		selfUser = RootUser.getInstance();
		SharedPreferences share = getSharedPreferences(
				Constants.SHARE_USERINFO, MODE_PRIVATE);
		selfUser.setName(share.getString("app_user", null));
		selfUser.setNickname(share.getString("app_user_nickname", null));
		// selfUser.setPic_url(share.getString("app_user_pic", null));
		selfUser.setSex(share.getString("app_user_sex", null));
		selfUser.setAddress(share.getString("app_user_address", null));
		selfUser.setSignature(share.getString("app_user_signature", null));
		shareMapView = (MapView) findViewById(R.id.shareMapView);
		logoutShareBtn = (Button) findViewById(R.id.closeShareBtn);
		logoutShareBtn.setOnClickListener(this);
		addShareBtn = (Button) findViewById(R.id.addShareBtn);
		addShareBtn.setOnClickListener(this);
		myNameTv = (TextView) findViewById(R.id.tv_me_name);
		myLocTv = (TextView) findViewById(R.id.tv_me_loc);
		otherNameTv = (TextView) findViewById(R.id.tv_contact_name);
		otherLocTv = (TextView) findViewById(R.id.tv_contact_loc);

		// 一开始都能看到自己的位置
		myNameTv.setText(selfUser.getName());
		myLocTv.setText("" + selfUser.getLongitude());
		otherNameTv.setText(contactUser.getName());
		otherLocTv.setText("" + contactUser.getLongitude());
		// 我自己请求别人 的位置共享, 一开始就监控 别人是否同意
		if (contactUser.getStatus_share() == Constants.SELF_REQUEST_OTHER) {
			addShareBtn.setVisibility(android.view.View.INVISIBLE);
			Toast.makeText(this, "自己请求分享别人的位置，加广播监听", Toast.LENGTH_SHORT).show();
			//TODO  暂时 
			if(thread ==null){
				thread = new Thread(new GetLocationThread());
				keepGetLoc = true;
				thread.start();
			}
//			getAgreethread = new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO 改为 boolean
//					while (true) {
//						if (contactUser.getStatus_agree() == Constants.OTHER_AGREE_SHARE)
//							thread = new Thread(new GetLocationThread());
//						keepGetLoc = true;
//						thread.start();
//					}
//				}
//			});
//			getAgreethread.start();
		} else if (contactUser.getStatus_share() == Constants.OTHER_REQUEST_SELF) {
			Toast.makeText(this, "别人请求分享自己的位置", Toast.LENGTH_SHORT).show();
//			if(thread ==null){
//				thread = new Thread(new GetLocationThread());
//				thread.start();
//			}
			// 别人请求我自己 的位置共享, 添加 同意按钮, 同意则显示比人的位置， agreeAddRequest();
			addShareBtn.setVisibility(android.view.View.VISIBLE);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		// 加入共享 发送同意加入共享请求
		if (!Constants.isNetworkAvailable(this)) {
			Toast.makeText(this, "无可用网络", Toast.LENGTH_SHORT).show();
			return;
		}
		//允许共享
		if (v.getId() == R.id.addShareBtn) {
			// 开始接收 好友的位置
			if (!thread.isAlive()) {
				keepGetLoc = true;
				thread.start();
				agreeAddRequest(this);
				// 同意之后，就只能由退出键了
				addShareBtn.setVisibility(android.view.View.INVISIBLE);
			}
			// agreeAddRequest(this, );
		} else if (v.getId() == R.id.closeShareBtn) {
			// 结束
			for (int i = 0; i < Constants.requestShareUserList.size(); i++) {
				if (selfUser.getName().equals(
						Constants.requestShareUserList.get(i).getName())) {
					Constants.requestShareUserList.remove(i);
					if (thread.isAlive()) {
						keepGetLoc = false;
						// TODO
					}
					finish();
					break;
				}
			}
		}
	}

	private void agreeAddRequest(final Context activity) {
		httpUtils = new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// 设置超时时间
		SharedPreferences share = activity.getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
		RequestParams params = new RequestParams();
		JSONObject json = new JSONObject();
		try {
			json.put("clientName", share.getString("app_user", null));
			json.put("agreeShareUserName", contactUser.getName());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.addBodyParameter("agreeShareLoc", json.toString());
		httpUtils.send(HttpMethod.POST, Constants.HOST
				+ Constants.AGREE_SHARE_MY_LOCATION, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO 服务器验证
						Toast.makeText(activity, "失败", Toast.LENGTH_SHORT)
								.show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.i("TEST_REC", "接收到的结果为---》" + arg0.result);
						JSONObject info;
						try {
							info = new JSONObject(arg0.result);
							if (info.getString("agreeShareLoc").equals("true")) {
								// TODO 保存新朋友到本地数据库, 删除这个朋友在数据库中的请求
								Toast.makeText(activity, "已同意共享",
										Toast.LENGTH_SHORT).show();
							} else {
								// Toast.makeText(activity,
								// info.getString("message"),
								// Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	public class GetLocationThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (keepGetLoc) {
				try {
					Thread.sleep(1000);// 线程暂停1秒，单位毫秒
					getFriendLocation(ShareLocationActivity.this);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void getFriendLocation(final Context activity) {
		httpUtils = new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// 设置超时时间
		SharedPreferences share = getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
		RequestParams params = new RequestParams();
		JSONObject json = new JSONObject();
		try {
			json.put("clientName", share.getString("app_user", null));
			json.put("getLocFriendName", contactUser.getName());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.addBodyParameter("getContactLoc", json.toString());
		httpUtils.send(HttpMethod.POST, Constants.HOST
				+ Constants.GET_CONTACT_LOCATION, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO 服务器验证
						Log.i("TEST_REC", "没收到" + contactUser.getName() + "的位置");
						// Toast.makeText(activity, "已同意",
						// Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.i("TEST_REC", "收到" + contactUser.getName() + "的位置");
						JSONObject info;
						try {
							info = new JSONObject(arg0.result);
							if (info.getString("getContactLoc").equals("true")) {
								contactUser.setLatitude(info
										.getDouble("location_latitude"));
								contactUser.setLongitude(info
										.getDouble("location_lontitude"));
								Log.i("TEST_REC",
										contactUser.getName()
												+ "的位置:"
												+ "纬度："
												+ info.getDouble("location_latitude")
												+ " 经度："
												+ info.getDouble("location_lontitude"));
								Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);// 发送消息
							} else {
								Toast.makeText(activity,
										info.getString("message"),
										Toast.LENGTH_SHORT).show();

							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

}
