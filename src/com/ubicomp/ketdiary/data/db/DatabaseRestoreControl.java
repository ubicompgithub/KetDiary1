package com.ubicomp.ketdiary.data.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.data.structure.CopingSkill;
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.QuestionTest;
import com.ubicomp.ketdiary.data.structure.TestResult;

/**
 * Control insertion and modification on database for the restore process
 * 
 * @author Andy Chen
 * @see ubicomp.soberdiary.data.database.DatabaseRestore
 */
public class DatabaseRestoreControl {

	private SQLiteOpenHelper dbHelper = null;
	private SQLiteDatabase db = null;

	public DatabaseRestoreControl() {
		dbHelper = new DBHelper(App.getContext());
	}

	/**
	 * Restore TestResult
	 * 
	 * @param data
	 *            TestResult data
	 */
	public void restoreTestResult(TestResult data) {
		db = dbHelper.getWritableDatabase();
		if (data.getIsPrime() == 1) {
			String sql = "UPDATE TestResult SET isPrime = 0" + " WHERE year ="
					+ data.getTv().getYear() + " AND month="
					+ data.getTv().getMonth() + " AND day ="
					+ data.getTv().getDay();
			db.execSQL(sql);
		}
		ContentValues content = new ContentValues();
		content.put("result", data.getResult());
		content.put("cassetteId", data.getCassette_id());
		content.put("year", data.getTv().getYear());
		content.put("month", data.getTv().getMonth());
		content.put("day", data.getTv().getDay());
		content.put("ts", data.getTv().getTimestamp());
		content.put("week", data.getTv().getWeek());
		content.put("isPrime", data.getIsPrime());
		content.put("isFilled", data.getIsFilled());
		content.put("weeklyScore", data.getWeeklyScore());
		content.put("score", data.getScore());
		content.put("upload", 1);
		db.insert("TestResult", null, content);
		db.close();
	}

	/**
	 * Restore NoteAdd
	 * 
	 * @param data
	 *            NoteAdd data
	 */
	public void restoreNoteAdd(NoteAdd data) {
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
		content.put("weeklyScore", data.getWeeklyScore());
		content.put("score", data.getScore());
		content.put("upload", 1);
		db.insert("NoteAdd", null, content);
		db.close();
	}

	/**
	 * Restore QuestionTest
	 * 
	 * @param data
	 *            QuestionTest data
	 */
	public void restoreQuestionTest(QuestionTest data) {
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
		content.put("score", data.getScore());
		content.put("upload", 1);
		db.insert("QuestionTest", null, content);
		db.close();
	}

	/**
	 * Restore CopingSkill
	 * 
	 * @param data
	 *            CopingSkill data
	 */
	public void restoreCopingSkill(CopingSkill data) {
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
		content.put("score", data.getScore());
		content.put("upload", 1);
		db.insert("CopingSkill", null, content);
		db.close();
	}


	/** Truncate the database */
	public void deleteAll() {
		db = dbHelper.getWritableDatabase();
		String sql = null;
		sql = "DELETE FROM TestResult";
		db.execSQL(sql);
		sql = "DELETE FROM NoteAdd";
		db.execSQL(sql);
		sql = "DELETE FROM TestDetail";
		db.execSQL(sql);
		sql = "DELETE FROM Ranking";
		db.execSQL(sql);
		sql = "DELETE FROM RankingShort";
		db.execSQL(sql);
		sql = "DELETE FROM QuestionTest";
		db.execSQL(sql);
		sql = "DELETE FROM CopingSkill";
		db.execSQL(sql);
		sql = "DELETE FROM ExchangeHistory";
		db.execSQL(sql);
		db.close();
	}
}
