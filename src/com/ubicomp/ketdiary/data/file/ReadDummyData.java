package com.ubicomp.ketdiary.data.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Calendar;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ubicomp.ketdiary.data.db.DatabaseControl;
import com.ubicomp.ketdiary.data.db.DatabaseRestoreControl;
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.TestResult;

public class ReadDummyData extends AsyncTask<Void, Void, Void> {
	
	private Context context;
	private static final String TAG = "ReadDummyData";
	
	public ReadDummyData(Context context) {
		this.context = context;
	}
	
	@Override
	protected void onPreExecute() {
	}
	
	@Override
	protected void onPostExecute(Void result) {
	}

	
	@Override
	protected Void doInBackground(Void... params) {
		insertNoteAdd();
		insertTestResult();
		return null;
	}
	
	private void deleteDb(){
		DatabaseRestoreControl drc = new DatabaseRestoreControl();
		drc.deleteAll();
	}
	
	private void insertNoteAdd(){
		try{
			File mainStorageDir = MainStorage.getMainStorageDirectory();
			File textFile;
			
			DatabaseControl db = new DatabaseControl();
			textFile = new File(mainStorageDir.getPath() + File.separator + "data.txt");


	        //建立FileReader物件，並設定讀取的檔案為SD卡中的output.txt檔案
			
			if(textFile.exists()){
		        FileReader fr = new FileReader(textFile);
		        //將BufferedReader與FileReader做連結
		        BufferedReader br = new BufferedReader(fr);
		        
		        NoteAdd noteAdd;
		        
		        String[] broken_text = null;
				String lines = "";
		        
		        while ( (lines = br.readLine()) != null) {
		        	
		        	broken_text = lines.split(" ");
		        	int isAfterTest = Integer.parseInt(broken_text[0]);
		        	int rYear = Integer.parseInt(broken_text[1]);
		        	int rMonth = Integer.parseInt(broken_text[2])-1;
		        	int rDay = Integer.parseInt(broken_text[3]);
		    		int timeslot = Integer.parseInt(broken_text[4]);;
		    		int category = Integer.parseInt(broken_text[5]);;
		    		int type = Integer.parseInt(broken_text[6]);;
		    		int items = Integer.parseInt(broken_text[7]);;
		    		int impact = Integer.parseInt(broken_text[8]);;
		    		String description= "";
		    		long ts = System.currentTimeMillis();
		        	
		    		noteAdd = new NoteAdd(isAfterTest, ts, rYear, rMonth, rDay, timeslot, category, type, items, impact, description, 0, 0); 
		        	//noteAdd = new NoteAdd(broken_text[0], broken_text[1], broken_text[2], broken_text[3], broken_text[4], 
		        	//		broken_text[4], broken_text[4], broken_text[4], broken_text[4], 6, "test", 0, 0);
		        	Log.d(TAG, ""+rMonth);
		    		db.insertNoteAdd(noteAdd);
		    		db.setNoteAddUploaded(ts);
		    }
	        }
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	private void insertTestResult(){
		try{
			File mainStorageDir = MainStorage.getMainStorageDirectory();
			File textFile;
			
			DatabaseControl db = new DatabaseControl();
			textFile = new File(mainStorageDir.getPath() + File.separator + "testResult.txt");

			
			if(textFile.exists()){
		        //建立FileReader物件，並設定讀取的檔案為SD卡中的output.txt檔案
		        FileReader fr = new FileReader(textFile);
		        //將BufferedReader與FileReader做連結
		        BufferedReader br = new BufferedReader(fr);
		        
		        TestResult testResult;
		        
		        String[] broken_text = null;
				String lines = "";
		        
		        while ( (lines = br.readLine()) != null) {
		        	
		        	broken_text = lines.split(" ");
		        	int result = Integer.parseInt(broken_text[0]);
		        	int year = Integer.parseInt(broken_text[1]);
		        	int month = Integer.parseInt(broken_text[2])-1;
		        	int day = Integer.parseInt(broken_text[3]);
		        	
		        	Calendar cal = Calendar.getInstance();
		    		cal.set(year, month, day, 0, 0, 0);
		    		cal.set(Calendar.MILLISECOND, 0);
		        	
		        	long tv = cal.getTimeInMillis();
		        	String cassette_id = "Dummy";
		        	int isPrime = 1;
		    		int isFilled = 0;
	
		        	
		    		testResult = new TestResult(result, tv, cassette_id, isPrime, isFilled, 0, 0); 
		        	//noteAdd = new NoteAdd(broken_text[0], broken_text[1], broken_text[2], broken_text[3], broken_text[4], 
		        	//		broken_text[4], broken_text[4], broken_text[4], broken_text[4], 6, "test", 0, 0);
		        	//Log.d(TAG, ""+rMonth);
		    		db.insertTestResult(testResult, false);
		    		db.setTestResultUploaded(tv);
	        }
	        }
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}

}
