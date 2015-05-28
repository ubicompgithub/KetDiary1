package com.ubicomp.ketdiary.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ubicomp.ketdiary.App;

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


}
