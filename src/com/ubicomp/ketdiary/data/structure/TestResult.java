package com.ubicomp.ketdiary.data.structure;


public class TestResult {
	
	private float result;
	private TimeValue tv;
	private String cassette_id;
	private int isPrime;
	private int isFilled;

	public TestResult(float result, TimeValue tv, String cassette_id
			,int isPrime, int isFilled) {
		this.result = result;
		this.tv = tv;
		this.cassette_id = cassette_id;
		this.isPrime = isPrime;
		this.isFilled= isFilled;

	}

}
