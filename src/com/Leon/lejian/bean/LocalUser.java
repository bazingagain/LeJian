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
		// ����̴����
		/*Intent it = new Intent(Intent.ACTION_SEND);
		it.putExtra(Intent.EXTRA_TEXT, "��������ͨ���ٶȵ�ͼSDK��������һ��λ��: " + currentAddr
				+ " -- " + result.getUrl());
		it.setType("text/plain");
		startActivity(Intent.createChooser(it, "���̴�����"));*/
	}

	public void login() {
		
	}
	public void register() {
		
	}
	
	public void sharPoi(){
		
	}
	
}
