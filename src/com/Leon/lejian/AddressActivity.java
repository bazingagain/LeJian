package com.Leon.lejian;

import org.json.JSONException;
import org.json.JSONObject;

import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.RootUser;
import com.Leon.lejian.util.ActionbarBackUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddressActivity extends Activity implements OnClickListener {
	private static final String APP_USER_ADDRESS = "app_user_address";
	private ProgressDialog myDialog = null;
	private HttpUtils httpUtils = null;
	LinearLayout addr_anhui = null;
	LinearLayout addr_aomen = null;
	LinearLayout addr_beijing = null;
	LinearLayout addr_chongqing = null;
	LinearLayout addr_fujian = null;
	LinearLayout addr_guangdong = null;
	LinearLayout addr_gansu = null;
	LinearLayout addr_guangxi = null;
	LinearLayout addr_guizhou = null;
	LinearLayout addr_hebei = null;
	LinearLayout addr_hubei = null;
	LinearLayout addr_heilongjiang = null;
	LinearLayout addr_hainan = null;
	LinearLayout addr_henan = null;
	LinearLayout addr_hunan = null;
	LinearLayout addr_jilin = null;
	LinearLayout addr_jiangsu = null;
	LinearLayout addr_jiangxi = null;
	LinearLayout addr_liaoning = null;
	LinearLayout addr_neimegngu = null;
	LinearLayout addr_ningxia = null;
	LinearLayout addr_qinghai = null;
	LinearLayout addr_sichuan = null;
	LinearLayout addr_shandong = null;
	LinearLayout addr_shanghai = null;
	LinearLayout addr_shanxi_shan = null;
	LinearLayout addr_shanxi_jin = null;
	LinearLayout addr_tianjin = null;
	LinearLayout addr_taiwan = null;
	LinearLayout addr_xizang = null;
	LinearLayout addr_xianggang = null;
	LinearLayout addr_xinjiang = null;
	LinearLayout addr_yunnan = null;
	LinearLayout addr_zhejiang = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address);
		initComponent();
	}

	private void initComponent() {
		ActionbarBackUtil.setActionbarBack(this, R.string.location,
				R.drawable.back_n);
		addr_anhui = (LinearLayout) findViewById(R.id.addr_anhui);
		addr_anhui.setOnClickListener(this);
		addr_aomen = (LinearLayout) findViewById(R.id.addr_aomen);
		addr_aomen.setOnClickListener(this);
		addr_beijing = (LinearLayout) findViewById(R.id.addr_beijing);
		addr_beijing.setOnClickListener(this);
		addr_chongqing = (LinearLayout) findViewById(R.id.addr_chongqing);
		addr_chongqing.setOnClickListener(this);
		addr_guangdong = (LinearLayout) findViewById(R.id.addr_guangdong);
		addr_guangdong.setOnClickListener(this);
		addr_gansu = (LinearLayout) findViewById(R.id.addr_gansu);
		addr_gansu.setOnClickListener(this);
		addr_guangxi = (LinearLayout) findViewById(R.id.addr_guangxi);
		addr_guangxi.setOnClickListener(this);
		addr_guizhou = (LinearLayout) findViewById(R.id.addr_guizhou);
		addr_guizhou.setOnClickListener(this);
		addr_hebei = (LinearLayout) findViewById(R.id.addr_hebei);
		addr_hebei.setOnClickListener(this);
		addr_hubei = (LinearLayout) findViewById(R.id.addr_hubei);
		addr_hubei.setOnClickListener(this);
		addr_heilongjiang = (LinearLayout) findViewById(R.id.addr_heilongjiang);
		addr_heilongjiang.setOnClickListener(this);
		addr_hainan = (LinearLayout) findViewById(R.id.addr_hainan);
		addr_hainan.setOnClickListener(this);
		addr_henan = (LinearLayout) findViewById(R.id.addr_henan);
		addr_henan.setOnClickListener(this);
		addr_hunan = (LinearLayout) findViewById(R.id.addr_hunan);
		addr_hunan.setOnClickListener(this);
		addr_jilin = (LinearLayout) findViewById(R.id.addr_jilin);
		addr_jilin.setOnClickListener(this);
		addr_jiangsu = (LinearLayout) findViewById(R.id.addr_jiangsu);
		addr_jiangsu.setOnClickListener(this);
		addr_jiangxi = (LinearLayout) findViewById(R.id.addr_jiangxi);
		addr_jiangxi.setOnClickListener(this);
		addr_liaoning = (LinearLayout) findViewById(R.id.addr_liaoning);
		addr_liaoning.setOnClickListener(this);
		addr_neimegngu = (LinearLayout) findViewById(R.id.addr_neimegngu);
		addr_neimegngu.setOnClickListener(this);
		addr_ningxia = (LinearLayout) findViewById(R.id.addr_ningxia);
		addr_ningxia.setOnClickListener(this);
		addr_qinghai = (LinearLayout) findViewById(R.id.addr_qinghai);
		addr_qinghai.setOnClickListener(this);
		addr_sichuan = (LinearLayout) findViewById(R.id.addr_sichuan);
		addr_sichuan.setOnClickListener(this);
		addr_shandong = (LinearLayout) findViewById(R.id.addr_shandong);
		addr_shandong.setOnClickListener(this);
		addr_shanghai = (LinearLayout) findViewById(R.id.addr_shanghai);
		addr_shanghai.setOnClickListener(this);
		addr_shanxi_shan = (LinearLayout) findViewById(R.id.addr_shanxi_shan);
		addr_shanxi_shan.setOnClickListener(this);
		addr_shanxi_jin = (LinearLayout) findViewById(R.id.addr_shanxi_jin);
		addr_shanxi_jin.setOnClickListener(this);
		addr_tianjin = (LinearLayout) findViewById(R.id.addr_tianjin);
		addr_tianjin.setOnClickListener(this);
		addr_taiwan = (LinearLayout) findViewById(R.id.addr_taiwan);
		addr_taiwan.setOnClickListener(this);
		addr_xizang = (LinearLayout) findViewById(R.id.addr_xizang);
		addr_xizang.setOnClickListener(this);
		addr_xianggang = (LinearLayout) findViewById(R.id.addr_xianggang);
		addr_xianggang.setOnClickListener(this);
		addr_xinjiang = (LinearLayout) findViewById(R.id.addr_xinjiang);
		addr_xinjiang.setOnClickListener(this);
		addr_yunnan = (LinearLayout) findViewById(R.id.addr_yunnan);
		addr_yunnan.setOnClickListener(this);
		addr_zhejiang = (LinearLayout) findViewById(R.id.addr_zhejiang);
		addr_zhejiang.setOnClickListener(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addr_anhui:
			if(!check())
				return ;
			circle(this,"安徽");
			break;
		case R.id.addr_aomen:
			if(!check())
				return ;
			circle(this,"澳门");
			break;
		case R.id.addr_beijing:
			if(!check())
				return ;
			circle(this,"北京");
			break;
		case R.id.addr_chongqing:
			if(!check())
				return ;
			circle(this,"重庆");
			break;
		case R.id.addr_fujian:
			if(!check())
				return ;
			circle(this,"福建");
			break;
		case R.id.addr_guangdong:
			if(!check())
				return ;
			circle(this,"广东");
			break;
		case R.id.addr_gansu:
			if(!check())
				return ;
			circle(this,"甘肃");
			break;
		case R.id.addr_guangxi:
			if(!check())
				return ;
			circle(this,"广西");
			break;
		case R.id.addr_guizhou:
			if(!check())
				return ;
			circle(this,"贵州");
			break;
		case R.id.addr_hebei:
			if(!check())
				return ;
			circle(this,"河北");
			break;
		case R.id.addr_hubei:
			if(!check())
				return ;
			circle(this,"湖北");
			break;
		case R.id.addr_heilongjiang:
			if(!check())
				return ;
			circle(this,"黑龙江");
			break;
		case R.id.addr_hainan:
			if(!check())
				return ;
			circle(this,"海南");
			break;
		case R.id.addr_henan:
			if(!check())
				return ;
			circle(this,"河南");
			break;
		case R.id.addr_hunan:
			if(!check())
				return ;
			circle(this,"湖南");
			break;
		case R.id.addr_jilin:
			if(!check())
				return ;
			circle(this,"吉林");
			break;
		case R.id.addr_jiangsu:
			if(!check())
				return ;
			circle(this,"江苏");
			break;
		case R.id.addr_jiangxi:
			if(!check())
				return ;
			circle(this,"江西");
			break;
		case R.id.addr_liaoning:
			if(!check())
				return ;
			circle(this,"辽宁");
			break;
		case R.id.addr_neimegngu:
			if(!check())
				return ;
			circle(this,"内蒙古");
			break;
		case R.id.addr_ningxia:
			if(!check())
				return ;
			circle(this,"宁夏");
			break;
		case R.id.addr_qinghai:
			if(!check())
				return ;
			circle(this,"青海");
			break;
		case R.id.addr_sichuan:
			if(!check())
				return ;
			circle(this,"四川");
			break;
		case R.id.addr_shandong:
			if(!check())
				return ;
			circle(this,"山东");
			break;
		case R.id.addr_shanghai:
			if(!check())
				return ;
			circle(this,"上海");
			break;
		case R.id.addr_shanxi_shan:
			if(!check())
				return ;
			circle(this,"陕西");
			break;
		case R.id.addr_shanxi_jin:
			if(!check())
				return ;
			circle(this,"山西");
			break;
		case R.id.addr_tianjin:
			if(!check())
				return ;
			circle(this,"天津");
			break;
		case R.id.addr_taiwan:
			if(!check())
				return ;
			circle(this,"台湾");
			break;
		case R.id.addr_xizang:
			if(!check())
				return ;
			circle(this,"西藏");
			break;
		case R.id.addr_xianggang:
			if(!check())
				return ;
			circle(this,"香港");
			break;
		case R.id.addr_xinjiang:
			if(!check())
				return ;
			circle(this,"新疆");
			break;
		case R.id.addr_yunnan:
			if(!check())
				return ;
			circle(this,"云南");
			break;
		case R.id.addr_zhejiang:
			if(!check())
				return ;
			circle(this,"浙江");
			break;
		default:
			break;
		}
	}
	private boolean check(){
		if(!isNetworkAvailable(this)){
			Toast.makeText(this, "无可用网络", Toast.LENGTH_SHORT).show();
			return false;
		}else if(!isLogin(this)){
			Toast.makeText(this, "请登录", Toast.LENGTH_SHORT).show();
			return false;
		}
			return true;
		
	}
	private boolean isLogin(Activity activity){
		SharedPreferences share = activity.getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
//		TODO排除没注册的情况
		if(share.getString("app_user", null)==null || (share.getString("app_user", null).length()==0))
		{
			return false;
		}
		return true;
	}

	private void changeUserInfo(String key, String value) {
		SharedPreferences share = getSharedPreferences(
				Constants.SHARE_USERINFO, MODE_PRIVATE);
		SharedPreferences.Editor edit = share.edit(); // 编辑文件
		edit.putString(key, value);
		edit.commit();
	}
	
	public void circle(Activity activity, String address) {
		myDialog = android.app.ProgressDialog.show(
				activity, null, null);
		updateAddress(activity,myDialog, address);
	}
	
	private void updateAddress(final Activity activity,final ProgressDialog myDialog, final String address){
		httpUtils = new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// 设置超时时间
		SharedPreferences share = activity.getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
		RequestParams params = new RequestParams();
		JSONObject json = new JSONObject();
		try {
			json.put("clientName", share.getString("app_user", null));
			json.put("address", address);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.addBodyParameter("userAddress", json.toString());
		httpUtils.send(HttpMethod.POST, Constants.HOST
				+ Constants.SET_PROFILE_ADDRESS, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0,
							String arg1) {
						myDialog.dismiss();
						Toast.makeText(activity, "保存失败", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.i("TEST_REC", "接收到的结果为---》" + arg0.result);
						JSONObject info;
							try {
								info = new JSONObject(arg0.result);
								if(info.getString("saveAddress").equals("true")){
									myDialog.dismiss();
									changeUserInfo("app_user_address", address);
									Toast.makeText(activity, "地址已保存", Toast.LENGTH_SHORT).show();
									RootUser rootUser = RootUser.getInstance();
									rootUser.setAddress(address);
									finish();
								}else{
									myDialog.dismiss();
									Toast.makeText(activity, "保存失败", Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
					}
				});
	}


	public boolean isNetworkAvailable(Activity activity) {
		Context context = activity.getApplicationContext();
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
