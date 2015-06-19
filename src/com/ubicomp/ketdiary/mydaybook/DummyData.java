package com.ubicomp.ketdiary.mydaybook;

import android.graphics.Bitmap;

public class DummyData {
	public int who; // 1 - 2	
	public int score; //-3 - 3	
	public int activityType;  // 1 - 8
	public Bitmap dotColor;
	public int month, day;
	public boolean passTest; // true/false
	
	public DummyData(int who, int score, int activityType, Bitmap bmp, int month, int day, boolean passTest) {
		this.who = who;
		this.score = score;
		this.activityType = activityType;
		this.dotColor = bmp;
		this.month = month;
		this.day = day;
		this.passTest = passTest;
	}

}
