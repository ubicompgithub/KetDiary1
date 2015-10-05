package com.ubicomp.ketdiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import com.ubicomp.ketdiary.data.db.DatabaseRestore;
import com.ubicomp.ketdiary.data.file.MainStorage;
import com.ubicomp.ketdiary.data.file.ReadDummyData;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.system.check.StartDateCheck;
import com.ubicomp.ketdiary.system.cleaner.Cleaner;
import com.ubicomp.ketdiary.ui.CustomToast;
import com.ubicomp.ketdiary.ui.CustomToastSmall;
import com.ubicomp.ketdiary.data.db.DatabaseControl;
import com.ubicomp.ketdiary.data.db.DatabaseRestoreControl;
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.TestDetail;

/**
 * The activity is for developer setting
 * 
 * @author Stanley Wang
 */
public class PreSettingActivity extends Activity {
	
	
	
	private EditText uid, did, target_good, target_bad, drink;
	private EditText voltage1, voltage2, ACountDown, VCountDown, V2CountDown;
	private EditText addDate, addTime, addPass;
	
	private Button saveButton, exchangeButton, restoreButton, debugButton,
			restoreVer1Button, dummyDataButton, changeButton, cleanButton, cassetteButton,
			addTestResultButton;
	
	private boolean debug;
	private Activity activity;
	private static final int MIN_NAME_LENGTH = 9;
	private static final String TAG = "PreSetting";
	
	private int mYear, mMonth, mDay;
	private int lYear, lMonth, lDay;
	private int v1, v2, aCount, vCount, v2Count;

	private TextView mDateDisplay;
	private Button mPickDate;

	private CheckBox lDateCheckBox;
	private TextView lDateDisplay;
	private Button lPickDate;

	private TextView versionText;

	private String target_g, target_b;
	private int target_t, drink_t;

	private CheckBox developer_switch;
	private CheckBox collectdata_switch;
	private CheckBox skip_saliva_switch;
	private CheckBox demo_switch;
	
	private AlertDialog.Builder builder;
	
	private File file;
	
