package com.mackwell.nlight_beta.messageType;

import com.mackwell.nlight_beta.R;

public enum EmergencyStatus implements BitField{
	
	INHIBIT_MODE("Inhibit Mode",R.string.emergencyStatus_INHIBIT_MODE),
	FUNCTION_TEST_DONE("FT Done",R.string.emergencyStatus_FUNCTION_TEST_DONE),
	DURATION_TEST_DONE("DT Done",R.string.emergencyStatus_DURATION_TEST_DONE),
	BATTERY_FULLY_CHARGED("Battery Full",R.string.emergencyStatus_BATTERY_FULLY_CHARGED),
	FUNCTION_TEST_PENDING("FT Pending",R.string.emergencyStatus_FUNCTION_TEST_PENDING),
	DURATION_TEST_PENDING("DT Pending",R.string.emergencyStatus_DURATION_TEST_PENDING),
	IDENTIFICATION_ACTIVE("Identification Activt",R.string.emergencyStatus_IDENTIFICATION_ACTIVE),
	PHYSICALLY_SELECTED("Physically Selected",R.string.emergencyStatus_PHYSICALLY_SELECTED),
	NORMAL("-",R.string.emergencyStatus_NORMAL);
	
	private final int flag;
	private final String description;
	private int stringId;
	
	EmergencyStatus(String des,int id) {
		this.flag = 1 << this.ordinal();
		this.description = des;
		this.stringId = id;
	}
	
	@Override
	public int getFlag() {
		return flag;
	}
	
	@Override
	public String getDescription() {
		return description;
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
