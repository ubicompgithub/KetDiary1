package com.ubicomp.ketdiary.mydaybook;

import android.graphics.Bitmap;

public class DummyData {
	public int who; // 1 - 2	
	public float score; //-3 - 3	
	public int activityType;  // 1 - 8
	public int month, day;
	public int passTest; // true/false
	
	public DummyData(int who, float score, int activityType, int month, int day, int passTest) {
		this.who = who;
		this.score = score;
		this.activityType = activityType;
		this.month = month;
		this.day = day;
		this.passTest = passTest;
	}

}
