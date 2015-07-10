package com.ubicomp.ketdiary.data.structure;

public class ExchangeHistory {

	private TimeValue tv;
	private int exchangeNum;

	public ExchangeHistory(long ts, int exchangeNum) {
		this.tv = TimeValue.generate(ts);
		this.exchangeNum = exchangeNum;
	}

	public TimeValue getTv() {
		return tv;
	}

	public int getExchangeNum() {
		return exchangeNum;
	}

}
