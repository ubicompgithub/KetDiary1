package com.ubicomp.ketdiary.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.db.DatabaseControl;

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
		try{
			File mainStorageDir = MainStorage.getMainStorageDirectory();
			File textFile;
			
			DatabaseControl db = new DatabaseControl();
			textFile = new File(mainStorageDir.getPath() + File.separator + "data.txt");


	        //建立FileReader物件，並設定讀取的檔案為SD卡中的output.txt檔案
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
	        	int rMonth = Integer.parseInt(broken_text[2]);
	        	int rDay = Integer.parseInt(broken_text[3]);
	    		int timeslot = Integer.parseInt(broken_text[4]);;
	    		int category = Integer.parseInt(broken_text[5]);;
	    		int type = Integer.parseInt(broken_text[6]);;
	    		int items = Integer.parseInt(broken_text[7]);;
	    		int impact = Integer.parseInt(broken_text[8]);;
	    		String description= "";

	        	
	    		noteAdd = new NoteAdd(isAfterTest,0, rYear, rMonth, rDay, timeslot, category, type, items, impact, description, 0, 0); 
	        	//noteAdd = new NoteAdd(broken_text[0], broken_text[1], broken_text[2], broken_text[3], broken_text[4], 
	        	//		broken_text[4], broken_text[4], broken_text[4], broken_text[4], 6, "test", 0, 0);
	        	Log.d(TAG, ""+rMonth);
	    		db.insertNoteAdd(noteAdd);
	        }
	    }catch(Exception e){
	        e.printStackTrace();
	    }
		return null;
	}

}
