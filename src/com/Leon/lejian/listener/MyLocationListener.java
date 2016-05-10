package com.Leon.lejian.listener;



import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.RootUser;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class MyLocationListener implements BDLocationListener{
	private BaiduMap mBaiduMap;
	private boolean isFirstLoc;
	private MapView mMapView;
	private Context context;
	static HttpUtils httpUtils;
	
	RootUser testUser;
	public MyLocationListener(Context context, BaiduMap mBaiduMap, MapView mMapView, boolean isFirstLoc) {
		this.context = context;
		this.mBaiduMap = mBaiduMap;
		this.isFirstLoc = isFirstLoc;
		this.mMapView = mMapView;
		httpUtils = new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// ���ó�ʱʱ��
	}
	@Override
	public void onReceiveLocation(BDLocation location) {
		 // map view ���ٺ��ڴ����½��յ�λ��
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
        Log.i("log", sb.toString());
        //��λ��ʵʱ����������
        //����λ�ù���Ựʱ�����Ϳͻ��ĵ���λ�õ���������
        if(Constants.getShareNum(context) > 0){
        	sendLocationToServer(location.getLatitude(), location.getLongitude());
        }
         testUser = RootUser.getInstance();
        testUser.setLocation(location);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                        // �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        //�������ݵ�������
        
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
	
	public void sendLocationToServer(double latitude, double lontitude){
		SharedPreferences share = ((Context) context).getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
//		TODO�ų�ûע������
		if(share.getString("app_user", "tempUser").equals("tempUser"))
			return ;
		RequestParams params = new RequestParams();
		JSONObject json = new JSONObject();
		try {
			json.put("clientName", share.getString("app_user", "tempUser"));
			json.put("latitude", latitude);
			json.put("lontitude", lontitude);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.addBodyParameter("location", json.toString());
//		HttpUtils httpUtils = new HttpUtils();
//		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// ���ó�ʱʱ��
		httpUtils.send(HttpMethod.POST, Constants.HOST
				+ Constants.SEND_LOCATION_PATH, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0,
							String arg1) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.i("TEST_REC", "���յ��Ľ��Ϊ---��" + arg0.result);
					}
				});
	}
	

}
