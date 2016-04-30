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
	// ��λ���
	private LocationClient mLocClient;
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	// UI���
	Button requestLocButton;
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ

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
//		���ø���marker
//		RadioGroup group = (RadioGroup) view.findViewById(R.id.radioGroup);
//		group.setOnCheckedChangeListener(this);
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		requestLocButton.setText("��ͨ");
		requestLocButton.setOnClickListener(this);
		initLocation();

		return view;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
//		if (group.getId() == R.id.radioGroup) {
//
//			if (checkedId == R.id.defaulticon) {
//				// ����null�򣬻ָ�Ĭ��ͼ��
//				mCurrentMarker = null;
//				mBaiduMap
//						.setMyLocationConfigeration(new MyLocationConfiguration(
//								mCurrentMode, true, null));
//			}
//			if (checkedId == R.id.customicon) {
//				// �޸�Ϊ�Զ���marker
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
				requestLocButton.setText("����");
				mCurrentMode = LocationMode.FOLLOWING;
				break;
			case COMPASS:
				requestLocButton.setText("��ͨ");
				mCurrentMode = LocationMode.NORMAL;
				break;
			case FOLLOWING:
				requestLocButton.setText("����");
				mCurrentMode = LocationMode.COMPASS;
				break;
			default:
				break;
			}
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));
	}

	void initLocation() {
		// ��ͼ��ʼ��
		mBaiduMap = mMapView.getMap();
		mCurrentMode = LocationMode.NORMAL;
		// ������λͼ��
		mBaiduMap.setMyLocationEnabled(true);
		// ��λ��ʼ��
		mLocClient = new LocationClient(context);
		mLocClient.registerLocationListener(new MyLocationListener(getActivity(),mBaiduMap,
				mMapView, true));
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setAddrType("all");// ���صĶ�λ���������ַ��Ϣ
		option.setCoorType("bd09ll"); // ������������
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
		// �˳�ʱ���ٶ�λ
//		mLocClient.stop();
		// �رն�λͼ��
//		mBaiduMap.setMyLocationEnabled(false);
//		mMapView.onDestroy();
//		mMapView = null;
		super.onDestroy();
	}

}
