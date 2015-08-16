package com.ubicomp.ketdiary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.ui.BarButtonGenerator;
import com.ubicomp.ketdiary.ui.Typefaces;

/**
 * Activity for Help Button
 * 
 * @author Andy Chen & Blue
 */
public class HelpActivity extends Activity {

	private LayoutInflater inflater;

	private Typeface wordTypefaceBold;

	private LinearLayout titleLayout;
	private LinearLayout mainLayout;

	private Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coping);

		this.activity = this;
		titleLayout = (LinearLayout) this
				.findViewById(R.id.coping_title_layout);
		mainLayout = (LinearLayout) this.findViewById(R.id.coping_main_layout);
		inflater = LayoutInflater.from(activity);
		wordTypefaceBold = Typefaces.getWordTypefaceBold();

		mainLayout.removeAllViews();

		View title = BarButtonGenerator.createTitleView(R.string.help_page);
		titleLayout.addView(title);

		setViews();
		
	}

	private void setViews(){
		RelativeLayout aboutView = createListView(R.string.about_title,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(activity, AboutActivity.class);
						startActivity(intent);
					}
				});
		mainLayout.addView(aboutView);

		RelativeLayout settingView = createListView(R.string.setting_title,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(activity, SettingActivity.class);
						startActivity(intent);
					}
				});
		mainLayout.addView(settingView);

		RelativeLayout manualView = createListView(R.string.manual_title,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						 Intent intent = new Intent();
						 intent.setClass(activity, TutorialActivity.class);
						 startActivity(intent);
					}
				});
		mainLayout.addView(manualView);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {

		//PreferenceControl.setNotificationTimeIdx(notificationGroup.getResult());

		BootBoardcastReceiver.setRegularNotification(getBaseContext(),
				getIntent());

		super.onPause();
	}

	private RelativeLayout createListView(int titleStr, OnClickListener listener) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.bar_list_item, null);
		TextView text = (TextView) layout
				.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(titleStr);
		layout.setOnClickListener(listener);
		return layout;
	}


}
