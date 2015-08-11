package com.ubicomp.ketdiary.data.db;

import java.util.Calendar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.ubicomp.ketdiary.MainActivity;
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.data.structure.TimeValue;

/**
 * This class is an AsyncTask for generating dummy BrAC test results
 * 
 * @author Stanley Wang
 */
public class DatabaseDummyData extends AsyncTask<Void, Void, Void> {

	private Context context;
	private ProgressDialog dialog = null;
	/**
	 * Constructor
	 * 
	 * @param context
	 *            Context of the Activity
	 */
	public DatabaseDummyData(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setMessage("Please Wait...");
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		insertDummyData();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (dialog != null)
			dialog.dismiss();
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}

	/** This method implements the generate algorithm of the dummy data */
	private void insertDummyData() {
		DatabaseControl db = new DatabaseControl();
		
		Calendar c = Calendar.getInstance();
		long ts;
		TestResult testResult;
		NoteAdd noteAdd;
		
		c.set(2015, 6, 12); 
		// 設定日期
		ts = c.getTimeInMillis();
		
		/* TestResult
		 * this.result = result;  //0:陰性(通過) 1:陽性(不通過)
		   this.tv = TimeValue.generate(tv);
		   this.cassette_id = cassette_id;
		   this.isPrime = isPrime;
		   this.isFilled= isFilled;
		   this.weeklyScore= weeklyScore;
		   this.score = score;
		 */
		testResult = new TestResult(0, ts, "tmp_id", 1, 1, 0, 0);
		db.insertTestResult(testResult,false);
		/*int isAfterTest, //Yes: 1
		 *long tv, 
		 *int rYear, 
		 *int rMonth, 
		 *int rDay, 
		 *int timeslot, //1: 早上, 2: 下午, 3: 晚上
		 *int category, //1: 自己     2:他人
		 *int type,     //1~8
		 *int items,    //
		 *int impact,   //0~6   = -3 ~ 3
		 *String description, 
		 *int weeklyScore, 
		 *int score
		 */
			
		noteAdd = new NoteAdd(1, ts, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 1, 1, 5, 504, 6, "test", 0, 0);
		db.insertNoteAdd(noteAdd);
		
		
		
		
		
		
		
	}

}
