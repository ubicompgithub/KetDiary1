package com.ubicomp.ketdiary;

import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ubicomp.ketdiary.db.DBControl;
import com.ubicomp.ketdiary.dialog.NoteDialog;

public class QTipActivity extends Activity {

	private Activity that;
	
	TextView tv_timer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new NoteDialog(this).show();
		setContentView(R.layout.activity_qtip);
		Button btn_know = (Button)findViewById(R.id.qtip_btn_know);
		that = this;
		tv_timer = (TextView)findViewById(R.id.qtip_tv_timer);
		
		btn_know.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(that, TipsActivity.class));
			}
		});;
		
		new Timer().schedule(task, 0, 1000);
		
	}
	
	private TimerTask task = new TimerTask(){
		@Override
		public void run() {
			handler.sendEmptyMessage(1);
		}
	};
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch(msg.what){
			case 1:
				long ms = 600 - (DBControl.inst.getTestMs(getApplicationContext()))/1000;
				long minutes = ms/60;
				long second = ms%60;
				tv_timer.setText(String.valueOf(minutes) + ":" + String.valueOf(second));
				break;
			}
		}
	};
}
