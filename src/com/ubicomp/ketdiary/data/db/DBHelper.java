package com.ubicomp.ketdiary.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database Helper for initializing the database or update the database
 * 
 * @author Andy Chen
 */
public class DBHelper extends SQLiteOpenHelper {

	/* SQLiteOpenHelper. need to migrate with */
	private static final String DATABASE_NAME = "rehabdiary";
	private static final int DB_VERSION = 13;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            Application Context
	 */
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TestResult Table
		db.execSQL("CREATE TABLE TestResult ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ " result INTEGER," + " cassetteId CHAR[255] NOT NULL,"
				+ " year INTEGER NOT NULL," 
				+ " month INTEGER NOT NULL," + " day INTEGER NOT NULL,"
				+ " ts INTEGER NOT NULL," + " week INTEGER NOT NULL,"
				+ " isPrime INTEGER NOT NULL, "	+ " isFilled INTEGER NOT NULL , "
				+ " weeklyScore INTEGER NOT NULL," + " score INTEGER NOT NULL,"
				+ " upload INTEGER NOT NULL DEFAULT 0" + ")");
		
		
		//  NoteAdd Table
		db.execSQL("CREATE TABLE NoteAdd ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ " isAfterTest INT NOT NULL," + " year INTEGER NOT NULL,"
				+ " month INTEGER NOT NULL," + " day INTEGER NOT NULL,"
				+ " ts INTEGER NOT NULL," + " week INTEGER NOT NULL,"
				+ " recordYear INTEGER NOT NULL,"
				+ " recordMonth INTEGER NOT NULL,"
				+ " recordDay INTEGER NOT NULL," 
				+ " timeSlot INTEGER NOT NULL," + " category INTEGER NOT NULL,"
				+ " type INTEGER NOT NULL," + " items INTEGER NOT NULL, "
				+ " impact INTEGER NOT NULL, "+ " description CHAR[255], "
				+ " weeklyScore INTEGER NOT NULL," + " score INTEGER NOT NULL,"
				+ " upload INTEGER NOT NULL DEFAULT 0" + ")");
		
		
		db.execSQL("CREATE TABLE TestDetail ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ " cassetteId CHAR[255] NOT NULL," + " year INTEGER NOT NULL,"
				+ " month INTEGER NOT NULL," + " day INTEGER NOT NULL,"
				+ " ts INTEGER NOT NULL," + " week INTEGER NOT NULL,"
				+ " failedState INTEGER NOT NULL," + " firstVoltage INTEGER NOT NULL,"
				+ " secondVoltage INTEGER NOT NULL," + " devicePower INTEGER NOT NULL, "
				+ " colorReading INTEGER NOT NULL, "+ " connectionFailRate FLOAT, " 
				+ " failedReason CHAR[255], "
				+ " hardwareVersion CHAR[255], "
				+ " upload INTEGER NOT NULL DEFAULT 0" + ")");
		
		db.execSQL("CREATE TABLE Ranking (" + " user_id CHAR[255] PRIMERY KEY,"
				+ " total_score INTEGER NOT NULL,"
				+ " test_score INTEGER NOT NULL  DEFAULT 0,"
				+ " note_score INTEGER NOT NULL  DEFAULT 0,"
				+ " question_score INTEGER NOT NULL  DEFAULT 0,"
				+ " coping_score INTEGER NOT NULL  DEFAULT 0,"
				
				+ " times_score INTEGER NOT NULL  DEFAULT 0,"
				+ " pass_score INTEGER NOT NULL  DEFAULT 0,"
				+ " normalQ_score INTEGER NOT NULL  DEFAULT 0,"
				+ " randomQ_score INTEGER NOT NULL  DEFAULT 0"+")");

		db.execSQL("CREATE TABLE RankingShort ("
				+ " user_id CHAR[255] PRIMERY KEY,"
				+ " total_score INTEGER NOT NULL DEFAULT 0" + ")");
		
		db.execSQL("CREATE TABLE QuestionTest ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " year INTEGER NOT NULL," + " month INTEGER NOT NULL,"
				+ " day INTEGER NOT NULL," + " ts INTEGER NOT NULL,"
				+ " week INTEGER NOT NULL," + " timeSlot INTEGER NOT NULL,"
				+ " questionType INTEGER NOT NULL,"
				+ " isCorrect INTEGER NOT NULL DEFAULT 0,"
				+ " selection CHAR[255] NOT NULL,"
				+ " choose INTEGER NOT NULL DEFAULT 0,"
				+ " score INTEGER NOT NULL,"
				+ " upload INTEGER NOT NULL DEFAULT 0" + ")");
		
		db.execSQL("CREATE TABLE CopingSkill ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " year INTEGER NOT NULL," + " month INTEGER NOT NULL,"
				+ " day INTEGER NOT NULL," + " ts INTEGER NOT NULL,"
				+ " week INTEGER NOT NULL," + " timeSlot INTEGER NOT NULL,"
				+ " skillType INTEGER NOT NULL,"
				+ " skillSelect INTEGER NOT NULL DEFAULT 0,"
				+ " recreation CHAR[255] NOT NULL,"
				+ " score INTEGER NOT NULL,"
				+ " upload INTEGER NOT NULL DEFAULT 0" + ")");
		
		db.execSQL("CREATE TABLE ExchangeHistory ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " ts INTEGER NOT NULL,"
				+ " exchangeCounter INTEGER NOT NULL,"
				+ " upload INTEGER NOT NULL DEFAULT 0,"
				+ " testSuccess INTEGER NOT NULL DEFAULT 0,"
				+ " hasData INTEGER NOT NULL DEFAULT 0" + ")");
		
		db.execSQL("CREATE TABLE Cassette ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " ts INTEGER NOT NULL,"
				+ " cassetteId CHAR[255] NOT NULL,"
				+ " isUsed INTEGER NOT NULL,"
				+ " upload INTEGER NOT NULL DEFAULT 0)");


	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int old_ver, int new_ver) {
		
		db.execSQL("DROP TABLE IF EXISTS Ranking");
			
		db.execSQL("CREATE TABLE Ranking (" + " user_id CHAR[255] PRIMERY KEY,"
				+ " total_score INTEGER NOT NULL,"
				+ " test_score INTEGER NOT NULL  DEFAULT 0,"
				+ " note_score INTEGER NOT NULL  DEFAULT 0,"
				+ " question_score INTEGER NOT NULL  DEFAULT 0,"
				+ " coping_score INTEGER NOT NULL  DEFAULT 0,"
				
				+ " times_score INTEGER NOT NULL  DEFAULT 0,"
				+ " pass_score INTEGER NOT NULL  DEFAULT 0,"
				+ " normalQ_score INTEGER NOT NULL  DEFAULT 0,"
				+ " randomQ_score INTEGER NOT NULL  DEFAULT 0"+")");
				
		
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	@Override
	public synchronized void close() {
		super.close();
	}

}