	private static final int DATE_DIALOG_ID = 0;
	private static final int LOCK_DIALOG_ID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pre_setting);
		activity = this;

		uid = (EditText) this.findViewById(R.id.uid_edit);
		uid.setText(PreferenceControl.getUID());
		
		did = (EditText) this.findViewById(R.id.did_edit);
		did.setText(PreferenceControl.getDeviceId());

		collectdata_switch = (CheckBox) this.findViewById(R.id.collectdata_switch);
		collectdata_switch.setChecked(PreferenceControl.getCollectData());
		
		developer_switch = (CheckBox) this.findViewById(R.id.developer_switch);
		developer_switch.setChecked(PreferenceControl.isDeveloper());
		
		skip_saliva_switch = (CheckBox) this.findViewById(R.id.skip_saliva_switch);
		skip_saliva_switch.setChecked(PreferenceControl.isSkip());
		
		demo_switch = (CheckBox) this.findViewById(R.id.demo_switch);
		demo_switch.setChecked(PreferenceControl.isDemo());
		
		target_good = (EditText) this.findViewById(R.id.target_positive_edit);
		target_good.setText(PreferenceControl.getPostiveGoal());

		target_bad = (EditText) this.findViewById(R.id.target_negative_edit);
		target_bad.setText(PreferenceControl.getNegativeGoal());

		drink = (EditText) this.findViewById(R.id.target_drink_edit);
		drink.setText(String.valueOf(PreferenceControl.getSavingDrinkCost()));
		
		voltage1 = (EditText) this.findViewById(R.id.voltage1_edit);
		voltage1.setText(String.valueOf(PreferenceControl.getVoltag1()));
		
		voltage2 = (EditText) this.findViewById(R.id.voltage2_edit);
		voltage2.setText(String.valueOf(PreferenceControl.getVoltag2()));
		
		ACountDown = (EditText) this.findViewById(R.id.after_countdown_edit);
		ACountDown.setText(String.valueOf(PreferenceControl.getAfterCountDown()));
		
		VCountDown = (EditText) this.findViewById(R.id.voltage1_countdown_edit);
		VCountDown.setText(String.valueOf(PreferenceControl.getVoltageCountDown()));
		
		V2CountDown = (EditText) this.findViewById(R.id.voltage2_countdown_edit);
		V2CountDown.setText(String.valueOf(PreferenceControl.getVoltage2CountDown()));
		
		mDateDisplay = (TextView) findViewById(R.id.date);
		mPickDate = (Button) findViewById(R.id.date_button);

		addDate = (EditText) this.findViewById(R.id.add_date_edit);
		
		addTime = (EditText) this.findViewById(R.id.add_time_edit);
		
		addPass = (EditText) this.findViewById(R.id.add_pass_edit);

		int[] startDateData = PreferenceControl.getStartDateData();
		mYear = startDateData[0];
		mMonth = startDateData[1];
		mDay = startDateData[2];

		int[] lockDateData = PreferenceControl.getLockDateData();
		lYear = lockDateData[0];
		lMonth = lockDateData[1];
		lDay = lockDateData[2];

		saveButton = (Button) this.findViewById(R.id.uid_OK);
		saveButton.setOnClickListener(new OKOnclickListener());
		
		addTestResultButton = (Button) this.findViewById(R.id.add_testResult_OK);
		addTestResultButton.setOnClickListener(new AddTestResultOnClickListener());
		
		builder = new AlertDialog.Builder(this);
		
		changeButton = (Button) this.findViewById(R.id.changeNote);
		changeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				 Intent intent = new Intent();
				 intent.setClass(activity, SelectActivity.class);
				 startActivity(intent);					
			}
			
		});
		
		cleanButton = (Button) this.findViewById(R.id.cleanButton);
		cleanButton.setOnClickListener(new OnClickListener(){
			
			private Thread cleanThread = null;
			@Override
			public void onClick(View v) {
				
				if (cleanThread != null && !cleanThread.isInterrupted()) { //May be used some day.
					cleanThread.interrupt();
					cleanThread = null;
				}
	
				cleanThread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Cleaner.clean();
						} catch (Exception e) {
						}
					}
				});
				cleanThread.start();
				try {
					cleanThread.join(500);
				} catch (InterruptedException e) {
				}
				
			}
			
			
		});
		
		cassetteButton = (Button) this.findViewById(R.id.debug_cassette_data);
		cassetteButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(activity, SalivaActivity.class);
				startActivity(intent);
			}
		
		});
		
		
		versionText = (TextView) this.findViewById(R.id.version);

		lDateCheckBox = (CheckBox) findViewById(R.id.system_lock);
		lDateDisplay = (TextView) findViewById(R.id.lock_date);
		lPickDate = (Button) findViewById(R.id.lock_button);

		lDateCheckBox.setChecked(PreferenceControl.isLocked());

		PackageInfo pinfo;
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String versionName = pinfo.versionName;
			versionText.setText(versionName);
		} catch (NameNotFoundException e) {
			versionText.setText("Unknown");
		}

		mPickDate.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		lPickDate.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				showDialog(LOCK_DIALOG_ID);
			}
		});

		updateDisplay();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("確定清除點數?");
		builder.setPositiveButton("確定", new CleanListener());
		builder.setNegativeButton("取消", null);
		AlertDialog cleanAlertDialog = builder.create();
		exchangeButton = (Button) this.findViewById(R.id.clean_OK);
		exchangeButton.setOnClickListener(new AlertOnClickListener(
				cleanAlertDialog));

		builder = new AlertDialog.Builder(this);
		builder.setTitle("回復資料?");
		builder.setPositiveButton("確定", new RestoreOnClickListener());
		builder.setNegativeButton("取消", null);
		AlertDialog resotreAlertDialog = builder.create();
		restoreButton = (Button) this.findViewById(R.id.restore);
		restoreButton.setOnClickListener(new AlertOnClickListener(
				resotreAlertDialog));

		builder = new AlertDialog.Builder(this);
		builder.setTitle("由SoberDiary Ver1回復資料?");
		//builder.setPositiveButton("確定", new RestoreVer1OnClickListener());
		builder.setNegativeButton("取消", null);
		AlertDialog resotreAlertDialogVer1 = builder.create();
		restoreVer1Button = (Button) this.findViewById(R.id.restore_ver1);
