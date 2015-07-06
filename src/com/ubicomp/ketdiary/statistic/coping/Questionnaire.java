package com.ubicomp.ketdiary.statistic.coping;

import com.ubicomp.ketdiary.data.structure.TimeValue;

public class Questionnaire {

	private TimeValue tv;
	private int type;
	private String seq;
	private int score;

	public Questionnaire(long ts, int type, String seq, int score) {
		this.tv = TimeValue.generate(ts);
		this.type = type;
		this.seq = seq;
		this.score = score;
	}

	public TimeValue getTv() {
		return tv;
	}

	public int getType() {
		return type;
	}

	public String getSeq() {
		return seq;
	}

	public int getScore() {
		return score;
	}

}
