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

public class AddFriendListviewAdapter extends BaseAdapter {
	private Context context; // 运行上下文
	private List<FriendUser> listFriend; // 信息集合
	private LayoutInflater listContainer; // 视图容器
	MyButton button = null;
	ImageView friend_pic = null;
	TextView friendName = null;
	Bitmap bitmap = null;
	byte[] userIconByte = null;
	String lejianUserPicPath = null;
	String fileName = null;
	File sdCardDir = null;

	public AddFriendListviewAdapter(Context context, List<FriendUser> listFriend) {
		super();
		this.context = context;
		this.listFriend = listFriend;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
	}

	@Override
	public int getCount() {
		return listFriend.size();
	}

	@Override
	public Object getItem(int position) {
		return listFriend.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = listContainer.inflate(R.layout.add_friend_listview_item,
					null);
		}

		// 不下载图片的话 ，可以不用找friend_pic
		sdCardDir = Environment.getExternalStorageDirectory();
		fileName = "USER_" + Constants.md5(listFriend.get(position).getName())
				+ ".jpg";
		try {
			lejianUserPicPath = sdCardDir.getCanonicalPath()
					+ "/LeJianTempUserPic/" + fileName;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		userIconByte = Constants.getBytesFromFile(new File(lejianUserPicPath));
		bitmap = setRequestAddUserImage(userIconByte);
		friend_pic = (ImageView) view.findViewById(R.id.add_friend_pic);
		if (bitmap != null) {
			friend_pic.setImageBitmap(bitmap);
		}else{
			 friend_pic.setImageDrawable(this.context.getResources().getDrawable(android.R.drawable.ic_menu_gallery));
		}
		friendName = (TextView) view.findViewById(R.id.add_friend_name);
		friendName.setText((CharSequence) listFriend.get(position).getName());

		button = (MyButton) view.findViewById(R.id.add_agreeBtn);
		button.setIndex(position);
		button.setOnClickListener(MyOnClickListener.getInstance(this,
				this.context, listFriend.get(position)));
		return view;
	}

	private Bitmap setRequestAddUserImage(byte[] userImageByte) {
		if (userImageByte != null) {
			bitmap = BitmapFactory.decodeByteArray(userImageByte, 0,
					userImageByte.length);
		}
		return bitmap;
	}
}
