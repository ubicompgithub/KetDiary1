package com.ubicomp.ketdiary.data.structure;

public class NoteAdd {
	private int isAfterTest;
	private TimeValue tv;
	private TimeValue recordTv;
	private int rtimeslot;
	private int category;
	private int type;
	private int items;
	private int impact;
	private String description;

	public NoteAdd(int isAfterTest, TimeValue tv, TimeValue recordTv,int rtimeslot,
			       int category, int type, int items,int impact, String description) {
		this.isAfterTest = isAfterTest;
		this.tv = tv;
		this.recordTv = recordTv;
		this.rtimeslot= rtimeslot;
		this.category = category;
		this.type = type;
		this.items = items;
		this.impact = impact;
		this.description=description;
	}

}
