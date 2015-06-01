package com.ubicomp.ketdiary.db;

import java.util.Calendar;

import android.app.AlarmManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.check.StartDateCheck;
import com.ubicomp.ketdiary.check.WeekNumCheck;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.data.structure.TimeValue;

/**
 * This class is used for controlling database on the mobile phone side
 * 
 * @author Andy Chen
 */

public class DatabaseControl {
	/**
	 * SQLiteOpenHelper
	 * 
	 * @see ubicomp.soberdiary.data.database.DBHelper
	 */
	private SQLiteOpenHelper dbHelper = null;
	/** SQLLiteDatabase */
	private SQLiteDatabase db = null;
	/** Lock for preventing congestion */
	private static final Object sqlLock = new Object();

	/** Constructor of DatabaseControl */
	public DatabaseControl() {
		dbHelper = new DBHelper(App.getContext());
	}
	
	// TestResult

	/**
	 * This method is used for getting all prime brac Detection
	 * 
	 * @return An array of Detection. If there are no detections, return null
	 * @see ubicomp.soberdiary.data.structure.Detection
	 */
	
	public TestResult[] getAllPrimeTestResult() {
		synchronized (sqlLock) {
			db = dbHelper.getReadableDatabase();
			String sql = "SELECT * FROM TestResult WHERE isPrime = 1 ORDER BY ts ASC";
			Cursor cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count == 0) {
				cursor.close();
				db.close();
				return null;
			}

			TestResult[] testResult = new TestResult[count];
			for (int i = 0; i < count; ++i) {
				cursor.moveToPosition(i);
				int result = cursor.getInt(1);
				String cassetteId = cursor.getString(2);
				long ts = cursor.getLong(6);
				int isPrime = cursor.getInt(8);
				int isFilled= cursor.getInt(9);
				int weeklyScore = cursor.getInt(10);
				int score = cursor.getInt(11);
				testResult[i] = new TestResult(result, ts, cassetteId, isPrime, isFilled, weeklyScore, score);
			}

			cursor.close();
			db.close();
			return testResult;
		}
	}

	/**
	 * This method is used for the latest result detection
	 * 
	 * @return Detection. If there are no Detection, return a dummy data.
	 * @see ubicomp.soberdiary.data.structure.Detection
	 *
	 */
	
	public TestResult getLatestTestResult() {
		synchronized (sqlLock) {
			db = dbHelper.getReadableDatabase();
			String sql = "SELECT * FROM TestResult ORDER BY ts DESC LIMIT 1";
			Cursor cursor = db.rawQuery(sql, null);
			if (!cursor.moveToFirst()) {
				cursor.close();
				db.close();
				return new TestResult(0, 0, "ket_default", 0, 0, 0, 0);
			}

			int result = cursor.getInt(1);
			String cassetteId = cursor.getString(2);
			long ts = cursor.getLong(6);
			int isPrime = cursor.getInt(8);
			int isFilled= cursor.getInt(9);
			int weeklyScore = cursor.getInt(10);
			int score = cursor.getInt(11);
			TestResult testResult = new TestResult(result, ts, cassetteId, isPrime, isFilled, weeklyScore, score);

			cursor.close();
			db.close();
			return testResult;
		}
	}

	/**
	 * This method is used for inserting a result detection
	 * 
	 * @return # of credits got by the user
	 * @param data
	 *            Inserted TestResult
	 * @param update
	 *            If update = true, the previous prime detection will be
	 *            replaced by current Detection
	 * @see ubicomp.soberdiary.data.structure.Detection
	 */
	
	public int insertTestResult(TestResult data, boolean update) {
		synchronized (sqlLock) {

			TestResult prev_data = getLatestTestResult();
			int weeklyScore = prev_data.getWeeklyScore();
			if (prev_data.getTv().getWeek() < data.getTv().getWeek())
				weeklyScore = 0;
			int score = prev_data.getScore();
			db = dbHelper.getWritableDatabase();
			if (!update) {
				boolean isPrime = !(data.isSameTimeBlock(prev_data));
				int isPrimeValue = isPrime ? 1 : 0;
				int addScore = 0;
				addScore += isPrimeValue;
				addScore += isPrime && data.getResult()== 0 ? 1 : 0;
				if (!StartDateCheck.afterStartDate())
					addScore = 0;

				ContentValues content = new ContentValues();
				content.put("result", data.getResult());
				content.put("year", data.getTv().getYear());
				content.put("month", data.getTv().getMonth());
				content.put("day", data.getTv().getDay());
				content.put("ts", data.getTv().getTimestamp());
				content.put("week", data.getTv().getWeek());
				content.put("isPrime", isPrimeValue);
				content.put("isFilled", data.getIsFilled());
				content.put("weeklyScore", weeklyScore + addScore);
				content.put("score", score + addScore);
				db.insert("TestResult", null, content);
				db.close();
				return addScore;
			} else {  //把之前的isPrime設成0
				int addScore = data.getResult()== 0 ? 1 : 0;
				if (!StartDateCheck.afterStartDate())
					addScore = 0;
				String sql = "UPDATE TestResult SET isPrime = 0 WHERE ts ="
						+ prev_data.getTv().getTimestamp();
				db.execSQL(sql);
				ContentValues content = new ContentValues();
				content.put("result", data.getResult());
				content.put("year", data.getTv().getYear());
				content.put("month", data.getTv().getMonth());
				content.put("day", data.getTv().getDay());
				content.put("ts", data.getTv().getTimestamp());
				content.put("week", data.getTv().getWeek());
				content.put("isPrime", 1);
				content.put("isFilled", data.getIsFilled());
				content.put("weeklyScore", weeklyScore + addScore);
				content.put("score", score + addScore);
				db.insert("TestResult", null, content);
				db.close();
				return addScore;
			}
		}
	}

	/**
	 * This method is used for getting result of today's prime detections
	 * 
	 * @return result 
	 */
	public int getTodayPrimeResult() {
		synchronized (sqlLock) {
			int result;
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DATE);

			db = dbHelper.getReadableDatabase();

			String sql = "SELECT result FROM TestResult WHERE year = "
					+ year + " AND month = " + month + " AND day = " + day
					+ " AND isPrime = 1" + " ORDER BY timeSlot ASC";
			Cursor cursor = db.rawQuery(sql, null);

			int count = cursor.getCount();
			result = cursor.getInt(1);

			cursor.close();
			db.close();
			return result;
		}
	}

	/**
	 * This method is used for getting result of previous n-day prime
	 * detections
	 * 
	 * @return An array of float (length = n_days*3) [idx], idx%3=0:morning,
	 *         idx%3=1:afternoon, idx%[3]=2:night
	 */
	public int[] getMultiDaysPrimeBrac(int n_days) {
		synchronized (sqlLock) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			final long DAY = AlarmManager.INTERVAL_DAY;
			long ts_days = (long) (n_days - 1) * DAY;
			long start_ts = cal.getTimeInMillis() - ts_days;

			String sql = "SELECT result,ts FROM TestResult WHERE ts >="
					+ start_ts + " AND isPrime = 1" + " ORDER BY ts ASC";
			db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, null);

			int[] result = new int[n_days];
			long ts_from = start_ts;
			long ts_to = start_ts + DAY;
			int pointer = 0;
			int count = cursor.getCount();

			for (int i = 0; i < result.length; ++i) {
				int _result;
				long _ts;
				while (pointer < count) {
					cursor.moveToPosition(pointer);
					_result = cursor.getInt(0);
					_ts = cursor.getLong(1);
					if (_ts < ts_from) {
						++pointer;
						continue;
					} else if (_ts >= ts_to) {
						break;
					}
					result[i] = _result;
					break;
				}
				ts_from += DAY;
				ts_to += DAY;

			}
			cursor.close();
			db.close();
			return result;
		}
	}

	/**
	 * This method is used for labeling which TestResult is uploaded to the
	 * server
	 * 
	 * @param ts
	 *            timestamp of the detection
	 */
	public void setTestResultUploaded(long ts) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "UPDATE TestResult SET upload = 1 WHERE ts=" + ts;
			db.execSQL(sql);
			db.close();
		}
	}

	/**
	 * This method is used for getting weekly scores of current week's
	 * TestResult
	 * 
	 * @return An array of weekly score. Length=# weeks
	 */
	public Integer[] getTestResultScoreByWeek() {
		synchronized (sqlLock) {
			db = dbHelper.getReadableDatabase();
			int curWeek = WeekNumCheck.getWeek(Calendar.getInstance()
					.getTimeInMillis());
			Integer[] scores = new Integer[curWeek + 1];

			String sql = "SELECT weeklyScore, week FROM TestResult WHERE week<="
					+ curWeek + " GROUP BY week";

			Cursor cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			int pointer = 0;
			int week = 0;
			for (int i = 0; i < scores.length; ++i) {
				while (pointer < count) {
					cursor.moveToPosition(pointer);
					week = cursor.getInt(1);
					if (week < i) {
						++pointer;
						continue;
					} else if (week > i)
						break;
					int weeklyScore = cursor.getInt(0);
					scores[i] = weeklyScore;
					break;
				}
			}
			for (int i = 0; i < scores.length; ++i)
				if (scores[i] == null)
					scores[i] = 0;

			cursor.close();
			db.close();
			return scores;
		}
	}

	/**
	 * This method is used for getting TestResult which are not uploaded to the
	 * server
	 * 
	 * @return An array of TestResult. If there are no TestResult, return null.
	 * @see ubicomp.soberdiary.data.structure.TestResult
	 */
	
	/*
	public TestResult[] getAllNotUploadedTestResult() {
		synchronized (sqlLock) {
			db = dbHelper.getReadableDatabase();
			long cur_ts = System.currentTimeMillis();
			String sql;

			sql = "SELECT * FROM Detection WHERE upload = 0  ORDER BY ts ASC";

			Cursor cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count == 0) {
				cursor.close();
				db.close();
				return null;
			}

			Detection[] detections = new Detection[count];
			for (int i = 0; i < count; ++i) {
				cursor.moveToPosition(i);
				float brac = cursor.getFloat(1);
				long ts = cursor.getLong(5);
				int emotion = cursor.getInt(8);
				int craving = cursor.getInt(9);
				boolean isPrime = cursor.getInt(10) == 1;
				int weeklyScore = cursor.getInt(11);
				int score = cursor.getInt(12);
				detections[i] = new Detection(brac, ts, emotion, craving,
						isPrime, weeklyScore, score);
			}
			cursor.close();
			db.close();
			return detections;
		}
	}*/

	/**
	 * This method is used for checking if the user do the brac detection at
	 * this time slot
	 * 
	 * @return true if the user do a brac detection at the current time slot
	 */
	/*
	public boolean detectionIsDone() {
		synchronized (sqlLock) {
			db = dbHelper.getReadableDatabase();
			long ts = System.currentTimeMillis();
			TimeValue tv = TimeValue.generate(ts);
			String sql = "SELECT id FROM Detection WHERE" + " year ="
					+ tv.getYear() + " AND month = " + tv.getMonth()
					+ " AND day= " + tv.getDay() + " AND timeSlot= "
					+ tv.getTimeslot();
			Cursor cursor = db.rawQuery(sql, null);
			boolean result = cursor.getCount() > 0;
			cursor.close();
			db.close();
			return result;
		}
	}*/

	/**
	 * This method is used for counting total passed prime detections
	 * 
	 * @return # of passed prime detections
	 */
	/*
	public int getPrimeDetectionPassTimes() {
		synchronized (sqlLock) {
			db = dbHelper.getReadableDatabase();
			String sql = "SELECT * FROM Detection WHERE isPrime = 1 AND brac < "
					+ Detection.BRAC_THRESHOLD;
			Cursor cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			cursor.close();
			db.close();
			return count;
		}
	}*/

	/**
	 * This method is used for checking if the user can replace the current
	 * detection
	 * 
	 * @return true if the user is allowed to replace the detection
	 */
	public boolean canTryAgain() {
		synchronized (sqlLock) {
			TimeValue curTV = TimeValue.generate(System.currentTimeMillis());
			int year = curTV.getYear();
			int month = curTV.getMonth();
			int day = curTV.getDay();
			int timeslot = curTV.getTimeslot();
			db = dbHelper.getReadableDatabase();
			String sql = "SELECT * FROM DETECTION WHERE year=" + year
					+ " AND month=" + month + " AND day=" + day
					+ " AND timeSlot=" + timeslot;
			Cursor cursor = db.rawQuery(sql, null);
			return (cursor.getCount() == 1);
		}
	}
	
	
	
	


}
