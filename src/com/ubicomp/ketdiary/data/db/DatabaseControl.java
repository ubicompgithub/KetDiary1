package com.ubicomp.ketdiary.data.db;

import java.util.Calendar;

import android.app.AlarmManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.data.structure.Cassette;
import com.ubicomp.ketdiary.data.structure.CopingSkill;
import com.ubicomp.ketdiary.data.structure.ExchangeHistory;
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.QuestionTest;
import com.ubicomp.ketdiary.data.structure.Rank;
import com.ubicomp.ketdiary.data.structure.TestDetail;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.data.structure.TimeValue;
import com.ubicomp.ketdiary.system.PreferenceControl;
import com.ubicomp.ketdiary.system.check.StartDateCheck;
import com.ubicomp.ketdiary.system.check.WeekNumCheck;


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
	private final static String TAG = "DatabaseControl";
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
	 * @return TestResult. If there are no TestResult, return a dummy data.
	 * @see ubicomp.soberdiary.data.structure.TestResult
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
	
	public TestResult getLatestTestResultID() {
		synchronized (sqlLock) {
			db = dbHelper.getReadableDatabase();
			String sql = "SELECT * FROM TestResult ORDER BY id DESC LIMIT 1";
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
			TestResult prevID_data = getLatestTestResultID();
			int weeklyScore = prevID_data.getWeeklyScore();
			if (prevID_data.getTv().getWeek() < data.getTv().getWeek())
				weeklyScore = prev_data.getWeeklyScore();
			if (prev_data.getTv().getWeek() < data.getTv().getWeek())
				weeklyScore = 0;
			int score = prevID_data.getScore();
			db = dbHelper.getWritableDatabase();
			if (!update) {
				boolean isPrime = !(data.isSameDay(prev_data) || data.isSameDay(prevID_data));
				// add by Andy
				int result = data.getResult();
//				if(isPrime){
//					if(result == 0)
//						PreferenceControl.setPosition(1);
//					else
//						PreferenceControl.setPosition(-1);
//				}
				//
				int isPrimeValue = isPrime ? 1 : 0;
				int addScore = 0;
				addScore += isPrimeValue;
				addScore += isPrime && data.getResult()== 0 ? 1 : 0;
				if (!StartDateCheck.afterStartDate())
					addScore = 0;

				ContentValues content = new ContentValues();
				content.put("result", data.getResult());
				content.put("cassetteId", data.getCassette_id());
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
				content.put("cassetteId", data.getCassette_id());
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
	
	public TestResult getDayTestResult(int Year, int Month, int Day) {
		synchronized (sqlLock) {

			db = dbHelper.getReadableDatabase();
			String sql;
			Cursor cursor;

			sql = "SELECT * FROM TestResult WHERE year = " + Year
					+ " AND month = " + Month + " AND day = "
					+ Day +" AND isPrime = 1";
			cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			if (!cursor.moveToFirst()) {
				cursor.close();
				db.close();
				return new TestResult(-1, 0, "ket_default", 0, 0, 0, 0);
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
					+ " AND isPrime = 1" + " ORDER BY ASC";
			Cursor cursor = db.rawQuery(sql, null);

			int count = cursor.getCount();
			result = cursor.getInt(1);

			cursor.close();
			db.close();
			return result;
		}
	}
	
	/**
	 * This method is used for getting result of today's prime detections
	 * 
	 * @return count 
	 */
	public int getTodayTestCount() {
		synchronized (sqlLock) {
			//int result;
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DATE);

			db = dbHelper.getReadableDatabase();

			String sql = "SELECT result FROM TestResult WHERE year = "
					+ year + " AND month = " + month + " AND day = " + day;
			Cursor cursor = db.rawQuery(sql, null);

			int count = cursor.getCount();
			//result = cursor.getInt(1);

			cursor.close();
			db.close();
			return count;
		}
	}
	
	public int getDayTestCount() {
		synchronized (sqlLock) {
			//int result;
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DATE);

			db = dbHelper.getReadableDatabase();

			String sql = "SELECT result FROM TestResult WHERE year = "
					+ year + " AND month = " + month + " AND day = " + day;
			Cursor cursor = db.rawQuery(sql, null);

			int count = cursor.getCount();
			//result = cursor.getInt(1);

			cursor.close();
			db.close();
			return count;
		}
	}
	
	/**
	 * This method is used for getting result of today's prime detections
	 * 
	 * @return count 
	 */
	public int noTestDayCount(long prev_ts, long ts) {  //TODO:
		synchronized (sqlLock) {
			//int result;
			TimeValue prev_tv = TimeValue.generate(prev_ts);
			TimeValue tv = TimeValue.generate(ts);
			
			int noTestDay = 0;
			int passDay = 0;
			final long DAY = AlarmManager.INTERVAL_DAY;
			
			if(tv.isSameDay(prev_tv) || prev_ts >= ts){
				Log.i(TAG, "Day: " + 0 );
				return 0;
			}
			
			db = dbHelper.getReadableDatabase();
			
			while(!tv.isSameDay(prev_tv) && prev_ts < ts){
				
				int year = prev_tv.getYear();
				int month = prev_tv.getMonth();
				int day = prev_tv.getDay();
				Log.i(TAG, "Day: " + day );
				
				String sql = "SELECT result FROM TestResult WHERE year = "
					+ year + " AND month = " + month + " AND day = " + day;
				Cursor cursor = db.rawQuery(sql, null);
				int count = cursor.getCount();
				if(count == 0){
					noTestDay++;
				}
				cursor.close();
				prev_ts = prev_ts + DAY;
				prev_tv = TimeValue.generate(prev_ts);
				passDay++;
			}
			Log.i(TAG, "PassDay: " + passDay + " NoTestDay " + noTestDay);
			if(noTestDay > 0 && StartDateCheck.afterStartDate()){			
				PreferenceControl.setCheckBars(true);
				PreferenceControl.setPosition(-noTestDay);
			}
			PreferenceControl.setOpenAppTimestamp();
			db.close();
			return noTestDay;
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
				int _result = -1;
				long _ts;
				result[i] = _result;
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
	 * This method is used for getting result of previous n-day prime
	 * detections
	 * 
	 * @return 
	 */
	public int[] getWeeklyPrimeBrac() {
		synchronized (sqlLock) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			
			int FIRST_DAY = Calendar.MONDAY;
			int day=1;
			while (cal.get(Calendar.DAY_OF_WEEK) != FIRST_DAY) {
				cal.add(Calendar.DATE, -1);
				day++;
	        }
			final long DAY = AlarmManager.INTERVAL_DAY;
			long start_ts = cal.getTimeInMillis();

			String sql = "SELECT result,ts FROM TestResult WHERE ts >="
					+ start_ts + " AND isPrime = 1" + " ORDER BY ts ASC";
			db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, null);

			int[] result = new int[day];
			long ts_from = start_ts;
			long ts_to = start_ts + DAY;
			int pointer = 0;
			int count = cursor.getCount();

			for (int i = 0; i < result.length; ++i) {
				int _result = -1;
				long _ts;
				result[i] = _result;
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
	
	public void modifyResultByTs(long ts, int result) {
		synchronized (sqlLock) {

			db = dbHelper.getReadableDatabase();
			String sql;
			//sql = "SELECT * FROM TestResult WHERE ts = " + ts + " AND isPrime = 1";
			sql = "UPDATE TestResult SET result = " + result + ", upload = 0 WHERE ts=" + ts;
			db.execSQL(sql);
			db.close();

			return ;
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
	
	
	public TestResult[] getAllNotUploadedTestResult() {
		synchronized (sqlLock) {
			db = dbHelper.getReadableDatabase();
			long cur_ts = System.currentTimeMillis();
			String sql;

			sql = "SELECT * FROM TestResult WHERE upload = 0  ORDER BY ts ASC";

			Cursor cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count == 0) {
				cursor.close();
				db.close();
				return null;
			}

			TestResult[] testResults = new TestResult[count];
			for (int i = 0; i < count; ++i) {
				cursor.moveToPosition(i);
				int result = cursor.getInt(1);
				String cassetteId = cursor.getString(2);
				long ts = cursor.getLong(6);
				int isPrime = cursor.getInt(8);
				int isFilled= cursor.getInt(9);
				int weeklyScore = cursor.getInt(10);
				int score = cursor.getInt(11);
				testResults[i] = new TestResult(result, ts, cassetteId, isPrime, isFilled, weeklyScore, score);
			}
			cursor.close();
			db.close();
			return testResults;
		}
	}

	/**
	 * This method is used for checking if the user do the brac detection at
	 * this time slot
	 * 
	 * @return true if the user do a brac detection at the current time slot
	 */
	
	public boolean detectionIsDone() {
		synchronized (sqlLock) {
			db = dbHelper.getReadableDatabase();
			long ts = System.currentTimeMillis();
			TimeValue tv = TimeValue.generate(ts);
			String sql = "SELECT id FROM TestResult WHERE" + " year ="
					+ tv.getYear() + " AND month = " + tv.getMonth()
					+ " AND day= " + tv.getDay();
			Cursor cursor = db.rawQuery(sql, null);
			boolean result = cursor.getCount() > 0;
			cursor.close();
			db.close();
			return result;
		}
	}

	/**
	 * This method is used for counting total passed prime detections
	 * 
	 * @return # of passed prime detections
	 */
	
	public int getPrimeTestPassTimes() {
		synchronized (sqlLock) {
			db = dbHelper.getReadableDatabase();
			String sql = "SELECT * FROM TestResult WHERE isPrime = 1 AND result = 0";
			Cursor cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			cursor.close();
			db.close();
			return count;
		}
	}

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
			//int timeslot = curTV.getTimeslot();
			db = dbHelper.getReadableDatabase();
			String sql = "SELECT * FROM TestResult WHERE year=" + year
					+ " AND month=" + month + " AND day=" + day;
			Cursor cursor = db.rawQuery(sql, null);
			return (cursor.getCount() == 1);
		}
	}
	
	
	
	// **** NoteAdd ****

	/**
	 * Get the latest NoteAdd
	 * 
	 * @return NoteAdd. If there are no EmotionManagement, return a
	 *         dummy data.
	 * @see ubicomp.soberdiary.data.structure.EmotionManagement
	 */
	
	public NoteAdd getLatestNoteAdd() {
		synchronized (sqlLock) {
			db = dbHelper.getReadableDatabase();
			String sql;
			Cursor cursor;
			sql = "SELECT * FROM NoteAdd ORDER BY ts DESC LIMIT 1";
			cursor = db.rawQuery(sql, null);
			if (!cursor.moveToFirst()) {
				cursor.close();
				db.close();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(0);
				int year = cal.get(Calendar.YEAR);
				int month = cal.get(Calendar.MONTH);
				int day = cal.get(Calendar.DAY_OF_MONTH);
				
				return new NoteAdd(0, 0, year, month, day,0 , 0, 0, 0, 0, null, 0, 0);
			}
			int isAfterTest = cursor.getInt(1);
			long ts = cursor.getLong(5);
			int year = cursor.getInt(7);
			int month = cursor.getInt(8);
			int day = cursor.getInt(9);
			int timeslot = cursor.getInt(10);
			int category = cursor.getInt(11);
			int type = cursor.getInt(12);
			int items = cursor.getInt(13);
			int impact = cursor.getInt(14);
			String reason = cursor.getString(15);
			int weeklyScore = cursor.getInt(16);
			int score = cursor.getInt(17);
			
			NoteAdd noteAdd = new NoteAdd(isAfterTest, ts, year, month, day, timeslot, 
					category, type, items, impact, reason, weeklyScore, score);
			
			cursor.close();
			db.close();
			return noteAdd;
			
		}
	}

	/**
	 * Insert an NoteAdd result
	 * 
	 * @return # of credits got by the user
	 * @see ubicomp.soberdiary.data.structure.EmotionManagement
	 */
	
	public int insertNoteAdd(NoteAdd data) {
		synchronized (sqlLock) {
			NoteAdd prev_data = getLatestNoteAdd();
			int addScore = 0;
			if (!prev_data.getTv().isSameTimeBlock(data.getTv()))
				addScore = 1;
			if (!StartDateCheck.afterStartDate())
				addScore = 0;

			db = dbHelper.getWritableDatabase();
			ContentValues content = new ContentValues();
			content.put("isAfterTest", data.getIsAfterTest());
			content.put("year", data.getTv().getYear());
			content.put("month", data.getTv().getMonth());
			content.put("day", data.getTv().getDay());
			content.put("ts", data.getTv().getTimestamp());
			content.put("week", data.getTv().getWeek());
			content.put("timeslot", data.getTimeSlot());
			content.put("recordYear", data.getRecordTv().getYear());
			content.put("recordMonth", data.getRecordTv().getMonth());
			content.put("recordDay", data.getRecordTv().getDay());
			content.put("category", data.getCategory());
			content.put("type", data.getType());
			content.put("items", data.getItems());
			content.put("impact", data.getImpact());
			content.put("description", data.getDescription());
			content.put("weeklyScore", prev_data.getWeeklyScore() + addScore);
			content.put("score", prev_data.getScore() + addScore);
			db.insert("NoteAdd", null, content);
			db.close();
			return addScore;
		}
	}

	/**
	 * Get all NoteAdd results which are not uploaded to the server
	 * 
	 * @return An array of NoteAdd results If there are no
	 *         NoteAdd, return null.
	 * @see ubicomp.soberdiary.data.structure.EmotionManagement
	 */
	
	public NoteAdd[] getNotUploadedNoteAdd() {
		synchronized (sqlLock) {
			NoteAdd[] data = null;

			db = dbHelper.getReadableDatabase();
			String sql;
			Cursor cursor;

			sql = "SELECT * FROM NoteAdd WHERE upload = 0";
			cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count == 0) {
				cursor.close();
				db.close();
				return null;
			}

			data = new NoteAdd[count];

			for (int i = 0; i < count; ++i) {
				cursor.moveToPosition(i);
				int isAfterTest = cursor.getInt(1);
				long ts = cursor.getLong(5);
				int year = cursor.getInt(7);
				int month = cursor.getInt(8);
				int day = cursor.getInt(9);
				int timeslot=cursor.getInt(10);
				int category = cursor.getInt(11);
				int type = cursor.getInt(12);
				int items = cursor.getInt(13);
				int impact = cursor.getInt(14);
				String reason = cursor.getString(15);
				int weeklyScore = cursor.getInt(16);
				int score = cursor.getInt(17);
				data[i] = new NoteAdd(isAfterTest, ts, year, month, day, timeslot, category, type, items, impact, reason, weeklyScore, score);
			}

			cursor.close();
			db.close();

			return data;
		}
	}
	
	/**
	 * Label the NoteAdd result uploaded
	 * 
	 * @param ts
	 *            Timestamp of the uploaded EmotionManagement
	 * @see ubicomp.soberdiary.data.structure.EmotionManagement
	 */
	
	public void setNoteAddUploaded(long ts) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "UPDATE NoteAdd SET upload = 1 WHERE ts = "
					+ ts;
			db.execSQL(sql);
			db.close();
		}
	}
	
	
	
	/**
	 * This method is used for getting all prime brac Detection
	 * 
	 * @return An array of Detection. If there are no detections, return null
	 * @see ubicomp.soberdiary.data.structure.Detection
	 */
	
	public NoteAdd[] getAllNoteAdd() {
		synchronized (sqlLock) {
			db = dbHelper.getReadableDatabase();
			String sql = "SELECT * FROM NoteAdd WHERE impact >= 0 AND items > 0 ORDER BY recordYear, recordMonth, recordDay, timeslot ASC"; // TODO: Just get useful data
			Cursor cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count == 0) {
				cursor.close();
				db.close();
				return null;
			}

			NoteAdd[] noteAdd = new NoteAdd[count];
			for (int i = 0; i < count; ++i) {
				cursor.moveToPosition(i);
				int isAfterTest = cursor.getInt(1);
				long ts = cursor.getLong(5);
				int year = cursor.getInt(7);
				int month = cursor.getInt(8);
				int day = cursor.getInt(9);
				int timeslot = cursor.getInt(10);
				int category = cursor.getInt(11);
				int type = cursor.getInt(12);
				int items = cursor.getInt(13);
				int impact = cursor.getInt(14);
				String reason = cursor.getString(15);
				int weeklyScore = cursor.getInt(16);
				int score = cursor.getInt(17);
				noteAdd[i] = new NoteAdd(isAfterTest, ts, year, month, day, 
						timeslot, category, type, items, impact, reason, weeklyScore, score);
			}

			cursor.close();
			db.close();
			return noteAdd;
		}
	}
	
	
	/**
	 * Get NoteAdd results by date
	 * 
	 * @param rYear
	 *            record Year
	 * @param rMonth
	 *            record Month (0~11)
	 * @param rDay
	 *            record Day of Month
	 * @return An array of EmotionManagement results @ rYear/rMonth/rDay. If
	 *         there are no EmotionManagement, return null.
	 * @see ubicomp.soberdiary.data.structure.EmotionManagement
	 */
	
	public NoteAdd[] getDayNoteAdd(int rYear, int rMonth, int rDay) {
		synchronized (sqlLock) {
			NoteAdd[] data = null;

			db = dbHelper.getReadableDatabase();
			String sql;
			Cursor cursor;

			sql = "SELECT * FROM NoteAdd WHERE recordYear = " + rYear
					+ " AND recordMonth = " + rMonth + " AND recordDay = "
					+ rDay +" AND type > -1 ORDER BY id DESC";
			cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count == 0) {
				cursor.close();
				db.close();
				return null;
			}

			data = new NoteAdd[count];

			for (int i = 0; i < count; ++i) {
				cursor.moveToPosition(i);
				int isAfterTest = cursor.getInt(1);
				long ts = cursor.getLong(5);
				int year = cursor.getInt(7);
				int month = cursor.getInt(8);
				int day = cursor.getInt(9);
				int timeslot = cursor.getInt(10);
				int category = cursor.getInt(11);
				int type = cursor.getInt(12);
				int items = cursor.getInt(13);
				int impact = cursor.getInt(14);
				String reason = cursor.getString(15);
				int weeklyScore = cursor.getInt(16);
				int score = cursor.getInt(17);
				data[i] = new NoteAdd(isAfterTest, ts, year, month, day, 
						timeslot, category, type, items, impact, reason, weeklyScore, score);
			}

			cursor.close();
			db.close();

			return data;
		}
	}
	
	
	public NoteAdd[] getDayNoteAddbyCategory(int rYear, int rMonth, int rDay, int category) {
		synchronized (sqlLock) {
			NoteAdd[] data = null;

			db = dbHelper.getReadableDatabase();
			String sql;
			Cursor cursor;

			sql = "SELECT * FROM NoteAdd WHERE recordYear = " + rYear
					+ " AND recordMonth = " + rMonth + " AND recordDay = "
					+ rDay +" AND category = " + category + " ORDER BY id DESC";
			cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count == 0) {
				cursor.close();
				db.close();
				return null;
			}

			data = new NoteAdd[count];

			for (int i = 0; i < count; ++i) {
				cursor.moveToPosition(i);
				int isAfterTest = cursor.getInt(1);
				long ts = cursor.getLong(5);
				int year = cursor.getInt(7);
				int month = cursor.getInt(8);
				int day = cursor.getInt(9);
				int timeslot = cursor.getInt(10);
				int category2 = cursor.getInt(11);
				int type = cursor.getInt(12);
				int items = cursor.getInt(13);
				int impact = cursor.getInt(14);
				String reason = cursor.getString(15);
				int weeklyScore = cursor.getInt(16);
				int score = cursor.getInt(17);
				data[i] = new NoteAdd(isAfterTest, ts, year, month, day, 
						timeslot, category2, type, items, impact, reason, weeklyScore, score);
			}

			cursor.close();
			db.close();

			return data;
		}
	}
	
	
	/**
	 * Get if there are EmotionManagement results at the date
	 * 
	 * @param tv
	 *            TimeValue of the date
	 * @return true if exists EmotionManagement
	 * @see ubicomp.soberdiary.data.structure.TimeValue
	 */
	
	public NoteAdd getTsNoteAdd(long ts) {
		synchronized (sqlLock) {
			NoteAdd data = null;
			
			db = dbHelper.getReadableDatabase();
			String sql;
			Cursor cursor;

			sql = "SELECT * FROM NoteAdd WHERE" + " ts ="
					+ ts  ;
			cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			if (!cursor.moveToFirst()) {
				cursor.close();
				db.close();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(0);
				int year = cal.get(Calendar.YEAR);
				int month = cal.get(Calendar.MONTH);
				int day = cal.get(Calendar.DAY_OF_MONTH);
				
				return new NoteAdd(0, 0, year, month, day, -1 , -1, -1, -1, -1, null, 0, 0);
			}
			int isAfterTest = cursor.getInt(1);
			//long ts = cursor.getLong(5);
			int year = cursor.getInt(7);
			int month = cursor.getInt(8);
			int day = cursor.getInt(9);
			int timeslot = cursor.getInt(10);
			int category = cursor.getInt(11);
			int type = cursor.getInt(12);
			int items = cursor.getInt(13);
			int impact = cursor.getInt(14);
			String reason = cursor.getString(15);
			int weeklyScore = cursor.getInt(16);
			int score = cursor.getInt(17);
			
			NoteAdd noteAdd = new NoteAdd(isAfterTest, ts, year, month, day, timeslot, 
					category, type, items, impact, reason, weeklyScore, score);
			
			cursor.close();
			db.close();
			return noteAdd;
		}
	}
	
	
	public NoteAdd[] getDayNoteAddPos(int rYear, int rMonth, int rDay) {
		synchronized (sqlLock) {
			NoteAdd[] data = null;

			db = dbHelper.getReadableDatabase();
			String sql;
			Cursor cursor;

			sql = "SELECT * FROM NoteAdd WHERE recordYear = " + rYear
					+ " AND recordMonth = " + rMonth + " AND recordDay = "
					+ rDay +" AND type > -1 ORDER BY id DESC";
			cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count == 0) {
				cursor.close();
				db.close();
				return null;
			}

			data = new NoteAdd[count];

			for (int i = 0; i < count; ++i) {
				cursor.moveToPosition(i);
				int isAfterTest = cursor.getInt(1);
				long ts = cursor.getLong(5);
				int year = cursor.getInt(7);
				int month = cursor.getInt(8);
				int day = cursor.getInt(9);
				int timeslot = cursor.getInt(10);
				int category = cursor.getInt(11);
				int type = cursor.getInt(12);
				int items = cursor.getInt(13);
				int impact = cursor.getInt(14);
				String reason = cursor.getString(15);
				int weeklyScore = cursor.getInt(16);
				int score = cursor.getInt(17);
				data[i] = new NoteAdd(isAfterTest, ts, year, month, day, 
						timeslot, category, type, items, impact, reason, weeklyScore, score);
			}

			cursor.close();
			db.close();

			return data;
		}
	}
	
	public int[] getNoteAddTypeRank(long start_ts, long end_ts) {
		synchronized (sqlLock) {
			NoteAdd[] data = null;
			int[] rank = new int[8];

			db = dbHelper.getReadableDatabase();
			String sql;
			Cursor cursor;
			
			
			for(int i=1; i<=8; i++){
				sql = "SELECT * FROM NoteAdd WHERE type= "+i+" ORDER BY id DESC";
				cursor = db.rawQuery(sql, null);
				int count = cursor.getCount();
				rank[i-1] = count;
			}
			
			int max=0;
			int max_index=-1;
			for(int i=0; i<8; i++){
				Log.d(TAG,"type "+(i+1)+"count "+rank[i]);
				if(rank[i] > max){
					max = rank[i];
					max_index = i;
				}	
			}
			int[] result = new int[4];
			result[0] = max_index;
			int j=0;
			for(int i=0; i<8; i++){
				if(j<3){
					if(rank[i] < max){
						result[++j] = i;
					}
				}
			}
			for(int i=0; i<result.length;i++){
				Log.d(TAG, "result:"+result[i]);
			}
			return result;
		}
	}

	/**
	 * Get the latest 4 reasons of EmotionManagement by reason type
	 * 
	 * @param type
	 *            reason type of EmotionManagement.
	 * @return An array of reasons. There are no reasons, return null
	 */
	/*
	public String[] getEmotionManagementString(int type) {
		synchronized (sqlLock) {
			db = dbHelper.getReadableDatabase();
			String sql = "SELECT DISTINCT reason FROM EmotionManagement WHERE type = "
					+ type + " ORDER BY ts DESC LIMIT 4";
			String[] out = null;

			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.getCount() == 0) {
				cursor.close();
				db.close();
				return null;
			}
			out = new String[cursor.getCount()];

			for (int i = 0; i < out.length; ++i)
				if (cursor.moveToPosition(i))
					out[i] = cursor.getString(0);

			cursor.close();
			db.close();
			return out;
		}
	}*/

	/**
	 * Get if there are EmotionManagement results at the date
	 * 
	 * @param tv
	 *            TimeValue of the date
	 * @return true if exists EmotionManagement
	 * @see ubicomp.soberdiary.data.structure.TimeValue
	 */
	/*
	public boolean hasEmotionManagement(TimeValue tv) {
		synchronized (sqlLock) {
			db = dbHelper.getReadableDatabase();
			String sql;
			Cursor cursor;

			sql = "SELECT * FROM EmotionManagement WHERE" + " recordYear ="
					+ tv.getYear() + " AND recordMonth=" + tv.getMonth()
					+ " AND recordDay =" + tv.getDay();
			cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			cursor.close();
			db.close();
			return count > 0;
		}
	}*/
	
	
	// Ranking

	/**
	 * Get the user's rank
	 * 
	 * @return Rank. If there are no data, return dummy data with UID=""
	 * @see ubicomp.soberdiary.data.structure.Rank
	 */
	public Rank getMyRank() {
		synchronized (sqlLock) {
			db = dbHelper.getReadableDatabase();
			String sql = "SELECT * FROM Ranking WHERE user_id='"
					+ PreferenceControl.getUID() + "'";
			Cursor cursor = db.rawQuery(sql, null);
			if (!cursor.moveToFirst()) {
				cursor.close();
				db.close();
				return new Rank("", 0);
			}
			String uid = cursor.getString(0);
			int score = cursor.getInt(1);
			int test = cursor.getInt(2);
			int note = cursor.getInt(3);
			int question = cursor.getInt(4);
			int coping = cursor.getInt(5);
			int[] additionals = new int[4];
			for (int j = 0; j < additionals.length; ++j)
				additionals[j] = cursor.getInt(6 + j);
			Rank rank = new Rank(uid, score, test, note, question, coping, additionals);
			cursor.close();
			db.close();
			return rank;
		}
	}

	/**
	 * Get all user's ranks
	 * 
	 * @return An array of Rank. If there are no Rank, return null.
	 * @see ubicomp.soberdiary.data.structure.Rank
	 */
	public Rank[] getAllRanks() {
		synchronized (sqlLock) {
			Rank[] ranks = null;
			db = dbHelper.getReadableDatabase();
			String sql = "SELECT * FROM Ranking ORDER BY total_score DESC, user_id ASC";
			Cursor cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count == 0) {
				cursor.close();
				db.close();
				return null;
			}
			ranks = new Rank[count];
			for (int i = 0; i < count; ++i) {
				cursor.moveToPosition(i);
				String uid = cursor.getString(0);
				int score = cursor.getInt(1);
				int test = cursor.getInt(2);
				int note = cursor.getInt(3);
				int question = cursor.getInt(4);
				int coping = cursor.getInt(5);
				int[] additionals = new int[4];
				for (int j = 0; j < additionals.length; ++j)
					additionals[j] = cursor.getInt(5 + j);
				ranks[i] = new Rank(uid, score, test, note, question, coping, additionals);
			}
			cursor.close();
			db.close();
			return ranks;
		}
	}

	/**
	 * Get the user's rank in a short period
	 * 
	 * @return An array of Rank. If there are no Rank, return null.
	 * @see ubicomp.soberdiary.data.structure.Rank
	 */
	public Rank[] getAllRankShort() {
		synchronized (sqlLock) {
			Rank[] ranks = null;
			db = dbHelper.getReadableDatabase();
			String sql = "SELECT * FROM RankingShort ORDER BY total_score DESC, user_id ASC";
			Cursor cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count == 0) {
				cursor.close();
				db.close();
				return null;
			}
			ranks = new Rank[count];
			for (int i = 0; i < count; ++i) {
				cursor.moveToPosition(i);
				String uid = cursor.getString(0);
				int score = cursor.getInt(1);
				ranks[i] = new Rank(uid, score);
			}
			cursor.close();
			db.close();
			return ranks;
		}
	}

	/**
	 * Truncate the Ranking table
	 * 
	 * @see ubicomp.soberdiary.data.structure.Rank
	 */
	public void clearRank() {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "DELETE  FROM Ranking";
			db.execSQL(sql);
			db.close();
		}
	}

	/**
	 * Update the Rank
	 * 
	 * @param data
	 *            Updated Rank
	 * @see ubicomp.soberdiary.data.structure.Rank
	 */
	public void updateRank(Rank data) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "SELECT * FROM Ranking WHERE user_id = '"
					+ data.getUid() + "'";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.getCount() == 0) {
				ContentValues content = new ContentValues();
				content.put("user_id", data.getUid());
				content.put("total_score", data.getScore());
				content.put("test_score", data.getTest());
				content.put("note_score", data.getNote());
				content.put("question_score", data.getQuestion());
				content.put("coping_score", data.getCoping());
				content.put("times_score", data.getTestTimes());
				content.put("pass_score", data.getTestPass());
				content.put("normalQ_score", data.getNormalQ());
				content.put("randomQ_score", data.getRandomQ());
				
				db.insert("Ranking", null, content);
			} else {
				sql = "UPDATE Ranking SET" + " total_score = "
						+ data.getScore() + "," + " test_score = "
						+ data.getTest() + "," + " note_score = "
						+ data.getNote() + "," + " question_score="
						+ data.getQuestion() + "," + " coping_score = "
						+ data.getCoping() + "," + " times_score = "
						+ data.getTestTimes() + "," + " pass_score = "
						+ data.getTestPass() + "," + " normalQ_score = "
						+ data.getNormalQ() + "," + " randomQ_score = "
						+ data.getRandomQ()
						+ " WHERE user_id = " + "'" + data.getUid() + "'";
				db.execSQL(sql);
			}
			cursor.close();
			db.close();
		}
	}

	/**
	 * Truncate the RankingShort table
	 * 
	 * @see ubicomp.soberdiary.data.structure.Rank
	 */
	public void clearRankShort() {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "DELETE  FROM RankingShort";
			db.execSQL(sql);
			db.close();
		}
	}

	/**
	 * Update the Rank in a short period
	 * 
	 * @param data
	 *            Updated Rank in a short period
	 * @see ubicomp.soberdiary.data.structure.Rank
	 */
	public void updateRankShort(Rank data) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "SELECT * FROM RankingShort WHERE user_id = '"
					+ data.getUid() + "'";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.getCount() == 0) {
				ContentValues content = new ContentValues();
				content.put("user_id", data.getUid());
				content.put("total_score", data.getScore());
				db.insert("RankingShort", null, content);
			} else {
				sql = "UPDATE RankingShort SET" + " total_score = "
						+ data.getScore() + " WHERE user_id = " + "'"
						+ data.getUid() + "'";
				db.execSQL(sql);
			}
			cursor.close();
			db.close();
		}
	}
	
	
	
	
	 
	//Cassette   //TODO: Working on here
	
	public Cassette[] getAllCassette() {
		synchronized (sqlLock) {
			Cassette[] cassette = null;
			db = dbHelper.getReadableDatabase();
			String sql = "SELECT * FROM Cassette ORDER BY ts DESC";
			Cursor cursor = db.rawQuery(sql, null);
			int count = cursor.getCount();
			if (count == 0) {
				cursor.close();
				db.close();
				return null;
			}
			cassette = new Cassette[count];
			for (int i = 0; i < count; ++i) {
				cursor.moveToPosition(i);
				long ts = cursor.getLong(1);
				String cid = cursor.getString(2);
				int isUsed = cursor.getInt(3);
				cassette[i] = new Cassette(ts, isUsed, cid);
			}
			cursor.close();
			db.close();
			return cassette;
		}
	}
	
	public void modifyCassetteById(String casseteId, int isUsed) {
		synchronized (sqlLock) {

			db = dbHelper.getReadableDatabase();
			String sql;
			//sql = "SELECT * FROM TestResult WHERE ts = " + ts + " AND isPrime = 1";
			sql = "UPDATE Cassette SET isUsed = " + isUsed + " WHERE cassetteId = '" + casseteId + "'";
			db.execSQL(sql);
			db.close();

			return ;
		}
	}
	
	public void deleteCassetteById(String casseteeId) {
		synchronized (sqlLock) {

			db = dbHelper.getReadableDatabase();
			String sql;
			//sql = "SELECT * FROM TestResult WHERE ts = " + ts + " AND isPrime = 1";
			sql = "DELETE FROM Cassette WHERE cassetteId= '" + casseteeId + "'";
			db.execSQL(sql);
			db.close();

			return ;
		}
	}

	
	
	public void insertCassette(String cassette_id ){
		db = dbHelper.getWritableDatabase();
		ContentValues content = new ContentValues();
		content.put("cassetteId", cassette_id);
		content.put("isUsed", 1);
		content.put("ts", 0);
		db.insert("Cassette", null, content);
		db.close();
		return ;
			
	}
	
	public boolean checkCassette(String cassette_id){
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "SELECT * FROM Cassette WHERE cassetteId = '"
					+ cassette_id + "'" + " AND isUsed = 1";
			Cursor cursor = db.rawQuery(sql, null);
			boolean check;
			if (cursor.getCount() == 0) {
				check = true;
			}
			else{
				check = false;
			}
			cursor.close();
			db.close();
			return check;
		}
	}
	
	/**
	 * Truncate the RankingShort table
	 * 
	 * @see ubicomp.soberdiary.data.structure.Cassette
	 */
	public void clearCassette() {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "DELETE FROM Cassette";
			db.execSQL(sql);
			db.close();
		}
	}

	/**
	 * Update CassetteId from Server 
	 * 
	 * @param data
	 *            Updated CassetteId in a short period
	 * @see ubicomp.ketdiary.data.structure.CassetteId
	 */
	public void updateCassette(Cassette data) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "SELECT * FROM Cassette WHERE cassetteId = '"
					+ data.getCassetteId() + "'";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.getCount() == 0) {
				ContentValues content = new ContentValues();
				content.put("ts", data.getTv().getTimestamp());
				content.put("cassetteId", data.getCassetteId());
				content.put("isUsed", data.getisUsed());
				db.insert("Cassette", null, content);
			} else {
				sql = "UPDATE Cassette SET" + " ts = "
						+ data.getTv().getTimestamp() + "," + " isUsed = "
						+ data.getisUsed() + " WHERE cassetteId = " 
						+ "'"+ data.getCassetteId() + "'";
				db.execSQL(sql);
			}
			cursor.close();
			db.close();
		}
	}

	
	// TestDetail
	
	/**
	 * This method is used for the latest result detection
	 * 
	 * @return Detection. If there are no Detection, return a dummy data.
	 * @see ubicomp.soberdiary.data.structure.Detection
	 *
	 */
	
	public TestDetail getLatestTestDetail() {
		synchronized (sqlLock) {
			db = dbHelper.getReadableDatabase();
			String sql = "SELECT * FROM TestDetail ORDER BY ts DESC LIMIT 1";
			Cursor cursor = db.rawQuery(sql, null);
			if (!cursor.moveToFirst()) {
				cursor.close();
				db.close();
				return new TestDetail("", 0, 0, 0, 0, 0, 0, 0, "", "");
			}

			long ts = cursor.getLong(5);
			String cassetteId = cursor.getString(1) ;
			int failedState = cursor.getInt(7);
			int firstVoltage = cursor.getInt(8);
			int secondVoltage = cursor.getInt(9);
			int devicePower = cursor.getInt(10);
			int colorReading = cursor.getInt(11);
			float connectionFailRate = cursor.getFloat(12);
			String failedReason = cursor.getString(13);
			String hardwareVersion = cursor.getString(14);

			TestDetail testDetail = new TestDetail(cassetteId, ts, failedState, firstVoltage,
					secondVoltage, devicePower, colorReading,
	                connectionFailRate, failedReason, hardwareVersion);

			cursor.close();
			db.close();
			return testDetail;
		}
	}

		/**
		 * Insert a TestDetail recorded detailed information of breath condition
		 * when the user takes BrAC tests
		 * 
		 * @param data
		 *            inserted BreathDetail
		 * @see ubicomp.soberdiary.data.structure.BreathDetail
		 */
		public void insertTestDetail(TestDetail data) {
			synchronized (sqlLock) {
				db = dbHelper.getWritableDatabase();

				String sql = "SELECT * FROM TestDetail WHERE ts ="
						+ data.getTv().getTimestamp();
				Cursor cursor = db.rawQuery(sql, null);
				if (!cursor.moveToFirst()) {
					ContentValues content = new ContentValues();
					content.put("year", data.getTv().getYear());
					content.put("month", data.getTv().getMonth());
					content.put("day", data.getTv().getDay());
					content.put("ts", data.getTv().getTimestamp());
					content.put("week", data.getTv().getWeek());
					content.put("cassetteId", data.getCassetteId());
					content.put("failedState", data.getFailedState());
					content.put("firstVoltage", data.getFirstVoltage());
					content.put("secondVoltage", data.getSecondVoltage());
					content.put("devicePower", data.getDevicePower());
					content.put("colorReading", data.getColorReading());
					content.put("connectionFailRate",
							data.getConnectionFailRate());
					content.put("failedReason", data.getFailedReason());
					content.put("hardwareVersion", data.getHardwareVersion());
					db.insert("TestDetail", null, content);
				}
				cursor.close();
				db.close();
			}
		}

		/**
		 * Get all TestDetail which are not uploaded to the server
		 * 
		 * @return An array of BreathDetail. If there are no BreathDetail, return
		 *         null.
		 * @see ubicomp.soberdiary.data.structure.BreathDetail
		 */
		public TestDetail[] getNotUploadedTestDetail() {
			synchronized (sqlLock) {
				TestDetail[] data = null;
				db = dbHelper.getReadableDatabase();
				String sql;
				Cursor cursor;
				sql = "SELECT * FROM TestDetail WHERE upload = 0";
				cursor = db.rawQuery(sql, null);
				int count = cursor.getCount();
				if (count == 0) {
					cursor.close();
					db.close();
					return null;
				}

				data = new TestDetail[count];

				for (int i = 0; i < count; ++i) {
					cursor.moveToPosition(i);
					long ts = cursor.getLong(5);
					String cassetteId = cursor.getString(1) ;
					int failedState = cursor.getInt(7);
					int firstVoltage = cursor.getInt(8);
					int secondVoltage = cursor.getInt(9);
					int devicePower = cursor.getInt(10);
					int colorReading = cursor.getInt(11);
					float connectionFailRate = cursor.getFloat(12);
					String failedReason = cursor.getString(13);
					String hardwardVersion = cursor.getString(14);

					data[i] = new TestDetail(cassetteId, ts, failedState, firstVoltage,
							secondVoltage, devicePower, colorReading,
			                connectionFailRate, failedReason, hardwardVersion);
				}
				cursor.close();
				db.close();
				return data;
			}
		}

		/**
		 * Label the TestDetail uploaded
		 * 
		 * @param ts
		 *            Timestamp of the uploaded TestDetail
		 * @see ubicomp.soberdiary.data.structure.TestDetail
		 */
		public void setTestDetailUploaded(long ts) {
			synchronized (sqlLock) {
				db = dbHelper.getWritableDatabase();
				String sql = "UPDATE TestDetail SET upload = 1 WHERE ts = " + ts;
				db.execSQL(sql);
				db.close();
			}
		}


		
		// QuestionTest

		/**
		 * Get the latest StorytellingTest result
		 * 
		 * @return StorytellingTest. If there are no StorytellingTest, return a
		 *         dummy data.
		 * @see ubicomp.soberdiary.data.structure.StorytellingTest
		 */
		public QuestionTest getLatestQuestionTest() {
			synchronized (sqlLock) {
				db = dbHelper.getReadableDatabase();
				String sql;
				Cursor cursor;
				sql = "SELECT * FROM QuestionTest WHERE isCorrect = 1 ORDER BY ts DESC LIMIT 1";
				cursor = db.rawQuery(sql, null);
				if (!cursor.moveToFirst()) {
					cursor.close();
					db.close();
					return new QuestionTest(0, 0, 0, "", 0, 0);
				}
				long ts = cursor.getLong(4);
				int type = cursor.getInt(7);
				int isCorrect = cursor.getInt(8);
				String selection = cursor.getString(9);
				int choose = cursor.getInt(10);
				int score = cursor.getInt(11);
				
				cursor.close();
				db.close();
				
				return new QuestionTest(ts, type, isCorrect, selection,
						choose, score);
			}
		}

		/**
		 * Insert a QuestionTest result
		 * 
		 * @return # of credits got by the user
		 * @param data
		 *            inserted StorytellingTest
		 * @see ubicomp.soberdiary.data.structure.StorytellingTest
		 */
		public int insertQuestionTest(QuestionTest data) {
			synchronized (sqlLock) {
				QuestionTest prev_data = getLatestQuestionTest();
				int addScore = 0;
				if (!prev_data.getTv().isSameTimeBlock(data.getTv())
						&& (data.getisCorrect()==1) )
					addScore = 1;
				
				if(data.getQuestionType() == 1 && (data.getisCorrect()==1)){
					addScore = 3;
				}
				
				if (!StartDateCheck.afterStartDate())
					addScore = 0;
				
								
				db = dbHelper.getWritableDatabase();
				ContentValues content = new ContentValues();
				content.put("year", data.getTv().getYear());
				content.put("month", data.getTv().getMonth());
				content.put("day", data.getTv().getDay());
				content.put("ts", data.getTv().getTimestamp());
				content.put("week", data.getTv().getWeek());
				content.put("timeslot", data.getTv().getTimeslot());
				content.put("questionType", data.getQuestionType());
				content.put("isCorrect", data.getisCorrect());
				content.put("selection", data.getSelection());
				content.put("choose", data.getChoose());
				content.put("score", prev_data.getScore() + addScore);
				db.insert("QuestionTest", null, content);
				db.close();
				return addScore;
			}
		}

		/**
		 * Get all StorytellingTest results which are not uploaded to the server
		 * 
		 * @return An array of StorytellingTest. If there are no StorytellingTest,
		 *         return null.
		 * @see ubicomp.soberdiary.data.structure.StorytellingTest
		 */
		public QuestionTest[] getNotUploadedQuestionTest() {
			synchronized (sqlLock) {
				QuestionTest[] data = null;

				db = dbHelper.getReadableDatabase();
				String sql;
				Cursor cursor;

				sql = "SELECT * FROM QuestionTest WHERE upload = 0";
				cursor = db.rawQuery(sql, null);
				int count = cursor.getCount();
				if (count == 0) {
					cursor.close();
					db.close();
					return null;
				}

				data = new QuestionTest[count];

				for (int i = 0; i < count; ++i) {
					cursor.moveToPosition(i);
					long ts = cursor.getLong(4);
					int type = cursor.getInt(7);
					int isCorrect = cursor.getInt(8);
					String selection = cursor.getString(9);
					int choose = cursor.getInt(10);
					int score = cursor.getInt(11);
					data[i] = new QuestionTest(ts, type, isCorrect, selection,
							choose, score);
				}

				cursor.close();
				db.close();

				return data;
			}
		}

		/**
		 * Label the QuestionTest result uploaded
		 * 
		 * @param ts
		 *            Timestamp of the uploaded StorytellingTest
		 * @see ubicomp.soberdiary.data.structure.StorytellingTest
		 */
		public void setQuestionTestUploaded(long ts) {
			synchronized (sqlLock) {
				db = dbHelper.getWritableDatabase();
				String sql = "UPDATE QuestionTest SET upload = 1 WHERE ts = "
						+ ts;
				db.execSQL(sql);
				db.close();
			}
		}
		
		/**
		 * This method is used for checking if the user do the brac detection at
		 * this time slot
		 * 
		 * @return true if the user do a brac detection at the current time slot
		 */
		
		public boolean randomQuestionDone() {
			synchronized (sqlLock) {
				db = dbHelper.getReadableDatabase();
				long ts = System.currentTimeMillis();
				TimeValue tv = TimeValue.generate(ts);
				String sql = "SELECT id FROM QuestionTest WHERE" + " year ="
						+ tv.getYear() + " AND month = " + tv.getMonth()
						+ " AND day= " + tv.getDay() + " AND questionType= 1";
				Cursor cursor = db.rawQuery(sql, null);
				boolean result = cursor.getCount() > 0;
				cursor.close();
				db.close();
				return result;
			}
		}
		
		// CopingSkill

		/**
		 * Get the latest CopingSkill result
		 * 
		 * @return CopingSkill. If there are no CopingSkill, return a dummy data.
		 * @see ubicomp.soberdiary.data.structure.EmotionDIY
		 */
		public CopingSkill getLatestCopingSkill() {
			synchronized (sqlLock) {
				db = dbHelper.getReadableDatabase();
				String sql;
				Cursor cursor;
				sql = "SELECT * FROM CopingSkill ORDER BY ts DESC LIMIT 1";
				cursor = db.rawQuery(sql, null);
				if (!cursor.moveToFirst()) {
					cursor.close();
					db.close();
					return new CopingSkill(0, 0, 0, null, 0);
				}
				long ts = cursor.getLong(4);
				int skillType = cursor.getInt(7);
				int skillSelect = cursor.getInt(8);
				String recreation = cursor.getString(9);
				int score = cursor.getInt(10);
				
				cursor.close();
				db.close();
				
				return new CopingSkill(ts, skillType, skillSelect, recreation, score);
				
			}
		}

		/**
		 * Insert an CopingSkill result
		 * 
		 * @return # credits got by the user
		 * @see ubicomp.soberdiary.data.structure.CopingSkill
		 */
		public int insertCopingSkill(CopingSkill data) {
			synchronized (sqlLock) {
				CopingSkill prev_data = getLatestCopingSkill();
				int addScore = 0;
				if (!prev_data.getTv().isSameTimeBlock(data.getTv()))
					addScore = 1;
				if (!StartDateCheck.afterStartDate())
					addScore = 0;
				db = dbHelper.getWritableDatabase();
				ContentValues content = new ContentValues();
				content.put("year", data.getTv().getYear());
				content.put("month", data.getTv().getMonth());
				content.put("day", data.getTv().getDay());
				content.put("ts", data.getTv().getTimestamp());
				content.put("week", data.getTv().getWeek());
				content.put("timeslot", data.getTv().getTimeslot());
				content.put("skillType", data.getSkillType());
				content.put("skillSelect", data.getSkillSelect());
				content.put("recreation", data.getRecreation());
				content.put("score", prev_data.getScore() + addScore);
				db.insert("CopingSkill", null, content);
				db.close();
				return addScore;
			}
		}

		/**
		 * Get all CopingSkill results which are not uploaded to the server
		 * 
		 * @return An array of EmotionDIY. If there are no EmotionDIY, return null.
		 * @see ubicomp.soberdiary.data.structure.EmotionDIY
		 */
		public CopingSkill[] getNotUploadedCopingSkill() {
			synchronized (sqlLock) {
				CopingSkill[] data = null;

				db = dbHelper.getReadableDatabase();
				String sql;
				Cursor cursor;

				sql = "SELECT * FROM CopingSkill WHERE upload = 0";
				cursor = db.rawQuery(sql, null);
				int count = cursor.getCount();
				if (count == 0) {
					cursor.close();
					db.close();
					return null;
				}

				data = new CopingSkill[count];

				for (int i = 0; i < count; ++i) {
					cursor.moveToPosition(i);
					long ts = cursor.getLong(4);
					int skillType = cursor.getInt(7);
					int skillSelect = cursor.getInt(8);
					String recreation = cursor.getString(9);
					int score = cursor.getInt(10);
					data[i] = new CopingSkill(ts, skillType, skillSelect, recreation, score);
				}

				cursor.close();
				db.close();

				return data;
			}
		}

		/**
		 * Label the CopingSkill result uploaded
		 * 
		 * @param ts
		 *            Timestamp of the Emotion DIY result
		 * @see ubicomp.soberdiary.data.structure.EmotionDIY
		 */
		public void setCopingSkillUploaded(long ts) {
			synchronized (sqlLock) {
				db = dbHelper.getWritableDatabase();
				String sql = "UPDATE CopingSkill SET upload = 1 WHERE ts = " + ts;
				db.execSQL(sql);
				db.close();
			}
		}
		
		// ExchangeHistory

		/**
		 * Insert a ExchangeHistory when the user exchanges credits for coupons
		 * 
		 * @param data
		 *            inserted ExchangeHistory
		 * @see ubicomp.soberdiary.data.structure.ExchangeHistory
		 */
		public void insertExchangeHistory(ExchangeHistory data) {
			synchronized (sqlLock) {
				db = dbHelper.getWritableDatabase();
				ContentValues content = new ContentValues();
				content.put("ts", data.getTv().getTimestamp());
				content.put("exchangeCounter", data.getExchangeNum());
				db.insert("ExchangeHistory", null, content);
				db.close();
			}
		}

		/**
		 * Get all ExchangeHistory which are not uploaded to the server
		 * 
		 * @return An array of ExchangeHistory. If there are no ExchangeHistory,
		 *         return null.
		 * @see ubicomp.soberdiary.data.structure.ExchangeHistory
		 */
		public ExchangeHistory[] getNotUploadedExchangeHistory() {
			synchronized (sqlLock) {
				ExchangeHistory[] data = null;
				db = dbHelper.getReadableDatabase();
				String sql;
				Cursor cursor;
				sql = "SELECT * FROM ExchangeHistory WHERE upload = 0";
				cursor = db.rawQuery(sql, null);
				int count = cursor.getCount();
				if (count == 0) {
					cursor.close();
					db.close();
					return null;
				}

				data = new ExchangeHistory[count];

				for (int i = 0; i < count; ++i) {
					cursor.moveToPosition(i);
					long ts = cursor.getLong(1);
					int exchangeCounter = cursor.getInt(2);
					data[i] = new ExchangeHistory(ts, exchangeCounter);
				}
				cursor.close();
				db.close();
				return data;
			}
		}

		/**
		 * Label the ExchangeHistory uploaded
		 * 
		 * @param ts
		 *            Timestamp of the uploaded ExchangeHistory
		 * @see ubicomp.soberdiary.data.structure.ExchangeHistory
		 */
		public void setExchangeHistoryUploaded(long ts) {
			synchronized (sqlLock) {
				db = dbHelper.getWritableDatabase();
				String sql = "UPDATE ExchangeHistory SET upload = 1 WHERE ts = "
						+ ts;
				db.execSQL(sql);
				db.close();
			}
		}

}
