package com.Leon.lejian.adapter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.Leon.lejian.R;
import com.Leon.lejian.api.Constants;
import com.Leon.lejian.bean.FriendUser;
import com.Leon.lejian.listener.MyOnClickListener;
import com.Leon.lejian.view.MyButton;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactListviewAdapter extends BaseAdapter {
	private Context context; // 运行上下文
	private List<FriendUser> listContact; // 信息集合
	private LayoutInflater listContainer; // 视图容器
	ImageView contact_pic = null;
	TextView friendName = null;

	public ContactListviewAdapter(Context context, List<FriendUser> listContact) {
		super();
		this.context = context;
		this.listContact = listContact;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
	}

	@Override
	public int getCount() {
		return listContact.size();
	}

	@Override
	public Object getItem(int position) {
		return listContact.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		Bitmap bitmap = null;
		byte[] userIconByte = null;
		String lejianUserPicPath = null;
		String fileName = null;
		File sdCardDir = null;
		if (convertView == null) {
			view = listContainer.inflate(R.layout.contact_listview_item,
					null);
		}
		contact_pic = (ImageView) view.findViewById(R.id.contact_pic);
		// 不下载图片的话 ，可以不用找contact_pic
		sdCardDir = Environment.getExternalStorageDirectory();
		fileName = "USER_" + Constants.md5(listContact.get(position).getName())
				+ ".jpg";
		try {
			lejianUserPicPath = sdCardDir.getCanonicalPath()
					+ "/LeJianTempUserPic/" + fileName;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File lejianTempDir = null; 
		try {
			lejianTempDir = new File(sdCardDir.getCanonicalPath()+"/LeJianTempUserPic");
			if(!lejianTempDir.exists())
				lejianTempDir.mkdir();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File picFile = new File(lejianUserPicPath);
		//TODO  判断是否存在 头像文件
		if(!picFile.exists()){
			contact_pic.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_gallery));
		}
		else{
			userIconByte = Constants.getBytesFromFile(picFile);
			if (userIconByte != null) {
			bitmap = BitmapFactory.decodeByteArray(userIconByte, 0,
					userIconByte.length);
			}
			if (bitmap != null) {
				contact_pic.setImageBitmap(bitmap);
			}else{
				contact_pic.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_gallery));
			}
		}
		
		friendName = (TextView) view.findViewById(R.id.contact_name);
		friendName.setText((CharSequence) listContact.get(position).getName());
		return view;
	}

}
