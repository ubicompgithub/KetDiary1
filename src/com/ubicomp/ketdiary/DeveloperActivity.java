package com.ubicomp.ketdiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ubicomp.ketdiary.system.Config;

/** Activity for checking if the user belongs to developers */
public class DeveloperActivity extends Activity {

	/** EditText for entering the password */
	private EditText password;
	/** Button for entering and checking if the password is correct */
	private Button enter;

	private static final String PASSWORD = Config.PASSWORD;

	@Override
	/**Create the activity*/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_developer);

		password = (EditText) this.findViewById(R.id.developer_edit);
		enter = (Button) this.findViewById(R.id.developer_button);
		enter.setOnClickListener(new EnterOnClickListener());
	}

	/** OnClickListener for the Enter Button */
	private class EnterOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			String pwd = password.getText().toString();
			if (pwd.equals(PASSWORD)) {
				Intent newIntent = new Intent(getBaseContext(),
						PreSettingActivity.class);
				startActivity(newIntent);
				finish();
			} else
				finish();
		}

	}

}
