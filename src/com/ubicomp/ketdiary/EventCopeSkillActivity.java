package com.ubicomp.ketdiary;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ubicomp.ketdiary.db.DBTip;
//import com.ubicomp.ketdiary.dialog.NoteDialog;

/** Event Cope Skill Page
 * 
 * @author mudream
 *
 */
public class EventCopeSkillActivity extends Activity {
	
	/** self activity*/
	private Activity that;
	
	TextView tv_timer;
	TextView tv_tips;
	Button btn_know;
	Button btn_tipup;
	Button btn_tipdown;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//new NoteDialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen).show();
		setContentView(R.layout.activity_qtip);
		that = this;
		tv_timer = (TextView)findViewById(R.id.qtip_tv_timer);
		tv_tips = (TextView)findViewById(R.id.qtip_tv_tips);
		btn_know = (Button)findViewById(R.id.qtip_btn_know);
		btn_tipup = (Button)findViewById(R.id.qtip_btn_tipup);
		btn_tipdown = (Button)findViewById(R.id.qtip_btn_tipdown);
		btn_know.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btn_know.setVisibility(8);
				btn_tipup.setVisibility(0);
				btn_tipdown.setVisibility(0);
				tv_tips.setText(DBTip.inst.getTip());
			}
		});;
		
		btn_tipup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_tips.setText(DBTip.inst.getTip());
			}
		});
		
		btn_tipdown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_tips.setText(DBTip.inst.getTip());
			}
		});
		
		new Timer().schedule(task, 0, 1000);
		
	}
	
	private TimerTask task = new TimerTask(){
		@Override
		public void run() {
			handler.sendEmptyMessage(1);
		}
	};
	
	private Handler handler = new Handler(){
		long ms = 600;
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch(msg.what){
			case 1:
				ms -= 1;
				long minutes = ms/60;
				long second = ms%60;
				tv_timer.setText(String.valueOf(minutes) + ":" + String.valueOf(second));
				break;
			}
		}
	};
	@Override  
	public void onBackPressed() {
	    //super.onBackPressed(); 
		//App.onTerminate();
	    // Do extra stuff here
	}
}
