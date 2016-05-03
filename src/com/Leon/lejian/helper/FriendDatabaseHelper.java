package com.Leon.lejian.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FriendDatabaseHelper extends SQLiteOpenHelper {
	private static final String name = "database.db";//���ݿ�����
    private static final int version = 1;//���ݿ�汾
	public FriendDatabaseHelper(Context context) {
		super(context, name, null, version);
	}

//	ֻ����һ�ű�
	@Override
	public void onCreate(SQLiteDatabase db) {
		 Log.e("DBOpenHelper", "DBOpenHelperDBOpenHelperDBOpenHelperDBOpenHelper");
		 db.execSQL("CREATE TABLE IF NOT EXISTS friends (id integer primary key autoincrement, friend_name varchar(50), nickname varchar(50),pic_url varchar(50), sex varchar(4), address varchar(50),signature varchar(50))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e("DBOpenHelper", "onUpgradeonUpgradeonUpgradeonUpgrade");
        db.execSQL("DROP TABLE IF EXISTS friends");
	}
	

}
