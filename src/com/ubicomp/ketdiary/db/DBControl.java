package com.ubicomp.ketdiary.db;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ubicomp.ketdiary.App;
import com.ubicomp.ketdiary.data.structure.NoteAdd;
import com.ubicomp.ketdiary.data.structure.TestResult;


/** 
 * Main Data Control Center
 * @author mudream
 *
 */
public class DBControl {
	
	private String TAG = "DBControl";
	
	/** Profile strings*/
	private String PREFILE_STR_USERID = "user_id_1";
	private String PREFILE_STR_DEVICEID = "device_id";
	private String PREFILE_STR_ISDEV = "is_dev";
		
	private String PREFILE_STR_ISTESTING = "is_testing";
	private String PREFILE_STR_TESTDATETIME = "test_datetime";
	
	private String PREFILE_NAME = "ket_db";
	
	/** create instance*/
	public static DBControl inst = new DBControl();
	public DBControl(){}
	
	public String getUserID(){
		Log.d("asdasd", "GetUserID");
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
		return settings.getString(PREFILE_STR_USERID, "guest1");
	}
	
	/**
	 * Change UserID
	 * @param _user_id
	 */
	public void setUserID(String _user_id){
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
        settings.edit().putString(PREFILE_STR_USERID, _user_id).commit();
	}
	
	/**
	 * Get if user is the developer
	 * @return Is user a parameter?
	 */
	public boolean getIsDev(){
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
        return settings.getBoolean(PREFILE_STR_ISDEV, false);
	}
	
	/**
	 * Set a user is a developer or not
	 * @param _is_dev
	 */
	public void setIsDev(boolean _is_dev){
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
        settings.edit().putBoolean(PREFILE_STR_ISDEV, _is_dev).commit();
	}
	
	/**
	 * Get Device's ID
	 * @return DeviceID
	 */
	public String getDeviceID(){
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
		return settings.getString(PREFILE_STR_DEVICEID, "SimpleBLEPeripheral");
	}
	
	/**
	 * Set Device's ID
	 * @param _device_id
	 */
	public void setDeviceID(String _device_id){
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
        settings.edit().putString(PREFILE_STR_DEVICEID, _device_id).commit();
	}
	
	/**
	 * Start the 10mins client part,
	 * here will record the certain time
	 */
	public void startTesting(){
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
		DateFormat dt = DateFormat.getDateTimeInstance();
		settings.edit().putString(PREFILE_STR_TESTDATETIME, dt.format(new Date()))
					   .putBoolean(PREFILE_STR_ISTESTING, true)
					   .commit();
	}
	
	/**
	 * Stop 10mins client part
	 */
	public void stopTesting(){
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
		settings.edit().putBoolean(PREFILE_STR_ISTESTING, false);
	}
	
	/**
	 * Check if it is in 10mins testing
	 * @return
	 */
	public boolean isTesting(){
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
		return settings.getBoolean(PREFILE_STR_ISTESTING, false);
	}
	
	/**
	 * Get passing millionsecond since starting test
	 * @return
	 */
	public long getTestMs(){
		Context context = App.getContext();
		if(isTesting() == false) return 100000;
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
		DateFormat df = DateFormat.getDateTimeInstance();
		Date dt;
		try {
			String get_start_dt = settings.getString(PREFILE_STR_TESTDATETIME, "");
			dt = df.parse(get_start_dt);
			Date dnow = new Date();
			return (dnow.getTime()-dt.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			
			// TODO: remove magic number
			/** A magic number*/
			return 100000;
		}
		
	}
	
	// TODO: use sqlite
	
	Vector<Datatype.TestDetail> not_uploaded_testdetail = new Vector<Datatype.TestDetail>();
	Vector<Datatype.Patient> not_uploaded_patient = new Vector<Datatype.Patient>();
	/**
	 * Add TestDetail and upload
	 * @param ttd
	 */
	public void addTestResult(Datatype.TestDetail ttd){ 
		not_uploaded_testdetail.add(ttd);
		DataUploader.upload();
	}
	
	/**
	 * Get not upload TestDetail
	 * @return
	 * @see DataUploader
	 */
	public Vector<Datatype.TestDetail> getNotUploadedTestDetail(){
		Vector<Datatype.TestDetail> ret = new Vector<Datatype.TestDetail>();
		for(int lx = 0;lx < not_uploaded_testdetail.size();lx++)
			ret.add(not_uploaded_testdetail.get(lx));
		not_uploaded_testdetail.clear();
		return ret;
	}
	
	
	//Test Result new
	
	Vector<TestResult> notUploadedTestResult = new Vector<TestResult>();

	/**
	 * Add TestResult and upload
	 * @param ttd
	 */
	public void addTestResult(TestResult data){ //insertTestResult
		notUploadedTestResult.add(data);
		DataUploader.upload();
	}
	
	/**
	 * Get not upload TestResult
	 * @return
	 * @see DataUploader
	 */
	public Vector<TestResult> getNotUploadedTestResult(){
		Vector<TestResult> ret = new Vector<TestResult>();
		for(int lx = 0;lx < notUploadedTestResult.size();lx++)
			ret.add(notUploadedTestResult.get(lx));
		notUploadedTestResult.clear();
		return ret;
	}
	
	
	
	//NoteAdd
	
	Vector<NoteAdd> notUploadedNoteAdd = new Vector<NoteAdd>();

	/**
	 * Add NoteAdd and upload
	 * @param data
	 */
	public void addNoteAdd(NoteAdd data){ //insertTestResult
		notUploadedNoteAdd.add(data);
		DataUploader.upload();
	}
	
	/**
	 * Get not upload NoteAdd
	 * @return
	 * @see DataUploader
	 */
	public Vector<NoteAdd> getNotUploadedNoteAdd(){
		Vector<NoteAdd> ret = new Vector<NoteAdd>();
		for(int lx = 0;lx < notUploadedNoteAdd.size();lx++)
			ret.add(notUploadedNoteAdd.get(lx));
		notUploadedNoteAdd.clear();
		return ret;
	}
		
		
	/**
	 * Add Patient and upload
	 * @param ttd
	 */
	public void addPatient(Datatype.Patient p){
		not_uploaded_patient.add(p);
		DataUploader.upload();
	}
	
	/**
	 * Get not upload Patient
	 * @return
	 * @see DataUploader
	 */
	public Vector<Datatype.Patient> getNotUploadedPatient(){
		Vector<Datatype.Patient> ret = new Vector<Datatype.Patient>();
		for(int lx = 0;lx < not_uploaded_patient.size();lx++)
			ret.add(not_uploaded_patient.get(lx));
		not_uploaded_patient.clear();
		return ret;
	}
}
