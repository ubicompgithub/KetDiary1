package com.ubicomp.ketdiary.db;

import java.util.Date;

/** Database type define
 * 
 * @author mudream
 *
 */

public class Datatype {
	
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
	public TestDetail newTestDetail(){
		return new TestDetail();
	}
	
}
