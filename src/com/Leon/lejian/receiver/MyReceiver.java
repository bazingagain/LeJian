package com.Leon.lejian.receiver;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.Leon.lejian.AddNotificationActivity;
import com.Leon.lejian.ContactProfileActivity;
import com.Leon.lejian.MainActivity;
import com.Leon.lejian.ShareLocationActivity;
import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.FriendUser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import cn.jpush.android.api.JPushInterface;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
			Log.d(TAG, "���ܵ������������Զ�����Ϣ");
			String title = bundle.getString(JPushInterface.EXTRA_TITLE);
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG, "���ܵ�����������֪ͨ");
			receivingNotification(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			Log.d(TAG, "�û��������֪ͨ");
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
		JSONObject extrasJson;
		try {
			extrasJson = new JSONObject(extras);
			if (extrasJson.getString("type").equals("agree")) {
				FriendUser friendUser = new FriendUser(
						extrasJson.getString("friend_name"),
						extrasJson.getString("friend_nickname"),
						extrasJson.getString("pic_url"),
						extrasJson.getString("friend_sex"),
						extrasJson.getString("friend_address"),
						extrasJson.getString("friend_signature"));
				Constants.contactUserList.add(friendUser);
			}
			else if (extrasJson.getString("type").equals("add")) {
				FriendUser friendUser = new FriendUser(
						extrasJson.getString("friend_name"),
						extrasJson.getString("friend_nickname"),
						extrasJson.getString("pic_url"),
						extrasJson.getString("friend_sex"),
						extrasJson.getString("friend_address"),
						extrasJson.getString("friend_signature"));
				Constants.requestUserList.add(friendUser);
				//�����û���ͷ��
				download(extrasJson.getString("friend_name"), "add");
				
			}else if(extrasJson.getString("type").equals("requestShareLoc")){
				//�ӵ����˵Ĺ���λ������  ����������������List��
				for(int i= 0; i < Constants.requestShareUserList.size(); i++){
					if(extrasJson.getString("friend_name").equals(Constants.requestShareUserList.get(i).getName())){
						//����list������������� (������ ����������)
						return ;
					}
				}
				//TODO  ���� requestSharNiti��listview
				FriendUser friendUser = new FriendUser(
						extrasJson.getString("friend_name"),
						extrasJson.getString("friend_nickname"),
						extrasJson.getString("pic_url"),
						extrasJson.getString("friend_sex"),
						extrasJson.getString("friend_address"),
						extrasJson.getString("friend_signature"));
				friendUser.setStatus_share(Constants.OTHER_REQUEST_SELF);
				Constants.requestShareUserList.add(friendUser);
			}
			else if(extrasJson.getString("type").equals("agreeShareLoc")){
				//���յ��Է�ͬ�� �Լ��Ĺ���λ������ (�� ����λ��Activity�ʵʱ��ȡ����ʾ���ѵ�λ��)
				//TODO  ����ʾΪ�ɼ���֪ͨ()
//				FriendUser friendUser = new FriendUser(
//						extrasJson.getString("friend_name"),
//						extrasJson.getString("friend_nickname"),
//						extrasJson.getString("pic_url"),
//						extrasJson.getString("friend_sex"),
//						extrasJson.getString("friend_address"),
//						extrasJson.getString("friend_signature"));
//				friendUser.setStatus_share(Constants.SELF_REQUEST_OTHER);
				for(int i= 0; i < Constants.requestShareUserList.size(); i++){
					if(extrasJson.getString("friend_name").equals(Constants.requestShareUserList.get(i).getName())){
						//TODO����list������������� ��ˢ�� ���ShareLocactivity�ĺ��ѵ�λ��(������ȡ����û���λ��)
						//ֻ�нӵ����˵�ͬ�⹲��֮�󣬲ſ��Ի�ȡ���˵���Ϣ
						Constants.requestShareUserList.get(i).setStatus_share(Constants.SELF_REQUEST_OTHER);
						Constants.requestShareUserList.get(i).setStatus_agree(Constants.OTHER_AGREE_SHARE);;
						return ;
					}
				}
				//list������û��,˵���Լ��Ѿ�ȡ������� ����λ�ã� �� DO Nothing 
				return;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void openNotification(Context context, Bundle bundle) {

		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		try {
			JSONObject extrasJson = new JSONObject(extras);
			if (extrasJson.getString("type").equals("add")) {
				Intent intent = new Intent(context,
						AddNotificationActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				
				//������ADDNotifition�£�Ҫ����  AddNotification�� listview
				context.startActivity(intent);
			} else if (extrasJson.getString("type").equals("agree")) {
				Intent intent = new Intent(context, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Bundle fragmentBundle = new Bundle();
				fragmentBundle.putString("fragment", "contactFragment");
				intent.putExtra("fragment", fragmentBundle);
				context.startActivity(intent);
			}else if(extrasJson.getString("type").equals("requestShareLoc")){
				//���յ����Ѷ��Լ��� ����λ������  (���빲��λ��Activity�� ʵʱ���� ���ѵ�ʵʱλ��)
				//ͬ�⹲������� ShareLocationAcitvity  ��ʾ�����˵� λ����Ϣ
				FriendUser requestShareUser = new FriendUser(
						extrasJson.getString("friend_name"),
						extrasJson.getString("friend_nickname"),
						extrasJson.getString("pic_url"),
						extrasJson.getString("friend_sex"),
						extrasJson.getString("friend_address"),
						extrasJson.getString("friend_signature"));
				//ֱ����ת�� sharenotifragment
				Intent intent = new Intent(context, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Bundle shareBundle = new Bundle();
				shareBundle.putString("fragment", "shareNotificationFragment");
				intent.putExtra("fragment", shareBundle);
				context.startActivity(intent);
				
			}else if(extrasJson.getString("type").equals("agreeShareLoc")){
				//���յ��Է�ͬ�� �Լ��Ĺ���λ������ (�� ����λ��Activity�ʵʱ��ȡ����ʾ���ѵ�λ��)
				//TODO  ����ʾΪ�ɼ���֪ͨ
				
			}
		} catch (Exception e) {
			Log.w(TAG, "Unexpected: extras is not a valid json", e);
			return;
		}
	}
	
	private void download(final String userName, String type){
		//md5
		 File sdCardDir = Environment.getExternalStorageDirectory();
		 String lejianUserPicPath = null;
		 File userPicFile =null;
		 final String fileName = "USER_"+Constants.md5(userName)+".jpg";
			try {
				File picDirFile = new File(sdCardDir+"/LeJianTempUserPic");
				if(!picDirFile.exists())
					picDirFile.mkdir();
				userPicFile = new File(sdCardDir+"/LeJianTempUserPic/"+fileName);
				if(userPicFile.exists()){
					//ÿ�����ض�����
					userPicFile.delete();
				}
				lejianUserPicPath = sdCardDir.getCanonicalPath()+"/LeJianTempUserPic/"+fileName;
			} catch (IOException e) {
				e.printStackTrace();
			}
		HttpUtils http = new HttpUtils();
		HttpHandler handler = http.download(Constants.HOST_PIC_RESOURCE+fileName,
				lejianUserPicPath,
		    true, // ���Ŀ���ļ����ڣ�����δ��ɵĲ��ּ������ء���������֧��RANGEʱ���������ء�
		    true, // ��������󷵻���Ϣ�л�ȡ���ļ�����������ɺ��Զ���������
		    new RequestCallBack<File>() {
		        @Override
		        public void onSuccess(ResponseInfo<File> responseInfo) {
		        	Log.d("DOWNLOAD", responseInfo.result.getPath());
		        	Log.d("DOWNLOAD", "�����û�ͼƬ�ɹ�");
		        }
		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	Log.e("DOWNLOAD", "�����û�ͼƬʧ��");
		        }
		});
		//����cancel()����ֹͣ����
//		handler.cancel();
	}

}
