package com.ubicomp.ketdiary;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ubicomp.ketdiary.system.Config;
import com.ubicomp.ketdiary.system.PreferenceControl;

/**
 * BoardcastReceiver receive boot ACTION_TIME_CHANGED ACTION_TIMEZONE_CHANGED
 * BOOT_COMPLETED
 * 
 * @author Stanley Wang
 */
public class BootBoardcastReceiver extends BroadcastReceiver {

	/** RequestCode for regular notification */
	private static final int requestCodeRegularNotification = 0x2013;
	/** RequestCode for regular Internet check */
	private static final int requestCodeRegularCheck = 0x2014;
	/** RequestCode for daily event */
	private static final int requestCodeDailyEvent = 0x2015;

	/** TAG for logcat */
	private static final String TAG = "BOOT_BC_RECEIVER";

	@Override
	/**Receive the messages from the Android system*/
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();

		Log.d(TAG, TAG + " - " + action);

		if (action.equals(Intent.ACTION_TIME_CHANGED)
				|| action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {// ||
																	// action.equals(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE)){
			PreferenceControl.timeReset();
		}

		setRegularNotification(context, intent);
		setRegularCheck(context, intent);		
		setDailyEvent(context, intent);
	}

	/** Method for setting regular notification alarm */
	public static void setRegularNotification(Context context, Intent intent) {

		Log.d(TAG, "setRegularNotification");
		int notification_minutes = PreferenceControl.getNotificationTime();
		long notification_gap = notification_minutes * 60 * 1000;

		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Intent service_intent = new Intent();
		service_intent.setClass(context, AlarmReceiver.class);
		service_intent.setAction(Config.ACTION_REGULAR_NOTIFICATION);

		Calendar c = Calendar.getInstance();

		int cur_year = c.get(Calendar.YEAR);
		int cur_month = c.get(Calendar.MONTH);
		int cur_date = c.get(Calendar.DAY_OF_MONTH);
		int cur_hour = c.get(Calendar.HOUR_OF_DAY);
		int cur_min = c.get(Calendar.MINUTE);

		if (notification_minutes == 120) {
			if (cur_min < 30) {
				if (cur_hour % 2 == 0) {
					c.set(cur_year, cur_month, cur_date, cur_hour, 30, 0);
				} else {
					c.set(cur_year, cur_month, cur_date, cur_hour, 30, 0);
					c.add(Calendar.HOUR_OF_DAY, 1);
				}
			} else {
				if (cur_hour % 2 == 0) {
					c.set(cur_year, cur_month, cur_date, cur_hour, 30, 0);
					c.add(Calendar.HOUR_OF_DAY, 2);
				} else {
					c.set(cur_year, cur_month, cur_date, cur_hour, 30, 0);
					c.add(Calendar.HOUR_OF_DAY, 1);
				}
			}
		} else if (notification_minutes == 60) {
			if (cur_min < 30) {
				c.set(cur_year, cur_month, cur_date, cur_hour, 30, 0);
			} else {
				c.set(cur_year, cur_month, cur_date, cur_hour, 30, 0);
				c.add(Calendar.HOUR_OF_DAY, 1);
			}
		} else if (notification_minutes == 30) {
			if (cur_min < 30) {
				c.set(cur_year, cur_month, cur_date, cur_hour, 30, 0);
			} else {
				c.set(cur_year, cur_month, cur_date, cur_hour, 0, 0);
				c.add(Calendar.HOUR_OF_DAY, 1);
			}
		} else if (notification_minutes < 0) {// cancel
			// do nothing on c
		} else {
			// do not change c
		}

		PendingIntent pending = PendingIntent.getBroadcast(context,
				requestCodeRegularNotification, service_intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(pending);
		if (notification_minutes > 0)
			alarm.setRepeating(AlarmManager.RTC_WAKEUP,
					c.getTimeInMillis() + 10, notification_gap, pending);
	}

	/** Method for setting regular Internet check alarm */
	public static void setRegularCheck(Context context, Intent intent) {
		Log.d(TAG, "setRegularCheck");
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Intent check_intent = new Intent();
		check_intent.setClass(context, AlarmReceiver.class);
		check_intent.setAction(Config.ACTION_REGULAR_CHECK);

		PendingIntent pending2 = PendingIntent.getBroadcast(context,
				requestCodeRegularCheck, check_intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		alarm.cancel(pending2);
		alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 10000,
				AlarmManager.INTERVAL_HALF_HOUR, pending2);

		Intent regularCheckIntent = new Intent(context, UploadService.class);
		context.startService(regularCheckIntent);
	}
	
	/** Method for setting daily event alarm */
	public static void setDailyEvent(Context context, Intent intent) {
		Log.d(TAG, "setDailyEvent");
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 1);
		
		Intent check_intent = new Intent();
		check_intent.setClass(context, AlarmReceiver.class);
		check_intent.setAction(Config.ACTION_DAILY_EVENT);

		PendingIntent pending2 = PendingIntent.getBroadcast(context,
				requestCodeDailyEvent, check_intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		alarm.cancel(pending2);
		alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				c.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, pending2);

		//alarm.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pending2);
		
	}
}