//		restoreVer1Button.setOnClickListener(new AlertOnClickListener(
//				resotreAlertDialogVer1));
		restoreVer1Button.setOnClickListener(new RestoreVer1OnClickListener());

		debug = PreferenceControl.isDebugMode();
		debugButton = (Button) this.findViewById(R.id.debug_normal_switch);

		if (debug)
			debugButton.setText("Switch to normal mode");
		else
			debugButton.setText("Switch to debug mode");

		debugButton.setOnClickListener(new DebugOnClickListener());

		builder = new AlertDialog.Builder(this);
		builder.setTitle("使用Dummy Data?");
		builder.setMessage("使用後將會清除現有資料");
		builder.setPositiveButton("確定", new DummyDataOnClickListener());
		builder.setNegativeButton("取消", null);
		AlertDialog dummyDataDialog = builder.create();
		dummyDataButton = (Button) findViewById(R.id.debug_dummy_data);
		dummyDataButton.setOnClickListener(new AlertOnClickListener(
				dummyDataDialog));

	}

	private class RestoreOnClickListener implements
			DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			DatabaseRestore rd = new DatabaseRestore(uid.getText().toString(),
					activity);
			rd.execute();
		}
	}

//	private class RestoreVer1OnClickListener implements
//			DialogInterface.OnClickListener {
//		@Override
//		public void onClick(DialogInterface dialog, int which) {
//			/*DatabaseRestoreVer1 rd = new DatabaseRestoreVer1(uid.getText()
//					.toString(), activity);
//			rd.execute();*/
//		}
//	}
	
	private class RestoreVer1OnClickListener implements
	View.OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(activity, ModifyActivity.class);
			startActivity(intent);	
		}
}

	private class DummyDataOnClickListener implements
			DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			//DatabaseDummyData ddd = new DatabaseDummyData(activity);
			//ddd.execute();
			ReadDummyData ddd = new ReadDummyData(activity);
			ddd.execute();
		}
	}

	private class DebugOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			debug = !debug;
			PreferenceControl.setDebugMode(debug);
			if (debug) {
				debugButton.setText("Switch to normal mode");
			} else {
				debugButton.setText("Switch to debug mode");
			}
		}
	}

	private class OKOnclickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			String text = uid.getText().toString();
			String text2 = did.getText().toString();
			
			boolean check = true;
			//Log.d(TAG, "uid_length " +text.length());
			if (text.length() < MIN_NAME_LENGTH)
				check = false;
			if (!text.startsWith("rehab"))
				check = false;
			if (text.contains(" "))
				check = false;
			
			target_g = target_good.getText().toString();
			if (target_g.length() == 0)
				check = false;
			
			target_b = target_bad.getText().toString();
			if (target_bad.getText().toString().length() == 0)
				check = false;


			if (drink.getText().toString().length() == 0)
				check = false;
			else {
				drink_t = Integer.valueOf(drink.getText().toString());
				if (drink_t == 0)
					check = false;
			}
			// new Add
			if (voltage1.getText().toString().length() == 0)
				check = false;
			else {
				v1 = Integer.valueOf(voltage1.getText().toString());
				if (v1 == 0)
					check = false;
			}
			
			if (voltage2.getText().toString().length() == 0)
				check = false;
			else {
				v2 = Integer.valueOf(voltage2.getText().toString());
				if (v2 == 0)
					check = false;
			}
			
			if (ACountDown.getText().toString().length() == 0)
				check = false;
			else {
				aCount = Integer.valueOf(ACountDown.getText().toString());
				if (aCount < 180) //至少要三分鐘才能傳回照片
					check = false;
			}
			
			if (VCountDown.getText().toString().length() == 0)
				check = false;
			else {
				vCount = Integer.valueOf(VCountDown.getText().toString());
				if (vCount == 0)
					check = false;
			}
			
			if (V2CountDown.getText().toString().length() == 0)
				check = false;
			else {
				v2Count = Integer.valueOf(V2CountDown.getText().toString());
				if (v2Count == 0)
					check = false;
			}
			
			//Toast.makeText(activity, String.valueOf(check), Toast.LENGTH_SHORT).show();
			CustomToastSmall.generateToast(String.valueOf(check));
			
			if (check) {
				PreferenceControl.setUID(text);
				PreferenceControl.setDeviceId(text2);
				
				PreferenceControl.setIsDemo(demo_switch.isChecked());
				PreferenceControl.setIsSkip(skip_saliva_switch.isChecked());
				PreferenceControl.setIsDeveloper(developer_switch.isChecked());
				PreferenceControl.setCollectData(collectdata_switch.isChecked());
				PreferenceControl.setGoal2(target_g, target_b);
				//PreferenceControl.setGoal(target_g, target_t, drink_t);
				
				PreferenceControl.setVoltage1(v1);
				PreferenceControl.setVoltage2(v2);
				PreferenceControl.setAfterCountDown(aCount);
				PreferenceControl.setVoltageCountDown(vCount);
				PreferenceControl.setVoltage2CountDown(v2Count);
				
				PreferenceControl.setStartDate(mYear, mMonth, mDay);

				PreferenceControl.setLocked(lDateCheckBox.isChecked());
				if (lDateCheckBox.isChecked()) {
					PreferenceControl.setLockDate(lYear, lMonth, lDay);
				}
				activity.finish();
			}
		}
	}
	
	private void addTestResult(int result, int year,int month, int day,int intTime){
		
    	
    	//add new result
    	Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, (intTime + 1)*7, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
    	
    	long tv = cal.getTimeInMillis();
    	String cassette_id = "Dummy";
    	int isPrime = 1;
		int isFilled = 0;
		
		TestResult testResult;
		testResult = new TestResult(result, tv, cassette_id, isFilled, isPrime, 0, 0); 
		
		DatabaseControl db = new DatabaseControl();
		
		int addScore = db.insertTestResult(testResult, false);

		//db.setTestResultUploaded(tv);
		TestDetail testDetail = new TestDetail(cassette_id, tv, 0, 0, 0, 0, 0, 0, "Dummy", "Dummy");
		
		db.insertTestDetail(testDetail);
		
		File dir = MainStorage.getMainStorageDirectory();
		
		file = new File(dir + File.separator +String.valueOf(tv));
		boolean success = true;
		if (!file.exists()) {
		    success = file.mkdirs();
		}
		File testFile = new File(file + File.separator + "voltage.txt");
		File detectionFile = new File(file + File.separator + "color_raw.txt");
		
		try {
			testFile.createNewFile();
			detectionFile.createNewFile();
		} catch (Exception e) {
			Log.d("File",e.toString());
		}
		
		//加分		
		Log.d(TAG,""+tv+" "+addScore);
		
		PreferenceControl.setPoint(addScore);
		Log.d(TAG, "AddScore:"+addScore);
		
		//
		long lastTV = PreferenceControl.getLatestTestCompleteTime();
		
		if(tv > lastTV)
			PreferenceControl.setLatestTestCompleteTime(tv);
		
		//bar位置
		int addPos = 0;
		if (addScore == 0 && result == 1){ // TestFail & get no credit 
			CustomToast.generateToast(R.string.after_test_fail, -1);
			//addPos = -1;
			addPos = 0;
		}
		else if(result == 1){
			CustomToast.generateToast(R.string.after_test_fail, addScore);
			//addPos = -1;
			addPos = 0;
		}
		else{
			CustomToast.generateToast(R.string.after_test_pass, addScore);
			//addPos = 1;
			addPos = 2;
		}
		
		if(StartDateCheck.afterStartDate())
			PreferenceControl.setPosition(addPos);
		
		
		CustomToastSmall.generateToast("新增成功");
	}
	
	private class AddTestResultOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			String sDate, sTime, sPass;
			sDate = addDate.getText().toString();
			sTime = addTime.getText().toString();
			sPass = addPass.getText().toString();

			long llDate = Long.parseLong(sDate);
			if(llDate > 1e8){
				CustomToastSmall.generateToast("時間格式錯誤");
				return;
			}
			int intDate = (int)llDate;
			final int intTime = Integer.valueOf(sTime);
			int intPass = Integer.valueOf(sPass);
			
			final int result = intPass; // 1 is fail, 0 is pass
			final int year = intDate / 10000;
			final int month =( (intDate % 10000 ) / 100 )- 1;
			final int day = intDate % 100;
	    	
			Calendar rightNow = Calendar.getInstance();
			int now_year = rightNow.get(Calendar.YEAR);
			int now_month = rightNow.get(Calendar.MONTH) + 1;
			int now_day = rightNow.get(Calendar.DAY_OF_MONTH);
			long now_Date = now_year*10000 + now_month*100 + now_day;
			
	    	//check date or pass 
			if(now_Date <= llDate){
				CustomToastSmall.generateToast("只限新增今天以前");
	    		return;
			}
	    	if(year > 10000 || (month > 11 || month < 0) || (day > 31 || day < 0) || (intTime < 0 || intTime > 3)){
	    		//Toast.makeText(this, "時間格式錯誤", Toast.LENGTH_SHORT).show();
	    		CustomToastSmall.generateToast("時間格式錯誤");
	    		return;
	    	}
	    	if(result > 1 || result < 0){
	    		//Toast.makeText(this, "pass/fail請輸入0或1", Toast.LENGTH_SHORT).show();
	    		CustomToastSmall.generateToast("pass/fail請輸入0或1");
	    		return;
	    	}
			
	    	String mesg = "";
	    	mesg += year + "/" + (month+1) + "/" + day + "/";
	    	if(intTime == 0)
	    		mesg += "上午\n";
	    	if(intTime == 1)
	    		mesg += "下午\n";
	    	if(intTime == 2)
	    		mesg += "晚上\n";
	    	if(result == 0)
	    		mesg += "檢測 : 通過\n";
	    	if(result == 1)
	    		mesg += "檢測 : 未通過\n";
	    	
	    	
		    builder.setTitle("確定新增該事件?");
		    builder.setMessage(mesg);
		    //builder.setIcon(android.R.drawable.ic_dialog_info);
		    builder.setPositiveButton("確定",
		       new DialogInterface.OnClickListener()
		       {
		 
		           @Override
		           public void onClick(DialogInterface dialog, int which)
		           {
		        	   addTestResult(result, year, month, day, intTime);
		           }
		       });
		    builder.setNegativeButton("取消",
		       new DialogInterface.OnClickListener()
		       {
		 
		           @Override
		           public void onClick(DialogInterface dialog, int which)
		           {
		               //Toast.makeText(DialogTest2.this, "取消",
		               //   Toast.LENGTH_LONG).show();
		 
		           }
		       });
		    builder.create().show();
		}
	};

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

	private class CleanListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			PreferenceControl.exchangeCoupon();
		}
	}

	private void updateDisplay() {
		this.mDateDisplay
				.setText(new StringBuilder().append(mMonth + 1).append("-")
						.append(mDay).append("-").append(mYear).append(" "));
		this.lDateDisplay
				.setText(new StringBuilder().append(lMonth + 1).append("-")
						.append(lDay).append("-").append(lYear).append(" "));
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	private DatePickerDialog.OnDateSetListener lDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			lYear = year;
			lMonth = monthOfYear;
			lDay = dayOfMonth;
			updateDisplay();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		case LOCK_DIALOG_ID:
			return new DatePickerDialog(this, lDateSetListener, lYear, lMonth,
					lDay);
		}
		return null;
	}
	
}
