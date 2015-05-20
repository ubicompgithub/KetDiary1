package com.ubicomp.ketdiary.data.structure;

public class TestDetail {
	
	private String cassetteId;
	private int failedState;
	private int firstVoltage;
	private int secondVoltage;
	private int devicePower;
	private int colorReading;
	private float connectionFailRate;
	private int failedReason;

	public TestDetail(String cassetteId,int failedState,int firstVoltage,
					int secondVoltage, int devicePower, int colorReading,
	                float connectionFailRate, int failedReason) {
		this.cassetteId=cassetteId;
		this.failedState= failedState;
		this.firstVoltage=firstVoltage;
		this.secondVoltage=secondVoltage;
		this.devicePower=devicePower;
		this.colorReading=colorReading;
		this.connectionFailRate=connectionFailRate;
		this.failedReason=failedReason;
	}

}
