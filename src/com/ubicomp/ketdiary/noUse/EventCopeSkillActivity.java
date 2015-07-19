package com.ubicomp.ketdiary.noUse;

import java.io.File;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ubicomp.ketdiary.AlarmService;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.BluetoothLE.BluetoothLE_old;
import com.ubicomp.ketdiary.BluetoothLE.BluetoothListener;
import com.ubicomp.ketdiary.R.id;
import com.ubicomp.ketdiary.R.layout;
import com.ubicomp.ketdiary.color.ColorDetect2;
import com.ubicomp.ketdiary.db.DBTip;
import com.ubicomp.ketdiary.db.TestDataParser;
import com.ubicomp.ketdiary.file.ColorRawFileHandler;
import com.ubicomp.ketdiary.file.MainStorage;
import com.ubicomp.ketdiary.file.VoltageFileHandler;
import com.ubicomp.ketdiary.system.PreferenceControl;
//import com.ubicomp.ketdiary.dialog.NoteDialog;

/** Event Cope Skill Page
 * 
 * @author Andy Chen
 *
 */
public class EventCopeSkillActivity extends Activity implements BluetoothListener{
	
	private static final String TAG = "Bluetooth_Event"; 
	
	/** self activity*/
	private Activity activity;
	
	private TextView tv_timer;
	private TextView tv_tips;
	private Button btn_know;
	private Button btn_tipup;
	private Button btn_tipdown;
	
	private BluetoothLE_old ble = null;
	private TestDataParser testDataParser;
	private ColorRawFileHandler colorRawFileHandler;
	private VoltageFileHandler voltageFileHandler;
	//private long timestamp = 0;
	private File mainDirectory;
	
	private boolean first_connect=true;
	private long startTime;
	private Handler handler2 = new Handler();
	private Handler handler3 = new Handler();
	private long timeout = 2000;
	private boolean write_success = false;
	private boolean start_write = false;
	
	//Debug
	private ScrollView debugScrollView;
	private EditText debugMsg;
	private ChangeMsgHandler msgHandler;
	private TextView debugBracValueView;
	private Button btn_debug, btn_note;
	private boolean is_debug = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//new NoteDialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen).show();
		setContentView(R.layout.activity_qtip);
		activity = this;
		tv_timer = (TextView)findViewById(R.id.qtip_tv_timer);
		tv_tips = (TextView)findViewById(R.id.qtip_tv_tips);
		btn_know = (Button)findViewById(R.id.qtip_btn_know);
		btn_tipup = (Button)findViewById(R.id.qtip_btn_tipup);
		btn_tipdown = (Button)findViewById(R.id.qtip_btn_tipdown);
		
		btn_debug = (Button)findViewById(R.id.debug_button_1);
		btn_note =( Button)findViewById(R.id.debug_button_2);
		debugScrollView = (ScrollView)findViewById(R.id.debug_scroll_view);
		
