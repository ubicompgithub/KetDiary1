package com.ubicomp.ketdiary;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ubicomp.ketdiary.db.DBControl;

/** Setting Page
 * 
 * @author mudream
 *
 */
public class DevActivity extends Activity {
	
	private CheckBox cb_is_dev;
	private EditText et_user_id;
	private EditText et_device_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dev);
		cb_is_dev = (CheckBox)findViewById(R.id.dev_cb_is_dev);
		et_user_id = (EditText)findViewById(R.id.dev_et_user_id);
		et_device_id = (EditText)findViewById(R.id.dev_et_device_id);
		cb_is_dev.setChecked(DBControl.inst.getIsDev());
		et_user_id.setText(DBControl.inst.getUserID());
		et_device_id.setText(DBControl.inst.getDeviceID());
		Button btn_enter = (Button)findViewById(R.id.dev1_btn_enter);
		Button btn_cancel = (Button)findViewById(R.id.dev1_btn_cancel);
		btn_enter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DBControl.inst.setIsDev(cb_is_dev.isChecked());
				DBControl.inst.setUserID(et_user_id.getText().toString());
				DBControl.inst.setDeviceID(et_device_id.getText().toString());
				finish();
			}
		});
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
