package com.Leon.lejian.listener;


import android.util.Log;

import com.Leon.lejian.bean.TestUser;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MyLocationListener implements BDLocationListener{
	private BaiduMap mBaiduMap;
	private boolean isFirstLoc;
	private MapView mMapView;
	TestUser testUser;
	public MyLocationListener(BaiduMap mBaiduMap, MapView mMapView, boolean isFirstLoc) {
		this.mBaiduMap = mBaiduMap;
		this.isFirstLoc = isFirstLoc;
		this.mMapView = mMapView;
	}
	@Override
	public void onReceiveLocation(BDLocation location) {
		 // map view 销毁后不在处理新接收的位置
        if (location == null || mMapView == null) {
            return;
        }
        StringBuffer sb = new StringBuffer(256);  
        sb.append("time : ");  
        sb.append(location.getTime());  
        sb.append("\nerror code : ");  
        sb.append(location.getLocType());  
        sb.append("\nlatitude : ");  
        sb.append(location.getLatitude());  
        sb.append("\nlontitude : ");  
        sb.append(location.getLongitude());  
        sb.append("\nradius : ");  
        sb.append(location.getRadius());  
        sb.append("\ndirection : ");  
        sb.append(location.getDirection());
        if (location.getLocType() == BDLocation.TypeGpsLocation){  
             sb.append("\nspeed : ");  
             sb.append(location.getSpeed());  
             sb.append("\nsatellite : ");  
             sb.append(location.getSatelliteNumber());  
             } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){  
             sb.append("\naddr : ");  
             sb.append(location.getAddrStr());  
          }   
        
        Log.e("log", sb.toString());  
         testUser = TestUser.getInstance();
        testUser.setLocation(location);
        
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(u);
        }
	}
	public void onReceivePoi(BDLocation poiLocation) {
	}
	

}
