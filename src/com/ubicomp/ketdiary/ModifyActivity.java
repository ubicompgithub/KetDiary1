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
	private static final String[] RESULT = {"陰性", "陽性", ""};
	
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
		
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("確定修改結果?");
//		//builder.setPositiveButton("確定", new ModifyListener());
//		builder.setNegativeButton("取消", null);
//		AlertDialog cleanAlertDialog = builder.create();
		
		for(int i=0; i<testResult.length; i++){
			
			int year = testResult[i].getTv().getYear();
			int month = testResult[i].getTv().getMonth();
			int day = testResult[i].getTv().getDay();
			final long ts = testResult[i].getTv().getTimestamp();
			final int result = testResult[i].getResult();
			String text = year+"年"+(month+1)+"月"+day+"日"+" 結果: "+RESULT[result];
			
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("確定修改結果?");
//			builder.setPositiveButton("確定", new ModifyListener(ts, result)); 
//			builder.setNegativeButton("取消", null);
			builder.setNegativeButton("確定", new ModifyListener(ts, result)); 
			builder.setPositiveButton("取消", null);
			AlertDialog cleanAlertDialog = builder.create();	
			RelativeLayout aboutView = createListView(text,
					new AlertOnClickListener(cleanAlertDialog));
			mainLayout.addView(aboutView);		
		}
	}
	private class AlertOnClickListener implements View.OnClickListener {

		private AlertDialog alertDialog;

		public AlertOnClickListener(AlertDialog ad) {
			this.alertDialog = ad;
		}

		@Override
		public void onClick(View v) {
			alertDialog.show();
		}
	}
	
	private void updateView(){
		mainLayout.removeAllViews();
		setViews();
	}
	
	private class ModifyListener implements
	DialogInterface.OnClickListener {
		long ts;
		int result;
		public ModifyListener(long ts, int result){
			this.ts = ts;
			this.result = result;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			db.modifyResultByTs(ts, (result ^ 1) );
			updateView();
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
