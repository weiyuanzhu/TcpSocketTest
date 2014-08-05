package com.mackwell.nlight.messageType;

import com.mackwell.nlight.R;

public enum FailureStatus implements BitField{
	
	CIRCUIT_FAILURE("Circuit Failed",R.string.failureStatus_CIRCUIT_FAILURE),
	BATTERY_DURATION_FAILURE("Battery Duration",R.string.failureStatus_BATTERY_DURATION_FAILURE),
	BATTERY_FAILURE("Battery Failed",R.string.failureStatus_BATTERY_FAILURE),
	LAMP_FAILURE("Lamp Failed",R.string.failureStatus_LAMP_FAILURE),
	FUNCTION_TEST_MAXIMUM_DELAY_EXCEEDED("FT Max",R.string.failureStatus_FUNCTION_TEST_MAXIMUM_DELAY_EXCEEDED), // device is unable to perform the function test within the maximum timeout period
	DURATION_TEST_MAXIMUM_DELAY_EXCEEDED("DT Max",R.string.failureStatus_DURATION_TEST_MAXIMUM_DELAY_EXCEEDED),
	FUNCTION_TEST_FAILURE("FT Failed",R.string.failureStatus_FUNCTION_TEST_FAILURE),
	DURATION_TEST_FAILURE("DT Failed",R.string.failureStatus_DURATION_TEST_FAILURE),
	ALL_OK("All OK",R.string.failureStatus_ALL_OK);
	
	private final int flag;
	private final String description;
	private int stringId;
	
	FailureStatus(String des,int id) {
		this.flag = 1 << this.ordinal();
		this.description = des;
		this.stringId = id;
	}
	
	@Override
	public int getFlag() {
		return flag;
	}
	
	public String getDescription()
	{
		return this.description;
		
	}
	
	public int getStringId(){
		return stringId;
	}
			

	
	@Override
	public String toString() {
		// capitalise first character
		return super.toString().charAt(0) + 
				super.toString().toLowerCase().replaceAll("_", " ").substring(1, super.toString().length()); 
	}
}
