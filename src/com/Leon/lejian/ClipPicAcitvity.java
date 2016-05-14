package com.Leon.lejian;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.Leon.lejian.api.Constants;
import com.Leon.lejian.util.ActionbarBackUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zhy.utils.ImageLoader;
import com.zhy.utils.ImageLoader.Type;
import com.zhy.view.ClipImageLayout;

/**
 * http://blog.csdn.net/lmj623565791/article/details/39761281
 * 
 */
public class ClipPicAcitvity extends Activity {
	private ClipImageLayout mClipImageLayout;
	String imgPath = null;
	private ProgressDialog myDialog = null;
	byte[] datas = null;
	String lejianUserPicPath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clippic);
		ActionbarBackUtil.setActionbarBack(this, R.string.clip_pic,
				R.drawable.back_n);
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("pic_url");
		imgPath = bundle.getString("pic_url");
		mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
		ImageLoader.getInstance(3, Type.LIFO).loadImage(imgPath,
				mClipImageLayout.mZoomImageView);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.clippic_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.id_action_clip:
			if (!Constants.isNetworkAvailable(this)) {
				Toast.makeText(this, "�޿�������", Toast.LENGTH_SHORT).show();
				return super.onOptionsItemSelected(item);
			}
			if(!checkHaveStorage()){
				Toast.makeText(this, "���ⲿ�洢", Toast.LENGTH_SHORT).show();
				return super.onOptionsItemSelected(item);
			}
			//����ȡ����ͼƬ�洢������  
			String tempFile = null;
			SharedPreferences share = getSharedPreferences(
					Constants.SHARE_USERINFO, MODE_PRIVATE);
//			if(imgPath.endsWith(".jpg")||imgPath.endsWith(".JPG")){
//				tempFile = share.getString("app_user", null)+".jpg";
//			}
//			else if(imgPath.endsWith(".jpeg")||imgPath.endsWith(".JPEG")){
//				tempFile = share.getString("app_user", null)+".jpeg";
//			}
//			else if(imgPath.endsWith(".png")||imgPath.endsWith(".PNG")){
//				tempFile = share.getString("app_user", null)+".png";
//			}
			storeUserPicInLoc(share.getString("app_user", null));
			//�ϴ�
			uploadUserPic();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * ���û���ͷ���ϴ���������
	 */
	private void uploadUserPic() {
		if(lejianUserPicPath == null){
			//TODO  ��ɾ
			Toast.makeText(this, "��ȡ�洢���ļ�������", Toast.LENGTH_SHORT).show();;
			return ;
		}
		myDialog = android.app.ProgressDialog.show(this, null, null);
		Log.i("USER_PIC_PATH", "�û�ͷ���ļ�  PATHΪ��" + lejianUserPicPath);
		try {
			SharedPreferences share = getSharedPreferences(
					Constants.SHARE_USERINFO, MODE_PRIVATE);
			RequestParams params = new RequestParams();
			JSONObject json = new JSONObject();
			json.put("clientName", share.getString("app_user", null));
			json.put("fileNameWithNoSuffix", lejianUserPicPath.replace("/", ""));
			Log.i("USER_PIC_PATH", "�������û�ͷ���ļ�  PATHΪ��" + lejianUserPicPath.replace("/", ""));
			if(imgPath.endsWith(".jpg")||lejianUserPicPath.endsWith(".JPG")){
				json.put("userPicFilePathEnd", "jpg");
			}
			else if(imgPath.endsWith(".jpeg")||imgPath.endsWith(".JPEG")){
				json.put("userPicFilePathEnd", "jpeg");
			}
			else if(imgPath.endsWith(".png")||imgPath.endsWith(".PNG")){
				json.put("userPicFilePathEnd", "png");
			}
			params.addBodyParameter("setClientPic", json.toString());
			params.addBodyParameter("file", new File(lejianUserPicPath));
			HttpUtils httpUtils = new HttpUtils();
			httpUtils.configCurrentHttpCacheExpiry(1000 * 10);// ���ó�ʱʱ��
			httpUtils.send(HttpMethod.POST, Constants.HOST
					+ Constants.SET_PROFILE_ICON, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							myDialog.dismiss();
							Toast.makeText(getApplicationContext(), "ͼƬ�ϴ�ʧ��",
									Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							Log.i("TEST_REC", "���յ��Ľ��Ϊ---��" + arg0.result);
							try {
								JSONObject json = new JSONObject(arg0.result);
								if (json.getString("sendUserPicResult").equals("true")) {
									myDialog.dismiss();
									//
									Constants.userPic = datas;
									finish();
									Log.d(Constants.DEBUG, "ͷ���ϴ��ɹ�");
								}else if(json.getString("sendUserPicResult").endsWith("false")){
									myDialog.dismiss();
									Toast.makeText(getApplicationContext(), json.getString("message"),
											Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					});
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}
	
	private void storeUserPicInLoc(String userName){
		Bitmap bitmap = mClipImageLayout.clip();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		datas = baos.toByteArray();
		 // ��ȡSD����Ŀ¼
        File sdCardDir = Environment.getExternalStorageDirectory();
		try {
			File picDirFile = new File(sdCardDir+"/LeJianUserPic");
			if(!picDirFile.exists())
				picDirFile.mkdir();
			String fileName = "USER_"+Constants.md5(userName)+".jpg";
			lejianUserPicPath = sdCardDir.getCanonicalPath()+"/LeJianUserPic/"+fileName;
			File userPicFile = new File(lejianUserPicPath);
			if(userPicFile.exists()){
				userPicFile.delete();
			}
			//д�� �����ļ�
			File picFile = getFileFromBytes(datas, lejianUserPicPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Constants.userPic = datas;

	}
	
	private  boolean checkHaveStorage(){
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{
			return false ;
		}
		return true;
	}
	public  File getFileFromBytes(byte[] b, String outputFile) { 
        File ret = null;  
        BufferedOutputStream stream = null;  
        try {  
            ret = new File(outputFile);  
            FileOutputStream fstream = new FileOutputStream(ret);  
            stream = new BufferedOutputStream(fstream);  
            stream.write(b);  
        } catch (Exception e) {  
            // log.error("helper:get file from byte process error!");  
            e.printStackTrace();  
        } finally {  
            if (stream != null) {  
                try {  
                    stream.close();  
                } catch (IOException e) {  
                    // log.error("helper:get file from byte process error!");  
                    e.printStackTrace();  
                }  
            }  
        }  
        return ret;  
    } 
}
