package com.ubicomp.ketdiary.data.structure;

import java.util.Calendar;


public class NoteAdd {
	private int isAfterTest;
	private TimeValue tv;
	private TimeValue recordTv;
	private int timeslot;
	private int category;
	private int type;
	private int items;
	private int impact;
	private String description;
	private int weeklyScore;
	private int score;

	public NoteAdd(int isAfterTest, long tv, int rYear, int rMonth, int rDay, int timeslot,
			int category, int type, int items, int impact, String description, int weeklyScore, int score) {
		this.isAfterTest = isAfterTest;
		this.tv = TimeValue.generate(tv);
		
		Calendar cal = Calendar.getInstance();
		cal.set(rYear, rMonth, rDay, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.recordTv = TimeValue.generate(cal.getTimeInMillis());
		this.timeslot = timeslot;
		this.category = category;
		this.type = type;
		this.items = items;
		this.impact = impact;
		this.description=description;
		this.weeklyScore=weeklyScore;
		this.score=score;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(tv.toString());
		sb.append(' ');
		sb.append(recordTv.toString());
		sb.append(' ');
		sb.append(isAfterTest);
		sb.append(' ');
		sb.append(category);
		sb.append(' ');
		sb.append(type);
		sb.append(' ');
		sb.append(items);
		sb.append(' ');
		sb.append(impact);
		sb.append(' ');
		sb.append(description);
		sb.append(' ');
		sb.append(score);
		return sb.toString();
	}

	public TimeValue getTv() {
		return tv;
	}

	public TimeValue getRecordTv() {
		return recordTv;
	}
	
	public int getTimeSlot() {
		return timeslot;
	}

	public int getIsAfterTest() {
		return isAfterTest;
	}
	
	
	public int getCategory() {
		return category;
	}

	public int getType() {
		return type;
	}
	
	public int getItems() {
		return items;
	}
	
	public int getImpact() {
		return impact;
	}

	public String getDescription() {
		return description;
	}
	
	public int getWeeklyScore() {
		return weeklyScore;
	}

	public int getScore() {
		return score;
	}

	
	
}
