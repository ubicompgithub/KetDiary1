package com.ubicomp.ketdiary;

import com.ubicomp.ketdiary.test.bluetoothle.BLEWrapper;
import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TestActivity extends Activity {

	TextView label_btn, label_subtitle, label_title;
	
	Activity that;
	
	private class TestState{
		public void onStart(){return;}
		public void onExit(){return;}
		public void onClick(){return;}
	}
	
	TestState CertainState = null;
	BLEWrapper ble_wrapper = null;

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
			label_btn.setText("10");
			label_subtitle.setText("請準備口水");
			label_title.setText("準備中....");
			new CountDownTimer(10000, 1000){
		        public void onTick(long ms){
		           label_btn.setText(String.valueOf(ms/1000));                 
		        }
		        public void onFinish() {
		        	if(ble_wrapper.isConn()){
		        		setState(new EmbedState());
		        	}else{
		        		setState(new InfoReConnState());
		        	}
		        }
		    }.start();
		    
		    ble_wrapper = new BLEWrapper(that);
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
			label_btn.setText("");
			label_subtitle.setText("");
			label_title.setText("");
			if(ble_wrapper.getState() >= ble_wrapper.STATE_EMBED){
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
		private CountDownTimer test_timer = null;
		@Override
		public void onStart(){
			// TODO: setup camera
			label_btn.setText("");
			label_subtitle.setText("請將臉對準中央，並吐口水");
			label_title.setText("請吐口水");
			// TODO: BLE msg handler
			test_timer = new CountDownTimer(60000, 500){
		        public void onTick(long ms){
		        	if(ble_wrapper.getState() >= ble_wrapper.STATE_1PASS){
		        		setState(new CamStage2State());
		        		test_timer.cancel();
		        	}
		        }
		        public void onFinish() {
		        	if(ble_wrapper.getState() >= ble_wrapper.STATE_1PASS){
		        		setState(new CamStage2State());
		        	}else{
		        		setState(new NoSavilaState());
		        	}
		        }
		    };
		    test_timer.start();
		}
		@Override
		public void onExit(){
			if(test_timer != null)
				test_timer.cancel();
		}
	}
	private class NoSavilaState extends TestState{
		@Override
		public void onStart(){
			label_btn.setText("確認");
			label_title.setText("沒口水？");
			label_subtitle.setText("請按確認回到吐口水階段");
		}
		@Override 
		public void onClick(){
			setState(new CamStage1State());
		}
	}
	private class CamStage2State extends TestState{
		@Override
		public void onStart(){
			// TODO: setup animate
			new CountDownTimer(60000, 500){
		        public void onTick(long ms){
		        	// TODO: running bar
		        }
		        public void onFinish() {
		        	if(ble_wrapper.getState() >= ble_wrapper.STATE_2PASS){
		        		setState(new FormState());
		        	}else{
		        		setState(new NoSavilaState());
		        	}
		        }
		    };
		}
	}
	private class NoMuchSavilaState extends TestState{
		@Override
		public void onStart(){
			label_btn.setText("確認");
			label_title.setText("口水量不足");
			label_subtitle.setText("請多吐口水");
		}
		@Override
		public void onClick(){
			setState(new CamStage2State());
		}
	}
	private class FormState extends TestState{
		@Override
		public void onStart(){
			label_title.setText("form stage");
			// TODO: setup form
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
		that = this;
	}
}
