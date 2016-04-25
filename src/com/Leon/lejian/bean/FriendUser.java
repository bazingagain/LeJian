package com.Leon.lejian.bean;

import java.io.Serializable;

public class FriendUser implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String nickname;
	private String pic_url;
	private String sex;
	private String address;
	private String signature;
	private double latitude;
	private double longitude;
	
	public FriendUser(){
		
	}
	public String getName() {
		return name;
	}
	public FriendUser(String name, String nickname, String pic_url,
			String sex, String address, String signature) {
		super();
		this.name = name;
		this.nickname = nickname;
		this.pic_url = pic_url;
		this.sex = sex;
		this.address = address;
		this.signature = signature;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPic_url() {
		return pic_url;
	}
	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
