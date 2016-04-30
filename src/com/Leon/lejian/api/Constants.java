package com.Leon.lejian.api;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.Leon.lejian.bean.FriendUser;

public class Constants {
	public static final String HOST = "HTTP://182.254.234.35/myla/public";
//	public static final String HOST = "HTTP://119.85.210.46/myla/public";
	public static final String REGISTER_PATH = "/userRegister";
	public static final String LOGIN_PATH = "/userLogin";
	public static final String ADD_PATH = "/userAdd";
	public static final String AGREE_PATH = "/userAgree";
	public static final String SEND_LOCATION_PATH = "/userSetLocation";
	
	public static final String SHARE_LOCATION_PATH = "/userShareLocation";
	public static final String GET_CONTACT_LOCATION = "/getContactLocation";
	public static final String AGREE_SHARE_MY_LOCATION = "/agreeShareLocation";
	
	
	public static final String FEEDBACK_USER = "/userFeedback";
	public static final String MODIFY_USER_PASSWORD = "/userModifyPassword";
	public static final String SET_PROFILE_SIGNATURE = "/userProfile/setSignature";
	public static final String SET_PROFILE_ICON = "/userProfile/setIcon";
	public static final String SET_PROFILE_SEX = "/userProfile/setSex";
	public static final String SET_PROFILE_ADDRESS = "/userProfile/setAddress";
	public static final String SET_PROFILE_NICKNAME = "/userProfile/setNickname";
	
	public static final String USER_NAME = "default_user";
	public static final String SHARE_USERINFO = "SHARE_USERINFO";
	public static final int SELF_REQUEST_OTHER = 1; 
	public static final int OTHER_REQUEST_SELF = 2; 
	
	public static final int OTHER_AGREE_SHARE = 1; 
	
	public static final String DEBUG = "DEBUG_INFO";
	
	public static ArrayList<FriendUser> contactUserList = new ArrayList<FriendUser>();
	public static ArrayList<FriendUser> requestUserList = new ArrayList<FriendUser>();
	public static ArrayList<FriendUser> requestShareUserList = new ArrayList<FriendUser>();
	
	public static byte[] userPic = null;
	
	public static boolean isNetworkAvailable(Context activity) {
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
