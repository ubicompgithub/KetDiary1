package com.ubicomp.ketdiary;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TestActivity extends Activity {

	TextView label_btn, label_subtitle, label_title;
	
	private class TestState{
		public void onStart(){return;}
		public void onExit(){return;}
		public void onClick(){return;}
	}
	
	TestState CertainState = null;
	
	protected void setState(TestState sts){
		if(CertainState != null)
			CertainState.onExit();
		CertainState = sts;
		CertainState.onStart();
	}
	
	private class InitState extends TestState{
		@Override
		public void onStart(){
			label_btn.setText("開始");
			label_subtitle.setText("請點選開始進行測試");
			label_title.setText("測試尚未開始");
		}
		@Override
		public void onClick(){
			setState(new ConnState());
		}
	}
	private class ConnState extends TestState{
		@Override
		public void onStart(){
			label_btn.setText("5");
			label_subtitle.setText("請準備口水");
			label_title.setText("準備中....");
			new CountDownTimer(5000, 1000){
		        public void onTick(long ms){
		           label_btn.setText(String.valueOf(ms/1000));                 
		        }
		        public void onFinish() {
		        	if(BLEConn()){
		        		setState(new EmbedState());
		        	}else{
		        		setState(new InfoReConnState());
		        	}
		        }
		    }.start();
		}
	}	
	private class InfoReConnState extends TestState{
		@Override
		public void onStart(){
			label_btn.setText("確認");
			label_subtitle.setText("請點選確認鍵");
			label_title.setText("連線錯誤");
		}
		@Override
		public void onClick(){
			setState(new InitState());
		}
	}
	private class EmbedState extends TestState{
		@Override
		public void onStart(){
			if(BLEEmbed()){
				setState(new CamStage1State());
			}else{
				setState(new InfoEmbedState());
			}
		}
	}
	private class InfoEmbedState extends TestState{
		@Override
		public void onStart(){
			label_btn.setText("確認");
			label_subtitle.setText("請插入試紙後按下確認");
			label_title.setText("無試紙插入");
		}
		@Override
		public void onClick(){
			setState(new EmbedState());
		}
	}
	private class CamStage1State extends TestState{
		@Override
		public void onStart(){
			// TODO: setup camera
			// TODO: setup animate
			// TODO: setup clean up middle
			label_subtitle.setText("請將臉對準中央，並吐口水");
			label_title.setText("請吐口水");
			// TODO: BLE msg handler
			// Excess 1min has no slava -> judge to 
		}
	}
	private class CamStage2State extends TestState{
		@Override
		public void onStart(){
			
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		label_btn = (TextView)findViewById(R.id.tv_btn);
		label_subtitle = (TextView)findViewById(R.id.tv_subtitle);
		label_title = (TextView)findViewById(R.id.tv_title);

		setState(new InitState());
		
		((ImageView)findViewById(R.id.imageView1)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CertainState.onClick();
			}
		});
	}
	
	public boolean BLEEmbed(){
		// TODO: true BLE implement
		return true;
	}
	
	public boolean BLEConn(){
		// TODO: true BLE implement
		return true;
	}
	
	public int BLEState(){
		return 1;
	}
}
