package com.Leon.lejian.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.Leon.lejian.bean.FriendUser;
import com.Leon.lejian.bean.RootUser;

public class Constants {
//	public static final String HOST = "HTTP://182.254.234.35/myla/public";
//	public static final String HOST_PIC_RESOURCE = "HTTP://182.254.234.35/myla/storage/app/userPic/";
	public static final String HOST = "http://125.82.60.201/myla/public";
	public static final String HOST_PIC_RESOURCE = "http://125.82.60.201/myla/storage/app/userPic/";
	
	public static final String REGISTER_PATH = "/userRegister";
	public static final String LOGIN_PATH = "/userLogin";
	public static final String ADD_PATH = "/userAdd";
	public static final String AGREE_PATH = "/userAgree";
	public static final String SHOW_SIGNAL_USER_LOC = "/showSignalUserLoc";

	public static final String SYNC_RELATION_TABLE = "/userSyncRelationTable";
	public static final String SEND_LOCATION_PATH = "/userSetLocation";
	
	public static final int TYPE_FROME_ME = 1;
	public static final int TYPE_FROME_OTHER = 2;
	public static final int STATUS_ONLINE = 1;
	public static final int STATUS_OFFLINE = 2;
	
	
	public static final String SHARE_LOCATION_PATH = "/userShareLocation";
	public static final String GET_CONTACT_LOCATION = "/getContactLocation";
	public static final String CLOSE_SHARE_LOCATION = "/closeShareLocation";
	public static final String AGREE_SHARE_MY_LOCATION = "/agreeShareLocation";
	
	
	public static final String FEEDBACK_USER = "/userFeedback";
	public static final String MODIFY_USER_PASSWORD = "/userModifyPassword";
	public static final String SET_PROFILE_SIGNATURE = "/userProfile/setSignature";
	public static final String SET_PROFILE_ICON = "/userProfile/setIcon";
	public static final String SET_PROFILE_SEX = "/userProfile/setSex";
	public static final String SET_PROFILE_ADDRESS = "/userProfile/setAddress";
	public static final String SET_PROFILE_NICKNAME = "/userProfile/setNickname";
	public static final String UPDATE_TEMPSHARE = "/userProfile/updateTempshare";
	
	public static final String USER_NAME = "default_user";
	public static final String SHARE_USERINFO = "SHARE_USERINFO";
	
	
	public static final String DEBUG = "DEBUG_INFO";
	
	public static ArrayList<FriendUser> contactUserList = new ArrayList<FriendUser>();
	public static ArrayList<FriendUser> requestUserList = new ArrayList<FriendUser>();
//	public static ArrayList<FriendUser> requestShareUserList = new ArrayList<FriendUser>();
	
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
	
	/** 
     * 文件转化为字节数组 
     *  
     * @param file 
     * @return 
     */  
    public static byte[] getBytesFromFile(File file) {  
        byte[] ret = null;  
        try {  
            if (file == null) {  
                // log.error("helper:the file is null!");  
                return null;  
            }  
            FileInputStream in = new FileInputStream(file);  
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);  
            byte[] b = new byte[4096];  
            int n;  
            while ((n = in.read(b)) != -1) {  
                out.write(b, 0, n);  
            }  
            in.close();  
            out.close();  
            ret = out.toByteArray();  
        } catch (IOException e) {  
            // log.error("helper:get bytes from file process error!");  
            e.printStackTrace();  
        }  
        return ret;  
    }  
    
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
    public static void addShareNum(Context context) {
		SharedPreferences share = context.getSharedPreferences(
				Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = share.edit(); // 编辑文件
		edit.putInt("app_user_sharenum", share.getInt("app_user_sharenum", 0) + 1);
		edit.commit();
		RootUser rootUser = RootUser.getInstance();
		rootUser.setShareNum(share.getInt("app_user_sharenum", 0));
	}
    public static void decShareNum(Context context) {
    	SharedPreferences share = context.getSharedPreferences(
    			Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
    	SharedPreferences.Editor edit = share.edit(); // 编辑文件
    	if(share.getInt("app_user_sharenum", 0) >= 0){
    		edit.putInt("app_user_sharenum", share.getInt("app_user_sharenum", 0) - 1);
    	}
    	edit.commit();
    	RootUser rootUser = RootUser.getInstance();
    	rootUser.setShareNum(share.getInt("app_user_sharenum", 0));
    }
    public static int getShareNum(Context context) {
    	SharedPreferences share = context.getSharedPreferences(
    			Constants.SHARE_USERINFO, Context.MODE_PRIVATE);
    	return share.getInt("app_user_sharenum", 0);
    }
}
