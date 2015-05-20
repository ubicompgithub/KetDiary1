package com.ubicomp.ketdiary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database Helper for initializing the database or update the database
 * 
 * @author Stanley Wang
 */
public class DBHelper extends SQLiteOpenHelper {

	/* SQLiteOpenHelper. need to migrate with */
	private static final String DATABASE_NAME = "drugfreediary";
	private static final int DB_VERSION = 1;

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
		// new Detection Table, rename as Testing
		db.execSQL("CREATE TABLE Testing ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ " brac FLOAT," + " year INTEGER NOT NULL,"
				+ " month INTEGER NOT NULL," + " day INTEGER NOT NULL,"
				+ " ts INTEGER NOT NULL," + " week INTEGER NOT NULL,"
				+ " timeSlot INTEGER NOT NULL," + " trigger INTEGER NOT NULL,"
				+ " craving INTEGER NOT NULL," + " isPrime INTEGER NOT NULL, "
				+ " weeklyScore INTEGER NOT NULL," + " score INTEGER NOT NULL,"
				+ " upload INTEGER NOT NULL DEFAULT 0" + ")");
		
		
		//  old Detection Table
		db.execSQL("CREATE TABLE Detection ("
				+ " id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ " brac FLOAT NOT NULL," + " year INTEGER NOT NULL,"
				+ " month INTEGER NOT NULL," + " day INTEGER NOT NULL,"
				+ " ts INTEGER NOT NULL," + " week INTEGER NOT NULL,"
				+ " timeSlot INTEGER NOT NULL," + " emotion INTEGER NOT NULL,"
				+ " craving INTEGER NOT NULL," + " isPrime INTEGER NOT NULL, "
				+ " weeklyScore INTEGER NOT NULL," + " score INTEGER NOT NULL,"
				+ " upload INTEGER NOT NULL DEFAULT 0" + ")");

	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int old_ver, int new_ver) {

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
