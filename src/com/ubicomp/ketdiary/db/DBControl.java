package com.ubicomp.ketdiary.db;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ubicomp.ketdiary.App;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class DBControl {
	
	private String TAG = "DBControl";
	
	private String PREFILE_STR_USERID = "user_id_1";
	private String PREFILE_STR_DEVICEID = "device_id";
	private String PREFILE_STR_ISDEV = "is_dev";
		
	private String PREFILE_STR_ISTESTING = "is_testing";
	private String PREFILE_STR_TESTDATETIME = "test_datetime";
	
	private String PREFILE_NAME = "ket_db";
	
	public static DBControl inst = new DBControl();
	
	
	public class TypeTestDetail{
		public boolean is_filled;
		public Date date;
		public int time_trunk;
		public int catagory_id;
		public int type_id;
		public int reason_id;
		public String description;
	}
	
	public DBControl(){}
	
	public String getUserID(){
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
		return settings.getString(PREFILE_STR_USERID, "guest");
	}
	
	public void setUserID(String _user_id){
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
        settings.edit().putString(PREFILE_STR_USERID, _user_id).commit();
	}
	
	public boolean getIsDev(){
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
        return settings.getBoolean(PREFILE_STR_ISDEV, false);
	}
	
	public void setIsDev(boolean _is_dev){
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
        settings.edit().putBoolean(PREFILE_STR_ISDEV, _is_dev).commit();
	}
	
	public String getDeviceID(){
		Context context = App.getContext();

		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
		return settings.getString(PREFILE_STR_DEVICEID, "SimpleBLEPeripheral");
	}
	
	public void setDeviceID(String _device_id){
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
        settings.edit().putString(PREFILE_STR_DEVICEID, _device_id).commit();
	}
	
	public void startTesting(){
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
		DateFormat dt = DateFormat.getDateTimeInstance();
		settings.edit().putString(PREFILE_STR_TESTDATETIME, dt.format(new Date()))
					   .putBoolean(PREFILE_STR_ISTESTING, true)
					   .commit();
	}
	
	public void stopTesting(){
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
		settings.edit().putBoolean(PREFILE_STR_ISTESTING, false);
	}
	
	public boolean isTesting(){
		Context context = App.getContext();
		SharedPreferences settings = context.getSharedPreferences(PREFILE_NAME, 0);
		return settings.getBoolean(PREFILE_STR_ISTESTING, false);
	}
	
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
			return 100000;
		}
		
	}
	
	void addTestResult(Context context, TypeTestDetail ttd){
		
	}
}
