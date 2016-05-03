package com.Leon.lejian;


import java.io.File;
import java.io.IOException;

import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.FriendUser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendProfileActivity extends Activity{
	private ImageView friend_pic = null;
	private String pic_url = null;
	private TextView friend_name = null;
	private TextView friend_nickname = null;
	private TextView friend_sex = null;
	private TextView friend_address = null;
	private TextView friend_signature = null;
	private Intent intent = null;
	private Bundle bundle = null;
	private FriendUser user = null;	
	
	Bitmap bitmap = null;
	byte[] userIconByte = null;
	String lejianUserPicPath = null;
	String fileName = null;
	File sdCardDir = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_profile);
		intent = getIntent();
		bundle = intent.getBundleExtra("showRequstUserInfo");
		user= (FriendUser) bundle.getSerializable("friend");
		initComponent(user);
	}
	
	private void initComponent(FriendUser user){
		friend_pic = (ImageView) findViewById(R.id.friend_pic);
		friend_name = (TextView) findViewById(R.id.friend_name);
		friend_nickname = (TextView) findViewById(R.id.friend_nickname);
		friend_sex = (TextView) findViewById(R.id.friend_sex);
		friend_address = (TextView) findViewById(R.id.friend_address);
		friend_signature = (TextView) findViewById(R.id.friend_signature);
		setValue(user);
	}
	
	private void setValue(FriendUser user){
		friend_pic.setImageBitmap(getUserIcon(user.getName()));
		friend_name.setText("姓名："+ user.getName());
		friend_nickname.setText("昵称：" + user.getNickname());
		pic_url = user.getPic_url();
		friend_sex.setText(user.getSex());
		friend_address.setText(user.getAddress());
		friend_signature.setText(user.getSignature());
	}
	
	private Bitmap getUserIcon(String userName) {
		// 不下载图片的话 ，可以不用找contact_pic
		sdCardDir = Environment.getExternalStorageDirectory();
		fileName = "USER_" + Constants.md5(userName)
				+ ".jpg";
		try {
			lejianUserPicPath = sdCardDir.getCanonicalPath()
					+ "/LeJianTempUserPic/" + fileName;
		} catch (IOException e) {
			e.printStackTrace();
		}
		userIconByte = Constants.getBytesFromFile(new File(lejianUserPicPath));
		if (userIconByte != null) {
			bitmap = BitmapFactory.decodeByteArray(userIconByte, 0,
					userIconByte.length);
		}
		return bitmap;
	}
	

}
