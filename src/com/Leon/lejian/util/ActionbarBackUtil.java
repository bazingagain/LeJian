package com.Leon.lejian.util;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;

@SuppressLint("NewApi") public class ActionbarBackUtil {
	public static void setActionbarBack(Activity activity, int resTitle, int resDrable){
		ActionBar actionBar = activity.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(resTitle);
		actionBar.setHomeAsUpIndicator(resDrable);  
	}
}
