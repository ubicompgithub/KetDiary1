package com.ubicomp.ketdiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ubicomp.ketdiary.file.ReadDummyData;
import com.ubicomp.ketdiary.system.PreferenceControl;

/**
 * The activity is for developer setting
 * 
 * @author Stanley Wang
 */
public class PreSettingActivity extends Activity {

	private EditText uid, did, target_good, target, drink;
	private EditText voltage1, voltage2, ACountDown, VCountDown;

	private Button saveButton, exchangeButton, restoreButton, debugButton,
			restoreVer1Button, dummyDataButton;
	private boolean debug;
	private Activity activity;
	private static final int MIN_NAME_LENGTH = 3;

	private int mYear, mMonth, mDay;
	private int lYear, lMonth, lDay;
	private int v1, v2, aCount, vCount;

	private TextView mDateDisplay;
	private Button mPickDate;

	private CheckBox lDateCheckBox;
	private TextView lDateDisplay;
	private Button lPickDate;

	private TextView versionText;

	private String target_g;
	private int target_t, drink_t;

	private CheckBox developer_switch;
	private CheckBox collectdata_switch;

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

		target_good = (EditText) this.findViewById(R.id.target_good_edit);
		target_good.setText(PreferenceControl.getSavingGoal());

		target = (EditText) this.findViewById(R.id.target_money_edit);
		target.setText(String.valueOf(PreferenceControl.getSavingGoalMoney()));

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
		
		mDateDisplay = (TextView) findViewById(R.id.date);
		mPickDate = (Button) findViewById(R.id.date_button);

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
		builder.setPositiveButton("確定", new RestoreVer1OnClickListener());
		builder.setNegativeButton("取消", null);
		AlertDialog resotreAlertDialogVer1 = builder.create();
		restoreVer1Button = (Button) this.findViewById(R.id.restore_ver1);
		restoreVer1Button.setOnClickListener(new AlertOnClickListener(
				resotreAlertDialogVer1));

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
			/*DatabaseRestore rd = new DatabaseRestore(uid.getText().toString(),
					activity);
			rd.execute();*/
		}
	}

	private class RestoreVer1OnClickListener implements
			DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			/*DatabaseRestoreVer1 rd = new DatabaseRestoreVer1(uid.getText()
					.toString(), activity);
			rd.execute();*/
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
			if (text.length() < MIN_NAME_LENGTH)
				check = false;
			if (!text.startsWith("rehab"))
				check = false;

			target_g = target_good.getText().toString();
			if (target_g.length() == 0)
				check = false;

			if (target.getText().toString().length() == 0)
				check = false;
			else {
				target_t = Integer.valueOf(target.getText().toString());
				if (target_t <= 0)
					check = false;
			}

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
				if (aCount == 0)
					check = false;
			}
			
			if (VCountDown.getText().toString().length() == 0)
				check = false;
			else {
				vCount = Integer.valueOf(VCountDown.getText().toString());
				if (vCount == 0)
					check = false;
			}
			
			Toast.makeText(activity, String.valueOf(check), Toast.LENGTH_SHORT).show();
			
			if (check) {
				PreferenceControl.setUID(text);
				PreferenceControl.setDeviceId(text2);
				
				PreferenceControl.setIsDeveloper(developer_switch.isChecked());
				PreferenceControl.setCollectData(collectdata_switch.isChecked());
				PreferenceControl.setGoal(target_g, target_t, drink_t);
				
				PreferenceControl.setVoltage1(v1);
				PreferenceControl.setVoltage2(v2);
				PreferenceControl.setAfterCountDown(aCount);
				PreferenceControl.setVoltageCountDown(vCount);
				
				PreferenceControl.setStartDate(mYear, mMonth, mDay);

				PreferenceControl.setLocked(lDateCheckBox.isChecked());
				if (lDateCheckBox.isChecked()) {
					PreferenceControl.setLockDate(lYear, lMonth, lDay);
				}
				activity.finish();
			}
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

	private class CleanListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			//PreferenceControl.exchangeCoupon();
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
