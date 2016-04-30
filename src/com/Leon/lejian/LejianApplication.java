package com.Leon.lejian;



import cn.jpush.android.api.JPushInterface;
import android.app.Application;

public class LejianApplication extends Application{
	@Override
	public void onCreate() {
		super.onCreate();
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		
//		SharedPreferences share = getSharedPreferences("APP_PROFILE", MODE_PRIVATE);   
//	    SharedPreferences.Editor edit = share.edit(); //±à¼­ÎÄ¼þ  
	}
	
}
