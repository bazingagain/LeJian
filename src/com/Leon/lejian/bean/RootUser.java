package com.Leon.lejian.bean;

import com.baidu.location.BDLocation;

public class RootUser extends FriendUser{
	/**
	 */
	private static final long serialVersionUID = 1L;
	private RootUser(){
	};
	private BDLocation location = null;
	//饿汉模式（单例模式）-线程安全
	private static final RootUser  testUser = new RootUser();
	public BDLocation getLocation() {
		return location;
	}

	public void setLocation(BDLocation location) {
		this.location = location;
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
	}

	public static RootUser getInstance(){
		return testUser;
	}

}
