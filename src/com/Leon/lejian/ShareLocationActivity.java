package com.Leon.lejian;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.Leon.lejian.service.DatabaseService;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * ʵʱ����λ����Ϣ �Լ��Ƚ��룬�ȴ���һ���˵�ͬ�⣬ͬ��֮������ʾ��һ���˵�λ��
 * 
 * ����˳���ǰ��activity�����������
 * 
 * @author xiao
 * 
 */
public class ShareLocationActivity extends Activity implements OnClickListener {
	private Button logoutShareBtn = null;
	private TextView myNameTv = null;
	private TextView myLocTv = null;
	private TextView otherNameTv = null;
	private TextView otherLocTv = null;

	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private Marker mMarkerMe;
	private Marker mMarkerOther;
	BitmapDescriptor bdMe = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_gcoding);
	BitmapDescriptor bdOther = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_gcoding);

	private String contactUserName = null;
	private FriendUser contactUser = null;
	private RootUser selfUser = null;
	private HttpUtils httpUtils = null;
	SharedPreferences share = null;
	Timer timer = new Timer();

	Handler timeHandler = new Handler() {
		@SuppressLint("HandlerLeak") public void handleMessage(Message msg) {
			if (msg.what == 1) {
				Log.i("OTHER", "�Լ���"+selfUser.getLongitude()+" : "+selfUser.getLatitude());
				Log.i("OTHER", "���ѣ�"+contactUser.getLongitude()+" : "+contactUser.getLatitude());
				myLocTv.setText(String.valueOf(selfUser.getLongitude()));
				otherLocTv.setText(String.valueOf(contactUser.getLongitude()));
//				LatLng llMe = mMarkerMe.getPosition();
				LatLng llMeNew = new LatLng(selfUser.getLatitude(),
						selfUser.getLongitude());
				mMarkerMe.setPosition(llMeNew);
//				LatLng llOther = mMarkerOther.getPosition();
				LatLng llOtherNew = new LatLng(contactUser.getLatitude(),
						contactUser.getLongitude());
				mMarkerOther.setPosition(llOtherNew);
			}
			super.handleMessage(msg);
		};
	};

	TimerTask getLocTask = new TimerTask() {

		@Override
		public void run() {
			// ��Ҫ������:������Ϣ
			getFriendLocation(ShareLocationActivity.this);
		}
	};

	TimerTask checkOtherOnlineTask = new TimerTask() {

		@Override
		public void run() {
			// ��Ҫ������:������Ϣ
			DatabaseService dbService = new DatabaseService(
					ShareLocationActivity.this);
			dbService.createShareLocationTable();
			if (dbService.getShareLocationStatus(contactUserName) == Constants.STATUS_ONLINE) {
				// �û���ͬ����� ��ȡ�� ����Ƿ���յ��û�ͬ�������
				checkOtherOnlineTask.cancel();
				// Ȼ��ʼִ�л�ȡ�û���λ����Ϣ
				timer.schedule(getLocTask, 1000, 1000); // 1s��ִ��task,����1s�ٴ�ִ��
			}
			dbService.close();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_location);
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("contactUserName");
		this.contactUserName = bundle.getString("contactUserName");
		DatabaseService dbService = new DatabaseService(this);
		dbService.createFriendTable();
		this.contactUser = dbService.findFriendInfo(this.contactUserName);
		initComponent();
	}

	private void initRootUser() {
		selfUser = RootUser.getInstance();
		share = getSharedPreferences(Constants.SHARE_USERINFO, MODE_PRIVATE);
		selfUser.setLocation(RootUser.getInstance().getLocation());
		selfUser.setName(share.getString("app_user", null));
		selfUser.setNickname(share.getString("app_user_nickname", null));
		// selfUser.setPic_url(share.getString("app_user_pic", null));
		selfUser.setSex(share.getString("app_user_sex", null));
		selfUser.setAddress(share.getString("app_user_address", null));
		selfUser.setSignature(share.getString("app_user_signature", null));
	}

	private void setValue() {
		myNameTv.setText(selfUser.getName());
		myLocTv.setText("" + selfUser.getLongitude());
		otherNameTv.setText(contactUser.getName());
		otherLocTv.setText("" + contactUser.getLongitude());
		httpUtils = new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// ���ó�ʱʱ��
	}

	private void initComponent() {
		mMapView = (MapView) findViewById(R.id.shareMapView);
		logoutShareBtn = (Button) findViewById(R.id.closeShareBtn);
		logoutShareBtn.setOnClickListener(this);
		myNameTv = (TextView) findViewById(R.id.tv_me_name);
		myLocTv = (TextView) findViewById(R.id.tv_me_loc);
		otherNameTv = (TextView) findViewById(R.id.tv_contact_name);
		otherLocTv = (TextView) findViewById(R.id.tv_contact_loc);
		initRootUser();
		initBDMap();
		setValue();
		checkTask();
	}

	private void initBDMap() {
		mBaiduMap = mMapView.getMap();
		//����
		LatLng p = new LatLng(selfUser.getLatitude(), selfUser.getLongitude());
		MapStatus msu  = new MapStatus.Builder().target(p).zoom(6.0f).build();
//		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(6.0f);
		MapStatusUpdate sss = MapStatusUpdateFactory.newMapStatus(msu);
		mBaiduMap.setMapStatus(sss);
		initOverlay();
	}
	
	private void initOverlay(){
		LatLng llMe = new LatLng(selfUser.getLatitude(), selfUser.getLongitude());
		MarkerOptions ooMe = new MarkerOptions().position(llMe).icon(bdMe)
				.zIndex(9);
		mMarkerMe = (Marker) (mBaiduMap.addOverlay(ooMe));
		
		LatLng llOther = new LatLng(contactUser.getLatitude(), contactUser.getLongitude());
		MarkerOptions ooOther = new MarkerOptions().position(llOther).icon(bdOther)
				.zIndex(9);
		mMarkerOther = (Marker) (mBaiduMap.addOverlay(ooOther));
	}

	private void checkTask() {
		DatabaseService dbService = new DatabaseService(this);
		dbService.createShareLocationTable();

		if (dbService.getShareLocationType(contactUserName) == Constants.TYPE_FROME_OTHER) {
			if (dbService.getShareLocationStatus(contactUserName) == Constants.STATUS_ONLINE) {
				timer.schedule(getLocTask, 1000, 1000); // 1s��ִ��task,����1s�ٴ�ִ��
			}
		} else if (dbService.getShareLocationType(contactUserName) == Constants.TYPE_FROME_ME) {
			if (dbService.getShareLocationStatus(contactUserName) == Constants.STATUS_OFFLINE) {
				timer.schedule(checkOtherOnlineTask, 1000, 1000); // 1s��ִ��task,����1s�ٴ�ִ��
			} else if (dbService.getShareLocationStatus(contactUserName) == Constants.STATUS_ONLINE) {
				timer.schedule(getLocTask, 1000, 1000); // 1s��ִ��task,����1s�ٴ�ִ��
			}
		}
	}

	@Override
	protected void onPause() {
		timer.cancel();
		// MapView������������Activityͬ������activity����ʱ�����MapView.onPause()
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// MapView������������Activityͬ������activity�ָ�ʱ�����MapView.onResume()
		mMapView.onResume();
		super.onResume();
		// initRootUser();
		// setValue();
		// checkTask();
	}

	@Override
	protected void onDestroy() {
		// MapView������������Activityͬ������activity����ʱ�����MapView.destroy()
		mMapView.onDestroy();
		super.onDestroy();
		// ���� bitmap ��Դ
		bdMe.recycle();
		bdOther.recycle();
	}

	@Override
	public void onClick(View v) {
		// ���빲�� ����ͬ����빲������
		if (!Constants.isNetworkAvailable(this)) {
			Toast.makeText(this, "�޿�������", Toast.LENGTH_SHORT).show();
			return;
		}
		if (v.getId() == R.id.closeShareBtn) {
			// TODO ���͹ر��źŵ�����������һ���յ��źź�ֹͣ���ա�
			timer.cancel();
			DatabaseService dbService = new DatabaseService(this);
			dbService.createShareLocationTable();
			dbService.deleteShareLocationInfo(contactUserName);
			dbService.close();
			finish();
		}
	}

	private void getFriendLocation(final Activity activity) {
		RequestParams params = new RequestParams();
		JSONObject json = new JSONObject();
		try {
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
						// TODO ��������֤
						Log.i("TEST_REC", "û�յ�" + contactUser.getName() + "��λ��");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.i("TEST_REC", "�յ�" + contactUser.getName() + "��λ��");
						JSONObject info;
						try {
							info = new JSONObject(arg0.result);
							if (info.getString("getContactLoc").equals("true")) {
								contactUser.setLatitude(info
										.getDouble("location_latitude"));
								contactUser.setLongitude(info
										.getDouble("location_lontitude"));
								Log.i("OTHER",
										contactUser.getName()
												+ "��λ��:"
												+ "γ�ȣ�"
												+ info.getDouble("location_latitude")
												+ " ���ȣ�"
												+ info.getDouble("location_lontitude"));
								Message message = new Message();
								message.what = 1;
								timeHandler.sendMessage(message);// ������Ϣ
							} else {
								Log.i("TEST_REC", "��ȡ" + contactUser.getName()
										+ "��λ��ʧ��");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

}
