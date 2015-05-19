package com.ubicomp.ketdiary.db;

import java.util.Date;

import android.text.format.Time;

/**
 * Database type define
 * @author mudream
 *
 */
public class Datatype {
	
	/** create instance */
	public static Datatype inst = new Datatype();
	private Datatype(){}
	
	public class TestDetail{
		public boolean is_filled;
		public Date date;
		public int time_trunk;
		public int result;
		public int catagory_id;
		public int type_id;
		public int reason_id;
		public String description;
		public TestDetail(){}
	}
	
	public class Patient{
		public String UserId;
		//public Date JoinDate;
		//public int IsDropout;
		//public Date DropoutDate;
		//public Time ConnectionCheckTime;
		//public String AppVersion;
		public Patient(){
		
		}
	}
	
	/**
	 * Get a new TestDetail
	 * @return new TestDetail
	 */
	public TestDetail newTestDetail(){
		return new TestDetail();
	}
	
	public Patient newPatient(){
		return new Patient();
	}
	
}
