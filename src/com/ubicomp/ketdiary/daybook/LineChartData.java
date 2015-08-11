package com.ubicomp.ketdiary.daybook;


public class LineChartData {
	public int self_type; 	
	public float self_score; //-3 - 3
	public int other_type;
	public float other_score;
	public int month, day;
	public int result; 
	
	public LineChartData(int self_type, float self_score , int other_type, float other_score,  int month, int day, int result) {
		this.self_type = self_type;
		this.self_score = self_score;
		this.other_type = other_type;
		this.other_score = other_score;
		this.month = month;
		this.day = day;
		this.result = result;
	}
	
	public int getSelfType() {
		return self_type;
	}

	public float getSelfScore(){
		return self_score;
	}

	
	public int getOtherType() {
		return other_type;
	}

	public float getOtherScore() {
		return other_score;
	}

	public int getMonth() {
		return month;
	}
	
	public int getDay() {
		return day;
	}
	
	public int getResult() {
		return result;
	}
	
	

}
