package com.Leon.lejian.receiver;


import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver{
	private static final String TAG = "MyReceiver";
	private static final String TYPE_THIS = "add_quest";
    
    private NotificationManager nm;
	@Override
	public void onReceive(Context context, Intent intent) {
		if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
         
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + bundle.toString() );
         
        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "���ܵ������������Զ�����Ϣ");
        	String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        	String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "���ܵ�����������֪ͨ");
            receivingNotification(context,bundle);
 
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
        	Log.d(TAG, "�û��������֪ͨ");
           openNotification(context,bundle);
 
        } else {
        	Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
	}
	 private void receivingNotification(Context context, Bundle bundle){
	        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
	        Log.d(TAG, " title : " + title);
	        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
	        Log.d(TAG, "message : " + message);
	        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
	        Log.d(TAG, "extras : " + extras);
	    } 
	 
	   private void openNotification(Context context, Bundle bundle){
	        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
	        String myValue = ""; 
	        try {
	            JSONObject extrasJson = new JSONObject(extras);
	            myValue = extrasJson.optString(TYPE_THIS);
	            Log.w(TAG, myValue);
	        } catch (Exception e) {
	        	Log.w(TAG, "Unexpected: extras is not a valid json", e);
	            return;
	        }
//	        if (TYPE_THIS.equals() {
//	            Intent mIntent = new Intent(context, .class);
//	            mIntent.putExtras(bundle);
//	            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	            context.startActivity(mIntent);
//	        }
	    }

}
