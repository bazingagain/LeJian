package com.Leon.lejian.provider;


import com.Leon.lejian.AddFriendsActivity;
import com.Leon.lejian.R;
import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.RootUser;
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
import com.zxing.activity.CaptureActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.sax.StartElementListener;
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
	 * 搜索位置 分享
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
		subMenu.add(context.getString(R.string.plus_group_chat))
				.setIcon(R.drawable.ofm_group_chat_icon)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						return true;
					}
				});
		/*
		 * 添加朋友
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
		 * 分享位置
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
								String.format("当前位置： %f，%f", latLng.latitude, latLng.longitude),
								Toast.LENGTH_SHORT).show();
						
						return true;
					}
				});
		/*
		 * 扫描二维码
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
		// 分享短串结果
		SharedPreferences share = context.getSharedPreferences(
				Constants.SHARE_USERINFO, context.MODE_PRIVATE);
		RootUser testUser = RootUser.getInstance();
		Intent it = new Intent(Intent.ACTION_SEND);
		it.putExtra(Intent.EXTRA_TEXT,
				"您的朋友("+share.getString("app_user", null)+")通过乐见与您分享一个位置: " + testUser.getLocation().getAddrStr()
						+ " -- " + result.getUrl());
		it.setType("text/plain");
		context.startActivity(Intent.createChooser(it, "将位置分享到"));

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
			Toast.makeText(context, "抱歉，未找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}
		//在web端的搜索框下会有“测试分享点”这段话
		mShareUrlSearch
		.requestLocationShareUrl(new LocationShareURLOption()
				.location(result.getLocation()).snippet("测试分享点")
				.name(result.getAddress()));
		
	}

}