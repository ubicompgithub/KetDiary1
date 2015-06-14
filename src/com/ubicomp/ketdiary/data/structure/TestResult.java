package com.ubicomp.ketdiary.data.structure;



public class TestResult {
	
	public int result;
	public TimeValue tv;
	public String cassette_id;
	public int isPrime;
	public int isFilled;
	public int weeklyScore;
	public int score;
	private final static int MAX_WEEKLY_SCORE = 42;
	
	public TestResult(int result, long tv, String cassette_id
			,int isPrime, int isFilled, int weeklyScore, int score) {
		this.result = result;
		this.tv = TimeValue.generate(tv);
		this.cassette_id = cassette_id;
		this.isPrime = isPrime;
		this.isFilled= isFilled;
		this.weeklyScore= weeklyScore;
		this.score = score;

	}
	
	public boolean isSameTimeBlock(TestResult d) {
		return d != null && tv != null && tv.isSameTimeBlock(d.tv);
	}

	public boolean isSameDay(TestResult d) {
		return d != null && tv != null && tv.isSameDay(d.tv);
	}
	
	public static float weeklyScoreToProgress(int score) {
		float progress = (float) score * 100F / MAX_WEEKLY_SCORE;
		if (progress > 100.f)
			return 100.f;
		return progress;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(tv.toString());
		sb.append(' ');
		sb.append(result);
		sb.append(' ');
		sb.append(cassette_id);
		sb.append(' ');
		sb.append(isPrime);
		sb.append(' ');
		sb.append(isFilled);
		sb.append(' ');
		sb.append(weeklyScore);
		sb.append(' ');
		sb.append(score);
		return sb.toString();
	}

	public int getResult() {
		return result;
	}

	public TimeValue getTv() {
		return tv;
	}

	public String getCassette_id() {
		return cassette_id;
	}

	public int getIsPrime() {
		return isPrime;
	}

	public int getIsFilled() {
		return isFilled;
	}

	public int getWeeklyScore() {
		return weeklyScore;
	}

	public int getScore() {
		return score;
	}


}
