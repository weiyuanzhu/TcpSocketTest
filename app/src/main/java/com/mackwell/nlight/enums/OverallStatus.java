package com.mackwell.nlight.enums;

public enum OverallStatus {
	
	OK(0x01),
	NOTCONFIG(0x02),
	Fault(0x03);
	
	private int status;
	
	private OverallStatus(int status)
	{
		this.status = status;
	}
	
	public int getStatus()
	{
		return this.status;
		
	}
	
}
