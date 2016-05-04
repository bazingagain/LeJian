package com.Leon.lejian.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Leon.lejian.R;
import com.Leon.lejian.api.Constants;
import com.Leon.lejian.service.DatabaseService;

public class ShareLocListViewAdapter extends BaseAdapter {
	private Context context; // 运行上下文
	private ArrayList<String> listContact; // 信息集合
	private LayoutInflater listContainer; // 视图容器
	ImageView contact_pic = null;
	TextView friendName = null;
	TextView shareStatus = null;

	public ShareLocListViewAdapter(Context context,
			ArrayList<String> listContact) {
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
			view = listContainer.inflate(R.layout.sharnoti_listview_item, null);
		}
		contact_pic = (ImageView) view.findViewById(R.id.share_contact_pic);
		friendName = (TextView) view.findViewById(R.id.share_contact_name);
		shareStatus = (TextView) view.findViewById(R.id.share_status);
		// 不下载图片的话 ，可以不用找contact_pic
		try {
			sdCardDir = Environment.getExternalStorageDirectory();
			fileName = "USER_" + Constants.md5(listContact.get(position))
					+ ".jpg";
			lejianUserPicPath = sdCardDir.getCanonicalPath()
					+ "/LeJianTempUserPic/" + fileName;
			File lejianTempDir = null;
			lejianTempDir = new File(sdCardDir.getCanonicalPath()
					+ "/LeJianTempUserPic");
			if ((lejianTempDir == null)||!lejianTempDir.exists())
				lejianTempDir.mkdir();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File picFile = new File(lejianUserPicPath);
		// TODO 判断是否存在 头像文件
		if ((picFile == null) || !picFile.exists()) {
			contact_pic.setImageDrawable(context.getResources().getDrawable(
					android.R.drawable.ic_menu_gallery));
		} else {
			userIconByte = Constants.getBytesFromFile(picFile);
			if (userIconByte != null) {
				bitmap = BitmapFactory.decodeByteArray(userIconByte, 0,
						userIconByte.length);
			}
			if (bitmap != null) {
				contact_pic.setImageBitmap(bitmap);
			} else {
				contact_pic.setImageDrawable(context.getResources()
						.getDrawable(android.R.drawable.ic_menu_gallery));
			}
		}

		friendName.setText(listContact.get(position));
		DatabaseService dbService  =new DatabaseService(context);
		dbService.createShareLocationTable();
		int status = dbService.getShareLocationStatus(listContact.get(position));
		if(status == Constants.STATUS_ONLINE)
			shareStatus.setText("正在共享位置");
		else if(status == Constants.STATUS_OFFLINE)
			shareStatus.setText("等待共享位置");
			
		return view;
	}
}
