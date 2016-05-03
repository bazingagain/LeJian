package com.zhy.imageloader;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.Leon.lejian.ClipPicAcitvity;
import com.Leon.lejian.R;
import com.zhy.utils.CommonAdapter;

public class MyAdapter extends CommonAdapter<String>
{

	/**
	 * 文件夹路径 
	 */
	private String mDirPath;
	private Activity activity;

	public MyAdapter(Activity activity, List<String> mDatas, int itemLayoutId,
			String dirPath)
	{
		super(activity, mDatas, itemLayoutId);
		this.activity = activity;
		this.mDirPath = dirPath;
	}

	@Override
	public void convert(final com.zhy.utils.ViewHolder helper, final String item)
	{
		// 设置no_pic  
		helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
				// 设置图片  
		helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);
		
		final ImageView mImageView = helper.getView(R.id.id_item_image);
		
		mImageView.setColorFilter(null);
		// 设置ImageView的点击事件  
		mImageView.setOnClickListener(new OnClickListener()
		{
			// 设置ImageView的点击事件  
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(activity, ClipPicAcitvity.class);
				Bundle bundle = new Bundle();
				bundle.putString("pic_url", mDirPath + "/" + item);
				intent.putExtra("pic_url", bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.startActivity(intent);
				activity.finish();
			}
		});
	}
}
