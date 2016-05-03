package com.Leon.lejian;

//import com.Leon.lejian.listener.MyLocationListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.data.JPushView;

import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.LocalUser;
import com.Leon.lejian.fragment.ContactFragment;
import com.Leon.lejian.fragment.MapFragment;
import com.Leon.lejian.fragment.MeFragment;
import com.Leon.lejian.fragment.ShareNotificationFragment;
import com.baidu.mapapi.SDKInitializer;

public class MainActivity extends FragmentActivity implements
		OnCheckedChangeListener {
	/*
	 * fragment
	 */
	Fragment fragment;
	RadioGroup radioGroup;
	FragmentManager manager = getSupportFragmentManager();
	FragmentTransaction transaction;
	public static LocalUser localUser = null;
	/**
	 * 检查是否登录
	 * @return
	 */
	private boolean checkIsLogin(){
		SharedPreferences share = getSharedPreferences(
				Constants.SHARE_USERINFO, MODE_PRIVATE);
		if ((!share.contains("app_user")) || share.getString("app_user", null) == null) {
			return false;
		}
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		if(!checkIsLogin()){
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.create_zoomin, R.anim.create_zoomout);
			finish();
			return;
		}
		
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 百度地图窗口初始化
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);

		setOverflowShowingAlways();
		radioGroup = (RadioGroup) findViewById(R.id.radio_group_bottom);
		radioGroup.setOnCheckedChangeListener(this);
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("fragment");
		if (bundle == null) {
			fragment = new MapFragment(getApplicationContext());
		}
		else if(bundle.getString("fragment").equals("contactFragment")){
			fragment = new ContactFragment();
		}else if(bundle.getString("fragment").equals("shareNotificationFragment")){
			fragment = new ShareNotificationFragment();
		}
		transaction(fragment);
	}

	private void transaction(Fragment fragment) {
		transaction = manager.beginTransaction();
		transaction.replace(R.id.container, fragment);
		transaction.commit();
	}
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.finish_zoomin, R.anim.finish_zoomout);
	}

	@Override
	protected void onPause() {
		// mMapView.onPause();
		super.onPause();
		JPushInterface.onPause(this);
	}

	@Override
	protected void onResume() {
		// mMapView.onResume();
		super.onResume();
		JPushInterface.onResume(this);
		if(!checkIsLogin()){
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.create_zoomin, R.anim.create_zoomout);
			finish();
			return;
		}
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		// mLocClient.stop();
		// // 关闭定位图层
		// mBaiduMap.setMyLocationEnabled(false);
		// mMapView.onDestroy();
		// mMapView = null;
		super.onDestroy();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (group.getId() == R.id.radio_group_bottom) {
			if (checkedId == R.id.radio_news) {
				fragment = new MapFragment(this.getApplicationContext());

			} else if (checkedId == R.id.radio_tushang) {
				fragment = new ShareNotificationFragment();

			} else if (checkedId == R.id.radio_luntan) {
				fragment = new ContactFragment();

			} else if (checkedId == R.id.radio_setting) {
				fragment = new MeFragment();
			}
			transaction(fragment);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	private void setOverflowShowingAlways() {
		try {

			// Field rflect
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
