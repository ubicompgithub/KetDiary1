package com.ubicomp.ketdiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.ui.BarButtonGenerator;
import com.ubicomp.ketdiary.ui.Typefaces;

/**
 * Activity for Help Button
 * 
 * @author Andy Chen
 */
public class ModifyActivity extends Activity {

	private LayoutInflater inflater;

	private Typeface wordTypefaceBold;

	private LinearLayout titleLayout;
	private LinearLayout mainLayout;

	private Activity activity;
	private DatabaseControl db;
	private static final String[] RESULT = {"陰性", "陽性"};
	
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
		
		db = new DatabaseControl();
		
		

		setViews();
		
	}

	private void setViews(){
		
		TestResult[] testResult = db.getAllPrimeTestResult();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("確定修改結果?");
		//builder.setPositiveButton("確定", new ModifyListener());
		builder.setNegativeButton("取消", null);
		AlertDialog cleanAlertDialog = builder.create();
		
		for(int i=0; i<testResult.length; i++){
			
			int year = testResult[i].getTv().getYear();
			int month = testResult[i].getTv().getMonth();
			int day = testResult[i].getTv().getDay();
			final long ts = testResult[i].getTv().getTimestamp();
			final int result = testResult[i].getResult();
			String text = year+"年"+(month+1)+"月"+day+"日"+" 結果: "+RESULT[result];
			RelativeLayout aboutView = createListView(text,
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							db.modifyResultByTs(ts, (result ^ 1) );
//							finish();
//							startActivity(getIntent());
							updateView();
						}
			});
			mainLayout.addView(aboutView);		
		}
	}
	
	private void updateView(){
		mainLayout.removeAllViews();
		setViews();
	}
	
	private class ModifyListener implements
	DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
	/*DatabaseRestoreVer1 rd = new DatabaseRestoreVer1(uid.getText()
			.toString(), activity);
	rd.execute();*/
		}
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

	private RelativeLayout createListView(String titleStr, OnClickListener listener) {

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
