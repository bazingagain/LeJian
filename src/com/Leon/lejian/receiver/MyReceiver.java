package com.Leon.lejian.receiver;

import org.json.JSONObject;

import com.Leon.lejian.AddNotificationActivity;
import com.Leon.lejian.bean.FriendUser;

import cn.jpush.android.api.JPushInterface;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "MyReceiver";
	private static final String TYPE_THIS = "add_quest";
	String requstUserName = null;
	String requsetUserNickname = null;
	String requsetUserPic_url = null;
	String requsetUserSex = null;
	String requsetUserAddress = null;
	String requsetUserSignature = null;

	private NotificationManager nm;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (null == nm) {
			nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
		}

		Bundle bundle = intent.getExtras();
		Log.d(TAG,
				"onReceive - " + intent.getAction() + ", extras: "
						+ bundle.toString());

		if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			Log.d(TAG, "接受到推送下来的自定义消息");
			String title = bundle.getString(JPushInterface.EXTRA_TITLE);
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG, "接受到推送下来的通知");
			receivingNotification(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			Log.d(TAG, "用户点击打开了通知");
			openNotification(context, bundle);

		} else {
			Log.d(TAG, "Unhandled intent - " + intent.getAction());
		}
	}

	private void receivingNotification(Context context, Bundle bundle) {
		String title = bundle
				.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
		Log.d(TAG, " title : " + title);
		String message = bundle.getString(JPushInterface.EXTRA_ALERT);
		Log.d(TAG, "message : " + message);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Log.d(TAG, "extras : " + extras);
	}

	private void openNotification(Context context, Bundle bundle) {

		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		try {
			JSONObject extrasJson = new JSONObject(extras);
			Intent intent = new Intent(context, AddNotificationActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			Bundle friendBundle = new Bundle();
			FriendUser friendUser = new FriendUser(extrasJson.getString("friend_name"),extrasJson.getString("friend_nickname"), extrasJson.getString("pic_url"), extrasJson.getString("friend_sex"), extrasJson.getString("friend_address"), extrasJson.getString("friend_signature"));
			friendBundle.putSerializable("friend", friendUser);
			intent.putExtra("requserAddFriend", friendBundle);
			context.startActivity(intent);
		} catch (Exception e) {
			Log.w(TAG, "Unexpected: extras is not a valid json", e);
			return;
		}
	}

}
