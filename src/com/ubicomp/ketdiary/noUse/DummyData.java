package com.ubicomp.ketdiary.noUse;

import android.graphics.Bitmap;

public class DummyData {
	public int who; // 1 - 2	
	public float self_score; //-3 - 3
	public float other_score;
	public int activityType;  // 1 - 8
	public int month, day;
	public int passTest; // true/false
	
	public DummyData(int who, float self_score ,float other_score, int activityType, int month, int day, int passTest) {
		this.who = who;
		this.self_score = self_score;
		this.other_score = other_score;
		this.activityType = activityType;
		this.month = month;
		this.day = day;
		this.passTest = passTest;
	}

}
