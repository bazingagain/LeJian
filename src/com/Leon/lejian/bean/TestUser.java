package com.Leon.lejian.bean;

import com.baidu.location.BDLocation;

public class TestUser {
	private TestUser(){};
	private BDLocation location = null;
	//����ģʽ������ģʽ��-�̰߳�ȫ
	private static final TestUser  testUser = new TestUser();
	public BDLocation getLocation() {
		return location;
	}

	public void setLocation(BDLocation location) {
		this.location = location;
	}

	public static TestUser getInstance(){
		return testUser;
	}

}
