package com.ubicomp.ketdiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class InfoActivity extends Activity {

	private Activity that;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		ImageView iv = ((ImageView)findViewById(R.id.about_logo0));
		that = this;
		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent ten = new Intent(that, DevActivity.class);
				startActivity(ten);
			}
		});
	}
}
