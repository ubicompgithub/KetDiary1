package com.ubicomp.ketdiary.statistic.coping;

import com.ubicomp.ketdiary.data.structure.TimeValue;

public class EmotionDIY {

	private TimeValue tv;
	private int selection;
	private String recreation;
	private int score;
	
	public EmotionDIY(long ts,int selection,String recreation,int score){
		this.tv = TimeValue.generate(ts);
		this.selection = selection;
		this.recreation = recreation==null?"":recreation;
		this.score = score;
	}

	public TimeValue getTv() {
		return tv;
	}

	public int getSelection() {
		return selection;
	}

	public String getRecreation() {
		return recreation;
	}

	public int getScore() {
		return score;
	}
}
