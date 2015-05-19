package com.ubicomp.ketdiary.data.structure;


public class TestResult {
	
	private float result;
	private TimeValue tv;
	private int trigger_type;
	private int trigger_item;
	private boolean isPrime;
	private int weeklyScore;
	private int score;
	public static final float BRAC_THRESHOLD = 0.06f;
	public static final float BRAC_THRESHOLD_HIGH = 0.25f;
	private final static int MAX_WEEKLY_SCORE = 42;

	public TestResult() {
		this.brac = brac;
		//this.tv = TimeValue.generate(timestamp);
		this.trigger_type = trigger_type;
		this.trigger_item = trigger_item;
		this.isPrime = isPrime;
		this.weeklyScore = weeklyScore;
		this.score = score;
	}

}
