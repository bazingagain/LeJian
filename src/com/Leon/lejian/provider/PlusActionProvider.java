package com.Leon.lejian.provider;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.Leon.lejian.AddFriendsActivity;
import com.Leon.lejian.LoginActivity;
import com.Leon.lejian.R;
import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.FriendUser;
import com.Leon.lejian.bean.RootUser;
import com.Leon.lejian.service.DatabaseService;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.share.LocationShareURLOption;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zxing.activity.CaptureActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

public class PlusActionProvider extends ActionProvider implements
		OnGetShareUrlResultListener, OnGetGeoCoderResultListener{

	private Context context;
	private Activity activity;
	/**
	 * ����λ�� ����
	 */
	private ShareUrlSearch mShareUrlSearch = null;
	private GeoCoder mGeoCoder = null;

	public PlusActionProvider(Context context) {
		super(context);
		this.context = context;
		mShareUrlSearch = ShareUrlSearch.newInstance();
		mShareUrlSearch.setOnGetShareUrlResultListener(this);
		mGeoCoder = GeoCoder.newInstance();
		mGeoCoder.setOnGetGeoCodeResultListener(this);
	}

	@Override
	public View onCreateActionView() {
		return null;
	}

	@Override
	public void onPrepareSubMenu(SubMenu subMenu) {
		subMenu.clear();
		subMenu.add(context.getString(R.string.plus_realtime_share))
				.setIcon(R.drawable.ofm_group_chat_icon)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						final SharedPreferences share = context.getSharedPreferences(
								Constants.SHARE_USERINFO, context.MODE_PRIVATE);
						if(!Constants.isNetworkAvailable(context)){
							Toast.makeText(context, "�޿�������", Toast.LENGTH_SHORT).show();
							return true;
						}
						if(share.getBoolean("app_user_tempshare", false)){
							startSendShareUrl(share);
						}else{
							new AlertDialog.Builder(context)
							.setMessage("ȷ��������ʱ����")
							.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											updateTempshare(share.getString("app_user", null), share);
										}
									})
							.setNegativeButton("ȡ��",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog,
												int which) {
										}
									}).show();
						}
						return true;
					}
				});
		/*
		 * �������
		 */
		subMenu.add(context.getString(R.string.plus_add_friend))
				.setIcon(R.drawable.ofm_add_icon)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Intent intetn = new Intent(context, AddFriendsActivity.class);
						context.startActivity(intetn);
						return true;
					}
				});
		/*
		 * ����λ��
		 */
		subMenu.add(context.getString(R.string.plus_location_share))
				.setIcon(R.drawable.ofm_feedback_icon)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						RootUser testUser = RootUser.getInstance();
						LatLng latLng = new LatLng(testUser.getLocation()
								.getLatitude(), testUser.getLocation()
								.getLongitude());
						mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
						Toast.makeText(
								context,
								String.format("��ǰλ�ã� %f��%f", latLng.latitude, latLng.longitude),
								Toast.LENGTH_SHORT).show();
						return true;
					}
				});
		/*
		 * ɨ���ά��
		 */
		subMenu.add(context.getString(R.string.plus_scan))
				.setIcon(R.drawable.ofm_qrcode_icon)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Intent intent = new Intent(context, CaptureActivity.class);
						context.startActivity(intent);
						return false;
					}
				});
	}

	@Override
	public boolean hasSubMenu() {
		return true;
	}

	@Override
	public void onGetLocationShareUrlResult(ShareUrlResult result) {
		// ����̴����
		SharedPreferences share = context.getSharedPreferences(
				Constants.SHARE_USERINFO, context.MODE_PRIVATE);
		RootUser testUser = RootUser.getInstance();
		Intent it = new Intent(Intent.ACTION_SEND);
		it.putExtra(Intent.EXTRA_TEXT,
				"��������("+share.getString("app_user", null)+")ͨ���ּ���������һ��λ��: " + testUser.getLocation().getAddrStr()
						+ " -- " + result.getUrl());
		it.setType("text/plain");
		context.startActivity(Intent.createChooser(it, "��λ�÷���"));

	}
	public void startSendShareUrl(SharedPreferences share){
		Intent it = new Intent(Intent.ACTION_SEND);
		it.putExtra(Intent.EXTRA_TEXT,
				"��������("+share.getString("app_user", null)+")ͨ���ּ���������һ��ʵʱ�켣: "
						+ " -- " + Constants.HOST + Constants.SHOW_SIGNAL_USER_LOC+"/"+share.getString("app_user", null));
		it.setType("text/plain");
		context.startActivity(Intent.createChooser(it, "��λ�÷���"));
	}

	@Override
	public void onGetPoiDetailShareUrlResult(ShareUrlResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(context, "��Ǹ��δ�ҵ����",
					Toast.LENGTH_LONG).show();
			return;
		}
		//��web�˵��������»��С����Է���㡱��λ�
		mShareUrlSearch
		.requestLocationShareUrl(new LocationShareURLOption()
				.location(result.getLocation()).snippet("���Է����")
				.name(result.getAddress()));
		
	}
	
	private void updateTempshare(String userNameStr ,final SharedPreferences share){
		RequestParams params = new RequestParams();
		JSONObject json = new JSONObject();
		try {
			json.put("clientName", userNameStr);
			json.put("tempshare", true);
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
						SharedPreferences.Editor edit = share.edit(); // �༭�ļ�
						edit.putBoolean("app_user_tempshare", true);
						edit.commit();
						startSendShareUrl(share);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		});
	}

}