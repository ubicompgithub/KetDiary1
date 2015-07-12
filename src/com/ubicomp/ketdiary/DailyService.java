package com.ubicomp.ketdiary;

import java.util.Calendar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.db.DatabaseControl;
import com.ubicomp.ketdiary.system.PreferenceControl;

/**
 * Service for daily do something
 * 
 * @author Andy Chen
 */
public class DailyService extends Service {

	/**
	 * Start the uploading service
	 * 
	 * @param context
	 *            Application context
	 */
	public static void startUploadService(Context context) {
		Intent intent = new Intent(context, DailyService.class);
		context.startService(intent);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		DatabaseControl db = new DatabaseControl();
		Calendar c  = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		int Year = c.get(Calendar.YEAR);
		int Month = c.get(Calendar.MONTH);
		int Date = c.get(Calendar.DAY_OF_MONTH);
		TestResult testResult = db.getDayTestResult(Year, Month, Date);
		if(testResult.tv.getTimestamp() == 0){
			PreferenceControl.setPosition(-1);
		}
		
		return Service.START_REDELIVER_INTENT;
	}

}