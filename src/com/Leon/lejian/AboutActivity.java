package com.Leon.lejian;

import com.Leon.lejian.util.ActionbarBackUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		// …Ë÷√œ‘ æ∑µªÿ
		ActionbarBackUtil.setActionbarBack(this, R.string.about,
				R.drawable.back_n);

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
