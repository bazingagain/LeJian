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
        String sql ="CREATE TABLE IF NOT EXISTS friends (id integer primary key autoincrement, friend_name varchar(50), nickname varchar(50),pic_url varchar(50), sex varchar(4), address varchar(50),signature varchar(50))";
        dbOpenHelper.getWritableDatabase().execSQL(sql);
    }
    
    public void saveFriendInfo(FriendUser friendInfo) {
        dbOpenHelper.getWritableDatabase().execSQL(
                "insert into friends (friend_name, nickname, pic_url, sex, address, signature) values(?,?,?,?,?,?)",
                new Object[] { friendInfo.getName(), friendInfo.getNickname(),
                		friendInfo.getPic_url(), friendInfo.getSex(),
                		friendInfo.getAddress(), friendInfo.getSignature() });
    }
    public void updateFriendInfo(FriendUser friendInfo) {
        dbOpenHelper.getWritableDatabase().execSQL(
                "update friends set nickname=?, pic_url=?, sex=?,address=?,signature=? where friend_name=?",
                new Object[] { friendInfo.getNickname(), friendInfo.getPic_url(),
                		friendInfo.getSex(), friendInfo.getAddress(),
                		friendInfo.getSignature(), friendInfo.getName() });
    }
    public FriendUser findFriendInfo(String friendName) {
        Cursor cursor = dbOpenHelper.getWritableDatabase().rawQuery(
                "select friend_name, nickname, pic_url, sex, address, signature from friends where friend_name=?",
                new String[] { friendName });
        if (cursor.moveToNext()) {
        	FriendUser friendInfo = new FriendUser();
        	// id 0
        	friendInfo.setName(cursor.getString(1));
        	friendInfo.setNickname(cursor.getString(2));
        	friendInfo.setPic_url(cursor.getString(3));
        	friendInfo.setSex(cursor.getString(4));
        	friendInfo.setAddress(cursor.getString(5));
        	friendInfo.setSignature(cursor.getString(6));
            return friendInfo;
        }
        return null;
    }
//    public FriendUser findFriendInfo(int position) {
//    	Cursor cursor = dbOpenHelper.getWritableDatabase().rawQuery(
//    			"select friend_name, nickname, pic_url, sex, address, signature from friends where friend_name=?",
//    			new String[] { friendName });
//    	if (cursor.moveToNext()) {
//    		FriendUser friendInfo = new FriendUser();
//    		// id 0
//    		friendInfo.setName(cursor.getString(1));
//    		friendInfo.setNickname(cursor.getString(2));
//    		friendInfo.setPic_url(cursor.getString(3));
//    		friendInfo.setSex(cursor.getString(4));
//    		friendInfo.setAddress(cursor.getString(5));
//    		friendInfo.setSignature(cursor.getString(6));
//    		return friendInfo;
//    	}
//    	return null;
//    }
    
    public ArrayList<FriendUser> findAllFriendInfo(){
    	ArrayList<FriendUser> allFriend = new ArrayList<FriendUser>();
    	 Cursor cursor = dbOpenHelper.getWritableDatabase().rawQuery(
                 "select friend_name, nickname, pic_url, sex, address, signature from friends",null);
         while(cursor.moveToNext()) {
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
