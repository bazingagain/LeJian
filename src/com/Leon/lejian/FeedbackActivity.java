package com.Leon.lejian;
import org.json.JSONException;
import org.json.JSONObject;

import com.Leon.lejian.api.Constants;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class FeedbackActivity extends Activity implements OnClickListener{
	EditText editText_feedback = null;
	EditText editText_email = null;
	String feedbackStr = null;
	String email = null;
	Button feedbackBtn = null;
	private ProgressDialog myDialog = null;
	private HttpUtils httpUtils = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		initComponent();
	}
	
	private void initComponent(){
		ActionbarBackUtil.setActionbarBack(this, R.string.app_user_setting,
				R.drawable.back_n);
		editText_feedback = (EditText) findViewById(R.id.editText_feedback);
		editText_email = (EditText) findViewById(R.id.editText_email);
		feedbackBtn = (Button) findViewById(R.id.feedbackBtn);
		feedbackBtn.setOnClickListener(this);
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
		if(v.getId() == R.id.feedbackBtn){
			feedbackStr = editText_feedback.getText().toString();
			email = editText_email.getText().toString();
			if(check()){
				circle(this);
			}
			
		}
	}
	
	public void circle(Activity activity) {
		myDialog = android.app.ProgressDialog.show(
				activity, null, null);
		sendFeedback(activity,myDialog);
	}
	
	private void sendFeedback(final Activity activity,final ProgressDialog myDialog){
		httpUtils = new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// 设置超时时间
		SharedPreferences share = activity.getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
		//在已登录的情况下
		RequestParams params = new RequestParams();
		JSONObject json = new JSONObject();
		try {
			json.put("clientName", share.getString("app_user", null));
			json.put("content", feedbackStr);
			json.put("email", email);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.addBodyParameter("userFeedback", json.toString());
		httpUtils.send(HttpMethod.POST, Constants.HOST
				+ Constants.FEEDBACK_USER, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0,
							String arg1) {
						myDialog.dismiss();
						Toast.makeText(activity, "反馈提交失败", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.i("TEST_REC", "接收到的结果为---》" + arg0.result);
						JSONObject info;
							try {
								info = new JSONObject(arg0.result);
								if(info.getString("saveFeedback").equals("true")){
									myDialog.dismiss();
									Toast.makeText(activity, "反馈提交成功", Toast.LENGTH_LONG).show();
									finish();
								}else{
									myDialog.dismiss();
									Toast.makeText(activity, "反馈提交失败", Toast.LENGTH_LONG).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
					}
				});
	}
	
	private boolean check(){
		if(feedbackStr.isEmpty()){
			Toast.makeText(this, "反馈内容不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}else if(email.isEmpty()){
			Toast.makeText(this, "邮件地址不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}else if(!Constants.isNetworkAvailable(this)){
			Toast.makeText(this, "没有可用网络", Toast.LENGTH_SHORT).show();;
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

}
