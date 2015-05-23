package com.ubicomp.ketdiary.data.structure;

public class NoteAdd {
	public int isAfterTest;
	public TimeValue tv;
	public TimeValue recordTv;
	public int rtimeslot;
	public int category;
	public int type;
	public int items;
	public int impact;
	public String description;

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
