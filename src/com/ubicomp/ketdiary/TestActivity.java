package com.ubicomp.ketdiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubicomp.ketdiary.db.DBControl;
import com.ubicomp.ketdiary.test.bluetoothle.BLEWrapper;
import com.ubicomp.ketdiary.test.bluetoothle.DBGWrapper;
import com.ubicomp.ketdiary.test.bluetoothle.Wrapper;

public class TestActivity extends Activity {

	TextView label_btn, label_subtitle, label_title;
	ImageView img_bg, img_ac, img_btn;
	Activity that;
	
	private class TestState{
		public void onStart(){return;}
		public void onExit(){return;}
		public void onClick(){return;}
	}
	
	TestState CertainState = null;
	Wrapper ble_wrapper = null;

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
			//new NoteDialog(that).show();
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
		    if(ble_wrapper != null)
		    	ble_wrapper.Close();
		    
		    if(DBControl.inst.getIsDev(getApplicationContext()))
		    	ble_wrapper = new DBGWrapper();
		    else	
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
			if(ble_wrapper.getState() >= BLEWrapper.STATE_EMBED){
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
		private boolean move_to_stage2 = false;
		@Override
		public void onStart(){
			// TODO: setup camera
			label_btn.setText("");
			label_subtitle.setText("請將臉對準中央，並吐口水");
			label_title.setText("請吐口水");
			test_timer = new CountDownTimer(60000, 500){
		        public void onTick(long ms){
		        	if(move_to_stage2) return;
		        	if(ble_wrapper.getState() >= BLEWrapper.STATE_1PASS){
		        		test_timer.cancel();
		        		move_to_stage2 = true;
		        		setState(new CamStage2State());
		        	}
		        }
		        public void onFinish() {
		        	if(move_to_stage2) return;
		        	if(ble_wrapper.getState() >= BLEWrapper.STATE_1PASS){
		        		setState(new CamStage2State());
		        	}else{
		        		setState(new TLEState());
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
	private class CamStage2State extends TestState{
		private int ptr;
		private int[] pids;
		@Override
		public void onStart(){
			Log.d("hi", "hi");
			// TODO: setup animate
			label_btn.setText("Stage2");
			img_bg.setVisibility(0);
			img_ac.setVisibility(0);
			img_btn.setVisibility(4);
			ptr = 0;
			pids = new int[5];
			pids[0] = R.drawable.test_progress_1;
			pids[1] = R.drawable.test_progress_2;
			pids[2] = R.drawable.test_progress_3;
			pids[3] = R.drawable.test_progress_4;
			pids[4] = R.drawable.test_progress_5;
			img_ac.setImageResource(pids[0]);
			new CountDownTimer(5000, 1000){
		        public void onTick(long ms){
		        	ptr++;
		        	img_ac.setImageResource(pids[ptr]);
		        }
		        public void onFinish() {
		        	if(ble_wrapper.getState() >= BLEWrapper.STATE_2PASS){
		        		setState(new FormState());
		        	}else{
		        		setState(new NoMuchSavilaState());
		        	}
		        }
		    }.start();
		}
		@Override
		public void onExit(){
			img_bg.setVisibility(4);
			img_ac.setVisibility(4);
			img_btn.setVisibility(0);
		}
	}
	private class NoMuchSavilaState extends TestState{
		private CountDownTimer timer;
		private boolean move_to_init = false;
		@Override
		public void onStart(){
			label_btn.setText("確認");
			label_title.setText("口水量不足，請再多吐一些");
			label_subtitle.setText("仍在檢測中");
			timer = new CountDownTimer(60000, 60000){
				public void onTick(long ms){}
				public void onFinish(){
					if(move_to_init) return;
					setState(new TLEState());
				}
			};
			timer.start();
		}
		@Override
		public void onClick(){
			move_to_init = true;
			timer.cancel();
			setState(new CamStage2State());
		}
	}
	private class TLEState extends TestState{
		@Override
		public void onStart(){
			label_btn.setText("確定");
			label_title.setText("測試超時");
		}
		@Override
		public void onClick(){
			setState(new InitState());
		}
	}
	private class FormState extends TestState{
		@Override
		public void onStart(){
			DBControl.inst.startTesting(getApplicationContext());
			startActivity(new Intent(that, QTipActivity.class));		
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		label_btn = (TextView)findViewById(R.id.tv_btn);
		label_subtitle = (TextView)findViewById(R.id.tv_subtitle);
		label_title = (TextView)findViewById(R.id.tv_title);
		
		img_bg = (ImageView)findViewById(R.id.iv_bar_bg);
		img_ac = (ImageView)findViewById(R.id.iv_bar_ac);
		img_btn = (ImageView)findViewById(R.id.imageView1);
		
		setState(new InitState());
		
		img_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CertainState.onClick();
			}
		});
		that = this;
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱
        menu.add(0, 0, 0, "說明");
        menu.add(0, 1, 1, "離開");
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //依據itemId來判斷使用者點選哪一個item
        switch(item.getItemId()) {
            case 0:
                //在TextView上顯示說明
    			startActivity(new Intent(that, InfoActivity.class));
                break;
            case 1:
                //結束此程式
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

}
