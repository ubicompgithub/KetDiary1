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

import com.ubicomp.ketdiary.data.db.DatabaseControl;
import com.ubicomp.ketdiary.data.structure.Cassette;
import com.ubicomp.ketdiary.ui.BarButtonGenerator;
import com.ubicomp.ketdiary.ui.Typefaces;

/**
 * Activity for Help Button
 * 
 * @author Andy Chen
 */
public class SalivaActivity extends Activity {

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
		
		Cassette[] cassette = db.getAllCassette();
		
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("確定修改結果?");
//		//builder.setPositiveButton("確定", new ModifyListener());
//		builder.setNegativeButton("取消", null);
//		AlertDialog cleanAlertDialog = builder.create();
		
		for(int i=0; i<cassette.length; i++){
			
			int isUsed = cassette[i].getisUsed();
			String cassetteId = cassette[i].getCassetteId();
			
			String text = "ID: " + cassetteId + " isUsed: " + isUsed;
//			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle( text + "\n");
			builder.setNegativeButton("修改", new ModifyListener(cassetteId, isUsed)); 
			builder.setPositiveButton("刪除", new CleanListener(cassetteId));
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
		String id;
		int isUsed;
		public ModifyListener(String id, int isUsed){
			this.id = id;
			this.isUsed = isUsed;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			db.modifyCassetteById(id, isUsed^1);
			updateView();
		}
	}
	
	private class CleanListener implements
	DialogInterface.OnClickListener {
		String id;
		
		public CleanListener(String id){
			this.id = id;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			db.deleteCassetteById(id);
			updateView();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
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
