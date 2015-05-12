package com.ubicomp.ketdiary;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ubicomp.ketdiary.db.DBControl;

public class DevActivity extends Activity {
	
	private CheckBox cb_is_dev;
	private EditText et_user_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dev);
		cb_is_dev = (CheckBox)findViewById(R.id.dev_cb_is_dev);
		et_user_id = (EditText)findViewById(R.id.dev_et_user_id);
		cb_is_dev.setChecked(DBControl.inst.getIsDev(getApplicationContext()));
		et_user_id.setText(String.valueOf(DBControl.inst.getUserID(getApplicationContext())));
		Button btn_enter = (Button)findViewById(R.id.dev1_btn_enter);
		btn_enter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DBControl.inst.setIsDev(getApplicationContext(), cb_is_dev.isChecked());
				DBControl.inst.setUserID(getApplicationContext(), 
										 Integer.parseInt(et_user_id.getText().toString()));
				finish();
			}
		});
		
	}
}