		debugMsg = (EditText)findViewById(R.id.debug_msg);
		btn_debug.setOnClickListener(new DebugOnClickListener());
		//btn_note.setOnClickListener(new QuestionOnClickListener());
		
		
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
				//tv_tips.setText(DBTip.inst.getTip());
				start_write = true;
			}
		});
		
		btn_tipdown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//tv_tips.setText(DBTip.inst.getTip());
				bleConnection();
			}
		});
		
		
		startTime = System.currentTimeMillis();
		
		File dir = MainStorage.getMainStorageDirectory();
		mainDirectory = new File(dir, String.valueOf(startTime));
		if (!mainDirectory.exists())
			if (!mainDirectory.mkdirs()) {
				return;
			}
		colorRawFileHandler = new ColorRawFileHandler(mainDirectory,String.valueOf(startTime));
		voltageFileHandler = new VoltageFileHandler(mainDirectory,String.valueOf(startTime));
		
		
		
		//timestamp = System.currentTimeMillis();
		

		
		
		
		
		//設定定時要執行的方法
		handler2.removeCallbacks(updateTimer);
		//new Timer().schedule(task, 0, 1000);
		handler2.postDelayed(updateTimer, 0);
		
		//設定定時要執行的方法
		handler3.removeCallbacks(updateTimer2);
		//new Timer().schedule(task, 0, 1000);
		handler3.postDelayed(updateTimer2, 0);
	}
	
	private Runnable updateTimer = new Runnable() {
		public void run() {
			
			
			
			long spentTime = System.currentTimeMillis() - startTime;
			
			spentTime = timeout - spentTime;
			
			long minius = (spentTime/1000)/60;
			long seconds = (spentTime/1000) % 60;
			
			tv_timer.setText(minius+":"+seconds);
			if(ble!=null)
				ble.bleWriteState((byte)3);
			
			handler2.postDelayed(this, 1000);
			
			if(spentTime < 0)
				handler2.removeCallbacks(updateTimer);
			/*
			final TextView time = (TextView) findViewById(R.id.timer);
			Long spentTime = System.currentTimeMillis() - startTime;
		            //計算目前已過分鐘數
		    
		            //計算目前已過秒數
		    
		    time.setText(minius+":"+seconds);
		    handler.postDelayed(this, 1000);*/
		}
    };
    
    private Runnable updateTimer2 = new Runnable() {
		public void run() {
			bleConnection();
			Toast.makeText(activity, "Start writing File", Toast.LENGTH_SHORT).show();
			//handler2.postDelayed(this, 1000);
			/*
			final TextView time = (TextView) findViewById(R.id.timer);
			Long spentTime = System.currentTimeMillis() - startTime;
		            //計算目前已過分鐘數
		    
		            //計算目前已過秒數
		    
		    time.setText(minius+":"+seconds);
		    handler.postDelayed(this, 1000);*/
		}
    };
    @Override
    public void onResume(){
    	super.onResume();

    }
    
    private void bleConnection(){
    	if(ble == null) {
			//ble = new BluetoothLE(this, "ket_020");
			ble = new BluetoothLE_old(this, PreferenceControl.getDeviceId());//PreferenceControl.getDeviceId();
		}
		ble.bleConnect();
		//ble.bleWriteState((byte)1);
		
		
    }
	
	private TimerTask task = new TimerTask(){
		@Override
		public void run() {
			testDataParser = new TestDataParser(System.currentTimeMillis());
			testDataParser.start();
			//UploadService.startUploadService(that);
			
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
				
				
				if(ms<=0){
					Intent a_intent = new Intent(activity, AlarmService.class);
					activity.startService(a_intent);
				}
				
				break;
			}
		}
	};
	
	public void onPause() {
		
		stop();
		super.onPause();
	}
	
	
	public void stop() { 
		if(ble!=null)
			ble.bleDisconnect();
		
		if (colorRawFileHandler != null) {
			colorRawFileHandler.close();
			colorRawFileHandler = null;
		}
		
		if (voltageFileHandler != null) {
			voltageFileHandler.close();
			voltageFileHandler = null;
		}
	}
	
	@Override  
	public void onBackPressed() {
	    super.onBackPressed(); 
		//App.onTerminate();
	    // Do extra stuff here
	}
	
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ble.onBleActivityResult(requestCode, resultCode, data);
    }
	
    @Override
    public void bleNotSupported() {
    	  Toast.makeText(this, "BLE not support", Toast.LENGTH_SHORT).show();
    	  //setState(new FailState("裝置不支援"));
//        this.finish();
    }

    @Override
    public void bleConnectionTimeout() {
    	Log.i(TAG, "connect timeout");
        Toast.makeText(this, "BLE connection timeout", Toast.LENGTH_SHORT).show();
        //setState(new FailState("連接逾時"));
    }

    @Override
    public void bleConnected() {
    	/*
    	ble.bleWriteState((byte)3);
    	if(first_connect){
    		ble.bleWriteState((byte)3);
    		first_connect =false;
    	}*/
    	//is_connect = true;
        Log.i(TAG, "BLE connected");
        Toast.makeText(this, "BLE connected", Toast.LENGTH_SHORT).show();
        ble.bleWriteState((byte)3);
    }

    @Override
    public void bleDisconnected() {
        Log.i(TAG, "BLE disconnected");
        Toast.makeText(this, "BLE disconnected", Toast.LENGTH_SHORT).show();
        
        //if(ble != null) {
        //    ble = null;
        //}
    }

    @Override
    public void bleWriteStateSuccess() {
        Log.i(TAG, "BLE ACTION_DATA_WRITE_SUCCESS");
        Toast.makeText(this, "BLE write state success", Toast.LENGTH_SHORT).show();
        write_success = true;
    }

    @Override
    public void bleWriteStateFail() {
        Log.i(TAG, "BLE ACTION_DATA_WRITE_FAIL");
        Toast.makeText(this, "BLE writefstate fail", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bleNoPlug() {
        //Log.i(TAG, "No test plug");
    	
        //Toast.makeText(this, "No test plug", Toast.LENGTH_SHORT).show();
        //setState(new FailState("請將試紙匣插入裝置"));
    }

    @Override
    public void blePlugInserted(byte[] plugId) {
        Log.i(TAG, "Test plug is inserted");
        if(!write_success){
        	ble.bleWriteState((byte)3);
        }
        /*
        if(!first_connect){
        	Toast.makeText(this, "Test plug is inserted", Toast.LENGTH_SHORT).show();
        	
        	if(camera_initial)
        		setState(new FiveSecondState());
        	first_connect=true;
        }*/
        
    }



    @Override
    public void bleColorReadings(byte[] colorReadings) {
    	String feature, feature2;
    	String str1 ="";
    	String str2 ="";
    	int[] color = new int[4];
    	for(int i=0; i<8; i+=2){
    		//color[i/2] = colorReadings[i]+ colorReadings[i+1]*256;
    		color[i/2] = ((colorReadings[i+1] & 0xFF) << 8) | (colorReadings[i] & 0xFF);
    		str1 = str1+ " " + String.valueOf(color[i/2]);
    	}
    	//ColorDetect2.colorDetect(color);
    	feature = ColorDetect2.colorDetect2(color);
    	
    	writeToColorRawFile(str1+"\n");
    	//writeToColorRawFile(feature+"\n");
    	
    	
    	
    	showDebug("First:"+str1+"\n");
    	int[] color2 = new int[4];
    	for(int i=8; i<16; i+=2){
    		//color2[(i-8)/2] = colorReadings[i]+colorReadings[i+1]*256;
    		
    		color2[(i-8)/2] = ((colorReadings[i+1] & 0xFF) << 8) | (colorReadings[i] & 0xFF);
    		str2 = str2+ " " + String.valueOf(color2[(i-8)/2]);
    	}
    	
    	feature2 = ColorDetect2.colorDetect2(color2);
    	//writeToVoltageFile(feature2+"\n");
    	writeToVoltageFile(str2+"\n");
    	showDebug("Second:"+str2+"\n");
    	
    	//showDebug(">First:"+str1+" Second:"+str2);
    	Log.i(TAG, "First: "+str1);
    	Log.i(TAG, "Second: "+str2);
    	
        //Log.i(TAG, "Color sensor readings");
    }
    
    protected void writeToVoltageFile(String str) {
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("VOLTAGE", str);
		msg.setData(data);
		voltageFileHandler.sendMessage(msg);
	}
    
    protected void writeToColorRawFile(String str) {
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("COLOR", str);
		msg.setData(data);
		colorRawFileHandler.sendMessage(msg);
	}
    

	@Override
	public void bleElectrodeAdcReading(byte header, byte[] adcReading) {
		// TODO Auto-generated method stub
		//String str = new String(adcReading);
		String str = String.valueOf(adcReading[0]);
		String str2 = String.valueOf(header);
		
		String str3= " "+str2+" "+str;
		Log.i(TAG, str3);
		
		//showDebug(">"+str3);
		
		//writeToVoltageFile(str+str2+"\n");
		/*
		if(state == CAMERA_STATE && (int)adcReading[0]> FIRST_VOLTAGE_THRESHOLD && !first_voltage){
			setState(new Stage2State());
			first_voltage=true;
		}
		else if(in_stage2 && (int)adcReading[0]< SECOND_VOLTAGE_THRESHOLD && !second_voltage){
			//setState(new DoneState());
			second_voltage=true;
		}*/
	}
	
	private void checkDebug(boolean debug) {
		
		if (debug) {
			debugScrollView.setVisibility(View.VISIBLE);
			msgHandler = new ChangeMsgHandler();
			debugMsg.setText("");
			debugMsg.setOnKeyListener(null);
			//TextView debugText = (TextView)findViewById(R.id.debug_mode_text);

		} else {
			debugScrollView.setVisibility(View.INVISIBLE);
			return;
		}

	}

	private class DebugOnClickListener implements View.OnClickListener {

		private int cond;

		@Override
		public void onClick(View v) {
				
			
			if(is_debug){
				//debugScrollView.setVisibility(View.VISIBLE);
				is_debug = false;
			}
			else{
				//debugScrollView.setVisibility(View.INVISIBLE);
				is_debug = true;
			}
			checkDebug(is_debug);
		}		
	}


	public void showDebug(String message, int type) {
		//Boolean debug = PreferenceControl.isDebugMode();
		if (is_debug && msgHandler != null) {
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putInt("type", type);
			data.putString("message", message);
			msg.setData(data);
			msg.what = 0;
			msgHandler.sendMessage(msg);
		}
	}

	public void showDebug(String message) {
		showDebug(message, 0);
	}

	@SuppressLint("HandlerLeak")
	private class ChangeMsgHandler extends Handler {
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			int type = data.getInt("type");
			if (type == 0) {
				debugMsg.append("\n" + data.getString("message"));
				debugScrollView.scrollTo(0, debugMsg.getBottom() + 100);
				debugMsg.invalidate();
			} else if (type == 1) {
				//showDebugVoltage(data.getString("message"));
			}
		}
	}


	@Override
	public void bleTakePictureSuccess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateProcessRate(float rate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearProcesssRate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showImgPreview(String filePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayCurrentId(String id) {
		// TODO Auto-generated method stub
		
	}
}
