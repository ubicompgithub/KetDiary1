package com.ubicomp.ketdiary.data.structure;

public class Cassette {

	private TimeValue tv;
	private int isUsed;
	private String cassetteId;

	public Cassette(long ts, int isUsed, String cassetteId) {
		this.tv = TimeValue.generate(ts);
		this.isUsed = isUsed;
		this.cassetteId = cassetteId;
	}

	public TimeValue getTv() {
		return tv;
	}

	public int getisUsed() {
		return isUsed;
	}

	public String getCassetteId() {
		return cassetteId;
	}


}
