package com.ubicomp.ketdiary.data.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Scanner;

import android.content.Context;
import android.util.Log;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.R;
import com.ubicomp.ketdiary.data.db.DatabaseControl;
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.TestDetail;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.ui.CustomToast;

/**
 * Handle the BrAC detection data
 * 
 * @author Stanley Wang
 */
public class TestDataParser2 {

	private static final String TAG = "TEST_DATA_PARSER";

	protected long ts;
	protected Context context;
	protected double sensorResult = 0;
	protected DatabaseControl db;
	//protected DBControl db;
	
	public NoteAdd noteAdd = null;
	
	public static final int NOTHING = 0;
	public static final int ERROR = -1;
	public static final int SUCCESS = 1;

	/**
	 * Constructor
	 * 
	 * @param timestamp
	 *            timestamp of the detection
	 */
	public TestDataParser2(long timestamp) {
		this.ts = timestamp;
		this.context = App.getContext();
		db = new DatabaseControl();
		//db = new DBControl();
	}
	
	/** start to handle the noteAdd data */
	public void startAddNote2(int isAfterTest, int day, int timeslot, int type, int items, int impact, String descripiton) {

		Log.i(TAG, "TDP AddNote2 Start");
		Calendar cal = Calendar.getInstance();
		//cal.setTimeInMillis(0);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int days = cal.get(Calendar.DAY_OF_MONTH);
		
		int date = days - day;
		int category;
		if(type <=5 && type > 0)
			category=0;
		else if(type > 5)
			category=1;
		else
			category=-1;
		
		if(category == -1){
			items = -1;
			impact = -1;
			
			return;
		}

		if(ts == 0)
			ts = System.currentTimeMillis();
		
		noteAdd = new NoteAdd(isAfterTest, ts, year, month, date, timeslot, category, type, items, impact, descripiton, 0, 0);


		
		int addScore  = db.insertNoteAdd(noteAdd);
		CustomToast.generateToast(R.string.note_done, addScore);
		if (PreferenceControl.checkCouponChange())
			PreferenceControl.setCouponChange(true);
		
		PreferenceControl.setPoint(addScore);

	}
	
	/** start to handle the noteAdd data */
	public static void startAfterAddNote3(int isAfterTest, int day, int timeslot, int type, int items, int impact, String descripiton) {

		Log.i(TAG, "TDP AddNote3 Start");
		Calendar cal = Calendar.getInstance();
		//cal.setTimeInMillis(0);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int days = cal.get(Calendar.DAY_OF_MONTH);
		
		int date = days - day;
		int category;
		if(type <=5 && type > 0)
			category=0;
		else if(type > 5)
			category=1;
		else
			category=-1;
		
		if(category == -1){
			items = -1;
			impact = -1;			
			return;
		}

//		long timestamp = ts;
//		if(ts == 0)
//			ts = System.currentTimeMillis();
		
		long ts = PreferenceControl.getUpdateDetectionTimestamp();
		
		NoteAdd noteAdd = new NoteAdd(isAfterTest, ts, year, month, date, timeslot, category, type, items, impact, descripiton, 0, 0);
		boolean update = false;
		if (ts == PreferenceControl.getUpdateDetectionTimestamp())
			update = true;
		PreferenceControl.setUpdateDetection(update);
		//PreferenceControl.setUpdateDetectionTimestamp(0);
		
		DatabaseControl db = new DatabaseControl();
		db.insertNoteAdd(noteAdd);
		
		//db.addTestResult(testResult);
		//DBControl.inst.addNoteAdd(noteAdd);
	}
	
	public void startTestDetail(String cassetteId, int failedState,int firstVoltage,
			int secondVoltage, int devicePower, int colorReading,
            float connectionFailRate, String failedReason, String hardwareVersion) {
		
		TestDetail testDetail = new TestDetail(cassetteId, ts, failedState, firstVoltage,
				secondVoltage, devicePower, colorReading,
                connectionFailRate, failedReason, hardwareVersion);
		
		db.insertTestDetail(testDetail);

	}
	
	/**
	 * get detection result
	 * 
	 * @return BrAC value reading from the sensor
	 */
	public double getResult() {
		return sensorResult;
	}

	
}
