package com.ubicomp.ketdiary.data.structure;

public class TestDetail {
	
	public String cassetteId;
	public int failedState;
	public int firstVoltage;
	public int secondVoltage;
	public int devicePower;
	public int colorReading;
	public float connectionFailRate;
	public int failedReason;

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
