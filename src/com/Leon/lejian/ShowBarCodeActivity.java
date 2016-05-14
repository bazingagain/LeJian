package com.Leon.lejian;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.Leon.lejian.api.Constants;
import com.Leon.lejian.util.ActionbarBackUtil;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.encoder.QRCode;
import com.zxing.encoding.EncodingHandler;

public class ShowBarCodeActivity extends Activity{
	TextView nametv = null;
	TextView addresstv = null;
	ImageView myPicImg = null;
	ImageView myBarCodeImg = null;
	Bitmap bitmap;
	byte[] clipPic = null;
	Bitmap userPicBitmap = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showbarcode);
		initComponent();
	}
	private void initComponent(){
		ActionbarBackUtil.setActionbarBack(this, R.string.barcode_label,
				R.drawable.back_n);
		SharedPreferences share = getSharedPreferences(
				Constants.SHARE_USERINFO, MODE_PRIVATE);
		myPicImg = (ImageView) findViewById(R.id.barcode_userPicImg);
		clipPic = Constants.userPic;// 裁剪传过来的头像
		if(clipPic!=null){
			userPicBitmap = BitmapFactory.decodeByteArray(clipPic, 0, clipPic.length);
		}
		if (userPicBitmap != null)
		{
			myPicImg.setImageBitmap(userPicBitmap);
		}
		myBarCodeImg = (ImageView) findViewById(R.id.barcode_userBarCode);
		nametv = (TextView) findViewById(R.id.barcode_userName);
		addresstv = (TextView) findViewById(R.id.barcode_userAddress);
		try {
			bitmap = EncodingHandler.createQRCode(share.getString("app_user", null), 350);
			myBarCodeImg.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		nametv.setText(share.getString("app_user", null));
		addresstv.setText("中国 "+share.getString("app_user_address", null));
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
