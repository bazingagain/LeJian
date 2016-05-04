package com.Leon.lejian.service;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

import com.Leon.lejian.bean.FriendUser;
import com.Leon.lejian.helper.FriendDatabaseHelper;

public class DatabaseService {
	private FriendDatabaseHelper dbOpenHelper;

	public DatabaseService(Context context) {
		dbOpenHelper = new FriendDatabaseHelper(context);
	}

	public void dropTable(String taleName) {
		dbOpenHelper.getWritableDatabase().execSQL(
				"DROP TABLE IF EXISTS " + taleName);
	}

	public void closeDatabase(String DatabaseName) {
		dbOpenHelper.getWritableDatabase().close();

	}

	public void createFriendTable() {
		String sql = "CREATE TABLE IF NOT EXISTS friends (id integer primary key autoincrement, friend_name varchar(50), nickname varchar(50),pic_url varchar(50), sex varchar(4), address varchar(50),signature varchar(50))";
		dbOpenHelper.getWritableDatabase().execSQL(sql);
	}

	public void saveFriendInfo(FriendUser friendInfo) {
		dbOpenHelper
				.getWritableDatabase()
				.execSQL(
						"insert into friends (friend_name, nickname, pic_url, sex, address, signature) values(?,?,?,?,?,?)",
						new Object[] { friendInfo.getName(),
								friendInfo.getNickname(),
								friendInfo.getPic_url(), friendInfo.getSex(),
								friendInfo.getAddress(),
								friendInfo.getSignature() });
	}

	public void updateFriendInfo(FriendUser friendInfo) {
		dbOpenHelper
				.getWritableDatabase()
				.execSQL(
						"update friends set nickname=?, pic_url=?, sex=?,address=?,signature=? where friend_name=?",
						new Object[] { friendInfo.getNickname(),
								friendInfo.getPic_url(), friendInfo.getSex(),
								friendInfo.getAddress(),
								friendInfo.getSignature(), friendInfo.getName() });
	}

	/*
	 * 共享联系人队列
	 */
	public void createShareLocationTable() {
		String sql = "CREATE TABLE IF NOT EXISTS shareLoc (id integer primary key autoincrement, friend_name varchar(50), type int, status int)";
		dbOpenHelper.getWritableDatabase().execSQL(sql);
	}

	public void saveShareLocationInfo(String friendName, int type, int status) {
		dbOpenHelper
				.getWritableDatabase()
				.execSQL(
						"insert into shareLoc (friend_name, type, status) values(?,?,?)",
						new Object[] { friendName, type, status });
	}

	public void updateShareLocationStatus(String friendName, int status) {
		dbOpenHelper.getWritableDatabase().execSQL(
				"update shareLoc set status = ? where friend_name = ?",
				new Object[] { status, friendName });
	}

	public void deleteShareLocationInfo(String name) {
		dbOpenHelper.getWritableDatabase().execSQL(
				"delete from shareLoc where friend_name = ?",
				new String[] { name });
	}
	public void deleteShareLocationInfo(int position) {
		int i = 0;
		String userName = null;
		Cursor cursor = dbOpenHelper.getWritableDatabase().rawQuery(
				"select friend_name from shareLoc",null);
		while (cursor.moveToNext()) {
			if( position == i){
				userName = cursor.getString(0);
				break;
			}
		}
		dbOpenHelper.getWritableDatabase().execSQL(
				"delete from shareLoc where friend_name = ?",
				new String[] { userName });
	}

	public int getShareLocationStatus(String name) {
		int status = 0;
		Cursor cursor = dbOpenHelper.getWritableDatabase().rawQuery(
				"select status from shareLoc where friend_name = ?",
				new String[] { name });
		while (cursor.moveToNext()) {
			status = cursor.getInt(0);
		}
		return status;
	}

	public int getShareLocationType(String name) {
		int type = 0;
		Cursor cursor = dbOpenHelper.getWritableDatabase().rawQuery(
				"select type from shareLoc where friend_name = ?",
				new String[] { name });
		while (cursor.moveToNext()) {
			type = cursor.getInt(0);
		}
		return type;
	}

	public ArrayList<String> findAllShareLocationName() {
		ArrayList<String> shareLocName = new ArrayList<String>();
		Cursor cursor = dbOpenHelper.getWritableDatabase().rawQuery(
				"select friend_name from shareLoc", null);
		while (cursor.moveToNext()) {
			shareLocName.add(cursor.getString(0));
		}
		return shareLocName;
	}
	
	public boolean containShareUser(String name){
		Cursor cursor = dbOpenHelper.getReadableDatabase().rawQuery(
				"select count(*) from shareLoc where friend_name = ?", new String[]{name});
		cursor.moveToFirst();
		if(cursor.getInt(0) == 0)
			return false;
		return true;
		
	}

	public FriendUser findFriendInfo(String friendName) {
		Cursor cursor = dbOpenHelper
				.getWritableDatabase()
				.rawQuery(
						"select friend_name, nickname, pic_url, sex, address, signature from friends where friend_name=?",
						new String[] { friendName });
		if (cursor.moveToNext()) {
			FriendUser friendInfo = new FriendUser();
			// id 0
			friendInfo.setName(cursor.getString(0));
			friendInfo.setNickname(cursor.getString(1));
			friendInfo.setPic_url(cursor.getString(2));
			friendInfo.setSex(cursor.getString(3));
			friendInfo.setAddress(cursor.getString(4));
			friendInfo.setSignature(cursor.getString(5));
			return friendInfo;
		}
		return null;
	}

	// public FriendUser findFriendInfo(String friendName) {
	// Cursor cursor = dbOpenHelper.getWritableDatabase().rawQuery(
	// "select friend_name, nickname, pic_url, sex, address, signature from friends where friend_name=?",
	// new String[] { friendName });
	// if (cursor.moveToNext()) {
	// FriendUser friendInfo = new FriendUser();
	// // id 0
	// friendInfo.setName(cursor.getString(0));
	// friendInfo.setNickname(cursor.getString(1));
	// friendInfo.setPic_url(cursor.getString(2));
	// friendInfo.setSex(cursor.getString(3));
	// friendInfo.setAddress(cursor.getString(4));
	// friendInfo.setSignature(cursor.getString(5));
	// return friendInfo;
	// }
	// return null;
	// }
	//
	public ArrayList<FriendUser> findAllFriendInfo() {
		ArrayList<FriendUser> allFriend = new ArrayList<FriendUser>();
		Cursor cursor = dbOpenHelper
				.getWritableDatabase()
				.rawQuery(
						"select friend_name, nickname, pic_url, sex, address, signature from friends",
						null);
		while (cursor.moveToNext()) {
			FriendUser friendInfo = new FriendUser();
			friendInfo.setName(cursor.getString(0));
			friendInfo.setNickname(cursor.getString(1));
			friendInfo.setPic_url(cursor.getString(2));
			friendInfo.setSex(cursor.getString(3));
			friendInfo.setAddress(cursor.getString(4));
			friendInfo.setSignature(cursor.getString(5));
			allFriend.add(friendInfo);
		}
		return allFriend;
	}

	public long getDataCount(String tableName) {
		Cursor cursor = dbOpenHelper.getReadableDatabase().rawQuery(
				"select count(*) from " + tableName, null);
		cursor.moveToFirst();
		return cursor.getLong(0);
	}

	public void close() {
		dbOpenHelper.close();
	}

}
