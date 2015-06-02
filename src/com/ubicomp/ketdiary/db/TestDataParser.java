package com.ubicomp.ketdiary.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import android.content.Context;
import android.util.Log;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.TestResult;
import com.ubicomp.ketdiary.file.MainStorage;
import com.ubicomp.ketdiary.system.PreferenceControl;

/**
 * Handle the BrAC detection data
 * 
 * @author Stanley Wang
 */
public class TestDataParser {

	private static final String TAG = "TEST_DATA_PARSER";

	protected long ts;
	protected Context context;
	protected double sensorResult = 0;
	//protected DatabaseControl db;
	protected DBControl db;
	
	public static final int NOTHING = 0;
	public static final int ERROR = -1;
	public static final int SUCCESS = 1;

	/**
	 * Constructor
	 * 
	 * @param timestamp
	 *            timestamp of the detection
	 */
	public TestDataParser(long timestamp) {
		this.ts = timestamp;
		this.context = App.getContext();
		//db = new DatabaseControl();
		db = new DBControl();
	}

	/** start to handle the detection data */
	public void start() {

		File mainStorageDir = MainStorage.getMainStorageDirectory();
		File textFile, questionFile;

		textFile = new File(mainStorageDir.getPath() + File.separator + ts
				+ File.separator + ts + ".txt");
		//questionFile = new File(mainStorageDir.getPath() + File.separator + ts
		//		+ File.separator + "question.txt");
		
		questionFile = new File(mainStorageDir.getPath() + File.separator + "0"
				+ File.separator + "question.txt");
		
		Log.i(TAG, "TDP Start");
		
		int q_result = getQuestionResult(questionFile);
		int type = q_result / 1000;
		int item = (q_result % 1000)/10;
		int impact = q_result % 10;
		
		if (q_result == -1) {
			type = -1;
			item = -1;
			impact = -1;
		}

		int test_result = 0; 
		long timestamp = ts;
		int is_prime = 1;
		int is_filled = 1;

		//Detection detection = new Detection(brac, timestamp, emotion, craving,				false, 0, 0);
		TestResult testResult = new TestResult(test_result, timestamp, "tmp_id", is_prime, is_filled, 0, 0);
		boolean update = false;
		if (timestamp == PreferenceControl.getUpdateDetectionTimestamp())
			update = true;
		PreferenceControl.setUpdateDetection(false);
		PreferenceControl.setUpdateDetectionTimestamp(0);
		
		//db.addTestResult(testResult);
		DBControl.inst.addTestResult(testResult);
		//int addScore = db.insertDetection(detection, update);
		//if (addScore == 0 && !detection.isPass()) // TestFail & get no credit
			//CustomToast.generateToast(R.string.after_test_fail, -1);
		//else if (!detection.isPass())
			//CustomToast.generateToast(R.string.after_test_fail, addScore);
		//else
			//CustomToast.generateToast(R.string.after_test_pass, addScore);

		//int prevShowWeek = PreferenceControl.getPrevShowWeek();
		//int prevShowWeekState = PreferenceControl.getPrevShowWeekState();
		//Detection curDetection = db.getLatestDetection();
		//int curState = StorytellingGraphics.getPageIdx(
			//	curDetection.getWeeklyScore(), curDetection.getTv().getWeek());
		/*
		if (prevShowWeek < curDetection.getTv().getWeek())
			prevShowWeekState = 0;
		boolean pageChange = (prevShowWeekState < curState);
		PreferenceControl.setPageChange(pageChange);

		if (sensorResult < Detection.BRAC_THRESHOLD)
			if (emotion <= 2 || craving >= 4)
				PreferenceControl.setTestResult(1);
			else
				PreferenceControl.setTestResult(0);
		else if (sensorResult < Detection.BRAC_THRESHOLD_HIGH)
			PreferenceControl.setTestResult(2);
		else
			PreferenceControl.setTestResult(3);
		 */
	}
	
	/** start to handle the noteAdd data */
	public void startAddNote() {

		File mainStorageDir = MainStorage.getMainStorageDirectory();
		File questionFile;


		questionFile = new File(mainStorageDir.getPath() + File.separator + ts
			+ File.separator + "question.txt");

		Log.i(TAG, "TDP Start");
		
		int q_result = getQuestionResult(questionFile);
		int type = q_result / 1000;
		int item = (q_result % 1000)/10;
		int impact = q_result % 10;
		
		
		int category = 1;
		
		if (q_result == -1) {
			type = -1;
			item = -1;
			impact = -1;
		}

		int test_result = 0; 
		long timestamp = ts;
		int is_prime = 1;
		int is_filled = 1;

		NoteAdd noteAdd = new NoteAdd(1, ts, ts, category, type, item, impact, "test", 0);
		boolean update = false;
		if (timestamp == PreferenceControl.getUpdateDetectionTimestamp())
			update = true;
		PreferenceControl.setUpdateDetection(false);
		PreferenceControl.setUpdateDetectionTimestamp(0);
		
		//db.addTestResult(testResult);
		DBControl.inst.addNoteAdd(noteAdd);
	}

	/**
	 * get detection result
	 * 
	 * @return BrAC value reading from the sensor
	 */
	public double getResult() {
		return sensorResult;
	}

	/** Tempory not use
	 * Parse the detection text file
	 * 
	 * @param textFile
	 *            file contains all the detection value
	 * @return BrAC value
	 */
	/*
	protected double parseTextFile(File textFile) {
		double median = 0;
		try {
			@SuppressWarnings("resource")
			Scanner s = new Scanner(textFile);
			int index = 0;
			List<Double> valueArray2 = new ArrayList<Double>();

			while (s.hasNext()) {
				index++;
				String word = s.next();
				if (index % 2 == 0) {
					valueArray2.add(Double.valueOf(word));
				}
			}
			if (valueArray2.size() == 0)
				return ERROR;
			Double[] values = valueArray2
					.toArray(new Double[valueArray2.size()]);
			Arrays.sort(values);
			median = values[(values.length - 1) / 2];

		} catch (FileNotFoundException e1) {
			Log.d(TAG, "FILE NOT FOUND");
			return ERROR;
		}
		return median;
	}
	*/
	/**
	 * Parse the questionnaire file
	 * 
	 * @param textFile
	 *            file contains all the questionnaire result
	 * @return emotion * 100 + craving
	 */
	protected int getQuestionResult(File textFile) {
		int result = -1;
		try {
			@SuppressWarnings("resource")
			Scanner s = new Scanner(textFile);

			int type = 0;
			int item = 0;
			int impact = 0;

			if (s.hasNextInt())
				type = s.nextInt();
			if (s.hasNextInt())
				item = s.nextInt();
			if (s.hasNextInt())
				impact = s.nextInt();

			if (type == 0 || item == 0)
				return -1;
			
			result = item * 10 + impact;

		} catch (FileNotFoundException e1) {
			return ERROR;
		}
		return result;
	}
}
