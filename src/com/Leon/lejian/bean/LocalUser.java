package com.Leon.lejian.bean;

import com.baidu.location.BDLocation;


public class LocalUser{
	private String currentAddr = null;
	private BDLocation location = null;
	
	public String getCurrentAddr() {
		return currentAddr;
	}
	

	public BDLocation getLocation() {
		return location;
	}


	public void setLocation(BDLocation location) {
		this.location = location;
	}


	public void setCurrentAddr(String currentAddr) {
		this.currentAddr = currentAddr;
	}

	public void sharHomeLocation() {
		// 分享短串结果
		/*Intent it = new Intent(Intent.ACTION_SEND);
		it.putExtra(Intent.EXTRA_TEXT, "您的朋友通过百度地图SDK与您分享一个位置: " + currentAddr
				+ " -- " + result.getUrl());
		it.setType("text/plain");
		startActivity(Intent.createChooser(it, "将短串分享到"));*/
	}

	public void login() {
		
	}
	public void register() {
		
	}
	
	public void sharPoi(){
		
	}
	
}
