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
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.Rank;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.data.structure.TimeValue;
import com.ubicomp.ketdiary.system.PreferenceControl;

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
				boolean isPrime = !(data.isSameDay(prev_data));
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
			int timeslot = curTV.getTimeslot();
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
			int advice = cursor.getInt(3);
			int manage = cursor.getInt(4);
			int story = cursor.getInt(5);
			int[] additionals = new int[8];
			for (int j = 0; j < additionals.length; ++j)
				additionals[j] = cursor.getInt(6 + j);
			Rank rank = new Rank(uid, score, test, advice, manage, story,
					additionals);
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
				int advice = cursor.getInt(3);
				int manage = cursor.getInt(4);
				int story = cursor.getInt(5);
				int[] additionals = new int[8];
				for (int j = 0; j < additionals.length; ++j)
					additionals[j] = cursor.getInt(6 + j);
				ranks[i] = new Rank(uid, score, test, advice, manage, story,
						additionals);
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
				content.put("advice_score", data.getAdvice());
				content.put("manage_score", data.getManage());
				content.put("story_score", data.getStory());
				content.put("advice_questionnaire",
						data.getAdviceQuestionnaire());
				content.put("advice_emotion_diy", data.getAdviceEmotionDiy());
				content.put("manage_voice", data.getManageVoice());
				content.put("manage_emotion", data.getManageEmotion());
				content.put("manage_additional", data.getManageAdditional());
				content.put("story_read", data.getStoryRead());
				content.put("story_test", data.getStoryTest());
				content.put("story_fb", data.getStoryFb());
				db.insert("Ranking", null, content);
			} else {
				sql = "UPDATE Ranking SET" + " total_score = "
						+ data.getScore() + "," + " test_score = "
						+ data.getTest() + "," + " advice_score = "
						+ data.getAdvice() + "," + " manage_score="
						+ data.getManage() + "," + " story_score = "
						+ data.getStory() + "," + " advice_questionnaire="
						+ data.getAdviceQuestionnaire() + ","
						+ " advice_emotion_diy=" + data.getAdviceEmotionDiy()
						+ "," + " manage_voice=" + data.getManageVoice() + ","
						+ " manage_emotion=" + data.getManageEmotion() + ","
						+ " manage_additional=" + data.getManageAdditional()
						+ "," + " story_read=" + data.getStoryRead() + ","
						+ " story_test=" + data.getStoryTest() + ","
						+ " story_fb=" + data.getStoryFb()
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

}
