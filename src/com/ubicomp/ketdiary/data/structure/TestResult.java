package com.ubicomp.ketdiary.data.structure;

import java.util.Date;


public class TestResult {
	
	public float result;
	public Date date;
	public TimeValue tv;
	public String cassette_id;
	public int isPrime;
	public int isFilled;

	public TestResult(int result, long tv, String cassette_id
			,int isPrime, int isFilled) {
		this.result = result;
		this.tv = TimeValue.generate(tv);;
		this.cassette_id = cassette_id;
		this.isPrime = isPrime;
		this.isFilled= isFilled;

	}

}
