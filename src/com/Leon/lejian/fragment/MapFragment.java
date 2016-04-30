package com.Leon.lejian.fragment;

import com.Leon.lejian.R;
import com.Leon.lejian.listener.MyLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MapFragment extends Fragment implements OnCheckedChangeListener,
		OnClickListener {
	// 定位相关
	private LocationClient mLocClient;
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	// UI相关
	Button requestLocButton;
	boolean isFirstLoc = true;// 是否首次定位

	Context context;

	public MapFragment(Context context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_location, container,
				false);

		requestLocButton = (Button) view.findViewById(R.id.button_mode);
//		设置更换marker
//		RadioGroup group = (RadioGroup) view.findViewById(R.id.radioGroup);
//		group.setOnCheckedChangeListener(this);
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		requestLocButton.setText("普通");
		requestLocButton.setOnClickListener(this);
		initLocation();

		return view;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
//		if (group.getId() == R.id.radioGroup) {
//
//			if (checkedId == R.id.defaulticon) {
//				// 传入null则，恢复默认图标
//				mCurrentMarker = null;
//				mBaiduMap
//						.setMyLocationConfigeration(new MyLocationConfiguration(
//								mCurrentMode, true, null));
//			}
//			if (checkedId == R.id.customicon) {
//				// 修改为自定义marker
//				mCurrentMarker = BitmapDescriptorFactory
//						.fromResource(R.drawable.icon_gcoding);
//				mBaiduMap
//						.setMyLocationConfigeration(new MyLocationConfiguration(
//								mCurrentMode, true, mCurrentMarker));
//			}
//		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_mode)
			switch (mCurrentMode) {
			case NORMAL:
				requestLocButton.setText("跟随");
				mCurrentMode = LocationMode.FOLLOWING;
				break;
			case COMPASS:
				requestLocButton.setText("普通");
				mCurrentMode = LocationMode.NORMAL;
				break;
			case FOLLOWING:
				requestLocButton.setText("罗盘");
				mCurrentMode = LocationMode.COMPASS;
				break;
			default:
				break;
			}
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));
	}

	void initLocation() {
		// 地图初始化
		mBaiduMap = mMapView.getMap();
		mCurrentMode = LocationMode.NORMAL;
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(context);
		mLocClient.registerLocationListener(new MyLocationListener(getActivity(),mBaiduMap,
				mMapView, true));
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(2000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	@Override
	public void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	public void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		// 退出时销毁定位
//		mLocClient.stop();
		// 关闭定位图层
//		mBaiduMap.setMyLocationEnabled(false);
//		mMapView.onDestroy();
//		mMapView = null;
		super.onDestroy();
	}

}
