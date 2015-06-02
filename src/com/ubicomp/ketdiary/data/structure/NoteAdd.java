package com.ubicomp.ketdiary.data.structure;


public class NoteAdd {
	private int isAfterTest;
	private TimeValue tv;
	private TimeValue recordTv;
	private int category;
	private int type;
	private int items;
	private int impact;
	private String description;
	private int score;

	public NoteAdd(int isAfterTest, long tv, long recordTv, 
			int category, int type, int items, int impact, String description, int score) {
		this.isAfterTest = isAfterTest;
		this.tv = TimeValue.generate(tv);
		this.recordTv = TimeValue.generate(recordTv);;
		this.category = category;
		this.type = type;
		this.items = items;
		this.impact = impact;
		this.description=description;
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

	public int getScore() {
		return score;
	}

	
	
}
