package com.ubicomp.ketdiary.data.structure;

import java.util.Calendar;

import android.app.AlarmManager;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.system.check.WeekNumCheck;

public class TimeValue {
	private int year;
	private int month;
	private int day;
	private int hour;
	private int timeslot;
	private int day_of_week;
	private int week;
	private long timestamp;
	public static final int TIME_MORNING = 0;
	public static final int TIME_NOON = 1;
	public static final int TIME_NIGHT = 2;

	public static TimeValue generate(long timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
		int week = WeekNumCheck.getWeek(timestamp);
		int timeslot;
		if (hour < 12)
			timeslot = TimeValue.TIME_MORNING;
		else if (hour < 20)
			timeslot = TimeValue.TIME_NOON;
		else
			timeslot = TimeValue.TIME_NIGHT;

		return new TimeValue(year, month, day, hour, day_of_week, timeslot, timestamp, week);
	}

	protected TimeValue(int year, int month, int day, int hour, int day_of_week, int timeslot,
			long timestamp, int week) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.day_of_week = day_of_week;
		this.timeslot = timeslot;
		this.timestamp = timestamp;
		this.week = week;
	}

	public String toString() {
		return year + "/" + (month + 1) + "/" + day;
	}

	public String toSimpleDateString() {
		if (isToday())
			return App.getContext().getString(0);// R.string.today);
		return (month + 1) + "/" + day;
	}

	public boolean isSameTimeBlock(TimeValue tv) {
		return tv != null && this.year == tv.year && this.month == tv.month
				&& this.day == tv.day && this.timeslot == tv.timeslot;
	}

	public boolean isSameDay(TimeValue tv) {
		return tv != null && this.year == tv.year && this.month == tv.month
				&& this.day == tv.day;
	}

	public int beforeAfter(int year, int month, int day) {
		if (this.year != year)
			return year - this.year;
		if (this.month != month)
			return month - this.month;
		return day - this.day;
	}

	public String toFileString() {
		return year + "_" + (month + 1) + "_" + day;
	}

	public int toClickValue() {
		return (month + 1) * 100 + day;
	}

	public String toDetailString() {
		return year + "/" + (month + 1) + "/" + day + ":" + timeslot
				+ " @week=" + week + " ts = " + timestamp;
	}

	public boolean afterADay(TimeValue tv) {
		long gap = this.timestamp - tv.timestamp;
		return gap > AlarmManager.INTERVAL_DAY;
	}

	public boolean afterAWeek(TimeValue tv) {
		long gap = this.timestamp - tv.timestamp;
		return gap > AlarmManager.INTERVAL_DAY * 7;
	}

	public boolean isToday() {
		Calendar cal = Calendar.getInstance();
		int cYear = cal.get(Calendar.YEAR);
		int cMonth = cal.get(Calendar.MONTH);
		int cDay = cal.get(Calendar.DAY_OF_MONTH);

		return cYear == year && cMonth == month && cDay == day;
	}

	public boolean showNotificationDialog(long curTime, boolean long_time) {
		long gap = curTime - timestamp;
		if (long_time)
			return gap > AlarmManager.INTERVAL_DAY * 7;
		return gap > AlarmManager.INTERVAL_DAY * 3;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}
	
	public int getDayOfWeek(){
		return day_of_week;
	}

	public int getHour() {
		return hour;
	}

	public int getTimeslot() {
		return timeslot;
	}

	public int getWeek() {
		return week;
	}

	public long getTimestamp() {
		return timestamp;
	}

}
