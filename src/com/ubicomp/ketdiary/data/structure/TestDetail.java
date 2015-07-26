package com.ubicomp.ketdiary.data.structure;

public class TestDetail {
	
	public String cassetteId;
	public TimeValue tv;
	public int failedState;
	public int firstVoltage;
	public int secondVoltage;
	public int devicePower;
	public int colorReading;
	public float connectionFailRate;
	public String failedReason;
	public String hardwareVersion;

	public TestDetail(String cassetteId, long tv, int failedState,int firstVoltage,
					int secondVoltage, int devicePower, int colorReading,
	                float connectionFailRate, String failedReason, String hardwareVersion) {
		this.cassetteId=cassetteId;
		this.tv = TimeValue.generate(tv);
		this.failedState= failedState;
		this.firstVoltage=firstVoltage;
		this.secondVoltage=secondVoltage;
		this.devicePower=devicePower;
		this.colorReading=colorReading;
		this.connectionFailRate=connectionFailRate;
		this.failedReason=failedReason;
		this.hardwareVersion = hardwareVersion;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(tv.toString());
		sb.append(' ');
		sb.append(cassetteId);
		sb.append(' ');
		sb.append(failedState);
		sb.append(' ');
		sb.append(firstVoltage);
		sb.append(' ');
		sb.append(secondVoltage);
		sb.append(' ');
		sb.append(devicePower);
		sb.append(' ');
		sb.append(colorReading);
		sb.append(' ');
		sb.append(connectionFailRate);
		sb.append(' ');
		sb.append(failedReason);
		return sb.toString();
	}

	public String getCassetteId() {
		return cassetteId;
	}

	public TimeValue getTv() {
		return tv;
	}


	public int getFailedState() {
		return failedState;
	}

	public int getFirstVoltage() {
		return firstVoltage;
	}

	
	public int getSecondVoltage() {
		return secondVoltage;
	}
	
	public int getDevicePower() {
		return devicePower;
	}
	

	public int getColorReading() {
		return colorReading;
	}
	
	public float getConnectionFailRate() {
		return connectionFailRate;
	}
	
	public String getFailedReason() {
		return failedReason;
	}
	
	public String getHardwareVersion() {
		return hardwareVersion;
	}
}
