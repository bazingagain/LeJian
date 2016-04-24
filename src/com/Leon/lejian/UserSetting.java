package com.Leon.lejian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class UserSetting extends Activity implements OnClickListener{
	LinearLayout modify_password = null;
	LinearLayout feedback = null;
	LinearLayout about = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_setting);
		initComponent();
	}
	
	private void initComponent(){
		modify_password = (LinearLayout) findViewById(R.id.modify_password);
		modify_password.setOnClickListener(this);
		feedback = (LinearLayout) findViewById(R.id.feedback);
		feedback.setOnClickListener(this);
		about = (LinearLayout) findViewById(R.id.about);
		about.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.modify_password:
			Intent intentModifyPassword = new Intent(UserSetting.this, ModifyPasswordActivity.class);
			startActivity(intentModifyPassword);
			break;
		case R.id.feedback:
			Intent intentFeedback = new Intent(UserSetting.this, FeedbackActivity.class);
			startActivity(intentFeedback);
			break;
		case R.id.about:
			Intent intentAbout = new Intent(UserSetting.this, AboutActivity.class);
			startActivity(intentAbout);
			break;

		default:
			break;
		}
	}
}